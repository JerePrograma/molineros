package ar.com.ospim.liquidaciones.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;

import com.liferay.portal.model.User;

public class CuentaServiceUtil {

	private static CuentaServiceImpl instance;

	private static CuentaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CuentaServiceImpl();
		}
		return instance;
	}

	public static void update(PlanCuentas pCuenta, User user, int entidad) throws Exception {
		getInstance().update(pCuenta, user, entidad);

	}

	public static void guardar(PlanCuentas pCuenta, User user, int entidad) throws Exception {
		getInstance().guardar(pCuenta, user, entidad);
	}

	public static void eliminar(PlanCuentas pCuenta, Date desde, Date hasta, User user, int entidad)
			throws Exception {
		if (getInstance().estaUtilizado(pCuenta, desde, hasta, entidad)) {
			throw new ConceptoUtilizadoException();
		}
		if (entidad==WebKeysGlobal.AMTIMA) {
			getInstance().eliminarPlanCuenta(pCuenta, user, entidad);
		}else{
			getInstance().eliminar(pCuenta, desde, hasta, user, entidad);
		}
		
	}
	
	public static List<PlanCuentas> getPlanCuentas(int idCuenta, int entidad) {
		
		return getInstance().getPlanCuentas(idCuenta, entidad);
	}
	
    public static List<PlanCuentas> getPlanCuentas(int idCuenta,Date fecha, int entidad) {
		
		return getInstance().getPlanCuentas(idCuenta,fecha, entidad);
	}
	
	public static PlanCuentas getCuentaById(int idCuenta, int entidad) {
		return getInstance().getCuentaById(idCuenta, entidad);
	}
	
	public static PlanCuentas getCuentaById(int idCuenta,Date fecha, int entidad) {
		return getInstance().getCuentaById(idCuenta,fecha, entidad);
	}
	
    public static PlanCuentas getCuentaByNroCuenta(String idCuenta,Date desde,Date hasta, int entidad) {
		return getInstance().getCuentaByNroCuenta(idCuenta,desde,hasta, entidad);
	}
	
}