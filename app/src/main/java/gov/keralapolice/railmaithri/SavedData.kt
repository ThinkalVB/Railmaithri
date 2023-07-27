package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import gov.keralapolice.railmaithri.Helper.Companion.loadFormData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SavedData : AppCompatActivity() {
    private lateinit var progressPB:        ProgressBar
    private lateinit var syncBT:            Button
    private lateinit var resultLayout:      LinearLayout
    private var          dataCount          = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_data)
        supportActionBar!!.hide()

        progressPB    = findViewById(R.id.progress_bar)
        syncBT        = findViewById(R.id.load_more)
        resultLayout  = findViewById(R.id.form_data_list)

        syncBT.setOnClickListener { syncData() }
    }

    override fun onResume() {
        super.onResume()
        loadDataToSync()
    }

    private fun loadDataToSync() {
        resultLayout.removeAllViews()
        val passengerStatistics = loadFormData(this, Storage.PASSENGER_STATISTICS)
        val psKeys              = passengerStatistics.keys()
        while (psKeys.hasNext()) {
            val psKeys = psKeys.next()
            val value  = passengerStatistics.getJSONObject(psKeys)
            val button = PassengerStatistics.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val strangerCheck = loadFormData(this, Storage.STRANGER_CHECK)
        val scKeys        = strangerCheck.keys()
        while (scKeys.hasNext()) {
            val scKeys = scKeys.next()
            val value  = strangerCheck.getJSONObject(scKeys)
            val button = StrangerCheck.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val intelligenceInformation = loadFormData(this, Storage.INTELLIGENCE_INFORMATION)
        val ifKeys                  = intelligenceInformation.keys()
        while (ifKeys.hasNext()) {
            val ifKeys = ifKeys.next()
            val value  = intelligenceInformation.getJSONObject(ifKeys)
            val button = IntelligenceInformation.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val lostProperty = loadFormData(this, Storage.LOST_PROPERTY)
        val lpKeys       = lostProperty.keys()
        while (lpKeys.hasNext()) {
            val lpKeys = lpKeys.next()
            val value  = lostProperty.getJSONObject(lpKeys)
            val button = LostProperty.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val abandonedProperty = loadFormData(this, Storage.ABANDONED_PROPERTY)
        val apKeys            = abandonedProperty.keys()
        while (apKeys.hasNext()) {
            val apKeys = apKeys.next()
            val value  = abandonedProperty.getJSONObject(apKeys)
            val button = AbandonedProperty.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val reliablePerson = loadFormData(this, Storage.RELIABLE_PERSON)
        val rpKeys         = reliablePerson.keys()
        while (rpKeys.hasNext()) {
            val rpKeys = rpKeys.next()
            val value  = reliablePerson.getJSONObject(rpKeys)
            val button = ReliablePerson.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val emergencyContact = loadFormData(this, Storage.EMERGENCY_CONTACTS)
        val ecKeys           = emergencyContact.keys()
        while (ecKeys.hasNext()) {
            val ecKeys = ecKeys.next()
            val value  = emergencyContact.getJSONObject(ecKeys)
            val button = EmergencyContact.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val poi   = loadFormData(this, Storage.POI)
        val pKeys = poi.keys()
        while (pKeys.hasNext()) {
            val pKeys  = pKeys.next()
            val value  = poi.getJSONObject(pKeys)
            val button = POI.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val unauthorizedPerson  = loadFormData(this, Storage.UNAUTHORIZED_PEOPLE)
        val upKeys              = unauthorizedPerson.keys()
        while (upKeys.hasNext()) {
            val upKeys  = upKeys.next()
            val value  = unauthorizedPerson.getJSONObject(upKeys)
            val button = UnauthorizedPerson.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val crimeMemo   = loadFormData(this, Storage.CRIME_MEMO)
        val cmKeys      = crimeMemo.keys()
        while (cmKeys.hasNext()) {
            val cmKeys  = cmKeys.next()
            val value   = crimeMemo.getJSONObject(cmKeys)
            val button  = CrimeMemo.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val surakshaSamithiMember = loadFormData(this, Storage.SURAKSHA_SAMITHI_MEMBERS)
        val ssKeys                = surakshaSamithiMember.keys()
        while (ssKeys.hasNext()) {
            val ssKeys  = ssKeys.next()
            val value  = surakshaSamithiMember.getJSONObject(ssKeys)
            val button = SurakshaSamithiMember.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val railVolunteer = loadFormData(this, Storage.RAIL_VOLUNTEER)
        val rvKeys        = railVolunteer.keys()
        while (rvKeys.hasNext()) {
            val rvKeys  = rvKeys.next()
            val value  = railVolunteer.getJSONObject(rvKeys)
            val button = RailVolunteer.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }

        val railMaithriMeeting = loadFormData(this, Storage.RAILMAITHRI_MEETING)
        val rmKeys             = railMaithriMeeting.keys()
        while (rmKeys.hasNext()) {
            val rmKeys  = rmKeys.next()
            val value   = railMaithriMeeting.getJSONObject(rmKeys)
            val button  = RailMaithriMeeting.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }
    }

    private fun syncData() {
        syncBT.isClickable    = false
        progressPB.visibility = View.VISIBLE
        val token             = Helper.getData(this, Storage.TOKEN)!!

        val passengerStatistics = loadFormData(this, Storage.PASSENGER_STATISTICS)
        val psKeys              = passengerStatistics.keys()
        while (psKeys.hasNext()) {
            dataCount++
            val psKeys    = psKeys.next()
            val formData  = passengerStatistics.getJSONObject(psKeys)
            CoroutineScope(Dispatchers.IO).launch {
                sendFormData(URL.PASSENGER_STATISTICS, Storage.PASSENGER_STATISTICS, formData, token)
            }
        }

        val strangerCheck = loadFormData(this, Storage.STRANGER_CHECK)
        val scKeys        = strangerCheck.keys()
        while (scKeys.hasNext()) {
            dataCount++
            val scKeys    = scKeys.next()
            val formData  = strangerCheck.getJSONObject(scKeys)
            CoroutineScope(Dispatchers.IO).launch {
                sendFormData(URL.STRANGER_CHECK, Storage.STRANGER_CHECK, formData, token)
            }
        }

        if (dataCount == 0){
            syncBT.isClickable    = true
            progressPB.visibility = View.GONE
        }
    }

    private fun sendFormData(url: String, storage: String, formData: JSONObject, token: String): Boolean {
        var isSuccess = false
        var uuid      = ""

        var fieldLabel: String?   = null
        var file:      ByteArray? = null
        var fileName:  String?    = null

        when (url) {
            URL.PASSENGER_STATISTICS -> {
                uuid       = formData.getString("last_updated")
            }
            URL.STRANGER_CHECK -> {
                uuid       = formData.getString("checking_date_time")
                fieldLabel = "photo"
            }
            else -> {
                return false
            }
        }

        if (formData.optBoolean("__have_file", false)){
            fileName = formData.getString("__file_name")
            file     = Helper.loadFile(this, uuid)
        }

        val response = Helper.sendFormData(url, formData, token, file=file, fileName=fileName, fieldLabel=fieldLabel)
        if (response.first == ResponseType.SUCCESS) {
            Helper.removeFormData(this, uuid, storage)
            Helper.purgeFile(this, uuid)
            isSuccess = true
        } else if (response.first == ResponseType.API_ERROR) {
            Helper.showToast(this, response.second)
        } else if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.showToast(this, "No internet, try after some time")
        }

        dataCount--
        if (dataCount == 0) {
            Handler(Looper.getMainLooper()).post {
                syncBT.isClickable    = true
                progressPB.visibility = View.GONE
                loadDataToSync()
            }
        }
        return isSuccess
    }
}