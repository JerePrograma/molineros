package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Diagnóstico no bloqueante de deudas preexistentes en ReclamosBaseAction.
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

        exigir(baseAction.indexOf("parseEvaluacionReclamo(evaluacion)") >= 0,
                "falta el parser centralizado de evaluación");
        exigir(baseAction.indexOf("\"AUTORIZADA\".equals(normalizada)") >= 0,
                "falta aceptar el enum AUTORIZADA");
        exigir(baseAction.indexOf("\"RECHAZADA\".equals(normalizada)") >= 0,
                "falta aceptar el enum RECHAZADA");
        exigir(baseAction.indexOf(
                "Los datos del Reclamo Prestacional son inválidos."
        ) >= 0, "falta reconstrucción fail closed");
        exigir(baseAction.indexOf(
                "tipoGestionCierreReclamo <= 0 && tipoGestionVisible > 0"
        ) >= 0, "falta fallback de gestión visible");
        exigir(baseAction.indexOf("evaluacion  == \"Autorizado\"") < 0,
                "persiste comparación String por identidad para Autorizado");
        exigir(baseAction.indexOf("evaluacion  == \"Rechazado\"") < 0,
                "persiste comparación String por identidad para Rechazado");

        System.out.println("DIAGNOSTICO_RECLAMOS_BASE_ACTION_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
