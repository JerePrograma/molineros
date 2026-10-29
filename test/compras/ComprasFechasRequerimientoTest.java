import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.service.ServiceContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/* Test focalizado de los parsers productivos y sus entradas publicas.
 * Patron: ComprasOrdenMedicaPermisoTest (reflection/servicios sustituidos).
 * Referencia funcional: liquidaciones/editar_liquidacion.jsp, fechaEmision > hoy.
 * No DB, Document Library, red, Tomcat ni persistencia. */
public class ComprasFechasRequerimientoTest {
    private static class DocumentoDePrueba extends DocumentoLibraryComprasHelper {
        DocumentoDePrueba(ServiceContext contexto) throws Exception { super(contexto); }
    }
    private static int checks;
    private static int failed;
    private static Method documento;
    private static Method presupuesto;
    private static DocumentoLibraryComprasHelper DOCUMENTOS;
    private static final RequerimientoCompraComparativaHelper COMPARATIVA = new RequerimientoCompraComparativaHelper();

    public static void main(String[] args) throws Exception {
        ServiceContext contexto = new ServiceContext();
        contexto.setScopeGroupId(1L);
        contexto.setUserId(1L);
        DOCUMENTOS = new DocumentoDePrueba(contexto);
        documento = DocumentoLibraryComprasHelper.class.getDeclaredMethod("parseFechaDocumento", new Class[] {String.class});
        documento.setAccessible(true);
        presupuesto = RequerimientoCompraComparativaHelper.class.getDeclaredMethod("fecha", new Class[] {String.class});
        presupuesto.setAccessible(true);
        System.out.println("Timezone=" + TimeZone.getDefault().getID());
        Calendar hoy = Calendar.getInstance();
        probarFecha("HOY", hoy.getTime(), false);
        Calendar ayer = (Calendar) hoy.clone();
        ayer.add(Calendar.DATE, -1);
        probarFecha("AYER", ayer.getTime(), false);
        Calendar manana = (Calendar) hoy.clone();
        manana.add(Calendar.DATE, 1);
        probarFecha("MANANA", manana.getTime(), true);
        Calendar finAnio = (Calendar) hoy.clone();
        finAnio.set(Calendar.MONTH, Calendar.DECEMBER);
        finAnio.set(Calendar.DATE, 31);
        if (finAnio.get(Calendar.DAY_OF_YEAR) > hoy.get(Calendar.DAY_OF_YEAR)) {
            probarFecha("FUTURO_MISMO_ANIO", finAnio.getTime(), true);
        }
        Calendar otroMes = (Calendar) hoy.clone();
        otroMes.set(Calendar.DATE, 1);
        otroMes.add(Calendar.MONTH, 1);
        probarFecha("PRIMER_DIA_MES_SIGUIENTE", otroMes.getTime(), true);
        Calendar otroAnio = (Calendar) hoy.clone();
        otroAnio.add(Calendar.YEAR, 1);
        otroAnio.set(Calendar.MONTH, Calendar.JANUARY);
        otroAnio.set(Calendar.DATE, 1);
        probarFecha("PRIMER_DIA_ANIO_SIGUIENTE", otroAnio.getTime(), true);
        comprobar("ADJUNTO_BISIESTO_VALIDO", documento, DOCUMENTOS, "2024-02-29", false, null);
        comprobar("PRESUPUESTO_BISIESTO_VALIDO", presupuesto, COMPARATIVA, "29/02/2024", false, null);
        comprobar("ADJUNTO_NO_BISIESTO", documento, DOCUMENTOS, "2025-02-29", true, null);
        comprobar("PRESUPUESTO_NO_BISIESTO", presupuesto, COMPARATIVA, "29/02/2025", true, null);
        comprobar("ADJUNTO_DIA_INVALIDO", documento, DOCUMENTOS, "2025-04-31", true, null);
        comprobar("PRESUPUESTO_DIA_INVALIDO", presupuesto, COMPARATIVA, "31/04/2025", true, null);
        comprobar("ADJUNTO_FORMATO_INVALIDO", documento, DOCUMENTOS, "fecha", true, null);
        comprobar("PRESUPUESTO_FORMATO_INVALIDO", presupuesto, COMPARATIVA, "01/01/2025x", true, null);
        comprobar("ADJUNTO_OBLIGATORIO", documento, DOCUMENTOS, "", true, null);
        comprobar("ADJUNTO_OBLIGATORIO_NULO", documento, DOCUMENTOS, null, true, null);
        verificar("PRESUPUESTO_OPCIONAL_VACIO", invocar(presupuesto, COMPARATIVA, "") == null);
        verificar("PRESUPUESTO_OPCIONAL_NULO", invocar(presupuesto, COMPARATIVA, null) == null);
        entradaOrdenMedicaFutura(manana.getTime());
        entradaComparativa(manana.getTime(), true);
        entradaComparativa(hoy.getTime(), false);
        System.out.println("checks=" + checks + " failed=" + failed);
        if (failed > 0) { throw new AssertionError("Validaciones de fecha fallidas: " + failed); }
    }

    private static void probarFecha(String nombre, Date fecha, boolean rechazar) throws Exception {
        comprobar("ADJUNTO_" + nombre, documento, DOCUMENTOS,
                new SimpleDateFormat("yyyy-MM-dd").format(fecha), rechazar, rechazar ? "hoy" : null);
        comprobar("PRESUPUESTO_" + nombre, presupuesto, COMPARATIVA,
                new SimpleDateFormat("dd/MM/yyyy").format(fecha), rechazar, rechazar ? "hoy" : null);
    }

    private static Object invocar(Method metodo, Object helper, String valor) throws Exception {
        try { return metodo.invoke(helper, new Object[] {valor}); }
        catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            if (causa instanceof Exception) { throw (Exception) causa; }
            throw e;
        }
    }

    private static void comprobar(String nombre, Method metodo, Object helper,
            String valor, boolean rechazar, String mensaje) throws Exception {
        try {
            Object resultado = invocar(metodo, helper, valor);
            verificar(nombre, !rechazar && resultado instanceof Date);
        } catch (Exception e) {
            verificar(nombre, rechazar && (mensaje == null ||
                    (e.getMessage() != null && e.getMessage().toLowerCase().indexOf(mensaje) >= 0)));
        }
    }

    private static void entradaOrdenMedicaFutura(Date futuro) throws Exception {
        final int[] consultasArchivo = new int[] {0};
        UploadPortletRequest request = (UploadPortletRequest) Proxy.newProxyInstance(
                UploadPortletRequest.class.getClassLoader(), new Class[] {UploadPortletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getFile".equals(method.getName())) { consultasArchivo[0]++; }
                        return null;
                    }
                });
        boolean rechazoFecha = false;
        try {
            DOCUMENTOS.validarOrdenMedica(request, "orden_medica",
                    new SimpleDateFormat("yyyy-MM-dd").format(futuro), null);
        } catch (Exception e) {
            rechazoFecha = e.getMessage() != null && e.getMessage().toLowerCase().indexOf("hoy") >= 0;
        }
        verificar("REQUEST_ORDEN_MEDICA_RECHAZA_FUTURO", rechazoFecha);
        verificar("REQUEST_ORDEN_MEDICA_VALIDA_ANTES_ARCHIVO", consultasArchivo[0] == 0);
    }

    private static void entradaComparativa(Date fecha, boolean rechazar) {
        RequerimientoCompraComparativa fila = new RequerimientoCompraComparativa();
        fila.setIdPrestador(7);
        List<RequerimientoCompraComparativa> filas = new ArrayList<RequerimientoCompraComparativa>();
        filas.add(fila);
        Map<String, String> entrada = new HashMap<String, String>();
        entrada.put("prestador_7", "7");
        entrada.put("fecha_7", new SimpleDateFormat("dd/MM/yyyy").format(fecha));
        entrada.put("pago_7", "30");
        boolean rechazada = false;
        boolean completada = false;
        try { COMPARATIVA.aplicarEntrada(filas, entrada); completada = true; }
        catch (IllegalArgumentException e) {
            rechazada = e.getMessage().indexOf("Prestador 7:") >= 0
                    && e.getMessage().toLowerCase().indexOf("hoy") >= 0;
        }
        verificar(rechazar ? "REQUEST_COMPARATIVA_RECHAZA_FUTURO" : "REQUEST_COMPARATIVA_ACEPTA_HOY",
                rechazar ? rechazada : completada && fila.getFechaPresupuesto() != null);
    }

    private static void verificar(String nombre, boolean correcto) {
        checks++;
        if (!correcto) { failed++; }
        System.out.println((correcto ? "OK " : "FAIL ") + nombre);
    }
}