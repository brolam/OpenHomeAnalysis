package br.com.brolam.oha.supervisory.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.List;

import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.helpers.AppCompatPreferenceHelper;
import br.com.brolam.oha.supervisory.ui.helpers.OhaBackupHelper;
import br.com.brolam.oha.supervisory.ui.helpers.OhaEnergyUseSyncHelper;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class OhaSettingsActivity extends AppCompatPreferenceHelper {
    //Registrar as Preferences para atualizar a tela se o valor da preference for atualizado.
    static HashMap<String, Preference> upgradeablePreferences = new HashMap<>();

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || EnergyUsePreferenceFragment.class.getName().equals(fragmentName)
                || BackupPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class EnergyUsePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_energy_use);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_HOST_NAME));
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_DATE));
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_HOUR));
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_SEQUENCE));
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_VOLTS));
            bindPreferenceSummaryToValue(findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_OFTEN_LOGGER_RESET));
            //Registrar as Preferences para monitorar as alterações para atualizar a tela:
            upgradeablePreferences.put(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_HOUR, findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_HOUR));
            upgradeablePreferences.put(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_SEQUENCE, findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_SEQUENCE));
            upgradeablePreferences.put(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_OFTEN_LOGGER_RESET, findPreference(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_OFTEN_LOGGER_RESET));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            upgradeablePreferences.remove(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_HOUR);
            upgradeablePreferences.remove(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_SEQUENCE);
            upgradeablePreferences.remove(OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_OFTEN_LOGGER_RESET);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BackupPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_backup);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(OhaBackupHelper.BACKUP_FREQUENCY));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        //Atualizar a tela com o novo valor da Preference.
        key = OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_DURATION_LOGGER_RUNNING.equals(key) ? OhaEnergyUseSyncHelper.ENERGY_USE_SYNC_OFTEN_LOGGER_RESET : key;
        Preference preference = upgradeablePreferences.get(key);
        if (preference != null) {
            bindPreferenceSummaryToValue(preference);
        }

    }
}