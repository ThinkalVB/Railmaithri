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

class StrangerCheck : AppCompatActivity() {
    private lateinit var mode:                  String
    private lateinit var progressPB:            ProgressBar
    private lateinit var actionBT:              Button

    private lateinit var locationUtil:          LocationUtil
    private lateinit var fileUtil:              FileUtil

    private lateinit var dateFrom:              FieldEditText
    private lateinit var dateTo:                FieldEditText
    private lateinit var policeStation:         FieldSpinner
    private lateinit var name:                  FieldEditText
    private lateinit var identificationMarks:   FieldEditText
    private lateinit var purposeOfVisit:        FieldEditText
    private lateinit var age:                   FieldEditText
    private lateinit var emailID:               FieldEditText
    private lateinit var mobileNumber:          FieldEditText
    private lateinit var languagesKnown:        FieldEditText
    private lateinit var placeOfCheck:          FieldEditText
    private lateinit var isForeigner:           FieldSpinner
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

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
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
                    val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
                    val stationID = profile.getJSONArray("police_station").getJSONObject(0).getInt("id")
                    val utcTime   = Helper.getUTC()
                    formData.put("checking_date_time", utcTime)
                    formData.put("police_station", stationID)
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
                val uuid     = formData.getString("checking_date_time")

                val updatedFormData = getFormData(formData)
                if (updatedFormData != null) {
                    storeFile(formData, uuid)
                    Helper.saveFormData(this, formData, Storage.STRANGER_CHECK, uuid)
                    finish()
                }
            }
        }
    }

    private fun generateFields() {
        dateFrom = FieldEditText(this,
            fieldType  = "date",
            fieldLabel = "checking_date_time__gte",
            fieldName  = "Date from",
            isRequired = false
        )
        dateTo = FieldEditText(this,
            fieldType  = "date",
            fieldLabel = "checking_date_time__lte",
            fieldName  = "Date to",
            isRequired = false
        )
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
        isForeigner = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.BOOLEAN_ANSWERS)!!),
            "is_foreigner",
            "Is foreigner",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
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
        policeStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "police_station",
            "Police Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(policeStation.getLayout())
        form.addView(name.getLayout())
        form.addView(identificationMarks.getLayout())
        form.addView(purposeOfVisit.getLayout())
        form.addView(age.getLayout())
        form.addView(emailID.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(landPhoneNumber.getLayout())
        form.addView(languagesKnown.getLayout())
        form.addView(placeOfCheck.getLayout())
        form.addView(isForeigner.getLayout())
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
        val response = Helper.sendFormData(URL.STRANGER_CHECK, formData, token, fileUtil)

        val uuid = formData.getString("checking_date_time")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            storeFile(formData, uuid)
            Helper.saveFormData(this, formData, Storage.STRANGER_CHECK, uuid)
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
            policeStation.hide()
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

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            policeStation.exportData(formData)
            locationUtil.exportLocation(formData)
            dateFrom.exportData(formData, tailPadding = "T00:00:00")
            dateTo.exportData(formData, tailPadding = "T23:59:59")
            name.exportData(formData)
            identificationMarks.exportData(formData)
            purposeOfVisit.exportData(formData)
            age.exportData(formData)
            emailID.exportData(formData)
            mobileNumber.exportData(formData)
            landPhoneNumber.exportData(formData)
            languagesKnown.exportData(formData)
            placeOfCheck.exportData(formData)
            isForeigner.exportData(formData)
            nativeState.exportData(formData)
            nativePoliceStation.exportData(formData)
            nativeAddress.exportData(formData)
            idCardDetails.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        policeStation.importData(formData)
        name.importData(formData)
        identificationMarks.importData(formData)
        purposeOfVisit.importData(formData)
        age.importData(formData)
        emailID.importData(formData)
        mobileNumber.importData(formData)
        landPhoneNumber.importData(formData)
        languagesKnown.importData(formData)
        placeOfCheck.importData(formData)
        isForeigner.importData(formData)
        nativeState.importData(formData)
        nativePoliceStation.importData(formData)
        nativeAddress.importData(formData)
        idCardDetails.importData(formData)
        remarks.importData(formData)
        locationUtil.importLocation(formData)

        if (mode == Mode.UPDATE_FORM && formData.getBoolean("__have_file")){
            val uuid     = formData.getString("checking_date_time")
            val fileName = formData.getString("__file_name")
            fileUtil.loadFile(this, uuid , fileName)
        } else {
            fileUtil.registerLink(formData)
            locationUtil.disableUpdate()
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val name      = formData.getString("name")
            val createdOn = formData.getString("checking_date_time")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nName: ${name}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  StrangerCheck::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}