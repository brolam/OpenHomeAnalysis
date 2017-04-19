package br.com.brolam.oha.supervisory.data.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import java.util.Date;
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

    SQLiteDatabase sqLiteDatabase;
    Date endDate;
    Calendar cursorCurrentDate;
    Cursor cursorEnergyUseDay;
    int count;

    public OhaEnergyUseDaysCursor(SQLiteDatabase sqLiteDatabase, Date endDate, int count){
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
        this.cursorEnergyUseDay = getCursorEnergyUseDay(this.cursorCurrentDate);
        return  super.onMove(oldPosition, newPosition) ;
    }

    /**
     * Recuperar o total da utilização de energia para um dia
     * @param cursorDate informar o dia da utilização de energia.
     * @return
     */
    private Cursor getCursorEnergyUseDay(Calendar cursorDate) {
        long longBeginDate = cursorDate.getTime().getTime();
        long longEndDate = OhaHelper.getDateEnd(cursorDate.getTime(), false).getTime();
        String selection = String.format("%s BETWEEN ? AND ?", EnergyUseLogEntry.COLUMN_DATE_TIME);
        String selectionArgs[] = new String[]{Long.toString(longBeginDate), Long.toString(longEndDate)};
        return  sqLiteDatabase.query(
                EnergyUseLogEntry.FROM_INDEX_DATE_TIME,
                EnergyUseLogEntry.COLUMNS_CALC_TOTAL,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
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
        return this.cursorEnergyUseDay.moveToFirst()? this.cursorEnergyUseDay.getString(i): null;
    }

    @Override
    public short getShort(int i) {
        return this.cursorEnergyUseDay.moveToFirst()? this.cursorEnergyUseDay.getShort(i): 0;
    }

    @Override
    public int getInt(int i) {
        return this.cursorEnergyUseDay.moveToFirst()? this.cursorEnergyUseDay.getInt(i): 0;
    }

    @Override
    public long getLong(int i) {
        if ( EnergyUseLogEntry.INDEX_COLUMN_CALC_DATE == i ){
            return cursorCurrentDate.getTime().getTime();
        }
        return this.cursorEnergyUseDay.moveToFirst()? this.cursorEnergyUseDay.getLong(i): 0;
    }

    @Override
    public float getFloat(int i) {
        return this.cursorEnergyUseDay.moveToFirst()?this.cursorEnergyUseDay.getFloat(i): 0;
    }

    @Override
    public double getDouble(int i) {
        return this.cursorEnergyUseDay.moveToFirst()? this.cursorEnergyUseDay.getDouble(i): 0.00;
    }

    @Override
    public boolean isNull(int i) {
        return this.cursorEnergyUseDay.isNull(i);
    }

}
