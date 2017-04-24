package br.com.brolam.oha.supervisory.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import br.com.brolam.oha.supervisory.sync.tasks.IOhaTask;
import br.com.brolam.oha.supervisory.sync.tasks.OhaBackupTask;
import br.com.brolam.oha.supervisory.sync.tasks.OhaEnergyUseLogTask;
import br.com.brolam.oha.supervisory.ui.helpers.OhaBackupHelper;

/**
 * Executar em background a tarefa de sincronização {@link OhaEnergyUseLogTask}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaSyncService extends IntentService implements IOhaTask {
    private static final String TAG = "OhaSyncService";
    //Variável stática para somente permitir uma instância do objeto OhaEnergyUseLogTask.
    private static OhaEnergyUseLogTask ohaEnergyUseLogTask;
    private static OhaBackupTask ohaBackupTask;
    private SharedPreferences preferences;

    public OhaSyncService() {
        super(TAG);

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        this.preferences =  PreferenceManager.getDefaultSharedPreferences(getContext());
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Inicirar o serviço em background.
     */
    public static void start(Context context){
        Intent intent = new Intent(context, OhaSyncService.class);
        context.startService(intent);
    }

    /**
     * Executar as tarefas
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Priorizar a execução da tarefa de backup e restore.
        if (isBackupAndRestoreOperation()) {
            if ((ohaBackupTask == null) || (ohaBackupTask.isRunning() == false)) {
                this.ohaBackupTask = new OhaBackupTask(this);
                this.ohaBackupTask.execute();
            }
        } else {
            if ((ohaEnergyUseLogTask == null) || (ohaEnergyUseLogTask.isRunning() == false)) {
                ohaEnergyUseLogTask = new OhaEnergyUseLogTask(this);
                ohaEnergyUseLogTask.execute();
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public SharedPreferences getPreferences() {
        return this.preferences;
    }

    @Override
    public boolean isBackupAndRestoreOperation() {
        OhaBackupHelper ohaBackupHelper = new OhaBackupHelper(this.preferences);
        return ohaBackupHelper.isBackupTime() || ohaBackupHelper.isRestoreRequest();
    }


}
