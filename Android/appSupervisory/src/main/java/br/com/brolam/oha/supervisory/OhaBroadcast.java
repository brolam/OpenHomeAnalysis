package br.com.brolam.oha.supervisory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import br.com.brolam.oha.supervisory.sync.OhaEnergyUseSyncService;

/**
 * Organizar todas os Broadcast do aplicativo
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaBroadcast extends BroadcastReceiver {
    //Evento para acionar a sincronização dos logs de utilização de energia.
    public static final String START_SYNC_ENERGY_USE = "br.com.brolam.oha.supervisory.broadcast.START_SYNC_ENERGY_USE";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case START_SYNC_ENERGY_USE:
                OhaEnergyUseSyncService.start(context);
                return;
            case Intent.ACTION_BOOT_COMPLETED:
                OhaEnergyUseSyncService.start(context);
                return;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                OhaEnergyUseSyncService.start(context);
                return;

        }
    }

    /**
     * Registrar o agendamento para acionar o serviço de sincronização dos logs
     * de utilização de energia.
     * @param context informar um contexto válido.
     */
    public static void registerSyncAlarm(Context context) {
        //Realizar um verificação se o PendingIntent está ativo a cada 5 segundos.
        int INTERVAL_IMPORT = 5000;
        Intent intent = new Intent(context, OhaBroadcast.class);
        intent.setAction(START_SYNC_ENERGY_USE);
        //Utilizar o parâmetro PendingIntent.FLAG_UPDATE_CURRENT para manter somente um
        //PendingIntent ativo e evitar o acionamento da sincronização mais de uma vez.
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Agendar o acionamento.
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INTERVAL_IMPORT,
                INTERVAL_IMPORT,
                sender);
    }
}
