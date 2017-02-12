package br.com.brolam.ohaenergyuselog.apiV1.models;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.util.List;

/** OhaConnectionStatus -  recuperar informações sobre a conexão do módulo WiFI.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaConnectionStatus implements Serializable {

    /*****************************************************************************************
     * Constantes dos campos e flags para facilitar a leitura do conteúdo.
     ****************************************************************************************/
    private static final byte FIELD_NAME_HOME_WIFI = 0;
    private static final byte FIELD_IP_HOME_WIFI= 1;
    private static final byte FIELD_IP_LOCAL = 2;
    private static final String FLAG_COLUMNS = ",";
    private static final String FLAG_BEGIN = "<";
    private static final String FLAG_END = ">";
    /***************************************************************************************/

    /**
     * Nome da rede WiFi que o módulo WiFi está conectado.
     */
    private String nameHomeWiFi;

    /**
     * IP na rede WiFi.
     */
    private String ipHomeWiFi;


    /**
     * IP da rede interna do módulo WiFi.
     */
    private String ipLocal;


    public OhaConnectionStatus(String nameHomeWiFi, String ipHomeWiFi, String ipLocal) {
        this.nameHomeWiFi = nameHomeWiFi;
        this.ipHomeWiFi = ipHomeWiFi;
        this.ipLocal = ipLocal;
    }


    /**
     * Analisar e criar um {@see OhaConnectionStatus } conforme parâmetro abaixo:
     * @param strings informar o conteúdo retornar na API.
     * @return uma instância de OhaConnectionStatus.
     */
    public static OhaConnectionStatus parse(List<String> strings) {

        for (String strValue : strings) {
            //A situação da conexão válida deve ter os sinalizadores de inicio e fim e 3 colunas.
            if ((strValue.indexOf(FLAG_BEGIN) != -1) && (strValue.indexOf(FLAG_END) != -1)) {
                String[] values = strValue.replace(FLAG_BEGIN, "").replace(FLAG_END, "").split(FLAG_COLUMNS);
                if (values.length == 3) {
                    String nameHomeWiFi = values[FIELD_NAME_HOME_WIFI];
                    String ipHomeWiFi = values[FIELD_IP_HOME_WIFI];
                    String ipLocal = values[FIELD_IP_LOCAL];
                    return new OhaConnectionStatus(nameHomeWiFi, ipHomeWiFi, ipLocal);
                }
            }
        }
        return null;
    }
}
