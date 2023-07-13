package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject

class PassengerStatistics : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var formData:          JSONObject
    private lateinit var train:             FieldSpinner
    private lateinit var coachNumber:       FieldEditText
    private lateinit var density:           FieldSpinner
    private lateinit var compartmentType:   FieldSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passenger_statistics)
        mode        = intent.getStringExtra("mode")!!
        progressPB  = findViewById(R.id.progress_bar)
        actionBT    = findViewById(R.id.action)

        if(mode == Mode.NEW_FORM){
            formData      = JSONObject()
            actionBT.text = "Save"
            formData.put("utc_timestamp", Helper.getUTC())
            formData.put("last_updated",  Helper.getUTC())
        }
        if(mode == Mode.UPDATE_FORM){
            actionBT.text = "Update"
        }
        if(mode == Mode.VIEW_FORM){
            actionBT.visibility = View.GONE
        }
        if(mode == Mode.SEARCH_FORM){
            actionBT.text = "Search"
        }

        train = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.TRAINS_LIST)!!),
            "train",
            "Train",
            isRequired = true
        )
        coachNumber = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "coach",
            fieldName = "Coach number",
            isRequired = true
        )
        density = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.DENSITY_TYPES)!!),
            "density",
            "Density",
            isRequired = true
        )
        compartmentType = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.COMPARTMENT_TYPES)!!),
            "compartment_type",
            "Compartment type",
            isRequired = true
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(train.getLayout())
        form.addView(coachNumber.getLayout())
        form.addView(density.getLayout())
        form.addView(compartmentType.getLayout())

        actionBT.setOnClickListener {
            if(extractData()) {
                progressPB.visibility = View.VISIBLE
                actionBT.isClickable  = false
                CoroutineScope(Dispatchers.IO).launch {  sendFormData()  }
            }
        }
    }

    private fun sendFormData() {
        try {
            val clientNT  = OkHttpClient().newBuilder().build()
            val token     = Helper.getData(this, Storage.TOKEN)!!
            val request   = API.post(URL.PASSENGER_STATISTICS, formData, token)
            val response  = clientNT.newCall(request).execute()
            if (response.isSuccessful) {
                val key = formData.getString("utc_timestamp")
                Helper.removeFormData(this, key, Storage.PASSENGER_STATISTICS)
                finish()
            } else {
                val apiResponse  = response.body!!.string()
                val errorMessage = Helper.getError(apiResponse)
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Log.d("RailMaithri", e.stackTraceToString())
            Helper.saveFormData(this, formData, Storage.PASSENGER_STATISTICS)
        } finally {
            Handler(Looper.getMainLooper()).post {
                actionBT.isClickable  = true
                progressPB.visibility = View.GONE
            }
        }
    }

    private fun extractData(): Boolean {
        try{
            train.exportData(formData)
            density.exportData(formData)
            compartmentType.exportData(formData)
            coachNumber.exportData(formData)
        }catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return false
        }
        return true
    }
}