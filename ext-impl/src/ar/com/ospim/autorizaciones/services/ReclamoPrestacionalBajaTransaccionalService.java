package ar.com.ospim.autorizaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarReclamoPrestacionalException;
import ar.com.ospim.util.ConnectionHelper;

/**
 * Ejecuta la baja local y el alta/reactivación de outbox en una única
 * transacción PostgreSQL.
 */
public final class ReclamoPrestacionalBajaTransaccionalService {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoPrestacionalBajaTransaccionalService.class
    );

    private static final String ESTADO_DESTINO_ANULADO = "AN";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private ReclamoPrestacionalBajaTransaccionalService() {
    }

    public static void borrar(
            int idReclamo,
            String screenName,
            Integer idReintegroApp)
            throws SystemException,
            ImposibleBorrarReclamoPrestacionalException {

        Connection con = null;
        CallableStatement baja = null;
        ResultSet resultado = null;

        try {
            con = ConnectionHelper.getConnection();
            baja = con.prepareCall(
                    "{call autorizaciones.borra_reclamo_prestacional(?, ?)}"
            );
            baja.setInt(1, idReclamo);
            baja.setString(2, screenName);

            resultado = baja.executeQuery();
            while (resultado.next()) {
                if (resultado.getInt(1) == 0) {
                    ConnectionHelper.rollback(con);
                    throw new ImposibleBorrarReclamoPrestacionalException();
                }
            }

            if (idReintegroApp != null && idReintegroApp.intValue() > 0) {
                registrarOutboxEnTransaccion(
                        con,
                        idReclamo,
                        idReintegroApp.intValue()
                );
            }

            con.commit();
        } catch (ImposibleBorrarReclamoPrestacionalException e) {
            throw e;
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo completar baja transaccional. reclamo="
                    + idReclamo
                    + " reintegroApp=" + idReintegroApp, e);
            throw new SystemException(e);
        } finally {
            cerrar(resultado);
            cerrar(baja);
            cerrar(con);
        }
    }

    private static void registrarOutboxEnTransaccion(
            Connection con,
            int idReclamo,
            int idReintegroApp) throws SQLException {

        PreparedStatement update = null;
        PreparedStatement insert = null;

        try {
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
            update.setString(3, "BAJA_LOCAL_CONFIRMADA");
            update.setInt(4, idReintegroApp);
            update.setString(5, ESTADO_DESTINO_ANULADO);

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
                insert.setString(3, ESTADO_DESTINO_ANULADO);
                insert.setString(4, ESTADO_PENDIENTE);
                insert.setString(5, "BAJA_LOCAL_CONFIRMADA");
                insert.executeUpdate();
            }
        } finally {
            cerrar(insert);
            cerrar(update);
        }
    }

    private static void cerrar(AutoCloseable recurso) {
        if (recurso == null) {
            return;
        }
        try {
            recurso.close();
        } catch (Exception e) {
            _log.debug("No se pudo cerrar recurso de baja transaccional.", e);
        }
    }
}
