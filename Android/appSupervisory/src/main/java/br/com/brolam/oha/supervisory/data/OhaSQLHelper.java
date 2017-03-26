package br.com.brolam.oha.supervisory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Gerencia um banco de dados local para dados do supervisory.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "supervisory.db";

    public OhaSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(OhaEnergyUseContract.EnergyUseLogEntry.getSQLCreate());
        sqLiteDatabase.execSQL(OhaEnergyUseContract.EnergyUseLogEntry.getSQLCreateIndex(
                OhaEnergyUseContract.EnergyUseLogEntry.INDEX_GROUP_BY_DATE_TIME)
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
