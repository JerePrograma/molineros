package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class AvisoVencimientoLoteSeguimiento extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = 1370414037305809229L;
	private static Log logger = LogFactoryUtil.getLog(AvisoVencimientoLoteSeguimiento.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo AvisoVencimientoLoteSeguimiento");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		List<String>emails = new ArrayList<String>();
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			
			 Calendar c = Calendar.getInstance();
			 int   nD=c.get(Calendar.DAY_OF_WEEK);
			 
			 if(nD==2){
			   int diasAlVencimiento = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("SEGUIMIENTO_EMPRESAS_DIAS_AL_VENCIMIENTO"));
			   int diasAntesAviso = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("SEGUIMIENTO_EMPRESAS_DIAS_ANTES_AVISO"));
			   String eMailsString = TraeListasServiceUtil.getSystemConfig("SEGUIMIENTO_EMPRESAS_MAILS_AVISO");
			   String[] vMails=null;
			   if(eMailsString!=null && !"".equalsIgnoreCase(eMailsString)) {
				 vMails=eMailsString.split(";");
				 for(int x=0;x<vMails.length;x++) {
					emails.add(vMails[x]);
				 }
			   }
			
			   List<TipoLoteEmpresa> ccs = LlamadoServiceUtil.avisoVencimientoLotesSeguimientoEmpresas(diasAlVencimiento, diasAntesAviso);
			
			   if(ccs.size()>0) {
			      String body ="Se recuerda el vencimiento de los siguientes lotes:\n\n";
			      for(TipoLoteEmpresa p:ccs){
				     body += "   " + p.getTipoLote() +" "+p.getLote()+" Vto: "+p.getDescripcionLote() +" \n";
			      }
			      EnviaEmailsThread.enviarMailDesatendido("Aviso de Vencimiento de Lotes", body ,  emails, 0); 
			   }
			}   
			   
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Aviso de Vencimientos de Lote");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

	
    
    
    
}
