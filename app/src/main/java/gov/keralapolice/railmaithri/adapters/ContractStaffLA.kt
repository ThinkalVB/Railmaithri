package gov.keralapolice.railmaithri.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import gov.keralapolice.railmaithri.R
import gov.keralapolice.railmaithri.models.ContractStaffMD
import gov.keralapolice.railmaithri.models.StrangerCheckMD
import android.view.LayoutInflater as LayoutInflater1


class ContractStaffLA(
    private val context1:     Context,
    private val ContractData: List<ContractStaffMD>
) : BaseAdapter() {
    private val context = context1
    private val contractDatum = ContractData
    override fun getCount(): Int {
        return contractDatum.size
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

        attr1?.text = "Name"
        val1?.text = contractDatum[position].name

        attr2?.text = "Age"
        val2?.text = contractDatum[position].age.toString()

        attr3?.text = "Category"
        val3?.text = contractDatum[position].staff_porter_category_label

        attr4?.text = "Job Details"
        val4?.text = contractDatum[position]?.job_details
        return listItemView!!
    }
}