package br.com.brolam.oha.supervisory.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Date;

/**
 * Organizar o acesso e gravação nas tabelas e colunas do banco de dados onde será
 * registrado a utilização de energia .
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaEnergyUseContract {

    //Definir a URL base de acesso:
    public static final String CONTENT_AUTHORITY = "br.com.brolam.oha.supervisory.energy.use";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Definir o caminho por recurso:
    public static final String PATH_LOG = "log";
    public static final String PATH_LOG_DAYS = "log_days";

    //Definir as URLs:
    public static final Uri CONTENT_URI_LOG = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_LOG)
            .build();

    public static final Uri CONTENT_URI_LOG_DAY = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_LOG_DAYS)
            .build();
    /**
     * Armazenar os registros de utilização da energia.
     */
    public static final class EnergyUseLogEntry implements BaseColumns {
        //Definir o nome  da tabela no banco de dados
        public static final String TABLE_NAME = "energyUserLog";

        //Definir os nomes dos índices da tabela:
        public static final String INDEX_GROUP_BY_DATE_TIME = "idx_" + TABLE_NAME + "_group_by_date_time";

        //Definir os nomes dos campos na tabela:

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
        //Lista com todas as colunas da tabela.
        public static final String[] COLUMN_ALL = new String[]{
                _ID,
                COLUMN_SEQUENCE,
                COLUMN_DATE_TIME,
                COLUMN_DURATION,
                COLUMN_VOLTAGE,
                COLUMN_WATTS_1,
                COLUMN_WATTS_2,
                COLUMN_WATTS_3,
                COLUMN_WATTS_TOTAL
        };

        //Campos calculado por Watts Horas:
        public static final String[] COLUMNS_SUM_WH = new String[]{
                String.format("MIN(%s)", COLUMN_DATE_TIME),
                String.format("MAX(%s)", COLUMN_DATE_TIME),
                String.format("SUM(%s)", COLUMN_DURATION),
                String.format("SUM((%s * %s) / 3600.00)", COLUMN_WATTS_1, COLUMN_DURATION ),
                String.format("SUM((%s * %s) / 3600.00)", COLUMN_WATTS_2, COLUMN_DURATION ),
                String.format("SUM((%s * %s) / 3600.00)", COLUMN_WATTS_3, COLUMN_DURATION ),
                String.format("SUM((%s * %s) / 3600.00)", COLUMN_WATTS_TOTAL, COLUMN_DURATION ),
        };
        //Índices referente a lista de campos COLUMNS_SUM_WH, favor sempre utilizar
        //esses Índices para acessar as colunas no cursor.
        public static final byte INDEX_COLUMN_SUM_DATE_TIME_MIN = 0;
        public static final byte INDEX_COLUMN_SUM_DATE_TIME_MAX = 1;
        public static final byte INDEX_COLUMN_SUM_DURATION = 2;
        public static final byte INDEX_COLUMN_SUM_WH_1 = 3;
        public static final byte INDEX_COLUMN_SUM_WH_2 = 4;
        public static final byte INDEX_COLUMN_SUM_WH_3 = 5;
        public static final byte INDEX_COLUMN_SUM_WH_TOTAL = 6;
        //Somente disponível no {@link OhaEnergyUseDaysCursor}
        public static final byte INDEX_COLUMN_SUM_KWH_COST = 7;

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
                case INDEX_GROUP_BY_DATE_TIME:
                    return String.format("CREATE INDEX %s ON %s (%s, %s, %s, %s, %s, %s, %s);", indexName, TABLE_NAME, COLUMN_DATE_TIME, COLUMN_WATTS_1, COLUMN_WATTS_2, COLUMN_WATTS_3, COLUMN_WATTS_TOTAL, COLUMN_DURATION, COLUMN_VOLTAGE);
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
}
