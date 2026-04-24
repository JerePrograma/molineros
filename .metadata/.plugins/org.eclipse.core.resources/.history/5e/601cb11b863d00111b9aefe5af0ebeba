package ar.com.ospim.prestadores.services;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.NoSuchConvenioPrestacionalEntryException;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.util.ConnectionHelper;

import ar.com.ospim.util.StringUtils;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * servicio test que nos da acceso a los datos de la aplicaci�n (BD).
 *
 */
public class ConvenioPrestacionalServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ConvenioPrestacionalServiceImpl.class);

	/**
	 * Metodo que obtiene un convenio prestacional a partir de la clave primaria, en caso de
	 * que est� dado de baja o de no encontrarlo retorna null
	 *
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacional(int idConvenioPrest) throws SystemException,
			NoSuchConvenioPrestacionalEntryException {

		_log.info("[CONV-PREST-SVC][GET-CAB][START] Inicio getConvenioPrestacional id=" + idConvenioPrest);
		Connection con = null;
		CallableStatement stmt = null;
		ConvenioPrestacional convenioPrestacional = null;

		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_cab(?)}";
			_log.debug("[CONV-PREST-SVC][GET-CAB][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			_log.debug("[CONV-PREST-SVC][GET-CAB][CONN] Conexión obtenida=" + (con != null));
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenioPrest);
			_log.debug("[CONV-PREST-SVC][GET-CAB][PARAM] idConvenioPrest=" + idConvenioPrest);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				convenioPrestacional = ConvenioPrestacional.getMapping(rs, "convprest_");
				_log.debug("[CONV-PREST-SVC][GET-CAB][ROW] convenioPrestacional=" + convenioPrestacional);
			}
		} catch (Exception e) {
			_log.error("Error al obtener convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][GET-CAB][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][GET-CAB][END] Fin getConvenioPrestacional resultado=" + convenioPrestacional);
		return convenioPrestacional;
	}

	/**
	 * Metodo que obtiene la lista de detalles a partir de la clave primaria del
	 * conv.prestacional, en caso de no encontrarla arroja excepci�n
	 *
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static List<ConvenioPrestacionalDetalle> getConvePrestDetalles(int idConvPrest)
			throws SystemException {

		_log.info("[CONV-PREST-SVC][GET-DET][START] Inicio getConvePrestDetalles idConvPrest=" + idConvPrest);

		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		List<ConvenioPrestacionalDetalle> detalles = new ArrayList<ConvenioPrestacionalDetalle>();

		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_det(?)}";
			_log.debug("[CONV-PREST-SVC][GET-DET][SQL] " + sql);

			con = ConnectionHelper.getConnection();
			_log.debug("[CONV-PREST-SVC][GET-DET][CONN] Conexión obtenida=" + (con != null));

			stmt = con.prepareCall(sql);
			stmt.setInt(1, idConvPrest);

			_log.debug("[CONV-PREST-SVC][GET-DET][PARAM] idConvPrest=" + idConvPrest);

			rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDet = ConvenioPrestacionalDetalle.getMapping(rs, "convprestdet_");
				detalles.add(cpDet);
				_log.debug("[CONV-PREST-SVC][GET-DET][ROW] detalle agregado=" + cpDet);
			}

		} catch (Exception e) {
			_log.error("[CONV-PREST-SVC][GET-DET][ERROR] Error al obtener detalles convenio", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][GET-DET][FINALLY] Recursos cerrados");
		}

		_log.info("[CONV-PREST-SVC][GET-DET][END] Fin getConvePrestDetalles cantidad=" + detalles.size());
		return detalles;
	}

	public List<ConvenioPrestacional> buscarConveniosPrestacionales(BusquedaConvenioPrestacionalFiltro filtro) {
		_log.info("[CONV-PREST-SVC][SEARCH][START] Inicio buscarConveniosPrestacionales filtro=" + filtro);
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacional> listaCoveniosPrest = new ArrayList<ConvenioPrestacional>();
		try {
			String sql = "{call convenio_prest.buscar_convenios_prestacionales_cab(?,?,?,?)}";
			_log.debug("[CONV-PREST-SVC][SEARCH][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getIdPrestador()==null){
				stmt.setNull(1, Types.INTEGER);
				_log.debug("[CONV-PREST-SVC][SEARCH][PARAM] idPrestador=NULL");
			}else{
				stmt.setInt(1, filtro.getIdPrestador());
				_log.debug("[CONV-PREST-SVC][SEARCH][PARAM] idPrestador=" + filtro.getIdPrestador());
			}
			stmt.setString(2, filtro.getCuit());
			stmt.setString(3, filtro.getDescripcion());
			stmt.setInt(4, filtro.getEstado());
			_log.debug("[CONV-PREST-SVC][SEARCH][PARAM] cuit=" + filtro.getCuit()
					+ ", descripcion=" + filtro.getDescripcion()
					+ ", estado=" + filtro.getEstado());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacional convPrest = ConvenioPrestacional.getMapping(rs, "convprest_");
				listaCoveniosPrest.add(convPrest);
				_log.debug("[CONV-PREST-SVC][SEARCH][ROW] convenio agregado=" + convPrest);
				// ContratoDetalle contratoDetalle =
				// ContratoDetalle.getMapping(rs, "cd_");
				// int indexOf = listaContratos.indexOf(contrato);
				// if (indexOf == -1) {
				// listaContratos.add(contrato);
				// } else {
				// contrato = listaContratos.get(indexOf);
				// }
				// List<ContratoDetalle> listaContratoDetalle = contrato
				// .getContratoDetalle();
				// if (listaContratoDetalle == null) {
				// listaContratoDetalle = new ArrayList<ContratoDetalle>();
				// }
				// listaContratoDetalle.add(contratoDetalle);
				// contrato.setDetalleContrato(listaContratoDetalle);
			}
		} catch (Exception e) {
			_log.error("Error al traer convenios prestacionales", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][SEARCH][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][SEARCH][END] Fin buscarConveniosPrestacionales cantidad=" + listaCoveniosPrest.size());
		return listaCoveniosPrest;
	}

	/**
	 * Metodo que actualiza un reintegro, le cambia el estado a un estado dado,
	 * como estado auditado
	 *
	 * @throws NoSuchContratoPrestacionEntryException
	 * @throws SystemException
	 */
	/**
	 * actualiza un convenio prest. y sus items en estados ALTA; MODIF; BAJA
	 *
	 * @throws NoSuchConvenioPrestacionalEntryException
	 * @throws SystemException
	 */
	public void actualizarConvenioPrestacional(ConvenioPrestacional convPrest, String userName) throws SystemException {

		_log.info("[CONV-PREST-SVC][UPDATE][START] Inicio actualizarConvenioPrestacional id="
				+ (convPrest != null ? convPrest.getId() : "null") + ", user=" + userName);

		if (convPrest == null) {
			throw new SystemException("No se puede actualizar un convenio nulo");
		}

		if (convPrest.getId() <= 0) {
			throw new SystemException("No se puede actualizar un convenio con id inválido: " + convPrest.getId());
		}

		if (convPrest.getPrestador() == null || convPrest.getPrestador().getId_prestador() <= 0) {
			throw new SystemException("No se puede actualizar un convenio sin prestador válido");
		}

		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call convenio_prest.actualizar_convenio_cab(?,?,?,?,?,?,?,?,?)}";

			_log.debug("[CONV-PREST-SVC][UPDATE][SQL-CAB] " + sql);

			con = ConnectionHelper.getConnectionForTransaction();
			_log.debug("[CONV-PREST-SVC][UPDATE][CONN] Conexión transaccional obtenida=" + (con != null));

			// CABECERA
			stmt = con.prepareCall(sql);
			stmt.setInt(1, convPrest.getId());
			stmt.setInt(2, convPrest.getPrestador().getId_prestador());
			stmt.setInt(3, convPrest.getEstado().getIntValue());
			stmt.setInt(4, convPrest.getDiaRecepcion());
			stmt.setString(5, convPrest.getCondicionDePago());
			stmt.setInt(6, convPrest.getTipoPago().getId());

			if (convPrest.getVigencia() != null) {
				stmt.setDate(7, new java.sql.Date(convPrest.getVigencia().getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}

			if (convPrest.getVencimiento() != null) {
				stmt.setDate(8, new java.sql.Date(convPrest.getVencimiento().getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
			}

			stmt.setString(9, userName);

			_log.debug("[CONV-PREST-SVC][UPDATE][CAB] Actualizando cabecera convenio id=" + convPrest.getId());

			stmt.executeUpdate();

			_log.debug("[CONV-PREST-SVC][UPDATE][CAB] Cabecera actualizada correctamente");

			// HISTORIZAR DETALLES ACTUALES Y ELIMINARLOS DE LA TABLA OPERATIVA
			sincronizarDetallesConvenio(con, convPrest, userName);

			_log.debug("[CONV-PREST-SVC][UPDATE][DET] Nueva foto de detalles insertada. cantidad="
					+ (convPrest.getConvenioPrestDetalle() != null ? convPrest.getConvenioPrestDetalle().size() : 0));

			con.commit();
			_log.info("[CONV-PREST-SVC][UPDATE][COMMIT] Commit realizado correctamente");

		} catch (IllegalArgumentException e) {
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][ROLLBACK] Rollback por regla de negocio", e);
			throw e;
		} catch (SQLException e) {
			_log.error("Error al actualizar convenio prest.", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][UPDATE][ROLLBACK] Rollback ejecutado por SQLException", e);
			throw new SystemException(e);
		} catch (Exception e) {
			_log.error("Error al actualizar convenio prest.", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][UPDATE][ROLLBACK] Rollback ejecutado por Exception", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][UPDATE][FINALLY] Recursos cerrados");
		}

		_log.info("[CONV-PREST-SVC][UPDATE][END] Fin actualizarConvenioPrestacional");
	}

	/**
	 * Metodo que aplica borrado lógico de un convenio prestacional a partir de la clave
	 * primaria, no borra el reintegro convenio prestacional, solo lo da de baja
	 *
	 * @throws NoSuchConvenioPrestacionalEntryException
	 * @throws SystemException
	 */
	public void eliminarConvenioPrestacional(int idconvenioPrest, String userName)
			throws NoSuchConvenioPrestacionalEntryException, SystemException {

		_log.info("[CONV-PREST-SVC][DELETE][START] Inicio eliminarConvenioPrestacional id=" + idconvenioPrest + ", user=" + userName);

		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call convenio_prest.eliminar_convenio_prestacional(?,?)}";

			_log.debug("[CONV-PREST-SVC][DELETE][SQL] " + sql);

			con = ConnectionHelper.getConnectionForTransaction();
			_log.debug("[CONV-PREST-SVC][DELETE][CONN] Conexión transaccional obtenida=" + (con != null));

			// HISTORIZAR DETALLES ACTUALES Y ELIMINARLOS DE LA TABLA OPERATIVA
			int migrados = migrarDetallesConvenioAHistorico(
					con,
					idconvenioPrest,
					userName,
					"ELIMINACION_CONVENIO",
					true);

			_log.debug("[CONV-PREST-SVC][DELETE][HIS] Detalles migrados a histórico=" + migrados);

			// BAJA LÓGICA DE CABECERA
			stmt = con.prepareCall(sql);
			stmt.setInt(1, idconvenioPrest);
			stmt.setString(2, userName);

			_log.debug("[CONV-PREST-SVC][DELETE][PARAMS] idconvenioPrest=" + idconvenioPrest + ", userName=" + userName);

			stmt.executeUpdate();

			con.commit();
			_log.info("[CONV-PREST-SVC][DELETE][COMMIT] Commit realizado correctamente");

		} catch (SQLException e) {
			_log.error("Error al dar de baja el convenio prestacional", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][DELETE][ROLLBACK] Rollback ejecutado por SQLException", e);

			if (WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE.equals(e.getSQLState())) {
				throw new NoSuchConvenioPrestacionalEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al dar de baja el convenio prestacional", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][DELETE][ROLLBACK] Rollback ejecutado por Exception", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][DELETE][FINALLY] Recursos cerrados");
		}
		return;
	}

	/**
	 * Metodo que actualiza un reintegro, le cambia el estado a un estado dado,
	 * como estado auditado
	 *
	 * @throws NoSuchContratoPrestacionEntryException
	 * @throws SystemException
	 */
	public void cambiarEstadoConvenioPrestacional(int idConvenioPrest, int estado,
												  String userName) throws NoSuchConvenioPrestacionalEntryException,
			SystemException {
		_log.info("[CONV-PREST-SVC][CHANGE-STATE][START] Inicio cambiarEstadoConvenioPrestacional id="
				+ idConvenioPrest + ", estado=" + estado + ", user=" + userName);

		Connection con = null;
		CallableStatement stmt = null;

		try {
			con = ConnectionHelper.getConnection();

			/*
			 * NO se historizan detalles acá.
			 * Este método modifica únicamente el estado de la cabecera del convenio.
			 * Historizar detalle en este flujo mezclaría auditoría de cabecera con versionado
			 * de detalle y podría dejar inconsistente el convenio activo.
			 */
			String sql = "{call convenio_prest.cambio_estado_convenio_prest(?,?,?)}";

			_log.debug("[CONV-PREST-SVC][CHANGE-STATE][SQL] " + sql);

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenioPrest);
			stmt.setInt(2, estado);
			stmt.setString(3, userName);

			_log.debug("[CONV-PREST-SVC][CHANGE-STATE][PARAMS] idConvenioPrest=" + idConvenioPrest
					+ ", estado=" + estado + ", userName=" + userName);

			stmt.executeUpdate();

			_log.info("[CONV-PREST-SVC][CHANGE-STATE][END] Estado actualizado correctamente");

		} catch (SQLException e) {
			_log.debug(e.getMessage(), e);

			if (WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE.equals(e.getSQLState())) {
				throw new NoSuchConvenioPrestacionalEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][CHANGE-STATE][FINALLY] Recursos cerrados");
		}
		return;
	}

	public int insertarConvenioPrestacional(ConvenioPrestacional convPrest, String screenName) throws SystemException {

		_log.info("[CONV-PREST-SVC][INSERT][START] Inicio insertarConvenioPrestacional user=" + screenName);

		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		int idConvenioPrest = 0;

		try {
			String sql = "{call convenio_prest.insertar_convenio_cab(?, ?, ?, ?, ?, ?, ?, ?)}";

			_log.debug("[CONV-PREST-SVC][INSERT][SQL-CAB] " + sql);

			con = ConnectionHelper.getConnectionForTransaction();
			_log.debug("[CONV-PREST-SVC][INSERT][CONN] Conexión transaccional obtenida=" + (con != null));

			stmt = con.prepareCall(sql);
			stmt.setInt(1, convPrest.getPrestador().getId_prestador());
			stmt.setInt(2, convPrest.getEstado().getIntValue());
			stmt.setInt(3, convPrest.getDiaRecepcion());
			stmt.setString(4, convPrest.getCondicionDePago());
			stmt.setInt(5, convPrest.getTipoPago().getId());

			if (convPrest.getVigencia() != null) {
				stmt.setDate(6, new java.sql.Date(convPrest.getVigencia().getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}

			if (convPrest.getVencimiento() != null) {
				stmt.setDate(7, new java.sql.Date(convPrest.getVencimiento().getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}

			stmt.setString(8, screenName);

			rs = stmt.executeQuery();
			while (rs.next()) {
				idConvenioPrest = rs.getInt(1);
				_log.debug("[CONV-PREST-SVC][INSERT][CAB] idConvenioPrest generado=" + idConvenioPrest);
			}

			if (idConvenioPrest <= 0) {
				throw new SQLException("No se obtuvo un id_convenio_prest válido al insertar la cabecera");
			}

			convPrest.setId(idConvenioPrest);

			sincronizarDetallesConvenio(con, convPrest, screenName);

			_log.debug("[CONV-PREST-SVC][INSERT][DET] Detalles sincronizados. cantidad="
					+ (convPrest.getConvenioPrestDetalle() != null ? convPrest.getConvenioPrestDetalle().size() : 0));

			con.commit();
			_log.info("[CONV-PREST-SVC][INSERT][COMMIT] Commit realizado correctamente");

		} catch (IllegalArgumentException e) {
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][ROLLBACK] Rollback por regla de negocio", e);
			throw e;
		} catch (SQLException e) {
			_log.error("Error al insertar convenio prestacional", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][INSERT][ROLLBACK] Rollback ejecutado por SQLException", e);
			throw new SystemException(e);
		} catch (Exception e) {
			_log.error("Error al insertar convenio prestacional", e);
			ConnectionHelper.rollback(con);
			_log.error("[CONV-PREST-SVC][INSERT][ROLLBACK] Rollback ejecutado por Exception", e);
			throw new SystemException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					_log.warn("[CONV-PREST-SVC][INSERT][FINALLY][WARN] No se pudo cerrar ResultSet", e);
				}
			}

			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][INSERT][FINALLY] Recursos cerrados");
		}

		_log.info("[CONV-PREST-SVC][INSERT][END] Fin insertarConvenioPrestacional idGenerado=" + idConvenioPrest);
		return idConvenioPrest;
	}

	/**
	 * Metodo que obtiene la lista de prestaciones por detalle del c�digo a partir de la clave primaria del
	 * conv.prestacional, en caso de no encontrarla arroja excepci�n
	 *
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public List<ConvenioPrestacionalDetalle> getPrestacionesDetallesPorCodigo(int idConvPrest)
			throws SystemException {

		_log.info("[CONV-PREST-SVC][GET-DET-DESG][START] Inicio getPrestacionesDetallesPorCodigo idConvPrest=" + idConvPrest);
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> detalles = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_det_desglosado(?)}";
			_log.debug("[CONV-PREST-SVC][GET-DET-DESG][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvPrest);
			_log.debug("[CONV-PREST-SVC][GET-DET-DESG][PARAM] idConvPrest=" + idConvPrest);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle convPrestDet = ConvenioPrestacionalDetalle
						.getMapping(rs, "convprestdet_");
				convPrestDet.setCodigo(convPrestDet.getCodigo()+"-"+convPrestDet.getPrestacion().getDescripcion());
				detalles.add(convPrestDet);
				_log.debug("[CONV-PREST-SVC][GET-DET-DESG][ROW] detalle agregado=" + convPrestDet);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones detalle por c�digo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][GET-DET-DESG][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][GET-DET-DESG][END] Fin getPrestacionesDetallesPorCodigo cantidad=" + detalles.size());
		return detalles;
	}

	/**
	 * Metodo que obtiene un convenio prestacional de un prestador, en caso de
	 * que est� dado de baja o de no encontrarlo retorna null
	 *
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacionalPorPrestador(int idPrestador) throws SystemException,
			NoSuchConvenioPrestacionalEntryException {

		_log.info("[CONV-PREST-SVC][GET-POR-PREST][START] Inicio getConvenioPrestacionalPorPrestador idPrestador=" + idPrestador);
		Connection con = null;
		CallableStatement stmt = null;
		ConvenioPrestacional convenioPrestacional = null;

		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_cab_por_prestador(?)}";
			_log.debug("[CONV-PREST-SVC][GET-POR-PREST][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestador);
			_log.debug("[CONV-PREST-SVC][GET-POR-PREST][PARAM] idPrestador=" + idPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				convenioPrestacional = ConvenioPrestacional.getMapping(rs, "convprest_");
				_log.debug("[CONV-PREST-SVC][GET-POR-PREST][ROW] convenioPrestacional=" + convenioPrestacional);
			}
		} catch (Exception e) {
			_log.error("Error al obtener convenio prestacional por prestador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][GET-POR-PREST][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][GET-POR-PREST][END] Fin getConvenioPrestacionalPorPrestador resultado=" + convenioPrestacional);
		return convenioPrestacional;
	}

	public List<ConvenioPrestacionalDetalle> detalleValorizarTratamiento(int id_prestador, Date fechaDesde, Date fechaHasta, String 	codigo,int plan) throws SystemException{

		_log.info("[CONV-PREST-SVC][VALORIZAR][START] Inicio detalleValorizarTratamiento id_prestador="
				+ id_prestador + ", codigo=" + codigo + ", plan=" + plan);
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> contratoDetalle = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call convenio_prest.busca_contrato_detalle_existente_para_valorizar_tratamiento(?,?,?,?,?)}";
			_log.debug("[CONV-PREST-SVC][VALORIZAR][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);

			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));

			stmt.setString(4, codigo);
			if(plan!=0){
				stmt.setInt(5, plan);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}

			_log.debug("[CONV-PREST-SVC][VALORIZAR][PARAMS] fechaDesde=" + fechaDesde
					+ ", fechaHasta=" + fechaHasta
					+ ", codigo=" + codigo
					+ ", plan=" + plan);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDetalle = ConvenioPrestacionalDetalle
						.getMapping(rs, "cd_");
				contratoDetalle.add(cpDetalle);
				_log.debug("[CONV-PREST-SVC][VALORIZAR][ROW] detalle agregado=" + cpDetalle);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalle convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][VALORIZAR][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][VALORIZAR][END] Fin detalleValorizarTratamiento cantidad=" + contratoDetalle.size());
		return contratoDetalle;
	}

	public List<ConvenioPrestacionalDetalle> detalleValorizarTratamientoV01(int id_prestador, Date fechaDesde, Date fechaHasta, String 	codigo,int plan) throws SystemException{

		_log.info("[CONV-PREST-SVC][VALORIZAR-V01][START] Inicio detalleValorizarTratamientoV01 id_prestador="
				+ id_prestador + ", codigo=" + codigo + ", plan=" + plan);
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> contratoDetalle = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call convenio_prest.busca_contrato_detalle_existente_para_valorizar_tratamiento_v01(?,?,?,?,?)}";
			_log.debug("[CONV-PREST-SVC][VALORIZAR-V01][SQL] " + sql);
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);

			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));

			stmt.setString(4, codigo);
			if(plan!=0){
				stmt.setInt(5, plan);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}

			_log.debug("[CONV-PREST-SVC][VALORIZAR-V01][PARAMS] fechaDesde=" + fechaDesde
					+ ", fechaHasta=" + fechaHasta
					+ ", codigo=" + codigo
					+ ", plan=" + plan);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDetalle = ConvenioPrestacionalDetalle
						.getMapping(rs, "convprestdet_");
				contratoDetalle.add(cpDetalle);
				_log.debug("[CONV-PREST-SVC][VALORIZAR-V01][ROW] detalle agregado=" + cpDetalle);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalle convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			_log.debug("[CONV-PREST-SVC][VALORIZAR-V01][FINALLY] Recursos cerrados");
		}
		_log.info("[CONV-PREST-SVC][VALORIZAR-V01][END] Fin detalleValorizarTratamientoV01 cantidad=" + contratoDetalle.size());
		return contratoDetalle;
	}



	public Integer getIdPrestacionPorCodigo(String codigo) {
		Connection con = null;
		java.sql.PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (StringUtils.checkEmpty(codigo)) {
				return null;
			}

			String sql = "select public.trae_id_prestacion_por_codigo(?) as id_prestacion";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, codigo.trim());

			rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}

			int idPrestacion = rs.getInt("id_prestacion");
			return rs.wasNull() ? null : Integer.valueOf(idPrestacion);

		} catch (Exception e) {
			_log.debug(e);
			throw new RuntimeException("Error buscando id_prestacion por código: " + codigo, e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public String getDescripcionPrestacionPorCodigo(String codigo) {
		Connection con = null;
		java.sql.PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (StringUtils.checkEmpty(codigo)) {
				return null;
			}

			String sql = "select public.trae_descripcion_prestacion_por_codigo(?) as descripcion_prestacion";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, codigo.trim());

			rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}

			String descripcion = rs.getString("descripcion_prestacion");
			return rs.wasNull() ? null : descripcion;

		} catch (Exception e) {
			_log.debug(e);
			throw new RuntimeException("Error buscando descripción de prestación por código: " + codigo, e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	private int migrarDetallesConvenioAHistorico(Connection con,
												 int idConvenioPrest,
												 String userName,
												 String motivo,
												 boolean eliminarOrigen) throws SQLException {

		_log.info("[CONV-PREST-SVC][HIS][START] Inicio migrarDetallesConvenioAHistorico idConvenioPrest="
				+ idConvenioPrest + ", user=" + userName + ", motivo=" + motivo
				+ ", eliminarOrigen=" + eliminarOrigen);

		PreparedStatement stmtBuscar = null;
		PreparedStatement stmtMigrar = null;
		ResultSet rsBuscar = null;
		ResultSet rsMigrar = null;

		int migrados = 0;

		try {
			String sqlBuscar = "SELECT id_convenio_prest_detalle " +
					"FROM convenio_prest.convenio_prestacional_detalle " +
					"WHERE id_convenio_prest = ? " +
					"ORDER BY id_convenio_prest_detalle";

			String sqlMigrar = "SELECT convenio_prest.migrar_convenio_det_a_historico(?, ?, ?, ?, ?)";

			_log.debug("[CONV-PREST-SVC][HIS][SQL-BUSCAR] " + sqlBuscar);
			_log.debug("[CONV-PREST-SVC][HIS][SQL-MIGRAR] " + sqlMigrar);

			stmtBuscar = con.prepareStatement(sqlBuscar);
			stmtBuscar.setInt(1, idConvenioPrest);

			rsBuscar = stmtBuscar.executeQuery();

			stmtMigrar = con.prepareStatement(sqlMigrar);

			while (rsBuscar.next()) {
				int idDetalle = rsBuscar.getInt(1);

				stmtMigrar.clearParameters();
				stmtMigrar.setInt(1, idDetalle);
				stmtMigrar.setString(2, userName);
				stmtMigrar.setString(3, motivo);
				stmtMigrar.setBoolean(4, eliminarOrigen);
				stmtMigrar.setNull(5, Types.TIMESTAMP);

				_log.debug("[CONV-PREST-SVC][HIS][CALL] Migrando detalle idDetalle=" + idDetalle);

				rsMigrar = stmtMigrar.executeQuery();

				if (!rsMigrar.next()) {
					throw new SQLException("No se obtuvo id histórico al migrar detalle id=" + idDetalle);
				}

				int idHistorico = rsMigrar.getInt(1);

				_log.debug("[CONV-PREST-SVC][HIS][OK] detalle id=" + idDetalle
						+ " migrado a histórico idHistorico=" + idHistorico);

				rsMigrar.close();
				rsMigrar = null;

				migrados++;
			}

			_log.info("[CONV-PREST-SVC][HIS][END] Fin migrarDetallesConvenioAHistorico. migrados=" + migrados);
			return migrados;

		} finally {
			if (rsMigrar != null) {
				try { rsMigrar.close(); } catch (Exception e) { _log.warn(e.getMessage(), e); }
			}
			if (rsBuscar != null) {
				try { rsBuscar.close(); } catch (Exception e) { _log.warn(e.getMessage(), e); }
			}

			ConnectionHelper.cerrar(stmtMigrar);
			ConnectionHelper.cerrar(stmtBuscar);
		}
	}

	private void insertarDetallesConvenio(Connection con,
										  int idConvenioPrest,
										  List<ConvenioPrestacionalDetalle> detalles,
										  String screenName) throws SQLException {

		CallableStatement stmtDet = null;

		try {
			String sqlDet = "{call convenio_prest.insertar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

			_log.debug("[CONV-PREST-SVC][DET][SQL] " + sqlDet);

			if (detalles == null || detalles.size() == 0) {
				_log.debug("[CONV-PREST-SVC][DET][SKIP] No hay detalles para insertar");
				return;
			}

			stmtDet = con.prepareCall(sqlDet);

			for (ConvenioPrestacionalDetalle cpDet : detalles) {
				if (cpDet == null) {
					continue;
				}

				if (cpDet.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
					_log.debug("[CONV-PREST-SVC][DET][SKIP] Detalle omitido por estado BAJA. detalle=" + cpDet);
					continue;
				}

				stmtDet.clearParameters();
				stmtDet.setInt(1, idConvenioPrest);
				stmtDet.setTimestamp(2, cpDet.getFechaDesde() != null ? new java.sql.Timestamp(cpDet.getFechaDesde().getTime()) : null);
				stmtDet.setTimestamp(3, cpDet.getFechaHasta() != null ? new java.sql.Timestamp(cpDet.getFechaHasta().getTime()) : null);
				stmtDet.setInt(4, cpDet.getPrestacion().getId());
				stmtDet.setString(5, cpDet.getCodigo());
				stmtDet.setInt(6, cpDet.getIdPlan());
				stmtDet.setBigDecimal(7, cpDet.getCoseguro());
				stmtDet.setString(8, cpDet.getTipoValorizacion());
				stmtDet.setBigDecimal(9, cpDet.getImporte());
				stmtDet.setBigDecimal(10, cpDet.getPorcentaje());
				stmtDet.setString(11, cpDet.getServicio());
				stmtDet.setString(12, screenName);

				_log.debug("[CONV-PREST-SVC][DET][INSERT] Insertando detalle=" + cpDet);

				stmtDet.executeUpdate();

				_log.debug("[CONV-PREST-SVC][DET][OK] Detalle insertado");
			}

		} finally {
			ConnectionHelper.cerrar(stmtDet);
		}
	}

	public String validarDetalleExistente(ConvenioPrestacional convenio) throws Exception {

		_log.info("[CONV-PREST-SVC][VALID-DET][START] Inicio validarDetalleExistente");

		if (convenio == null) {
			_log.warn("[CONV-PREST-SVC][VALID-DET][WARN] convenio es null");
			return null;
		}

		List<ConvenioPrestacionalDetalle> detalles = convenio.getConvenioPrestDetalle();

		if (detalles == null || detalles.isEmpty()) {
			_log.debug("[CONV-PREST-SVC][VALID-DET][SKIP] convenio sin detalles");
			return null;
		}

		_log.debug("[CONV-PREST-SVC][VALID-DET][CAB] idConvenio=" + convenio.getId()
				+ ", idPrestador=" + (convenio.getPrestador() != null ? convenio.getPrestador().getId_prestador() : 0)
				+ ", cantidadDetalles=" + detalles.size()
				+ ", vigencia=" + formatearFechaServicio(convenio.getVigencia())
				+ ", vencimiento=" + formatearFechaServicio(convenio.getVencimiento()));

		List<ItemDetalleValidacion> itemsValidos = new ArrayList<ItemDetalleValidacion>();

		for (int i = 0; i < detalles.size(); i++) {
			ConvenioPrestacionalDetalle det = detalles.get(i);
			int nroItem = i + 1;

			if (debeIgnorarseEnValidacion(det)) {
				_log.debug("[CONV-PREST-SVC][VALID-DET][SKIP] item=" + nroItem + " ignorado. motivo=null/BAJA");
				continue;
			}

			logDetalleServicio("VAL", nroItem, det);

			if (det.getPrestacion() == null || det.getPrestacion().getId() <= 0) {
				String msg = "El ítem nro: " + nroItem + " no tiene id_prestacion resuelto";
				_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
				return msg;
			}

			if (det.getFechaDesde() == null) {
				String msg = "El ítem nro: " + nroItem + " no tiene fecha desde informada";
				_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
				return msg;
			}

			if (det.getFechaHasta() != null && det.getFechaHasta().before(det.getFechaDesde())) {
				String msg = "El ítem nro: " + nroItem + " tiene fecha hasta menor a fecha desde";
				_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
				return msg;
			}

			if (convenio.getVigencia() != null
					&& truncarFechaValidacion(det.getFechaDesde()).before(truncarFechaValidacion(convenio.getVigencia()))) {
				String msg = "El ítem nro: " + nroItem + " tiene fecha desde anterior a la vigencia del convenio";
				_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
				return msg;
			}

			if (convenio.getVencimiento() != null) {
				Date fechaDesdeDet = truncarFechaValidacion(det.getFechaDesde());
				Date fechaVencConvenio = truncarFechaValidacion(convenio.getVencimiento());

				if (fechaDesdeDet.after(fechaVencConvenio)) {
					String msg = "El ítem nro: " + nroItem + " tiene fecha desde posterior al vencimiento del convenio";
					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
					return msg;
				}

				if (det.getFechaHasta() != null) {
					Date fechaHastaDet = truncarFechaValidacion(det.getFechaHasta());
					if (fechaHastaDet.after(fechaVencConvenio)) {
						String msg = "El ítem nro: " + nroItem + " tiene fecha hasta posterior al vencimiento del convenio";
						_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
						return msg;
					}
				}
			}

			itemsValidos.add(new ItemDetalleValidacion(nroItem, det));
		}

		Map<ClaveDetalle, List<ItemDetalleValidacion>> grupos = agruparItemsPorClaveFuncional(itemsValidos);

		for (Map.Entry<ClaveDetalle, List<ItemDetalleValidacion>> entry : grupos.entrySet()) {
			ClaveDetalle clave = entry.getKey();
			List<ItemDetalleValidacion> grupo = entry.getValue();

			ordenarItemsPorFechaDesde(grupo);

			_log.debug("[CONV-PREST-SVC][VALID-DET][GRUPO] id_prestacion=" + clave.idPrestacion
					+ ", id_plan=" + clave.idPlan
					+ ", cantidad=" + grupo.size());

			for (int i = 0; i < grupo.size(); i++) {
				ItemDetalleValidacion actual = grupo.get(i);

				_log.debug("[CONV-PREST-SVC][VALID-DET][GRUPO-ITEM] item=" + actual.nroItem
						+ ", keyFuncional=" + buildClaveFuncionalDebug(actual.detalle)
						+ ", codigo=" + safe(actual.detalle.getCodigo())
						+ ", fechaDesde=" + formatearFechaServicio(actual.detalle.getFechaDesde())
						+ ", fechaHasta=" + formatearFechaServicio(actual.detalle.getFechaHasta()));
			}

			for (int i = 0; i < grupo.size() - 1; i++) {
				ItemDetalleValidacion actual = grupo.get(i);
				ItemDetalleValidacion siguiente = grupo.get(i + 1);

				Date fechaDesdeActual = truncarFechaValidacion(actual.detalle.getFechaDesde());
				Date fechaDesdeSiguiente = truncarFechaValidacion(siguiente.detalle.getFechaDesde());

				if (mismaFechaValidacion(fechaDesdeActual, fechaDesdeSiguiente)) {
					String msg = "El ítem nro: " + actual.nroItem
							+ " tiene duplicado exacto con el ítem nro: "
							+ siguiente.nroItem
							+ " para la misma prestación y plan (misma fecha desde)";

					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR][DETAIL] "
							+ "A={idPrestacion=" + actual.detalle.getPrestacion().getId()
							+ ", codigo=" + safe(actual.detalle.getCodigo())
							+ ", idPlan=" + actual.detalle.getIdPlan()
							+ ", fechaDesde=" + formatearFechaServicio(actual.detalle.getFechaDesde())
							+ ", fechaHasta=" + formatearFechaServicio(actual.detalle.getFechaHasta())
							+ "} "
							+ "B={idPrestacion=" + siguiente.detalle.getPrestacion().getId()
							+ ", codigo=" + safe(siguiente.detalle.getCodigo())
							+ ", idPlan=" + siguiente.detalle.getIdPlan()
							+ ", fechaDesde=" + formatearFechaServicio(siguiente.detalle.getFechaDesde())
							+ ", fechaHasta=" + formatearFechaServicio(siguiente.detalle.getFechaHasta())
							+ "}");

					return msg;
				}

				if (!fechaDesdeSiguiente.after(fechaDesdeActual)) {
					String msg = "La secuencia temporal de la prestación/plan está desordenada entre los ítems nro: "
							+ actual.nroItem + " y " + siguiente.nroItem;

					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
					return msg;
				}

				Date fechaHastaActual = truncarFechaValidacion(actual.detalle.getFechaHasta());

				// Sólo rechazo solapamiento real.
				// Si fechaHasta es null acá, todavía no es error: el service de sincronización puede normalizar la cadena.
				if (fechaHastaActual != null && !fechaHastaActual.before(fechaDesdeSiguiente)) {
					String msg = "El ítem nro: " + actual.nroItem
							+ " se superpone temporalmente con el ítem nro: " + siguiente.nroItem
							+ " para la misma prestación y plan";

					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR] " + msg);
					_log.warn("[CONV-PREST-SVC][VALID-DET][ERROR][DETAIL] "
							+ "A={idPrestacion=" + actual.detalle.getPrestacion().getId()
							+ ", codigo=" + safe(actual.detalle.getCodigo())
							+ ", idPlan=" + actual.detalle.getIdPlan()
							+ ", fechaDesde=" + formatearFechaServicio(actual.detalle.getFechaDesde())
							+ ", fechaHasta=" + formatearFechaServicio(actual.detalle.getFechaHasta())
							+ "} "
							+ "B={idPrestacion=" + siguiente.detalle.getPrestacion().getId()
							+ ", codigo=" + safe(siguiente.detalle.getCodigo())
							+ ", idPlan=" + siguiente.detalle.getIdPlan()
							+ ", fechaDesde=" + formatearFechaServicio(siguiente.detalle.getFechaDesde())
							+ ", fechaHasta=" + formatearFechaServicio(siguiente.detalle.getFechaHasta())
							+ "}");

					return msg;
				}
			}
		}

		_log.info("[CONV-PREST-SVC][VALID-DET][END] Fin validarDetalleExistente sin conflictos");
		return null;
	}

	private DiagnosticoComparacion diagnosticarComparacion(
			ConvenioPrestacionalDetalle a, int itemA,
			ConvenioPrestacionalDetalle b, int itemB) {

		String codigoA = normalizarCodigoServicio(a.getCodigo());
		String codigoB = normalizarCodigoServicio(b.getCodigo());

		boolean mismoCodigo = codigoA.equals(codigoB);
		boolean mismoPlan = a.getIdPlan() == b.getIdPlan();
		boolean mismaFechaDesde = formatearFechaServicioKey(a.getFechaDesde())
				.equals(formatearFechaServicioKey(b.getFechaDesde()));

		boolean conflictoExacto = mismoCodigo && mismoPlan && mismaFechaDesde;

		DiagnosticoComparacion d = new DiagnosticoComparacion();
		d.setItemA(itemA);
		d.setItemB(itemB);
		d.setBusinessKeyA(buildBusinessKeyServicio(a));
		d.setBusinessKeyB(buildBusinessKeyServicio(b));
		d.setMismoCodigo(mismoCodigo);
		d.setMismoPlan(mismoPlan);
		d.setMismaFechaDesde(mismaFechaDesde);
		d.setConflictoExacto(conflictoExacto);

		return d;
	}

	private boolean debeIgnorarseEnValidacion(ConvenioPrestacionalDetalle det) {
		return det == null || det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA;
	}

	private void logDetalleServicio(String alias, int nroItem, ConvenioPrestacionalDetalle det) {

		if (det == null) {
			_log.debug("[CONV-PREST-SVC][VALID-DET][DETAIL] alias=" + alias + ", item=" + nroItem + " -> null");
			return;
		}

		_log.debug("[CONV-PREST-SVC][VALID-DET][DETAIL] alias=" + alias
				+ ", item=" + nroItem
				+ ", id=" + det.getId()
				+ ", estado=" + det.getEstado()
				+ ", codigo=" + safe(det.getCodigo())
				+ ", idPlan=" + det.getIdPlan()
				+ ", fechaDesde=" + formatearFechaServicio(det.getFechaDesde())
				+ ", fechaHasta=" + formatearFechaServicio(det.getFechaHasta())
				+ ", businessKeyExacta=" + buildBusinessKeyServicio(det));
	}

	private String buildBusinessKeyServicio(ConvenioPrestacionalDetalle det) {
		if (det == null) {
			return "";
		}

		return normalizarCodigoServicio(det.getCodigo())
				+ "|" + formatearFechaServicioKey(det.getFechaDesde())
				+ "|" + det.getIdPlan();
	}

	private String normalizarCodigoServicio(String codigo) {
		return codigo != null ? codigo.trim().toLowerCase() : "";
	}

	private String formatearFechaServicio(Date fecha) {
		if (fecha == null) {
			return "null";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
	}

	private String formatearFechaServicioKey(Date fecha) {
		if (fecha == null) {
			return "";
		}
		return new SimpleDateFormat("yyyyMMdd").format(fecha);
	}

	private String safe(String value) {
		return value != null ? value : "";
	}

	private static class DiagnosticoComparacion {

		private int itemA;
		private int itemB;
		private String businessKeyA;
		private String businessKeyB;
		private boolean mismoCodigo;
		private boolean mismoPlan;
		private boolean mismaFechaDesde;
		private boolean conflictoExacto;

		public int getItemA() { return itemA; }
		public void setItemA(int itemA) { this.itemA = itemA; }

		public int getItemB() { return itemB; }
		public void setItemB(int itemB) { this.itemB = itemB; }

		public String getBusinessKeyA() { return businessKeyA; }
		public void setBusinessKeyA(String businessKeyA) { this.businessKeyA = businessKeyA; }

		public String getBusinessKeyB() { return businessKeyB; }
		public void setBusinessKeyB(String businessKeyB) { this.businessKeyB = businessKeyB; }

		public boolean isMismoCodigo() { return mismoCodigo; }
		public void setMismoCodigo(boolean mismoCodigo) { this.mismoCodigo = mismoCodigo; }

		public boolean isMismoPlan() { return mismoPlan; }
		public void setMismoPlan(boolean mismoPlan) { this.mismoPlan = mismoPlan; }

		public boolean isMismaFechaDesde() { return mismaFechaDesde; }
		public void setMismaFechaDesde(boolean mismaFechaDesde) { this.mismaFechaDesde = mismaFechaDesde; }

		public boolean isConflictoExacto() { return conflictoExacto; }
		public void setConflictoExacto(boolean conflictoExacto) { this.conflictoExacto = conflictoExacto; }
	}

	private void sincronizarDetallesConvenio(Connection con,
											 ConvenioPrestacional convPrest,
											 String userName) throws SQLException {

		List<ConvenioPrestacionalDetalle> actuales = obtenerDetallesOperativos(con, convPrest.getId());

		Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> actualesPorClave =
				agruparActualesPorClave(actuales);

		Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> entradaPorClave =
				agruparDetallesPorClave(convPrest.getConvenioPrestDetalle());

		Set<Integer> idsMarcadosParaBorradoFisico =
				obtenerIdsMarcadosParaBorradoFisico(convPrest.getConvenioPrestDetalle());

		Set<ClaveDetalle> todasLasClaves = new LinkedHashSet<ClaveDetalle>();
		todasLasClaves.addAll(actualesPorClave.keySet());
		todasLasClaves.addAll(entradaPorClave.keySet());

		for (ClaveDetalle clave : todasLasClaves) {
			List<ConvenioPrestacionalDetalle> grupoActual = actualesPorClave.get(clave);
			List<ConvenioPrestacionalDetalle> grupoEntrada = entradaPorClave.get(clave);

			procesarGrupoDetalle(
					con,
					convPrest.getId(),
					clave,
					grupoActual,
					grupoEntrada,
					idsMarcadosParaBorradoFisico,
					userName);
		}
	}

	private void procesarGrupoDetalle(Connection con,
									  int idConvenioPrest,
									  ClaveDetalle clave,
									  List<ConvenioPrestacionalDetalle> actualesGrupo,
									  List<ConvenioPrestacionalDetalle> grupoEntrada,
									  Set<Integer> idsMarcadosParaBorradoFisico,
									  String userName) throws SQLException {

		List<ConvenioPrestacionalDetalle> actuales = filtrarDetallesNoBaja(actualesGrupo);
		List<ConvenioPrestacionalDetalle> entrada = filtrarDetallesNoBaja(grupoEntrada);

		ordenarPorFechaDesde(actuales);
		normalizarCadenaTemporal(clave, entrada);

		Map<Integer, ConvenioPrestacionalDetalle> actualesPorId =
				new HashMap<Integer, ConvenioPrestacionalDetalle>();

		for (ConvenioPrestacionalDetalle actual : actuales) {
			if (actual != null && actual.getId() > 0) {
				actualesPorId.put(actual.getId(), actual);
			}
		}

		Set<Integer> idsYaProcesados = new HashSet<Integer>();

		ConvenioPrestacionalDetalle finalOperativo = null;
		if (entrada != null && !entrada.isEmpty()) {
			finalOperativo = entrada.get(entrada.size() - 1);
		}

		// 1) Todo lo anterior al último se considera antecesor:
		//    se actualiza (si hace falta) y se migra a histórico.
		for (int i = 0; entrada != null && i < entrada.size() - 1; i++) {
			ConvenioPrestacionalDetalle detEntrada = entrada.get(i);

			if (detEntrada == null) {
				continue;
			}

			if (detEntrada.getId() <= 0) {
				throw new IllegalArgumentException(
						"No se puede generar más de una versión para la misma prestación/plan " +
								"si las versiones intermedias no existen previamente. " +
								"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan);
			}

			ConvenioPrestacionalDetalle actual = actualesPorId.get(detEntrada.getId());

			if (actual == null) {
				throw new IllegalArgumentException(
						"Se intentó versionar un detalle inexistente. " +
								"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan +
								", id_detalle=" + detEntrada.getId());
			}

			if (!crearClave(actual).equals(crearClave(detEntrada))) {
				throw new IllegalArgumentException(
						"El detalle cambió de clave funcional. " +
								"id_detalle=" + detEntrada.getId());
			}

			boolean cambiaFechaDesde = !mismaFecha(detEntrada.getFechaDesde(), actual.getFechaDesde());
			if (cambiaFechaDesde && !esPosteriorAHoy(actual.getFechaDesde())) {
				throw new IllegalArgumentException(
						"No se puede modificar la fecha desde de un detalle vigente o pasado. " +
								"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan +
								", id_detalle=" + detEntrada.getId());
			}

			actualizarDetalleConvenio(con, detEntrada, userName);

			historizarYEliminarDetalle(
					con,
					detEntrada,
					detEntrada.getFechaHasta(),
					userName,
					"DETALLE_REEMPLAZADO_EN_UPDATE");

			idsYaProcesados.add(detEntrada.getId());
		}

		// 2) Todo actual que no va a ser el operativo final:
		//    delete físico si corresponde; si no, histórico.
		for (ConvenioPrestacionalDetalle actual : actuales) {
			if (actual == null || actual.getId() <= 0) {
				continue;
			}

			if (idsYaProcesados.contains(actual.getId())) {
				continue;
			}

			if (finalOperativo != null && finalOperativo.getId() > 0
					&& actual.getId() == finalOperativo.getId()) {
				continue;
			}

			if (idsMarcadosParaBorradoFisico.contains(actual.getId())) {
				eliminarDetalleConvenioFisico(con, actual.getId());

				_log.debug("[CONV-PREST-SVC][SYNC][DELETE-FISICO] detalle id=" + actual.getId()
						+ " eliminado físicamente. id_prestacion=" + clave.idPrestacion
						+ ", id_plan=" + clave.idPlan);

				idsYaProcesados.add(actual.getId());
				continue;
			}

			Date fechaHastaHistorico = resolverFechaHastaHistoricoParaEliminado(actual, entrada);

			historizarYEliminarDetalle(
					con,
					actual,
					fechaHastaHistorico,
					userName,
					"DETALLE_ELIMINADO_EN_UPDATE");

			_log.debug("[CONV-PREST-SVC][SYNC][DELETE/HIS] detalle id=" + actual.getId()
					+ " migrado a histórico. id_prestacion=" + clave.idPrestacion
					+ ", id_plan=" + clave.idPlan
					+ ", fechaHastaHistorico=" + formatearFechaServicio(fechaHastaHistorico));

			idsYaProcesados.add(actual.getId());
		}

		// 3) Persistir SOLO el operativo final
		if (finalOperativo != null) {
			if (finalOperativo.getId() > 0) {
				ConvenioPrestacionalDetalle actualFinal = actualesPorId.get(finalOperativo.getId());

				if (actualFinal == null) {
					throw new IllegalArgumentException(
							"Se intentó actualizar un detalle final inexistente. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan +
									", id_detalle=" + finalOperativo.getId());
				}

				if (!crearClave(actualFinal).equals(crearClave(finalOperativo))) {
					throw new IllegalArgumentException(
							"El detalle final cambió de clave funcional. " +
									"id_detalle=" + finalOperativo.getId());
				}

				boolean cambiaFechaDesde = !mismaFecha(finalOperativo.getFechaDesde(), actualFinal.getFechaDesde());
				if (cambiaFechaDesde && !esPosteriorAHoy(actualFinal.getFechaDesde())) {
					throw new IllegalArgumentException(
							"No se puede modificar la fecha desde de un detalle vigente o pasado. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan +
									", id_detalle=" + finalOperativo.getId());
				}

				actualizarDetalleConvenio(con, finalOperativo, userName);

				_log.debug("[CONV-PREST-SVC][SYNC][UPDATE-FINAL] detalle id=" + finalOperativo.getId()
						+ ", id_prestacion=" + clave.idPrestacion
						+ ", id_plan=" + clave.idPlan
						+ ", fechaDesde=" + formatearFechaServicio(finalOperativo.getFechaDesde())
						+ ", fechaHasta=" + formatearFechaServicio(finalOperativo.getFechaHasta()));
			}
			else {
				if (!actuales.isEmpty() && !esPosteriorAHoy(finalOperativo.getFechaDesde())) {
					throw new IllegalArgumentException(
							"No se puede agregar un nuevo detalle con la misma prestación y plan " +
									"si la fecha desde es igual o menor a la actual. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan +
									", fecha_desde=" + formatearFechaServicio(finalOperativo.getFechaDesde()));
				}

				finalOperativo.setId(0);
				insertarDetalleConvenio(con, idConvenioPrest, finalOperativo, userName);

				_log.debug("[CONV-PREST-SVC][SYNC][INSERT-FINAL] detalle nuevo insertado. id_prestacion="
						+ clave.idPrestacion + ", id_plan=" + clave.idPlan
						+ ", fechaDesde=" + formatearFechaServicio(finalOperativo.getFechaDesde())
						+ ", fechaHasta=" + formatearFechaServicio(finalOperativo.getFechaHasta()));
			}
		}
	}

	private static final class ClaveDetalle {
		private final int idPrestacion;
		private final int idPlan;

		private ClaveDetalle(int idPrestacion, int idPlan) {
			this.idPrestacion = idPrestacion;
			this.idPlan = idPlan;
		}

		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ClaveDetalle)) return false;
			ClaveDetalle other = (ClaveDetalle) o;
			return idPrestacion == other.idPrestacion && idPlan == other.idPlan;
		}

		public int hashCode() {
			int result = idPrestacion;
			result = 31 * result + idPlan;
			return result;
		}
	}

	private ClaveDetalle crearClave(ConvenioPrestacionalDetalle det) {
		if (det == null || det.getPrestacion() == null) {
			throw new IllegalArgumentException("Detalle sin prestación resuelta");
		}
		return new ClaveDetalle(det.getPrestacion().getId(), det.getIdPlan());
	}

	private Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> agruparDetallesPorClave(
			List<ConvenioPrestacionalDetalle> detalles) {

		Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> out =
				new LinkedHashMap<ClaveDetalle, List<ConvenioPrestacionalDetalle>>();

		if (detalles == null) {
			return out;
		}

		for (ConvenioPrestacionalDetalle det : detalles) {
			if (det == null || det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			ClaveDetalle clave = crearClave(det);
			List<ConvenioPrestacionalDetalle> grupo = out.get(clave);

			if (grupo == null) {
				grupo = new ArrayList<ConvenioPrestacionalDetalle>();
				out.put(clave, grupo);
			}

			grupo.add(det);
		}

		return out;
	}

	private boolean esPosteriorAHoy(Date fecha) {
		if (fecha == null) {
			return false;
		}

		Calendar hoy = Calendar.getInstance();
		hoy.set(Calendar.HOUR_OF_DAY, 0);
		hoy.set(Calendar.MINUTE, 0);
		hoy.set(Calendar.SECOND, 0);
		hoy.set(Calendar.MILLISECOND, 0);

		Calendar f = Calendar.getInstance();
		f.setTime(fecha);
		f.set(Calendar.HOUR_OF_DAY, 0);
		f.set(Calendar.MINUTE, 0);
		f.set(Calendar.SECOND, 0);
		f.set(Calendar.MILLISECOND, 0);

		return f.after(hoy);
	}

	private boolean mismaFecha(Date a, Date b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;

		Calendar ca = Calendar.getInstance();
		ca.setTime(a);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);

		Calendar cb = Calendar.getInstance();
		cb.setTime(b);
		cb.set(Calendar.HOUR_OF_DAY, 0);
		cb.set(Calendar.MINUTE, 0);
		cb.set(Calendar.SECOND, 0);
		cb.set(Calendar.MILLISECOND, 0);

		return ca.getTime().equals(cb.getTime());
	}

	private Date finDelDiaAnterior(Date fechaDesdeNueva) {
		Calendar c = Calendar.getInstance();
		c.setTime(fechaDesdeNueva);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DATE, -1);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	private List<ConvenioPrestacionalDetalle> obtenerDetallesOperativos(Connection con, int idConvenioPrest) throws SQLException {
		List<ConvenioPrestacionalDetalle> out = new ArrayList<ConvenioPrestacionalDetalle>();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql =
					"SELECT id_convenio_prest_detalle, fecha_desde, fecha_hasta, id_prestacion, " +
							"       codigo, id_plan, coseguro, tipo_valorizacion, importe, porcentaje, servicio " +
							"  FROM convenio_prest.convenio_prestacional_detalle " +
							" WHERE id_convenio_prest = ? " +
							" ORDER BY id_convenio_prest_detalle";

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idConvenioPrest);
			rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle det = new ConvenioPrestacionalDetalle();
				det.setId(rs.getInt("id_convenio_prest_detalle"));
				det.setFechaDesde(rs.getTimestamp("fecha_desde"));
				det.setFechaHasta(rs.getTimestamp("fecha_hasta"));
				det.setPrestacion(new Prestacion(rs.getInt("id_prestacion"), null));
				det.setCodigo(rs.getString("codigo"));
				det.setIdPlan(rs.getInt("id_plan"));
				det.setCoseguro(rs.getBigDecimal("coseguro"));
				det.setTipoValorizacion(rs.getString("tipo_valorizacion"));
				det.setImporte(rs.getBigDecimal("importe"));
				det.setPorcentaje(rs.getBigDecimal("porcentaje"));
				det.setServicio(rs.getString("servicio"));
				out.add(det);
			}

			return out;
		}
		finally {
			if (rs != null) {
				try { rs.close(); } catch (Exception e) { _log.warn(e.getMessage(), e); }
			}
			ConnectionHelper.cerrar(stmt);
		}
	}

	private void historizarYEliminarDetalle(Connection con,
											ConvenioPrestacionalDetalle actual,
											Date fechaHastaHistorico,
											String userName,
											String motivo) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql =
					"SELECT convenio_prest.migrar_convenio_det_a_historico(?, ?, ?, ?, ?)";

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, actual.getId());
			stmt.setString(2, userName);
			stmt.setString(3, motivo);
			stmt.setBoolean(4, true);

			if (fechaHastaHistorico != null) {
				stmt.setTimestamp(5, new Timestamp(fechaHastaHistorico.getTime()));
			} else {
				stmt.setNull(5, Types.TIMESTAMP);
			}

			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new SQLException("No se pudo migrar a histórico el detalle id=" + actual.getId());
			}

			int idHistorico = rs.getInt(1);

			_log.debug("[CONV-PREST-SVC][HIS][OK] detalle id=" + actual.getId()
					+ " migrado a histórico idHistorico=" + idHistorico
					+ ", fechaHastaHistorico=" + formatearFechaServicio(fechaHastaHistorico)
					+ ", motivo=" + motivo);
		}
		finally {
			if (rs != null) {
				try { rs.close(); } catch (Exception e) { _log.warn(e.getMessage(), e); }
			}
			ConnectionHelper.cerrar(stmt);
		}
	}

	private void insertarDetalleConvenio(Connection con,
										 int idConvenioPrest,
										 ConvenioPrestacionalDetalle cpDet,
										 String screenName) throws SQLException {

		CallableStatement stmtDet = null;

		try {
			String sqlDet = "{call convenio_prest.insertar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmtDet = con.prepareCall(sqlDet);

			stmtDet.setInt(1, idConvenioPrest);
			stmtDet.setTimestamp(2, cpDet.getFechaDesde() != null ? new Timestamp(cpDet.getFechaDesde().getTime()) : null);
			stmtDet.setTimestamp(3, cpDet.getFechaHasta() != null ? new Timestamp(cpDet.getFechaHasta().getTime()) : null);
			stmtDet.setInt(4, cpDet.getPrestacion().getId());
			stmtDet.setString(5, cpDet.getCodigo());
			stmtDet.setInt(6, cpDet.getIdPlan());
			stmtDet.setBigDecimal(7, cpDet.getCoseguro());
			stmtDet.setString(8, cpDet.getTipoValorizacion());
			stmtDet.setBigDecimal(9, cpDet.getImporte());
			stmtDet.setBigDecimal(10, cpDet.getPorcentaje());
			stmtDet.setString(11, cpDet.getServicio());
			stmtDet.setString(12, screenName);

			stmtDet.executeUpdate();
		}
		finally {
			ConnectionHelper.cerrar(stmtDet);
		}
	}

	private void actualizarDetalleConvenio(Connection con,
										   ConvenioPrestacionalDetalle cpDet,
										   String screenName) throws SQLException {

		PreparedStatement stmt = null;

		try {
			String sql = "SELECT convenio_prest.actualizar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = con.prepareStatement(sql);

			stmt.setInt(1, cpDet.getId());
			stmt.setTimestamp(2, cpDet.getFechaDesde() != null ? new Timestamp(cpDet.getFechaDesde().getTime()) : null);
			stmt.setTimestamp(3, cpDet.getFechaHasta() != null ? new Timestamp(cpDet.getFechaHasta().getTime()) : null);
			stmt.setInt(4, cpDet.getPrestacion().getId());
			stmt.setString(5, cpDet.getCodigo());
			stmt.setInt(6, cpDet.getIdPlan());
			stmt.setBigDecimal(7, cpDet.getCoseguro());
			stmt.setString(8, cpDet.getTipoValorizacion());
			stmt.setBigDecimal(9, cpDet.getImporte());
			stmt.setBigDecimal(10, cpDet.getPorcentaje());
			stmt.setString(11, cpDet.getServicio());
			stmt.setString(12, screenName);

			stmt.executeQuery();
		}
		finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	private Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> agruparActualesPorClave(
			List<ConvenioPrestacionalDetalle> detalles) {

		Map<ClaveDetalle, List<ConvenioPrestacionalDetalle>> out =
				new LinkedHashMap<ClaveDetalle, List<ConvenioPrestacionalDetalle>>();

		if (detalles == null) {
			return out;
		}

		for (ConvenioPrestacionalDetalle det : detalles) {
			if (det == null) {
				continue;
			}

			ClaveDetalle clave = crearClave(det);
			List<ConvenioPrestacionalDetalle> grupo = out.get(clave);

			if (grupo == null) {
				grupo = new ArrayList<ConvenioPrestacionalDetalle>();
				out.put(clave, grupo);
			}

			grupo.add(det);
		}

		return out;
	}

	private List<ConvenioPrestacionalDetalle> filtrarDetallesNoBaja(
			List<ConvenioPrestacionalDetalle> detalles) {

		List<ConvenioPrestacionalDetalle> out = new ArrayList<ConvenioPrestacionalDetalle>();

		if (detalles == null) {
			return out;
		}

		for (ConvenioPrestacionalDetalle det : detalles) {
			if (det == null) {
				continue;
			}

			if (det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				continue;
			}

			out.add(det);
		}

		return out;
	}

	private void ordenarPorFechaDesde(List<ConvenioPrestacionalDetalle> detalles) {
		if (detalles == null || detalles.isEmpty()) {
			return;
		}

		Collections.sort(detalles, new Comparator<ConvenioPrestacionalDetalle>() {
			public int compare(ConvenioPrestacionalDetalle a, ConvenioPrestacionalDetalle b) {
				Date fa = a != null ? a.getFechaDesde() : null;
				Date fb = b != null ? b.getFechaDesde() : null;

				if (fa == null && fb == null) return 0;
				if (fa == null) return -1;
				if (fb == null) return 1;
				return fa.compareTo(fb);
			}
		});
	}

	private void normalizarCadenaTemporal(ClaveDetalle clave,
										  List<ConvenioPrestacionalDetalle> grupo) {

		if (grupo == null || grupo.isEmpty()) {
			return;
		}

		ordenarPorFechaDesde(grupo);

		for (int i = 0; i < grupo.size(); i++) {
			ConvenioPrestacionalDetalle actual = grupo.get(i);

			if (actual == null || actual.getFechaDesde() == null) {
				throw new IllegalArgumentException(
						"Detalle sin fecha_desde para la clave funcional. " +
								"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan);
			}

			if (i < grupo.size() - 1) {
				ConvenioPrestacionalDetalle siguiente = grupo.get(i + 1);

				if (siguiente == null || siguiente.getFechaDesde() == null) {
					throw new IllegalArgumentException(
							"Detalle siguiente sin fecha_desde para la clave funcional. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan);
				}

				if (!siguiente.getFechaDesde().after(actual.getFechaDesde())) {
					throw new IllegalArgumentException(
							"Se detectaron fechas desde duplicadas o desordenadas para la misma prestación y plan. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan);
				}

				Date nuevaFechaHasta = finDelDiaAnterior(siguiente.getFechaDesde());

				if (nuevaFechaHasta.before(actual.getFechaDesde())) {
					throw new IllegalArgumentException(
							"Cadena temporal inválida para la misma prestación y plan. " +
									"id_prestacion=" + clave.idPrestacion + ", id_plan=" + clave.idPlan);
				}

				actual.setFechaHasta(nuevaFechaHasta);
			}
			else {
				// La última versión queda abierta.
				actual.setFechaHasta(null);
			}
		}
	}

	private Date resolverFechaHastaHistoricoParaEliminado(
			ConvenioPrestacionalDetalle actual,
			List<ConvenioPrestacionalDetalle> entradaOrdenada) {

		if (actual == null) {
			return null;
		}

		if (entradaOrdenada == null || entradaOrdenada.isEmpty()) {
			return actual.getFechaHasta();
		}

		Date fechaActual = actual.getFechaDesde();

		for (ConvenioPrestacionalDetalle entrada : entradaOrdenada) {
			if (entrada == null || entrada.getFechaDesde() == null) {
				continue;
			}

			if (fechaActual == null || entrada.getFechaDesde().after(fechaActual)) {
				return finDelDiaAnterior(entrada.getFechaDesde());
			}
		}

		return actual.getFechaHasta();
	}

	private static final class ItemDetalleValidacion {
		private final int nroItem;
		private final ConvenioPrestacionalDetalle detalle;

		private ItemDetalleValidacion(int nroItem, ConvenioPrestacionalDetalle detalle) {
			this.nroItem = nroItem;
			this.detalle = detalle;
		}
	}

	private Map<ClaveDetalle, List<ItemDetalleValidacion>> agruparItemsPorClaveFuncional(
			List<ItemDetalleValidacion> items) {

		Map<ClaveDetalle, List<ItemDetalleValidacion>> out =
				new LinkedHashMap<ClaveDetalle, List<ItemDetalleValidacion>>();

		if (items == null) {
			return out;
		}

		for (ItemDetalleValidacion item : items) {
			if (item == null || item.detalle == null) {
				continue;
			}

			ClaveDetalle clave = crearClave(item.detalle);
			List<ItemDetalleValidacion> grupo = out.get(clave);

			if (grupo == null) {
				grupo = new ArrayList<ItemDetalleValidacion>();
				out.put(clave, grupo);
			}

			grupo.add(item);
		}

		return out;
	}

	private void ordenarItemsPorFechaDesde(List<ItemDetalleValidacion> items) {
		if (items == null || items.isEmpty()) {
			return;
		}

		Collections.sort(items, new Comparator<ItemDetalleValidacion>() {
			public int compare(ItemDetalleValidacion a, ItemDetalleValidacion b) {
				Date fa = truncarFechaValidacion(a != null && a.detalle != null ? a.detalle.getFechaDesde() : null);
				Date fb = truncarFechaValidacion(b != null && b.detalle != null ? b.detalle.getFechaDesde() : null);

				if (fa == null && fb == null) return 0;
				if (fa == null) return -1;
				if (fb == null) return 1;
				return fa.compareTo(fb);
			}
		});
	}

	private Date truncarFechaValidacion(Date fecha) {
		if (fecha == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private boolean mismaFechaValidacion(Date a, Date b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}

	private String buildClaveFuncionalDebug(ConvenioPrestacionalDetalle det) {
		if (det == null || det.getPrestacion() == null) {
			return "";
		}

		return det.getPrestacion().getId() + "|" + det.getIdPlan();
	}

	private Set<Integer> obtenerIdsMarcadosParaBorradoFisico(List<ConvenioPrestacionalDetalle> detalles) {
		Set<Integer> out = new HashSet<Integer>();

		if (detalles == null || detalles.isEmpty()) {
			return out;
		}

		for (ConvenioPrestacionalDetalle det : detalles) {
			if (det == null) {
				continue;
			}

			if (det.getId() > 0 && det.getEstado() == ConvenioPrestacionalDetalle.ESTADOS.BAJA) {
				out.add(det.getId());
			}
		}

		return out;
	}

	private void eliminarDetalleConvenioFisico(Connection con, int idConvenioPrestDetalle) throws SQLException {

		PreparedStatement stmt = null;

		try {
			String sql =
					"DELETE FROM convenio_prest.convenio_prestacional_detalle " +
							"WHERE id_convenio_prest_detalle = ?";

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idConvenioPrestDetalle);

			int afectados = stmt.executeUpdate();

			if (afectados <= 0) {
				throw new SQLException("No se pudo eliminar físicamente el detalle id=" + idConvenioPrestDetalle);
			}
		}
		finally {
			ConnectionHelper.cerrar(stmt);
		}
	}
}