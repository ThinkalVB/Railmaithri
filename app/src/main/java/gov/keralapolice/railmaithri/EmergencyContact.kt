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

class EmergencyContact : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var policeStation:     FieldSpinner
    private lateinit var district:          FieldSpinner
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var category:          FieldSpinner
    private lateinit var name:              FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var email:             FieldEditText
    private lateinit var remarks:           FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emergency_contact)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))

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
                    Helper.saveFormData(this, formData, Storage.EMERGENCY_CONTACTS, uuid)
                    finish()
                }
            }
        }
    }

    private fun generateFields() {
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        district = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.DISTRICTS_LIST)!!),
            "district",
            "District",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        category = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.CONTACT_TYPES)!!),
            "contacts_category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        name = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "contact_number",
            fieldName = "Mobile number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        email = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "email",
            fieldName = "E-mail",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        remarks = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "remarks",
            fieldName = "Remarks",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(policeStation.getLayout())
        form.addView(district.getLayout())
        form.addView(railwayStation.getLayout())
        form.addView(category.getLayout())
        form.addView(name.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(email.getLayout())
        form.addView(remarks.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.EMERGENCY_CONTACTS, formData, token)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.EMERGENCY_CONTACTS, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun renderFields() {
        locationUtil.hide()
        remarks.hide()

        if (mode == Mode.SEARCH_FORM) {
            actionBT.text = "Search"
        } else {
            locationUtil.show()
            remarks.show()

            if(mode == Mode.VIEW_FORM){
                actionBT.visibility = View.GONE
            } else{
                actionBT.text = "Save"
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try {
            policeStation.exportData(formData)
            district.exportData(formData)
            railwayStation.exportData(formData)
            category.exportData(formData)
            name.exportData(formData)
            mobileNumber.exportData(formData)
            email.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        policeStation.importData(formData)
        district.importData(formData)
        railwayStation.importData(formData)
        category.importData(formData)
        name.importData(formData)
        mobileNumber.importData(formData)
        email.importData(formData)
        remarks.importData(formData)

        if (mode == Mode.VIEW_FORM) {
            locationUtil.disableUpdate()
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val name      = formData.getString("name")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nName: ${name}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  EmergencyContact::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}