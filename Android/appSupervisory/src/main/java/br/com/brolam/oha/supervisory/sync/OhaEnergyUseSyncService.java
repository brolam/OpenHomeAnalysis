package br.com.brolam.oha.supervisory.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import br.com.brolam.oha.supervisory.sync.tasks.OhaEnergyUseLogTask;
import br.com.brolam.oha.supervisory.ui.helpers.OhaEnergyUseSyncHelper;

/**
 * Executar em background a tarefa de sincronização {@link OhaEnergyUseLogTask}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseSyncService extends IntentService implements OhaEnergyUseSyncHelper.IOhaEnergyUseSyncHelper {
    private static final String TAG = "OhaEnergyUseSyncService";
    //Variável stática para somente permitir uma instância do objeto OhaEnergyUseLogTask.
    private static OhaEnergyUseLogTask ohaEnergyUseLogTask;
    //Recuperar as preferências da tarefe de sincronizaão:
    private OhaEnergyUseSyncHelper ohaEnergyUseSyncHelper;

    public OhaEnergyUseSyncService() {
        super(TAG);
        ohaEnergyUseLogTask = new OhaEnergyUseLogTask();
    }

    /**
     * Inicirar o serviço em background.
     */
    public static void start(Context context){
        Intent intent = new Intent(context, OhaEnergyUseSyncService.class);
        context.startService(intent);
    }

    /**
     * Executar a tarafe de sincronização:
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!ohaEnergyUseLogTask.isRunning()) {
            ohaEnergyUseSyncHelper = new OhaEnergyUseSyncHelper(this);
            ohaEnergyUseSyncHelper.setIOhaEnergyUseSyncHelper(this);
            ohaEnergyUseLogTask.execute(ohaEnergyUseSyncHelper);
        }
    }

    /**
     * Monitorar as alterações nas preferências de sincronizacão e
     * parar o serviço se a sincronização for desligada.
     */
    @Override
    public void onChange(String property) {
        if (ohaEnergyUseSyncHelper.isSyncTurnOff()){
            ohaEnergyUseLogTask.stop();
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( ohaEnergyUseSyncHelper != null)
            ohaEnergyUseSyncHelper.destroy();
    }
}
