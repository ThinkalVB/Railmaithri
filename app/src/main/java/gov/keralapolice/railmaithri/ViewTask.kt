package gov.keralapolice.railmaithri

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ViewTask : AppCompatActivity() {
    private lateinit var formNameTV:    TextView
    private lateinit var token:         String
    private lateinit var locationUtil:  LocationUtil
    private lateinit var progressPB:    ProgressBar
    private lateinit var saveBT:        Button
    private lateinit var remarks:       FieldEditText
    private lateinit var fetchURL:      String
    private lateinit var patchURL:      String
    private lateinit var taskType:      String
    private var taskID                  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_task)
        supportActionBar!!.hide()

        formNameTV = findViewById<TextView>(R.id.form_name)
        taskType   = intent.getStringExtra("task_type")!!
        taskID     = intent.getIntExtra("task_id", 0)
        token      = Helper.getData(this, Storage.TOKEN)!!

        formNameTV.text = taskType
        locationUtil    = LocationUtil(this, findViewById(R.id.ly_location))
        progressPB      = findViewById(R.id.progress_bar)
        saveBT          = findViewById(R.id.save)
        locationUtil.disableUpdate()

        saveBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { sendRemarks() }
        }
        CoroutineScope(Dispatchers.IO).launch { fetchAndPopulate() }
    }

    private fun generateLayout(label: String, value: String): LinearLayout {
        val scale      = this.resources.displayMetrics.density
        val padding4dp = (4 * scale + 0.5f).toInt()
        val width128dp = (128 * scale + 0.5f).toInt()
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setPadding(padding4dp, padding4dp, padding4dp, padding4dp)

        val textViewLabel    = TextView(this)
        textViewLabel.width  = width128dp
        textViewLabel.setTextColor(ContextCompat.getColor(this, R.color.black))
        textViewLabel.text   = label
        textViewLabel.setTypeface(null, Typeface.BOLD)
        val textViewValue    = TextView(this)
        textViewValue.text   = value

        linearLayout.addView(textViewLabel)
        linearLayout.addView(textViewValue)
        return linearLayout
    }

    private fun fetchAndPopulate() {
        val fields = findViewById<LinearLayout>(R.id.fields)
        when (taskType) {
            "Lonely Passenger" -> {
                patchURL = URL.LONELY_PASSENGER + "${taskID}/"
                fetchURL = URL.LONELY_PASSENGER + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response        = Helper.getFormData(fetchURL, JSONObject(), token)
                    val lonelyPassenger = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Train",      lonelyPassenger.optString("train_name")))
                        fields.addView(generateLayout("Coach",      lonelyPassenger.optString("coach")))
                        fields.addView(generateLayout("Mobile",     lonelyPassenger.optString("mobile_number")))
                        fields.addView(generateLayout("Age",        lonelyPassenger.optString("age")))
                        fields.addView(generateLayout("Name",       lonelyPassenger.optString("name")))
                        fields.addView(generateLayout("Starting",   lonelyPassenger.optString("entrain_station_label")))
                        fields.addView(generateLayout("Ending",     lonelyPassenger.optString("detrain_station_label")))
                        fields.addView(generateLayout("Gender",     lonelyPassenger.optString("gender")))
                        fields.addView(generateLayout("Seat",       lonelyPassenger.optString("seat")))
                        fields.addView(generateLayout("Date",       lonelyPassenger.optString("date_of_journey")))
                        fields.addView(generateLayout("PNR",        lonelyPassenger.optString("pnr_number")))
                        fields.addView(generateLayout("Dress code", lonelyPassenger.optString("dress_code")))
                        fields.addView(generateLayout("Remarks",    lonelyPassenger.optString("remarks")))
                        locationUtil.hide()
                    }
                }
            }
            "Incident in Train" -> {
                patchURL = URL.INCIDENT_REPORT + "${taskID}/"
                fetchURL = URL.INCIDENT_REPORT + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val incidentInTrain = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Incident", incidentInTrain.optString("incident_type")))
                        fields.addView(generateLayout("Train",    incidentInTrain.optString("train")))
                        fields.addView(generateLayout("Coach",    incidentInTrain.optString("coach")))
                        fields.addView(generateLayout("Mobile",   incidentInTrain.optString("mobile_number")))
                        fields.addView(generateLayout("Details",  incidentInTrain.optString("incident_details")))
                        updateLocation(incidentInTrain)
                    }
                }
            }
            "Incident in Platform" -> {
                patchURL = URL.INCIDENT_REPORT + "${taskID}/"
                fetchURL = URL.INCIDENT_REPORT + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val incidentInPlatform = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Incident", incidentInPlatform.optString("incident_type")))
                        fields.addView(generateLayout("Platform", incidentInPlatform.optString("platform_number")))
                        fields.addView(generateLayout("Station",  incidentInPlatform.optString("railway_station_label")))
                        fields.addView(generateLayout("Details",  incidentInPlatform.optString("incident_details")))
                        updateLocation(incidentInPlatform)
                    }
                }
            }
            "Incident in Track" -> {
                patchURL = URL.INCIDENT_REPORT + "${taskID}/"
                fetchURL = URL.INCIDENT_REPORT + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val incidentInTrack = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Incident", incidentInTrack.optString("incident_type")))
                        fields.addView(generateLayout("Track",    incidentInTrack.optString("track_location")))
                        fields.addView(generateLayout("Details",  incidentInTrack.optString("incident_details")))
                        updateLocation(incidentInTrack)
                    }
                }
            }
            "Intruder Alert" -> {
                patchURL = URL.INTRUDER_ALERT + "${taskID}/"
                fetchURL = URL.INTRUDER_ALERT + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val intruderAlert = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Train ",  intruderAlert.optString("train_label")))
                        fields.addView(generateLayout("Mobile",  intruderAlert.optString("mobile_number")))
                        fields.addView(generateLayout("Remarks", intruderAlert.optString("remarks")))
                        updateLocation(intruderAlert)
                    }
                }
            }
            "Intelligence" -> {
                patchURL = URL.INTELLIGENCE_INFORMATION + "${taskID}/"
                fetchURL = URL.INTELLIGENCE_INFORMATION + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val intelligenceInformation = JSONObject(response.second).getJSONArray("results").optJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Type",        intelligenceInformation.optString("intelligence_type")))
                        fields.addView(generateLayout("Severity",    intelligenceInformation.optString("severity")))
                        fields.addView(generateLayout("Mobile",      intelligenceInformation.optString("mobile_number")))
                        fields.addView(generateLayout("Information", intelligenceInformation.optString("information")))
                        fields.addView(generateLayout("Remarks",     intelligenceInformation.optString("remarks")))
                        updateLocation(intelligenceInformation)
                    }
                }
            }
            "SOS Alert" -> {
                patchURL = URL.SOS + "${taskID}/"
                fetchURL = URL.SOS + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = Helper.getFormData(fetchURL, JSONObject(), token)
                    val sosAlert = JSONObject(response.second).getJSONArray("results").optJSONObject(0)
                    runOnUiThread {
                        fields.addView(generateLayout("Message", sosAlert.optString("remarks")))
                        updateLocation(sosAlert)
                    }
                }
            }
        }

        remarks = FieldEditText(
            this,
            fieldType = "multiline",
            fieldLabel = "attended_remarks",
            fieldName = "Closing remarks",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, Mode.NEW_FORM)
        )
        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(remarks.getLayout())
    }

    private fun sendRemarks() {
        val formData = JSONObject()
        try{
            remarks.exportData(formData)
        } catch (e: Exception) {
            Helper.showToast(this, e.message!!)
            return
        }

        val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        val officerID = profile.getInt("id")
        formData.put("beat_officer_id", officerID)
        formData.put("status", 5)

        try {
            val beatID = profile.getJSONObject("last_beat_assignment").getInt("id")
            formData.put("beat_assignment", beatID)
        } catch(_: Exception){ }

        val response = Helper.patchFormData(patchURL, formData, token)
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        } else {
            Helper.showToast(this, response.second)
        }
    }

    private fun updateLocation(locationData: JSONObject){
        val latitude  = locationData.getDouble("latitude")
        val longitude = locationData.getDouble("longitude")
        locationUtil.importLocation(latitude, longitude, 0.0f)
    }

}