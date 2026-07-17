package ar.com.ospim.autorizaciones.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Ejecuta reintentos de outbox fuera del request del usuario.
 *
 * Usa un único hilo daemon, permite disparos oportunísticos y mantiene un
 * barrido periódico de respaldo una vez inicializado el módulo.
 */
public final class ReclamoAppMobileOutboxDispatcher {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileOutboxDispatcher.class
    );

    private static final long INTERVALO_MINIMO_MS = 60000L;
    private static final long INTERVALO_PERIODICO_MINUTOS = 1L;
    private static final int TAMANIO_LOTE = 20;
    private static final AtomicBoolean INICIADO = new AtomicBoolean(false);
    private static final AtomicBoolean EN_EJECUCION = new AtomicBoolean(false);
    private static final AtomicLong ULTIMO_DISPARO = new AtomicLong(0L);

    private static final ScheduledExecutorService EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
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
     * Inicia una sola tarea periódica. Llamadas repetidas son idempotentes.
     */
    public static void iniciar() {
        if (!INICIADO.compareAndSet(false, true)) {
            return;
        }

        try {
            EXECUTOR.scheduleWithFixedDelay(
                    new Runnable() {
                        public void run() {
                            ejecutarLote();
                        }
                    },
                    INTERVALO_PERIODICO_MINUTOS,
                    INTERVALO_PERIODICO_MINUTOS,
                    TimeUnit.MINUTES
            );
            _log.info("Despachador outbox AppMobile iniciado.");
        } catch (RuntimeException e) {
            INICIADO.set(false);
            _log.error("No se pudo iniciar despachador outbox AppMobile.", e);
        }
    }

    /**
     * Programa un intento inmediato si no existe otro en curso y transcurrió el
     * intervalo mínimo. Retorna sin esperar base ni red.
     */
    public static void disparar() {
        iniciar();

        long ahora = System.currentTimeMillis();
        long ultimo = ULTIMO_DISPARO.get();

        if (ahora - ultimo < INTERVALO_MINIMO_MS) {
            return;
        }
        if (!ULTIMO_DISPARO.compareAndSet(ultimo, ahora)) {
            return;
        }

        try {
            EXECUTOR.execute(new Runnable() {
                public void run() {
                    ejecutarLote();
                }
            });
        } catch (RuntimeException e) {
            _log.error("No se pudo programar outbox AppMobile.", e);
        }
    }

    private static void ejecutarLote() {
        if (!EN_EJECUCION.compareAndSet(false, true)) {
            return;
        }

        try {
            int procesados = ReclamoAppMobileOutboxService
                    .procesarPendientes(TAMANIO_LOTE);
            if (procesados > 0) {
                _log.info("Outbox AppMobile procesada. cantidad="
                        + procesados);
            }
        } catch (Exception e) {
            _log.error("No se pudo procesar outbox AppMobile.", e);
        } finally {
            ULTIMO_DISPARO.set(System.currentTimeMillis());
            EN_EJECUCION.set(false);
        }
    }

    static boolean estaIniciado() {
        return INICIADO.get();
    }

    static boolean estaEnEjecucion() {
        return EN_EJECUCION.get();
    }

    static long getIntervaloMinimoMs() {
        return INTERVALO_MINIMO_MS;
    }
}
