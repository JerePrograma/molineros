package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.WorkflowDefinition;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class CajaChicaServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(CajaChicaServiceImpl.class);

	private static CajaChicaServiceImpl instance = null;

	public static CajaChicaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CajaChicaServiceImpl();
		}
		return instance;
	}

	public long add(CajaChica cajaChica, String screenName,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cajaChica = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.inserta_caja_chica_ospim(?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,cajaChica.getDescripcion());
			stmt.setString(2, cajaChica.getObservaciones());
			stmt.setInt(3, cajaChica.getConcepto().getId());
			stmt.setInt(4,cajaChica.getSeccional().getId());
			stmt.setInt(5,cajaChica.getEstado().getId());
			stmt.setDouble(6,cajaChica.getImporteOriginal() );
			stmt.setString(7,screenName);
			
			if(entidad==WebKeysGlobal.UOMA){
				stmt.setString(8,cajaChica.getEmailsController());
				stmt.setBoolean(9,cajaChica.getPideSeccionalGasto());
				stmt.setInt(10, cajaChica.getConceptoUnicoOP().getId());
			}else{
				stmt.setBoolean(8,cajaChica.getPideSeccionalGasto());
				stmt.setInt(9, cajaChica.getConceptoUnicoOP().getId());
			}
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_cajaChica = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cajaChica;
	}

	
	public long update(CajaChica cajaChica, String screenName,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cajaChica = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim(?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma(?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima(?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setString(2, cajaChica.getObservaciones());
			stmt.setDouble(3,cajaChica.getImporteOriginal() );
			stmt.setString(4,screenName);
			if(entidad==WebKeysGlobal.UOMA){
				stmt.setString(5, cajaChica.getEmailsController());
				stmt.setBoolean(6,cajaChica.getPideSeccionalGasto());
				stmt.setInt(7, cajaChica.getConceptoUnicoOP().getId() );
			}else{
				stmt.setBoolean(5,cajaChica.getPideSeccionalGasto());
				stmt.setInt(6, cajaChica.getConceptoUnicoOP().getId() );
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_cajaChica = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al modificar Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cajaChica;
	}
	
	
	public List<CajaChica> list(String descripcion,int concepto, int estado,int entidad,Connection connectionParameter)
			throws SystemException {
		return list(descripcion,concepto, estado,entidad,0,connectionParameter);
	}
	
	
	public List<CajaChica> list(String descripcion,int concepto, int estado,int entidad,int id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim(?,?,?,?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma(?,?,?,?)}";
			}
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			if (null != descripcion && descripcion.trim().length() > 0) {
				stmt.setString(1, descripcion);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (concepto>0) {
				stmt.setInt(2, concepto);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (estado>0) {
				stmt.setInt(3, estado);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (estado>0) {
				stmt.setInt(3, estado);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (id>0) {
				stmt.setInt(4,id);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<CajaChica>();
			while (rs.next()) {
				CajaChica archivo = CajaChica.getMapping(rs);
				archivo.setEntidad(entidad);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Caja Chica", e);
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
	
	public List<Concepto> getConceptos(Date fecha,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = null;
		try {
			String sql = "{call cajachica.trae_concepto_por_id_maestro(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			if (id>0) {
				stmt.setInt(2, id);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Concepto>();
			while (rs.next()) {
				Concepto archivo = Concepto.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al conceptos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public Double getSaldo(Integer idCaja,Integer entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Double saldo = 0D;
		try {
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
			  sql ="{call cajachica.saldo_caja_chica_ospim(?)}";
			}  
			if(entidad==WebKeysGlobal.UOMA){
				  sql ="{call cajachica.saldo_caja_chica_uoma(?)}";
			} 
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idCaja);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				saldo = rs.getDouble(1);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar el saldo de Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return saldo;
	}
	
	public long updateEstado(CajaChica cajaChica, String screenName,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cajaChica = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_estado(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_estado(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_estado(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setInt(2, cajaChica.getEstado().getId() );
			stmt.setString(3,screenName);
			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_cajaChica = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar estado Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cajaChica;
	}
	
	
	public long addUsuarioHabilitado(int idCajaChica,int entidad,User user,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cajaChica = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.inserta_caja_chica_ospim_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.inserta_caja_chica_uoma_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.inserta_caja_chica_amtima_usuario(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idCajaChica);
			stmt.setLong(2, user.getUserId());
			stmt.setString(3,screenName);

			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_cajaChica = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_cajaChica;
	}

	public List<User> usuariosHabilidados(int idCajaChica,int entidad,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<User> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_usuarios(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_usuarios(?)}";
			}
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idCajaChica);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<User>();
			while (rs.next()) {
				User usuario = UserLocalServiceUtil.getUserById(rs.getLong("user_id"));
				list.add(usuario);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Usuarios de Caja Chica", e);
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
	
	public long deleteUsuarioHabilitado(int idCajaChica,int entidad,User user,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cajaChica = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.elimina_caja_chica_ospim_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.elimina_caja_chica_uoma_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.elimina_caja_chica_amtima_usuario(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idCajaChica);
			stmt.setLong(2, user.getUserId());
			stmt.setString(3,screenName);

			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_cajaChica = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Usuario Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_cajaChica;
	}
	
	public WorkflowDefinition getEstadoActual(int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<WorkflowDefinition> list = null;
		WorkflowDefinition estado= new WorkflowDefinition();		
		
		try {
			String sql = "{call cajachica.trae_ultimo_estado_caja_chica(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (id>0) {
				stmt.setInt(1, id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}ResultSet rs = stmt.executeQuery();
			list = new ArrayList<WorkflowDefinition>();
			while (rs.next()) {
				String estadoDes="";
				for(int i = 0; i < WebKeysCajaChica.ESTADO_CAJA_CHICA.length; i++ ) {
		            if(Integer.parseInt(WebKeysCajaChica.ESTADO_CAJA_CHICA[i][0])==rs.getInt("estado_id")) { 
		               estadoDes=WebKeysCajaChica.ESTADO_CAJA_CHICA[i][1];
		               break;
		            }		   
		        }
				estado = new WorkflowDefinition(rs.getInt("estado_id"),estadoDes,rs.getDate("fecha"));
				list.add(estado);
			}
			if(list.size()>0){
				estado=list.get(0);
			}
		} catch (Exception e) {
			_log.error("Error al traer ultimo estado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return estado;
	}
	
	public long addComprobante(long idCajaChica,ComprobanteCajaChica comprobante ,String screenName,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.inserta_caja_chica_ospim_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.inserta_caja_chica_uoma_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.inserta_caja_chica_amtima_comprobante(?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idCajaChica);
			stmt.setDate(2, new java.sql.Date(comprobante.getFechaEmision().getTime()));
			stmt.setInt(3, comprobante.getConceptos().get(0).getConceptoComprobante().getId());
			stmt.setString(4, comprobante.getAcreedorEmpresa().getCuit());
			stmt.setString(5, comprobante.getTipoComprobante());
			stmt.setInt(6, comprobante.getPtoVenta());
			stmt.setString(7, comprobante.getLetraComprobante());
			stmt.setString(8, comprobante.getNroComprobante());
			stmt.setDouble(9, comprobante.getImporte().doubleValue());
			stmt.setString(10, comprobante.getObservaciones());
			stmt.setString(11,screenName);
			stmt.setString(12, comprobante.getAcreedorEmpresa().getSucursal());
            
			if(entidad==WebKeysGlobal.UOMA ||
					entidad==WebKeysGlobal.OSPIM){
				if(comprobante.getSeccional()!=null){
				   stmt.setInt(13, comprobante.getSeccional().getId());
				}else{
				   stmt.setNull(13, Types.INTEGER);
				}
				
				if(entidad==WebKeysGlobal.UOMA){
					stmt.setInt(14,comprobante.getCentroCosto().getId());
					
					stmt.setDouble(15,comprobante.getGravadoIVA().doubleValue());
					stmt.setDouble(16,comprobante.getTasaIva());
					stmt.setDouble(17,comprobante.getIva().doubleValue());
					stmt.setDouble(18,comprobante.getPercepcionIVA().doubleValue());
					stmt.setDouble(19,comprobante.getPercepcionIIBB().doubleValue());
					stmt.setInt(20, comprobante.getJurisdiccionIIBB());
					stmt.setDouble(21,comprobante.getOtrosTributos().doubleValue());
				}
			}
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public List<ComprobanteCajaChica> comprobantesPendientesRendicion(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_comprobantes_pendientes_rendicion(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobantes_pendientes_rendicion(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica archivo = ComprobanteCajaChica.getMapping(rs,entidad);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	
	public ComprobanteCajaChica comprobantePorId(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		ComprobanteCajaChica archivo= new ComprobanteCajaChica();
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_comprobante_por_id(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobante_por_id(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				archivo = ComprobanteCajaChica.getMapping(rs,entidad);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return archivo;
	}
	
	public long updateComprobante(long idComprobanteCajaChica,ComprobanteCajaChica comprobante ,String screenName,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_comprobante(?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idComprobanteCajaChica);
			stmt.setDate(2, new java.sql.Date(comprobante.getFechaEmision().getTime()));
			stmt.setInt(3, comprobante.getConceptos().get(0).getConceptoComprobante().getId());
			stmt.setString(4, comprobante.getAcreedorEmpresa().getCuit());
			stmt.setString(5, comprobante.getTipoComprobante());
			stmt.setInt(6, comprobante.getPtoVenta());
			stmt.setString(7, comprobante.getLetraComprobante());
			stmt.setString(8, comprobante.getNroComprobante());
			stmt.setDouble(9, comprobante.getImporte().doubleValue());
			stmt.setString(10, comprobante.getObservaciones());
			stmt.setString(11,screenName);
			stmt.setString(12, comprobante.getAcreedorEmpresa().getSucursal());
			
			if(entidad==WebKeysGlobal.UOMA || 
					entidad==WebKeysGlobal.OSPIM){
				if(comprobante.getSeccional()!=null){
				   stmt.setInt(13, comprobante.getSeccional().getId());
				}else{
				   stmt.setNull(13, Types.INTEGER);
				}
				if(entidad==WebKeysGlobal.UOMA){
					stmt.setInt(14,comprobante.getCentroCosto().getId());
					
					stmt.setDouble(15,comprobante.getGravadoIVA().doubleValue());
					stmt.setDouble(16,comprobante.getTasaIva());
					stmt.setDouble(17,comprobante.getIva().doubleValue());
					stmt.setDouble(18,comprobante.getPercepcionIVA().doubleValue());
					stmt.setDouble(19,comprobante.getPercepcionIIBB().doubleValue());
					stmt.setInt(20, comprobante.getJurisdiccionIIBB());
					stmt.setDouble(21,comprobante.getOtrosTributos().doubleValue());
				}
			}
			
            
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}

	
	public long deleteComprobante(long idComprobanteCajaChica,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.delete_caja_chica_ospim_comprobante(?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.delete_caja_chica_uoma_comprobante(?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.delete_caja_chica_amtima_comprobante(?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idComprobanteCajaChica);
			
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_comprobante;
	}
	
	
	public long solicitaReposicionComprobante(long idComprobanteCajaChica,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_comprobante_solicita_reposicion(?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante_solicita_reposicion(?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_comprobante_solicita_reposicion(?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idComprobanteCajaChica);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public List<ComprobanteCajaChica> comprobantesEnviadosARendicion(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_comprobantes_enviados_a_rendicion(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobantes_enviados_a_rendicion(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica archivo = ComprobanteCajaChica.getMapping(rs,entidad);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes enviados a rendicion Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public long procesaRechazoComprobante(long idComprobanteCajaChica,Boolean rechazado,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_comprobante_rechazo_reposicion(?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante_rechazo_reposicion(?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_comprobante_rechazo_reposicion(?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idComprobanteCajaChica);
			stmt.setBoolean(2,rechazado);
			
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al rechazar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public long addOrdenDePagoOspim(CajaChica cajaChica,OrdenPagoOspim op ,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			sql = "{call cajachica.inserta_caja_chica_ospim_orden_de_pago(?,?,?)}";	
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setInt(2, op.getId());
			stmt.setString(3,screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Orden de Pago Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	
	public long ingresaReposicion(CajaChica cajaChica,Date fecha ,int entidad,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_ingreso_reposicion(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_ingreso_reposicion(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_ingreso_reposicion(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3,screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
			
						
		} catch (SQLException e) {
			_log.error("Error al ingresar Orden de Pago Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_comprobante;
	}
	
	public WorkflowDefinition getUltimoEstadoPorId(int id,int idEstado)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<WorkflowDefinition> list = null;
		WorkflowDefinition estado= new WorkflowDefinition();		
		
		try {
			String sql = "{call cajachica.trae_ultimo_estado_caja_chica(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (id>0) {
				stmt.setInt(1, id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (idEstado>0) {
				stmt.setInt(2, idEstado);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<WorkflowDefinition>();
			while (rs.next()) {
				String estadoDes="";
				for(int i = 0; i < WebKeysCajaChica.ESTADO_CAJA_CHICA.length; i++ ) {
		            if(Integer.parseInt(WebKeysCajaChica.ESTADO_CAJA_CHICA[i][0])==rs.getInt("estado_id")) { 
		               estadoDes=WebKeysCajaChica.ESTADO_CAJA_CHICA[i][1];
		               break;
		            }		   
		        }
				estado = new WorkflowDefinition(rs.getInt("estado_id"),estadoDes,rs.getDate("fecha"));
				list.add(estado);
			}
			if(list.size()>0){
				estado=list.get(0);
			}
		} catch (Exception e) {
			_log.error("Error al traer ultimo estado por id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return estado;
	}
	
	public List<ComprobanteCajaChica> reporteCajaChica(int entidad, int id,Date fechaHasta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
			   sql = "{call cajachica.trae_cajas_chicas_ospim_reporte(?,?)}";
			} 
			if(entidad==WebKeysGlobal.UOMA){
				   sql = "{call cajachica.trae_cajas_chicas_uoma_reporte(?,?)}";
			} 
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica comp = new ComprobanteCajaChica();
				
				comp.setConceptos(new ArrayList<ComprobanteConcepto>());
				
				comp.setFechaEmision(rs.getDate("fecha"));
				comp.setImporteComprobante(rs.getBigDecimal("importe_comprobante"));
				comp.setNroComprobante(rs.getString("nro"));
				comp.setTipoComprobante(rs.getString("tipo"));
				comp.setPtoVenta(rs.getInt("ptovta"));
				comp.setLetraComprobante(rs.getString("letra"));
				comp.setObservaciones(rs.getString("observaciones"));
				ComprobanteConcepto concepto = new ComprobanteConcepto(new Concepto(rs.getInt("concepto_id"),rs.getString("descripcion")));
				comp.getConceptos().add(concepto);
				
				
				String cuitAcreedor = rs.getString("cuit_acreedor");
				String sucuAcreedor = rs.getString("sucursal_acreedor");
				String razonSocial = rs.getString("razon_social");
				Empresa acreedor = new Empresa(cuitAcreedor, sucuAcreedor, razonSocial);
				
/*				
		        try {
					acreedor = EmpresaServiceUtil.getEmpleadorCompleto(cuitAcreedor, sucuAcreedor);
				} catch (Exception e1) {
					
				} 
*/
				
		        if(acreedor==null){
					
					if (cuitAcreedor.equals(WebKeysGlobal.CUIT_AMTIMA)
							|| cuitAcreedor.equals(WebKeysGlobal.CUIT_OSPIM)
							|| cuitAcreedor.equals(WebKeysGlobal.CUIT_UOMA)){
						
					   List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales(Integer.parseInt(sucuAcreedor),null, cuitAcreedor);
					   if(seccionales.size()>0){
						   for(Seccional s:seccionales){
						     if(s.getId()==Integer.parseInt(sucuAcreedor)){  
						        acreedor= new Empresa(cuitAcreedor,sucuAcreedor,s.getDescripcion());
						        break;
						     }   
						   }  
					   }
					
					}else{
					  acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
					}
					
				}
		        comp.setAcreedorEmpresa(acreedor);
		        
		        
		        Seccional seccional = new Seccional();
		        try{
		        	seccional.setId_seccional(rs.getInt("seccional_id"));
		        }catch(Exception e){
		        	
		        }
		        try{
		        	seccional.setDescripcion(rs.getString("seccional_descripcion"));
		        }catch(Exception e){
		        	
		        	
		        }
		        comp.setSeccional(seccional);
		        
				list.add(comp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para reporte Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public long procesaApruebaSinOPComprobante(long idComprobanteCajaChica,Boolean aprueba,int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_comprobante_aprueba_reposicion_sin_op(?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante_aprueba_reposicion_sin_op(?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call cajachica.update_caja_chica_amtima_comprobante_aprueba_reposicion_sin_op(?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, (int)idComprobanteCajaChica);
			stmt.setBoolean(2,aprueba);
			
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al Aprobar Comprobante Sin OP Caja Chica", e);
			throw new SystemException(e);
		} finally {			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public Double getUltimoNroComprobante(String tipo,String letra,String cuit,String sucursal,Integer entidad,Integer ptoVta,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Double nro = 0D;
		try {
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
			  sql ="{call cajachica.ultimo_nro_comprobante_caja_chica_ospim(?,?,?,?,?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
			  sql ="{call cajachica.ultimo_nro_comprobante_caja_chica_uoma(?,?,?,?,?)}";	
			}

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuit);
			stmt.setString(2, sucursal);
			stmt.setString(3, tipo);
			stmt.setString(4, letra);
			stmt.setInt(5, ptoVta);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				nro = rs.getDouble(1);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar el ultimo nro comprobante de Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return nro;
	}

	public long addOrdenDePagoUoma(CajaChica cajaChica,OrdenPago op ,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			sql = "{call cajachica.inserta_caja_chica_uoma_orden_de_pago(?,?,?)}";	
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setInt(2, op.getId());
			stmt.setString(3,screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Orden de Pago Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public List<Concepto> getConceptosMaestro(int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = null;
		try {
			String sql = "{call cajachica.trae_concepto_uoma_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1, id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Concepto>();
			while (rs.next()) {
				Concepto archivo = Concepto.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al conceptos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public long updateOrdenDePagoUomaComprobante(CajaChica cajaChica,OrdenPago op,Integer comp ,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_comprobante = 0;
		try {
			
			String sql ="";
			sql = "{call cajachica.update_caja_chica_uoma_orden_de_pago_comprobante(?,?,?,?)}";	
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, cajaChica.getId());
			stmt.setInt(2, op.getId());
			stmt.setInt(3,comp);
			stmt.setString(4,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Orden de Pago Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public List<ComprobanteCajaChica> comprobantesPendientesInforme(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_comprobantes_pendientes_informe_diario(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobantes_pendientes_informe_diario(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica archivo = ComprobanteCajaChica.getMappingInforme(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes para informe Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public long updateComprobantesPendientesInforme(int entidad,int id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		Integer id_comprobante = 0;
		try {
			String sql = "";
			
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante_fecha_informe(?)}";
			}
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al hacer update para informe Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	
	public long updateComprobantesPendientesRecibo(int entidad,int idCajaChica,int idSeccional,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		Integer id_comprobante = 0;
		try {
			String sql = "";
			
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.update_caja_chica_ospim_comprobante_fecha_recibo(?,?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.update_caja_chica_uoma_comprobante_fecha_recibo(?,?)}";
			}
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			if (idCajaChica>0) {
				stmt.setInt(1,idCajaChica);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (idSeccional>0) {
				stmt.setInt(2,idSeccional);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al hacer update para recibo Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}
	

	public Integer  verificaImpresionRecibo(int entidad,int idCajaChica,int idSeccional,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		Integer id_comprobante = 0;
		try {
			String sql = "";
			
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.recibo_impreso_caja_chica_ospim(?,?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.recibo_impreso_caja_chica_uoma(?,?)}";
			}
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (idCajaChica>0) {
				stmt.setInt(1,idCajaChica);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (idSeccional>0) {
				stmt.setInt(2,idSeccional);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_comprobante = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al hacer verificacion impresion recibo Caja Chica", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_comprobante;
	}

	public List<ComprobanteCajaChica> comprobantesPorOP(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobante_por_op(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica archivo = ComprobanteCajaChica.getMapping(rs,entidad);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean verificaComprobante(ComprobanteCajaChica comprobante,int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret=false;
		ComprobanteCajaChica archivo= new ComprobanteCajaChica();
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.existe_comprobante_uoma(?,?,?,?,?,?,?,?,?,?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (comprobante.getPtoVenta() >0) {
				stmt.setInt(1,comprobante.getPtoVenta());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (comprobante.getTipoComprobante() !=null) {
				stmt.setString(2,comprobante.getTipoComprobante());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (comprobante.getNroComprobante() !=null) {
				stmt.setString(3,comprobante.getNroComprobante());
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			if (comprobante.getLetraComprobante() !=null) {
				stmt.setString(4,comprobante.getLetraComprobante());
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (comprobante.getSucuComprobante() >0) {
				stmt.setInt(5,comprobante.getSucuComprobante());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			if (comprobante.getAcreedorEmpresa().getCuit() !=null) {
				stmt.setString(6,comprobante.getAcreedorEmpresa().getCuit());
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			if (comprobante.getAcreedorEmpresa().getSucursal() !=null) {
				stmt.setString(7,comprobante.getAcreedorEmpresa().getSucursal());
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (comprobante.getConceptos().get(0).getConceptoComprobante().getId() >0) {
				stmt.setInt(8,comprobante.getConceptos().get(0).getConceptoComprobante().getId());
			} else {
				stmt.setNull(8, Types.INTEGER);
			}
			
			if (comprobante.getTasaIva()  >0) {
				stmt.setDouble(9,comprobante.getTasaIva());
			} else {
				stmt.setDouble(9,0D);
			}
			
			if (comprobante.getJurisdiccionIIBB() >0) {
				stmt.setInt(10,comprobante.getJurisdiccionIIBB());
			} else {
				stmt.setInt(10,0);
			}
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al verificar Comprobante Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<ComprobanteCajaChica> comprobantesEnviadosARendicionResumido(int entidad,int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteCajaChica> list = null;
		try {
			String sql = "";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call cajachica.trae_cajas_chicas_ospim_comprobantes_enviados_a_rendicion(?)}";
			}
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call cajachica.trae_cajas_chicas_uoma_comprobantes_enviados_a_rendicion(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			if (id>0) {
				stmt.setInt(1,id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteCajaChica>();
			while (rs.next()) {
				ComprobanteCajaChica archivo = ComprobanteCajaChica.getMappingResumido(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes enviados a rendicion Caja Chica", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

}
