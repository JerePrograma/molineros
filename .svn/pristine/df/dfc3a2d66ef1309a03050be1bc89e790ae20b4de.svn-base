package ar.com.ospim.tercerizadora.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;

public class Consolidar extends TercerizadoraService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 538861644513924582L;
	private static Log _log = LogFactoryUtil.getLog(Consolidar.class);
	
	private static Calendar fechaInicioSiEstabaEnTramite = null;
	private static Calendar fechaFinSiEstabaEnTramite = null;
	
	public static List<AfiTercerizadoraServicio> getTercerizadorasDependientePlanPeriodo(String cuilTitular, int idPlanNuevo, Date fechaHastaPlanActual, 
			Date fechaDesdeNuevoPlan, Date fechaHastaNuevoPlan, List<AfiTercerizadoraServicio> afiliadoTercerizadoras) {
		
		_log.info("Corriendo Tercerizadora Consolidar");
		
		fechaInicioSiEstabaEnTramite = Calendar.getInstance();
//		fechaInicioSiEstabaEnTramite.set(Calendar.YEAR, 2016);
//		fechaInicioSiEstabaEnTramite.set(Calendar.YEAR, 2017); // solicitado x Sandra 17/11/2017		
		fechaInicioSiEstabaEnTramite.set(Calendar.YEAR, 2018); // solicitado x Sandra 10/01/2019
//		fechaInicioSiEstabaEnTramite.set(Calendar.MONTH, Calendar.SEPTEMBER);
		fechaInicioSiEstabaEnTramite.set(Calendar.MONTH, Calendar.DECEMBER); // solicitado x Sandra 10/01/2019
		
		fechaInicioSiEstabaEnTramite.set(Calendar.DATE, 1);
		fechaInicioSiEstabaEnTramite.set(Calendar.HOUR_OF_DAY, 0);
		fechaInicioSiEstabaEnTramite.set(Calendar.MINUTE, 0);
		fechaInicioSiEstabaEnTramite.set(Calendar.SECOND, 0);
		fechaInicioSiEstabaEnTramite.set(Calendar.MILLISECOND, 0);
				
		ArrayList<TercerizadoraServicio> tercerizadorasPlanNuevo;
		AfiTercerizadoraServicio tercAfiVigente = null;
		AfiTercerizadoraServicio tercAfiNueva = null;
		AfiTercerizadoraServicio tercAfiContinuidad = null;
		TercerizadoraServicio tercPlanVigente = null;
		
		fechaFinSiEstabaEnTramite = Calendar.getInstance();
		fechaFinSiEstabaEnTramite.setTime(fechaInicioSiEstabaEnTramite.getTime());
		fechaFinSiEstabaEnTramite.add(Calendar.DATE, -1); // seteamos el día anterior que sería el 31/08/2016
		
		try {
			tercerizadorasPlanNuevo=(ArrayList<TercerizadoraServicio>) TercerizadoraServiceUtil.getInstance().getTercerizadoraPlan(idPlanNuevo);
			
			//traigo la ultimo xq estan ordenads x fecha fin y la vigente queda última.
			tercPlanVigente = tercerizadorasPlanNuevo.get(tercerizadorasPlanNuevo.size()-1); 
			
//			CASO 1: Primer plan del afiliado, seteamos la primer tercerizadora
			if(afiliadoTercerizadoras == null || afiliadoTercerizadoras.size() == 0){
				
//				Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
				if((tercPlanVigente.getFechaInicio()==null || 
						(tercPlanVigente.getFechaInicio() !=null &&	
					fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0)) &&
					(fechaDesdeNuevoPlan.compareTo(fechaInicioSiEstabaEnTramite.getTime()) >= 0)	
				){
					tercAfiVigente = new AfiTercerizadoraServicio();
					tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
					tercAfiVigente.setTercerizadora(tercPlanVigente);
					tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
					tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
					tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA); 
					
					// si el plan Omint si intenta insertar antes del fechaInicioSiEstabaEnTramite,
					// meter una tercerizadora En Trámite manualmente.
				}else if(fechaDesdeNuevoPlan.compareTo(fechaInicioSiEstabaEnTramite.getTime()) < 0){
						tercAfiContinuidad = new AfiTercerizadoraServicio();
						tercAfiContinuidad.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiContinuidad.setTercerizadora(new TercerizadoraServicio("ETR","EN TRAMITE"));
						tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiContinuidad.setFechaFinPres(fechaFinSiEstabaEnTramite.getTime());
						tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						
						tercAfiVigente = new AfiTercerizadoraServicio();
						tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiVigente.setTercerizadora(tercPlanVigente);
						tercAfiVigente.setFechaInicioPres(fechaInicioSiEstabaEnTramite.getTime());
						tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
						tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
				}
			}else{
//				suponer que la tercerizadora vigente del afiliado es la ultima esta bien? no lo se...
				tercAfiVigente = afiliadoTercerizadoras.get(afiliadoTercerizadoras.size()-1);
//				tercAfiVigente = TercerizadoraServiceUtil.buscarUltimaTercerizadoraDelAfiliado(null, cuilTitular);
				tercPlanVigente = tercAfiVigente!=null?tercAfiVigente.getTercerizadora():null;
				
//				CASO 2:cambia a otro plan verificar si es diferente tercerizadora: 
//				debemos chequear si esta la tercerizadora vigente en la lista de las tercerizadoras correspondiente al plan nuevo
				int pos = tercerizadorasPlanNuevo.indexOf(tercAfiVigente.getTercerizadora());
				if(pos == -1){ // no la encontro
					
					if(tercPlanVigente.getId_tercerizadora().equals("ETR")){
						
						if(fechaDesdeNuevoPlan.compareTo(fechaInicioSiEstabaEnTramite.getTime()) < 0){
							
							tercAfiVigente.setFechaFinPres(fechaFinSiEstabaEnTramite.getTime());
							
							tercAfiNueva = new AfiTercerizadoraServicio();
							tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
							tercAfiNueva.setTercerizadora(tercerizadorasPlanNuevo.get(0));
							tercAfiNueva.setFechaInicioPres(fechaInicioSiEstabaEnTramite.getTime());
							tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
							tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						}else{
							tercAfiVigente.setFechaFinPres(fechaHastaPlanActual);
							
							tercAfiNueva = new AfiTercerizadoraServicio();
							tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
							tercAfiNueva.setTercerizadora(tercerizadorasPlanNuevo.get(0));
							tercAfiNueva.setFechaInicioPres(fechaDesdeNuevoPlan);
							tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
							tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						}
					}
					
					if(!tercPlanVigente.getId_tercerizadora().equals("ETR") 
							&& !tercPlanVigente.getId_tercerizadora().equals("CSA")){
						
					
						if((tercAfiVigente.getTercerizadora().getFechaFin()!=null 
							&& tercAfiVigente.getTercerizadora().getFechaFin().compareTo(fechaHastaPlanActual) >= 0)
								|| tercAfiVigente.getTercerizadora().getFechaFin()==null	
								){
							tercAfiVigente.setFechaFinPres(fechaHastaPlanActual);
						}else{
							
							tercAfiVigente.setFechaFinPres(tercAfiVigente.getTercerizadora().getFechaFin());
						}
						tercAfiNueva = new AfiTercerizadoraServicio();
						tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiNueva.setTercerizadora(tercerizadorasPlanNuevo.get(0));
						tercAfiNueva.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					}
					
//					if((tercAfiVigente.getTercerizadora().getFechaFin()!=null 
//						&& tercAfiVigente.getTercerizadora().getFechaFin().compareTo(fechaHastaPlanActual) >= 0)
//							|| tercAfiVigente.getTercerizadora().getFechaFin()==null	
//							){
//						tercAfiVigente.setFechaFinPres(fechaHastaPlanActual);
//					}else{
//						
//						tercAfiVigente.setFechaFinPres(tercAfiVigente.getTercerizadora().getFechaFin());
//					}
//					
////					Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
//					if(tercPlanVigente.getFechaInicio()==null || 
//							(tercPlanVigente.getFechaInicio() !=null &&	
//						fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0 )){
//						tercAfiNueva = new AfiTercerizadoraServicio();
//						tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
//						tercAfiNueva.setTercerizadora(tercPlanVigente);
//						tercAfiNueva.setFechaInicioPres(fechaDesdeNuevoPlan);
//						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
//						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//					}else{ // existe continuidad de tercerizadoras para la vigencia desde del plan
//						tercAfiContinuidad = new AfiTercerizadoraServicio();
//						tercAfiContinuidad.setAfiliado(new Afiliado(cuilTitular, 0));
//						tercAfiContinuidad.setTercerizadora(tercerizadorasPlanNuevo.get(0));
//						tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
//						tercAfiContinuidad.setFechaFinPres(tercerizadorasPlanNuevo.get(0).getFechaFin());
//						tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//						
//						tercAfiNueva = new AfiTercerizadoraServicio();
//						tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
//						tercAfiNueva.setTercerizadora(tercPlanVigente);
//						tercAfiNueva.setFechaInicioPres(tercPlanVigente.getFechaInicio());
//						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
//						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
//					}
				} else{ // encontro la tercerizador vigente con la que necesita el plan
//					CASO 3 necesito prolongar la duracion de la tercerizadora,					
//						tercAfiVigente = new AfiTercerizadoraServicio();
//						tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
//						tercAfiVigente.setTercerizadora(tercPlanVigente);
//						tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
//						tercAfiVigente.setNuevo(true);
					
					
				}
			}
			
			if(afiliadoTercerizadoras == null || afiliadoTercerizadoras.size() == 0){
				afiliadoTercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
				afiliadoTercerizadoras.add(tercAfiVigente);
				
			}
			if(tercAfiContinuidad != null){
				afiliadoTercerizadoras.add(tercAfiContinuidad);
			}
			if(tercAfiNueva != null){
				afiliadoTercerizadoras.add(tercAfiNueva);
			}

		} catch (Exception e) {
			_log.error(e);
		}
		
		return afiliadoTercerizadoras;
	}

}
