package ar.com.ospim.novedades.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.novedades.beans.BusquedaPreAfiliadosFiltro;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.novedades.beans.PreAfiliadoTotal;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PreAfiliadoServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(PreAfiliadoServiceImpl.class);

	public static int insertaPreAfiliadoEntry(PreAfiliado preAfi, String userName, int empresa_usr)
					throws SystemException, DuplicateAfiliadoIdException {
		
		Connection con = null;
		CallableStatement stmt = null;
		int inte_obtenido = -1;
		
		try {
			String sql = "{call novedades_sss.inserta_pre_carga_afiliado (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, preAfi.getCuil_titular());
			if(preAfi.getInte() != null && !StringUtils.checkEmpty(preAfi.getInte())){
				stmt.setInt(2, preAfi.getInte()); //Se resuelve por script BD si corresponde p novedad pre_afiliado, para afiliado del padron mismo inte
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			
			stmt.setString(3, preAfi.getCuil());
			stmt.setString(4, preAfi.getApellido().toUpperCase());
			stmt.setString(5, preAfi.getNombre().toUpperCase());
			stmt.setInt(6, preAfi.getId_parentesco_sss());
			stmt.setInt(7, preAfi.getId_estado_civil_sss());
			stmt.setString(8, preAfi.getDocumento_tipo());
			stmt.setString(9, preAfi.getDocumento_numero());
			stmt.setString(10, preAfi.getSexo().toUpperCase());
			stmt.setString(11, preAfi.getDiscapacitado());
			stmt.setInt(12, preAfi.getNacionalidad());
			stmt.setDate(13, new java.sql.Date(preAfi.getNaci_fecha().getTime()));
			stmt.setInt(14, preAfi.getId_seccional());
			stmt.setDate(15, new java.sql.Date(preAfi.getVigen_fecha().getTime()));
			stmt.setString(16, preAfi.getObservaciones());
			stmt.setString(17, preAfi.getEmail());
			stmt.setString(18, WebKeysAfiliados.DEFAULT_TIPO_DOMICILIO); // 0,
			stmt.setString(19, preAfi.getCalle().toUpperCase());
			stmt.setString(20, preAfi.getNumero());
			stmt.setString(21, preAfi.getPiso());
			stmt.setString(22, preAfi.getDepto());
			stmt.setString(23, preAfi.getPostal_codi());
			stmt.setString(24, preAfi.getBarrio());
			stmt.setInt(25, preAfi.getId_provincia());
			stmt.setInt(26, preAfi.getId_localidad());
			stmt.setString(27, preAfi.getCod_area_telefono());
			stmt.setString(28, preAfi.getTelefono());
			stmt.setString(29, preAfi.getCod_area_celular());
			stmt.setString(30, preAfi.getCelular());
			stmt.setString(31, preAfi.getCod_area_tel_laboral());
			stmt.setString(32, preAfi.getTel_laboral());
			stmt.setString(33, preAfi.getCuit());
			stmt.setString(34, preAfi.getSucursal());
			stmt.setDate(35, new java.sql.Date(preAfi.getFecha_ingre().getTime()));
			stmt.setInt(36, preAfi.getId_revista());
			stmt.setInt(37, preAfi.getId_categoria());
			stmt.setString(38, preAfi.getEscala_salarial());
			if(preAfi.getId_plan() != null){
				stmt.setInt(39, preAfi.getId_plan());
				stmt.setDate(40, new java.sql.Date(preAfi.getVigenDesde().getTime()));
				if(preAfi.getVigenHasta() != null){
					stmt.setDate(41, new java.sql.Date(preAfi.getVigenHasta().getTime()));
				}else{
					stmt.setNull(41, Types.DATE);
				}
				stmt.setInt(42, preAfi.getId_motivo_baja());
				stmt.setString(43, preAfi.getId_tercerizadora());
				stmt.setDate(44, new java.sql.Date(preAfi.getFecha_inicio_prestacion().getTime()));
				if(preAfi.getFecha_fin_prestacion() != null){
					stmt.setDate(45, new java.sql.Date(preAfi.getFecha_fin_prestacion().getTime()));
				}else{
					stmt.setNull(45, Types.DATE);
				}
				
			}else{
				stmt.setNull(39, Types.INTEGER);
				stmt.setNull(40, Types.DATE);
				stmt.setNull(41, Types.DATE);
				stmt.setNull(42, Types.INTEGER);
				stmt.setNull(43, Types.VARCHAR);
				stmt.setNull(44, Types.DATE);
				stmt.setNull(45, Types.DATE);
			}
			stmt.setString(46, preAfi.getTipo_novedad());
			stmt.setString(47, userName);
			stmt.setInt(48, empresa_usr);

			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				inte_obtenido = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.debug("Error al guardar pre-afiliado", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateAfiliadoIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			logger.debug("Error al guardar pre-afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return inte_obtenido;
	}
	
	
	public List<PreAfiliadoTotal> buscarPreCargaAfiliados(BusquedaPreAfiliadosFiltro filtro) throws SystemException{
		
		List<PreAfiliadoTotal> listaBusq = new ArrayList<PreAfiliadoTotal>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.buscar_pre_cargas_afiliados(?, ? ,? ,? ,? ,? ,? ,? ,? )}";
					
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getId() == null || filtro.getId() == 0){
				stmt.setNull(1, Types.INTEGER);
			}else{
				stmt.setInt(1, filtro.getId());
			}
			stmt.setString(2, filtro.getCuilTitular());
			if(filtro.getInte() == null){
				stmt.setNull(3, Types.VARCHAR);
			}else{
				stmt.setString(3, filtro.getInte());
			}
			if(filtro.getFechaDesde() == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}
			if(filtro.getFechaHasta() == null){
				stmt.setNull(5, Types.DATE);
			}else{
				stmt.setDate(5, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}
			stmt.setInt(6, filtro.getSeccional_int());
			stmt.setInt(7, filtro.getEstado());
			if(filtro.getEmpresa_usr() == null){
				stmt.setNull(8, Types.DOUBLE);
			}else{
				stmt.setDouble(8, filtro.getEmpresa_usr());
			}	
			stmt.setInt(9, filtro.getPagina());

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				PreAfiliadoTotal pat = PreAfiliadoTotal.getMapping(rs);
				
				listaBusq.add(pat);
			}

		} catch (SQLException s) {
			logger.error("error al buscar pre-carga de afiliados", s);			
		} catch (Exception e) {
			logger.error("error al buscar pre-carga de afiliados", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaBusq;
	}

	public int existePreAfiliado(String cuil) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call novedades_sss.existe_pre_afiliado(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}
	
	public static void borrarPreAfiliado(String cuil, int inte, int id, boolean esCascada, String screenName) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call novedades_sss.borrar_pre_afiliado(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setInt(3, id);
			stmt.setBoolean(4, esCascada);
			stmt.setString(5, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al 	borrar pre-afiliado", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al borrar pre-afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public static PreAfiliadoTotal buscarPreAfiliado(String cuil_titular, Integer inte, Integer idPreAfi)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		PreAfiliadoTotal pa = new PreAfiliadoTotal();
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
			if (rs.next()) {
				pa = PreAfiliadoTotal.getMapping(rs);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("error al buscar pre-afiliado x ID", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return pa;
	}

	public static void actualizaPreAfiliado(PreAfiliado preAfi, String screenname) throws SystemException{
		
		Connection con = null;
		CallableStatement stmt = null;
	
		try {		
			con = ConnectionHelper.getConnection();
			String sql = "{call novedades_sss.actualiza_pre_afiliado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, preAfi.getId());
			stmt.setString(2, preAfi.getCuil_titular());
			stmt.setInt(3, preAfi.getInte());
			stmt.setString(4, preAfi.getApellido().toUpperCase());
			stmt.setString(5, preAfi.getNombre().toUpperCase());
			stmt.setInt(6, preAfi.getId_parentesco_sss());
			stmt.setInt(7, preAfi.getId_estado_civil_sss());
			stmt.setString(8, preAfi.getSexo().toUpperCase());
			stmt.setString(9, preAfi.getDiscapacitado());
			stmt.setInt(10, preAfi.getNacionalidad());
			stmt.setDate(11, new java.sql.Date(preAfi.getNaci_fecha().getTime()));
			stmt.setInt(12, preAfi.getId_seccional());
			stmt.setDate(13, new java.sql.Date(preAfi.getVigen_fecha().getTime()));
			stmt.setString(14, preAfi.getObservaciones());
			stmt.setString(15, preAfi.getEmail());
			stmt.setString(16, preAfi.getCalle().toUpperCase());
			stmt.setString(17, preAfi.getNumero());
			stmt.setString(18, preAfi.getPiso());
			stmt.setString(19, preAfi.getDepto());
			stmt.setString(20, preAfi.getPostal_codi());
			stmt.setString(21, preAfi.getBarrio());
			stmt.setInt(22, preAfi.getId_provincia());
			stmt.setInt(23, preAfi.getId_localidad());
			stmt.setString(24, preAfi.getCod_area_telefono());
			stmt.setString(25, preAfi.getTelefono());
			stmt.setString(26, preAfi.getCod_area_celular());
			stmt.setString(27, preAfi.getCelular());
			stmt.setString(28, preAfi.getCod_area_tel_laboral());
			stmt.setString(29, preAfi.getTel_laboral());			
			stmt.setString(30, preAfi.getCuit());			
			stmt.setString(31, preAfi.getSucursal());			
			stmt.setDate(32, new java.sql.Date(preAfi.getFecha_ingre().getTime()));
			stmt.setInt(33, preAfi.getId_revista());			
			stmt.setInt(34, preAfi.getId_categoria());			
			stmt.setString(35, preAfi.getEscala_salarial());
			if(preAfi.getId_plan() != null){
				stmt.setInt(36, preAfi.getId_plan());
				stmt.setDate(37, new java.sql.Date(preAfi.getVigenDesde().getTime()));
				if(preAfi.getVigenHasta() != null){
					stmt.setDate(38, new java.sql.Date(preAfi.getVigenHasta().getTime()));
				}else{
					stmt.setNull(38, Types.DATE);
				}
				stmt.setInt(39, preAfi.getId_motivo_baja());
				stmt.setString(40, preAfi.getId_tercerizadora());
				stmt.setDate(41, new java.sql.Date(preAfi.getFecha_inicio_prestacion().getTime()));
				if(preAfi.getFecha_fin_prestacion() != null){
					stmt.setDate(42, new java.sql.Date(preAfi.getFecha_fin_prestacion().getTime()));
				}else{
					stmt.setNull(42, Types.DATE);
				}
				
			}else{
				stmt.setNull(36, Types.INTEGER);
				stmt.setNull(37, Types.DATE);
				stmt.setNull(38, Types.DATE);
				stmt.setNull(39, Types.INTEGER);
				stmt.setNull(40, Types.VARCHAR);
				stmt.setNull(41, Types.DATE);
				stmt.setNull(42, Types.DATE);
			}
			
			stmt.setString(43, screenname);			
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al actualizar pre-afiliado", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al actualizar pre-afiliado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
}
