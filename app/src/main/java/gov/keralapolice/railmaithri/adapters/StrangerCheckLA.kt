package gov.keralapolice.railmaithri.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.StrangerCheckMD
import android.view.LayoutInflater as LayoutInflater1


class StrangerCheckLA(
    private val context1:     Context,
    private val strangerData: List<StrangerCheckMD>
) : BaseAdapter() {
    private val context       = context1
    private val strangerDatum = strangerData
    override fun getCount(): Int {
        return strangerDatum.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var listItemView = convertView
        if (listItemView == null) {
            val inflater = LayoutInflater1.from(context)
            listItemView = inflater.inflate(R.layout.search_data_view, parent, false)
        }

        val name          = listItemView?.findViewById<TextView>(R.id.name)
        val languageKnown = listItemView?.findViewById<TextView>(R.id.language)
        val age           = listItemView?.findViewById<TextView>(R.id.age)
        val purpose       = listItemView?.findViewById<TextView>(R.id.purpose)
        val mobile        = listItemView?.findViewById<TextView>(R.id.mobile)

        name?.text          = strangerDatum[position]?.name
        languageKnown?.text = "Language Known: " + strangerDatum[position]?.languages_known
        age?.text           = "Age: " + strangerDatum[position]?.age
        purpose?.text       = "Purpose: " + strangerDatum[position]?.purpose_of_visit
        mobile?.text        = "Mobile: " + strangerDatum[position]?.mobile_number
        return listItemView!!
    }
}
