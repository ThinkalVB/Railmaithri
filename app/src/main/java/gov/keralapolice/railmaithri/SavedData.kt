package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import gov.keralapolice.railmaithri.Helper.Companion.loadFormData
import org.json.JSONObject

class SavedData : AppCompatActivity() {
    private lateinit var progressPB:        ProgressBar
    private lateinit var syncBT:            Button
    private lateinit var resultLayout:      LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_data)
        supportActionBar!!.hide()

        progressPB    = findViewById(R.id.progress_bar)
        syncBT        = findViewById(R.id.load_more)
        resultLayout  = findViewById(R.id.form_data_list)

        val passengerStatistics = loadFormData(this, Storage.PASSENGER_STATISTICS)
        val psKeys              = passengerStatistics.keys()
        while (psKeys.hasNext()) {
            val psKeys = psKeys.next()
            val value  = passengerStatistics.getJSONObject(psKeys)
            val button = PassengerStatistics.generateButton(this, value, Mode.UPDATE_FORM)
            resultLayout.addView(button)
        }
    }
}