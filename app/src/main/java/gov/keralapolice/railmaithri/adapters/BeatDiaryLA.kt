package gov.keralapolice.railmaithri.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.BeatDiaryMD
import android.view.LayoutInflater as LayoutInflater1


class BeatDiaryLA(
    private val context1:      Context,
    private val beatDiaryData: List<BeatDiaryMD>
) : BaseAdapter() {
    private val context = context1
    private val beatDiaryDatum = beatDiaryData
    override fun getCount(): Int {
        return beatDiaryDatum.size
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

        val attr1 = listItemView?.findViewById<TextView>(R.id.attr1)
        val val1 = listItemView?.findViewById<TextView>(R.id.val1)
        val attr2 = listItemView?.findViewById<TextView>(R.id.attr2)
        val val2 = listItemView?.findViewById<TextView>(R.id.val2)
        val attr3 = listItemView?.findViewById<TextView>(R.id.attr3)
        val val3 = listItemView?.findViewById<TextView>(R.id.val3)
        val attr4 = listItemView?.findViewById<TextView>(R.id.attr4)
        val val4 = listItemView?.findViewById<TextView>(R.id.val4)

        attr1?.text = "Duty"
        val1?.text = beatDiaryDatum[position].beat_label

       // val1?.text = beatDiaryData[position].utc_timestamp.take(16).replace("T", "\t")

        attr2?.text = "officer"
        val2?.text = beatDiaryDatum[position].beat_officer_label

        attr3?.text = "Assigned on"
        val3?.text = beatDiaryDatum[position].assigned_on.take(16).replace("T", "\t")

        attr4?.text = "Note"
        val4?.text = beatDiaryDatum[position].assignment_note
        return listItemView!!
    }
}