package ar.com.ospim.autorizaciones.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Dispara reintentos de outbox fuera del request del usuario.
 *
 * No crea un cron adicional: se activa oportunísticamente desde operaciones
 * frecuentes del módulo y limita el trabajo a una ejecución por minuto.
 */
public final class ReclamoAppMobileOutboxDispatcher {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileOutboxDispatcher.class
    );

    private static final long INTERVALO_MINIMO_MS = 60000L;
    private static final int TAMANIO_LOTE = 20;
    private static final AtomicBoolean EN_EJECUCION = new AtomicBoolean(false);
    private static final AtomicLong ULTIMO_DISPARO = new AtomicLong(0L);

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor(new ThreadFactory() {
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(
                            runnable,
                            "reclamo-appmobile-outbox"
                    );
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                }
            });

    private ReclamoAppMobileOutboxDispatcher() {
    }

    /**
     * Programa un intento si no existe otro en curso y transcurrió el intervalo
     * mínimo. Retorna inmediatamente.
     */
    public static void disparar() {
        long ahora = System.currentTimeMillis();
        long ultimo = ULTIMO_DISPARO.get();

        if (ahora - ultimo < INTERVALO_MINIMO_MS) {
            return;
        }
        if (!ULTIMO_DISPARO.compareAndSet(ultimo, ahora)) {
            return;
        }
        if (!EN_EJECUCION.compareAndSet(false, true)) {
            return;
        }

        try {
            EXECUTOR.execute(new Runnable() {
                public void run() {
                    try {
                        int procesados = ReclamoAppMobileOutboxService
                                .procesarPendientes(TAMANIO_LOTE);
                        if (procesados > 0) {
                            _log.info("Outbox AppMobile procesada. cantidad="
                                    + procesados);
                        }
                    } catch (Exception e) {
                        _log.error(
                                "No se pudo procesar outbox AppMobile.",
                                e
                        );
                    } finally {
                        EN_EJECUCION.set(false);
                    }
                }
            });
        } catch (RuntimeException e) {
            EN_EJECUCION.set(false);
            _log.error("No se pudo programar outbox AppMobile.", e);
        }
    }

    static boolean estaEnEjecucion() {
        return EN_EJECUCION.get();
    }

    static long getIntervaloMinimoMs() {
        return INTERVALO_MINIMO_MS;
    }
}
