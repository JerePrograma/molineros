package ar.com.ospim.autorizaciones.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

/**
 * Operaciones de outbox usadas por el intento inmediato posterior a la baja.
 */
public final class ReclamoAppMobileOutboxDirectService {

    private static final Log _log = LogFactoryUtil.getLog(
            ReclamoAppMobileOutboxDirectService.class
    );

    private ReclamoAppMobileOutboxDirectService() {
    }

    public static void confirmarProcesado(
            int idReintegroApp,
            String estadoDestino) throws SystemException {

        if (idReintegroApp <= 0) {
            throw new IllegalArgumentException(
                    "El id de reintegro externo debe ser positivo."
            );
        }
        if (estadoDestino == null || estadoDestino.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "El estado destino es obligatorio."
            );
        }

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "UPDATE autorizaciones.reclamo_appmobile_outbox "
                    + "SET estado_proceso = 'PROCESADO', "
                    + "procesado_en = NOW(), bloqueado_hasta = NULL, "
                    + "ultimo_error = NULL, actualizado_en = NOW() "
                    + "WHERE id_reintegro_app = ? "
                    + "AND estado_destino = ? "
                    + "AND procesado_en IS NULL"
            );
            stmt.setInt(1, idReintegroApp);
            stmt.setString(2, estadoDestino.trim());
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            ConnectionHelper.rollback(con);
            _log.error("No se pudo confirmar outbox AppMobile. reintegroApp="
                    + idReintegroApp
                    + " estado=" + estadoDestino, e);
            throw new SystemException(e);
        } finally {
            cerrar(stmt);
            cerrar(con);
        }
    }

    private static void cerrar(AutoCloseable recurso) {
        if (recurso == null) {
            return;
        }
        try {
            recurso.close();
        } catch (Exception e) {
            _log.debug("No se pudo cerrar recurso de confirmación outbox.", e);
        }
    }
}
