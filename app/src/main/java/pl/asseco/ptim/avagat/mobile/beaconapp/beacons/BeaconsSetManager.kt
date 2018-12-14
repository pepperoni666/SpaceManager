package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.util.Log
import com.estimote.coresdk.recognition.packets.ConfigurableDevice
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.connection.api.DeviceConnection
import com.estimote.mgmtsdk.connection.api.DeviceConnectionCallback

class BeaconsSetManager (val deviceConnector: DeviceConnector){
    internal val configurableDevices: MutableList<BeaconObject> = mutableListOf()

    fun beaconsSetUpdate(newList: MutableList<ConfigurableDevice>){
        val x = BeaconObject(newList[0], deviceConnector.connect(newList[0]))
        /*for(i in newList){
            if(!contains(i))
                configurableDevices.add(BeaconObject(i, deviceConnector.connect(i)))
        }
        val listToRemove = mutableListOf<Int>()
        for(i in 1..configurableDevices.size){
            if(!newList.contains(configurableDevices[i-1].device)) {
                listToRemove.add(i-1)
            }
        }*/
        /*for(i in listToRemove){
            configurableDevices[i].destroy()
            configurableDevices.removeAt(i)
        }*/
    }

    private fun contains(el: ConfigurableDevice): Boolean{
        for(i in configurableDevices){
            if(i.device==el)
                return true
        }
        return false
    }
}