package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast
import org.altbeacon.beacon.*
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.SMNotificationManager

class BeaconScanner(private val smAppController: SMAppController): Service(), BeaconConsumer {

    interface SMAppController{
        val smNotificationManager: SMNotificationManager
        val context: Context
        fun notifyDatasetChanged()
    }

    private val beaconManager:BeaconManager = BeaconManager.getInstanceForApplication(smAppController.context)
    private val region = Region("MyRegionId", null, null, null)
    val beaconList: MutableList<MyBeacon> = mutableListOf()
    val savedBeacons: MutableList<MyBeacon> = mutableListOf()

    init {
        attachBaseContext(smAppController.context)
        this.beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        this.beaconManager.enableForegroundServiceScanning(smAppController.smNotificationManager.getNotification(), smAppController.smNotificationManager.NOTIFICATION_ID)
        this.beaconManager.setEnableScheduledScanJobs(false)
        this.beaconManager.bind(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onBeaconServiceConnect() {
        this.beaconManager.backgroundBetweenScanPeriod = 0
        this.beaconManager.backgroundScanPeriod = 1000
        this.beaconManager.foregroundBetweenScanPeriod = 0
        this.beaconManager.foregroundScanPeriod = 1000
        this.beaconManager.addRangeNotifier { beacons, region ->
                val beacns: MutableList<Beacon> = mutableListOf()
                beacns.addAll(beacons)

                val toRem = mutableListOf<MyBeacon>()
                for (b in beaconList) {
                    if (!beacns.contains(b as Beacon)) {
                        if (b.isExpired()) {
                            toRem.add(b)
                        }
                    }
                }
                for (b in toRem)
                    beaconList.remove(b)

                val iterator = beacns.iterator()
                while (iterator.hasNext()) {
                    val b = MyBeacon(iterator.next())

                    //Nieznane beacony w biurze, cięgle są, ciągle irrytują!!!
                    if (b.id2.toString() == "100" && b.id3.toString() == "100")
                        continue

                    if (!beaconList.contains(b)) {
                        beaconList.add(b)
                    }

                    if (savedBeacons.contains(b)) {
                        beaconList[beaconList.indexOf(b)].saveBeacon(
                            savedBeacons[savedBeacons.indexOf(b)].name,
                            savedBeacons[savedBeacons.indexOf(b)].calibratedRssi
                        )
                    }

                    beaconList[beaconList.indexOf(b)].updateBeacon(b)

                    /*val msg = JSONObject()
                                try {
                                    msg.put("tst", Timestamp(System.currentTimeMillis()).getTime())
                                    msg.put("mac", b.getBluetoothAddress())
                                    msg.put("uuid", b.getId1())
                                    msg.put("major", b.getId2())
                                    msg.put("minor", b.getId3())
                                    msg.put("rssi", beaconList[beaconList.indexOf(b)].rssi)
                                    msg.put("distance", b.getDistance())
                                    MGAApp.getMqttContext().getMqttService().publish("beacon", msg.toString())
                                    if (!beaconList[beaconList.indexOf(b)].name.isEmpty()) {
                                        msg.put("rangerssi", beaconList[beaconList.indexOf(b)].calibratedRssi)
                                        msg.put("rangestatus", if (beaconList[beaconList.indexOf(b)].isClose) "in" else "out")
                                        MGAApp.getMqttContext().getMqttService().publish("proximity", msg.toString())
                                    }
                                } catch (e: JSONException) {

                                }*/

                }
            smAppController.notifyDatasetChanged()
        }
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!btAdapter.isEnabled) {
            btAdapter.enable()
        }
        try {
            this.beaconManager.startRangingBeaconsInRegion(region)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun stop(){
        try {
            this.beaconManager.stopRangingBeaconsInRegion(region)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}