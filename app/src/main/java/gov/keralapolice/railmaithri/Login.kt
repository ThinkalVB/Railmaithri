package gov.keralapolice.railmaithri

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var token:         String
    private lateinit var profile:       JSONObject

    private lateinit var clientNT:      OkHttpClient
    private lateinit var progressPB:    ProgressBar
    private lateinit var loginBT:       Button
    private lateinit var usernameET:    EditText
    private lateinit var passwordET:    EditText
    private lateinit var loginData:     JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar!!.hide()

        requestPermissions()
        val token = Helper.getData(this, Storage.TOKEN)
        if (token != null && token != "") {
            startActivity(Intent(this, Home::class.java))
            finish()
        }

        clientNT   = OkHttpClient().newBuilder().build()
        progressPB = findViewById(R.id.progress_bar)
        loginBT    = findViewById(R.id.login)
        usernameET = findViewById(R.id.username)
        passwordET = findViewById(R.id.password)

        loginBT.setOnClickListener {
            val username = usernameET.text.toString()
            val password = passwordET.text.toString()
            if(username.isBlank() or password.isBlank()) {
                val message = "Both username and password are required to login"
                Helper.showToast(this, message)
            } else {
                val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                loginData    = JSONObject()
                loginData.put("username", username)
                loginData.put("password", password)
                loginData.put("device_id", deviceID.toString())
                loginData.put("app_version", App.APP_VERSION)
                registerFirebase()
            }
        }
    }

    private fun registerFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Helper.showToast(this, "Firebase failed to connect")
                return@OnCompleteListener
            }
            loginData.put("fcm_token", task.result)
            CoroutineScope(Dispatchers.IO).launch { login() }
        })
    }

    private fun login() {
        Handler(Looper.getMainLooper()).post {
            loginBT.isClickable   = false
            progressPB.visibility = View.VISIBLE
        }

        try {
            val request  = API.post(URL.MOBILE_LOGIN, loginData, null)
            val response = clientNT.newCall(request).execute()
            if (response.isSuccessful) {
                val authData = JSONObject(response.body!!.string())
                profile  = authData.getJSONObject("user")
                token    = authData.getString("token")

                val cacheStatus = startCaching(token)
                if(cacheStatus){
                    Helper.saveData(this, Storage.PROFILE, profile.toString())
                    Helper.saveData(this, Storage.TOKEN, token)
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else{
                    val message = "Failed to cache data, try after some time"
                    Helper.showToast(this, message, Toast.LENGTH_LONG)
                }
            } else {
                val errorMessage = Helper.getError(response.body!!.string())
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Helper.showToast(this, "Server unreachable !!")
        } finally {
            Handler(Looper.getMainLooper()).post {
                loginBT.isClickable   = true
                progressPB.visibility = View.GONE
            }
        }
    }

    private fun requestPermissions() {
        val appPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
        )
        val neededPermissions = ArrayList<String>()
        for (permission in appPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission)
            }
        }
        if(neededPermissions.isNotEmpty()) {
            val requestCode = 108
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), requestCode)
        }
    }

    private fun startCaching(token: String) : Boolean {
        var status = true
        status = status && cacheData(URL.RAILWAY_STATIONS_LIST, Storage.RAILWAY_STATIONS_LIST)
        status = status && cacheData(URL.TRAINS_LIST, Storage.TRAINS_LIST)
        status = status && cacheData(URL.INTELLIGENCE_SEVERITY_TYPES, Storage.INTELLIGENCE_SEVERITY_TYPES)
        status = status && cacheData(URL.INTELLIGENCE_TYPES, Storage.INTELLIGENCE_TYPES)
        status = status && cacheData(URL.COMPARTMENT_TYPES, Storage.COMPARTMENT_TYPES)
        status = status && cacheData(URL.DENSITY_TYPES, Storage.DENSITY_TYPES)
        status = status && cacheData(URL.MEETING_TYPES, Storage.MEETING_TYPES)
        status = status && cacheData(URL.POI_TYPES, Storage.POI_TYPES)
        status = status && cacheData(URL.POLICE_STATIONS_LIST, Storage.POLICE_STATIONS_LIST)
        status = status && cacheData(URL.DISTRICTS_LIST, Storage.DISTRICTS_LIST)
        status = status && cacheData(URL.STATES_LIST, Storage.STATES_LIST)
        status = status && cacheData(URL.ABANDONED_PROPERTY_TYPES, Storage.ABANDONED_PROPERTY_TYPES)
        status = status && cacheData(URL.RAIL_VOLUNTEER_TYPES, Storage.RAIL_VOLUNTEER_TYPES)
        status = status && cacheData(URL.GENDER_TYPES, Storage.GENDER_TYPES)
        status = status && cacheData(URL.CONTACT_TYPES, Storage.CONTACT_TYPES)
        status = status && cacheData(URL.WATCH_ZONE_TYPES, Storage.WATCH_ZONE_TYPES)
        status = status && cacheData(URL.VENDOR_TYPES, Storage.VENDOR_TYPES)
        status = status && cacheData(URL.LOST_PROPERTY_TYPES, Storage.LOST_PROPERTY_TYPES)
        status = status && cacheData(URL.FOUND_IN_TYPES, Storage.FOUND_IN_TYPES)
        status = status && cacheData(URL.SURAKSHA_SAMITHI_LIST, Storage.SURAKSHA_SAMITHI_LIST)
        status = status && cacheData(URL.SHOP_TYPES, Storage.SHOP_TYPES)
        status = status && cacheData(URL.CRIME_MEMO_TYPES, Storage.CRIME_MEMO_TYPES)
        status = status && cacheData(URL.WATCH_ZONE, Storage.WATCH_ZONE)
        return status
    }

    private fun cacheData(url: String, storage: String): Boolean {
        val request  = API.get(url, token)
        val response = clientNT.newCall(request).execute()
        return if (response.isSuccessful) {
            Helper.saveData(this, storage, response.body!!.string())
            true
        } else {
            false
        }
    }
}