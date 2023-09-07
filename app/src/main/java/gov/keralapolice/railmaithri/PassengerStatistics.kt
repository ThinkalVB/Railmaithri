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

class PassengerStatistics : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var dateFrom:          FieldEditText
    private lateinit var dateTo:            FieldEditText
    private lateinit var train:             FieldSpinner
    private lateinit var coachNumber:       FieldEditText
    private lateinit var density:           FieldSpinner
    private lateinit var compartmentType:   FieldSpinner
    private lateinit var search:            FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passenger_statistics)
        supportActionBar!!.hide()

        mode        = intent.getStringExtra("mode")!!
        progressPB  = findViewById(R.id.progress_bar)
        actionBT    = findViewById(R.id.action)
        generateFields()

        actionBT.setOnClickListener { performAction() }
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
                    formData.put("last_updated", utcTime)
                    formData.put("data_from", "Beat Officer")
                    formData.put("last_updated", utcTime)
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
                val uuid     = formData.getString("last_updated")

                val updatedFormData = getFormData(formData)
                if (updatedFormData != null) {
                    Helper.saveFormData(this, formData, Storage.PASSENGER_STATISTICS, uuid)
                    finish()
                }
            }
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
            fieldLabel = "last_updated__gte",
            fieldName  = "Date from",
            isRequired = false
        )
        dateTo = FieldEditText(this,
            fieldType  = "date",
            fieldLabel = "last_updated__lte",
            fieldName  = "Date to",
            isRequired = false
        )
        train = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.TRAINS_LIST)!!),
            "train",
            "Train",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        coachNumber = FieldEditText(this,
            fieldType  = "text",
            fieldLabel = "coach",
            fieldName  = "Coach number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        density = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.DENSITY_TYPES)!!),
            "density",
            "Density",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        compartmentType = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.COMPARTMENT_TYPES)!!),
            "compartment_type",
            "Compartment type",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(search.getLayout())
        form.addView(dateFrom.getLayout())
        form.addView(dateTo.getLayout())
        form.addView(train.getLayout())
        form.addView(coachNumber.getLayout())
        form.addView(density.getLayout())
        form.addView(compartmentType.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.PASSENGER_STATISTICS, formData, token)

        val uuid = formData.getString("last_updated")
        if(response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.PASSENGER_STATISTICS, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun renderFields() {
        if (mode == Mode.SEARCH_FORM){
            coachNumber.hide()

            actionBT.text = "Search"
        } else if(mode == Mode.VIEW_FORM || mode== Mode.UPDATE_FORM || mode == Mode.NEW_FORM){
            dateFrom.hide()
            dateTo.hide()
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
            dateFrom.exportData(formData, tailPadding = "T00:00:00")
            dateTo.exportData(formData, tailPadding = "T23:59:59")
            train.exportData(formData)
            density.exportData(formData)
            compartmentType.exportData(formData)
            coachNumber.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        train.importData(formData)
        density.importData(formData)
        compartmentType.importData(formData)
        coachNumber.importData(formData)
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Passenger Statistics")
            val train     = Helper.getValueFromID(context, formData, "train", Storage.TRAINS_LIST)
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