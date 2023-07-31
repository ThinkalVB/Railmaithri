package gov.keralapolice.railmaithri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
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

    private lateinit var locationUtil:      LocationUtil
    private lateinit var shopCategory:      FieldSpinner
    private lateinit var shopName:          FieldEditText
    private lateinit var ownerName:         FieldEditText
    private lateinit var aadhaarNumber:     FieldEditText
    private lateinit var mobileNumber:      FieldEditText
    private lateinit var licenseNumber:     FieldEditText
    private lateinit var railwayStation:    FieldSpinner
    private lateinit var platformNumber:    FieldEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_and_labours)
        supportActionBar!!.hide()

        mode         = intent.getStringExtra("mode")!!
        progressPB   = findViewById(R.id.progress_bar)
        actionBT     = findViewById(R.id.action)

        locationUtil = LocationUtil(this, findViewById(R.id.ly_location))

        prepareActionButton()
        renderForm()
//        actionBT.setOnClickListener { performAction() }
//
//        if (mode == Mode.VIEW_FORM || mode == Mode.UPDATE_FORM) {
//            val formData = JSONObject(intent.getStringExtra("data")!!)
//            loadFormData(formData)
//        }
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
            fieldName = "Aadhaar Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        mobileNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "contact_number",
            fieldName = "Mobile Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        licenseNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "licence_number",
            fieldName = "License Number",
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        railwayStation = FieldSpinner(this,
            JSONArray(Helper.getData(this, Storage.RAILWAY_STATIONS_LIST)!!),
            "railway_station",
            "Railway Station",
            addEmptyValue = Helper.resolveAddEmptyValue(false, mode),
            isRequired = Helper.resolveIsRequired(true, mode)
        )
        platformNumber = FieldEditText(this,
            fieldType = "number",
            fieldLabel = "platform_number",
            fieldName = "Platform Number",
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
        }
    }
}