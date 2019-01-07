package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.recognition.packets.ConfigurableDevice
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.connection.api.DeviceConnection
import com.estimote.mgmtsdk.connection.api.DeviceConnectionCallback
import com.estimote.mgmtsdk.feature.settings.SettingCallback

class BeaconsSetManager (val deviceConnector: DeviceConnector){
    internal val configurableDevices: MutableList<BeaconObject> = mutableListOf()
    private val connectingDevices: MutableList<ConfigurableDevice> = mutableListOf()
    internal val visibleDevices: MutableList<Beacon> = mutableListOf()

    fun visibleBeaconsSetUpdate(newList: MutableList<Beacon>, context: Context){
        for(i in newList){
            if(!visibleDevices.contains(i)) {
                visibleDevices.add(i)
                Toast.makeText(context, "New Beacon!", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "##############Beacon appear")
            }
        }
        deviceConnector.notifyChange()
        val tmp = visibleDevices.toMutableList()
        for(i in tmp){
            if(!newList.contains(i)){
                visibleDevices.removeAt(visibleDevices.indexOf(i))
                Toast.makeText(context, "Beacon gone", Toast.LENGTH_LONG).show()
                Log.d("TAG", "##############Beacon gone")
            }
        }
        deviceConnector.notifyChange()
    }

    fun beaconsSetUpdate(newList: MutableList<ConfigurableDevice>){
        for(i in newList){
            if(!contains(i) && !connectingDevices.contains(i)) {
                connectingDevices.add(i)
                val devConnection = deviceConnector.connect(i)
                devConnection?.connect(object: DeviceConnectionCallback{
                    override fun onConnected() {
                        devConnection?.settings.deviceInfo.name().get(object: SettingCallback<String>{
                            override fun onSuccess(value: String?) {
                                val beac = BeaconObject(i, devConnection)
                                beac.name = value!!
                                configurableDevices.add(beac)
                                connectingDevices.removeAt(connectingDevices.indexOf(i))
                                deviceConnector.notifyChange()
                            }

                            override fun onFailure(exception: DeviceConnectionException?) {
                                Log.d("TAGTAG", "================= Device connection Failed")
                            }

                        })
                        Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Connected")
                    }

                    override fun onConnectionFailed(exception: DeviceConnectionException?) {
                        Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Connection Failed")
                    }

                    override fun onDisconnected() {
                        Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Disconnected")
                    }

                })
            }
        }
        /*val listToRemove = mutableListOf<Int>()
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

    private fun containsVisible(el: Beacon): Boolean{
        for(i in visibleDevices){
            if(i.macAddress==el)
                return true
        }
        return false
    }

    private fun contains(el: ConfigurableDevice): Boolean{
        for(i in configurableDevices){
            if(i.device==el)
                return true
        }
        return false
    }
}