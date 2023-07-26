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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
    private lateinit var profile: JSONObject
    private lateinit var clientNT: OkHttpClient
    private lateinit var logoutBT: ImageView
    private lateinit var savedDataBT: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar!!.hide()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })

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
            val intent = Intent(this, PassengerStatistics::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Stranger check
        findViewById<ImageView>(R.id.add_stranger_check).setOnClickListener {
            val intent = Intent(this, StrangerCheck::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_stranger_check).setOnClickListener {
            val intent = Intent(this, StrangerCheck::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Intelligence information
        findViewById<ImageView>(R.id.add_intelligence_information).setOnClickListener {
            val intent = Intent(this, IntelligenceInformation::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_intelligence_information).setOnClickListener {
            val intent = Intent(this, IntelligenceInformation::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Lost property
        findViewById<ImageView>(R.id.add_lost_property).setOnClickListener {
            val intent = Intent(this, LostProperty::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_lost_property).setOnClickListener {
            val intent = Intent(this, LostProperty::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Abandoned property
        findViewById<ImageView>(R.id.add_abandoned_property).setOnClickListener {
            val intent = Intent(this, AbandonedProperty::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_abandoned_property).setOnClickListener {
            val intent = Intent(this, AbandonedProperty::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Reliable person
        findViewById<ImageView>(R.id.add_reliable_person).setOnClickListener {
            val intent = Intent(this, ReliablePerson::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_reliable_person).setOnClickListener {
            val intent = Intent(this, ReliablePerson::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Emergency contact
        findViewById<ImageView>(R.id.add_emergency_contact).setOnClickListener {
            val intent = Intent(this, EmergencyContact::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_emergency_contact).setOnClickListener {
            val intent = Intent(this, EmergencyContact::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // POI
        findViewById<ImageView>(R.id.add_poi).setOnClickListener {
            val intent = Intent(this, POI::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_poi).setOnClickListener {
            val intent = Intent(this, POI::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Unauthorized person
        findViewById<ImageView>(R.id.add_unauthorized_person).setOnClickListener {
            val intent = Intent(this, UnauthorizedPerson::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_unauthorized_person).setOnClickListener {
            val intent = Intent(this, UnauthorizedPerson::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Crime memo
        findViewById<ImageView>(R.id.add_crime_memo).setOnClickListener {
            val intent = Intent(this, CrimeMemo::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_crime_memo).setOnClickListener {
            val intent = Intent(this, CrimeMemo::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Suraksha samithi member
        findViewById<ImageView>(R.id.add_suraksha_samithi_member).setOnClickListener {
            val intent = Intent(this, SurakshaSamithiMember::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_suraksha_samithi_member).setOnClickListener {
            val intent = Intent(this, SurakshaSamithiMember::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Rail volunteer
        findViewById<ImageView>(R.id.add_rail_volunteer).setOnClickListener {
            val intent = Intent(this, RailVolunteer::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivity(intent)
        }
        findViewById<Button>(R.id.search_rail_volunteer).setOnClickListener {
            val intent = Intent(this, RailVolunteer::class.java)
            intent.putExtra("mode", Mode.SEARCH_FORM)
            startActivity(intent)
        }

        // Close communication
        findViewById<ImageView>(R.id.close_communication).setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        startTracking()
    }

    private fun startTracking() {
        handleLocationUpdates()
    }

    private fun handleLocationUpdates() {
        if (Helper.haveLocationPermission(this)){
            startLocationService()
        }
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, TrackingService::class.java)
            intent.action = "startLocationService"
            startService(intent)
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show()
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
                stopLocationService()
                unregisterReceiver(locationStateReceiver)
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
        altitude: String
    ) {
        try {
            Toast.makeText(this@Home, "API", Toast.LENGTH_SHORT).show()
            val locationData = JSONObject()
            locationData.put("beat_officer", officerID)
            locationData.put("latitude", latitude)
            locationData.put("longitude", longitude)
            locationData.put("accuracy", accuracy)
            locationData.put("speed", speed)
            locationData.put("heading", heading)
            locationData.put("altitude", altitude)
            locationData.put("utc_timestamp", Helper.getUTC())
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val clientNT = OkHttpClient().newBuilder().build()
                    val request = API.post(URL.LOCATION_UPDATE, locationData, token)
                    val response = clientNT.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("Railmaithri", altitude)
                    }
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
    }

    private val locationStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            saveLocationDataToServer(
                intent.getDoubleExtra("latitude",0.0).toString()!!,
                intent.getDoubleExtra("longitude",0.0).toString()!!,
                intent.getDoubleExtra("accuracy",0.0).toString()!!,
                intent.getDoubleExtra("speed",0.0).toString()!!,
                intent.getDoubleExtra("heading",0.0).toString()!!,
                intent.getDoubleExtra("altitude",0.0).toString()!!,
            )
        }
    }

    override fun onResume() {
        val intentFilter = IntentFilter("Location")
        registerReceiver(locationStateReceiver, intentFilter)
        super.onResume()
    }
}