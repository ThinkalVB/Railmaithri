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
import org.json.JSONArray
import org.json.JSONObject

class SearchData : AppCompatActivity() {
    private lateinit var progressPB:        ProgressBar
    private lateinit var loadMoreBT:        Button
    private lateinit var searchURL:         String
    private lateinit var baseURL:           String
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
        baseURL       = searchURL
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
            if (baseURL == URL.SURAKSHA_SAMITHI_MEMBERS) {
                isEndOfResult = true
                val formData  = JSONArray(response.second)
                Handler(Looper.getMainLooper()).post { renderFormData(formData) }
            } else {
                val resultData = JSONObject(response.second)
                val nextURL    = resultData.getString("next")
                if (nextURL == "null"){
                    isEndOfResult = true
                } else {
                    pageNumber ++
                }
                val formData = resultData.getJSONArray("results")
                Handler(Looper.getMainLooper()).post { renderFormData(formData) }
            }
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

    private fun renderFormData(formData: JSONArray) {
        for (i in 0 until formData.length()) {
            val formDatum       = formData.getJSONObject(i)
            var button: Button? = null
            if (searchURL == URL.PASSENGER_STATISTICS) {
                button = PassengerStatistics.generateButton(this, formDatum)
            } else if (searchURL == URL.STRANGER_CHECK){
                button = StrangerCheck.generateButton(this, formDatum)
            } else if (searchURL == URL.INTELLIGENCE_INFORMATION){
                button = IntelligenceInformation.generateButton(this, formDatum)
            } else if (searchURL == URL.LOST_PROPERTY){
                button = LostProperty.generateButton(this, formDatum)
            }else if (searchURL == URL.ABANDONED_PROPERTY){
                button = AbandonedProperty.generateButton(this, formDatum)
            }else if (searchURL == URL.RELIABLE_PERSON){
                button = ReliablePerson.generateButton(this, formDatum)
            }else if (searchURL == URL.EMERGENCY_CONTACTS){
                button = EmergencyContact.generateButton(this, formDatum)
            }else if (searchURL == URL.POI){
                button = POI.generateButton(this, formDatum)
            }else if (searchURL == URL.UNAUTHORIZED_PEOPLE){
                button = UnauthorizedPerson.generateButton(this, formDatum)
            }else if (searchURL == URL.CRIME_MEMO){
                button = CrimeMemo.generateButton(this, formDatum)
            }else if (searchURL == URL.SURAKSHA_SAMITHI_MEMBERS){
                button = SurakshaSamithiMember.generateButton(this, formDatum)
            }else if (searchURL == URL.RAIL_VOLUNTEER){
                button = RailVolunteer.generateButton(this, formDatum)
            }else if (searchURL == URL.RAILMAITHRI_MEETING){
                button = RailMaithriMeeting.generateButton(this, formDatum)
            } else if (searchURL == URL.BEAT_DIARY){
                button = BeatDiary.generateButton(this, formDatum)
            }
            resultLayout.addView(button)
        }
    }
}