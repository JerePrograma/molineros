package ar.com.ospim.automatico.service;

import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;

/** SOLO classpath de prueba aislada. No consulta configuracion ni base real. */
public class ReportesServiceUtil {
    public static int consultas;
    public static String modo = "VALIDO";
    public static ReportesAutomaticosConfiguracion getConfiguracion() {
        consultas++;
        if ("CONFIG_NULL".equals(modo)) return null;
        ReportesAutomaticosConfiguracion datos = new ReportesAutomaticosConfiguracion();
        datos.setMailFrom("origen@example.invalid");
        datos.setPass("fixture-sin-secreto");
        if ("CLAVE_VACIA".equals(modo)) datos.setPass("");
        if ("REMITENTE_INVALIDO".equals(modo)) datos.setMailFrom("remitente-invalido");
        return datos;
    }
}
