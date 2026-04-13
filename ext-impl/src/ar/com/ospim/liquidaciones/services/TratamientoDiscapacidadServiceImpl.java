package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.Documento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.DuplicateTratamientoDiscapacidadIdException;
import ar.com.ospim.liquidaciones.ImposibleBorrarTratamientoDiscapacidadException;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class TratamientoDiscapacidadServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(TratamientoDiscapacidadServiceImpl.class);

	public List<TratamientoDiscapacidad> buscarTratamientosDiscapacidad(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador, String cuit,
			String prestador, int numero, int estado, String codPrestaci) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<TratamientoDiscapacidad> listaLiquidaciones = null;
		listaLiquidaciones = new ArrayList<TratamientoDiscapacidad>();
		
		//CAMBIAR EL STORED
		try {
			String sql = "{call autorizaciones.buscar_tratamientos_discapacidad(?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setString(3, cuil_titular);
			if (inte == 0) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, inte);
			}
			if (entidad != null && entidad.length() == 0) {
				entidad = null;}
			stmt.setString(5, entidad);
			if (nroAfi == 0) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, nroAfi);
			}
			stmt.setString(7, cuit);
			stmt.setString(8, prestador);
			stmt.setInt(9, estado);
			stmt.setString(10, codPrestaci);
			stmt.setBoolean(11, true);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TratamientoDiscapacidad tratamiento = TratamientoDiscapacidad
						.getMapping(rs, "td_");
				tratamiento.setPrestacion(Prestacion.getMapping(rs, "n_"));				
				tratamiento.getPrestacion().setId_prestacion(rs.getInt("td_" + "id_prestacion"));
				tratamiento.setAfiliado(Afiliado.getMapping(rs, "a_"));
				listaLiquidaciones.add(tratamiento);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidaciones;
	}

	public List<TratamientoDiscapacidad> buscarTratamientosDiscapacidad(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador, String cuit,
			String prestador, int numero, int estado, String codPrestaci,boolean incluyeAntiguos) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<TratamientoDiscapacidad> listaLiquidaciones = null;
		listaLiquidaciones = new ArrayList<TratamientoDiscapacidad>();
		
		//CAMBIAR EL STORED
		try {
			String sql = "{call autorizaciones.buscar_tratamientos_discapacidad(?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setString(3, cuil_titular);
			if (inte == 0) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, inte);
			}
			if (entidad != null && entidad.length() == 0) {
				entidad = null;}
			stmt.setString(5, entidad);
			if (nroAfi == 0) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, nroAfi);
			}
			stmt.setString(7, cuit);
			stmt.setString(8, prestador);
			stmt.setInt(9, estado);
			stmt.setString(10, codPrestaci);
			stmt.setBoolean(11, incluyeAntiguos);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TratamientoDiscapacidad tratamiento = TratamientoDiscapacidad
						.getMapping(rs, "td_");
				tratamiento.setPrestacion(Prestacion.getMapping(rs, "n_"));				
				tratamiento.getPrestacion().setId_prestacion(rs.getInt("td_" + "id_prestacion"));
				tratamiento.setAfiliado(Afiliado.getMapping(rs, "a_"));
				listaLiquidaciones.add(tratamiento);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidaciones;
	}

	
	public TratamientoDiscapacidad getTratamientoDiscapacidad(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		TratamientoDiscapacidad td = null;
		try {
			String sql = "{call buscar_tratamiento_discapacidad_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				td = TratamientoDiscapacidad.getMapping(rs, "td_");
				td.setPrestacion(Prestacion.getMapping(rs, "n_"));
				td.getPrestacion().setId_prestacion(rs.getInt("td_" + "id_prestacion"));
				td.setAfiliado(Afiliado.getMapping(rs, "a_"));
				
				Prestador prestador = new Prestador();
				try{
				   prestador = PrestadorServiceUtil.getPrestador(rs.getInt("td_" + "id_prestador"));
				}catch(Exception e){
					_log.error("Error al buscar prestador del tratamiento", e);
				}   
				td.setPrestador(prestador);
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar tratamiento", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return td;
	}

	/**
	 * Metodo que obtiene la lista de docuentos faltantes a partir de la clave primaria
	 * del tratamiento
	 * 
	 * @throws SystemException
	 */
	public List<Documento> getDocFaltanteTratamientoDiscapacidad(
			int id_tratamiento) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;		
		List<Documento> documentoItems = new ArrayList<Documento>();
		try {
			String sql = "{call busca_documentos_faltantes_por_id_tratamiento(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_tratamiento);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Documento doc = new Documento(rs.getInt("id_documento"), "");										
				documentoItems.add(doc);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener documentación faltante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return documentoItems;
	}
	
	public int save(int id_prestacion, String cuil, int inte, BigDecimal cantidad,
			BigDecimal importe_total, String periodicidad, Date periodo_desde,
			Date periodo_hasta, String userName,
			String cuit, String prestador, String id_seccional, String observaciones, boolean recupera_ape, int estado,
			BigDecimal cantidad_viajes_mes, BigDecimal cantidad_kilometros_dia, BigDecimal cantidad_kilometros_mes, BigDecimal importe_kilometro_unit, BigDecimal hs_espera_dia,
			BigDecimal hs_espera_mes, BigDecimal importe_hs_espera_unit, BigDecimal importe_tercerizado, String id_tercerizadora,
			int id_prestador,String esExcepcion) throws SystemException,
			DuplicateTratamientoDiscapacidadIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			
			String sql = "{call inserta_tratamiento_discapacidad (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			_log.debug("Obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestacion);
			stmt.setString(2, cuil);
			stmt.setInt(3, inte);
			stmt.setBigDecimal(4, cantidad);
			stmt.setString(5, periodicidad);
			stmt.setDate(6, periodo_desde != null ? new java.sql.Date(periodo_desde.getTime()) : null);
			stmt.setDate(7, periodo_hasta != null ? new java.sql.Date(periodo_hasta.getTime()) : null);
			stmt.setBigDecimal(8, importe_total);
			stmt.setString(9, userName);
			stmt.setInt(10, id_prestador);	
			stmt.setString(11, observaciones);
			stmt.setBoolean(12, recupera_ape);
			stmt.setInt(13, estado);
			stmt.setString(14, cuit);
			stmt.setString(15, prestador);
			stmt.setString(16, id_seccional);			
			stmt.setBigDecimal(17, cantidad_viajes_mes);
			stmt.setBigDecimal(18, cantidad_kilometros_dia);
			stmt.setBigDecimal(19, cantidad_kilometros_mes);
			stmt.setBigDecimal(20, importe_kilometro_unit);
			stmt.setBigDecimal(21, hs_espera_dia);
			stmt.setBigDecimal(22, hs_espera_mes);
			stmt.setBigDecimal(23, importe_hs_espera_unit);
			stmt.setBigDecimal(24, importe_tercerizado);
			stmt.setString(25, id_tercerizadora);
			stmt.setBoolean(26, esExcepcion.equalsIgnoreCase("SI")?true:false);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar tratamiento discapacidad", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateTratamientoDiscapacidadIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar tratamiento discapacidad", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}

	public void update(int id_tratamiento, int id_prestacion, String cuil,
			int inte, BigDecimal cantidad, BigDecimal importe_total,
			String periodicidad, Date periodo_desde, Date periodo_hasta,
			String userName,
			String cuit, String prestador, String id_seccional, String observaciones, boolean recupera_ape, int estado, int[] documentacion,
			BigDecimal cantidad_viajes_mes, BigDecimal cantidad_kilometros_dia, BigDecimal cantidad_kilometros_mes, BigDecimal importe_kilometro_unit, BigDecimal hs_espera_dia,
			BigDecimal hs_espera_mes, BigDecimal importe_hs_espera_unit, BigDecimal importe_tercerizado, String id_tercerizadora,int id_prestador,
			String esExcepcion
		) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_tratamiento_discapacidad (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_prestacion);
			stmt.setString(2, cuil);
			stmt.setInt(3, inte);
			stmt.setBigDecimal(4, cantidad);
			stmt.setString(5, periodicidad);
			stmt.setDate(6, new java.sql.Date(periodo_desde.getTime()));
			stmt.setDate(7, new java.sql.Date(periodo_hasta.getTime()));
			stmt.setString(8, userName);
			stmt.setBigDecimal(9, importe_total);
			stmt.setInt(10, id_tratamiento);
			stmt.setInt(11, id_prestador);
			stmt.setString(12, observaciones);
			stmt.setBoolean(13, recupera_ape);
			stmt.setInt(14, estado);
			stmt.setString(15, cuit);
			stmt.setString(16, prestador);
			stmt.setString(17, id_seccional);
			stmt.setBigDecimal(18, cantidad_viajes_mes);
			stmt.setBigDecimal(19, cantidad_kilometros_dia);
			stmt.setBigDecimal(20, cantidad_kilometros_mes);
			stmt.setBigDecimal(21, importe_kilometro_unit);
			stmt.setBigDecimal(22, hs_espera_dia);
			stmt.setBigDecimal(23, hs_espera_mes);
			stmt.setBigDecimal(24, importe_hs_espera_unit);
			stmt.setBigDecimal(25, importe_tercerizado);
			stmt.setString(26, id_tercerizadora);
			stmt.setBoolean(27, esExcepcion.equalsIgnoreCase("SI")?true:false);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al actualizar tratamiento discapacidad", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void borrar(int id, String screenName) throws SQLException,
			ImposibleBorrarTratamientoDiscapacidadException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_tratamiento_discapacidad(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarTratamientoDiscapacidadException();
				}
			}
		} catch (ImposibleBorrarTratamientoDiscapacidadException e) {
			_log.error("Error al borrar tratamiento", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	/**
	 * @throws SystemException
	 */
	public void borrarDocumentosFaltantes(int id_tratamiento) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_documentos_faltantes_tratamiento(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_tratamiento);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * @throws SystemException
	 */
	public void cargarDocumentosFaltantes(int id_tratamiento, int id_documento, String usuario) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call carga_documento_faltante_tratamiento(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_tratamiento);
			stmt.setInt(2, id_documento);
			stmt.setString(3, usuario);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo cambia el estado a estado en el parámetro
	 * 
	 * @throws SystemException
	 */
	public void cambiarEstadoTratamiento(int id_tratamiento, int estado,
			String userName) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call cambio_estado_tratamiento(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_tratamiento);
			stmt.setInt(2, estado);
			stmt.setString(3, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);			
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

}