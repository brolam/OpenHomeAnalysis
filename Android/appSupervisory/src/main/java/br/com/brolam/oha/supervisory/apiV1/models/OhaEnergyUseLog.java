package br.com.brolam.oha.supervisory.apiV1.models;

import android.content.Context;

import br.com.brolam.oha.supervisory.R;


/** OhaEnergyUseLog - Ler o conteúdo do Log de consumo de Energía, exemplo "1:<181900|220.00|0.00|0.33|2.36|1525422>",
 *  e assim faciliar o acesso ao valor de cada coluna através de atributos.
 *  E também aplicar verificações e garantir a integridade das informações.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseLog {

    /*****************************************************************************************
     * Constantes para facilitar o acesso ao conteúdo do log.
     ****************************************************************************************/
    private static final byte   FIELD_STR_TIME = 0;
    private static final byte   FIELD_VOLTS = 1;
    private static final byte   FIELD_AMPS_PER_PHASE_1 = 2;
    private static final byte   FIELD_AMPS_PER_PHASE_2 = 3;
    private static final byte   FIELD_AMPS_PER_PHASE_3 = 4;
    private static final byte   FIELD_DURATION = 5;
    private static final String FLAG_LOG_COLUMN = ":";
    private static final byte   FLAG_LOG_COLUMN_AMOUNT = 2;
    private static final String FLAG_LOG_BEGIN = "<";
    private static final String FLAG_LOG_END = ">";
    private static final String FLAG_LOG_COLUMN_VALUES = "\\|";
    private static final byte   FLAG_LOG_COLUMN_VALUES_COLUMNS = 6;
    private static final byte   FLAG_AMOUNT_PHASES = 3;
    /***************************************************************************************/

    /**
     * Repositório do log, corresponde a data/hora onde o log foi gravado, exemplo 2016010123.
     * Esse campo é utilizado na geração de um ID único para o log.
     */
    private long repository;

    /**
     * Sequência do log que deve ser maior ou igual a 1 para log válido ou -1 para logs inválidos.
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
     * Média de Volts registrada via sensor ou zero quando o sensor está desativado.
     */
    private double avgVolts;

    /**
     * Média de amperes lido por fase na duração do log {@see duration }
     */
    private double[] avgAmpsPerPhase;

    /**
     * Duração em milisegundo da leitura do log .
     */
    private long duration;

    /**
     * Texto com o conteúdo do log que foi analisado com erro.
     */
    private String strLogContentWithError;

    /**
     * Erro gerado na leitura do conteúdo do log.
     */
    private Exception exceptionLogContentWithError;

    /**
     * Construtor padrão e deve ser acionado em todos os construtores.
     */
    private OhaEnergyUseLog(long repository) {
        this.repository = repository;
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
     * Facilitar a construção de uma instância de {@see OhaEnergyUseLog} invalida.
     * @param repository informar o repositório válido.
     * @param sequence informar -1 se não for possível recuperar a sequência no conteúdo do log.
     * @param strDate informar um texto no formado yyyyMMdd, exemplo: "20160131".
     * @param strLogContentWithError informar o conteúdo do log que gerou o erro.
     * @param exceptionLogContentWithError informar o erro gerado na leitura do conteúdo do log.
     */
    public OhaEnergyUseLog(long repository, int sequence, String strDate, String strLogContentWithError, Exception exceptionLogContentWithError) {
        this(repository);
        this.setSequence(sequence);
        this.setStrDate(strDate);
        this.strLogContentWithError = strLogContentWithError;
        this.exceptionLogContentWithError = exceptionLogContentWithError;
    }

    /**
     * Analisar o conteúdo do log para construir um {@see OhaEnergyUseLog}
     * @param strDate informar texto com a data no formado yyyyMMdd, exemplo: "20160131".
     * @param strLogContent informar texto com o conteúdo do log, exemplo: 1:<235959|220.00|0.56|0.56|0.56|5559>
     * @return uma instância de {@see OhaEnergyUseLog} válido ou com erro {@see getExceptionLogContentWithError}.
     * @see OhaStatusLog
     */
    public static OhaEnergyUseLog parse(String strDate, String strHour, String strLogContent) {
        final int FLAG_SEQUENCE = 0; //Índice da sequência no conteúdo do log.
        final int FLAG_FIELDS = 1; //Índice das colunas no conteúdo do log.

        long repository = Long.valueOf(String.format("%s%s", strDate, strHour));
        int sequence = -1; //Valor padrão da sequência quando o log for inválido.

        OhaEnergyUseLog ohaEnergyUseLog = null;
        try {

            //Após o split, alista de strings deve ter dois itens, o primeiro com a sequência e a segundo as colunas.
            String[] strings = strLogContent.split(FLAG_LOG_COLUMN);

            if (strings.length == FLAG_LOG_COLUMN_AMOUNT) {
                sequence = Integer.valueOf(strings[FLAG_SEQUENCE]);
                String columns = strings[FLAG_FIELDS];
                /*Validar o conteúdo do log:
                  1 - verificar se o conteúdo do log inicia com a tag FLAG_LOG_BEGIN;
                  2 - verificar se o conteúdo do log finaliza com a tag FLAG_LOG_END.
                */
                if ((columns.indexOf(FLAG_LOG_BEGIN) != -1) && (columns.indexOf(FLAG_LOG_END) != -1)) {
                    String[] fields = columns
                            .replace(FLAG_LOG_BEGIN, "")
                            .replace(FLAG_LOG_END, "")
                            .split(FLAG_LOG_COLUMN_VALUES);
                    if (fields.length == FLAG_LOG_COLUMN_VALUES_COLUMNS) {
                        ohaEnergyUseLog = new OhaEnergyUseLog(repository);
                        ohaEnergyUseLog.setSequence(sequence);
                        ohaEnergyUseLog.setStrDate(strDate);
                        ohaEnergyUseLog.setStrTime(fields[FIELD_STR_TIME]);
                        ohaEnergyUseLog.setAvgVolts(Double.valueOf(fields[FIELD_VOLTS]));
                        double avgAmpsPerPhase[] = new double[]{
                                Double.valueOf(fields[FIELD_AMPS_PER_PHASE_1]),
                                Double.valueOf(fields[FIELD_AMPS_PER_PHASE_2]),
                                Double.valueOf(fields[FIELD_AMPS_PER_PHASE_3]),
                        };
                        ohaEnergyUseLog.setAvgAmpsPerPhase(avgAmpsPerPhase);
                        ohaEnergyUseLog.setDuration(Long.valueOf(fields[FIELD_DURATION]));

                    }

                }

            }
            //Sinalizar que o conteúdo do log é inválido:
            if (ohaEnergyUseLog == null) {
                    throw new Exception("Energy log is valid.");
            }
        } catch (Exception e) {
            ohaEnergyUseLog = new OhaEnergyUseLog(repository, sequence, strDate, strLogContent, e);
        }
        return ohaEnergyUseLog;
    }

    /**
     * Gerar um ID único que pode ser utilizado como chave para recuperar um log.
     * @return retornar a numero inteiro, no formato YYYYMMddHHmmSequence exemplo 2016012359000001 ou -1 se o log for inválido.
     */
    public long getId() {
        if (getSequence() < 1)
            return -1;
        else
            return Long.valueOf(String.format("%s%06d", getRepository(), getSequence()));
    }

    public long getRepository() {
        return repository;
    }

    public int getSequence() {
        return sequence;
    }

    public String getStrDate() {
        return strDate;
    }

    public String getStrTime() {
        return strTime;
    }

    public double getAvgVolts() {
        return avgVolts;
    }

    public double[] getAvgAmpsPerPhase() {
        return avgAmpsPerPhase;
    }

    public long getDuration() {
        return duration;
    }

    public String getStrLogContentWithError() {
        return strLogContentWithError;
    }

    public Exception getExceptionLogContentWithError() {
        return exceptionLogContentWithError;
    }


    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setAvgVolts(double avgVolts) {
        this.avgVolts = avgVolts;
    }

    public void setAvgAmpsPerPhase(double[] avgAmpsPerPhase) {
        this.avgAmpsPerPhase = avgAmpsPerPhase;
    }


}
