package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Diagnóstico no bloqueante de deudas preexistentes del servidor de Reclamos.
 *
 * No pertenece al cambio de selector/editor. Se conserva separado para que el
 * gate focalizado no fuerce modificaciones de servidor fuera de alcance.
 */
public final class ReclamosBaseActionLegacyDiagnosticTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamosBaseActionLegacyDiagnosticTest() {
    }

    public static void main(String[] args) throws Exception {
        String baseAction = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "ReclamosBaseAction.java"
        );
        String entryAction = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamosPrestacionesServiceUtil.java"
        );

        List<String> deudas = new ArrayList<String>();

        diagnosticar(baseAction.indexOf("parseEvaluacionReclamo(evaluacion)") >= 0,
                "falta el parser centralizado de evaluación", deudas);
        diagnosticar(baseAction.indexOf("\"AUTORIZADA\".equals(normalizada)") >= 0,
                "falta aceptar el enum AUTORIZADA", deudas);
        diagnosticar(baseAction.indexOf("\"RECHAZADA\".equals(normalizada)") >= 0,
                "falta aceptar el enum RECHAZADA", deudas);
        diagnosticar(baseAction.indexOf(
                "Los datos del Reclamo Prestacional son inválidos."
        ) >= 0, "falta reconstrucción fail closed", deudas);
        diagnosticar(baseAction.indexOf(
                "tipoGestionCierreReclamo <= 0 && tipoGestionVisible > 0"
        ) >= 0, "falta fallback de gestión visible", deudas);
        diagnosticar(baseAction.indexOf("evaluacion  == \"Autorizado\"") < 0,
                "persiste comparación String por identidad para Autorizado", deudas);
        diagnosticar(baseAction.indexOf("evaluacion  == \"Rechazado\"") < 0,
                "persiste comparación String por identidad para Rechazado", deudas);

        String deleteBlock = bloque(
                entryAction,
                "if(cmd.equals(Constants.DELETE))",
                "if(cmd.equals(\"cerrar\"))"
        );
        diagnosticar(deleteBlock.indexOf(
                "getReclamoPrestacional(idReclamoDeBuscador)"
        ) < 0, "DELETE relee el reclamo después de invocar borrar", deudas);
        diagnosticar(deleteBlock.indexOf("ClienteAppMobile") < 0,
                "DELETE sincroniza AppMobile directamente además del servicio", deudas);
        diagnosticar(service.indexOf("BAJAS_RECIENTES") < 0,
                "persiste cache temporal BAJAS_RECIENTES", deudas);
        diagnosticar(service.indexOf("esBajaReciente") < 0,
                "persiste guard temporal esBajaReciente", deudas);
        diagnosticar(service.indexOf("registrarBajaReciente") < 0,
                "persiste registro temporal registrarBajaReciente", deudas);
        diagnosticar(service.indexOf(
                "ReclamoPrestacionalBajaTransaccionalService.borrar("
        ) >= 0, "falta baja transaccional centralizada", deudas);
        diagnosticar(service.indexOf("registrarOutboxSeguro(") >= 0,
                "falta outbox de fallback", deudas);

        if (!deudas.isEmpty()) {
            for (String deuda : deudas) {
                System.err.println("DEUDA_LEGACY: " + deuda);
            }
            throw new AssertionError(
                    "Se detectaron " + deudas.size()
                            + " deudas legacy de servidor fuera del alcance del PR"
            );
        }

        System.out.println("DIAGNOSTICO_RECLAMOS_BASE_ACTION_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static String bloque(String contenido, String inicio, String fin) {
        int desde = contenido.indexOf(inicio);
        int hasta = contenido.indexOf(fin, desde);
        if (desde < 0 || hasta <= desde) {
            throw new AssertionError(
                    "No se pudo delimitar el bloque entre ["
                            + inicio + "] y [" + fin + "]"
            );
        }
        return contenido.substring(desde, hasta);
    }

    private static void diagnosticar(
            boolean condicion,
            String mensaje,
            List<String> deudas) {
        if (!condicion) {
            deudas.add(mensaje);
        }
    }
}
