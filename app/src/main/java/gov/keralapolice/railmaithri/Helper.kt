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
    }
}