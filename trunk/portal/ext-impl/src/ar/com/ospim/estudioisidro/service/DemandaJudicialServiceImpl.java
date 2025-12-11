package ar.com.ospim.estudioisidro.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.EstadisticaPrestAutorizada;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionLoteProcesado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.beans.RespuestaPreAutorizPSDTO;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.estudioisidro.beans.DemandaJudicial;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class DemandaJudicialServiceImpl implements Serializable {

	private static final long serialVersionUID = 2824658007123438770L;
	private static Log _log = LogFactoryUtil
			.getLog(DemandaJudicialServiceImpl.class);
	
	
	public Integer insertaDemanda(DemandaJudicial demanda, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_demanda = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda(?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setString(1, demanda.getEntidad());
			
			if(demanda.getFecha() ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (demanda.getFecha().getTime()));
			}
			
			stmt.setString(3, demanda.getTipo());
			
			if(demanda.getExpediente() ==null){
				  stmt.setNull(4, Types.VARCHAR );	
			}else{
				  stmt.setString(4,demanda.getExpediente());
			}
			
			if(demanda.getCaratula() ==null){
				  stmt.setNull(5, Types.VARCHAR );	
			}else{
				  stmt.setString(5,demanda.getCaratula());
			}
			
			
			if(demanda.getJuzgado() ==null){
				  stmt.setNull(6, Types.VARCHAR );	
			}else{
				  stmt.setString(6,demanda.getJuzgado());
			}
			
			stmt.setString(7, demanda.getCuit());
			
			if(demanda.getObservaciones() ==null){
				  stmt.setNull(8, Types.VARCHAR );	
			}else{
				  stmt.setString(8,demanda.getObservaciones());
			}

			if(demanda.getMontoOriginal()==null){
				stmt.setNull(9, Types.DOUBLE);	
			}else{
				stmt.setDouble(9,demanda.getMontoOriginal());
			}
			
			stmt.setString(10, screenName);
			
			
			if(demanda.getSucursal() ==null){
				  stmt.setNull(11, Types.VARCHAR );	
			}else{
				  stmt.setString(11,demanda.getSucursal());
			}
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_demanda;
	}
	
	
	
	public long insertaDemandaEstado(Integer id,Estado e,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		int id_demanda = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_estado(?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,id);
			
			stmt.setString(2, e.getId());
			stmt.setDate(3, new java.sql.Date (e.getFecha().getTime()));
			if(e.getObservacionesExternas()!=null){
			  stmt.setString(4,e.getObservacionesExternas());
			}else {
			  stmt.setNull(4, Types.VARCHAR);	
			}
			
			stmt.setString(5, screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e1) {
			_log.error("Error al insertar demanda - estado ", e1);
			throw new SystemException(e1);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_demanda;
	}

	
	public Integer insertaDemandaActa(Integer demandaId,Acta acta,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_demanda = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_acta(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setInt(2,acta.getId());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar demanda - acta ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_demanda;
	}
	
	
	public Integer insertaDemandaConvenio(Integer demandaId,Convenio convenio,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_demanda = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_convenio(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setInt(2,convenio.getId());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar demanda - convenio ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_demanda;
	}
	
	
	public Integer insertaDemandaCheque(Integer demandaId,Cheque cheque,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_demanda = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_cheque(?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setString(2,cheque.getCuit());
			stmt.setBigDecimal(3,cheque.getNumero());
			stmt.setInt(4,cheque.getBanco().getId_banco());
			stmt.setInt(5,cheque.getCuentaBancaria().getId_cuenta_bcria());
			stmt.setString(6, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar demanda - cheque ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_demanda;
	}
	
	
	public List<DemandaJudicial> getLista(DemandaJudicial filtro,Integer pagina,Connection connectionParameter) throws SystemException {
		List<DemandaJudicial> demandas = new ArrayList<DemandaJudicial>();
		
		
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		
		
		try {
			String sql = "{call judicial.busca_demandas(?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(filtro.getId()!=null) {
				stmt.setInt(1,filtro.getId());
			}else {
				stmt.setNull(1, Types.INTEGER);	
			}
			if(filtro.getTipo()!=null && !"".equals(filtro.getTipo())) {
				stmt.setString(2, filtro.getTipo());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getFechaDde()!=null) {
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDde().getTime()));
			}else {
				stmt.setNull(3, Types.DATE);	
			}
			if(filtro.getFechaHta()!=null) {
				stmt.setDate(4, new java.sql.Date(filtro.getFechaHta().getTime()));
			}else {
				stmt.setNull(4, Types.DATE);	
			}
			
			if(filtro.getEntidad()!=null && !"".equals(filtro.getEntidad())) {
				stmt.setString(5,filtro.getEntidad());
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getExpediente()!=null && !"".equals(filtro.getExpediente())) {
				stmt.setString(6,filtro.getExpediente());
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
			
			if(filtro.getCaratula()!=null && !"".equals(filtro.getCaratula())) {
				stmt.setString(7,filtro.getCaratula());
			}else {
				stmt.setNull(7,Types.VARCHAR);
			}
			
			if(filtro.getCuit()!=null && !"".equals(filtro.getCuit())) {
				stmt.setString(8,filtro.getCuit());
			}else {
				stmt.setNull(8,Types.VARCHAR);
			}
			
			if(filtro.getSucursal()!=null && !"".equals(filtro.getSucursal())) {
				stmt.setString(9,filtro.getSucursal());
			}else {
				stmt.setNull(9,Types.VARCHAR);
			}
			
			if(filtro.getUltimoEstado()!=null && !"".equals(filtro.getUltimoEstado())) {
				stmt.setString(10,filtro.getUltimoEstado());
			}else {
				stmt.setNull(10,Types.VARCHAR);
			}
				
			stmt.setInt(11, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DemandaJudicial comp =DemandaJudicial.getMapping("", rs);
				demandas.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Demandas",
					e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return demandas;
	}
	
	
	public List<Acta> getActasByIdDemanda(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Acta>actas = new ArrayList<Acta>(); 
		try {
			
			String sql = "{ call judicial.demanda_acta_by_id_demanda(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rst = stmt.executeQuery();
			while (rst.next()) {
				Acta acta = new Acta();
				acta.setId(rst.getInt("acta_id"));
				acta.setNumero(rst.getString("acta_nro"));
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("error al buscar Actas De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return actas;
	}

	
	
	public List<Convenio> getConveniosByIdDemanda(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Convenio>convenios = new ArrayList<Convenio>(); 
		try {
			
			String sql = "{ call judicial.demanda_convenios_by_id_demanda(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rst = stmt.executeQuery();
			while (rst.next()) {
				Convenio convenio = new Convenio();
				convenio.setId(rst.getInt("convenio_id"));
				convenio.setNumero(rst.getString("convenio_nro"));
				convenios.add(convenio);
			}
		} catch (Exception e) {
			_log.error("error al buscar Convenios De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return convenios;
	}
	
	
	
	public List<Cheque> getChequesByIdDemanda(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Cheque>cheques = new ArrayList<Cheque>(); 
		try {
			
			String sql = "{ call judicial.demanda_cheques_by_id_demanda(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Cheque cheque = new Cheque();
				cheque.setCuit(rs.getString("cuit"));
				cheque.setNumero(rs.getBigDecimal("numero"));
				CuentaBancaria cb = new CuentaBancaria();
				cb.setId_cuenta_bcria(rs.getInt("cta_bcria_id"));
				cb.setDescripcion(rs.getString("cta_bcria_desc"));
				Banco b =new Banco(rs.getInt("banco_id"),rs.getString("banco_desc"));
				cb.setBanco(b);
				cheque.setCuentaBancaria(cb);
				cheques.add(cheque);
			}
		} catch (Exception e) {
			_log.error("error al buscar Cheque De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return cheques;
	}

	
	
	public List<Estado> getEstadosByIdDemanda(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Estado>estados = new ArrayList<Estado>(); 
		try {
			
			String sql = "{ call judicial.demanda_estados_by_id_demanda(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Estado estado = new Estado();
				estado.setIdSerial(rs.getInt("id"));
				estado.setId(rs.getString("estado_id"));
				estado.setFecha(rs.getDate("fecha"));
				estado.setObservacionesExternas(rs.getString("observaciones"));
				estados.add(estado);
			}
		} catch (Exception e) {
			_log.error("error al buscar Estados De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return estados;
	}
	
	
	
	
	public Integer updateDemanda(DemandaJudicial demanda, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_demanda = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.update_demanda(?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setInt(1, demanda.getId());
			
			stmt.setString(2, demanda.getEntidad());
			
			if(demanda.getFecha() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (demanda.getFecha().getTime()));
			}
			
			stmt.setString(4, demanda.getTipo());
			
			if(demanda.getExpediente() ==null){
				  stmt.setNull(5, Types.VARCHAR );	
			}else{
				  stmt.setString(5,demanda.getExpediente());
			}
			
			if(demanda.getCaratula() ==null){
				  stmt.setNull(6, Types.VARCHAR );	
			}else{
				  stmt.setString(6,demanda.getCaratula());
			}
			
			
			if(demanda.getJuzgado() ==null){
				  stmt.setNull(7, Types.VARCHAR );	
			}else{
				  stmt.setString(7,demanda.getJuzgado());
			}
			
			stmt.setString(8, demanda.getCuit());
			
			if(demanda.getObservaciones() ==null){
				  stmt.setNull(9, Types.VARCHAR );	
			}else{
				  stmt.setString(9,demanda.getObservaciones());
			}

			if(demanda.getMontoOriginal()==null){
				stmt.setNull(10, Types.DOUBLE);	
			}else{
				stmt.setDouble(10,demanda.getMontoOriginal());
			}
			
			stmt.setString(11, screenName);
			
			
			if(demanda.getSucursal() ==null){
				  stmt.setNull(12, Types.VARCHAR );	
			}else{
				  stmt.setString(12,demanda.getSucursal());
			}
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_demanda = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al update Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_demanda;
	}
	
	
	public long deleteChequesByIdDemanda(int idDemanda,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_cheques_by_id_demanda(?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idDemanda);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Cheques Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}

	
	
	public long deleteEstadosByIdDemanda(int idDemanda,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_estados_by_id_demanda(?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idDemanda);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Estados Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}
	
	
	public long deleteActasByIdDemanda(int idDemanda,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_actas_by_id_demanda(?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idDemanda);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Actas Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}
	
	
	public long deleteConveniosByIdDemanda(int idDemanda,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_convenios_by_id_demanda(?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idDemanda);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Convenios Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}
	
	
	public Integer deleteDemanda(Integer idDemanda,String user,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_demanda(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idDemanda);
			stmt.setString(2, user);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Demanda", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}
	
	
	
	public Integer insertaAsiento(Integer id_demanda,Asiento asiento, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_asiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_asiento(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setInt(1,id_demanda);
			
			if(asiento.getFecha() ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (asiento.getFecha().getTime()));
			}
			
			if(asiento.getDescripcion()==null){
				  stmt.setNull(3, Types.VARCHAR );	
			}else{
				  stmt.setString(3,asiento.getDescripcion());
			}
			
			stmt.setString(4, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_asiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Demanda asiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_asiento;
	}

	
	public Integer insertaAsientoDetalle(Integer demandaId,Integer asientoId,Detalle detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.inserta_demanda_asiento_detalle(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setInt(2,asientoId);
			stmt.setInt(3,detalle.getCuenta().getId());
			stmt.setInt(4,detalle.getPase());
			stmt.setDouble(5, detalle.getDebe().doubleValue());
			stmt.setDouble(6, detalle.getHaber().doubleValue());
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_ = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar demanda - detalle asiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}
	
	
	public List<Asiento> getAsientosByIdDemanda(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Asiento>asientos = new ArrayList<Asiento>(); 
		try {
			
			String sql = "{ call judicial.demanda_asientos_by_id_demanda(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			
			Map <Integer,Asiento> res = new HashMap<Integer,Asiento>();
			
			while (rs.next()) {
			    
			    Detalle detalle = new Detalle();
			    PlanCuentas pc=new PlanCuentas(rs.getInt("cuenta_id"));
			    detalle.setCuenta(pc);
			    detalle.setDebe(new BigDecimal(rs.getDouble("debe")));
			    detalle.setHaber(new BigDecimal(rs.getDouble("haber")));
			    detalle.setPase(rs.getInt("pase"));
			    
			    Asiento a =res.get(rs.getInt("asiento_id"));
			    if(a==null) {
			    	Asiento asiento = new Asiento();
			    	asiento.setDetalle(new ArrayList<Detalle>());
			    	asiento.setFecha(rs.getDate("fecha"));
				    asiento.setDescripcion(rs.getString("descripcion"));
				    asiento.setId(rs.getInt("asiento_id"));
				   
			    	a=asiento;
			    }
			    a.getDetalle().add(detalle);
			    res.put(rs.getInt("asiento_id"), a);
			}
			
			for (Asiento value : res.values()) {
			    asientos.add(value);
			}
			
			
		} catch (Exception e) {
			_log.error("error al buscar Estados De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return asientos;
	}


	public Asiento getAsientosByIdDemanda_IdAsiento(Integer demanda_id,
			Integer asiento_id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		Asiento asiento = new Asiento(); 
		try {
			
			String sql = "{ call judicial.demanda_asiento_by_id_demanda_asiento(?,?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, demanda_id);
			stmt.setInt(2, asiento_id);
			ResultSet rs = stmt.executeQuery();
			
			Map <Integer,Asiento> res = new HashMap<Integer,Asiento>();
			
			while (rs.next()) {
			    
			    Detalle detalle = new Detalle();
			    PlanCuentas pc=new PlanCuentas(rs.getInt("cuenta_id"));
			    detalle.setCuenta(pc);
			    detalle.setDebe(new BigDecimal(rs.getDouble("debe")));
			    detalle.setHaber(new BigDecimal(rs.getDouble("haber")));
			    detalle.setPase(rs.getInt("pase"));
			    
			    Asiento a =res.get(rs.getInt("asiento_id"));
			    if(a==null) {
			    	
			    	asiento.setDetalle(new ArrayList<Detalle>());
			    	asiento.setFecha(rs.getDate("fecha"));
				    asiento.setDescripcion(rs.getString("descripcion"));
				    asiento.setId(rs.getInt("asiento_id"));
				   
			    	a=asiento;
			    }
			    a.getDetalle().add(detalle);
			    res.put(rs.getInt("asiento_id"), a);
			}
			
			
			
		} catch (Exception e) {
			_log.error("error al buscar Estados De demanda Id", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return asiento;
	}

	
	public Integer updateAsiento(Integer id_demanda,Asiento asiento, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_asiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.update_demanda_asiento(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setInt(1,id_demanda);
			stmt.setInt(2,asiento.getId());
			
			if(asiento.getFecha() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (asiento.getFecha().getTime()));
			}
			
			if(asiento.getDescripcion()==null){
				  stmt.setNull(4, Types.VARCHAR );	
			}else{
				  stmt.setString(4,asiento.getDescripcion());
			}
			
			stmt.setString(5, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_asiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error update Demanda asiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_asiento;
	}

	
	public Integer deleteAsientoDetalle(Integer demandaId,Integer asientoId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_demanda_asiento_detalle(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setInt(2,asientoId);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_ = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete demanda - detalle asiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}

	public Integer deleteAsiento(Integer demandaId,Integer asientoId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_ = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call judicial.delete_demanda_asiento(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,demandaId);
			stmt.setInt(2,asientoId);
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_ = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete demanda - asiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_;
	}

	
	public List<Asiento> getAsientosByFechas(String entidad,
			Date dde,Date hta, Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		List<Asiento>asientos = new ArrayList<Asiento>(); 
		try {
			
			String sql = "{ call judicial.demanda_asientos_by_fechas(?,?,?) }";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, entidad);
			stmt.setDate(2, new java.sql.Date (dde.getTime()));
			stmt.setDate(3, new java.sql.Date (hta.getTime()));
			ResultSet rs = stmt.executeQuery();
			
			Map <Integer,Asiento> res = new HashMap<Integer,Asiento>();
			
			while (rs.next()) {
			    
			    Detalle detalle = new Detalle();
			    PlanCuentas pc=new PlanCuentas(rs.getInt("cuenta_id"));
			    detalle.setCuenta(pc);
			    detalle.setDebe(new BigDecimal(rs.getDouble("debe")));
			    detalle.setHaber(new BigDecimal(rs.getDouble("haber")));
			    detalle.setPase(rs.getInt("pase"));
			    
			    Asiento a =res.get(rs.getInt("asiento_id"));
			    if(a==null) {
			    	Asiento asiento = new Asiento();
			    	asiento.setDetalle(new ArrayList<Detalle>());
			    	asiento.setFecha(rs.getDate("fecha"));
				    asiento.setDescripcion(rs.getString("descripcion") + " Expediente " +
			    	rs.getString("expediente")+ " Carátula " +rs.getString("caratula") );
				    asiento.setId(rs.getInt("asiento_id"));
				   
			    	a=asiento;
			    }
			    a.getDetalle().add(detalle);
			    res.put(rs.getInt("asiento_id"), a);
			}
			
			for (Asiento value : res.values()) {
			    asientos.add(value);
			}
			
			
		} catch (Exception e) {
			_log.error("error al buscar Asientos De demandas Fechas", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return asientos;
	}

	
}
