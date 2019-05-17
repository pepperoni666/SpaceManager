package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.media.AudioManager
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon


class SavedBeaconSettingsActivity : AppCompatActivity() {

    private lateinit var thisBeacon: MyBeacon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_beacon_settings_activity)
        if(intent.hasExtra("position")){
            thisBeacon = (application as SMApp).beaconScanner.savedBeacons[intent.getIntExtra("position", 0)]
            supportActionBar?.title = thisBeacon.name
            supportActionBar?.subtitle = String.format("%.2f", thisBeacon.calibratedRssi)
        }
        //TODO: to change beacon, onitemlongclick?
        //changing ring sound mode
        /*val amanager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        amanager.ringerMode = AudioManager.RINGER_MODE_VIBRATE*/
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}