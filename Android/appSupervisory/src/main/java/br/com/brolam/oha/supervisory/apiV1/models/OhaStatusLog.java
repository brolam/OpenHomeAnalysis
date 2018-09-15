package br.com.brolam.oha.supervisory.apiV1.models;

import java.util.List;

/** Definir todas as possíveis situações(Status) retornadas na API.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public enum OhaStatusLog {
    /** Sinalizar que não existe logs para a data solicitada. */
    LOG_DATE_NOT_EXISTS,

    /** Sinalizar que a leitura dos logs chegou ao final.*/
    LOG_END_OF_FILE,

    /** Sinalizar que o Cartão de memória do dispositivo OHA está com problema. */
    OHA_STATUS_NOT_SD,

    /** Sinalizar que a data e hora do dispositivo OHA não está atualizada. */
    OHA_STATUS_NOT_DATE,

    /** Sinalizar que os registros de logs não está finalizada para a data e hora informada.*/
    OHA_STATUS_RUNNING,

    /** Sinalizar que os registros de logs está finalizado para a data e hora informada.*/
    OHA_STATUS_FINISHED,

    /** Sinalizar o final da requisição*/
    OHA_REQUEST_END,

    /** Sinalizar que o Arduino não está respondendo ao módulo WIFI ( ESP8266)*/
    OHA_REQUEST_TIMEOUT;

    private static final String FLAG_COLUMNS = "\\|";
    private static final String FLAG_BEGIN = "<";
    private static final String FLAG_END = ">";

    /**
     * Informar a duração de execução em milissegundos da Arduino desde o último reset.
     */
    private long durationBoardRunning = -1;

    /**
     * Recuperar a duração de execução em milissegundos da Arduino desde o último reset ou
     * retornar a -1 se o valor for inválido.
     */
    public long getDurationBoardRunning() {
        return durationBoardRunning;
    }

    /**
     * Recuperar um OhaStatusLog conforme texto com o nome do Status
     * @param ohaStatusLogContent texto com o conteúdo do OhaStatusLog.
     * @return retornar com um OhaStatusLog válido ou nulo.
     */
    public static OhaStatusLog getOhaStatusLog(String ohaStatusLogContent){
        for(OhaStatusLog ohaStatusLog : OhaStatusLog.values() ){
            if (ohaStatusLogContent.contains(ohaStatusLog.toString())){
                ohaStatusLog.durationBoardRunning = getDurationBoardRunning(ohaStatusLogContent);
                return ohaStatusLog;
            }
        }
        return null;
    }

    /**
     * Recuperar a duração de execução em milissegundos da placa Arduino desde o último reset.
     * @param ohaStatusLogContent informar o conteúdo do OhaStatusLog
     * @return duração em milissegundos ou -1 se não for possível recupear a duração.
     */
    private static long getDurationBoardRunning(String ohaStatusLogContent) {
        try {
            if ( (ohaStatusLogContent.indexOf(FLAG_BEGIN) != -1) && (ohaStatusLogContent.indexOf(FLAG_END) != -1) ) {
                String[] values = ohaStatusLogContent.replace(FLAG_BEGIN, "").replace(FLAG_END, "").split(FLAG_COLUMNS);
                return values.length == 2 ? Long.parseLong(values[1]) : 0;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Recuperar um OhaStatusLog no final de uma lista de textos.
     * @param strings lista de texto.
     * @return retornar com um OhaStatusLog válido ou nulo.
     */
    public static OhaStatusLog getOhaStatusLog(List<String> strings) {
        //Localicar o OhaStatusLog nos 3 ultimos itens da lista.
        for (int item = 1; item <= strings.size(); item++) {
            String ohaStatusLogContent = strings.get(strings.size() - item);
            OhaStatusLog ohaStatusLog = getOhaStatusLog(ohaStatusLogContent);
            if ((ohaStatusLog != null) || item > 3) return ohaStatusLog;
        }
        return null;
    }

    /**
     * Verificar se existe um OhaStatusLog em uma lista.
     * @param ohaStatusLog informar um OhaStatusLog válido
     * @param strings informar uma lista válida.
     * @return verdadeiro se o OhaStatusLog existir nos e últimos itens da lista.
     */
    public static boolean exists(OhaStatusLog ohaStatusLog , List<String> strings ){
        //Localicar o OhaStatusLog nos 3 ultimos itens da lista.
        for (int item = 1; item <= strings.size(); item++) {
            String ohaStatusLogName = strings.get(strings.size() - item);
            if (ohaStatusLogName.contains(ohaStatusLog.toString()))
                return true;
            else if (item > 3) break;
        }
        return false;
    }
}




