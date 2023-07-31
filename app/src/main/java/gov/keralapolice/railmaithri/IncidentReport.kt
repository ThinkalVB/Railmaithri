package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class IncidentReport : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var fileUtil:          FileUtil
    private lateinit var incidentType:      FieldSpinner
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var platformNumber:    FieldEditText
    private lateinit var tracklocation:     FieldEditText
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

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        prepareActionButton()
        renderForm()
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

    private fun renderForm() {
        incidentType = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.INCIDENT_REPORT)!!),
            "incident_type",
            "Incident Type",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        platformNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "platform_number",
            fieldName = "Platform Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        tracklocation = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "track_location",
            fieldName = "Track Location",
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
            fieldName = "Coach Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        contactNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "coach_number",
            fieldName = "mobile_number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        details = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "platform_number",
            fieldHeight= 98,
            fieldName = "details",

            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(incidentType.getLayout())
        form.addView(railwayStation.getLayout())
        form.addView(platformNumber.getLayout())
        form.addView(tracklocation.getLayout())
        form.addView(train.getLayout())
        form.addView(coachNumber.getLayout())
        form.addView(contactNumber.getLayout())
        form.addView(details.getLayout())

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
        }
    }
}