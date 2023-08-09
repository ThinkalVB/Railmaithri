package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RunOver : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var progressPB:    ProgressBar
    private lateinit var actionBT:      Button

    private lateinit var dateOfOccurrence:      FieldEditText
    private lateinit var timeOfOccurrence:      FieldEditText
    private lateinit var placeOfOccurrence:     FieldEditText
    private lateinit var betweenStation1:       FieldSpinner
    private lateinit var betweenStation2:       FieldSpinner
    private lateinit var sourceOfInfo:          FieldEditText
    private lateinit var runOverCategory:       FieldSpinner
    private lateinit var runOverCause:          FieldSpinner
    private lateinit var gender:                FieldSpinner
    private lateinit var contactNumber:         FieldEditText
    private lateinit var age:                   FieldEditText
    private lateinit var name:                  FieldEditText
    private lateinit var address:               FieldEditText
    private lateinit var relativesContact:      FieldEditText
    private lateinit var victimDetails:         FieldEditText
    private lateinit var crimeNumber:           FieldEditText
    private lateinit var bodyIdentified:        Spinner
    private lateinit var caseRegisteredIn:      Spinner
    private lateinit var localPoliceStation:    FieldEditText
    private lateinit var district:              FieldSpinner
    private lateinit var railwayPoliceStation:  FieldSpinner
    private lateinit var identificationDetails: FieldEditText
    private lateinit var remarks:               FieldEditText

    private val NO   = 1
    private val YES  = 0

    private val RAILWAY_POLCIE_STATION = 0
    private val LOCAL_POLICE_STATION   = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.run_over)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

        bodyIdentified   = findViewById(R.id.body_identified)
        caseRegisteredIn = findViewById(R.id.case_registered_in)
        bodyIdentified.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderForm(position)
            }
        }

        prepareActionButton()
        generateFields()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
    }

    private fun loadFormData(formData: JSONObject) {
        when (formData.getBoolean("is_identified")) {
            true -> {
                bodyIdentified.setSelection(YES)
                renderForm(YES)
                name.importData(formData)
                address.importData(formData)
                relativesContact.importData(formData)
            }
            false -> {
                bodyIdentified.setSelection(NO)
                renderForm(NO)
                victimDetails.importData(formData)
                crimeNumber.importData(formData)
                railwayPoliceStation.importData(formData)
                localPoliceStation.importData(formData)
            }
        }
        dateOfOccurrence.importData(formData)
        timeOfOccurrence.importData(formData)
        placeOfOccurrence.importData(formData)
        betweenStation1.importData(formData)
        betweenStation2.importData(formData)
        contactNumber.importData(formData)
        runOverCategory.importData(formData)
        runOverCause.importData(formData)
        sourceOfInfo.importData(formData)
        age.importData(formData)
        district.importData(formData)
        identificationDetails.importData(formData)
        remarks.importData(formData)
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
            if (formData == null) {
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
            Helper.saveFormData(this, formData, Storage.RUN_OVER, uuid)
            finish()
        }
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.RUN_OVER, formData, token)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }

        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.RUN_OVER, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            dateOfOccurrence.exportData(formData)
            timeOfOccurrence.exportData(formData)
            val occurredOn = formData.getString("date_of_occurance") + "T"+ formData.getString("time_of_occurance")
            formData.put("date_time_of_occurance", occurredOn)
            placeOfOccurrence.exportData(formData)
            betweenStation1.exportData(formData)
            betweenStation2.exportData(formData)
            runOverCategory.exportData(formData)
            runOverCause.exportData(formData)
            sourceOfInfo.exportData(formData)
            age.exportData(formData)
            identificationDetails.exportData(formData)
            remarks.exportData(formData)
            gender.exportData(formData)
            contactNumber.exportData(formData)
            identificationDetails.exportData(formData)
            remarks.exportData(formData)
            district.exportData(formData)
            when (caseRegisteredIn.selectedItemPosition) {
                RAILWAY_POLCIE_STATION -> {
                    formData.put("case_registered_in", "Railway Police Station")
                }
                LOCAL_POLICE_STATION -> {
                    formData.put("case_registered_in", "Local Police Station")
                }
            }

            when (bodyIdentified.selectedItemPosition) {
                YES -> {
                    formData.put("is_identified", true)
                    name.exportData(formData)
                    address.exportData(formData)
                    relativesContact.exportData(formData)
                }
                NO -> {
                    formData.put("is_identified", false)
                    victimDetails.exportData(formData)
                    crimeNumber.exportData(formData)
                    railwayPoliceStation.exportData(formData)
                    localPoliceStation.exportData(formData)
                    district.exportData(formData)
                }
            }
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun renderForm(position: Int) {
        val form = findViewById<LinearLayout>(R.id.dynamic_fields)
        form.removeAllViews()
        form.addView(dateOfOccurrence.getLayout())
        form.addView(timeOfOccurrence.getLayout())
        form.addView(placeOfOccurrence.getLayout())
        form.addView(betweenStation1.getLayout())
        form.addView(betweenStation2.getLayout())
        form.addView(contactNumber.getLayout())
        form.addView(runOverCategory.getLayout())
        form.addView(runOverCause.getLayout())
        form.addView(sourceOfInfo.getLayout())
        form.addView(age.getLayout())
        form.addView(district.getLayout())
        when (position) {
            YES -> {
                form.addView(name.getLayout())
                form.addView(address.getLayout())
                form.addView(relativesContact.getLayout())
            }
            NO -> {
                form.addView(victimDetails.getLayout())
                form.addView(crimeNumber.getLayout())
                form.addView(railwayPoliceStation.getLayout())
                form.addView(localPoliceStation.getLayout())
            }
        }
        form.addView(identificationDetails.getLayout())
        form.addView(remarks.getLayout())
    }

    private fun generateFields() {
        dateOfOccurrence = FieldEditText(this,
            fieldType = "date",
            fieldLabel = "date_of_occurance",
            fieldName = "Date of occurrence",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        timeOfOccurrence = FieldEditText(this,
            fieldType = "time",
            fieldLabel = "time_of_occurance",
            fieldName = "Time of occurrence",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        placeOfOccurrence = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "place_of_occurance",
            fieldName = "Place of occurrence",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        betweenStation1 = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "between_station_1",
            "Between station 1",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        betweenStation2 = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "between_station_2",
            "Between station 2",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        runOverCategory = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RUN_OVER_TYPES)!!),
            "category",
            "Category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        runOverCause = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RUN_OVER_CAUSE_TYPES)!!),
            "cause",
            "Cause",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        sourceOfInfo = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "source_of_information",
            fieldName = "Source of information",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        contactNumber = FieldEditText(this,
            fieldType = "phone",
            fieldLabel = "contact_number",
            fieldName = "Contact number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        gender = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.GENDER_TYPES)!!),
            "gender",
            "Gender",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        age = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "age",
            fieldName = "Age",
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
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        relativesContact = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "contact_number",
            fieldName = "Relatives contact",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        victimDetails = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "victim_details",
            fieldName = "Victim details",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        crimeNumber = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "crime_number",
            fieldName = "Crime number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayPoliceStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.POLICE_STATIONS_LIST)!!),
            "railway_police_station",
            "Railway police station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        localPoliceStation = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "local_police_station",
            fieldName = "Local police station",
            isRequired = Helper.resolveIsRequired(false, mode)
        )
        district = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.DISTRICTS_LIST)!!),
            "district",
            "District",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        identificationDetails = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "identification_details",
            fieldName = "Identification details",
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

    companion object {
        fun generateButton(
            context: Context,
            formData: JSONObject,
            mode: String? = Mode.VIEW_FORM
        ): Button {
            val formID       = formData.optString("id", "Not assigned")
            val isIdentified = formData.getString("is_identified")
            val createdOn    = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nIdentified: ${isIdentified}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context, RunOver::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}