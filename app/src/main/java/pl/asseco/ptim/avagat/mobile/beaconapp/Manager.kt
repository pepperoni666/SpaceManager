package pl.asseco.ptim.avagat.mobile.beaconapp

import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.recognition.packets.ConfigurableDevice
import com.estimote.coresdk.service.BeaconManager
import com.estimote.mgmtsdk.connection.api.DeviceConnection
import com.estimote.mgmtsdk.connection.api.DeviceConnectionProvider
import java.util.*
import com.estimote.mgmtsdk.common.exceptions.DeviceConnectionException
import com.estimote.mgmtsdk.connection.api.DeviceConnectionCallback



class Manager(val activity: MainActivity){
    private val beaconManager: BeaconManager = BeaconManager(activity.applicationContext)

    private var devaiceConnection: DeviceConnection? = null

    fun connect(){
        beaconManager.setRangingListener(object: BeaconManager.BeaconRangingListener{
            override fun onBeaconsDiscovered(beaconRegion: BeaconRegion?, beacons: MutableList<Beacon>?) {
                var s = ""
                if (beacons != null) {
                    for(i in beacons){
                        s += "Measure Power: " + i.measuredPower + "\n" + "RSSI: " + i.rssi + "\n\n"
                    }
                }
                activity.setText(s)
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
                activity.setText("Starting ranging...")
            }
        })
    }

    fun destroy(){

    }

    fun discover(){
        beaconManager.setForegroundScanPeriod(2000, 2000)
        beaconManager.connect(object: BeaconManager.ServiceReadyCallback{
            override fun onServiceReady() {
                beaconManager.setConfigurableDevicesListener(object: BeaconManager.ConfigurableDevicesListener{
                    override fun onConfigurableDevicesFound(configurableDevices: MutableList<ConfigurableDevice>?) {
                        var s = ""
                        if (configurableDevices != null) {
                            for(i in configurableDevices){
                                s += i.appVersion + "\n"
                            }
                        }
                        activity.setText(s)
                        /*activity.connectionProvider!!.connectToService(object: DeviceConnectionProvider.ConnectionProviderCallback{
                            override fun onConnectedToService() {
                                devaiceConnection = activity.connectionProvider!!.getConnection(device)
                                devaiceConnection!!.connect(object : DeviceConnectionCallback {
                                    override fun onConnected() {
                                        // Do something with your connection.
                                        // You can for example read device settings, or make an firmware update.
                                        devaiceConnection!!.
                                    }

                                    override fun onDisconnected() {
                                        // Every time your device gets disconnected, you can handle that here.
                                        // For example: in this state you can try reconnecting to your device.
                                    }

                                    override fun onConnectionFailed(exception: DeviceConnectionException) {
                                        // Handle every connection error here.
                                    }
                                })
                            }
                        })*/
                    }
                })
                beaconManager.startConfigurableDevicesDiscovery()
            }

        })
    }
}