package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast
import org.altbeacon.beacon.*
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.Logger
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.SMNotificationManager
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager


class BeaconScanner(private val smAppController: SMAppController) : Service(), BeaconConsumer {

    interface SMAppController {
        val smNotificationManager: SMNotificationManager
        val context: Context
        fun getSavedBeacons(): MutableList<MyBeacon>
        fun notifyDatasetChanged()
        fun beaconStateChange(beacon: MyBeacon)
    }

    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(smAppController.context)
    private val region = Region("MyRegionId", null, null, null)
    val beaconList: MutableList<MyBeacon> = mutableListOf()
    val savedBeacons: MutableList<MyBeacon>
    var running: Boolean = false

    init {
        attachBaseContext(smAppController.context)
        savedBeacons = smAppController.getSavedBeacons()
        this.beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        this.beaconManager.enableForegroundServiceScanning(
            smAppController.smNotificationManager.getNotification(),
            smAppController.smNotificationManager.NOTIFICATION_ID
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onBeaconServiceConnect() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(smAppController.context)
        val scannPeriod: Long? = preferences.getString("scan_period", "1000").toLongOrNull()
        if (scannPeriod == null) {
            Toast.makeText(smAppController.context, "Invalid scan period value! Using 1000 ms", Toast.LENGTH_LONG)
                .show()
        }
        this.beaconManager.backgroundBetweenScanPeriod = 0
        this.beaconManager.backgroundScanPeriod = scannPeriod ?: 1000
        this.beaconManager.foregroundBetweenScanPeriod = 0
        this.beaconManager.foregroundScanPeriod = scannPeriod ?: 1000
        this.beaconManager.addRangeNotifier { beacons, region ->
            val beacns: MutableList<Beacon> = mutableListOf()
            beacns.addAll(beacons)

            val toRem = mutableListOf<MyBeacon>()
            for (b in beaconList) {
                if (!beacns.contains(b as Beacon)) {
                    if (b.isExpired()) {
                        if (b.isClose) {
                            b.isClose = false
                            smAppController.beaconStateChange(beaconList[beaconList.indexOf(b)])
                        }
                        toRem.add(b)
                    }
                }
            }
            for (b in toRem)
                beaconList.remove(b)

            val preferences = PreferenceManager.getDefaultSharedPreferences(smAppController.context)
            val scansBeforeGone: Int? = preferences.getString("scans_before_gone", "10").toIntOrNull()
            if(scansBeforeGone==null){
                Toast.makeText(smAppController.context, "Invalid number of scans! Using 10 ", Toast.LENGTH_LONG).show()
            }

            val iterator = beacns.iterator()
            while (iterator.hasNext()) {
                val b = MyBeacon(scansBeforeGone ?: 10, iterator.next())


                if (!beaconList.contains(b)) {
                    beaconList.add(b)
                }
                var wasClose = false

                if (savedBeacons.contains(b)) {
                    beaconList[beaconList.indexOf(b)].saveBeacon(
                        savedBeacons[savedBeacons.indexOf(b)].name,
                        savedBeacons[savedBeacons.indexOf(b)].calibratedRssi,
                        savedBeacons[savedBeacons.indexOf(b)].actionTagIn,
                        savedBeacons[savedBeacons.indexOf(b)].actionTagOut
                    )
                    wasClose = beaconList[beaconList.indexOf(b)].isClose
                }

                beaconList[beaconList.indexOf(b)].updateBeacon(b)

                if (savedBeacons.contains(b)) {
                    val isClose = beaconList[beaconList.indexOf(b)].isClose
                    if (wasClose != isClose) {
                        smAppController.beaconStateChange(beaconList[beaconList.indexOf(b)])
                    }
                }

            }
            smAppController.notifyDatasetChanged()
        }
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!btAdapter.isEnabled) {
            btAdapter.enable()
        }
        try {
            this.beaconManager.startRangingBeaconsInRegion(region)
            running = true
            Logger.log("Beacon scanning been started")
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun start() {
        this.beaconManager.bind(this)
    }

    fun stop() {
        try {
            this.beaconManager.stopRangingBeaconsInRegion(region)
            beaconList.clear()
            smAppController.notifyDatasetChanged()
            this.beaconManager.unbind(this)
            Logger.log("Beacon scanning been stopped")
            running = false
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}