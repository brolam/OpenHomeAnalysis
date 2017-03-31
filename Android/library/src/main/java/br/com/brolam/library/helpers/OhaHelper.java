package br.com.brolam.library.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strTime  informar texto com a Hora, Minuto e segundo no formato HHmmss
     * @param duration informar a duração em milisegundos que será adicionada a data.
     * @return um Calendar com o resultado da soma da strDate + strTime +  duration
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate, String strTime, long duration) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = simpleDateFormat.parse(strDate + strTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while ( duration > 0 ) {
            calendar.add(Calendar.MILLISECOND, (duration > Integer.MAX_VALUE? Integer.MAX_VALUE : (int)duration));
            duration = duration - Integer.MAX_VALUE;
        }
        return calendar;
    }

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @return um Calendar conforme o paramento strDate
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate) throws ParseException {
        return getCalendar(strDate,"000000",0);
    }

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strHour  informar texto com a hora no formato HH
     * @return um Calendar conforme os paramentos  strDate e strHour
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate, String strHour) throws ParseException {
        return getCalendar(strDate, strHour + "0000",0);
    }

    /**
     * Recuperar a quantidade de dias entre duas datas.
     * @param begin informar a data de inicio
     * @param end informar a data final
     * @return quantidade de dias entre as duas datas.
     */
    public static int getAmountDays(long begin, long end) {
        double days = ((end / (24.00 * 60.00 * 60.00 * 1000.00))
                - (begin / (24.00 * 60.00 * 60.00 * 1000.00)));
        return  (int)days;
    }

    /**
     * Recuperar uma data reiniciando a hora.
     * @param date informar uma data válida.
     * @return uma data com a hora igual a 00:00:00.
     */
    public static Date getDateBegin(Date date)  {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    /**
     * Recuperar uma data com a hora final do dia.
     * @param date informar uma data válida.
     * @param untilToday informar se a hora final não deve ultrapassar a hora atual.
     * @return uma data com a hora igual a 23:59:59.
     */
    public static Date getDateEnd(Date date, boolean untilToday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date dateToday = new Date();
        if ( untilToday && ( dateToday.before(calendar.getTime()) )){
            calendar.setTime(dateToday);
        }
        return calendar.getTime();
    }
}
