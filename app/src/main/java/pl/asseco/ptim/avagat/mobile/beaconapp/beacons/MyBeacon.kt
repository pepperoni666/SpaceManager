package pl.asseco.ptim.avagat.mobile.beaconapp.beacons

import org.altbeacon.beacon.Beacon

class MyBeacon: Beacon {

    private var emptyScanCount = 0

    var name: String = ""
    var calibratedRssi: Double = 0.0

    //for Kalman
    private val Q = 1e-5
    private val RR = Math.pow(0.05, 2.0)
    var rssi: Double? = null
    private var P = 1.0

    var isClose = false
    var actionTagIn: String? = null
    var actionTagOut: String? = null

    private var scansBeforeGone: Int = 10

    constructor(scansBeforeGone: Int, mac: String, uuid: String, major: String, minor: String):
        super(
            Beacon.Builder()
                .setBluetoothAddress(mac)
                .setId1(uuid)
                .setId2(major)
                .setId3(minor).build()
        ){ this.scansBeforeGone = scansBeforeGone}

    constructor(scansBeforeGone: Int, b: Beacon): super(b) {this.scansBeforeGone = scansBeforeGone}

    fun isExpired(): Boolean {
        emptyScanCount++
        return emptyScanCount == scansBeforeGone
    }

    fun saveBeacon(name: String, rssi: Double, actionTagIn: String?, actionTagOut: String?) {
        this.name = name
        this.calibratedRssi = rssi
        this.actionTagIn = actionTagIn
        this.actionTagOut = actionTagOut
    }

    fun saveBeacon(name: String, rssi: Double) {
        this.name = name
        this.calibratedRssi = rssi
    }

    fun removeBeacon() {
        this.name = ""
        this.calibratedRssi = 0.0
    }

    fun updateBeacon(beacon: MyBeacon) {
        emptyScanCount = 0
        if (rssi != null) {

            // time update
            val xhatminus = rssi!!
            val Pminus = P + Q

            // measurement update
            val K = Pminus / (Pminus + RR)
            rssi = xhatminus + K * (beacon.getRssi().toDouble() - xhatminus)
            P = (1 - K) * Pminus


            if (calibratedRssi != 0.0) {
                if (rssi!! >= calibratedRssi && !isClose) {
                    isClose = true
                } else if (rssi!! < calibratedRssi && isClose) {
                    isClose = false
                }
            }
        } else
            rssi = beacon.getRssi().toDouble()
    }

    override fun equals(b: Any?): Boolean {
        if (b is Beacon) {
            if (super.getBluetoothAddress() == b.bluetoothAddress) {
                return true
            }
        }
        return false
    }
}