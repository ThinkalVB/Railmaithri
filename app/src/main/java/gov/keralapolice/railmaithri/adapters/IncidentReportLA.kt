package gov.keralapolice.railmaithri.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.IncidentReportMD
import android.view.LayoutInflater as LayoutInflater1

class IncidentReportLA(
    private val context1:     Context,
    private val incidentData: List<IncidentReportMD>
) : BaseAdapter() {
    private val context       = context1
    private val incidentDatum = incidentData
    override fun getCount(): Int {
        return incidentDatum.size
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

        val attr1   = listItemView?.findViewById<TextView>(R.id.attr1)
        val val1    = listItemView?.findViewById<TextView>(R.id.val1)
        val attr2   = listItemView?.findViewById<TextView>(R.id.attr2)
        val val2    = listItemView?.findViewById<TextView>(R.id.val2)
        val attr3   = listItemView?.findViewById<TextView>(R.id.attr3)
        val val3    = listItemView?.findViewById<TextView>(R.id.val3)
        val attr4   = listItemView?.findViewById<TextView>(R.id.attr4)
        val val4    = listItemView?.findViewById<TextView>(R.id.val4)

        attr1?.text = "Type"
        val1?.text  = incidentDatum[position].incident_type

        when (incidentDatum[position].incident_type) {
            "Platform" -> {
                attr2?.text = "Platform"
                val2?.text  = incidentDatum[position].platform_number

                attr3?.text = "Railway station"
                val3?.text  = incidentDatum[position].railway_station_label

                attr4?.visibility = View.GONE
                val4?.visibility  = View.GONE
            }
            "Train" -> {
                attr2?.text = "Train"
                val2?.text  = incidentDatum[position].train_name

                attr3?.text = "Coach"
                val3?.text  = incidentDatum[position].coach

                attr4?.text = "Contact"
                val4?.text  = incidentDatum[position].mobile_number
            }
            "Track" -> {
                attr2?.text = "Location"
                val2?.text  = incidentDatum[position].track_location

                attr3?.text = "Details"
                val3?.text  = incidentDatum[position].incident_details

                attr4?.visibility = View.GONE
                val4?.visibility  = View.GONE
            }
        }
        return listItemView!!
    }
}