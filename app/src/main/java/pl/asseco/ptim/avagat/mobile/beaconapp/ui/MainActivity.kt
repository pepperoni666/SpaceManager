package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.ListView
import android.widget.Toast
import com.estimote.coresdk.common.config.EstimoteSDK
import com.estimote.mgmtsdk.connection.api.DeviceConnectionProvider
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.proximity_sdk.api.*
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.state.BeaconState
import com.google.android.gms.common.api.GoogleApiClient
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.BeaconsScannManager
import com.google.android.gms.location.places.ui.PlaceAutocomplete.getStatus
import com.google.android.gms.awareness.snapshot.BeaconStateResult
import android.support.annotation.NonNull
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import com.google.android.gms.common.api.ResultCallback
import pl.asseco.ptim.avagat.mobile.beaconapp.R


class MainActivity : AppCompatActivity() {

    private var beaconsScannManager: BeaconsScannManager? = null
    var connectionProvider: DeviceConnectionProvider? = null
    private lateinit var listView: ListView
    private lateinit var adapter: BeaconListAdapter

    val MY_PERMISSION_LOCATION: Int = 1
    val MY_PERMISSION_BLUETOOTH: Int = 2

    private val TAG = MainActivity::class.java.simpleName
    private var proximityObservationHandle: ProximityObserver.Handler? = null

    val BEACON_TYPE_FILTERS: List<BeaconState.TypeFilter> = listOf(
        BeaconState.TypeFilter.with("spacemanager", "spacemanager/")
    )

    lateinit var client: GoogleApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(pl.asseco.ptim.avagat.mobile.beaconapp.R.layout.activity_main)

        client = GoogleApiClient.Builder(applicationContext)
            .addApi(Awareness.API)
            .build()

        client.connect()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_LOCATION
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                MY_PERMISSION_BLUETOOTH
            )
            return
        }

        val b: Button = findViewById(R.id.beacon_state_btn)

        b.setOnClickListener {
            Awareness.SnapshotApi.getBeaconState(client, BEACON_TYPE_FILTERS)
                .setResultCallback(object : ResultCallback<BeaconStateResult> {
                    override fun onResult(@NonNull beaconStateResult: BeaconStateResult) {
                        if (!beaconStateResult.status.isSuccess) {
                            Log.e("TAG", "Could not get beacon state.")
                            return
                        }
                        val beaconState = beaconStateResult.beaconState
                        beaconState?.beaconInfo
                    }
                })
        }

        connectionProvider = DeviceConnectionProvider(applicationContext)
        val cloudCredentials = EstimoteCloudCredentials("space-manager-owa", "a4717c8775d24adde02f07b5dead7053")
        EstimoteSDK.initialize(applicationContext, "space-manager-owa", "a4717c8775d24adde02f07b5dead7053")
        val zoneKey  = listOf("3.1", "3.2","3.3", "firstSon")//getZoneTag(intent) as? String
        beaconsScannManager = BeaconsScannManager(this)
        beaconsScannManager!!.connect()
        //beaconsScannManager!!.discover()
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                whenBeaconIsCloseThenTriggerAnAction(
                    zoneKey,
                    cloudCredentials/*,
                    { text.text =  "Beacon is now close!"*//*Toast.makeText(this, "Beacon is now close!", Toast.LENGTH_LONG).show()*//* },
                    { text.text = "Beacon is now far!"*//*Toast.makeText(this, "Beacon is now far!", Toast.LENGTH_LONG).show()*//* }*/)
            },
            onRequirementsMissing = { Toast.makeText(this, "Unable to start scan. Requirements not fulfilled: ${it.joinToString()}", Toast.LENGTH_LONG).show() },
            onError = { Toast.makeText(this, "Error while checking requirements: ${it.message}", Toast.LENGTH_LONG).show() })

        listView = findViewById(R.id.beacon_list)
        adapter = BeaconListAdapter(this, beaconsScannManager!!.beaconsSetManager)
        listView.adapter = adapter


    }

    fun notifyDataSetChanged(){
        adapter.notifyDataSetChanged()
    }

    private fun whenBeaconIsCloseThenTriggerAnAction(keys: List<String>,
                                                     cloudCredentials: EstimoteCloudCredentials
                                                     /*actionToTriggerWhenBeaconComesClose: (ProximityZoneContext) -> Unit*/) {
        val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withTelemetryReportingDisabled()
            .withLowLatencyPowerMode()
            .onError { Toast.makeText(this, "Proximity observation error: ${it.message}", Toast.LENGTH_LONG).show() }
            .withAnalyticsReportingDisabled()
            .build()
        val zonesList: MutableList<ProximityZone> = mutableListOf()
        for(z in keys){
            zonesList.add(ProximityZoneBuilder()
                .forTag(z)
                .inNearRange()
                .onEnter {
                    Toast.makeText(this, "Beacon close: ${it.tag}", Toast.LENGTH_LONG).show()
                }
                .onExit {
                    Toast.makeText(this, "Beacon far: ${it.tag}", Toast.LENGTH_LONG).show()
                }
                .build())
        }
        proximityObservationHandle = proximityObserver.startObserving(zonesList)
    }

    override fun onDestroy() {
        proximityObservationHandle?.stop()
        connectionProvider!!.destroy()
        beaconsScannManager!!.destroy()
        super.onDestroy()
    }

    companion object {
        private val GPIO_PIN_NAME_KEY = "gpio_pin_name"
        private val BEACON_IDENTIFIER_KEY = "beacon_identifier"
        private val LIGHT_LEVEL_THRESHOLD_KEY = "light_level_threshold"
        private val TEMPERATURE_THRESHOLD_KEY = "temperature_threshold"
        private val APP_ID_KEY = "space-beaconsScannManager-owa"
        private val APP_TOKEN_KEY = "a4717c8775d24adde02f07b5dead7053"
        private val ZONE_TAG_KEY = "firstSon"

        fun <T> createSensorsIntent(context: Context,
                                    activityClass: Class<T>,
                                    gpioPinName: String,
                                    beaconId: String,
                                    lightLevelThreshold: Double = 0.0,
                                    temperatureThreshold: Double = 20.0): Intent {
            val intent = Intent(context, activityClass)
            intent.putExtra(GPIO_PIN_NAME_KEY, gpioPinName)
            intent.putExtra(BEACON_IDENTIFIER_KEY, beaconId)
            intent.putExtra(LIGHT_LEVEL_THRESHOLD_KEY, lightLevelThreshold)
            intent.putExtra(TEMPERATURE_THRESHOLD_KEY, temperatureThreshold)
            return intent
        }

        fun <T> createProximityIntent(context: Context,
                                      activityClass: Class<T>,
                                      appId: String,
                                      appToken: String,
                                      zoneTag: String): Intent {
            val intent = Intent(context, activityClass)
            intent.putExtra(APP_ID_KEY, appId)
            intent.putExtra(APP_TOKEN_KEY, appToken)
            intent.putExtra(ZONE_TAG_KEY, zoneTag)
            return intent
        }

        fun getGpioPinName(intent: Intent) = intent.extras.getString(GPIO_PIN_NAME_KEY)
        fun getBeaconIdentifier(intent: Intent) = intent.extras.getString(BEACON_IDENTIFIER_KEY)
        fun getLightLevelThreshold(intent: Intent) = intent.extras.getDouble(LIGHT_LEVEL_THRESHOLD_KEY)
        fun getTemperatureThreshold(intent: Intent) = intent.extras.getDouble(TEMPERATURE_THRESHOLD_KEY)
        fun getAppId(intent: Intent) = intent.extras.getString(APP_ID_KEY)
        fun getAppToken(intent: Intent) = intent.extras.getString(APP_TOKEN_KEY)
        fun getZoneTag(intent: Intent) = intent.extras.getString(ZONE_TAG_KEY)
    }
}
