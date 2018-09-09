package br.com.brolam.library.helpers;

import android.content.Context;
import android.text.format.DateUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import br.com.brolam.library.R;

/**
 * Disponibilizar funcionalidades para converter e formatar texto, número, data e etc.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaHelper {
    static String defaultCurrencySymbol;
    /**
     * Obter um texto com o Ano, Mês e Dia.
     *
     * @param date informar uma data válida.
     * @return texto no formato yyyyMMdd
     */
    public static String getStrDate(Date date) {
        return new SimpleDateFormat("yyMMdd", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um texto com a Hora, Minuto e Segundo.
     *
     * @param date informar uma data válida.
     * @return texto no formato HHmmss.
     */
    public static String getStrTime(Date date) {
        return new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um texto com a hora.
     *
     * @param date informar uma data válida.
     * @return texto no formato HH
     */
    public static String getStrHour(Date date) {
        return new SimpleDateFormat("HH", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um texto com a Hora e Minuto
     *
     * @param date informar uma data válida.
     * @return texto no formato HHmm.
     */
    public static String getStrTimeHHmm(Date date) {
        return new SimpleDateFormat("HHmm", Locale.ENGLISH).format(date);
    }

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     *
     * @param strDate  informar texto com data no formato YYYYMMDD
     * @param strTime  informar texto com a Hora, Minuto e segundo no formato HHmmss
     * @param duration informar a duração em milisegundos que será adicionada a data.
     * @return um Calendar com o resultado da soma da strDate + strTime +  duration
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate, String strTime, long duration) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = simpleDateFormat.parse(strDate + strTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (duration > 0) {
            calendar.add(Calendar.MILLISECOND, (duration > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) duration));
            duration = duration - Integer.MAX_VALUE;
        }
        return calendar;
    }

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     *
     * @param strDate informar texto com data no formato YYYYMMDD
     * @return um Calendar conforme o paramento strDate
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate) throws ParseException {
        return getCalendar(strDate, "000000", 0);
    }

    /**
     * Obter um Calendar conforme os paramentos abaixo:
     *
     * @param strDate informar texto com data no formato YYYYMMDD
     * @param strHour informar texto com a hora no formato HH
     * @return um Calendar conforme os paramentos  strDate e strHour
     * @throws ParseException
     */
    public static Calendar getCalendar(String strDate, String strHour) throws ParseException {
        return getCalendar(strDate, strHour + "0000", 0);
    }

    /**
     * Recuperar a quantidade de dias entre duas datas.
     * @param begin informar a data de inicio
     * @param end   informar a data final
     * @return quantidade de dias entre as duas datas.
     */
    public static long getAmountDays(long begin, long end) {
        double days = ((end / (24.00 * 60.00 * 60.00 * 1000.00))
                - (begin / (24.00 * 60.00 * 60.00 * 1000.00)));
        return days > 0 && days < 1 ? 1 : Math.round(days);

    }

    /**
     * Recuperar uma data com hora, minutos e segundos zerados.
     * @param date informar uma data válida.
     */
    public static Date getDateBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Recuperar uma data com a hora final do dia.
     * @param date       informar uma data válida.
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
        if (untilToday && (dateToday.before(calendar.getTime()))) {
            calendar.setTime(dateToday);
        }
        return calendar.getTime();
    }

    /**
     * Recuperar uma hora com minutos e segundos zerados.
     * @param date informar uma data válida.
     */
    public static Date getBeginHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }

    /**
     * Recuperar hora com minutos e segundos iguais a 59.
     * @param date informar uma data válida.
     */
    public static Date getEndHour(Date date, boolean untilToday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        Date dateToday = new Date();
        if (untilToday && (dateToday.before(calendar.getTime()))) {
            calendar.setTime(dateToday);
        }
        return calendar.getTime();

    }

    /**
     * Recuperar uma data formatada conforme o parâmetro date Format.
     *
     * @param date       informar uma data válida
     * @param dateFormat informar um formato conforme o Date and Time Patterns: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     * @return texto com a data formatada.
     */
    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    /**
     * Recuperar uma data formatada conforme o parâmetro date Format.
     *
     * @param longDateTime informar uma data válida
     * @param dateFormat   informar um formato conforme o Date and Time Patterns{@link SimpleDateFormat} :
     *                     https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     * @return texto com a data formatada.
     */
    public static String formatDate(long longDateTime, String dateFormat) {
        Date date = new Date(longDateTime);
        return formatDate(date, dateFormat);
    }

    //Recuperar o símbolo monentário padrão para o usuário.
    public static String getDefaultCurrencySymbol(){
        if ( defaultCurrencySymbol == null){
            defaultCurrencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
        }
        return defaultCurrencySymbol;
    }

    /**
     * Recuperar um número formatado conforme o decimalFormat.
     * @param number  informar um número válido
     * @param pattern informar o formato conforme o {@link DecimalFormat}:
     *                https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html
     * @return texto com o número formatado.
     */
    public static String formatNumber(double number, String pattern) {
        return new DecimalFormat(pattern).format(number);
    }

    /**
     * Recuperar um número formatado conforme o decimalFormat mais o símbolo monentário
     * @param number  informar um número válido
     * @param pattern informar o formato conforme o {@link DecimalFormat}:
     *                https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html
     * @return texto com o número formatado.
     */
    public static String formatMoney(double number, String pattern) {
        return String.format("%s%s", getDefaultCurrencySymbol(), new DecimalFormat(pattern).format(number));
    }

    /**
     * Recuperar um texto para ser editado em um EditText.
     * @param value informar um valor numérico válido.
     * @return texto formatado para ser editado em um EditText.
     */
    public static String getEditable(double value) {
        return value == 0.00 ? "" : String.valueOf(value);
    }

    /**
     * Recuperar o percentual da precisão do tempo de leitura sobre 24 horas.
     * @param context  informar um contexto válido.
     * @param duration duração em segundos da leitura.
     * @param untilNow informar se o tempo total não deve ser maior do que a hora atual.
     * @return texto formatado informando o percentual da precisão, EX: Accuracy 99.99%
     */
    public static String formatAccuracyDay(Context context, double duration, boolean untilNow) {
        Date date = new Date();
        long beginDate = getDateBegin(date).getTime();
        long endDate = getDateEnd(date, false).getTime();
        return formatAccuracy(context, beginDate, endDate, duration, untilNow);
    }
    
    /**
     * Recuperar o percentual da precisão do tempo de leitura sobre um período.
     * @param context  informar um contexto válido.
     * @param beginDateTime informar a data e hora inicial.
     * @param endDateTime informar a data e hora final.
     * @param duration duração em segundos da leitura.
     * @param untilNow informar se a data e hora final deve ser menor do que a data e hora atual.
     * @return texto formatado informando o percentual da precisão, EX: Accuracy 99.99%
     */
    public static String formatAccuracy(Context context, long beginDateTime, long endDateTime, double duration, boolean untilNow) {
        long now = new Date().getTime();
        //Se o paramentro untilNow for verdadeiro, considerar o hora final a hora atual:
        double periodInMillis = (((untilNow && now < endDateTime) ? now : endDateTime) - beginDateTime) / 1000.00;
        double accuracy = duration > 0 ? (duration / periodInMillis) * 100 : 0.00;
        return String.format(context.getString(R.string.format_accuracy_day), formatNumber(accuracy, "#0.00"));
    }

    /**
     * Recuperar um texto com a primeira letra maiúscula.
     * @param string informar uma string válida.
     */
    public static String formatCamelCase(String string) {
        return string.length() > 0 ? string.replaceFirst(
                Character.toString(string.charAt(0)),
                Character.toString(Character.toUpperCase(string.charAt(0)))
        ) : string;
    }

    /**
     * Converter um texto em um valor numérico
     * @param string texto com um valor numérico válido.
     * @param ifError valor que deve ser retornado se o texto for inválido.
     * @return um valor numérico válido.
     */
    public static double convertToDouble(String string, double ifError){
        if ((string  == null ) || string.isEmpty() ) return ifError;
        try {
            return Double.parseDouble(string);
        } catch (Exception e){
            e.printStackTrace();
            return ifError;
        }
    }

    /**
     * Converter milisegundos em horas.
     *
     * @param millis informar os milisegundos.
     * @return total em horas.
     */
    public static double convertMillisToHours(double millis) {
        return millis > 0.00 ? millis / DateUtils.HOUR_IN_MILLIS : 0.00;
    }

    /**
     * Converter segundos em horas
     */
    public static double convertSecondsToHours(double seconds) {
        return seconds > 0.00 ? (seconds * 1000.00) / DateUtils.HOUR_IN_MILLIS : 0.00;
    }

    /**
     * Converter segundos em dias
     */
    public static double convertSecondsToDays(double seconds) {
        return seconds > 0.00 ? (seconds * 1000.00) / DateUtils.DAY_IN_MILLIS : 0.00;
    }

    /**
     * Converter o total de Wh em Kwh.
     *
     * @param totalWh total de Wh.
     * @return Kwh
     */
    public static double convertWhToKWH(double totalWh) {
        return totalWh > 0 ? totalWh / 1000.00 : 0.00;
    }


    /**
     * Informar o processo de compactação ou descompactação de um arquivo.
     */
    public interface IZipFile {
        void progress(long size, long sizeProcessed);
    }

    /**
     * Compactar uma arquivo.
     * @param inputFile informar o arquivo a ser compactado.
     * @param zipFilePath informar o nome e caminho zip.
     * @param iZipFile se necessário, informar uma {@link IZipFile}.
     * @throws IOException
     */
    public static void zipFile(File inputFile, String zipFilePath, IZipFile iZipFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        ZipEntry zipEntry = new ZipEntry(inputFile.getName());
        zipOutputStream.putNextEntry(zipEntry);
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        FileChannel fileChannel = fileInputStream.getChannel();
        FileLock fileLock = fileChannel.tryLock(0L, Long.MAX_VALUE, /*shared*/true);
        long sizeToZip = fileInputStream.available();
        long sizeCompacted = 0;
        try {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                sizeCompacted += bytesRead;
                zipOutputStream.write(buf, 0, bytesRead);
                if (iZipFile != null) iZipFile.progress(sizeToZip, sizeCompacted);
            }
        } finally {
            fileLock.release();
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileOutputStream.close();
        }
    }

    /**
     * Descompactar um arquivo zip.
     * @param zipFilePath informar o caminho completo do arquivo zip.
     * @param fileName se necessário informar o nome do arquivo que deve ser descompactado ou nulo para todos.
     * @param directory informar o diretório onde os arquivo serão descompactados.
     * @param iZipFile se necessário, informar uma {@link IZipFile}.
     * @throws IOException
     */
    public static void unZipFile(String zipFilePath, String fileName, String directory, IZipFile iZipFile) throws IOException {
        FileInputStream streamZipFile = new FileInputStream(zipFilePath);
        ZipInputStream streamZips = new ZipInputStream(streamZipFile);
        try {
            ZipEntry zipEntry = null;
            while ((zipEntry = streamZips.getNextEntry()) != null) {
                long sizeZip = zipEntry.getSize();
                long sizeProcessed = 0;
                if (!zipEntry.isDirectory()) {
                    if ( (fileName == null) || zipEntry.getName().contains(fileName)) {
                        byte[] buf = new byte[1024];
                        int bytesRead;
                        String fileOutputName = String.format("%s/%s", directory, zipEntry.getName());
                        FileOutputStream fileOutputStream = new FileOutputStream(fileOutputName);
                        while ((bytesRead = streamZips.read(buf)) > 0) {
                            sizeProcessed += bytesRead;
                            fileOutputStream.write(buf, 0, bytesRead);
                            if (iZipFile != null) iZipFile.progress(sizeZip, sizeProcessed);
                        }
                    }
                }

            }
        } finally {
            if (streamZipFile != null) streamZipFile.close();
            if (streamZips != null) streamZips.close();
        }
    }
}
