package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject

class SearchData : AppCompatActivity() {
    private lateinit var searchURL:         String
    private lateinit var parameters:        JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()

        searchURL  = intent.getStringExtra("search_url")!!
        parameters = JSONObject(intent.getStringExtra("parameters")!!)

        Log.e("Railmaithri", searchURL)
        Log.e("Railmaithri", parameters.toString())
    }
}