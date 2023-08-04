package gov.keralapolice.railmaithri

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import org.json.JSONArray
import org.json.JSONObject

class WatchZone : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_zone)

        val watchZoneList = findViewById<LinearLayout>(R.id.watch_zone_list)
        val watchZones    = JSONArray(Helper.getData(this, Storage.WATCH_ZONE)!!)
        for (i in 0 until watchZones.length()) {
            val watchZone   = watchZones.getJSONObject(i)
            val button      = generateButton(this, watchZone)
            watchZoneList.addView(button)
        }
    }

    companion object{
        fun generateButton(context: Context, formData: JSONObject, mode: String? = Mode.VIEW_FORM): Button {
            val formID    = formData.optString("id", "Not assigned")
            val category  = formData.getString("watch_zone_category_label")

            val startingStation = formData.getString("between_station_1_label")
            val endingStation   = formData.getString("between_station_2_label")
            val shortData = "ID ${formID}\nCategory: ${category}\nStart point: ${startingStation}\nEnd point : $endingStation"

            val button = Button(context)
            button.isAllCaps = false
            button.gravity = Gravity.START
            button.text = shortData
            return button
        }
    }
}