package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

public class AvisoCobranzaAcuerdosPagoConvenio extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactoryUtil.getLog(AvisoCobranzaAcuerdosPagoConvenio.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo CobranzaAcuerdosPago");
		int diasAvisoVenciConvenio = Integer.valueOf(TraeListasServiceUtil.getSystemConfig("SEGUIMIENTO_EMPRESAS_AVISO_DIAS_AL_VENCIMIENTO_PAGO_CONVENIO"));
		
		List<ConvenioPagosReporte> todosConvenioPagosReporte = new ArrayList<ConvenioPagosReporte>();
		List<ConvenioPagosReporte> convenioNoOSReporte;
		List<ConvenioPagosReporte> convenioServiceReporte;

		convenioNoOSReporte = ConvenioNoOSServiceUtil.getPagosConveniosAvisoVencimiento(diasAvisoVenciConvenio);
		convenioServiceReporte = ConvenioServiceUtil.getConveniosAvisoVencimiento(diasAvisoVenciConvenio);
		
		logger.debug("convenioNoOSReporte " + convenioNoOSReporte.size());
		logger.debug("convenioServiceReporte " + convenioServiceReporte.size());

		
		
		todosConvenioPagosReporte.addAll(convenioNoOSReporte);
		todosConvenioPagosReporte.addAll(convenioServiceReporte);
		
		logger.debug("todo " + todosConvenioPagosReporte.size());
		
		
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		
		try {
		   rac = ReportesServiceUtil.getConfiguracion();
	       List<String> email = new ArrayList<String>();

		
		   if(todosConvenioPagosReporte.size()>0) {
		      for(ConvenioPagosReporte p:todosConvenioPagosReporte){
		    	  
		    	  BigDecimal total = new BigDecimal(0);
		    	  total = total.add(p.getImporte());
		    	  total =  total.add(p.getInteres());
		    	  
			      String body ="Estimado le informamos que conforme a nuestros registros se encuentra próximo a vencer la cuota " + p.getIdCuota() +   " del convenio " + p.getNumeroConvenio() +  "\n\n";
			      body = body + "Con fecha de vencimiento:  "  +  p.getFechaPagoAsString()  + "  y un importe de: $ " +  total  +  "\n\n";
			      body = body + "Recuerde enviar el comprobante de transferencia vía mail para poder imputar correctamente el pago.  "  +  "\n\n\n"; 
			      		      
			      body = body +  "Por favor ante cualquier consulta contáctese a OSPIM al telefono 0810-345-0208 o al 5238-3900 de lunes a viernes de 9 a 18 hs o al mail  rdecandido@ospim.org.ar" + "\n\n";
			       
			      email = new ArrayList<String>();	    	  
		    	  email.add(p.getContactoEmail());
		    	  if (!StringUtils.checkEmpty(p.getContactoEmail())){
					   EnviaEmailsThread.enviarMailDesatendido("No Responder - Aviso de Vencimiento de convenio " +  p.getNumeroConvenio()  , body ,  email, 0);
				  }
		      }

		   }
		   
		  //Enviamos reporte al Estudio con los aviso 
		   
		   HSSFWorkbook wb = new HSSFWorkbook();	
		   try {
		      wb= ReporteCobranzaAcuerdosPagoConvenio.generaReporte(todosConvenioPagosReporte);
		   } catch (Exception e) {
			  logger.debug("Error al generar subida padron AcuerdosPagoConvenio");
		   }
			 
		  enviarReporteEstudio(wb);
			   
		  ra.setUltimaEjecucion(new Date());
		  ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de CobranzaAcuerdosPago");
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

	

	private static void enviarReporteEstudio( HSSFWorkbook wb) {
		
		// obtener configuracion del reporte_automatico
//		ReportesAutomaticosConfiguracion rac = null;
//      	try {
//	         rac = ReportesServiceUtil.getConfiguracion();
//		} catch (SystemException e) {
//			e.printStackTrace();
//		}
		
		List<String> emails;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_SEGUIMIENTO_EMPRESAS_PAGO_CONVENIO");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
		
//		MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
//				emails, "listado de convenio próximos a vencer " ,
//				"listado de convenio impagos " , wb, "Reporte Estudio convenios próximos a vencer.xls");
		
		EnviaEmailsThread.enviarMailDesatendido("Listado de convenio próximos a vencer ", "Listado de convenio impagos ", emails, wb, "Reporte Estudio convenios próximos a vencer.xls");
		
	}

    
    
}
