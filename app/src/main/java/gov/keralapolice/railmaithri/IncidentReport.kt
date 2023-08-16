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

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")
        if(mode == Mode.NEW_FORM){
            locationUtil.fetchLocation(this)
        }
        renderFields()
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
        dateTo.hide()
        incidentTypes.hide()
        railwayStation.hide()
        platformNumber.hide()
        trackLocation.hide()
        train.hide()
        coachNumber.hide()
        contactNumber.hide()
        details.hide()
        fileUtil.hide()
        locationUtil.hide()

        incidentTypes.show()
        if (mode == Mode.UPDATE_FORM || mode == Mode.NEW_FORM || mode == Mode.SEARCH_FORM){
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
                details.show()
                fileUtil.show()
                locationUtil.show()
                actionBT.text = "Save"
            }
        }
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