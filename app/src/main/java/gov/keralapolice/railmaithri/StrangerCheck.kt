package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

class StrangerCheck : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var locationUtil:          LocationUtil
    private lateinit var fileUtil:              FileUtil
    private lateinit var name:                  FieldEditText
    private lateinit var identificationMarks:   FieldEditText
    private lateinit var purposeOfVisit:        FieldEditText
    private lateinit var age:                   FieldEditText
    private lateinit var emailID:               FieldEditText
    private lateinit var mobileNumber:          FieldEditText
    private lateinit var languagesKnown:        FieldEditText
    private lateinit var placeOfCheck:          FieldEditText
    private lateinit var nativeState:           FieldSpinner
    private lateinit var nativePoliceStation:   FieldEditText
    private lateinit var nativeAddress:         FieldEditText
    private lateinit var remarks:               FieldEditText
    private lateinit var landPhoneNumber:       FieldEditText
    private lateinit var idCardDetails:         FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stranger_check)
        supportActionBar!!.hide()

        mode        = intent.getStringExtra("mode")!!
        progressPB  = findViewById(R.id.progress_bar)
        actionBT    = findViewById(R.id.action)

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))
        fileUtil     = FileUtil(this, findViewById(R.id.ly_file))

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
                formData.put("checking_date_time", utcTime)
                formData.put("utc_timestamp",      utcTime)
                CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
            }
        } else if (mode == Mode.SEARCH_FORM) {
            var formData = getFormData()
            if (formData == null){
                formData = JSONObject()
            }
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("search_url", URL.STRANGER_CHECK)
            intent.putExtra("parameters", formData.toString())
            startActivity(intent)
        } else if (mode == Mode.UPDATE_FORM){
            val formData = JSONObject(intent.getStringExtra("data")!!)
            getFormData(formData)
            Helper.saveFormData(this, formData, Storage.STRANGER_CHECK)
            finish()
        }
    }

    private fun renderForm() {
        name = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        identificationMarks = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "identification_marks_details",
            fieldName = "Identification marks",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        purposeOfVisit = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "purpose_of_visit",
            fieldName = "Purpose of visit",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        age = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "age",
            fieldName = "Age",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        emailID = FieldEditText(this,
            fieldType = "email",
            fieldLabel = "email",
            fieldName = "Email ID",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "mobile_number",
            fieldName = "Phone number",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        languagesKnown = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "languages_known",
            fieldName = "Languages known",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        placeOfCheck = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "place_of_check",
            fieldName = "Place of check",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativeState = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.STATES_LIST)!!),
            "native_state",
            "Native state",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativePoliceStation = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "native_police_station",
            fieldName = "Native police station",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nativeAddress = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "native_address",
            fieldName = "Native address",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        remarks = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "remarks",
            fieldName = "Remarks",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        landPhoneNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "land_phone_number",
            fieldName = "Land Phone number",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        idCardDetails = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "id_card_details",
            fieldName = "ID card details",
            isRequired = Helper.resolveIsRequired(false, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(name.getLayout())
        form.addView(identificationMarks.getLayout())
        form.addView(purposeOfVisit.getLayout())
        form.addView(age.getLayout())
        form.addView(emailID.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(landPhoneNumber.getLayout())
        form.addView(languagesKnown.getLayout())
        form.addView(placeOfCheck.getLayout())
        form.addView(nativeState.getLayout())
        form.addView(nativePoliceStation.getLayout())
        form.addView(nativeAddress.getLayout())
        form.addView(idCardDetails.getLayout())
        form.addView(remarks.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.STRANGER_CHECK, formData, token)

        Helper.showToast(this, response.second)
        if(response.first == ResponseType.SUCCESS) {
            val key = formData.getString("utc_timestamp")
            Helper.removeFormData(this, key, Storage.STRANGER_CHECK)
            finish()
        } else if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.STRANGER_CHECK)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
            locationUtil.fetchLocation(this)
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
        if (!locationUtil.haveLocation()) {
            Helper.showToast(this, "Location is mandatory")
            return null
        } else {
            locationUtil.exportLocation(formData)
        }

        try{
            name.exportData(formData)
            identificationMarks.exportData(formData)
            purposeOfVisit.exportData(formData)
            age.exportData(formData)
            emailID.exportData(formData)
            mobileNumber.exportData(formData)
            landPhoneNumber.exportData(formData)
            languagesKnown.exportData(formData)
            placeOfCheck.exportData(formData)
            nativeState.exportData(formData)
            nativePoliceStation.exportData(formData)
            nativeAddress.exportData(formData)
            idCardDetails.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }

        val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        val officerID = profile.getJSONArray("user_group_detail").getJSONObject(0).getInt("boundary")
        formData.put("police_station", officerID)
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        name.importData(formData)
        identificationMarks.importData(formData)
        purposeOfVisit.importData(formData)
        age.importData(formData)
        emailID.importData(formData)
        mobileNumber.importData(formData)
        landPhoneNumber.importData(formData)
        languagesKnown.importData(formData)
        placeOfCheck.importData(formData)
        nativeState.importData(formData)
        nativePoliceStation.importData(formData)
        nativeAddress.importData(formData)
        idCardDetails.importData(formData)
        remarks.importData(formData)
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val train     = formData.getString("train")
            val createdOn = formData.getString("last_updated")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nTrain: ${train}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  PassengerStatistics::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}