package ar.com.ospim.liquidaciones.services;

import java.util.List;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.PlanPrestacion;

public class PlanPrestacionServiceUtil {
	
	public static final int PLAN_ID_DUMMY = 1;
	
	private static PlanPrestacionServiceImpl instance = null;

	public static PlanPrestacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new PlanPrestacionServiceImpl();
		}
		return instance;
	}

	public static List<PlanPrestacion> traePlanPrestaciones(int prestacionId,
			String prestacion, int planId) {
		return getInstance().traePlanPrestaciones(prestacionId, prestacion, planId);
	}
	
	public static List<PlanPrestacion> traePlanPrestaciones(String codigo,
			String prestacion, int planId) {
		return getInstance().traePlanPrestaciones(codigo, prestacion, planId);
	}
	
	public static List<PlanPrestacion> traePlanPrestaciones(String codigo,
			String prestacion, int planId, String protesis) {
		return getInstance().traePlanPrestaciones(codigo, prestacion, planId, protesis);
	}

	public static List<Prestacion> traeTipoNomencladorPrestaciones(int idTipoNomenclador, String codigoPrestacion, String descripcionPrestacion){
		return getInstance().traeTipoNomencladorPrestaciones(idTipoNomenclador, codigoPrestacion, descripcionPrestacion);
	}
}
