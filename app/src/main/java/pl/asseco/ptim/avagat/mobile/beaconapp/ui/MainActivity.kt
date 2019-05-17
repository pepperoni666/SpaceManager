package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import pl.asseco.ptim.avagat.mobile.beaconapp.R
import pl.asseco.ptim.avagat.mobile.beaconapp.SMApp
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.ExitListener
import pl.asseco.ptim.avagat.mobile.beaconapp.utils.Logger


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationBiew: NavigationView
    private lateinit var toolbar: Toolbar

    val MY_PERMITION: Int = 1

    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationBiew = findViewById(R.id.nav_view)
        navigationBiew.setNavigationItemSelectedListener(this)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BeaconListFragment()).commit()
            navigationBiew.setCheckedItem(R.id.nav_all_beacons)
            toolbar.title = getString(R.string.all_beacons)
        }

        if(!(application as SMApp).beaconScanner.running){
            navigationBiew.menu.findItem(R.id.nav_run).setVisible(true)
            navigationBiew.menu.findItem(R.id.nav_stop).setVisible(false)
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMITION
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                MY_PERMITION
            )
            return
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMITION
            )
            return
        }

        startService(Intent(this, ExitListener::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all_beacons -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BeaconListFragment())
                    .commit()
                Logger.log("AllBeacons-fragment selected")
                toolbar.title = getString(R.string.all_beacons)
            }
            R.id.nav_saved_beacons -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SavedBeaconListFragment()).commit()
                Logger.log("SavedBeacons-fragment selected")
                toolbar.title = getString(R.string.saved_beacons)
            }
            R.id.nav_settings -> {
                //Logger.log("SavedBeacons-fragment selected")
            }
            R.id.nav_stop -> {
                navigationBiew.menu.findItem(R.id.nav_run).setVisible(true)
                item.setVisible(false)
                (application as SMApp).beaconScanner.stop()
            }
            R.id.nav_run -> {
                navigationBiew.menu.findItem(R.id.nav_stop).setVisible(true)
                item.setVisible(false)
                (application as SMApp).beaconScanner.start()
            }
            else -> {
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
