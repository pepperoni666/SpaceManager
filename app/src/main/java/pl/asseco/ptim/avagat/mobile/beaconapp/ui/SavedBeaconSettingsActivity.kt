package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.drm.DrmStore
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.media.AudioManager
import android.view.View
import android.widget.*
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.Actions


class SavedBeaconSettingsActivity : AppCompatActivity() {
    private var position: Int = -1
    private var oldInActionTag: String? = null
    private var oldOutActionTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_beacon_settings_activity)
        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
            supportActionBar?.title = (application as SMApp).beaconScanner.savedBeacons[position].name
            supportActionBar?.subtitle = String.format("%.2f", (application as SMApp).beaconScanner.savedBeacons[position].calibratedRssi)
            oldInActionTag = (application as SMApp).beaconScanner.savedBeacons[position].actionTagIn
            oldOutActionTag = (application as SMApp).beaconScanner.savedBeacons[position].actionTagOut
        }
        val spinnerIn: Spinner = findViewById(R.id.saved_beacon_settings_in_spinner)
        spinnerIn.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position!=-1){
                    (application as SMApp).beaconScanner.savedBeacons[this@SavedBeaconSettingsActivity.position].actionTagIn = Actions.ACTION_TAGS_LIST[position]
                }
            }

        }
        val spinnerOut: Spinner = findViewById(R.id.saved_beacon_settings_out_spinner)
        spinnerOut.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position!=-1){
                    (application as SMApp).beaconScanner.savedBeacons[this@SavedBeaconSettingsActivity.position].actionTagOut = Actions.ACTION_TAGS_LIST[position]
                }
            }

        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, Actions.ACTION_NAMES_LIST)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinnerIn.adapter = adapter
        if((application as SMApp).beaconScanner.savedBeacons[position].actionTagIn!=null){
            spinnerIn.setSelection(Actions.ACTION_TAGS_LIST.indexOf((application as SMApp).beaconScanner.savedBeacons[position].actionTagIn))
        }
        spinnerOut.adapter = adapter
        if((application as SMApp).beaconScanner.savedBeacons[position].actionTagOut!=null){
            spinnerOut.setSelection(Actions.ACTION_TAGS_LIST.indexOf((application as SMApp).beaconScanner.savedBeacons[position].actionTagOut))
        }
        val saveBtn: Button = findViewById(R.id.saved_beacon_settings_save_btn)
        saveBtn.setOnClickListener {
            if(oldInActionTag != (application as SMApp).beaconScanner.savedBeacons[position].actionTagIn || oldOutActionTag != (application as SMApp).beaconScanner.savedBeacons[position].actionTagOut){
                (application as SMApp).updateBeacon((application as SMApp).beaconScanner.savedBeacons[position])
                Toast.makeText(this, "Changes saved!", Toast.LENGTH_LONG).show()
            }
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}