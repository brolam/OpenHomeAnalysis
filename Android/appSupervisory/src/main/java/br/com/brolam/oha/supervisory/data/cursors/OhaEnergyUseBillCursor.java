package br.com.brolam.oha.supervisory.data.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;


/**
 * Recuperar as contas com o total de utilização de energia no período da conta.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseBillCursor extends AbstractCursor {

    SQLiteDatabase sqLiteDatabase;
    Cursor cursorEnergyUseBill;
    Cursor cursorEnergyUse;

    public OhaEnergyUseBillCursor(SQLiteDatabase sqLiteDatabase, Cursor cursorEnergyUseBill){
        this.sqLiteDatabase = sqLiteDatabase;
        this.cursorEnergyUseBill = cursorEnergyUseBill;
    }

    /**
     * Recuperar a conta de energia para a nova posição do cursor.
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        this.cursorEnergyUseBill.moveToPosition(newPosition);
        if ( this.cursorEnergyUse != null) this.cursorEnergyUse.close();
        this.cursorEnergyUse = getCursorEnergyUseBill(this.cursorEnergyUseBill);
        return  super.onMove(oldPosition, newPosition) ;
    }

    /**
     * Recuperar o total da utilização de energia no período da conta.
     * @param cursor informar o cursor com a posição atual da conta.
     * @return
     */
    private Cursor getCursorEnergyUseBill(Cursor cursor) {

        long longDateBegin = cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM);
        long longDateEnd = cursor.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO);
        String from = String.format("%s INDEXED BY %s", EnergyUseLogEntry.TABLE_NAME, EnergyUseLogEntry.INDEX_DATE_TIME);
        String selection = String.format("%s BETWEEN ? AND ?", EnergyUseLogEntry.COLUMN_DATE_TIME);
        String selectionArgs[] = new String[]{Long.toString(longDateBegin), Long.toString(longDateEnd)};
        return  sqLiteDatabase.query(
                from,
                EnergyUseLogEntry.COLUMNS_CALC_TOTAL,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private boolean isCursorEnergyUseBill(int i) {
        return  ( i < this.cursorEnergyUseBill.getColumnCount());
    }

    @Override
    public int getCount() {
        return cursorEnergyUseBill.getCount();
    }

    @Override
    public String[] getColumnNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getString(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getString(col): "";
    }


    @Override
    public short getShort(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getShort(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getShort(col): 0;
    }

    @Override
    public int getInt(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getInt(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getInt(col): 0;
    }

    @Override
    public long getLong(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getLong(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getLong(col): 0;
    }

    @Override
    public float getFloat(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getFloat(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getFloat(col): 0;
    }

    @Override
    public double getDouble(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getDouble(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.getDouble(col): 0;
    }

    @Override
    public boolean isNull(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.isNull(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return cursorEnergyUse.moveToFirst()? this.cursorEnergyUse.isNull(col): true;
    }
}
