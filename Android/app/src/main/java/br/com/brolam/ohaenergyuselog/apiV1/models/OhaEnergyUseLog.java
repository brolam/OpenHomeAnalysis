package br.com.brolam.ohaenergyuselog.apiV1.models;

import android.content.Context;


/** OhaEnergyUseLog - Ler o conteúdo do Log consumo de Energía "1:<181900|220.00|0.00|0.33|2.36|1525422>",
 *  para faciliar o acesso ao valor de cada coluna através de atributos dessa class.
 *  E também aplicar verificaçõs para garantir a integridade das informações.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseLog {

    /*****************************************************************************************
     * Constantes dos atributos e separadores para facilitar o acesso aos indices
     * do conteúdo do log.
     ****************************************************************************************/
    private static final byte FIELD_STR_TIME = 0;
    private static final byte FIELD_VOLTS = 1;
    private static final byte FIELD_AMPERES_PHASE_1 = 2;
    private static final byte FIELD_AMPERES_PHASE_2 = 3;
    private static final byte FIELD_AMPERES_PHASE_3 = 4;
    private static final byte FIELD_DURATION = 5;
    private static final String FLAG_LOG_COLUMN = ":";
    private static final byte   FLAG_LOG_AMOUNT = 2;
    private static final byte   FLAG_LOG_COLUMNS = 6;
    private static final String FLAG_LOG_BEGIN = "<";
    private static final String FLAG_LOG_END = ">";
    private static final String FLAG_LOG_COLUMN_VALUES = "\\|";
    private static final byte FLAG_AMOUNT_PHASES = 3;
    /***************************************************************************************/

    /**
     * Sequência do repositório do log, corresponde a data & hora que o logo foi gerado. EX: 2016010123.
     * Esse campo deve ser utilizar para gerar a base do ID único do log.
     */
    private long sequenceRoot;

    /**
     * Sequência do log maior ou igual a 1 para log válido ou -1 para logs inválidos.
     */
    private int sequence;

    /**
     * Texto informando a data no formato yyyyMMdd, exemplo: "20160131".
     */
    private String strDate;

    /**
     * Texto informando a hora no formato HHmmss, exemplo: "085959".
     */
    private String strTime;

    /**
     * Média deVolts registrada via sensor ou Zero quando o sensor está desativado.
     */
    private double avgVolts;

    /**
     * Média de amperes lido por fase na duração do log {@see duration }
     */
    private double[] avgAmpsPerPhase;

    /**
     * Duração da leitura do log em milisegundo.
     */
    private long duration;

    /**
     * Texto com o conteúdo do log que foi processado com erro.
     */
    private String strLogContentWithError;

    /**
     * Exception gerado na leitura do conteúdo do log.
     * @see build
     */
    private Exception exceptionLogContentWithError;

    /**
     * Construtor padrão e deve ser acionado em todos os construtores.
     */
    private OhaEnergyUseLog(long sequenceRoot) {
        this.sequenceRoot = sequenceRoot;
        this.sequence = -1;
        this.strDate = "00000000";
        this.strTime = "000000";
        this.avgVolts = 0.00;
        this.avgAmpsPerPhase = new double[FLAG_AMOUNT_PHASES];
        for(byte phase = 0; phase < FLAG_AMOUNT_PHASES; phase++){
            avgAmpsPerPhase[phase] = 0.00;
        }
        this.duration = 0;
        this.strLogContentWithError = "";
        this.exceptionLogContentWithError = null;
    }


    /**
     * Facilitar a construção de uma instância de OhaEnergyUseLog com a situação(ohaStatusLog) invalida.
     * @param sequence informar -1 se não for possível recuperar a sequência no conteúdo do log.
     * @param strDate informar um texto no formado yyyyMMdd, exemplo: "20160131".
     * @param strLogContentWithError informar o conteúdo do log que gerou o erro.
     * @param exceptionLogContentWithError informar o erro gerado na leitura do conteúdo do log.
     */
    public OhaEnergyUseLog(long sequenceRoot, int sequence, String strDate, String strLogContentWithError, Exception exceptionLogContentWithError) {
        this(sequenceRoot);
        this.sequence = sequence;
        this.strDate = strDate;
        this.strLogContentWithError = strLogContentWithError;
        this.exceptionLogContentWithError = exceptionLogContentWithError;
    }


    /**
     * Construi uma instância da class OhaEnergyUseLog lendo o conteúdo do Log.
     * @param context informar o contexto da tela ou serviço.
     * @param strDate informar texto com a data no formado yyyyMMdd, exemplo: "20160131".
     * @param strLogContent informar texto com o conteúdo do log, exemplo: 1:<235959|220.00|0.56|0.56|0.56|5559>
     * @return retornar com uma instância de OhaEnergyUseLog válido e atributos preenchido ou uma instância de OhaEnergyUseLog com
     * o campo ohaStatusLog preenchido sinalizando algum problema na leitura do conteúdo do log.
     * @see OhaStatusLog
     */
    public static OhaEnergyUseLog build(Context context, String strDate, String strHour, String strLogContent) {
        final int I_SEQUENCE = 0; //indice para recuperar a sequência no conteúdo do log.
        final int I_FIELDS = 1; //indice para recuperar as colunas no conteúdo do log.

        long sequenceRoot = Long.valueOf(String.format("%s%s", strDate, strHour));
        int sequence = -1; //Valor padrão da sequência quando o log for inválido.

        OhaEnergyUseLog ohaEnergyUseLog = null;
        try {

            //Após o split o array strings deve conter 2 colunas, a primeira com a sequência do log e a segunda coluna com os atributos do
            //log separadas por DIVISOR_LOG_VALUES.
            String[] strings = strLogContent.split(FLAG_LOG_COLUMN);

            if (strings.length == DIVISOR_LOG_AMOUNT) {
                sequence = Integer.valueOf(strings[I_SEQUENCE]);
                //Para validar o conteúdo do log, é necessário verificar se o conteúdo do log inicia com a tag TAG_LOG_START e é
                //finalizado com a tag TAG_LOG_END e também deve ter a quantiade de DIVISOR_LOG_COLUMNS.
                if ((strings[I_FIELDS].indexOf(TAG_LOG_START) != -1) && (strings[I_FIELDS].indexOf(TAG_LOG_END) != -1)) {
                    String[] fields = strings[I_FIELDS].replace(TAG_LOG_START, "").replace(TAG_LOG_END, "").split(DIVISOR_LOG_VALUES);
                    if (fields.length == DIVISOR_LOG_COLUMNS) {
                        ohaEnergyUseLog = new OhaEnergyUseLog(sequenceRoot);
                        ohaEnergyUseLog.sequence = sequence;
                        ohaEnergyUseLog.strDate = strDate;
                        ohaEnergyUseLog.strTime = fields[STR_TIME];
                        ohaEnergyUseLog.voltage = Double.valueOf(fields[VOLTAGE]);
                        ohaEnergyUseLog.currentPhase1 = Double.valueOf(fields[CURRENT_PHASE_1]);
                        ohaEnergyUseLog.currentPhase2 = Double.valueOf(fields[CURRENT_PHASE_2]);
                        ohaEnergyUseLog.currentPhase3 = Double.valueOf(fields[CURRENT_PHASE_3]);
                        ohaEnergyUseLog.pastMillis =  Long.valueOf(fields[PAST_MILLIS]);
                        ohaEnergyUseLog.ohaStatusLog = OhaStatusLog.VALID;
                    }

                }

            }

            /**
             * Se não for possível validar o log, e necessário verificar se o conteúdo do log é somente um OhaStatusLog
             * com a situação do retorno da API ou se ocorreu algum erro na leitura do conteúdo do log.
             * @see OhaStatusLog
             * @see OhaEnergyUseApi */
            if (ohaEnergyUseLog == null) {
                OhaStatusLog ohaStatusLog = OhaStatusLog.getOhaStatusLog(strLogContent);

                if (ohaStatusLog != null) {
                    ohaEnergyUseLog = new OhaEnergyUseLog(sequenceRoot,ohaStatusLog);
                } else {
                    throw new Exception(context.getString(R.string.exception_log_content_invalid));
                }
            }
        } catch (Exception e) {
            ohaEnergyUseLog = new OhaEnergyUseLog(sequenceRoot, sequence, strDate, strLogContent, OhaStatusLog.INVALID, e);
        }
        return ohaEnergyUseLog;

    }

    /**
     * ID único do Log de Utilização de Energia concatenando a sequenceRoot & sequence.
     * @return será retornado a numero inteiro, exemplo 201601011 ou -1 se não for possível recupera a sequência do log.
     */
    public long getId() {
        if (getSequence() < 1)
            return -1;
        else
            return Long.valueOf(String.format("%s%06d", getSequenceRoot(), getSequence()));
    }



}
