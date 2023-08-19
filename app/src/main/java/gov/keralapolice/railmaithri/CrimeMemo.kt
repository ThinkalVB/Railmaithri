package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CrimeMemo : AppCompatActivity() {
    private lateinit var mode:                  String
    private lateinit var progressPB:            ProgressBar
    private lateinit var actionBT:              Button

    private lateinit var fileUtil:              FileUtil
    private lateinit var crimeMemoCategory:     FieldSpinner
    private lateinit var crimeDetails:          FieldEditText
    private lateinit var policeStation:         FieldSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crime_memo)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
        renderFields()
    }

    private fun performAction() {
        when (mode) {
            Mode.NEW_FORM -> {
                val formData = getFormData()
                if (formData != null) {
                    val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
                    val stationID = profile.getJSONArray("police_station").getJSONObject(0).getInt("id")
                    val utcTime   = Helper.getUTC()
                    formData.put("utc_timestamp",  utcTime)
                    formData.put("police_station", stationID)
                    CoroutineScope(Dispatchers.IO).launch { sendFormData(formData) }
                }
            }
            Mode.SEARCH_FORM -> {
                var formData = getFormData()
                if (formData == null) {
                    formData = JSONObject()
                }
                val intent = Intent()
                intent.putExtra("parameters", formData.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
            Mode.UPDATE_FORM -> {
                val formData = JSONObject(intent.getStringExtra("data")!!)
                val uuid = formData.getString("utc_timestamp")

                val updatedFormData = getFormData(formData)
                if (updatedFormData != null) {
                    storeFile(formData, uuid)
                    Helper.saveFormData(this, formData, Storage.CRIME_MEMO, uuid)
                    finish()
                }
            }
        }
    }

    private fun generateFields() {
        crimeMemoCategory = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.CRIME_MEMO_TYPES)!!),
            "crime_memo_category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        crimeDetails = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "memo_details",
            fieldName = "Memo Details",
            fieldHeight=294,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(crimeMemoCategory.getLayout())
        form.addView(policeStation.getLayout())
        form.addView(crimeDetails.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.CRIME_MEMO, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.CRIME_MEMO, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun storeFile(formData: JSONObject, uuid: String) {
        if (fileUtil.haveFile()) {
            formData.put("__have_file", true)
            formData.put("__file_name", fileUtil.getFileName())
            fileUtil.saveFile(this, uuid)
        } else {
            formData.put("__have_file", false)
            formData.put("__file_name", "No file")
        }
    }

    private fun renderFields() {
        policeStation.hide()
        fileUtil.hide()

        if (mode == Mode.SEARCH_FORM) {
            policeStation.show()
            actionBT.text = "Search"
        } else {
            fileUtil.show()

            if(mode == Mode.VIEW_FORM){
                policeStation.show()
                actionBT.visibility = View.GONE
            } else{
                actionBT.text = "Save"
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            crimeMemoCategory.exportData(formData)
            crimeDetails.exportData(formData)
            policeStation.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        crimeMemoCategory.importData(formData)
        crimeDetails.importData(formData)
        policeStation.importData(formData)

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val category  = formData.getString("crime_memo_category")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nCategory: ${category}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity   = Gravity.START
            button.text      = shortData
            button.setOnClickListener {
                val intent = Intent(context,  CrimeMemo::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}