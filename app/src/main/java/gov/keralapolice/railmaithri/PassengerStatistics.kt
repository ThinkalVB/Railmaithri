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

    private lateinit var train:             FieldSpinner
    private lateinit var coachNumber:       FieldEditText
    private lateinit var density:           FieldSpinner
    private lateinit var compartmentType:   FieldSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passenger_statistics)
        supportActionBar!!.hide()

        mode        = intent.getStringExtra("mode")!!
        progressPB  = findViewById(R.id.progress_bar)
        actionBT    = findViewById(R.id.action)

        actionBT.setOnClickListener {
            val formData = getFormData()
            if (formData != null) {
                when (mode) {
                    Mode.NEW_FORM -> {
                        CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
                    }
                    Mode.SEARCH_FORM -> {
                        val intent = Intent(this, SearchData::class.java)
                        intent.putExtra("search_url", URL.PASSENGER_STATISTICS)
                        intent.putExtra("parameters", formData.toString())
                        startActivity(intent)
                    }
                    Mode.UPDATE_FORM -> {
                        Helper.saveFormData(this, formData, Storage.PASSENGER_STATISTICS)
                        finish()
                    }
                }
            }
        }

        prepareActionButton()
        renderForm()
        if(mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM){
            populateData()
        }
    }

    private fun populateData(){
        val formData = JSONObject(intent.getStringExtra("data")!!)
        train.importData(formData)
        density.importData(formData)
        compartmentType.importData(formData)
        coachNumber.importData(formData)
    }

    private fun renderForm() {
        train = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.TRAINS_LIST)!!),
            "train",
            "Train",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        coachNumber = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "coach",
            fieldName = "Coach number",
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

        Helper.showToast(this, response.second)
        if(response.first == ResponseType.SUCCESS){
            val key = formData.getString("utc_timestamp")
            Helper.removeFormData(this, key, Storage.PASSENGER_STATISTICS)
            finish()
        } else if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.PASSENGER_STATISTICS)
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

    private fun getFormData(): JSONObject? {
        val formData = JSONObject()
        try{
            if(mode == Mode.NEW_FORM){
                val utcTime = Helper.getUTC()
                formData.put("utc_timestamp", utcTime)
                formData.put("last_updated",  utcTime)
            }
            train.exportData(formData)
            density.exportData(formData)
            compartmentType.exportData(formData)
            coachNumber.exportData(formData)
        }catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
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