package co.com.k4soft.parqueaderouco.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String FORMATO_FECHA_HORA = "yyyy-MM-dd HH:mm:ss";

    public static Date convertirStringToDate(String fecha) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_HORA, Locale.ENGLISH);
        return dateFormat.parse(fecha);
    }

    public static String convertirDateToString(Date fecha) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_HORA, Locale.ENGLISH);
        return dateFormat.format(fecha);
    }

}
