package pl.asseco.ptim.avagat.mobile.beaconapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.BeaconScanner
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.*

class SMApp : Application(), BeaconScanner.SMAppController {
    override fun beaconStateChange(beacon: MyBeacon) {
        if(beacon.isClose){
            actions.stateChangedAction(beacon.actionTagIn)
        }
        else{
            actions.stateChangedAction(beacon.actionTagOut)
        }
    }

    interface CurentFragment {
        fun notifyDatasetChanged()
    }

    private val logger: Logger = Logger()
    lateinit var beaconScanner: BeaconScanner
    override lateinit var smNotificationManager: SMNotificationManager
    private lateinit var database: DatabaseHandler
    override val context: Context = this
    var curentFragment: CurentFragment? = null
    private lateinit var actions: Actions

    override fun onCreate() {
        super.onCreate()
        database = DatabaseHandler(this)
        smNotificationManager = SMNotificationManager(this)
        beaconScanner = BeaconScanner(this)
        actions = Actions(this)
        Logger.log("Application start")
    }

    override fun notifyDatasetChanged() {
        curentFragment?.notifyDatasetChanged()
    }

    override fun getSavedBeacons(): MutableList<MyBeacon> {
        return database.getAllSavedBeacons()
    }

    fun saveBeacon(beacon: MyBeacon){
        Logger.log("Saving beacon: " + beacon.name)
        database.saveBeacon(beacon)
        beaconScanner.savedBeacons.add(beacon)
    }

    fun deleteBeacon(beacon: MyBeacon){
        Logger.log("Deleting beacon: " + beacon.name)
        database.deleteBeacon(beacon)
        beaconScanner.beaconList[beaconScanner.beaconList.indexOf(beacon)].removeBeacon()
        beaconScanner.savedBeacons.remove(beacon)
    }

    fun updateBeacon(beacon: MyBeacon){
        Logger.log("Updating beacon " + beaconScanner.savedBeacons[beaconScanner.savedBeacons.indexOf(beacon)].name + ", to " + beacon.name)
        database.updateBeacon(beacon)
        beaconScanner.savedBeacons[beaconScanner.savedBeacons.indexOf(beacon)].saveBeacon(beacon.name, beacon.calibratedRssi)
    }

    fun exit() {
        stopService(Intent(this, ExitListener::class.java))
        if (!beaconScanner.running) {
            Logger.log("Application exit")
            logger.close()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        else{
            Logger.log("Application closed, but service left running")
        }
    }
}