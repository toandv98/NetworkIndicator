package com.toandv.networkindicator.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.toandv.networkindicator.R
import com.toandv.networkindicator.utils.SchedulerUtils.schedulerJob

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val hide = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_hide))
        val boot = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_boot))
        val lock = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_lock_screen))

        hide?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            schedulerJob(requireContext())
            true
        }
        boot?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            schedulerJob(requireContext())
            true
        }
        lock?.setOnPreferenceChangeListener { _, _ ->
            schedulerJob(requireContext())
            true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}