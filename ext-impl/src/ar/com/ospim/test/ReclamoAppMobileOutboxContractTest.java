package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de la outbox durable de Reclamos Prestacionales.
 */
public final class ReclamoAppMobileOutboxContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoAppMobileOutboxContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String schema = leer(
                "sql/postgresql/autorizaciones/"
                        + "reclamo_appmobile_outbox.sql"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileOutboxService.java"
        );
        String dispatcher = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileOutboxDispatcher.java"
        );
        String reclamos = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamosPrestacionesServiceUtil.java"
        );

        assertContains(
                "tabla durable",
                schema,
                "autorizaciones.reclamo_appmobile_outbox"
        );
        assertContains(
                "estados restringidos",
                schema,
                "CHECK (estado_proceso IN ('PENDIENTE', 'PROCESANDO', 'PROCESADO'))"
        );
        assertContains(
                "evento pendiente único",
                schema,
                "ux_reclamo_appmobile_outbox_pendiente"
        );
        assertContains(
                "único sólo para no procesados",
                schema,
                "WHERE procesado_en IS NULL"
        );
        assertContains(
                "índice operativo",
                schema,
                "ix_reclamo_appmobile_outbox_proceso"
        );
        assertContains(
                "transacción de esquema",
                schema,
                "BEGIN;"
        );
        assertContains(
                "commit de esquema",
                schema,
                "COMMIT;"
        );

        assertContains(
                "registro idempotente",
                service,
                "AND procesado_en IS NULL"
        );
        assertContains(
                "reactiva pendiente",
                service,
                "estado_proceso = ?, proximo_intento = NOW()"
        );
        assertContains(
                "lease temporal",
                service,
                "bloqueado_hasta = NOW() + INTERVAL"
        );
        assertContains(
                "incrementa intentos al reclamar",
                service,
                "intentos = intentos + 1"
        );
        assertContains(
                "recupera lease vencido",
                service,
                "bloqueado_hasta < NOW()"
        );
        assertContains(
                "usa cliente confirmado",
                service,
                "ReclamoAppMobileSyncClient"
        );
        assertContains(
                "marca procesado",
                service,
                "procesado_en = NOW()"
        );
        assertContains(
                "reprograma error",
                service,
                "proximo_intento = NOW() + (? * INTERVAL '1 minute')"
        );
        assertContains(
                "backoff acotado",
                service,
                "return Math.min(demora, 60)"
        );
        assertContains(
                "límite de lote",
                service,
                "Math.min(limite, 100)"
        );
        assertContains(
                "rollback de escritura",
                service,
                "ConnectionHelper.rollback(con)"
        );

        assertBefore(
                "lease antes de sincronización",
                service,
                "if (!tomarLease(evento.getId()))",
                "ReclamoAppMobileSyncClient"
        );
        assertBefore(
                "confirmación antes de procesado",
                service,
                "boolean confirmado = ReclamoAppMobileSyncClient",
                "marcarProcesado(evento.getId())"
        );

        assertContains(
                "scheduler único",
                dispatcher,
                "newSingleThreadScheduledExecutor"
        );
        assertContains(
                "hilo daemon",
                dispatcher,
                "thread.setDaemon(true)"
        );
        assertContains(
                "inicio idempotente",
                dispatcher,
                "INICIADO.compareAndSet(false, true)"
        );
        assertContains(
                "ejecución periódica",
                dispatcher,
                "scheduleWithFixedDelay"
        );
        assertContains(
                "periodicidad en minutos",
                dispatcher,
                "TimeUnit.MINUTES"
        );
        assertContains(
                "exclusión de ejecuciones simultáneas",
                dispatcher,
                "EN_EJECUCION.compareAndSet(false, true)"
        );
        assertContains(
                "lote operativo acotado",
                dispatcher,
                "TAMANIO_LOTE = 20"
        );
        assertContains(
                "procesador invocado",
                dispatcher,
                "ReclamoAppMobileOutboxService"
        );
        assertContains(
                "módulo inicia scheduler",
                reclamos,
                "ReclamoAppMobileOutboxDispatcher.iniciar();"
        );

        System.out.println("CONTRATO_OUTBOX_APPMOBILE_RECLAMO_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static void assertContains(
            String etiqueta,
            String contenido,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontró [" + esperado + "]"
            );
        }
    }

    private static void assertBefore(
            String etiqueta,
            String contenido,
            String primero,
            String segundo) {

        int posPrimero = contenido.indexOf(primero);
        int posSegundo = contenido.indexOf(segundo);

        if (posPrimero < 0 || posSegundo < 0 || posPrimero >= posSegundo) {
            throw new AssertionError(
                    etiqueta + ": orden inválido entre ["
                            + primero + "] y [" + segundo + "]"
            );
        }
    }
}
