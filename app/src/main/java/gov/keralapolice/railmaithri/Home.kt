package gov.keralapolice.railmaithri

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import gov.keralapolice.railmaithri.models.LocationModel
import gov.keralapolice.railmaithri.roomDB.DatabaseClient
import gov.keralapolice.railmaithri.services.Network
import gov.keralapolice.railmaithri.services.TrackingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.Calendar

class Home : AppCompatActivity() {
    private lateinit var token: String
    private var officerID: Int = 0
    private lateinit var profile:       JSONObject
    private lateinit var clientNT:      OkHttpClient
    private lateinit var logoutBT:      ImageView
    private lateinit var savedDataBT:   ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar!!.hide()

        clientNT = OkHttpClient().newBuilder().build()
        profile = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        officerID = profile.getInt("id")
        token = Helper.getData(this, Storage.TOKEN)!!
        logoutBT = findViewById(R.id.logout)
        savedDataBT = findViewById(R.id.saved_data)


        findViewById<TextView>(R.id.officer_name).text = profile.getString("username")
        savedDataBT.setOnClickListener {
            val intent = Intent(this, SavedData::class.java)
            startActivity(intent)
        }
        logoutBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { logout() }
        }

        // Passenger statistics
        findViewById<ImageView>(R.id.add_passenger_statistics).setOnClickListener {
            val intent = Intent(this, PassengerStatistics::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_passenger_statistics).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.PASSENGER_STATISTICS)
            startActivity(intent)
        }

        // Stranger check
        findViewById<ImageView>(R.id.add_stranger_check).setOnClickListener {
            val intent = Intent(this, StrangerCheck::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_stranger_check).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.STRANGER_CHECK)
            startActivity(intent)
        }

        // Intelligence information
        findViewById<ImageView>(R.id.add_intelligence_information).setOnClickListener {
            val intent = Intent(this, IntelligenceInformation::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_intelligence_information).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.INTELLIGENCE_INFORMATION)
            startActivity(intent)
        }

        // Lost property
        findViewById<ImageView>(R.id.add_lost_property).setOnClickListener {
            val intent = Intent(this, LostProperty::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_lost_property).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.LOST_PROPERTY)
            startActivity(intent)
        }

        // Abandoned property
        findViewById<ImageView>(R.id.add_abandoned_property).setOnClickListener {
            val intent = Intent(this, AbandonedProperty::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_abandoned_property).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.ABANDONED_PROPERTY)
            startActivity(intent)
        }

        // Reliable person
        findViewById<ImageView>(R.id.add_reliable_person).setOnClickListener {
            val intent = Intent(this, ReliablePerson::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_reliable_person).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.RELIABLE_PERSON)
            startActivity(intent)
        }

        // Emergency contact
        findViewById<ImageView>(R.id.add_emergency_contact).setOnClickListener {
            val intent = Intent(this, EmergencyContact::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_emergency_contact).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.EMERGENCY_CONTACTS)
            startActivity(intent)
        }

        // POI
        findViewById<ImageView>(R.id.add_poi).setOnClickListener {
            val intent = Intent(this, POI::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_poi).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.POI)
            startActivity(intent)
        }

        // Unauthorized person
        findViewById<ImageView>(R.id.add_unauthorized_person).setOnClickListener {
            val intent = Intent(this, UnauthorizedPerson::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_unauthorized_person).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.UNAUTHORIZED_PEOPLE)
            startActivity(intent)
        }

        // Crime memo
        findViewById<ImageView>(R.id.add_crime_memo).setOnClickListener {
            val intent = Intent(this, CrimeMemo::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_crime_memo).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.CRIME_MEMO)
            startActivity(intent)
        }

        // Suraksha samithi member
        findViewById<ImageView>(R.id.add_suraksha_samithi_member).setOnClickListener {
            val intent = Intent(this, SurakshaSamithiMember::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_suraksha_samithi_member).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.SURAKSHA_SAMITHI_MEMBERS)
            startActivity(intent)
        }

        // Rail volunteer
        findViewById<ImageView>(R.id.add_rail_volunteer).setOnClickListener {
            val intent = Intent(this, RailVolunteer::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_rail_volunteer).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.RAIL_VOLUNTEER)
            startActivity(intent)
        }

        // Close communication
        findViewById<ImageView>(R.id.close_communication).setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        // RailMaithri meeting
        findViewById<ImageView>(R.id.add_railmaithri_meeting).setOnClickListener {
            val intent = Intent(this, RailMaithriMeeting::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_railmaithri_meeting).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.RAILMAITHRI_MEETING)
            startActivity(intent)
        }

        // Beat diary
        findViewById<ImageView>(R.id.add_beat_diary).setOnClickListener {
            try {
                profile.getJSONObject("last_beat_assignment").getInt("id")
                val intent = Intent(this, BeatDiary::class.java)
                intent.putExtra("mode", Mode.NEW_FORM)
                startActivity(intent)
            } catch(_: Exception){
                Helper.showToast(this, "No tasks assigned")
            }
        }
        findViewById<Button>(R.id.search_beat_diary).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.BEAT_DIARY)
            startActivity(intent)
        }

        // Shop and labours
        findViewById<ImageView>(R.id.add_shop_and_labours).setOnClickListener {
            val intent = Intent(this, ShopAndLabours::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_shop_and_labours).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.SHOPS)
            startActivity(intent)
        }

        //Run over
        findViewById<ImageView>(R.id.add_run_over).setOnClickListener {
            val intent = Intent(this, RunOver::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_run_over).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.RUN_OVER)
            startActivity(intent)
        }

        //Incident Report
        findViewById<ImageView>(R.id.add_incident_report).setOnClickListener {
            val intent = Intent(this, IncidentReport::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_incident_report).setOnClickListener {
            val intent = Intent(this, SearchData::class.java)
            intent.putExtra("base_url", URL.INCIDENT_REPORT)
            startActivity(intent)
        }

        // Watch zone
        findViewById<ImageView>(R.id.watch_zone).setOnClickListener {
            val intent = Intent(this, WatchZone::class.java)
            startActivity(intent)
        }

        // Task list
        findViewById<ImageView>(R.id.task_list).setOnClickListener {
            val intent = Intent(this, TaskList::class.java)
            startActivity(intent)
        }

        startTracking()
        Log.e("Railwayman", java.time.LocalDateTime.now().toString())
    }

    private fun startTracking() {
        if (Helper.haveLocationPermission(this)){
            startLocationService()
        }
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, TrackingService::class.java)
            intent.action = "startLocationService"
            startService(intent)
            val c = Calendar.getInstance()
            c[Calendar.HOUR_OF_DAY] = c[Calendar.HOUR_OF_DAY] + 1
            c[Calendar.MINUTE] = c[Calendar.MINUTE]
            c[Calendar.SECOND] = 0
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationServices::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun stopLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, TrackingService::class.java)
            intent.action = "stopLocationService"
            startService(intent)
        }
    }

    private fun logout() {
        Handler(Looper.getMainLooper()).post {
            logoutBT.isClickable = false
        }

        val data = JSONObject()
        data.put("app_version", App.APP_VERSION)

        try {
            val request = API.post(URL.MOBILE_LOGOUT, data, token)
            val response = clientNT.newCall(request).execute()
            Log.e("Railmaithri", response.code.toString())
            if (response.isSuccessful || response.code == 401) {
                Helper.saveData(this, Storage.TOKEN, "")
                try{
                    stopLocationService()
                    unregisterReceiver(locationStateReceiver)
                } catch (_: Exception){ }
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                val errorMessage = "Server refused to logout"
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Log.e("Railmaithri", e.toString())
            Helper.showToast(this, "Server unreachable !!")
        } finally {
            Handler(Looper.getMainLooper()).post {
                logoutBT.isClickable = true
            }
        }
    }

    private fun saveLocationDataToServer(
        latitude: String,
        longitude: String,
        accuracy: String,
        speed: String,
        heading: String,
        altitude: String,
        time: String,
        isFromDB: Boolean,
        task: LocationModel? = null
    ) {
        try {
            if (accuracy.toFloat() > 50) {
                //remove from DB
                if (isFromDB) {
                    DatabaseClient.getInstance(applicationContext).appDatabase
                        .taskLocation()
                        .delete(task)
                }
                return
            }

            val locationData = JSONObject()
            locationData.put("beat_officer", officerID)
            locationData.put("latitude", latitude)
            locationData.put("longitude", longitude)
            locationData.put("accuracy", accuracy)
            locationData.put("speed", speed)
            locationData.put("heading", heading)
            locationData.put("altitude", altitude)
            locationData.put("utc_timestamp", time)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val clientNT = OkHttpClient().newBuilder().build()
                    val request  = API.post(URL.LOCATION_UPDATE, locationData, token)
                    val response = clientNT.newCall(request).execute()
                    if (response.isSuccessful) {
                        if (isFromDB){
                            //remove from DB
                            DatabaseClient.getInstance(applicationContext).appDatabase
                                .taskLocation()
                                .delete(task)
                        }
                    } else if (response.code == 401){
                        try{
                            stopLocationService()
                            unregisterReceiver(locationStateReceiver)
                        } catch (_: Exception){ }
                    }
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
    }

    private val locationStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Network.isAvailable(this@Home)) {
                saveLocationDataToServer(
                    intent.getDoubleExtra("latitude", 0.0).toString(),
                    intent.getDoubleExtra("longitude", 0.0).toString(),
                    intent.getFloatExtra("accuracy", 0.0F).toString(),
                    intent.getFloatExtra("speed", 0.0F).toString(),
                    intent.getFloatExtra("heading", 0.0F).toString(),
                    intent.getDoubleExtra("altitude", 0.0).toString(),
                    Helper.getUTC(),
                    false,
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val taskList = DatabaseClient
                        .getInstance(applicationContext)
                        .appDatabase
                        .taskLocation()
                        .all

                    if (taskList.isNotEmpty()){
                        //offline syncing
                        startOfflineSyncing(taskList)
                    }
                }

            } else {
                //creating a task
                val task = LocationModel()
                task.latitude       = intent.getDoubleExtra("latitude", 0.0).toString()
                task.longitude      = intent.getDoubleExtra("longitude", 0.0).toString()
                task.accuracy       = intent.getDoubleExtra("accuracy", 0.0).toString()
                task.speed          = intent.getDoubleExtra("speed", 0.0).toString()
                task.heading        = intent.getDoubleExtra("heading", 0.0).toString()
                task.altitude       = intent.getDoubleExtra("altitude", 0.0).toString()
                task.utc_timestamp  = Helper.getUTC()

                //adding to database
                DatabaseClient.getInstance(applicationContext).appDatabase
                    .taskLocation()
                    .insert(task)
            }
        }
    }

    private fun startOfflineSyncing(taskList: MutableList<LocationModel>) {
        for (i in 0 until taskList.size){
            saveLocationDataToServer(
                taskList[i].latitude,
                taskList[i].longitude,
                taskList[i].accuracy,
                taskList[i].speed,
                taskList[i].heading,
                taskList[i].altitude,
                taskList[i].utc_timestamp,
                true,
                taskList[i]
            )
        }
    }

    override fun onResume() {
        val intentFilter = IntentFilter("Location")
        registerReceiver(locationStateReceiver, intentFilter)
        super.onResume()
    }
}