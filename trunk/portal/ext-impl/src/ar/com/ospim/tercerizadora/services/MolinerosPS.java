package ar.com.ospim.tercerizadora.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;

public class MolinerosPS extends TercerizadoraService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5118074872779197583L;
	private static Log _log = LogFactoryUtil.getLog(MolinerosPS.class);
	
	public static List<AfiTercerizadoraServicio> getTercerizadorasDependientePlanPeriodo(String cuilTitular, int idPlanNuevo, Date fechaHastaPlanActual, 
			Date fechaDesdeNuevoPlan, Date fechaHastaNuevoPlan, List<AfiTercerizadoraServicio> afiliadoTercerizadoras) {
		
		_log.info("Corriendo Tercerizadora MolinerosPS");
		
		ArrayList<TercerizadoraServicio> tercerizadorasPlanNuevo;
		AfiTercerizadoraServicio tercAfiVigente = null;
		AfiTercerizadoraServicio tercAfiNueva = null;
		AfiTercerizadoraServicio tercAfiContinuidad = null;
		TercerizadoraServicio tercPlanVigente = null;
		
		try {
			tercerizadorasPlanNuevo=(ArrayList<TercerizadoraServicio>) TercerizadoraServiceUtil.getInstance().getTercerizadoraPlan(idPlanNuevo);
			
			//traigo la ultimo xq estan ordenads x fecha fin y la vigente queda última.
			tercPlanVigente = tercerizadorasPlanNuevo.get(tercerizadorasPlanNuevo.size()-1);  
			
//			CASO 1: Primer plan del afiliado, seteamos la primer tercerizadora
			if(afiliadoTercerizadoras == null || afiliadoTercerizadoras.size() == 0){
				
//				Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
				if(tercPlanVigente.getFechaInicio()==null || 
						(tercPlanVigente.getFechaInicio() !=null &&	
					fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0 )){
					tercAfiVigente = new AfiTercerizadoraServicio();
					tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
					tercAfiVigente.setTercerizadora(tercPlanVigente);
					tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
					tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
					tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA); 
				}else{ // existe continuidad de tercerizadoras para la vigencia desde del plan
					tercAfiContinuidad = new AfiTercerizadoraServicio();
					tercAfiContinuidad.setAfiliado(new Afiliado(cuilTitular, 0));
					tercAfiContinuidad.setTercerizadora(tercerizadorasPlanNuevo.get(0));
					tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
					tercAfiContinuidad.setFechaFinPres(tercerizadorasPlanNuevo.get(0).getFechaFin());
					tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					
					tercAfiVigente = new AfiTercerizadoraServicio();
					tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
					tercAfiVigente.setTercerizadora(tercPlanVigente);
					tercAfiVigente.setFechaInicioPres(tercPlanVigente.getFechaInicio());
					tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
					tercAfiVigente.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
				}
			}else{
//				deshacemos cambios nuevos, porque puede ser que se arrepientan de bajas cascadas o seleccionan otro plan nuevo, etc...
//				for (int j=0; j<afiliadoTercerizadoras.size();j++){
//					
//					AfiTercerizadoraServicio afiTercServ = afiliadoTercerizadoras.get(j); 
//							
//					if(afiTercServ.isNuevo()){
//						afiliadoTercerizadoras.remove(afiTercServ);
//					}
//				}
//				suponer que la tercerizadora vigente del afiliado es la ultima esta bien? no lo se...
				tercAfiVigente = afiliadoTercerizadoras.get(afiliadoTercerizadoras.size()-1);
				
//				CASO 2:cambia a otro plan verificar si es diferente tercerizadora: 
//				debemos chequear si esta la tercerizadora vigente en la lista de las tercerizadoras correspondiente al plan nuevo
				int pos = tercerizadorasPlanNuevo.indexOf(tercAfiVigente.getTercerizadora());
				if(pos == -1){ // no la encontro
					if((tercAfiVigente.getTercerizadora().getFechaFin()!=null 
						&& tercAfiVigente.getTercerizadora().getFechaFin().compareTo(fechaHastaPlanActual) >= 0)
							|| tercAfiVigente.getTercerizadora().getFechaFin()==null	
							){
						tercAfiVigente.setFechaFinPres(fechaHastaPlanActual);
					}else{
						
						tercAfiVigente.setFechaFinPres(tercAfiVigente.getTercerizadora().getFechaFin());
					}
					
//					Si la fecha del plan es mayor o igual al dia que empieza la tercerizadora
					if(tercPlanVigente.getFechaInicio()==null || 
							(tercPlanVigente.getFechaInicio() !=null &&	
						fechaDesdeNuevoPlan.compareTo(tercPlanVigente.getFechaInicio()) >= 0 )){
						tercAfiNueva = new AfiTercerizadoraServicio();
						tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiNueva.setTercerizadora(tercPlanVigente);
						tercAfiNueva.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					}else{ // existe continuidad de tercerizadoras para la vigencia desde del plan
						tercAfiContinuidad = new AfiTercerizadoraServicio();
						tercAfiContinuidad.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiContinuidad.setTercerizadora(tercerizadorasPlanNuevo.get(0));
						tercAfiContinuidad.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiContinuidad.setFechaFinPres(tercerizadorasPlanNuevo.get(0).getFechaFin());
						tercAfiContinuidad.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
						
						tercAfiNueva = new AfiTercerizadoraServicio();
						tercAfiNueva.setAfiliado(new Afiliado(cuilTitular, 0));
						tercAfiNueva.setTercerizadora(tercPlanVigente);
						tercAfiNueva.setFechaInicioPres(tercPlanVigente.getFechaInicio());
						tercAfiNueva.setFechaFinPres(fechaHastaNuevoPlan);
						tercAfiNueva.setEstado(AfiTercerizadoraServicio.ESTADOS.ALTA);
					}
				} else{ // encontro la tercerizador vigente con la que necesita el plan
//					CASO 3 necesito prolongar la duracion de la tercerizadora,					
//						tercAfiVigente = new AfiTercerizadoraServicio();
//						tercAfiVigente.setAfiliado(new Afiliado(cuilTitular, 0));
//						tercAfiVigente.setTercerizadora(tercPlanVigente);
//						tercAfiVigente.setFechaInicioPres(fechaDesdeNuevoPlan);
						tercAfiVigente.setFechaFinPres(fechaHastaNuevoPlan);
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
