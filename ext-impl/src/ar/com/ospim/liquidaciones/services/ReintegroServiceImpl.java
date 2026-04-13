package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroIdException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.beans.DetalleCuota;
import ar.com.ospim.liquidaciones.beans.PlanPrestacion;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * servicio test que nos da acceso a los datos de la aplicación (BD).
 * 
 */
public class ReintegroServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ReintegroServiceImpl.class);

	/**
	 * Metodo que obtiene un reintegro a partir de la clave primaria, en caso de
	 * que está dado de baja o de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public Reintegro getReintegroEntry(int id_reintegro)
			throws SystemException, NoSuchReintegroEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		Reintegro reintegro = null;
		Domicilio afiDomicilio = null;
		Afiliado afiliado = null;
		Seccional seccional = null;

		try {
			String sql = "{call busca_reintegro_header_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reintegro = Reintegro.getMapping(rs, "r_");
				afiliado = Afiliado.getMapping(rs, "a_");
				afiDomicilio = Domicilio.getMappingAfiDomicilio(rs, "ad_");
				afiliado.setDomicilioDefault(afiDomicilio);
				seccional = Seccional.getMappingSeccionalParaReintegros(rs,
						"s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);
			}
		} catch (Exception e) {
			_log.error("Error al obtener reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegro;
	}

	/**
	 * Metodo que obtiene la lista de prestaciones a partir de la clave primaria
	 * del reintegro, en caso de no encontrarla arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public List<ReintegroPrestacionNormal> getPrestacionesReintegroEntry(
			int id_reintegro) throws SystemException {	
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		PlanPrestacion planPrestacion = null;
		List<ReintegroPrestacionNormal> reintegroPrestaciones = new ArrayList<ReintegroPrestacionNormal>();
		try {
			String sql = "{call busca_prestaciones_reintegro_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ReintegroPrestacionNormal reintegroPrestacion = ReintegroPrestacionNormal
						.getMapping(rs, "rp_");
				reintegroPrestacion.setId_reintegro(id_reintegro);
				// prestador = Prestador.getMapping(rs, "pd_");
				// prestador.setId_prestador(reintegroPrestacion.getId_prestador());
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				planPrestacion = PlanPrestacion.getMapping(rs, "pp_");
				planPrestacion.setNomenclador(prestacion);
				reintegroPrestacion.setId_reclamo_prestacional(prestacion.getIdReclamopPrestacional() );
				reintegroPrestacion.setId_prestacion_reclamo(prestacion.getIdPrestacionReclamoPrestacional());
				reintegroPrestacion.setPlan_prestacion(planPrestacion);
				// reintegroPrestacion.setPrestador(prestador);
				reintegroPrestaciones.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegroPrestaciones;
	}

	/**
	 * Metodo que obtiene la lista de prestaciones de protesis a partir de la
	 * clave primaria del reintegro, en caso de no encontrarla arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public List<ReintegroPrestacionOdoProtesis> getPrestacionesReintegroOdoProtesisEntry(
			int id_reintegro) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		PlanPrestacion planPrestacion = null;
		List<ReintegroPrestacionOdoProtesis> reintegroPrestaciones = new ArrayList<ReintegroPrestacionOdoProtesis>();
		try {
			String sql = "{call busca_prestaciones_reintegro_odo_protesis_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ReintegroPrestacionOdoProtesis reintegroPrestacion = ReintegroPrestacionOdoProtesis
						.getMapping(rs, "rp_");
				reintegroPrestacion.setId_reintegro(id_reintegro);
				// prestador = Prestador.getMapping(rs, "pd_");
				// prestador.setId_prestador(reintegroPrestacion.getId_prestador());
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				planPrestacion = PlanPrestacion.getMapping(rs, "pp_");
				planPrestacion.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(planPrestacion);
				// reintegroPrestacion.setPrestador(prestador);
				reintegroPrestaciones.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones de protesis", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegroPrestaciones;
	}

	/**
	 * Metodo que obtiene la lista de prestaciones de ortopedia y ortodoncia a
	 * partir de la clave primaria del reintegro, en caso de no encontrarla
	 * arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public List<ReintegroPrestacionOdoOrtopediaOrtodoncia> getPrestacionesReintegroOdoOrtopediaOrtodonciaEntry(
			int id_reintegro) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		PlanPrestacion planPrestacion = null;
		List<ReintegroPrestacionOdoOrtopediaOrtodoncia> reintegroPrestaciones = new ArrayList<ReintegroPrestacionOdoOrtopediaOrtodoncia>();
		try {
			String sql = "{call busca_prestaciones_reintegro_orto_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = ReintegroPrestacionOdoOrtopediaOrtodoncia
						.getMapping(rs, "rp_");
				reintegroPrestacion.setId_reintegro(id_reintegro);
				// prestador = Prestador.getMapping(rs, "pd_");
				// prestador.setId_prestador(reintegroPrestacion.getId_prestador());
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				planPrestacion = PlanPrestacion.getMapping(rs, "pp_");
				planPrestacion.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(planPrestacion);
				// reintegroPrestacion.setPrestador(prestador);
				reintegroPrestaciones.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones de protesis", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegroPrestaciones;
	}

	/**
	 * Metodo que obtiene la lista de detalles de las cuotas de los tratamientos
	 * de ortopedia y ortodoncia a partir de la clave primaria del reintegro que
	 * es el el id del tratamiento en este caso, en caso de no encontrarla
	 * arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public List<DetalleCuota> getDetalleCuotaReintegroOdoOrtopediaOrtodonciaEntry(
			int id_reintegro) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<DetalleCuota> detalleCuotas = new ArrayList<DetalleCuota>();

		try {
			String sql = "{call busca_detalle_cuota_reintegro_orto_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				detalleCuotas.add(detalleCuota);
			}
		} catch (Exception e) {
			_log.debug("Error al buscar detalle cuota de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return detalleCuotas;
	}

	/**
	 * Metodo que obtiene un detalles de la cuota de los tratamientos de
	 * ortopedia y ortodoncia a partir del numero de reintegro que está en la
	 * cuota como id_cuota
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public DetalleCuota getDetalleCuotaReintegroOrtoEntry(int id_reintegro)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		DetalleCuota detalleCuota = null;

		try {
			String sql = "{call busca_detalle_cuota_reintegro_orto_por_id_cuota(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				detalleCuota = DetalleCuota.getMapping(rs, "dc_");
			}
		} catch (Exception e) {
			_log.debug("Error al buscar detalle cuota de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return detalleCuota;
	}

	/**
	 * metodo que carga un nuevo afiliado a partir de los parámetros")); si no
	 * lo puede insertar retorna null
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 */

	public int cargaReintegroEntry(Date fecha, Date periodo, String entidad,
			String cuil_titular, int inte, int seccional,
			String tipo_reintegro, int estado, String userName, Date dado_baja,
			String usr_baja, String obs, String cbu,String cuilCuenta ,
			String emailCuenta, String apellidoCuenta, String nombreCuenta ,Connection connectionParameter) throws SystemException,
			DuplicateReintegroIdException {		
		
		Connection con = null;
		CallableStatement stmt = null;
		int id_reintegro = 0;
		try {
			String sql = "{call inserta_reintegro (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			if(connectionParameter==null){
				con= ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			stmt.setDate(4, new java.sql.Date(periodo.getTime()));
			stmt.setInt(5, seccional);
			stmt.setString(6, userName);
			stmt.setInt(7, estado);
			stmt.setString(8, entidad);
			stmt.setString(9, tipo_reintegro);
			stmt.setString(10, obs);
			stmt.setString(11, cbu);
			stmt.setString(12, cuilCuenta);
			stmt.setString(13, emailCuenta);
			stmt.setString(14, apellidoCuenta);
			stmt.setString(15, nombreCuenta);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_reintegro = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al guardar reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al guardar reintegro", e);
			throw new SystemException(e);
		} finally{
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_reintegro;
	}

	/**
	 * metodo que carga una nueva prestacion a partir de los parámetros si no lo
	 * puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public int cargaReintegroPrestacionEntry(String cuilTitular, int inte, int idReintegro, String cuit, String descripcion, 
		int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucu, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, String cuitEntidad, String sucuEntidad, Date comprobanteFecha, 
		BigDecimal importeComprobante, int motivoAltaDiscapacidad, BigDecimal cargoOspim, BigDecimal cargoOspimPrestadora, 
		BigDecimal cargoImesa,Connection con) throws SystemException, DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {
		
		CallableStatement stmt = null;
		// TEMPORAL, el plan por ahora es uno siempre, en la solución final
		// deberíamos incorporar una cartilla
		int idPlan = 1;
		
		int idReintegroRenglon = 0;
		
		try {
		
			String sql = "{call inserta_prestacion (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			// id_plan = aporteService.getPlanAfiliado(con, cuil_titular, inte,
			// periodo).getId();
			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, idPlan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setDate(15, new java.sql.Date(periodo.getTime()));
			stmt.setString(16, cuitEntidad);
			stmt.setString(17, sucuEntidad);
			stmt.setDate(18, new java.sql.Date(comprobanteFecha.getTime()));
			stmt.setBigDecimal(19, importeComprobante);
			stmt.setInt(20, motivoAltaDiscapacidad);
			stmt.setBigDecimal(21, cargoOspim);
			stmt.setBigDecimal(22, cargoOspimPrestadora);
			stmt.setString(23, comproaDebitarSucu);
			stmt.setString(24, comproaDebitarLetra);
			stmt.setBigDecimal(25, cargoImesa);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				idReintegroRenglon = rs.getInt(1);
			}
			
			
		} catch (SQLException e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		} 
		return idReintegroRenglon ;
	}

	
	public void grabaDatosDelReclamoPrestacionaldelReintegro(int id_reintegro,  int idReclamo , int idPrestacionReclamo,
			int id_reintegrorenglon  , String userName,   Connection connetionParam) throws SystemException,
			DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connetionParam==null){
				con= ConnectionHelper.getConnection();
			}else{
				con = connetionParam;
			}
			String sql = "{call inserta_datos_reclamo_reintegro (?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, id_reintegrorenglon);
			stmt.setInt(3, idReclamo);
			stmt.setInt(4, idPrestacionReclamo);
			stmt.setString(5, userName);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
			_log.debug("Alta " + timestamp);
			stmt.setTimestamp(6, timestamp);			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al cargar datos del reclamo del reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar datos del reclamo prestacional del reintegro", e);
			throw new SystemException(e);
		} finally {
			if(connetionParam == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		} 
		return;
	}

	
	/**
	 * metodo que carga una nueva prestacion de protesis a partir de los
	 * parámetros si no lo puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void cargaReintegroPrestacionOdoProtesisEntry(String cuilTitular, int inte, int idReintegro, String cuit, String descripcion, 
		int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, int pieza, String cara, int idPrestadorExterno, boolean esExcepcion,
		int idReclamoPrestacional, int idReclamoPrestacionalPrestaciones,
		BigDecimal cargoOspim , BigDecimal cargoPrestadora, BigDecimal cargoImesa,
		Connection con) 
		throws SystemException, DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {
		
		CallableStatement stmt = null;
		// TEMPORAL, el plan por ahora es uno siempre, en la solución final
		// deberíamos incorporar una cartilla
		int idPlan = 1;
		
		try {	
			
			String sql = "{call inserta_prestacion_odo_protesis(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, idPlan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setInt(15, pieza);
			stmt.setString(16, cara);
			stmt.setInt(17, idPrestadorExterno);
			stmt.setBoolean(18, esExcepcion);
			stmt.setString(19, comproaDebitarSucursal);
			stmt.setString(20, comproaDebitarLetra);
			
			// Se agregan las tres columnas para grabar la tabla: 
			// reintegro_reclamo_prestacional
			stmt.setInt(21, idReclamoPrestacional);
			stmt.setInt(22, idReclamoPrestacionalPrestaciones);
			// Cargo Ospim + Cargo Prestadora
			stmt.setBigDecimal(23, cargoOspim);
			stmt.setBigDecimal(24, cargoPrestadora);
			stmt.setBigDecimal(25, cargoImesa);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al cargar prestacion para reintegro de prótesis",e);
			if (e.getSQLState().equalsIgnoreCase(WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		} 
		return;
	}

	/**
	 * metodo que carga una nueva prestacion de ortopedia ortodoncia a partir de
	 * los parámetros si no lo puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void cargaReintegroPrestacionOdoOrtoEntry(String cuilTitular, int inte, int idReintegro, String cuit, String descripcion, 
		int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, int pieza, String cara, int idPrestadorExterno, BigDecimal presupuesto, 
		int nroCuotas, Connection con) throws SystemException, DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {

		CallableStatement stmt = null;
		// TEMPORAL, el plan por ahora es uno siempre, en la solución final
		// deberíamos incorporar una cartilla
		int idPlan = 1;
		try {
			
			String sql = "{call inserta_prestacion_odo_orto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, idPlan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setInt(15, pieza);
			stmt.setString(16, cara);
			stmt.setInt(17, idPrestadorExterno);
			stmt.setBigDecimal(18, presupuesto);
			stmt.setInt(19, nroCuotas);
			stmt.setString(20, comproaDebitarLetra);
			stmt.setString(21, comproaDebitarSucursal);
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al cargar prestacion para reintegro de ortodoncia y ortopedia",e);
			if (e.getSQLState().equalsIgnoreCase(WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		} 
	}

	/**
	 * metodo que carga una nueva prestacion a partir de los parámetros si no lo
	 * puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void actualizaReintegroPrestacionEntry(String cuilTitular, int inte, int idReintegro, String cuit, String descripcion, 
		int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, Date altaFecha, int idPrestacionAnterior, String codigoAnterior, 
		String cuitEntidad, String sucuEntidad, Date comprobanteFecha, BigDecimal importeComprobante, int motivoAltaDiscapacidad, 
		BigDecimal cargoOspim, BigDecimal cargoPrestadora, BigDecimal cargoImesa) throws SystemException, DuplicateReintegroPrestacionIdException, 
	 AfiliadoSinPlanException {

		Connection con = null;
		CallableStatement stmt = null;
		int idPlan = 1;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_prestacion (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, idPlan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(altaFecha.getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setInt(15, idPrestacionAnterior);
			stmt.setString(16, codigoAnterior);
			stmt.setDate(17, new java.sql.Date(periodo.getTime()));
			stmt.setString(18, cuitEntidad);
			stmt.setString(19, sucuEntidad);
			stmt.setDate(20, new java.sql.Date(comprobanteFecha.getTime()));
			stmt.setBigDecimal(21, importeComprobante);
			stmt.setInt(22, motivoAltaDiscapacidad);
			stmt.setBigDecimal(23, cargoOspim);
			stmt.setBigDecimal(24, cargoPrestadora);
			if(comproaDebitarSucursal!=null) {
			   stmt.setString(25, comproaDebitarSucursal);
			}else {
			   stmt.setNull(25, Types.VARCHAR);	
			}
			stmt.setString(26, comproaDebitarLetra);
			
			stmt.setBigDecimal(27, cargoImesa);
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al actualizar prestacion para reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * metodo que carga una nueva prestacion de protesis de odontología a partir
	 * de los parámetros si no lo puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void actualizaReintegroPrestacionOdoProtesisEntry(String cuilTitular, int inte, int idReintegro, String cuit, String descripcion, 
		int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, Date altaFecha, int idPrestacionAnterior, String codigoAnterior, 
		int pieza, String cara, int idPrestadorExterno, boolean esExcepcion) throws SystemException, DuplicateReintegroPrestacionIdException, 
	 AfiliadoSinPlanException {

		Connection con = null;
		CallableStatement stmt = null;
		int id_plan = 1;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_prestacion_odo_protesis (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, id_plan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(altaFecha.getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setInt(15, idPrestacionAnterior);
			stmt.setString(16, codigoAnterior);
			stmt.setInt(17, pieza);
			stmt.setString(18, cara);
			stmt.setInt(19, idPrestadorExterno);
			stmt.setBoolean(20, esExcepcion);
			stmt.setString(21, comproaDebitarSucursal);
			stmt.setString(22, comproaDebitarLetra);

			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al actualizar prestacion para reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * metodo que carga una nueva prestacion de or de odontología a partir de
	 * los parámetros si no lo puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void actualizaReintegroPrestacionOdoOrtoEntry(String cuilTitular, int inte, int idReintegro, String cuit, 
		String descripcion, int idPrestacion, String codigo, Date prestacionFecha, BigDecimal cantidad, BigDecimal importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucu, String comproaDebitarNumero, 
		String tercerizado, Date periodo, String userName, Date altaFecha, int idPrestacionAnterior, String codigoAnterior, 
		int pieza, String cara, int idPrestadorExterno, BigDecimal honorarios, int nroCuotas) throws SystemException, 
	 DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {

		Connection con = null;
		CallableStatement stmt = null;
		int id_plan = 1;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_prestacion_odo_orto (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idReintegro);
			stmt.setString(2, cuit);
			stmt.setString(3, descripcion);
			stmt.setInt(4, idPrestacion);
			stmt.setInt(5, id_plan);
			stmt.setDate(6, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(7, cantidad);
			stmt.setBigDecimal(8, importe);
			stmt.setString(9, comproaDebitarTipo);
			stmt.setString(10, comproaDebitarNumero);
			stmt.setString(11, tercerizado);
			stmt.setTimestamp(12, new java.sql.Timestamp(altaFecha.getTime()));
			stmt.setString(13, userName);
			stmt.setString(14, codigo);
			stmt.setInt(15, idPrestacionAnterior);
			stmt.setString(16, codigoAnterior);
			stmt.setInt(17, pieza);
			stmt.setString(18, cara);
			stmt.setInt(19, idPrestadorExterno);
			stmt.setBigDecimal(20, honorarios);
			stmt.setInt(21, idPrestadorExterno);
			stmt.setString(22, comproaDebitarLetra);
			stmt.setString(23, comproaDebitarSucu);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			_log.error("Error al actualizar prestacion para reintegro odo ort", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateReintegroPrestacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * metodo que actualiza un afiliado a partir de los parámetros , si no lo
	 * puede actualizar retorna null
	 * 
	 * @throws NoSuchReintegroEntryException
	 * @throws SystemException
	 */
	public void actualizaReintegroEntry(int id_reintegro, Date fecha,
			String userName, int id_seccional, String obs)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_reintegro_fecha (?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, userName);
			stmt.setInt(4, id_seccional);
			stmt.setString(5, obs);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al actualizar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado lógico de un reintegro a partir de la clave
	 * primaria, no borra el reintegro físicamente, solo lo da de baja
	 * 
	 * @throws NoSuchReintegroEntryException
	 * @throws SystemException
	 */
	public void borraReintegroEntry(int id_reintegro, String userName)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_reintegro(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	
	public void borraReintegroReclamoPrestacion (int idReclamo, int idPrestacionReclamo,String userName )
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_reintegro_reclamo(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idReclamo);
			stmt.setInt(2, idPrestacionReclamo );
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	/**
	 * Metodo que aplica borrado lógico de un reintegro prestacion a partir de
	 * la clave primaria, borra la prestacion físicamente
	 * 
	 * @throws NoSuchReintegroPrestacionEntryException
	 * @throws SystemException
	 */
	public void borraReintegroPrestacionEntry(int id_reintegro,
			int id_prestacion, Date alta_fecha, int id_plan,
			String tipo_compro, String nro_compro, String userName)
			throws NoSuchReintegroPrestacionEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_reintegro_prestacion(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, id_prestacion);

			// SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
			// "dd/MM/yyyy HH:mm:ss");
			// Date fecha_alta = null;
			//		
			// try {
			// fecha_alta = formatoDeFecha.parse(DateFormat
			// .getDateTimeInstance(DateFormat.MEDIUM,
			// DateFormat.MEDIUM).format(alta_fecha));
			// } catch (Exception e) {
			// fecha_alta = null;
			// }
			stmt.setTimestamp(3, new java.sql.Timestamp(alta_fecha.getTime()));
		
			
			stmt.setInt(4, id_plan);
			stmt.setString(5, tipo_compro);
			stmt.setString(6, nro_compro);
			stmt.setString(7, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro prestacion", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroPrestacionEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro prestacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado físico de un reintegro prestacion de protesis
	 * odontolog{ia a partir de la clave primaria, borra la prestacion
	 * físicamente
	 * 
	 * @throws NoSuchReintegroPrestacionEntryException
	 * @throws SystemException
	 */
	public void borraReintegroPrestacionOdoProtesisEntry(int id_reintegro,
			int id_prestacion, Date alta_fecha, int id_plan,
			String tipo_compro, String nro_compro, String userName)
			throws NoSuchReintegroPrestacionEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_reintegro_prestacion_odo_protesis(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, id_prestacion);

			// SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
			// "dd/MM/yyyy HH:mm:ss");
			// Date fecha_alta = null;
			//		
			// try {
			// fecha_alta = formatoDeFecha.parse(DateFormat
			// .getDateTimeInstance(DateFormat.MEDIUM,
			// DateFormat.MEDIUM).format(alta_fecha));
			// } catch (Exception e) {
			// fecha_alta = null;
			// }
			stmt.setTimestamp(3, new java.sql.Timestamp(alta_fecha.getTime()));

			stmt.setInt(4, id_plan);
			stmt.setString(5, tipo_compro);
			stmt.setString(6, nro_compro);
			stmt.setString(7, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro prestacion", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroPrestacionEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro prestacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	public CantidadImporteRegistrados getCantidadImporteRegistrados(
			int idPrestacion, String cuilTitular, int inte, int anio, String tipo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		CantidadImporteRegistrados ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = new String();
			if(null!=tipo && tipo.equals("ort")){
				sql = "{call buscar_cantidad_importe_ort(?,?,?,?)}";
			}else{
				sql = "{call buscar_cantidad_importe(?,?,?,?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestacion);
			stmt.setString(2, cuilTitular);
			stmt.setInt(3, inte);
			Calendar cal = Calendar.getInstance();
			cal.set(anio, 1, 1);
			stmt.setDate(4, new java.sql.Date(cal.getTime().getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = new CantidadImporteRegistrados();
				ret.cantidad = rs.getDouble("cantidad");
				ret.importe = rs.getDouble("importe");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public BigDecimal getCantidadPrestacionesAnio(String cuilTitular, int inte,
			int idPrestacion) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		BigDecimal cantidad = BigDecimal.ZERO;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_cantidad_prestaciones_anio(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, idPrestacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantidad = rs.getBigDecimal("cantidad_prestaciones");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantidad;
	}

	public String getIdReintegrosAnio(String cuilTitular, int inte,
			int idPrestacion) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		String ids_reintegros = "";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_ids_reintegros_anio(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, idPrestacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ids_reintegros += " " + rs.getInt("id_reintegro_user");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ids_reintegros;
	}
	
	public BigDecimal getTotalPrestacionesEntreFechas(String cuilTitular,
			int inte, int idPrestacion, Date fecha_desde, Date fecha_hasta,
			String cuit, String sucu) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		BigDecimal cantidad = BigDecimal.ZERO;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_total_prestaciones_fechas(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, idPrestacion);
			stmt.setDate(4, new java.sql.Date(fecha_desde.getTime()));
			stmt.setDate(5, new java.sql.Date(fecha_hasta.getTime()));
			stmt.setString(6, cuit);
			stmt.setString(7, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantidad = rs.getBigDecimal(1) != null ? rs.getBigDecimal(1)
						: BigDecimal.ZERO;
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantidad;
	}

	public BigDecimal getCantidadPrestacionesEntreFechas(String cuilTitular,
			int inte, int idPrestacion, Date fecha_desde, Date fecha_hasta,
			String cuit, String sucu) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		BigDecimal cantidad = BigDecimal.ZERO;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_cantidad_prestaciones_fechas(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setInt(3, idPrestacion);
			stmt.setDate(4, new java.sql.Date(fecha_desde.getTime()));
			stmt.setDate(5, new java.sql.Date(fecha_hasta.getTime()));
			stmt.setString(6, cuit);
			stmt.setString(7, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantidad = rs.getBigDecimal(1) != null ? rs.getBigDecimal(1)
						: BigDecimal.ZERO;
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantidad;
	}

	public boolean getPrestacionHechaAAfiliado(String cuilTitular, int inte,
			int idPrestacion, int pieza, String cara) throws SystemException {
		List<Reintegro> reintegros = ReintegroServiceUtil
				.buscarHistoricoPrestacionesOdoProtesis(cuilTitular, inte);
		for (Reintegro reintegro : reintegros) {
			List<ReintegroPrestacion> reintegroPrestaciones = reintegro
					.getReintegroPrestacion();
			for (ReintegroPrestacion reintegroPrestacion : reintegroPrestaciones) {
				if (reintegroPrestacion.getId_prestacion() == idPrestacion
						&& ((ReintegroPrestacionOdoProtesis) reintegroPrestacion)
								.getCara().equals(
										String.valueOf(pieza) + " " + cara)) {
					return true;
				}
			}
		}
		return false;
	}

	public BigDecimal getCantidadPrestacionesProtesisAnio(String cuilTitular)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		BigDecimal cantidad = BigDecimal.ZERO;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_cantidad_prestaciones_protesis_anio(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cantidad = rs.getBigDecimal("cantidad_prestaciones");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar topes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cantidad;
	}

	public List<Reintegro> getHistoricoPrestacionesOdoProtesis(
			String cuilTitular, int inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call lista_prestaciones_odo_protesis_afil(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), "", rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionOdoProtesis reintegroPrestacion = new ReintegroPrestacionOdoProtesis(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), "", "", rs
								.getBigDecimal("rp__importe"));
				reintegroPrestacion.setCara(rs.getString("rp__pieza"));

				Prestacion prestacion = new Prestacion(rs
						.getInt("rp__prestacion"), rs
						.getString("n__descripcion"));
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);

				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar histórico de protesis", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> getHistoricoPrestacionesOdoOrto(String cuilTitular,
			int inte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call lista_prestaciones_odo_orto_afil(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), "", rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				// en el caso de ortopedia se muestran todos las cuotas del
				// reitnegro
				listaReintegros.add(reintegro);

				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);

				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> listaDetalleCuota = new ArrayList<DetalleCuota>();
				if (listaDetalleCuota == null) {
					listaDetalleCuota = new ArrayList<DetalleCuota>();
				}
				listaDetalleCuota.add(detalleCuota);
				reintegro.setDetalleCuota(listaDetalleCuota);

			}
		} catch (SQLException e) {
			_log.error("Error al buscar histórico de protesis", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public Reintegro traeResumenOP(Reintegro reintegro, int id_reintegro, String tipo_reintegro) {
		Connection con = null;
		CallableStatement stmt = null;		
		try {
			String sql = "{call buscar_resumen_op_reintegros(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setString(2, tipo_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
			    reintegro.setIdOP(rs.getInt("r__id_orden_pago")); 
			    reintegro.setChequeOP(rs.getBigDecimal("r__nro_cheque") != null ? rs.getBigDecimal("r__nro_cheque").toBigInteger() : null); 
			    reintegro.setFechaOP(rs.getDate("r__fecha_op"));
				reintegro.setId_lista_reintegro(rs.getInt("opor_id_lista_reintegro_pago"));					    						    	
			}
		} catch (Exception e) {
			_log.error("Error al traer los datos de la OP para el reintegro ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegro;
	}

	public List<Reintegro> buscarReintegros(String entidad, Date fechaDesde,
			Date fechaHasta, Date periodoDesde, Date periodoHasta,
			String codPrestad, int nroAfi, int inte, String cuil_titular,
			int seccional, int numero, String alta_usr) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros(?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				if (rs.getInt("id_orden_pago_ospim_lista") != 0){
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs.getInt("opor_id_lista_reintegro_pago"));
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionNormal reintegroPrestacion = new ReintegroPrestacionNormal(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getBigDecimal("rp__cantidad"), rs
								.getDate("rp__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
		
				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}
	
	public List<PrestacionesReclamo> buscarPrestacionesReclamosdeAfiliadoEnReintegro(String cuil,int inte, boolean esReintegro, int marca_rein_liq, String plan ) {
			
			List<PrestacionesReclamo > PrestacionesDelReclamo = new ArrayList<PrestacionesReclamo >();
			Connection con = null;
			CallableStatement stmt = null;
			try {
				String sql;
				if (marca_rein_liq > 0)
					sql = "{call autorizaciones.reclamos_prestacionales_prestacionesclinicas_by_afi_reinliq (?,?,?,?,?)}";
				else
					sql = "{call autorizaciones.reclamos_prestacionales_prestacionesclinicas_by_afiliado (?,?,?)}";
					
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,inte);
				stmt.setString (2, cuil  );
				stmt.setBoolean(3, esReintegro);
				if (marca_rein_liq > 0) {
					stmt.setInt(4, marca_rein_liq);
					stmt.setString (5, plan);				
				}

				ResultSet rs = stmt.executeQuery();
				while (rs.next())   {
					PrestacionesReclamo  pla = PrestacionesReclamo.getMapping_1("nom_", rs);						
					PrestacionesDelReclamo.add(pla);
			    }
			} catch (Exception e) {
				_log.error("Error al buscar prestaciones del reclamo", e);
			} finally {
				ConnectionHelper.cerrar(stmt, con);
			}
			return PrestacionesDelReclamo ;
		}
	
	public List<PrestacionesReclamo> buscarPrestacionesReclamosdeAfiliadoEnReintegroPorLote(String cuil,int inte, boolean esReintegro ,String nroLoteFiltro ) {
		
		List<PrestacionesReclamo > PrestacionesDelReclamo = new ArrayList<PrestacionesReclamo >();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.reclamos_prestacionales_prestacionesclinicas_by_afi_nro_lote (?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (cuil != null && StringUtils.checkEmpty(cuil) ) {
				stmt.setNull(1, Types.INTEGER);	
				stmt.setNull (2,  Types.VARCHAR );
			}else {
				stmt.setInt(1,inte);
				stmt.setString (2, cuil  );
			}
			stmt.setBoolean(3,esReintegro);
			stmt.setInt(4,Integer.valueOf(nroLoteFiltro));

			ResultSet rs = stmt.executeQuery();
			while (rs.next())   {
				PrestacionesReclamo  pla = PrestacionesReclamo.getMapping_1("nom_", rs);						
				PrestacionesDelReclamo.add(pla);
		    }
		} catch (Exception e) {
			_log.error("Error al buscar prestaciones del reclamo por nro lote", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return PrestacionesDelReclamo ;
	}

	public List<PrestacionesReclamo> buscarPrestacionesReclamosdeAfiliadoEnReintegroFarmacia(String cuil,int inte) {
		
		List<PrestacionesReclamo > PrestacionesDelReclamo = new ArrayList<PrestacionesReclamo >();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.reclamos_prestacionales_farmacia_by_afiliado (?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,inte);
			stmt.setString (2, cuil  );
			ResultSet rs = stmt.executeQuery();
			while (rs.next())   {
				PrestacionesReclamo  pla = PrestacionesReclamo.getMapping_1("med_", rs);						
				PrestacionesDelReclamo.add(pla);
		                     	}
		} catch (Exception e) {
			_log.error("Error al buscar prestaciones del reclamo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return PrestacionesDelReclamo ;
	}

	
			
	public List<Reintegro> buscarReintegrosPagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_pagos(?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionNormal reintegroPrestacion = new ReintegroPrestacionNormal(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getBigDecimal("rp__cantidad"), rs
								.getDate("rp__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);

				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	
	
	public List<Reintegro> buscarReintegrosImpagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_impagos(?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionNormal reintegroPrestacion = new ReintegroPrestacionNormal(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getBigDecimal("rp__cantidad"), rs
								.getDate("p__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				
				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoOrto(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_orto(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				reintegro.getAfiliado().setId_ospim(rs.getInt("r__id_ospim"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				// en el caso de ortopedia se muestran todos las cuotas del
				// reitnegro
				listaReintegros.add(reintegro);

				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);

				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> listaDetalleCuota = new ArrayList<DetalleCuota>();
				if (listaDetalleCuota == null) {
					listaDetalleCuota = new ArrayList<DetalleCuota>();
				}
				listaDetalleCuota.add(detalleCuota);
				reintegro.setDetalleCuota(listaDetalleCuota);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoOrtoDetallados(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_orto(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				reintegro.getAfiliado().setId_ospim(rs.getInt("r__id_ospim"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				// en el caso de ortopedia se muestran todos las cuotas del
				// reitnegro
				listaReintegros.add(reintegro);
				
				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				

				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);

				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> listaDetalleCuota = new ArrayList<DetalleCuota>();
				if (listaDetalleCuota == null) {
					listaDetalleCuota = new ArrayList<DetalleCuota>();
				}
				listaDetalleCuota.add(detalleCuota);
				reintegro.setDetalleCuota(listaDetalleCuota);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoOrtoPagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_orto_pagos(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				reintegro.getAfiliado().setId_ospim(rs.getInt("r__id_ospim"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				// en el caso de ortopedia se muestran todos las cuotas del
				// reitnegro
				listaReintegros.add(reintegro);

				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);

				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> listaDetalleCuota = new ArrayList<DetalleCuota>();
				if (listaDetalleCuota == null) {
					listaDetalleCuota = new ArrayList<DetalleCuota>();
				}
				listaDetalleCuota.add(detalleCuota);
				reintegro.setDetalleCuota(listaDetalleCuota);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoOrtoImpagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_orto_impagos(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				reintegro.getAfiliado().setId_ospim(rs.getInt("r__id_ospim"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				// se guardan todos los reintegros del query en el caso de
				// ortopedia ortodoncia
				
				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				listaReintegros.add(reintegro);

				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);

				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> listaDetalleCuota = new ArrayList<DetalleCuota>();
				if (listaDetalleCuota == null) {
					listaDetalleCuota = new ArrayList<DetalleCuota>();
				}
				listaDetalleCuota.add(detalleCuota);
				reintegro.setDetalleCuota(listaDetalleCuota);

				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoProtesis(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_protesis(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				
				// Duvi. 29-03-2022
				// Estas dos lineas se encontraban comentadas.
				// Se vuelven a descomentar intentando que funcione correcate¿mente el campo de 
				// Pago Transferencia en la grilla de Busqueda de Reintegros.				
				reintegro.setCbu(rs.getString("r__cbu") != null ? rs.getString("r__cbu") : rs.getString("r__cbu"));
				reintegro.setCuilCuenta(rs.getString("r__cuil_cuenta") != null ? rs.getString("r__cuil_cuenta") : rs.getString("r__cuil_cuenta"));
				
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionOdoProtesis reintegroPrestacion = new ReintegroPrestacionOdoProtesis(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getDate("rp__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoProtesisPagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_protesis_pagos(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				
				// Duvi. 29-03-2022
				// Estas dos lineas se encontraban comentadas.
				// Se vuelven a descomentar intentando que funcione correcate¿mente el campo de 
				// Pago Transferencia en la grilla de Busqueda de Reintegros.
				reintegro.setCbu(rs.getString("r__cbu") != null ? rs.getString("r__cbu") : rs.getString("r__cbu"));
				reintegro.setCuilCuenta(rs.getString("r__cuil_cuenta") != null ? rs.getString("r__cuil_cuenta") : rs.getString("r__cuil_cuenta"));

				
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroPrestacionOdoProtesis reintegroPrestacion = new ReintegroPrestacionOdoProtesis(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getDate("rp__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public List<Reintegro> buscarReintegrosOdoProtesisImpagos(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String alta_usr,
			int estado) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reintegro> listaReintegros = new ArrayList<Reintegro>();
		try {
			String sql = "{call buscar_reintegros_odo_protesis_impagos(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getDate("r__fecha"), rs
						.getDate("r__periodo"), rs.getInt("r__id_seccional"),
						rs.getString("r__cuil_titular"), rs.getInt("r__inte"),
						rs.getString("r__descripcion"), rs
								.getInt("r__reintegro"), rs
								.getString("r__tipo_reintegro"), rs
								.getDate("r__b_fecha"), rs
								.getString("r__b_usr"), entidad, rs
								.getInt("r__id_plan"), rs
								.getString("r__nombre_plan"), rs
								.getDate("r__fecha_baja"), rs
								.getInt("r__id_orden_pago"), rs
								.getBigDecimal("r__nro_cheque") != null ? rs
								.getBigDecimal("r__nro_cheque").toBigInteger()
								: null, rs.getDate("r__fecha_op"), rs
								.getInt("r__estado"));
				reintegro.setId_reintegro_user(rs
						.getInt("r__id_reintegro_user"));
				if (rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setEstado(2);
				}
				reintegro.setId_lista_reintegro(rs
						.getInt("opor_id_lista_reintegro_pago"));
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				
				
				// Duvi. 29-03-2022
				// Estas dos lineas se encontraban comentadas.
				// Se vuelven a descomentar intentando que funcione correcate¿mente el campo de 
				// Pago Transferencia en la grilla de Busqueda de Reintegros.
				reintegro.setCbu(rs.getString("r__cbu") != null ? rs.getString("r__cbu") : rs.getString("r__cbu"));
				reintegro.setCuilCuenta(rs.getString("r__cuil_cuenta") != null ? rs.getString("r__cuil_cuenta") : rs.getString("r__cuil_cuenta"));
					
				
				ReintegroPrestacionOdoProtesis reintegroPrestacion = new ReintegroPrestacionOdoProtesis(
						reintegro, rs.getInt("rp__prestacion"), rs
								.getString("rp__codigo"), rs
								.getString("rp__cuit"), rs
								.getString("rp__descripcion"), rs
								.getBigDecimal("rp__importe"), rs
								.getDate("rp__fecha_prestacion"), rs
								.getString("rp__comprobante"));
				List<ReintegroPrestacion> listaReintegrosPrest = reintegro
						.getReintegroPrestacion();
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroPrestacion>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}

	public static class CantidadImporteRegistrados {
		public double cantidad;
		public double importe;
	}

	public List<ReporteOrdenPagoReintegros> getReintegros(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_reporte_reintegros(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionNormal reintegroPrestacion = ReintegroPrestacionNormal
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionNormal> reintegroPrestacionList = new ArrayList<ReintegroPrestacionNormal>();
				reintegroPrestacionList.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion().add(
							reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<ReporteOrdenPagoReintegros> getReintegrosOdoProtesis(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_reporte_reintegros_odo_protesis(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionOdoProtesis reintegroPrestacion = ReintegroPrestacionOdoProtesis
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionOdoProtesis> reintegroPrestacionList = new ArrayList<ReintegroPrestacionOdoProtesis>();
				reintegroPrestacionList.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion().add(
							reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<ReporteOrdenPagoReintegros> getReintegrosOdoOrto(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_reporte_reintegros_odo_orto(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = ReintegroPrestacionOdoOrtopediaOrtodoncia
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionOdoOrtopediaOrtodoncia> reintegroPrestacionList = new ArrayList<ReintegroPrestacionOdoOrtopediaOrtodoncia>();
				reintegroPrestacionList.add(reintegroPrestacion);

				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);
				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> detalles = new ArrayList<DetalleCuota>();
				detalles.add(detalleCuota);
				reintegro.setDetalleCuota(detalles);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion().add(
							reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de reporte", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	/**
	 * Metodo que actualiza un reintegro, le cambia el estado a un estado dado,
	 * como estado auditado
	 * 
	 * @throws NoSuchReintegroPrestacionEntryException
	 * @throws SystemException
	 */
	public void cambiarEstadoReintegroEntry(int id_reintegro, int estado,
			String userName, String tipo_reintegro, Connection connectionParameter)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}	
			String sql = "{call cambio_estado_reintegro(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, estado);
			stmt.setString(3, userName);
			stmt.setString(4, tipo_reintegro);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
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
		return;
	}

	/**
	 * Metodo que guarda en la base las cuotas, un a cuota determinado en el
	 * parámetro, la guarda en estado default
	 * 
	 * @throws SystemException
	 */
	public void cargaOrtoCuotas(int id_reintegro, int nro_cuota,
			int porcentaje, BigDecimal importe_cuota, String userName, Connection connectionParameter)
			throws SystemException {		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}				
			String sql = "{call carga_cuota_ortopedia(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, nro_cuota);
			stmt.setInt(3, porcentaje);
			stmt.setBigDecimal(4, importe_cuota);
			stmt.setString(5, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		}finally{
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return;
	}

	/**
	 * Metodo que actualiza el importe de las las cuotas, un a cuota determinado
	 * en el parámetro, la guarda en estado default
	 * 
	 * @throws SystemException
	 */
	public void actualizaImporteOrtoCuotas(int id_reintegro, int nro_cuota,
			int porcentaje, BigDecimal importe_cuota, String userName, Connection connectionParameter)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call actualiza_cuota_ortopedia_nro(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, nro_cuota);
			stmt.setInt(3, porcentaje);
			stmt.setBigDecimal(4, importe_cuota);
			stmt.setString(5, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
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
		return;
	}

	public static void actualizaOrtoDetalleCuota(DetalleCuota detalleCuota,
			String username) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_cuota_ortopedia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1,new java.sql.Date(detalleCuota.getFecha().getTime()));
			stmt.setDate(2, new java.sql.Date(detalleCuota.getPeriodo().getTime()));
			stmt.setString(3, detalleCuota.getDiagnostico());
			stmt.setString(4, detalleCuota.getPlan_tratamiento());
			stmt.setString(5, detalleCuota.getTiempo_estimado());
			stmt.setString(6, detalleCuota.getPronostico());
			stmt.setString(7, detalleCuota.getInforme());
			stmt.setString(8, detalleCuota.getCompro_a_debitar_tipo());
			stmt.setString(9, detalleCuota.getComproaDebitarLetra());
			stmt.setString(10, detalleCuota.getComproaDebitarSucursal());
			stmt.setString(11, detalleCuota.getCompro_a_debitar_numero());
			stmt.setString(12, username);
			stmt.setInt(13, detalleCuota.getId_reintegro());
			stmt.setInt(14, detalleCuota.getNro_cuota());
			
			stmt.setInt(15, detalleCuota.getId_Reclamo());
			stmt.setInt(16, detalleCuota.getId_ReclamoPrestaciones());
			
			/* Graba cuota, no graba el %. Se calcula on-the-fly por pantalla */
			stmt.setBigDecimal(17, detalleCuota.getImporte());		
						
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al cargar cuota para reintegro de ortop", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado físico de una cuota de un reintegro
	 * 
	 * @throws NoSuchReintegroEntryException
	 * @throws SystemException
	 */
	public void borrarOrtoCuotas(int id_reintegro, String userName)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_cuota_ortopedia(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar la cuota", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar la cuota", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	public void borrarOrtoCuota(int id_reintegro, int nro_cuota)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_cuota_ortopedia_nro(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, nro_cuota);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar la cuota", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar la cuota", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que actualiza una cuota, le cambia el estado a un estado dado,
	 * como estado autorizado
	 * 
	 * @throws NoSuchReintegroPrestacionEntryException
	 * @throws SystemException
	 */
	public void cambiarEstadoCuota(int id_reintegro, int cuota, int estado,
			String userName) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call cambio_estado_cuota(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setInt(2, cuota);
			stmt.setInt(3, estado);
			stmt.setString(4, userName);
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

	public static int getPrimerReitnegroLista(int idLista)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_reintegro = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_id_primer_reintegro_lista_reporte(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLista);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_reintegro = rs.getInt("id_reintegro");
			}
		} catch (SQLException e) {
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_reintegro;
	}

	public String getTipoReintegroLista(int idLista, int id_reintegro)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		String tipo_reintegro = "";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_tipo_reintegro_lista_reporte(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLista);
			stmt.setInt(2, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tipo_reintegro = rs.getString("tipo_reintegro");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar en la lista de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tipo_reintegro;
	}
	
	
	public List<ReintegroPrestacionNormal> recuperaReintegrosDelAnio(String origen,String cuil) throws SystemException{
		List <ReintegroPrestacionNormal> lr = new ArrayList<ReintegroPrestacionNormal>();
		Connection con = null;
		CallableStatement stmt = null;
		String tipo_reintegro = "";
		try {
			con = ConnectionHelper.getConnection();
			String sql="";
			if(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS.equalsIgnoreCase(origen)){
			   sql = "{call trae_cantidad_prestaciones_protesis_anio_v01(?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionNormal rp = new ReintegroPrestacionNormal();
				rp.setFecha_prestacion(rs.getDate("fecha_prestacion"));
				rp.setImporte(rs.getBigDecimal("cantidad_prestaciones"));
				lr.add(rp);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar en la lista de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lr;
	}
	

	
	public ReintegroPrestacionNormal recuperaReintegrosUltimaProtesisDental(String origen,String cuilTitular, int inte, 
																					String cara, String pieza, String codigo) throws SystemException{
		ReintegroPrestacionNormal rp = null;
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql="";
			if(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS.equalsIgnoreCase(origen)){
			   sql = "{call trae_ultima_prestacion_protesis_dental(?,?,?,?,?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
            stmt.setString(3, cara);
			stmt.setString(4, pieza);
			stmt.setString(5, codigo);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				rp = new ReintegroPrestacionNormal();
				rp.setFecha_prestacion(rs.getDate("fecha_prestacion"));
				rp.setCodigo(rs.getString("codigo"));
			}
		} catch (SQLException e) {
			_log.error("Error al traer ultima prestación ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return rp;
	}
	
	
	
	
	
	
	public List<ConvenioPrestacionalDetalle> recuperaTopesReintegrosDelAnio(String origen,Integer idPlan) throws SystemException{
		List <ConvenioPrestacionalDetalle> lr = new ArrayList<ConvenioPrestacionalDetalle>();
		Connection con = null;
		CallableStatement stmt = null;
		Integer tipo_reintegro = 4;
		try {
			con = ConnectionHelper.getConnection();
			String sql="";
			sql = "{call trae_topes_reintegro_anio_v01(?,?)}";
			if(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS.equalsIgnoreCase(origen)){
				 tipo_reintegro = WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS_MARCA;  
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, tipo_reintegro);
			stmt.setInt(2, idPlan);
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ConvenioPrestacionalDetalle rp = new ConvenioPrestacionalDetalle();
				rp.setFechaDesde(rs.getDate("fecha_desde"));
				rp.setFechaHasta(rs.getDate("fecha_hasta"));
				rp.setImporte(rs.getBigDecimal("tope"));
				rp.setPorcentaje(BigDecimal.ZERO);
				lr.add(rp);
			}
			
		} catch (SQLException e) {
			_log.error("Error al buscar en la lista de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lr;
	}
	
	
//
	
	public List<ReintegroPrestacionNormal> getComprobantesPrestacionesReintegro(
			int idPrestacion, String comproTipo, String comproLetra, String comproNro, 
			String cuitEntidad, String sucursalEntidad, 
			String cuilTitular, int inte, Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
	
		List<ReintegroPrestacionNormal> reintegroPrestaciones = new ArrayList<ReintegroPrestacionNormal>();
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call busca_prestaciones_reintegro_por_comprobante(?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestacion);
			stmt.setString(2,comproTipo);
			stmt.setString(3,comproLetra);
			stmt.setString(4, comproNro);
			stmt.setString(5, cuitEntidad);
			stmt.setString(6, sucursalEntidad);
			stmt.setString(7,cuilTitular);
			stmt.setInt(8,inte);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ReintegroPrestacionNormal reintegroPrestacion = new ReintegroPrestacionNormal();
				
				reintegroPrestacion.setId_prestacion(rs.getInt("id_prestacion"));
				reintegroPrestacion.setCompro_a_debitar_tipo(rs.getString("compro_tipo"));
				reintegroPrestacion.setComproaDebitarLetra(rs.getString("compro_letra"));
				reintegroPrestacion.setCompro_a_debitar_numero(rs.getString("compro_nro"));
				reintegroPrestacion.setCuit_entidad(rs.getString("cuit_entidad"));
				reintegroPrestacion.setSucursal_entidad(rs.getString("sucursal_entidad"));
				reintegroPrestacion.setPeriodo(rs.getDate("periodo"));
				
				Reintegro reintegro = new Reintegro();
				Afiliado afiliado = new Afiliado();
				afiliado.setCuil_titular(rs.getString("cuil_titular"));
				afiliado.setInte(rs.getInt("inte") );
				reintegro.setAfiliado(afiliado);
				reintegro.setId_reintegro(rs.getInt("id_reintegro"));
				reintegroPrestacion.setReintegro(reintegro);
				
				reintegroPrestaciones.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return reintegroPrestaciones;
	}

	
//	
	
	public static Integer getIdOPReintegroLista(Integer idLista)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_op = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_id_op_reintegro_lista(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLista);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_op = rs.getInt("id_orden_pago");
			}
		} catch (SQLException e) {
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_op;
	}

	
}
