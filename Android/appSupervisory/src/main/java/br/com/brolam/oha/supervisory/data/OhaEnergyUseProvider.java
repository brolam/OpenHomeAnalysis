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
import br.com.brolam.oha.supervisory.data.cursors.OhaEnergyUseDaysCursor;
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
    public static final int CODE_ENERGY_USER_LOG_DAYS = 101;
    //Esse UriMatcher será utilizado para realicionar as URIs as códigos supracitados.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OhaSQLHelper ohaSQLHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Mapeamento das URIs referente aos logs de utilização de energia:
        final String energyUseLogAuthority = CONTENT_AUTHORITY;
        matcher.addURI(energyUseLogAuthority, PATH_LOG, CODE_ENERGY_USER_LOG);
        matcher.addURI(energyUseLogAuthority, PATH_LOG_DAYS, CODE_ENERGY_USER_LOG_DAYS);
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
        getContext().getContentResolver().notifyChange(OhaEnergyUseContract.CONTENT_URI_LOG_DAY, null);
        return values.length;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = this.ohaSQLHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_ENERGY_USER_LOG:
                return sqLiteDatabase.query(
                        EnergyUseLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            //Retornar a um  {@link OhaEnergyUseDaysCursor};
            case CODE_ENERGY_USER_LOG_DAYS:
                //Sempre utilizar o index {@link EnergyUseLogEntry.INDEX_GROUP_BY_DATE_TIME}
                //para realizar a consulta por dia:
                String from = String.format("%s INDEXED BY %s", EnergyUseLogEntry.TABLE_NAME, EnergyUseLogEntry.INDEX_GROUP_BY_DATE_TIME);
                Date dateNow = OhaHelper.getDateEnd(new Date(),false);
                Cursor cursor = sqLiteDatabase.query(
                        from,
                        EnergyUseLogEntry.COLUMNS_SUM_WH,
                        selection,
                        selectionArgs,
                        //Group BY por dia, mas essa solução ainda não é a definitiva
                        //Será desenvolvida uma solução para facilitar a soma por dia, semana, mês e etc.
                        String.format("CAST((( %s / 86400000.00) - ( %s / 86400000.00)) AS INT)", EnergyUseLogEntry.COLUMN_DATE_TIME, dateNow.getTime()),
                        null,
                        sortOrder);
                return new OhaEnergyUseDaysCursor(ohaSQLHelper, cursor);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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
}
