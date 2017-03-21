package br.com.brolam.ohaenergyuselog.apiV1.models;

import java.io.Serializable;
import java.util.List;

/**
 * OhaConnectionStatus -  recuperar informações sobre a conexão do módulo WiFi.
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
    private static final byte FIELD_NAME_WIFI = 2;
    private static final byte FIELD_IP_WIFI = 3;
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
     * Nome do AP WiFi
     */
    private String nameWiFi;

    /**
     * IP do AP WiFi
     */
    private String ipWiFi;

    public OhaConnectionStatus(String nameHomeWiFi, String ipHomeWiFi, String nameWiFi, String ipWiFi) {
        this.nameHomeWiFi = nameHomeWiFi;
        this.ipHomeWiFi = ipHomeWiFi;
        this.nameWiFi = nameWiFi;
        this.ipWiFi = ipWiFi;
    }

    /**
     * Analisar e criar um {@see OhaConnectionStatus } conforme parâmetro abaixo:
     * @param strings informar o conteúdo retornar na API.
     * @return uma instância de OhaConnectionStatus ou null se o conteúdo for inválido.
     */
    public static OhaConnectionStatus parse(List<String> strings) {

        for (String strValue : strings) {
            //O conteúdo deve ter os sinalizadores de inicio e final e conter 4 colunas.
            if ((strValue.indexOf(FLAG_BEGIN) != -1) && (strValue.indexOf(FLAG_END) != -1)) {
                String[] values = strValue.replace(FLAG_BEGIN, "").replace(FLAG_END, "").split(FLAG_COLUMNS);
                if (values.length == 4) {
                    String nameHomeWiFi = values[FIELD_NAME_HOME_WIFI];
                    String ipHomeWiFi = values[FIELD_IP_HOME_WIFI];
                    String nameWiFi = values[FIELD_NAME_WIFI];
                    String ipWiFi = values[FIELD_IP_WIFI];
                    return new OhaConnectionStatus(nameHomeWiFi, ipHomeWiFi, nameWiFi, ipWiFi);
                }
            }
        }
        return null;
    }
}
