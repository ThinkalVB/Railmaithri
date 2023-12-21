package gov.keralapolice.railmaithri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class TaskList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list)
        supportActionBar!!.hide()

        CoroutineScope(Dispatchers.IO).launch {  fetchTasks()  }
    }

    private fun fetchTasks(){
        val token    = Helper.getData(this, Storage.TOKEN)!!
        val response = Helper.getFormData(URL.TASK_LIST, JSONObject(), token)
        if (response.first == ResponseType.SUCCESS) {
            runOnUiThread {
                populateTasks(JSONArray(response.second))
            }
        } else {
            Helper.showToast(this, response.second)
        }
    }

    private fun populateTasks(tasks: JSONArray){
        val linearLayout: LinearLayout = findViewById(R.id.task_list)
        for (i in 0 until tasks.length()) {
            val task   = tasks.getJSONObject(i)
            val button = Button(this)
            val buttonData = "ID: ${task.getString("id")}\n" +
                    "Task : ${task.getString("notification_type")}\n" +
                    task.getString("notification_message")
            button.isAllCaps = false
            button.gravity   = Gravity.START
            button.text      = buttonData
            button.setBackgroundResource(R.drawable.search_view_box)
            button.setPadding(16, 10, 16, 10)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 0, 8, 16)
            button.layoutParams = layoutParams
            button.setOnClickListener {
                val taskType = task.getString("notification_type")
                val taskID   = task.getInt("item_id")
                val intent   = Intent(this, ViewTask::class.java)
                intent.putExtra("task_type", taskType)
                intent.putExtra("task_id", taskID)
                startActivity(intent)
            }
            linearLayout.addView(button)
        }
    }
}