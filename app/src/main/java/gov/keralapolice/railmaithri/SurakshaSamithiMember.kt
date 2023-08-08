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

class SurakshaSamithiMember : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button

    private lateinit var surakshaSamithi:   FieldSpinner
    private lateinit var name:              FieldEditText
    private lateinit var address:           FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var email:             FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.suraksha_samithi_member)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

        prepareActionButton()
        renderForm()
        actionBT.setOnClickListener { performAction() }

        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
            val formData = JSONObject(intent.getStringExtra("data")!!)
            loadFormData(formData)
        }
    }

    private fun performAction() {
        if (mode == Mode.NEW_FORM){
            val formData = getFormData()
            if (formData != null) {
                val utcTime = Helper.getUTC()
                formData.put("utc_timestamp", utcTime)
                CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
            }
        } else if (mode == Mode.SEARCH_FORM) {
            var formData = getFormData()
            if (formData == null){
                formData = JSONObject()
            }

            val profile   = JSONObject(Helper.getData(this, Storage.PROFILE)!!)
            val stationID = profile.getJSONArray("police_station").getJSONObject(0).getInt("id")
            formData.put("suraksha_samithi__police_station", stationID)

            val intent = Intent()
            intent.putExtra("parameters", formData.toString())
            setResult(RESULT_OK, intent)
            finish()
        } else if (mode == Mode.UPDATE_FORM){
            val formData = JSONObject(intent.getStringExtra("data")!!)
            val uuid     = formData.getString("utc_timestamp")
            getFormData(formData)
            Helper.saveFormData(this, formData, Storage.SURAKSHA_SAMITHI_MEMBERS, uuid)
            finish()
        }
    }

    private fun renderForm() {
        surakshaSamithi = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.SURAKSHA_SAMITHI_LIST)!!),
            "suraksha_samithi",
            "Suraksha Samithi",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        name = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        address = FieldEditText(this,
            fieldType = "multiline",
            fieldLabel = "address",
            fieldName = "Address",
            fieldHeight=98,
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "mobile_number",
            fieldName = "Mobile Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        email = FieldEditText(this,
            fieldType = "email",
            fieldLabel = "email",
            fieldName = "E-mail",
            isRequired = Helper.resolveIsRequired(false, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(surakshaSamithi.getLayout())
        form.addView(name.getLayout())
        form.addView(address.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(email.getLayout())
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.SURAKSHA_SAMITHI_MEMBERS, formData, token)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.SURAKSHA_SAMITHI_MEMBERS, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
        }
        if(mode == Mode.UPDATE_FORM) {
            actionBT.text = "Update"
        }
        if(mode == Mode.VIEW_FORM) {
            actionBT.visibility = View.GONE
        }
        if(mode == Mode.SEARCH_FORM) {
            actionBT.text = "Search"
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        try{
            surakshaSamithi.exportData(formData)
            name.exportData(formData)
            address.exportData(formData)
            mobileNumber.exportData(formData)
            email.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        surakshaSamithi.importData(formData)
        name.importData(formData)
        address.importData(formData)
        mobileNumber.importData(formData)
        email.importData(formData)
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID           = formData.optString("id", "Not assigned")
            val surakshaSamithi  = formData.getString("suraksha_samithi")
            val createdOn        = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nSurakshaSamithi: ${surakshaSamithi}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity   = Gravity.START
            button.text      = shortData
            button.setOnClickListener {
                val intent = Intent(context,  SurakshaSamithiMember::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }


}