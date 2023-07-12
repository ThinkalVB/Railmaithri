package gov.keralapolice.railmaithri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject

class Home : AppCompatActivity() {
    private lateinit var token:         String
    private lateinit var profile:       JSONObject
    private lateinit var clientNT:      OkHttpClient
    private lateinit var logoutBT:      ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar!!.hide()

        clientNT  = OkHttpClient().newBuilder().build()
        profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        token     = Helper.getData(this, Storage.TOKEN)!!
        logoutBT  = findViewById<ImageView>(R.id.logout)

        logoutBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {  logout() }
        }
        findViewById<TextView>(R.id.officer_name).text = profile.getString("username")
    }
    private fun logout() {
        Handler(Looper.getMainLooper()).post {
            logoutBT.isClickable   = false
        }

        val data = JSONObject()
        data.put("app_version", App.APP_VERSION)

        try {
            val request  = API.post(URL.MOBILE_LOGOUT, data, token)
            val response = clientNT.newCall(request).execute()
            Log.e("Railmaithri", response.code.toString())
            if (response.isSuccessful || response.code == 401) {
                Helper.saveData(this, Storage.TOKEN, "")
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
                logoutBT.isClickable   = true
            }
        }
    }
}