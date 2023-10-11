package gov.keralapolice.railmaithri

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import gov.keralapolice.railmaithri.Helper.Companion.loadFormData
import org.json.JSONObject

class BeatDiary : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var actionBT:      Button

    private lateinit var note:          EditText
    private lateinit var utcTime:       String
    private lateinit var profile:       JSONObject
    private lateinit var beatData:      JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beat_diary)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        actionBT     = findViewById(R.id.action)
        note         = findViewById(R.id.note)
        utcTime      = Helper.getUTC()
        profile      = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
        beatData     = profile.getJSONObject("last_beat_assignment")
        showAssignmentData()

        val serverNotesLY  = findViewById<LinearLayout>(R.id.serverNoteList)
        val savedNotesLY   = findViewById<LinearLayout>(R.id.savedNoteList)
        val beatDiary       = loadFormData(this, Storage.BEAT_DIARY)
        val bdKeys= beatDiary.keys()
        while (bdKeys.hasNext()) {
            val bdKeys   = bdKeys.next()
            val value = beatDiary.getJSONObject(bdKeys)
            val button   = generateButton(this, value, Mode.UPDATE_FORM)
            savedNotesLY.addView(button)
        }

        prepareActionButton()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            utcTime      = formData.getString("utc_timestamp")
            note.setText(formData.getString("description"))
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

    private fun performAction() {
        if (mode == Mode.NEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = getFormData()
            if (formData != null) {
                val beatID  = beatData.getInt("id")
                formData.put("beat_assignment", beatID)
                formData.put("utc_timestamp", utcTime)
                Helper.saveFormData(this, formData, Storage.BEAT_DIARY, utcTime)
                finish()
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

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM || mode == Mode.UPDATE_FORM){
            actionBT.text = "Save"
        }
        if(mode == Mode.VIEW_FORM) {
            actionBT.visibility = View.GONE
        }
    }

    fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
        val createdOn  = formData.getString("utc_timestamp").take(16).replace("T", "\t")
        val description= formData.getString("description")

        val shortData = "$createdOn\n$description"
        val button    = Button(context)
        button.isAllCaps = false
        button.gravity   = Gravity.START
        button.text      = shortData

        button.setOnClickListener {
            utcTime   = formData.getString("utc_timestamp")
            note.setText(formData.getString("description"))
        }
        return button
    }

}