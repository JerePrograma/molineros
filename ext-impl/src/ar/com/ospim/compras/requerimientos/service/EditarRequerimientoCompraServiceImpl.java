package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class EditarRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);


    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final String SQL_GUARDAR_REQUERIMIENTO =
            "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_GUARDAR_REQUERIMIENTO_DETALLE =
            "{ ? = call compras.guardar_requerimiento_detalle(?,?,?,?,?,?) }";

    private static final String SQL_BORRAR_REQUERIMIENTO_DETALLE =
            "{ call compras.borrar_requerimiento_detalle(?,?) }";

    private static final String SQL_BORRAR_REQUERIMIENTO =
            "{ call compras.borrar_requerimiento(?,?) }";

    private static final String SQL_CAMBIAR_ESTADO =
            "{ call compras.cambiar_estado_requerimiento(?,?,?) }";

    private static final String SQL_ACTUALIZAR_ESTADO_ESPERADO =
            "UPDATE compras.requerimiento " +
                    "SET estado = ?, modi_fecha = now(), modi_usr = ? " +
                    "WHERE id_requerimiento = ? AND estado = ? AND baja_fecha IS NULL";

    private static final String SQL_GET_REQUERIMIENTO_BLOQUEO =
            "SELECT estado, baja_fecha " +
                    "FROM compras.requerimiento " +
                    "WHERE id_requerimiento = ? " +
                    "FOR UPDATE";

    private static final String SQL_GET_DETALLES_BLOQUEO =
            "SELECT id_detalle, cantidad " +
                    "FROM compras.requerimiento_detalle " +
                    "WHERE id_requerimiento = ? AND baja_fecha IS NULL " +
                    "FOR UPDATE";

    private static final String SQL_ACTUALIZAR_DETALLE_COTIZACION =
            "UPDATE compras.requerimiento_detalle " +
                    "SET precio_unitario_estimado = ?, " +
                    "    precio_total_estimado = ?, " +
                    "    id_prestador = ?, " +
                    "    modi_fecha = now(), " +
                    "    modi_usr = ? " +
                    "WHERE id_requerimiento = ? AND id_detalle = ? AND baja_fecha IS NULL";

    private static final String SQL_EXISTE_PRESTADOR_ENVIADO =
            "SELECT 1 " +
                    "FROM compras.requerimiento_cotizacion_prestador " +
                    "WHERE id_requerimiento = ? " +
                    "  AND id_prestador = ? " +
                    "  AND estado_envio = ?";

    private static final String SQL_DETALLES_INCOMPLETOS_COTIZACION =
            "SELECT count(*) AS total, " +
                    "       sum(CASE WHEN cantidad <= 0 " +
                    "                 OR precio_unitario_estimado IS NULL " +
                    "                 OR precio_total_estimado IS NULL " +
                    "                 OR id_prestador IS NULL " +
                    "                THEN 1 ELSE 0 END) AS incompletos " +
                    "FROM compras.requerimiento_detalle " +
                    "WHERE id_requerimiento = ? AND baja_fecha IS NULL";

    private static final String SQL_LISTAR_ARTICULOS =
            "SELECT id, id_sector, sector_descripcion, descripcion " +
                    "FROM compras.listar_articulos(?, ?)";

    private static final String SQL_GET_ARTICULO =
            "SELECT id, id_sector, sector_descripcion, descripcion " +
                    "FROM compras.get_articulo(?)";

    private static final String SQL_GUARDAR_ARTICULO =
            "{ ? = call compras.guardar_articulo(?,?,?) }";

    private static final String SQL_BORRAR_ARTICULO =
            "{ call compras.borrar_articulo(?) }";

    public int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        validarRequerimientoParaGuardar(requerimiento);

        if (requerimiento.getIdRequerimientoCompra() > 0) {
            RequerimientoCompra actual =
                    BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                            requerimiento.getIdRequerimientoCompra()
                    );

            if (actual == null) {
                throw new Exception("No se encontró el requerimiento de compra informado.");
            }

            if (!actual.puedeEditarEstructura()) {
                throw new Exception(
                        "Solo se puede editar la estructura de requerimientos en estado Pendiente."
                );
            }
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, requerimiento.getId());
            stmt.setString(3, emptyToNull(requerimiento.getAfiliadoCuilTitular()));
            setNullableInteger(stmt, 4, requerimiento.getAfiliadoInt());
            stmt.setString(5, emptyToNull(requerimiento.getAfiliadoNombre()));
            stmt.setString(6, emptyToNull(requerimiento.getAfiliadoApellido()));
            stmt.setString(7, emptyToNull(requerimiento.getAfiliadoDocumentoTipo()));
            stmt.setString(8, emptyToNull(requerimiento.getAfiliadoDocumentoNro()));
            stmt.setString(9, emptyToNull(requerimiento.getAfiliadoDireccion()));
            stmt.setString(10, emptyToNull(requerimiento.getAfiliadoLocalidad()));
            stmt.setString(11, emptyToNull(requerimiento.getAfiliadoProvincia()));
            stmt.setString(12, emptyToNull(requerimiento.getAfiliadoCelular()));
            stmt.setString(13, emptyToNull(requerimiento.getAfiliadoTelefono()));
            stmt.setString(14, emptyToNull(requerimiento.getAfiliadoEmail()));
            setNullableInteger(stmt, 15, requerimiento.getIdSector());
            setNullableInteger(stmt, 16, requerimiento.getCargoOspim());
            setNullableInteger(stmt, 17, requerimiento.getCargoTercerizadora());
            stmt.setString(18, emptyToNull(requerimiento.getIdTercerizadora()));
            stmt.setBoolean(19, requerimiento.isRecupero());
            stmt.setString(20, emptyToNull(requerimiento.getObservaciones()));
            stmt.setString(21, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        validarDetalleParaGuardar(detalle);

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO_DETALLE);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, detalle.getId());
            setNullableInteger(stmt, 3, getIdRequerimientoDetalle(detalle));
            setNullableInteger(stmt, 4, detalle.getIdArticulo());
            setNullableInteger(stmt, 5, detalle.getCantidad());
            stmt.setString(6, emptyToNull(detalle.getObservaciones()));
            stmt.setString(7, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarDetalle(int idDetalle, String usuario) throws Exception {
        if (idDetalle <= 0) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_REQUERIMIENTO_DETALLE);
            stmt.setInt(1, idDetalle);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_REQUERIMIENTO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void cambiarEstado(int idRequerimientoCompra, int idEstadoNuevo, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (!WebKeysCompras.esEstadoValido(idEstadoNuevo)) {
            throw new Exception("Estado de requerimiento inválido.");
        }

        RequerimientoCompra requerimientoActual =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimientoActual == null) {
            throw new Exception(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        if (!WebKeysCompras.validarTransicionEstado(
                requerimientoActual.getEstado(),
                idEstadoNuevo
        )) {
            throw new Exception(
                    "La transición de estado solicitada no es válida."
            );
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_CAMBIAR_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        RequerimientoCompra requerimiento =
                validarRequerimientoParaEnviarACotizar(idRequerimientoCompra);

        NotificacionCotizacionResultado resultado =
                NotificarCotizacionPrestadorServiceUtil.notificarPrestadores(
                        requerimiento.getIdRequerimientoCompra(),
                        usuario,
                        companyId
                );

        if (resultado == null
                || resultado.getTotalCandidatos() <= 0
                || resultado.getEnviados() <= 0) {

            throw new Exception(
                    "No se pudo enviar la solicitud a ningún prestador. "
                            + "El requerimiento permanece Pendiente."
            );
        }

        actualizarEstadoEsperado(
                idRequerimientoCompra,
                WebKeysCompras.ESTADO_PENDIENTE,
                WebKeysCompras.ESTADO_A_COTIZAR,
                usuario
        );

        return resultado;
    }

    public NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception("No se encontró el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeReintentarNotificaciones()) {
            throw new Exception(
                    "Solo se pueden reintentar notificaciones de requerimientos en estado A cotizar."
            );
        }

        return NotificarCotizacionPrestadorServiceUtil.notificarPrestadores(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public void guardarAvanceCotizacion(int idRequerimientoCompra,
                                        List<RequerimientoCompraDetalle> detalles,
                                        String usuario) throws Exception {

        guardarCotizacion(idRequerimientoCompra, detalles, usuario, false);
    }

    public void cerrarCotizacion(int idRequerimientoCompra,
                                 List<RequerimientoCompraDetalle> detalles,
                                 String usuario) throws Exception {

        guardarCotizacion(idRequerimientoCompra, detalles, usuario, true);
    }

    public List<CompraArticulo> listarArticulos(Integer idSector, String texto) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<CompraArticulo> articulos = new ArrayList<CompraArticulo>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_ARTICULOS);

            setNullableInteger(stmt, 1, idSector);
            stmt.setString(2, emptyToNull(texto));

            rs = stmt.executeQuery();

            while (rs.next()) {
                articulos.add(mapearArticulo(rs));
            }

            return articulos;
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<CompraArticulo> listarArticulosPorSector(int idSector) throws Exception {
        if (idSector <= 0) {
            throw new Exception("Debe informar el sector.");
        }

        return listarArticulos(Integer.valueOf(idSector), null);
    }

    public CompraArticulo getArticulo(int idArticulo) throws Exception {
        if (idArticulo <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_ARTICULO);
            stmt.setInt(1, idArticulo);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearArticulo(rs);
            }

            return null;
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int guardarArticulo(Integer idArticulo,
                               Integer idSector,
                               String descripcion) throws Exception {

        String descripcionNormalizada = normalizarDescripcionArticulo(descripcion);

        CompraArticulo articulo = new CompraArticulo();
        articulo.setId(idArticulo);
        articulo.setIdSector(idSector);
        articulo.setDescripcion(descripcionNormalizada);

        validarArticuloParaGuardar(articulo);
        validarArticuloDuplicado(articulo);

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_ARTICULO);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, idArticulo);
            setNullableInteger(stmt, 3, idSector);
            stmt.setString(4, emptyToNull(descripcionNormalizada));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarArticulo(int idArticulo) throws Exception {
        if (idArticulo <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_ARTICULO);
            stmt.setInt(1, idArticulo);

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void validarRequerimientoParaGuardar(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector.");
        }

        validarPorcentaje(requerimiento.getCargoOspim(), "Cargo OSPIM");
        validarPorcentaje(requerimiento.getCargoTercerizadora(), "Cargo tercerizadora");

        int cargoOspim = requerimiento.getCargoOspim() != null
                ? requerimiento.getCargoOspim().intValue()
                : 0;

        int cargoTercerizadora = requerimiento.getCargoTercerizadora() != null
                ? requerimiento.getCargoTercerizadora().intValue()
                : 0;

        if (cargoOspim + cargoTercerizadora > 100) {
            throw new Exception("La suma de cargos no puede superar 100.");
        }

        if (cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {
            throw new Exception("Debe informar la tercerizadora cuando su cargo es mayor a cero.");
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(requerimiento.getAfiliadoCuilTitular())) {
                throw new Exception("Debe informar el CUIL titular del afiliado.");
            }

            if (requerimiento.getAfiliadoInt() == null || requerimiento.getAfiliadoInt().intValue() < 0) {
                throw new Exception("Debe informar el integrante del afiliado.");
            }
        }
    }

    private void validarDetalleParaGuardar(RequerimientoCompraDetalle detalle) throws Exception {
        if (detalle == null) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        Integer idRequerimiento = getIdRequerimientoDetalle(detalle);

        if (idRequerimiento == null || idRequerimiento.intValue() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (detalle.getIdArticulo() == null || detalle.getIdArticulo().intValue() <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        if (detalle.getCantidad() == null) {
            detalle.setCantidad(Integer.valueOf(1));
        }

        if (detalle.getCantidad().intValue() <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio unitario estimado no puede ser negativo.");
        }

        if (detalle.getPrecioTotalEstimadoInformado() != null
                && detalle.getPrecioTotalEstimadoInformado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio total estimado no puede ser negativo.");
        }
    }

    private RequerimientoCompra validarRequerimientoParaEnviarACotizar(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception("No se encontró el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw new Exception("Solo se pueden enviar a cotizar requerimientos Pendientes.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("El requerimiento debe tener sector.");
        }

        if (!requerimiento.tieneDetalles()) {
            throw new Exception("El requerimiento debe tener al menos un detalle válido.");
        }

        if (requerimiento.isRequiereAfiliado()
                && !requerimiento.tieneAfiliadoInformado()) {
            throw new Exception("El sector requiere afiliado y el requerimiento no lo tiene informado.");
        }

        return requerimiento;
    }

    private void actualizarEstadoEsperado(int idRequerimientoCompra,
                                          int estadoEsperado,
                                          int estadoNuevo,
                                          String usuario) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_ACTUALIZAR_ESTADO_ESPERADO);
            stmt.setInt(1, estadoNuevo);
            stmt.setString(2, emptyToNull(usuario));
            stmt.setInt(3, idRequerimientoCompra);
            stmt.setInt(4, estadoEsperado);

            int actualizados = stmt.executeUpdate();

            if (actualizados != 1) {
                throw new Exception(
                        "El requerimiento fue modificado por otro proceso. Recargue la pantalla."
                );
            }
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void guardarCotizacion(int idRequerimientoCompra,
                                   List<RequerimientoCompraDetalle> detalles,
                                   String usuario,
                                   boolean cerrar) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        Connection con = null;
        boolean autoCommitOriginal = true;

        try {
            con = ConnectionHelper.getConnection();
            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);

            validarEstadoCotizacion(con, idRequerimientoCompra);

            Map<Integer, Integer> cantidades =
                    getCantidadesDetalle(con, idRequerimientoCompra);

            if (cantidades.isEmpty()) {
                throw new Exception("El requerimiento no tiene detalles activos.");
            }

            actualizarDetallesCotizacion(
                    con,
                    idRequerimientoCompra,
                    cantidades,
                    detalles,
                    usuario,
                    cerrar
            );

            if (cerrar) {
                validarCotizacionCompleta(con, idRequerimientoCompra);
                actualizarEstadoCotizacionCerrada(con, idRequerimientoCompra, usuario);
            }

            con.commit();
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception rollbackError) {
                    _log.error(rollbackError);
                }
            }

            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommitOriginal);
                } catch (Exception ignored) {
                }

                try {
                    con.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void validarEstadoCotizacion(Connection con,
                                         int idRequerimientoCompra) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(SQL_GET_REQUERIMIENTO_BLOQUEO);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception("No se encontró el requerimiento de compra informado.");
            }

            if (rs.getTimestamp("baja_fecha") != null) {
                throw new Exception("El requerimiento se encuentra anulado.");
            }

            int estado = rs.getInt("estado");

            if (estado != WebKeysCompras.ESTADO_A_COTIZAR) {
                throw new Exception(
                        "Solo se puede cargar o cerrar cotización en estado A cotizar."
                );
            }
        } finally {
            cerrar(rs);
            cerrar(stmt);
        }
    }

    private Map<Integer, Integer> getCantidadesDetalle(Connection con,
                                                       int idRequerimientoCompra) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<Integer, Integer> cantidades = new HashMap<Integer, Integer>();

        try {
            stmt = con.prepareStatement(SQL_GET_DETALLES_BLOQUEO);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                cantidades.put(
                        Integer.valueOf(rs.getInt("id_detalle")),
                        Integer.valueOf(rs.getInt("cantidad"))
                );
            }

            return cantidades;
        } finally {
            cerrar(rs);
            cerrar(stmt);
        }
    }

    private void actualizarDetallesCotizacion(Connection con,
                                             int idRequerimientoCompra,
                                             Map<Integer, Integer> cantidades,
                                             List<RequerimientoCompraDetalle> detalles,
                                             String usuario,
                                             boolean cerrar) throws Exception {

        if (detalles == null) {
            detalles = new ArrayList<RequerimientoCompraDetalle>();
        }

        Set<Integer> recibidos = new HashSet<Integer>();

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle == null || detalle.getIdInt() <= 0) {
                continue;
            }

            Integer idDetalle = Integer.valueOf(detalle.getIdInt());
            Integer cantidad = cantidades.get(idDetalle);

            if (cantidad == null) {
                throw new Exception(
                        "El detalle " + idDetalle + " no pertenece al requerimiento."
                );
            }

            if (detalle.getPrecioUnitarioEstimado() != null
                    && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("El precio unitario no puede ser negativo.");
            }

            if (detalle.getIdPrestador() != null
                    && !existePrestadorEnviado(
                    con,
                    idRequerimientoCompra,
                    detalle.getIdPrestador().intValue()
            )) {
                throw new Exception(
                        "El prestador seleccionado no fue notificado correctamente para este requerimiento."
                );
            }

            BigDecimal total =
                    WebKeysCompras.calcularPrecioTotal(
                            cantidad,
                            detalle.getPrecioUnitarioEstimado()
                    );

            actualizarDetalleCotizacion(
                    con,
                    idRequerimientoCompra,
                    idDetalle.intValue(),
                    detalle.getPrecioUnitarioEstimado(),
                    total,
                    detalle.getIdPrestador(),
                    usuario
            );

            recibidos.add(idDetalle);
        }

        if (cerrar && recibidos.size() < cantidades.size()) {
            throw new Exception(
                    "Debe enviar todos los detalles para cerrar la cotización."
            );
        }
    }

    private boolean existePrestadorEnviado(Connection con,
                                          int idRequerimientoCompra,
                                          int idPrestador) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(SQL_EXISTE_PRESTADOR_ENVIADO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idPrestador);
            stmt.setString(3, WebKeysCompras.ENVIO_ENVIADO);
            rs = stmt.executeQuery();

            return rs.next();
        } finally {
            cerrar(rs);
            cerrar(stmt);
        }
    }

    private void actualizarDetalleCotizacion(Connection con,
                                             int idRequerimientoCompra,
                                             int idDetalle,
                                             BigDecimal precioUnitario,
                                             BigDecimal total,
                                             Integer idPrestador,
                                             String usuario) throws Exception {

        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(SQL_ACTUALIZAR_DETALLE_COTIZACION);
            setNullableBigDecimal(stmt, 1, precioUnitario);
            setNullableBigDecimal(stmt, 2, total);
            setNullableInteger(stmt, 3, idPrestador);
            stmt.setString(4, emptyToNull(usuario));
            stmt.setInt(5, idRequerimientoCompra);
            stmt.setInt(6, idDetalle);

            if (stmt.executeUpdate() != 1) {
                throw new Exception(
                        "No se pudo actualizar el detalle " + idDetalle + "."
                );
            }
        } finally {
            cerrar(stmt);
        }
    }

    private void validarCotizacionCompleta(Connection con,
                                           int idRequerimientoCompra) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(SQL_DETALLES_INCOMPLETOS_COTIZACION);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            if (!rs.next() || rs.getInt("total") <= 0) {
                throw new Exception("El requerimiento no tiene detalles activos.");
            }

            int incompletos = rs.getInt("incompletos");

            if (incompletos > 0) {
                throw new Exception(
                        "No se puede cerrar la cotización: hay detalles sin precio o prestador."
                );
            }
        } finally {
            cerrar(rs);
            cerrar(stmt);
        }
    }

    private void actualizarEstadoCotizacionCerrada(Connection con,
                                                   int idRequerimientoCompra,
                                                   String usuario) throws Exception {

        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(SQL_ACTUALIZAR_ESTADO_ESPERADO);
            stmt.setInt(1, WebKeysCompras.ESTADO_COTIZADO);
            stmt.setString(2, emptyToNull(usuario));
            stmt.setInt(3, idRequerimientoCompra);
            stmt.setInt(4, WebKeysCompras.ESTADO_A_COTIZAR);

            if (stmt.executeUpdate() != 1) {
                throw new Exception(
                        "El requerimiento fue modificado o cerrado por otro proceso."
                );
            }
        } finally {
            cerrar(stmt);
        }
    }

    private void validarArticuloParaGuardar(CompraArticulo articulo) throws Exception {
        if (articulo == null) {
            throw new Exception("Debe informar el artículo.");
        }

        if (articulo.getIdSector() == null || articulo.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector del artículo.");
        }

        if (WebKeysCompras.isEmpty(articulo.getDescripcion())) {
            throw new Exception("Debe informar la descripción del artículo.");
        }
    }

    private CompraArticulo mapearArticulo(ResultSet rs) throws Exception {
        CompraArticulo articulo = new CompraArticulo();

        articulo.setId(Integer.valueOf(rs.getInt("id")));
        articulo.setIdSector(Integer.valueOf(rs.getInt("id_sector")));
        articulo.setSectorDescripcion(rs.getString("sector_descripcion"));
        articulo.setDescripcion(rs.getString("descripcion"));

        return articulo;
    }

    private Integer getIdRequerimientoDetalle(RequerimientoCompraDetalle detalle) {
        if (detalle.getIdRequerimiento() != null && detalle.getIdRequerimiento().intValue() > 0) {
            return detalle.getIdRequerimiento();
        }

        if (detalle.getIdRequerimientoCompra() > 0) {
            return Integer.valueOf(detalle.getIdRequerimientoCompra());
        }

        return null;
    }

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            throw new Exception(label + " debe estar entre 0 y 100.");
        }
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableInteger(PreparedStatement stmt, int index, Integer value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableBigDecimal(PreparedStatement stmt, int index, BigDecimal value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.NUMERIC);
        } else {
            stmt.setBigDecimal(index, WebKeysCompras.normalizarImporte(value));
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }

    private void cerrar(Statement stmt) {
        if (stmt == null) {
            return;
        }

        try {
            stmt.close();
        } catch (Exception ignored) {
        }
    }

    private String normalizarDescripcionArticulo(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String normalizado = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalizado = DIACRITICOS.matcher(normalizado).replaceAll("");
        normalizado = normalizado.replaceAll("\\s+", " ");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    private void validarArticuloDuplicado(CompraArticulo articulo) throws Exception {
        if (articulo == null
                || articulo.getIdSector() == null
                || articulo.getIdSector().intValue() <= 0
                || WebKeysCompras.isEmpty(articulo.getDescripcion())) {

            return;
        }

        String descripcionNormalizada =
                normalizarDescripcionArticulo(articulo.getDescripcion());

        List<CompraArticulo> existentes =
                listarArticulos(
                        articulo.getIdSector(),
                        null
                );

        if (existentes == null) {
            return;
        }

        for (int i = 0; i < existentes.size(); i++) {
            CompraArticulo existente = existentes.get(i);

            if (existente == null) {
                continue;
            }

            if (articulo.getId() != null
                    && existente.getId() != null
                    && articulo.getId().intValue() == existente.getId().intValue()) {

                continue;
            }

            String descripcionExistente =
                    normalizarDescripcionArticulo(existente.getDescripcion());

            if (descripcionNormalizada != null
                    && descripcionNormalizada.equals(descripcionExistente)) {

                throw new Exception(
                        "Ya existe un artículo con la descripción '"
                                + descripcionNormalizada
                                + "' para el sector seleccionado."
                );
            }
        }
    }
}
