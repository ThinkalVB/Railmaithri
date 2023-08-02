package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ShopAndLabours : AppCompatActivity() {
    private lateinit var mode:              String
    private lateinit var progressPB:        ProgressBar
    private lateinit var actionBT:          Button
    private lateinit var addLaboursBT:      Button

    private lateinit var locationUtil:      LocationUtil
    private lateinit var shopCategory:      FieldSpinner
    private lateinit var shopName:          FieldEditText
    private lateinit var ownerName:         FieldEditText
    private lateinit var aadhaarNumber:     FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var licenseNumber:     FieldEditText
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var platformNumber:    FieldEditText
    private var labourData                  = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_and_labours)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)
        addLaboursBT = findViewById(R.id.add_labours)

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))

        prepareActionButton()
        renderForm()
        actionBT.setOnClickListener { performAction() }
        addLaboursBT.setOnClickListener {
            val intent = Intent(this, Labour::class.java)
            intent.putExtra("mode", Mode.NEW_FORM)
            startActivityForResult(intent, 1000)
        }

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
                formData.put("data_from", "Beat Officer")
                CoroutineScope(Dispatchers.IO).launch {  sendFormData(formData)  }
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
        } else if (mode == Mode.UPDATE_FORM){
            val formData = JSONObject(intent.getStringExtra("data")!!)
            val uuid     = formData.getString("utc_timestamp")
            getFormData(formData)
            Helper.saveFormData(this, formData, Storage.SHOPS, uuid)
            finish()
        }
    }

    private fun sendFormData(formData: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = false
            progressPB.visibility = View.VISIBLE
        }

        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.sendFormData(URL.SHOPS, formData, token)

        val uuid = formData.getString("utc_timestamp")
        if (response.first == ResponseType.SUCCESS) {
            val shopID = JSONObject(response.second).getInt("id")
            sendLabourData(shopID, token)
            Helper.showToast(this, "success")
            finish()
        }
        Helper.showToast(this, response.second)
        if (response.first == ResponseType.NETWORK_ERROR) {
            Helper.saveFormData(this, formData, Storage.SHOPS, uuid)
            finish()
        }

        Handler(Looper.getMainLooper()).post {
            actionBT.isClickable  = true
            progressPB.visibility = View.GONE
        }
    }

    private fun sendLabourData(shopID: Int, token: String) {
        for (i in 0 until labourData.length()) {
            val labourDatum = labourData.getJSONObject(i)
            labourDatum.put("shop", shopID)

            if (labourDatum.getBoolean("__have_file")){
                val uuid     = labourDatum.getString("utc_timestamp")
                val fileName = labourDatum.getString("__file_name")
                val file     = Helper.loadFile(this, uuid)
                val response = Helper.sendFormData(URL.LABOURS, labourDatum, token, null, file, fileName, "photo")
                Log.e("Railmaithri", response.toString())
            } else {
                val response = Helper.sendFormData(URL.LABOURS, labourDatum, token)
                Log.e("Railmaithri", response.toString())
            }
        }
    }

    private fun getFormData(formData: JSONObject = JSONObject()): JSONObject? {
        if (mode == Mode.NEW_FORM){
            if (!locationUtil.haveLocation()) {
                Helper.showToast(this, "Location is mandatory")
                return null
            } else {
                locationUtil.exportLocation(formData)
            }
        }

        try{
            shopCategory.exportData(formData)
            shopName.exportData(formData)
            ownerName.exportData(formData)
            aadhaarNumber.exportData(formData)
            mobileNumber.exportData(formData)
            licenseNumber.exportData(formData)
            railwayStation.exportData(formData)
            platformNumber.exportData(formData)
        } catch (e: Exception){
            Helper.showToast(this, e.message!!)
            return null
        }
        return formData
    }

    private fun loadFormData(formData: JSONObject) {
        shopCategory.importData(formData)
        shopName.importData(formData)
        ownerName.importData(formData)
        aadhaarNumber.importData(formData)
        mobileNumber.importData(formData)
        licenseNumber.importData(formData)
        railwayStation.importData(formData)
        platformNumber.importData(formData)

        val latitude  = formData.getDouble("latitude")
        val longitude = formData.getDouble("longitude")
        var accuracy  = 0.0f
        if (mode == Mode.UPDATE_FORM) {
            accuracy = formData.getDouble("accuracy").toFloat()
        }
        locationUtil.importLocation(latitude, longitude, accuracy)
        if(mode == Mode.VIEW_FORM){
            locationUtil.disableUpdate()
        }
    }

    private fun prepareActionButton() {
        if(mode == Mode.NEW_FORM){
            actionBT.text = "Save"
            locationUtil.fetchLocation(this)
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

    private fun renderForm() {
        shopCategory = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.SHOP_TYPES)!!),
            "shop_category",
            "Shop category",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        shopName = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "name",
            fieldName = "Shop name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        ownerName = FieldEditText(this,
            fieldType = "text",
            fieldLabel = "owner_name",
            fieldName = "Owner name",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        aadhaarNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "aadhar_number",
            fieldName = "Aadhaar number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "contact_number",
            fieldName = "Mobile number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        licenseNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "licence_number",
            fieldName = "License number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        platformNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "platform_number",
            fieldName = "Platform number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )

        val form = findViewById<LinearLayout>(R.id.form)
        form.addView(shopCategory.getLayout())
        form.addView(shopName.getLayout())
        form.addView(ownerName.getLayout())
        form.addView(aadhaarNumber.getLayout())
        form.addView(mobileNumber.getLayout())
        form.addView(licenseNumber.getLayout())
        form.addView(railwayStation.getLayout())
        form.addView(platformNumber.getLayout())

        if (mode == Mode.SEARCH_FORM){
            findViewById<ConstraintLayout>(R.id.ly_location).visibility = View.GONE
            addLaboursBT.visibility = View.GONE
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID      = formData.optString("id", "Not assigned")
            val shopName    = formData.getString("name")
            val createdOn   = formData.getString("utc_timestamp")
                .take(16).replace("T", "\t")
            val shortData = "ID ${formID}\nName: ${shopName}\nDate: $createdOn"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            button.setOnClickListener {
                val intent = Intent(context,  ShopAndLabours::class.java)
                intent.putExtra("mode", mode)
                intent.putExtra("data", formData.toString())
                context.startActivity(intent)
            }
            return button
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            val labourDatum = JSONObject(resultIntent!!.getStringExtra("data")!!)
            val form        = findViewById<LinearLayout>(R.id.form)
            form.addView(Labour.generateButton(this, labourDatum, Mode.VIEW_FORM))
            labourData.put(labourDatum)
        }
    }
}