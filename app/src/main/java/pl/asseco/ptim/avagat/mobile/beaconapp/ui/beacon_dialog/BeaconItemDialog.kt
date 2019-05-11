package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.Toast
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon

open class BeaconItemDialog: DialogFragment() {
    interface BeaconDialogCallback {
        fun saveBeacon(name: String, rssi: Double)
        fun startCalibrating()
        fun cancelCalibration()
        fun cancel()
        fun onDialogBuild()
        fun deleteSelected()
    }

    lateinit var callback: BeaconDialogCallback

    protected lateinit var view_: View

    private var calibratedRssi: Double? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        view_ = inflater.inflate(R.layout.item_beacon_dialog, null)

        val holder = BeaconItemDialogHolder(view_)

        holder.beacon_item_dialog_calibrate_btn.setOnClickListener(View.OnClickListener {
            callback.startCalibrating()
            holder.startCalibrating()
            //BeaconDialogHolder holder = (BeaconDialogHolder) view_.getTag();
        })
        holder.beacon_item_dialog_cancel_btn.setOnClickListener(View.OnClickListener {
            callback.cancelCalibration()
            holder.stopCalibrating()
            //BeaconDialogHolder holder = (BeaconDialogHolder) view_.getTag();
        })

        view_.tag = holder

        // Inflate and set the layout for the dialog
        // Pass null as the parent view_ because its going in the dialog layout
        builder.setView(view_)
            .setNeutralButton("save") { dialog, which ->
                val holder = view_.tag as BeaconItemDialogHolder
                if (holder.beacon_item_dialog_name.text.toString().isNotEmpty() && calibratedRssi != null) {
                    callback.saveBeacon(holder.beacon_item_dialog_name.text.toString(), calibratedRssi!!)
                } else {
                    Toast.makeText(
                        context,
                        "Beacon hasn't been saved!~\n No name was given or not calibrated.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(
                "cancel"
            ) { dialog, id -> this@BeaconItemDialog.dialog.cancel() }
        callback.onDialogBuild()
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        callback.cancel()
    }

    fun calibrationFinished(rssi: Double) {
        calibratedRssi = rssi
        val holder = view_.tag as BeaconItemDialogHolder
        holder.stopCalibrating()
    }

    fun setBeaconData(beacon: MyBeacon) {
        val holder = view_.tag as BeaconItemDialogHolder
        if (calibratedRssi == null && beacon.calibratedRssi != 0.0) {
            calibratedRssi = beacon.calibratedRssi
        } else if (calibratedRssi != null && beacon.calibratedRssi == 0.0 || calibratedRssi != null && calibratedRssi != beacon.calibratedRssi)
            beacon.saveBeacon("", calibratedRssi!!)
        holder.setData(beacon)
    }

    fun timerTick(millisUntilFinished: Long) {
        val holder = view_.tag as BeaconItemDialogHolder
        holder.timerTick(millisUntilFinished.toInt() / 1000)
    }
}