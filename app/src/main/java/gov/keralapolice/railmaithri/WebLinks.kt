package gov.keralapolice.railmaithri

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.json.JSONArray
import org.json.JSONObject

class WebLinks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_links)
        supportActionBar!!.hide()

        val webLinksList = findViewById<LinearLayout>(R.id.web_links_list)

        val webLinksData = JSONObject(Helper.getData(this, Storage.WEB_LINKS)!!)
        val webLinks = webLinksData.getJSONArray("results")

        for (i in 0 until webLinks.length()) {
            val webLink = webLinks.getJSONObject(i)
            val cardView = generateWebLinkCardView(this, webLink)
            webLinksList.addView(cardView)
        }
    }

    private fun generateWebLinkCardView(context: Context, webLinkData: JSONObject): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.web_link_item, null) as LinearLayout
        val textView = cardView.findViewById<TextView>(R.id.textView)

        textView.text = generateWebLinkText(webLinkData)

        cardView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLinkData.getString("url")))
            context.startActivity(intent)
        }

        return cardView
    }
    private fun generateWebLinkText(webLinkData: JSONObject): String {
            val description = webLinkData.getString("description")
            val url = webLinkData.getString("url")

            return "Description: $description\nURL: $url"
    }
}
