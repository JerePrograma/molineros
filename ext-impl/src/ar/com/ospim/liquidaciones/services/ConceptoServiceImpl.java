package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroConcepto;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroCuenta;
import ar.com.ospim.tesoreria.beans.ConceptoAfip;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ConceptoServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(ConceptoServiceImpl.class);

	public List<ParametroConcepto> getParametrosConceptos(int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ParametroConcepto> lista = null;
		String sql ="{call buscar_parametros_conceptos()}";
		try{
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_parametros_conceptos_uoma()}";
			}
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ParametroConcepto>();
			while (rs.next()) {
				ParametroConcepto op = ParametroConcepto.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_parametros_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public void update(Concepto concepto, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_concepto_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_concepto_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)}";
			} else {
				sql = "{call actualizar_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, concepto.getId());
			stmt.setString(2, concepto.getDescripcion());
			if (concepto.getPlanCuentas() != null) {
				stmt.setInt(3, concepto.getPlanCuentas().getId());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (concepto.getPlanCuentasPasivo() != null) {
				stmt.setInt(4, concepto.getPlanCuentasPasivo().getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			stmt.setBoolean(5, concepto.isLiquidaciones());
			stmt.setBoolean(6, concepto.isEgreso());
			stmt.setBoolean(7, concepto.isIngreso());
			stmt.setDate(8, new java.sql.Date(concepto.getValidoDesde()
					.getTime()));
			stmt.setDate(9, new java.sql.Date(concepto.getValidoHasta()
					.getTime()));
			stmt.setBoolean(10, concepto.isSubEgreso());
			stmt.setBoolean(11, concepto.isSubIngreso());
			stmt.setString(12, user.getScreenName());
			if (entidad == WebKeysGlobal.UOMA) {
				if(concepto.getIdSeccional()>0){
					stmt.setInt(13, concepto.getIdSeccional());
				}else{
					stmt.setNull(13, Types.INTEGER);
				}
				stmt.setInt(14, concepto.getIdSecuencial());
			}
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al buscar  pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public List<PrestacionConcepto> getPrestacionesConceptos(
			Calendar desdeEjercicio, Calendar hastaEjercicio) {
		Connection con = null;
		CallableStatement stmt = null;
		List<PrestacionConcepto> lista = null;
		try {
			String sql = "{call buscar_nomenclador_conceptos(?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desdeEjercicio.getTimeInMillis()));
			stmt.setDate(2, new java.sql.Date(hastaEjercicio.getTimeInMillis()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PrestacionConcepto>();
			while (rs.next()) {
				PrestacionConcepto op = PrestacionConcepto.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_nomenclador_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public PrestacionConcepto getPrestacionesConceptosActualPorIdPrestacion(
			int id, Date desdeEjercicio, Date hastaEjercicio) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_nomenclador_conceptos_por_id_prestacion(?, ?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setDate(2, new java.sql.Date(desdeEjercicio.getTime()));
			stmt.setDate(3, new java.sql.Date(hastaEjercicio.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PrestacionConcepto.getMapping(rs);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_nomenclador_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public void update(Connection con, Prestacion prest, BigDecimal coefHono,
			BigDecimal coefGastos, User user) {
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_nomenclador(?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, prest.getId_prestacion());
			stmt.setString(2, prest.getCodigo());
			stmt.setString(3, prest.getDescripcion());
			stmt.setBigDecimal(4, coefGastos);
			stmt.setBigDecimal(5, coefHono);
			stmt.setString(6, user.getScreenName());
			stmt.setInt(7, prest.getMarca_rein_liq());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al actualizar prestacion concepto", e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void updateNomencladorConcepto(Connection connectionParam, int id_prestacion,
			int idNomencladorConcepto, int cocneptoId, Date desde, Date hasta,
			int tipo, User user) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_nomenclador_concepto(?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			stmt = connectionParam.prepareCall(sql.toString());
			stmt.setInt(1, id_prestacion);
			stmt.setInt(2, idNomencladorConcepto);
			stmt.setInt(3, cocneptoId);
			stmt.setDate(4, new java.sql.Date(desde.getTime()));
			stmt.setDate(5, new java.sql.Date(hasta.getTime()));
			stmt.setInt(6, tipo);
			stmt.setString(7, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al actualizar prestacion concepto", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void guardar(Concepto concepto, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_concepto_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_concepto_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call insertar_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, concepto.getDescripcion());
			if (concepto.getPlanCuentas() != null) {
				stmt.setInt(2, concepto.getPlanCuentas().getId());
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (concepto.getPlanCuentasPasivo() != null) {
				stmt.setInt(3, concepto.getPlanCuentasPasivo().getId());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			stmt.setBoolean(4, concepto.isLiquidaciones());
			stmt.setBoolean(5, concepto.isEgreso());
			stmt.setBoolean(6, concepto.isIngreso());
			stmt.setDate(7, new java.sql.Date(concepto.getValidoDesde()
					.getTime()));
			stmt.setDate(8, new java.sql.Date(concepto.getValidoHasta()
					.getTime()));
			stmt.setBoolean(9, concepto.isSubEgreso());
			stmt.setBoolean(10, concepto.isSubIngreso());
			stmt.setString(11, user.getScreenName());
			if (entidad == WebKeysGlobal.UOMA) {
				if (concepto.getIdSeccional() > 0) {
					stmt.setInt(12, concepto.getIdSeccional());
				} else {
					stmt.setNull(12, Types.INTEGER);
				}
			}
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				concepto.setId(executeQuery.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al actualizar prestacion concepto", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void eliminar(Concepto concepto, Date desde, Date hasta, User user,
			int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_concepto_amtima(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_concepto_uoma(?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call eliminar_concepto(?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, concepto.getId());
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			stmt.setString(4, user.getScreenName());
			if (entidad == WebKeysGlobal.UOMA) {
				stmt.setInt(5, concepto.getIdSecuencial());
				stmt.setInt(6, concepto.getIdSeccional());
			}
			
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al eliminar concepto", e);
			if (e.getMessage().contains("violates foreign key constraint")) {
				throw new ConceptoUtilizadoException();
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public boolean estaUtilizado(Concepto concepto, Date desde, Date hasta,
			int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_concepto_utilizado_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_concepto_utilizado_uoma(?, ?, ?, ?)}";
			} else {
				sql = "{call verificar_concepto_utilizado(?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, concepto.getId());
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			if(entidad==WebKeysGlobal.UOMA){
				stmt.setInt(4, concepto.getIdSecuencial());	
			}
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getBoolean(1);
			}
		} catch (Exception e) {
			logger.error("Error al eliminar concepto", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return true;
	}

	public void guardar(PrestacionConcepto prestConcepto, User user,
			Date inicioEjercicio, Date finEjercicio) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_nomenclador_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, prestConcepto.getPrestacion().getCodigo());
			stmt.setString(2, prestConcepto.getPrestacion().getDescripcion());
			stmt.setInt(3, prestConcepto.getHonorariosAmbulatorio().getId());
			stmt.setInt(4, prestConcepto.getHonorariosInternacion().getId());
			stmt.setInt(5, prestConcepto.getGastosAmbulatorio().getId());
			stmt.setInt(6, prestConcepto.getGastosInternacion().getId());
			stmt.setBigDecimal(7, prestConcepto.getCoeficienteGastos());
			stmt.setBigDecimal(8, prestConcepto.getCoeficienteHonorarios());

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha1800 = sdf.parse("01/01/1800");

			stmt.setDate(9, new java.sql.Date(fecha1800.getTime()));
			// stmt.setDate(9, new java.sql.Date(inicioEjercicio.getTime()));
			// QUE VAYA DIRECTAMENTE 1800 PARA TODOS LOS EJERCICIOS
			stmt.setDate(10, new java.sql.Date(finEjercicio.getTime()));
			stmt.setString(11, user.getScreenName());
			stmt.setInt(12, prestConcepto.getPrestacion().getMarca_rein_liq());
			stmt.setInt(13, prestConcepto.getIdTipoNomenclador());
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				prestConcepto.getPrestacion().setId_prestacion(
						executeQuery.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al insertar_nomenclador_concepto", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void eliminar(PrestacionConcepto prestacionConcepto, User user)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call eliminar_prestacion_concepto(?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, prestacionConcepto.getPrestacion().getId());
			stmt.setString(2, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			if (e.getMessage().contains("viola la llave")) {
				throw new ConceptoUtilizadoException();
			}
			logger.error("Error al eliminar eliminar_prestacion_concepto", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void reemplazarNomencladorConcepto(Connection con,
			int id_prestacion, Date desdeNuevo, Date desdeOriginal,
			Date hastaNuevo, Date hastaOriginal,
			int idNomencladorConceptoAReemplazar, int idNuevoConcepto,
			Calendar desdeEjercicioActual, Calendar infinito, User user,
			int tipo_id) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call reemplazar_prestacion_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestacion);
			stmt.setDate(2, new java.sql.Date(desdeOriginal.getTime()));
			stmt.setDate(3, new java.sql.Date(hastaOriginal.getTime()));
			stmt.setInt(4, idNomencladorConceptoAReemplazar);
			stmt.setInt(5, idNuevoConcepto);
			stmt.setDate(6, new java.sql.Date(desdeNuevo.getTime()));
			stmt.setDate(7, new java.sql.Date(hastaNuevo.getTime()));
			stmt.setString(8, user.getScreenName());
			stmt.setInt(9, tipo_id);
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al eliminar reemplazar_prestacion_concepto", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}

	}

	public void reemplazar(Concepto conceptoOriginal, Concepto conceptoNuevo,
			User user, int entidad) throws Exception {
		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();

			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reemplazar_concepto_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reemplazar_concepto_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call reemplazar_concepto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, conceptoOriginal.getId());
			stmt.setDate(2, new java.sql.Date(conceptoOriginal.getValidoDesde()
					.getTime()));
			stmt.setDate(3, new java.sql.Date(conceptoOriginal.getValidoHasta()
					.getTime()));
			stmt.setString(4, conceptoNuevo.getDescripcion());
			if (conceptoNuevo.getPlanCuentas() != null) {
				stmt.setInt(5, conceptoNuevo.getPlanCuentas().getId());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			if (conceptoNuevo.getPlanCuentasPasivo() != null) {
				stmt.setInt(6, conceptoNuevo.getPlanCuentasPasivo().getId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setBoolean(7, conceptoNuevo.isLiquidaciones());
			stmt.setBoolean(8, conceptoNuevo.isEgreso());
			stmt.setBoolean(9, conceptoNuevo.isIngreso());
			stmt.setDate(10, new java.sql.Date(conceptoNuevo.getValidoDesde()
					.getTime()));
			stmt.setDate(11, new java.sql.Date(conceptoNuevo.getValidoHasta()
					.getTime()));
			stmt.setBoolean(12, conceptoNuevo.isSubEgreso());
			stmt.setBoolean(13, conceptoNuevo.isSubIngreso());
			stmt.setString(14, user.getScreenName());
			if (entidad == WebKeysGlobal.UOMA) {
				if (conceptoNuevo.getIdSeccional() > 0) {
					stmt.setInt(15, conceptoNuevo.getIdSeccional());
				} else {
					stmt.setNull(15, Types.INTEGER);
				}
				stmt.setInt(16, conceptoOriginal.getIdSecuencial());
				
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				conceptoNuevo.setId(rs.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al reemplazar_concepto", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<ParametroConcepto> getParametrosConceptos(Date validoDesde,
			Date validoHasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ParametroConcepto> lista = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_parametros_conceptos_por_fecha_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_parametros_conceptos_por_fecha_uoma(?, ?)}";
			} else {
				sql = "{call buscar_parametros_conceptos_por_fecha(?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(validoDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(validoHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ParametroConcepto>();
			while (rs.next()) {
				ParametroConcepto op = ParametroConcepto.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_parametros_conceptos_por_fecha", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public Concepto getConceptoCuitComproTipo(String cuit,
			String comproTipo, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		Concepto concepto = null;
		try {
			logger.debug("getConceptoCuitComproTipo(String cuit "+cuit+ " , String comproTipo "+comproTipo +" , int entidad "+ entidad +" )");
			
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_cuit_compro_tipo_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_cuit_compro_tipo_uoma(?, ?)}";
			} else {
				sql = "{call buscar_concepto_cuit_compro_tipo(?, ?)}";
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, comproTipo);
			ResultSet rs = stmt.executeQuery();			
			while (rs.next()) {
				concepto = Concepto.getMapping(rs);				
			}
		} catch (Exception e) {
			logger.error("Error al buscar concepto", e);
			logger.debug("getConceptoCuitComproTipo(String cuit "+cuit+ " , String comproTipo "+comproTipo +" , int entidad "+ entidad +" )");
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return concepto;
	}

	public void reemplazarParametroConcepto(
			ParametroConcepto parametroConceptoOriginal,
			ParametroConcepto pcNuevo, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reemplazar_parametro_concepto_amtima(?, ?, ?, ?, ?, ?, ?)}";
			}
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reemplazar_parametro_concepto_uoma(?, ?, ?, ?, ?, ?, ?)}";
			} else {

				sql = "{call reemplazar_parametro_concepto(?, ?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, pcNuevo.getParametro());
			stmt.setDate(2, new java.sql.Date(parametroConceptoOriginal
					.getValidoDesde().getTime()));
			stmt.setDate(3, new java.sql.Date(parametroConceptoOriginal
					.getValidoHasta().getTime()));
			stmt.setDate(4, new java.sql.Date(pcNuevo.getValidoDesde()
					.getTime()));
			stmt.setDate(5, new java.sql.Date(pcNuevo.getValidoHasta()
					.getTime()));
			stmt.setInt(6, pcNuevo.getConceptoId());
			stmt.setString(7, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al reemplazar_parametro_concepto", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<ParametroCuenta> getParametrosCuentas(Date validoDesde,
			Date validoHasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ParametroCuenta> lista = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_parametros_cuenta_por_fecha_amtima(?, ?)}";
			}else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_parametros_cuenta_por_fecha_uoma(?, ?)}";
			} else {
				sql = "{call buscar_parametros_cuenta_por_fecha(?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(validoDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(validoHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ParametroCuenta>();
			while (rs.next()) {
				ParametroCuenta op = ParametroCuenta.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_parametros_cuenta_por_fecha", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<ParametroCuenta> getParametrosCuentas(int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ParametroCuenta> lista = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_parametros_cuenta_amtima()}";
			}else if (entidad == WebKeysGlobal.UOMA) { 
				sql = "{call uoma.buscar_parametros_cuenta_uoma()}";
				}else {
					sql = "{call buscar_parametros_cuenta()}";
				}
//			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ParametroCuenta>();
			while (rs.next()) {
				ParametroCuenta op = ParametroCuenta.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_parametros_cuenta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public void reemplazarParametroCuenta(
			ParametroCuenta parametroCuentaOriginal, ParametroCuenta pcNuevo,
			User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reemplazar_parametro_cuenta_amtima(?, ?, ?, ?, ?, ?, ?)}";
			}
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reemplazar_parametro_cuenta_uoma(?, ?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call reemplazar_parametro_cuenta(?, ?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, pcNuevo.getParametro());
			stmt.setDate(2, new java.sql.Date(parametroCuentaOriginal
					.getValidoDesde().getTime()));
			stmt.setDate(3, new java.sql.Date(parametroCuentaOriginal
					.getValidoHasta().getTime()));
			stmt.setDate(4, new java.sql.Date(pcNuevo.getValidoDesde()
					.getTime()));
			stmt.setDate(5, new java.sql.Date(pcNuevo.getValidoHasta()
					.getTime()));
			stmt.setInt(6, pcNuevo.getPlanCuentas().getId());
			stmt.setString(7, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al reemplazar_parametro_concepto", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public boolean verificarEquivalenciasConceptosCompleto(Date desde,
			Date hasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_equivalencias_conceptos_completo_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_equivalencias_conceptos_completo_uoma(?, ?)}";
			} else {
				sql = "{call verificar_equivalencias_conceptos_completo(?, ?)}";
			}
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desde.getTime()));
			stmt.setDate(2, new java.sql.Date(hasta.getTime()));
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getBoolean(1);
			}
		} catch (Exception e) {
			logger.error("Error al verificar_equivalencias_conceptos_completo",
					e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return false;
	}

	public boolean verificarEquivalenciasPrestacionesCompleto(Date desde,
			Date hasta, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_equivalencias_prestaciones_completo_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_equivalencias_prestaciones_completo_uoma(?, ?)}";
			} else {
				sql = "{call verificar_equivalencias_prestaciones_completo(?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desde.getTime()));
			stmt.setDate(2, new java.sql.Date(hasta.getTime()));
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getBoolean(1);
			}
		} catch (Exception e) {
			logger.error(
					"Error al verificar_equivalencias_prestaciones_completo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return false;
	}

	public void update(TipoMovBcrio tipo, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_tipo_mov_bcrio_amtima(?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_tipo_mov_bcrio_uoma(?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call actualizar_tipo_mov_bcrio(?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipo.getDescripcion());
			stmt.setDate(2, new java.sql.Date(tipo.getValidoDesde().getTime()));
			stmt.setDate(3, new java.sql.Date(tipo.getValidoHasta().getTime()));
			if (tipo.getConcepto() != null) {
				stmt.setInt(4, tipo.getConcepto().getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			stmt.setInt(5, tipo.getId_tipo_mov());
			stmt.setString(6, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al buscar_nomenclador_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void reemplazar(TipoMovBcrio tipoEnBase, TipoMovBcrio tipo,
			User user, int entidad) throws Exception {
		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reemplazar_tipo_mov_bcrio_amtima(?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reemplazar_tipo_mov_bcrio_uoma(?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call reemplazar_tipo_mov_bcrio(?, ?, ?, ?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, tipoEnBase.getId_tipo_mov());
			stmt.setDate(2, new java.sql.Date(tipoEnBase.getValidoDesde()
					.getTime()));
			stmt.setDate(3, new java.sql.Date(tipoEnBase.getValidoHasta()
					.getTime()));
			stmt.setString(4, tipo.getDescripcion());
			stmt.setDate(5, new java.sql.Date(tipo.getValidoDesde().getTime()));
			stmt.setDate(6, new java.sql.Date(tipo.getValidoHasta().getTime()));
			if (tipo.getConcepto() != null) {
				stmt.setInt(7, tipo.getConcepto().getId());
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			stmt.setString(8, user.getScreenName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tipo.setId_tipo_mov(rs.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al reemplazar_tipo_mov_bcrio", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void guardar(TipoMovBcrio tipo, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;

			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_tipo_mov_bcrio_amtima(?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_tipo_mov_bcrio_uoma(?, ?, ?, ?, ?)}";
			} else {
				sql = "{call insertar_tipo_mov_bcrio(?, ?, ?, ?, ?)}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipo.getDescripcion());
			stmt.setDate(2, new java.sql.Date(tipo.getValidoDesde().getTime()));
			stmt.setDate(3, new java.sql.Date(tipo.getValidoHasta().getTime()));
			if (tipo.getConcepto() != null) {
				stmt.setInt(4, tipo.getConcepto().getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			stmt.setString(5, user.getScreenName());
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				tipo.setId_tipo_mov(executeQuery.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al insertar_tipo_mov_bcrio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void eliminar(TipoMovBcrio tipoMovBcrio, User user, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_tipo_mov_bcrio_amtima(?, ? )}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_tipo_mov_bcrio_uoma(?, ? )}";
			} else {
				sql = "{call eliminar_tipo_mov_bcrio(?, ? )}";
			}

			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, tipoMovBcrio.getId_tipo_mov());
			stmt.setString(2, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al eliminar_tipo_mov_bcrio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public List<ConceptoAfip> getConceptosAfip(Date desdeEjercicio,
			Date hastaEjercicio) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConceptoAfip> lista = null;
		try {
			String sql = "{call buscar_conceptos_afip(?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desdeEjercicio.getTime()));
			stmt.setDate(2, new java.sql.Date(hastaEjercicio.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ConceptoAfip>();
			while (rs.next()) {
				ConceptoAfip op = ConceptoAfip.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_conceptos_afip", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public void guardar(ConceptoAfip cAfip, User user) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_concepto_afip(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cAfip.getDescripcion());
			stmt.setString(2, cAfip.getCodigoConcepto());
			stmt.setString(3, cAfip.getCodigoContraConcepto());
			stmt.setBoolean(4, cAfip.isLiquidable());
			stmt.setString(5, cAfip.getDebitoCredito());
			stmt.setDate(6, new java.sql.Date(cAfip.getValidoDesde().getTime()));
			stmt.setDate(7, new java.sql.Date(cAfip.getValidoHasta().getTime()));
			stmt.setInt(8, cAfip.getConcepto().getId());
			stmt.setString(9, user.getScreenName());
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				cAfip.setId(executeQuery.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al insertar_concepto_afip", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void update(ConceptoAfip cAfip, User user) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_concepto_afip(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cAfip.getDescripcion());
			stmt.setString(2, cAfip.getCodigoConcepto());
			stmt.setString(3, cAfip.getCodigoContraConcepto());
			stmt.setBoolean(4, cAfip.isLiquidable());
			stmt.setString(5, cAfip.getDebitoCredito());
			stmt.setDate(6, new java.sql.Date(cAfip.getValidoDesde().getTime()));
			stmt.setDate(7, new java.sql.Date(cAfip.getValidoHasta().getTime()));
			stmt.setInt(8, cAfip.getConcepto().getId());
			stmt.setInt(9, cAfip.getId());
			stmt.setString(10, user.getScreenName());
			stmt.execute();
		} catch (Exception e) {
			logger.error("Error al actualizar_concepto_afip", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void reemplazar(ConceptoAfip cAfipEnBase, ConceptoAfip cAfip,
			User user) throws Exception {
		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call reemplazar_concepto_afip(?, ?, ?, ?, ?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, cAfipEnBase.getId());
			stmt.setDate(2, new java.sql.Date(cAfipEnBase.getValidoDesde()
					.getTime()));
			stmt.setDate(3, new java.sql.Date(cAfipEnBase.getValidoHasta()
					.getTime()));
			stmt.setString(4, cAfip.getDescripcion());
			stmt.setDate(5, new java.sql.Date(cAfip.getValidoDesde().getTime()));
			stmt.setDate(6, new java.sql.Date(cAfip.getValidoHasta().getTime()));
			stmt.setInt(7, cAfip.getConcepto().getId());
			stmt.setString(8, user.getScreenName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				cAfip.setId(rs.getInt(1));
			}
		} catch (Exception e) {
			logger.error("Error al eliminar reemplazar_concepto_afip", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int getIdConceptoAjuste() throws Exception {
		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_id_concepto_ajuste()}";
			logger.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_id_concepto_ajuste", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public List<ParametroCuenta> getParametrosContabilidad(String parametro,int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ParametroCuenta> lista = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_parametros_contabilidad_amtima(?)}";
			}else if (entidad == WebKeysGlobal.UOMA) { 
				sql = "{call uoma.buscar_parametros_contabilidad_uoma(?)}";
				}else {
					sql = "{call buscar_parametros_contabilidad(?)}";
				}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,parametro);
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ParametroCuenta>();
			while (rs.next()) {
				ParametroCuenta op = ParametroCuenta.getMapping(rs);
				lista.add(op);
			}
		} catch (Exception e) {
			logger.error("Error al buscar_parametros_cuenta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
}
