package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class LostProperty : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var fileUtil:                  FileUtil
    private lateinit var lostProperty:              FieldSpinner
    private lateinit var keptInPoliceStation:       FieldSpinner
    private lateinit var descrption:                FieldEditText
    private lateinit var foundIn:                   FieldSpinner
    private lateinit var foundOn:                   FieldEditText
    private lateinit var remarks:                   FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lost_property)
        supportActionBar!!.hide()

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
        lostProperty = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.LOST_PROPERTY_TYPES)!!),
            "lost_property_category",
            "Lost Property Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        keptInPoliceStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "kept_in_police_station",
            "Kept In Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        descrption = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "description",
            fieldName = "Description",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        foundIn = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.FOUND_IN_TYPES)!!),
            "found_in",
            "Found In",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        foundOn = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "found_on",
            fieldName = "Found On",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        descrption = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "description",
            fieldName = "Description",
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

        form.addView(lostProperty.getLayout())
        form.addView(keptInPoliceStation.getLayout())
        form.addView(descrption.getLayout())
        form.addView(foundIn.getLayout())
        form.addView(foundOn.getLayout())
        form.addView(remarks.getLayout())


        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_file).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
        }
    }

}