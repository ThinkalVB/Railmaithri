package gov.keralapolice.railmaithri.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.RunOverMD
import android.view.LayoutInflater as LayoutInflater1

class RunOverLA(
    private val context1:    Context,
    private val runOverData: List<RunOverMD>
) : BaseAdapter() {
    private val context       = context1
    private val runOverDatum  = runOverData
    override fun getCount(): Int {
        return runOverDatum.size
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
        val val1  = listItemView?.findViewById<TextView>(R.id.val1)
        val attr2 = listItemView?.findViewById<TextView>(R.id.attr2)
        val val2  = listItemView?.findViewById<TextView>(R.id.val2)
        val attr3 = listItemView?.findViewById<TextView>(R.id.attr3)
        val val3  = listItemView?.findViewById<TextView>(R.id.val3)
        val attr4 = listItemView?.findViewById<TextView>(R.id.attr4)
        val val4  = listItemView?.findViewById<TextView>(R.id.val4)

        attr1?.text = "Category"
        val1?.text = runOverDatum[position].category

        attr2?.text = "Cause"
        val2?.text = runOverDatum[position].cause_label

        attr3?.text = "Registered in"
        val3?.text = runOverDatum[position].case_registered_in

        attr4?.text = "Is identified"
        val4?.text = runOverDatum[position].is_identified.toString()
        return listItemView!!
    }
}