package gov.keralapolice.railmaithri.adapters

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.StrangerModel
import android.view.LayoutInflater as LayoutInflater1


class ListAdapterStrangerCheck(
    private val context1: Context,
    private val strangerDatas: List<StrangerModel>
) : BaseAdapter() {
    private val context = context1
    private val strangerData = strangerDatas
    override fun getCount(): Int {
        return strangerData.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var listitemView = convertView
        if (listitemView == null) {

            val inflater = LayoutInflater1.from(context)
            listitemView = inflater.inflate(R.layout.search_data_view, parent, false)

        }

        val topLayout = listitemView?.findViewById<LinearLayout>(R.id.top_layout)
        val id = listitemView?.findViewById<TextView>(R.id.imageView)
        val name = listitemView?.findViewById<TextView>(R.id.name)
        val languageKnown = listitemView?.findViewById<TextView>(R.id.language)
        val age = listitemView?.findViewById<TextView>(R.id.age)
        val purpose = listitemView?.findViewById<TextView>(R.id.purpose)
        val mobile = listitemView?.findViewById<TextView>(R.id.mobile)
        name?.text = strangerData[position]?.name
        languageKnown?.text = "Language Known: " + strangerData[position]?.languages_known
        age?.text = "Age: " + strangerData[position]?.age
        purpose?.text = "Purpose: " + strangerData[position]?.purpose_of_visit

        mobile?.text = "Mobile: " + strangerData[position]?.mobile_number

        return listitemView!!
    }
}
