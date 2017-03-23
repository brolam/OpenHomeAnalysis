package br.com.brolam.ohaenergyuselog.apiV1.models;

import java.util.List;

/** Definir todas as possíveis situações(Status) retornadas na API.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public enum OhaStatusLog {
    /** Sinalizar que não existe logs para a data solicitada. */
    LOG_DATE_NOT_EXISTS,

    /** Sinalizar que não existe um log para a data e sequência solicitada.*/
    LOG_FILE_NOT_EXISTS,

    /** Sinalizar que o Cartão de memória do dispositivo OHA está com problema. */
    OHA_STATUS_NOT_SD,

    /** Sinalizar que a data e hora do dispositivo OHA não está atualizada. */
    OHA_STATUS_NOT_DATE,

    /** Sinalizar que os registros de logs não está finalizada para a data e hora informada.*/
    OHA_STATUS_RUNNING,

    /** Sinalizar que os registros de logs está finalizado para a data e hora informada.*/
    OHA_STATUS_FINISHED,

    /** Sinalizar o final da requisição*/
    OHA_REQUEST_END;

    /**
     * Recuperar um OhaStatusLog conforme texto com o nome do Status
     * @param ohaStatusLogName texto com o nome do OhaStatusLog.
     * @return retornar com um OhaStatusLog válido ou nulo.
     */
    public static OhaStatusLog getOhaStatusLog(String ohaStatusLogName){
        for(OhaStatusLog ohaStatus : OhaStatusLog.values() ){
            if (ohaStatusLogName.contains(ohaStatus.toString())){
                return ohaStatus;
            }
        }
        return null;
    }

    /**
     * Recuperar um OhaStatusLog no final de uma lista de textos.
     * @param strings lista de texto.
     * @return retornar com um OhaStatusLog válido ou nulo.
     */
    public static OhaStatusLog getOhaStatusLog(List<String> strings) {
        //Localicar o OhaStatusLog nos 3 ultimos itens da lista.
        for (int item = 1; item <= strings.size(); item++) {
            String ohaStatusLogName = strings.get(strings.size() - item);
            OhaStatusLog ohaStatusLog = getOhaStatusLog(ohaStatusLogName);
            if ((ohaStatusLog != null) || item > 3) return ohaStatusLog;
        }
        return null;
    }
}




