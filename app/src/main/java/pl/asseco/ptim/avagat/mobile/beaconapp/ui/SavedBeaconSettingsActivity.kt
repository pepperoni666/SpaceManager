package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.drm.DrmStore
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.media.AudioManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.Actions


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
        val spinnerIn: Spinner = findViewById(R.id.saved_beacon_settings_in_spinner)
        spinnerIn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

        }
        val spinnerOut: Spinner = findViewById(R.id.saved_beacon_settings_out_spinner)
        spinnerOut.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //
            }

        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, Actions.ACTION_LIST)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinnerIn.adapter = adapter
        spinnerOut.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}