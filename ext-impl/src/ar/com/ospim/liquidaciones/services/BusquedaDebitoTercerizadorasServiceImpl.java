package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

/**
 * <a href="BusquedaDebitoTercerizadorasServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 *
 * @author Pablo Conde
 */
public class BusquedaDebitoTercerizadorasServiceImpl {
    static int entidad = WebKeysGlobal.OSPIM;


    private static final Log _log = LogFactoryUtil.getLog(BusquedaDebitoTercerizadorasServiceImpl.class);

    public List<DebitosLiquidacionesPendientes> getBusquedaDebitosaLiquidacionesPendientes(Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras)
            throws NumberFormatException {


        Connection con = null;
        CallableStatement stmt = null;
        ArrayList<DebitosLiquidacionesPendientes> debitosAutogestion = null;
        debitosAutogestion = new ArrayList<DebitosLiquidacionesPendientes>();
        BigDecimal montoTotal = new BigDecimal(0);
        BigDecimal montoPrestador = new BigDecimal(0);


        try {
            String sql = "{call public.reporte_debito_liq_pendientes(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());
            stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
            stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
            stmt.setString(3, idTercerizadoras);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DebitosLiquidacionesPendientes debito = new DebitosLiquidacionesPendientes();

                debito.setHospitalesAutogestion(rs.getString("v_nombre_prestador"));
                debito.setFactura(rs.getString("v_numero_factura"));
                debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
                debito.setCargoPrestadoraReclamo(rs.getBigDecimal("v_cargo_reclamo") != null ? rs.getBigDecimal("v_cargo_reclamo") : new BigDecimal("0"));


                //acumulador
                montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0"));

                debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
                montoTotal = montoTotal.add(rs.getBigDecimal("v_monto_prestador"));

                debitosAutogestion.add(debito);


            }
            debitosaTotal.setMontoLiquidacionPendiente(montoPrestador);
            debitosaTotal.setMontoLiquidacionPendienteDebito(montoPrestador);
        } catch (Exception e) {
            _log.error(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
        return debitosAutogestion;
    }


    public List<?> getBusquedaDebitosaGrabados(String tipo, Date fechaHasta, String idTercerizadoras)
            throws SystemException, NumberFormatException, ParseException {
        _log.info("[CHECK-GRABADOS] tipo=" + tipo + " fechaHasta=" + fechaHasta + " terc=" + idTercerizadoras);

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        // Listas SIEMPRE no-null
        ArrayList<DebitosLiquidacionesPendientes> debitosAutogestion = new ArrayList<DebitosLiquidacionesPendientes>();
        ArrayList<DebitosHospitales> debitosHospitales = new ArrayList<DebitosHospitales>();
        ArrayList<DebitosaReintegros> listaReintegros = new ArrayList<DebitosaReintegros>();
        ArrayList<DebitosaPrestadores> listaPrestadores = new ArrayList<DebitosaPrestadores>();

        try {
            String sql = "{call public.reporte_debito_grabado_detalle(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            stmt.setString(1, tipo);

            if (fechaHasta != null) {
                stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }

            stmt.setString(3, idTercerizadoras);

            rs = stmt.executeQuery();

            if (WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES.equals(tipo)) {

                while (rs.next()) {
                    DebitosLiquidacionesPendientes debito = new DebitosLiquidacionesPendientes();

                    debito.setHospitalesAutogestion(rs.getString("descripcion"));
                    debito.setFactura(rs.getString("numero_factura"));

                    BigDecimal monto = rs.getBigDecimal("monto_debitar");
                    if (monto == null) monto = BigDecimal.ZERO;
                    debito.setMonto(monto);

                    BigDecimal reclamo = rs.getBigDecimal("monto_debitar_reclamo");
                    if (reclamo == null) reclamo = BigDecimal.ZERO;
                    debito.setCargoPrestadoraReclamo(reclamo);

                    debitosAutogestion.add(debito);
                }
                return debitosAutogestion;

            } else if (WebKeysLiquidaciones.DEBITOS_HOSPITALES.equals(tipo)) {

                while (rs.next()) {
                    DebitosHospitales debito = new DebitosHospitales();

                    debito.setHospital(rs.getString("descripcion"));
                    debito.setFactura(rs.getString("numero_factura"));

                    BigDecimal monto = rs.getBigDecimal("monto_debitar");
                    if (monto == null) monto = BigDecimal.ZERO;
                    debito.setMonto(monto);

                    // numero_op es numeric en tabla -> tomar como String estable
                    BigDecimal op = rs.getBigDecimal("numero_op");
                    debito.setOrdenPago(op != null ? op.toPlainString() : rs.getString("numero_op"));

                    // >>> CLAVE: el ID ahora está en liquidacion_id (NO en numero)
                    Integer liq = (Integer) rs.getObject("liquidacion_id"); // NULL-safe
                    debito.setIdLiquidacion(liq); // Debe ser Integer en el bean

                    // Nuevos campos (pueden venir null)
                    debito.setCargoPrestadora(rs.getBigDecimal("cargo_prestadora"));
                    debito.setImporteTotal(rs.getBigDecimal("importe_total"));

                    // Dedup: si no tenés un criterio sólido, deduplicá por (op + liq) y tolerá nulls
                    if (!this.existeElemento(debitosHospitales, debito)) {
                        debitosHospitales.add(debito);
                    }
                }
                return debitosHospitales;
            } else if (WebKeysLiquidaciones.DEBITOS_REINTEGROS.equals(tipo)) {

                while (rs.next()) {
                    DebitosaReintegros reintegro = new DebitosaReintegros();

                    reintegro.setDescripcion(rs.getString("descripcion"));
                    reintegro.setDocumento(rs.getString("numero_documento"));
                    reintegro.setFechaOP(rs.getDate("fecha_op"));
                    reintegro.setSeccional(rs.getString("desc_seccional"));
                    reintegro.setNumeroOP(rs.getString("numero_op"));

                    reintegro.setApellido(rs.getString("apellido"));
                    reintegro.setNombre(rs.getString("nombre"));
                    reintegro.setNumReintegro(rs.getInt("numero"));

                    BigDecimal monto = rs.getBigDecimal("monto_debitar");
                    if (monto == null) monto = BigDecimal.ZERO;
                    reintegro.setImporteTotal(monto);

                    reintegro.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));

                    listaReintegros.add(reintegro);
                }
                return listaReintegros;

            } else if (WebKeysLiquidaciones.DEBITOS_PRESTADORES.equals(tipo)) {

                while (rs.next()) {
                    DebitosaPrestadores prestador = new DebitosaPrestadores();

                    prestador.setPrestador(rs.getString("descripcion"));
                    prestador.setFactura(rs.getString("numero_factura"));
                    prestador.setOrdenPago(rs.getString("numero_op"));

                    BigDecimal monto = rs.getBigDecimal("monto_debitar");
                    if (monto == null) monto = BigDecimal.ZERO;

                    prestador.setCargoPrestadora(monto);
                    prestador.setMonto(monto);

                    prestador.setIdLiquidacion(rs.getInt("numero"));
                    prestador.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
                    prestador.setReclamosPrestacionales(rs.getString("reclamos"));

                    if (!this.existeElemento(listaPrestadores, prestador)) {
                        listaPrestadores.add(prestador);
                    }
                }
                return listaPrestadores;
            }

            // Tipo desconocido => lista vacía (pero no null)
            return new ArrayList<Object>();

        } catch (SQLException e) {
            _log.error("Error en getBusquedaDebitosaGrabados tipo=" + tipo
                    + " fechaHasta=" + (fechaHasta != null ? fechaHasta.getTime() : "null")
                    + " tercerizadoras=" + idTercerizadoras, e);
            throw new SystemException(e);

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<DebitosHospitales> getBusquedaDebitosHospitales(
            Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {
        Connection con = null;
        CallableStatement stmt = null;
        List<DebitosHospitales> debitosHospitales = null;
        BigDecimal montoPrestador = new BigDecimal(0);
        BigDecimal aux = new BigDecimal(0);
        Boolean existe = false;

        try {
            String sql = "{call public.reporte_debito_hospitales(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());
            stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
            stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
            stmt.setString(3, idTercerizadoras);

            ResultSet rs = stmt.executeQuery();
            debitosHospitales = new ArrayList<DebitosHospitales>();
            while (rs.next()) {
                DebitosHospitales debito = new DebitosHospitales();


                debito.setHospital(rs.getString("v_nombre_prestador"));
                debito.setFactura(rs.getString("v_numero_factura"));
                debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
                debito.setOrdenPago(rs.getString("v_id_orden_pago"));

                debito.setIdLiquidacion(rs.getInt("v_id_liquidacion"));


                //				if (!this.existeElemento(debitosHospitales, debito) ) {
                //					debitosHospitales.add(debito);
                //					montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0") ) ;
                //				}

                //DS AGregad0 2022-09-21 Prueba porque traia 2 prestaciones para una liquidacion y no sumaba
                montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0"));
                aux = rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0");
                existe = false;
                for (DebitosHospitales deb : debitosHospitales) {
                    if (deb.getIdLiquidacion() == debito.getIdLiquidacion()) {
                        deb.setMonto(deb.getMonto().add(aux));
                        existe = true;
                    }
                }
                if (!existe) debitosHospitales.add(debito);
                //Fin Agregado
            }
            debitosaTotal.setMontoHospitales(montoPrestador);
            debitosaTotal.setMontoHospitaleDebito(montoPrestador);


        } catch (Exception e) {
            _log.error(e);
            _log.debug(e.getMessage());
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
        return debitosHospitales;
    }

    public List<DebitosHospitales> getBusquedaDebitosHospitalesStatus(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosHospitales> debitosHospitales = new ArrayList<DebitosHospitales>();
        BigDecimal montoPrestador = BigDecimal.ZERO;

        final boolean dbg = _log.isDebugEnabled();
        final int sampleRowsToLog = 50;
        final String rid = "HO-STATUS#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final java.sql.Date sqlDesde = (fechaDesde == null) ? null : new java.sql.Date(fechaDesde.getTime());
        final java.sql.Date sqlHasta = (fechaHasta == null) ? null : new java.sql.Date(fechaHasta.getTime());

        int rowCount = 0;
        int statusTrueCount = 0;
        int statusFalseCount = 0;
        int statusNullCount = 0;
        int idLiqNullCount = 0;
        int opIdNullCount = 0;

        Set<Integer> distinctLiq = new HashSet<Integer>();
        Set<String> distinctOp = new HashSet<String>();
        Map<Integer, Integer> noCoincidePorLiq = new HashMap<Integer, Integer>();

        final String sql = "SELECT * FROM public.reporte_debito_hospitales_con_status(?::date, ?::date, ?::varchar)";

        try {
            // ---- Guardas básicas (fail-closed) ----
            if (fechaDesde == null || fechaHasta == null) {
                _log.warn(prefix(rid) + "getBusquedaDebitosHospitalesStatus: fechas invalidas desde="
                        + fmt(sdf, fechaDesde) + " hasta=" + fmt(sdf, fechaHasta) + " -> retorno vacío");
                return new ArrayList<DebitosHospitales>();
            }

            if (Validator.isNull(idTercerizadoras)) {
                _log.warn(prefix(rid) + "getBusquedaDebitosHospitalesStatus: tercerizadora null/vacía -> retorno vacío (evito mezclar terceros)");
                return new ArrayList<DebitosHospitales>();
            }
            final String terc = idTercerizadoras.trim();

            con = ConnectionHelper.getConnection();
            if (dbg) _log.debug(prefix(rid) + "[DB] gotConnection=" + (con != null));

            ps = con.prepareStatement(sql);

            ps.setDate(1, sqlDesde);
            ps.setDate(2, sqlHasta);
            ps.setString(3, terc);

            _log.info(prefix(rid) + "[IN] fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                    + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                    + " tercerizadoras=" + safe(terc));

            if (dbg) {
                _log.debug(prefix(rid) + "[SQL] " + sql);
                _log.debug(prefix(rid) + "[SQL-PARAMS] p1(fechaDesde)=" + sqlDesde.toString()
                        + " p2(fechaHasta)=" + sqlHasta.toString()
                        + " p3(idTercerizadoras)=" + safe(terc));
            }

            long tQ0 = System.currentTimeMillis();
            rs = ps.executeQuery();
            long tQ1 = System.currentTimeMillis();
            if (dbg) _log.debug(prefix(rid) + "[DB] executeQuery ok, elapsedMs=" + (tQ1 - tQ0));

            if (dbg) {
                try {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix(rid)).append("[RS-META] columns=").append(cols).append(" => ");
                    for (int i = 1; i <= cols; i++) {
                        if (i > 1) sb.append(" | ");
                        sb.append(i).append(":")
                                .append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(")");
                    }
                    _log.debug(sb.toString());
                } catch (Exception metaEx) {
                    _log.warn(prefix(rid) + "[RS-META] no se pudo leer metadata", metaEx);
                }
            }

            while (rs.next()) {
                rowCount++;

                DebitosHospitales debito = new DebitosHospitales();

                BigDecimal opId = rs.getBigDecimal("v_id_orden_pago");
                if (opId == null) opIdNullCount++;
                String opIdStr = (opId == null) ? null : opId.toPlainString();
                debito.setOrdenPago(opIdStr);
                if (opIdStr != null) distinctOp.add(opIdStr);

                String hospital = rs.getString("v_nombre_prestador");
                String factura = rs.getString("v_numero_factura");
                debito.setHospital(hospital);
                debito.setFactura(factura);

                BigDecimal monto = rs.getBigDecimal("v_monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                debito.setMonto(monto);

                int idLiq = rs.getInt("v_id_liquidacion");
                boolean idLiqWasNull = rs.wasNull();
                if (idLiqWasNull) {
                    idLiqNullCount++;
                    idLiq = 0;
                }
                debito.setIdLiquidacion(idLiq);
                if (!idLiqWasNull) distinctLiq.add(idLiq);

                boolean st = rs.getBoolean("status");
                boolean stWasNull = rs.wasNull();
                if (stWasNull) {
                    statusNullCount++;
                    debito.setStatus(null);
                } else {
                    debito.setStatus(Boolean.valueOf(st));
                    if (st) {
                        statusTrueCount++;
                        noCoincidePorLiq.put(idLiq, noCoincidePorLiq.getOrDefault(idLiq, 0) + 1);
                    } else {
                        statusFalseCount++;
                    }
                }

                BigDecimal cargoPrestadora = rs.getBigDecimal("v_cargo_ospim");
                BigDecimal importeTotal = rs.getBigDecimal("v_importe");
                debito.setCargoPrestadora(cargoPrestadora);
                debito.setImporteTotal(importeTotal);

                montoPrestador = montoPrestador.add(monto);
                debitosHospitales.add(debito);

                if (dbg && rowCount <= sampleRowsToLog) {
                    _log.debug(prefix(rid) + "[ROW#" + rowCount + "] "
                            + "opId=" + bd(opId)
                            + " liq=" + idLiq + (idLiqWasNull ? "(NULL->0)" : "")
                            + " status=" + (stWasNull ? "NULL" : String.valueOf(st))
                            + " monto=" + bd(monto)
                            + " hospital=" + safe(hospital)
                            + " factura=" + safe(factura)
                            + " cargoPrestadora=" + bd(cargoPrestadora)
                            + " importeTotal=" + bd(importeTotal));
                }
            }

            // ---- CLAVE: NO PISAR TOTALES SI NO HAY FILAS ----
            if (debitosaTotal != null) {
                if (rowCount > 0) {
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-BEFORE] montoHospitales="
                            + bd(debitosaTotal.getMontoHospitales())
                            + " montoHospitaleDebito=" + bd(debitosaTotal.getMontoHospitaleDebito()));
                    debitosaTotal.setMontoHospitales(montoPrestador);
                    debitosaTotal.setMontoHospitaleDebito(montoPrestador);
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-AFTER] montoHospitales="
                            + bd(debitosaTotal.getMontoHospitales())
                            + " montoHospitaleDebito=" + bd(debitosaTotal.getMontoHospitaleDebito()));
                } else {
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-SKIP] rows=0 -> no piso debitosaTotal (mantengo totales previos para fallback)");
                }
            } else {
                _log.warn(prefix(rid) + "[TOTALS] debitosaTotal=null (no se setean totales)");
            }

            _log.info(prefix(rid) + "[OUT] rows=" + rowCount
                    + " distinctLiq=" + distinctLiq.size()
                    + " distinctOp=" + distinctOp.size()
                    + " statusTrue(NO COINCIDE)=" + statusTrueCount
                    + " statusFalse(OK)=" + statusFalseCount
                    + " statusNull=" + statusNullCount
                    + " idLiqNull=" + idLiqNullCount
                    + " opIdNull=" + opIdNullCount
                    + " montoPrestadorSum=" + bd(montoPrestador));

            if (dbg) {
                logTopNoCoincidePorLiq(rid, noCoincidePorLiq, 20);
                logSampleList(rid, debitosHospitales, 10);
            }

            return debitosHospitales;

        } catch (Exception e) {
            _log.error(prefix(rid) + "Error en getBusquedaDebitosHospitalesStatus", e);
            return debitosHospitales;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosaPrestadores> getBusquedaDebitosPrestadoresStatus(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosaPrestadores> out = new ArrayList<DebitosaPrestadores>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final boolean dbg = _log.isDebugEnabled();
        final int sampleRowsToLog = 50;
        final String rid = "PR-STATUS#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final java.sql.Date sqlDesde = (fechaDesde == null) ? null : new java.sql.Date(fechaDesde.getTime());
        final java.sql.Date sqlHasta = (fechaHasta == null) ? null : new java.sql.Date(fechaHasta.getTime());

        int rowCount = 0;
        int statusTrueCount = 0;
        int statusFalseCount = 0;
        int statusNullCount = 0;
        int reinNullCount = 0;
        int opNullCount = 0;

        Set<Integer> distinctReintegro = new HashSet<Integer>();
        Set<String> distinctOp = new HashSet<String>();
        Map<Integer, Integer> noCoincidePorReintegro = new HashMap<Integer, Integer>();

        final String sql = "SELECT * FROM public.reporte_debito_prestadores_con_status(?::date, ?::date, ?::varchar)";

        try {
            // ---- Guardas básicas (fail-closed) ----
            if (fechaDesde == null || fechaHasta == null) {
                _log.warn(prefix(rid) + "getBusquedaDebitosPrestadoresStatus: fechas invalidas desde="
                        + fmt(sdf, fechaDesde) + " hasta=" + fmt(sdf, fechaHasta) + " -> retorno vacío");
                return new ArrayList<DebitosaPrestadores>();
            }

            if (Validator.isNull(idTercerizadoras)) {
                _log.warn(prefix(rid) + "getBusquedaDebitosPrestadoresStatus: tercerizadora null/vacía -> retorno vacío (evito mezclar terceros)");
                return new ArrayList<DebitosaPrestadores>();
            }
            final String terc = idTercerizadoras.trim();

            _log.info(prefix(rid) + "[IN] fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                    + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                    + " tercerizadoras=" + safe(terc));

            if (dbg) {
                _log.debug(prefix(rid) + "[SQL] " + sql);
                _log.debug(prefix(rid) + "[SQL-PARAMS] p1(fechaDesde)=" + (sqlDesde == null ? "NULL" : sqlDesde.toString())
                        + " p2(fechaHasta)=" + (sqlHasta == null ? "NULL" : sqlHasta.toString())
                        + " p3(idTercerizadoras)=" + safe(terc));
            }

            con = ConnectionHelper.getConnection();
            if (dbg) _log.debug(prefix(rid) + "[DB] gotConnection=" + (con != null));

            ps = con.prepareStatement(sql);

            if (sqlDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, sqlDesde);

            if (sqlHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, sqlHasta);

            ps.setString(3, terc);

            long tQ0 = System.currentTimeMillis();
            rs = ps.executeQuery();
            long tQ1 = System.currentTimeMillis();
            if (dbg) _log.debug(prefix(rid) + "[DB] executeQuery ok, elapsedMs=" + (tQ1 - tQ0));

            if (dbg) {
                try {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix(rid)).append("[RS-META] columns=").append(cols).append(" => ");
                    for (int i = 1; i <= cols; i++) {
                        if (i > 1) sb.append(" | ");
                        sb.append(i).append(":")
                                .append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(")");
                    }
                    _log.debug(sb.toString());
                } catch (Exception metaEx) {
                    _log.warn(prefix(rid) + "[RS-META] no se pudo leer metadata", metaEx);
                }
            }

            while (rs.next()) {
                rowCount++;

                DebitosaPrestadores deb = new DebitosaPrestadores();

                int rein = rs.getInt("reintegro");
                boolean reinWasNull = rs.wasNull();
                if (reinWasNull) {
                    reinNullCount++;
                    rein = 0;
                }
                deb.setIdLiquidacion(rein);
                if (!reinWasNull) distinctReintegro.add(Integer.valueOf(rein));

                // consistencia con persistidores
                if (!reinWasNull && rein > 0) {
                    deb.setNumero(BigDecimal.valueOf(rein));
                } else {
                    deb.setNumero(null);
                }

                String prestador = rs.getString("prestador");
                String factura = rs.getString("num_comprobante");
                deb.setPrestador(prestador);
                deb.setFactura(factura);

                String op = rs.getString("id_orden_pago");
                if (op == null) opNullCount++;
                deb.setOrdenPago(op);
                if (op != null) distinctOp.add(op);

                BigDecimal cargoPrestadora = rs.getBigDecimal("cargo_prestadora");
                if (cargoPrestadora == null) cargoPrestadora = BigDecimal.ZERO;
                deb.setCargoPrestadora(cargoPrestadora);

                BigDecimal monto = rs.getBigDecimal("monto");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setMonto(monto);

                // NOTA: acá estabas sumando cargoPrestadora; mantengo comportamiento.
                montoPrestadorSum = montoPrestadorSum.add(cargoPrestadora);

                int rp = rs.getInt("id_reclamo_prestacional");
                boolean rpWasNull = rs.wasNull();
                if (!rpWasNull) deb.setReclamoPrestacional(rp);

                boolean st = rs.getBoolean("status");
                boolean stWasNull = rs.wasNull();
                if (stWasNull) {
                    statusNullCount++;
                    deb.setStatus(null);
                } else {
                    deb.setStatus(Boolean.valueOf(st));
                    if (st) {
                        statusTrueCount++;
                        noCoincidePorReintegro.put(rein, noCoincidePorReintegro.getOrDefault(rein, 0) + 1);
                    } else {
                        statusFalseCount++;
                    }
                }

                out.add(deb);

                if (dbg && rowCount <= sampleRowsToLog) {
                    _log.debug(prefix(rid) + "[ROW#" + rowCount + "] "
                            + "rein=" + rein + (reinWasNull ? "(NULL->0)" : "")
                            + " op=" + safe(op)
                            + " status=" + (stWasNull ? "NULL" : String.valueOf(st))
                            + " cargoPrestadora=" + bd(cargoPrestadora)
                            + " monto=" + bd(monto)
                            + " prestador=" + safe(prestador)
                            + " factura=" + safe(factura));
                }
            }

            // ---- CLAVE: NO PISAR TOTALES SI NO HAY FILAS ----
            if (debitosaTotal != null) {
                if (rowCount > 0) {
                    debitosaTotal.setMontoPrestadores(montoPrestadorSum);
                    debitosaTotal.setMontoPrestadoreDebito(montoPrestadorSum);
                } else {
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-SKIP] rows=0 -> no piso debitosaTotal (mantengo totales previos para fallback)");
                }
            }

            _log.info(prefix(rid) + "[OUT] rows=" + rowCount
                    + " distinctReintegro=" + distinctReintegro.size()
                    + " distinctOp=" + distinctOp.size()
                    + " statusTrue(NO COINCIDE)=" + statusTrueCount
                    + " statusFalse(OK)=" + statusFalseCount
                    + " statusNull=" + statusNullCount
                    + " reinNull=" + reinNullCount
                    + " opNull=" + opNullCount
                    + " montoPrestadorSum=" + bd(montoPrestadorSum));

            if (dbg) {
                logTopNoCoincidePorLiq(rid, noCoincidePorReintegro, 20);
                logSampleListPR(rid, out, 10);
            }

            return out;

        } catch (Exception e) {
            _log.error(prefix(rid) + "Error en getBusquedaDebitosPrestadoresStatus", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosaReintegros> getBusquedaDebitosReintegrosStatus(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosaReintegros> out = new ArrayList<DebitosaReintegros>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final boolean dbg = _log.isDebugEnabled();
        final int sampleRowsToLog = 50;
        final String rid = "RE-STATUS#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final java.sql.Date sqlDesde = (fechaDesde == null) ? null : new java.sql.Date(fechaDesde.getTime());
        final java.sql.Date sqlHasta = (fechaHasta == null) ? null : new java.sql.Date(fechaHasta.getTime());

        int rowCount = 0;
        int statusTrueCount = 0;
        int statusFalseCount = 0;
        int statusNullCount = 0;
        int reinNullCount = 0;
        int reinInvalidCount = 0;
        int opNullCount = 0;

        Set<Integer> distinctReintegro = new HashSet<Integer>();
        Set<String> distinctOp = new HashSet<String>();
        Map<Integer, Integer> noCoincidePorReintegro = new HashMap<Integer, Integer>();

        final String sql = "SELECT * FROM public.reporte_debito_reintegros_con_status(?::date, ?::date, ?::varchar)";

        try {
            // ---- Guardas básicas (fail-closed) ----
            if (fechaDesde == null || fechaHasta == null) {
                _log.warn(prefix(rid) + "getBusquedaDebitosReintegrosStatus: fechas invalidas desde="
                        + fmt(sdf, fechaDesde) + " hasta=" + fmt(sdf, fechaHasta) + " -> retorno vacío");
                return new ArrayList<DebitosaReintegros>();
            }

            if (Validator.isNull(idTercerizadoras)) {
                _log.warn(prefix(rid) + "getBusquedaDebitosReintegrosStatus: tercerizadora null/vacía -> retorno vacío (evito mezclar terceros)");
                return new ArrayList<DebitosaReintegros>();
            }
            final String terc = idTercerizadoras.trim();

            _log.info(prefix(rid) + "[IN] fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                    + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                    + " tercerizadoras=" + safe(terc));

            if (dbg) {
                _log.debug(prefix(rid) + "[SQL] " + sql);
                _log.debug(prefix(rid) + "[SQL-PARAMS] p1(fechaDesde)=" + (sqlDesde == null ? "NULL" : sqlDesde.toString())
                        + " p2(fechaHasta)=" + (sqlHasta == null ? "NULL" : sqlHasta.toString())
                        + " p3(idTercerizadoras)=" + safe(terc));
            }

            con = ConnectionHelper.getConnection();
            if (dbg) _log.debug(prefix(rid) + "[DB] gotConnection=" + (con != null));

            ps = con.prepareStatement(sql);
            if (sqlDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, sqlDesde);
            if (sqlHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, sqlHasta);

            ps.setString(3, terc);

            long tQ0 = System.currentTimeMillis();
            rs = ps.executeQuery();
            long tQ1 = System.currentTimeMillis();
            if (dbg) _log.debug(prefix(rid) + "[DB] executeQuery ok, elapsedMs=" + (tQ1 - tQ0));

            if (dbg) {
                try {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix(rid)).append("[RS-META] columns=").append(cols).append(" => ");
                    for (int i = 1; i <= cols; i++) {
                        if (i > 1) sb.append(" | ");
                        sb.append(i).append(":")
                                .append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(")");
                    }
                    _log.debug(sb.toString());
                } catch (Exception metaEx) {
                    _log.warn(prefix(rid) + "[RS-META] no se pudo leer metadata", metaEx);
                }
            }

            while (rs.next()) {
                rowCount++;

                DebitosaReintegros deb = new DebitosaReintegros();

                int rein = rs.getInt("id_reintegro");
                boolean reinWasNull = rs.wasNull();
                if (reinWasNull) {
                    reinNullCount++;
                    rein = 0;
                }
                if (!reinWasNull && rein > 0) {
                    distinctReintegro.add(Integer.valueOf(rein));
                } else if (!reinWasNull && rein <= 0) {
                    reinInvalidCount++;
                }
                deb.setNumReintegro(rein);

                String op = rs.getString("id_orden_pago");
                if (op == null) {
                    opNullCount++;
                } else {
                    op = op.trim();
                    if (op.length() == 0) {
                        op = null;
                        opNullCount++;
                    }
                }
                deb.setNumeroOP(op);
                if (op != null) distinctOp.add(op);

                deb.setSeccional(rs.getString("seccional"));
                deb.setDocumento(rs.getString("docu_numero"));
                deb.setDescripcion(rs.getString("descripcion_r"));
                deb.setApellido(rs.getString("apellido"));
                deb.setNombre(rs.getString("nombre"));

                BigDecimal monto = rs.getBigDecimal("monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setImporteTotal(monto);
                montoPrestadorSum = montoPrestadorSum.add(monto);

                try {
                    Timestamp ts = rs.getTimestamp("alta_fecha_op");
                    if (ts != null) deb.setFechaOP(new Date(ts.getTime()));
                } catch (Exception ignore) {}

                try {
                    int rp = rs.getInt("id_reclamo_prestacional");
                    if (!rs.wasNull()) deb.setReclamoPrestacional(rp);
                } catch (Exception ignore) {}

                boolean st = rs.getBoolean("status");
                boolean stWasNull = rs.wasNull();
                if (stWasNull) {
                    statusNullCount++;
                    deb.setStatus(null);
                } else {
                    deb.setStatus(Boolean.valueOf(st));
                    if (st) {
                        statusTrueCount++;
                        noCoincidePorReintegro.put(rein, noCoincidePorReintegro.getOrDefault(rein, 0) + 1);
                    } else {
                        statusFalseCount++;
                    }
                }

                out.add(deb);

                if (dbg && rowCount <= sampleRowsToLog) {
                    _log.debug(prefix(rid) + "[ROW#" + rowCount + "] "
                            + "rein=" + rein + (reinWasNull ? "(NULL->0)" : "")
                            + " op(bean)=" + safe(deb.getNumeroOP())
                            + " status=" + (stWasNull ? "NULL" : String.valueOf(st))
                            + " monto=" + bd(monto)
                            + " doc=" + safe(deb.getDocumento())
                            + " ape=" + safe(deb.getApellido()));
                }
            }

            // ---- CLAVE: NO PISAR TOTALES SI NO HAY FILAS ----
            if (debitosaTotal != null) {
                if (rowCount > 0) {
                    debitosaTotal.setMontoReintegros(montoPrestadorSum);
                    debitosaTotal.setMontoReintegroDebito(montoPrestadorSum);
                } else {
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-SKIP] rows=0 -> no piso debitosaTotal (mantengo totales previos para fallback)");
                }
            }

            _log.info(prefix(rid) + "[OUT] rows=" + rowCount
                    + " distinctReintegro=" + distinctReintegro.size()
                    + " distinctOp=" + distinctOp.size()
                    + " statusTrue(NO COINCIDE)=" + statusTrueCount
                    + " statusFalse(OK)=" + statusFalseCount
                    + " statusNull=" + statusNullCount
                    + " reinNull=" + reinNullCount
                    + " reinInvalid(<=0)=" + reinInvalidCount
                    + " opNull/blank=" + opNullCount
                    + " montoPrestadorSum=" + bd(montoPrestadorSum));

            if (dbg) {
                logTopNoCoincidePorLiq(rid, noCoincidePorReintegro, 20);
                logSampleListRE(rid, out, 10);
            }

            return out;

        } catch (Exception e) {
            _log.error(prefix(rid) + "Error en getBusquedaDebitosReintegrosStatus", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosLiquidacionesPendientes> getBusquedaDebitosLiquidacionesStatus(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosLiquidacionesPendientes> out = new ArrayList<DebitosLiquidacionesPendientes>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final boolean dbg = _log.isDebugEnabled();
        final int sampleRowsToLog = 50;
        final String rid = "LI-STATUS#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final java.sql.Date sqlDesde = (fechaDesde == null) ? null : new java.sql.Date(fechaDesde.getTime());
        final java.sql.Date sqlHasta = (fechaHasta == null) ? null : new java.sql.Date(fechaHasta.getTime());

        int rowCount = 0;
        int statusTrueCount = 0;
        int statusFalseCount = 0;
        int statusNullCount = 0;
        int liqNullCount = 0;

        Set<Integer> distinctLiq = new HashSet<Integer>();
        Map<Integer, Integer> noCoincidePorLiq = new HashMap<Integer, Integer>();

        final String sql = "SELECT * FROM public.reporte_debito_liq_pendientes_con_status(?::date, ?::date, ?::varchar)";

        try {
            // ---- Guardas básicas (fail-closed) ----
            if (fechaDesde == null || fechaHasta == null) {
                _log.warn(prefix(rid) + "getBusquedaDebitosLiquidacionesStatus: fechas invalidas desde="
                        + fmt(sdf, fechaDesde) + " hasta=" + fmt(sdf, fechaHasta) + " -> retorno vacío");
                return new ArrayList<DebitosLiquidacionesPendientes>();
            }

            if (Validator.isNull(idTercerizadoras)) {
                _log.warn(prefix(rid) + "getBusquedaDebitosLiquidacionesStatus: tercerizadora null/vacía -> retorno vacío (evito mezclar terceros)");
                return new ArrayList<DebitosLiquidacionesPendientes>();
            }
            final String terc = idTercerizadoras.trim();

            _log.info(prefix(rid) + "[IN] fechaDesde=" + fmt(sdf, fechaDesde) + " (" + ms(fechaDesde) + ")"
                    + " fechaHasta=" + fmt(sdf, fechaHasta) + " (" + ms(fechaHasta) + ")"
                    + " tercerizadoras=" + safe(terc));

            if (dbg) {
                _log.debug(prefix(rid) + "[SQL] " + sql);
                _log.debug(prefix(rid) + "[SQL-PARAMS] p1(fechaDesde)=" + (sqlDesde == null ? "NULL" : sqlDesde.toString())
                        + " p2(fechaHasta)=" + (sqlHasta == null ? "NULL" : sqlHasta.toString())
                        + " p3(idTercerizadoras)=" + safe(terc));
            }

            con = ConnectionHelper.getConnection();
            if (dbg) _log.debug(prefix(rid) + "[DB] gotConnection=" + (con != null));

            ps = con.prepareStatement(sql);
            if (sqlDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, sqlDesde);
            if (sqlHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, sqlHasta);

            ps.setString(3, terc);

            long tQ0 = System.currentTimeMillis();
            rs = ps.executeQuery();
            long tQ1 = System.currentTimeMillis();
            if (dbg) _log.debug(prefix(rid) + "[DB] executeQuery ok, elapsedMs=" + (tQ1 - tQ0));

            if (dbg) {
                try {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix(rid)).append("[RS-META] columns=").append(cols).append(" => ");
                    for (int i = 1; i <= cols; i++) {
                        if (i > 1) sb.append(" | ");
                        sb.append(i).append(":")
                                .append(md.getColumnLabel(i)).append("(").append(md.getColumnTypeName(i)).append(")");
                    }
                    _log.debug(sb.toString());
                } catch (Exception metaEx) {
                    _log.warn(prefix(rid) + "[RS-META] no se pudo leer metadata", metaEx);
                }
            }

            while (rs.next()) {
                rowCount++;

                DebitosLiquidacionesPendientes deb = new DebitosLiquidacionesPendientes();

                Integer liqObj = null;
                try {
                    liqObj = (Integer) rs.getObject("v_id_liquidacion"); // NULL-safe real
                } catch (Exception ignore) {
                }

                boolean liqWasNull = (liqObj == null);
                int liq = 0;

                if (liqWasNull) {
                    liqNullCount++;
                } else {
                    liq = liqObj.intValue();
                    distinctLiq.add(Integer.valueOf(liq));

                    // >>> CLAVE: poblar numero para que las keys funcionen
                    deb.setNumero(BigDecimal.valueOf((long) liq));
                }

                deb.setHospitalesAutogestion(rs.getString("v_nombre_prestador"));
                deb.setFactura(rs.getString("v_numero_factura"));

                BigDecimal monto = rs.getBigDecimal("v_monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setMonto(monto);
                montoPrestadorSum = montoPrestadorSum.add(monto);

                try { deb.setCargoPrestadora(rs.getBigDecimal("v_cargo_ospim")); } catch (Exception ignore) {}
                try { deb.setCargoPrestadoraReclamo(rs.getBigDecimal("v_cargo_reclamo")); } catch (Exception ignore) {}

                boolean st = rs.getBoolean("status");
                boolean stWasNull = rs.wasNull();
                if (stWasNull) {
                    statusNullCount++;
                    deb.setStatus(null);
                } else {
                    deb.setStatus(Boolean.valueOf(st));
                    if (st) {
                        statusTrueCount++;
                        noCoincidePorLiq.put(liq, noCoincidePorLiq.getOrDefault(liq, 0) + 1);
                    } else {
                        statusFalseCount++;
                    }
                }

                out.add(deb);

                if (dbg && rowCount <= sampleRowsToLog) {
                    _log.debug(prefix(rid) + "[ROW#" + rowCount + "] "
                            + "liq=" + liq + (liqWasNull ? "(NULL->0)" : "")
                            + " status=" + (stWasNull ? "NULL" : String.valueOf(st))
                            + " monto=" + bd(monto)
                            + " prestador=" + safe(deb.getHospitalesAutogestion())
                            + " factura=" + safe(deb.getFactura())
                            + " numero=" + (deb.getNumero() != null ? deb.getNumero().toPlainString() : "NULL"));
                }
            }

            // ---- CLAVE: NO PISAR TOTALES SI NO HAY FILAS ----
            if (debitosaTotal != null) {
                if (rowCount > 0) {
                    debitosaTotal.setMontoLiquidacionPendiente(montoPrestadorSum);
                    debitosaTotal.setMontoLiquidacionPendienteDebito(montoPrestadorSum);
                } else {
                    if (dbg) _log.debug(prefix(rid) + "[TOTALS-SKIP] rows=0 -> no piso debitosaTotal (mantengo totales previos para fallback)");
                }
            }

            _log.info(prefix(rid) + "[OUT] rows=" + rowCount
                    + " distinctLiq=" + distinctLiq.size()
                    + " statusTrue(NO COINCIDE)=" + statusTrueCount
                    + " statusFalse(OK)=" + statusFalseCount
                    + " statusNull=" + statusNullCount
                    + " liqNull=" + liqNullCount
                    + " montoPrestadorSum=" + bd(montoPrestadorSum));

            if (dbg) {
                logTopNoCoincidePorLiq(rid, noCoincidePorLiq, 20);
                logSampleListLI(rid, out, 10);
            }

            return out;

        } catch (Exception e) {
            _log.error(prefix(rid) + "Error en getBusquedaDebitosLiquidacionesStatus", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosaReintegros> getBusquedaDebitosReintegrosStatusBorrador(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras,
            String workKey
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosaReintegros> out = new ArrayList<DebitosaReintegros>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final String terc = (idTercerizadoras == null || idTercerizadoras.trim().isEmpty())
                ? null
                : idTercerizadoras.trim();

        final String wk = (workKey == null || workKey.trim().isEmpty())
                ? null
                : workKey.trim();

        // Fail-safe: sin workKey no leemos borrador
        if (wk == null) {
            _log.warn("getBusquedaDebitosReintegrosStatusBorrador: workKey vacío/null -> retorno vacío (fail-safe). terc=" + terc);
            if (debitosaTotal != null) {
                debitosaTotal.setMontoReintegros(BigDecimal.ZERO);
                debitosaTotal.setMontoReintegroDebito(BigDecimal.ZERO);
            }
            return out;
        }

        final String sql =
                "SELECT * FROM public.reporte_debito_reintegros_borrador_status(" +
                        "?::date, ?::date, ?::varchar, ?::text)";

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            if (fechaDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));

            if (fechaHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));

            if (terc == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, terc);

            ps.setString(4, wk);

            rs = ps.executeQuery();

            while (rs.next()) {
                DebitosaReintegros deb = new DebitosaReintegros();

                int rein = rs.getInt("id_reintegro");
                if (rs.wasNull()) rein = 0;
                deb.setNumReintegro(rein);

                deb.setNumeroOP(rs.getString("id_orden_pago"));
                deb.setSeccional(rs.getString("seccional"));
                deb.setDocumento(rs.getString("docu_numero"));
                deb.setDescripcion(rs.getString("descripcion_r"));
                deb.setApellido(rs.getString("apellido"));
                deb.setNombre(rs.getString("nombre"));

                BigDecimal monto = rs.getBigDecimal("monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setImporteTotal(monto);
                montoPrestadorSum = montoPrestadorSum.add(monto);

                Timestamp ts = rs.getTimestamp("alta_fecha_op");
                if (ts != null) deb.setFechaOP(new Date(ts.getTime()));

                int rp = rs.getInt("id_reclamo_prestacional");
                if (!rs.wasNull()) deb.setReclamoPrestacional(rp);

                boolean st = rs.getBoolean("status");
                if (rs.wasNull()) deb.setStatus(null);
                else deb.setStatus(Boolean.valueOf(st));

                out.add(deb);
            }

            if (debitosaTotal != null) {
                debitosaTotal.setMontoReintegros(montoPrestadorSum);
                debitosaTotal.setMontoReintegroDebito(montoPrestadorSum);
            }

            return out;

        } catch (Exception e) {
            _log.error("Error en getBusquedaDebitosReintegrosStatusBorrador", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosHospitales> getBusquedaDebitosHospitalesStatusBorrador(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras,
            String workKey
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        final String wk = (workKey == null || workKey.trim().isEmpty())
                ? null
                : workKey.trim();

        // BORRADOR: tercerizadora obligatoria (evita mezclar terceros)
        if (idTercerizadoras == null || idTercerizadoras.trim().isEmpty()) {
            _log.warn("getBusquedaDebitosHospitalesStatusBorrador: tercerizadora null/vacía -> retorno vacío");
            return new ArrayList<DebitosHospitales>();
        }

        // BORRADOR: workKey obligatorio
        if (wk == null) {
            _log.warn("getBusquedaDebitosHospitalesStatusBorrador: workKey null/vacío -> retorno vacío");
            return new ArrayList<DebitosHospitales>();
        }

        List<DebitosHospitales> out = new ArrayList<DebitosHospitales>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final String terc = idTercerizadoras.trim();

        final String sql =
                "SELECT * FROM public.reporte_debito_hospitales_borrador_status(?::date, ?::date, ?::varchar, ?::text)";

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            if (fechaDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));

            if (fechaHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));

            ps.setString(3, terc);
            ps.setString(4, wk);

            rs = ps.executeQuery();

            while (rs.next()) {
                DebitosHospitales debito = new DebitosHospitales();

                String op = rs.getString("v_orden_pago");
                debito.setOrdenPago(op != null ? op.trim() : null);

                debito.setHospital(rs.getString("v_nombre_prestador"));
                debito.setFactura(rs.getString("v_numero_factura"));

                BigDecimal monto = rs.getBigDecimal("v_monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                debito.setMonto(monto);
                montoPrestadorSum = montoPrestadorSum.add(monto);

                int idLiq = rs.getInt("v_id_liquidacion");
                if (rs.wasNull()) idLiq = 0;
                debito.setIdLiquidacion(idLiq);

                debito.setCargoPrestadora(rs.getBigDecimal("v_cargo_ospim"));
                debito.setImporteTotal(rs.getBigDecimal("v_importe"));

                boolean st = rs.getBoolean("status");
                if (rs.wasNull()) debito.setStatus(null);
                else debito.setStatus(Boolean.valueOf(st));

                out.add(debito);
            }

            if (debitosaTotal != null) {
                debitosaTotal.setMontoHospitales(montoPrestadorSum);
                debitosaTotal.setMontoHospitaleDebito(montoPrestadorSum);
            }

            return out;

        } catch (Exception e) {
            _log.error("Error en getBusquedaDebitosHospitalesStatusBorrador", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosLiquidacionesPendientes> getBusquedaDebitosLiquidacionesStatusBorrador(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras,
            String workKey
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosLiquidacionesPendientes> out = new ArrayList<DebitosLiquidacionesPendientes>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final String terc = (idTercerizadoras == null || idTercerizadoras.trim().isEmpty())
                ? null
                : idTercerizadoras.trim();

        final String wk = (workKey == null || workKey.trim().isEmpty())
                ? null
                : workKey.trim();

        // Fail-safe: si estoy en BORRADOR y no tengo workKey, no devuelvo nada (evita fugas/mezclas)
        if (wk == null) {
            _log.warn("getBusquedaDebitosLiquidacionesStatusBorrador: workKey vacío/null -> retorno vacío (fail-safe). terc=" + terc);
            if (debitosaTotal != null) {
                debitosaTotal.setMontoLiquidacionPendiente(BigDecimal.ZERO);
                debitosaTotal.setMontoLiquidacionPendienteDebito(BigDecimal.ZERO);
            }
            return out;
        }

        final String sql =
                "SELECT * FROM public.reporte_debito_liq_pendientes_borrador_status(" +
                        "?::date, ?::date, ?::varchar, ?::text)";

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            if (fechaDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));

            if (fechaHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));

            if (terc == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, terc);

            // NUEVO param 4
            ps.setString(4, wk);

            rs = ps.executeQuery();

            while (rs.next()) {
                DebitosLiquidacionesPendientes deb = new DebitosLiquidacionesPendientes();

                deb.setHospitalesAutogestion(rs.getString("v_nombre_prestador"));
                deb.setFactura(rs.getString("v_numero_factura"));

                BigDecimal monto = rs.getBigDecimal("v_monto_prestador");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setMonto(monto);
                montoPrestadorSum = montoPrestadorSum.add(monto);

                try { deb.setCargoPrestadora(rs.getBigDecimal("v_cargo_ospim")); } catch (Exception ignore) {}
                try { deb.setCargoPrestadoraReclamo(rs.getBigDecimal("v_cargo_reclamo")); } catch (Exception ignore) {}

                boolean st = rs.getBoolean("status");
                if (rs.wasNull()) deb.setStatus(null);
                else deb.setStatus(Boolean.valueOf(st));

                out.add(deb);
            }

            if (debitosaTotal != null) {
                debitosaTotal.setMontoLiquidacionPendiente(montoPrestadorSum);
                debitosaTotal.setMontoLiquidacionPendienteDebito(montoPrestadorSum);
            }

            return out;

        } catch (Exception e) {
            _log.error("Error en getBusquedaDebitosLiquidacionesStatusBorrador", e);
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<DebitosaPrestadores> getBusquedaDebitosPrestadoresStatusBorrador(
            Date fechaDesde,
            Date fechaHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras,
            String workKey
    ) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<DebitosaPrestadores> out = new ArrayList<DebitosaPrestadores>();
        BigDecimal montoPrestadorSum = BigDecimal.ZERO;

        final String terc = (idTercerizadoras == null || idTercerizadoras.trim().isEmpty())
                ? null
                : idTercerizadoras.trim();

        final String wk = (workKey == null || workKey.trim().isEmpty())
                ? null
                : workKey.trim();

        // Fail-safe: sin workKey no existe BORRADOR coherente
        if (wk == null) {
            _log.warn("getBusquedaDebitosPrestadoresStatusBorrador: workKey vacío/null -> retorno vacío. terc=" + terc);
            if (debitosaTotal != null) {
                debitosaTotal.setMontoPrestadores(BigDecimal.ZERO);
                debitosaTotal.setMontoPrestadoreDebito(BigDecimal.ZERO);
            }
            return out;
        }

        final String sql =
                "SELECT * FROM public.reporte_debito_prestadores_borrador_status(" +
                        "?::date, ?::date, ?::varchar, ?::text)";

        try {
            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            // 1) fecha_ini
            if (fechaDesde == null) ps.setNull(1, Types.DATE);
            else ps.setDate(1, new java.sql.Date(fechaDesde.getTime()));

            // 2) fecha_fin
            if (fechaHasta == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, new java.sql.Date(fechaHasta.getTime()));

            // 3) id_tercerizadora
            if (terc == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, terc);

            // 4) work_key
            ps.setString(4, wk);

            rs = ps.executeQuery();

            while (rs.next()) {
                DebitosaPrestadores deb = new DebitosaPrestadores();

                // IDs (PR requiere ID)
                BigDecimal regId = rs.getBigDecimal("registro_id"); // puede venir null si tu data histórica lo tiene así
                int regIdInt = rs.getInt("registro_id_int");        // NOT NULL en tabla; igual leo seguro

                // Si registro_id viene null, fallback al int (pero sin inventar 0)
                if (regId == null) {
                    if (regIdInt <= 0) {
                        _log.warn("PR BORRADOR: registro_id y registro_id_int inválidos. " +
                                "registro_id=null registro_id_int=" + regIdInt +
                                " terc=" + terc + " wk=" + wk +
                                " factura=" + rs.getString("num_comprobante") +
                                " op=" + rs.getString("id_orden_pago") +
                                " prestador=" + rs.getString("prestador"));
                        // decisión: skip (recomendado) para no propagar basura
                        continue;
                    }
                    regId = BigDecimal.valueOf(regIdInt);
                } else {
                    // si viene, validás coherencia básica con el int si te interesa:
                    // (opcional) si regId no entra en int exacto, al menos no rompas acá
                }

                // Mantengo tu contrato actual:
                // - numero = ID numérico que usa el inserter/guard
                // - idLiquidacion = el int "legacy" que venís usando
                deb.setNumero(regId);
                deb.setIdLiquidacion(regIdInt);

                deb.setPrestador(rs.getString("prestador"));
                deb.setFactura(rs.getString("num_comprobante"));
                deb.setOrdenPago(rs.getString("id_orden_pago"));

                BigDecimal cargoPrestadora = rs.getBigDecimal("cargo_prestadora");
                if (cargoPrestadora == null) cargoPrestadora = BigDecimal.ZERO;
                deb.setCargoPrestadora(cargoPrestadora);

                BigDecimal monto = rs.getBigDecimal("monto");
                if (monto == null) monto = BigDecimal.ZERO;
                deb.setMonto(monto);

                // legacy: acumulabas cargo_prestadora
                montoPrestadorSum = montoPrestadorSum.add(cargoPrestadora);

                Integer rp = null;
                int rpRaw = rs.getInt("id_reclamo_prestacional");
                if (!rs.wasNull()) rp = rpRaw;
                deb.setReclamoPrestacional(rp);

                // status: hoy siempre false desde SQL
                deb.setStatus(Boolean.FALSE);

                out.add(deb);
            }

            if (debitosaTotal != null) {
                debitosaTotal.setMontoPrestadores(montoPrestadorSum);
                debitosaTotal.setMontoPrestadoreDebito(montoPrestadorSum);
            }

            return out;

        } catch (Exception e) {
            _log.error("Error en getBusquedaDebitosPrestadoresStatusBorrador", e);
            if (debitosaTotal != null) {
                debitosaTotal.setMontoPrestadores(montoPrestadorSum);
                debitosaTotal.setMontoPrestadoreDebito(montoPrestadorSum);
            }
            return out;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    // ============================================================================
    // Helpers de log (nombres distintos para evitar type-erasure con List<?>)
    // ============================================================================
    private void logSampleListPR(String rid, List<DebitosaPrestadores> list, int sampleN) {
        if (list == null) {
            _log.debug(prefix(rid) + "[LIST-PR] null");
            return;
        }
        _log.debug(prefix(rid) + "[LIST-PR] size=" + list.size() + " sampleN=" + sampleN);

        int n = Math.min(sampleN, list.size());
        for (int i = 0; i < n; i++) {
            DebitosaPrestadores d = list.get(i);
            _log.debug(prefix(rid) + "[LIST#" + (i + 1) + "][PR] "
                    + "rein=" + d.getIdLiquidacion()
                    + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                    + " cargoPrestadora=" + bd(d.getCargoPrestadora())
                    + " monto=" + bd(d.getMonto())
                    + " op=" + safe(d.getOrdenPago())
                    + " prestador=" + safe(d.getPrestador())
                    + " factura=" + safe(d.getFactura()));
        }
    }

    private void logSampleListRE(String rid, List<DebitosaReintegros> list, int sampleN) {
        if (list == null) {
            _log.debug(prefix(rid) + "[LIST-RE] null");
            return;
        }
        _log.debug(prefix(rid) + "[LIST-RE] size=" + list.size() + " sampleN=" + sampleN);

        int n = Math.min(sampleN, list.size());
        for (int i = 0; i < n; i++) {
            DebitosaReintegros d = list.get(i);
            _log.debug(prefix(rid) + "[LIST#" + (i + 1) + "][RE] "
                    + "rein=" + d.getNumReintegro()
                    + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                    + " monto=" + bd(d.getImporteTotal())
                    + " op=" + safe(d.getNumeroOP())
                    + " doc=" + safe(d.getDocumento())
                    + " ape=" + safe(d.getApellido())
                    + " nom=" + safe(d.getNombre()));
        }
    }

    private void logSampleListLI(String rid, List<DebitosLiquidacionesPendientes> list, int sampleN) {
        if (list == null) {
            _log.debug(prefix(rid) + "[LIST-LI] null");
            return;
        }
        _log.debug(prefix(rid) + "[LIST-LI] size=" + list.size() + " sampleN=" + sampleN);

        int n = Math.min(sampleN, list.size());
        for (int i = 0; i < n; i++) {
            DebitosLiquidacionesPendientes d = list.get(i);
            _log.debug(prefix(rid) + "[LIST#" + (i + 1) + "][LI] "
                    + "liq=" + bd(d.getNumero())
                    + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                    + " monto=" + bd(d.getMonto())
                    + " prestador=" + safe(d.getHospitalesAutogestion())
                    + " factura=" + safe(d.getFactura())
                    + " cargoPrestadora=" + bd(d.getCargoPrestadora())
                    + " cargoReclamo=" + bd(d.getCargoPrestadoraReclamo()));
        }
    }

    /* ===================== helpers de debug ===================== */

    private String prefix(String rid) {
        return "[" + rid + "] ";
    }

    private String safe(String s) {
        if (s == null) return "NULL";
        String t = s.trim();
        if (t.length() > 200) t = t.substring(0, 200) + "...";
        return "\"" + t.replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }

    private String fmt(SimpleDateFormat sdf, Date d) {
        return (d == null) ? "NULL" : sdf.format(d);
    }

    private String ms(Date d) {
        return (d == null) ? "NULL" : String.valueOf(d.getTime());
    }

    private String bd(BigDecimal b) {
        return (b == null) ? "NULL" : b.toPlainString();
    }

    private void logTopNoCoincidePorLiq(String rid, Map<Integer, Integer> map, int topN) {
        if (map == null || map.isEmpty()) {
            _log.debug(prefix(rid) + "[NO-COINCIDE] none");
            return;
        }

        // Java 5: sin diamond
        List<Map.Entry<Integer, Integer>> entries =
                new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());

        // Java 5: sin lambda, sin List.sort
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {
                Integer av = (a != null ? a.getValue() : null);
                Integer bv = (b != null ? b.getValue() : null);

                // nulls last
                if (av == null && bv == null) return 0;
                if (av == null) return 1;
                if (bv == null) return -1;

                // DESC por value
                if (bv.intValue() > av.intValue()) return 1;
                if (bv.intValue() < av.intValue()) return -1;
                return 0;
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append(prefix(rid)).append("[NO-COINCIDE] top ").append(topN).append(" liqId => count: ");

        int n = (topN < entries.size()) ? topN : entries.size();
        for (int i = 0; i < n; i++) {
            Map.Entry<Integer, Integer> e = entries.get(i);
            if (i > 0) sb.append(", ");
            sb.append(e.getKey()).append("=>").append(e.getValue());
        }

        _log.debug(sb.toString());
    }

    // Entry-point único: sirve para HO/LI/RE/PR sin cambiar llamadas.
    private void logSampleList(String rid, List<?> list, int sampleN) {
        if (list == null) {
            _log.debug(prefix(rid) + "[LIST] null");
            return;
        }
        _log.debug(prefix(rid) + "[LIST] size=" + list.size() + " sampleN=" + sampleN);

        int n = Math.min(sampleN, list.size());
        for (int i = 0; i < n; i++) {
            Object o = list.get(i);

            if (o instanceof DebitosHospitales) {
                logOneHO(rid, i + 1, (DebitosHospitales) o);

            } else if (o instanceof DebitosLiquidacionesPendientes) {
                logOneLI(rid, i + 1, (DebitosLiquidacionesPendientes) o);

            } else if (o instanceof DebitosaReintegros) {
                logOneRE(rid, i + 1, (DebitosaReintegros) o);

            } else if (o instanceof DebitosaPrestadores) {
                logOnePR(rid, i + 1, (DebitosaPrestadores) o);

            } else {
                // fallback: no te quedes sin logs por un tipo nuevo
                _log.debug(prefix(rid) + "[LIST#" + (i + 1) + "] type="
                        + (o == null ? "null" : o.getClass().getName())
                        + " value=" + String.valueOf(o));
            }
        }
    }

    private void logOneHO(String rid, int idx, DebitosHospitales d) {
        _log.debug(prefix(rid) + "[LIST#" + idx + "][HO] "
                + "opId=" + bd(d.getNumero())
                + " liq=" + d.getIdLiquidacion()
                + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                + " monto=" + bd(d.getMonto())
                + " hospital=" + safe(d.getHospital())
                + " factura=" + safe(d.getFactura())
                + " cargoPrestadora=" + bd(d.getCargoPrestadora())
                + " importeTotal=" + bd(d.getImporteTotal()));
    }

    private void logOneLI(String rid, int idx, DebitosLiquidacionesPendientes d) {
        _log.debug(prefix(rid) + "[LIST#" + idx + "][LI] "
                + "liq=" + bd(d.getNumero()) // en LI suele ser BigDecimal liquidacion_id
                + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                + " monto=" + bd(d.getMonto())
                + " prestador=" + safe(d.getHospitalesAutogestion())
                + " factura=" + safe(d.getFactura())
                + " cargoPrestadora=" + bd(d.getCargoPrestadora())
                + " cargoReclamo=" + bd(d.getCargoPrestadoraReclamo()));
    }

    private void logOneRE(String rid, int idx, DebitosaReintegros d) {
        _log.debug(prefix(rid) + "[LIST#" + idx + "][RE] "
                + "reintegro=" + d.getNumReintegro()
                + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                + " monto=" + bd(d.getImporteTotal()) // en tu código actual usás importeTotal=monto_prestador
                + " op=" + safe(d.getNumeroOP())
                + " doc=" + safe(d.getDocumento())
                + " seccional=" + safe(d.getSeccional())
                + " ape=" + safe(d.getApellido())
                + " nom=" + safe(d.getNombre())
                + " reclamo=" + d.getReclamoPrestacional());
    }

    private void logOnePR(String rid, int idx, DebitosaPrestadores d) {
        _log.debug(prefix(rid) + "[LIST#" + idx + "][PR] "
                + "numero=" + bd(d.getNumero())
                + " liq=" + d.getIdLiquidacion()
                + " status=" + (d.getStatus() == null ? "NULL" : d.getStatus().toString())
                + " monto=" + bd(d.getMonto())
                + " prestador=" + safe(d.getPrestador())
                + " factura=" + safe(d.getFactura())
                + " op=" + safe(d.getOrdenPago())
                + " cargoPrestadora=" + bd(d.getCargoPrestadora())
                + " reclamo=" + d.getReclamoPrestacional());
    }

    public List<DebitosaReintegros> getBusquedaDebitosReintegros(Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {

        Connection con = null;
        CallableStatement stmt = null;
        ArrayList<DebitosaReintegros> listaReintegros = null;
        listaReintegros = new ArrayList<DebitosaReintegros>();
        BigDecimal montoTotal = new BigDecimal(0);
        BigDecimal montoPrestador = new BigDecimal(0);
        try {

            String sql = "{call public.reporte_debito_reintegros(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());

            stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
            stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
            stmt.setString(3, idTercerizadoras);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DebitosaReintegros reintegro = new DebitosaReintegros();

                reintegro.setDescripcion(rs.getString("descripcion_r"));
                reintegro.setDocumento(rs.getString("docu_numero"));
                reintegro.setFechaOP(rs.getDate("alta_fecha_op"));
                //reintegro.setImporteTotal(rs.getBigDecimal("importe"));
                reintegro.setSeccional(rs.getString("seccional"));
                reintegro.setNumeroOP(rs.getString("id_orden_pago"));
                //reintegro.setFechaOP(rs.getDate("alta_fecha_op"));
                //reintegro.setCargoPrestadora(rs.getBigDecimal("monto_prestador"));

                //acumulador

                reintegro.setApellido(rs.getString("apellido"));
                reintegro.setNombre(rs.getString("nombre"));
                reintegro.setNumReintegro(rs.getInt("id_reintegro"));
                montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_prestador") != null ? rs.getBigDecimal("monto_prestador") : new BigDecimal("0"));

                montoTotal = montoTotal.add(rs.getBigDecimal("monto_prestador"));
                reintegro.setImporteTotal(rs.getBigDecimal("monto_prestador"));

                Integer reclamo = 0;
                try {
                    reclamo = rs.getInt("id_reclamo_prestacional");
                } catch (Exception e) {
                }

                reintegro.setReclamoPrestacional(reclamo);
                listaReintegros.add(reintegro);

            }

            debitosaTotal.setMontoReintegros(montoPrestador);
            debitosaTotal.setMontoReintegroDebito(montoPrestador);
        } catch (Exception e) {
            _log.error(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return listaReintegros;

    }

    public List<DebitosaPrestadores> getBusquedaDebitosPrestadores(
            Date periodoDesde,
            Date periodoHasta,
            DebitosaTotal debitosaTotal,
            String idTercerizadoras
    ) {

        Connection con = null;
        CallableStatement stmt = null;
        ArrayList<DebitosaPrestadores> listaPrestadores = null;
        listaPrestadores = new ArrayList<DebitosaPrestadores>();
        BigDecimal montoTotal = new BigDecimal(0);
        BigDecimal montoPrestador = new BigDecimal(0);

        int rows = 0;
        int added = 0;
        int dup = 0;
        int nullId = 0;

        try {
            String sql = "{call public.reporte_debito_prestadores(?,?,?)}";

            _log.info("[PR][BUSQ][START] terc=" + idTercerizadoras
                    + " desde=" + (periodoDesde != null ? new java.sql.Date(periodoDesde.getTime()) : "null")
                    + " hasta=" + (periodoHasta != null ? new java.sql.Date(periodoHasta.getTime()) : "null"));

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());

            if (null != periodoDesde) {
                stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            if (null != periodoHasta) {
                stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setString(3, idTercerizadoras);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows++;

                DebitosaPrestadores prestador = new DebitosaPrestadores();

                prestador.setPrestador(rs.getString("prestador"));
                prestador.setFactura(rs.getString("num_comprobante"));
                //prestadore.setMonto(rs.getBigDecimal("monto"));
                prestador.setOrdenPago(rs.getString("id_orden_pago"));
                prestador.setCargoPrestadora(rs.getBigDecimal("cargo_prestadora"));
                prestador.setMonto(rs.getBigDecimal("cargo_prestadora"));
                montoTotal = montoTotal.add(rs.getBigDecimal("cargo_prestadora"));

                // ID que siempre "se vio bien" en pantalla:
                int rein = rs.getInt("reintegro");
                boolean reinWasNull = rs.wasNull();
                prestador.setIdLiquidacion(rein);

                // GARANTÍA: también seteo el ID que usan los persistidores (numero) a partir del mismo reintegro.
                // Sin inventar: mismo valor que ya estabas mostrando.
                if (!reinWasNull && rein > 0) {
                    prestador.setNumero(BigDecimal.valueOf(rein));
                } else {
                    prestador.setNumero(null);
                    nullId++;
                    _log.warn("[PR][BUSQ][ROW] reintegro NULL/<=0 -> numero queda null. reintegro="
                            + rein + " prestador=" + rs.getString("prestador")
                            + " factura=" + rs.getString("num_comprobante")
                            + " op=" + rs.getString("id_orden_pago"));
                }

                try {
                    prestador.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
                } catch (Exception e) {
                    // se mantiene comportamiento existente
                }

                if (!this.existeElemento(listaPrestadores, prestador)) {
                    listaPrestadores.add(prestador);
                    added++;
                    montoPrestador = montoPrestador.add(
                            rs.getBigDecimal("cargo_prestadora") != null
                                    ? rs.getBigDecimal("cargo_prestadora")
                                    : new BigDecimal("0")
                    );
                } else {
                    dup++;
                }

                if (_log.isDebugEnabled() && rows <= 10) {
                    _log.debug("[PR][BUSQ][ROW#" + rows + "] reintegro=" + rein
                            + " numero=" + (prestador.getNumero() != null ? prestador.getNumero().toPlainString() : "null")
                            + " idLiquidacion=" + prestador.getIdLiquidacion()
                            + " prestador=" + prestador.getPrestador()
                            + " factura=" + prestador.getFactura()
                            + " op=" + prestador.getOrdenPago()
                            + " reclamo=" + prestador.getReclamoPrestacional());
                }

                //ordenPagoAnterior = prestadore.getOrdenPago();
            }

            debitosaTotal.setMontoPrestadores(montoPrestador);
            debitosaTotal.setMontoPrestadoreDebito(montoPrestador);

            _log.info("[PR][BUSQ][END] rows=" + rows + " added=" + added + " dup=" + dup + " nullId=" + nullId
                    + " terc=" + idTercerizadoras
                    + " montoPrestador=" + montoPrestador);

        } catch (Exception e) {
            _log.error(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return listaPrestadores;
    }


    private boolean existeElemento(ArrayList<DebitosaPrestadores> listaPrestadores, DebitosaPrestadores prestador) {
        boolean found = false;
        String rp = "";
        if (prestador.getReclamosPrestacionales() != null && prestador.getReclamosPrestacionales().length() > 0) {
            rp = prestador.getReclamosPrestacionales();
        } else {
            rp = prestador.getReclamoPrestacional() == null || prestador.getReclamoPrestacional() == 0 ? "" : prestador.getReclamoPrestacional().toString();
        }
        for (DebitosaPrestadores pres : listaPrestadores) {
            if (pres.getIdLiquidacion() == prestador.getIdLiquidacion()) {
                found = true;
                if (pres.getReclamosPrestacionales() == null || !pres.getReclamosPrestacionales().contains(rp)) {
                    pres.setReclamosPrestacionales(pres.getReclamosPrestacionales() + ";" + rp);
                }
                //also do something
                break;
            }
        }
        if (!found) prestador.setReclamosPrestacionales(rp);
        return found;
    }

    private boolean existeElemento(List<DebitosHospitales> lista, DebitosHospitales debito) {
        if (lista == null || debito == null) return false;

        final Integer idX = debito.getIdLiquidacion();
        final String facX = debito.getFactura();
        final String opX  = debito.getOrdenPago();
        final String hospX = debito.getHospital();

        for (DebitosHospitales deb : lista) {
            if (deb == null) continue;

            final Integer idIt = deb.getIdLiquidacion();

            if (eqInt(idIt, idX)
                    && eq(deb.getFactura(), facX)
                    && eq(deb.getOrdenPago(), opX)
                    && eq(deb.getHospital(), hospX)) {
                return true;
            }
        }
        return false;
    }

    private boolean eqInt(Integer a, Integer b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private boolean eq(String a, String b) {
        return (a == null ? "" : a).equals(b == null ? "" : b);
    }

    public DebitosaTotal getBuscarTotalesDebitos(Date fecha, String idTercerizadora) {

        Connection con = null;
        CallableStatement stmt = null;
        DebitosaTotal deb = null;

        try {
            String sql = "{call buscar_totales_debitos(?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());

            if (null != fecha) {
                stmt.setDate(1, new java.sql.Date(fecha.getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setString(2, idTercerizadora);


            ResultSet rs = stmt.executeQuery();
            deb = new DebitosaTotal();
            deb.setExisteDebito(false);
            while (rs.next()) {

                deb.setExisteDebito(true);
                deb.setMontoHospitales(rs.getBigDecimal("v_monto_hospital"));
                deb.setMontoHospitaleDebito(rs.getBigDecimal("v_monto_hospital_debito"));

                deb.setMontoLiquidacionPendiente(rs.getBigDecimal("v_monto_autogestion"));
                deb.setMontoLiquidacionPendienteDebito(rs.getBigDecimal("v_monto_autogestion_devito"));

                deb.setMontoPrestadores(rs.getBigDecimal("v_monto_prestador"));
                deb.setMontoPrestadoreDebito(rs.getBigDecimal("v_monto_prestador_debito"));

                deb.setMontoReintegros(rs.getBigDecimal("v_monto_reintegro"));
                deb.setMontoReintegroDebito(rs.getBigDecimal("v_monto_reintegro_debito"));


            }
        } catch (Exception e) {
            _log.error(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return deb;
    }


    public int grabarTotalesDebitos(DebitosaTotal deb, String user, Date fecha, String idTercerizadoras) throws SystemException {
        Connection con = null;
        CallableStatement stmt = null;
        try {
            String sql = "{call insertar_totales_debitos(?,?,?,?,?,?,?,?,?,?,?)}";
            _log.debug("creando conexion");
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());
            stmt.setBigDecimal(1, deb.getMontoHospitales());
            stmt.setBigDecimal(2, deb.getMontoHospitaleDebito());
            stmt.setBigDecimal(3, deb.getMontoPrestadores());
            stmt.setBigDecimal(4, deb.getMontoPrestadoreDebito());
            stmt.setBigDecimal(5, deb.getMontoLiquidacionPendiente());
            stmt.setBigDecimal(6, deb.getMontoLiquidacionPendienteDebito());
            stmt.setBigDecimal(7, deb.getMontoReintegros());
            stmt.setBigDecimal(8, deb.getMontoReintegroDebito());
            stmt.setString(9, user);
            stmt.setDate(10, new java.sql.Date(fecha.getTime()));
            stmt.setString(11, idTercerizadoras);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            _log.error("Error al insertar el item de catastro", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
        return 0;
    }

    public int grabarLiquidacionesPendientesDebitos(DebitosLiquidacionesPendientes deb, String user, Date fecha, String idTercerizadoras)
            throws SystemException {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            _log.debug("creando conexion");
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            stmt.setDate(1, new java.sql.Date(fecha.getTime()));
            stmt.setString(2, WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES);
            stmt.setString(3, idTercerizadoras);
            stmt.setBigDecimal(4, deb.getNumero());
            stmt.setString(5, deb.getHospitalesAutogestion());
            stmt.setString(6, deb.getFactura());
            stmt.setBigDecimal(7, deb.getMonto());
            stmt.setBigDecimal(8, deb.getCargoPrestadoraReclamo());
            stmt.setNull(9, Types.NUMERIC);
            stmt.setNull(10, Types.VARCHAR);
            stmt.setNull(11, Types.VARCHAR);
            stmt.setNull(12, Types.VARCHAR);
            stmt.setNull(13, Types.VARCHAR);
            stmt.setNull(14, Types.DATE);
            stmt.setString(15, user);
            stmt.setNull(16, Types.INTEGER);

            rs = stmt.executeQuery();
            int inserted = 0;
            while (rs.next()) {
                inserted = rs.getInt(1);
            }

            // NUEVO: recalcular totales para el periodo/tercerizadora
            recalcularTotalesDebitos(fecha, idTercerizadoras, user);

            return inserted;

        } catch (SQLException e) {
            _log.error("Error al insertar el item de catastro", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int grabarHospitalesDebitos(DebitosHospitales deb, String user, Date fecha, String idTercerizadoras)
            throws SystemException {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        // FAIL-CLOSED básico
        if (fecha == null) {
            _log.warn("grabarHospitalesDebitos(CERRADO): fecha null -> no inserto");
            return 0;
        }
        if (deb == null) {
            _log.warn("grabarHospitalesDebitos(CERRADO): deb null -> no inserto");
            return 0;
        }
        if (Validator.isNull(idTercerizadoras) || Validator.isNull(idTercerizadoras.trim())) {
            _log.warn("grabarHospitalesDebitos(CERRADO): idTercerizadoras vacío -> no inserto");
            return 0;
        }

        // descripcion es NOT NULL en DDL -> no mandes null
        String hospital = deb.getHospital();
        if (hospital == null) hospital = "";

        // monto (si lo dejás null, sumas/totales se vuelven raros)
        BigDecimal monto = (deb.getMonto() != null) ? deb.getMonto() : BigDecimal.ZERO;

        // numero_op (numeric) desde ordenPago string
        BigDecimal opId = parseBigDecimalOrNull(deb.getOrdenPago());

        // nuevos
        BigDecimal cargoPrestadora = deb.getCargoPrestadora(); // numeric(15,2) nullable
        BigDecimal importeTotal    = deb.getImporteTotal();    // numeric(15,2) nullable
        Integer liquidacionId      = deb.getIdLiquidacion();   // Integer nullable (ya lo cambiaste)

        try {
            if (liquidacionId == null || liquidacionId.intValue() <= 0) {
                _log.warn("[HO][CERRADO][INS][SKIP] liquidacion_id null/<=0 "
                        + "op=" + safe(deb.getOrdenPago())
                        + " hosp=" + safe(deb.getHospital())
                        + " fact=" + safe(deb.getFactura()));
                return 0;
            }
            _log.info("[HO][CERRADO][INS][FIELDS] terc=" + safe(idTercerizadoras)
                    + " periodo=" + new java.sql.Date(fecha.getTime())
                    + " op=" + safe(deb.getOrdenPago())
                    + " liq=" + liquidacionId
                    + " cargo=" + (cargoPrestadora != null ? cargoPrestadora.toPlainString() : "null")
                    + " imp=" + (importeTotal != null ? importeTotal.toPlainString() : "null"));
            // V3: 20 parámetros
            String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            stmt.setDate(1, new java.sql.Date(fecha.getTime()));                       // periodo
            stmt.setString(2, WebKeysLiquidaciones.DEBITOS_HOSPITALES);                // tipo
            stmt.setString(3, idTercerizadoras.trim());                                // id_tercerizadora
            stmt.setBigDecimal(4, deb.getNumero());                                    // numero (puede ser null)
            stmt.setString(5, hospital);                                               // descripcion NOT NULL
            stmt.setString(6, deb.getFactura());                                       // numero_factura
            stmt.setBigDecimal(7, monto);                                              // monto_debitar
            stmt.setNull(8, Types.NUMERIC);                                            // monto_debitar_reclamo (HO no usa)

            if (opId != null) stmt.setBigDecimal(9, opId);                             // numero_op
            else stmt.setNull(9, Types.NUMERIC);

            stmt.setNull(10, Types.VARCHAR);                                           // apellido
            stmt.setNull(11, Types.VARCHAR);                                           // nombre
            stmt.setNull(12, Types.VARCHAR);                                           // numero_documento
            stmt.setNull(13, Types.VARCHAR);                                           // desc_seccional
            stmt.setNull(14, Types.TIMESTAMP);                                         // fecha_op (timestamp en DDL)
            stmt.setString(15, user);                                                  // alta_usr

            stmt.setNull(16, Types.INTEGER);                                           // id_reclamo_prestacional
            stmt.setNull(17, Types.VARCHAR);                                           // reclamos (string)

            if (cargoPrestadora != null) stmt.setBigDecimal(18, cargoPrestadora);      // cargo_prestadora
            else stmt.setNull(18, Types.NUMERIC);

            if (importeTotal != null) stmt.setBigDecimal(19, importeTotal);            // importe_total
            else stmt.setNull(19, Types.NUMERIC);

            if (liquidacionId != null) stmt.setInt(20, liquidacionId.intValue());      // liquidacion_id
            else stmt.setNull(20, Types.INTEGER);

            rs = stmt.executeQuery();
            int inserted = 0;
            while (rs.next()) inserted = rs.getInt(1);

            recalcularTotalesDebitos(fecha, idTercerizadoras, user);
            return inserted;

        } catch (SQLException e) {
            _log.error("Error al insertar HO (cerrado) via V3", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int grabarReintegrosDebitos(DebitosaReintegros deb, String user, Date fecha, String idTercerizadoras)
            throws SystemException {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        // FAIL-CLOSED: RE requiere reintegro válido
        Integer rein = null;
        try {
            rein = (deb != null ? Integer.valueOf(deb.getNumReintegro()) : null);
        } catch (Exception ignore) {}

        if (rein == null || rein.intValue() <= 0) {
            _log.warn("grabarReintegrosDebitos(CERRADO): reintegro inválido (" + rein + ") -> no inserto."
                    + " terc=" + safe(idTercerizadoras)
                    + " fecha=" + (fecha != null ? new java.sql.Date(fecha.getTime()) : "null")
                    + " doc=" + safe(deb != null ? deb.getDocumento() : null)
                    + " ape=" + safe(deb != null ? deb.getApellido() : null)
                    + " nom=" + safe(deb != null ? deb.getNombre() : null));
            return 0;
        }

        try {
            String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            // 1) periodo (date)
            stmt.setDate(1, new java.sql.Date(fecha.getTime()));

            // 2) tipo
            stmt.setString(2, WebKeysLiquidaciones.DEBITOS_REINTEGROS);

            // 3) tercerizadora
            stmt.setString(3, idTercerizadoras);

            // 4) registro_id (numeric) = num reintegro
            stmt.setBigDecimal(4, BigDecimal.valueOf(rein.intValue()));

            // 5) descripcion
            stmt.setString(5, (deb != null ? deb.getDescripcion() : null));

            // 6) factura (no aplica)
            stmt.setNull(6, Types.VARCHAR);

            // 7) monto
            stmt.setBigDecimal(7, (deb != null && deb.getImporteTotal() != null) ? deb.getImporteTotal() : BigDecimal.ZERO);

            // 8) cargo_reclamo (no aplica)
            stmt.setNull(8, Types.NUMERIC);

            // 9) orden_pago_id (numeric) -> parse seguro (evita NumberFormatException)
            BigDecimal opId = parseBigDecimalOrNull(deb != null ? deb.getNumeroOP() : null);
            if (opId != null) stmt.setBigDecimal(9, opId);
            else stmt.setNull(9, Types.NUMERIC);

            // 10..13 datos persona
            stmt.setString(10, (deb != null ? deb.getApellido() : null));
            stmt.setString(11, (deb != null ? deb.getNombre() : null));
            stmt.setString(12, (deb != null ? deb.getDocumento() : null));
            stmt.setString(13, (deb != null ? deb.getSeccional() : null));

            // 14) fecha op (date)
            if (deb != null && deb.getFechaOP() != null) stmt.setDate(14, new java.sql.Date(deb.getFechaOP().getTime()));
            else stmt.setNull(14, Types.DATE);

            // 15) usuario
            stmt.setString(15, user);

            // 16) reclamo prestacional
            if (deb != null && deb.getReclamoPrestacional() != null) stmt.setInt(16, deb.getReclamoPrestacional().intValue());
            else stmt.setNull(16, Types.INTEGER);

            rs = stmt.executeQuery();
            int inserted = 0;
            while (rs.next()) {
                inserted = rs.getInt(1);
            }

            recalcularTotalesDebitos(fecha, idTercerizadoras, user);

            return inserted;

        } catch (SQLException e) {
            _log.error("Error al insertar reintegro (cerrado)", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int grabarPrestadoresDebitos(
            DebitosaPrestadores deb,
            String user,
            Date fecha,
            String idTercerizadoras
    ) throws SystemException {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        BigDecimal registroId = (deb != null ? deb.getNumero() : null);
        if (registroId == null && deb != null && deb.getIdLiquidacion() > 0) {
            registroId = BigDecimal.valueOf(deb.getIdLiquidacion());
            _log.info("[PR][CERRADO][INS][ID-FALLBACK] numero=null -> uso idLiquidacion. idLiquidacion="
                    + deb.getIdLiquidacion()
                    + " registroId=" + registroId.toPlainString()
                    + " terc=" + idTercerizadoras);
        }

        try {
            String sql = "{? = call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            stmt.registerOutParameter(1, Types.INTEGER);

            stmt.setDate(2, new java.sql.Date(fecha.getTime()));
            stmt.setString(3, WebKeysLiquidaciones.DEBITOS_PRESTADORES);
            stmt.setString(4, idTercerizadoras);

            // numero
            if (registroId != null) stmt.setBigDecimal(5, registroId);
            else stmt.setNull(5, Types.NUMERIC);

            stmt.setString(6, deb.getPrestador());
            stmt.setString(7, deb.getFactura());

            // monto_debitar: revisar si debe ser deb.getCargoPrestadora() en vez de deb.getMonto()
            if (deb.getMonto() != null) stmt.setBigDecimal(8, deb.getMonto());
            else stmt.setNull(8, Types.NUMERIC);

            stmt.setNull(9, Types.NUMERIC);

            if (deb.getOrdenPago() != null) stmt.setBigDecimal(10, new BigDecimal(deb.getOrdenPago()));
            else stmt.setNull(10, Types.NUMERIC);

            stmt.setNull(11, Types.VARCHAR);
            stmt.setNull(12, Types.VARCHAR);
            stmt.setNull(13, Types.VARCHAR);
            stmt.setNull(14, Types.VARCHAR);
            stmt.setNull(15, Types.TIMESTAMP);

            stmt.setString(16, user);

            if (deb.getReclamoPrestacional() != null) stmt.setInt(17, deb.getReclamoPrestacional());
            else stmt.setNull(17, Types.INTEGER);

            if (deb.getReclamosPrestacionales() != null) stmt.setString(18, deb.getReclamosPrestacionales());
            else stmt.setNull(18, Types.VARCHAR);

            // cargo_prestadora
            if (deb.getCargoPrestadora() != null) stmt.setBigDecimal(19, deb.getCargoPrestadora());
            else stmt.setNull(19, Types.NUMERIC);

            // importe_total
            if (deb.getMonto() != null) stmt.setBigDecimal(20, deb.getMonto());
            else stmt.setNull(20, Types.NUMERIC);

            // liquidacion_id
            if (deb.getIdLiquidacion() > 0) stmt.setInt(21, deb.getIdLiquidacion());
            else stmt.setNull(21, Types.INTEGER);

            stmt.execute();
            int inserted = stmt.getInt(1);

            recalcularTotalesDebitos(fecha, idTercerizadoras, user);
            return inserted;

        } catch (SQLException e) {
            _log.error("Error al insertar item prestadores en detalles", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean existeReporteDebitoTercerizadoras(Date periodoDesde, Date periodoHasta, String idTercerizadora) {

        Connection con = null;
        CallableStatement stmt = null;

        boolean result = true;

        try {
            String sql = "{call existe_reporte_debito_tercerizadoras(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql.toString());

            if (null != periodoDesde) {
                stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            if (null != periodoHasta) {
                stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setString(3, idTercerizadora);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    return false;
                }
            }


        } catch (Exception e) {
            _log.error(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return result;
    }


    // ============================================================================
    // 1) EXISTE REPORTE GRABADO (FAIL-SAFE) + tipoProceso
    // ============================================================================
    public boolean existeReporteGrabadoDebitoTercerizadoras(Date periodoHasta, String idTercerizadora, String tipoProceso) {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        boolean result = false; // FAIL-SAFE

        try {
            String sql = "{call existe_reporte_grabado_debito_tercerizadoras(?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            if (periodoHasta != null) stmt.setDate(1, new java.sql.Date(periodoHasta.getTime()));
            else stmt.setNull(1, Types.DATE);

            stmt.setString(2, idTercerizadora);
            stmt.setString(3, tipoProceso);

            _log.info("[HAY_GRABADO_DAO] sql=" + sql
                    + " periodoHasta=" + periodoHasta
                    + " terc=" + idTercerizadora
                    + " tipo=" + tipoProceso);

            rs = stmt.executeQuery();
            while (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                result = (total != null && total.compareTo(BigDecimal.ZERO) > 0);
                _log.info("[HAY_GRABADO_DAO] total=" + total + " => result=" + result);
            }

        } catch (Exception e) {
            _log.error("[HAY_GRABADO_DAO] ERROR", e);
            result = false; // FAIL-SAFE: si falla, NO marques cerrado
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return result;
    }

    // ============================================================================
    // 10) HELPERS COMUNES
    // ============================================================================
    private void closeQuietly(ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignore) {}
    }

    private static BigDecimal parseBigDecimalOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() == 0) return null;
        try {
            return new BigDecimal(t);
        } catch (Exception ignore) {
            return null;
        }
    }

    private Comprobante getComprobante(String idTercerizadoras, BigDecimal totalDebitoPrestadoras, Date periodo) throws SystemException {

        Date fechaActual = new Date();

        int ptoVenta = 2;
        String tipoC = "NDB";
        String nro = ComprobanteServiceUtil.getUltimoNroDebito(entidad);
        int suma = Integer.valueOf(nro) + 1;


        String nroC = String.valueOf(suma);
        String letra = "";
        int cantCuotas = 0;

        Date fechaEmisionC = fechaActual;
        Date fechaRecepcionC = fechaActual;
        int sucu = 0;
        Date fechaVencimientoC = DateUtils.anyadeMeses(fechaActual, 1);

        String cuit = WebKeysGlobal.CUIT_OSPIM;

        Comprobante comprobante = new Comprobante(ptoVenta, tipoC, nroC, cuit,
                fechaEmisionC, fechaRecepcionC,
                totalDebitoPrestadoras, letra, sucu, fechaVencimientoC, null, periodo);


        String cuitAcreedor = null;
        if ("MPS".equalsIgnoreCase(idTercerizadoras)) {
            cuitAcreedor = WebKeysGlobal.PREVENCION_CUIT;
        } else if ("OMI".equalsIgnoreCase(idTercerizadoras)) {
            cuitAcreedor = WebKeysGlobal.OMINT_CUIT;
        } else if ("MEN".equalsIgnoreCase(idTercerizadoras)) {
            cuitAcreedor = WebKeysGlobal.ENSALUD_CUIT;

        }

        Empresa empresa = null;
        empresa = new Empresa(cuitAcreedor, "000", null);
        empresa.setId_seccional(0);


        comprobante.setAcreedorEmpresa(empresa);
        comprobante.setObservaciones("Debito Tercerizadora");
        comprobante.setCantCuotas(cantCuotas);
        comprobante.setNroAnticipo(0);

        comprobante.setAlta_fecha(new Date());


        List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
        ComprobanteConcepto concepto = new ComprobanteConcepto();

        //concepto.set
        Concepto conceptoCompro = new Concepto();

        conceptoCompro.setId(89);
        concepto.setImporte(totalDebitoPrestadoras);
        concepto.setConceptoComprobante(conceptoCompro);

        conceptos.add(concepto);

        comprobante.setConceptos(conceptos);

        return comprobante;

    }


    public int grabarTotalesDebitos(BigDecimal totalDebitoPrestadoras, User user, Date fecha, Date periodo, String idTercerizadoras) throws SystemException {
        Comprobante comp = null;

        comp = getComprobante(idTercerizadoras, totalDebitoPrestadoras, periodo);

        try {
            ComprobanteServiceUtil.save(comp, user, entidad);
        } catch (Exception e) {
            _log.error(e);
        }


        return 0;
    }


    public List<DebitosaTotal> getArchivosDebitos(Date periodo, String idTercerizadora) throws SystemException {
        Connection con = null;
        CallableStatement stmt = null;
        List<DebitosaTotal> list = new ArrayList<DebitosaTotal>();

        try {
            con = ConnectionHelper.getConnection();

            String sql = "{call trae_debitos_tercerizadoras_por_periodo(?,?)}";
            stmt = con.prepareCall(sql);

            if (periodo != null) stmt.setDate(1, new java.sql.Date(periodo.getTime()));
            else stmt.setNull(1, Types.DATE);

            stmt.setString(2, idTercerizadora);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DebitosaTotal archivo = DebitosaTotal.getMapping(rs, "deb_");
                list.add(archivo);
            }

        } catch (Exception e) {
            _log.error("Error al trae_debitos_tercerizadoras_por_periodo()", e);
            throw new SystemException(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }

        return list;
    }

    public int grabarBorradorLiquidacionesPendientesDebitos(
            DebitosLiquidacionesPendientes deb,
            String user,
            Date periodoFechaDesde,
            String idTercerizadora,
            String workKey
    ) throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        idTercerizadora = (idTercerizadora != null) ? idTercerizadora.trim() : "";
        workKey = (workKey != null) ? workKey.trim() : "";

        if (Validator.isNull(idTercerizadora)) {
            _log.warn("grabarBorradorLiquidacionesPendientesDebitos: idTercerizadora vacío -> no inserto");
            return 0;
        }
        if (Validator.isNull(workKey)) {
            _log.warn("grabarBorradorLiquidacionesPendientesDebitos: workKey vacío -> no inserto (BORRADOR)");
            return 0;
        }
        if (periodoFechaDesde == null) {
            _log.warn("grabarBorradorLiquidacionesPendientesDebitos: periodoFechaDesde null -> no inserto");
            return 0;
        }

        try {
            con = ConnectionHelper.getConnection();

            // IMPORTANTE: +2 params (work_scope, work_key)
            String sql =
                    "SELECT public.inserta_debitos_tercerizadoras_pendientes(" +
                            "?::timestamp, ?::varchar, ?::numeric, ?::varchar, ?::varchar, ?::numeric, ?::numeric, ?::numeric, ?::varchar, ?::char(1), ?::text)";

            ps = con.prepareStatement(sql);

            // 1) periodo_fecha_desde
            setTsOrNull(ps, 1, periodoFechaDesde);

            // 2) id_tercerizadora
            ps.setString(2, idTercerizadora);

            // 3) liquidacion_id (numeric)
            setBigDecOrNull(ps, 3, (deb != null ? deb.getNumero() : null));

            // 4) prestador_nombre
            ps.setString(4, (deb != null ? deb.getHospitalesAutogestion() : null));

            // 5) numero_factura
            ps.setString(5, (deb != null ? deb.getFactura() : null));

            // 6) monto_prestador
            setBigDecOrNull(ps, 6, (deb != null ? deb.getMonto() : null));

            // 7) cargo_prestadora
            setBigDecOrNull(ps, 7, (deb != null ? deb.getCargoPrestadora() : null));

            // 8) cargo_reclamo
            setBigDecOrNull(ps, 8, (deb != null ? deb.getCargoPrestadoraReclamo() : null));

            // 9) usuario
            ps.setString(9, user);

            // 10) work_scope (BORRADOR)
            ps.setObject(10, "B", Types.CHAR);

            // 11) work_key (BORRADOR)
            ps.setString(11, workKey);

            rs = ps.executeQuery();
            return readSingleInt(rs);

        } catch (SQLException e) {
            _log.error("Error al insertar borrador liquidaciones pendientes", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }


    public int grabarBorradorReintegrosDebitos(
            DebitosaReintegros deb,
            String user,
            Date periodoFechaDesde,
            String idTercerizadora,
            String workKey
    ) throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        idTercerizadora = (idTercerizadora != null) ? idTercerizadora.trim() : "";
        workKey = (workKey != null) ? workKey.trim() : "";

        if (Validator.isNull(idTercerizadora)) {
            _log.warn("grabarBorradorReintegrosDebitos: idTercerizadora vacío -> no inserto");
            return 0;
        }
        if (Validator.isNull(workKey)) {
            _log.warn("grabarBorradorReintegrosDebitos: workKey vacío -> no inserto (BORRADOR)");
            return 0;
        }
        if (periodoFechaDesde == null) {
            _log.warn("grabarBorradorReintegrosDebitos: periodoFechaDesde null -> no inserto");
            return 0;
        }

        // FAIL-CLOSED: RE requiere reintegro válido
        Integer rein = null;
        try {
            rein = (deb != null ? Integer.valueOf(deb.getNumReintegro()) : null);
        } catch (Exception ignore) {}

        if (rein == null || rein.intValue() <= 0) {
            _log.warn("grabarBorradorReintegrosDebitos: reintegro inválido (" + rein + ") -> no inserto. "
                    + " terc=" + idTercerizadora + " wk=" + safe(workKey)
                    + " doc=" + safe(deb != null ? deb.getDocumento() : null)
                    + " ape=" + safe(deb != null ? deb.getApellido() : null)
                    + " nom=" + safe(deb != null ? deb.getNombre() : null));
            return 0;
        }

        try {
            con = ConnectionHelper.getConnection();

            // +2 params (work_scope, work_key)
            String sql =
                    "SELECT public.inserta_debitos_tercerizadoras_reintegros(" +
                            "?::timestamp, ?::varchar, ?::int, ?::varchar, ?::varchar, ?::varchar, ?::numeric, ?::varchar, ?::timestamp, " +
                            "?::numeric, ?::varchar, ?::varchar, ?::int, ?::varchar, ?::char(1), ?::text)";

            ps = con.prepareStatement(sql);

            // 1) periodo_fecha_desde
            setTsOrNull(ps, 1, periodoFechaDesde);

            // 2) id_tercerizadora
            ps.setString(2, idTercerizadora);

            // 3) reintegro_numero (int) - validado
            ps.setInt(3, rein.intValue());

            // 4) numero_documento
            ps.setString(4, (deb != null ? deb.getDocumento() : null));

            // 5) seccional_descripcion
            ps.setString(5, (deb != null ? deb.getSeccional() : null));

            // 6) descripcion
            ps.setString(6, (deb != null ? deb.getDescripcion() : null));

            // 7) monto_debitar
            setBigDecOrNull(ps, 7, (deb != null ? deb.getImporteTotal() : null));

            // 8) orden_pago_numero (varchar)
            String op = (deb != null ? deb.getNumeroOP() : null);
            if (op != null) {
                op = op.trim();
                if (op.length() == 0) op = null;
            }
            ps.setString(8, op);

            // 9) orden_pago_fecha (timestamp)
            if (deb != null && deb.getFechaOP() != null) ps.setTimestamp(9, toTs(deb.getFechaOP()));
            else ps.setNull(9, Types.TIMESTAMP);

            // 10) cargo_prestadora
            setBigDecOrNull(ps, 10, (deb != null ? deb.getCargoPrestadora() : null));

            // 11) apellido
            ps.setString(11, (deb != null ? deb.getApellido() : null));

            // 12) nombre
            ps.setString(12, (deb != null ? deb.getNombre() : null));

            // 13) reclamo_prestacional
            setIntOrNull(ps, 13, (deb != null ? deb.getReclamoPrestacional() : null));

            // 14) usuario
            ps.setString(14, user);

            // 15) work_scope (BORRADOR)
            ps.setObject(15, "B", Types.CHAR);

            // 16) work_key (BORRADOR)
            ps.setString(16, workKey);

            rs = ps.executeQuery();
            return readSingleInt(rs);

        } catch (SQLException e) {
            _log.error("Error al insertar borrador reintegros", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public int grabarBorradorPrestadoresDebitos(
            DebitosaPrestadores deb,
            String user,
            Date periodoFechaDesde,
            String idTercerizadora,
            String workKey
    ) throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        idTercerizadora = (idTercerizadora != null) ? idTercerizadora.trim() : "";
        workKey = (workKey != null) ? workKey.trim() : "";

        _log.info("[PR][BORRADOR][INS][START] terc=" + idTercerizadora
                + " workKey=" + workKey
                + " periodoDesde=" + (periodoFechaDesde != null ? new java.sql.Timestamp(periodoFechaDesde.getTime()) : "null")
                + " user=" + user
                + " deb{prestador=" + safe(deb != null ? deb.getPrestador() : null)
                + " factura=" + safe(deb != null ? deb.getFactura() : null)
                + " op=" + safe(deb != null ? deb.getOrdenPago() : null)
                + " reclamo=" + (deb != null ? deb.getReclamoPrestacional() : null)
                + " idLiquidacion=" + (deb != null ? deb.getIdLiquidacion() : 0)
                + " numero=" + (deb != null && deb.getNumero() != null ? deb.getNumero().toPlainString() : "null")
                + "}");

        if (Validator.isNull(idTercerizadora)) {
            _log.warn("grabarBorradorPrestadoresDebitos: idTercerizadora vacío -> no inserto");
            return 0;
        }
        if (Validator.isNull(workKey)) {
            _log.warn("grabarBorradorPrestadoresDebitos: workKey vacío -> no inserto (BORRADOR)");
            return 0;
        }
        if (periodoFechaDesde == null) {
            _log.warn("grabarBorradorPrestadoresDebitos: periodoFechaDesde null -> no inserto");
            return 0;
        }

        // --- CONSISTENCIA PR: registro_id (numeric) <-> registro_id_int (int NOT NULL)
        // GARANTÍA: si numero viene null pero idLiquidacion viene bien (viene de reintegro), lo uso.
        java.math.BigDecimal registroId = (deb != null ? deb.getNumero() : null);
        if (registroId == null && deb != null && deb.getIdLiquidacion() > 0) {
            registroId = BigDecimal.valueOf(deb.getIdLiquidacion());
            _log.info("[PR][BORRADOR][INS][ID-FALLBACK] numero=null -> uso idLiquidacion. idLiquidacion="
                    + deb.getIdLiquidacion() + " registroId=" + registroId.toPlainString()
                    + " workKey=" + workKey + " terc=" + idTercerizadora);
        }

        Integer registroIdInt = null;

        if (registroId != null) {
            try {
                registroIdInt = registroId.intValueExact();
            } catch (ArithmeticException ex) {
                _log.warn("grabarBorradorPrestadoresDebitos: registro_id no convertible a int exacto. registro_id="
                        + String.valueOf(registroId) + " -> no inserto (evito inconsistencia)");
                return 0;
            }
        } else {
            _log.warn("grabarBorradorPrestadoresDebitos: registro_id NULL (deb.getNumero()) -> no inserto (PR requiere ID)");
            _log.warn("PR sin registro_id: prestador=" + safe(deb.getPrestador())
                    + " factura=" + safe(deb.getFactura())
                    + " op=" + safe(deb.getOrdenPago())
                    + " reclamo=" + deb.getReclamoPrestacional()
                    + " workKey=" + workKey
                    + " idLiquidacion=" + deb.getIdLiquidacion());
            return 0;
        }

        try {
            con = ConnectionHelper.getConnection();

            // IMPORTANTE: +2 params (work_scope, work_key)
            String sql =
                    "SELECT public.inserta_debitos_tercerizadoras_prestadores(" +
                            "?::timestamp, ?::varchar, ?::numeric, ?::int, ?::varchar, ?::varchar, ?::numeric, ?::varchar, ?::numeric, ?::int, ?::text, ?::varchar, ?::char(1), ?::text)";
            ps = con.prepareStatement(sql);

            // 1) periodo_fecha_desde
            setTsOrNull(ps, 1, periodoFechaDesde);

            // 2) id_tercerizadora
            ps.setString(2, idTercerizadora);

            // 3) registro_id (numeric)
            setBigDecOrNull(ps, 3, registroId);

            // 4) registro_id_int (int NOT NULL)
            ps.setInt(4, registroIdInt);

            // 5) prestador_nombre
            ps.setString(5, (deb != null ? deb.getPrestador() : null));

            // 6) numero_factura
            ps.setString(6, (deb != null ? deb.getFactura() : null));

            // 7) monto_debitar
            setBigDecOrNull(ps, 7, (deb != null ? deb.getMonto() : null));

            // 8) orden_pago_numero (varchar)
            ps.setString(8, (deb != null ? deb.getOrdenPago() : null));

            // 9) cargo_prestadora
            setBigDecOrNull(ps, 9, (deb != null ? deb.getCargoPrestadora() : null));

            // 10) reclamo_prestacional
            setIntOrNull(ps, 10, (deb != null ? deb.getReclamoPrestacional() : null));

            // 11) reclamos_prestacionales (text)
            ps.setString(11, (deb != null ? deb.getReclamosPrestacionales() : null));

            // 12) usuario
            ps.setString(12, user);

            // 13) work_scope (BORRADOR)
            ps.setObject(13, "B", Types.CHAR);

            // 14) work_key (BORRADOR)
            ps.setString(14, workKey);

            if (_log.isDebugEnabled()) {
                _log.debug("[PR][BORRADOR][INS][SQL] terc=" + idTercerizadora
                        + " workKey=" + workKey
                        + " registroId=" + (registroId != null ? registroId.toPlainString() : "null")
                        + " registroIdInt=" + registroIdInt
                        + " prestador=" + safe(deb != null ? deb.getPrestador() : null)
                        + " factura=" + safe(deb != null ? deb.getFactura() : null)
                        + " op=" + safe(deb != null ? deb.getOrdenPago() : null));
            }

            rs = ps.executeQuery();
            int inserted = readSingleInt(rs);

            _log.info("[PR][BORRADOR][INS][END] inserted=" + inserted
                    + " terc=" + idTercerizadora
                    + " workKey=" + workKey
                    + " registroId=" + registroId.toPlainString()
                    + " registroIdInt=" + registroIdInt);

            return inserted;

        } catch (SQLException e) {
            _log.error("Error al insertar borrador prestadores", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public int grabarBorradorHospitalesDebitos(
            DebitosHospitales deb, String user, Date periodoFechaDesde, String idTercerizadora, String workKey
    ) throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        idTercerizadora = (idTercerizadora != null) ? idTercerizadora.trim() : "";

        if (Validator.isNull(idTercerizadora)) {
            _log.warn("grabarBorradorHospitalesDebitos: idTercerizadora vacío -> no inserto");
            return 0;
        }
        workKey = (workKey != null) ? workKey.trim() : "";
        if (Validator.isNull(workKey)) {
            _log.warn("grabarBorradorHospitalesDebitos: workKey vacío -> no inserto (BORRADOR)");
            return 0;
        }
        if (periodoFechaDesde == null) {
            _log.warn("grabarBorradorHospitalesDebitos: periodoFechaDesde null -> no inserto");
            return 0;
        }

        try {
            con = ConnectionHelper.getConnection();

            // +1 parámetro: id_tercerizadora
            String sql =
                    "SELECT public.inserta_debitos_tercerizadoras_hospitales(" +
                            "?::timestamp, ?::varchar, ?::numeric, ?::varchar, ?::varchar, ?::numeric, ?::varchar, " +
                            "?::numeric, ?::numeric, ?::int, ?::varchar, ?::char(1), ?::text)";
            ps = con.prepareStatement(sql);

            // 1) periodo_fecha_desde
            ps.setTimestamp(1, new java.sql.Timestamp(periodoFechaDesde.getTime()));

            // 2) id_tercerizadora
            ps.setString(2, idTercerizadora);

            // 3) orden_pago_id (numeric)
            setBigDecOrNull(ps, 3, (deb != null ? deb.getNumero() : null));

            // 4) hospital_nombre
            ps.setString(4, (deb != null ? deb.getHospital() : null));

            // 5) numero_factura
            ps.setString(5, (deb != null ? deb.getFactura() : null));

            // 6) monto_debitar
            setBigDecOrNull(ps, 6, (deb != null ? deb.getMonto() : null));

            // 7) orden_pago_numero (varchar)
            ps.setString(7, (deb != null ? deb.getOrdenPago() : null));

            // 8) cargo_prestadora
            setBigDecOrNull(ps, 8, (deb != null ? deb.getCargoPrestadora() : null));

            // 9) importe_total
            setBigDecOrNull(ps, 9, (deb != null ? deb.getImporteTotal() : null));

            // 10) liquidacion_id (int NOT NULL en tabla)
            if (deb == null) ps.setInt(10, 0);
            else ps.setInt(10, deb.getIdLiquidacion());

            // 11) usuario
            ps.setString(11, user);
            ps.setObject(12, "B", Types.CHAR);
            ps.setString(13, workKey);

            rs = ps.executeQuery();
            return readSingleInt(rs);

        } catch (SQLException e) {
            _log.error("Error al insertar borrador hospitales", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    /// BORRAR BORRADOR por (tipo + periodo + terc)
    // - Acepta tipo corto (LI/HO/RE/PR) y también llaves largas (DEBITOS_* / nombres).
    // - Normaliza y LOGUEA siempre el tipo original vs normalizado.
    // - Fail-safe: si no hay scope completo => NO borra.
    // - Llama a la función DB: borra_borrador_debitos_tercerizadoras_por_periodo_y_tipo
    // ============================================================================/ ============================================================================
    public int borrarBorradorDebitosTercerizadorasPorTipoYPeriodo(
            String tipoKey,
            Date periodoFechaDesde,
            String idTercerizadora,
            String workKey
    ) throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        final String tipoKeyRaw = tipoKey;
        final String workKeyRaw = workKey;

        tipoKey = (tipoKey != null) ? tipoKey.trim() : "";
        idTercerizadora = (idTercerizadora != null) ? idTercerizadora.trim() : "";
        workKey = (workKey != null) ? workKey.trim() : "";

        String tipoSel = normalizeTipoSel(tipoKey);
        if (tipoSel == null) tipoSel = "";
        tipoSel = tipoSel.trim();

        if (Validator.isNotNull(tipoKey)) {
            String t = tipoKey.toUpperCase().trim();
            if ("DEBITOS_LIQ_PENDIENTES".equals(t)) tipoSel = "LI";
            else if ("DEBITOS_HOSPITALES".equals(t)) tipoSel = "HO";
            else if ("DEBITOS_REINTEGROS".equals(t)) tipoSel = "RE";
            else if ("DEBITOS_PRESTADORES".equals(t)) tipoSel = "PR";
        }

        if (!("LI".equals(tipoSel) || "HO".equals(tipoSel) || "RE".equals(tipoSel) || "PR".equals(tipoSel))) {
            _log.warn("[BORRAR-BORRADOR][POR-TIPO-PERIODO] tipo inválido: raw=" + safe(tipoKeyRaw)
                    + " normalized=" + safe(tipoSel) + " -> no borro");
            return 0;
        }
        if (Validator.isNull(idTercerizadora)) {
            _log.warn("[BORRAR-BORRADOR][POR-TIPO-PERIODO] idTercerizadora null/vacío -> no borro"
                    + " tipoSel=" + safe(tipoSel) + " rawTipo=" + safe(tipoKeyRaw));
            return 0;
        }
        if (periodoFechaDesde == null) {
            _log.warn("[BORRAR-BORRADOR][POR-TIPO-PERIODO] periodoFechaDesde null -> no borro"
                    + " tipoSel=" + safe(tipoSel) + " rawTipo=" + safe(tipoKeyRaw)
                    + " terc=" + safe(idTercerizadora));
            return 0;
        }
        if (Validator.isNull(workKey)) {
            _log.warn("[BORRAR-BORRADOR][POR-TIPO-PERIODO] workKey null/vacío -> no borro"
                    + " tipoSel=" + safe(tipoSel)
                    + " rawTipo=" + safe(tipoKeyRaw)
                    + " terc=" + safe(idTercerizadora)
                    + " periodoFechaDesde(ms)=" + String.valueOf(periodoFechaDesde.getTime()));
            return 0;
        }

        try {
            con = ConnectionHelper.getConnection();

            final String sql =
                    "SELECT public.borra_borrador_debitos_tercerizadoras_por_periodo_y_tipo(" +
                            "?::timestamp, ?::varchar, ?::varchar, ?::text)";

            ps = con.prepareStatement(sql);
            setTsOrNull(ps, 1, periodoFechaDesde);
            ps.setString(2, tipoSel);
            ps.setString(3, idTercerizadora);
            ps.setString(4, workKey);

            rs = ps.executeQuery();
            int deleted = readSingleInt(rs);

            _log.info("[BORRAR-BORRADOR][POR-TIPO-PERIODO] deleted=" + deleted
                    + " tipoRaw=" + safe(tipoKeyRaw)
                    + " tipoSel=" + safe(tipoSel)
                    + " periodoFechaDesde(ms)=" + String.valueOf(periodoFechaDesde.getTime())
                    + " terc=" + safe(idTercerizadora)
                    + " workKey=" + safe(workKey));

            return deleted;

        } catch (SQLException e) {
            _log.error("Error al borrar borrador por (tipo+periodo+terc+workKey) (db-fn)."
                    + " tipoRaw=" + safe(tipoKeyRaw)
                    + " tipoSel=" + safe(tipoSel)
                    + " periodoFechaDesde=" + (periodoFechaDesde != null ? String.valueOf(periodoFechaDesde.getTime()) : "null")
                    + " terc=" + safe(idTercerizadora)
                    + " workKeyRaw=" + safe(workKeyRaw)
                    + " workKey=" + safe(workKey), e);
            throw new SystemException(e);

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    private static java.sql.Timestamp toTs(Date d) {
        return (d == null) ? null : new java.sql.Timestamp(d.getTime());
    }

    private static void setTsOrNull(PreparedStatement ps, int idx, Date d) throws SQLException {
        java.sql.Timestamp ts = toTs(d);
        if (ts == null) ps.setNull(idx, Types.TIMESTAMP);
        else ps.setTimestamp(idx, ts);
    }

    private static void setBigDecOrNull(PreparedStatement ps, int idx, BigDecimal v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.NUMERIC);
        else ps.setBigDecimal(idx, v);
    }

    private static void setIntOrNull(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v.intValue());
    }

    private static int readSingleInt(ResultSet rs) throws SQLException {
        return (rs != null && rs.next()) ? rs.getInt(1) : 0;
    }

    private String mapTipoProcesoToTipoDetalle(String tipoProceso) {
        if (tipoProceso == null) return null;
        if ("LI".equalsIgnoreCase(tipoProceso)) return WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES;
        if ("HO".equalsIgnoreCase(tipoProceso)) return WebKeysLiquidaciones.DEBITOS_HOSPITALES;
        if ("RE".equalsIgnoreCase(tipoProceso)) return WebKeysLiquidaciones.DEBITOS_REINTEGROS;
        if ("PR".equalsIgnoreCase(tipoProceso)) return WebKeysLiquidaciones.DEBITOS_PRESTADORES;
        return null;
    }

    public int[] reabrirDebitosTercerizadorasPeriodo(
            String tipoSel,
            Date periodoFechaDesde,
            Date periodoHasta,
            String idTercerizadora,
            String usuario
    ) throws SystemException {

        Connection con = null;
        PreparedStatement psDelDraft = null;
        PreparedStatement psReabrir = null;
        PreparedStatement psRecalcTotales = null;
        PreparedStatement psForceZero = null;
        ResultSet rs = null;

        boolean oldAutoCommit = true;

        int deletedDraftPrev = 0;
        int insertedBorrador = 0;
        int deletedGrabado = 0;

        int recalcRc = 0;
        int updatedZeroCols = 0;

        // ---------- Validaciones mínimas (fail-closed) ----------
        String t = (tipoSel != null) ? tipoSel.trim().toUpperCase() : "";
        if (!("LI".equals(t) || "HO".equals(t) || "RE".equals(t) || "PR".equals(t))) {
            throw new SystemException(new Exception("tipoSel inválido: " + tipoSel));
        }

        if (periodoFechaDesde == null || periodoHasta == null) {
            throw new SystemException(new Exception("Periodo inválido: desde/hasta null"));
        }

        String terc = (idTercerizadora != null) ? idTercerizadora.trim().toUpperCase() : "";
        if (Validator.isNull(terc)) {
            throw new SystemException(new Exception("Tercerizadora inválida"));
        }

        String u = (usuario != null && usuario.trim().length() > 0) ? usuario.trim() : "system";

        // Canonical workKey: YYYY-MM|TERC
        String workKey = buildWorkKey(periodoFechaDesde, terc);
        if (Validator.isNull(workKey)) {
            throw new SystemException(new Exception("workKey inválido (no se pudo construir)"));
        }

        // ---------- SQL ----------
        final String sqlDeleteDraft =
                "SELECT public.borra_borrador_debitos_tercerizadoras_por_periodo_y_tipo(" +
                        "?::timestamp, ?::varchar, ?::varchar, ?::text)";

        final String sqlReabrir =
                "SELECT inserted_borrador, deleted_grabado " +
                        "FROM public.reabrir_debitos_tercerizadoras_periodo(" +
                        "?::varchar, ?::timestamp, ?::date, ?::varchar, ?::varchar)";

        // Recalcular totales (MISMA CONEXIÓN / MISMA TX)
        final String sqlRecalcTotales =
                "SELECT public.recalcular_totales_debitos(?::date, ?::varchar, ?::varchar)";

        // Force-zero del tipo seleccionado (para no arrastrar montos cerrados)
        final String sqlForceZero;
        if ("HO".equals(t)) {
            sqlForceZero = "UPDATE public.reporte_totales_debitos " +
                    "SET monto_hospital = 0, monto_hospital_debito = 0 " +
                    "WHERE fecha = ?::date AND id_tercerizadora = ?::varchar";
        } else if ("RE".equals(t)) {
            sqlForceZero = "UPDATE public.reporte_totales_debitos " +
                    "SET monto_reintegro = 0, monto_reintegro_debito = 0 " +
                    "WHERE fecha = ?::date AND id_tercerizadora = ?::varchar";
        } else if ("PR".equals(t)) {
            sqlForceZero = "UPDATE public.reporte_totales_debitos " +
                    "SET monto_prestador = 0, monto_prestador_debito = 0 " +
                    "WHERE fecha = ?::date AND id_tercerizadora = ?::varchar";
        } else { // "LI"
            sqlForceZero = "UPDATE public.reporte_totales_debitos " +
                    "SET monto_autogestion = 0, monto_autogestion_debito = 0 " +
                    "WHERE fecha = ?::date AND id_tercerizadora = ?::varchar";
        }

        try {
            con = ConnectionHelper.getConnection();
            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            // -------------------------------------------------------
            // PASO 0) BORRAR BORRADOR PREEXISTENTE (mismo período/terc/tipo/workKey)
            // -------------------------------------------------------
            psDelDraft = con.prepareStatement(sqlDeleteDraft);
            setTsOrNull(psDelDraft, 1, periodoFechaDesde);
            psDelDraft.setString(2, t);
            psDelDraft.setString(3, terc);
            psDelDraft.setString(4, workKey);

            rs = psDelDraft.executeQuery();
            deletedDraftPrev = readSingleInt(rs);
            try { rs.close(); } catch (Exception ignore) {}
            rs = null;

            // -------------------------------------------------------
            // PASO 1) INSERTAR BORRADOR DESDE GRABADOS + BORRAR GRABADOS
            // -------------------------------------------------------
            psReabrir = con.prepareStatement(sqlReabrir);
            psReabrir.setString(1, t);
            psReabrir.setTimestamp(2, new java.sql.Timestamp(periodoFechaDesde.getTime()));
            psReabrir.setDate(3, new java.sql.Date(periodoHasta.getTime()));
            psReabrir.setString(4, terc);
            psReabrir.setString(5, u);

            rs = psReabrir.executeQuery();
            if (rs != null && rs.next()) {
                insertedBorrador = rs.getInt("inserted_borrador");
                deletedGrabado   = rs.getInt("deleted_grabado");
            } else {
                throw new SystemException(new Exception("reabrir_debitos_tercerizadoras_periodo no devolvió fila"));
            }
            try { rs.close(); } catch (Exception ignore) {}
            rs = null;

            // -------------------------------------------------------
            // PASO 2) RECALCULAR TOTALES (desde detalles que QUEDARON cerrados)
            // OJO: usar el mismo "periodo" que tu reporte_totales_debitos.fecha (en tu flujo es periodoHasta)
            // -------------------------------------------------------
            psRecalcTotales = con.prepareStatement(sqlRecalcTotales);
            psRecalcTotales.setDate(1, new java.sql.Date(periodoHasta.getTime()));
            psRecalcTotales.setString(2, terc);
            psRecalcTotales.setString(3, u);

            rs = psRecalcTotales.executeQuery();
            recalcRc = (rs != null && rs.next()) ? rs.getInt(1) : 0;
            try { rs.close(); } catch (Exception ignore) {}
            rs = null;

            // -------------------------------------------------------
            // PASO 3) FORCE-ZERO del tipoSel (blindaje contra arrastre)
            // -------------------------------------------------------
            psForceZero = con.prepareStatement(sqlForceZero);
            psForceZero.setDate(1, new java.sql.Date(periodoHasta.getTime()));
            psForceZero.setString(2, terc);
            updatedZeroCols = psForceZero.executeUpdate();

            con.commit();

            _log.info("[REABRIR][SERVICE][OK] tipoSel=" + t
                    + " terc=" + terc
                    + " workKey=" + workKey
                    + " periodoDesde(ms)=" + periodoFechaDesde.getTime()
                    + " periodoHasta(ms)=" + periodoHasta.getTime()
                    + " deletedDraftPrev=" + deletedDraftPrev
                    + " insertedBorrador=" + insertedBorrador
                    + " deletedGrabado=" + deletedGrabado
                    + " recalcTotalesRc=" + recalcRc
                    + " forceZeroUpdated=" + updatedZeroCols);

            return new int[]{ insertedBorrador, deletedGrabado };

        } catch (SystemException se) {
            rollbackQuietly(con);
            throw se;
        } catch (Exception e) {
            rollbackQuietly(con);
            _log.error("Error ejecutando reabrirDebitosTercerizadorasPeriodo (con recalc totales + force-zero)", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            try { if (psForceZero != null) psForceZero.close(); } catch (Exception ignore) {}
            try { if (psRecalcTotales != null) psRecalcTotales.close(); } catch (Exception ignore) {}
            try { if (psReabrir != null) psReabrir.close(); } catch (Exception ignore) {}
            try { if (psDelDraft != null) psDelDraft.close(); } catch (Exception ignore) {}
            try {
                if (con != null) {
                    try { con.setAutoCommit(oldAutoCommit); } catch (Exception ignore) {}
                    con.close();
                }
            } catch (Exception ignore) {}
        }
    }

    private void rollbackQuietly(Connection con) {
        if (con == null) return;
        try { con.rollback(); } catch (Exception ignore) {}
    }

    private String buildWorkKey(Date periodoFechaDesde, String tercUp) {
        if (periodoFechaDesde == null) return null;
        if (Validator.isNull(tercUp)) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(periodoFechaDesde);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 1..12
        String mm = (month < 10 ? "0" + month : String.valueOf(month));

        return year + "-" + mm + "|" + tercUp.trim().toUpperCase();
    }

    public int recalcularTotalesDebitos(Date periodo, String idTercerizadora, String usuario)
            throws SystemException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            // SELECT para funciones que retornan scalar
            final String sql = "SELECT public.recalcular_totales_debitos(?::date, ?::varchar, ?::varchar)";

            ps = con.prepareStatement(sql);

            if (periodo != null) ps.setDate(1, new java.sql.Date(periodo.getTime()));
            else ps.setNull(1, Types.DATE);

            ps.setString(2, idTercerizadora);
            ps.setString(3, usuario);

            rs = ps.executeQuery();
            return (rs != null && rs.next()) ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            _log.error("Error recalculando totales debitos", e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    private String normalizeTipoSel(String tipoSel) {
        String t = (tipoSel != null) ? tipoSel.trim().toUpperCase() : "";
        if ("LI".equals(t) || "HO".equals(t) || "RE".equals(t) || "PR".equals(t)) return t;
        return ""; // <- no inventes LI
    }

    public int cantidadReporteGrabadoDebitoTercerizadoras(Date periodoHasta, String idTercerizadora, String tipoProcesoDb) {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        int count = 0; // fail-safe

        String terc = (idTercerizadora != null) ? idTercerizadora.trim().toUpperCase() : "";
        String tipo = (tipoProcesoDb != null) ? tipoProcesoDb.trim().toUpperCase() : "";

        if (periodoHasta == null) return 0;
        if (Validator.isNull(terc) || "0".equals(terc)) return 0;
        if (Validator.isNull(tipo)) return 0;

        try {
            String sql = "{call existe_reporte_grabado_debito_tercerizadoras(?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            stmt.setDate(1, new java.sql.Date(periodoHasta.getTime()));
            stmt.setString(2, terc);
            stmt.setString(3, tipo);

            // BAJAR A DEBUG o condicionar por flag
            if (_log.isDebugEnabled()) {
                _log.debug("[HAY_GRABADO_DAO] sql=" + sql + " periodoHasta=" + periodoHasta + " terc=" + terc + " tipo=" + tipo);
            }

            rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                java.math.BigDecimal total = rs.getBigDecimal("total");
                if (total != null) {
                    try { count = total.intValue(); } catch (Exception ignore) { count = 0; }
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("[HAY_GRABADO_DAO] total=" + total + " => count=" + count);
                }
            }
        } catch (Exception e) {
            _log.error("[HAY_GRABADO_DAO] ERROR", e);
            count = 0; // fail-safe
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return count;
    }

    public List<Map<String, Object>> getPeriodosTrabajadosDebitosTercerizadoras(
            String idTercerizadora,
            String tipoProceso,
            Date desde,
            Date hasta,
            boolean incluirCerrados,
            boolean incluirBorradores
    ) throws SystemException {

        final String rid = "PERIODOS#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
        final boolean dbg = _log.isDebugEnabled();

        // OJO: acá preservamos NULL (para "todas")
        String terc = (idTercerizadora != null) ? idTercerizadora.trim().toUpperCase() : null;
        String tipo = (tipoProceso != null) ? tipoProceso.trim() : null;

        // FAIL-SAFE ajustado:
        // - Si no hay tercerizadora y NO hay rango => retorno vacío (evita query gigante accidental)
        // - Si no hay tercerizadora PERO hay rango => permito "todas" (terc = NULL)
        if (Validator.isNull(terc) || "0".equals(terc)) {
            if (desde == null && hasta == null) {
                _log.warn(prefix(rid) + "getPeriodosTrabajadosDebitosTercerizadoras: tercerizadora vacía/0 y sin rango -> retorno vacío");
                return new ArrayList<Map<String, Object>>();
            }
            // habilitar "todas"
            terc = null;
            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[OVERRIDE] tercerizadora vacía/0 con rango -> ejecuto para TODAS");
            }
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Map<String, Object>> out = new ArrayList<Map<String, Object>>();

        final String sql =
                "SELECT estado, id_tercerizadora, tipo_proceso, tipo_detalle, periodo_mes, periodo_label, " +
                        "       work_key, ultima_modificacion, usuario, cantidad_registros " +
                        "FROM public.get_periodos_trabajados_debitos_tercerizadoras(" +
                        "     ?::varchar, ?::varchar, ?::date, ?::date, ?::boolean, ?::boolean)";

        try {
            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[IN] terc=" + safe(terc)
                        + " tipoProceso=" + safe(tipo)
                        + " desde=" + (desde != null ? new java.sql.Date(desde.getTime()).toString() : "NULL")
                        + " hasta=" + (hasta != null ? new java.sql.Date(hasta.getTime()).toString() : "NULL")
                        + " incCerr=" + (incluirCerrados ? "1" : "0")
                        + " incBor=" + (incluirBorradores ? "1" : "0"));
            }
            if (dbg) _log.debug(prefix(rid) + "[SQL] " + sql);

            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            // >>> clave: bind de terc NULL vs string
            if (Validator.isNull(terc)) ps.setNull(1, Types.VARCHAR);
            else ps.setString(1, terc);

            if (Validator.isNull(tipo) || "0".equals(tipo)) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, tipo);

            if (desde == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, new java.sql.Date(desde.getTime()));

            if (hasta == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, new java.sql.Date(hasta.getTime()));

            ps.setBoolean(5, incluirCerrados);
            ps.setBoolean(6, incluirBorradores);

            rs = ps.executeQuery();

            int rows = 0;
            while (rs.next()) {
                rows++;
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put("estado", rs.getString("estado"));
                m.put("id_tercerizadora", rs.getString("id_tercerizadora"));
                m.put("tipo_proceso", rs.getString("tipo_proceso"));
                m.put("tipo_detalle", rs.getString("tipo_detalle"));
                m.put("periodo_mes", rs.getDate("periodo_mes"));
                m.put("periodo_label", rs.getString("periodo_label"));
                m.put("work_key", rs.getString("work_key"));
                m.put("ultima_modificacion", rs.getTimestamp("ultima_modificacion"));
                m.put("usuario", rs.getString("usuario"));
                m.put("cantidad_registros", Long.valueOf(rs.getLong("cantidad_registros")));
                out.add(m);
            }

            _log.info(prefix(rid) + "[OUT] rows=" + rows);
            return out;

        } catch (SQLException e) {
            _log.error(prefix(rid) + "Error en getPeriodosTrabajadosDebitosTercerizadoras terc=" + safe(terc)
                    + " tipo=" + safe(tipo), e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }

    public List<Map<String, Object>> getPeriodosPendientesDebitosTercerizadoras(
            String idTercerizadora,
            String tipoProceso,
            Date desde,
            Date hasta
    ) throws SystemException {

        final String rid = "PERIODOS-PEND#" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
        final boolean dbg = _log.isDebugEnabled();

        // OJO: preservo NULL para "todas"
        String terc = (idTercerizadora != null) ? idTercerizadora.trim().toUpperCase() : null;
        String tipo = (tipoProceso != null) ? tipoProceso.trim().toUpperCase() : null;

        // FAIL-SAFE:
        // - si no hay tercerizadora y no hay rango => vacío
        // - si no hay tercerizadora pero sí rango => permito TODAS
        if (Validator.isNull(terc) || "0".equals(terc)) {
            if (desde == null && hasta == null) {
                _log.warn(prefix(rid) + "getPeriodosPendientesDebitosTercerizadoras: tercerizadora vacía/0 y sin rango -> retorno vacío");
                return new ArrayList<Map<String, Object>>();
            }
            terc = null;
            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[OVERRIDE] tercerizadora vacía/0 con rango -> ejecuto para TODAS");
            }
        }

        if (Validator.isNull(tipo) || "0".equals(tipo)) {
            tipo = null;
        }

        // DEFENSIVO: si vinieron invertidas, las doy vuelta
        Date desdeEff = desde;
        Date hastaEff = hasta;
        if (desdeEff != null && hastaEff != null && desdeEff.after(hastaEff)) {
            Date tmp = desdeEff;
            desdeEff = hastaEff;
            hastaEff = tmp;

            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[SWAP] rango invertido detectado -> desde="
                        + new java.sql.Date(desdeEff.getTime()) + " hasta=" + new java.sql.Date(hastaEff.getTime()));
            }
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Map<String, Object>> out = new ArrayList<Map<String, Object>>();

        final String sql =
                "SELECT estado, id_tercerizadora, tipo_proceso, tipo_detalle, periodo_mes, periodo_label, " +
                        "       work_key, ultima_modificacion, usuario, cantidad_registros " +
                        "FROM public.get_periodos_pendientes_debitos_tercerizadoras(" +
                        "     ?::varchar, ?::varchar, ?::date, ?::date) " +
                        "ORDER BY id_tercerizadora ASC, tipo_proceso ASC, periodo_mes DESC";

        try {
            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[IN] terc=" + safe(terc)
                        + " tipoProceso=" + safe(tipo)
                        + " desdePeriodo=" + (desdeEff != null ? new java.sql.Date(desdeEff.getTime()).toString() : "NULL")
                        + " hastaPeriodo=" + (hastaEff != null ? new java.sql.Date(hastaEff.getTime()).toString() : "NULL"));
            }
            if (dbg) _log.debug(prefix(rid) + "[SQL] " + sql);

            con = ConnectionHelper.getConnection();
            ps = con.prepareStatement(sql);

            if (Validator.isNull(terc)) ps.setNull(1, Types.VARCHAR);
            else ps.setString(1, terc);

            if (Validator.isNull(tipo)) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, tipo);

            if (desdeEff == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, new java.sql.Date(desdeEff.getTime()));

            if (hastaEff == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, new java.sql.Date(hastaEff.getTime()));

            rs = ps.executeQuery();

            int rows = 0;
            while (rs.next()) {
                rows++;

                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put("estado", rs.getString("estado"));
                m.put("id_tercerizadora", rs.getString("id_tercerizadora"));
                m.put("tipo_proceso", rs.getString("tipo_proceso"));
                m.put("tipo_detalle", rs.getString("tipo_detalle"));
                m.put("periodo_mes", rs.getDate("periodo_mes"));
                m.put("periodo_label", rs.getString("periodo_label"));
                m.put("work_key", rs.getString("work_key"));
                m.put("ultima_modificacion", rs.getTimestamp("ultima_modificacion"));
                m.put("usuario", rs.getString("usuario"));
                m.put("cantidad_registros", Long.valueOf(rs.getLong("cantidad_registros")));

                out.add(m);
            }

            if (_log.isInfoEnabled()) {
                _log.info(prefix(rid) + "[OUT] rows=" + rows);
            }
            return out;

        } catch (SQLException e) {
            _log.error(prefix(rid) + "Error en getPeriodosPendientesDebitosTercerizadoras terc=" + safe(terc)
                    + " tipo=" + safe(tipo), e);
            throw new SystemException(e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            ConnectionHelper.cerrar(ps, con);
        }
    }
}
