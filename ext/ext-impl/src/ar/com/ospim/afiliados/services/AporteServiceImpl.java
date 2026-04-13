package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compass.core.util.backport.java.util.Collections;

//import ar.com.ospim.afiliados.action.GuardarOtrosDatosAction.AportesYEgreso;
import ar.com.ospim.afiliados.beans.AfiAporte;
import ar.com.ospim.afiliados.beans.AfiAporteList;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.afiliados.beans.TipoAporte.ID_GENERADO;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="AporteServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class AporteServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(AporteServiceImpl.class);

	private void actualizarFechaIds(boolean modificoUoma, Date fechaBajaUoma,
			boolean modificoAmtima, Date fechaBajaAmtima,
			boolean modificoOspim, Date fechaBajaOspim, Connection con,
			String cuil) throws SQLException {
		CallableStatement stmt = null;
		try {
			String sqlPlan = "{call actualiza_fecha_ids_afiliado(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sqlPlan.toString());
			stmt.setBoolean(1, modificoUoma);
			stmt.setDate(2, fechaBajaUoma == null ? null : new java.sql.Date(
					fechaBajaUoma.getTime()));
			stmt.setBoolean(3, modificoAmtima);
			stmt.setDate(4, fechaBajaAmtima == null ? null : new java.sql.Date(
					fechaBajaAmtima.getTime()));
			stmt.setBoolean(5, modificoOspim);
			stmt.setDate(6, fechaBajaOspim == null ? null : new java.sql.Date(
					fechaBajaOspim.getTime()));
			stmt.setString(7, cuil);
			stmt.executeUpdate();
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}
	}

	public Plan getPlanAfiliado(Connection con, String cuil, int inte)
			throws Exception {
		CallableStatement stmt = null;
		Plan plan = null;
		try {
			// Busco el plan del afiliado
			String sqlPlan = "{call trae_plan_afiliado(?,?)}";
			stmt = con.prepareCall(sqlPlan.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			ResultSet rsPlan = stmt.executeQuery();

			while (rsPlan.next()) {
				plan = new Plan(rsPlan.getInt("id_plan"),
						rsPlan.getString("descripcion"),
						rsPlan.getInt("id_plan_omint"),
						rsPlan.getString("descripcion_omint"));
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}
		return plan;
	}

	public Map<Integer, AfiAporte> traeAportesAfi(Connection con, String cuil,
			int inte) throws Exception {

		Map<Integer, AfiAporte> aportes = null;
		CallableStatement stmt = null;
		try {
			String sqlList = "{call trae_tipos_aporte_afi(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			aportes = new HashMap<Integer, AfiAporte>();
			while (rs.next()) {
				AfiAporte bp = new AfiAporte(rs.getString("cuil_titular"),
						rs.getInt("inte"), rs.getString("cuil"),
						rs.getInt("id_aporte"), rs.getString("descripcion"),
						rs.getDate("fecha_ingreso"),
						rs.getDate("fecha_egreso"),
						rs.getString("motivo_baja"), rs.getInt("id_afiliado"),rs.getBoolean("es_os"));
				MotivoBaja mot = new MotivoBaja(rs.getInt("id_motivo_baja"),
						rs.getString("motivo_baja"));
				bp.setMotivo_baja(mot);
				aportes.put(bp.getTipoAporte().getId_aporte(), bp);
			}

			return aportes;
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}

	}

	public Map<Integer, AfiAporte> getTiposAportesPlan(Connection con,
			String cuil, int inte, int id_plan) throws Exception {
		CallableStatement stmt = null;
		Map<Integer, AfiAporte> aportes = null;
		try {
			String sqlList = "{call trae_tipos_aporte_plan(?,?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setInt(3, id_plan);
			ResultSet rs = stmt.executeQuery();
			aportes = new HashMap<Integer, AfiAporte>();
			while (rs.next()) {
				AfiAporte bp = new AfiAporte(rs.getString("cuil_titular"),
						rs.getInt("inte"), rs.getString("cuil"),
						rs.getInt("id_aporte"), rs.getString("descripcion"),
						rs.getDate("fechaingreso"), null, null, 0, rs.getBoolean("es_os"));
				aportes.put(bp.getTipoAporte().getId_aporte(), bp);
			}
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}

		return aportes;

	}

	public Map<Integer, AfiAporte> preparaAportesNuevoPlan(
			Map<Integer, AfiAporte> mapActual, Map<Integer, AfiAporte> mapNueva)
			throws Exception {

		List<AfiAporte> listaNueva = new ArrayList<AfiAporte>();
		List<AfiAporte> listaActual = new ArrayList<AfiAporte>();

		listaNueva.addAll(mapNueva.values());
		listaActual.addAll(mapActual.values());

		for (AfiAporte aporteNuevo : listaNueva) {
			AfiAporte aporteActual = mapActual.get(aporteNuevo.getTipoAporte()
					.getId_aporte());
			// elimino fechas inicio de los que coinciden con el nuevo plan...
			if (aporteActual != null) {
				aporteActual.setFecha_egre(null);
				mapActual.put(aporteActual.getTipoAporte().getId_aporte(),
						aporteActual);
				// sino existe en la lista lo agrego
			} else {
				mapActual.put(aporteNuevo.getTipoAporte().getId_aporte(),
						aporteNuevo);
			}
		}

		for (AfiAporte aporteActual : listaActual) {
			AfiAporte aporteNuevo = mapNueva.get(aporteActual.getTipoAporte()
					.getId_aporte());
			if (null == aporteNuevo) {
				AfiAporte aCambiar = mapActual.get(aporteActual.getTipoAporte()
						.getId_aporte());
				if(null==aporteActual.getFecha_egre()){
					aCambiar.setFecha_egre(new Date());
				}
				mapActual.put(aporteActual.getTipoAporte().getId_aporte(),
						aCambiar);
			}
		}

		return mapActual;
	}

	@SuppressWarnings("deprecation")
	// funcion vieja
	public AfiAporteList buscaAportesPorPlan(String id_plan, String cuil,
			int inte) throws Exception {
		Connection con = null;
		AfiAporteList afiAporteList = new AfiAporteList();
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			// Plan actual del afiliado
			afiAporteList.setPlan(getPlanAfiliado(con, cuil, inte));

			// No tiene plan, traigo los aportes correspondientes al plan
			// solicitado
			if (null == afiAporteList.getPlan() && null != id_plan) {
				afiAporteList.setMapAportes(getTiposAportesPlan(con, cuil,
						inte, Integer.parseInt(id_plan)));
			} else if (null != afiAporteList.getPlan()) {
				// Traigo aportes del afiliado...
				afiAporteList.setMapAportes(traeAportesAfi(con, cuil, inte));
				// No es el mismo plan, busco los aportes correspondientes al
				// plan
				if (null != id_plan
						&& !id_plan.trim()
								.equals(String.valueOf(afiAporteList.getPlan()
										.getId()))) {
					Map<Integer, AfiAporte> aportesPlanNuevo = getTiposAportesPlan(
							con, cuil, inte, Integer.parseInt(id_plan));
					Map<Integer, AfiAporte> aportesPlanMerged = preparaAportesNuevoPlan(
							afiAporteList.getMapAportes(), aportesPlanNuevo);
					afiAporteList.setMapAportes(aportesPlanMerged);
				}
			}

		} finally {
			ConnectionHelper.cerrar(con);
		}
		return afiAporteList;
	}

	@SuppressWarnings("deprecation")
	public AfiAporteList buscaAportesPorPlan(String id_plan, String cuil,
			int inte, String fechaEgreso, String id_motivo_baja,
			boolean isPlusTres) throws Exception {
		Connection con = null;
		AfiAporteList afiAporteList = new AfiAporteList();
		try {
			con = ConnectionHelper.getConnection();
			// Plan actual del afiliado
			afiAporteList.setPlan(getPlanAfiliado(con, cuil, inte));

			// No tiene plan, traigo los aportes correspondientes al plan
			// solicitado
			if (null == afiAporteList.getPlan() && null != id_plan) {
				afiAporteList.setMapAportes(getTiposAportesPlan(con, cuil,
						inte, Integer.parseInt(id_plan)));
			} else if (null != afiAporteList.getPlan()) {
				// Traigo aportes del afiliado...
				afiAporteList.setMapAportes(traeAportesAfi(con, cuil, inte));
				// No es el mismo plan, busco los aportes correspondientes al
				// plan
				if (null != id_plan) {
					boolean flag = false;
					flag = id_plan.trim().equals(
							String.valueOf(afiAporteList.getPlan().getId()));
					if (!flag) {
						Map<Integer, AfiAporte> aportesPlanNuevo = getTiposAportesPlan(
								con, cuil, inte, Integer.parseInt(id_plan));
						Map<Integer, AfiAporte> aportesPlanMerged = preparaAportesNuevoPlan(
								afiAporteList.getMapAportes(), aportesPlanNuevo);
						afiAporteList.setMapAportes(aportesPlanMerged);
					}
				}
			}
			if (null != fechaEgreso && !fechaEgreso.equals("")) {
				Map<Integer, AfiAporte> aportesConMotBaja = afiAporteList
						.getMapAportes();
				for (AfiAporte aporte : aportesConMotBaja.values()) {
					int idAporte = aporte.getTipoAporte().getId_aporte();
					Date fEgre = aporte.getFecha_egre();
					MotivoBaja motivo_de_baja = new MotivoBaja(
							Integer.parseInt(id_motivo_baja), null);
					aporte.setMotivo_baja(motivo_de_baja);
					if (aporte.isEs_os() && isPlusTres) {
						if (null == fEgre) {
							Calendar aux = Calendar.getInstance();
							aux.setTime(DateUtils.parse(fechaEgreso,
									DateUtils.SHORT));
							aux.add(Calendar.MONTH, 3);
							fEgre = aux.getTime();
							aporte.setFecha_egre(fEgre);
						} else {
							fEgre = DateUtils.parse(fechaEgreso,
									DateUtils.SHORT);
							aporte.setFecha_egre(fEgre);
						}
					} else {
						fEgre = DateUtils.parse(fechaEgreso, DateUtils.SHORT);
						aporte.setFecha_egre(fEgre);
					}
				}
			}
		} finally {
			ConnectionHelper.cerrar(con);
		}
		return afiAporteList;
	}
		
	public List<AporteAfiliado> buscaAportesAfipAfiliado(String cuil,
			Date fecha_desde) throws Exception {
		List<AporteAfiliado> listaResultado = new ArrayList<AporteAfiliado>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call buscar_aportes_afip_meses(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setDate(2, new java.sql.Date(fecha_desde.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				AporteAfiliado bp = new AporteAfiliado(WebKeysGlobal.TIPO_BOLETA_OS, WebKeysGlobal.DENO_APORTE_OSPIM, rs.getString("cuil_titular"),
						rs.getString("apellido"), rs.getString("nombre"),
						rs.getDate("fecha_ingre"), rs.getDate("fecha_baja"),
						rs.getString("cuit"), rs.getString("razon_soc"),
						rs.getDate("periodo"), rs.getDouble("importe"),
						rs.getDate("fecha_transf"),
						rs.getBigDecimal("contrib_est"),
						rs.getBigDecimal("total_terc"),
						rs.getDate("fecha_terc"),
						rs.getBigDecimal("liq_actas"),
						rs.getBigDecimal("comisionOS"),
						rs.getDate("fechaTransf"),
						rs.getDate("fecha_recauda"),
						rs.getString("concepto"),
						rs.getBigDecimal("remuneracion"),
						rs.getString("id_terc")
						);
				//bp.setMostrar(true);
				listaResultado.add(bp);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaResultado;
	}
	
	public List<AporteAfiliado> buscaAportesEmpleadoresAfiliado(String cuil,
			Date fecha_desde) throws Exception {
		List<AporteAfiliado> listaResultado = new ArrayList<AporteAfiliado>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sqlList = "{call ver_aportes_afiliado(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setDate(2, new java.sql.Date(fecha_desde.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				AporteAfiliado bp = new AporteAfiliado(rs.getInt("tipo_aporte"),rs.getString("deno_tipo_aporte"),rs.getString("cuil_titular"),
						rs.getString("apellido"), rs.getString("nombre"),
						rs.getDate("fecha_ingre"), 
						rs.getString("cuit"), rs.getString("razon_soc"),
						rs.getDate("periodo"),
						rs.getBigDecimal("remuneracion"),
						rs.getBigDecimal("importe"),
						rs.getDate("fecha_transf"),						
						rs.getDate("fecha_recauda"));
				//bp.setMostrar(false);
				listaResultado.add(bp);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
			
		}

		return listaResultado;
	}

	public Plan getPlanAfiliado(Connection con, String cuilTitular, int inte,
			Date periodo) throws SQLException, AfiliadoSinPlanException {
		CallableStatement stmt = null;
		Plan plan = null;
		try {
			// Busco el plan del afiliado
			String sqlPlan = "{call trae_plan_afiliado_fecha(?,?,?)}";
			stmt = con.prepareCall(sqlPlan.toString());
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
			stmt.setDate(3, new java.sql.Date(periodo.getTime()));
			ResultSet rsPlan = stmt.executeQuery();

			while (rsPlan.next()) {
				plan = new Plan(rsPlan.getInt("id_plan"),
						rsPlan.getString("descripcion"));
			}
		} finally {
			if(con != null){
				ConnectionHelper.cerrar(stmt);
			}else{
				ConnectionHelper.cerrar(stmt, con);
			}	
		}
		if (plan == null) {
			throw new AfiliadoSinPlanException("El afiliado no tiene plan");
		}
		return plan;
	}
}
