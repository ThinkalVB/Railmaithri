package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        prepareActionButton()
        renderForm()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
    }

    private fun performAction() {
        if (mode == Mode.NEW_FORM){
            val formData = getFormData()
            if (formData != null) {
                val utcTime = Helper.getUTC()
                formData.put("utc_timestamp", utcTime)
                if (fileUtil.haveFile()) {
                    formData.put("__have_file", true)
                    formData.put("__file_name", fileUtil.getFileName())
                    fileUtil.saveFile(this, utcTime)
                } else {
                    formData.put("__have_file", false)
                    formData.put("__file_name", "No file")
                }

                val intent = Intent()
                intent.putExtra("data", formData.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        } else {
            return
        }
    }

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
        }
        if(mode == Mode.VIEW_FORM) {
            actionBT.visibility = View.GONE
        }
    }

    private fun loadFormData(formData: JSONObject) {
        name.importData(formData)
        gender.importData(formData)
        mobileNumber.importData(formData)
        aadhaarNumber.importData(formData)
        address.importData(formData)
        nativePoliceStation.importData(formData)
        nativeState.importData(formData)
        migrantOrNot.importData(formData)

        if (mode == Mode.VIEW_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            name.exportData(formData)
            gender.exportData(formData)
            mobileNumber.exportData(formData)
            aadhaarNumber.exportData(formData)
            address.exportData(formData)
            nativePoliceStation.exportData(formData)
            nativeState.exportData(formData)
            migrantOrNot.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
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
            fieldName  = "Mobile number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        aadhaarNumber = FieldEditText(this,
            fieldType  = "number",
            fieldLabel = "aadhaar_number",
            fieldName  = "Aadhaar number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        address = FieldEditText(this,
            fieldType  = "multiline",
            fieldLabel = "address",
            fieldName  = "Address",
            fieldHeight= 98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativeState = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.STATES_LIST)!!),
            "native_police_station",
            "Native state",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativePoliceStation = FieldEditText(this,
            fieldType  = "text",
            fieldLabel = "native_police_station",
            fieldName  = "Native police station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        migrantOrNot = FieldEditText(this,
            fieldType = "boolean",
            fieldLabel = "migrant_or_not",
            fieldName = "Migrant or not",
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

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID      = formData.optString("id", "Not assigned")
            val name        = formData.getString("name")
            val createdOn   = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nName: ${name}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  Labour::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}