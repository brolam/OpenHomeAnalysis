package br.com.brolam.oha.supervisory;
/**
 * Organizar todas as Exceções do aplicativo
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaException extends Exception {

    public OhaException(String message) {
        super(message);
    }

    /****************************************************************************************
     * EnergyUseLog - Exceções para o tratamento de erros referente a tarefa de processamento
     * dos logs utilização de energia.
     ****************************************************************************************/
    public static class EnergyUseLogSyncFatalError extends OhaException {
        public EnergyUseLogSyncFatalError(String message) {
            super(message);
        }
    }

    public static class EnergyUseLogSdCardFatalError extends OhaException {
        public EnergyUseLogSdCardFatalError(String message) {
            super(message);
        }
    }

    public static class EnergyUseLogRead extends Exception {
        public EnergyUseLogRead(String message) {
            super(message);
        }
    }

    public static class EnergyUseLogReadSequenceBroken extends Exception {
        public EnergyUseLogReadSequenceBroken(Integer previous, int current) {
            super(String.format("EnergyUseLog read sequence was broken previous(%s) current(%s)", previous, current));
        }
    }

    public static class EnergyUseRequestTimeOut extends Exception {
        public EnergyUseRequestTimeOut(String message) {
            super(message);
        }
    }

    public static class BackupAndRestoreOperation extends Exception {
        public BackupAndRestoreOperation(String message) {
            super(message);
        }
    }
}
