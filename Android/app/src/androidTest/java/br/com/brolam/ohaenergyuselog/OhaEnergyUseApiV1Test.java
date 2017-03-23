package br.com.brolam.ohaenergyuselog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.ohaenergyuselog.apiV1.OhaEnergyUseApi;
import br.com.brolam.ohaenergyuselog.apiV1.models.OhaConnectionStatus;
import br.com.brolam.ohaenergyuselog.apiV1.models.OhaEnergyUseLog;
import br.com.brolam.ohaenergyuselog.apiV1.models.OhaSequenceLog;
import br.com.brolam.ohaenergyuselog.apiV1.models.OhaStatusLog;

import static org.junit.Assert.*;

/**
 * Realizar testes nos métodos da {@link OhaEnergyUseApi} conectado ao Registrador de Logs de
 * consumo de energia.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
@RunWith(AndroidJUnit4.class)
public class OhaEnergyUseApiV1Test {
    private static final String TAG = "OHA-Test";
    private static final String OHA_HOST_NAME = "192.168.0.14";

    @Test
    public void setStatus() throws Exception {
        OhaStatusLog ohaStatusLog = null;
        //Realizar até 3 tentativas:
        for (int tryCount = 0; tryCount < 3; tryCount++) {
            Date date = new Date();
            String strDate = OhaHelper.getStrDate(date);
            String strTime = OhaHelper.getStrTime(date);
            ohaStatusLog = OhaEnergyUseApi.setStatus(OHA_HOST_NAME, strDate, strTime);
            //Parar com as tentativas se houver sucesso.
            if (ohaStatusLog == OhaStatusLog.OHA_REQUEST_END) break;
        }
        assertNotNull("The method OhaEnergyUseApi.setStatus returned null!", ohaStatusLog);
        assertTrue("The status has not been configured successfully.", ohaStatusLog == OhaStatusLog.OHA_REQUEST_END);
    }

    @Test
    public void getStatus() throws Exception {
        Date date = new Date();
        String strDate = OhaHelper.getStrDate(date);
        String strHour = OhaHelper.getStrHour(date);
        List<String> strings = OhaEnergyUseApi.getStatus(OHA_HOST_NAME, strDate, strHour);
        OhaSequenceLog ohaSequenceLog = OhaSequenceLog.parse(strings);
        boolean ohaStatusLogValid = (ohaSequenceLog.getOhaStatusLog() == OhaStatusLog.OHA_STATUS_RUNNING) || (ohaSequenceLog.getOhaStatusLog() == OhaStatusLog.OHA_STATUS_FINISHED);
        assertTrue(String.format("The OhaStatusLog %s  is not valid!", ohaSequenceLog.getOhaStatusLog()), ohaStatusLogValid);
    }

    @Test
    public void getLogs() throws Exception {
        Calendar calendar = Calendar.getInstance();
        String strDate = OhaHelper.getStrDate(calendar.getTime());
        String strHour = OhaHelper.getStrHour(calendar.getTime());
        //Definir o dia de exclusão no registrador de logs.
        calendar.add(Calendar.DATE, 1);
        String strDateDelete = OhaHelper.getStrDate(calendar.getTime());
        List<String> strings = OhaEnergyUseApi.getLogs(OHA_HOST_NAME, strDate, strHour, 1, 100, strDateDelete);
        assertTrue("The OhaEnergyUseApi.getLogs is empty!", strings.size() > 2);
        Context context = InstrumentationRegistry.getTargetContext();
        String penultStrLogContent = strings.get(strings.size() -2); //Recuperar o antepenúltimo item da lista.
        String lastStrLogContent = strings.get(strings.size() -1); //Recuperar o ultimo item da lista.
        for(String strLogContent:strings ) {
            if (lastStrLogContent.equals(strLogContent) == false) {
                OhaEnergyUseLog ohaEnergyUseLog = OhaEnergyUseLog.parse(context, strDate, strHour, strLogContent);
                OhaStatusLog ohaStatusLog = null;
                //Verificar se o penúltimo conteúdo é um OhaStatusLog:
                if (penultStrLogContent.equals(strLogContent)){
                    ohaStatusLog = OhaStatusLog.getOhaStatusLog(penultStrLogContent);
                }
                assertFalse(String.format("The content log: %s error: %s ", strLogContent, ohaEnergyUseLog.getExceptionLogContentWithError()), ohaEnergyUseLog.getExceptionLogContentWithError() != null && ohaStatusLog == null );
            } else {
                OhaStatusLog ohaStatusLog = OhaStatusLog.getOhaStatusLog(lastStrLogContent);
                assertTrue("The getLogs request was not completed with successfully!", ohaStatusLog == OhaStatusLog.OHA_REQUEST_END);
            }
        }
    }

    @Test
    public void getConnection() throws Exception {
        List<String> strings = OhaEnergyUseApi.getConnection(OHA_HOST_NAME);
        OhaConnectionStatus ohaConnectionStatus = OhaConnectionStatus.parse(strings);
        assertNotNull("The method OhaEnergyUseApi.getConnection returned null!", ohaConnectionStatus);
    }

    @Test
    public void reset() throws Exception {
        OhaStatusLog ohaStatusLog = OhaEnergyUseApi.reset(OHA_HOST_NAME);
        assertTrue("The reset request was not completed with successfully!", ohaStatusLog == OhaStatusLog.OHA_REQUEST_END);
    }

    @Test
    public void ohaEnergyUseApiAllMethods() throws Exception {
        int tenSeconds = 10000;
        setStatus();
        synchronized(this){
            this.wait(tenSeconds);
        }
        getStatus();
        synchronized(this){
            this.wait(tenSeconds);
        }
        getConnection();
        synchronized(this){
            this.wait(tenSeconds);
        }
        getLogs();
    }
}
