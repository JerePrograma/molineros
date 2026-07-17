package ar.com.ospim.autorizaciones.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

/**
 * Outbox durable para sincronizar estados de Reclamos Prestacionales con
 * AppMobile.
 *
 * Requiere ejecutar previamente:
 * sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
 */
public final class ReclamoAppMobileOutboxService {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileOutboxService.class
    );

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PROCESANDO = "PROCESANDO";
    private static final String ESTADO_PROCESADO = "PROCESADO";
    private static final int MAX_ERROR_LENGTH = 2000;
    private static final int LEASE_MINUTOS = 5;

    private ReclamoAppMobileOutboxService() {
    }

    public static void registrarPendiente(
            int idReclamo,
            int idReintegroApp,
            String estadoDestino,
            String motivo) throws SystemException {

        validarEvento(idReintegroApp, estadoDestino);

        Connection con = null;
        PreparedStatement update = null;
        PreparedStatement insert = null;

        try {
            con = ConnectionHelper.getConnection();

            update = con.prepareStatement(
                    "UPDATE autorizaciones.reclamo_appmobile_outbox "
                    + "SET id_reclamo = ?, estado_proceso = ?, "
                    + "proximo_intento = NOW(), bloqueado_hasta = NULL, "
                    + "ultimo_error = ?, actualizado_en = NOW() "
                    + "WHERE id_reintegro_app = ? "
                    + "AND estado_destino = ? "
                    + "AND procesado_en IS NULL"
            );
            update.setInt(1, idReclamo);
            update.setString(2, ESTADO_PENDIENTE);
            update.setString(3, limitar(motivo));
            update.setInt(4, idReintegroApp);
            update.setString(5, estadoDestino.trim());

            int actualizados = update.executeUpdate();
            if (actualizados == 0) {
                insert = con.prepareStatement(
                        "INSERT INTO autorizaciones.reclamo_appmobile_outbox "
                        + "(id_reclamo, id_reintegro_app, estado_destino, "
                        + "estado_proceso, intentos, proximo_intento, "
                        + "ultimo_error, creado_en, actualizado_en) "
                        + "VALUES (?, ?, ?, ?, 0, NOW(), ?, NOW(), NOW())"
                );
                insert.setInt(1, idReclamo);
                insert.setInt(2, idReintegroApp);
                insert.setString(3, estadoDestino.trim());
                insert.setString(4, ESTADO_PENDIENTE);
                insert.setString(5, limitar(motivo));
                insert.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo registrar outbox AppMobile. reclamo="
                    + idReclamo
                    + " reintegroApp=" + idReintegroApp
                    + " estado=" + estadoDestino, e);
            throw new SystemException(e);
        } finally {
            cerrar(insert);
            cerrar(update);
            cerrar(con);
        }
    }

    public static int procesarPendientes(int limite) throws SystemException {
        int limiteSeguro = Math.max(1, Math.min(limite, 100));
        List<EventoOutbox> candidatos = buscarCandidatos(limiteSeguro);
        if (candidatos.isEmpty()) {
            return 0;
        }

        String token = ReclamoAppMobileAuthClient.obtenerToken();
        if (token == null || token.trim().length() == 0) {
            _log.warn("No se procesó outbox AppMobile: token no disponible.");
            return 0;
        }

        int procesados = 0;
        for (EventoOutbox evento : candidatos) {
            if (!tomarLease(evento.getId())) {
                continue;
            }

            boolean confirmado = ReclamoAppMobileSyncClient
                    .actualizarEstadoReintegro(
                            evento.getIdReintegroApp(),
                            evento.getEstadoDestino(),
                            token
                    );

            if (confirmado) {
                marcarProcesado(evento.getId());
                procesados++;
            } else {
                marcarError(
                        evento.getId(),
                        evento.getIntentos() + 1,
                        "HTTP_NO_CONFIRMADO"
                );
            }
        }

        return procesados;
    }

    private static List<EventoOutbox> buscarCandidatos(int limite)
            throws SystemException {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<EventoOutbox> eventos = new ArrayList<EventoOutbox>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "SELECT id, id_reclamo, id_reintegro_app, "
                    + "estado_destino, intentos, proximo_intento "
                    + "FROM autorizaciones.reclamo_appmobile_outbox "
                    + "WHERE procesado_en IS NULL "
                    + "AND proximo_intento <= NOW() "
                    + "AND (estado_proceso = ? OR "
                    + "(estado_proceso = ? AND bloqueado_hasta < NOW())) "
                    + "ORDER BY id ASC LIMIT ?"
            );
            stmt.setString(1, ESTADO_PENDIENTE);
            stmt.setString(2, ESTADO_PROCESANDO);
            stmt.setInt(3, limite);

            rs = stmt.executeQuery();
            while (rs.next()) {
                eventos.add(new EventoOutbox(
                        rs.getLong("id"),
                        rs.getInt("id_reclamo"),
                        rs.getInt("id_reintegro_app"),
                        rs.getString("estado_destino"),
                        rs.getInt("intentos"),
                        rs.getTimestamp("proximo_intento")
                ));
            }

            return eventos;
        } catch (SQLException e) {
            _log.error("No se pudo consultar outbox AppMobile.", e);
            throw new SystemException(e);
        } finally {
            cerrar(rs);
            cerrar(stmt);
            cerrar(con);
        }
    }

    private static boolean tomarLease(long id) throws SystemException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "UPDATE autorizaciones.reclamo_appmobile_outbox "
                    + "SET estado_proceso = ?, "
                    + "bloqueado_hasta = NOW() + INTERVAL '"
                    + LEASE_MINUTOS
                    + " minutes', intentos = intentos + 1, "
                    + "actualizado_en = NOW() "
                    + "WHERE id = ? AND procesado_en IS NULL "
                    + "AND proximo_intento <= NOW() "
                    + "AND (estado_proceso = ? OR "
                    + "(estado_proceso = ? AND bloqueado_hasta < NOW()))"
            );
            stmt.setString(1, ESTADO_PROCESANDO);
            stmt.setLong(2, id);
            stmt.setString(3, ESTADO_PENDIENTE);
            stmt.setString(4, ESTADO_PROCESANDO);

            boolean tomado = stmt.executeUpdate() == 1;
            con.commit();
            return tomado;
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo tomar lease de outbox id=" + id, e);
            throw new SystemException(e);
        } finally {
            cerrar(stmt);
            cerrar(con);
        }
    }

    private static void marcarProcesado(long id) throws SystemException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "UPDATE autorizaciones.reclamo_appmobile_outbox "
                    + "SET estado_proceso = ?, procesado_en = NOW(), "
                    + "bloqueado_hasta = NULL, ultimo_error = NULL, "
                    + "actualizado_en = NOW() WHERE id = ?"
            );
            stmt.setString(1, ESTADO_PROCESADO);
            stmt.setLong(2, id);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo marcar outbox procesado id=" + id, e);
            throw new SystemException(e);
        } finally {
            cerrar(stmt);
            cerrar(con);
        }
    }

    private static void marcarError(long id, int intentos, String error)
            throws SystemException {

        int demoraMinutos = calcularDemoraMinutos(intentos);
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "UPDATE autorizaciones.reclamo_appmobile_outbox "
                    + "SET estado_proceso = ?, bloqueado_hasta = NULL, "
                    + "proximo_intento = NOW() + (? * INTERVAL '1 minute'), "
                    + "ultimo_error = ?, actualizado_en = NOW() "
                    + "WHERE id = ? AND procesado_en IS NULL"
            );
            stmt.setString(1, ESTADO_PENDIENTE);
            stmt.setInt(2, demoraMinutos);
            stmt.setString(3, limitar(error));
            stmt.setLong(4, id);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo reprogramar outbox id=" + id, e);
            throw new SystemException(e);
        } finally {
            cerrar(stmt);
            cerrar(con);
        }
    }

    private static int calcularDemoraMinutos(int intentos) {
        int exponente = Math.max(0, Math.min(intentos - 1, 6));
        int demora = 1 << exponente;
        return Math.min(demora, 60);
    }

    private static void validarEvento(
            int idReintegroApp,
            String estadoDestino) {

        if (idReintegroApp <= 0) {
            throw new IllegalArgumentException(
                    "El id de reintegro externo debe ser positivo."
            );
        }
        if (estadoDestino == null || estadoDestino.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "El estado destino de AppMobile es obligatorio."
            );
        }
    }

    private static String limitar(String valor) {
        if (valor == null) {
            return null;
        }
        if (valor.length() <= MAX_ERROR_LENGTH) {
            return valor;
        }
        return valor.substring(0, MAX_ERROR_LENGTH);
    }

    private static void cerrar(AutoCloseable recurso) {
        if (recurso == null) {
            return;
        }
        try {
            recurso.close();
        } catch (Exception e) {
            _log.debug("No se pudo cerrar recurso de outbox.", e);
        }
    }

    private static final class EventoOutbox {
        private final long id;
        private final int idReclamo;
        private final int idReintegroApp;
        private final String estadoDestino;
        private final int intentos;
        private final Timestamp proximoIntento;

        private EventoOutbox(
                long id,
                int idReclamo,
                int idReintegroApp,
                String estadoDestino,
                int intentos,
                Timestamp proximoIntento) {

            this.id = id;
            this.idReclamo = idReclamo;
            this.idReintegroApp = idReintegroApp;
            this.estadoDestino = estadoDestino;
            this.intentos = intentos;
            this.proximoIntento = proximoIntento;
        }

        private long getId() {
            return id;
        }

        @SuppressWarnings("unused")
        private int getIdReclamo() {
            return idReclamo;
        }

        private int getIdReintegroApp() {
            return idReintegroApp;
        }

        private String getEstadoDestino() {
            return estadoDestino;
        }

        private int getIntentos() {
            return intentos;
        }

        @SuppressWarnings("unused")
        private Timestamp getProximoIntento() {
            return proximoIntento;
        }
    }
}
