package ar.com.ospim.compras.service;

import java.sql.CallableStatement;
import java.sql.Connection;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AprobacionRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(AprobacionRequerimientoCompraServiceImpl.class);

    public void cambiarEstado(int idRequerimientoCompra, int estadoNuevo, String comentario, String usuario)
            throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);

        validarCambioEstadoRequerimiento(requerimiento, estadoNuevo, comentario);

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
        if (estadoActual == WebKeysCompras.ESTADO_BORRADOR) {
            return estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_APROBACION
                    || estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_COTIZACION
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_PENDIENTE_APROBACION) {
            return estadoNuevo == WebKeysCompras.ESTADO_APROBADO
                    || estadoNuevo == WebKeysCompras.ESTADO_OBSERVADO
                    || estadoNuevo == WebKeysCompras.ESTADO_RECHAZADO
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_OBSERVADO) {
            return estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_APROBACION
                    || estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_COTIZACION
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_APROBADO) {
            return estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_COTIZACION
                    || estadoNuevo == WebKeysCompras.ESTADO_COTIZADO
                    || estadoNuevo == WebKeysCompras.ESTADO_EN_COMPRA
                    || estadoNuevo == WebKeysCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_PENDIENTE_COTIZACION) {
            return estadoNuevo == WebKeysCompras.ESTADO_COTIZADO
                    || estadoNuevo == WebKeysCompras.ESTADO_EN_COMPRA
                    || estadoNuevo == WebKeysCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_COTIZADO) {
            return estadoNuevo == WebKeysCompras.ESTADO_EN_COMPRA
                    || estadoNuevo == WebKeysCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        if (estadoActual == WebKeysCompras.ESTADO_EN_COMPRA) {
            return estadoNuevo == WebKeysCompras.ESTADO_CERRADO
                    || estadoNuevo == WebKeysCompras.ESTADO_ANULADO;
        }

        return false;
    }

    private void validarCambioEstadoRequerimiento(RequerimientoCompra requerimiento, int estadoNuevo, String comentario)
            throws Exception {

        if (requerimiento == null || requerimiento.getIdRequerimientoCompra() <= 0) {
            throw new Exception("No se encontro el requerimiento de compra.");
        }

        if (!validarCambioEstado(requerimiento.getEstado(), estadoNuevo)) {
            throw new Exception(
                    "Cambio de estado invalido: "
                            + WebKeysCompras.getEstadoDescripcion(requerimiento.getEstado())
                            + " -> "
                            + WebKeysCompras.getEstadoDescripcion(estadoNuevo)
            );
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_APROBACION
                || estadoNuevo == WebKeysCompras.ESTADO_APROBADO
                || estadoNuevo == WebKeysCompras.ESTADO_PENDIENTE_COTIZACION
                || estadoNuevo == WebKeysCompras.ESTADO_COTIZADO
                || estadoNuevo == WebKeysCompras.ESTADO_EN_COMPRA) {

            if (isEmpty(requerimiento.getMotivo()) && isEmpty(requerimiento.getDetalleRequerimiento())) {
                throw new Exception("El requerimiento debe tener detalle o motivo cargado.");
            }
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_OBSERVADO
                || estadoNuevo == WebKeysCompras.ESTADO_RECHAZADO
                || estadoNuevo == WebKeysCompras.ESTADO_ANULADO) {

            if (isEmpty(comentario)) {
                throw new Exception("Debe informar un comentario para observar, rechazar o anular.");
            }
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
