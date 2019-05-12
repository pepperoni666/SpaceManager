package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon

class BeaconItemDialogHolder {

    val beacon_item_dialog_name: EditText
    private val beacon_item_dialog_uuid: TextView
    private val beacon_item_dialog_mac: TextView
    private val beacon_item_dialog_major: TextView
    private val beacon_item_dialog_minor: TextView
    private val beacon_item_dialog_rssi: TextView
    private val calibrating: LinearLayout
    private val beacon_item_dialog_calibrating_timer: TextView
    val beacon_item_dialog_calibrate_btn: Button
    val beacon_item_dialog_cancel_btn: Button
    private val calibrated: LinearLayout
    private val rssi: LinearLayout
    private val beacon_item_dialog_calibrated_rssi: TextView
    val beacon_item_dialog_calibrated_rssi_editable: EditText

    constructor(itemView: View) {
        this.beacon_item_dialog_name = itemView.findViewById(R.id.beacon_item_dialog_name)
        this.beacon_item_dialog_uuid = itemView.findViewById(R.id.beacon_item_dialog_uuid)
        this.beacon_item_dialog_mac = itemView.findViewById(R.id.beacon_item_dialog_mac)
        this.beacon_item_dialog_major = itemView.findViewById(R.id.beacon_item_dialog_major)
        this.beacon_item_dialog_minor = itemView.findViewById(R.id.beacon_item_dialog_minor)
        this.beacon_item_dialog_rssi = itemView.findViewById(R.id.beacon_item_dialog_rssi)
        this.calibrating = itemView.findViewById(R.id.beacon_item_dialog_calibrating)
        this.beacon_item_dialog_calibrating_timer = itemView.findViewById(R.id.beacon_item_dialog_calibrating_timer)
        this.beacon_item_dialog_calibrate_btn = itemView.findViewById(R.id.beacon_item_dialog_calibrate_btn)
        this.beacon_item_dialog_cancel_btn = itemView.findViewById(R.id.beacon_item_dialog_cancel_btn)
        this.beacon_item_dialog_calibrated_rssi = itemView.findViewById(R.id.beacon_item_dialog_calibrated_rssi)
        this.calibrated = itemView.findViewById(R.id.beacon_item_dialog_calibrated)
        this.beacon_item_dialog_calibrated_rssi_editable =
            itemView.findViewById(R.id.beacon_item_dialog_calibrated_rssi_editable)
        this.rssi = itemView.findViewById(R.id.beacon_item_dialog_rssi_)
    }

    fun setData(beacon: MyBeacon) {
        if (beacon.name !== "" && beacon_item_dialog_name.text.toString().isEmpty())
            beacon_item_dialog_name.setText(beacon.name)
        beacon_item_dialog_uuid.text = beacon.id1.toString()
        beacon_item_dialog_mac.text = beacon.bluetoothAddress
        beacon_item_dialog_major.text = beacon.id2.toString()
        beacon_item_dialog_minor.text = beacon.id3.toString()
        if (beacon.rssi == null) {
            rssi.visibility = View.GONE
        } else {
            rssi.visibility = View.VISIBLE
            beacon_item_dialog_rssi.text = String.format("%.2f", beacon.rssi)
        }
        if (beacon.calibratedRssi !== .0) {
            calibrated.visibility = View.VISIBLE
            if (beacon_item_dialog_calibrated_rssi_editable.visibility == View.VISIBLE && beacon_item_dialog_calibrated_rssi_editable.text.toString().isEmpty()) {
                beacon_item_dialog_calibrated_rssi_editable.setText(String.format("%.2f", beacon.calibratedRssi))
            } else
                beacon_item_dialog_calibrated_rssi.text = String.format("%.2f", beacon.calibratedRssi)
        } else
            calibrated.visibility = View.GONE
    }

    fun setupCalibratableRssi() {
        beacon_item_dialog_calibrated_rssi.visibility = View.GONE
        beacon_item_dialog_calibrated_rssi_editable.visibility = View.VISIBLE
    }

    fun startCalibrating() {
        calibrating.visibility = View.VISIBLE
        beacon_item_dialog_calibrate_btn.visibility = View.GONE
        beacon_item_dialog_cancel_btn.visibility = View.VISIBLE
    }

    fun stopCalibrating() {
        if(beacon_item_dialog_cancel_btn.visibility == View.VISIBLE){
            calibrating.visibility = View.GONE
            beacon_item_dialog_calibrate_btn.visibility = View.VISIBLE
            beacon_item_dialog_cancel_btn.visibility = View.GONE
        }
    }

    fun timerTick(t: Int) {
        beacon_item_dialog_calibrating_timer.text = t.toString() + ""
    }
}