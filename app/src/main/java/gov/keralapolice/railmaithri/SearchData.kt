package gov.keralapolice.railmaithri

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
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
        when (searchURL) {
            URL.STRANGER_CHECK -> {
                val strangerData = gson.fromJson(formData!!.toString(), Array<StrangerCheckMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                myListAdapter    = StrangerCheckLA(this@SearchData, strangerData)
                listData.adapter = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Name", R.id.val1, strangerData[position].name)
                    addAttribute(dialog, R.id.att2, "Age", R.id.val2, strangerData[position].age.toString())
                    addAttribute(dialog, R.id.att3, "Languages", R.id.val3, strangerData[position].languages_known)
                    addAttribute(dialog, R.id.att4, "Identification", R.id.val4, strangerData[position].identification_marks_details)
                    addAttribute(dialog, R.id.att5, "Mobile", R.id.val5, strangerData[position].mobile_number)
                    addAttribute(dialog, R.id.att6, "Purpose of visit", R.id.val6, strangerData[position].purpose_of_visit)
                    addAttribute(dialog, R.id.att7, "Is foreigner", R.id.val7, strangerData[position].is_foreigner.toString())
                    if (strangerData[position].is_foreigner){
                        addAttribute(dialog, R.id.att8, "Country", R.id.val8, strangerData[position].country_name)
                    } else {
                        addAttribute(dialog, R.id.att8, "State", R.id.val8, strangerData[position].native_state_label)
                        addAttribute(dialog, R.id.att9, "Police station", R.id.val9, strangerData[position].native_police_station)
                        addAttribute(dialog, R.id.att10, "Address", R.id.val10, strangerData[position].native_address)
                        addAttribute(dialog, R.id.att11, "Remarks", R.id.val11, strangerData[position].remarks)
                    }

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    if (strangerData[position].latitude != null) {
                        locationButton.visibility = View.VISIBLE
                        locationButton.setOnClickListener {
                            dialog.hide()
                            openMap(strangerData[position].latitude, strangerData[position].longitude)
                        }
                    } else {
                        locationButton.visibility = View.INVISIBLE
                    }

                    // For loading image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (strangerData[position].photo != null) {
                        Glide.with(this).load(strangerData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_stranger_check)
                    }
                    dialog.show()
                }
            }
            URL.PASSENGER_STATISTICS -> {

            }
            URL.INTELLIGENCE_INFORMATION -> {

            }
            URL.LOST_PROPERTY -> {

            }
            URL.ABANDONED_PROPERTY -> {

            }
            URL.RELIABLE_PERSON -> {

            }
            URL.EMERGENCY_CONTACTS -> {

            }
            URL.POI -> {

            }
            URL.UNAUTHORIZED_PEOPLE -> {

            }
            URL.CRIME_MEMO -> {

            }
            URL.SURAKSHA_SAMITHI_MEMBERS -> {

            }
            URL.RAIL_VOLUNTEER -> {

            }
            URL.RAILMAITHRI_MEETING -> {

            }
            URL.BEAT_DIARY -> {

            }
            URL.INCIDENT_REPORT -> {

            }
            URL.SHOPS -> {

            }
            URL.RUN_OVER -> {

            }
        }
    }

    private fun addAttribute(dialog: Dialog, attrID: Int, attrName: String, valID: Int, valName: String) {
        (dialog.findViewById<View>(attrID) as TextView).text = attrName
        (dialog.findViewById<View>(valID)  as TextView).text = valName
    }

    private fun openMap(latitude: Float, longitude: Float) {
            val mapUri = Uri.parse("geo:0,0?q=${latitude},${longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            try {
                this.startActivity(mapIntent)
            } catch (e: ActivityNotFoundException) {
                val message = "Failed to open map"
                Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
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


