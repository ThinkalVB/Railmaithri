package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ContractStaff : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var fileUtil:              FileUtil

    private lateinit var category:              FieldSpinner
    private lateinit var name:                  FieldEditText
    private lateinit var age:                   FieldEditText
    private lateinit var gender:                FieldSpinner
    private lateinit var aadhar:                FieldEditText
    private lateinit var jobDetails:            FieldEditText
    private lateinit var mobileNumber:          FieldEditText
    private lateinit var address:               FieldEditText
    private lateinit var nativePoliceStation:   FieldEditText
    private lateinit var railwayStation:        FieldSpinner
    private lateinit var radioGroupisMigrant:   RadioGroup
    private lateinit var radioButtonYes:        RadioButton
    private lateinit var radioButtonNo:         RadioButton
    private lateinit var nativeState:           FieldSpinner
    private lateinit var search:                FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contract_staff)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
        renderFields()
    }
    private fun performAction() {
        when (mode) {
            Mode.NEW_FORM -> {
                val formData = getFormData()
                if (formData != null) {
                    val utcTime = Helper.getUTC()
                    formData.put("utc_timestamp", utcTime)

                    CoroutineScope(Dispatchers.IO).launch { sendFormData(formData) }
                }
            }

            Mode.SEARCH_FORM -> {
                var formData = getFormData()
                if (formData == null) {
                    formData = JSONObject()
                }
                val intent = Intent()
                intent.putExtra("parameters", formData.toString())
                setResult(RESULT_OK, intent)
                finish()
            }

            Mode.UPDATE_FORM -> {
                val formData = JSONObject(intent.getStringExtra("data")!!)
                val uuid = formData.getString("utc_timestamp")

                val updatedFormData = getFormData(formData)
                if (updatedFormData != null) {
                    storeFile(formData, uuid)
                    Helper.saveFormData(this, formData, Storage.CONTRACT_STAFF, uuid)
                    finish()
                }
            }
        }
    }
    private fun generateFields() {
        search = FieldEditText(
            this,
            fieldType = "text",
            fieldLabel = "search",
            fieldName = "Search",
            isRequired = false
        )
        category = FieldSpinner(
            this,
            JSONArray(Helper.getData(this, Storage.CONTRACT_STAFF_TYPES)!!),
            "staff_porter_category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        name = FieldEditText(
            this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        age = FieldEditText(
            this,
            fieldType = "number",
            fieldLabel = "age",
            fieldName = "Age",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        gender = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.GENDER_TYPES)!!),
            "gender",
            "Gender",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "phone",
            fieldLabel = "mobile_number",
            fieldName = "Mobile Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        aadhar = FieldEditText(this,
            fieldType = "aadhar",
            fieldLabel = "aadhar_number",
            fieldName = "Aadhar Number",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        jobDetails = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "job_details",
            fieldName = "Job Details",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        address = FieldEditText(
            this,
            fieldType = "multiline",
            fieldLabel = "address",
            fieldName = "Address",
            fieldHeight = 98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativeState = FieldSpinner(
            this,
            JSONArray(Helper.getData(this, Storage.STATES_LIST)!!),
            "native_state",
            "Native state",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativePoliceStation = FieldEditText(
            this,
            fieldType = "text",
            fieldLabel = "native_police_station",
            fieldName = "Native police station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(
            this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        val defaultState = "Kerala"
        nativeState.setSelectedItem(defaultState)

        val labelIsMigrant = TextView(this)
        labelIsMigrant.text = "Is Migrant"
        labelIsMigrant.textSize = 14f // Adjust the text size as needed
        labelIsMigrant.setTypeface(null, Typeface.BOLD)


        radioGroupisMigrant = RadioGroup(this)
        radioGroupisMigrant.id = View.generateViewId()

        radioButtonNo = RadioButton(this)
        radioButtonNo.id = View.generateViewId()
        radioButtonNo.text = "No"

        radioButtonYes = RadioButton(this)
        radioButtonYes.id = View.generateViewId()
        radioButtonYes.text = "Yes"

        radioButtonNo.isChecked = true
        radioGroupisMigrant.orientation = LinearLayout.HORIZONTAL
        //Radio Group
        radioGroupisMigrant.addView(radioButtonNo)
        radioGroupisMigrant.addView(radioButtonYes)

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(search.getLayout())
        form.addView(category.getLayout())
        form.addView(name.getLayout())
        form.addView(age.getLayout())
        form.addView(gender.getLayout())
        form.addView(aadhar.getLayout())
        form.addView(jobDetails.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(address.getLayout())
        form.addView(labelIsMigrant)
        form.addView(radioGroupisMigrant)
        form.addView(nativeState.getLayout())
        form.addView(nativePoliceStation.getLayout())
        form.addView(railwayStation.getLayout())

}
    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.CONTRACT_STAFF, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.CONTRACT_STAFF, uuid)
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

    private fun renderFields() {
        if (mode == Mode.SEARCH_FORM){
            fileUtil.hide()
            category.hide()
            name.hide()
            age.hide()
            gender.hide()
            aadhar.hide()
            jobDetails.hide()
            mobileNumber.hide()
            address.hide()
            nativePoliceStation.hide()
            railwayStation.hide()
            nativeState.hide()


            actionBT.text = "Search"
        } else if(mode == Mode.VIEW_FORM || mode== Mode.UPDATE_FORM || mode == Mode.NEW_FORM){
            search.hide()

            when (mode) {
                Mode.VIEW_FORM -> {
                    actionBT.visibility = View.GONE
                }
                Mode.UPDATE_FORM -> {
                    actionBT.text = "Update"
                }
                Mode.NEW_FORM -> {
                    actionBT.text = "Save"
                }
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            search.exportData(formData)
            category.exportData(formData)
            name.exportData(formData)
            age.exportData(formData)
            gender.exportData(formData)
            aadhar.exportData(formData)
            jobDetails.exportData(formData)
            mobileNumber.exportData(formData)
            address.exportData(formData)
            val isMigrantValue = radioButtonYes.isChecked
            formData.put("migrant_or_not", isMigrantValue)
            nativePoliceStation.exportData(formData)
            railwayStation.exportData(formData)
            nativeState.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        category.importData(formData)
        name.importData(formData)
        age.importData(formData)
        gender.importData(formData)
        age.importData(formData)
        aadhar.importData(formData)
        jobDetails.importData(formData)
        mobileNumber.importData(formData)
        address.importData(formData)
        val isMigrantValue = formData.optBoolean("migrant_or_not", false)
        if (isMigrantValue) {
            radioGroupisMigrant.check(radioButtonYes.id)
        } else {
            radioGroupisMigrant.check(radioButtonNo.id)
        }
        nativeState.importData(formData)
        nativePoliceStation.importData(formData)
        railwayStation.importData(formData)

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
        if (mode == Mode.VIEW_FORM) {
            fileUtil.registerLink(formData)
        }

    }
    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Contract Staff")
            val name      = formData.getString("name")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nName: ${name}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  ContractStaff::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}