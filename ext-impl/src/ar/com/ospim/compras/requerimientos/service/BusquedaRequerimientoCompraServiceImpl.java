package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.util.ConnectionHelper;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

/**
 * Capa de persistencia de Requerimientos de Compra.
 *
 * Regla de esta clase:
 * - abrir/cerrar conexiones;
 * - ejecutar CALL funcionales o SQL directo de lectura;
 * - parametrizar;
 * - mapear ResultSet a beans.
 *
 * No contiene validaciones funcionales ni reglas de negocio.
 */
public class BusquedaRequerimientoCompraServiceImpl {

    private static final String SQL_BUSCAR_REQUERIMIENTOS =
            "{call compras.buscar_requerimientos(?,?,?,?,?,?,?,?)}";

    private static final String SQL_GET_REQUERIMIENTO =
            "{call compras.get_requerimiento(?)}";

    private static final String SQL_GET_REQUERIMIENTO_DETALLE =
            "{call compras.get_requerimiento_detalle_clasificado(?)}";

    private static final String SQL_LISTAR_SECTORES =
            "SELECT s.id_sector AS id, s.descripcion, "
                    + "s.requiere_afiliado "
                    + "FROM compras.sector_requerimiento s "
                    + "WHERE compras.es_sector_seleccionable_compras("
                    + "s.id_sector) "
                    + "ORDER BY s.descripcion";

    private static final String SQL_LISTAR_TIPOS_PRESTACION =
            "SELECT t.id_tipo_prestacion::INTEGER "
                    + "AS id_tipo_prestacion, t.descripcion, "
                    + "t.id_sector, s.descripcion AS sector_descripcion "
                    + "FROM compras.tipo_prestacion t "
                    + "JOIN compras.sector_requerimiento s "
                    + "ON s.id_sector = t.id_sector "
                    + "WHERE s.activo = TRUE "
                    + "AND s.baja_fecha IS NULL "
                    + "ORDER BY t.id_tipo_prestacion";

    private static final String SQL_GET_ESTADO =
            "SELECT r.estado AS id, "
                    + "compras.estado_requerimiento_descripcion("
                    + "r.estado) AS descripcion "
                    + "FROM compras.requerimiento r "
                    + "WHERE r.id_requerimiento = ?";

    private static final String SQL_GET_SECTOR =
            "SELECT s.id_sector AS id, s.descripcion, "
                    + "s.requiere_afiliado "
                    + "FROM compras.sector_requerimiento s "
                    + "WHERE s.id_sector = ? "
                    + "AND s.activo = TRUE "
                    + "AND s.baja_fecha IS NULL";

    private static final String SQL_BUSCAR_PRESTADORES_ENVIADOS =
            "{call compras.buscar_prestadores_enviados(?,?,?)}";

    private static final String SQL_LISTAR_PRESTADORES_ENVIADOS =
            "{call compras.listar_prestadores_enviados(?,?)}";

    private static final String SQL_HAY_PRESTADORES_PENDIENTES_NOTIFICACION =
            "{call compras.hay_prestadores_pendientes_notificacion(?)}";

    private static final String SQL_LISTAR_PRESUPUESTOS =
            "SELECT rp.id_requerimiento_presupuesto, "
                    + "rp.id_requerimiento, rp.id_prestador, "
                    + "rp.tipo_documento, rp.fecha_documento, "
                    + "rp.dl_group_id, rp.dl_folder_id, "
                    + "rp.dl_file_entry_id, rp.dl_file_uuid, "
                    + "rp.nombre_original, rp.nombre_persistido, "
                    + "rp.titulo, rp.descripcion_prestador, "
                    + "rp.alta_fecha, rp.alta_usr "
                    + "FROM compras.requerimiento_presupuesto rp "
                    + "WHERE rp.id_requerimiento = ? "
                    + "AND rp.tipo_documento = 1 "
                    + "AND rp.baja_fecha IS NULL "
                    + "ORDER BY rp.alta_fecha DESC, "
                    + "rp.id_requerimiento_presupuesto DESC";

    private static final String SQL_GET_PRESUPUESTO =
            "SELECT rp.* "
                    + "FROM compras.requerimiento_presupuesto rp "
                    + "WHERE rp.id_requerimiento_presupuesto = ? "
                    + "AND rp.id_requerimiento = ? "
                    + "AND rp.tipo_documento = 1 "
                    + "AND rp.baja_fecha IS NULL";

    private static final String SQL_GET_ORDEN_MEDICA =
            "SELECT rp.* "
                    + "FROM compras.requerimiento_presupuesto rp "
                    + "WHERE rp.id_requerimiento = ? "
                    + "AND rp.tipo_documento = 2 "
                    + "AND rp.baja_fecha IS NULL";

    private static final String SQL_BUSCAR_ITEMS_HISTORICOS_AFILIADO =
            "{call compras.buscar_items_historicos_afiliado(?,?,?,?,?)}";

    private static final String SQL_TIENE_SITUACION_MEDICA_VIGENTE =
            "SELECT EXISTS ("
                    + "SELECT 1 FROM public.afi_situ_medica sm "
                    + "WHERE sm.cuil_titular = ? "
                    + "AND sm.inte = ? "
                    + "AND sm.baja_fecha IS NULL "
                    + "AND (sm.vigen_hasta IS NULL "
                    + "OR sm.vigen_hasta > CURRENT_DATE))";

    private static final String SQL_EXISTE_REQUERIMIENTO_DUPLICADO =
            "{ ? = call compras.existe_requerimiento_duplicado(?,?,?,?,?) }";

    private static final String SQL_LISTAR_PRESTADORES_ADJUDICADOS =
            "SELECT DISTINCT d.id_prestador "
                    + "FROM compras.requerimiento_detalle d "
                    + "WHERE d.id_requerimiento = ? "
                    + "AND d.baja_fecha IS NULL";

    private static final String SQL_LISTAR_PRESUPUESTOS_PRESTADOR =
            "SELECT rp.* "
                    + "FROM compras.requerimiento_presupuesto rp "
                    + "WHERE rp.id_requerimiento = ? "
                    + "AND rp.id_prestador = ? "
                    + "AND rp.tipo_documento = 1 "
                    + "AND rp.baja_fecha IS NULL "
                    + "ORDER BY rp.id_requerimiento_presupuesto";

    private static final String SQL_GET_PEDIDO_COTIZACION_PRESTADOR =
            "SELECT pc.* "
                    + "FROM compras.requerimiento_pedido_cotizacion pc "
                    + "JOIN compras.requerimiento_cotizacion_prestador rcp "
                    + "ON rcp.id_requerimiento = pc.id_requerimiento "
                    + "AND rcp.id_prestador = pc.id_prestador "
                    + "WHERE pc.id_requerimiento = ? "
                    + "AND pc.id_prestador = ? "
                    + "AND pc.intento = rcp.intentos "
                    + "AND rcp.estado_envio IN ('ENVIADO', 'COTIZADO') "
                    + "ORDER BY pc.intento DESC LIMIT 1";

    public List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompra> resultado =
                new ArrayList<RequerimientoCompra>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BUSCAR_REQUERIMIENTOS);

            setNullableInteger(stmt, 1, filtro.getIdEstado());
            setNullableInteger(stmt, 2, filtro.getIdSector());
            stmt.setString(3, filtro.getAfiliadoCuilTitular());
            setNullableInteger(stmt, 4, filtro.getAfiliadoInt());
            stmt.setString(5, filtro.getIdTercerizadora());
            setNullableBoolean(stmt, 6, filtro.getRecupero());
            setNullableBoolean(stmt, 7, filtro.getSurge());
            stmt.setString(8, filtro.getTexto());

            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapRequerimiento(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        RequerimientoCompra requerimiento =
                getCabeceraRequerimiento(idRequerimientoCompra);

        if (requerimiento != null) {
            requerimiento.setDetalles(
                    getDetalles(idRequerimientoCompra)
            );
        }

        return requerimiento;
    }

    public RequerimientoCompra getCabeceraRequerimiento(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? mapRequerimiento(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraDetalle> resultado =
                new ArrayList<RequerimientoCompraDetalle>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO_DETALLE);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapDetalle(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraDetalle> buscarItemsHistoricosAfiliado(
            String cuilTitular,
            int inte,
            int idSector,
            int idRequerimientoExcluir,
            int limite) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraDetalle> resultado =
                new ArrayList<RequerimientoCompraDetalle>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BUSCAR_ITEMS_HISTORICOS_AFILIADO);
            stmt.setString(1, cuilTitular);
            stmt.setInt(2, inte);
            stmt.setInt(3, idSector);
            stmt.setInt(4, idRequerimientoExcluir);
            stmt.setInt(5, limite);
            rs = stmt.executeQuery();

            while (rs.next()) {
                RequerimientoCompraDetalle detalle =
                        new RequerimientoCompraDetalle();
                detalle.setIdPrestacion(
                        getInteger(rs, "id_prestacion")
                );
                detalle.setIdTipoNomenclador(
                        getInteger(rs, "id_tipo_nomenclador")
                );
                detalle.setCodigoNomenclador(
                        getString(rs, "codigo")
                );
                detalle.setDescripcionNomenclador(
                        getString(rs, "descripcion")
                );
                resultado.add(detalle);
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraSector> listarSectores() throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraSector> resultado =
                new ArrayList<RequerimientoCompraSector>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_SECTORES);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapSector(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<TipoPrestacionCompra> listarTiposPrestacion()
            throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TipoPrestacionCompra> resultado =
                new ArrayList<TipoPrestacionCompra>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_TIPOS_PRESTACION);
            rs = stmt.executeQuery();

            while (rs.next()) {
                TipoPrestacionCompra tipo =
                        new TipoPrestacionCompra();
                tipo.setId(getInteger(rs, "id_tipo_prestacion"));
                tipo.setDescripcion(getString(rs, "descripcion"));
                tipo.setIdSector(getInteger(rs, "id_sector"));
                tipo.setSectorDescripcion(
                        getString(rs, "sector_descripcion")
                );
                resultado.add(tipo);
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompraEstado getEstado(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            RequerimientoCompraEstado estado =
                    new RequerimientoCompraEstado();
            estado.setId(getInteger(rs, "id"));
            estado.setDescripcion(getString(rs, "descripcion"));
            return estado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompraSector getSector(int idSector)
            throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_SECTOR);
            stmt.setInt(1, idSector);
            rs = stmt.executeQuery();

            return rs.next() ? mapSector(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean tieneSituacionMedicaVigente(
            String cuilTitular,
            int inte) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_TIENE_SITUACION_MEDICA_VIGENTE);
            stmt.setString(1, cuilTitular);
            stmt.setInt(2, inte);
            rs = stmt.executeQuery();

            return rs.next() && rs.getBoolean(1);
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean existeRequerimientoDuplicado(
            String cuilTitular,
            int inte,
            int idPrestacion,
            java.util.Date fechaOrdenMedica,
            int idRequerimientoExcluir)
            throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con =
                    ConnectionHelper.getConnection();

            stmt =
                    con.prepareCall(
                            SQL_EXISTE_REQUERIMIENTO_DUPLICADO
                    );

            stmt.registerOutParameter(
                    1,
                    Types.BOOLEAN
            );

            stmt.setString(
                    2,
                    cuilTitular
            );

            stmt.setInt(
                    3,
                    inte
            );

            stmt.setInt(
                    4,
                    idPrestacion
            );

            if (fechaOrdenMedica == null) {
                stmt.setNull(
                        5,
                        Types.DATE
                );
            } else {
                stmt.setDate(
                        5,
                        new java.sql.Date(
                                fechaOrdenMedica.getTime()
                        )
                );
            }

            stmt.setInt(
                    6,
                    idRequerimientoExcluir
            );

            stmt.execute();

            boolean value =
                    stmt.getBoolean(1);

            return !stmt.wasNull()
                    && value;

        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    public List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<PrestadorCotizacion> resultado =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BUSCAR_PRESTADORES_ENVIADOS);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, texto);
            stmt.setInt(3, limite);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPrestadorCotizacion(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra,
            int limite) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<PrestadorCotizacion> resultado =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_PRESTADORES_ENVIADOS);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, limite);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPrestadorCotizacion(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    SQL_HAY_PRESTADORES_PENDIENTES_NOTIFICACION
            );
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() && rs.getBoolean(1);
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_PRESUPUESTOS);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPresupuesto(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_PRESUPUESTO);
            stmt.setInt(1, idRequerimientoPresupuesto);
            stmt.setInt(2, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? mapPresupuesto(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_ORDEN_MEDICA);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPresupuesto(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<Integer> listarPrestadoresAdjudicados(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Integer> resultado = new ArrayList<Integer>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_PRESTADORES_ADJUDICADOS);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int value = rs.getInt("id_prestador");
                resultado.add(
                        rs.wasNull() ? null : Integer.valueOf(value)
                );
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarPresupuestosPrestador(
            int idRequerimientoCompra,
            int idPrestador) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_LISTAR_PRESUPUESTOS_PRESTADOR);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idPrestador);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPresupuesto(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private RequerimientoCompra mapRequerimiento(ResultSet rs) throws Exception {
        RequerimientoCompra r = new RequerimientoCompra();

        r.setId(getInteger(rs, "id"));
        r.setAltaFecha(rs.getTimestamp("alta_fecha"));
        r.setAltaUsr(getString(rs, "alta_usr"));
        r.setModiFecha(rs.getTimestamp("modi_fecha"));
        r.setModiUsr(getString(rs, "modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(getString(rs, "baja_usr"));

        r.setAfiliadoCuilTitular(getString(rs, "afiliado_cuil_titular"));
        r.setAfiliadoInt(getInteger(rs, "afiliado_int"));
        r.setAfiliadoIdOspim(getInteger(rs, "afiliado_id_ospim"));
        r.setAfiliadoNombre(getString(rs, "afiliado_nombre"));
        r.setAfiliadoApellido(getString(rs, "afiliado_apellido"));
        r.setAfiliadoNombreApellido(getString(rs, "afiliado_nombre_apellido"));
        r.setAfiliadoDocumentoTipo(getString(rs, "afiliado_documento_tipo"));
        r.setAfiliadoDocumentoNro(getString(rs, "afiliado_documento_nro"));
        r.setAfiliadoDocumento(getString(rs, "afiliado_documento"));
        r.setAfiliadoDireccion(getString(rs, "afiliado_direccion"));
        r.setAfiliadoLocalidad(getString(rs, "afiliado_localidad"));
        r.setAfiliadoProvincia(getString(rs, "afiliado_provincia"));
        r.setAfiliadoCelular(getString(rs, "afiliado_celular"));
        r.setAfiliadoTelefono(getString(rs, "afiliado_telefono"));
        r.setAfiliadoEmail(getString(rs, "afiliado_email"));

        r.setIdSector(getInteger(rs, "id_sector"));
        r.setSectorDescripcion(getString(rs, "sector_descripcion"));
        r.setRequiereAfiliado(getBoolean(rs, "requiere_afiliado"));
        r.setCargoOspim(getInteger(rs, "cargo_ospim"));
        r.setCargoTercerizadora(getInteger(rs, "cargo_tercerizadora"));
        r.setIdTercerizadora(getString(rs, "id_tercerizadora"));
        r.setRecupero(getBoolean(rs, "recupero"));
        r.setSurge(getBoolean(rs, "surge"));
        r.setLegales(getBoolean(rs, "legales"));
        r.setObservaciones(getString(rs, "observaciones"));
        r.setIdEstado(getInteger(rs, "id_estado"));
        r.setEstadoDescripcion(getString(rs, "estado_descripcion"));

        return r;
    }

    private RequerimientoCompraDetalle mapDetalle(ResultSet rs) throws Exception {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();

        d.setId(getInteger(rs, "id"));
        d.setIdRequerimiento(getInteger(rs, "id_requerimiento"));

        d.setTipoItem(getString(rs, "tipo_item"));
        d.setIdTipoPrestacion(
                getInteger(rs, "id_tipo_prestacion")
        );
        d.setTipoPrestacionDescripcion(
                getString(rs, "tipo_prestacion")
        );
        d.setCodigoItem(getString(rs, "codigo_item"));
        d.setDescripcionItem(getString(rs, "descripcion_item"));

        d.setIdPrestacion(getInteger(rs, "id_prestacion"));
        d.setIdTipoNomenclador(getInteger(rs, "id_tipo_nomenclador"));
        d.setCodigoNomenclador(getString(rs, "codigo_nomenclador"));
        d.setDescripcionNomenclador(getString(rs, "descripcion_nomenclador"));

        d.setIdMedicamento(getInteger(rs, "id_medicamento"));
        d.setTroquel(getInteger(rs, "troquel"));
        d.setNombreMedicamento(getString(rs, "nombre_medicamento"));

        d.setCantidad(getInteger(rs, "cantidad"));

        d.setPrecioUnitarioEstimado(
                getNullableBigDecimal(rs, "precio_unitario_estimado")
        );

        d.setPrecioTotalEstimado(
                getNullableBigDecimal(rs, "precio_total_estimado")
        );

        d.setIdPrestador(getInteger(rs, "id_prestador"));
        d.setPrestadorCuit(getString(rs, "prestador_cuit"));
        d.setPrestadorRazonSocial(getString(rs, "prestador_razon_social"));
        d.setObservaciones(getString(rs, "observaciones"));

        return d;
    }

    private RequerimientoCompraSector mapSector(ResultSet rs) throws Exception {
        RequerimientoCompraSector sector = new RequerimientoCompraSector();
        sector.setId(getInteger(rs, "id"));
        sector.setDescripcion(getString(rs, "descripcion"));
        sector.setRequiereAfiliado(getBoolean(rs, "requiere_afiliado"));
        return sector;
    }

    private PrestadorCotizacion mapPrestadorCotizacion(ResultSet rs)
            throws Exception {

        PrestadorCotizacion prestador = new PrestadorCotizacion();
        Integer idPrestador = getInteger(rs, "id_prestador");
        Integer idTipoPrestador = getInteger(rs, "id_tipo_prestador");

        prestador.setIdPrestador(
                idPrestador != null ? idPrestador.intValue() : 0
        );
        prestador.setDescripcion(getString(rs, "descripcion"));
        prestador.setCuit(
                getString(
                        rs,
                        "cuit"
                )
        );

        prestador.setEmail(
                getString(
                        rs,
                        "email"
                )
        );

        prestador.setEmailDestino(
                getString(
                        rs,
                        "email_destino"
                )
        );

        prestador.setIdTipoPrestador(
                idTipoPrestador != null ? idTipoPrestador.intValue() : 0
        );
        prestador.setTipoPrestador(getString(rs, "tipo_prestador"));
        prestador.setEstadoEnvio(getString(rs, "estado_envio"));
        return prestador;
    }

    private RequerimientoCompraPresupuesto mapPresupuesto(ResultSet rs)
            throws Exception {

        RequerimientoCompraPresupuesto presupuesto =
                new RequerimientoCompraPresupuesto();

        presupuesto.setIdRequerimientoPresupuesto(
                getInteger(rs, "id_requerimiento_presupuesto")
        );
        presupuesto.setIdRequerimiento(getInteger(rs, "id_requerimiento"));
        presupuesto.setIdPrestador(getInteger(rs, "id_prestador"));

        if (hasColumn(rs, "tipo_documento")) {
            presupuesto.setTipoDocumento(getInteger(rs, "tipo_documento"));
        }

        if (hasColumn(rs, "fecha_documento")) {
            presupuesto.setFechaDocumento(rs.getDate("fecha_documento"));
        }

        if (hasColumn(rs, "numero_receta")) {
            presupuesto.setNumeroReceta(getString(rs, "numero_receta"));
        }

        presupuesto.setDlGroupId(getLong(rs, "dl_group_id"));
        presupuesto.setDlFolderId(getLong(rs, "dl_folder_id"));
        presupuesto.setDlFileEntryId(getLong(rs, "dl_file_entry_id"));
        presupuesto.setDlFileUuid(getString(rs, "dl_file_uuid"));
        presupuesto.setNombreOriginal(getString(rs, "nombre_original"));
        presupuesto.setNombrePersistido(getString(rs, "nombre_persistido"));
        presupuesto.setTitulo(getString(rs, "titulo"));
        presupuesto.setDescripcionPrestador(
                getString(rs, "descripcion_prestador")
        );

        if (hasColumn(rs, "alta_fecha")) {
            presupuesto.setAltaFecha(rs.getTimestamp("alta_fecha"));
        }

        presupuesto.setAltaUsr(getString(rs, "alta_usr"));

        if (hasColumn(rs, "baja_fecha")) {
            presupuesto.setBajaFecha(rs.getTimestamp("baja_fecha"));
        }

        presupuesto.setBajaUsr(getString(rs, "baja_usr"));
        return presupuesto;
    }

    private void setNullableInteger(
            CallableStatement stmt,
            int index,
            Integer value) throws Exception {

        if (value == null || value.intValue() < 0) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableBoolean(
            CallableStatement stmt,
            int index,
            Boolean value) throws Exception {

        if (value == null) {
            stmt.setNull(index, Types.BOOLEAN);
        } else {
            stmt.setBoolean(index, value.booleanValue());
        }
    }

    private String getString(ResultSet rs, String column) throws Exception {
        return hasColumn(rs, column) ? rs.getString(column) : null;
    }

    private Integer getInteger(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        int value = rs.getInt(column);
        return rs.wasNull() ? null : Integer.valueOf(value);
    }

    private Long getLong(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        long value = rs.getLong(column);
        return rs.wasNull() ? null : Long.valueOf(value);
    }

    private Boolean getBoolean(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        boolean value = rs.getBoolean(column);
        return rs.wasNull() ? null : Boolean.valueOf(value);
    }

    private BigDecimal getNullableBigDecimal(ResultSet rs, String column)
            throws Exception {

        return hasColumn(rs, column) ? rs.getBigDecimal(column) : null;
    }

    private boolean hasColumn(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void closeQuietly(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }

    public RequerimientoCompraPedidoCotizacion
    getPedidoCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador)
            throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con =
                    ConnectionHelper.getConnection();

            stmt =
                    con.prepareStatement(
                            SQL_GET_PEDIDO_COTIZACION_PRESTADOR
                    );

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            rs =
                    stmt.executeQuery();

            return rs.next()
                    ? mapPedidoCotizacion(
                    rs
            )
                    : null;

        } finally {
            closeQuietly(
                    rs
            );

            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    private RequerimientoCompraPedidoCotizacion
    mapPedidoCotizacion(
            ResultSet rs)
            throws Exception {

        RequerimientoCompraPedidoCotizacion documento =
                new RequerimientoCompraPedidoCotizacion();

        documento.setIdRequerimiento(
                getInteger(
                        rs,
                        "id_requerimiento"
                )
        );

        documento.setIdPrestador(
                getInteger(
                        rs,
                        "id_prestador"
                )
        );

        documento.setIntento(
                getInteger(
                        rs,
                        "intento"
                )
        );

        documento.setDlGroupId(
                getLong(
                        rs,
                        "dl_group_id"
                )
        );

        documento.setDlFolderId(
                getLong(
                        rs,
                        "dl_folder_id"
                )
        );

        documento.setDlFileEntryId(
                getLong(
                        rs,
                        "dl_file_entry_id"
                )
        );

        documento.setDlFileUuid(
                getString(
                        rs,
                        "dl_file_uuid"
                )
        );

        documento.setNombreOriginal(
                getString(
                        rs,
                        "nombre_original"
                )
        );

        documento.setNombrePersistido(
                getString(
                        rs,
                        "nombre_persistido"
                )
        );

        documento.setTitulo(
                getString(
                        rs,
                        "titulo"
                )
        );

        if (hasColumn(
                rs,
                "alta_fecha"
        )) {

            documento.setAltaFecha(
                    rs.getTimestamp(
                            "alta_fecha"
                    )
            );
        }

        documento.setAltaUsr(
                getString(
                        rs,
                        "alta_usr"
                )
        );

        return documento;
    }
}
