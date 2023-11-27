package gov.keralapolice.railmaithri

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import gov.keralapolice.railmaithri.Helper.Companion.loadFormData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class BeatDiary : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var saveBT:        Button
    private lateinit var syncBT:        Button

    private lateinit var token:         String
    private lateinit var note:          EditText
    private lateinit var utcTime:       String
    private lateinit var profile:       JSONObject
    private lateinit var beatData:      JSONObject
    private var assignmentID            = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beat_diary)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        saveBT       = findViewById(R.id.save)
        syncBT       = findViewById(R.id.sync)
        note         = findViewById(R.id.note)

        token        = Helper.getData(this, Storage.TOKEN)!!
        utcTime      = Helper.getUTC()
        profile      = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        beatData     = profile.getJSONObject("last_beat_assignment")
        assignmentID = beatData.getInt("id")

        findViewById<TextView>(R.id.officer_name).text = profile.getString("username")

        showAssignmentData()
        showSavedData()
        CoroutineScope(Dispatchers.IO).launch {
            showServerData()
        }

        saveBT.setOnClickListener { saveData() }
        syncBT.setOnClickListener { syncData() }
    }

    private fun showServerData() {
        val parameters = JSONObject().put("id", assignmentID)
        val response = Helper.getFormData(URL.BEAT_ASSIGNMENT_DIARY, parameters, token)
        if (response.first == ResponseType.SUCCESS) {
            val resultData = JSONObject(response.second)
            val serverNotes = resultData.getJSONArray("results").getJSONObject(0).getJSONArray("beatAssignmentToBeatDiaryPid")

            Handler(Looper.getMainLooper()).post {
                val serverNotesLY  = findViewById<LinearLayout>(R.id.serverNoteList)
                serverNotesLY.removeAllViews()
                (0 until serverNotes.length()).forEach {
                    val serverNote = serverNotes.getJSONObject(it)
                    val button   = generateButton(this, serverNote, false)
                    serverNotesLY.addView(button)
                }
            }
        } else {
            Helper.showToast(this, response.second)
        }
    }

    private fun showSavedData() {
        val savedNotesLY   = findViewById<LinearLayout>(R.id.savedNoteList)
        val beatDiary       = loadFormData(this, Storage.BEAT_DIARY)
        val bdKeys= beatDiary.keys()

        savedNotesLY.removeAllViews()
        while (bdKeys.hasNext()) {
            val bdKeys   = bdKeys.next()
            val value = beatDiary.getJSONObject(bdKeys)
            val button   = generateButton(this, value)
            savedNotesLY.addView(button)
        }
    }

    private fun showAssignmentData() {
        val beatLabel = beatData.getString("beat_label")
        val beatFrom = beatData.getString("from_time_label").slice(IntRange(0, 15)).replace("T", " ")
        val beatTo = beatData.getString("to_time_label").slice(IntRange(0, 15)).replace("T", " ")
        val assignmentNote = beatData.getString("assignment_note")
        val assignmentOn = beatData.getString("assigned_on").slice(IntRange(0, 15)).replace("T", " ")

        findViewById<TextView>(R.id.assignmentNote).text = assignmentNote
        findViewById<TextView>(R.id.duration).text = "${beatFrom} <-> ${beatTo}"
        findViewById<TextView>(R.id.dutyNote).text = beatLabel
        findViewById<TextView>(R.id.addedOn).text = assignmentOn
    }

    private fun saveData() {
        val formData = getFormData()
        if (formData != null) {
            val beatID  = beatData.getInt("id")
            formData.put("beat_assignment", beatID)
            formData.put("utc_timestamp", utcTime)
            Helper.saveFormData(this, formData, Storage.BEAT_DIARY, utcTime)
            finish()
        }
    }

    private fun syncData() {
        val token         = Helper.getData(this, Storage.TOKEN)!!
        val beatDiary = loadFormData(this, Storage.BEAT_DIARY)
        val bdKeys = beatDiary.keys()
        while (bdKeys.hasNext()) {
            val bdKeys    = bdKeys.next()
            val formData  = beatDiary.getJSONObject(bdKeys)
            CoroutineScope(Dispatchers.IO).launch {
                sendFormData(URL.BEAT_DIARY, Storage.BEAT_DIARY, formData, token)
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        val noteData = note.text.toString()
        if (noteData.isEmpty()) {
            Helper.showToast(this, "Data is mandatory")
            return null
        } else {
            formData.put("description", noteData)
        }
        return formData
    }

    fun generateButton(context: Context, formData: JSONObject, isInteractive: Boolean=true): Button {
        val createdOn  = formData.getString("utc_timestamp").take(16).replace("T", "\t")
        val description= formData.getString("description")

        val shortData    = "$createdOn\n$description"
        val button       = Button(context)
        button.isAllCaps = false
        button.gravity   = Gravity.START
        button.text      = shortData
        if(isInteractive){
            button.setTextColor(getColor(R.color.Green))
            button.setOnClickListener {
                utcTime   = formData.getString("utc_timestamp")
                note.setText(formData.getString("description"))
            }
        }
        return button
    }

    private fun sendFormData(url: String, storage: String, formData: JSONObject, token: String) {
        var uuid = formData.getString("utc_timestamp")
        val response = Helper.sendFormData(url, formData, token)
        when (response.first) {
            ResponseType.SUCCESS -> {
                Helper.removeFormData(this, uuid, storage)
                Helper.purgeFile(this, uuid)
                Handler(Looper.getMainLooper()).post {
                    showSavedData()
                }
                showServerData()
            }
            ResponseType.API_ERROR -> {
                Helper.showToast(this, response.second)
            }
            ResponseType.NETWORK_ERROR -> {
                Helper.showToast(this, "No internet, try after some time")
            }
        }
    }
}