package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
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
    private lateinit var search:                FieldEditText
    private lateinit var memoDetails:           FieldEditText
    private lateinit var caseRegisteredIn:      FieldSpinner
    private lateinit var localPoliceStation:    FieldEditText
    private lateinit var otherPoliceStation:    FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crime_memo)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        caseRegisteredIn.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderFields()
            }
        }

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
                    val utcTime = Helper.getUTC()
                    formData.put("utc_timestamp",  utcTime)
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
        search = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "search",
            fieldName = "Search",
            isRequired = false
        )
        crimeMemoCategory = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.CRIME_MEMO_TYPES)!!),
            "crime_memo_category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        memoDetails = FieldEditText(this,
            fieldMaxLines = 16,
            fieldType = "multiline",
            fieldLabel = "memo_details",
            fieldName = "Memo Details",
            fieldHeight=100,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        crimeDetails = FieldEditText(this,
            fieldMaxLines = 16,
            fieldType = "multiline",
            fieldLabel = "crime_details",
            fieldName = "Crime Details",
            fieldHeight=100,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        caseRegisteredIn = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.CRIME_MEMO_STATION_TYPES)!!),
            "case_registered_in",
            "Case Registered In",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        localPoliceStation = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "local_police_station",
            fieldName = "Local Police Station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        otherPoliceStation = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "other_police_station",
            fieldName = "Other Police Station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(search.getLayout())
        form.addView(crimeMemoCategory.getLayout())
        form.addView(caseRegisteredIn.getLayout())
        form.addView(policeStation.getLayout())
        form.addView(localPoliceStation.getLayout())
        form.addView(otherPoliceStation.getLayout())
        form.addView(memoDetails.getLayout())
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
        search.hide()
        crimeMemoCategory.hide()
        caseRegisteredIn.hide()
        policeStation.hide()
        localPoliceStation.hide()
        otherPoliceStation.hide()
        memoDetails.hide()
        crimeDetails.hide()

        if (mode == Mode.SEARCH_FORM){
            search.show()
            crimeMemoCategory.show()
            caseRegisteredIn.show()
            actionBT.text = "Search"

        } else if(mode == Mode.VIEW_FORM || mode== Mode.UPDATE_FORM || mode == Mode.NEW_FORM){
            fileUtil.show()
            crimeMemoCategory.show()
            memoDetails.show()
            crimeDetails.show()
            caseRegisteredIn.show()
            when (caseRegisteredIn.getData().toString()) {
                "Railway Police Station" -> {
                    policeStation.show()
                }
                "Local Police Station" -> {
                    localPoliceStation.show()
                }
                "Others" ->{
                    otherPoliceStation.show()
                }
            }

            when (mode) {
                Mode.VIEW_FORM -> {
                    actionBT.visibility = View.GONE
                }
                Mode.UPDATE_FORM -> {
                    actionBT.text = "Update"
                }
                Mode.NEW_FORM -> {
                    actionBT.text = "Save"
                }
            }
        }
    }
    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            search.exportData(formData)
            crimeMemoCategory.exportData(formData)
            memoDetails.exportData(formData)
            caseRegisteredIn.exportData(formData)
            localPoliceStation.exportData(formData)
            otherPoliceStation.exportData(formData)
            crimeDetails.exportData(formData)
            policeStation.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        caseRegisteredIn.importData(formData)
        when (caseRegisteredIn.getData().toString()) {
            "Railway Police Station" -> {
                policeStation.importData(formData)
            }

            "Local Police Station" -> {
                localPoliceStation.importData(formData)
            }

            "Others" -> {
                otherPoliceStation.importData(formData)
            }
        }
        crimeMemoCategory.importData(formData)
        memoDetails.importData(formData)
        crimeDetails.importData(formData)

        if (mode == Mode.UPDATE_FORM) {
            val uuid = formData.getString("utc_timestamp")
            if (formData.getBoolean("__have_file")) {
                val fileName = formData.getString("__file_name")
                fileUtil.loadFile(this, uuid, fileName)
            }
            if (mode == Mode.VIEW_FORM) {
                fileUtil.registerLink(formData)
            }
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Crime Memo")
            val category  = Helper.getValueFromID(context, formData, "crime_memo_category", Storage.CRIME_MEMO_TYPES)
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