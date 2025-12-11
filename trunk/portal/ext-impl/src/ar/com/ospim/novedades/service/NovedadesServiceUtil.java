package ar.com.ospim.novedades.service;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.novedades.beans.AfiliadoCambioCuil;
import ar.com.ospim.novedades.beans.ArchivoNovedad;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.beans.NovedadEmpleadorTotal;
import ar.com.ospim.novedades.beans.NovedadTotal;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class NovedadesServiceUtil {

	private static Log logger = LogFactoryUtil.getLog(NovedadesServiceUtil.class);

	private static NovedadesServiceImpl instance = null;

	public static NovedadesServiceImpl getInstance() {
		if (null == instance) {
			instance = new NovedadesServiceImpl();
		}
		return instance;
	}
	
	public List<ArchivoNovedad> getArchivosNovedades(Date fechaDesde) throws SystemException{
	
		return getInstance().getArchivosNovedades(fechaDesde) ;
	}
	
	public List<NovedadTotal> getNovedades(String cuil_titular, String cuil, String tdoc, String nrodoc, 
			String apellido, String nombre, String tipoNov, String tipoOri, Date fechaProc, int pagina_sel) throws SystemException{
		
		return getInstance().getNovedades(cuil_titular, cuil, tdoc, nrodoc, apellido, nombre, tipoNov, tipoOri, fechaProc, pagina_sel) ;
		
	}
	
	public List<NovedadEmpleadorTotal> getNovedadesEmpleadores(Date fechaHasta, String tipoNovedadEmpl,  int pagina_sel) throws SystemException{
		
		return getInstance().getNovedadesEmpleadores(fechaHasta, tipoNovedadEmpl, pagina_sel);
		
	}	
	
	public Novedad getNovedadById(int idNovedad) throws SystemException{
		
		return getInstance().getNovedadById(idNovedad) ;
	}
	
	public boolean cambiaCuil(AfiliadoCambioCuil cambioCuil, String user) throws SystemException{
		
		return getInstance().cambiaCuil(cambioCuil, user); 
	}
	
	public List<ReporteNovedadesSSSProcesadas> getReportesNovedadesSSSProcesadas() throws SystemException{
		
		return getInstance().getReportesNovedadesSSSProcesadas();
		
	}	
	
	public List<ReporteNovedadesSSSProcesadas> getEstadisticaNovedadesSSSProcesadas() throws SystemException{
		
		return getInstance().getEstadisticaNovedadesSSSProcesadas();
		
	}	
}
