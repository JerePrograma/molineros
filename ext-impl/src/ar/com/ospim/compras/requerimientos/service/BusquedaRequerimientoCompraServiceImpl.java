package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.ConnectionHelper;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Capa de persistencia de Requerimientos de Compra.
 *
 * Regla de esta clase:
 * - abrir/cerrar conexiones;
 * - ejecutar CALL a funciones PostgreSQL;
 * - parametrizar;
 * - mapear ResultSet a beans.
 *
 * No contiene validaciones funcionales ni reglas de negocio.
 */
public class BusquedaRequerimientoCompraServiceImpl {

    private static final String SQL_BUSCAR_REQUERIMIENTOS =
            "SELECT rb.*, afi.id_seccional AS afiliado_id_seccional, "
                    + "sec.descripcion AS afiliado_seccional_descripcion "
                    + "FROM compras.buscar_requerimientos(?,?,?,?,?,?,?,?,?,?,?) rb "
                    + "LEFT JOIN public.afiliado afi "
                    + "ON afi.cuil_titular = rb.afiliado_cuil_titular "
                    + "AND afi.inte = rb.afiliado_int "
                    + "LEFT JOIN public.seccional sec ON sec.id_seccional = afi.id_seccional "
                    + "ORDER BY rb.id DESC";

    /* La entrada sin filtros pagina IDs antes de armar las cabeceras. */
    private static final String SQL_BUSCAR_REQUERIMIENTOS_INICIALES =
            "WITH candidatos AS ("
                    + "SELECT r.id_requerimiento FROM compras.requerimiento r "
                    + "JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector "
                    + "WHERE r.baja_fecha IS NULL"
                    + "), pagina AS ("
                    + "SELECT id_requerimiento FROM candidatos "
                    + "ORDER BY id_requerimiento DESC LIMIT ? OFFSET ?"
                    + ") SELECT r.*, r.id_requerimiento AS id, r.estado AS id_estado, "
                    + "s.descripcion AS sector_descripcion, s.requiere_afiliado, "
                    + "NULLIF(concat_ws(', ', NULLIF(btrim(r.afiliado_apellido), ''), "
                    + "NULLIF(btrim(r.afiliado_nombre), '')), '')::VARCHAR "
                    + "AS afiliado_nombre_apellido, "
                    + "NULLIF(concat_ws(' ', NULLIF(btrim(r.afiliado_documento_tipo), ''), "
                    + "NULLIF(btrim(r.afiliado_documento_nro), '')), '')::VARCHAR "
                    + "AS afiliado_documento, "
                    + "compras.estado_requerimiento_descripcion(r.estado) AS estado_descripcion, "
                    + "tot.total_registros FROM pagina p "
                    + "JOIN compras.requerimiento r ON r.id_requerimiento = p.id_requerimiento "
                    + "JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector "
                    + "CROSS JOIN (SELECT count(*) AS total_registros FROM candidatos) tot "
                    + "ORDER BY r.id_requerimiento DESC";

    private static final String SQL_GET_REQUERIMIENTO =
            "SELECT rb.*, r.observacion_interna, r.motivo_baja "
                    + "FROM compras.get_requerimiento(?) rb "
                    + "JOIN compras.requerimiento r ON r.id_requerimiento = rb.id";

    private static final String SQL_GET_REQUERIMIENTO_DETALLE =
            "{call compras.get_requerimiento_detalle_clasificado(?)}";

    private static final String SQL_LISTAR_SECTORES =
            "{call compras.listar_sectores_requerimiento()}";

    private static final String SQL_LISTAR_TIPOS_PRESTACION =
            "{call compras.listar_tipos_prestacion()}";

    private static final String SQL_GET_ESTADO =
            "{call compras.get_estado_requerimiento(?)}";

    private static final String SQL_GET_SECTOR =
            "{call compras.get_sector_requerimiento(?)}";

    private static final String SQL_LISTAR_PRESTADORES_ENVIADOS =
            "{call compras.listar_prestadores_enviados(?,?)}";

    private static final String SQL_HAY_PRESTADORES_PENDIENTES_NOTIFICACION =
            "{call compras.hay_prestadores_pendientes_notificacion(?)}";

    private static final String SQL_BUSCAR_EMPRESAS_COTIZACION =
            "{call compras.buscar_empresas_cotizacion(?,?,?,?)}";

    private static final String SQL_BUSCAR_EMPRESAS_COTIZACION_RAPIDA =
            "{call compras.buscar_empresas_cotizacion_rapida(?,?,?,?)}";

    private static final String SQL_REQUERIMIENTO_HABILITADO_BUSQUEDA_EMPRESA =
            "{call compras.es_requerimiento_habilitado_busqueda_empresa_cotizacion(?)}";

    private static final String SQL_LISTAR_PRESUPUESTOS =
            "{call compras.listar_documentos_requerimiento(?,?)}";

    private static final String SQL_GET_PRESUPUESTO =
            "{call compras.get_documento_requerimiento(?,?,?)}";

    private static final String SQL_LISTAR_COTIZACIONES_EMPRESA =
            "{call compras.listar_documentos_requerimiento(?,?)}";

    private static final String SQL_GET_COTIZACION_EMPRESA =
            "{call compras.get_documento_requerimiento(?,?,?)}";

    private static final String SQL_GET_ORDEN_MEDICA =
            "{call compras.listar_ordenes_medicas_requerimiento(?)}";

    private static final String SQL_BUSCAR_ITEMS_HISTORICOS_AFILIADO =
            "{call compras.buscar_items_historicos_afiliado_clasificado(?,?,?,?,?)}";

    private static final String SQL_TIENE_SITUACION_MEDICA_VIGENTE =
            "{call compras.tiene_situacion_medica_vigente(?,?)}";

    private static final String SQL_EXISTE_REQUERIMIENTO_DUPLICADO =
            "{ ? = call compras.existe_requerimiento_duplicado(?,?,?,?,?) }";

    private static final String SQL_LISTAR_PRESTADORES_ADJUDICADOS =
            "{call compras.listar_prestadores_adjudicados(?)}";

    private static final String SQL_LISTAR_PRESUPUESTOS_PRESTADOR =
            "{call compras.listar_presupuestos_prestador(?,?)}";

    private static final String SQL_GET_PEDIDO_COTIZACION_PRESTADOR =
            "{call compras.get_pedido_cotizacion_prestador(?,?)}";

    public List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<RequerimientoCompra> resultado =
                new ArrayList<RequerimientoCompra>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_BUSCAR_REQUERIMIENTOS);

            setParametrosBusqueda(stmt, filtro, filtro.getIdEstado(), 0);

            rs = stmt.executeQuery();

            while (rs.next()) {
                RequerimientoCompra requerimiento = mapRequerimiento(rs);
                requerimiento.setAfiliadoIdSeccional(getInteger(rs, "afiliado_id_seccional"));
                requerimiento.setAfiliadoSeccionalDescripcion(
                        rs.getString("afiliado_seccional_descripcion"));
                resultado.add(requerimiento);
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompra> buscarRequerimientosInicialesPaginados(
            int pagina) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompra> resultado = new ArrayList<RequerimientoCompra>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_BUSCAR_REQUERIMIENTOS_INICIALES);
            stmt.setInt(1,
                    BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA);
            stmt.setLong(2, ((long) pagina - 1L)
                    * BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RequerimientoCompra requerimiento = mapRequerimiento(rs);
                requerimiento.setTotalRegistros(rs.getInt("total_registros"));
                resultado.add(requerimiento);
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompra> buscarRequerimientosPaginados(
            RequerimientoCompraFiltro filtro,
            boolean incluirReclamoRp,
            int pagina) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompra> resultado = new ArrayList<RequerimientoCompra>();
        String consulta = "SELECT * FROM compras.buscar_requerimientos(?,?,?,?,?,?,?,?,?,?,?)";
        StringBuilder sql = new StringBuilder("WITH requerimientos AS (");
        sql.append(consulta);
        if (incluirReclamoRp) {
            sql.append(" UNION ALL ").append(consulta);
        }
        sql.append(") SELECT rb.*, tot.total_registros FROM requerimientos rb ")
                .append("CROSS JOIN (SELECT count(*) AS total_registros FROM requerimientos) tot ")
                .append("ORDER BY rb.id DESC LIMIT ? OFFSET ?");

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(sql.toString());
            setParametrosBusqueda(stmt, filtro, filtro.getIdEstado(), 0);
            int parametros = 11;
            if (incluirReclamoRp) {
                setParametrosBusqueda(stmt, filtro,
                        Integer.valueOf(WebKeysCompras.ESTADO_RECLAMO_RP), parametros);
                parametros += 11;
            }
            stmt.setInt(parametros + 1,
                    BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA);
            stmt.setLong(parametros + 2, ((long) pagina - 1L)
                    * BusquedaRequerimientoCompraServiceUtil.REQUERIMIENTOS_POR_PAGINA);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RequerimientoCompra requerimiento = mapRequerimiento(rs);
                requerimiento.setTotalRegistros(rs.getInt("total_registros"));
                resultado.add(requerimiento);
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void setParametrosBusqueda(PreparedStatement stmt,
            RequerimientoCompraFiltro filtro, Integer estado, int inicio) throws Exception {
        setNullableInteger(stmt, inicio + 1, estado);
        setNullableInteger(stmt, inicio + 2, filtro.getIdSector());
        stmt.setString(inicio + 3, filtro.getAfiliadoCuilTitular());
        setNullableInteger(stmt, inicio + 4, filtro.getAfiliadoInt());
        stmt.setString(inicio + 5, filtro.getIdTercerizadora());
        setNullableBoolean(stmt, inicio + 6, filtro.getRecupero());
        setNullableBoolean(stmt, inicio + 7, filtro.getSurge());
        stmt.setString(inicio + 8, filtro.getTexto());
        setNullableDate(stmt, inicio + 9, filtro.getFechaAltaDesde());
        setNullableDate(stmt, inicio + 10, filtro.getFechaAltaHasta());
        setNullableInteger(stmt, inicio + 11, filtro.getIdRequerimientoCompra());
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
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_GET_REQUERIMIENTO);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            RequerimientoCompra requerimiento = mapRequerimiento(rs);
            requerimiento.setObservacionInterna(rs.getString("observacion_interna"));
            requerimiento.setMotivoBaja(rs.getString("motivo_baja"));
            return requerimiento;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<Map<String, Object>> getFilasPedidoPresupuestoPdf(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> filas =
                new ArrayList<Map<String, Object>>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    "{call compras.get_requerimiento_compra_pdf(?)}"
            );
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();
            ResultSetMetaData columnas = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<String, Object>();
                for (int i = 1; i <= columnas.getColumnCount(); i++) {
                    fila.put(columnas.getColumnLabel(i), rs.getObject(i));
                }
                filas.add(fila);
            }
            return filas;
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

    public Map<Integer, String> listarDrogasMedicamentosBatch(
            List<Integer> troqueles) throws Exception {

        Map<Integer, String> resultado = new HashMap<Integer, String>();
        if (troqueles == null || troqueles.isEmpty()) {
            return resultado;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT m.troquel, m.droga FROM public.medicamentos m "
                        + "WHERE m.troquel IN ("
        );
        for (int i = 0; i < troqueles.size(); i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
        sql.append(
                ") AND m.fecha <= current_date AND m.baja_fecha IS NULL "
                        + "AND m.fecha = (SELECT max(m2.fecha) "
                        + "FROM public.medicamentos m2 "
                        + "WHERE m2.nro_registro = m.nro_registro)"
        );

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < troqueles.size(); i++) {
                stmt.setInt(i + 1, troqueles.get(i).intValue());
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                Integer troquel = Integer.valueOf(rs.getInt("troquel"));
                String droga = rs.getString("droga");
                droga = droga != null ? droga.trim() : "";

                if (!resultado.containsKey(troquel)) {
                    resultado.put(troquel, droga);
                } else if (!resultado.get(troquel).equals(droga)) {
                    // Una asociacion ambigua usa la convencion de dato ausente.
                    resultado.put(troquel, "");
                }
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

                detalle.setIdTipoPrestacion(
                        getInteger(rs, "id_tipo_prestacion")
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
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraSector> resultado =
                new ArrayList<RequerimientoCompraSector>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_SECTORES);
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
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<TipoPrestacionCompra> resultado =
                new ArrayList<TipoPrestacionCompra>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_TIPOS_PRESTACION);
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
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_ESTADO);
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
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_SECTOR);
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
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_TIENE_SITUACION_MEDICA_VIGENTE);
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

    public List<Empresa> buscarEmpresasCotizacion(
            String cuit,
            String descripcion,
            String sucursal,
            int limite) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<Empresa> resultado = new ArrayList<Empresa>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BUSCAR_EMPRESAS_COTIZACION);
            stmt.setString(1, cuit);
            stmt.setString(2, descripcion);
            stmt.setString(3, sucursal);
            stmt.setInt(4, limite);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(
                        new Empresa(
                                rs.getString("cuit"),
                                rs.getString("sucursal"),
                                rs.getString("razon_soc")
                        )
                );
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<Empresa> buscarEmpresasCotizacionRapida(
            String cuit,
            String descripcion,
            String sucursal,
            int limite) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<Empresa> resultado = new ArrayList<Empresa>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    SQL_BUSCAR_EMPRESAS_COTIZACION_RAPIDA
            );
            stmt.setString(1, cuit);
            stmt.setString(2, descripcion);
            stmt.setString(3, sucursal);
            stmt.setInt(4, limite);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(
                        new Empresa(
                                rs.getString("cuit"),
                                rs.getString("sucursal"),
                                rs.getString("razon_soc")
                        )
                );
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean esRequerimientoHabilitadoBusquedaEmpresaCotizacion(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    SQL_REQUERIMIENTO_HABILITADO_BUSQUEDA_EMPRESA
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
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_PRESUPUESTOS);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, 1);
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

    public List<RequerimientoCompraPresupuesto> listarCotizacionesEmpresa(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_COTIZACIONES_EMPRESA);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, 3);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapCotizacionEmpresa(rs));
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
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_PRESUPUESTO);
            stmt.setInt(1, idRequerimientoPresupuesto);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setInt(3, 1);
            rs = stmt.executeQuery();

            return rs.next() ? mapPresupuesto(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompraPresupuesto getCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_COTIZACION_EMPRESA);
            stmt.setInt(1, idRequerimientoPresupuesto);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setInt(3, 3);
            rs = stmt.executeQuery();

            return rs.next() ? mapCotizacionEmpresa(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_ORDEN_MEDICA);
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

    public Map<Integer, List<PrestadorCotizacion>> listarPrestadoresAdjudicadosBatch(
            List<Integer> ids) throws Exception {
        Map<Integer, List<PrestadorCotizacion>> resultado =
                new HashMap<Integer, List<PrestadorCotizacion>>();
        if (ids.isEmpty()) {
            return resultado;
        }
        StringBuilder array = new StringBuilder("{");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                array.append(',');
            }
            array.append(ids.get(i).intValue());
        }
        array.append('}');
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall("{call compras.listar_prestadores_adjudicados_batch(?)}");
            stmt.setString(1, array.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = Integer.valueOf(rs.getInt("id_requerimiento"));
                List<PrestadorCotizacion> prestadores = resultado.get(id);
                if (prestadores == null) {
                    prestadores = new ArrayList<PrestadorCotizacion>();
                    resultado.put(id, prestadores);
                }
                PrestadorCotizacion prestador = new PrestadorCotizacion();
                prestador.setIdPrestador(rs.getInt("id_prestador"));
                prestador.setDescripcion(rs.getString("descripcion"));
                prestadores.add(prestador);
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
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<Integer> resultado = new ArrayList<Integer>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_PRESTADORES_ADJUDICADOS);
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
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_PRESUPUESTOS_PRESTADOR);
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

    private RequerimientoCompraPresupuesto mapCotizacionEmpresa(ResultSet rs)
            throws Exception {

        RequerimientoCompraPresupuesto presupuesto =
                mapPresupuesto(rs);

        presupuesto.setEmpresaAdjudicada(rs.getBoolean("empresa_adjudicada"));
        presupuesto.setEmpresaCuit(getString(rs, "empresa_cuit"));
        presupuesto.setEmpresaSucursal(
                getString(rs, "empresa_sucursal")
        );
        presupuesto.setDescripcionEmpresa(
                getString(rs, "descripcion_empresa")
        );

        return presupuesto;
    }

    private void setNullableInteger(
            PreparedStatement stmt,
            int index,
            Integer value) throws Exception {

        if (value == null || value.intValue() < 0) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableDate(
            PreparedStatement stmt,
            int index,
            java.util.Date value) throws Exception {

        if (value == null) {
            stmt.setNull(index, Types.DATE);
        } else {
            stmt.setDate(
                    index,
                    new java.sql.Date(value.getTime())
            );
        }
    }

    private void setNullableBoolean(
            PreparedStatement stmt,
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
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con =
                    ConnectionHelper.getConnection();

            stmt =
                    con.prepareCall(
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
    public List<SectorNomencladorCompra> getConfiguracionNomenclador(
            int idSector, Integer idTipoPrestacion) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SectorNomencladorCompra> resultado =
                new ArrayList<SectorNomencladorCompra>();
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "SELECT * FROM compras.resolver_sector_nomenclador(?,?)");
            stmt.setInt(1, idSector);
            if (idTipoPrestacion == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, idTipoPrestacion.intValue());
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(SectorNomencladorCompra.getMapping(rs));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<Integer> listarSectoresNomenclador() throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Integer> resultado = new ArrayList<Integer>();
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "SELECT DISTINCT sn.id_sector FROM compras.sector_nomenclador sn "
                            + "JOIN compras.sector_requerimiento s "
                            + "ON s.id_sector = sn.id_sector "
                            + "WHERE s.activo = TRUE AND s.baja_fecha IS NULL "
                            + "ORDER BY sn.id_sector");
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(Integer.valueOf(rs.getInt("id_sector")));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<Nomenclador> buscarNomencladores(
            int idSector, Integer idTipoPrestacion,
            String descripcion, String codigo,
            List<Integer> troqueles) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Nomenclador> resultado = new ArrayList<Nomenclador>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM compras.buscar_nomencladores(?,?,?,?,"
        );
        if (troqueles == null) {
            sql.append("NULL::INTEGER[])");
        } else {
            sql.append("ARRAY[");
            for (int i = 0; i < troqueles.size(); i++) {
                if (i > 0) {
                    sql.append(',');
                }
                sql.append('?');
            }
            sql.append("]::INTEGER[])");
        }
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(sql.toString());
            stmt.setInt(1, idSector);
            if (idTipoPrestacion == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, idTipoPrestacion.intValue());
            }
            stmt.setString(3, WebKeysCompras.trimToNull(descripcion));
            stmt.setString(4, WebKeysCompras.trimToNull(codigo));
            if (troqueles != null) {
                for (int i = 0; i < troqueles.size(); i++) {
                    Integer troquel = troqueles.get(i);
                    if (troquel == null) {
                        stmt.setNull(5 + i, Types.INTEGER);
                    } else {
                        stmt.setInt(5 + i, troquel.intValue());
                    }
                }
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(Nomenclador.getMapping(rs));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

}
