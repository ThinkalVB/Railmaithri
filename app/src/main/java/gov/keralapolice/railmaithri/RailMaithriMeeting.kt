package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RailMaithriMeeting : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var progressPB:    ProgressBar
    private lateinit var actionBT:      Button

    private lateinit var meetingType:           FieldSpinner
    private lateinit var meetingDate:           FieldEditText
    private lateinit var participants:          FieldEditText
    private lateinit var gistOfDecisionTaken:   FieldEditText
    private lateinit var nextMeetingDate:       FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rail_maithri_meeting)
        supportActionBar!!.hide()

        mode = intent.getStringExtra("mode")!!
        progressPB = findViewById(R.id.progress_bar)
        actionBT = findViewById(R.id.action)

        prepareActionButton()
        renderForm()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
    }

    private fun performAction() {
        if (mode == Mode.NEW_FORM) {
            val formData = getFormData()
            if (formData != null) {
                val utcTime = Helper.getUTC()
                formData.put("utc_timestamp", utcTime)
                CoroutineScope(Dispatchers.IO).launch { sendFormData(formData) }
            }
        } else if (mode == Mode.SEARCH_FORM) {
            var formData = getFormData()
            if (formData == null) {
                formData = JSONObject()
            }
            val intent = Intent()
            intent.putExtra("parameters", formData.toString())
            setResult(RESULT_OK, intent)
            finish()
        } else if (mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            val uuid = formData.getString("utc_timestamp")
            getFormData(formData)
            Helper.saveFormData(this, formData, Storage.RAILMAITHRI_MEETING, uuid)
            finish()
        }
    }

    private fun renderForm() {
        meetingType = FieldSpinner(
            this,
            JSONArray(Helper.getData(this, Storage.MEETING_TYPES)!!),
            "meeting_type",
            "Meeting type",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        meetingDate = FieldEditText(
            this,
            fieldType = "date",
            fieldLabel = "meeting_date",
            fieldName = "Meeting date",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        participants = FieldEditText(
            this,
            fieldType = "text",
            fieldLabel = "participants",
            fieldName = "Participants",
            fieldHeight = 98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        gistOfDecisionTaken = FieldEditText(
            this,
            fieldType = "text",
            fieldLabel = "gist_of_decisions_taken",
            fieldName = "Gist of decisions taken",
            fieldHeight = 98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        nextMeetingDate = FieldEditText(
            this,
            fieldType = "date",
            fieldLabel = "next_meeting_date",
            fieldName = "Next meeting date",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(meetingType.getLayout())
        form.addView(meetingDate.getLayout())
        form.addView(participants.getLayout())
        form.addView(gistOfDecisionTaken.getLayout())
        form.addView(nextMeetingDate.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.RAILMAITHRI_MEETING, formData, token)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.RAILMAITHRI_MEETING, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable = true
            progressPB.visibility = View.GONE
        }
    }

    private fun prepareActionButton() {
        if (mode == Mode.NEW_FORM) {
            actionBT.text = "Save"
        }
        if (mode == Mode.UPDATE_FORM) {
            actionBT.text = "Update"
        }
        if (mode == Mode.VIEW_FORM) {
            actionBT.visibility = View.GONE
        }
        if (mode == Mode.SEARCH_FORM) {
            actionBT.text = "Search"
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try {
            meetingType.exportData(formData)
            meetingDate.exportData(formData)
            participants.exportData(formData)
            gistOfDecisionTaken.exportData(formData)
            nextMeetingDate.exportData(formData)
        } catch (e: Exception) {
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        meetingType.importData(formData)
        meetingDate.importData(formData)
        participants.importData(formData)
        gistOfDecisionTaken.importData(formData)
        nextMeetingDate.importData(formData)
    }

    companion object {
        fun generateButton(
            context: Context,
            formData: JSONObject,
            mode: String? = Mode.VIEW_FORM
        ): Button {
            val formID = formData.optString("id", "Not assigned")
            val type = formData.getString("meeting_type")
            val createdOn = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nMeeting: ${type}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context, RailMaithriMeeting::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}
