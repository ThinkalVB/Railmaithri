package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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

    private fun fetchAndPopulate() {
        when (taskType) {
            "Lonely Passenger" -> {
                patchURL = URL.LONELY_PASSENGER + "${taskID}/"
                fetchURL = URL.LONELY_PASSENGER + "?id=${taskID}"
                CoroutineScope(Dispatchers.IO).launch {
                    val response        = Helper.getFormData(fetchURL, JSONObject(), token)
                    val lonelyPassenger = JSONObject(response.second).getJSONArray("results").getJSONObject(0)
                    runOnUiThread {
                        findViewById<LinearLayout>(R.id.ly_train).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_train).text = lonelyPassenger.optString("train_name")
                        findViewById<LinearLayout>(R.id.ly_coach_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_coach_number).text = lonelyPassenger.optString("coach")
                        findViewById<LinearLayout>(R.id.ly_contact_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_contact_number).text = lonelyPassenger.optString("mobile_number")
                        findViewById<LinearLayout>(R.id.ly_age).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_age).text = lonelyPassenger.optString("age")
                        findViewById<LinearLayout>(R.id.ly_name).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_name).text = lonelyPassenger.optString("name")
                        findViewById<LinearLayout>(R.id.ly_remarks).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_remarks).text = lonelyPassenger.optString("remarks")
                        findViewById<LinearLayout>(R.id.ly_from_station).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_from_station).text = lonelyPassenger.optString("entrain_station_label")
                        findViewById<LinearLayout>(R.id.ly_to_station).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_to_station).text = lonelyPassenger.optString("detrain_station_label")
                        findViewById<LinearLayout>(R.id.ly_gender).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_gender).text = lonelyPassenger.optString("gender")
                        findViewById<LinearLayout>(R.id.ly_seat_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_seat_number).text = lonelyPassenger.optString("seat")
                        findViewById<LinearLayout>(R.id.ly_date_of_journey).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_date_of_journey).text = lonelyPassenger.optString("date_of_journey")
                        findViewById<LinearLayout>(R.id.ly_pnr_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_pnr_number).text = lonelyPassenger.optString("pnr_number")
                        findViewById<LinearLayout>(R.id.ly_dress_code).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_dress_code).text = lonelyPassenger.optString("dress_code")
                        locationUtil.hideLayout()
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
                        findViewById<LinearLayout>(R.id.ly_incident_type).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_incident_type).text = incidentInTrain.optString("incident_type")
                        findViewById<LinearLayout>(R.id.ly_train).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_train).text = incidentInTrain.optString("train")
                        findViewById<LinearLayout>(R.id.ly_coach_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_coach_number).text = incidentInTrain.optString("coach")
                        findViewById<LinearLayout>(R.id.ly_contact_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_contact_number).text = incidentInTrain.optString("mobile_number")
                        findViewById<LinearLayout>(R.id.ly_details).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_details).text = incidentInTrain.optString("incident_details")
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
                        findViewById<LinearLayout>(R.id.ly_platform_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_platform_number).text = incidentInPlatform.optString("platform_number")
                        findViewById<LinearLayout>(R.id.ly_incident_type).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_incident_type).text = incidentInPlatform.optString("incident_type")
                        findViewById<LinearLayout>(R.id.ly_railway_station).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_railway_station).text = incidentInPlatform.optString("railway_station_label")
                        findViewById<LinearLayout>(R.id.ly_details).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_details).text = incidentInPlatform.optString("incident_details")
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
                        findViewById<LinearLayout>(R.id.ly_track_location).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_track_location).text = incidentInTrack.optString("track_location")
                        findViewById<LinearLayout>(R.id.ly_incident_type).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_incident_type).text = incidentInTrack.optString("incident_type")
                        findViewById<LinearLayout>(R.id.ly_details).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_details).text = incidentInTrack.optString("incident_details")
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
                        findViewById<LinearLayout>(R.id.ly_train).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_train).text = intruderAlert.optString("train_label")
                        findViewById<LinearLayout>(R.id.ly_contact_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_contact_number).text = intruderAlert.optString("mobile_number")
                        findViewById<LinearLayout>(R.id.ly_remarks).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_remarks).text = intruderAlert.optString("remarks")
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
                        findViewById<LinearLayout>(R.id.ly_severity).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_severity).text = intelligenceInformation.optString("severity")
                        findViewById<LinearLayout>(R.id.ly_contact_number).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_contact_number).text = intelligenceInformation.optString("mobile_number")
                        findViewById<LinearLayout>(R.id.ly_information).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_information).text = intelligenceInformation.optString("information")
                        findViewById<LinearLayout>(R.id.ly_remarks).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_remarks).text = intelligenceInformation.optString("remarks")
                        findViewById<LinearLayout>(R.id.ly_intelligence_type).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_intelligence_type).text = intelligenceInformation.optString("intelligence_type")
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
                        findViewById<LinearLayout>(R.id.ly_sos_message).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.val_sos_message).text = sosAlert.optString("sos_message")
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
        val beatID    = profile.getJSONObject("last_beat_assignment").getInt("id")
        val officerID = profile.getInt("id")

        formData.put("beat_assignment_id", beatID)
        formData.put("beat_officer_id", officerID)
        formData.put("status", 5)
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