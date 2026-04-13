package ar.com.ospim.crm.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import ar.com.ospim.crm.beans.BusquedaContactoFiltro;
import ar.com.ospim.crm.beans.BusquedaDocumLegalFiltro;
import ar.com.ospim.crm.beans.CRMEficacia;
import ar.com.ospim.crm.beans.CRMEstadistica;
import ar.com.ospim.crm.beans.CRMEstadisticaCierre;
import ar.com.ospim.crm.beans.CRMEstadisticaRendimiento;
import ar.com.ospim.crm.beans.CategoriaContacto;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.ContactoCRMTotal;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.beans.DerivacionSeguimiento;
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.crm.beans.DocumentoLegalCRMTotal;
import ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.TipoContacto;
import ar.com.ospim.crm.beans.TipoReclamo;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.UserUtil;

/**
 * 
 * @author sergio
 *
 */

public class CrmServiceImpl {
	
	private static Log logger = LogFactoryUtil.getLog(CrmServiceImpl.class);

	public List<CategoriaContacto> buscarCategoriasContacto()
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<CategoriaContacto> lista = new ArrayList<CategoriaContacto>();
		
		try {
			String sql = "{call crm.buscar_categorias()}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CategoriaContacto cc = CategoriaContacto.getMapping("cate_", rs);		
				lista.add(cc);
			}
		} catch (Exception e) {
			logger.error("error al buscar categorias contacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}

	public  List<TipoContacto> buscarTiposContacto()
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<TipoContacto> lista = new ArrayList<TipoContacto>();
		
		try {
			String sql = "{call crm.buscar_tipos_contacto()}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TipoContacto tc = TipoContacto.getMapping("tipo_", rs);		
				lista.add(tc);
			}
		} catch (Exception e) {
			logger.error("error al buscar tipos contacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}
	
	public  List<MotivoContacto> buscarMotivosContacto()
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<MotivoContacto> lista = new ArrayList<MotivoContacto>();
		
		try {
			String sql = "{call crm.buscar_motivos()}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				MotivoContacto mc = MotivoContacto.getMappingConPredeterm("mot_", rs);		
				lista.add(mc);
			}
		} catch (Exception e) {
			logger.error("error al buscar motivos contacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}
	
	public  int insertaContacto(ContactoCRM contacto, String screenName, String sector, Connection connectionParameter) 
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;		
		
		try {
			if(connectionParameter!=null) {
				con = connectionParameter;
			}else {
				con = ConnectionHelper.getConnection();
			}
			
			String sql = "{call crm.inserta_contacto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			if(contacto.getAfiliado() != null){
				stmt.setString(1, contacto.getAfiliado().getCuil_titular());
				stmt.setInt(2, contacto.getAfiliado().getInte());
			}else{
				stmt.setNull(1, Types.VARCHAR);
				stmt.setNull(2, Types.INTEGER);
			}
			if(contacto.getNoAfiliado() != null){
				stmt.setString(3, contacto.getNoAfiliado().getDocumentoTipo());
				stmt.setString(4, contacto.getNoAfiliado().getDocumentoNumero());
				stmt.setString(5, contacto.getNoAfiliado().getApellido());
				stmt.setString(6, contacto.getNoAfiliado().getNombre());
				stmt.setString(7, contacto.getNoAfiliado().getTelefono());
				stmt.setString(8, contacto.getNoAfiliado().getEmail());
			}else{
				stmt.setNull(3, Types.VARCHAR);
				stmt.setNull(4, Types.VARCHAR);
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.VARCHAR);
				stmt.setNull(7, Types.VARCHAR);
				stmt.setNull(8, Types.VARCHAR);
			}
			stmt.setString(9, contacto.getDescripcion());
			stmt.setString(10, contacto.getEstado().name());
			stmt.setInt(11, contacto.getIdCrmRelacionado());
			stmt.setString(12, contacto.getComentarioCierre());
			stmt.setInt(13, contacto.getMotivo().getId());
			stmt.setInt(14, contacto.getCategoria().getId());
			stmt.setInt(15, contacto.getTipo().getId());
			stmt.setInt(16, contacto.getImportancia());
			stmt.setInt(17, contacto.getIncumplimientoDelContrato());
			stmt.setString(18, contacto.getComentarioAvance());
			stmt.setString(19, screenName);
			stmt.setString(20, sector);
			if(contacto.getContactoSeccional()!=null && contacto.getContactoSeccional().getIdContacto() != 0){
				stmt.setInt(21, contacto.getContactoSeccional().getIdContacto());
			}else{
				stmt.setNull(21, Types.INTEGER);
			}
			if(contacto.getPrestador()!=null){
				stmt.setInt(22, contacto.getPrestador().getId_prestador());
			}else{
				stmt.setNull(22, Types.INTEGER);
			}
			if(contacto.getEmpresa()!=null && StringUtils.checkNotEmpty(contacto.getEmpresa().getCuit())) {
				stmt.setString(23, contacto.getEmpresa().getCuit());
				stmt.setString(24, contacto.getEmpresa().getSucursal());
			}else {
				stmt.setNull(23, Types.VARCHAR);
				stmt.setNull(24, Types.VARCHAR);
			}
			if(contacto.getCompaniero()!=null && StringUtils.checkNotEmpty(contacto.getCompaniero().getUsuario())) {
				stmt.setString(25, contacto.getCompaniero().getEdificio());
				stmt.setString(26, contacto.getCompaniero().getGrupo());
				stmt.setString(27, contacto.getCompaniero().getUsuario());
			}else {
				stmt.setNull(25, Types.VARCHAR);
				stmt.setNull(26, Types.VARCHAR);
				stmt.setNull(27, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar contacto", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return -1;
	}
	
	public int actualizaContacto(ContactoCRM contacto, String screenName, String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call crm.actualiza_contacto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(contacto.getAfiliado() != null){
				stmt.setString(1, contacto.getAfiliado().getCuil_titular());
				stmt.setInt(2, contacto.getAfiliado().getInte());
			}else{
				stmt.setNull(1, Types.VARCHAR);
				stmt.setNull(2, Types.INTEGER);
			}
			if(contacto.getNoAfiliado() != null){
				stmt.setString(3, contacto.getNoAfiliado().getDocumentoTipo());
				stmt.setString(4, contacto.getNoAfiliado().getDocumentoNumero());
				stmt.setString(5, contacto.getNoAfiliado().getApellido());
				stmt.setString(6, contacto.getNoAfiliado().getNombre());
				stmt.setString(7, contacto.getNoAfiliado().getTelefono());
				stmt.setString(8, contacto.getNoAfiliado().getEmail());
			}else{
				stmt.setNull(3, Types.VARCHAR);
				stmt.setNull(4, Types.VARCHAR);
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.VARCHAR);
				stmt.setNull(7, Types.VARCHAR);
				stmt.setNull(8, Types.VARCHAR);
			}
			stmt.setString(9, contacto.getDescripcion());
			stmt.setString(10, contacto.getEstado().name());
			stmt.setInt(11, contacto.getIdCrmRelacionado());
			stmt.setString(12, contacto.getComentarioCierre());
			stmt.setInt(13, contacto.getMotivo().getId());
			stmt.setInt(14, contacto.getCategoria().getId());
			stmt.setInt(15, contacto.getTipo().getId());
			stmt.setInt(16, contacto.getImportancia());
			stmt.setInt(17, contacto.getIncumplimientoDelContrato());
			stmt.setString(18, contacto.getComentarioAvance());
			stmt.setInt(19, contacto.getIdContacto());
			stmt.setString(20, screenName);
			stmt.setString(21, sector);
			
			
			
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				return rs.getInt(1);
//			}
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error al actualizar contacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 1;
	}

	public ContactoCRM buscarContactoCRM(int id) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		try {
			String sql = "{call crm.buscar_contacto_por_id(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				contacto = ContactoCRM.getMapping("con_", rs);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return contacto;
	}
	
	public  ContactoCRM buscarContactoCRMbyIdContacto(int idContacto) throws SystemException {
			
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();

		try {
			String sql = "{call crm.buscar_contacto_por_id_contacto(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idContacto);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
								
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm x idcontacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.keySet(); // mapaContactos.descendingKeySet();
		
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) { // debe ser 1 solo.
			Integer key = (Integer) iterator.next();
			contacto = mapaContactos.get(key);
		}
		
		return contacto;
	}
	
	public  List<ContactoCRM> buscarUltimosContactosCRM(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {
//			String sql = "{call crm.buscar_contactos_por_cuil(?,?)}";
			String sql = "{call crm.buscar_contactos_por_cuil(?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				contacto = ContactoCRM.getMapping("con_", rs);
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	

	public  List<ContactoCRM> buscarUltimosContactosCRM_idreclamo_cuil_inte_solo_asociados(int id_reclamo , String cuilTitular, int inte ) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {			
			String sql = "{call crm.buscar_contactos_por_cuil_e_idreclamo_soloasociados(?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_reclamo );
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				                       
				contacto = ContactoCRM.getMappingConReclamo("con_", rs);								
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm con data reclamo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	

	
	public  List<ContactoCRM> buscarUltimosContactosCRM_idreclamo_cuil_inte(int id_reclamo , String cuilTitular, int inte ) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {			
			String sql = "{call crm.buscar_contactos_por_cuil_e_idreclamo_con_data_reclamos(?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_reclamo );
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				                       
				contacto = ContactoCRM.getMappingConReclamo("con_", rs);								
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm con data reclamo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	
	
	public  List<ContactoCRM> buscarUltimosContactosCRMconDataReclamo(String cuilTitular, int inte ) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {			
			String sql = "{call crm.buscar_contactos_por_cuil_con_data_reclamos(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				                       
				contacto = ContactoCRM.getMappingConReclamo("con_", rs);								
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm con data reclamo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	

	public List<DerivacionNotificacion> insertaDerivacion(int idContacto, EdificioSectorUsuarioLiferay derivacion, String observaciones,
			String screenName, String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DerivacionNotificacion dn = null;
		 List<DerivacionNotificacion> derivNotificaciones = new ArrayList<DerivacionNotificacion>();
		try {
			String sql = "{call crm.inserta_derivacion(?,?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idContacto);
			stmt.setString(2, derivacion.getUsuario());
			stmt.setString(3, derivacion.getGrupo());
			stmt.setString(4, derivacion.getEdificio());
			stmt.setString(5, observaciones);			
			stmt.setString(6, screenName);
			stmt.setString(7, sector);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				 dn = DerivacionNotificacion.getMapping("deriv_", rs);
				 derivNotificaciones.add(dn);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar derivacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return derivNotificaciones;
	}

	public  List<ContactoCRM> buscarHistoricoContactosAfi(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();
		
		try {
			String sql = "{call crm.buscar_contactos_historicos(?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
								
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		
		} catch (Exception e) {
			logger.error("error al buscar historico contactos Afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.descendingKeySet(); //mapaContactos.keySet();
		
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = (Integer) iterator.next();
			contacto = mapaContactos.get(key);
			contactos.add(contacto);
		}
//		Collections.sort(contactos);
		return contactos;
	}
	
	public  List<ContactoCRMTotal> busquedaContactosCRM(BusquedaContactoFiltro filtro, int pagina, User usuario) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRMTotal contacto = null;
		CRMEficacia eficacia = null;
		
		List<ContactoCRMTotal> listaContactos = new ArrayList<ContactoCRMTotal>();
		
		try {
			String sql = "{call crm.busqueda_contactos(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? , ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, filtro.getCuil_titular());
			if(!StringUtils.checkEmpty(filtro.getInte())){
				stmt.setInt(2, Integer.parseInt(filtro.getInte()));
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			if(filtro.getFechaDesde() == null){
				stmt.setNull(3, Types.DATE);
			}else{	
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}
			if(filtro.getFechaHasta() == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}
			stmt.setString(5, filtro.getEstado());
			stmt.setInt(6, filtro.getMotivo());
			stmt.setInt(7, filtro.getCategoria());
			stmt.setInt(8, filtro.getTipo());
			stmt.setInt(9, filtro.getImportancia());
			stmt.setInt(10, filtro.getIncumplimientoContacto());
			stmt.setInt(11, filtro.getIncluirA());
			stmt.setInt(12, filtro.getNro_contacto());
			stmt.setString(13, filtro.getSector());
			stmt.setString(14, filtro.getUsuario());
			stmt.setInt(15, filtro.getIdPlan());
			stmt.setInt(16, filtro.getIdPlanOmint());
			stmt.setInt(17, filtro.getEficaciaConformidad());
			stmt.setInt(18, filtro.getPagina());
			stmt.setInt(19, filtro.getSeccional());
			stmt.setString(20, filtro.getNoAfiliadoDocNumero());
			stmt.setString(21, usuario.getScreenName());
			stmt.setString(22,  String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()));
			if(filtro.getPrestador()!=null) {
			   stmt.setInt(23, filtro.getPrestador());
			} else {
			   stmt.setInt(23,0);	
			}
			
			if(filtro.getCuit()!=null) {
				   stmt.setString(24, filtro.getCuit());
			} else {
				   stmt.setNull(24,Types.VARCHAR);	
			}
			
			if(filtro.getSucursal()!=null) {
				   stmt.setString(25, filtro.getSucursal());
			} else {
				   stmt.setNull(25,Types.VARCHAR);	
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				contacto = ContactoCRMTotal.getMapping("con_", rs);
				eficacia = CRMEficacia.getMapping("efi_", rs);
				contacto.setEficacia(eficacia);
				
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de contactos crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	
	public  List<ContactoCRM> busquedaContactosCRMxls(BusquedaContactoFiltro filtro, User usuario) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {
			logger.debug("busqueda_contactos_xls: " + "desde: " + filtro.getFechaDesde() + " hasta: "+ filtro.getFechaHasta());
			
			String sql = "{call crm.busqueda_contactos_xls(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, filtro.getCuil_titular());
			if(!StringUtils.checkEmpty(filtro.getInte())){
				stmt.setInt(2, Integer.parseInt(filtro.getInte()));
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
//			stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
//			stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			if(filtro.getFechaDesde() == null){
				stmt.setNull(3, Types.DATE);
			}else{	
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}
			if(filtro.getFechaHasta() == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}
			stmt.setString(5, filtro.getEstado());
			stmt.setInt(6, filtro.getMotivo());
			stmt.setInt(7, filtro.getCategoria());
			stmt.setInt(8, filtro.getTipo());
			stmt.setInt(9, filtro.getImportancia());
			stmt.setInt(10, filtro.getIncumplimientoContacto());
			stmt.setInt(11, filtro.getIncluirA());
			stmt.setInt(12, filtro.getNro_contacto());
			stmt.setString(13, filtro.getSector());
			stmt.setString(14, filtro.getUsuario());
			stmt.setInt(15, filtro.getIdPlan());
			stmt.setInt(16, filtro.getIdPlanOmint());
			stmt.setInt(17, filtro.getEficaciaConformidad());
			stmt.setInt(18, filtro.getSeccional() );
			stmt.setString(19, filtro.getNoAfiliadoDocNumero() );
			stmt.setString(20, usuario.getScreenName());
			stmt.setString(21,  String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
//				contacto = ContactoCRM.getMapping("con_", rs);
				contacto = ContactoCRMTotal.getMapping("con_", rs);

				// si es necesario mapear CRMEficacia
				listaContactos.add(contacto);
				
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de contactos crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	
	public  List<CRMEstadistica> estadisticaAgrupada(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<CRMEstadistica> resultados = new ArrayList<CRMEstadistica>();
		CRMEstadistica crmEstad = null;
		
		try {
			String sql = "{call crm.estadistica_agrupada(?, ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				crmEstad = CRMEstadistica.getMapping("", rs);
				resultados.add(crmEstad);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de estadistica agrupada crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return resultados;
	}
	
	public  List<CRMEstadisticaRendimiento> estadisticaRendimiento(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<CRMEstadisticaRendimiento> resultados = new ArrayList<CRMEstadisticaRendimiento>();
		CRMEstadisticaRendimiento crmEstad = null;
		
		try {
			String sql = "{call crm.estadistica_rendimiento(?, ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				crmEstad = CRMEstadisticaRendimiento.getMapping("", rs);
				resultados.add(crmEstad);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de estadistica rendimiento crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return resultados;
	}

	public  List<CRMEstadisticaCierre> estadisticaCierres(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<CRMEstadisticaCierre> resultados = new ArrayList<CRMEstadisticaCierre>();
		CRMEstadisticaCierre crmEstad = null;
		
		try {
			String sql = "{call crm.estadistica_cierres(?, ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				crmEstad = CRMEstadisticaCierre.getMapping("", rs);
				resultados.add(crmEstad);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de estadistica cierres crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return resultados;
	}
	
	public  int insertaEficacia(CRMEficacia eficacia, String screenName, String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call crm.inserta_eficacia(?, ?, ?, ?, ?, ?) }";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, eficacia.getIdContacto());
			stmt.setString(2, eficacia.getContacto_a());
			stmt.setBoolean(3, eficacia.isConforme());
			stmt.setString(4, eficacia.getObservaciones());
			stmt.setString(5, screenName);
			stmt.setString(6, sector);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar eficacia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}
	
	public  DerivacionNotificacion getNotificacionDerivacion(String screenName) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DerivacionNotificacion dn = null;
		
		try {
			String sql = "{call crm.buscar_derivacion_notificacion(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				 dn = DerivacionNotificacion.getMapping("deriv_", rs);
			}
		} catch (SQLException e) {
			logger.error("Error al buscar notificacion derivacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return dn;
	}
	
	public List<DerivacionNotificacion> getNotificacionDerivacionSector(String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DerivacionNotificacion dn = null;
		List<DerivacionNotificacion> derivaciones = new ArrayList<DerivacionNotificacion>();
		try {
			String sql = "{call crm.buscar_derivacion_notificacion_sector(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setString(1, sector);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				 dn = DerivacionNotificacion.getMapping("deriv_", rs);
				 derivaciones.add(dn);
			}
			
		} catch (SQLException e) {
			logger.error("Error al buscar notificacion derivacion sector", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return derivaciones;
	}

	public List<DerivacionSeguimiento> buscarSeguimientoContactoCRMbyIdContacto(int idContacto) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<DerivacionSeguimiento> seguimientos = new ArrayList<DerivacionSeguimiento>();
		DerivacionSeguimiento ds = null;
		
		try {
			String sql = "{call crm.buscar_seguimiento_contacto_por_id_contacto(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idContacto);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				ds = DerivacionSeguimiento.getMapping("seg_", rs) ;
				
				seguimientos.add(ds);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar seguimiento contacto crm x idcontacto", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	
		return seguimientos;
	}
	
	public  List<MotivoContacto> buscarMotivosDocumentoLegal()
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<MotivoContacto> lista = new ArrayList<MotivoContacto>();
		
		try {
			String sql = "{call crm.buscar_motivos_legales()}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				MotivoContacto mc = MotivoContacto.getMapping("mot_", rs);		
				lista.add(mc);
			}
		} catch (Exception e) {
			logger.error("error al buscar motivos doc. legal", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}
	
	public List<TipoReclamo> buscarTiposReclamo()
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<TipoReclamo> lista = new ArrayList<TipoReclamo>();
		
		try {
			String sql = "{call crm.buscar_tipos_reclamo() }";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TipoReclamo tr = TipoReclamo.getMapping("tipo_", rs);		
				lista.add(tr);
			}
		} catch (Exception e) {
			logger.error("error al buscar tipos reclamos doc. legal", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}
	
	public List<DocumentoLegalCRM> buscarUltimosReclamosCRM(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DocumentoLegalCRM reclamo = null;
		List<DocumentoLegalCRM> listaReclamos = new ArrayList<DocumentoLegalCRM>();
		
		try {
			String sql = "{call crm.buscar_reclamos_por_cuil(?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reclamo = DocumentoLegalCRM.getMapping("rec_", rs);
				listaReclamos.add(reclamo);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar reclamos crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaReclamos;
	}
	
	public  int insertaDocumentoLegal(DocumentoLegalCRM reclamo, String screenName, String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call crm.inserta_documento_legal(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(reclamo.getAfiliado() != null){
				stmt.setString(1, reclamo.getAfiliado().getCuil_titular());
				stmt.setInt(2, reclamo.getAfiliado().getInte());
			}else{
				stmt.setNull(1, Types.VARCHAR);
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, reclamo.getDescripcion());
			
			if(reclamo.getNoAfiliado() != null){
				stmt.setString(4, reclamo.getNoAfiliado().getDocumentoTipo());
				stmt.setString(5, reclamo.getNoAfiliado().getDocumentoNumero());
				stmt.setString(6, reclamo.getNoAfiliado().getApellido());
				stmt.setString(7, reclamo.getNoAfiliado().getNombre());
				stmt.setString(8, reclamo.getNoAfiliado().getTelefono());
				stmt.setString(9, reclamo.getNoAfiliado().getEmail());
			}else{
				stmt.setNull(4, Types.VARCHAR);
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.VARCHAR);
				stmt.setNull(7, Types.VARCHAR);
				stmt.setNull(8, Types.VARCHAR);
				stmt.setNull(9, Types.VARCHAR);
			}
			stmt.setInt(10, reclamo.getMotivo().getId());
			stmt.setInt(11, reclamo.getTipo().getId());
			if(reclamo.getFechaNotificacion()!=null){
				stmt.setDate(12, new java.sql.Date(reclamo.getFechaNotificacion().getTime()));
			}else{
				stmt.setNull(12, Types.DATE);
			}
			if(reclamo.getFechaVencimiento()!=null){
				stmt.setDate(13, new java.sql.Date(reclamo.getFechaVencimiento().getTime()));
			}else{
				stmt.setNull(13, Types.DATE);
			}
			if(reclamo.getFechaRespuesta()!=null){
				stmt.setDate(14, new java.sql.Date(reclamo.getFechaRespuesta().getTime()));
			}else{
				stmt.setNull(14, Types.DATE);
			}
			if(reclamo.getFechaAvisoAlEstudio()!=null){
				stmt.setDate(15, new java.sql.Date(reclamo.getFechaAvisoAlEstudio().getTime()));
			}else{
				stmt.setNull(15, Types.DATE);
			}
			if(reclamo.getFechaContactoPSOM()!=null){
				stmt.setDate(16, new java.sql.Date(reclamo.getFechaContactoPSOM().getTime()));
			}else{
				stmt.setNull(16, Types.DATE);
			}
			stmt.setString(17, reclamo.getExpediente());
			stmt.setString(18, reclamo.getResolucion());
			stmt.setString(19, reclamo.getDescripcionSolucion());
			if(reclamo.getTramiteNumero()!=null){
				stmt.setInt(20, reclamo.getTramiteNumero());
			}else{
				stmt.setNull(20, Types.INTEGER);
			}
			stmt.setString(21, reclamo.getRadicacion());
			if(reclamo.getImporteReclamado() != null){
				stmt.setBigDecimal(22, reclamo.getImporteReclamado());
			}else{
				stmt.setNull(22, Types.NUMERIC);
			}
			stmt.setString(23, screenName);
			stmt.setString(24, sector);
			stmt.setString(25, reclamo.getDescripcionEstudio());
			stmt.setBoolean(26, reclamo.isTieneAntecedentes());
			stmt.setBoolean(27, reclamo.isConcluido());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar documento legal", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}
	
	public DocumentoLegalCRM buscarReclamoCRM(int id) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DocumentoLegalCRM documentoLegal = null;
		try {
			String sql = "{call crm.buscar_reclamo_por_id(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				documentoLegal = DocumentoLegalCRM.getMapping("rec_", rs);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar reclamo crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return documentoLegal;
	}
	
	public int actualizaDocumentoLegal(DocumentoLegalCRM reclamo, String screenName, String sector) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call crm.actualiza_documento_legal(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
	
			if(reclamo.getAfiliado() != null){
				stmt.setString(1, reclamo.getAfiliado().getCuil_titular());
				stmt.setInt(2, reclamo.getAfiliado().getInte());
			}else{
				stmt.setNull(1, Types.VARCHAR);
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, reclamo.getDescripcion());
			
			if(reclamo.getNoAfiliado() != null){
				stmt.setString(4, reclamo.getNoAfiliado().getDocumentoTipo());
				stmt.setString(5, reclamo.getNoAfiliado().getDocumentoNumero());
				stmt.setString(6, reclamo.getNoAfiliado().getApellido());
				stmt.setString(7, reclamo.getNoAfiliado().getNombre());
				stmt.setString(8, reclamo.getNoAfiliado().getTelefono());
				stmt.setString(9, reclamo.getNoAfiliado().getEmail());
			}else{
				stmt.setNull(4, Types.VARCHAR);
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.VARCHAR);
				stmt.setNull(7, Types.VARCHAR);
				stmt.setNull(8, Types.VARCHAR);
				stmt.setNull(9, Types.VARCHAR);
			}
			stmt.setInt(10, reclamo.getMotivo().getId());
			stmt.setInt(11, reclamo.getTipo().getId());
			if(reclamo.getFechaNotificacion()!=null){
				stmt.setDate(12, new java.sql.Date(reclamo.getFechaNotificacion().getTime()));
			}else{
				stmt.setNull(12, Types.DATE);
			}
			if(reclamo.getFechaVencimiento()!=null){
				stmt.setDate(13, new java.sql.Date(reclamo.getFechaVencimiento().getTime()));
			}else{
				stmt.setNull(13, Types.DATE);
			}
			if(reclamo.getFechaRespuesta()!=null){
				stmt.setDate(14, new java.sql.Date(reclamo.getFechaRespuesta().getTime()));
			}else{
				stmt.setNull(14, Types.DATE);
			}
			if(reclamo.getFechaAvisoAlEstudio()!=null){
				stmt.setDate(15, new java.sql.Date(reclamo.getFechaAvisoAlEstudio().getTime()));
			}else{
				stmt.setNull(15, Types.DATE);
			}
			if(reclamo.getFechaContactoPSOM()!=null){
				stmt.setDate(16, new java.sql.Date(reclamo.getFechaContactoPSOM().getTime()));
			}else{
				stmt.setNull(16, Types.DATE);
			}
			stmt.setString(17, reclamo.getExpediente());
			stmt.setString(18, reclamo.getResolucion());
			stmt.setString(19, reclamo.getDescripcionSolucion());
			if(reclamo.getTramiteNumero()!=null){
				stmt.setInt(20, reclamo.getTramiteNumero());
			}else{
				stmt.setNull(20, Types.INTEGER);
			}
			stmt.setString(21, reclamo.getRadicacion());
			if(reclamo.getImporteReclamado() != null){
				stmt.setBigDecimal(22, reclamo.getImporteReclamado());
			}else{
				stmt.setNull(22, Types.NUMERIC);
			}
			stmt.setInt(23, reclamo.getId());
			stmt.setString(24, screenName);
			stmt.setString(25, sector);
			stmt.setString(26, reclamo.getDescripcionEstudio());
			stmt.setBoolean(27, reclamo.isTieneAntecedentes());
			stmt.setBoolean(28, reclamo.isConcluido());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error al actualizar reclamo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 1;
	}
	
	public  List<DocumentoLegalCRMTotal> busquedaReclamosCRM(BusquedaDocumLegalFiltro filtro, int pagina) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DocumentoLegalCRMTotal documLegal = null;

		List<DocumentoLegalCRMTotal> listaReclamos = new ArrayList<DocumentoLegalCRMTotal>();
		
		try {
			String sql = "{call crm.busqueda_reclamos(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, filtro.getCuil_titular());
			if(!StringUtils.checkEmpty(filtro.getInte())){
				stmt.setInt(2, Integer.parseInt(filtro.getInte()));
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			if(filtro.getFechaDesde() == null){
				stmt.setNull(3, Types.DATE);
			}else{	
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}
			if(filtro.getFechaHasta() == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}
			stmt.setInt(5, filtro.getMotivo());
			stmt.setInt(6, filtro.getTipoReclamo());
			stmt.setInt(7, filtro.getIncluirA());
			stmt.setInt(8, filtro.getIdDocumLegal());
			stmt.setInt(9, filtro.getIdPlan());
			stmt.setInt(10, filtro.getIdPlanOmint());
			stmt.setBoolean(11, filtro.isTieneAntecedente());
			stmt.setBoolean(12, filtro.isConcluido());
			stmt.setBoolean(13, filtro.isNoConcluido());
			stmt.setInt(14, filtro.getPagina());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				documLegal = DocumentoLegalCRMTotal.getMapping("rec_", rs);
				
				listaReclamos.add(documLegal);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de reclamos crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaReclamos;
	}
	
	public  List<DocumentoLegalCRM> busquedaReclamosCRMxls(BusquedaDocumLegalFiltro filtro) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		DocumentoLegalCRM documLegal = null;
		List<DocumentoLegalCRM> listaReclamos = new ArrayList<DocumentoLegalCRM>();
		
		try {
			String sql = "{call crm.busqueda_reclamos_xls(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, filtro.getCuil_titular());
			if(!StringUtils.checkEmpty(filtro.getInte())){
				stmt.setInt(2, Integer.parseInt(filtro.getInte()));
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			if(filtro.getFechaDesde() == null){
				stmt.setNull(3, Types.DATE);
			}else{	
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}
			if(filtro.getFechaHasta() == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}
			stmt.setInt(5, filtro.getMotivo());
			stmt.setInt(6, filtro.getTipoReclamo());
			stmt.setInt(7, filtro.getIncluirA());
			stmt.setInt(8, filtro.getIdDocumLegal());
			stmt.setInt(9, filtro.getIdPlan());
			stmt.setInt(10, filtro.getIdPlanOmint());
			stmt.setBoolean(11, filtro.isTieneAntecedente());
			stmt.setBoolean(12, filtro.isConcluido());
			stmt.setBoolean(13,filtro.isNoConcluido());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				documLegal = DocumentoLegalCRM.getMapping("rec_", rs);
				
				listaReclamos.add(documLegal);
			}
		
		} catch (Exception e) {
			logger.error("error en la busqueda de reclamos crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaReclamos;
	}
	
    public  List<ContactoCRM> buscarUltimosContactosCRMSeccional(Integer contactoseccional, Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {
			String sql = "{call crm.buscar_contactos_por_contacto_seccional(?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, contactoseccional);
			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				contacto = ContactoCRM.getMapping("con_", rs);
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
    
    public  List<ContactoCRM> buscarUltimosContactosCRM(EdificioSectorUsuarioLiferay companiero, String usuario, Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		List<ContactoCRM> listaContactos = new ArrayList<ContactoCRM>();
		
		try {
			String sql = "{call crm.buscar_contactos_por_companiero(?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, companiero.getEdificio());
			stmt.setString(2, companiero.getGrupo());
			stmt.setString(3, companiero.getUsuario());
			stmt.setString(4, usuario);
			stmt.setDate(5, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(6, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				contacto = ContactoCRM.getMapping("con_", rs);
				contacto.setCompaniero(companiero);
				listaContactos.add(contacto);
			}
		
		} catch (Exception e) {
			logger.error("error al buscar contacto crm", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaContactos;
	}
	
}
