package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class ReliablePerson : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var name:              FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var policeStation:     FieldSpinner
    private lateinit var place:             FieldEditText
    private lateinit var description:       FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reliable_person)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

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
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "mobile_number",
            fieldName = "Mobile number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        place = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "information",
            fieldName = "Information",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        description = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "description",
            fieldName = "Description",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(name.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(policeStation.getLayout())
        form.addView(place.getLayout())
        form.addView(description.getLayout())
    }
}