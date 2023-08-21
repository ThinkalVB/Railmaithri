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

class LostProperty : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var fileUtil:              FileUtil
    private lateinit var dateFrom:              FieldEditText
    private lateinit var dateTo:                FieldEditText
    private lateinit var lostPropertyCategory:  FieldSpinner
    private lateinit var descrption:            FieldEditText
    private lateinit var foundIn:               FieldSpinner
    private lateinit var foundOn:               FieldEditText
    private lateinit var remarks:               FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lost_property)
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
                    val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
                    val stationID = profile.getJSONArray("police_station").getJSONObject(0).getInt("id")
                    val utcTime   = Helper.getUTC()
                    formData.put("utc_timestamp", utcTime)
                    formData.put("kept_in_police_station", stationID)
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
                    Helper.saveFormData(this, formData, Storage.LOST_PROPERTY, uuid)
                    finish()
                }
            }
        }
    }

    private fun renderFields() {
        dateFrom.hide()
        dateTo.hide()
        fileUtil.hide()

        if (mode == Mode.SEARCH_FORM) {
            dateFrom.show()
            dateTo.show()
            actionBT.text = "Search"
        } else {
            fileUtil.show()
            if(mode == Mode.VIEW_FORM){
                actionBT.visibility = View.GONE
            } else{
                actionBT.text = "Save"
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
        lostPropertyCategory = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.LOST_PROPERTY_TYPES)!!),
            "lost_property_category",
            "Lost Property Category",
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
            fieldType = "date",
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
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(lostPropertyCategory.getLayout())
        form.addView(descrption.getLayout())
        form.addView(foundIn.getLayout())
        form.addView(foundOn.getLayout())
        form.addView(remarks.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.LOST_PROPERTY, formData, token, fileUtil)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.LOST_PROPERTY, uuid)
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

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        if (mode == Mode.NEW_FORM){
            if (!fileUtil.haveFile()) {
                Helper.showToast(this, "File is mandatory")
                return null
            }
        }

        try{
            dateFrom.exportData(formData, tailPadding = "T00:00:00")
            dateTo.exportData(formData, tailPadding = "T23:59:59")
            lostPropertyCategory.exportData(formData)
            descrption.exportData(formData)
            foundIn.exportData(formData)
            foundOn.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        lostPropertyCategory.importData(formData)
        descrption.importData(formData)
        foundIn.importData(formData)
        foundOn.importData(formData)
        remarks.importData(formData)

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
            val formID    = formData.optString("id", "Not assigned")
            val category  = formData.getString("lost_property_category")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\ncategory: ${category}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  LostProperty::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }

}