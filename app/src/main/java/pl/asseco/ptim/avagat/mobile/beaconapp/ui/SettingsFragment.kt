package pl.asseco.ptim.avagat.mobile.beaconapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import pl.asseco.ptim.avagat.mobile.beaconapp.R

class SettingsFragment: PreferenceFragmentCompat() {

    private val preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            Toast.makeText(this@SettingsFragment.context, "Restart application to apply changes!", Toast.LENGTH_LONG).show()
        }

    override fun onCreatePreferences(p0: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onDestroy() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onDestroy()
    }
}