package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
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
    private var pageNumber                  = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()

        progressPB = findViewById(R.id.progress_bar)
        loadMoreBT = findViewById(R.id.load_more)
        searchURL  = intent.getStringExtra("search_url")!!
        parameters = JSONObject(intent.getStringExtra("parameters")!!)

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
            renderFormData(formData)
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

    private fun renderFormData(formData: JSONObject){
        Log.e("Railmaithri", formData.toString())
    }
}