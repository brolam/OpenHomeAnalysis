package br.com.brolam.ohaenergyuselog.apiV1;

import java.io.IOException;
import java.util.List;
import br.com.brolam.library.helpers.OhaNetworkHelper;
import br.com.brolam.ohaenergyuselog.apiV1.models.OhaStatusLog;

/**
 * OhaEnergyUseApi - Facilitar o acesso aos WebMethods e realizar requisições Http
 *                   no registrador de consumo de energia.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseApi {
    static final String URL_LOG = "log/";                //Solicitação de logs
    static final String URL_STATUS = "status/";          //Solicitação (GET) ou alteração(POST) do Status do registrador de consumo de energia.
    static final String URL_RESET = "reset/";            //Solicitação para reiniciar o Arduino, para mais detalhes {@see reset()}
    static final String URL_CONNECTION = "connection/";  //Solicitação da situação da conexão com o registrator de logs.

    /**
     * Recuperar uma sequencia de logs de consumo de energia conforme parâmetro abaixo:
     * @param hostName informar o nome ou endereço IP do registrador de logs na rede.
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strHour  informar texto com a hora no formato HH
     * @param startSequence informar o inicio da sequência do log.
     * @param amountLogs informar a quantidade de logs.
     * @param deleteDate informar a data dos logs que podem ser excluidos para liberar espaço no cartão de memória do registrador de logs.
     * @return uma lista de logs de consumo de energia no formato texto.
     * @throws IOException
     */
    public static List<String> getLogs(String hostName, String strDate, String strHour, int startSequence, int amountLogs, String deleteDate) throws IOException {
        String url = OhaNetworkHelper.parseUrl
                (
                        hostName,
                        URL_LOG,
                        strDate,
                        strHour,
                        String.valueOf(startSequence),
                        String.valueOf(amountLogs),
                        deleteDate
                );
        return OhaNetworkHelper.requestHttp("GET", url);
    }


    /**
     * Atualizar o status, data e hora do registrador de logs.
     * @param hostName informar o nome ou endereço IP do registrador de logs na rede.
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strTime  informar texto com a Hora, Minuto e segundo no formato HHmmss
     * @return um OhaStatusLog {@see OhaStatusLog.OHA_ACTION_END}
     * @throws IOException
     */
    public static OhaStatusLog setStatus(String hostName, String strDate, String strTime) throws IOException {
        String url = OhaNetworkHelper.parseUrl(hostName, URL_STATUS, strDate, strTime);
        List<String> strings = OhaNetworkHelper.requestHttp("POST", url);
        for (String strLog : strings) {
            for(OhaStatusLog ohaStatus:  OhaStatusLog.values() )
                if ( strLog.contains(ohaStatus.toString()))
                    return ohaStatus;
        }
        return null;
    }

    /**
     * Recuperar o status da sequência conforme parâmetros abaixo e do registrador de logs.
     * @param hostName informar o nome ou endereço IP do registrador de logs na rede.
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strHour  informar texto com a hora no formato HH
     * @return lista de texto com o conteúdo do status da sequência e do registrador de logs.
     * @throws IOException
     */
    public static List<String> getStatus(String hostName, String strDate, String strHour) throws IOException {
        String url = OhaNetworkHelper.parseUrl(hostName, URL_STATUS, strDate, strHour);
        return OhaNetworkHelper.requestHttp("GET", url);
    }

    /**
     * Reiniciar o registrador de logs.
     * @param hostName informar o nome ou endereço IP do registrador de logs na rede.
     * @return um OhaStatusLog {@see OhaStatusLog.OHA_ACTION_END}
     * @throws IOException
     */
    public static OhaStatusLog reset(String hostName) throws IOException {
        String url = OhaNetworkHelper.parseUrl(hostName, URL_RESET);
        List<String> strings = OhaNetworkHelper.requestHttp("POST", url);
        for (String strLog : strings) {
            for(OhaStatusLog ohaStatus:  OhaStatusLog.values() )
                if ( strLog.contains(ohaStatus.toString()))
                    return ohaStatus;
        }
        return null;
    }

    /**
     * Recuperar informações sobre a conexão do registrador de log com a rede.
     * @param hostName informar o nome ou endereço IP do registrador de logs na rede.
     * @return lista de texto com o conteúdo da situação da conexão ou nulo se o conteúdo for inválido.
     * @throws IOException
     */
    public static List<String> getConnection(String hostName) throws IOException {
        String url = OhaNetworkHelper.parseUrl(hostName, URL_CONNECTION);
        return OhaNetworkHelper.requestHttp("GET", url);
    }
}
