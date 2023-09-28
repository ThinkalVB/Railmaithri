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
import gov.keralapolice.railmaithri.adapters.ListAdapterStrangerCheck
import gov.keralapolice.railmaithri.models.StrangerModel
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
    private lateinit var tv1: TextView

    private lateinit var myListAdapter: ListAdapterStrangerCheck
    private lateinit var listData: ListView
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_data)
        supportActionBar!!.hide()
        dialog = Dialog(this@SearchData);

        progressPB = findViewById(R.id.progress_bar)
        loadMoreBT = findViewById(R.id.load_more)
        searchURL = intent.getStringExtra("base_url")!!
        // resultLayout  = findViewById(R.id.form_data_list)
        filterBT = findViewById(R.id.filter_button)
        parameters = JSONObject()
        listData = findViewById(R.id.form_data_item)
        tv1 = findViewById(R.id.textView2)
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
        //resultLayout.removeAllViews()
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

                // renderFormData(formData)
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
        //  for (i in 0 until formData.length()) {
        //   val formDatum       = formData.getJSONObject(i)
        //Log.e("data",strangerData.toString()+"data");
//        var button: Button? = null
        if (searchURL == URL.PASSENGER_STATISTICS) {
            // button = PassengerStatistics.generateButton(this, formDatum)
        } else if (searchURL == URL.STRANGER_CHECK) {
            val strangerData =
                gson.fromJson(formData!!.toString(), Array<StrangerModel>::class.java).toList()
            dialog.setContentView(R.layout.stranger_check_popup)
            myListAdapter = ListAdapterStrangerCheck(this@SearchData, strangerData)
            listData.adapter = myListAdapter

            listData.setOnItemClickListener { parent, view, position, id ->
                Log.e("test", "test")
                // var image = dialog.findViewById<View>(R.id.re_id) as ImageButton
                (dialog.findViewById<View>(R.id.name) as TextView).text = HtmlCompat.fromHtml(
                    "<b><i>" + strangerData[position].name + "</i></b>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                (dialog.findViewById<View>(R.id.age) as TextView).text = HtmlCompat.fromHtml(
                    "<b>Age :  </b>" + strangerData[position].age.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                (dialog.findViewById<View>(R.id.language) as TextView).text = HtmlCompat.fromHtml(
                    "<b>Language Known :   </b> " + strangerData[position].languages_known.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                (dialog.findViewById<View>(R.id.idantification) as TextView).text =
                    HtmlCompat.fromHtml(
                        "<b>Identification Mark :  </b> " + strangerData[position].identification_marks_details.toString(),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                (dialog.findViewById<View>(R.id.mobile) as TextView).text = HtmlCompat.fromHtml(
                    "<b>Mobile Number :  </b> " + strangerData[position].mobile_number,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                (dialog.findViewById<View>(R.id.purpose) as TextView).text = HtmlCompat.fromHtml(
                    "<b>Purpose of Visit :  </b> " + strangerData[position].purpose_of_visit,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )


                dialog.show()

            }
        } else if (searchURL == URL.INTELLIGENCE_INFORMATION) {

        } else if (searchURL == URL.LOST_PROPERTY) {


            // button = LostProperty.generateButton(this, formDatum)
        } else if (searchURL == URL.ABANDONED_PROPERTY) {
            // button = AbandonedProperty.generateButton(this, formDatum)
        } else if (searchURL == URL.RELIABLE_PERSON) {
            //button = ReliablePerson.generateButton(this, formDatum)
        } else if (searchURL == URL.EMERGENCY_CONTACTS) {
            //button = EmergencyContact.generateButton(this, formDatum)
        } else if (searchURL == URL.POI) {
            //button = POI.generateButton(this, formDatum)
        } else if (searchURL == URL.UNAUTHORIZED_PEOPLE) {
            //button = UnauthorizedPerson.generateButton(this, formDatum)
        } else if (searchURL == URL.CRIME_MEMO) {
            //button = CrimeMemo.generateButton(this, formDatum)
        } else if (searchURL == URL.SURAKSHA_SAMITHI_MEMBERS) {
            //button = SurakshaSamithiMember.generateButton(this, formDatum)
        } else if (searchURL == URL.RAIL_VOLUNTEER) {
            //button = RailVolunteer.generateButton(this, formDatum)
        } else if (searchURL == URL.RAILMAITHRI_MEETING) {
            //button = RailMaithriMeeting.generateButton(this, formDatum)
        } else if (searchURL == URL.BEAT_DIARY) {
            //button = BeatDiary.generateButton(this, formDatum)
        } else if (searchURL == URL.INCIDENT_REPORT) {
            //button = IncidentReport.generateButton(this, formDatum)
        } else if (searchURL == URL.SHOPS) {
            //button = ShopAndLabours.generateButton(this, formDatum)
        } else if (searchURL == URL.RUN_OVER) {
            // button = RunOver.generateButton(this, formDatum)
        }


        // for (i in 0 until formData.length()) {
        /* val formDatum       = formData.getJSONObject(i)
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
         }  else if (searchURL == URL.INCIDENT_REPORT){
             button = IncidentReport.generateButton(this, formDatum)
         } else if (searchURL == URL.SHOPS){
             button = ShopAndLabours.generateButton(this, formDatum)
         } else if (searchURL == URL.RUN_OVER){
             button = RunOver.generateButton(this, formDatum)
         }
         resultLayout.addView(button)
     }*/
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


