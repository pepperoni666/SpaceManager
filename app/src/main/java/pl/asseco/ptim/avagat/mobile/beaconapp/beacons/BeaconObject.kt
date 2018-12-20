package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.util.Log
import com.estimote.coresdk.recognition.packets.ConfigurableDevice
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.connection.api.DeviceConnection
import com.estimote.mgmtsdk.connection.api.DeviceConnectionCallback

class BeaconObject(val device: ConfigurableDevice, val deviceConnection: DeviceConnection?){

    lateinit var name: String

    fun destroy() {
        deviceConnection?.close()
        deviceConnection?.destroy()
    }

    /*init{
        deviceConnection?.connect(object: DeviceConnectionCallback{
            override fun onConnected() {
                Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Connected")
            }

            override fun onConnectionFailed(exception: DeviceConnectionException?) {
                Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Connection Failed")
            }

            override fun onDisconnected() {
                Log.d("TAGTAG", "++++++++++++++++++++++++++++++++++++++++++++++ Disconnected")
            }

        })
    }*/
}