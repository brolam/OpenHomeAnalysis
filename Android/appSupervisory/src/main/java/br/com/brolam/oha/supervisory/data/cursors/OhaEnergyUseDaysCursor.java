package br.com.brolam.oha.supervisory.data.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import java.util.Date;
import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.data.OhaSQLHelper;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;

/**
 * Recupear a utilização de energia por dia, {@link EnergyUseLogEntry}
 * na ordem decrescente.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseDaysCursor extends AbstractCursor {

    private OhaSQLHelper ohaSQLHelper;
    Cursor cursorEnergyUseDay;
    Double kWhCost = 0.00;

    public OhaEnergyUseDaysCursor(OhaSQLHelper ohaSQLHelper, Cursor cursor){
        this.ohaSQLHelper = ohaSQLHelper;
        this.cursorEnergyUseDay = cursor;
    }

    /**
     * Recupear a utilização de energia para a nova posição do cursor na ordem decrescente.
     * @param oldPosition
     * @param newPosition
     * @return
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        this.cursorEnergyUseDay.moveToPosition(newPosition);
        this.kWhCost = 0.65; //TODO EnergyBill - Informar o custo por KWH.
        return  super.onMove(oldPosition, newPosition) ;
    }

    @Override
    public int getCount() {
        return this.cursorEnergyUseDay !=  null? cursorEnergyUseDay.getCount():0;
    }

    @Override
    public String[] getColumnNames() {
        assertCursorEnergyUseDay();
        return this.getColumnNames();
    }

    @Override
    public String getString(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getString(i);
    }

    @Override
    public short getShort(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getShort(i);
    }

    @Override
    public int getInt(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getInt(i);
    }

    @Override
    public long getLong(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getLong(i);
    }

    @Override
    public float getFloat(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        if ( i == EnergyUseLogEntry.INDEX_COLUMN_SUM_KWH_COST)
            return kWhCost;
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.getDouble(i);
    }

    @Override
    public boolean isNull(int i) {
        assertCursorEnergyUseDay();
        return this.cursorEnergyUseDay.isNull(i);
    }

    /**
     * Validar a situação do cursor:
     */
    private void assertCursorEnergyUseDay() {
        if ((this.cursorEnergyUseDay == null) || (getCount() == 0))
            throw new IllegalArgumentException("The Cursor EnergyUseDay is empty!");
    }
}
