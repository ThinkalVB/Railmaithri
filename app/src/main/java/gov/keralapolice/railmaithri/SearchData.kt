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
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import gov.keralapolice.railmaithri.adapters.IncidentReportLA
import gov.keralapolice.railmaithri.adapters.LostPropertyLA
import gov.keralapolice.railmaithri.adapters.PoiLA
import gov.keralapolice.railmaithri.adapters.RailVolunteerLA
import gov.keralapolice.railmaithri.adapters.ReliablePersonLA
import gov.keralapolice.railmaithri.adapters.RunOverLA
import gov.keralapolice.railmaithri.adapters.StrangerCheckLA
import gov.keralapolice.railmaithri.adapters.SurakshaSamithiMemberLA
import gov.keralapolice.railmaithri.adapters.UnauthorizedPersonLA
import gov.keralapolice.railmaithri.models.IncidentReportMD
import gov.keralapolice.railmaithri.models.LostPropertyMD
import gov.keralapolice.railmaithri.models.PoiMD
import gov.keralapolice.railmaithri.models.RailVolunteerMD
import gov.keralapolice.railmaithri.models.ReliablePersonMD
import gov.keralapolice.railmaithri.models.RunOverMD
import gov.keralapolice.railmaithri.models.StrangerCheckMD
import gov.keralapolice.railmaithri.models.SurakshaSamithiMemberMD
import gov.keralapolice.railmaithri.models.UnauthorizedPersonMD
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
                val strangerData  = gson.fromJson(formData!!.toString(), Array<StrangerCheckMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter = StrangerCheckLA(this@SearchData, strangerData)
                listData.adapter  = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Name", R.id.val1, strangerData[position].name)
                    addAttribute(dialog, R.id.att2, "Age", R.id.val2, strangerData[position].age.toString())
                    addAttribute(dialog, R.id.att3, "Languages", R.id.val3, strangerData[position].languages_known)
                    addAttribute(dialog, R.id.att4, "Identification", R.id.val4, strangerData[position].identification_marks_details)
                    addAttribute(dialog, R.id.att5, "Mobile", R.id.val5, strangerData[position].mobile_number)
                    addAttribute(dialog, R.id.att6, "Purpose of visit", R.id.val6, strangerData[position].purpose_of_visit)
                    addAttribute(dialog, R.id.att7, "Is foreigner", R.id.val7, strangerData[position].is_foreigner.toString())

                    if (strangerData[position].is_foreigner) {
                        addAttribute(dialog, R.id.att8, "Country", R.id.val8, strangerData[position].country_name)
                        hideAttribute(dialog, R.id.att9, R.id.val9)
                        hideAttribute(dialog, R.id.att10, R.id.val10)
                        hideAttribute(dialog, R.id.att11, R.id.val11)
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

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (strangerData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(strangerData[position].photo)
                        }
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
                val lostPropertyData = gson.fromJson(formData!!.toString(), Array<LostPropertyMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter    = LostPropertyLA(this@SearchData, lostPropertyData)
                listData.adapter     = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Category", R.id.val1, lostPropertyData[position].lost_property_category_label)
                    addAttribute(dialog, R.id.att2, "Police station", R.id.val2, lostPropertyData[position].police_station_label)
                    addAttribute(dialog, R.id.att3, "Station number", R.id.val3, lostPropertyData[position].police_station_number)
                    addAttribute(dialog, R.id.att4, "Found in", R.id.val4, lostPropertyData[position].found_in)
                    addAttribute(dialog, R.id.att5, "Found on", R.id.val5, lostPropertyData[position].found_on)
                    addAttribute(dialog, R.id.att6, "Remarks", R.id.val6, lostPropertyData[position].return_remarks)
                    addAttribute(dialog, R.id.att7, "Description", R.id.val7, lostPropertyData[position].description)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    locationButton.visibility = View.INVISIBLE

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (lostPropertyData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(lostPropertyData[position].photo)
                        }
                        Glide.with(this).load(lostPropertyData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_lost_property)
                    }
                    dialog.show()
                }
            }
            URL.ABANDONED_PROPERTY -> {

            }
            URL.RELIABLE_PERSON -> {
                val reliablePersonData = gson.fromJson(formData!!.toString(), Array<ReliablePersonMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter    = ReliablePersonLA(this@SearchData, reliablePersonData)
                listData.adapter     = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Name", R.id.val1, reliablePersonData[position].name)
                    addAttribute(dialog, R.id.att2, "Mobile Number", R.id.val2, reliablePersonData[position].mobile_number)
                    addAttribute(dialog, R.id.att3, "Police Number", R.id.val3, reliablePersonData[position].police_station_label)
                    addAttribute(dialog, R.id.att4, "Place", R.id.val4, reliablePersonData[position].place)
                    addAttribute(dialog, R.id.att5, "Description", R.id.val5, reliablePersonData[position].description)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    locationButton.visibility = View.INVISIBLE

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (reliablePersonData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(reliablePersonData[position].photo)
                        }
                        Glide.with(this).load(reliablePersonData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_reliable_person)
                    }
                    dialog.show()
                }
            }
            URL.EMERGENCY_CONTACTS -> {

            }
            URL.POI -> {
                val poiData = gson.fromJson(formData!!.toString(), Array<PoiMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter    = PoiLA(this@SearchData, poiData)
                listData.adapter     = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Name", R.id.val1, poiData[position].name)
                    addAttribute(dialog, R.id.att2, "Category", R.id.val2, poiData[position].poi_category_label)
                    addAttribute(dialog, R.id.att3, "Added by", R.id.val3, poiData[position].added_by_label)
                    addAttribute(dialog, R.id.att4, "Police station", R.id.val4, poiData[position].police_station_label)
                    addAttribute(dialog, R.id.att5, "District", R.id.val5, poiData[position].district_label)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    if (poiData[position].latitude != null) {
                        locationButton.visibility = View.VISIBLE
                        locationButton.setOnClickListener {
                            dialog.hide()
                            openMap(poiData[position].latitude, poiData[position].longitude)
                        }
                    } else {
                        locationButton.visibility = View.INVISIBLE
                    }

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (poiData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(poiData[position].photo)
                        }
                        Glide.with(this).load(poiData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_poi)
                    }
                    dialog.show()
                }
            }
            URL.UNAUTHORIZED_PEOPLE -> {
                val unauthorizedPersonData = gson.fromJson(formData!!.toString(), Array<UnauthorizedPersonMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter    = UnauthorizedPersonLA(this@SearchData, unauthorizedPersonData)
                listData.adapter     = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Category", R.id.val1, unauthorizedPersonData[position].category_label)
                    addAttribute(dialog, R.id.att2, "Police station", R.id.val2, unauthorizedPersonData[position].police_station_label)
                    addAttribute(dialog, R.id.att3, "Place Of Check", R.id.val3, unauthorizedPersonData[position].place_of_check)
                    addAttribute(dialog, R.id.att4, "Description", R.id.val4, unauthorizedPersonData[position].description)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    if (unauthorizedPersonData[position].latitude != null) {
                        locationButton.visibility = View.VISIBLE
                        locationButton.setOnClickListener {
                            dialog.hide()
                            openMap(unauthorizedPersonData[position].latitude, unauthorizedPersonData[position].longitude)
                        }
                    } else {
                        locationButton.visibility = View.INVISIBLE
                    }

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (unauthorizedPersonData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(unauthorizedPersonData[position].photo)
                        }
                        Glide.with(this).load(unauthorizedPersonData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_unauthorized_person)
                    }
                    dialog.show()
                }

            }
            URL.CRIME_MEMO -> {

            }
            URL.SURAKSHA_SAMITHI_MEMBERS -> {
                val surakshaSamithiMemberData = gson.fromJson(formData!!.toString(), Array<SurakshaSamithiMemberMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter    = SurakshaSamithiMemberLA(this@SearchData, surakshaSamithiMemberData)
                listData.adapter     = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Suraksha Samithi", R.id.val1, surakshaSamithiMemberData[position].suraksha_samithi_label)
                    addAttribute(dialog, R.id.att2, "Name", R.id.val2, surakshaSamithiMemberData[position].name)
                    addAttribute(dialog, R.id.att3, "Address", R.id.val3, surakshaSamithiMemberData[position].address)
                    addAttribute(dialog, R.id.att4, "Mobile Number", R.id.val4, surakshaSamithiMemberData[position].mobile_number)
                    addAttribute(dialog, R.id.att5, "Email", R.id.val5, surakshaSamithiMemberData[position].email)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    locationButton.visibility = View.INVISIBLE

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (surakshaSamithiMemberData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(surakshaSamithiMemberData[position].photo)
                        }
                        Glide.with(this).load(surakshaSamithiMemberData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_suraksha_samithi_member)
                    }
                    dialog.show()
                }
            }
            URL.RAIL_VOLUNTEER -> {
                val railVolunteerData  = gson.fromJson(formData!!.toString(), Array<RailVolunteerMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter = RailVolunteerLA(this@SearchData, railVolunteerData)
                listData.adapter  = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Name", R.id.val1, railVolunteerData[position].name)
                    addAttribute(dialog, R.id.att2, "Category", R.id.val2, railVolunteerData[position].rail_volunteer_category_label)
                    addAttribute(dialog, R.id.att3, "Age", R.id.val3, railVolunteerData[position].age.toString())
                    addAttribute(dialog, R.id.att4, "Email ID", R.id.val4, railVolunteerData[position].email)
                    addAttribute(dialog, R.id.att5, "Gender", R.id.val5, railVolunteerData[position].gender)
                    addAttribute(dialog, R.id.att6, "Mobile number", R.id.val6, railVolunteerData[position].mobile_number)
                    addAttribute(dialog, R.id.att7, "Entrain station", R.id.val7, railVolunteerData[position].entrain_station_label)
                    addAttribute(dialog, R.id.att8, "Detrain station", R.id.val8, railVolunteerData[position].detrain_station_label)
                    addAttribute(dialog, R.id.att9, "Nearest station", R.id.val9, railVolunteerData[position].nearest_railway_station_label)

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    locationButton.visibility = View.INVISIBLE

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (railVolunteerData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(railVolunteerData[position].photo)
                        }
                        Glide.with(this).load(railVolunteerData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_rail_volunteer)
                    }
                    dialog.show()
                }
            }
            URL.RAILMAITHRI_MEETING -> {

            }
            URL.BEAT_DIARY -> {

            }
            URL.INCIDENT_REPORT -> {
                val incidentData  = gson.fromJson(formData!!.toString(), Array<IncidentReportMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter = IncidentReportLA(this@SearchData, incidentData)
                listData.adapter  = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Type", R.id.val1, incidentData[position].incident_type)
                    addAttribute(dialog, R.id.att2, "Description", R.id.val2, incidentData[position].incident_details)
                    when (incidentData[position].incident_type) {
                        "Platform" -> {
                            addAttribute(dialog, R.id.att3, "Railway station", R.id.val3, incidentData[position].railway_station_label)
                            addAttribute(dialog, R.id.att4, "Platform", R.id.val4, incidentData[position].platform_number)
                            hideAttribute(dialog, R.id.att5, R.id.val5)
                        }
                        "Train" -> {
                            addAttribute(dialog, R.id.att3, "Train", R.id.val3, incidentData[position].train_name)
                            addAttribute(dialog, R.id.att4, "Coach", R.id.val4, incidentData[position].coach)
                            addAttribute(dialog, R.id.att5, "Mobile", R.id.val5, incidentData[position].mobile_number)
                        }
                        "Track" -> {
                            addAttribute(dialog, R.id.att3, "Railway station", R.id.val3, incidentData[position].railway_station_label)
                            addAttribute(dialog, R.id.att4, "Track", R.id.val4, incidentData[position].track_location)
                            hideAttribute(dialog, R.id.att5, R.id.val5)
                        }
                    }

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    if (incidentData[position].latitude != null) {
                        locationButton.visibility = View.VISIBLE
                        locationButton.setOnClickListener {
                            dialog.hide()
                            openMap(incidentData[position].latitude, incidentData[position].longitude)
                        }
                    } else {
                        locationButton.visibility = View.INVISIBLE
                    }

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (incidentData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(incidentData[position].photo)
                        }
                        Glide.with(this).load(incidentData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_incident_report)
                    }
                    dialog.show()
                }
            }
            URL.SHOPS -> {

            }
            URL.RUN_OVER -> {
                val runOverData  = gson.fromJson(formData!!.toString(), Array<RunOverMD>::class.java).toList()
                dialog.setContentView(R.layout.search_data_popup)
                val myListAdapter = RunOverLA(this@SearchData, runOverData)
                listData.adapter  = myListAdapter

                listData.setOnItemClickListener { parent, view, position, id ->
                    // For loading attribute values
                    addAttribute(dialog, R.id.att1, "Occurred on", R.id.val1, runOverData[position].date_time_of_occurance)
                    addAttribute(dialog, R.id.att2, "Category", R.id.val2, runOverData[position].category)
                    addAttribute(dialog, R.id.att3, "Place", R.id.val3, runOverData[position].place_of_occurance)
                    addAttribute(dialog, R.id.att4, "B/w Station 1", R.id.val4, runOverData[position].between_station_1_label)
                    addAttribute(dialog, R.id.att5, "B/w Station 2", R.id.val5, runOverData[position].between_station_2_label)
                    addAttribute(dialog, R.id.att6, "Information from", R.id.val6, runOverData[position].source_of_information)
                    addAttribute(dialog, R.id.att7, "Remarks", R.id.val7, runOverData[position].remarks)
                    addAttribute(dialog, R.id.att8, "Cause", R.id.val8, runOverData[position].cause_label)
                    addAttribute(dialog, R.id.att9, "Victim Details", R.id.val9, runOverData[position].victim_details)
                    addAttribute(dialog, R.id.att10, "Crime Number", R.id.val10, runOverData[position].crime_number)
                    addAttribute(dialog, R.id.att11, "District", R.id.val11, runOverData[position].district_label)
                    addAttribute(dialog, R.id.att12, "Cause", R.id.val12, runOverData[position].cause_label)
                    addAttribute(dialog, R.id.att13, "Details", R.id.val13, runOverData[position].identification_details)

                    if (runOverData[position].is_identified) {
                        addAttribute(dialog, R.id.att14, "Name", R.id.val14, runOverData[position].name)
                        addAttribute(dialog, R.id.att15, "Age", R.id.val15, runOverData[position].age.toString())
                        addAttribute(dialog, R.id.att16, "Gender", R.id.val16, runOverData[position].gender)
                        addAttribute(dialog, R.id.att17, "Address", R.id.val17, runOverData[position].address)
                        addAttribute(dialog, R.id.att18, "Contact", R.id.val18, runOverData[position].contact_number)
                    } else {
                        hideAttribute(dialog, R.id.att14, R.id.val14)
                        hideAttribute(dialog, R.id.att15, R.id.val15)
                        hideAttribute(dialog, R.id.att16, R.id.val16)
                        hideAttribute(dialog, R.id.att17, R.id.val17)
                        hideAttribute(dialog, R.id.att18, R.id.val18)
                    }

                    if (runOverData[position].case_registered_in == "Local Police Station") {
                        addAttribute(dialog, R.id.att20, "Local PS", R.id.val20, runOverData[position].local_police_station)
                    } else {
                        addAttribute(dialog, R.id.att20, "Railway PS", R.id.val20, runOverData[position].railway_police_station_label)
                    }

                    // For opening location in google maps
                    val locationButton = (dialog.findViewById<View>(R.id.open_location) as Button)
                    locationButton.visibility = View.INVISIBLE

                    // For loading and opening image
                    val imageView = (dialog.findViewById<View>(R.id.search_data_image) as ImageView)
                    if (runOverData[position].photo != null) {
                        imageView.setOnClickListener {
                            dialog.hide()
                            openImage(runOverData[position].photo)
                        }
                        Glide.with(this).load(runOverData[position].photo).into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.im_run_over)
                    }
                    dialog.show()
                }
            }
        }
    }

    private fun addAttribute(dialog: Dialog, attrID: Int, attrName: String, valID: Int, valName: String) {
        val attributeField = (dialog.findViewById<View>(attrID) as TextView)
        val valueField     = (dialog.findViewById<View>(valID)  as TextView)

        attributeField.visibility = View.VISIBLE
        valueField.visibility     = View.VISIBLE
        attributeField.text       = attrName
        valueField.text           = valName
    }

    private fun hideAttribute(dialog: Dialog, attrID: Int, valID: Int){
        val attributeField = (dialog.findViewById<View>(attrID) as TextView)
        val valueField     = (dialog.findViewById<View>(valID)  as TextView)

        attributeField.visibility = View.GONE
        valueField.visibility     = View.GONE
    }

    private fun openImage(imageURL: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageURL))
        this.startActivity(intent)
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


