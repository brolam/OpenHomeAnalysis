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
    private int count;
    private Date beginDate;
    private Date endDate;
    Cursor cursorEnergyUseDay = null;
    Double kWhCost = 0.00;

    public OhaEnergyUseDaysCursor(){
        this.count = 0;
    }

    public OhaEnergyUseDaysCursor(OhaSQLHelper ohaSQLHelper, Date beginDate, Date endDate){
        this.ohaSQLHelper = ohaSQLHelper;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.count = OhaHelper.getAmountDays(this.beginDate.getTime(), this.endDate.getTime());
    }

    /**
     * Recupear a utilização de energia para a nova posição do cursor na ordem decrescente.
     * @param oldPosition
     * @param newPosition
     * @return
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        SQLiteDatabase sqLiteDatabase = this.ohaSQLHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.endDate);
        calendar.add(Calendar.DATE,newPosition * -1); //ordem decrescente
        //Definir a hora incial e final do dia:
        long beginDate = OhaHelper.getDateBegin(calendar.getTime()).getTime();
        long endDate = OhaHelper.getDateEnd(calendar.getTime(),true).getTime();
        String selection = String.format("%s BETWEEN ? AND ?", EnergyUseLogEntry.COLUMN_DATE_TIME);
        String[] selectionArgs = new String[]{String.valueOf(beginDate), String.valueOf(endDate)};
        //Fechar o possição anterior.
        if ( this.cursorEnergyUseDay != null){
            this.cursorEnergyUseDay.close();
        }
        //Sempre utilizar o index {@link EnergyUseLogEntry.INDEX_GROUP_BY_DATE_TIME}
        //para realizar a consulta por dia:
        String from = String.format("%s INDEXED BY %s", EnergyUseLogEntry.TABLE_NAME, EnergyUseLogEntry.INDEX_GROUP_BY_DATE_TIME );
        this.cursorEnergyUseDay = sqLiteDatabase.query(
                from,
                EnergyUseLogEntry.COLUMNS_SUM,
                selection,
                selectionArgs,
                null,
                null,
                null);
        this.kWhCost = 0.65; //TODO EnergyBill - Informar o custo por KWH.
        return super.onMove(oldPosition, newPosition) && this.cursorEnergyUseDay.moveToFirst() ;
    }

    @Override
    public int getCount() {
        return count;
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
