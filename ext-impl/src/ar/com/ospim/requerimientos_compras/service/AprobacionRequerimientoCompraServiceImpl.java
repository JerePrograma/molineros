package ar.com.ospim.requerimientos_compras.service;

import java.sql.CallableStatement;
import java.sql.Connection;

import ar.com.ospim.requerimientos_compras.WebKeysRequerimientosCompras;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AprobacionRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(AprobacionRequerimientoCompraServiceImpl.class);

    public void cambiarEstado(int idRequerimientoCompra, int estadoNuevo, String comentario, String usuario) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            String sql = "{call cambiar_estado_requerimiento_compra(?,?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, estadoNuevo);
            stmt.setString(3, comentario);
            stmt.setString(4, usuario);
            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean validarCambioEstado(int estadoActual, int estadoNuevo) {
        if (estadoActual == WebKeysRequerimientosCompras.ESTADO_BORRADOR) {
            return estadoNuevo == WebKeysRequerimientosCompras.ESTADO_PENDIENTE_APROBACION
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysRequerimientosCompras.ESTADO_PENDIENTE_APROBACION) {
            return estadoNuevo == WebKeysRequerimientosCompras.ESTADO_APROBADO
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_OBSERVADO
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_RECHAZADO
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysRequerimientosCompras.ESTADO_OBSERVADO) {
            return estadoNuevo == WebKeysRequerimientosCompras.ESTADO_PENDIENTE_APROBACION
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysRequerimientosCompras.ESTADO_APROBADO) {
            return estadoNuevo == WebKeysRequerimientosCompras.ESTADO_EN_COMPRA
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysRequerimientosCompras.ESTADO_EN_COMPRA) {
            return estadoNuevo == WebKeysRequerimientosCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysRequerimientosCompras.ESTADO_ANULADO;
        }

        return false;
    }
}
