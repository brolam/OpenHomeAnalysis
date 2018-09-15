package br.com.brolam.oha.supervisory.ui.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.R;
import br.com.brolam.oha.supervisory.ui.OhaSettingsActivity;

/**
 * Recuperar as preferências de sincronização dos logs de utilização de energia
 * {@link OhaSettingsActivity}
 * {@link br.com.brolam.oha.supervisory.sync.tasks.OhaEnergyUseLogTask}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseSyncHelper {
    public static final String ENERGY_USE_SYNC_SETUP_MODE = "energy_use_sync_setup_mode";
    public static final String ENERGY_USE_SYNC_HOST_NAME = "energy_use_sync_host_name";
    public static final String ENERGY_USE_SYNC_DATE = "energy_use_sync_date";
    public static final String ENERGY_USE_SYNC_HOUR = "energy_use_sync_hour";
    public static final String ENERGY_USE_SYNC_SEQUENCE = "energy_use_sync_sequence";
    public static final String ENERGY_USE_SYNC_VOLTS = "energy_use_sync_volts";
    public static final String ENERGY_USE_SYNC_OFTEN_LOGGER_RESET = "energy_use_sync_often_logger_reset";
    public static final String ENERGY_USE_SYNC_DURATION_LOGGER_RUNNING = "energy_use_sync_duration_logger_running";
    public static final String ENERGY_USE_SYNC_DAYS_SD_CARD_STORED = "energy_use_sync_days_sd_card_stored";
    public static final String ENERGY_USE_SYNC_SENSOR_TO_AMPERES = "energy_use_sync_sensor_to_amperes";

    Context context;
    SharedPreferences preferences;

    public OhaEnergyUseSyncHelper(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    public OhaEnergyUseSyncHelper(Context context ) {
        this(context,PreferenceManager.getDefaultSharedPreferences(context) );
    }


    public Context getContext() {
        return context;
    }

    public boolean isSetupMode() {
        return preferences.getBoolean(ENERGY_USE_SYNC_SETUP_MODE, true);
    }

    public String getHostName() {
        String hostName = preferences.getString(ENERGY_USE_SYNC_HOST_NAME, null);
        if (hostName == null) {
            setHostName(context.getString(R.string.pref_host_name_energy_use_sync_default));
            return getHostName();
        }
        return hostName;
    }

    public String getStrDate() {
        String strDate = preferences.getString(ENERGY_USE_SYNC_DATE, null);
        if (strDate == null) {
            setDate(OhaHelper.getStrDate(new Date()));
            return getStrDate();
        }
        return strDate;
    }

    public String getStrHour() {
        String strHour = preferences.getString(ENERGY_USE_SYNC_HOUR, null);
        if (strHour == null) {
            setHour(OhaHelper.getStrHour(new Date()));
            return getStrHour();
        }
        return strHour;
    }

    public int getSequence() {
        int sequence = Integer.valueOf(preferences.getString(ENERGY_USE_SYNC_SEQUENCE, "-1"));
        if (sequence < 0) {
            setSequence(0);
            return getSequence();
        }
        return sequence;
    }

    public double getDefaultVolts(Double volts) {
        return volts < 100 ? Double.valueOf(preferences.getString(ENERGY_USE_SYNC_VOLTS, "220")) : volts;
    }

    public int getOftenLoggerReset(){
        return Integer.valueOf(preferences.getString(ENERGY_USE_SYNC_OFTEN_LOGGER_RESET, "48"));
    }

    public long getOftenLoggerResetMillisecond() {
        return getOftenLoggerReset() * DateUtils.HOUR_IN_MILLIS;
    }

    public long getDurationLoggerRunning(){
        return preferences.getLong(ENERGY_USE_SYNC_DURATION_LOGGER_RUNNING, 0);
    }

    public int getDaysSdCardStored(){
        return Integer.valueOf(preferences.getString(ENERGY_USE_SYNC_DAYS_SD_CARD_STORED, context.getString(R.string.pref_energy_use_sync_days_sd_card_stored_default)));
    }

    public double getSensorValueToAmperes(){
        return Double.valueOf(preferences.getString(ENERGY_USE_SYNC_SENSOR_TO_AMPERES, context.getString(R.string.pref_energy_use_sensor_to_amperes_default)));
    }

    public void setSetupModeOn() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ENERGY_USE_SYNC_SETUP_MODE, true);
        editor.commit();
    }

    public void setHostName(String hostName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENERGY_USE_SYNC_HOST_NAME, hostName);
        editor.commit();
    }

    public void setDate(String strDate) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENERGY_USE_SYNC_DATE, strDate);
        editor.commit();
    }

    public void setHour(String strHour) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENERGY_USE_SYNC_HOUR, strHour);
        editor.commit();
    }

    public void setSequence(int sequence) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENERGY_USE_SYNC_SEQUENCE, String.valueOf(sequence));
        editor.commit();
    }

    public void setDurationLoggerRunning(long milliseconds) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(ENERGY_USE_SYNC_DURATION_LOGGER_RUNNING, milliseconds);
        editor.commit();
    }

    public void parseDefaultValues(){
        if ( preferences.getString(ENERGY_USE_SYNC_DATE, null) == null){
            getHostName();
            getStrDate();
            getStrHour();
            getSequence();
        }
    }

}
