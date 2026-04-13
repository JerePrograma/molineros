package ar.com.ospim.automatico.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.global.beans.Destinatario;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceImpl;
import ar.com.ospim.crm.beans.ContactoCRM;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SchedulerServiceUtil {
	
	private static Log logger = LogFactoryUtil.getLog(SchedulerServiceUtil.class);

	private static SchedulerServiceImpl instance = null;

	public static SchedulerServiceImpl getInstance() {
		if (null == instance) {
			instance = new SchedulerServiceImpl();
		}
		return instance;
	}
	
	public static void addParameters(String codigo, Integer idJob, List<String>parameters ) throws SystemException{
		String str="";
		for(String s:parameters){
		  str += s +";"; 	
		}
		getInstance().addParameters(codigo, idJob, str);
	}
	
	
	public static List<String> getParameters(String codigo) throws SystemException {
		return getInstance().getParameters(codigo);
	}
	
	public static void run(Integer idJobs) throws SystemException{
		getInstance().run(idJobs);
	}
	
	public static List<String> status(Integer idJobs) throws SystemException{
		return getInstance().status(idJobs);
	}
	
}
