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
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var incidentType:      Spinner
    private lateinit var fileUtil:          FileUtil
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var platformNumber:    FieldEditText
    private lateinit var trackLocation:     FieldEditText
    private lateinit var train:             FieldSpinner
    private lateinit var coachNumber:       FieldEditText
    private lateinit var contactNumber:     FieldEditText
    private lateinit var details:           FieldEditText

    private val PLATFORM = 0
    private val TRACK    = 1
    private val TRAIN    = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incident_report)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")
        incidentType = findViewById(R.id.incident_type)
        incidentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderForm(position)
            }
        }

        prepareActionButton()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
    }

    private fun loadFormData(formData: JSONObject) {
        Log.e("Railmaithri", formData.toString())
        when (formData.getString("incident_type")) {
            "Platform" -> {
                incidentType.setSelection(PLATFORM)
                renderForm(PLATFORM)
                platformNumber.importData(formData)
                railwayStation.importData(formData)
            }
            "Track" -> {
                incidentType.setSelection(TRACK)
                renderForm(TRACK)
                railwayStation.importData(formData)
                trackLocation.importData(formData)
            }
            "Train" -> {
                incidentType.setSelection(TRAIN)
                renderForm(TRAIN)
                train.exportData(formData)
                coachNumber.importData(formData)
                contactNumber.importData(formData)
            }
        }
        details.importData(formData)

        val latitude  = formData.getDouble("latitude")
        val longitude = formData.getDouble("longitude")
        var accuracy  = 0.0f
        if (mode == Mode.UPDATE_FORM) {
            accuracy = formData.getDouble("accuracy").toFloat()
        }
        locationUtil.importLocation(latitude, longitude, accuracy)
        if(mode == Mode.VIEW_FORM){
            locationUtil.disableUpdate()
        }

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    private fun performAction() {
        if (mode == Mode.NEW_FORM){
            val formData = getFormData()
            if (formData != null) {
                val utcTime = Helper.getUTC()
                formData.put("incident_date_time", utcTime)
                formData.put("data_from", "Beat Officer")
                formData.put("utc_timestamp", utcTime)
                CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
            }
        } else if (mode == Mode.SEARCH_FORM) {
            var formData = getFormData()
            if (formData == null) {
                formData = JSONObject()
            }
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("search_url", URL.INCIDENT_REPORT)
            intent.putExtra("parameters", formData.toString())
            startActivity(intent)
        } else if (mode == Mode.UPDATE_FORM){
            val formData = JSONObject(intent.getStringExtra("data")!!)
            val uuid     = formData.getString("utc_timestamp")
            getFormData(formData)
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.INCIDENT_REPORT, uuid)
            finish()
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        if (mode == Mode.NEW_FORM){
            if (!locationUtil.haveLocation()) {
                Helper.showToast(this, "Location is mandatory")
                return null
            } else {
                locationUtil.exportLocation(formData)
            }
        }

        try{
            when (incidentType.selectedItemPosition) {
                PLATFORM -> {
                    formData.put("incident_type", "Platform")
                    platformNumber.exportData(formData)
                    railwayStation.exportData(formData)
                }
                TRACK -> {
                    formData.put("incident_type", "Track")
                    railwayStation.exportData(formData)
                    trackLocation.exportData(formData)
                }
                TRAIN -> {
                    formData.put("incident_type", "Train")
                    train.exportData(formData)
                    coachNumber.exportData(formData)
                    contactNumber.exportData(formData)
                }
            }
            details.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.INCIDENT_REPORT, formData, token, fileUtil)
        Helper.showToast(this, response.second)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            finish()
        } else if (response.first == ResponseType.NETWORK_ERROR) {
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

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
            locationUtil.fetchLocation(this)
        }
        if(mode == Mode.UPDATE_FORM) {
            actionBT.text = "Update"
        }
        if(mode == Mode.VIEW_FORM) {
            actionBT.visibility = View.GONE
        }
        if(mode == Mode.SEARCH_FORM) {
            actionBT.text = "Search"
        }
    }

    private fun generateFields() {
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
            fieldType = "number",
            fieldLabel = "coach_number",
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

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
        }
    }

    private fun renderForm(position: Int) {
        val form = findViewById<LinearLayout>(R.id.dynamic_fields)
        form.removeAllViews()
        when (position) {
            PLATFORM -> {
                form.addView(railwayStation.getLayout())
                form.addView(platformNumber.getLayout())
            }
            TRACK -> {
                form.addView(railwayStation.getLayout())
                form.addView(trackLocation.getLayout())
            }
            TRAIN -> {
                form.addView(train.getLayout())
                form.addView(coachNumber.getLayout())
                form.addView(contactNumber.getLayout())
            }
        }
        form.addView(details.getLayout())
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