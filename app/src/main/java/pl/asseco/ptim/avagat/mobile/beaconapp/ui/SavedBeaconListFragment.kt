package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog.BeaconItemDialog
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_dialog.SavedBeaconItemDialog
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.beacon_list.SavedBeaconListAdapter

class SavedBeaconListFragment: Fragment(), SMApp.CurentFragment, BeaconItemDialog.BeaconDialogCallback {
    override fun deleteSelected() {
        if((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon!!)) {
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
        }
        else {
            (activity!!.application as SMApp).saveBeacon(selectedBeacon!!)
        }
        selectedBeacon = null
        dialog?.dismiss()
        dialog = null
        adapter.notifyDataSetChanged()
    }

    override fun startCalibrating() {
    }

    override fun cancelCalibration() {
    }

    override fun cancel() {
    }

    override fun onDialogBuild() {
        if((activity!!.application as SMApp).beaconScanner.savedBeacons.contains(selectedBeacon!!)){
            dialog?.calibrationFinished(selectedBeacon!!.calibratedRssi)
        }
        dialog?.setBeaconData(selectedBeacon!!)
    }

    private lateinit var listView: ListView
    private lateinit var adapter: SavedBeaconListAdapter

    private var dialog: SavedBeaconItemDialog? = null

    private var selectedBeacon: MyBeacon? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmentl_beacon_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = SavedBeaconListAdapter(activity!!)
        listView = activity!!.findViewById(R.id.beacon_list)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, SavedBeaconSettingsActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
        }
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            selectedBeacon = (activity!!.application as SMApp).beaconScanner.savedBeacons[position]
            dialog = SavedBeaconItemDialog()
            dialog!!.callback = this@SavedBeaconListFragment
            dialog!!.show(activity!!.supportFragmentManager, "beacon")
            true
        }
        listView.adapter = adapter
        (activity!!.application as SMApp).curentFragment = this
    }

    override fun notifyDatasetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        (activity!!.application as SMApp).curentFragment = null
        super.onDestroy()
    }
}