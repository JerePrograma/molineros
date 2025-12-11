package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


public class PlanServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(PlanServiceUtil.class);
	
	private static PlanServiceImpl instance = null;

	public static PlanServiceImpl getInstance() {
		if (null == instance) {
			instance = new PlanServiceImpl();
		}
		return instance;
	}

	public List<Plan> buscaTodosPlanes() throws Exception {
		return getInstance().buscaTodosPlanes();
	}
	
	public Plan buscaPlanPorId(int idPlan) throws Exception {
		return getInstance().buscaPlanPorId(idPlan);
	}
	
//	public AfiPlan buscarUltimoPlan(String cuilTitular) throws Exception {
//		return getInstance().buscarUltimoPlan(cuilTitular);
//	}

	public AfiPlan buscarUltimoPlanAportes(String cuilTitular) throws Exception {
		return getInstance().buscarUltimoPlanAportes(cuilTitular);
	}

	public AfiPlan buscarPenultimoPlanAportes(String cuilTitular) throws Exception {
		return getInstance().buscarPenultimoPlanAportes(cuilTitular);
	}
	
	public List<TipoAporte> buscaAportesPorPlan(int idPlan) throws Exception {
		return getInstance().buscaAportesPorPlan(idPlan);
	}
	
	/**
	 * Inserta nuevo plan con sus respectivos aportes.
	 * 
	 * caso 1: Si inserta nuevo plan y es alta afiliado titular, todos los aportes que generan id de socio, deben generar los nuevos ids.
	 * @param afiPlanNuevo
	 * @param screenName
	 * @return
	 * @throws Exception
	 */
	public boolean insertaPlanyAportes(Connection con,  AfiPlan afiPlanNuevo, String screenName) throws Exception {
		
		Map<Integer,Boolean> aporteGeneraIdsSocio = new java.util.HashMap<Integer,Boolean>();
		
		for (Iterator<TipoAporte> iterator = afiPlanNuevo.getPlan().getAportes().iterator(); iterator.hasNext();) {
			TipoAporte ta = iterator.next();
			
			aporteGeneraIdsSocio.put(ta.getId_aporte(), ta.getGenera_id_socio()!=null?true:false);
		}		
		
		return getInstance().insertaPlanyAportes(con, afiPlanNuevo, aporteGeneraIdsSocio, screenName);
	}
	
	/**
	 * Da de baja plan y aportes actuales 
	 * e Inserta nuevo plan con sus respectivos aportes,
	 * verifica continuidad del plan y id_socio de los aportes.
	 * 
	 * caso 2: Si inserta nuevo plan, porque esta cambiando plan, se debe analizar si el aporte ya existia desde el plan anterior, 
	 * porque en este caso se mantiene el id socio ya generado, pero si el aporte no esta en el plan anterior debe generar un nuevo id socio. 
	 * 
	 * caso 2.1: Baja de plan actual, por baja cascada
	 * 
	 * @param afiPlanActual
	 * @param afiPlanNuevo
	 * @param screenName
	 * @return
	 * @throws Exception
	 */
	public boolean cambioDePlanyAportes(Connection con, AfiPlan afiPlanActual, AfiPlan afiPlanNuevo, boolean esBajaCascada, String screenName) throws Exception {
		
		boolean result = false;
		
		Calendar fechaInicial = Calendar.getInstance(), fechaFinal = Calendar.getInstance();
		Map<Integer,Boolean> aporteGeneraIdsSocio = new java.util.HashMap<Integer,Boolean>();
		
//		caso 2: hay que cambiar el plan? Si y solo si afiPlanActual trae motivo de baja, y ademas afiPlanNuevo no es null.
//		caso 2.1 Si afiPlanNuevo es null y no hay motivo baja en AfiPlanActual, no hacemos nada, no se ha cambiado nada para el plan, retornamos True
//		caso 2.2 Si afiPlanNuevo es null y hay motivo baja en AfiPlanActual, y esBaja cascada, solo actualizamos baja del plan actual
				
		if(afiPlanNuevo == null && afiPlanActual != null && afiPlanActual.getMotivoBaja().getId_motivo_baja() == 0){
			return true;
		}
//		preparo todos los aportes que le correspondan generar id_socio
		if(afiPlanNuevo != null && afiPlanNuevo.getPlan().getAportes() != null){
			for (Iterator<TipoAporte> iterator = afiPlanNuevo.getPlan().getAportes().iterator(); iterator.hasNext();) {
				TipoAporte ta = iterator.next();
				aporteGeneraIdsSocio.put(ta.getId_aporte(), ta.getGenera_id_socio()!=null?true:false);
			}		
		}
		
		if(afiPlanNuevo != null){ // debería hacer el cambio de plan... (con baja x cambio de plan o baja cascada con plan segun regla 1)
			
//			si las fechas de vigen hasta del plan actual, es igual a la fecha vigen desde + 1 dia del plan nuevo, 
//			entonces tenemos continuidad y debemos mantener algun id_socio previamente generado. Sino generamos ids_socio nuevos si corresponde.
			fechaInicial.setTime(afiPlanActual.getVigenHasta());
			fechaFinal.setTime(afiPlanNuevo.getVigenDesde());
			int idAporteAux=0;
			
			if(DateUtils.diferenciaDias(fechaInicial, fechaFinal) == 1){
				for (Iterator<AfiAportes> iterator = afiPlanActual.getAportes().iterator(); iterator.hasNext();) {
					AfiAportes aa = iterator.next();
					if(aa.getAporte().getGenera_id_socio()!=null){
						idAporteAux = aa.getAporte().getId_aporte();
						if(aporteGeneraIdsSocio.containsKey(idAporteAux)){
							aporteGeneraIdsSocio.remove(idAporteAux);
							aporteGeneraIdsSocio.put(idAporteAux, false);
						}
					}
				}
			}
	//		actualizamos la baja del plan que actualmente tenian... (afi_plan y afi_aportes)
			result = getInstance().actualizaPlanyAportes(con, afiPlanActual, aporteGeneraIdsSocio, screenName);
	//		insertamos plan nuevo... (afi_plan y afi_aportes)		
			result = result && getInstance().insertaPlanyAportes(con, afiPlanNuevo, aporteGeneraIdsSocio, screenName);
			
		}else if(esBajaCascada){ // x baja casacada y sin/con cobertura tras la baja, actualizamos el plan Actual y si corresponde el plan cobertura
			
			
//DS - Agregado para baja anterior al plan actual 2022-11-02			
			if(DateUtils.compararFechasTruncarEnDia(afiPlanActual.getVigenDesde(),afiPlanActual.getVigenHasta())>0){
				List<AfiPlan> lista = null;
				lista = PlanServiceUtil.getInstance().historicoPlanyAportes(afiPlanActual.getCuil_titular());
				
				AfiPlan apn = null;
				for(AfiPlan ap:lista) {
					
					if( (DateUtils.compararFechasTruncarEnDia(afiPlanActual.getVigenHasta(),ap.getVigenDesde())>0  &&
						ap.getVigenHasta()==null) ||
						DateUtils.compararFechasTruncarEnDia(afiPlanActual.getVigenHasta(),ap.getVigenDesde())>0  &&
						DateUtils.compararFechasTruncarEnDia(afiPlanActual.getVigenHasta(),ap.getVigenHasta())<=0) {
						apn=ap;
					}
				}
				
				if(apn!=null) {
					apn.setMotivoBaja(afiPlanActual.getMotivoBaja());
					apn.setVigenHasta(afiPlanActual.getVigenHasta()); 
					//apn.setEstado(AfiPlan.ESTADOS.MODIFICADO);
					//apn.setVigenDesde(ap.getVigenDesde());
					//apn.setPlan(ap.getPlan());
					//apn.setId_plan_omint(ap.getId_plan_omint());
					
					result = getInstance().actualizaPlanyAportesConBaja(con, apn, null, screenName);
				}
			}else {
				result = getInstance().actualizaPlanyAportes(con, afiPlanActual, null, screenName);
			}
// Fin Agregado			
			
//			result = getInstance().actualizaPlanyAportes(con, afiPlanActual, null, screenName);
			
		}
		return result; 
	}
	
	public List<AfiAportes> buscaUltimosIdsSocio(String cuil_titular)throws Exception {
	
		return getInstance().buscaUltimosIdsSocio(cuil_titular);
	
	}
	
	public List<AfiAportes> consultaUltimosComponentesPlanVigente(String cuil_titular)throws Exception {
		
		return getInstance().consultaUltimosComponentesPlanVigente(cuil_titular);
	
	}
	
	public void borrarPlanDelAfiliado(Connection con, double idPlanSerial, String username)throws Exception {
		
		getInstance().eliminarPlan(con, idPlanSerial, username);
		
	}	
	
	public List<AfiPlan> historicoPlanyAportes(String cuilTitular) throws Exception {
		
		return getInstance().historicoPlanyAportes(cuilTitular);
		
	}
	
	public AfiliacionPrevencionDTO buscarAfiliacionPrevencion(String cuilTitular, int inte) throws Exception {
		
		return getInstance().buscarAfiliacionPrevencion(cuilTitular, inte);
		
	}
	
	public static List<AfiPlan> traeHistoricoPlanes(String cuilTitular) throws Exception {
		
		return getInstance().traeHistoricoPlanes(cuilTitular);
		
	}
	/**
	 * 
	 * @param planes
	 * @return mensaje, si es vacio no se solapan, si hay solapamiento, el mensaje sirve para indicar cuales Planes se solapan...
	 * 
	 */
	public static String seSolapanVigencias(List<AfiPlan> planes) {
		
		String mensaje = null;

		for (int i = 0; i < planes.size(); i++) {
			AfiPlan afit1 = planes.get(i);
			for (int j = 0; j < planes.size(); j++) {
				AfiPlan afit2 = planes.get(j);
				if (!afit2.equals(afit1) && afit1.getBajaFecha()==null && afit2.getBajaFecha()==null) {
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getVigenDesde(), afit2.getVigenDesde()) >= 0) {
						if (null == afit2.getVigenHasta()) {
							mensaje = "Se solapan los planes: " +afit1.getPlan().getDescripcion() + " y " + afit2.getPlan().getDescripcion();
							break;
						}else if(null==afit1.getVigenHasta() && DateUtils.compararFechasTruncarEnDia(
								afit2.getVigenHasta(), afit1.getVigenDesde())>=0){
							mensaje = null;
							break;
						}
					}
					if (DateUtils.compararFechasTruncarEnDia(
							afit1.getVigenDesde(), afit2.getVigenDesde()) <= 0) {
						if (null == afit1.getVigenHasta()) {
							mensaje = "Se solapan los planes: " +afit1.getPlan().getDescripcion() + " y " + afit2.getPlan().getDescripcion();
							break;
						} else if (afit2.getVigenHasta()==null && DateUtils.compararFechasTruncarEnDia(
								afit1.getVigenHasta(), afit2.getVigenDesde())>=0 ){
							mensaje = "Se solapan los planes: " +afit1.getPlan().getDescripcion() + " y " + afit2.getPlan().getDescripcion();
							break;
						}
					}
				}
			}
			if(mensaje !=null){
				break;
			}

		}
		return mensaje;
	}
	
	public void borrarAfiPlan(Connection con, int id, String screenName) throws SystemException {
		
		getInstance().borrarAfiPlan(con, id, screenName);
	}
	
	public BigDecimal insertaAfiPlan(Connection con,  AfiPlan ap, String screenName) throws Exception {
		
		return getInstance().insertaAfiPlan(con, ap, screenName);
		
	}
	
	public int actualizarAfiPlan(Connection con,  AfiPlan ap, String screenName) throws Exception {
		
		return getInstance().actualizaAfiPlan(con, ap, screenName);
		
	}
	
	public static void ajustaIDsAfiPlan(Connection con, String cuilTitular) throws SystemException {
		
		getInstance().ajustaIDsAfiPlan(con, cuilTitular);
		
	}
	
	public static boolean restablecerCobMedicaBeneficiario(String cuilTitular, int inte, Date fechaInicio, String screenName) throws SystemException {
		
		return getInstance().restablecerCobMedicaBeneficiario(cuilTitular, inte, fechaInicio, screenName);
		
	}
	
	public static boolean suspenderCobMedicaBeneficiario(String cuilTitular, int inte, Date fechaFin, String screenName) throws SystemException {
		
		return getInstance().suspenderCobMedicaBeneficiario(cuilTitular, inte, fechaFin, screenName);
		
	}
	
	public static List<AfiSuspencionCobertura> getSuspencionesCobMedicaBeneficiario(String cuilTitular, int inte) throws SystemException {
		
		return getInstance().getSuspencionesCobMedicaBeneficiario(cuilTitular, inte);
		
	}
	
	
}
