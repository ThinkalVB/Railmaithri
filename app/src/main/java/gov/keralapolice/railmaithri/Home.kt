package gov.keralapolice.railmaithri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject

class Home : AppCompatActivity() {
    private lateinit var token:         String
    private lateinit var profile:       JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar!!.hide()

        profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        token     = Helper.getData(this, Storage.TOKEN)!!


        val logoutBT = findViewById<ImageView>(R.id.logout)
        logoutBT.setOnClickListener {
            Helper.saveData(this, Storage.TOKEN, "")
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        findViewById<TextView>(R.id.officer_name).text = profile.getString("username")


    }
}