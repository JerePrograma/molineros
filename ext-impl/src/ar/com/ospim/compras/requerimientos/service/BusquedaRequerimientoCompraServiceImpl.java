package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper;

import ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;
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

    private static final String SQL_BUSCAR_REQUERIMIENTOS_FILTRADOS =
            "SELECT rb.* FROM compras.requerimiento_base() rb CROSS JOIN (SELECT q.*, NULLIF(upper(btrim(q.p_texto)), '') A"
                    + "S v_texto, NULLIF(regexp_replace(COALESCE(q.p_afiliado_cuil_titular, ''), '[^0-9]', '', 'g'), '') AS v_cuil FR"
                    + "OM (SELECT ?::INTEGER AS p_estado, ?::INTEGER AS p_sector, ?::VARCHAR AS p_afiliado_cuil_titular, ?::INTEGER A"
                    + "S p_afiliado_int, ?::VARCHAR AS p_id_tercerizadora, ?::BOOLEAN AS p_recupero, ?::BOOLEAN AS p_surge, ?::VARCHA"
                    + "R AS p_texto, ?::DATE AS p_fecha_alta_desde, ?::DATE AS p_fecha_alta_hasta, ?::INTEGER AS p_id_requerimiento_c"
                    + "ompra, ?::INTEGER AS p_estado_anulado) q) p WHERE (rb.baja_fecha IS NULL OR rb.id_estado = p.p_estado_anulado)"
                    + " AND ( p.p_estado IS NULL OR rb.id_estado = p.p_estado ) AND ( p.p_sector IS NULL OR rb.id_sector = p.p_sector"
                    + " ) AND ( p.p_id_requerimiento_compra IS NULL OR rb.id = p.p_id_requerimiento_compra ) AND ( p.v_cuil IS NULL O"
                    + "R regexp_replace( COALESCE( rb.afiliado_cuil_titular, '' ), '[^0-9]', '', 'g' ) LIKE '%' || p.v_cuil || '%' ) "
                    + "AND ( p.p_afiliado_int IS NULL OR rb.afiliado_int = p.p_afiliado_int ) AND ( NULLIF( btrim(p.p_id_tercerizador"
                    + "a), '' ) IS NULL OR upper( COALESCE( rb.id_tercerizadora, '' ) ) = upper( btrim(p.p_id_tercerizadora) ) ) AND "
                    + "( p.p_recupero IS NULL OR rb.recupero = p.p_recupero ) AND ( p.p_surge IS NULL OR rb.surge = p.p_surge ) AND ("
                    + " p.p_fecha_alta_desde IS NULL OR rb.alta_fecha >= p.p_fecha_alta_desde ) AND ( p.p_fecha_alta_hasta IS NULL OR"
                    + " rb.alta_fecha < p.p_fecha_alta_hasta + INTERVAL '1 day' ) AND ( p.v_texto IS NULL OR rb.id::VARCHAR = btrim(p"
                    + ".p_texto) OR upper( COALESCE( rb.observaciones, '' ) ) LIKE '%' || p.v_texto || '%' OR upper( COALESCE( rb.sec"
                    + "tor_descripcion, '' ) ) LIKE '%' || p.v_texto || '%' OR upper( COALESCE( rb.afiliado_nombre_apellido, '' ) ) L"
                    + "IKE '%' || p.v_texto || '%' OR upper( COALESCE( rb.afiliado_documento, '' ) ) LIKE '%' || p.v_texto || '%' OR "
                    + "EXISTS ( SELECT 1 FROM compras.requerimiento_detalle d WHERE d.id_requerimiento = rb.id AND d.baja_fecha IS NU"
                    + "LL AND ( upper( COALESCE( d.tipo_item, '' ) ) LIKE '%' || p.v_texto || '%' OR upper( COALESCE(d.codigo_nomencl"
                    + "ador, '') ) LIKE '%' || p.v_texto || '%' OR upper( COALESCE(d.descripcion_nomenclador, '') ) LIKE '%' || p.v_t"
                    + "exto || '%' OR upper( COALESCE(d.nombre_medicamento, '') ) LIKE '%' || p.v_texto || '%' OR COALESCE(d.troquel:"
                    + ":VARCHAR, '') LIKE '%' || btrim(p.p_texto) || '%' OR upper( COALESCE( d.observaciones, '' ) ) LIKE '%' || p.v_"
                    + "texto || '%' ) ) )";

    private static final String SQL_BUSCAR_REQUERIMIENTOS =
            "SELECT rb.*, afi.id_seccional AS afiliado_id_seccional, sec.descripcion AS afiliado_seccional_descripcion FROM ("
                    + SQL_BUSCAR_REQUERIMIENTOS_FILTRADOS
                    + ") rb LEFT JOIN public.afiliado afi ON afi.cuil_titular = rb.afiliado_cuil_titular AND afi.inte = rb.afiliado_int LEFT JOIN public.seccional sec ON sec.id_seccional = afi.id_seccional ORDER BY rb.id DESC";

    /* La entrada sin filtros pagina IDs antes de armar las cabeceras. */
    private static final String SQL_BUSCAR_REQUERIMIENTOS_INICIALES =
            "WITH candidatos AS ("
                    + "SELECT r.id_requerimiento FROM compras.requerimiento r "
                    + "JOIN compras.sector_requerimiento s ON s.id_sector = r.id_sector "
                    + "WHERE (r.baja_fecha IS NULL OR r.estado = ?)"
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

    private static final String SQL_LISTAR_TIPOS_PRESTACION =
            "{call compras.listar_tipos_prestacion()}";

    private static final String SQL_GET_SECTOR =
            "{call compras.get_sector_requerimiento(?)}";

    private static final String SQL_BUSCAR_EMPRESAS_COTIZACION =
            "{call compras.buscar_empresas_cotizacion(?,?,?,?)}";

    private static final String SQL_BUSCAR_EMPRESAS_COTIZACION_RAPIDA =
            "{call compras.buscar_empresas_cotizacion_rapida(?,?,?,?)}";

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

            String estadosIds = ConfiguracionComprasHelper.leerObligatoria(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS, con);
            int estadoAnulado = ConfiguracionComprasHelper.resolverEstado("ANULADO", estadosIds);
            setParametrosBusqueda(stmt, filtro, filtro.getIdEstado(), 0);
            stmt.setInt(12, estadoAnulado);

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
            String estadosIds = ConfiguracionComprasHelper.leerObligatoria(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS, con);
            int estadoAnulado = ConfiguracionComprasHelper.resolverEstado("ANULADO", estadosIds);
            int porPagina = ConfiguracionComprasHelper.resolverLimite("REQUERIMIENTOS_POR_PAGINA",
                    ConfiguracionComprasHelper.leerObligatoria("COMPRAS_LIMITES", con));
            stmt.setInt(1, estadoAnulado);
            stmt.setInt(2, porPagina);
            stmt.setLong(3, ((long) pagina - 1L) * porPagina);
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
        String consulta = SQL_BUSCAR_REQUERIMIENTOS_FILTRADOS;
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
            String estadosIds = ConfiguracionComprasHelper.leerObligatoria(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS, con);
            int estadoAnulado = ConfiguracionComprasHelper.resolverEstado("ANULADO", estadosIds);
            setParametrosBusqueda(stmt, filtro, filtro.getIdEstado(), 0);
            stmt.setInt(12, estadoAnulado);
            int parametros = 12;
            if (incluirReclamoRp) {
                setParametrosBusqueda(stmt, filtro,
                        Integer.valueOf(ConfiguracionComprasHelper.resolverEstado("RECLAMO_RP", estadosIds)), parametros);
                stmt.setInt(parametros + 12, estadoAnulado);
                parametros += 12;
            }
            int porPagina = ConfiguracionComprasHelper.resolverLimite("REQUERIMIENTOS_POR_PAGINA",
                    ConfiguracionComprasHelper.leerObligatoria("COMPRAS_LIMITES", con));
            stmt.setInt(parametros + 1, porPagina);
            stmt.setLong(parametros + 2, ((long) pagina - 1L) * porPagina);
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
        Connection con=null;
        try{con=ConnectionHelper.getConnection();return listarSectores(con);}
        finally{ConnectionHelper.cerrar(con);}
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement("SELECT estado AS id FROM compras.requerimiento WHERE id_requerimiento = ?");
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            RequerimientoCompraEstado estado = new RequerimientoCompraEstado();
            estado.setId(getInteger(rs, "id"));
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

    public boolean existeRequerimientoDuplicado(String cuil,int integrante,int prestacion,
            java.util.Date fecha,int excluir)throws Exception {
        Connection con=null;
        try{con=ConnectionHelper.getConnection();return existeRequerimientoDuplicado(con,cuil,integrante,prestacion,fecha,excluir);}
        finally{ConnectionHelper.cerrar(con);}
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra, int limite) throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return listarPrestadoresEnviados(con, idRequerimientoCompra, limite);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(Connection con,
            int idRequerimientoCompra, int limite) throws Exception {
        return new NotificarCotizacionPrestadorHelper().listarPrestadoresEnviados(con, idRequerimientoCompra, limite);
    }

    public boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {
        return new NotificarCotizacionPrestadorHelper().hayPrestadoresPendientesNotificacionJava(idRequerimientoCompra);
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
        return new PresupuestoCompraHelper().esRequerimientoHabilitadoBusquedaEmpresaCotizacion(idRequerimientoCompra);
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
        RequerimientoCompra r = RequerimientoCompra.paraMapeo();

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
        try {
            con = ConnectionHelper.getConnection();
            return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                    .resolverConfiguracion(idSector, idTipoPrestacion,
                            listarSectores(con), listarTiposPrestacion(con),
                            listarAsociacionesNomenclador(con, idSector));
        } finally {
            ConnectionHelper.cerrar(con);
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
        // Adaptador de compatibilidad: la decision funcional reside en el Helper.
        return ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper
                .buscarConfigurados(idSector, idTipoPrestacion, descripcion, codigo, troqueles);
    }


    public String getConfiguracionCompra(String clave) throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return getConfiguracionCompra(clave, con);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    /** Lee el valor crudo sin cerrar la conexion de la operacion. */
    public String getConfiguracionCompra(String clave, Connection con)
            throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT valor FROM public.system_config WHERE id = ?");
            stmt.setString(1, clave);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            String valor = rs.getString("valor");
            if (rs.next()) {
                throw new java.sql.SQLException(
                        "La configuracion de Compras tiene una clave duplicada: " + clave);
            }
            return valor;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public RequerimientoCompra getRequerimientoCompra(Connection con,
            int idRequerimientoCompra) throws Exception {
        RequerimientoCompra requerimiento = getCabeceraRequerimiento(con, idRequerimientoCompra);
        if (requerimiento != null) {
            requerimiento.setDetalles(getDetalles(con, idRequerimientoCompra));
        }
        return requerimiento;
    }

    public RequerimientoCompra getCabeceraRequerimiento(Connection con,
            int idRequerimientoCompra) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
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
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraDetalle> getDetalles(Connection con,
            int idRequerimientoCompra) throws Exception {
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraDetalle> resultado = new ArrayList<RequerimientoCompraDetalle>();
        try {
            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO_DETALLE);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(mapDetalle(rs));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraSector> listarSectores(Connection con)throws Exception {
        return NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(listarCatalogoSectores(con),
                getConfiguracionCompra("COMPRAS_SECTORES_NO_SELECCIONABLES",con));
    }

    public List<Map<String,Object>> listarCatalogoSectores(Connection con)throws Exception {
        PreparedStatement stmt=null;ResultSet rs=null;
        List<Map<String,Object>> catalogo=new ArrayList<Map<String,Object>>();
        try{
            stmt=con.prepareStatement("SELECT id_sector,descripcion,requiere_afiliado,activo,baja_fecha "
                    + "FROM compras.sector_requerimiento ORDER BY descripcion");
            rs=stmt.executeQuery();
            while(rs.next()){
                Map<String,Object> fila=new HashMap<String,Object>();fila.put("id",getInteger(rs,"id_sector"));
                fila.put("descripcion",getString(rs,"descripcion"));fila.put("requiere_afiliado",Boolean.valueOf(rs.getBoolean("requiere_afiliado")));
                fila.put("activo",Boolean.valueOf(rs.getBoolean("activo")));fila.put("baja",rs.getTimestamp("baja_fecha"));catalogo.add(fila);
            }
        }finally{closeQuietly(rs);ConnectionHelper.cerrar(stmt);}
        return catalogo;
    }

    public List<TipoPrestacionCompra> listarTiposPrestacion(Connection con) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TipoPrestacionCompra> resultado = new ArrayList<TipoPrestacionCompra>();
        try {
            stmt = con.prepareStatement("SELECT t.id_tipo_prestacion,t.descripcion,t.id_sector, "
                    + "s.descripcion AS sector_descripcion FROM compras.tipo_prestacion t "
                    + "JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector "
                    + "ORDER BY t.id_tipo_prestacion");
            rs = stmt.executeQuery();
            while (rs.next()) {
                TipoPrestacionCompra tipo = new TipoPrestacionCompra();
                tipo.setId(getInteger(rs, "id_tipo_prestacion"));
                tipo.setDescripcion(getString(rs, "descripcion"));
                tipo.setIdSector(getInteger(rs, "id_sector"));
                tipo.setSectorDescripcion(getString(rs, "sector_descripcion"));
                resultado.add(tipo);
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<SectorNomencladorCompra> listarAsociacionesNomenclador(int idSector)
            throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return listarAsociacionesNomenclador(con, idSector);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public List<SectorNomencladorCompra> listarAsociacionesNomenclador(Connection con,
            int idSector) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SectorNomencladorCompra> resultado = new ArrayList<SectorNomencladorCompra>();
        try {
            stmt = con.prepareStatement("SELECT sn.id_sector,sn.id_tipo_prestacion,sn.id_tipo_nomenclador, "
                    + "sn.marca_rein_liq,sn.codigo_nomenclador, "
                    + "tn.id_tipo_nomenclador AS tipo_nomenclador_catalogo "
                    + "FROM compras.sector_nomenclador sn LEFT JOIN public.tipo_nomenclador tn "
                    + "ON tn.id_tipo_nomenclador = sn.id_tipo_nomenclador "
                    + "WHERE sn.id_sector = ? ORDER BY sn.id_tipo_nomenclador,sn.marca_rein_liq,sn.codigo_nomenclador");
            stmt.setInt(1, idSector);
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(SectorNomencladorCompra.getMapping(rs));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public Nomenclador obtenerNomencladorCanonico(int idPrestacion) throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return obtenerNomencladorCanonico(con, idPrestacion);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public Nomenclador obtenerNomencladorCanonico(Connection con, int idPrestacion)
            throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM autorizaciones.busca_nomenclador_por_id(?)");
            stmt.setInt(1, idPrestacion);
            rs = stmt.executeQuery();
            return rs.next() ? Nomenclador.getMapping(rs) : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<Nomenclador> buscarNomencladoresCandidatos(Integer idTipo,
            String descripcion, String codigo) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Nomenclador> resultado = new ArrayList<Nomenclador>();
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement("SELECT * FROM autorizaciones.busca_nomenclador(?,?,NULL,?,FALSE,NULL)");
            if (idTipo == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, idTipo.intValue());
            }
            stmt.setString(2, WebKeysCompras.trimToNull(descripcion));
            stmt.setString(3, WebKeysCompras.trimToNull(codigo));
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

    public boolean existeRequerimientoDuplicado(Connection con,String cuil,int integrante,
            int prestacion,java.util.Date fecha,int excluir)throws Exception {
        return NomencladorCompraBusquedaHelper.existeRequerimientoDuplicado(con,cuil,integrante,prestacion,fecha,excluir);
    }

    public List<Map<String,Object>> listarDatosDuplicado(Connection con,String cuil,int integrante,int prestacion)throws Exception {
        PreparedStatement stmt=null;ResultSet rs=null;
        List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();
        try{
            stmt=con.prepareStatement("SELECT r.id_requerimiento,r.estado,r.baja_fecha AS requerimiento_baja,"
                    + "r.afiliado_cuil_titular,r.afiliado_int,d.tipo_item,d.id_prestacion,d.baja_fecha AS detalle_baja,"
                    + "rp.tipo_documento,rp.fecha_documento,rp.baja_fecha AS documento_baja FROM compras.requerimiento r "
                    + "JOIN compras.requerimiento_detalle d ON d.id_requerimiento=r.id_requerimiento "
                    + "JOIN compras.requerimiento_presupuesto rp ON rp.id_requerimiento=r.id_requerimiento "
                    + "WHERE r.afiliado_cuil_titular=? AND r.afiliado_int=? AND d.id_prestacion=? "
                    + "ORDER BY r.id_requerimiento,d.id_detalle,rp.id_requerimiento_presupuesto");
            stmt.setString(1,cuil);stmt.setInt(2,integrante);stmt.setInt(3,prestacion);rs=stmt.executeQuery();
            while(rs.next()){
                Map<String,Object> f=new HashMap<String,Object>();int valor_id_requerimiento=rs.getInt("id_requerimiento");f.put("id_requerimiento",rs.wasNull()?null:Integer.valueOf(valor_id_requerimiento));
                int valor_estado=rs.getInt("estado");f.put("estado",rs.wasNull()?null:Integer.valueOf(valor_estado));f.put("requerimiento_baja",rs.getTimestamp("requerimiento_baja"));
                f.put("cuil",rs.getString("afiliado_cuil_titular"));int valor_integrante=rs.getInt("afiliado_int");f.put("integrante",rs.wasNull()?null:Integer.valueOf(valor_integrante));
                f.put("tipo_item",rs.getString("tipo_item"));int valor_id_prestacion=rs.getInt("id_prestacion");f.put("id_prestacion",rs.wasNull()?null:Integer.valueOf(valor_id_prestacion));
                f.put("detalle_baja",rs.getTimestamp("detalle_baja"));int valor_tipo_documento=rs.getInt("tipo_documento");f.put("tipo_documento",rs.wasNull()?null:Integer.valueOf(valor_tipo_documento));
                f.put("fecha_documento",rs.getDate("fecha_documento"));f.put("documento_baja",rs.getTimestamp("documento_baja"));filas.add(f);
            }return filas;
        }finally{closeQuietly(rs);ConnectionHelper.cerrar(stmt);}
    }

    public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(
            Connection con, int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
            stmt = con.prepareCall(SQL_GET_ORDEN_MEDICA);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapPresupuesto(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarPresupuestos(
            Connection con, int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> resultado =
                new ArrayList<RequerimientoCompraPresupuesto>();

        try {
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
            ConnectionHelper.cerrar(stmt);
        }
    }

    public RequerimientoCompraPresupuesto bloquearDocumento(Connection con,
            int idRequerimiento, int idDocumento) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT rp.* FROM compras.requerimiento_presupuesto rp "
                    + "WHERE rp.id_requerimiento = ? AND rp.id_requerimiento_presupuesto = ? FOR UPDATE");
            stmt.setInt(1, idRequerimiento);
            stmt.setInt(2, idDocumento);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return rs.getInt("tipo_documento") == RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA
                    ? mapCotizacionEmpresa(rs) : mapPresupuesto(rs);
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraPresupuesto> listarDocumentos(Connection con,
            int idRequerimiento, int tipoDocumento) throws Exception {
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraPresupuesto> documentos = new ArrayList<RequerimientoCompraPresupuesto>();
        try {
            stmt = con.prepareCall(SQL_LISTAR_PRESUPUESTOS);
            stmt.setInt(1, idRequerimiento);
            stmt.setInt(2, tipoDocumento);
            rs = stmt.executeQuery();
            while (rs.next()) {
                documentos.add(tipoDocumento == RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA
                        ? mapCotizacionEmpresa(rs) : mapPresupuesto(rs));
            }
            return documentos;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<Empresa> buscarEmpresasCotizacion(Connection con, String cuit,
            String descripcion, String sucursal, int limite) throws Exception {
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<Empresa> empresas = new ArrayList<Empresa>();
        try {
            stmt = con.prepareCall(SQL_BUSCAR_EMPRESAS_COTIZACION);
            stmt.setString(1, cuit);
            stmt.setString(2, descripcion);
            stmt.setString(3, sucursal);
            stmt.setInt(4, limite);
            rs = stmt.executeQuery();
            while (rs.next()) {
                empresas.add(new Empresa(rs.getString("cuit"), rs.getString("sucursal"), rs.getString("razon_soc")));
            }
            return empresas;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }
}
