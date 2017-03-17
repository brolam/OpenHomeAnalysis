package br.com.brolam.library.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Disponibilizar funcionalidades para converter e formatar texto, número, data e etc.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaHelper {

    /**
     * Obter um texto com o Ano, Mês e Dia.
     * @param date informar uma data válida.
     * @return texto no formato yyyyMMdd
     */
    public static String getStrDate(Date date) {
        return new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(date);
    }


    /**
     * Obter um texto com a Hora, Minuto e Segundo.
     * @param date informar uma data válida.
     * @return texto no formato HHmmss.
     */
    public static String getStrTime(Date date) {
        return new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um texto com a hora.
     * @param date informar uma data válida.
     * @return texto no formato HH
     */
    public static String getStrHour(Date date) {
        return new SimpleDateFormat("HH", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um texto com a Hora e Minuto
     * @param date informar uma data válida.
     * @return texto no formato HHmm.
     */
    public static String getStrTimeHHmm(Date date) {
        return new SimpleDateFormat("HHmm", Locale.ENGLISH).format(date);
    }
}
