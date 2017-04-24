package br.com.brolam.oha.supervisory.sync.tasks;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Definir os recursos para executar uma tarefa.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public interface IOhaTask {
    Context getContext();
    SharedPreferences getPreferences();
    boolean isBackupAndRestoreOperation();
}
