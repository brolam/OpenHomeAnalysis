package br.com.brolam.ohaenergyuselog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.ohaenergyuselog.apiV1.OhaEnergyUseApi;
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
    private static final String OHA_HOST_NAME = "192.168.0.7";

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
        assertNotNull("The method OhaEnergyUseApi.setStatus returned to null!", ohaStatusLog);
        assertTrue("The status has not been configured successfully.", ohaStatusLog == OhaStatusLog.OHA_REQUEST_END);
    }
}
