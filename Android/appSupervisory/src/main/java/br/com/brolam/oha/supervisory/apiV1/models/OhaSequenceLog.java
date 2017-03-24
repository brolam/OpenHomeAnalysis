package br.com.brolam.oha.supervisory.apiV1.models;

import android.text.format.DateUtils;
import java.io.Serializable;
import java.util.List;

/**
 * OhaSequenceLog -  recuperar informações sobre o controle de  geração de logs, informando a última sequência
 *                    e situação da geração de logs para uma determinada data e hora.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaSequenceLog implements Serializable {

    /*****************************************************************************************
     * Constantes dos campos e flags para facilitar a leitura do conteúdo.
     ****************************************************************************************/
    private static final byte FIELD_SEQUENCE = 0;
    private static final byte FIELD_STATUS = 1;
    private static final byte FIELD_DURATION_BOARD_RUNNING = 2;
    private static final String FLAG_COLUMNS = ":";
    private static final String FLAG_BEGIN = "<";
    private static final String FLAG_END = ">";
    /***************************************************************************************/

    /**
     * Informar a ultima sequência de registro de log.
     */
    private int lastSequence;

    /**
     * Informar a duração em milissegundos em que a placa Arduino está em execução desde o último reset.
     */
    private long durationBoardRunning;

    /**
     * Informar a situação do processo de registros dos logs.
     * @see OhaStatusLog STOPPED
     * @see OhaStatusLog RUNNING
     */
    private OhaStatusLog ohaStatusLog;

    public OhaSequenceLog(int lastSequence, OhaStatusLog ohaStatusLog, long durationBoardRunning){
        this.lastSequence = lastSequence;
        this.ohaStatusLog = ohaStatusLog;
        this.durationBoardRunning = durationBoardRunning;
    }

    public int getLastSequence() {
        return lastSequence;
    }

    public double getTotalBoardHoursRunning(){
        if ( this.durationBoardRunning > 0){
            return (double)durationBoardRunning / (double) DateUtils.HOUR_IN_MILLIS;
        }
        return 0.00;
    }


    public OhaStatusLog getOhaStatusLog() {
        return ohaStatusLog;
    }

    /**
     * Analisar e criar um {@see OhaSequenceLog } conforme parâmetro abaixo:
     * @param strings informar o conteúdo retornar na API.
     * @return uma instância de OhaSequenceLog.
     */
    public static OhaSequenceLog parse(List<String> strings) {
        OhaStatusLog ohaStatusLog = null;
        int lastSequence = -1; //Valor padrão para sequência inválida.
        long durationBoardRunning = 0;

        for (String strValue : strings) {
            //Uma sequencia válida deve ter os sinalizadores de inicio e fim e 3 colunas/.
            if ( (strValue.indexOf(FLAG_BEGIN) != -1) && (strValue.indexOf(FLAG_END) != -1) ){
                String[] values = strValue.replace(FLAG_BEGIN,"").replace(FLAG_END,"").split(FLAG_COLUMNS);
                if ( values.length == 3){
                    lastSequence = Integer.valueOf(values[FIELD_SEQUENCE]);
                    ohaStatusLog = OhaStatusLog.getOhaStatusLog(values[FIELD_STATUS]);
                    durationBoardRunning = Long.valueOf(values[FIELD_DURATION_BOARD_RUNNING]);
                    break;
                }
            //Tentar recuepar o primeiro OhaStatusLog válido.
            } else if ( ohaStatusLog == null ){
                ohaStatusLog = OhaStatusLog.getOhaStatusLog(strValue);
            }
        }
        if ( (lastSequence == -1) && ( ohaStatusLog == null ) ){
            ohaStatusLog = OhaStatusLog.OHA_REQUEST_END;
        }
        return new OhaSequenceLog(lastSequence, ohaStatusLog, durationBoardRunning);
    }
}
