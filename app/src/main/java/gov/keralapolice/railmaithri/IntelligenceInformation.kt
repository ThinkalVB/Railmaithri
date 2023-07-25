package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import org.json.JSONObject

class IntelligenceInformation : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var fileUtil:          FileUtil
    private lateinit var intelligenceType:  FieldSpinner
    private lateinit var severity:          FieldSpinner
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var information:       FieldEditText
    private lateinit var remarks:           FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intelligence_information)
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
        intelligenceType = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.INTELLIGENCE_TYPES)!!),
            "intelligence_type",
            "Intelligence Type",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        severity = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.INTELLIGENCE_SEVERITY_TYPES)!!),
            "severity",
            "Severity",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "mobile_number",
            fieldName = "Phone number",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        information = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "information",
            fieldName = "Information",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        remarks = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "remarks",
            fieldName = "Remarks",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(false, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(intelligenceType.getLayout())
        form.addView(severity.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(information.getLayout())
        form.addView(remarks.getLayout())

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_file).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
        }
    }

}