package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class Labour : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var fileUtil:              FileUtil
    private lateinit var name:                  FieldEditText
    private lateinit var gender:                FieldSpinner
    private lateinit var mobileNumber:          FieldEditText
    private lateinit var aadhaarNumber:         FieldEditText
    private lateinit var address:               FieldEditText
    private lateinit var nativePoliceStation:   FieldEditText
    private lateinit var nativeState:           FieldSpinner
    private lateinit var migrantOrNot:          FieldEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.labour)
        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        prepareActionButton()
        renderForm()
    }
    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
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
        name = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        gender = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.GENDER_TYPES)!!),
            "gender",
            "Gender",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType  = "number",
            fieldLabel = "mobile_number",
            fieldName  = "Mobile Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        aadhaarNumber = FieldEditText(this,
            fieldType  = "number",
            fieldLabel = "aadhaar_number",
            fieldName  = "Aadhaar Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        address = FieldEditText(this,
            fieldType  = "multiline",
            fieldLabel = "address",
            fieldName  = "Address",
            fieldHeight= 98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativePoliceStation = FieldEditText(this,
            fieldType  = "text",
            fieldLabel = "native_police_station",
            fieldName  = "Native Police Station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativeState = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.STATES_LIST)!!),
            "native_police_station",
            "Native Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        migrantOrNot = FieldEditText(this,
            fieldType = "boolean",
            fieldLabel = "migrant_or_not",
            fieldName = "Migrant Or Not",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(name.getLayout())
        form.addView(gender.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(aadhaarNumber.getLayout())
        form.addView(address.getLayout())
        form.addView(nativePoliceStation.getLayout())
        form.addView(nativeState.getLayout())
        form.addView(migrantOrNot.getLayout())

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
        }
    }

}