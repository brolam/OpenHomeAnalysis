package br.com.brolam.oha.supervisory.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Date;

import br.com.brolam.library.helpers.OhaHelper;

/**
 * Organizar o acesso e gravação nas tabelas e colunas do banco de dados onde será
 * registrado a utilização de energia.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseContract {

    /*Definir a URL base de acesso:*/
    public static final String CONTENT_AUTHORITY = "br.com.brolam.oha.supervisory.energy.use";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*Definir o caminho por recurso:*/
    public static final String PATH_LOG = "log";
    public static final String PATH_DAYS = "days";
    public static final String PATH_DAYS_PARAM_BEING_DATE = "beginDate";
    public static final String PATH_BILL = "bill";

    /*Definir as URLs:*/
    public static final Uri CONTENT_URI_LOG = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_LOG)
            .build();

    public static final Uri CONTENT_URI_DAYS = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_DAYS)
            .build();

    public static Uri getUriDays(Date beginDate){
       return CONTENT_URI_DAYS.buildUpon().appendQueryParameter(PATH_DAYS_PARAM_BEING_DATE, Long.toString(beginDate.getTime())).build();
    }

    public static final Uri CONTENT_URI_BILL = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_BILL)
            .build();
    /**
     * Armazenar os registros de utilização da energia.
     */
    public static final class EnergyUseLogEntry implements BaseColumns {
        //Definir o nome  da tabela no banco de dados
        public static final String TABLE_NAME = "energyUserLog";

        //Definir os nomes dos índices da tabela:
        public static final String INDEX_DATE_TIME = "idx_" + TABLE_NAME + "_date_time";

        //Definir os nomes das origens por indices:
        public static final String FROM_INDEX_DATE_TIME = String.format("%s INDEXED BY %s", TABLE_NAME, INDEX_DATE_TIME);

        /*Definir os nomes dos campos na tabela:*/
        //Sequencia por bloco de registro {@link br.com.brolam.oha.supervisory.apiV1.models.OhaEnergyUseLog.repository}
        public static final String COLUMN_SEQUENCE = "sequence";
        //Data e hora do registro do log.
        public static final String COLUMN_DATE_TIME = "date_time";
        //Duração em segundos entre esse log e o log anterior.
        public static final String COLUMN_DURATION = "duration";
        //Tensão média na Duração do log.
        public static final String COLUMN_VOLTAGE = "voltage";
        //Watts média da fase 1 na Duração do log.
        public static final String COLUMN_WATTS_1 = "watts1";
        //Watts média da fase 2 na Duração do log.
        public static final String COLUMN_WATTS_2 = "watts2";
        //Watts média da fase 3 na Duração do log.
        public static final String COLUMN_WATTS_3 = "watts3";
        //Watts média das fases 1,2 e 3 na Duração do log.
        public static final String COLUMN_WATTS_TOTAL = "watts_total";

        /*Campos calculado por Watts Horas: */
        //Recuperar o período de disponibilidade de logs:
        public static final String[] COLUMNS_CALC_PERIOD = new String[]{
                String.format("MIN(%s)", COLUMN_DATE_TIME),
                String.format("MAX(%s)", COLUMN_DATE_TIME)
        };
        //Índices referente a lista de campos COLUMNS_CALC_PERIOD, favor sempre utilizar
        //esses Índices para acessar as colunas no cursor.
        public static final byte INDEX_COLUMNS_CALC_PERIOD_BEGIN = 0;
        public static final byte INDEX_COLUMNS_CALC_PERIOD_END = 1;

        public static final String[] COLUMNS_CALC_TOTAL = new String[]{
                String.format("SUM(%s)", COLUMN_DURATION),
                String.format("SUM((%s * %s) / 3600.00)", COLUMN_WATTS_TOTAL, COLUMN_DURATION ),
                String.format("MAX(%s)", COLUMN_WATTS_TOTAL),
                //Recuperar o custo por KWH:
                String.format("(SELECT AVG(%s) FROM %s WHERE %s BETWEEN %s AND %s)", EnergyUseBillEntry.COLUMN_KWH_COST, EnergyUseBillEntry.TABLE_NAME, EnergyUseLogEntry.COLUMN_DATE_TIME, EnergyUseBillEntry.COLUMN_FROM, EnergyUseBillEntry.COLUMN_TO),
        };

        //Índices referente a lista de campos COLUMNS_CALC, favor sempre utilizar
        //esses Índices para acessar as colunas no cursor.
        public static final byte INDEX_COLUMN_CALC_DURATION_SUN = 0;
        public static final byte INDEX_COLUMN_CALC_WH_TOTAL_SUN = 1;
        public static final byte INDEX_COLUMN_CALC_WATTS_MAX = 2;
        public static final byte INDEX_COLUMN_CALC_KWH_COST = 3;
        //Somente disponível no {@link OhaEnergyUseDaysCursor}
        public static final byte INDEX_COLUMN_CALC_DATE = 4;

        /**
         * Recuperar o SQL para criar a tabela energyUserLog no banco de dados.
         */
        public static String getSQLCreate(){
            return String.format("CREATE TABLE %s ( %s LONG PRIMARY KEY, %s INTEGER, %s LONG, %s REAL, %s REAL, %s REAL, %s REAL, %s REAL, %s REAL)",
                    TABLE_NAME,
                    _ID,
                    COLUMN_SEQUENCE,
                    COLUMN_DATE_TIME,
                    COLUMN_DURATION,
                    COLUMN_VOLTAGE,
                    COLUMN_WATTS_1,
                    COLUMN_WATTS_2,
                    COLUMN_WATTS_3,
                    COLUMN_WATTS_TOTAL);
        }

        /**
         * Recuperar o SQL para criar um índice na tabela energyUserLog.
         */
        public static String getSQLCreateIndex(String indexName) {
            switch (indexName){
                case INDEX_DATE_TIME:
                    return String.format("CREATE INDEX %s ON %s (%s, %s, %s, %s);", indexName, TABLE_NAME, COLUMN_DATE_TIME, COLUMN_WATTS_TOTAL, COLUMN_DURATION);
                default:
                    throw new IllegalArgumentException(String.format("Index %s from table %s does not exist", indexName, TABLE_NAME ));
            }
        }

        /**
         * Analisar e validar um registro de utilização de energia
         */
        public static ContentValues parse(long id, int sequence, Date dateTime, double duration, double voltage, double watts1, double watts2, double watts3){
            ContentValues contentValues = new ContentValues();
            contentValues.put(_ID, id);
            contentValues.put(COLUMN_SEQUENCE, sequence );
            contentValues.put(COLUMN_DATE_TIME, dateTime.getTime() );
            contentValues.put(COLUMN_DURATION, duration );
            contentValues.put(COLUMN_VOLTAGE, voltage );
            contentValues.put(COLUMN_WATTS_1, watts1 );
            contentValues.put(COLUMN_WATTS_2, watts2 );
            contentValues.put(COLUMN_WATTS_3, watts3 );
            contentValues.put(COLUMN_WATTS_TOTAL, watts1 + watts2 + watts3 );
            return contentValues;
        }
    }

    /**
     * Armazenar a conta de utilização da energia.
     */
    public static final class EnergyUseBillEntry implements BaseColumns {
        //Definir o nome  da tabela no banco de dados:
        public static final String TABLE_NAME = "energyUserBill";

        /*Definir os nomes dos campos na tabela:*/
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FROM = "period_from";
        public static final String COLUMN_TO = "period_to";
        public static final String COLUMN_KWH_COST = "kwh_cost";
        public static final String[] COLUMN_ALL = new String[]{
                _ID,
                COLUMN_TITLE,
                COLUMN_FROM,
                COLUMN_TO,
                COLUMN_KWH_COST
        };

        //Índices referente a lista de campos COLUMN_ALL, favor sempre utilizar
        //esses Índices para acessar as colunas no cursor.
        public static final byte INDEX_COLUMN_ID = 0;
        public static final byte INDEX_COLUMN_TITLE = 1;
        public static final byte INDEX_COLUMN_FROM = 2;
        public static final byte INDEX_COLUMN_TO = 3;
        public static final byte INDEX_COLUMN_KWH_COST = 4;
        //Somente disponível no {@link OhaEnergyUseBillCursor}
        public static final byte INDEX_COLUMN_CALC_DURATION_SUN = 5;
        public static final byte INDEX_COLUMN_CALC_WH_TOTAL_SUN = 6;
        public static final byte INDEX_COLUMN_CALC_WATTS_MAX = 7;

        /**
         * Recuperar o SQL para criar a tabela energyUserBill no banco de dados.
         */
        public static String getSQLCreate(){
            return String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY autoincrement, %s TEXT, %s LONG, %s LONG, %s REAL)",
                    TABLE_NAME,
                    _ID,
                    COLUMN_TITLE,
                    COLUMN_FROM,
                    COLUMN_TO,
                    COLUMN_KWH_COST
            );
        }

        /**
         * Analisar e validar um conta de utilização de energia
         */
        public static ContentValues parse(String title, Date from, Date to, double kwhCust) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_TITLE, title);
            contentValues.put(COLUMN_FROM, OhaHelper.getDateBegin(from).getTime());
            contentValues.put(COLUMN_TO, OhaHelper.getDateEnd(to, false).getTime());
            contentValues.put(COLUMN_KWH_COST, kwhCust);
            return contentValues;
        }

    }
}
