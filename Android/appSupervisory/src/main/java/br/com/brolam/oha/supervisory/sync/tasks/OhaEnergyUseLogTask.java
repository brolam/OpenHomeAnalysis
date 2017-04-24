package br.com.brolam.oha.supervisory.sync.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.apiV1.OhaEnergyUseApi;
import br.com.brolam.oha.supervisory.apiV1.models.OhaEnergyUseLog;
import br.com.brolam.oha.supervisory.apiV1.models.OhaStatusLog;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract;
import br.com.brolam.oha.supervisory.ui.helpers.OhaEnergyUseSyncHelper;
import static br.com.brolam.oha.supervisory.OhaException.*;

/**
 * Realizar a importação dos logs de utilização de energia e monitorar o funcionamento
 * do Registrador de Utilização de Energia.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseLogTask {
    private static final String TAG = "OhaEnergyUseLogTask";
    //Definir a duração máxima em segundo de um Log de utilização de energia:
    private static final int MAX_DURATION_LOG = 35;
    //Definir a duração padrão se não for possível recuperar a duração real do Log:
    private static final double DEFAULT_DURATION =  9.9; // Segundos.
    //Definir a quantidade de tentativas para realizar uma requisição(GET ou POST) com sucesso
    //no Registrador de Utilização de Energiar:
    private static final int NUMBER_ATTEMPTS = 3;
    //Ajudante para recuperar os parâmetros de preferência na sincronização dos logs.
    private OhaEnergyUseSyncHelper ohaEnergyUseSyncHelper;
    //Informar se a sincronização está em execuão ou parada:
    private boolean flagRunning;

    IOhaTask iOhaTask;

    public OhaEnergyUseLogTask(IOhaTask iOhaTask) {
        super();
        this.iOhaTask = iOhaTask;
        this.flagRunning = false;
        this.ohaEnergyUseSyncHelper = new OhaEnergyUseSyncHelper(iOhaTask.getContext(), iOhaTask.getPreferences());
    }

    /**
     * Executar o fluxo principal da sincronização.
     */
    public void execute() {
        this.flagRunning = true;
        //Executar até a sincronização estiver ligada {@link SettingsActivity.EnergyUsePreferenceFragment}
        // e se a sincronização estiver em execução:
        while (!ohaEnergyUseSyncHelper.isSetupMode() && flagRunning) {
            try {
                //Atualizar o status e data hora do Registrador de Utilização de Energia.
                setStatus();
                //Recupear o endereço do Registrador, e data hora que deve ser sincronizada:
                String hostName = ohaEnergyUseSyncHelper.getHostName();
                String strDate = ohaEnergyUseSyncHelper.getStrDate();
                String strHour = ohaEnergyUseSyncHelper.getStrHour();
                //Recuperar o status para a data hora que deve ser sincronizada:
                OhaStatusLog ohaStatusLog = getOhaStatusLog(hostName, strDate, strHour);
                //Atualizar a data hora de sincronização se a data hora informada
                //não existir:
                if (ohaStatusLog == OhaStatusLog.LOG_DATE_NOT_EXISTS) {
                    setNextOhaSequenceLog();
                } if ( ohaStatusLog.getDurationBoardRunning() > ohaEnergyUseSyncHelper.getOftenLoggerResetMillisecond() ) {
                    tryResetEnergyUseLogger();
                    delay(10 * DateUtils.SECOND_IN_MILLIS );
                } else {
                    //Executar até existir logs disponíveis para a data hora informada:
                    while (doImport(ohaStatusLog, hostName, strDate, strHour)) {
                        delay(15 * DateUtils.SECOND_IN_MILLIS);
                    }
                }
                //O Registrador não é multitarefas, dessa forma, e necessário executar delays
                //no fluxo de sincronização para não parar os registros de utilização de energia/
                // por muito tempo:
                delay(DateUtils.SECOND_IN_MILLIS * 15);
            } catch (EnergyUseLogSdCardFatalError e) {
                //Reiniciar o Registrador de Utilização de Energia para tentar corrigir o problema
                //no SD Card do Registrador.
                tryResetEnergyUseLogger();
                Log.e(TAG, e.toString());
            } catch (EnergyUseRequestTimeOut e) {
                //Reiniciar o Registrador de Utilização de Energia para tentar corrigir o problema
                //de comunicação entre o Registrador(Arduino) e o Módulo WiFI(ESP8266).
                tryResetEnergyUseLogger();
                Log.e(TAG, e.toString());
            } catch (EnergyUseLogSyncFatalError e) {
                //Desligar a sincronização quando ocorrer um erro fatal
                ohaEnergyUseSyncHelper.setSetupModeOn();
                Log.e(TAG, e.toString());
            } catch (BackupAndRestoreOperation e){
                Log.e(TAG, e.toString());
                break;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        this.flagRunning = false;
    }

    /**
     * Atualizar o status e data hora do Registrador de Utilização de Energia
     * com a data e hora atual.
     * @throws IOException se o Registrador não for localizado na Rede.
     */
    private void setStatus() throws IOException {
        Date date = new Date();
        String strDate = OhaHelper.getStrDate(date);
        String strTime = OhaHelper.getStrTime(date);
        OhaEnergyUseApi.setStatus(ohaEnergyUseSyncHelper.getHostName(), strDate, strTime);
        //O Registrador não é multitarefas, dessa forma, e necessário executar delays
        //no fluxo de sincronização para não parar os registros de utilização de energia/
        // por muito tempo:
        delay(DateUtils.SECOND_IN_MILLIS * 5);
    }

    /**
     * Realizar a importação dos logs de utilização de energia.
     * @param ohaStatusLog informar o OhaStatusLog.OHA_STATUS_RUNNING ou OhaStatusLog.OHA_STATUS_FINISHED
     * @param hostName informar o enderço do Registrador na rede.
     * @param strDate informar uma data válida.
     * @param strHour informar uma hora válida.
     * @return verdadeiro se ainda é possível importar mais logs.
     * @throws IOException se o Registrador de Utilização de Energia não for localizado na Rede.
     * @throws ParseException se não for possível converter o parâmetro strDate em uma data válida.
     * @throws EnergyUseLogSyncFatalError se a data e hora informada for maior do que a data e hora atual.
     * @throws EnergyUseLogRead se ocorrer erro na leitura dos logs.
     * @throws EnergyUseLogSdCardFatalError se ocorrer qualquer problema no SD Card do Registrador.
     * @throws EnergyUseRequestTimeOut se ocorrer problema de comunicação entre o Registrador(Arduino) e o Módulo WiFi(ESP8266).
     * @throws InterruptedException se ocorrer erro na requisição de delay.
     */
    private boolean doImport(OhaStatusLog ohaStatusLog, String hostName, String strDate, String strHour)
            throws
            IOException,
            ParseException,
            EnergyUseLogSyncFatalError,
            EnergyUseLogRead,
            EnergyUseLogSdCardFatalError,
            EnergyUseRequestTimeOut,
            InterruptedException,
            BackupAndRestoreOperation{
        //Recuperar a última sequencia importada:
        int startSequence = ohaEnergyUseSyncHelper.getSequence();
        //Informar se é permitido importar os logs mesmo se a sequencia do log estiver quebrada,
        //se em todas as tentavidas essa quebra de seguencia nao for corrigida.
        boolean allowSequenceBroken = false;
        //Realizar as tentativas:
        List<String> energyUseLogs = new ArrayList<>();
        for (int tryCount = 1; tryCount <= NUMBER_ATTEMPTS; tryCount++) {
            energyUseLogs = getLogs(hostName, strDate, strHour, startSequence);
            try {
                //Validar o contúdo dos logs:
                ContentValues[] energyLogsValues = parseEnergyLogs(strDate, strHour, energyUseLogs, allowSequenceBroken);
                if (energyLogsValues.length > 0) {
                    //Salvar os logs no banco de dados:
                    Context context = ohaEnergyUseSyncHelper.getContext();
                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.bulkInsert(OhaEnergyUseContract.CONTENT_URI_LOG, energyLogsValues);
                    //Atualizar a última sequencia importada:
                    int lastSequenceSaved = energyLogsValues[energyLogsValues.length - 1].getAsInteger(OhaEnergyUseContract.EnergyUseLogEntry.COLUMN_SEQUENCE);
                    ohaEnergyUseSyncHelper.setSequence(lastSequenceSaved);
                    break;
                }
            } catch (EnergyUseLogRead e) {
                Log.e(TAG, e.toString());
            } catch (EnergyUseLogReadSequenceBroken e) {
                Log.e(TAG, e.toString());
            }
            //Permitir salvar os logs mesmo se a sequencia for quebrada na última tentativa:
            allowSequenceBroken = tryCount == (NUMBER_ATTEMPTS - 1);
        }
        //Se a geração de logs para a data e hora informada já foi finalizada(OHA_STATUS_FINISHED)
        //e já foi importados todos os logs(LOG_FILE_NOT_EXISTS), atualizar as preferências de sincronização
        //com a próxima data e hora.
        if (OhaStatusLog.exists(OhaStatusLog.LOG_FILE_NOT_EXISTS, energyUseLogs)) {
            if (ohaStatusLog == OhaStatusLog.OHA_STATUS_FINISHED) {
                setNextOhaSequenceLog();
                return false;
            }
        } else if ( OhaStatusLog.exists(OhaStatusLog.LOG_DATE_NOT_EXISTS, energyUseLogs)) {
            //Retornar a falso se data e hora informada não existir:
            return false;
        }
        //Retornar a verdadeiro se todos os logs ainda não foram importados:
        return true;
    }

    /**
     * Recuperar o status do Registrador de Utilização de Energia para a data hora informada.
     * @param hostName informar o enderço do Registrador na rede.
     * @param strDate informar uma data válida.
     * @param strHour informar uma hora válida.
     * @return um {@link OhaStatusLog}
     * @throws IOException se o Registrador de Utilização de Energia não for localizado na Rede.
     * @throws EnergyUseLogRead se ocorrer erro na leitura dos logs.
     * @throws EnergyUseLogSdCardFatalError se ocorrer qualquer problema no SD Card do Registrador.
     * @throws EnergyUseRequestTimeOut se ocorrer problema de comunicação entre o Registrador(Arduino) e o Módulo WiFi(ESP8266).
     * @throws InterruptedException se ocorrer erro na requisição de delay.
     */
    private OhaStatusLog getOhaStatusLog(String hostName, String strDate, String strHour)
            throws
            IOException,
            EnergyUseLogRead,
            EnergyUseLogSdCardFatalError,
            EnergyUseRequestTimeOut,
            InterruptedException,
            BackupAndRestoreOperation {
        List<String> strings = new ArrayList<>();
        //Realizar as tentativas para recuperar o status
        for (int tryCount = 1; tryCount <= NUMBER_ATTEMPTS; tryCount++) {
            strings = OhaEnergyUseApi.getStatus(hostName, strDate, strHour);
            //Se não existir logs para a data hora informada, encerrar as tentarivas
            //e informar para o fluxo principal que não existe logs para a data hora informada:
            if ( OhaStatusLog.exists(OhaStatusLog.LOG_DATE_NOT_EXISTS, strings)){
                return OhaStatusLog.LOG_DATE_NOT_EXISTS;
            }
            //Verificar se o status retornado é válido:
            assertOhaStatusLog(strings, tryCount);
            //Verificar se a requisição foi concluída com sucesso:
            if (OhaStatusLog.exists(OhaStatusLog.OHA_REQUEST_END, strings)) {
                //Localizar os status OHA_STATUS_RUNNING ou OHA_STATUS_FINISHED:
                for (String ohaStatusLogContent : strings) {
                    OhaStatusLog ohaStatusLog = OhaStatusLog.getOhaStatusLog(ohaStatusLogContent);
                    if (ohaStatusLog == OhaStatusLog.OHA_STATUS_RUNNING || ohaStatusLog == OhaStatusLog.OHA_STATUS_FINISHED) {
                        //Encerrar as tentativas e retornar com o OhaStatusLog localizado:
                        return ohaStatusLog;
                    }
                }
            }
            //Executar um delay e tentar novamente:
            delay(DateUtils.SECOND_IN_MILLIS * 10);
        }
        throw new EnergyUseLogRead("The OhaStatusLog content is invalid!");
    }

    /**
     * Recuperar os logs de utilização de energia.
     * @param hostName informar o enderço do Registrador na rede.
     * @param strDate informar uma data válida.
     * @param strHour informar uma hora válida.
     * @param startSequence informar a ultima sequencia importada.
     * @return uma lista com conteúdo de {@link OhaEnergyUseLog} ou {@link OhaStatusLog}
     * @throws ParseException se não for possível converter o parâmetro strDate em uma data válida.
     * @throws IOException se o Registrador de Utilização de Energia não for localizado na Rede.
     * @throws EnergyUseLogRead se ocorrer erro na leitura dos logs.
     * @throws EnergyUseLogSdCardFatalError se ocorrer qualquer problema no SD Card do Registrador.
     * @throws EnergyUseRequestTimeOut se ocorrer problema de comunicação entre o Registrador(Arduino) e o Módulo WiFi(ESP8266).
     * @throws InterruptedException se ocorrer erro na requisição de delay.
     */
    private List<String> getLogs(String hostName, String strDate, String strHour, int startSequence)
            throws
            ParseException,
            IOException,
            EnergyUseLogRead,
            EnergyUseLogSdCardFatalError,
            EnergyUseRequestTimeOut,
            InterruptedException,
            BackupAndRestoreOperation{
        //Definir a data de exclusão dos logs para liberar espaço no
        //SD Card do Registrador de Utilização de Energia:
        Calendar calendar = OhaHelper.getCalendar(strDate);
        calendar.add(Calendar.DATE, -1);
        String strDateLogDelete = OhaHelper.getStrDate(calendar.getTime());
        //Realizar as tentativas:
        List<String> strings = new ArrayList<>();
        for (int tryCount = 1; tryCount <= NUMBER_ATTEMPTS; tryCount++) {
            strings = OhaEnergyUseApi.getLogs(hostName, strDate, strHour, startSequence, 25, strDateLogDelete);
            //Verificar se o status retornado é válido:
            assertOhaStatusLog(strings, tryCount);
            if (OhaStatusLog.exists(OhaStatusLog.OHA_REQUEST_END, strings)) {
                break;
            }
            //Executar um delay e tentar novamente:
            delay(DateUtils.SECOND_IN_MILLIS * 10);
        }
        //Se existir somente o status OHA_REQUEST_END, a lista não tem conteúdo de logs e está vazia!
        if ( strings.size() < 1){
            new EnergyUseLogRead(String.format("getLogs on %s, %s and %s is empty!", strDate, strHour, startSequence));
        }
        return strings;
    }

    /**
     * Validar uma lista de logs de utilização de energia.
     * @param strDate informar uma data válida.
     * @param strHour informar uma hora válida.
     * @param energyUseLogs informar uma lista com os conteúdos dos logs.
     * @param allowSequenceBroken infomrar se é permitido validar mesmo se a sequencia de logs for quebrada.
     * @return uma lista de {@link ContentValues} com o conteúdo dos logs.
     * @throws ParseException se não for possível converter o parâmetro strDate em uma data válida.
     * @throws EnergyUseLogRead se ocorrer erro na leitura dos logs.
     * @throws EnergyUseLogReadSequenceBroken se a sequencia dos logs for quebrada.
     */
    private ContentValues[] parseEnergyLogs(String strDate, String strHour, List<String> energyUseLogs, boolean allowSequenceBroken)
            throws
            ParseException,
            EnergyUseLogRead,
            EnergyUseLogReadSequenceBroken {
        //Informar a última data e hora validada.
        Date previousDateTime = null;
        //Informar a última sequencia validada.
        int previousSequence = -1;
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
        for (String strLogContent : energyUseLogs) {
            OhaEnergyUseLog ohaEnergyUseLog = OhaEnergyUseLog.parse(strDate, strHour, strLogContent);
            //Se não existir erro na validação do log:
            if (ohaEnergyUseLog.getExceptionLogContentWithError() == null) {
                long id = ohaEnergyUseLog.getId();
                int sequence = ohaEnergyUseLog.getSequence();
                //Verificar se a sequencia foi quebrada:
                if ((previousSequence > 0) && (previousSequence + 1 != sequence)) {
                    if (allowSequenceBroken)
                        break;
                    else
                        throw new EnergyUseLogReadSequenceBroken(previousSequence, sequence);
                }
                //Recuperar o conteúdo do log:
                double volts = ohaEnergyUseSyncHelper.getDefaultVolts(ohaEnergyUseLog.getAvgVolts());
                Calendar dateTime = OhaHelper.getCalendar(ohaEnergyUseLog.getStrDate(), ohaEnergyUseLog.getStrTime(), ohaEnergyUseLog.getDuration());
                double duration = getDurationBetweenLogs(previousDateTime, dateTime);
                double watts1 = volts * ohaEnergyUseLog.getAvgAmpsPerPhase()[0];
                double watts2 = volts * ohaEnergyUseLog.getAvgAmpsPerPhase()[1];
                double watts3 = volts * ohaEnergyUseLog.getAvgAmpsPerPhase()[2];
                ContentValues values = OhaEnergyUseContract.EnergyUseLogEntry.parse(
                        id,
                        sequence,
                        dateTime.getTime(),
                        duration,
                        volts,
                        watts1,
                        watts2,
                        watts3
                );
                valuesArrayList.add(values);
                //Atualizar a última data, hora e sequencia validada:
                previousDateTime = dateTime.getTime();
                previousSequence = sequence;
            } else {
                //Se for o final da lista de conteúdos de logs:
                if ((OhaStatusLog.getOhaStatusLog(strLogContent) != null)) {
                    break; //Validação realizad com sucesso.
                } else {//Se o conteúdo do log for inválido:
                    if (allowSequenceBroken)
                        break;
                    else
                        throw new EnergyUseLogRead(ohaEnergyUseLog.getStrLogContentWithError());
                }
            }
        }
        //Retonar com a lista de logs validados:
        ContentValues[] resultContentValues = new ContentValues[valuesArrayList.size()];
        valuesArrayList.toArray(resultContentValues);
        return resultContentValues;

    }

    /**
     * Recuperar a duração em segundos entre a data de registro de dois logs de utilização de energia.
     * @param previous data de registro do log anterior
     * @param current  data de registro do log atual.
     * @return duração em segungos.
     */
    private double getDurationBetweenLogs(Date previous, Calendar current) {
        if (previous != null) {
            double seconds = (current.getTimeInMillis() - previous.getTime()) / 1000.00;
            if ((seconds > 0.00) && (seconds <= MAX_DURATION_LOG)) {
                return seconds;
            }
        }
        return DEFAULT_DURATION;
    }

    /**
     * Atualizar a próxima sequencia de importação dos logs nas preferências de sincronização.
     * @throws ParseException se não for possível converter o parâmetro strDate em uma data válida.
     * @throws EnergyUseLogSyncFatalError se a data e hora informada for maior do que a data e hora atual.
     */
    private void setNextOhaSequenceLog()
            throws
            ParseException,
            EnergyUseLogSyncFatalError {
        Calendar calendar = OhaHelper.getCalendar(ohaEnergyUseSyncHelper.getStrDate(), ohaEnergyUseSyncHelper.getStrHour());
        calendar.add(Calendar.HOUR, 1);
        if (calendar.after(new Date())) {
            throw new EnergyUseLogSyncFatalError("Do not set the import period greater than the current date!");
        }
        ohaEnergyUseSyncHelper.setDate(OhaHelper.getStrDate(calendar.getTime()));
        ohaEnergyUseSyncHelper.setHour(OhaHelper.getStrHour(calendar.getTime()));
        ohaEnergyUseSyncHelper.setSequence(1);
    }

    /**
     * Reiniciar o Registrador de Utilização de Energia
     */
    private void tryResetEnergyUseLogger() {
        try {
            for (int tryCount = 1; tryCount < NUMBER_ATTEMPTS; tryCount++) {
                OhaStatusLog ohaStatusLog = OhaEnergyUseApi.reset(this.ohaEnergyUseSyncHelper.getHostName());
                if (ohaStatusLog == OhaStatusLog.OHA_REQUEST_END) break;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Validar o OhaStatusLog retornado nas requisição realizadas no Registrador de Utilização de Energia.
     * @param strings list com o conteúdo do OhaStatusLog.
     * @param tryCount quantidade de tentativas para o mesmo tipo de validação.
     * @throws EnergyUseLogRead se ocorrer erro na leitura dos logs.
     * @throws EnergyUseLogSdCardFatalError se ocorrer qualquer problema no SD Card do Registrador.
     * @throws EnergyUseRequestTimeOut se ocorrer problema de comunicação entre o Registrador(Arduino) e o Módulo WiFi(ESP8266).
     */
    private void assertOhaStatusLog(List<String> strings, int tryCount)
            throws
            EnergyUseLogSdCardFatalError,
            EnergyUseLogRead,
            EnergyUseRequestTimeOut,
            BackupAndRestoreOperation {

        if ( this.iOhaTask.isBackupAndRestoreOperation()){
            throw new BackupAndRestoreOperation("Energy Use sync was forced to stop, because backup or restore operation!");
        } else if ((!flagRunning) || (this.ohaEnergyUseSyncHelper.isSetupMode())) {
            throw new EnergyUseLogRead("Energy Use sync was forced to stop!");
        } else if (OhaStatusLog.exists(OhaStatusLog.OHA_STATUS_NOT_SD, strings)) {
            throw new EnergyUseLogSdCardFatalError("SD Card is not available on Energy Use Looger");
        } else if (OhaStatusLog.exists(OhaStatusLog.OHA_STATUS_NOT_DATE, strings)) {
            throw new EnergyUseLogRead("Date and Hour are not set on OHA Energy Use Logger");
        } else if (OhaStatusLog.exists(OhaStatusLog.OHA_REQUEST_TIMEOUT, strings)) {
            if (tryCount > 2)
                throw new EnergyUseRequestTimeOut("Energy Use Logger is not respond to WIFI module.");
        }
    }

    /**
     * Realizar um delay na linha de execução.
     */
    private void delay(long milliseconds) {
        synchronized (this) {
            try {
                wait(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Informar se a sincronização está em execução.
     * @return
     */
    public boolean isRunning() {
        return flagRunning;
    }

    /**
     * Soliciar o encerramento da sincronização.
     */
    public void stop() {
        this.flagRunning = false;
    }
}