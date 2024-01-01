package gov.keralapolice.railmaithri

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject

class Chat : AppCompatActivity() {
    private lateinit var officersSP  : Spinner
    private lateinit var officers    : JSONArray
    private lateinit var messageET   : EditText
    private lateinit var messageList : LinearLayout
    private lateinit var clientNT    : OkHttpClient
    private lateinit var token       : String
    private lateinit var sendBT      : Button

    private var messages    : JSONArray = JSONArray()
    private var receiverID  : Int = 0
    private var senderID    : Int = 0
    private var SENDED      : Int = 1
    private var RECEIVED    : Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)
        supportActionBar!!.hide()

        clientNT    = OkHttpClient().newBuilder().build()
        token       = Helper.getData(this, Storage.TOKEN)!!
        senderID    = JSONObject(Helper.getData(this, Storage.PROFILE)!!).getInt("id")
        messageList = findViewById(R.id.message_list)
        officersSP  = findViewById(R.id.receiver)
        messageET   = findViewById(R.id.message)
        officersSP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                messageList.removeAllViews()
                receiverID = officers.getJSONObject(position).getInt("id")
                CoroutineScope(Dispatchers.IO).launch {  fetchMessages()  }
            }
        }
        sendBT = findViewById(R.id.send)
        sendBT.setOnClickListener {
            if(messageET.text.isNotEmpty()){
                sendBT.isClickable = false
                CoroutineScope(Dispatchers.IO).launch {
                    postMessage(messageET.text.toString())
                    Handler(Looper.getMainLooper()).post {
                        sendBT.isClickable = true
                        messageList.removeAllViews()
                        messageET.text.clear()
                    }
                    fetchMessages()
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {  fetchOfficersList()  }
    }

    private fun postMessage(messageStr: String) {
        try {
            val message = JSONObject()
            message.put("message", messageStr)
            message.put("utc_timestamp", Helper.getUTC())
            message.put("sender", senderID)

            Log.e("Railmaithri", message.toString())

            val request  = API.post(URL.CLOSE_COMMUNICATION, message, token)
            val response = clientNT.newCall(request).execute()
            if (response.isSuccessful) {
                mapMessage(JSONObject(response.body!!.string()))
            } else {
                val apiResponse  = response.body!!.string()
                Log.e("Railmaithri", apiResponse.toString())
                val errorMessage = Helper.getError(apiResponse)
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Log.d("RailMaithri", e.stackTraceToString())
        }
    }

    private fun mapMessage(messageObject: JSONObject){
        Log.d("RailMaithri", messageObject.toString())
        try {
            val message = JSONObject()
            message.put("receiver", receiverID)
            message.put("message", messageObject.getInt("id"))

            val request  = API.post(URL.COMMUNICATION_RECEIVER, message, token)
            val response = clientNT.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("RailMaithri", "Success")
            } else {
                val apiResponse  = response.body!!.string()
                val errorMessage = Helper.getError(apiResponse)
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Log.d("RailMaithri", e.stackTraceToString())
        }
    }

    private fun fetchOfficersList(){
        val request  = API.get(URL.OFFICERS_IN_PS, token)
        val response = clientNT.newCall(request).execute()
        if (response.isSuccessful) {
            officers        = JSONArray()
            val allOfficers = JSONArray(response.body!!.string())
            (0 until allOfficers.length()).forEach {
                val officer   = allOfficers.getJSONObject(it)
                val officerID = officer.getInt("id")
                if(officerID != senderID){
                    officers.put(officer)
                }
            }
            Handler(Looper.getMainLooper()).post {
                officersSP.adapter = Helper.makeArrayAdapter(officers, this)
            }
        } else {
            Log.d("RailMaithri", "Failed to fetch officers list")
        }
    }

    private fun renderMessage(message: String, messageType: Int) {
        val messageTV = TextView(this)
        messageTV.textSize = 20f
        messageTV.text     = message
        if(messageType == SENDED) {
            messageTV.gravity  = Gravity.END
        }
        Handler(Looper.getMainLooper()).post {
            messageList.addView(messageTV)
        }
    }

    private fun fetchMessages(){
        messages = JSONArray()
        fetchMessage(receiverID, senderID)
        for (i in 0 until messages.length()) {
            val messageObj    = messages.getJSONObject(i)
            val msgReceiverID = messageObj.getInt("receiver")
            val message       = messageObj.getString("message_label")
            if (msgReceiverID == senderID){
                renderMessage(message, RECEIVED)
            } else {
                renderMessage(message, SENDED)
            }
        }
    }

    private fun fetchMessage(receiverID: Int, senderID: Int){
        val url       = URL.COMMUNICATION_RECEIVER +  "?receiver_id=${receiverID}&sender_id=${senderID}"
        val request   = API.get(url, token)
        val response  = clientNT.newCall(request).execute()

        if (response.isSuccessful) {
            val messagesObj  = JSONObject(response.body!!.string())
            val messageArray = messagesObj.getJSONArray("results")

            for (i in 0 until messageArray.length()) {
                messages.put(messageArray.getJSONObject(i))
            }
        } else {
            Log.d("RailMaithri", "Failed to fetch messages")
        }
    }
}