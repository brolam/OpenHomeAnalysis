package br.com.brolam.oha.supervisory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Disponibilizar informações da utilização de energia através de um ContentProvider
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseProvider extends ContentProvider {

    //Definir os códigos que serão relacionados as URIs para facilitar a identificaçãos das mesmas.
    public static final int CODE_ENERGY_USER_LOG = 100;
    //Esse UriMatcher será utilizado para realicionar as URIs as códigos supracitados.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OhaSQLHelper ohaSQLHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Mapeamento das URIs referente aos logs de utilização de energia:
        final String energyUseLogAuthority = OhaEnergyUseContract.CONTENT_AUTHORITY;
        matcher.addURI(energyUseLogAuthority, OhaEnergyUseContract.PATH_LOG, CODE_ENERGY_USER_LOG);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        this.ohaSQLHelper = new OhaSQLHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = this.ohaSQLHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_ENERGY_USER_LOG:
                return bulkInsertEnergyUseLogs(uri, values, sqLiteDatabase);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Inclusão em bloco dos logs de utilização de energia.
     */
    private int bulkInsertEnergyUseLogs(@NonNull Uri uri, @NonNull ContentValues[] values, SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            for (int row = 0; row < values.length; row++) {
                ContentValues value = values[row];
                long insertedResult = sqLiteDatabase.insertWithOnConflict(
                        OhaEnergyUseContract.EnergyUseLogEntry.TABLE_NAME,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if ( insertedResult == -1){
                    throw new SQLException(String.format("The EnergyUseLog content on the row %s It is not valid.", row));
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return values.length;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
}
