package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import org.json.JSONObject

class SearchData : AppCompatActivity() {
    private lateinit var progressPB:        ProgressBar
    private lateinit var loadMoreBT:        Button
    private lateinit var searchURL:         String
    private lateinit var parameters:        JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()

        progressPB = findViewById(R.id.progress_bar)
        loadMoreBT = findViewById(R.id.load_more)
        searchURL  = intent.getStringExtra("search_url")!!
        parameters = JSONObject(intent.getStringExtra("parameters")!!)
        search()
    }

    private fun search(){
        Log.e("Railmaithri", searchURL)
        Log.e("Railmaithri", parameters.toString())
    }
}