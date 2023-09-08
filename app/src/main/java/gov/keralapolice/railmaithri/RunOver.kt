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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RunOver : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var progressPB:    ProgressBar
    private lateinit var actionBT:      Button

    private lateinit var search:                FieldEditText
    private lateinit var dateFrom:              FieldEditText
    private lateinit var dateTo:                FieldEditText
    private lateinit var dateOfOccurrence:      FieldEditText
    private lateinit var timeOfOccurrence:      FieldEditText
    private lateinit var placeOfOccurrence:     FieldEditText
    private lateinit var betweenStation1:       FieldSpinner
    private lateinit var betweenStation2:       FieldSpinner
    private lateinit var sourceOfInfo:          FieldEditText
    private lateinit var runOverCategory:       FieldSpinner
    private lateinit var runOverCause:          FieldSpinner
    private lateinit var gender:                FieldSpinner
    private lateinit var age:                   FieldEditText
    private lateinit var name:                  FieldEditText
    private lateinit var address:               FieldEditText
    private lateinit var relativesContact:      FieldEditText
    private lateinit var victimDetails:         FieldEditText
    private lateinit var crimeNumber:           FieldEditText
    private lateinit var bodyIdentified:        FieldSpinner
    private lateinit var caseRegisteredIn:      FieldSpinner
    private lateinit var localPoliceStation:    FieldEditText
    private lateinit var district:              FieldSpinner
    private lateinit var railwayPoliceStation:  FieldSpinner
    private lateinit var identificationDetails: FieldEditText
    private lateinit var remarks:               FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.run_over)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        generateFields()

        bodyIdentified.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderFields()
            }
        }
        caseRegisteredIn.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                renderFields()
            }
        }

        actionBT.setOnClickListener { performAction() }
        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
        renderFields()
    }

    private fun renderFields() {
        if (mode == Mode.SEARCH_FORM){
            dateOfOccurrence.hide()
            timeOfOccurrence.hide()
            placeOfOccurrence.hide()
            betweenStation1.hide()
            betweenStation2.hide()
            sourceOfInfo.hide()
            age.hide()
            name.hide()
            address.hide()
            relativesContact.hide()
            victimDetails.hide()
            crimeNumber.hide()
            caseRegisteredIn.hide()
            localPoliceStation.hide()
            railwayPoliceStation.hide()
            district.hide()
            identificationDetails.hide()
            remarks.hide()

            actionBT.text = "Search"
        } else if(mode == Mode.VIEW_FORM || mode== Mode.UPDATE_FORM || mode == Mode.NEW_FORM){
            search.hide()
            dateFrom.hide()
            dateTo.hide()
            railwayPoliceStation.hide()
            localPoliceStation.hide()
            name.hide()
            age.hide()
            address.hide()
            gender.hide()
            relativesContact.hide()

            when (caseRegisteredIn.getData().toString()) {
                "Local Police Station" -> {
                    localPoliceStation.show()
                }
                "Railway Police Station" -> {
                    railwayPoliceStation.show()
                }
            }
            
            when (bodyIdentified.getData().toString()) {
                "true" -> {
                    name.show()
                    age.show()
                    address.show()
                    gender.show()
                    relativesContact.show()
                }
            }

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

    private fun loadFormData(formData: JSONObject){
        bodyIdentified.importData(formData)
        when (bodyIdentified.getData().toString()) {
            "true" -> {
                name.importData(formData)
                age.importData(formData)
                gender.importData(formData)
                address.importData(formData)
                relativesContact.importData(formData)
            }
        }
        caseRegisteredIn.importData(formData)
        when (caseRegisteredIn.getData().toString()) {
            "Local Police Station" -> {
                localPoliceStation.importData(formData)
            }
            "Railway Police Station" -> {
                railwayPoliceStation.importData(formData)
            }
        }

        val occurredOn = formData.getString("date_time_of_occurance").split("T").toTypedArray()
        formData.put("doc", occurredOn[0])
        formData.put("toc", occurredOn[1])
        dateOfOccurrence.importData(formData)
        timeOfOccurrence.importData(formData)

        placeOfOccurrence.importData(formData)
        betweenStation1.importData(formData)
        betweenStation2.importData(formData)
        sourceOfInfo.importData(formData)
        runOverCategory.importData(formData)
        runOverCause.importData(formData)
        caseRegisteredIn.importData(formData)
        district.importData(formData)
        identificationDetails.importData(formData)
        remarks.importData(formData)
        victimDetails.importData(formData)
        crimeNumber.importData(formData)
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            if (mode != Mode.SEARCH_FORM) {
                dateOfOccurrence.exportData(formData)
                timeOfOccurrence.exportData(formData)
                val occurredOn = dateOfOccurrence.getData() + "T"+ timeOfOccurrence.getData()
                formData.put("date_time_of_occurance", occurredOn)
            }
            search.exportData(formData)
            placeOfOccurrence.exportData(formData)
            betweenStation1.exportData(formData)
            betweenStation2.exportData(formData)
            sourceOfInfo.exportData(formData)
            runOverCategory.exportData(formData)
            runOverCause.exportData(formData)
            bodyIdentified.exportData(formData)
            caseRegisteredIn.exportData(formData)
            name.exportData(formData)
            age.exportData(formData)
            gender.exportData(formData)
            address.exportData(formData)
            relativesContact.exportData(formData)
            victimDetails.exportData(formData)
            crimeNumber.exportData(formData)
            localPoliceStation.exportData(formData)
            district.exportData(formData)
            railwayPoliceStation.exportData(formData)
            identificationDetails.exportData(formData)
            remarks.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun performAction() {
        when (mode) {
            Mode.NEW_FORM -> {
                val formData = getFormData()
                if (formData != null) {
                    val utcTime = Helper.getUTC()
                    formData.put("utc_timestamp", utcTime)
                    CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
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
                val uuid     = formData.getString("utc_timestamp")

                val updatedFormData = getFormData(formData)
                if (updatedFormData != null) {
                    Helper.saveFormData(this, formData, Storage.RUN_OVER, uuid)
                    finish()
                }
            }
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

    private fun generateFields() {
        search = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "search",
            fieldName = "Search",
            isRequired = false
        )
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
        dateOfOccurrence = FieldEditText(this,
            fieldType = "date",
            fieldLabel = "doc",
            fieldName = "Date of occurrence",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        timeOfOccurrence = FieldEditText(this,
            fieldType = "time",
            fieldLabel = "toc",
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
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "between_station_1",
            "Between station 1",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        betweenStation2 = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
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
            isRequired = Helper.resolveIsRequired(false, mode)
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
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        relativesContact = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "contact_number",
            fieldName = "Relatives contact",
            isRequired = Helper.resolveIsRequired(true, mode)
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
        caseRegisteredIn = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.STATION_TYPE)!!),
            "case_registered_in",
            "Case registered in",
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
        bodyIdentified = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.BOOLEAN_ANSWERS)!!),
            "is_identified",
            "Body identified ?",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.dynamic_fields)
        form.addView(search.getLayout())
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(dateOfOccurrence.getLayout())
        form.addView(timeOfOccurrence.getLayout())
        form.addView(placeOfOccurrence.getLayout())
        form.addView(betweenStation1.getLayout())
        form.addView(betweenStation2.getLayout())
        form.addView(sourceOfInfo.getLayout())
        form.addView(runOverCategory.getLayout())
        form.addView(runOverCause.getLayout())
        form.addView(bodyIdentified.getLayout())
        form.addView(caseRegisteredIn.getLayout())
        form.addView(name.getLayout())
        form.addView(age.getLayout())
        form.addView(gender.getLayout())
        form.addView(address.getLayout())
        form.addView(relativesContact.getLayout())
        form.addView(victimDetails.getLayout())
        form.addView(crimeNumber.getLayout())
        form.addView(localPoliceStation.getLayout())
        form.addView(railwayPoliceStation.getLayout())
        form.addView(district.getLayout())
        form.addView(identificationDetails.getLayout())
        form.addView(remarks.getLayout())
    }

    companion object {
        fun generateButton(
            context: Context,
            formData: JSONObject,
            mode: String? = Mode.VIEW_FORM
        ): Button {
            val formID      = formData.optString("id", "Run Over")
            val place       = formData.getString("place_of_occurance")
            val createdOn   = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nPlace: ${place}\nDate: $createdOn"

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