package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.beans.SeccionalExcel;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.ContactoElectronico.Tipo;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.seccional.beans.GestionSeccional;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.CentroCosto;

/**
 * <a href="AporteServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class SeccionalServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(SeccionalServiceImpl.class);
	
	public List<Seccional> buscarSeccionales(Integer codigo,String descripcion,	Integer provincia) throws Exception {
		List<Seccional> listaResultado = new ArrayList<Seccional>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccionales(?,?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			stmt.setString(2, descripcion);
			
			if (provincia!=null && provincia>0) {
			  stmt.setInt(3,provincia);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Seccional seccional = new Seccional(rs.getInt("id"),rs.getString("descripcion"));
				
				seccional.setCBU(rs.getString("cbu"));
				seccional.setCheque_a_la_orden(rs.getString("cheque_a_la_orden"));
				seccional.setContacto(rs.getString("contacto"));
				seccional.setDestino(rs.getString("destino_corr"));
				seccional.setId_domicilio(rs.getInt("id_domicilio"));
				seccional.setObservaciones(rs.getString("observaciones"));
				seccional.setTipo(rs.getString("tipo"));
				seccional.setVigen_fecha(rs.getDate("vigen_fecha"));
				seccional.setAmtima(rs.getBoolean("amtima"));
				seccional.setOspim(rs.getBoolean("ospim"));
				seccional.setUoma(rs.getBoolean("uoma"));
				seccional.setImaginaria(rs.getInt("imaginaria"));
				seccional.setId_delegacion_sss(rs.getInt("id_delegacion_sss"));
				seccional.setDescripcion_amtima(rs.getString("descripcion_amtima"));
				seccional.setDescripcion_uoma(rs.getString("descripcion_uoma"));				
				seccional.setHorarioAtencion(rs.getString("horario_atencion"));
				seccional.setNroTarjetaRecargable(rs.getString("nro_tarjeta_recargable"));
				Provincia prv = new Provincia();
				prv.setDescripcion(rs.getString("provincia"));
				prv.setId(rs.getInt("provincia_id"));
				Domicilio domicilio = new Domicilio();
				domicilio.setProvincia(prv);
				seccional.setDomicilio(domicilio);
				
				
				domicilio.setId_domicilio(rs.getInt("id_domicilio"));
				domicilio.setDomi_tipo(rs.getString("domi_tipo"));
				domicilio.setCalle(rs.getString("calle"));
				domicilio.setPiso(rs.getString("piso"));
				domicilio.setDepto(rs.getString("depto"));
				domicilio.setOficina(rs.getString("oficina"));
				domicilio.setPostal_codi(rs.getString("postal_codi"));
				domicilio.setBarrio(rs.getString("barrio"));
				domicilio.setObservaciones(rs.getString("observaciones_dom"));
				domicilio.setDomi_val(rs.getString("domi_val"));
				domicilio.setNumero(rs.getString("numero"));
				Localidad loca=new Localidad();
				loca.setId(rs.getInt("localidad_id"));
				loca.setDescripcion(rs.getString("localidad"));
				domicilio.setLocalidad(loca);
				seccional.setPagoSeccional(rs.getBoolean("pago_seccional"));
				
				
				
				listaResultado.add(seccional);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaResultado;
	}


	public List<Delegacion> buscarDelegacionesSeccional(Integer codigo) throws Exception {
		List<Delegacion> listaResultado = new ArrayList<Delegacion>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_delegaciones(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Delegacion delegacion = new Delegacion(rs.getInt("id"),rs.getString("descripcion"),rs.getInt("libro"),rs.getInt("rubrica"),
						rs.getInt("tomo"),rs.getBoolean("sedecentral"), new Date());
				listaResultado.add(delegacion);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaResultado;
	}

	
	public List<ContactoElectronico> buscarContactosSeccional(Integer codigo) throws Exception {
		List<ContactoElectronico> listaResultado = new ArrayList<ContactoElectronico>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_contactos_electronicos(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ContactoElectronico contacto = new ContactoElectronico(rs.getInt("id"), Tipo.getTipoById(rs.getString("tipo")), rs.getString("contacto"),
						rs.getString("observaciones"));  
				listaResultado.add(contacto);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaResultado;
	}

	public List<Telefono> buscarTelefonosSeccional(Integer codigo) throws Exception {
		List<Telefono> listaResultado = new ArrayList<Telefono>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_telefonos(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Telefono contacto = new Telefono(rs.getInt("id"), rs.getString("tipo"), rs.getString("codigo_pais"),rs.getString("codigo_area"),
						rs.getString("numero"),rs.getString("extension") ,rs.getString("observaciones"));  
				listaResultado.add(contacto);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaResultado;
	}
	
	public int update(Seccional seccional,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seccional = seccional.getId();
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call update_seccional(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seccional.getId());
			
			stmt.setString(2,seccional.getCBU());
			
			stmt.setString(3,seccional.getCheque_a_la_orden());
			
			stmt.setString(4, seccional.getContacto());
			
			stmt.setString(5,seccional.getDescripcion());
			
			stmt.setString(6,seccional.getDescripcion_amtima());
			
			stmt.setString(7,seccional.getDescripcion_uoma());
			
			stmt.setString(8,seccional.getDestinoCorrespondencia());
			
			stmt.setString(9,seccional.getObservaciones());
			
			stmt.setString(10, seccional.getTipo());
			
			stmt.setInt(11, seccional.getImaginaria());
			
			if(seccional.getImaginaria()==null){
				stmt.setNull(11, Types.INTEGER);
			}else{
				stmt.setInt(11, seccional.getImaginaria());
			}
			
			if(seccional.getVigen_fecha() ==null){
				  stmt.setNull(12, Types.DATE );	
			}else{
				  stmt.setDate(12, new java.sql.Date (seccional.getVigen_fecha().getTime()));
			}  
			
			stmt.setBoolean(13,seccional.isAmtima());
			stmt.setBoolean(14,seccional.isOspim());
			stmt.setBoolean(15,seccional.isUoma());
			stmt.setString(16, screenName);
			
			stmt.setString(17, seccional.getHorarioAtencion() );
			
			if(seccional.getNroTarjetaRecargable() ==null){
				stmt.setNull(18, Types.VARCHAR);
			}else{
				stmt.setString(18, seccional.getNroTarjetaRecargable());
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seccional = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seccional;
	}

	public int updateDomicilio(Seccional seccional,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seccional = seccional.getId();
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			Domicilio domicilio=seccional.getDomicilio();
			
			String sql = "{call update_seccional_domicilio(?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,domicilio.getId_domicilio());
			stmt.setInt(2,domicilio.getProvinciaId());
			stmt.setInt(3,domicilio.getLocalidadId());
			stmt.setString(4,domicilio.getCalle());
			stmt.setString(5,domicilio.getNumero());
			stmt.setString(6,domicilio.getPiso());
			stmt.setString(7,domicilio.getDepto());
			stmt.setString(8,domicilio.getPostal_codi());
			stmt.setString(9,domicilio.getBarrio());
			stmt.setString(10, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seccional = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update domicilio seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seccional;
	}

	
	public int addDomicilio(Seccional seccional,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seccional = seccional.getId();
		Integer id_domicilio=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			Domicilio domicilio=seccional.getDomicilio();
			
			String sql = "{call add_seccional_domicilio(?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,id_seccional);
			stmt.setInt(2,domicilio.getProvinciaId());
			stmt.setInt(3,domicilio.getLocalidadId());
			stmt.setString(4,domicilio.getCalle());
			stmt.setString(5,domicilio.getNumero());
			stmt.setString(6,domicilio.getPiso());
			stmt.setString(7,domicilio.getDepto());
			stmt.setString(8,domicilio.getPostal_codi());
			stmt.setString(9,domicilio.getBarrio());
			stmt.setString(10, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_domicilio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add domicilio seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_domicilio;
	}

	
	public int addContacto(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			
			if (contacto != null && contacto.getTelefono() != null && !contacto.getTelefono().getNumero().trim().equals("")) {
				
				sql = "{call add_seccional_telefono(?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,id_seccional);
				stmt.setString(2,contacto.getTelefono().getCodigoPais());
				stmt.setString(3,contacto.getTelefono().getCodigoArea());
				stmt.setString(4, contacto.getTelefono().getNumero());
				stmt.setString(5,contacto.getTelefono().getExtension());
				stmt.setString(6, contacto.getTelefono().getObservaciones());
				stmt.setString(7, screenName);	
				
			} else if(null!=contacto && null!=contacto.getContacto()) {
				
				sql = "{call add_seccional_contacto_electronico(?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,id_seccional);
				stmt.setString(2, contacto.getContacto().getTipo().getId());
				stmt.setString(3, contacto.getContacto().getContacto());
				stmt.setString(4,contacto.getContacto().getObservaciones());
				stmt.setString(5, screenName);
				
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add contacto seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}


	public int updateContacto(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			
			if (contacto != null && contacto.getTelefono() != null && !contacto.getTelefono().getNumero().trim().equals("")) {
				
				sql = "{call update_seccional_telefono(?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,contacto.getTelefono().getId());
				stmt.setString(2,contacto.getTelefono().getCodigoPais());
				stmt.setString(3,contacto.getTelefono().getCodigoArea());
				stmt.setString(4, contacto.getTelefono().getNumero());
				stmt.setString(5,contacto.getTelefono().getExtension());
				stmt.setString(6, contacto.getTelefono().getObservaciones());
				stmt.setString(7, screenName);	
				
			} else if(null!=contacto && null!=contacto.getContacto()) {
				
				sql = "{call update_seccional_contacto_electronico(?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,contacto.getContacto().getId());
				stmt.setString(2, contacto.getContacto().getTipo().getId());
				stmt.setString(3, contacto.getContacto().getContacto());
				stmt.setString(4,contacto.getContacto().getObservaciones());
				stmt.setString(5, screenName);
				
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add contacto seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}


	public int deleteContacto(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			
			if (contacto != null && contacto.getTelefono() != null && !contacto.getTelefono().getNumero().trim().equals("")) {
				
				sql = "{call delete_seccional_telefono(?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,id_seccional);
				stmt.setInt(2,contacto.getTelefono().getId());
				
			} else if(null!=contacto && null!=contacto.getContacto()) {
				
				sql = "{call delete_seccional_contacto_electronico(?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,id_seccional);
				stmt.setInt(2,contacto.getContacto().getId());
				
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer delete contacto seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}
	
	
	public Integer proximoNroSeccional(int id_provincia,String tipo,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_seccional=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			sql = "{call proximo_nro_seccional(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id_provincia);
			stmt.setString(2,tipo);
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seccional = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer proximo nro seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seccional;
	}

	
	public int addDelegacion(int id_seccional,Delegacion delegacion,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_delegacion=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			
				
			sql = "{call add_seccional_delegacion(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id_seccional);
			stmt.setInt(2,delegacion.getId());
				
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_delegacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add delegacion seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_delegacion;
	}

	public int deleteDelegacion(int id_seccional,Delegacion delegacion,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_delegacion=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
				
			sql = "{call delete_seccional_delegacion(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id_seccional);
			stmt.setInt(2,delegacion.getId());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_delegacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer delete delegacion seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_delegacion;
	}
	
	
	public List<SeccionalExcel> getListaSeccionalesContactos (int provincia)
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeccionalExcel> list = null;
		try {
			String sql ="";			
			sql = "{call reporte_seccionales_telefonos_emails(?)}";			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
						
			if (provincia>0 ) {
				stmt.setInt (1,provincia );
			} else {
				stmt.setNull(1, Types.INTEGER );
			}			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeccionalExcel>();
			while (rs.next()) {
				SeccionalExcel  archivo = SeccionalExcel.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel de Seccionales, reporte contactos seccionales", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<SeccionalExcel> getListaSeccionales (int provincia)
	
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeccionalExcel> list = null;
		try {
			String sql ="";			
			sql = "{call reporte_seccionales(?)}";			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
						
			if (provincia>0 ) {
				stmt.setInt (1,provincia );
			} else {
				stmt.setNull(1, Types.INTEGER );
			}			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeccionalExcel>();
			while (rs.next()) {
				SeccionalExcel  archivo = SeccionalExcel.getMappingSeccional(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error en la busqueda de registros de exportacion a excel de Seccionales reporte seccionales", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}


	
	public boolean existeNumeroSeccional(Integer idSeccional)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call existe_numero_seccional(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idSeccional);
			
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = rs.getBoolean(1);
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar numero Seccional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	
	public int add(Seccional seccional,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seccional = seccional.getId();
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call add_seccional(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seccional.getId());
			
			stmt.setString(2,seccional.getCBU());
			
			stmt.setString(3,seccional.getCheque_a_la_orden());
			
			stmt.setString(4, seccional.getContacto());
			
			stmt.setString(5,seccional.getDescripcion());
			
			stmt.setString(6,seccional.getDescripcion_amtima());
			
			stmt.setString(7,seccional.getDescripcion_uoma());
			
			stmt.setString(8,seccional.getDestinoCorrespondencia());
			
			stmt.setString(9,seccional.getObservaciones());
			
			stmt.setString(10, seccional.getTipo());
			
//			stmt.setInt(11, seccional.getImaginaria());
			if(seccional.getImaginaria()==null){
				stmt.setNull(11, Types.INTEGER);
			}else{
				stmt.setInt(11, seccional.getImaginaria());
			}
			
			if(seccional.getVigen_fecha() ==null){
				Date d = new Date();
				stmt.setDate(12, new java.sql.Date (d.getTime()));
			}else{
				  stmt.setDate(12, new java.sql.Date (seccional.getVigen_fecha().getTime()));
			}  
			
			stmt.setBoolean(13,seccional.isAmtima());
			stmt.setBoolean(14,seccional.isOspim());
			stmt.setBoolean(15,seccional.isUoma());
			
			
			stmt.setInt(16, seccional.getDomicilio().getId_domicilio());
			
			stmt.setString(17, screenName);
			stmt.setString(18, seccional.getHorarioAtencion() );
			
			if(seccional.getNroTarjetaRecargable()==null){
				stmt.setNull(19, Types.VARCHAR);
			}else{
			    stmt.setString(19,seccional.getNroTarjetaRecargable());
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seccional = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seccional;
	}

	
	public List<ClaseBase> traeCargosSeccional() throws Exception {
		List<ClaseBase> listaResultado = new ArrayList<ClaseBase>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call trae_seccional_cargos()}";
			stmt = con.prepareCall(sqlList.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ClaseBase cargo = new ClaseBase(rs.getString("id"),rs.getString("descripcion"));
				listaResultado.add(cargo);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaResultado;
	}
	
	
	public List<Contacto> buscarContactosPersonalesSeccional(Integer codigo) throws Exception {
		List<Contacto> listaResultado = new ArrayList<Contacto>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_contactos_personales(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Contacto contacto = new Contacto();
				contacto.setCargo(rs.getString("cargo"));
				contacto.setCargoDescripcion(rs.getString("cargo_descripcion"));
				contacto.setNombreApe(rs.getString("nombre"));
				Telefono telefono = new Telefono();
				telefono.setNumero(rs.getString("telefono_numero"));
				telefono.setTipo(rs.getString("telefono_tipo"));
				telefono.setId(rs.getInt("id"));
				telefono.setCodigoArea(rs.getString("cod_area"));
				contacto.setTelefono(telefono);
				listaResultado.add(contacto);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaResultado;
	}
	
	public int addContactoPersonal(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			
			sql = "{call add_seccional_contacto_personal(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id_seccional);
			stmt.setString(2, contacto.getCargo());
			stmt.setString(3, contacto.getNombreApe());
			stmt.setString(4, contacto.getTelefono().getTipo());
			stmt.setString(5, contacto.getTelefono().getNumero());
			stmt.setString(6, screenName);	
			stmt.setString(7, contacto.getTelefono().getCodigoArea());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add contacto personal seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}

	
	public int updateContactoPersonal(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
			sql = "{call update_seccional_contacto_personal(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,contacto.getTelefono().getId());
			stmt.setString(2, contacto.getCargo());
			stmt.setString(3, contacto.getNombreApe());
			stmt.setString(4, contacto.getTelefono().getTipo());
			stmt.setString(5, contacto.getTelefono().getNumero());
			stmt.setString(6, screenName);
			stmt.setString(7, contacto.getTelefono().getCodigoArea());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer add contacto personal seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}


	public int deleteContactoPersonal(int id_seccional,Contacto contacto,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer id_contacto=0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql="";
				
			sql = "{call delete_seccional_contacto_personal(?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,contacto.getTelefono().getId());
				stmt.setString(2, screenName);
				
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_contacto = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			_log.error("Error al hacer delete contacto personal seccional", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_contacto;
	}

	public List<Contacto> buscarContactosPersonalesSeccional(Integer codigo,String nombre) throws SystemException {
		List<Contacto> listaResultado = new ArrayList<Contacto>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_contactos_personales(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (nombre!=null && !"".equalsIgnoreCase(nombre)) {
				stmt.setString(2, nombre);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Contacto contacto = new Contacto();
				contacto.setCargo(rs.getString("cargo"));
				contacto.setCargoDescripcion(rs.getString("cargo_descripcion"));
				contacto.setNombreApe(rs.getString("nombre"));
				Telefono telefono = new Telefono();
				telefono.setNumero(rs.getString("telefono_numero"));
				telefono.setTipo(rs.getString("telefono_tipo"));
				telefono.setCodigoArea(rs.getString("cod_area"));
				telefono.setId(rs.getInt("id"));
				contacto.setTelefono(telefono);
				
				
				Seccional seccional = new Seccional();
				seccional.setId_seccional(rs.getInt("seccional_id"));
				seccional.setDescripcion(rs.getString("seccional_descripcion"));
				contacto.setSeccional(seccional);
				
				listaResultado.add(contacto);
			}
		}catch (Exception e) {
			_log.error(e);
			throw new SystemException();	
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}
		return listaResultado;
	}
	
	
	public Contacto buscarContactoPersonalSeccionalByID(Integer codigo) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		Contacto contacto = new Contacto();
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_contactos_personales_by_id(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
                
				contacto.setCargo(rs.getString("cargo"));
				contacto.setCargoDescripcion(rs.getString("cargo_descripcion"));
				contacto.setNombreApe(rs.getString("nombre"));
				
				Telefono telefono = new Telefono();
				telefono.setNumero(rs.getString("telefono_numero"));
				telefono.setTipo(rs.getString("telefono_tipo"));
				telefono.setId(rs.getInt("id"));
				telefono.setCodigoArea(rs.getString("cod_area"));
				contacto.setTelefono(telefono);
				
				Seccional seccional = new Seccional();
				seccional.setId_seccional(rs.getInt("seccional_id"));
				seccional.setDescripcion(rs.getString("seccional_descripcion"));
				contacto.setSeccional(seccional);
				
				ContactoElectronico ce =new ContactoElectronico();
				ce.setId(rs.getInt("id"));
				contacto.setContacto(ce);
				
			}
		} catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contacto;
	}

	public List<ContactoElectronico> buscarContactosSeccionalEmail(Integer codigo) throws SystemException {
		
		List<ContactoElectronico> listaResultado = new ArrayList<ContactoElectronico>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			_log.debug("Seccional: " + codigo);
			String sqlList = "{call buscar_seccional_contactos_electronicos_mail_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ContactoElectronico contacto = new ContactoElectronico(rs.getInt("id"), Tipo.getTipoById(rs.getString("tipo")), rs.getString("contacto"),
						rs.getString("observaciones"));  
				listaResultado.add(contacto);
			}
		}catch (Exception e) {
			_log.error(e);
			throw new SystemException();	
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaResultado;
	}
	
	public Map<String, Integer> desgloseSeccional(int idSeccional) throws SystemException {
		
		Map<String, Integer> resultado = new HashMap<String, Integer>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call reporte_desglose_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setInt(1, idSeccional);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				resultado.put(rs.getString("clave"), rs.getInt("total"));
				
			}
		} catch(Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return resultado;
	}
	
	public List<Contacto> buscarAutorizadesSeccionalByID(Integer codigo) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<Contacto>contactos=new ArrayList<Contacto>();
		
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_seccional_personal_orden_jerarquico(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Contacto contacto = new Contacto();        
				contacto.setCargo(rs.getString("cargo"));
				contacto.setCargoDescripcion(rs.getString("cargo_descripcion"));
				contacto.setNombreApe(rs.getString("nombre"));
				contactos.add(contacto);
			}
		} catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contactos;
	}

	public List<GestionSeccional> buscarGestionesxSeccional(Integer idSeccional) throws SystemException {
		
		GestionSeccional gs = null;
		List<GestionSeccional> resultados = new ArrayList<GestionSeccional>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_gestiones_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setInt(1, idSeccional);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				gs = GestionSeccional.getMapping("", rs);
				
				resultados.add(gs);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar gestion seccional", e);
			throw new SystemException(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return resultados;
	}
	
	public int insertarGestionSeccional(GestionSeccional gs, String screenName) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int idGestSecc = 0;
		
		try {
			con = ConnectionHelper.getConnection();
	
			String sql = "{call inserta_gestion_seccional(?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(gs.getFecha().getTime()));
			
			stmt.setString(2,gs.getObservaciones());
			
			stmt.setInt(3, gs.getSeccional().getId_seccional());
			
			stmt.setString(4, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				idGestSecc = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer insert de gestion seccional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return idGestSecc;
	}
	
	public List<Empresa> buscarEmpresasSeccionalByID(Integer codigo) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa>empresas=new ArrayList<Empresa>();
		
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_empresas_by_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Empresa empresa = new Empresa(); 
				empresa.setCuit(rs.getString("cuit"));
				empresa.setSucursal(rs.getString("sucursal"));
				empresa.setRazon_soc(rs.getString("razon_soc"));
				
				RamoEmpresa ramo = new RamoEmpresa();
				ramo.setDescripcion(rs.getString("descripcion_ramo_empresa"));
				ramo.setId_ramo_empresa(rs.getInt("id_ramo_empresa"));
				
				empresa.setRamoEmpresa(ramo);
				empresas.add(empresa);
			}
		} catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return empresas;
	}
	
	
	public List<CentroCosto> buscarCentroCostoSeccionalByID(Integer codigo) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<CentroCosto>centros=new ArrayList<CentroCosto>();
		
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call uoma.trae_centros_costos_by_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			
			if (codigo!=null && codigo>0) {
				stmt.setInt(1, codigo);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CentroCosto centro = new CentroCosto();
				centro.setId(rs.getInt("id"));
				centro.setDescripcion(rs.getString("descripcion"));
				centro.setPresupuesto(rs.getDouble("presupuesto"));
				centro.setEjecutado(rs.getDouble("ejecucion"));
				
				centros.add(centro);
				
			}
		} catch (Exception e) {
			_log.error(e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return centros;
	}

	public String[] buscarCuentaContablexSeccional(Integer idSeccional) throws SystemException {
		
		String[] resultado = null;

		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_cuenta_gasto_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setInt(1, idSeccional);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				resultado = new String[2];
				resultado[0] = rs.getString(1);
				resultado[1] = rs.getString(2);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar cuenta contable seccional", e);
			throw new SystemException(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return resultado;
	}
	
    public String buscarTarjetaRecargable(Integer idSeccional) throws SystemException {
		
		String resultado = "";

		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_tarjeta_recargable_seccional(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setInt(1, idSeccional);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				resultado = rs.getString(1);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar tarjeta recargable seccional", e);
			throw new SystemException(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return resultado;
	}
	
}
