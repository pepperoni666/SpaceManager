package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_list

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp

class BeaconListAdapter(private val context:Context): BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view_ for row item
        var rowView = convertView
        // reuse views
        if (rowView == null) {
            val inflater = LayoutInflater.from(parent?.context)
            rowView = inflater.inflate(R.layout.beacon_item, null)
            // configure view_ holder
            rowView!!.tag = BeaconItemViewHolder(rowView)
        }

        // fill data
        val holder = rowView.tag as BeaconItemViewHolder
        holder.setData(((context as Activity).application as SMApp).beaconScanner.beaconList[position])

        return rowView
    }

    override fun getItem(position: Int): Any {
        return ((context as Activity).application as SMApp).beaconScanner.beaconList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return ((context as Activity).application as SMApp).beaconScanner.beaconList.count()
    }

}
