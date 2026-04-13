package ar.com.ospim.tercerizadora.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;

/**
 * Es una implementación del patrón Factory,
 * asi independizamos el comportamiento y la relación entre las tercerizadoras-planes
 * 
 * @author Sergio
 *
 */
public class TercerizadoraFactory {

	public static List<AfiTercerizadoraServicio> getTercerizadorasDependientePlanPeriodo(String cuilTitular, int idPlanNuevo, Date fechaHastaPlanActual, 
			Date fechaDesdeNuevoPlan, Date fechaHastaNuevoPlan, List<AfiTercerizadoraServicio> afiliadoTercerizadoras){
		
		List<AfiTercerizadoraServicio> tercerizadoras =  null;
		/*
32;"OMINT USUFRUCTO"
23;"OMINT SALUD"
28;"OMINT - SINDICATO"
30;"OMINT MONOTRIBUTO"
35;"OMINT SERV.DOMESTICO"

10;"CONSOLIDAR SALUD"
11;"CONSOLIDAR SALUD - SINDICATO"
27;"DESCONOCIDO"
29;"CONSOLIDAR MONOTRIBUTO"
31;"CONSOLIDAR SALUD USUFRUCTO"
36;"CONSOLIDAR SERV.DOMESTICO"

		 * 26;"EN TRAMITE"
		 */
		if(idPlanNuevo == 23 || idPlanNuevo == 28 || idPlanNuevo == 30 || idPlanNuevo == 32 || idPlanNuevo == 35){

			tercerizadoras = Omint.getTercerizadorasDependientePlanPeriodo(cuilTitular, 
					idPlanNuevo, fechaHastaPlanActual, fechaDesdeNuevoPlan, fechaHastaNuevoPlan, afiliadoTercerizadoras);
			
		}else if(idPlanNuevo == 10 || idPlanNuevo == 11 || idPlanNuevo == 27 || idPlanNuevo == 29 || idPlanNuevo == 31 || idPlanNuevo == 36){
			
			tercerizadoras = Consolidar.getTercerizadorasDependientePlanPeriodo(cuilTitular, 
					idPlanNuevo, fechaHastaPlanActual, fechaDesdeNuevoPlan, fechaHastaNuevoPlan, afiliadoTercerizadoras);
			
		}else{
//			tercerizadoras = MolinerosPS.getTercerizadorasDependientePlanPeriodo(cuilTitular, 
//					idPlanNuevo, fechaHastaPlanActual, fechaDesdeNuevoPlan, fechaHastaNuevoPlan, afiliadoTercerizadoras);
			tercerizadoras = MolinerosEnSalud.getTercerizadorasDependientePlanPeriodo(cuilTitular, 
					idPlanNuevo, fechaHastaPlanActual, fechaDesdeNuevoPlan, fechaHastaNuevoPlan, afiliadoTercerizadoras);
		}
		
		
		return tercerizadoras;
	}
}
