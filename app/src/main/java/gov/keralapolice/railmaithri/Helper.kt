package gov.keralapolice.railmaithri

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.Manifest.permission
import android.location.Location
import android.location.LocationManager
import android.annotation.SuppressLint
import android.content.pm.PackageManager


class Helper {
    companion object {
        // Get current UTC timestamp
        fun getUTC() : String{
            return TimeZone.getTimeZone("UTC").let {
                val calendar  = Calendar.getInstance(it)
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                formatter.format(calendar.time)
            }
        }

        // Save a value against a key from shared preference
        fun saveData(context: Context, key: String, value: String) {
            val sharedPref = context.getSharedPreferences("app_store", Context.MODE_PRIVATE)
            val editor     = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        // Get a value against a key from shared preference
        fun getData(context: Context, key: String): String? {
            val sharedPref = context.getSharedPreferences("app_store", Context.MODE_PRIVATE)
            return sharedPref.getString(key, "")
        }

        // Remove a key and value from shared preference
        fun removeData(context: Context, key: String) {
            val sharedPref = context.getSharedPreferences("app_store", Context.MODE_PRIVATE)
            sharedPref.edit().remove(key).apply()
        }

        // Show a short message
        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, message, duration).show()
            }
        }

        // Decode error message from api response
        fun getError(response: String): String {
            return try {
                val apiResponse   = JSONObject(response)
                val errorMessages = apiResponse.getJSONArray("non_field_errors")
                errorMessages.getString(0)
            }catch (_: Exception) {
                var errorMessage = response.replace("[^A-Za-z0-9: ]".toRegex(), " ").trim()
                errorMessage = errorMessage.replace("\\s+".toRegex()) { it.value[0].toString() }
                errorMessage.lowercase().replaceFirstChar(Char::uppercase)
                errorMessage
            }
        }

        // Save the file to app memory
        fun saveFile(context: Context, file: ByteArray, fileName: String) {
            try {
                val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                outputStream.write(file)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Load file from app memory
        fun loadFile(context: Context, fileName: String): ByteArray? {
            var file: ByteArray? = null
            try {
                val inputStream = context.openFileInput(fileName)
                file = inputStream.readBytes()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file
        }

        // Remove from app memory
        fun purgeFile(context: Context, fileName: String) {
            try {
                val file = context.getFileStreamPath(fileName)
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Check if the phone have location permission
        fun haveLocationPermission(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isEnabled       = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val permission      = permission.ACCESS_FINE_LOCATION
            val havePermission  = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            return havePermission && isEnabled
        }

        // Get latest location
        @SuppressLint("MissingPermission")
        fun getLocation(context: Context, callback: (location: Location?) -> Unit) {
            val cToken          = CancellationTokenSource().token
            val fusedLocation   = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cToken)
            locationRequest.addOnSuccessListener {      location : Location? ->
                fusedLocation.lastLocation.addOnSuccessListener { location : Location? ->
                    callback(location)
                }
            }
        }

        // Save form data
        fun saveFormData(context: Context, formData: JSONObject, formType: String) {
            val key      = formData.getString("utc_timestamp")
            val savedStr = getData(context, formType)
            var savedObj = JSONObject()
            if(!savedStr.isNullOrEmpty()){
                savedObj = JSONObject(savedStr)
            }
            savedObj.put(key, formData)
            saveData(context, formType, savedObj.toString())
        }

        // Remove form data
        fun removeFormData(context: Context, key: String, formType: String) {
            val savedStr = getData(context, formType)
            var savedObj = JSONObject()
            if(!savedStr.isNullOrEmpty()){
                savedObj = JSONObject(savedStr)
                savedObj.remove(key)
            }
            saveData(context, formType, savedObj.toString())
        }

        // Load form data
        fun loadFormData(context: Context, key: String, formType: String): JSONObject {
            val savedStr = getData(context, formType)
            if(!savedStr.isNullOrEmpty()){
                val savedObj = JSONObject(savedStr)
                return savedObj.getJSONObject(key)
            }
            return JSONObject()
        }

        // Resolve the value of isRequired attribute based on the mode
        fun resolveIsRequired(defaultValue: Boolean, mode: String) : Boolean {
            return if(mode == Mode.SEARCH_FORM || mode == Mode.VIEW_FORM){
                false
            }else{
                defaultValue
            }
        }

        // Resolve the value of isReadonly attribute based on the mode
        fun resolveIsReadonly(defaultValue: Boolean, mode: String) : Boolean {
            return if(mode == Mode.VIEW_FORM){
                true
            }else{
                defaultValue
            }
        }

        // Resolve the value of addEmptyValue attribute based on the mode
        fun resolveAddEmptyValue(defaultValue: Boolean, mode: String) : Boolean {
            return if(mode == Mode.SEARCH_FORM){
                true
            }else{
                defaultValue
            }
        }
    }
}