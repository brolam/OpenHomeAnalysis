package br.com.brolam.oha.supervisory.sync.tasks;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.data.helpers.OhaSQLHelper;
import br.com.brolam.oha.supervisory.ui.helpers.OhaBackupHelper;

/**
 * Realizar o backup ou restore do banco de dados.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaBackupTask implements OhaHelper.IZipFile {
    private static String TAG = "OhaBackupTask";
    private boolean flagRunning;
    private OhaBackupHelper ohaBackupHelper;
    private Context context;

    public OhaBackupTask(IOhaTask iOhaTask) {
        this.context = iOhaTask.getContext();
        this.ohaBackupHelper = new OhaBackupHelper(iOhaTask.getPreferences());
        this.flagRunning = false;
    }

    private void backup() {
        try {
            this.flagRunning = true;
            String backupName = OhaHelper.formatDate(new Date(),"yyyy_MM_dd_hh_mm_ss");
            Log.i(TAG, String.format("Backup %s started", backupName));
            OhaSQLHelper.backup(context, backupName, this);
            this.ohaBackupHelper.setLastDateTime();
            Log.i(TAG, String.format("Backup %s completed", backupName));
        } catch (IOException e) {
            //TODO Notify that backup did not run with successfully.
            Log.e(TAG, String.format("Backup completed with error: %s", e.toString()));
        } finally {
            this.flagRunning = false;
        }
    }

    private void restore() {
        try {
            String backupRestoreFilePath = ohaBackupHelper.getBackupRestoreFilePath();
            Log.i(TAG, String.format("Restore %s started", backupRestoreFilePath));
            this.flagRunning = true;
            OhaSQLHelper.restore(context, backupRestoreFilePath, null);
            Log.i(TAG, String.format("Restore %s completed", backupRestoreFilePath));
        } catch (IOException e) {
            //TODO Notify that restore did not run with successfully.
            Log.e(TAG, String.format("Restore completed with error: %s", e.toString()));
        } finally {
            this.flagRunning = false;
            this.ohaBackupHelper.clearBackupRestoreFilePath();
            this.ohaBackupHelper.setLastDateTime();
        }
    }

    public boolean isRunning() {
        return this.flagRunning;
    }

    public void execute() {
        if (this.ohaBackupHelper.isRestoreRequest()) {
            restore();
        } else if (this.ohaBackupHelper.isBackupTime()) {
            backup();
        }
    }

    @Override
    public void progress(long size, long sizeProcessed) {

    }
}
