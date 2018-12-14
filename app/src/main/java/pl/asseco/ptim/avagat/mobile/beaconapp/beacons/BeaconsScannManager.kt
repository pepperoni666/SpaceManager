package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.util.Log
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.recognition.packets.ConfigurableDevice
import com.estimote.coresdk.service.BeaconManager
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.connection.api.DeviceConnection
import com.estimote.mgmtsdk.connection.api.DeviceConnectionCallback
import com.estimote.mgmtsdk.connection.api.DeviceConnectionProvider
import java.util.*
import pl.asseco.ptim.avagat.mobile.beaconapp.ui.MainActivity

interface DeviceConnector{
    fun connect(device: ConfigurableDevice): DeviceConnection?
}

class BeaconsScannManager(val activity: MainActivity): DeviceConnector{

    private val beaconManager: BeaconManager = BeaconManager(activity.applicationContext)

    //private var devaiceConnection: DeviceConnection? = null
    public var isProviderConnected: Boolean = false
    internal val beaconsSetManager: BeaconsSetManager =
        BeaconsSetManager(this)

    fun connect(){
        beaconManager.setRangingListener(object: BeaconManager.BeaconRangingListener{
            override fun onBeaconsDiscovered(beaconRegion: BeaconRegion?, beacons: MutableList<Beacon>?) {

            }
        })
        beaconManager.setMonitoringListener(object: BeaconManager.BeaconMonitoringListener{
            override fun onEnteredRegion(beaconRegion: BeaconRegion?, beacons: MutableList<Beacon>?) {
                beaconManager.startRanging(beaconRegion)
            }

            override fun onExitedRegion(beaconRegion: BeaconRegion?) {
                beaconManager.stopRanging(beaconRegion)
            }
        })
        beaconManager.connect(object: BeaconManager.ServiceReadyCallback{
            override fun onServiceReady() {
                beaconManager.startMonitoring(BeaconRegion("Beacons with default Estimote UUID",
                    UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), null, null))
            }
        })
    }

    fun destroy(){

    }

    override fun connect(device: ConfigurableDevice): DeviceConnection?{
        if(isProviderConnected) {
            return activity.connectionProvider!!.getConnection(device)
        }else{
            return null
        }
    }

    fun discover(){
        beaconManager.setForegroundScanPeriod(4000, 4000)
        beaconManager.setConfigurableDevicesListener(object: BeaconManager.ConfigurableDevicesListener{
            override fun onConfigurableDevicesFound(configurableDevices: MutableList<ConfigurableDevice>?) {
                this@BeaconsScannManager.beaconsSetManager.beaconsSetUpdate(configurableDevices!!)
                activity.notifyDataSetChanged()
                if(!isProviderConnected) {
                    activity.connectionProvider!!.connectToService(object :
                        DeviceConnectionProvider.ConnectionProviderCallback {
                        override fun onConnectedToService() {
                            isProviderConnected = true
                        }
                    })
                }
            }
        })
        beaconManager.connect(object: BeaconManager.ServiceReadyCallback{
            override fun onServiceReady() {
                beaconManager.startConfigurableDevicesDiscovery()
            }

        })
    }
}