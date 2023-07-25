package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray

class SurakshaSamithiMember : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var surakshaSamithi:   FieldSpinner
    private lateinit var name:              FieldEditText
    private lateinit var address:           FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var email:             FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.suraksha_samithi_member)
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
        surakshaSamithi = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.SURAKSHA_SAMITHI_LIST)!!),
            "suraksha_samithi",
            "Suraksha Samithi",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        name = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        address = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "address",
            fieldName = "Address",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "mobile_number",
            fieldName = "Mobile Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        email = FieldEditText(this,
            fieldType = "email",
            fieldLabel = "email",
            fieldName = "E-mail",
            isRequired = Helper.resolveIsRequired(false, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(surakshaSamithi.getLayout())
        form.addView(name.getLayout())
        form.addView(address.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(email.getLayout())
    }
}