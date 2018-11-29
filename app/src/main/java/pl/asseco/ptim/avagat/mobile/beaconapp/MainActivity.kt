package pl.asseco.ptim.avagat.mobile.beaconapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.estimote.mgmtsdk.connection.api.DeviceConnectionProvider
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.proximity_sdk.api.*

class MainActivity : AppCompatActivity() {

    private lateinit var text: TextView
    private var manager: Manager? = null
    var connectionProvider: DeviceConnectionProvider? = null

    private val TAG = MainActivity::class.java.simpleName
    private var proximityObservationHandle: ProximityObserver.Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectionProvider = DeviceConnectionProvider(this)
        text = findViewById(R.id.textView) as TextView
        val cloudCredentials = EstimoteCloudCredentials("space-manager-owa", "a4717c8775d24adde02f07b5dead7053")
        val zoneKey = "firstSon"//getZoneTag(intent) as? String
        manager = Manager(this)
        manager!!.discover()
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                whenBeaconIsCloseThenTriggerAnAction(
                    zoneKey,
                    cloudCredentials/*,
                    { text.text =  "Beacon is now close!"*//*Toast.makeText(this, "Beacon is now close!", Toast.LENGTH_LONG).show()*//* },
                    { text.text = "Beacon is now far!"*//*Toast.makeText(this, "Beacon is now far!", Toast.LENGTH_LONG).show()*//* }*/)
            },
            onRequirementsMissing = { text.text =  "Unable to start scan. Requirements not fulfilled: ${it.joinToString()}"/*Toast.makeText(this, "Unable to start scan. Requirements not fulfilled: ${it.joinToString()}", Toast.LENGTH_LONG).show()*/ },
            onError = { text.text =  "Error while checking requirements: ${it.message}"/*Toast.makeText(this, "Error while checking requirements: ${it.message}", Toast.LENGTH_LONG).show()*/ })
    }

    private fun whenBeaconIsCloseThenTriggerAnAction(key: String,
                                                     cloudCredentials: EstimoteCloudCredentials
                                                     /*actionToTriggerWhenBeaconComesClose: (ProximityZoneContext) -> Unit*/) {
        val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withTelemetryReportingDisabled()
            .withLowLatencyPowerMode()
            .onError { text.text =  "Proximity observation error: ${it.message}"/*Toast.makeText(this, "Proximity observation error: ${it.message}", Toast.LENGTH_LONG).show()*/ }
            .withAnalyticsReportingDisabled()
            .build()
        val zones: MutableList<ProximityZone> = arrayListOf()
        val zonesList: List<ProximityZone> = zones
        for( i in 1..5){
            zones.add(ProximityZoneBuilder()
                .forTag(key)
                .inCustomRange(i.toDouble()*3)
                .onEnter { text.text = "zone ${i}" }
                .onExit { text.text = "" }
                .build())
        }
        proximityObservationHandle = proximityObserver.startObserving(zonesList)
    }

    fun setText(s: String){
        text.text = s
    }

    override fun onDestroy() {
        connectionProvider!!.destroy()
        manager!!.destroy()
        super.onDestroy()
    }

    companion object {
        private val GPIO_PIN_NAME_KEY = "gpio_pin_name"
        private val BEACON_IDENTIFIER_KEY = "beacon_identifier"
        private val LIGHT_LEVEL_THRESHOLD_KEY = "light_level_threshold"
        private val TEMPERATURE_THRESHOLD_KEY = "temperature_threshold"
        private val APP_ID_KEY = "space-manager-owa"
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
