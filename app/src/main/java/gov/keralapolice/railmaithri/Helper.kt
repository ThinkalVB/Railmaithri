package gov.keralapolice.railmaithri

import android.content.Context
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
    }
}