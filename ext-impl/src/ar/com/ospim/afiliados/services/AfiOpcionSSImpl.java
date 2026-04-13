package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.FormOpcionSSSDuplicadoException;
import ar.com.ospim.afiliados.FormOpcionSSSNoEnviadoException;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

public class AfiOpcionSSImpl {

	private static Log logger = LogFactoryUtil.getLog(AfiOpcionSSImpl.class);
	
	public static int insertarOpcionSS(DetalleOpcionesSS det, String user) throws SQLException{
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call inserta_opcion_sss(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, det.getTipoExportacion());
			stmt.setInt(2, det.getDelegacionId());
			stmt.setString(3, det.getDelegacion());
			stmt.setInt(4, det.getLibro());
			stmt.setInt(5, det.getTomo());
			stmt.setInt(6, det.getNroFormulario());
			stmt.setInt(7, det.getOsElegida());
			stmt.setString(8, det.getRegimen());
			stmt.setString(9, det.getCuil());
			stmt.setString(10, det.getApellido());
			stmt.setString(11, det.getNombre());
			stmt.setString(12, det.getSexo());
			stmt.setString(13, det.getCalle());
			stmt.setString(14, det.getNumero());
			stmt.setInt(15, det.getPiso());
			stmt.setString(16, det.getDepartamento());
			stmt.setString(17, det.getLocalidad());
			stmt.setString(18, det.getProvincia());
			stmt.setString(19, det.getCodAreaTelParticular());
			stmt.setString(20, det.getTelParticular());
			stmt.setString(21, det.getCodAreaTelLaboral());
			stmt.setString(22, det.getTelLaboral());
			stmt.setString(23, det.getCodAreaCelular());
			stmt.setString(24, det.getTelCelular());
			stmt.setString(25, det.getEmail());
			stmt.setInt(26, det.getOsAnterior());
			stmt.setString(27, det.getCuit());
			if (det.getFechaElecc() != null) {
				stmt.setDate(28, new java.sql.Date(det.getFechaElecc().getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			if (det.getFechaCerti() != null) {
				stmt.setDate(29, new java.sql.Date(det.getFechaCerti().getTime()));
			} else {
				stmt.setNull(29, Types.DATE);
			}
			stmt.setString(30, det.getVersionSistema());
			stmt.setString(31, det.getCod_postal());
			stmt.setString(32, det.getUnificaApo());
			stmt.setString(33, det.getCuilConyuge());
			stmt.setString(34, det.getApeNomConyuge());
			if(StringUtils.checkEmpty(det.getProyecto())){
				stmt.setNull(35, Types.VARCHAR);
			}else{
				stmt.setString(35, det.getProyecto());
			}
			stmt.setString(36, user);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
		
	}
	
	public static int actualizarOpcionSS(DetalleOpcionesSS det, String user) throws SQLException{
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_opcion_sss(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, det.getTipoExportacion());
			stmt.setInt(2, det.getDelegacionId());
			stmt.setString(3, det.getDelegacion());
			stmt.setInt(4, det.getLibro());
			stmt.setInt(5, det.getTomo());
			stmt.setInt(6, det.getNroFormulario());
			stmt.setInt(7, det.getOsElegida());
			stmt.setString(8, det.getRegimen());
			stmt.setString(9, det.getCuil());
			stmt.setString(10, det.getApellido());
			stmt.setString(11, det.getNombre());
			stmt.setString(12, det.getSexo());
			stmt.setString(13, det.getCalle());
			stmt.setString(14, det.getNumero());
			stmt.setInt(15, det.getPiso());
			stmt.setString(16, det.getDepartamento());
			stmt.setString(17, det.getLocalidad());
			stmt.setString(18, det.getProvincia());
			stmt.setString(19, det.getCodAreaTelParticular());
			stmt.setString(20, det.getTelParticular());
			stmt.setString(21, det.getCodAreaTelLaboral());
			stmt.setString(22, det.getTelLaboral());
			stmt.setString(23, det.getCodAreaCelular());
			stmt.setString(24, det.getTelCelular());
			stmt.setString(25, det.getEmail());
			stmt.setInt(26, det.getOsAnterior());
			stmt.setString(27, det.getCuit());
			if (det.getFechaElecc() != null) {
				stmt.setDate(28, new java.sql.Date(det.getFechaElecc().getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			if (det.getFechaCerti() != null) {
				stmt.setDate(29, new java.sql.Date(det.getFechaCerti().getTime()));
			} else {
				stmt.setNull(29, Types.DATE);
			}
			stmt.setString(30, det.getVersionSistema());
			stmt.setString(31, det.getCod_postal());
			stmt.setString(32, det.getUnificaApo());
			stmt.setString(33, det.getCuilConyuge());
			stmt.setString(34, det.getApeNomConyuge());
			if(StringUtils.checkEmpty(det.getProyecto())){
				stmt.setNull(35, Types.VARCHAR);
			}else{
				stmt.setString(35, det.getProyecto());
			}
			stmt.setString(36, user);
			stmt.setInt(37, Integer.parseInt(String.valueOf(det.getId())));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
		
	}
		
	
	
	public DetalleOpcionesSS getOpcionSssPorCuil(String cuilOpcion,String nroForm) {
		Connection con = null;
		DetalleOpcionesSS det = null;
		CallableStatement stmt = null;
		try {
			//String sql = "{call buscar_opcion_sss_por_cuil(?)}";
			String sql = "{call buscar_opcion_sss_por_cuil_nroForm(?,?)}";			
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilOpcion);
			stmt.setString(2, nroForm);	
				
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				det = DetalleOpcionesSS.getMapping("opsss_", rs);
			}
		} catch (Exception e) {
			logger.debug("error al buscar detalle de Opcion SS", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return det;
	}
	
	public Delegacion getDelegacionPorId(int idDelegacion) {
		Connection con = null;
		Delegacion deleg = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_delegacion_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idDelegacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				deleg = Delegacion.getMapping(rs,"");
			}
		} catch (Exception e) {
			logger.debug("error al buscar delegacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return deleg;
	}
	/**
	 * RETURN -1 nro formulario no enviado, 0 nro formulario repetido, nroFomulario si esta ok para utilizar
	 * 
	 * */
	public int validarNroFormDuplicado(int nroFormulario, int idOpcionSSS) throws FormOpcionSSSDuplicadoException, FormOpcionSSSNoEnviadoException{
		/* Tuve que validar 2 cosas: Validar que se envie el bono a alguna seccional y si existe el envio que no exista rendicion previa */
		Connection con = null;
		CallableStatement stmt = null;
		Integer nroBono = 0, idOpcAux=null;
		java.sql.Date fechaRendicion = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call validar_nro_formulario_opcion_sss(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, nroFormulario);
			stmt.setInt(2, idOpcionSSS);
			
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) { // se espera uno o ningun resultado (ningun resultado = no se envio bono)
				
				nroBono = Integer.valueOf(rs.getInt(1));
				fechaRendicion = rs.getDate(2);
				idOpcAux = rs.getInt(3);
				
				if(fechaRendicion != null && nroFormulario == nroBono && idOpcionSSS != idOpcAux){ // entonces esta repetido
					return 0;
				}	
			}else{ // ningun resultado 
				return -1;
			}
		}catch (NumberFormatException e) {
			throw new FormOpcionSSSNoEnviadoException("Nro de formulario no enviado");		
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return nroBono;
	}
	
	public String validarOpcionSSS(String cuilOpcSss, int nroFormulario, String regimen, Date fechaCertificacion, int idOpcionSSS){
		
		Connection con = null;
		CallableStatement stmt = null;
		String result = "";
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call validar_opcion_sss(?, ?, ?, ?, ?)}";

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilOpcSss);
			stmt.setInt(2, nroFormulario);
			stmt.setString(3, regimen);
			stmt.setDate(4, new java.sql.Date(fechaCertificacion.getTime()));
			stmt.setInt(5, idOpcionSSS);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
		
	}
	
	public void eliminarOpcionSS(String cuil, String nroFormulario, String screenName)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call eliminar_opcion_sss(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setString(2, nroFormulario);
			stmt.setString(3, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al borrar opcion afi", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al borrar opcion afi", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public static int verificaCantidadFormulariosOpcionExportadosSSS() throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int cantidadExportados = 0;
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call verifica_cantidad_exportacion_opciones_sss()}";
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantidadExportados = rs.getInt(1) ;
			}
			
		} catch (SQLException e) {
			logger.error("Error al verifica cantidad exportacion opciones sss", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al verifica cantidad exportacion opciones sss", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return cantidadExportados;
	}
	
	public static int volverAtrasFormulariosOpcionExportadosSSS()
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call volver_atras_exportacion_opciones_sss()}";
			stmt = con.prepareCall(sql.toString());

			result = stmt.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("Error al volver atras exportacion opciones sss", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al volver atras exportacion opciones sss", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}
	
	public void recuperarOpcionSS(String cuil, String nroFormulario, String screenName)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call recuperar_opcion_sss(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setString(2, nroFormulario);
			stmt.setString(3, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al recuperar opcion afi", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al recuperar opcion afi", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	
}
