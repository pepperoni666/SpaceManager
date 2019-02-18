package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.feature.settings.SettingCallback
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.BeaconsSetManager

class BeaconListAdapter(private val context:Context, private val beaconsSetManager: BeaconsSetManager): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.beacon_item, parent, false)
        rowView.findViewById<TextView>(R.id.textView).text =
                beaconsSetManager.visibleDevices[position].macAddress.toString() + "\n" +
                beaconsSetManager.visibleDevices[position].proximityUUID + "\n" +
                beaconsSetManager.visibleDevices[position].major + "\n" +
                beaconsSetManager.visibleDevices[position].minor + "\n" +
                beaconsSetManager.visibleDevices[position].rssi + "\n" +
                beaconsSetManager.visibleDevices[position].measuredPower
        //val confDev = beaconsSetManager.configurableDevices
        /*if(confDev[confDev.keys.toList()[position]]?.isConnected == true){
            confDev[confDev.keys.toList()[position]]?.settings?.deviceInfo?.color()?.get(object: SettingCallback<String>{
                override fun onSuccess(value: String?) {
                    rowView.findViewById<TextView>(R.id.textView).text = value
                }

                override fun onFailure(exception: DeviceConnectionException?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }*/

        return rowView
    }

    override fun getItem(position: Int): Any {
        return beaconsSetManager.visibleDevices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return beaconsSetManager.visibleDevices.size
    }

}
