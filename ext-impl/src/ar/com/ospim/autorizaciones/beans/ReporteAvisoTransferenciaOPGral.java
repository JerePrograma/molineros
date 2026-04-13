package ar.com.ospim.autorizaciones.beans;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.farmacia.ordenespago.reportes.ReporteOPReintegrosFarmaciaPresta;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.FormaPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.util.StringUtils;

public class ReporteAvisoTransferenciaOPGral extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = -5751180873715418314L;

	private static int delayConfig =  Integer.parseInt(TraeListasServiceUtil.getSystemConfig("EMAIL_DELAY"));
	
	private static Log logger = LogFactoryUtil.getLog(ReporteAvisoTransferenciaOPGral.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo ReporteAvisoTransferenciaOPGral");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		ScheduledExecutorService scheduler  = Executors.newSingleThreadScheduledExecutor();
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			
			List<OrdenPagoOspim> aviso = IntegracionServiceUtil.getOrdenesPagoGRALSinAvisoTransferencia();
			generaAvisoTransferencia(aviso,scheduler);
			generaMarcasEmailEnviado(aviso);
					
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Avisos de Transferencias ReporteAvisoTransferenciaOPGral");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		scheduler.shutdown();
	}

	
	private void generaAvisoTransferencia(List<OrdenPagoOspim> ordenes,ScheduledExecutorService scheduler) throws Exception {
		//int contador = 0; 
		int contador = 1; 
		int delay = 0;
		
		Integer idOp;
		
		
		
		for(OrdenPagoOspim o:ordenes) {
			
			//contador = contador +1;
			delay =  contador*delayConfig;
			
			idOp=o.getId();
			OrdenPagoOspim op =OrdenPagoServiceUtil.getOrdenPagoOspim(idOp);

			String email = null;
			String[] razonDestino = OrdenPagoServiceUtil.getUltimaRazonSocialChequeYDestinoOP(op.getCuit(),
					(op.getAcreedor().getSucursal()!=null && !"".equals(op.getAcreedor().getSucursal())
					?op.getAcreedor().getSucursal():"000"), null, 2); //seccional 0 no va mas... SVA 02/10/2019
			email=razonDestino[3];
		
			logger.debug(email);
			if(StringUtils.checkNotEmpty(op.getCBUTransferencia()) && 
					StringUtils.checkNotEmpty(email) ){
			
			
			
			   List<String> extension=new ArrayList<String>();
		
			   logger.debug(email);
			   if(StringUtils.checkNotEmpty(email) && isValidEmail(email) ){
				
				logger.debug("Por enviar Reporte de OP .pdf a " + op.getEmailCBU() + " /  " + email);
				ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
				PdfServlet pdfServlet=new PdfServlet();
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("ID_ORDEN_PAGO",String.valueOf(op.getId()));		
				byte[] pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.ORDEN_PAGO_OSPIM, hm, PdfServlet.ORDEN_PAGO_OSPIM_PDF_FILENAME);
				logger.debug("Reporte de OP "+String.valueOf(op.getId())+".pdf size:"+pdfOp.length);
				pdfs.add(pdfOp);
				extension.add("pdf");
				hm=new HashMap<String, String>();
				hm.put("id_op_p", String.valueOf(op.getId()));		
				hm.put("entidad_p", String.valueOf(WebKeysGlobal.OSPIM));		
				byte[] pdfRet=pdfServlet.crearPdfComoAdjunto(PdfServlet.COMPROBANTE_RETEN_GANANCIAS, hm, PdfServlet.COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME);
				
				if(null!=pdfRet && pdfRet.length>914){  // con 914 sale en blanco el reporte... mayor q 914 hay algo...
					logger.debug("Reporte de Ret.Gcias "+String.valueOf(op.getId())+".pdf size:"+pdfRet.length);
					pdfs.add(pdfRet);
					extension.add("pdf");
				}	

				if(null!=op.getLiquidacionesListAsString() && !"".equals(op.getLiquidacionesListAsString().trim())){
					byte[] pdfDeb=pdfServlet.crearPdfsNotaDebito(op.getLiquidacionesListAsString());
					if(null!=pdfDeb){
						pdfs.add(pdfDeb);
						extension.add("pdf");
					}
				}
				
				List<String> emailCCO;
        		String destinos;
           		
        		emailCCO = new ArrayList<String>();
        		
        		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_SECCIONAL_CCO");
        		String[] auxDestinos = destinos.split(";");
        		for (String to : auxDestinos) {
        			emailCCO.add(to);
        		}
        		
//COMENTAR				
//   email="dsulfaro@uoma.org.ar";	
           	
				OrdenPago.enviarMailTransferenciaGral(op.getCuit(),op.getCBUTransferencia(), email,emailCCO, pdfs,delay,scheduler);   		
					
					
			  }
			  if(StringUtils.checkEmpty(email) ){
				logger.error("No se encontró el destinatario de correo para enviar comprobantes por pdf para la OP: " + String.valueOf(op.getId()) );
			
			  }	
		   }
		
		}
		
	}
		
	private void generaMarcasEmailEnviado(List<OrdenPagoOspim> ordenes) throws Exception {
				
		for(OrdenPagoOspim o:ordenes) {
			IntegracionServiceUtil.marcaAvisoTransferencia(o.getId());
		}
	}

	

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static boolean isValidEmail(String email) {
        try {
            new InternetAddress(email).validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
    
}
