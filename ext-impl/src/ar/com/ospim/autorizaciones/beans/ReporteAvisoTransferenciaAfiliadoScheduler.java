package ar.com.ospim.autorizaciones.beans;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

public class ReporteAvisoTransferenciaAfiliadoScheduler extends AgendadoJava implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3350238510754412121L;

	private static int delayConfig =  Integer.parseInt(TraeListasServiceUtil.getSystemConfig("EMAIL_DELAY"));
	
	private static Log logger = LogFactoryUtil.getLog(ReporteAvisoTransferenciaAfiliadoScheduler.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo ReporteAvisoTransferenciaAfiliado");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		ScheduledExecutorService scheduler  = Executors.newSingleThreadScheduledExecutor();
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			
			List<OrdenPagoOspim> aviso = IntegracionServiceUtil.getOrdenesPagoSinAvisoTransferenciaPagoAfiliado() ;
			generaAvisoTransferenciaSeccional(aviso,scheduler);	
			generaAvisoTransferenciaAfiliados(aviso,scheduler);
			generaMarcasEmailEnviado(aviso);
			
			
					
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Avisos de Transferencias ReporteAvisoTransferenciaAfiliado");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		scheduler.shutdown();
	}

	
	private void generaAvisoTransferenciaSeccional(List<OrdenPagoOspim> ordenes,ScheduledExecutorService scheduler) throws Exception {
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
			Seccional seccional =SeccionalServiceUtil.buscarSeccionalById(op.getId_seccional() );
			email = SeccionalServiceUtil.buscarContactosSeccionalEmail(seccional.getId()).get(0).getContacto(); 
     	
			List<String> extension=new ArrayList<String>();
		
			logger.debug(email);
			if(seccional != null && 
					StringUtils.checkNotEmpty(email) ){
				
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
//COMENTAR				
//   email="dsulfaro@uoma.org.ar";	
           	
           		List<String> emailCCO;
        		String destinos;
        		emailCCO = new ArrayList<String>();
                destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_INTEGRACION_CCO");
        		String[] auxDestinos = destinos.split(";");
        		for (String to : auxDestinos) {
        			emailCCO.add(to);
        		}

        		if(op.getReintegrosList()!=null && !op.getReintegrosList().isEmpty()){
        			Workbook wb = null;
        			wb=ReporteOPReintegrosFarmaciaPresta.generaReporteOPReintegrosFarmaciaPresta(op.getReintegrosList().toString());
        			
        			ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    wb.write(bos);
                    pdfs.add(bos.toByteArray());
                    extension.add("xls");
        		}
        		
        		
				OrdenPago.enviarMailTransferenciaSeccional(seccional.getDescripcion(),"Se adjunto el detalle de los pagos", email,emailCCO, pdfs, delay,scheduler,extension);
 			
				
					
			}
			if(StringUtils.checkEmpty(email) ){
				logger.error("No se encontró el destinatario de correo para enviar comprobantes por pdf para la OP: " + String.valueOf(op.getId()) );
			
			}	
		}
		
	
		
	}
	
	

	private void generaAvisoTransferenciaAfiliados(List<OrdenPagoOspim> ordenes,ScheduledExecutorService scheduler) throws Exception {
		Integer idOp;
		for (OrdenPagoOspim o : ordenes) {
			idOp = o.getId();
			OrdenPagoOspim op = OrdenPagoServiceUtil.getOrdenPagoOspim(idOp);

			List<FormaPago> pagos = null;
			if (op != null) {
				pagos = op.getFormaPago();
			}

			int delay = 0;

			int contador = 1;
//			int contador = 0;

			
				for (FormaPago pago : pagos) {

					//contador = contador + 1;
					delay = contador * delayConfig;

					String nombreCuenta = pago.getPago().getPagoBancario().getNombreCuenta();
					String apellidoCuenta = pago.getPago().getPagoBancario().getApellidoCuenta();
					String email = pago.getPago().getPagoBancario().getEmailCuenta();
					String importe = pago.getPago().getPagoBancario().getImporte().toString();
					logger.debug(email);
					if (nombreCuenta != null && StringUtils.checkNotEmpty(email)) {

//email = "dsulfaro@uoma.org.ar";

						String apeNom = apellidoCuenta + "  " + nombreCuenta;

						StringBuffer leyenda = new StringBuffer("");
						leyenda.append(
								"Le informamos que en las próximas 24/48 horas se realizará una transferencia bancaria de $ "
										+ importe + " , a la cuenta que usted nos informara oportunamente, en ");
						leyenda.append("concepto de reintegro.\n\r");
						leyenda.append("Cualquier aclaración, por favor comuníquese con su Seccional.\n\r");
						leyenda.append("Atte,\n\r");
						leyenda.append("Obra Social del Personal de la Industria Molinera");
						leyenda.append("\n\r");

						String numerosReclamos = null;

						numerosReclamos = ReclamosPrestacionesServiceUtil.traerNumerosReclamos(op.getId(),pago.getId());


						OrdenPago.enviarMailTransferenciaAfiliado(apeNom, leyenda.toString(), email, numerosReclamos,delay,scheduler);

						if (StringUtils.checkEmpty(email)) {
							logger.error(
									"No se encontró el destinatario de correo para enviar comprobantes por pdf para la OP: "
											+ String.valueOf(op.getId()));

						}
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

    
}
