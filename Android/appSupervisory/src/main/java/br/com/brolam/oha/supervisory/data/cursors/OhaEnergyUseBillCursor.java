package br.com.brolam.oha.supervisory.data.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;


/**
 * Recuperar as contas com o total de utilização de energia no período da conta.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseBillCursor extends AbstractCursor {

    IOhaEnergyUseTotalCache iOhaEnergyUseTotalCache;

    SQLiteDatabase sqLiteDatabase;
    Cursor cursorEnergyUseBill;
    HashMap<Integer,String> energyUseTotal;

    public OhaEnergyUseBillCursor(SQLiteDatabase sqLiteDatabase, Cursor cursorEnergyUseBill, IOhaEnergyUseTotalCache iOhaEnergyUseTotalCache){
        this.sqLiteDatabase = sqLiteDatabase;
        this.cursorEnergyUseBill = cursorEnergyUseBill;
        this.iOhaEnergyUseTotalCache = iOhaEnergyUseTotalCache;
    }

    /**
     * Recuperar a conta de energia para a nova posição do cursor.
     */
    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        this.cursorEnergyUseBill.moveToPosition(newPosition);
        this.energyUseTotal = this.iOhaEnergyUseTotalCache.getEnergyUseTotalOnCache(this.cursorEnergyUseBill.getLong(EnergyUseBillEntry.INDEX_COLUMN_FROM), this.cursorEnergyUseBill.getLong(EnergyUseBillEntry.INDEX_COLUMN_TO) );
        return  super.onMove(oldPosition, newPosition) ;
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
        return energyUseTotal != null? this.energyUseTotal.get(col): "";
    }

    @Override
    public short getShort(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getShort(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return energyUseTotal != null? Short.parseShort(this.energyUseTotal.get(col)): 0;
    }

    @Override
    public int getInt(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getInt(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return energyUseTotal != null? Integer.parseInt(this.energyUseTotal.get(col)): 0;
    }

    @Override
    public long getLong(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getLong(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return energyUseTotal != null? Long.parseLong(this.energyUseTotal.get(col)): 0;
    }

    @Override
    public float getFloat(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getFloat(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return energyUseTotal != null? Float.parseFloat(this.energyUseTotal.get(col)): 0;
    }

    @Override
    public double getDouble(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.getDouble(i);
        }
        int col = i - (this.cursorEnergyUseBill.getColumnCount());
        return energyUseTotal != null? Double.parseDouble(this.energyUseTotal.get(col)): 0;
    }

    @Override
    public boolean isNull(int i) {
        if ( isCursorEnergyUseBill(i) ){
            return this.cursorEnergyUseBill.isNull(i);
        }
        return energyUseTotal == null;
    }
}
