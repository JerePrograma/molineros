package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.empresas.beans.ReporteEntidadCamaraMasaBean;
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.afiliados.empleadores.ImposibleBorrarEmpresaException;
import ar.com.ospim.estudioisidro.beans.EstadoGestion;
import ar.com.ospim.estudioisidro.beans.TipoLoteEmpresa;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

/**
 * <a href="EmpresaServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class EmpresaServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(EmpresaServiceImpl.class);

	private static EmpresaServiceImpl instance = null;

	public static EmpresaServiceImpl getInstance() {
		if (null == instance) {
			instance = new EmpresaServiceImpl();
		}
		return instance;
	}

//	public EstadoGestion getEstadoEmpleador(String cuit) throws SQLException {
//		Connection con = null;
//		CallableStatement stmt = null;
//
//		EstadoGestion estado = null;
//		try {
//			String sql = "{call buscar_estado_empleador(?)}";
//			con = ConnectionHelper.getConnection();
//			stmt = con.prepareCall(sql.toString());
//			stmt.setString(1, cuit);
//
//			ResultSet rs = stmt.executeQuery();
//
//			while (rs.next()) {
//				estado = EstadoGestion.getMapping("estado_", rs);
//			}
//		} catch (Exception e) {
//			_log.error("Error al buscar estado de la empresa", e);
//		} finally {
//			ConnectionHelper.cerrar(stmt, con);
//		}
//		return estado;
//	}

	public List<Empresa> getEmpleadores(String cuit, String descripcion,
			String sucu, int idSeccional) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpresas = null;
		_log.debug("cuit: " + cuit + " descripcion: " + descripcion + " id_seccional: " + idSeccional);
		try {
			String sql = "{call buscar_empleadores(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setString(3, sucu);
			if (idSeccional > 0) {
				stmt.setInt(4, idSeccional);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMapping(rs);
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpresas;
	}
	
	public List<Empresa> getEmpleadores(String cuit, String descripcion,
			String sucu, int idSeccional,Connection connection) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if(null==connection){
			con = ConnectionHelper.getConnection();
		}else{
			con=connection;
		}
		
		_log.debug("cuit: " + cuit + " descripcion: " + descripcion + " id_seccional: " + idSeccional);
		
		List<Empresa> listaEmpresas = null;
		try {
			String sql = "{call buscar_empleadores(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setString(3, sucu);
			if (idSeccional > 0) {
				stmt.setInt(4, idSeccional);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMapping(rs);
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			if(null==connection){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return listaEmpresas;
	}


	public List<Empresa> getEmpleadoresSeguimiento(String cuit, String razon,
			Integer lote, int molinera) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpresas = null;
		
		_log.debug("cuit: " + cuit + " razon soc: " + razon + " lote: " + lote);
		
		try {
			String sql = "{call trae_empresas_seguimiento(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (null != cuit && !cuit.trim().equals("")) {
				stmt.setString(1, cuit);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != razon && !razon.trim().equals("")) {
				stmt.setString(2, razon);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			stmt.setNull(3, Types.INTEGER);
			
			if (molinera == 0) {
				stmt.setNull(4, Types.BOOLEAN);
			} else if (molinera == 1) {
				stmt.setBoolean(4, true);
			} else if (molinera == 2) {
				stmt.setBoolean(4, false);
			}
			
			if (null != lote && lote>0) {
				stmt.setInt(5, lote);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMappingSeguimiento(rs);
				buscaDomicilios(emp.getCuit(), "000", con, stmt);
				List<Contacto> contactos = new ArrayList<Contacto>();

				agregarTelefonos(emp.getCuit(), "000", contactos, con, stmt);
				agregarContactosE(emp.getCuit(), "000", contactos, con, stmt);

				emp.setContactos(contactos);
				//FIXME  cual elijo aca?
				if (emp != null) {
					emp.setTelefonos(getTelefonos(emp.getCuit(), "000"));
					emp.setContactosElectronicos(getContactosElectronicos(
							emp.getCuit(), "000"));
				}
				/*
				 * if (emp != null) {
				 * emp.setTelefonos(getTelefonos(emp.getCuit(), "000"));
				 * emp.setContactosElectronicos(getContactosElectronicos(
				 * emp.getCuit(), "000")); }
				 */
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("cuit: " + cuit + " razon soc: " + razon + " lote: " + lote);
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpresas;
	}

	public List<Empresa> getEmpresasPorRamo(int id_ramo) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpresas = null;
		try {
			String sql = "{call buscar_empresas_ramo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_ramo);

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMapping(rs);
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empresa x Ramo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpresas;
	}

	public List<Empresa> getEmpresasPorRamo(int id_ramo, Date fechaIni,
			Date fechaFin) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpresas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_empresas_ramo(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_ramo);
			stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMapping(rs);
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empresa x Ramo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpresas;
	}

	public List<Empresa> getEmpresasPorRamo(int id_ramo, int id_ramo_hasta,
			Date fechaIni, Date fechaFin) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpresas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_empresas_ramo(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_ramo);
			stmt.setInt(2, id_ramo_hasta);
			stmt.setDate(3, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			listaEmpresas = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa emp = Empresa.getMapping(rs);
				listaEmpresas.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empresa x Ramo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpresas;
	}

	/**
	 * Devuelve la empresa con su direccion, telefonos y medios de contacto
	 * electronicos
	 * 
	 * @param cuit
	 * @return la empresa
	 */
	public Empresa getEmpleador(String cuit, String sucu) {
		Connection con = null;
		CallableStatement stmt = null;
		Empresa emp = null;
		_log.debug("cuit: " + cuit + " sucu: " + sucu);
		try {
			String sql = "{call buscar_empleador_y_domi(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				emp = Empresa.getMapping(rs, "emp__");
				Domicilio domi = Domicilio.getMapping(rs, "dom__");
				Domicilio domiFiscal = Domicilio.getMapping(rs, "domfisc__");
				emp.setDomicilio(domi);
				emp.setDomicilioFiscal(domiFiscal);
				try {
					emp.setUbicacionCarpeta(rs.getString("ubicacion_carpeta"));
					emp.setCartaDoc(rs.getString("carta_doc"));
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		if (emp != null) {
			emp.setTelefonos(getTelefonos(cuit, sucu));
			emp.setContactosElectronicos(getContactosElectronicos(cuit, sucu));
		}

		return emp;
	}

	public Empresa getEmpleadorCompleto(String cuit, String sucu, Connection connection) {
		Connection con = null;
		CallableStatement stmt = null;
		Empresa emp = null;
		_log.debug("Empleador a buscar... " + cuit + " sucursal: " +sucu);
		try {
			if(null==connection){
				con = ConnectionHelper.getConnection();
			}else{
				con=connection;
			}
			String sql = "{call informacion_afip.buscar_datos_empresa(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				emp = Empresa.getMappingCompleto(rs, "");
				try {
					emp.setUbicacionCarpeta(rs.getString("ubicacion_carpeta"));
					emp.setCartaDoc(rs.getString("carta_doc"));
//					emp.setEstado(rs.getString("estado"));
					emp.setEstado(EstadoGestion.getMapping("estado_", rs));
				} catch (Exception e) {

				}
			}
			if (emp != null) {
				emp.setDomicilios(buscaDomicilios(cuit, sucu, con, stmt));
				List<Contacto> contactos = new ArrayList<Contacto>();

				agregarTelefonos(cuit, sucu, contactos, con, stmt);
				agregarContactosE(cuit, sucu, contactos, con, stmt);
				emp.setContactos(contactos);
				emp.setCuentasBcrias(getCuentasBancarias(cuit, sucu, con));
				emp.setRegimen(getRegimenRetencionGanancias(cuit, sucu));
			}

		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

		return emp;
	}

	public Seccional getSeccionalCompleto(String cuit, int idSeccional) {
		Connection con = null;
		CallableStatement stmt = null;
		Seccional emp = null;
		
		_log.debug("cuit: " + cuit + " id_seccional: " + idSeccional);
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call informacion_afip.buscar_datos_seccional(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setInt(2, idSeccional);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				emp = Seccional.getMappingCompleto(rs, "");
			}

		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return emp;
	}

	public void agregarContactosE(String cuit, String sucu,
			List<Contacto> contactos, Connection con, CallableStatement stmt)
			throws SQLException {
		String sql = "{call informacion_afip.buscar_contactos_empresa(?, ?)}";
		stmt = con.prepareCall(sql.toString());
		stmt.setString(1, cuit);
		stmt.setString(2, sucu);
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			ContactoElectronico cont = ContactoElectronico
					.getMappingCompleto(rs);
			Contacto contacto = new Contacto();
			contacto.setCargo(rs.getString("cargo"));
			contacto.setNombreApe(rs.getString("nomape"));
			contacto.setProfesion(rs.getString("profesion"));
			contacto.setContacto(cont);
			contactos.add(contacto);
		}

	}

	public void agregarTelefonos(String cuit, String sucu,
			List<Contacto> contactos, Connection con, CallableStatement stmt)
			throws SQLException {
		String sql = "{call informacion_afip.buscar_telefonos_empresa(?, ?)}";
		stmt = con.prepareCall(sql.toString());
		stmt.setString(1, cuit);
		stmt.setString(2, sucu);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			Telefono tele = Telefono.getMapping(rs);
			Contacto contacto = new Contacto();
			contacto.setCargo(rs.getString("cargo"));
			contacto.setNombreApe(rs.getString("nomape"));
			try{
			contacto.setProfesion(rs.getString("profesion"));
			}catch (Exception e) {
				// TODO: handle exception
			}
			contacto.setTelefono(tele);
			contactos.add(contacto);
		}

	}

	public List<Domicilio> buscaDomicilios(String cuit, String sucu,
			Connection con, CallableStatement stmt) throws SQLException {
		String sql = "{call informacion_afip.buscar_domicilios_empresa(?, ?)}";
		stmt = con.prepareCall(sql.toString());
		stmt.setString(1, cuit);
		stmt.setString(2, sucu);

		ResultSet rs = stmt.executeQuery();
		List<Domicilio> domicilios = new ArrayList<Domicilio>();
		while (rs.next()) {
			Domicilio domi = Domicilio.getMappingEmpresa(rs, "");
			domicilios.add(domi);
		}
		return domicilios;
	}

	/**
	 * Devuelve los telefonos de una empresa
	 * 
	 * @param cuit
	 * @return List<Telefono>
	 */
	public List<Telefono> getTelefonos(String cuit, String sucu) {
		List<Telefono> tels = new ArrayList<Telefono>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_telefonos_empleador(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Telefono tel = Telefono.getMapping(rs);
				tels.add(tel);
			}
		} catch (Exception e) {
			_log.error("Error al buscar telefonos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tels;
	}

	/**
	 * Devuelve los medios de contacto electronicos de una empresa
	 * 
	 * @param cuit
	 *            , sucursal
	 * @return List<ContactoElectronico>
	 */
	public List<ContactoElectronico> getContactosElectronicos(String cuit,
			String sucu) {
		List<ContactoElectronico> contactos = new ArrayList<ContactoElectronico>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_contactos_empleador(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ContactoElectronico contacto = ContactoElectronico
						.getMapping(rs);
				contactos.add(contacto);
			}
		} catch (Exception e) {
			_log.error("Error al buscar contactos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contactos;
	}

	public void save(Empresa empresa, Connection connectionParameter,
			String username) throws DuplicateEmpresaIdException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.inserta_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			_log.debug("Insertando empresa CUIT: " + empresa.getCuit());
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			int cont = 1;
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, empresa.getCuit());
			stmt.setString(cont++, empresa.getSucursal());
			stmt.setString(cont++, empresa.getRazon_soc());
			stmt.setInt(cont++, empresa.getId_ramo_empresa());
			if(empresa.getActividadPrincipal()!=null){
				stmt.setInt(cont++, empresa.getActividadPrincipal().getCodigo());
			}else{
				stmt.setNull(cont++, Types.INTEGER);
			}
			if(empresa.getActividadSecundaria()!=null){
				stmt.setInt(cont++, empresa.getActividadSecundaria().getCodigo());
			}else{
				stmt.setNull(cont++, Types.INTEGER);
			}
			
			stmt.setInt(cont++, empresa.getId_seccional());
			stmt.setString(cont++, empresa.getObservaciones());
			stmt.setString(cont++, empresa.getImpGanancias());
			stmt.setString(cont++, empresa.getImpIva());
			stmt.setString(cont++, empresa.getMonotributo());
			stmt.setString(cont++, empresa.getIntegranteSoc());
			stmt.setString(cont++, empresa.getEmpleador());
			stmt.setString(cont++, empresa.getActividadMonotributo());
			if (null != empresa.getEntidadCamaraEmpresa()) {
				stmt.setInt(cont++, empresa.getEntidadCamaraEmpresa()
						.getId_entidad_cam_empresa());
			} else {
				stmt.setNull(cont++, Types.SMALLINT);
			}

			stmt.setString(cont++, empresa.getDestinoCorrespondencia());
			stmt.setString(cont++, empresa.getCBU());
			stmt.setString(cont++, empresa.getPortaCheque());
			stmt.setString(cont++, empresa.getCaeCai());
			stmt.setString(cont++, empresa.getNumeroCaeCai());
			stmt.setInt(cont++, empresa.getRegimen().getCodigoRegimen());
			stmt.setString(cont++, username);
			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}

		} catch (SQLException e) {
			_log.error("Error al insertar empresa", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar empresa", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void updateSeccional(Seccional empresa,
			Connection connectionParameter, String username)
			throws DuplicateEmpresaIdException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.actualiza_seccional(?,?,?,?,?,?,?)}";

			_log.debug("Actualiza seccional CUIT: " + empresa.getCuit());
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, empresa.getCuit());
			stmt.setInt(2, empresa.getId_seccional());
			stmt.setString(3, empresa.getObservaciones());
			stmt.setString(4, empresa.getDestinoCorrespondencia());
			stmt.setString(5, empresa.getPortaCheque());
			stmt.setString(6, empresa.getCBU());
			stmt.setString(7, username);
			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}

		} catch (SQLException e) {
			_log.error("Error al insertar seccional", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar empresa", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void update(Empresa empresa, Connection connectionParameter,
			String username) throws DuplicateEmpresaIdException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.actualiza_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			_log.debug("Actualiza empresa CUIT: " + empresa.getCuit());

			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			int cont = 1;
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, empresa.getCuit());
			stmt.setString(cont++, empresa.getSucursal());
			stmt.setString(cont++, empresa.getRazon_soc());
			stmt.setInt(cont++, empresa.getId_ramo_empresa());
			stmt.setInt(cont++, empresa.getActividadPrincipal().getCodigo());
			stmt.setInt(cont++, empresa.getActividadSecundaria().getCodigo());
			stmt.setInt(cont++, empresa.getId_seccional());
			stmt.setString(cont++, empresa.getObservaciones());
			stmt.setString(cont++, empresa.getImpGanancias());
			stmt.setString(cont++, empresa.getImpIva());
			stmt.setString(cont++, empresa.getMonotributo());
			stmt.setString(cont++, empresa.getIntegranteSoc());
			stmt.setString(cont++, empresa.getEmpleador());
			stmt.setString(cont++, empresa.getActividadMonotributo());
			stmt.setInt(cont++, empresa.getEntidadCamaraEmpresa()
					.getId_entidad_cam_empresa());
			stmt.setString(cont++, empresa.getDestinoCorrespondencia());
			stmt.setString(cont++, empresa.getCBU());
			stmt.setString(cont++, empresa.getPortaCheque());
			stmt.setString(cont++, empresa.getCaeCai());
			stmt.setString(cont++, empresa.getNumeroCaeCai());
			stmt.setInt(cont++, empresa.getRegimen().getCodigoRegimen());
			stmt.setString(cont++, username);
			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}

		} catch (SQLException e) {
			_log.error("Error al update empresa", e);
			if (connectionParameter == null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					_log.error("Error al update empresa", e);
				}
			}
			
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al update empresa", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void saveDomicilio(String cuit, String sucur, Domicilio domicilio,
			Connection connectionParameter, String username)
			throws DuplicateEmpresaIdException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.actualiza_domicilio_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			int cont = 1;
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, cuit);
			stmt.setString(cont++, sucur);
			stmt.setString(cont++, domicilio.getCalle());
			stmt.setString(cont++, domicilio.getNumero());
			stmt.setString(cont++, domicilio.getPiso());
			stmt.setString(cont++, domicilio.getDepto());
			stmt.setString(cont++, domicilio.getOficina());
			stmt.setInt(cont++, domicilio.getLocalidadId());
			stmt.setInt(cont++, domicilio.getProvinciaId());
			stmt.setString(cont++, domicilio.getPostal_codi());
			stmt.setString(cont++, domicilio.getObservaciones());
			stmt.setInt(cont++, domicilio.getId_domicilio());
			stmt.setString(cont++, domicilio.getDomi_tipo());
			stmt.setString(cont++, domicilio.getCargo());
			stmt.setString(cont++, domicilio.getNomape());
			if (null != domicilio.getBaja_fecha()) {
				stmt.setDate(cont++, new java.sql.Date(domicilio
						.getBaja_fecha().getTime()));
			} else {
				stmt.setNull(cont++, Types.DATE);
			}
			stmt.setString(cont++, username);

			stmt.executeUpdate();
		
		} catch (SQLException e) {
			_log.error("Error al grabar domicilio empresa", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar domicilio", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void saveContacto(String cuit, String sucur, Contacto contacto,
			Connection connectionParameter, String username)
			throws DuplicateEmpresaIdException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (contacto.getTipoAsString().equals("TELEFONO")) {
				sql = "{call informacion_afip.actualizar_telefono_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else {
				sql = "{call informacion_afip.actualizar_contacto_empresa(?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			int cont = 1;
			stmt.setString(cont++, cuit);
			stmt.setString(cont++, sucur);

			if (contacto.getTipoAsString().equals("TELEFONO")) {
				stmt.setString(cont++, "C");
				stmt.setString(cont++, contacto.getTelefono().getCodigoPais());
				stmt.setString(cont++, contacto.getTelefono().getCodigoArea());
				stmt.setString(cont++, contacto.getTelefono().getNumero());
				stmt.setString(cont++, contacto.getTelefono().getExtension());
			} else {
				stmt.setString(cont++, contacto.getContacto().getTipo().getId());
				stmt.setString(cont++, contacto.getContacto().getContacto());
			}
			stmt.setString(cont++, contacto.getObservaciones());
			stmt.setInt(cont++, contacto.getIdContacto());
			if (null != contacto.getBajaFecha()) {
				stmt.setDate(cont++, new java.sql.Date(contacto.getBajaFecha()
						.getTime()));
			} else {
				stmt.setNull(cont++, Types.DATE);
			}
			stmt.setString(cont++, contacto.getCargo());
			stmt.setString(cont++, contacto.getNombreApe());
			stmt.setString(cont++, contacto.getProfesion());
			stmt.setString(cont++, username);

			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al insertar empresa", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar contacto", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public Integer saveCuentaBancaria(String cuit, String sucur,
			CuentaBancaria cuenta, Connection connectionParameter,
			String username) throws DuplicateEmpresaIdException,
			SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		Integer idCtaBcriaNueva= 0;
		
		try {
			String sql = null;

			sql = "{call informacion_afip.actualizar_cuenta_bancaria(?,?,?,?,?,?,?,?,?)}";

			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			int cont = 1;
			stmt.setString(cont++, cuit);
			stmt.setString(cont++, sucur);

			stmt.setInt(cont++, cuenta.getId_cuenta_bcria());
			stmt.setInt(cont++, cuenta.getBanco().getId_banco());
			stmt.setString(cont++, cuenta.getDescripcion());
			if (null != cuenta.getSucursalString()
					&& !"".equals(cuenta.getSucursalString().trim())) {
				stmt.setString(cont++, cuenta.getSucursalString());
			} else {
				stmt.setString(cont++, "000");
			}
			stmt.setString(cont++, cuenta.getCBU());

			if (null != cuenta.getBajaFecha()) {
				stmt.setDate(cont++, new java.sql.Date(cuenta.getBajaFecha()
						.getTime()));
			} else {
				stmt.setNull(cont++, Types.DATE);
			}
			stmt.setString(cont++, username);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				idCtaBcriaNueva = rs.getInt(1);
				
				cuenta.setId_cuenta_bcria(idCtaBcriaNueva);
			}
			
			if (connectionParameter == null) {
				con.commit();
			}

		} catch (SQLException e) {
			_log.error("Error al actualizar cuenta", e);
			if (connectionParameter == null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					_log.error("Error al  actualizar cuenta", e);
				}
			}
			
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
			
		} catch (Exception e) {
			_log.error("Error al insertar cuenta", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return idCtaBcriaNueva;
	}

	public List<CuentaBancaria> getCuentasBancarias(String cuit, String sucur,
			Connection connectionParameter) throws DuplicateEmpresaIdException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaBancaria> cuentas = null;
		try {
			String sql = null;

			sql = "{call informacion_afip.buscar_cuentas_bancarias(?,?)}";

			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			stmt = con.prepareCall(sql.toString());
			int cont = 1;
			stmt.setString(cont++, cuit);
			stmt.setString(cont++, sucur);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				if (null == cuentas) {
					cuentas = new ArrayList<CuentaBancaria>();
				}
				cuentas.add(CuentaBancaria.getMapping(rs));
			}

		} catch (Exception e) {
			_log.error("Error al buscar cuenta bcria", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return cuentas;
	}

	public List<CuentaBancaria> getCuentasBancariasPorBanco(String cuit, String sucur, int idBanco,
			Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaBancaria> cuentas = new ArrayList<CuentaBancaria>();
		
		try {
			String sql = null;

			sql = "{call informacion_afip.buscar_cuentas_bancarias_por_banco(?, ?, ?)}";

			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuit);
			stmt.setString(2, sucur);
			stmt.setInt(3, idBanco);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				cuentas.add(CuentaBancaria.getMapping(rs));
			}

		} catch (Exception e) {
			_log.error("Error al buscar cuenta bcria", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return cuentas;
	}
	
	public void borrar(String cuit, String sucu, String usr)
			throws ImposibleBorrarEmpresaException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_empresa(?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			stmt.setString(3, usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarEmpresaException();
				}
			}
		} catch (SQLException e) {
			_log.error("Error al borrar empresa", e);
			throw new SystemException();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void reactivar(String cuit, String sucu) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call reactivar_empresa(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			stmt.executeQuery();
		} catch (SQLException e) {
			_log.error("Error al buscar contactos", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public String saveAfiliadoComoEmpresa(String cuil_titular, String username)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_afiliado_como_empresa(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setString(2, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public String traerPrestadorDomicilioFiscal(String cuit)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		String domicilio = "";

		try {
			String sql = "{call informacion_afip.traer_prestador_domicilio_fiscal(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String piso = rs.getString("piso");
				String dpto = rs.getString("dpto");
				String cp = rs.getString("codigopostal");
				String provincia = rs.getString("provincia");
				String calle = rs.getString("calle");
				String nro = rs.getString("numero");
				String loc = rs.getString("localidad");

				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(calle)) ? "Calle "
								+ calle : "");
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(nro)) ? " N° " + nro : "");
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(piso)) ? " Piso  " + piso
								: "");
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(dpto)) ? " Dpto " + dpto
								: "");
				domicilio = domicilio + ", ";
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(loc)) ? loc : "");
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(cp)) ? " (CP" + cp + ")"
								: "");
				domicilio = domicilio
						+ ((StringUtils.checkNotEmpty(provincia)) ? " Provincia de "
								+ provincia
								: "");
			}

		} catch (Exception e) {
			_log.error("Error al buscar estado", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return domicilio;
	}

	public ReporteEntidadCamaraMasaBean getReporteEntidadCamaraMasa(String cuit, String sucur) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		ReporteEntidadCamaraMasaBean repo=new ReporteEntidadCamaraMasaBean();  
		try {
			String sql = "{call informacion_afip.reporte_cant_remu_camara_entidad(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			if(null!=sucur&&!sucur.equals("")){
				stmt.setString(2, sucur);
			}else{
				stmt.setString(2, "000");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				repo=ReporteEntidadCamaraMasaBean.getMapping(rs);				
			}
		} catch (Exception e) {
			_log.error("Error al buscar reporte entidad", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return repo;
	}
	
	public List<EstadoGestion> getEstadosEmpresa() {
		
		List<EstadoGestion> estados = new ArrayList<EstadoGestion>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_estados_empresa() }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				estados.add(EstadoGestion.getMapping("estado_",rs));
			}
		} catch (Exception e) {
			_log.error("Error al buscar Estados de Empresa", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return estados;
	}
	
	public List<TipoLoteEmpresa> getTiposLoteEmpresa() {
		
		List<TipoLoteEmpresa> tiposLotes = new ArrayList<TipoLoteEmpresa>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_lote_empresa() }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tiposLotes.add(TipoLoteEmpresa.getMapping("tlotemp_",rs));
			}
		} catch (Exception e) {
			_log.error("Error al buscar Tipos de Lote de Empresa", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tiposLotes;
	}
	
	public Regimen getRegimenRetencionGanancias(String cuit, String sucursal) {
		Connection con = null;
		CallableStatement stmt = null;
		Regimen reg = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_regimen_ret_ganancias(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucursal);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				reg = Regimen.getMapping(rs, "");
			}

		} catch (Exception e) {
			_log.error("Error al buscar código de régimen empresa", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return reg;
	}
	
	public static List<Regimen> getRegimenesRetencionGanancias() {
		Connection con = null;
		CallableStatement stmt = null;
		Regimen reg = null;
		List<Regimen> regimenes = new ArrayList<Regimen>();
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_regimenes_retencion_ganancias() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				reg = Regimen.getMapping(rs, "");
				regimenes.add(reg);
			}

		} catch (Exception e) {
			_log.error("Error al buscar códigos de régimen ganancias", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return regimenes;
	}
	
	public void updateCBU(Empresa empresa, Connection connectionParameter,
			String username) throws DuplicateEmpresaIdException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.actualiza_empresa_CBU(?,?,?,?)}";

			_log.debug("Actualiza CBU empresa CUIT: " + empresa.getCuit());

			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			int cont = 1;
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, empresa.getCuit());
			stmt.setString(cont++, empresa.getSucursal());
			stmt.setString(cont++, empresa.getCBU());
			stmt.setString(cont++, username);
			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}

		} catch (SQLException e) {
			_log.error("Error al update empresa CBU", e);
			if (connectionParameter == null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					_log.error("Error al update empresa CBU", e);
				}
			}
			
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEmpresaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al update empresa", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	
}
