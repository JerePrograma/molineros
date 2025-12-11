package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.util.StringUtils;

public class ReporteAvisoTransferenciaIntegracion extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = -5802027963999987103L;
	private static Log logger = LogFactoryUtil.getLog(ReporteAvisoTransferenciaIntegracion.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo ReporteAvisoTransferenciaIntegracion");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			
			List<OrdenPagoOspim> ccs = IntegracionServiceUtil.getOrdenesPagoSinAvisoTransferencia() ;
			generaAvisoTransferencia(ccs);
					
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Avisos de Transferencias Integracion");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	private void generaAvisoTransferencia(List<OrdenPagoOspim> ordenes) throws Exception {
		Integer idOp;
		for(OrdenPagoOspim o:ordenes) {
			idOp=o.getId();
			OrdenPagoOspim op =OrdenPagoServiceUtil.getOrdenPagoOspim(idOp);
			
			String[] razonDestino = OrdenPagoServiceUtil.getUltimaRazonSocialChequeYDestinoOP(op.getCuit(),"000", null, 2); //seccional 0 no va mas... SVA 02/10/2019
			String email=razonDestino[3];
		
			logger.debug(email);
			if(StringUtils.checkNotEmpty(op.getCBUTransferenciaIntegracion()) && 
					StringUtils.checkNotEmpty(email) ){
				
				logger.debug("Por enviar Reporte de OP .pdf a " + op.getEmailCBU() + " /  " + email);
				ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
				PdfServlet pdfServlet=new PdfServlet();
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("ID_ORDEN_PAGO",String.valueOf(op.getId()));		
				byte[] pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.ORDEN_PAGO_OSPIM, hm, PdfServlet.ORDEN_PAGO_OSPIM_PDF_FILENAME);
				logger.debug("Reporte de OP "+String.valueOf(op.getId())+".pdf size:"+pdfOp.length);
				pdfs.add(pdfOp);
				hm=new HashMap<String, String>();
				hm.put("id_op_p", String.valueOf(op.getId()));		
				hm.put("entidad_p", String.valueOf(WebKeysGlobal.OSPIM));		
				byte[] pdfRet=pdfServlet.crearPdfComoAdjunto(PdfServlet.COMPROBANTE_RETEN_GANANCIAS, hm, PdfServlet.COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME);
				
				if(null!=pdfRet && pdfRet.length>914){  // con 914 sale en blanco el reporte... mayor q 914 hay algo...
					logger.debug("Reporte de Ret.Gcias "+String.valueOf(op.getId())+".pdf size:"+pdfRet.length);
					pdfs.add(pdfRet);
				}	

				if(null!=op.getLiquidacionesListAsString() && !"".equals(op.getLiquidacionesListAsString().trim())){
					byte[] pdfDeb=pdfServlet.crearPdfsNotaDebito(op.getLiquidacionesListAsString());
					if(null!=pdfDeb){
						pdfs.add(pdfDeb);
					}
				}
				
//email="dsulfaro@uoma.org.ar";	

				
				List<String> emailCCO;
        		String destinos;
           		
        		emailCCO = new ArrayList<String>();
        		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_SECCIONAL_CCO");
        		String[] auxDestinos = destinos.split(";");
        		for (String to : auxDestinos) {
        			emailCCO.add(to);
        		}
           		
				OrdenPago.enviarMailTransferenciaIntegracion(op.getCuit(),op.getCBUTransferenciaIntegracion(), email,emailCCO, pdfs);
 				IntegracionServiceUtil.avisoTransferenciaOP(idOp);
				
					
			}
			if(StringUtils.checkEmpty(email) ){
				logger.error("No se encontró el destinatario de correo para enviar comprobantes por pdf para la OP: " + String.valueOf(op.getId()) );
			
			}	
		}
	}


	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

    
}
