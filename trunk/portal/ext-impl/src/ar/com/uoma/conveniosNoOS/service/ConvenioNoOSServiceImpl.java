package ar.com.uoma.conveniosNoOS.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.estudioisidro.beans.ConvenioPagosReporte;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.tesoreria.DuplicateConvenioIdException;
import ar.com.ospim.tesoreria.ImposibleBorrarConvenioException;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaConvenioBean;
import ar.com.ospim.tesoreria.beans.ReporteConvenioBean;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ConvenioPagoIngresado;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.ActasAcuerdos;
import jruby.objectweb.asm.Type;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ConvenioNoOSServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(ConvenioNoOSServiceImpl.class);

	private static ConvenioNoOSServiceImpl instance = null;

	public static ConvenioNoOSServiceImpl getInstance() {
		if (null == instance) {
			instance = new ConvenioNoOSServiceImpl();
		}
		return instance;
	}

	public List<Convenio> getConvenios(String convenioNro, String cuit,
			String empresa, String entidad) {
		_log.debug("buscando convenios");

		Connection con = null;
		CallableStatement stmt = null;
		List<Convenio> convenios = null;
		try {
			String sql = "{call buscar_convenios_no_os(?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, convenioNro);
			stmt.setString(2, cuit);
			stmt.setString(3, empresa);
			stmt.setString(4, entidad);

			ResultSet rs = stmt.executeQuery();
			convenios = new ArrayList<Convenio>();
			while (rs.next()) {
				Convenio conv = Convenio.getMappingNoOS(rs, "conv__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					conv.setEmpresa(emp);
				}
				convenios.add(conv);
			}
		} catch (Exception e) {
			_log.error("Error al traer convenios", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar convenios");
		return convenios;
	}

	public Convenio get(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_convenio_no_os(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Convenio conv = Convenio.getMappingNoOS(rs, "conv__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					conv.setEmpresa(emp);
				}
				return conv;
			}
		} catch (Exception e) {
			_log.error("Error al traer convenio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public List<ConvenioPago> getPagosConvenios(int id, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPago> pagos = null;
		try {
			String sql = "{call buscar_convenio_no_os_pagos(?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call buscar_convenio_no_os_pagos_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ConvenioPago>();
			while (rs.next()) {
				ConvenioPago pago = ConvenioPago.getMapping(rs, "CP__");
				Cheque cheque = Cheque.getMapping(rs, "CH__");
				Pagare pagare = new Pagare(rs.getBigDecimal("pa__nro_pagare"));
				if (cheque.getNumero() != null) {
					pago.setCheque(cheque);
				}
				if (rs.getBigDecimal("pa__nro_pagare") != null) {
					pago.setPagare(pagare);
				}
				pagos.add(pago);
			}
		} catch (Exception e) {
			_log.error("Error al buscar pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

	public List<ConvenioPago> getPagosConvenioRecibo(int id, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPago> pagos = null;
		try {
			String sql = "{call buscar_convenio_no_os_pagos(?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call buscar_convenio_no_os_pagos_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ConvenioPago>();
			while (rs.next()) {
				ConvenioPago pago = ConvenioPago.getMapping(rs, "CP__");
				Cheque cheque = Cheque.getMapping(rs, "CH__");
				Pagare pagare = new Pagare(rs.getBigDecimal("pa__nro_pagare"));
				if (cheque.getNumero() != null) {
					pago.setCheque(cheque);
				}
				if (rs.getBigDecimal("pa__nro_pagare") != null) {
					pago.setPagare(pagare);
				}
				pagos.add(pago);
			}
		} catch (Exception e) {
			_log.error("Error al buscar pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

	public void reactivar(int reciboId, String user) throws SystemException {
		_log.debug("Anulando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			sql = "{call reactivar_convenio_no_os(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboId);
			stmt.setString(2, user);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Anulando recibos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void borrar(int id, Date fechaBaja, String usr)
			throws ImposibleBorrarConvenioException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_convenio_no_os(?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setDate(2, new java.sql.Date(fechaBaja.getTime()));
			stmt.setString(3, usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarConvenioException();
				}
			}
		} catch (ImposibleBorrarConvenioException e) {
			_log.error("Error al borrar convenio", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int save(Convenio convenio, String screenName, Connection connectionParameter)
			throws SystemException, DuplicateConvenioIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter ==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call inserta_convenio_no_os (?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, convenio.getNumero());
			stmt.setString(2, convenio.getEmpresa().getCuit());
			stmt.setString(3, convenio.getEmpresa().getSucursal());
			java.sql.Date fechaIniSqlDate = new java.sql.Date(convenio
					.getFechaInicio().getTime());
			stmt.setDate(4, fechaIniSqlDate);
			stmt.setDate(5, null);
			stmt.setBigDecimal(6, convenio.getInteres());
			stmt.setBigDecimal(7, convenio.getAjusteCapital());
			stmt.setBigDecimal(8, convenio.getAjusteInteres());
			stmt.setBigDecimal(9, convenio.getDeudaActasRelacionadas());
			stmt.setBigDecimal(10, convenio.getDeudaConveniosRelacionados());
			stmt.setString(11, convenio.getEntidad());
			if (convenio.getEstadoSeguimiento() != null) {
				stmt.setInt(12, convenio.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(12, Types.INTEGER);
			}
			stmt.setString(13, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar convenio", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateConvenioIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar convenio no os", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter ==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public void save(Convenio convenio, ConvenioPago cp, String screenName, Connection connectionParameter)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call inserta_convenio_no_os_pagos(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, convenio.getId());
			stmt.setString(2, cp.getTipo().getMapping());
			stmt.setDate(3, new java.sql.Date(cp.getFechaPago().getTime()));
			stmt.setBigDecimal(4, cp.getImporte());
			stmt.setBigDecimal(5, cp.getInteres());
			stmt.setString(6, screenName);
			if (cp.getCheque() != null && cp.getCheque().getNumero() != null) {
				stmt.setBigDecimal(7, cp.getCheque().getNumero());
			} else {
				stmt.setNull(7, Types.NUMERIC);
			}
			if (cp.getCheque() != null) {
				stmt.setInt(8, cp.getCheque().getBanco().getId_banco());
			} else {
				stmt.setNull(8, Types.INTEGER);
			}
			stmt.setInt(9, cp.getNroCuota());

			if (cp.getPagare() != null) {
				stmt.setBigDecimal(10, cp.getPagare().getNumero());
			} else {
				stmt.setNull(10, Types.NUMERIC);
			}
			if (cp.getCheque() != null && cp.getCheque().getCuentaBancaria() !=null ) {
				stmt.setInt(11, cp.getCheque().getCuentaBancaria().getId_cuenta_bcria() );
			} else {
				stmt.setNull(11, Type.INT);
			}
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al salvar pago convenio no os", e);
			throw e;
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public int update(Convenio convenio, String screenName, Connection connectionParameter)
			throws SystemException, DuplicateConvenioIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call actualiza_convenio_no_os (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, convenio.getNumero());
			stmt.setString(2, convenio.getEmpresa().getCuit());
			stmt.setString(3, convenio.getEmpresa().getSucursal());
			java.sql.Date fechaIniSqlDate = new java.sql.Date(convenio
					.getFechaInicio().getTime());
			stmt.setDate(4, fechaIniSqlDate);
			stmt.setDate(5, null);
			stmt.setBigDecimal(6, convenio.getInteres());
			stmt.setBigDecimal(7, convenio.getAjusteCapital());
			stmt.setBigDecimal(8, convenio.getAjusteInteres());
			stmt.setBigDecimal(9, convenio.getDeudaActasRelacionadas());
			stmt.setBigDecimal(10, convenio.getDeudaConveniosRelacionados());
			stmt.setInt(11, convenio.getId());
			stmt.setString(12, convenio.getEntidad());
			if (convenio.getEstadoSeguimiento() != null) {
				stmt.setInt(13, convenio.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(13, Types.INTEGER);
			}
			stmt.setString(14, screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar convenio", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateConvenioIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al actualizar convenio", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public int borraPagosConvenio(int idConvenio, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_pagos_convenio_no_os_amtima(?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borra_pagos_convenio_no_os(?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenio);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al borrar convenio", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}

	public int borraActasRelacionadas(int idConvenio, Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call borra_acta_rela_convenio_no_os(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenio);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al borrar convenio no os", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public List<ActaRelacionada> getActasRelacionadas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaRelacionada> actas = null;
		try {
			String sql = "{call buscar_convenio_actas_no_os_relacionadas(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<ActaRelacionada>();
			while (rs.next()) {
				ActaRelacionada actaR = ActaRelacionada.getMapping(rs, "cr__");
				actaR.getActaRelacionada().setNumero(
						rs.getString("a__acta_relacionada_nro"));
				actas.add(actaR);
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return actas;
	}

	public void saveActaRelacionada(ActaRelacionada actaRel, String screenName, Connection connectionParameter)
			throws SQLException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call inserta_convenio_acta_no_os_relacionada (?, ?, ?, ?, ?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaRel.getConvenio().getId());
			stmt.setInt(2, actaRel.getActaRelacionada().getId());
			stmt.setBigDecimal(3, actaRel.getImporte());
			stmt.setBigDecimal(4, actaRel.getSaldo());
			stmt.setString(5, screenName);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al insertar acta relacionada", e);
			throw e;
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Convenio> getConveniosSinRecibos(String cuit, int entidad) {
		_log.debug("buscando convenios");

		Connection con = null;
		CallableStatement stmt = null;
		List<Convenio> convenios = null;
		try {
			String sql = "{call buscar_convenios_no_os_sin_recibos(?, ?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_convenios_sin_recibos(?, ?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_convenios_no_os_sin_recibos_amtima(?, ?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(
					2,
					entidad == WebKeysGlobal.AMTIMA ? WebKeysGlobal.ENTIDAD_AMTIMA
							: WebKeysGlobal.ENTIDAD_UOMA);

			ResultSet rs = stmt.executeQuery();
			convenios = new ArrayList<Convenio>();
			while (rs.next()) {
				Convenio conv = Convenio.getMapping(rs, "conv__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					conv.setEmpresa(emp);
				}
				convenios.add(conv);
			}
		} catch (Exception e) {
			_log.error("Error al traer convenios", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar convenios");
		return convenios;
	}

	public List<ConvenioPagoIngresado> getPagosIngresados(int id, int reciboId,
			int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPagoIngresado> pagos = null;
		try {
			String sql = "{call buscar_convenio_no_os_pagos_ingresados_amtima(?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_convenio_no_os_pagos_ingresados_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ConvenioPagoIngresado>();
			while (rs.next()) {
				int recibo_id = rs.getInt("recibo_id");
				if (reciboId == 0 || recibo_id == reciboId) {
					pagos.add(new ConvenioPagoIngresado(new Recibo(rs
							.getInt("recibo_id")), rs.getBigDecimal("importe"),
							rs.getBigDecimal("nro_cheque"), rs
									.getInt("id_banco")));
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

	public List<ReporteConvenioBean> reporteConvenios(Date fechaIni,
			Date fechaFin) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteConvenioBean> repo = null;
		try {
			String sql = "{call reporte_convenios_no_os(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ReporteConvenioBean>();
			while (rs.next()) {
				repo.add(ReporteConvenioBean.getMappingNoOS(rs));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_convenios", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}

	public List<ReporteCobranzaConvenioBean> reporteCobranzaConvenios(
			Date fechaIni, Date fechaFin) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteCobranzaConvenioBean> repo = null;
		try {
			String sql = "{call reporte_aplicacion_cobranzas_convenios_no_os(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ReporteCobranzaConvenioBean>();
			while (rs.next()) {
				repo.add(ReporteCobranzaConvenioBean.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_convenios", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}

	public boolean verificarReciboConvenio(int convenio_id, int entidad)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean existe = true;
		try {
			String sql = "{? = call existe_recibo_convenio_no_os_amtima(?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{? = call uoma.existe_recibo_convenio_no_os(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.BOOLEAN);
			stmt.setInt(2, convenio_id);
			stmt.executeUpdate();
			existe = stmt.getBoolean(1);
		} catch (SQLException e) {
			_log.error("Error al salvar pago acta", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return existe;
	}

	public List<ActasAcuerdos> reporteAcuerdosNoOS(String cuit, String sucu,
			Date fechaIni, Date fechaFin, Date fechaPago, int conSaldo) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActasAcuerdos> repo = null;
		try {
			String sql = "{call reporte_acuerdos_no_os(?, ?, ?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			stmt.setDate(3, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
			stmt.setDate(5, new java.sql.Date(fechaPago.getTime()));
			stmt.setInt(6, conSaldo);

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ActasAcuerdos>();
			while (rs.next()) {
				repo.add(ActasAcuerdos.getMappingAcuerdos(rs));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_actas pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}
	
	public List<ConvenioPagosReporte> getPagosConveniosAvisoVencimiento(Integer dias) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPagosReporte> pagos = null;
		try {
			String sql = "{call reporte_convenio_pagos_no_os(?)}";
		
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, dias);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ConvenioPagosReporte>();
			while (rs.next()) {
				ConvenioPagosReporte pago = ConvenioPagosReporte.getMapping(rs);
				pago.setTipoConvenio("UOMA  AMTIMA");
				pagos.add(pago);
			}
		} catch (Exception e) {
			_log.error("Error al buscar getPagosConveniosAvisoVencimiento", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

}
