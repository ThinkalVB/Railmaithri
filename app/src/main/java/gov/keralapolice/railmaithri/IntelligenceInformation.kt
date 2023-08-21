package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class IntelligenceInformation : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var fileUtil:          FileUtil
    private lateinit var dateFrom:          FieldEditText
    private lateinit var dateTo:            FieldEditText
    private lateinit var intelligenceType:  FieldSpinner
    private lateinit var severity:          FieldSpinner
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var information:       FieldEditText
    private lateinit var remarks:           FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intelligence_information)
        supportActionBar!!.hide()

        mode = intent.getStringExtra("mode")!!
        progressPB = findViewById(R.id.progress_bar)
        actionBT = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file), "photo")

        if (mode == Mode.NEW_FORM) {
            locationUtil.fetchLocation(this)
        }
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
                    val utcTime   = Helper.getUTC()
                    val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
                    val officerID = profile.getInt("id")
                    formData.put("utc_timestamp", utcTime)
                    formData.put("informer", officerID)
                    formData.put("data_from", "Beat Officer")
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
                    Helper.saveFormData(this, formData, Storage.INTELLIGENCE_INFORMATION, uuid)
                    finish()
                }
            }
        }
    }

    private fun generateFields() {
        dateFrom = FieldEditText(this,
            fieldType  = "date",
            fieldLabel = "utc_timestamp__gte",
            fieldName  = "Date from",
            isRequired = false
        )
        dateTo = FieldEditText(this,
            fieldType  = "date",
            fieldLabel = "utc_timestamp__lte",
            fieldName  = "Date to",
            isRequired = false
        )
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
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(intelligenceType.getLayout())
        form.addView(severity.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(information.getLayout())
        form.addView(remarks.getLayout())
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            locationUtil.exportLocation(formData)
            dateFrom.exportData(formData, tailPadding = "T00:00:00")
            dateTo.exportData(formData, tailPadding = "T23:59:59")
            intelligenceType.exportData(formData)
            severity.exportData(formData)
            mobileNumber.exportData(formData)
            information.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.INTELLIGENCE_INFORMATION, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.INTELLIGENCE_INFORMATION, uuid)
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
        dateFrom.hide()
        dateTo.hide()
        fileUtil.hide()
        locationUtil.hide()

        if (mode == Mode.SEARCH_FORM) {
            dateFrom.show()
            dateTo.show()
            actionBT.text = "Search"
        } else {
            fileUtil.show()
            locationUtil.show()

            if(mode == Mode.VIEW_FORM){
                actionBT.visibility = View.GONE
            } else{
                actionBT.text = "Save"
            }
        }
    }

    private fun loadFormData(formData: JSONObject) {
        intelligenceType.importData(formData)
        severity.importData(formData)
        mobileNumber.importData(formData)
        information.importData(formData)
        remarks.importData(formData)
        locationUtil.importLocation(formData)

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
        if (mode == Mode.VIEW_FORM) {
            fileUtil.registerLink(formData)
            locationUtil.disableUpdate()
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID           = formData.optString("id", "Not assigned")
            val intelligenceType = formData.getString("intelligence_type")
            val createdOn        = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nType: ${intelligenceType}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  IntelligenceInformation::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}