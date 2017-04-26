package br.com.brolam.oha.supervisory.ui.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

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
public class OhaBackupHelper {
    public static final String BACKUP_ACTIVATED = "backup_activated";
    public static final String BACKUP_FREQUENCY = "backup_frequency";
    public static final String BACKUP_LAST_DATE_TIME = "backup_last_date_time";
    public static final String BACKUP_RESTORE_FILE_PATH = "backup_restore_file_path";

    SharedPreferences preferences;
    public OhaBackupHelper(SharedPreferences preferences ) {
        this.preferences = preferences;
    }

    public OhaBackupHelper(Context context ) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isActivated() {
        return preferences.getBoolean(BACKUP_ACTIVATED, true);
    }


    public String getFrequency() {
        return preferences.getString(BACKUP_FREQUENCY, "12");
    }

    public long getLastDateTime() {
        Long nextDateTime = preferences.getLong(BACKUP_LAST_DATE_TIME, 0);
        if ( nextDateTime == 0 ){
            setLastDateTime();
            return getLastDateTime();
        }
        return nextDateTime;
    }

    public boolean isBackupTime() {
        if (isActivated() == false) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(getLastDateTime()));
        calendar.add(Calendar.HOUR, Integer.parseInt(getFrequency()));
        return calendar.getTime().before(new Date());
    }

    public boolean isRestoreRequest(){
        return getBackupRestoreFilePath() != null;
    }

    public String getBackupRestoreFilePath() {
        return preferences.getString(BACKUP_RESTORE_FILE_PATH, null);
    }

    public void setLastDateTime() {
        Date date = new Date();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(BACKUP_LAST_DATE_TIME, date.getTime());
        editor.commit();
    }

    public void setBackupTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, Integer.parseInt(getFrequency()) * -1);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(BACKUP_LAST_DATE_TIME, calendar.getTime().getTime());
        editor.commit();

    }

    public void setBackupRestoreFilePath(String backupRestoreFilePath){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BACKUP_RESTORE_FILE_PATH, backupRestoreFilePath);
        editor.commit();
    }

    public void clearBackupRestoreFilePath(){
        setBackupRestoreFilePath(null);
    }




}
