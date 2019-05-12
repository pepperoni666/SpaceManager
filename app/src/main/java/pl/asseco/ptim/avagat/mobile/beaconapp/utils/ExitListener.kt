package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp

class ExitListener: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        (application as SMApp).exit()
    }
}