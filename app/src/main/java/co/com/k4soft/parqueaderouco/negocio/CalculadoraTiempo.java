package co.com.k4soft.parqueaderouco.negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class CalculadoraTiempo {


    int MILISEGUNDOS = 1000;
    int SEGUNDOS_EN_UNA_HORA = 3600;
    int UNA_HORA = 1;


    public int calcularTiempoParqueado(String fechaEntrada, String fechaSalida) throws ParseException {
        Date entrada = DateUtil.convertirStringToDate(fechaEntrada);
        Date salida = DateUtil.convertirStringToDate(fechaSalida);

        int diferenciaEnSegundos = (int) (Math.abs(entrada.getTime() - salida.getTime()) / MILISEGUNDOS);

        return calcularHorasParaCobrar(diferenciaEnSegundos);
    }

    private int calcularHorasParaCobrar(int diferencia) {
        if (diferencia <= SEGUNDOS_EN_UNA_HORA) return UNA_HORA;
        return calcularHorasParaCobrar(diferencia - SEGUNDOS_EN_UNA_HORA) + UNA_HORA;
    }




}
