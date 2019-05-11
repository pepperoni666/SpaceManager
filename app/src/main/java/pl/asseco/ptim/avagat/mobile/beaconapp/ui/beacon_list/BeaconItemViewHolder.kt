package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon

class BeaconItemViewHolder {
    private val beacon_item_uuid: TextView
    private val beacon_item_major_or_name: TextView
    private val beacon_item_minor: TextView
    private val beacon_item_rssi: TextView
    private val beacon_item_status: ImageView

    constructor(itemView: View) {
        this.beacon_item_uuid = itemView.findViewById(R.id.beacon_item_uuid)
        this.beacon_item_major_or_name = itemView.findViewById(R.id.beacon_item_major_or_name)
        this.beacon_item_minor = itemView.findViewById(R.id.beacon_item_minor)
        this.beacon_item_rssi = itemView.findViewById(R.id.beacon_item_rssi)
        this.beacon_item_status = itemView.findViewById(R.id.beacon_item_status)
    }

    fun setData(beacon: MyBeacon) {
        if (beacon.name.isEmpty()) {
            beacon_item_uuid.visibility = View.VISIBLE
            beacon_item_uuid.text = beacon.id1.toString()
            beacon_item_major_or_name.textSize = 15f
            beacon_item_major_or_name.setPadding(0, 0, 0, 0)
            beacon_item_major_or_name.text = beacon.id2.toString()
            beacon_item_minor.visibility = View.VISIBLE
            beacon_item_minor.text = beacon.id3.toString()
            beacon_item_status.visibility = View.INVISIBLE
        } else {
            beacon_item_uuid.visibility = View.GONE
            beacon_item_uuid.text = ""
            beacon_item_major_or_name.textSize = 22f
            beacon_item_major_or_name.setPadding(30, 0, 0, 0)
            beacon_item_major_or_name.text = beacon.name
            beacon_item_minor.visibility = View.GONE
            beacon_item_minor.text = ""
            if (beacon.isClose)
                beacon_item_status.visibility = View.VISIBLE
            else
                beacon_item_status.visibility = View.INVISIBLE
        }
        if (beacon.rssi == null) {
            beacon_item_rssi.visibility = View.GONE
        } else {
            beacon_item_rssi.visibility = View.VISIBLE
            beacon_item_rssi.text = String.format("%.2f", beacon.rssi)
        }
    }
}