package br.com.brolam.oha.supervisory.data.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import br.com.brolam.library.helpers.OhaHelper;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;


/**
 * Recuperar a utilização de energia por dia, {@link EnergyUseLogEntry}
 * na ordem decrescente.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseDaysCursor extends AbstractCursor {

    IOhaEnergyUseTotalCache iOhaEnergyUseTotalCache;

    SQLiteDatabase sqLiteDatabase;
    Date endDate;
    Calendar cursorCurrentDate;
    HashMap<Integer,String> energyUseTotal;
    int count;

    public OhaEnergyUseDaysCursor(SQLiteDatabase sqLiteDatabase, Date endDate, int count, IOhaEnergyUseTotalCache iOhaEnergyUseTotalCache){
        this.iOhaEnergyUseTotalCache = iOhaEnergyUseTotalCache;
        this.sqLiteDatabase = sqLiteDatabase;
        this.endDate = endDate;
        this.count = count;
    }

    /**
     * Recupear a utilização de energia para a nova posição do cursor na ordem decrescente.
     * @param oldPosition
     * @param newPosition
     * @return
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        this.cursorCurrentDate = Calendar.getInstance();
        this.cursorCurrentDate.setTime(OhaHelper.getDateBegin(endDate));
        this.cursorCurrentDate.add(Calendar.DATE,  newPosition * -1);
        long longBeginDate = cursorCurrentDate.getTime().getTime();
        long longEndDate = OhaHelper.getDateEnd(cursorCurrentDate.getTime(), false).getTime();
        this.energyUseTotal = this.iOhaEnergyUseTotalCache.getEnergyUseTotalOnCache(longBeginDate, longEndDate);
        return super.onMove(oldPosition, newPosition) ;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String[] getColumnNames() {
        throw new UnsupportedOperationException();

    }

    @Override
    public String getString(int i) {
        if ( EnergyUseLogEntry.INDEX_COLUMN_CALC_DATE == i ){
            return String.valueOf(cursorCurrentDate.getTime().getTime());
        }
        return this.energyUseTotal != null? this.energyUseTotal.get(i): "";
    }

    @Override
    public short getShort(int i) {
        return !isEmpty(i)? Short.parseShort(getString(i)):0;
    }

    @Override
    public int getInt(int i) {
        return !isEmpty(i)? Integer.parseInt(getString(i)):0;
    }

    @Override
    public long getLong(int i) {
        return !isEmpty(i)? Long.parseLong(getString(i)):0;
    }

    @Override
    public float getFloat(int i) {
        return !isEmpty(i)? Float.parseFloat(getString(i)):0;
    }

    @Override
    public double getDouble(int i) {
        return !isEmpty(i)? Double.parseDouble(getString(i)):0;
    }

    @Override
    public boolean isNull(int i) {
        return this.energyUseTotal != null;
    }

    private boolean isEmpty(int i){
        String value = getString(i);
        return  (value == null) || value.isEmpty();
    }
}
