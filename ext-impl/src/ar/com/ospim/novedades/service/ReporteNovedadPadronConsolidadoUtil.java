package ar.com.ospim.novedades.service;

import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;

import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoAltas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoBajas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoInconsistencia;

public class ReporteNovedadPadronConsolidadoUtil {


	private static ReporteNovedadPadronConsolidadoServiceImpl instance = null;

	public static ReporteNovedadPadronConsolidadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReporteNovedadPadronConsolidadoServiceImpl();
		}
		return instance;
	}
	
	public static List<NovedadPadronConsolidadoBajas> getNovedadPadronConsolidadoBajas(Date fechaDesde) throws SystemException{
	
		return getInstance().getNovedadPadronConsolidadoBajas(fechaDesde) ;
	}
	

	public static List<NovedadPadronConsolidadoAltas> getNovedadPadronConsolidadoAltas(Date fechaDesde) throws SystemException{
		
		return getInstance().getNovedadPadronConsolidadoAltas(fechaDesde) ;
	}
	
	public static List<NovedadPadronConsolidadoInconsistencia> getNovedadPadronConsolidadoInconsistentes(Date fechaDesde) throws SystemException{
		
		return getInstance().getNovedadPadronConsolidadoInconsistentes(fechaDesde) ;
	}
	
	
	

		

}
