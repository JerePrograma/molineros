package ar.com.ospim.tesoreria.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.global.beans.ProcesoSQL;
import ar.com.ospim.procesaArchivos.beans.JubiladosSitaci;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import freemarker.template.SimpleDate;

public class LiquidaDesreguladosServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(LiquidaDesreguladosServiceImpl.class);

	public List<ConsolidadoLiquidaciones> getConsolidadoLiquidaciones(
			String id_terc, Date fechaDesde, Date fechaHasta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConsolidadoLiquidaciones> consolidado = null;
		try {
			String sql = "{call trae_ultimas_derivaciones_desregulados_filtro(?,?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());

			if (null != id_terc && id_terc.trim().length() > 0) {
				stmt.setString(1, id_terc);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != fechaDesde) {
				stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			if (null != fechaHasta) {
				stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			consolidado = new ArrayList<ConsolidadoLiquidaciones>();
			while (rs.next()) {
				consolidado.add(ConsolidadoLiquidaciones
						.getMappingDesregulados(rs));
			}
		} catch (Exception e) {
			logger.error("error al buscar consolidado liquidaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidado;
	}

	public List<String> getDerivaDesregulaString(String id_terc, Date fecha_liq)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_deriv_aportes_desregulados(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id_terc);
			stmt.setDate(2, new java.sql.Date(fecha_liq.getTime()));

			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			String delimiter = " ";
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("cuit_contribuyente"));
				linea.append(delimiter);
				linea.append(rs.getString("periodo"));
				linea.append(delimiter);
				linea.append(rs.getString("ingre_fecha"));
				linea.append(delimiter);
				linea.append(rs.getString("cuil_aportante"));
				linea.append(delimiter);
				linea.append(rs.getString("remuneracion"));
				linea.append(delimiter);
				linea.append(rs.getString("aporte"));
				linea.append(delimiter);
				linea.append(rs.getString("contribucion"));
				linea.append(delimiter);
				linea.append(rs.getString("total"));
				linea.append(delimiter);
				linea.append(rs.getString("total_tercerizadora"));
				linea.append(delimiter);
				linea.append(rs.getString("apellido_nombre"));
				linea.append(delimiter);
				linea.append(rs.getString("situacioncuil"));
				linea.append(delimiter);
				linea.append(rs.getString("fecha_pres_ddjj"));
				linea.append(delimiter);
				linea.append(rs.getString("fecha_proc_ddjj"));
				linea.append(delimiter);
				linea.append(rs.getString("original_rectificativa"));
				
				result.add(linea.toString());
			}
		} catch(Exception e) {
			logger.error(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;

	}

	public List<String> getAfiliadosSinAporteString(String id_terc,
			Date fecha_liq) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_deriv_desregulados_sin_aporte(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id_terc);
			stmt.setDate(2, new java.sql.Date(fecha_liq.getTime()));

			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			String delimiter = " ";
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("cuit_contribuyente"));
				linea.append(delimiter);
				linea.append(rs.getString("periodo"));
				linea.append(delimiter);
				linea.append(rs.getString("ingre_fecha"));
				linea.append(delimiter);
				linea.append(rs.getString("cuil_aportante"));
				linea.append(delimiter);
				linea.append(rs.getString("remuneracion"));
				linea.append(delimiter);
				linea.append(rs.getString("aporte"));
				linea.append(delimiter);
				linea.append(rs.getString("contribucion"));
				linea.append(delimiter);
				linea.append(rs.getString("total"));
				linea.append(delimiter);
				linea.append(rs.getString("total_tercerizadora"));
				linea.append(delimiter);
				linea.append(rs.getString("apellido_nombre"));
				linea.append(delimiter);
				linea.append(rs.getString("situacioncuil"));
				linea.append(delimiter);
				linea.append(rs.getString("fecha_pres_ddjj"));
				linea.append(delimiter);
				linea.append(rs.getString("fecha_proc_ddjj"));
				linea.append(delimiter);
				linea.append(rs.getString("original_rectificativa"));
				
				result.add(linea.toString());
			}
		} catch(Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;

	}

	public ProcesoSQL isRunningProcess() throws Exception {
		ProcesoSQL result = new ProcesoSQL();
		Connection con = null;
		CallableStatement stmt = null;

		try {
			//logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  consulta_proceso_psql(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, "liquidacion_tercerizadoras");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				result.setProcid(rs.getInt("procid"));				
				result.setFechaComienzo(rs.getTimestamp("comienzo"));
			}
		} catch(Exception e) {
			logger.error(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;

	}
	
	public boolean cancelaProceso(int procid) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  pg_cancel_backend(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, procid);
			stmt.executeQuery();	
		} catch(Exception e) {
			logger.error(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return true;

	}

	public List<String> getComisionesTercerizadoraString(String idTercerizadora,
			Date fechaLiq) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar periodo = Calendar.getInstance();
		periodo.setTime(fechaLiq);
		periodo.add(Calendar.MONTH, -1);
		
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			
			con = ConnectionHelper.getConnection();
			String sql = "{call reporte_deriv_comision_tercerizadoras(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, idTercerizadora);
			stmt.setDate(2, new java.sql.Date(fechaLiq.getTime()));

			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			String delimiter = " ";

//			StringBuffer titulo = new StringBuffer();
//			titulo.append("REPORTE COMISIONES PROYECTO VOLVER al "+ sdf.format(periodo.getTime()));
//			titulo.append(delimiter);
//			result.add(titulo.toString());
			
			StringBuffer cabecera = new StringBuffer();
			cabecera.append("CuilTitular");
			cabecera.append(delimiter);
			cabecera.append(String.format("%1$10s","Período"));
			cabecera.append(delimiter);
			cabecera.append(String.format("%1$11s","Remun"));
			cabecera.append(delimiter);
			cabecera.append(String.format("%1$11s","Aporte"));
			cabecera.append(delimiter);
			cabecera.append(String.format("%1$11s","Contrib"));
			cabecera.append(delimiter);
			cabecera.append(String.format("%1$11s","Total"));
			cabecera.append(delimiter);
			cabecera.append("Vigencia");
			result.add(cabecera.toString());
			
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("cuil_titular"));
				linea.append(delimiter);
				linea.append(rs.getString("periodo"));
				linea.append(delimiter);
				linea.append(rs.getString("remuneracion"));
				linea.append(delimiter);
				linea.append(rs.getString("aporte"));
				linea.append(delimiter);
				linea.append(rs.getString("contribucion"));
				linea.append(delimiter);
				linea.append(rs.getString("total"));
				linea.append(delimiter);
				linea.append(rs.getString("vigencia"));

				result.add(linea.toString());
			}
		} catch(Exception e) {
			logger.error(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;

	}
	
	/////////////////////////
	//Liquidacion Jubilados
	/////////////////////////
	
	public List<String> getPedidoInformeJubilados(
			 Date fechaDesde, Date fechaHasta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<String> consolidado = null;
		try {
			String sql = "{call pedido_informacion_jubilados_sitaci(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());

			if (null != fechaDesde) {
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != fechaHasta) {
				stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			consolidado = new ArrayList<String>();
			while (rs.next()) {
				consolidado.add(rs.getString("cadena"));
			}
		} catch (Exception e) {
			logger.error("error al buscar informe Jubilados citaci", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidado;
	}
	
	public List<JubiladosSitaci> getPeriodosProcesadosJubilados()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<JubiladosSitaci> consolidados = null;
		try {
			String sql = "{call jubilados_sitaci_periodos_procesados_consolidados()}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
/*
			if (null != id_terc && id_terc.trim().length() > 0) {
				stmt.setString(1, id_terc);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != fechaDesde) {
				stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			if (null != fechaHasta) {
				stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
*/			
			ResultSet rs = stmt.executeQuery();
			consolidados = new ArrayList<JubiladosSitaci>();
			while (rs.next()) {
				JubiladosSitaci j = new JubiladosSitaci();
				j.setPeriodo(rs.getString("periodo"));
				j.setTotalRegistros(rs.getInt("cantidad_registros"));
				j.setConceptoImporte(rs.getDouble("total"));
				j.setFechaLiquidado(rs.getDate("fecha_liquidado"));
				j.setImporteLiquidado(rs.getDouble("importe_liquidado"));
				
				consolidados.add(j);
			}
		} catch (Exception e) {
			logger.error("error al buscar consolidado liquidaciones jubilaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidados;
	}

	public Integer liquidarPeriodoJubilados(String periodo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call jubilados_sitaci_liquidar(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,periodo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Error al liquidar periodo Jubilados Sitaci", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<JubiladosSitaci> getJubilados(String periodo, String cuil, String dni,String tercerizadora)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<JubiladosSitaci> consolidados = null;
		try {
			String sql = "{call jubilados_sitaci_filtrar(?,?,?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());

			if (null != periodo && !"".equals(periodo)) {
				stmt.setString(1,periodo);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if (null != cuil && !"".equals(cuil)) {
				stmt.setString(2,cuil);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if (null != dni && !"".equals(dni)) {
				stmt.setString(3,dni);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if (null != tercerizadora && !"".equals(tercerizadora)) {
				stmt.setString(4,tercerizadora);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			consolidados = new ArrayList<JubiladosSitaci>();
			while (rs.next()) {
				JubiladosSitaci j = new JubiladosSitaci();
				j.setBeneficio(rs.getString("beneficio"));
				j.setAfiliado(rs.getString("afiliado"));
				j.setTipo1(rs.getString("tipo_1"));
				j.setTipo2(rs.getString("tipo_2"));
				j.setDni(rs.getString("dni"));
				j.setConcepto(rs.getString("concepto_empresa"));
				j.setSumatoria(rs.getDouble("sumatoria"));
				j.setConceptoImporte(rs.getDouble("concepto_importe"));
				j.setPeriodo(rs.getString("periodo"));
				j.setCuil(rs.getString("cuil"));
				j.setNacimiento(rs.getDate("nacimiento"));
				j.setSexo(rs.getString("sexo"));
				j.setFiller01(rs.getString("filler_01"));
				j.setFechaLiquidado(rs.getDate("liquidacion_fecha"));
				j.setImporteLiquidado(rs.getDouble("liquidacion_importe"));
				j.setRegistro(rs.getString("registro"));
				j.setTercerizadora(rs.getString("tercerizadora"));
				j.setTercerizadoraDescripcion(rs.getString("tercerizadora_descripcion"));
				j.setPeriodoLiquidacion(rs.getInt("periodo_liquidacion"));
			    
				consolidados.add(j);
			}
		} catch (Exception e) {
			logger.error("error al filtrar jubilados liquidacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidados;
	}
	
	public Integer eliminarPeriodoJubilados(String periodo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call jubilados_sitaci_eliminar(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,periodo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Error al eliminar periodo Jubilados Sitaci", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

}
