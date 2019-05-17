package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon

class SavedBeaconItemViewHolder {
    private val saved_beacon_item_name: TextView
    private val saved_beacon_item_calib_rssi: TextView

    constructor(itemView: View) {
        this.saved_beacon_item_name = itemView.findViewById(R.id.saved_beacon_item_name)
        this.saved_beacon_item_calib_rssi = itemView.findViewById(R.id.saved_beacon_item_calib_rssi)
    }

    fun setData(beacon: MyBeacon) {
        saved_beacon_item_name.text = beacon.name
        saved_beacon_item_calib_rssi.text = String.format("%.2f", beacon.calibratedRssi)
    }
}