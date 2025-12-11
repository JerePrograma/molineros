package ar.com.ospim.comprobantesPortalProveedores.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteAcompanante;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteHospital;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteIntegracion;
import ar.com.ospim.comprobantesPortalProveedores.beans.Sector;
import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.ConnectionHelper;

public class ComprobanteServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ComprobanteServiceImpl.class);

	private static ComprobanteServiceImpl instance = null;

	public static ComprobanteServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteServiceImpl();
		}
		return instance;
	}

	
	////////////////////////
	////////////////////////
	
	public static void saveComprobanteProveedor(Comprobante comp, String user)
			throws SystemException, ComprobanteExistenteException {
		getInstance().saveComprobanteProveedor(comp, user, null);
	}

	public void saveComprobanteProveedor(Comprobante comp, String user,
			Connection connectionParameter)
			throws SystemException, ComprobanteExistenteException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.add_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
						
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getRazon_soc() : null);
			stmt.setString(3, comp.getTipoComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setString(5, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(6 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			stmt.setDate(7, new java.sql.Date(comp.getFechaEmision().getTime()));
			stmt.setDate(8, new java.sql.Date(comp.getFechaVencimiento().getTime()));
			stmt.setDouble(9,comp.getImporteComprobante().doubleValue());
			stmt.setString(10, comp.getCae());
			stmt.setString(11,comp.getSectorDestino());
			if(comp.getAfiliado()!=null && comp.getAfiliado().getDocu_numero()!=null) {
			   stmt.setString(12,comp.getAfiliado().getDocu_numero());
			}else {
			   stmt.setNull(12, Types.VARCHAR);	   
			}
			if(comp.getCodigoPrestacion()!=null) {
			   stmt.setString(13,comp.getCodigoPrestacion());
			   stmt.setString(14,comp.getDescripcionPrestacion());
			}else {
			   stmt.setNull(13, Types.VARCHAR);
			   stmt.setNull(14, Types.VARCHAR);
			}
			if(comp.getComentario()!=null) {
			   stmt.setString(15,comp.getComentario());
			}else {
			   stmt.setNull(15, Types.VARCHAR);
			}
			
			if(comp.getObservaciones()!=null) {
			   stmt.setString(16,comp.getObservaciones());
			}else {
			   stmt.setNull(16, Types.VARCHAR);
			}
			stmt.setDate(17, new java.sql.Date(comp.getAlta_fecha().getTime()));
			stmt.setString(18,comp.getAlta_usr());
			stmt.setString(19, comp.getEstado());
			stmt.setString(20, comp.getEntidad());	
			stmt.setString(21, user);
			if(comp.getPeriodoPrestacion()!=null) {
			  stmt.setDate(22, new java.sql.Date(comp.getPeriodoPrestacion().getTime()));
			}else {
			  stmt.setNull(22,Types.DATE);	
			}
			stmt.setInt(23,comp.getId());
			
			if(comp.getCantidad()!=null) {
				   stmt.setInt(24,comp.getCantidad());
			}else {
				   stmt.setNull(24, Types.INTEGER);
			}
			
			if(comp.getIdPrestador()!=null) {
				   stmt.setInt(25,comp.getIdPrestador());
			}else {
				   stmt.setNull(25, Types.INTEGER);
			}
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			//_log.error("Error al insertar el comprobante proveedor", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new ComprobanteExistenteException(e);
			} else {
				throw new SystemException(e);
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public List<ClaseBase> getSectoresByUser(String user)
			throws SystemException {
		List<ClaseBase> sectores = new ArrayList<ClaseBase>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.sectores_by_usuario(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ClaseBase comp = new ClaseBase();
				comp.setId(rs.getString("sector"));
				sectores.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar sectores por usuario Comprobantes Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return sectores;
	}

	public List<Comprobante> getLista(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			stmt.setString(21,filtro.getEntidad());
			
			
			
			stmt.setString(22,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(23,filtro.getId());
			}else {
				stmt.setNull(23, Types.INTEGER);	
			}
			stmt.setInt(24, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = new Comprobante();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				
				/*
				if(dni!=null && !"null".equalsIgnoreCase(dni) && !"".equalsIgnoreCase(dni)){
					List<Afiliado> afis=EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(dni,"DU");
					if(afis!=null && !afis.isEmpty()) {
						afi=afis.get(0);
					}
				}
				*/
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	
	public int updateComprobante(Comprobante comp,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_preautorizacion = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call comprobantes.update_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getRazon_soc() : null);
			stmt.setString(3, comp.getTipoComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setString(5, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(6 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			stmt.setDate(7, new java.sql.Date(comp.getFechaEmision().getTime()));
			stmt.setDate(8, new java.sql.Date(comp.getFechaVencimiento().getTime()));
			stmt.setDouble(9,comp.getImporteComprobante().doubleValue());
			stmt.setString(10, comp.getCae());
			stmt.setString(11,comp.getSectorDestino());
			if(comp.getAfiliado()!=null && comp.getAfiliado().getDocu_numero()!=null) {
			   stmt.setString(12,comp.getAfiliado().getDocu_numero());
			}else {
			   stmt.setNull(12, Types.VARCHAR);	   
			}
			if(comp.getCodigoPrestacion()!=null) {
			   stmt.setString(13,comp.getCodigoPrestacion());
			   stmt.setString(14,comp.getDescripcionPrestacion());
			}else {
			   stmt.setNull(13, Types.VARCHAR);
			   stmt.setNull(14, Types.VARCHAR);
			}
			if(comp.getComentario()!=null) {
			   stmt.setString(15,comp.getComentario());
			}else {
			   stmt.setNull(15, Types.VARCHAR);
			}
			
			if(comp.getObservaciones()!=null) {
			   stmt.setString(16,comp.getObservaciones());
			}else {
			   stmt.setNull(16, Types.VARCHAR);
			}
			stmt.setDate(17, new java.sql.Date(comp.getFechaRecepcion().getTime()));
			stmt.setString(18,comp.getAlta_usr());
			stmt.setString(19, comp.getEstado());
			stmt.setString(20, comp.getEntidad());	
			stmt.setString(21, screenName);
			if(comp.getPeriodoPrestacion()!=null) {
			  stmt.setDate(22, new java.sql.Date(comp.getPeriodoPrestacion().getTime()));
			}else {
			  stmt.setNull(22,Types.DATE);	
			}
			stmt.setInt(23,comp.getId());
			
			if(comp.getCantidad()!=null) {
				   stmt.setInt(24,comp.getCantidad());
			}else {
				   stmt.setNull(24, Types.INTEGER);
			}
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_preautorizacion = rs.getInt(1);
			}
			
			
			
		} catch (SQLException e) {
			_log.error("Error al hacer update comprobante proveedores", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_preautorizacion;
	}
	
	public List<Sector> getSectores()
			throws SystemException {
		List<Sector> sectores = new ArrayList<Sector>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.sectores_list()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Sector comp = new Sector();
				comp.setId(rs.getString("sector"));
				sectores.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar sectores Comprobantes Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return sectores;
	}

	
	
	
	public List<User> getUsuariosHabilitadosBySector(Long companyId,String idSector,int entidad,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<User> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call comprobantes.usuarios_by_sector(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				//sql = "{call cajachica.trae_cajas_chicas_uoma_usuarios(?)}";
			}
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,idSector);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<User>();
			while (rs.next()) {
				User usuario = UserLocalServiceUtil.getUserByScreenName(companyId, rs.getString("usuario")) ;
				list.add(usuario);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Usuarios de Sector Portal Comprobantes", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	
	
	public Integer addUsuarioHabilitado(String sector,User usr,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_user = 0;
		try {
			
			String sql ="";
			sql = "{call comprobantes.add_usuario_habilitado(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, sector);
			stmt.setString(2, usr.getScreenName());
			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_user = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Usuario Habilitado Comprobantes Portal Proveedores", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_user;
	}

	public Integer deleteUsuarioHabilitado(String sector,User usr,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_user = 0;
		try {
			
			String sql ="";
			sql = "{call comprobantes.delete_usuario_habilitado(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, sector);
			stmt.setString(2, usr.getScreenName());
			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_user = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Usuario Habilitado Comprobantes Portal Proveedores", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_user;
	}
	
	public List<ComprobanteIntegracion> getListaIntegracion(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteIntegracion> comprobantes = new ArrayList<ComprobanteIntegracion>();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_integracion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getCarpeta()!=null) {
				  stmt.setDate(25, new java.sql.Date(filtro.getCarpeta().getTime()));
			}else {
				  stmt.setNull(25, Types.DATE);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(26, filtro.getPendientes());
			}else {
				stmt.setNull(26, Types.BOOLEAN);
			}
			
			stmt.setInt(27, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteIntegracion comp = new ComprobanteIntegracion();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				/*
				if(dni!=null && !"null".equalsIgnoreCase(dni) && !"".equalsIgnoreCase(dni)){
					List<Afiliado> afis=EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(dni,"DU");
					if(afis!=null && !afis.isEmpty()) {
						afi=afis.get(0);
					}
				}
				*/
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				
				comp.setCud(rs.getString("cud"));
				comp.setCudVto(rs.getDate("cud_vto"));
				comp.setImporteSolicitado(rs.getDouble("importe_solicitado"));
				comp.setDependencia(rs.getString("dependencia"));
				comp.setProvincia(rs.getInt("provincia"));
				comp.setCarpeta(rs.getDate("carpeta"));
				
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	
	public Integer updateCarpetaIntegracion(String ids,Boolean operacion,Date carpeta,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer rta = 0;
		try {
			
			String sql ="";
			sql = "{call comprobantes.update_carpeta_integracion(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, ids);
			stmt.setBoolean(2,operacion);
			if(carpeta!=null) {
			
			  stmt.setDate(3, new java.sql.Date(carpeta.getTime()));
			} else {
			  stmt.setNull(3, Types.DATE);	
			}
			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				rta = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al asignar Carpeta Integracion Comprobantes Portal Proveedores", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return rta;
	}
	
	public int updateComprobanteIntegracion(ComprobanteIntegracion comp,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_preautorizacion = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call comprobantes.update_comprobante_integracion(?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getLetraComprobante());
			stmt.setString(4, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(5 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			
			
			if(comp.getCud()!=null ) {
				   stmt.setString(6,comp.getCud());
			}else {
				   stmt.setNull(6, Types.VARCHAR);	   
			}
			
			if(comp.getCudVto()!=null) {
				  stmt.setDate(7, new java.sql.Date(comp.getCudVto().getTime()));
			}else {
				  stmt.setNull(7,Types.DATE);	
			}
			
			stmt.setDouble(8,comp.getImporteSolicitado().doubleValue());
			
			if(comp.getProvincia()!=null) {
				   stmt.setInt(9,comp.getProvincia());
			}else {
				   stmt.setNull(9, Types.INTEGER);
			}
			
			if(comp.getDependencia()!=null ) {
				   stmt.setString(10,comp.getDependencia());
			}else {
				   stmt.setNull(10, Types.VARCHAR);	   
			}
			
			stmt.setString(11, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_preautorizacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update comprobante proveedores integracion", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_preautorizacion;
	}

	
	public Nomenclador buscaNomencladorSSSByCodigo(String id) {
		Connection con = null;
		CallableStatement stmt = null;
		Nomenclador nomenclador = new Nomenclador();
		try {
			String sql = "{call comprobantes.nomenclador_sss_por_codigo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,id);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				nomenclador.setId_prestacion(rs.getInt("codigo"));
				nomenclador.setDescripcion(rs.getString("descripcion"));
				
				nomenclador.setCantidadDesde(rs.getInt("cantidad_desde"));
				nomenclador.setCantidadHasta(rs.getInt("cantidad_hasta"));
				nomenclador.setCantidadCorrecta(rs.getInt("cantidad_correcta"));
				nomenclador.setIdPrestacionOSPIM(rs.getInt("id_prestacion_ospim"));
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador SSS (Comprobantes)", e);
			
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return nomenclador;
		
	}
	
	
	public List<ComprobanteIntegracion> validaExistenciaComprobante(ComprobanteFiltro filtro) throws SystemException {
		List<ComprobanteIntegracion> comprobantes = new ArrayList<ComprobanteIntegracion>();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");
		Connection con = null;
		CallableStatement stmt = null;
		Integer carp=null;
		try {
			String sql = "{call comprobantes.integracion_valida_duplicado(?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(2, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(3, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setInt(4, filtro.getPtoVenta());		
			}else {
				stmt.setNull(4,Types.INTEGER);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(5,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
						
				
			if(filtro.getCarpeta()!=null) {
				carp=Integer.parseInt(sdf.format(filtro.getCarpeta()));
				stmt.setInt(6,carp);
			}else {
				stmt.setNull(6, Types.INTEGER);	
			}
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteIntegracion comp = new ComprobanteIntegracion();
				comp.setOrdenPagoId(BigDecimal.valueOf(rs.getLong("ordenpago_id")));
				comp.setLiquidacionId(BigDecimal.valueOf(rs.getLong("liquidacion_id")));
				comp.setCabeceraId(rs.getInt("cabecera_id"));
				comp.setLoteSSS(rs.getInt("lote_sss"));
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al validar Comprobantes Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	public Integer eliminarIntegracionPeriodo(Integer periodo,String entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call comprobantes.delete_integracion_ds_carpeta(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,periodo);
			stmt.setString(2,entidad);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al eliminar periodo ds integracion", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	//// ACOMPAÑANTES TERAPEUTICOS////////////////
	
	public List<ComprobanteAcompanante> getListaAcompanantes(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteAcompanante> comprobantes = new ArrayList<ComprobanteAcompanante>();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_acompanantes(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getReclamoId()!=null) {
				  stmt.setInt(25, filtro.getReclamoId().intValue());
			}else {
				  stmt.setNull(25, Types.INTEGER);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(26, filtro.getPendientes());
			}else {
				stmt.setNull(26, Types.BOOLEAN);
			}
			
			stmt.setInt(27, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteAcompanante comp = new ComprobanteAcompanante();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				/*
				if(dni!=null && !"null".equalsIgnoreCase(dni) && !"".equalsIgnoreCase(dni)){
					List<Afiliado> afis=EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(dni,"DU");
					if(afis!=null && !afis.isEmpty()) {
						afi=afis.get(0);
					}
				}
				*/
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				comp.setReclamoId(new BigDecimal(rs.getInt("id_reclamo_prestacional")));
				
				comp.setCantidadAreaMedica(rs.getDouble("cantidad_area_medica"));
				comp.setImporteAreaMedica(rs.getDouble("importe_area_medica")); 
				comp.setCargoOspim(rs.getDouble("cargo_ospim"));
				comp.setCargoTercerizadora(rs.getDouble("cargo_tercerizadora"));
				comp.setCargoTercerizadoraMonotributistas(rs.getDouble("cargo_tercerizadora_monotributo"));
				comp.setReconocidoSSS(rs.getDouble("reconocido_sss"));
				comp.setRecuperable(rs.getInt("recuperable"));
				comp.setObservacionesPrestacion(rs.getString("observacion_prestacion"));
				
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Acompañantes",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	
	public List<AutorizacionPrestacional> getListaAutorizacionesPrestacionales(ComprobanteFiltro filtro) throws SystemException {
		List<AutorizacionPrestacional> autorizaciones = new ArrayList<AutorizacionPrestacional>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_autorizaciones_prestacionales(?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(2,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(3,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(4, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(4, Types.DATE);	
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				AutorizacionPrestacional comp = new AutorizacionPrestacional();
				comp.setNroAutorizacion(rs.getInt("nro_autorizacion"));
				autorizaciones.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Autorizaciones Prestacionales",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return autorizaciones;
	}
	
	
	public int updateComprobanteReclamo(ComprobanteAcompanante comp,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_preautorizacion = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call comprobantes.update_comprobante_reclamo(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getLetraComprobante());
			stmt.setString(4, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(5 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			
			if(comp.getCantidadAreaMedica()!=null ) {
				   stmt.setDouble(6,comp.getCantidadAreaMedica());
			}else {
				   stmt.setNull(6, Types.DOUBLE);	   
			}
			
			if(comp.getImporteAreaMedica()!=null ) {
				   stmt.setDouble(7,comp.getImporteAreaMedica());
			}else {
				   stmt.setNull(7, Types.DOUBLE);	   
			}
			
			if(comp.getCargoOspim()!=null ) {
				   stmt.setDouble(8,comp.getCargoOspim());
			}else {
				   stmt.setNull(8, Types.DOUBLE);	   
			}
			
			if(comp.getCargoTercerizadora()!=null ) {
				   stmt.setDouble(9,comp.getCargoTercerizadora());
			}else {
				   stmt.setNull(9, Types.DOUBLE);	   
			}
			
			if(comp.getCargoTercerizadoraMonotributistas()!=null ) {
				   stmt.setDouble(10,comp.getCargoTercerizadoraMonotributistas());
			}else {
				   stmt.setNull(10, Types.DOUBLE);	   
			}
			
			if(comp.getReconocidoSSS()!=null ) {
				   stmt.setDouble(11,comp.getReconocidoSSS());
			}else {
				   stmt.setNull(11, Types.DOUBLE);	   
			}
			
			if(comp.getRecuperable() !=null ) {
				   stmt.setInt(12,comp.getRecuperable());
			}else {
				   stmt.setNull(12, Types.INTEGER);	   
			}
			
			if(comp.getObservacionesPrestacion()!=null ) {
				   stmt.setString(13,comp.getObservacionesPrestacion());
			}else {
				   stmt.setNull(13, Types.VARCHAR);	   
			}
			
			stmt.setString(14, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_preautorizacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update comprobante proveedores reclamo", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_preautorizacion;
	}
	////FIN ACOMPAÑANTES TERAPEUTICOS /////////
	
	
	///AVISO PAGO -- TRANSFERENCIAS INTERBANKING
	public List<Comprobante> getAvisosPagoByFechaTransferencia(Date fecha) throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.transferencia_interbanking_by_fecha_aviso(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(fecha!=null) {
			  stmt.setDate(1, new java.sql.Date(fecha.getTime())  );
			}else {
			  stmt.setNull(1, Types.DATE);	
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = new Comprobante();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setId(rs.getInt("id"));
				comp.setIdOp(rs.getInt("ordenpago_id"));
				comp.setFechaPrimerPago(rs.getDate("aviso_transferencia"));
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Pagados Portal Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	///Fin AVISO PAGO

	
	
////HOSPITALES////////////////
	
	public List<ComprobanteHospital> getListaHospitales(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteHospital> comprobantes = new ArrayList<ComprobanteHospital>();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_hospitales(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getLiquidacionId()!=null) {
				  stmt.setInt(25, filtro.getLiquidacionId().intValue());
			}else {
				  stmt.setNull(25, Types.INTEGER);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(26, filtro.getPendientes());
			}else {
				stmt.setNull(26, Types.BOOLEAN);
			}
			
			stmt.setInt(27, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteHospital comp = new ComprobanteHospital();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				/*
				if(dni!=null && !"null".equalsIgnoreCase(dni) && !"".equalsIgnoreCase(dni)){
					List<Afiliado> afis=EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(dni,"DU");
					if(afis!=null && !afis.isEmpty()) {
						afi=afis.get(0);
					}
				}
				*/
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				comp.setLiquidacionId(new BigDecimal(rs.getInt("id_liquidacion")));
				try {
				   comp.setIdPrestador(rs.getInt("prestador_id"));
				}catch (Exception e1) {}
				
				/*
				comp.setCantidadAreaMedica(rs.getDouble("cantidad_area_medica"));
				comp.setImporteAreaMedica(rs.getDouble("importe_area_medica")); 
				comp.setCargoOspim(rs.getDouble("cargo_ospim"));
				comp.setCargoTercerizadora(rs.getDouble("cargo_tercerizadora"));
				comp.setCargoTercerizadoraMonotributistas(rs.getDouble("cargo_tercerizadora_monotributo"));
				comp.setReconocidoSSS(rs.getDouble("reconocido_sss"));
				comp.setRecuperable(rs.getInt("recuperable"));
				comp.setObservacionesPrestacion(rs.getString("observacion_prestacion"));
				*/
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Hospitales",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	public int updateComprobanteLiquidacion(ComprobanteHospital comp,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_preautorizacion = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call comprobantes.update_comprobante_liquidacion(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getLetraComprobante());
			stmt.setString(4, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(5 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			
			if(comp.getIdPrestador() !=null ) {
				   stmt.setInt(6,comp.getIdPrestador());
			}else {
				   stmt.setNull(6, Types.INTEGER);	   
			}
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_preautorizacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update comprobante proveedores liquidacion", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_preautorizacion;
	}
	////FIN HOSPITALES /////////
	

	
////FARMACIA////////////////
	
	public List<ComprobanteHospital> getListaFarmacia(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteHospital> comprobantes = new ArrayList<ComprobanteHospital>();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_farmacia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getLiquidacionId()!=null) {
				  stmt.setInt(25, filtro.getLiquidacionId().intValue());
			}else {
				  stmt.setNull(25, Types.INTEGER);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(26, filtro.getPendientes());
			}else {
				stmt.setNull(26, Types.BOOLEAN);
			}
			
			stmt.setInt(27, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteHospital comp = new ComprobanteHospital();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				comp.setLiquidacionId(new BigDecimal(rs.getInt("id_liquidacion")));
				try {
				   comp.setIdPrestador(rs.getInt("prestador_id"));
				}catch (Exception e1) {}
				
				try {
				  comp.setReclamoId(new BigDecimal(rs.getInt("id_reclamo")));
				  String re="";
				  if(rs.getInt("id_estado_reclamo")==3) { //Reclamo cerrado == 3
					  re="CERRADO";
				  }
				  comp.setReclamoEstado(re);
				}catch(Exception e2) {}
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Farmacia",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
	public int updateComprobanteLiquidacionFarmacia(ComprobanteHospital comp,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_preautorizacion = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call comprobantes.update_comprobante_liquidacion(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, comp.getAcreedorEmpresa() != null ? comp.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getLetraComprobante());
			stmt.setString(4, String.format("%05d",comp.getPtoVenta()));
			stmt.setString(5 , String.format("%08d",Integer.parseInt(comp.getNroComprobante())));
			
			if(comp.getIdPrestador() !=null ) {
				   stmt.setInt(6,comp.getIdPrestador());
			}else {
				   stmt.setNull(6, Types.INTEGER);	   
			}
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_preautorizacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update comprobante proveedores liquidacion", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_preautorizacion;
	}
	////FIN FARMACIA /////////

	
	/////// PROVEEDORES //////////
////HOSPITALES////////////////
	
	public List<ComprobanteHospital> getListaProveedores(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteHospital> comprobantes = new ArrayList<ComprobanteHospital>();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_proveedores(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(25, filtro.getPendientes());
			}else {
				stmt.setNull(25, Types.BOOLEAN);
			}
			
			stmt.setInt(26, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteHospital comp = new ComprobanteHospital();
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				comp.setGenerado(rs.getBoolean("generado"));
				comp.setIdOp(rs.getInt("ordenpago_id"));
				try {
				   comp.setIdPrestador(rs.getInt("prestador_id"));
				}catch (Exception e1) {}
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Proveedores",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}

	
	/////// FIN PROVEEDORES ///////
	
	
////GERENCIADORAS////////////////
	
	public List<ComprobanteHospital> getListaGerenciadoras(ComprobanteFiltro filtro,Integer pagina) throws SystemException {
		List<ComprobanteHospital> comprobantes = new ArrayList<ComprobanteHospital>();
		Boolean evalua=true;
		String cadenaReclamo="";
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call comprobantes.traer_comprobantes_gerenciadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getCuit()!=null && 
					!"".equals(filtro.getAcreedorEmpresa().getCuit())) {
				stmt.setString(1, filtro.getAcreedorEmpresa().getCuit());		
			}else {
				stmt.setNull(1,Types.VARCHAR);
			}
		
			if(filtro.getAcreedorEmpresa()!=null && filtro.getAcreedorEmpresa().getRazon_soc()!=null &&
					!"".equals(filtro.getAcreedorEmpresa().getRazon_soc())) {
				stmt.setString(2, filtro.getAcreedorEmpresa().getRazon_soc());		
			}else {
				stmt.setNull(2,Types.VARCHAR);
			}
			
			if(filtro.getTipoComprobante()!=null && !"".equals(filtro.getTipoComprobante())) {
				stmt.setString(3, filtro.getTipoComprobante());		
			}else {
				stmt.setNull(3,Types.VARCHAR);
			}
			
			if(filtro.getLetraComprobante()!=null && !"".equals(filtro.getLetraComprobante()) ) {
				stmt.setString(4, filtro.getLetraComprobante());		
			}else {
				stmt.setNull(4,Types.VARCHAR);
			}
						
			if(filtro.getPtoVenta()!=0 ) {
				stmt.setString(5, String.format("%05d",filtro.getPtoVenta()));		
			}else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(filtro.getNroComprobante()!=null && !"".equals(filtro.getNroComprobante())) {
				stmt.setString(6,filtro.getNroComprobante() );		
			}else {
				stmt.setNull(6,Types.VARCHAR);
			}
						
			if(filtro.getFechaEmisionDesde()!=null) {
			  stmt.setDate(7, new java.sql.Date(filtro.getFechaEmisionDesde().getTime()));
			}else {
			  stmt.setNull(7, Types.DATE);	
			}
			if(filtro.getFechaEmisionHasta()!=null) {
			  stmt.setDate(8, new java.sql.Date(filtro.getFechaEmisionHasta().getTime()));
			}else {
			  stmt.setNull(8, Types.DATE);	
			}
		
			if(filtro.getFechaRecepcionDesde()!=null) {
			  stmt.setDate(9, new java.sql.Date(filtro.getFechaRecepcionDesde().getTime()));
			}else {
			  stmt.setNull(9, Types.DATE);	
			}
			if(filtro.getFechaRecepcionHasta()!=null) {
			  stmt.setDate(10, new java.sql.Date(filtro.getFechaRecepcionHasta().getTime()));
			}else {
			  stmt.setNull(10, Types.DATE);	
			}
			
			if(filtro.getAfiliado()!=null && filtro.getAfiliado().getDocu_numero()!=null && !"".equals(filtro.getAfiliado().getDocu_numero())) {
				stmt.setString(11,filtro.getAfiliado().getDocu_numero());
			}else {
				stmt.setNull(11,Types.VARCHAR);
			}
			
			if(filtro.getSectorDestino()!=null ) {
				stmt.setString(12,filtro.getSectorDestino());
			}else {
				stmt.setNull(12,Types.VARCHAR);
			}
			
			if(filtro.getEstado()!=null && !"".equals(filtro.getEstado())) {
				stmt.setString(13,filtro.getEstado());
			}else {
				stmt.setNull(13,Types.VARCHAR);
			}
			
			if(filtro.getCodigoPrestacion()!=null && !"".equals(filtro.getCodigoPrestacion())) {
				stmt.setString(14,filtro.getCodigoPrestacion());
			}else {
				stmt.setNull(14,Types.VARCHAR);
			}
			
			if(filtro.getDescripcionPrestacion()!=null && !"".equals(filtro.getDescripcionPrestacion())) {
				stmt.setString(15,filtro.getDescripcionPrestacion());
			}else {
				stmt.setNull(15,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoCodigo()!=null  && !"".equals(filtro.getMedicamentoCodigo()) ) {
				stmt.setString(16,filtro.getMedicamentoCodigo());
			}else {
				stmt.setNull(16,Types.VARCHAR);
			}
			
			if(filtro.getMedicamentoDescripcion()!=null  && !"".equals(filtro.getMedicamentoDescripcion())) {
				stmt.setString(17,filtro.getMedicamentoDescripcion());
			}else {
				stmt.setNull(17,Types.VARCHAR);
			}
			
			if(filtro.getPeriodoPrestacion()!=null) {
				  stmt.setDate(18, new java.sql.Date(filtro.getPeriodoPrestacion().getTime()));
			}else {
				  stmt.setNull(18, Types.DATE);	
			}
			
			if(filtro.getPeriodoHasta()!=null) {
				  stmt.setDate(19, new java.sql.Date(filtro.getPeriodoHasta().getTime()));
			}else {
				  stmt.setNull(19, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoDesde()!=null) {
				  stmt.setDate(20, new java.sql.Date(filtro.getFechaVencimientoDesde().getTime()));
			}else {
				  stmt.setNull(20, Types.DATE);	
			}
			
			if(filtro.getFechaVencimientoHasta()!=null) {
				  stmt.setDate(21, new java.sql.Date(filtro.getFechaVencimientoHasta().getTime()));
			}else {
				  stmt.setNull(21, Types.DATE);	
			}
			
			stmt.setString(22,filtro.getEntidad());
			
			
			
			stmt.setString(23,filtro.getAlta_usr());
			
			if(filtro.getId()!=null) {
				stmt.setInt(24,filtro.getId());
			}else {
				stmt.setNull(24, Types.INTEGER);	
			}
			
			if(filtro.getLiquidacionId()!=null) {
				  stmt.setInt(25, filtro.getLiquidacionId().intValue());
			}else {
				  stmt.setNull(25, Types.INTEGER);	
			}
			
			if(filtro.getPendientes()!=null) {
				stmt.setBoolean(26, filtro.getPendientes());
			}else {
				stmt.setNull(26, Types.BOOLEAN);
			}
			
			stmt.setInt(27, pagina);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteHospital comp = new ComprobanteHospital(new ArrayList<ReclamoPrestacional>());
				
				Empresa emp = new Empresa(rs.getString("cuit"),null,rs.getString("razon_social"));
				comp.setAcreedorEmpresa(emp);
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setPtoVenta(Integer.parseInt(rs.getString("punto_venta")));
				comp.setNroComprobante(rs.getString("numero"));
				comp.setFechaEmision(rs.getDate("emision"));
				comp.setFechaVencimiento(rs.getDate("vencimiento"));
				comp.setImporteComprobante(rs.getBigDecimal("importe"));
				comp.setCae(rs.getString("cae"));
				comp.setSectorDestino(rs.getString("area_liquidacion"));
				Afiliado afi = new Afiliado();
				String dni =rs.getString("dni");
				afi.setDocu_numero(dni);
				afi.setCuil_titular(rs.getString("cuil_titular"));
				afi.setInte(rs.getInt("inte"));
				afi.setCuil(rs.getString("cuil"));
				
				comp.setAfiliado(afi);
				comp.setCodigoPrestacion(rs.getString("prestacion_codigo"));
				comp.setDescripcionPrestacion(rs.getString("prestacion_descripcion"));
				comp.setComentario(rs.getString("comentario"));
				comp.setObservaciones(rs.getString("observacion"));
				comp.setEstado(rs.getString("estado"));
				comp.setFechaRecepcion(rs.getDate("alta_fecha_prv"));
				comp.setPeriodoPrestacion(rs.getDate("prestacion_periodo"));
				comp.setId(rs.getInt("id_externo"));
				comp.setTotalRegistros(rs.getInt("total_registros"));
				comp.setCantidad(rs.getInt("cantidad"));
				comp.setLiquidacionId(new BigDecimal(rs.getInt("id_liquidacion")));
				try {
				   comp.setIdPrestador(rs.getInt("prestador_id"));
				}catch (Exception e1) {}
				
				try {
				  cadenaReclamo=rs.getString("reclamo");	
				  String[] vReclamos =cadenaReclamo.split(";");
				  Boolean cerrado=true;
				  for(int i=0;i<=vReclamos.length-1;i++) {
					  String[] strReclamo=vReclamos[i].split("--");
					  ReclamoPrestacional r= new ReclamoPrestacional();
					  r.setId(Integer.parseInt(strReclamo[0].trim()));
					  r.setEstado(Integer.parseInt(strReclamo[1].trim()));
					  if(r.getEstado()!=3) cerrado=false;
					  comp.getReclamos().add(r);
				  }
				  String re="";
				  if(cerrado) { //Todos los Reclamos cerrados == 3
					  re="CERRADO";
				  }
				  comp.setReclamoEstado(re);
				  
				}catch(Exception e2) {}
				
				comprobantes.add(comp);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Portal Proveedores - Gerenciadoras",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}

	
//FIN GERENCIADORAS	
	
}
