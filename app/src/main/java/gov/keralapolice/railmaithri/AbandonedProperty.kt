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
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
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
                CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
            }
        } else if (mode == Mode.SEARCH_FORM) {
            var formData = getFormData()
            if (formData == null){
                formData = JSONObject()
            }
            val intent = Intent()
            intent.putExtra("parameters", formData.toString())
            setResult(RESULT_OK, intent)
            finish()
        } else if (mode == Mode.UPDATE_FORM){
            val formData = JSONObject(intent.getStringExtra("data")!!)
            val uuid     = formData.getString("utc_timestamp")
            getFormData(formData)
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.ABANDONED_PROPERTY, uuid)
            finish()
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
        }
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.ABANDONED_PROPERTY, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.ABANDONED_PROPERTY, uuid)
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

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            category.exportData(formData)
            foundBy.exportData(formData)
            whetherSeized.exportData(formData)
            crimeDetails.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }

        if (mode != Mode.NEW_FORM || mode == Mode.UPDATE_FORM) {
            val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
            val officerID = profile.getInt("id")
            formData.put("added_by", officerID)
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        category.importData(formData)
        foundBy.importData(formData)
        whetherSeized.importData(formData)
        crimeDetails.importData(formData)
        remarks.importData(formData)

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("utc_timestamp")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val category  = formData.getString("abandoned_property_category")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nCategory: ${category}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  AbandonedProperty::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}