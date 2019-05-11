package pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import pl.asseco.ptim.avagat.mobile.beaconapp.R

class SavedBeaconItemDialog: BeaconItemDialog() {
    interface SavedDialogCallback {
        fun saveSaved(name: String, rssi: Double?)
    }

    private val savedCallback: SavedDialogCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        view_ = inflater.inflate(R.layout.item_beacon_dialog, null)

        val holder = BeaconItemDialogHolder(view_)

        view_.tag = holder

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view_)
            // Add action buttons
            .setNeutralButton("delete") { dialog, id -> callback.deleteSelected() }

            .setPositiveButton("save") { dialog, id ->
                val holder = view_.tag as BeaconItemDialogHolder
                if (holder.beacon_item_dialog_name.text.toString().isNotEmpty() && holder.beacon_item_dialog_calibrated_rssi_editable.text.toString().isNotEmpty()) {
                    try {
                        val x =
                            java.lang.Double.parseDouble(holder.beacon_item_dialog_calibrated_rssi_editable.text.toString())
                        if (x >= 0)
                            throw NumberFormatException()
                        savedCallback!!.saveSaved(holder.beacon_item_dialog_name.text.toString(), x)
                    } catch (e: NumberFormatException) {
                        savedCallback!!.saveSaved(holder.beacon_item_dialog_name.text.toString(), null)
                    }

                }
            }
            .setNegativeButton("cancel") { dialog, id -> this@SavedBeaconItemDialog.dialog.cancel() }

        holder.beacon_item_dialog_calibrate_btn.visibility = View.GONE
        holder.setupCalibratableRssi()
        callback.onDialogBuild()
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        callback.cancel()
    }
}