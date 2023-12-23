package gov.keralapolice.railmaithri

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject

class LocationUtil(_activity: Activity, _locationLY: ConstraintLayout) {
    private var latitude:   Double? = null
    private var longitude:  Double? = null
    private var accuracy:   Float?  = null
    private var isHidden:   Boolean = false
    private var isRequired: Boolean = true

    private var locationDataTV: TextView
    private var locationAccuracyTV: TextView
    private var locationLY: ConstraintLayout
    private var getLocationBT: Button
    private var openLocationBT: Button
    private var labelText: TextView

    init {
        locationLY         = _locationLY
        locationDataTV     = _activity.findViewById(R.id.location_data)
        locationAccuracyTV = _activity.findViewById(R.id.location_accuracy)
        getLocationBT      = _activity.findViewById(R.id.get_location)
        openLocationBT     = _activity.findViewById(R.id.open_location)
        labelText          = _activity.findViewById(R.id.label_location)

        getLocationBT.setOnClickListener { fetchLocation(_activity.applicationContext) }
        openLocationBT.setOnClickListener {
            if (haveLocation()) {
                val mapUri = Uri.parse("geo:0,0?q=${latitude},${longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                try {
                    _activity.startActivity(mapIntent)
                } catch (e: ActivityNotFoundException) {
                    val message = "Failed to open map"
                    Toast.makeText(_activity.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            } else{
                val message = "Please fix a location to open it in map"
                Toast.makeText(_activity.applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
        fetchLocation(_activity)
    }
    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        if (Helper.haveLocationPermission(context)) {
            disableUpdate()

            locationDataTV.text       = "Locating ...."
            Helper.getLocation(context, fun(location: Location?) {
                if (location != null)  {
                    val latitude  = location.latitude
                    val longitude = location.longitude
                    val accuracy  = location.accuracy
                    importLocation(latitude, longitude, accuracy)
                } else{
                    latitude  = null
                    longitude = null
                    accuracy  = null
                    locationDataTV.text     = "Location : unknown !!"
                    locationAccuracyTV.text = "Accuracy : unknown !!"
                }
                enableUpdate()
            })
        } else {
            val message = "No GPS !!, please check permission"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    fun haveLocation(): Boolean {
        return !(latitude == null || longitude == null)
    }
    fun enableUpdate () {
        getLocationBT.isClickable = true
    }

    fun disableUpdate() {
        getLocationBT.isClickable = false
    }

    fun importLocation(_formData: JSONObject){
        val latitude  = _formData.getDouble("latitude")
        val longitude = _formData.getDouble("longitude")
        val accuracy  = _formData.optDouble("accuracy", 2.0).toFloat()
        importLocation(latitude, longitude, accuracy)
    }

    fun importLocation(_latitude: Double, _longitude: Double, _accuracy: Float) {
        latitude  = _latitude
        longitude = _longitude
        accuracy  = _accuracy

        val latitudeString      = latitude.toString().substring(0, 8)
        val longitudeString     = longitude.toString().substring(0, 8)
        val locationString      = "Location : ${latitudeString}, $longitudeString"
        val accuracyString      = "Accuracy : ${accuracy}m"
        locationDataTV.text     = locationString
        locationAccuracyTV.text = accuracyString
    }
    fun exportLocation(data: JSONObject): JSONObject {
        if(haveLocation()){
            if(!isHidden){
                data.put("latitude",  latitude)
                data.put("longitude", longitude)
                data.put("accuracy",  accuracy)
            }
        } else {
            if(isRequired) {
                val errorMessage = "Location is mandatory"
                throw(FieldRequiredException(errorMessage))
            }
        }
        return data
    }

    fun hide() {
        locationLY.visibility = View.GONE
        isHidden              = true
    }

    fun show() {
        locationLY.visibility = View.VISIBLE
        isHidden              = false
    }

    fun markAsRequired() {
        isRequired = true
        labelText.setTextColor(Color.RED)
    }

    fun markAsNotRequired() {
        isRequired = false
        labelText.setTextColor(Color.GRAY)
    }
}