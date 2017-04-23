package br.com.brolam.oha.supervisory.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import br.com.brolam.library.helpers.OhaHelper;
import br.com.brolam.oha.supervisory.data.cursors.OhaEnergyUseBillCursor;
import br.com.brolam.oha.supervisory.data.cursors.OhaEnergyUseDaysCursor;
import br.com.brolam.oha.supervisory.data.helpers.OhaSQLHelper;

import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;

/**
 * Disponibilizar informações da utilização de energia através de um ContentProvider
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseProvider extends ContentProvider {

    //Definir os códigos que serão relacionados as URIs para facilitar a identificaçãos das mesmas.
    public static final int CODE_ENERGY_USER_LOG = 100;
    public static final int CODE_ENERGY_USER_DAYS = 200;
    public static final int CODE_ENERGY_USER_BILL = 300;
    //Esse UriMatcher será utilizado para realicionar as URIs as códigos supracitados.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OhaSQLHelper ohaSQLHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Mapeamento das URIs referente aos logs de utilização de energia:
        final String energyUseLogAuthority = OhaEnergyUseContract.CONTENT_AUTHORITY;
        matcher.addURI(energyUseLogAuthority, PATH_LOG, CODE_ENERGY_USER_LOG);
        matcher.addURI(energyUseLogAuthority, PATH_DAYS, CODE_ENERGY_USER_DAYS);
        matcher.addURI(energyUseLogAuthority, PATH_BILL, CODE_ENERGY_USER_BILL);
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.ohaSQLHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_ENERGY_USER_BILL:
                long id = insertEnergyUseBill(uri, contentValues, sqLiteDatabase);
                return CONTENT_URI_BILL.buildUpon().appendPath(String.valueOf(id)).build();
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = this.ohaSQLHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_ENERGY_USER_LOG:
                return getOhaEnergyUseLogCursor(sqLiteDatabase, projection, selection, selectionArgs, sortOrder);
            case CODE_ENERGY_USER_DAYS:
                return getOhaEnergyUseDaysCursor(sqLiteDatabase, uri);
            case CODE_ENERGY_USER_BILL:
                return getOhaEnergyUseBillCursor(sqLiteDatabase, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
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

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        this.ohaSQLHelper.close();
        super.shutdown();
    }

    /**
     * Recuperar um cursor com os logs de utilização de energia.
     */
    private Cursor getOhaEnergyUseLogCursor(SQLiteDatabase sqLiteDatabase, String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return sqLiteDatabase.query(
                EnergyUseLogEntry.FROM_INDEX_DATE_TIME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Recuperar um cursor {@link OhaEnergyUseDaysCursor} com a utilização de energia por dia.
     */
    private Cursor getOhaEnergyUseDaysCursor(SQLiteDatabase sqLiteDatabase, Uri uri) {
        String strEndDate = uri.getQueryParameter(PATH_DAYS_PARAM_END_DATE);
        Date endDate = OhaHelper.getDateEnd(strEndDate == null ? new Date() : new Date(Long.parseLong(strEndDate)), false);
        Cursor cursor = sqLiteDatabase.query(
                EnergyUseLogEntry.FROM_INDEX_DATE_TIME,
                EnergyUseLogEntry.COLUMNS_CALC_PERIOD,
                String.format("%s <= ?", EnergyUseLogEntry.COLUMN_DATE_TIME),
                new String[]{Long.toString(endDate.getTime())},
                null,
                null,
                null);
        int count = 1;
        if (cursor.moveToFirst()) {
            count = (int) OhaHelper.getAmountDays(
                    cursor.getLong(EnergyUseLogEntry.INDEX_COLUMNS_CALC_PERIOD_BEGIN),
                    cursor.getLong(EnergyUseLogEntry.INDEX_COLUMNS_CALC_PERIOD_END)
            );
        }
        return new OhaEnergyUseDaysCursor(sqLiteDatabase, endDate, count);
    }

    /**
     * Recuperar um Cursor {@link OhaEnergyUseBillCursor} com a conta de energia e utilização no período da conta.
     */
    private Cursor getOhaEnergyUseBillCursor(SQLiteDatabase sqLiteDatabase, String selection, String[] selectionArgs) {
        Cursor cursor = sqLiteDatabase.query(EnergyUseBillEntry.TABLE_NAME, EnergyUseBillEntry.COLUMN_ALL, selection, selectionArgs, null, null, String.format("%s DESC", EnergyUseBillEntry.COLUMN_FROM));
        return  new OhaEnergyUseBillCursor(sqLiteDatabase, cursor);
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
                        EnergyUseLogEntry.TABLE_NAME,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (insertedResult == -1) {
                    throw new SQLException(String.format("The EnergyUseLog content on the row %s It is not valid.", row));
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        //Notificar a atualização para as Uri relacionadas a tabela EnergyUseLog
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(OhaEnergyUseContract.CONTENT_URI_DAYS, null);
        return values.length;
    }

    /**
     * Inclusão em bloco dos logs de utilização de energia.
     */
    private long insertEnergyUseBill(@NonNull Uri uri, @NonNull ContentValues values, SQLiteDatabase sqLiteDatabase) {
        long id = -1;
        sqLiteDatabase.beginTransaction();
        try {
            id = sqLiteDatabase.insert(
                    EnergyUseBillEntry.TABLE_NAME,
                    null,
                    values);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        //Notificar a atualização para as Uri relacionadas a tabela EnergyUseBill
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

}
