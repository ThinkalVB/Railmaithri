package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class UnauthorizedPerson : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var progressPB:    ProgressBar
    private lateinit var actionBT:      Button

    private lateinit var locationUtil:  LocationUtil
    private lateinit var fileUtil:      FileUtil
    private lateinit var category:      FieldSpinner
    private lateinit var description:   FieldEditText
    private lateinit var policeStation: FieldSpinner
    private lateinit var placeOfCheck:  FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unauthorized_person)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        if (mode == Mode.NEW_FORM) {
            locationUtil.fetchLocation(this)
        }
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
                    Helper.saveFormData(this, formData, Storage.UNAUTHORIZED_PEOPLE, uuid)
                    finish()
                }
            }
        }
    }

    private fun generateFields() {
        category = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.VENDOR_TYPES)!!),
            "category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        description = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "description",
            fieldName = "Description",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        placeOfCheck = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "place_of_check",
            fieldName = "Place of check",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(category.getLayout())
        form.addView(description.getLayout())
        form.addView(policeStation.getLayout())
        form.addView(placeOfCheck.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.UNAUTHORIZED_PEOPLE, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.UNAUTHORIZED_PEOPLE, uuid)
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
        fileUtil.hide()
        locationUtil.hide()

        if (mode == Mode.SEARCH_FORM) {
            actionBT.text = "Search"
        } else {
            fileUtil.show()
            locationUtil.show()

            if(mode == Mode.VIEW_FORM){
                actionBT.visibility = View.GONE
            } else{
                actionBT.text = "Save"
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            locationUtil.exportLocation(formData)
            category.exportData(formData)
            description.exportData(formData)
            policeStation.exportData(formData)
            placeOfCheck.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        locationUtil.importLocation(formData)
        category.importData(formData)
        description.importData(formData)
        policeStation.importData(formData)
        placeOfCheck.importData(formData)

        if(mode == Mode.VIEW_FORM){
            locationUtil.disableUpdate()
        }

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val category  = formData.getString("category")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nCategory: ${category}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  UnauthorizedPerson::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}