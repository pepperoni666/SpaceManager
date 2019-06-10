package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog.BeaconItemDialog
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog.SavedBeaconItemDialog
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_list.BeaconListAdapter

class BeaconListFragment : Fragment(), SMApp.CurentFragment, BeaconItemDialog.BeaconDialogCallback {
    override fun deleteSelected() {
        if ((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon!!)) {
            (activity!!.application as SMApp).deleteBeacon(selectedBeacon!!)
        }
        adapter.notifyDataSetChanged()
        dialog?.dismiss()
        selectedBeacon = null
        dialog = null
    }

    override fun saveBeacon(name: String, rssi: Double) {
        selectedBeacon?.saveBeacon(name, rssi)
        if ((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon)) {
            (activity!!.application as SMApp).updateBeacon(selectedBeacon!!)
        } else {
            (activity!!.application as SMApp).saveBeacon(selectedBeacon!!)
        }
        selectedBeacon = null
        dialog?.dismiss()
        dialog = null
        adapter.notifyDataSetChanged()
    }

    override fun startCalibrating() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val calibrationTime: Long? = preferences.getString("beacon_calibration_time", "30").toLongOrNull()
        if (calibrationTime == null) {
            Toast.makeText(context, "Invalid calibration time! Using 30s", Toast.LENGTH_LONG).show()
        }

        calibrating = true
        timer = object : CountDownTimer((calibrationTime ?: 30) * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                dialog?.timerTick(millisUntilFinished)
            }

            override fun onFinish() {
                dialog?.calibrationFinished(sumRssiCalib / rssiCalibCount.toDouble())
                calibrating = false
                rssiCalibCount = 0
                sumRssiCalib = 0.0
            }
        }.start()
    }

    override fun cancelCalibration() {
        calibrating = false
        rssiCalibCount = 0
        sumRssiCalib = 0.0
        timer?.cancel()
    }

    override fun cancel() {
        dialog = null
        selectedBeacon = null
        if (timer != null)
            cancelCalibration()
    }

    override fun onDialogBuild() {
        if ((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon!!)) {
            dialog?.calibrationFinished(selectedBeacon!!.calibratedRssi)
        }
        dialog?.setBeaconData(selectedBeacon!!)
    }

    private lateinit var listView: ListView

    private lateinit var adapter: BeaconListAdapter

    private var calibrating: Boolean = false
    private var sumRssiCalib = 0.0
    private var rssiCalibCount = 0
    private var timer: CountDownTimer? = null

    private var dialog: BeaconItemDialog? = null

    private var selectedBeacon: MyBeacon? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmentl_beacon_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = BeaconListAdapter(activity!!)
        listView = activity!!.findViewById(R.id.beacon_list)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedBeacon = (activity!!.application as SMApp).beaconScanner.beaconList[position]
            dialog = BeaconItemDialog()
            if ((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon!!)) {
                dialog = SavedBeaconItemDialog()
            }
            dialog!!.callback = this@BeaconListFragment
            dialog!!.show(activity!!.supportFragmentManager, "beacon")
        }
        listView.adapter = adapter
        (activity!!.application as SMApp).curentFragment = this
    }

    override fun notifyDatasetChanged() {
        if (dialog != null && selectedBeacon != null) {
            if ((activity!!.application as SMApp).beaconScanner.beaconList.contains(selectedBeacon!!)) {
                dialog?.setBeaconData(
                    (activity!!.application as SMApp).beaconScanner.beaconList[(activity!!.application as SMApp).beaconScanner.beaconList.indexOf(
                        selectedBeacon!!
                    )]
                )
                if (calibrating) {
                    rssiCalibCount++
                    sumRssiCalib += (activity!!.application as SMApp).beaconScanner.beaconList[(activity!!.application as SMApp).beaconScanner.beaconList.indexOf(
                        selectedBeacon!!
                    )].rssi!!
                }
            } else {
                if (calibrating) {
                    calibrating = false
                    rssiCalibCount = 0
                    sumRssiCalib = 0.0
                    timer?.cancel()
                }
                dialog?.dialog?.cancel()
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        (activity!!.application as SMApp).curentFragment = null
        super.onDestroy()
    }

}