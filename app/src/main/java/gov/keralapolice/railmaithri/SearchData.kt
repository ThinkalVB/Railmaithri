package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SearchData : AppCompatActivity() {
    private lateinit var progressPB:        ProgressBar
    private lateinit var loadMoreBT:        Button
    private lateinit var searchURL:         String
    private lateinit var parameters:        JSONObject
    private lateinit var resultLayout:      LinearLayout
    private var pageNumber                  = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()

        progressPB    = findViewById(R.id.progress_bar)
        loadMoreBT    = findViewById(R.id.load_more)
        searchURL     = intent.getStringExtra("search_url")!!
        parameters    = JSONObject(intent.getStringExtra("parameters")!!)
        resultLayout  = findViewById(R.id.form_data_list)

        CoroutineScope(Dispatchers.IO).launch {  searchFormData()  }
        loadMoreBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {  searchFormData()  }
        }
    }

    private fun searchFormData() {
        var isEndOfResult = false
        Handler(Looper.getMainLooper()).post {
            loadMoreBT.isClickable  = false
            progressPB.visibility   = View.VISIBLE
        }

        parameters.put("page", pageNumber)
        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.getFormData(searchURL, parameters, token)
        if (response.first == ResponseType.SUCCESS) {
            val formData = JSONObject(response.second)
            val nextURL  = formData.getString("next")
            if (nextURL == "null"){
                isEndOfResult = true
            } else {
                pageNumber ++
            }
            Handler(Looper.getMainLooper()).post { renderFormData(formData) }
        } else {
            Helper.showToast(this, response.second)
        }

        Handler(Looper.getMainLooper()).post {
            loadMoreBT.isClickable  = true
            progressPB.visibility = View.GONE
            if (isEndOfResult) {
                loadMoreBT.visibility = View.GONE
            }
        }
    }

    private fun renderFormData(response: JSONObject) {
        val formData = response.getJSONArray("results")
        for (i in 0 until formData.length()) {
            val formDatum       = formData.getJSONObject(i)
            var button: Button? = null
            if (searchURL == URL.PASSENGER_STATISTICS) {
                button = PassengerStatistics.generateButton(this, formDatum)
            } else if (searchURL == URL.STRANGER_CHECK){
                button = StrangerCheck.generateButton(this, formDatum)
            } else if (searchURL == URL.INTELLIGENCE_INFORMATION){
                button = IntelligenceInformation.generateButton(this, formDatum)
            }
            resultLayout.addView(button)
        }
    }
}