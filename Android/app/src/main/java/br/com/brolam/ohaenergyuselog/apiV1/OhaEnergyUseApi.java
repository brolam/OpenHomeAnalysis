package br.com.brolam.ohaenergyuselog.apiV1;

/**
 * OhaEnergyUseApi - Facilitar o acesso aos WebMethods e realizar requisições Http
 *                   no registrador de consumo de energia.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseApi {
    static final String DEBUG_TAG = "OhaEnergyUseApi";
    static final String URL_LOG = "log/";                //Solicitação de logs
    static final String URL_STATUS = "status/";          //Solicitação (GET) ou alteração(POST) do Status do registrador de consumo de energia.
    static final String URL_RESET = "reset/";            //Solicitação para reiniciar o Arduino, para mais detalhes {@see reset()}
    static final String URL_CONNECTION = "connection/";  //Solicitação da situação da conexão com o registrator de logs.
}
