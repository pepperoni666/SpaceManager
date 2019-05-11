package pl.asseco.ptim.avagat.mobile.beaconapp

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.BeaconScanner
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.Logger
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.SMNotificationManager

class SMApp: Application(), BeaconScanner.SMAppController{

    interface CurentFragment{
        fun notifyDatasetChanged()
    }

    private val logger: Logger = Logger()
    lateinit var beaconScanner: BeaconScanner
    override lateinit var smNotificationManager: SMNotificationManager
    override val context: Context = this
    var curentFragment: CurentFragment? = null

    override fun onCreate() {
        super.onCreate()
        smNotificationManager = SMNotificationManager(this)
        beaconScanner = BeaconScanner(this)
    }

    override fun notifyDatasetChanged() {
        curentFragment?.notifyDatasetChanged()
    }

    fun exit(){
        logger.close()
    }

    override fun onTerminate() {
        beaconScanner.stop()
        super.onTerminate()
    }
}