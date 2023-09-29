package gov.keralapolice.railmaithri

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.gson.GsonBuilder
import gov.keralapolice.railmaithri.adapters.StrangerCheckLA
import gov.keralapolice.railmaithri.models.StrangerCheckMD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

public class SearchData : AppCompatActivity() {
    private lateinit var progressPB: ProgressBar
    private lateinit var loadMoreBT: Button
    private lateinit var searchURL: String
    private lateinit var parameters: JSONObject
    private lateinit var resultLayout: LinearLayout
    private lateinit var filterBT: ImageButton
    private var pageNumber = 1

    private lateinit var myListAdapter: StrangerCheckLA
    private lateinit var listData:      ListView
    lateinit var dialog:                Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()

        dialog     = Dialog(this@SearchData);
        progressPB = findViewById(R.id.progress_bar)
        loadMoreBT = findViewById(R.id.load_more)
        searchURL  = intent.getStringExtra("base_url")!!
        filterBT   = findViewById(R.id.filter_button)
        parameters = JSONObject()
        listData   = findViewById(R.id.form_data_item)

        loadMoreBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { searchFormData() }
        }
        filterBT.setOnClickListener {
            when (searchURL) {
                URL.PASSENGER_STATISTICS -> {
                    val intent = Intent(this, PassengerStatistics::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.STRANGER_CHECK -> {
                    val intent = Intent(this, StrangerCheck::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.INTELLIGENCE_INFORMATION -> {
                    val intent = Intent(this, IntelligenceInformation::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.LOST_PROPERTY -> {
                    val intent = Intent(this, LostProperty::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.ABANDONED_PROPERTY -> {
                    val intent = Intent(this, AbandonedProperty::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.RELIABLE_PERSON -> {
                    val intent = Intent(this, ReliablePerson::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.EMERGENCY_CONTACTS -> {
                    val intent = Intent(this, EmergencyContact::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.POI -> {
                    val intent = Intent(this, POI::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.UNAUTHORIZED_PEOPLE -> {
                    val intent = Intent(this, UnauthorizedPerson::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.CRIME_MEMO -> {
                    val intent = Intent(this, CrimeMemo::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.SURAKSHA_SAMITHI_MEMBERS -> {
                    val intent = Intent(this, SurakshaSamithiMember::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.RAIL_VOLUNTEER -> {
                    val intent = Intent(this, RailVolunteer::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.RAILMAITHRI_MEETING -> {
                    val intent = Intent(this, RailMaithriMeeting::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.BEAT_DIARY -> {
                    filterBT.isClickable = false
                }

                URL.INCIDENT_REPORT -> {
                    val intent = Intent(this, IncidentReport::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.SHOPS -> {
                    val intent = Intent(this, ShopAndLabours::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }

                URL.RUN_OVER -> {
                    val intent = Intent(this, RunOver::class.java)
                    intent.putExtra("mode", Mode.SEARCH_FORM)
                    startActivityForResult(intent, 1000)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pageNumber = 1
        CoroutineScope(Dispatchers.IO).launch { searchFormData() }

    }

    private fun searchFormData() {
        var isEndOfResult = false
        Handler(Looper.getMainLooper()).post {
            loadMoreBT.isClickable = false
            progressPB.visibility = View.VISIBLE
        }

        parameters.put("page", pageNumber)
        val profile = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        val officerID = profile.getInt("id")
        try {
            if (searchURL == URL.RELIABLE_PERSON) {
                val stationID = profile.getJSONArray("police_station").getJSONObject(0).getInt("id")
                parameters.put("police_station", stationID)
            }
        } catch (_: Exception) {
        }

        if (searchURL == URL.BEAT_DIARY) {
            parameters.put("beat_officer", officerID)
        } else if (searchURL == URL.INCIDENT_REPORT) {
            parameters.put("informer", officerID)
        }

        val token = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.getFormData(searchURL, parameters, token)
        if (response.first == ResponseType.SUCCESS) {
            if (searchURL == URL.SURAKSHA_SAMITHI_MEMBERS) {
                isEndOfResult = true
                val formData = JSONArray(response.second)

                Handler(Looper.getMainLooper()).post {
                    renderFormData(formData)
                }
            } else {
                val resultData = JSONObject(response.second)
                val nextURL = resultData.getString("next")
                if (nextURL == "null") {
                    isEndOfResult = true
                } else {
                    pageNumber++
                }
                val formData = resultData.getJSONArray("results")
                Log.e("ss", "" + formData.toString())
                Handler(Looper.getMainLooper()).post { renderFormData(formData) }
            }
        } else {
            Helper.showToast(this, response.second)
        }

        Handler(Looper.getMainLooper()).post {
            loadMoreBT.isClickable = true
            progressPB.visibility = View.GONE
            if (isEndOfResult) {
                loadMoreBT.isClickable = false
            }
        }
    }

    private fun renderFormData(formData: JSONArray) {
        val gson = GsonBuilder().create()

        if (searchURL == URL.STRANGER_CHECK) {
            val strangerData = gson.fromJson(formData!!.toString(), Array<StrangerCheckMD>::class.java).toList()
            dialog.setContentView(R.layout.search_data_popup)
            myListAdapter    = StrangerCheckLA(this@SearchData, strangerData)
            listData.adapter = myListAdapter

            listData.setOnItemClickListener { parent, view, position, id ->
                // Name
                (dialog.findViewById<View>(R.id.att1) as TextView).text ="Name"
                (dialog.findViewById<View>(R.id.val1) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].name + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // Age
                (dialog.findViewById<View>(R.id.att2) as TextView).text ="Age"
                (dialog.findViewById<View>(R.id.val2) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].age + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // Known languages
                (dialog.findViewById<View>(R.id.att3) as TextView).text ="Languages"
                (dialog.findViewById<View>(R.id.val3) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].languages_known + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // Identification
                (dialog.findViewById<View>(R.id.att4) as TextView).text ="Identification"
                (dialog.findViewById<View>(R.id.val4) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].identification_marks_details + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // Mobile number
                (dialog.findViewById<View>(R.id.att5) as TextView).text ="Mobile"
                (dialog.findViewById<View>(R.id.val5) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].mobile_number + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                // Purpose of visit
                (dialog.findViewById<View>(R.id.att6) as TextView).text ="Purpose"
                (dialog.findViewById<View>(R.id.val6) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].purpose_of_visit + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                dialog.show()
            }
        }else  if (searchURL == URL.PASSENGER_STATISTICS) {

        } else if (searchURL == URL.INTELLIGENCE_INFORMATION) {

        } else if (searchURL == URL.LOST_PROPERTY) {

        } else if (searchURL == URL.ABANDONED_PROPERTY) {

        } else if (searchURL == URL.RELIABLE_PERSON) {

        } else if (searchURL == URL.EMERGENCY_CONTACTS) {

        } else if (searchURL == URL.POI) {

        } else if (searchURL == URL.UNAUTHORIZED_PEOPLE) {

        } else if (searchURL == URL.CRIME_MEMO) {

        } else if (searchURL == URL.SURAKSHA_SAMITHI_MEMBERS) {

        } else if (searchURL == URL.RAIL_VOLUNTEER) {

        } else if (searchURL == URL.RAILMAITHRI_MEETING) {

        } else if (searchURL == URL.BEAT_DIARY) {

        } else if (searchURL == URL.INCIDENT_REPORT) {

        } else if (searchURL == URL.SHOPS) {

        } else if (searchURL == URL.RUN_OVER) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        parameters = if (requestCode == 1000 && resultCode == RESULT_OK) {
            JSONObject(resultIntent!!.getStringExtra("parameters")!!)
        } else {
            JSONObject()
        }
    }
}


