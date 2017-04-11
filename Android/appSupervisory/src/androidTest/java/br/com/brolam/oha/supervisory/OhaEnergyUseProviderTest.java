package br.com.brolam.oha.supervisory;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.data.OhaEnergyUseContract;

import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Realizar testes no EnergyUseProvider.
 * consumo de energia.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
@RunWith(AndroidJUnit4.class)
public class OhaEnergyUseProviderTest {
    /**
     * Caso de teste -  Consumo de Energia no Dia:
     *  - Incluir uma simulação da utilização 24KWH no dia 01/01/2015.
     *  - Realizar um consulta e testar os seguintes resultados:
     *    - Data Inicio : 01/01/2015
     *    - Duração: 24Hs
     *    - KWH : 24.00
     */
    @Test
    public void caseEnergyUseDay() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Calendar dateBegin  =  Calendar.getInstance();
        //Utilização de energia no dia 01/01/2015.
        dateBegin.set(2015,1,1,0,0,0);
        dateBegin.set(Calendar.MILLISECOND,0);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(OhaHelper.getDateEnd(dateBegin.getTime(), false));
        Calendar beginHour = Calendar.getInstance();
        beginHour.setTime(dateBegin.getTime());
        //Realizar a inclusão:
        while (beginHour.before(endDate)){
            ContentValues[] contentValues = new ContentValues[360];
            for(int index = 0; index < contentValues.length; index++ ) {
                long id = Long.parseLong(String.format("%s%s%06d", OhaHelper.getStrDate(beginHour.getTime()), OhaHelper.getStrHour(beginHour.getTime()), index));
                //Gerar um consuno de 1000 Watts a cada 10 segundos.
                contentValues[index] = EnergyUseLogEntry.parse(
                        id,
                        index,
                        beginHour.getTime(),
                        10,
                        220.00,
                        400.00,
                        400.00,
                        200.00
                );
                beginHour.add(Calendar.MILLISECOND, 10000);
            }
            contentResolver.bulkInsert(
                    CONTENT_URI_LOG,
                    contentValues);

        }
        //Realizar a Consulta:
        String selection = String.format("%s BETWEEN ? AND ?", EnergyUseLogEntry.COLUMN_DATE_TIME);
        String[] selectionArgs = new String[]{
                String.valueOf(OhaHelper.getDateBegin(endDate.getTime()).getTime()),
                String.valueOf(endDate.getTime().getTime())
        };
        Cursor cursor = context.getContentResolver().query(
                OhaEnergyUseContract.CONTENT_URI_LOG_DAY,
                null,
                selection,
                selectionArgs,
                null
        );

        assertTrue("Energy Logs is empty!", cursor.moveToFirst());
        long longBeginDate = cursor.getLong(EnergyUseLogEntry.INDEX_COLUMN_CALC_DATE_TIME_MIN);
        assertEquals("Begin Date", dateBegin.getTime().getTime(), longBeginDate);
        Double duration = cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_DURATION_SUN);
        assertEquals("Duration", DateUtils.DAY_IN_MILLIS / 1000.00, duration, 0.00);
        Double totalKWH = OhaHelper.convertWhToKWH(cursor.getDouble(EnergyUseLogEntry.INDEX_COLUMN_CALC_WH_TOTAL_SUN));
        assertEquals("Total KWH", "24.00", OhaHelper.formatNumber(totalKWH,"#0.00"));
    }
}

