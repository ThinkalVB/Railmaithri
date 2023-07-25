package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class AbandonedProperty : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var fileUtil:          FileUtil
    private lateinit var category:          FieldSpinner
    private lateinit var foundBy:           FieldEditText
    private lateinit var whetherSeized:     FieldEditText
    private lateinit var crimeDetails:      FieldEditText
    private lateinit var remarks:           FieldEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.abandoned_property)
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
        category = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.ABANDONED_PROPERTY_TYPES)!!),
            "abandoned_property_category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        foundBy = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "detected_by",
            fieldName = "Found By",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        whetherSeized = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "seized_or_not",
            fieldName = "Whether seized?",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        crimeDetails = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "crime_case_details",
            fieldName = "Crime Details",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        remarks = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "remarks",
            fieldName = "Remarks",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(false, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(category.getLayout())
        form.addView(foundBy.getLayout())
        form.addView(whetherSeized.getLayout())
        form.addView(crimeDetails.getLayout())
        form.addView(remarks.getLayout())

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_file).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE

        }
    }
}