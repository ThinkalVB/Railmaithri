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

class IncidentReport : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var fileUtil:          FileUtil

    private lateinit var dateFrom:          FieldEditText
    private lateinit var dateTo:            FieldEditText
    private lateinit var incidentTypes:     FieldSpinner
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var platformNumber:    FieldEditText
    private lateinit var trackLocation:     FieldEditText
    private lateinit var train:             FieldSpinner
    private lateinit var coachNumber:       FieldEditText
    private lateinit var contactNumber:     FieldEditText
    private lateinit var details:           FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incident_report)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        incidentTypes.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderFields()
            }
        }
        actionBT.setOnClickListener { performAction() }

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        if(mode == Mode.NEW_FORM){
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
                    val utcTime = Helper.getUTC()
                    formData.put("incident_date_time", utcTime)
                    formData.put("data_from", "Beat Officer")
                    formData.put("utc_timestamp", utcTime)
                    CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
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
                val uuid     = formData.getString("utc_timestamp")
                getFormData(formData)
                storeFile(formData, uuid)
                Helper.saveFormData(this, formData, Storage.INCIDENT_REPORT, uuid)
                finish()
            }
        }
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.INCIDENT_REPORT, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.INCIDENT_REPORT, uuid)
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

    private fun generateFields() {
        dateFrom = FieldEditText(this,
            fieldType = "date",
            fieldLabel = "incident_date_time__gte",
            fieldName = "Date from",
            isRequired = false
        )
        dateTo = FieldEditText(this,
            fieldType = "date",
            fieldLabel = "incident_date_time__lte",
            fieldName = "Date to",
            isRequired = false
        )
        incidentTypes = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.INCIDENT_TYPES)!!),
            "incident_type",
            "Incident type",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        platformNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "platform_number",
            fieldName = "Platform number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        trackLocation = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "track_location",
            fieldName = "Track location",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        train = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.TRAINS_LIST)!!),
            "train",
            "Train",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        coachNumber = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "coach",
            fieldName = "Coach number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        contactNumber = FieldEditText(this,
            fieldType = "phone",
            fieldLabel = "mobile_number",
            fieldName = "Contact number",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        details = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "incident_details",
            fieldHeight= 98,
            fieldName = "Details",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.dynamic_fields)
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(incidentTypes.getLayout())
        form.addView(railwayStation.getLayout())
        form.addView(platformNumber.getLayout())
        form.addView(trackLocation.getLayout())
        form.addView(train.getLayout())
        form.addView(coachNumber.getLayout())
        form.addView(contactNumber.getLayout() )
        form.addView(details.getLayout())
    }

    private fun renderFields() {
        dateFrom.hide()
        dateTo.hide()
        incidentTypes.hide()
        railwayStation.hide()
        platformNumber.hide()
        trackLocation.hide()
        train.hide()
        coachNumber.hide()
        contactNumber.hide()
        fileUtil.hide()
        locationUtil.hide()

        incidentTypes.show()
        when (incidentTypes.getData().toString()) {
            "Platform" -> {
                railwayStation.show()
                platformNumber.show()
            }
            "Track" -> {
                railwayStation.show()
                trackLocation.show()
            }
            "Train" -> {
                train.show()
                coachNumber.show()
                contactNumber.show()
            }
        }

        if (mode == Mode.SEARCH_FORM) {
            dateFrom.show()
            dateTo.show()
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

    private fun loadFormData(formData: JSONObject) {
        incidentTypes.importData(formData)
        when (incidentTypes.getData().toString()) {
            "Platform" -> {
                platformNumber.importData(formData)
                railwayStation.importData(formData)
            }
            "Track" -> {
                railwayStation.importData(formData)
                trackLocation.importData(formData)
            }
            "Train" -> {
                train.importData(formData)
                coachNumber.importData(formData)
                contactNumber.importData(formData)
            }
        }
        details.importData(formData)
        locationUtil.importLocation(formData)

        if(mode == Mode.VIEW_FORM){
            locationUtil.disableUpdate()
        }

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            locationUtil.exportLocation(formData)
            dateFrom.exportData(formData, tailPadding = "T00:00:00")
            dateTo.exportData(formData, tailPadding = "T23:59:59")
            incidentTypes.exportData(formData)
            platformNumber.exportData(formData)
            railwayStation.exportData(formData)
            railwayStation.exportData(formData)
            trackLocation.exportData(formData)
            train.exportData(formData)
            coachNumber.exportData(formData)
            contactNumber.exportData(formData)
            details.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID       = formData.optString("id", "Not assigned")
            val incidentType = formData.getString("incident_type")
            val createdOn    = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nType: ${incidentType}\nCreated on: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  IncidentReport::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}