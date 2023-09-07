package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.json.JSONObject

class BeatDiary : AppCompatActivity() {
    private lateinit var mode:          String
    private lateinit var actionBT:      Button

    private lateinit var note:          EditText
    private lateinit var utcTime:       String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beat_diary)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        actionBT     = findViewById(R.id.action)
        note         = findViewById(R.id.note)
        utcTime      = Helper.getUTC()

        prepareActionButton()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            utcTime      = formData.getString("utc_timestamp")
            note.setText(formData.getString("description"))
        }
    }

    private fun performAction() {
        if (mode == Mode.NEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = getFormData()
            if (formData != null) {
                val profile = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
                val beatID  = profile.getJSONObject("last_beat_assignment").getInt("id")

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

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val noteID           = formData.optString("id", "Diary Notes")
            val createdOn        = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${noteID}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  BeatDiary::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }
}