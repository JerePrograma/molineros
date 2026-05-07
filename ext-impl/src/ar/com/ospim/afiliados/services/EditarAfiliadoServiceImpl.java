package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.DetalleDiscapacidad;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.autorizaciones.action.AutorizacionPrestacionalEmail;
import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.AportesMonotributo;
import ar.com.ospim.global.beans.AportesMonotributoClase;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.Incidente;

/**
 * servicio test que nos da acceso a los datos de la aplicación (BD).
 * 
 */
public class EditarAfiliadoServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(EditarAfiliadoServiceImpl.class);

	
	
	
	public int existeAfiliado(String cuil_titular, Date vigenFecha) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call existe_afiliado(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setDate(2, vigenFecha != null ? new java.sql.Date(vigenFecha.getTime()) : null);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

		return result;
	}
	
	
	public int validarBorradoIntegrante(String cuilTitular, int inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call validar_borrado_fisico(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

		return result;
	} 
	
	public int existeAfiliadoTitular(String cuil_titular,Date vigenFecha) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call existe_afiliado_titular(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setDate(2, vigenFecha !=  null ? new java.sql.Date(vigenFecha.getTime()): null);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	/**
	 * Metodo que obtiene un afiliado a partir de la clave primaria, en caso de
	 * que está dado de baja o de no encontrarlo retorna null
	 * 
	 * @param cuil_titular
	 * @param inte
	 * @param connectionParameter
	 * @return
	 * @throws SystemException
	 * @throws NoSuchAfiliadoEntryException
	 */
	 
	public Afiliado getAfiliadoEntry(String cuil_titular, int inte,
			Connection connectionParameter) throws SystemException,
			NoSuchAfiliadoEntryException {
		Connection con = null;
		Connection conLportal = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();

		try {
			String sql = "{call buscar_afiliado_por_cuil_inte(?,?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			conLportal = ConnectionHelper.getLPortalConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
						
				// Veo si tiene imágenes
				String sqlImage = "{call tiene_imagen_afiliado(?)}";
				stmt = conLportal.prepareCall(sqlImage.toString());
				stmt.setString(1, cuil_titular);
				ResultSet rsImage = stmt.executeQuery();
				while (rsImage.next()) {
					afiliado.setFolderid(rsImage.getInt(1));
					afiliado.setTitle(rsImage.getString(2));
					afiliado.setTiene_imagen(rsImage.getInt(3));
				}
				_log.debug("TIENE IMAGEN: " + afiliado.getTiene_imagen());
				_log.debug("TIENE TITLE: " + afiliado.getTitle());
				_log.debug("TIENE FOLDER_ID: " + afiliado.getFolderid());
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			if (afiliados.size() == 0) {
				throw new NoSuchAfiliadoEntryException(
						"No se ha encontrado un afiliado con esa clave primaria");
			}
		} catch (NoSuchAfiliadoEntryException e) {
			_log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(conLportal);
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return afiliados.get(0);
	}

	/**
	 * Metodo que obtiene un afiliado a partir de la clave primaria, aunque este
	 * haya sido dado de baja, en caso de no encontrarlo retorna null
	 * 
	 * @param connection
	 * 
	 * @throws SystemException
	 * @throws NoSuchAfiliadoEntryException
	 */
	public Afiliado getAfiliadoEntryInclusoDadoBaja(String cuil_titular,
			int inte, Connection connectionParameter) throws SystemException,
			NoSuchAfiliadoEntryException {
		Connection con = null;
		CallableStatement stmt = null;		
		Connection conLportal = null;
		Incidente incidente = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		try {
			String sql = "{call busca_afiliado_incluso_dado_baja_c_i(?,?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			conLportal = ConnectionHelper.getLPortalConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuil_titular);

			stmt.setInt(2, inte);

			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
				
				// Veo si tiene imágenes
				String sqlImage = "{call tiene_imagen_afiliado(?)}";
				stmt = conLportal.prepareCall(sqlImage.toString());
				stmt.setString(1, cuil_titular);
				ResultSet rsImage = stmt.executeQuery();
				while (rsImage.next()) {
					afiliado.setFolderid(rsImage.getInt(1));
					afiliado.setTitle(rsImage.getString(2));
					afiliado.setTiene_imagen(rsImage.getInt(3));
				}
				//Agrego un incidente de unidad operativa de un empleado si es que tiene
				incidente = BusquedaAfiliadoServiceImpl.buscarUltimoIncidente(cuil_titular, inte);
				if (incidente != null) {
					afiliado.addIncidente(incidente);
				}
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			if (afiliados.size() == 0) {
				throw new NoSuchAfiliadoEntryException(
						"No se ha encontrado un afiliado con esa clave primaria");
			}
		} catch (NoSuchAfiliadoEntryException e) {
			_log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(conLportal);
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return afiliados.get(0);
	}

	/**
	 * Metodo que obtiene un afiliado a partir de la clave primaria, aunque este
	 * haya sido dado de baja, en caso de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchAfiliadoEntryException
	 */
	public Afiliado getAfiliadoDadoBaja(String cuil_titular, int inte)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Domicilio afiDomicilio = null;
		try {
			String sql = "{call busca_afiliado_dado_baja_c_i(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				afiliado = new Afiliado();
				afiDomicilio = new Domicilio();
				afiliado.setCuil_titular(cuil_titular);
				afiliado.setInte(inte);
				afiliado.setId_ospim(rs.getInt("id_ospim"));
				afiliado.setId_uoma(rs.getInt("id_uoma"));
				afiliado.setId_amtima(rs.getInt("id_amtima"));
				afiliado.setApellido(rs.getString("apellido"));
				afiliado.setNombre(rs.getString("nombre"));
				afiliado.setDocumento_tipo(rs.getString("documento_tipo"));
				afiliado.setSexo(rs.getString("sexo"));
				afiliado.setCuil(rs.getString("cuil") != null ? rs
						.getString("cuil") : "");
				afiliado.setNaci_fecha(rs.getDate("naci_fecha"));
				afiliado.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
				afiliado.setCivil_esta(rs.getString("civil_esta"));
				afiliado.setNacionalidad(rs.getInt("nacionalidad"));
				afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
				afiliado.setParentesco(rs.getString("parentesco"));
				afiliado.setSeccional(new Seccional());
				afiliado.getSeccional().setId_seccional(rs.getInt("id_seccional"));
				afiliado.setAnterior_os(rs.getInt("anterior_os"));
				afiliado.setVigen_fecha(rs.getDate("vigen_fecha"));
				afiliado.setObservaciones(rs.getString("observaciones"));
				afiliado.setAlta_usr(rs.getString("alta_usr"));
				afiliado.setModi_usr(rs.getString("modi_usr"));
				afiliado.setDiscapacitado(rs.getString("discapacitado"));
				afiliado.setDocu_numero(rs.getString("docu_numero") != null ? rs
						.getString("docu_numero") : "");
				afiDomicilio.setDomi_tipo(rs.getString("domi_tipo"));
				afiDomicilio.setCalle(rs.getString("calle"));
				afiDomicilio.setPiso(rs.getString("piso") != null ? rs
						.getString("piso") : "");
				afiDomicilio.setDepto(rs.getString("depto") != null ? rs
						.getString("depto") : "");
				afiDomicilio.setOficina(rs.getString("oficina") != null ? rs
						.getString("oficina") : "");
				afiDomicilio.setPostal_codi(rs.getString("postal_codi"));
				afiDomicilio.setBarrio(rs.getString("barrio") != null ? rs
						.getString("barrio") : "");
				afiDomicilio.setTelefono(rs.getString("telefono") != null ? rs
						.getString("telefono") : "");
				afiDomicilio
						.setObservaciones(rs.getString("observaciones_dom"));
				afiDomicilio.setDomi_val(rs.getString("domi_val"));
				afiDomicilio.setAlta_usr(rs.getString("alta_usr_d"));
				afiDomicilio.setModi_usr(rs.getString("modi_usr_d"));
				afiDomicilio.setProvinciaId(rs.getInt("provincia"));
				afiDomicilio.setLocalidadId(rs.getInt("localidad"));
				afiDomicilio.setNumero(rs.getString("numero"));
				afiliado.setAportante_titular(rs.getInt("aportante_titular"));
				afiliado.setBaja_fecha(rs.getDate("baja_f"));
				afiliado.setBaja_usr(rs.getString("baja_u"));
				afiliado.setDomicilioDefault(afiDomicilio);
				afiliado.setUltimo_plan(new Plan(rs.getInt("id_plan"),""));
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}

	/**
	 * Metodo que obtiene un afiliado que ya existe en la base, si es que
	 * existe, sino retorna null
	 * 
	 * @throws SystemException
	 */
	public Afiliado getAfiliadoExistente(String nroDoc, String documento_tipo, Date vigenFecha)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		try {
			String sql = "{call buscar_afiliado_por_doc_y_tipo(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, documento_tipo);
			stmt.setInt(2, Integer.parseInt(nroDoc));
						
	        if(vigenFecha != null){
				stmt.setDate(3, new java.sql.Date(vigenFecha.getTime())); 
			}else{//ignora la validacion con una fecha muy baja
				stmt.setDate(3, new java.sql.Date(1220227200));  //returns 1 Jan 1970
			}
			
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
				
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			if (afiliados.size() == 0) {
				return null;
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}

	/**
	 * metodo que carga un nuevo afiliado a partir de los parámetros")); si no
	 * lo puede insertar retorna null
	 * 
	 * @param connection
	 * 
	 * @throws DuplicateAfiliadoIdException
	 * @throws SystemException
	 */

	public void insertaAfiliadoEntry(Afiliado afi, String opciones, String preCarga, String idPreAfi, String userName, 
			Date dado_baja, int motivo_baja, Connection connection)
					throws SystemException, DuplicateAfiliadoIdException {
		
		Connection con = null;
		CallableStatement stmt1 = null, stmt2 = null , stmt3 = null ;
		
		try {
			String sql = "{call inserta_afiliado (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt1 = con.prepareCall(sql.toString());
			stmt1.setString(1, afi.getCuil_titular());
			stmt1.setInt(2, afi.getInte());
			stmt1.setInt(3, afi.getId_ospim());
			stmt1.setInt(4, afi.getId_uoma());
			stmt1.setInt(5, afi.getId_amtima());
			stmt1.setString(6, afi.getApellido().toUpperCase());
			stmt1.setString(7, afi.getNombre().toUpperCase());
			stmt1.setString(8, afi.getDocumento_tipo());
			stmt1.setString(9, afi.getSexo().toUpperCase());
			stmt1.setString(10, afi.getCuil());
			stmt1.setDate(11, new java.sql.Date(afi.getNaci_fecha().getTime()));
			stmt1.setInt(12, afi.getId_civil_esta());
			stmt1.setInt(13, afi.getNacionalidad());
			stmt1.setInt(14, afi.getId_parentesco());
			stmt1.setInt(15, afi.getSeccional().getIdSeccional());
			stmt1.setInt(16, afi.getAnterior_os());
			stmt1.setDate(17, new java.sql.Date(afi.getVigen_fecha().getTime()));
			stmt1.setString(18, afi.getObservaciones());
			stmt1.setNull(19, Types.DATE);
			stmt1.setString(20, userName);
			stmt1.setString(21, afi.getDiscapacitado());
			stmt1.setString(22, afi.getDocu_numero());
			stmt1.setString(23, WebKeysAfiliados.DEFAULT_TIPO_DOMICILIO); // 0,
			// particular
			stmt1.setString(24, afi.getDomicilios()!=null?afi.getDomicilioDefault().getCalle().toUpperCase():"");
			stmt1.setString(25, afi.getDomicilios()!=null?afi.getDomicilioDefault().getPiso():"");
			stmt1.setString(26, afi.getDomicilios()!=null?afi.getDomicilioDefault().getDepto():"");
			stmt1.setNull(27, Types.CHAR);
			stmt1.setString(28, afi.getDomicilios()!=null?afi.getDomicilioDefault().getPostal_codi():"");
			stmt1.setString(29, afi.getDomicilios()!=null?afi.getDomicilioDefault().getBarrio():"");
			stmt1.setString(30, afi.getDomicilios()!=null?afi.getDomicilioDefault().getTelefono():"");
			stmt1.setNull(31, Types.CHAR);
			stmt1.setString(32, WebKeysAfiliados.DEFAULT_DOMICILIO_VALIDO); // 1,
			// valido
			stmt1.setInt(33, afi.getDomicilios()!=null?afi.getDomicilioDefault().getProvinciaId():0);
			stmt1.setInt(34, afi.getDomicilios()!=null?afi.getDomicilioDefault().getLocalidadId():0);
			stmt1.setString(35, afi.getDomicilios()!=null?afi.getDomicilioDefault().getNumero():"");
			stmt1.setDate(36, dado_baja == null ? null : new java.sql.Date(
					dado_baja.getTime()));
			if (motivo_baja == 0) {
				stmt1.setNull(37, Types.INTEGER);
			} else {
				stmt1.setInt(37, motivo_baja);
			}
			stmt1.setDate(38, afi.getId_ospim_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_ospim_baja_fecha().getTime()));
			stmt1.setDate(39, afi.getId_uoma_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_uoma_baja_fecha().getTime()));
			stmt1.setDate(40, afi.getId_amtima_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_amtima_baja_fecha().getTime()));
			
			stmt1.setString(41, afi.getDomicilios()!=null?afi.getDomicilioDefault().getCod_area_telefono():"");
			stmt1.setString(42, afi.getDomicilios()!=null?afi.getDomicilioDefault().getCod_area_celular():"");
			stmt1.setString(43, afi.getDomicilios()!=null?afi.getDomicilioDefault().getCelular():"");
			stmt1.setInt(44, afi.getCenso2013());
			stmt1.setString(45, afi.getDomicilios()!=null?afi.getDomicilioDefault().getCod_area_tel_laboral():"");
			stmt1.setString(46, afi.getDomicilios()!=null?afi.getDomicilioDefault().getTel_laboral():"");
			if(StringUtils.checkNotEmpty(afi.getEmail())){
				stmt1.setString(47, afi.getEmail().toLowerCase());
			}else{
				stmt1.setNull(47, Types.VARCHAR);
			}
			stmt1.setInt(48, afi.getTieneAntecedentesJudiciales());
			stmt1.setInt(49, afi.getClientePreferencial());
			stmt1.setString(50, afi.getProyecto());
			
			stmt1.executeUpdate();

			// SI VIENE DE OPCIONES ACTUALIZO LA TABLA DE OPCIONES
			String sqlUpdate = "{call novedades_sss.actualiza_marca_de_alta_portal(?, ?, ?, ?, ?) }";
			if (null != opciones && opciones.trim().equals("true")) {
				stmt2 = con.prepareCall(sqlUpdate.toString());
				stmt2.setString(1, afi.getCuil_titular());
				stmt2.setInt(2, afi.getInte());
				stmt2.setString(3, "opciones");
				stmt2.setNull(4, Types.INTEGER);
				stmt2.setString(5, userName);
				
				stmt2.executeUpdate();
			}
			if (null != preCarga && preCarga.trim().equals("true")) {
				stmt2 = con.prepareCall(sqlUpdate.toString());
				stmt2.setString(1, afi.getCuil_titular());
				stmt2.setInt(2, afi.getInte());
				stmt2.setString(3, "precarga");
				stmt2.setInt(4, Integer.parseInt(idPreAfi));
				stmt2.setString(5, userName);
				
				stmt2.executeUpdate();
			}
//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
			
			stmt3 = con.prepareCall(sqlInsert.toString());
			stmt3.setString(1, afi.getCuil_titular());
			stmt3.setInt(2, afi.getInte());
			stmt3.setInt(3, afi.getIdCorrespondencia());
			stmt3.setNull(4, Types.TIMESTAMP); // en este alta de afiliado todavia no conocemos la fecha de la impresion de credencial
			if(dado_baja != null){ // para afiliados integrantes que vengan con baja futura
				stmt3.setInt(5, motivo_baja);					
				stmt3.setTimestamp(6, new java.sql.Timestamp(dado_baja.getTime()) );
			}else{
				stmt3.setNull(5, Types.INTEGER);
				stmt3.setNull(6, Types.TIMESTAMP);
			}
			stmt3.setString(7, "alta");
			stmt3.setString(8, userName);
			
			stmt3.executeUpdate();
			

		} catch (SQLException e) {
			_log.debug("Error al guardar afiliado", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateAfiliadoIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug("Error al guardar afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt3);
			if(stmt2 != null){
				ConnectionHelper.cerrar(stmt2);
			}
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		return;
	}
	/**
	 * metodo que actualiza un afiliado a partir de los parámetros , si no lo
	 * puede actualizar retorna null
	 * 
	 * @param connection
	 * 
	 * @throws NoSuchAfiliadoEntryException
	 * @throws SystemException
	 */
	public void actualizaAfiliadoConDomicilioEntry(Afiliado afi, Date bajaFecha, int motivo_baja, String actualizaDom, String actualizaAfi, 
			String preCarga, String idPreAfi, String userName, Connection connection)

			throws NoSuchAfiliadoEntryException, SystemException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null;
		try {
			String sql = "{call actualiza_afiliado_y_domi (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, afi.getCuil_titular());
			stmt.setInt(2, afi.getInte());
			stmt.setString(3, afi.getApellido().toUpperCase());
			stmt.setString(4, afi.getNombre().toUpperCase());
			stmt.setString(5, afi.getDocumento_tipo());
			stmt.setString(6, afi.getSexo().toUpperCase());
			stmt.setString(7, afi.getCuil());
			stmt.setDate(8, afi.getNaci_fecha() == null ? null : new java.sql.Date(
					afi.getNaci_fecha().getTime()));
			stmt.setInt(9, afi.getId_civil_esta());
			stmt.setInt(10, afi.getNacionalidad());
			stmt.setInt(11, afi.getId_parentesco());
			stmt.setInt(12, afi.getSeccional().getIdSeccional());
			stmt.setInt(13, afi.getAnterior_os());
			stmt.setString(14, userName);
			stmt.setString(15, afi.getDiscapacitado());
			stmt.setString(16, afi.getDocu_numero());
			stmt.setString(17, WebKeysAfiliados.DEFAULT_TIPO_DOMICILIO); // 0,
			// particular
			stmt.setString(18, afi.getDomicilioDefault().getCalle().toUpperCase());
			stmt.setString(19, afi.getDomicilioDefault().getPiso());
			stmt.setString(20, afi.getDomicilioDefault().getDepto());
			stmt.setNull(21, Types.CHAR);
			stmt.setString(22, afi.getDomicilioDefault().getPostal_codi());
			stmt.setString(23, afi.getDomicilioDefault().getBarrio());
			stmt.setString(24, afi.getDomicilioDefault().getTelefono());
			stmt.setString(25, afi.getObservaciones());
			stmt.setString(26, WebKeysAfiliados.DEFAULT_DOMICILIO_VALIDO); // 1,
			// valido
			stmt.setInt(27, afi.getDomicilioDefault().getProvinciaId());
			stmt.setInt(28, afi.getDomicilioDefault().getLocalidadId());
			stmt.setString(29, afi.getDomicilioDefault().getNumero());
			stmt.setString(30, actualizaDom);
			stmt.setDate(31, afi.getVigen_fecha() == null ? null : new java.sql.Date(
					afi.getVigen_fecha().getTime()));
			stmt.setString(32, actualizaAfi);
			stmt.setDate(33, bajaFecha == null ? null : new java.sql.Date(
					bajaFecha.getTime()));
			if (motivo_baja > 0) {
				stmt.setInt(34, motivo_baja);
			} else {
				stmt.setNull(34, Types.INTEGER);
			}
			stmt.setDate(35, afi.getId_ospim_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_ospim_baja_fecha().getTime()));
			stmt.setDate(36, afi.getId_uoma_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_uoma_baja_fecha().getTime()));
			stmt.setDate(37, afi.getId_amtima_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_amtima_baja_fecha().getTime()));
			stmt.setString(38, afi.getDomicilioDefault().getCod_area_telefono());
			stmt.setString(39, afi.getDomicilioDefault().getCod_area_celular());
			stmt.setString(40, afi.getDomicilioDefault().getCelular());
			stmt.setInt(41, afi.getCenso2013());
			stmt.setString(42, afi.getDomicilioDefault().getCod_area_tel_laboral());
			stmt.setString(43, afi.getDomicilioDefault().getTel_laboral());
			if(StringUtils.checkNotEmpty(afi.getEmail())){
				stmt.setString(44, afi.getEmail().toLowerCase());
			}else{
				stmt.setNull(44, Types.VARCHAR);
			}
			
			stmt.setInt(45, afi.getTieneAntecedentesJudiciales());
			stmt.setInt(46, afi.getClientePreferencial());
			stmt.setString(47, afi.getProyecto());
			
			stmt.executeUpdate();
			
//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
			
			stmt2 = con.prepareCall(sqlInsert.toString());
			stmt2.setString(1, afi.getCuil_titular());
			stmt2.setInt(2, afi.getInte());
			stmt2.setInt(3, afi.getIdCorrespondencia());
			stmt2.setNull(4, Types.TIMESTAMP); // se actualiza si imprime credenciales
			if(bajaFecha != null){ // cascada
				stmt2.setInt(5, motivo_baja);					
				stmt2.setTimestamp(6, new java.sql.Timestamp(bajaFecha.getTime()) );
			}else if(afi.getBaja_fecha() != null){ // para afiliados integrantes que vengan con baja futura
					stmt2.setInt(5, afi.getId_motivo_baja());					
					stmt2.setTimestamp(6, new java.sql.Timestamp(afi.getBaja_fecha().getTime()) );
				}else{
					stmt2.setNull(5, Types.INTEGER);
					stmt2.setNull(6, Types.TIMESTAMP);
				}
			stmt2.setString(7, "modificacion");
			stmt2.setString(8, userName);
			
			stmt2.executeUpdate();
			
			// SI VIENE DE OPCIONES ACTUALIZO LA TABLA DE OPCIONES
			String sqlUpdate = "{call novedades_sss.actualiza_marca_de_alta_portal(?, ?, ?, ?, ?) }";
//			if (null != opciones && opciones.trim().equals("true")) {
//				stmt3 = con.prepareCall(sqlUpdate.toString());
//				stmt3.setString(1, afi.getCuil_titular());
//				stmt3.setInt(2, afi.getInte());
//				stmt3.setString(3, "opciones");
//				stmt3.setNull(4, Types.INTEGER);
//				stmt3.setString(5, userName);
//				
//				stmt3.executeUpdate();
//			}
			if (null != preCarga && preCarga.trim().equals("true")) {
				stmt3 = con.prepareCall(sqlUpdate.toString());
				stmt3.setString(1, afi.getCuil_titular());
				stmt3.setInt(2, afi.getInte());
				stmt3.setString(3, "precarga");
				stmt3.setInt(4, Integer.parseInt(idPreAfi));
				stmt3.setString(5, userName);
				
				stmt3.executeUpdate();
			}
			
		} catch (SQLException e) {
			_log.debug("error al actualizar afiliado", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchAfiliadoEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug("error al actualizar afiliado", e);
			throw new SystemException(e);
		} finally {
			if(stmt3 != null){
				ConnectionHelper.cerrar(stmt3);
			}
			if (connection == null) {
				
				ConnectionHelper.cerrar(stmt);
				ConnectionHelper.cerrar(stmt2,con);
			}else{
				ConnectionHelper.cerrar(stmt);
				ConnectionHelper.cerrar(stmt2);
			}
			
		}
		return;
	}

	public void actualizaAfiliadoEntry(Afiliado afi, String preCarga, String idPreAfi, String userName, String actualizaAfi, Date bajaFecha,
			int motivo_baja, Connection connection)
			throws NoSuchAfiliadoEntryException, SystemException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null;
		try {
			String sql = "{call actualiza_afiliado (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, afi.getCuil_titular());
			stmt.setInt(2, afi.getInte());
			stmt.setString(3, afi.getApellido().toUpperCase());
			stmt.setString(4, afi.getNombre().toUpperCase());
			stmt.setString(5, afi.getDocumento_tipo());
			stmt.setString(6, afi.getSexo());
			stmt.setString(7, afi.getCuil());
			stmt.setDate(8, afi.getNaci_fecha() == null ? null : new java.sql.Date(
					afi.getNaci_fecha().getTime()));
			stmt.setInt(9, afi.getId_civil_esta());
			stmt.setInt(10, afi.getNacionalidad());
			stmt.setInt(11, afi.getId_parentesco());
			stmt.setInt(12, afi.getSeccional().getIdSeccional());
			stmt.setInt(13, afi.getAnterior_os());
			stmt.setString(14, userName);
			stmt.setString(15, afi.getDiscapacitado());
			stmt.setString(16, afi.getDocu_numero());
			stmt.setString(17, afi.getObservaciones());
			stmt.setDate(18, afi.getVigen_fecha() == null ? null : new java.sql.Date(
					afi.getVigen_fecha().getTime()));
			stmt.setString(19, actualizaAfi);
			stmt.setDate(20, bajaFecha == null ? null : new java.sql.Date(
					bajaFecha.getTime()));
			if (motivo_baja > 0) {
				stmt.setInt(21, motivo_baja);
			} else {
				stmt.setNull(21, Types.INTEGER);
			}
			stmt.setDate(22, afi.getId_ospim_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_ospim_baja_fecha().getTime()));
			stmt.setDate(23, afi.getId_uoma_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_uoma_baja_fecha().getTime()));
			stmt.setDate(24, afi.getId_amtima_baja_fecha() == null ? null
					: new java.sql.Date(afi.getId_amtima_baja_fecha().getTime()));
			stmt.setInt(25, afi.getTieneAntecedentesJudiciales());
			stmt.setInt(26, afi.getClientePreferencial());
			stmt.setString(27, afi.getProyecto());
			stmt.setString(28, afi.getEmail());
			
			
			stmt.executeUpdate();
			
//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
			
			stmt2 = con.prepareCall(sqlInsert.toString());
			stmt2.setString(1, afi.getCuil_titular());
			stmt2.setInt(2, afi.getInte());
			stmt2.setInt(3, afi.getIdCorrespondencia());
			stmt2.setNull(4, Types.TIMESTAMP); // se actualiza si imprime credenciales
			if(bajaFecha != null){ // baja_cascada
				stmt2.setInt(5, motivo_baja);					
				stmt2.setTimestamp(6, new java.sql.Timestamp(bajaFecha.getTime()) );
			}else if(afi.getBaja_fecha() != null){ // para afiliados integrantes que vengan con baja futura
					stmt2.setInt(5, afi.getId_motivo_baja());					
					stmt2.setTimestamp(6, new java.sql.Timestamp(afi.getBaja_fecha().getTime()) );
				}else{
					stmt2.setNull(5, Types.INTEGER);
					stmt2.setNull(6, Types.TIMESTAMP);
				}
			stmt2.setString(7, "modificacion");
			stmt2.setString(8, userName);
			
			stmt2.executeUpdate();
			
			// SI VIENE DE OPCIONES ACTUALIZO LA TABLA DE OPCIONES
			String sqlUpdate = "{call novedades_sss.actualiza_marca_de_alta_portal(?, ?, ?, ?, ?) }";
//			if (null != opciones && opciones.trim().equals("true")) {
//				stmt3 = con.prepareCall(sqlUpdate.toString());
//				stmt3.setString(1, afi.getCuil_titular());
//				stmt3.setInt(2, afi.getInte());
//				stmt3.setString(3, "opciones");
//				stmt3.setNull(4, Types.INTEGER);
//				stmt3.setString(5, userName);
//				
//				stmt3.executeUpdate();
//			}
			if (preCarga != null && preCarga.trim().equals("true")) {
				stmt3 = con.prepareCall(sqlUpdate.toString());
				stmt3.setString(1, afi.getCuil_titular());
				stmt3.setInt(2, afi.getInte());
				stmt3.setString(3, "precarga");
				stmt3.setInt(4, Integer.parseInt(idPreAfi));
				stmt3.setString(5, userName);
				
				stmt3.executeUpdate();
			}
			
			
		} catch (SQLException e) {
			_log.debug("error al actualizar afiliado", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchAfiliadoEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug("error al actualizar afiliado", e);
			throw new SystemException(e);
		} finally {
			if (connection == null) {
				
				ConnectionHelper.cerrar(stmt,con);
				ConnectionHelper.cerrar(stmt2);
			}else{
				ConnectionHelper.cerrar(stmt);
				ConnectionHelper.cerrar(stmt2);
			}
			if(stmt3 != null){
				ConnectionHelper.cerrar(stmt3);
			}
		}
		return;
	}

	/**
	 * Metodo que aplica borrado lógico de un afiliado a partir de la clave
	 * primaria, no borra el afiliado físicamente, solo lo da de baja
	 * 
	 * @throws NoSuchAfiliadoEntryException
	 * @throws SystemException
	 */
	public void borraAfiliadoEntry(String cuil_titular, int inte,
			int motivo_baja, Date baja_fecha, String userName)
			throws NoSuchAfiliadoEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null, stmt2=null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call delete_afiliado(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.setInt(3, motivo_baja);
			stmt.setDate(4, baja_fecha == null ? null : new java.sql.Date(
					baja_fecha.getTime()));
			stmt.setString(5, userName);
			stmt.executeUpdate();
			
//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";	  
			
			stmt2 = con.prepareCall(sqlInsert.toString());
			stmt2.setString(1, cuil_titular);
			stmt2.setInt(2, inte);
			stmt2.setNull(3, Types.INTEGER);
			stmt2.setNull(4, Types.TIMESTAMP);
			stmt2.setInt(5, motivo_baja);					
			stmt2.setTimestamp(6, new java.sql.Timestamp(baja_fecha.getTime()) );
			stmt2.setString(7, "baja");
			stmt2.setString(8, userName);		
			
			stmt2.executeUpdate();
			
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchAfiliadoEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2, con);
		}
		return;
	}

	/**
	 * Borra fisicamente un integrante de un grupo familiar 
 	 * 
	 * @param cuil_titular
	 * @param inte
	 * @throws SystemException
	 */
	
	public void deleteAfiliadoEntry(String cuil_titular, int inte) throws  SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borrar_afiliado_fisicamente(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
				throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return;
	}
	
	/**
	 * Metodo que sugiere el próximo numero de integrante para un Cuil_titular
	 * dado
	 * 
	 * @throws SystemException
	 */
	public int getProximoIntePorCuil(String cuil_titular)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{? = call trae_ultimo_inte_por_cuiltitular(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, cuil_titular);
			stmt.executeUpdate();
			result = stmt.getInt(1);
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result + 1;
	}

	public int getTieneConyugeGrupoCuil(String cuil_titular, Date vigenFecha, 
			Connection connectionParameter) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			String sql = "{? = call tiene_conyuge_cuit(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, cuil_titular);
			stmt.setInt(3, WebKeysAfiliados.CONCUBINO_DEFAULT);
			stmt.setInt(4, WebKeysAfiliados.CONYUGE_DEFAULT);
			stmt.setDate(5, new java.sql.Date(vigenFecha.getTime()));
			


			stmt.executeUpdate();
			
			result = stmt.getInt(1);
			
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return result;
	}

	/**
	 * Método que trae un afiliado VIGENTE por el cuil, solo lo trare si baja
	 * fecha es nula o baja futura,
	 * 
	 * @return si no se encuentra ningún afiliado vigente, retorna rulo
	 */
//	TODO que es esta porqueria ???
	public Afiliado getAfiliadoXCuil(String cuil) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Afiliado afiliado = null;
		try {
			String sql = "{call busca_afiliado_por_cuil(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				afiliado = new Afiliado();
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
		} catch (Exception e) {
			afiliado = null;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}
	
	public Afiliado getAfiliadoXCuilPorVigenFecha(String cuil, Date vigenFecha) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Afiliado afiliado = null;
		try {
			String sql = "{call busca_afiliado_por_cuil_vigen_fechas(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setDate(2, new java.sql.Date(vigenFecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				afiliado = new Afiliado();
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
		} catch (Exception e) {
			afiliado = null;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}
	
	
	public Afiliado getAfiliadoXCuilInte(String cuil) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Afiliado afiliado = null;
		try {
			String sql = "{call busca_afiliado_existe_cuil_inte(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				afiliado = new Afiliado();
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
		} catch (Exception e) {
			afiliado = null;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}

	
	public Afiliado getAfiliadoXCuilIntePorVigenFecha(String cuil, Date vigenFecha) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Afiliado afiliado = null;
		try {
			String sql = "{call busca_afiliado_existe_cuil_inte(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				afiliado = new Afiliado();
				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
		} catch (Exception e) {
			afiliado = null;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return (afiliados.size() == 0) ? null : afiliados.get(0);
	}

	/**
	 * Metodo que obtiene un afiliado de la tabla opciones de la SuPER a partir
	 * de la clave primaria
	 * 
	 * @param connection
	 * 
	 * @throws SystemException
	 * @throws NoSuchAfiliadoEntryException
	 */
	public Afiliado getAfiliadoOpciones(String cuil_titular, int nroFormulario)
			throws SystemException, NoSuchAfiliadoEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Domicilio afiDomicilio = null;
		try {
			String sql = "{call buscar_afiliado_opciones(?,?)}";
			con = ConnectionHelper.getConnection();

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, nroFormulario);
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				afiliado = new Afiliado();
				afiDomicilio = new Domicilio();
				afiliado.setCuil_titular(cuil_titular);
				afiliado.setInte(0);
				afiliado.setId_ospim(rs.getInt("id_ospim"));
				afiliado.setId_uoma(rs.getInt("id_uoma"));
				afiliado.setId_amtima(rs.getInt("id_amtima"));
				afiliado.setApellido(rs.getString("apellido"));
				afiliado.setNombre(rs.getString("nombre"));
				afiliado.setDocumento_tipo(rs.getString("documento_tipo"));
				afiliado.setSexo(rs.getString("sexo"));
				afiliado.setCuil(rs.getString("cuil") != null ? rs.getString("cuil") : "");
				// afiliado.setNaci_fecha(rs.getDate("naci_fecha"));
				afiliado.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
				afiliado.setCivil_esta(rs.getString("civil_esta"));
				afiliado.setNacionalidad(rs.getInt("nacionalidad"));
				afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
				afiliado.setParentesco(rs.getString("parentesco"));
				afiliado.setSeccional(new Seccional());
				afiliado.getSeccional().setId_seccional(rs.getInt("id_seccional"));
				afiliado.getSeccional().setDescripcion(rs.getString("descripcion"));
				afiliado.setAnterior_os(rs.getInt("anterior_os"));
				afiliado.setVigen_fecha(rs.getDate("vigen_fecha"));
				afiliado.setObservaciones(rs.getString("observaciones"));
				afiliado.setAlta_usr(rs.getString("alta_usr"));
				afiliado.setModi_usr(rs.getString("modi_usr"));
				afiliado.setDiscapacitado(rs.getString("discapacitado"));
				afiliado.setDocu_numero(rs.getString("docu_numero") != null ? rs.getString("docu_numero") : "");
				afiliado.setEmail(rs.getString("email") != null ? rs.getString("email") : "");
				afiDomicilio.setDomi_tipo(rs.getString("domi_tipo"));
				afiDomicilio.setCalle(rs.getString("calle"));
				afiDomicilio.setPiso(rs.getString("piso") != null ? rs.getString("piso") : "");
				afiDomicilio.setDepto(rs.getString("depto") != null ? rs.getString("depto") : "");
				afiDomicilio.setOficina(rs.getString("oficina") != null ? rs.getString("oficina") : "");
				afiDomicilio.setPostal_codi(rs.getString("postal_codi")!=null ? rs.getString("postal_codi"):"");
				afiDomicilio.setBarrio(rs.getString("barrio") != null ? rs.getString("barrio") : "");
				afiDomicilio.setCod_area_telefono(rs.getString("cod_area_telefono") != null ? rs.getString("cod_area_telefono") : "");
				afiDomicilio.setTelefono(rs.getString("telefono") != null ? rs.getString("telefono") : "");
				afiDomicilio.setCod_area_tel_laboral(rs.getString("cod_area_tel_laboral") != null ? rs.getString("cod_area_tel_laboral") : "");
				afiDomicilio.setTel_laboral(rs.getString("tel_laboral") != null ? rs.getString("tel_laboral") : "");
				afiDomicilio.setCod_area_celular(rs.getString("cod_area_celular") != null ? rs.getString("cod_area_celular") : "");
				afiDomicilio.setCelular(rs.getString("celular") != null ? rs.getString("celular") : "");
				afiDomicilio.setObservaciones(rs.getString("observaciones_dom"));
				afiDomicilio.setDomi_val(rs.getString("domi_val"));
				afiDomicilio.setAlta_usr(rs.getString("alta_usr_d"));
				afiDomicilio.setModi_usr(rs.getString("modi_usr_d"));
				afiDomicilio.setProvinciaId(rs.getInt("provincia"));
				afiDomicilio.setLocalidadId(rs.getInt("localidad"));
				afiDomicilio.setNumero(rs.getString("numero")!=null&&!rs.getString("numero").equalsIgnoreCase("0")?rs.getString("numero"):"");
				afiliado.setAportante_titular(rs.getInt("aportante_titular"));
				afiliado.setBaja_fecha(rs.getDate("baja_f"));
				afiliado.setBaja_usr(rs.getString("baja_u"));
				afiliado.setIngre_fecha(rs.getDate("ingre_f"));
				afiliado.setDomicilioDefault(afiDomicilio);
				afiliado.setId_motivo_baja(rs.getInt("id_motivo_baja"));
				afiliado.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				afiliado.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				afiliado.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				afiliado.setProyecto(rs.getString("proyecto"));
				String cuit = rs.getString("cuit");
				String razon_soc = rs.getString("razon_soc");
				if (null != cuit) {
					Empresa empresa = new Empresa(cuit, "000", razon_soc);
					List<SituacionLaboral> sitlist = new ArrayList<SituacionLaboral>();
					SituacionLaboral sl = new SituacionLaboral(empresa, null,
							null);// SituacionLaboral sl=new
									// SituacionLaboral(empresa,
									// afiliado.getIngre_fecha(),null);
					sitlist.add(sl);
					afiliado.setLista_situ_laboral(sitlist);
				}

				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			if (afiliados.size() == 0) {
				throw new NoSuchAfiliadoEntryException(
						"No se ha encontrado un afiliado con esa clave primaria");
			}
		} catch (NoSuchAfiliadoEntryException e) {
			_log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return afiliados.get(0);
	}

	public DetalleDiscapacidad getDetalleDiscapacidadEntry(String cuil_titular,
			int inte, Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		DetalleDiscapacidad dd = new DetalleDiscapacidad();
		
		try {
			String sql = "{call busca_detalle_discapacidad_por_cuil_inte(?,?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				dd = DetalleDiscapacidad.getMapping(rs, "");
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return dd;
	}

	public static void actualizaDetalleDiscapacidad(
			DetalleDiscapacidad detalleDiscap, String username)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_detalle_discapacidad (?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, detalleDiscap.getCuil_titular());
			stmt.setInt(2, detalleDiscap.getInte());
			stmt.setString(3, detalleDiscap.getDiagnostico());
			stmt.setBoolean(4, detalleDiscap.isDependencia());
			stmt.setString(5, detalleDiscap.getTelefono_contacto());
			stmt.setString(6, username);
			stmt.setString(7, detalleDiscap.getCie_diez());
			stmt.setString(8, detalleDiscap.getTiposDiscapacidadDelAfiliado());
			
			stmt.executeUpdate();

		} catch (Exception e) {
			_log.error("Error al editar detalle discapacidad de afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	public static boolean estaVigenteEnOtroGrupoFliar(String cuil_titular, String inte)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call esta_vigente_en_otro_grupo_fliar(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, Integer.valueOf(inte));
			
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar vigencia de afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}
	
	public static boolean verificaIntegranteUnificaAportesGrupoFliar(String cuil_titular)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "select * from afiliado a, afi_situ_laboral asl "+
						 "where a.cuil_titular=asl.cuil_titular "+
						 "and a.cuil_titular='"+cuil_titular+"' and a.inte <>0 "+
						 "and a.aportante_titular=1 "+
						 "and asl.fecha_egre is null"; 
			
			stmt = con.prepareStatement(sql.toString());

			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar unificacion aportes de grupo familiar", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}
	
	public static String buscarObservacionesGrupoFliar(String cuil_titular, int inte)
			throws SystemException {

		String result = "";
		String obs = "";
		int integrante = 0;
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "select inte, observaciones from afiliado a"+
						 " where a.cuil_titular='"+ cuil_titular +
						 "' and a.inte <>"+ inte +
						 " order by inte";
						  
			stmt = con.prepareStatement(sql.toString());

			ResultSet rs = stmt.executeQuery();

			while(rs.next()){
			
				integrante = rs.getInt(1);
				obs = rs.getString(2);
				
				if(!StringUtils.checkEmpty(obs)){
					result += "integrante /"+integrante+ " " + obs;
				}
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar unificacion aportes de grupo familiar", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	/**
	 * getAfiliadosPorDocumento: a partir de esta funcion permito a una novedad de SSS, 
	 * elegir el cuil inte que corresponde para poder ingresar modo Edicion de Afiliado
	 * 
	 * @param nroDoc
	 * @param documento_tipo
	 * @return
	 * @throws SystemException
	 */
	public List<Afiliado> getAfiliadosPorDocumento(String nroDoc, String documento_tipo) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();

		try {
			String sql = "{call buscar_afiliado_por_doc_y_tipo(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
//			stmt.setString(1, nroDoc);
//			stmt.setString(2, documento_tipo);
			stmt.setString(1, documento_tipo);
			stmt.setInt(2, Integer.parseInt(nroDoc));
			
			stmt.setDate(3, new java.sql.Date(1220227200));  //returns 1 Jan 1970
			
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
				
				afiliados.add(afiliado);
			}

		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiliados;
	}

	public List<Afiliado> getAfiliadosPorDocumentoInclusoDadoDeBaja(String nroDoc, String documento_tipo) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();

		try {
			String sql = "{call buscar_afiliado_por_doc_y_tipo_incluso_dado_de_baja(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
//			stmt.setString(1, nroDoc);
//			stmt.setString(2, documento_tipo);
			stmt.setString(1, documento_tipo);
			stmt.setInt(2, Integer.parseInt(nroDoc));
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
				
				afiliados.add(afiliado);
			}

		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiliados;
	}

	public Afiliado getAfiliadoPreCarga(String cuil_titular, Integer inte, Integer idPreAfi)
			throws SystemException, NoSuchAfiliadoEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		Domicilio afiDomicilio = null;
		try {
			String sql = "{call novedades_sss.buscar_pre_afiliado_por_id(?, ?, ?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuil_titular);
			if(inte != null){
				stmt.setInt(2, inte);
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			if(idPreAfi != null){
				stmt.setInt(3, idPreAfi);
			}else{
				stmt.setNull(3, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				afiliado = new Afiliado();
				afiDomicilio = new Domicilio();
				afiliado.setCuil_titular(cuil_titular);
				afiliado.setInte(rs.getInt("inte"));
//				afiliado.setId_ospim(rs.getInt("id_ospim"));
//				afiliado.setId_uoma(rs.getInt("id_uoma"));
//				afiliado.setId_amtima(rs.getInt("id_amtima"));
				afiliado.setApellido(rs.getString("apellido"));
				afiliado.setNombre(rs.getString("nombre"));
				afiliado.setDocumento_tipo(rs.getString("documento_tipo"));
				afiliado.setSexo(rs.getString("sexo"));
				afiliado.setCuil(rs.getString("cuil") != null ? rs.getString("cuil") : "");
				afiliado.setNaci_fecha(rs.getDate("naci_fecha"));
				afiliado.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
//				afiliado.setCivil_esta(rs.getString("civil_esta"));
				afiliado.setNacionalidad(rs.getInt("nacionalidad"));
				afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
//				afiliado.setParentesco(rs.getString("parentesco"));
				afiliado.setSeccional(new Seccional());
				afiliado.getSeccional().setId_seccional(rs.getInt("id_seccional"));
//				afiliado.getSeccional().setDescripcion(rs.getString("descripcion"));
//				afiliado.setAnterior_os(rs.getInt("anterior_os"));
				afiliado.setAnterior_os(0); // fozamos porque no tenemos en pre_carga este dato
				afiliado.setVigen_fecha(rs.getDate("vigen_fecha"));
				afiliado.setObservaciones(rs.getString("observaciones"));
//				afiliado.setPres_ssalud_fecha(rs.getDate("pres_ssalud_fecha"));
//				afiliado.setAlta_usr(rs.getString("alta_usr"));
//				afiliado.setModi_usr(rs.getString("modi_usr"));
				afiliado.setDiscapacitado(rs.getString("discapacitado"));
				afiliado.setDocu_numero(rs.getString("documento_numero") != null ? rs.getString("documento_numero") : "");
				afiliado.setEmail(rs.getString("email") != null ? rs.getString("email") : "");
				afiDomicilio.setDomi_tipo(rs.getString("domi_tipo"));
				afiDomicilio.setCalle(rs.getString("calle"));
				afiDomicilio.setPiso(rs.getString("piso") != null ? rs.getString("piso") : "");
				afiDomicilio.setDepto(rs.getString("depto") != null ? rs.getString("depto") : "");
//				afiDomicilio.setOficina(rs.getString("oficina") != null ? rs.getString("oficina") : "");
				afiDomicilio.setPostal_codi(rs.getString("postal_codi"));
				afiDomicilio.setBarrio(rs.getString("barrio") != null ? rs.getString("barrio") : "");
				afiDomicilio.setCod_area_telefono(rs.getString("cod_area_telefono"));
				afiDomicilio.setTelefono(rs.getString("telefono") != null ? rs.getString("telefono") : "");
				afiDomicilio.setCod_area_tel_laboral(rs.getString("cod_area_tel_laboral"));
				afiDomicilio.setTel_laboral(rs.getString("tel_laboral") != null ? rs.getString("tel_laboral") : "");
				afiDomicilio.setCod_area_celular(rs.getString("cod_area_celular"));
				afiDomicilio.setCelular(rs.getString("celular") != null ? rs.getString("celular") : "");
//				afiDomicilio.setObservaciones(rs.getString("observaciones_dom"));
//				afiDomicilio.setDomi_val(rs.getString("domi_val"));
//				afiDomicilio.setAlta_usr(rs.getString("alta_usr_d"));
//				afiDomicilio.setModi_usr(rs.getString("modi_usr_d"));
				afiDomicilio.setProvinciaId(rs.getInt("id_provincia"));
				afiDomicilio.setLocalidadId(rs.getInt("id_localidad"));
				afiDomicilio.setNumero(rs.getString("numero"));
//				afiliado.setAportante_titular(rs.getInt("aportante_titular"));
//				afiliado.setBaja_fecha(rs.getDate("baja_f"));
//				afiliado.setBaja_usr(rs.getString("baja_u"));
//				afiliado.setIngre_fecha(rs.getDate("ingre_f")); // fecha que se carga?
				afiliado.setIngre_fecha(new Date()); // fecha que se carga?
				afiliado.setDomicilioDefault(afiDomicilio);
//				afiliado.setId_motivo_baja(rs.getInt("id_motivo_baja"));
//				afiliado.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
//				afiliado.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
//				afiliado.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				String cuit = rs.getString("cuit");
				String sucu = rs.getString("sucursal");
				String razon_soc = rs.getString("razon_social");
				int idRevista = rs.getInt("id_revista");
				int idCategoria = rs.getInt("id_categoria");
				String escalaSalarial = rs.getString("escala_salarial");

				Date fecha_ingr_laboral = rs.getDate("fecha_ingre");
				
				if (null != cuit) {
					Empresa empresa = new Empresa(cuit, sucu, razon_soc);
					List<SituacionLaboral> sitlist = new ArrayList<SituacionLaboral>();
					SituacionLaboral sl = new SituacionLaboral(afiliado, empresa, fecha_ingr_laboral, null, "", "",
							idCategoria, idRevista, null, escalaSalarial);
					sitlist.add(sl);
					afiliado.setLista_situ_laboral(sitlist);
				}
				 //Plan
				int idPlan = rs.getInt("id_plan");
				
				Plan p = PlanServiceUtil.getInstance().buscaPlanPorId(idPlan);
				
				AfiPlan ultimoPlan = new AfiPlan();
//			    p.setId(rs.getInt("id_plan"));
//			    p.setDescripcion(rs.getString("plan"));
			    
				if(p != null){
					ultimoPlan.setPlan(p);
			    	ultimoPlan.setVigenDesde(rs.getDate("plan_vigen_desde"));
			    	ultimoPlan.setVigenHasta(rs.getDate("plan_vigen_hasta"));
//			    	ultimoPlan.setId_motivo_baja(rs.getInt("plan_id_motivo_baja"));
			    	ultimoPlan.setId_plan_omint(p.getId_plan_omint());
			    	ultimoPlan.setMotivoBaja(new MotivoBaja(rs.getInt("plan_id_motivo_baja"), rs.getString("motivo_baja")));
			    
			    	afiliado.setAfiPlan(ultimoPlan);
				}
			    //Tercerizadora
			    afiliado.setId_tercerizadora(rs.getString("id_tercerizadora"));
			    afiliado.setDesc_tercerizadora(rs.getString("tercerizadora"));
			    
			    afiliado.setTipoOperacion(rs.getString("tipo_novedad"));
			    
//			    pat.setId_tercerizadora(rs.getString("id_tercerizadora"));
//			    pat.setTercerizadora(rs.getString("tercerizadora"));
//			    pat.setFecha_inicio_prestacion(rs.getDate("fecha_inicio_prestacion"));
//			    pat.setFecha_fin_prestacion(rs.getDate("fecha_fin_prestacion"));
			    
//			    pat.setDe_alta_portal(rs.getBoolean("de_alta_portal"));
//			    pat.setTipo_novedad(rs.getString("tipo_novedad"));

				afiliados.add(afiliado);
			}
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			if (afiliados.size() == 0) {
				throw new NoSuchAfiliadoEntryException(
						"No se ha encontrado un afiliado con esa clave primaria");
			}
		} catch (NoSuchAfiliadoEntryException e) {
			_log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiliados.get(0);
	}
	
	public static void actualizaDomicilio(String cuil_titular, int inte, Domicilio domicilio, String email, String username)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_domicilio_afiliado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.setString(3, domicilio.getDomi_tipo());
			stmt.setString(4, domicilio.getCalle().toUpperCase());
			stmt.setString(5, domicilio.getPiso());
			stmt.setString(6, domicilio.getDepto().toUpperCase());
			stmt.setString(7, domicilio.getPostal_codi());
			stmt.setString(8, domicilio.getBarrio().toUpperCase());
			stmt.setString(9, domicilio.getDomi_val());
			stmt.setInt(10, domicilio.getProvinciaId());
			stmt.setInt(11, domicilio.getLocalidadId());
			stmt.setString(12, domicilio.getNumero());
			stmt.setString(13, domicilio.getCod_area_telefono());
			stmt.setString(14, domicilio.getTelefono());
			stmt.setString(15, domicilio.getCod_area_celular());
			stmt.setString(16, domicilio.getCelular());
			stmt.setString(17, domicilio.getCod_area_tel_laboral());
			stmt.setString(18, domicilio.getTel_laboral());
			if(StringUtils.checkNotEmpty(email)){
				stmt.setString(19, email.toLowerCase());
			}else{
				stmt.setNull(19, Types.VARCHAR);
			}
			
			stmt.setInt(20, domicilio.getId_domicilio());
			stmt.setString(21, username);
			stmt.executeUpdate();

		} catch (Exception e) {
			_log.error("Error al actualizar domicilio de afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	public static void actualizaDomicilio2(String cuil_titular, int inte, Domicilio domicilio, String email, String username)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_domicilio_afiliado2(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.setString(3, domicilio.getDomi_tipo());
			stmt.setString(4, domicilio.getCalle().toUpperCase());
			stmt.setString(5, domicilio.getPiso());
			stmt.setString(6, domicilio.getDepto().toUpperCase());
			stmt.setString(7, domicilio.getPostal_codi());
			stmt.setString(8, domicilio.getBarrio().toUpperCase());
			stmt.setString(9, domicilio.getDomi_val());
			stmt.setInt(10, domicilio.getProvinciaId());
			stmt.setInt(11, domicilio.getLocalidadId());
			stmt.setString(12, domicilio.getNumero());
			stmt.setString(13, domicilio.getCod_area_telefono());
			stmt.setString(14, domicilio.getTelefono());
			stmt.setString(15, domicilio.getCod_area_celular());
			stmt.setString(16, domicilio.getCelular());
			stmt.setString(17, domicilio.getCod_area_tel_laboral());
			stmt.setString(18, domicilio.getTel_laboral());
			if(StringUtils.checkNotEmpty(email)){
				stmt.setString(19, email.toLowerCase());
			}else{
				stmt.setNull(19, Types.VARCHAR);
			}
			
			stmt.setInt(20, domicilio.getId_domicilio());
			stmt.setString(21, username);
			stmt.executeUpdate();

		} catch (Exception e) {
			_log.error("Error al actualizar domicilio de afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	public static void updateVigenDesde(Connection connectionParameter, String cuil_titular, Integer inte, Date vigenFecha, String username){
			
		Connection con = null;
		CallableStatement stmt = null;

		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection() ;
			}else{
				con = connectionParameter;
			}
			String sql = "{call actualiza_vigen_desde_grupo_fliar(?, ?, ?, ?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(vigenFecha.getTime()));
			stmt.setString(4, username);

			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public static void updateBajaFecha(Connection connectionParameter, String cuil_titular, Integer inte, Date bajaFecha, Integer idMotivoBaja, String username){
		Connection con = null;
		CallableStatement stmt = null;

		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection() ;
			}else{
				con = connectionParameter;
			}
			String sql = "{call actualiza_baja_fecha_grupo_fliar(?, ?, ?, ?, ?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			if(bajaFecha==null){
				stmt.setNull(3, Types.DATE);
				stmt.setNull(4, Types.INTEGER);
			}else{
				stmt.setDate(3, new java.sql.Date(bajaFecha.getTime()));
				stmt.setInt(4, idMotivoBaja);
			}
			stmt.setString(5, username);

			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public static List<Domicilio> historicoDomicilios(String cuilTitular) throws Exception {
				
		List<Domicilio> historico = new ArrayList<Domicilio>();;
		Domicilio d = null;
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnection();

			String sql = "{call buscar_historico_domicilios(?)}";
					
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) { 
				
				d = Domicilio.getMapping(rs, "afidom_");
				historico.add(d);
			}

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return historico;
	}
	
	public Afiliado getAfiliadoInclusoDadoBajaPorCuil(String cuil,
			Connection connectionParameter) throws SystemException,
			NoSuchAfiliadoEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		Connection conLportal = null;
		List<Afiliado> afiliados = new ArrayList<Afiliado>();
		try {
			String sql = "{call busca_afiliado_incluso_dado_baja_por_cuil(?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			conLportal = ConnectionHelper.getLPortalConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet rs = stmt.executeQuery();
			Afiliado afiliado = null;
			while (rs.next()) {
				
				afiliado = Afiliado.getMappingAfiliadoConDomicilioyDocDiscapacidad(rs);
				
				// Veo si tiene imágenes
				String sqlImage = "{call tiene_imagen_afiliado(?)}";
				stmt = conLportal.prepareCall(sqlImage.toString());
				stmt.setString(1, cuil);
				ResultSet rsImage = stmt.executeQuery();
				while (rsImage.next()) {
					afiliado.setFolderid(rsImage.getInt(1));
					afiliado.setTitle(rsImage.getString(2));
					afiliado.setTiene_imagen(rsImage.getInt(3));
				}

				afiliados.add(afiliado);
			}
			
			/*
			if (afiliados.size() > 1) {
				String cause = "Hay un problema de inconsistencia de datos, se ha encontrado más de un afiliado con esa clave primaria, "
						+ "el problema pudo ser causado porque no hay datos correctos relacionados con la vigencia de domicilios, "
						+ "esto se pudo inyectar por concurrencia, esto es válido solo hasta que se tengan en cuenta domicilios vigentes "
						+ "y tipos de domicilios";
				_log.debug(cause);
				throw new SystemException(cause);
			}
			*/
			if (afiliados.size() == 0) {
				throw new NoSuchAfiliadoEntryException(
						"No se ha encontrado un afiliado con esa clave primaria");
			}
		} catch (NoSuchAfiliadoEntryException e) {
			_log.debug(e.getMessage());
			throw e;
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(conLportal);
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return afiliados.get(0);
	}


	public DetalleOpcionesSS getOpcionSssPorCuil(String cuilOpcion) {
		Connection con = null;
		DetalleOpcionesSS det = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_opcion_sss_por_cuil(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilOpcion);	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				det = DetalleOpcionesSS.getMapping("opsss_", rs);
			}
		} catch (Exception e) {
			_log.error("error al buscar detalle de Opcion SS solo por CUIL", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return det;
	}
	
	
	public Integer updateAporteMonotributo(AportesMonotributo aporte,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		CallableStatement stmt1 = null;
		CallableStatement stmt2 = null;
	    Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  aporte_monotributo_categorias_update(?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setInt(1,aporte.getId());
			stmt.setInt(2,aporte.getCategoria());
			if(aporte.getDesde() !=null) {
			   stmt.setDate(3, new java.sql.Date(aporte.getDesde().getTime() ));
			}else {
			   stmt.setNull(3, Types.DATE);	
			}
			
			if(aporte.getHasta() !=null) {
			   stmt.setDate(4, new java.sql.Date(aporte.getHasta().getTime() ));
			}else {
			   stmt.setNull(4, Types.DATE);	
			}
			
			if(aporte.getAporte()==null) {
				stmt.setNull(5, Types.DOUBLE );
			}else{
				stmt.setDouble(5, aporte.getAporte());   
			}
			if(usr == null || usr  =="") {
				stmt.setNull(6, Types.VARCHAR );
			}else{
				stmt.setString(6, usr);   
			}
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
			
			if(aporte.getId()==0) aporte.setId(ret);
			
			/*
			if(!aporte.getClases().isEmpty()) {
				String sql1 = "{call  aporte_monotributo_categorias_clases_delete_by_padre(?)}";
				stmt1 = con.prepareCall(sql1.toString());
				stmt1.setInt(1,aporte.getId());
				stmt1.executeQuery();
			}
			*/
			
			String sql2 = "{call  aporte_monotributo_categorias_clases_update(?,?,?,?,?,?,?)}";
			for(AportesMonotributoClase c:aporte.getClases()) {
				stmt2 = con.prepareCall(sql2.toString());
				stmt2.setInt(1,c.getId());
				stmt2.setInt(2,aporte.getId());
				stmt2.setInt(3, aporte.getCategoria());
				stmt2.setString(4, c.getClase());
				stmt2.setDate(5, new java.sql.Date(c.getDesde().getTime() ));
				stmt2.setDate(6, new java.sql.Date(c.getHasta().getTime() ));
				stmt2.setDouble(7, c.getAporte());
				stmt2.executeQuery();
			}
			
			
			Boolean borrar=false;
			for(AportesMonotributoClase cc: aporte.getClasesOriginal()) {
				borrar=true;
				for(AportesMonotributoClase c:aporte.getClases()) {
				  if(c.getId().equals(cc.getId())) {
					borrar=false;
					break;
				  }
				}
				
				if (borrar) {
					String sql3 = "{call  aporte_monotributo_categorias_clases_delete_by_id(?)}";
					stmt1 = con.prepareCall(sql3.toString());
					stmt1.setInt(1,cc.getId());
					stmt1.executeQuery();
				}
			}
			
			
		} catch (Exception e) {
			_log.error("Error al actualizar Aportes Monotributo Escalas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}


	
	
	public Integer deleteAporteMonotributo(AportesMonotributo aporte)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		CallableStatement stmt2 = null;
	    Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  aporte_monotributo_categorias_clases_delete_by_padre(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,aporte.getId());
			stmt.executeQuery();
			
			String sql2 = "{call  aporte_monotributo_categorias_delete(?)}";
			stmt2 = con.prepareCall(sql2.toString());
			stmt2.setInt(1,aporte.getId());
			stmt2.executeQuery();
			
				
		} catch (Exception e) {
			_log.error("Error al deletear Aportes Monotributo Escalas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public void reenviarAutorizacionesRecortadasPorBaja(String cuilTitular, Date bajaFecha, int motivoBaja, String usuario)
	        throws SystemException {

	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    List<Integer> idsTratamiento = new ArrayList<Integer>();

	    try {
	        con = ConnectionHelper.getConnection();

	        String sql =
	            "SELECT id_autorizacion_prestacional " +
	            "FROM autorizaciones.buscar_autorizaciones_recortadas_por_baja(?,?,?)";

	        ps = con.prepareStatement(sql);
	        ps.setString(1, cuilTitular);
	        ps.setDate(2, new java.sql.Date(bajaFecha.getTime()));
	        ps.setInt(3, 10);

	        rs = ps.executeQuery();
	        while (rs.next()) {
	            idsTratamiento.add(rs.getInt("id_autorizacion_prestacional"));
	        }

	    } catch (Exception e) {
	        _log.error("Error buscando autorizaciones recortadas en histo", e);
	        throw new SystemException(e);
	    } finally {
	        ConnectionHelper.cerrar(ps, con);
	    }

	    if (idsTratamiento.isEmpty()) {
	        _log.info("No hay autorizaciones recortadas para reenviar. cuil=" + cuilTitular);
	        return;
	    }

	    for (Integer idTratamiento : idsTratamiento) {
	        try {
	            AutorizacionPrestacionalEmail.getInstance().enviarMailsAutorizacion(idTratamiento, true);
	        } catch (Exception ex) {
	            _log.error("Fallo reenviando mails para tratamiento id=" + idTratamiento, ex);
	        }
	    }
	}





}