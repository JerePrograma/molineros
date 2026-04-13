package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceImpl;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil;
import ar.com.ospim.mail.MailUtils;
import jcifs.smb.FileEntry;

public class SincronizarApp extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactoryUtil.getLog(SincronizarApp.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		try {
			
			String estado = "IN";
			String token = ClienteAppMobile.obtenerToken();

			if (token == null) {
			    logger.error("No se pudo obtener token");
			    return;
			}

			List<PreAutorizacion> lista = ClienteAppMobile.getPreAutorizacionessByEstado(estado, null, null);
			ServiceContext sc = new ServiceContext();
			
			try {
			    long companyId = PortalUtil.getDefaultCompanyId();
			    User defaultUser = UserLocalServiceUtil.getDefaultUser(companyId);
			    sc.setUserId(defaultUser.getUserId());
			} catch (Exception e) {
			    logger.error("No se pudo obtener el id del usuario", e);
			    return;
			}
			
			//procesa preautorizaciones
			List<Comprobante> comprobantes = ClienteAppMobileServiceUtil.procesarPreautorizaciones(lista);

			//separa insertados y no insertados
			List<Comprobante> comprobantesProcesados = new ArrayList<Comprobante>();
			List<Comprobante> comprobantesErroneos = new ArrayList<Comprobante>();
			
			for (Comprobante c : comprobantes) {
			    if ("INSERTADO".equalsIgnoreCase(c.getEstado())) {
			        comprobantesProcesados.add(c);
			    } else {
			        comprobantesErroneos.add(c);
			    }
			}

			Map<Integer, List<PreAutorizacion>> mSeccionalesA = new HashMap<Integer, List<PreAutorizacion>>();
			
			//procesa imagenes de los insertados
			for (Comprobante c : comprobantesProcesados) {
				
				List<PreAutorizacion> l= mSeccionalesA.get(c.getAfiliado().getSeccional().getId());
				if(l==null) l=new ArrayList<PreAutorizacion>();
				l.add(c.getPreAutorizacion());
				mSeccionalesA.put(c.getAfiliado().getSeccional().getId(), l);
				
			    try {
			        PreAutorizacion p = c.getPreAutorizacion();
			        if (p != null && p.getIdPedidoApp() > 0) {
			            ClienteAppMobile.procesarDocumentosDePedido(p.getIdPedidoApp(), token, sc);
			            logger.info("Imágenes procesadas automáticamente para preautorización: " + p.getId());
			        }
			    } catch (Exception e) {
			        logger.error("Error al procesar imágenes para preautorización: " + c.getIdPreautorizacion(), e);
			    }
			}
			
			
			
			for (Map.Entry<Integer, List<PreAutorizacion>> entry : mSeccionalesA.entrySet()) {
	    	    Integer idSeccional = entry.getKey();
	    	    List<PreAutorizacion> preautorizaciones = entry.getValue();

	    	    try {
	    	        String emailSeccional = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSeccional).get(0).getContacto();
	    	    	
	    	    	//String emailSeccional = "mauro.depascali@hotmail.com";
	    	    	
	    	        if (emailSeccional == null || emailSeccional.isEmpty()) {
	    	        	logger.warn("No se encontró email para la seccional " + idSeccional);
	    	            continue;
	    	        }

	    	        List<String> emails = new ArrayList<String>();
	    	        emails.add(emailSeccional);

	    	        StringBuilder cuerpo = new StringBuilder();
	    	        cuerpo.append("<p>Se procesaron automáticamente las siguientes preautorizaciones:</p>");
	    	        
	    	        cuerpo.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse: collapse;'>");
	    	        cuerpo.append("<tr>");
	    	        cuerpo.append("<th>ID</th>");
	    	        cuerpo.append("<th>Nombre</th>");
	    	        cuerpo.append("<th>CUIL</th>");
	    	        cuerpo.append("<th>Inte</th>");
	    	        cuerpo.append("</tr>");
	    	        
	    	        for (PreAutorizacion p : preautorizaciones) {
	    	            if (p.getAfiliado() != null) {
	    	            	cuerpo.append("<tr>");
	    	                cuerpo.append("<td>").append(p.getId()).append("</td>");
	    	                cuerpo.append("<td>").append(p.getAfiliado().getApellidoNombre()).append("</td>");
	    	                cuerpo.append("<td>").append(p.getAfiliado().getCuil_titular()).append("</td>");
	    	                cuerpo.append("<td>").append(p.getAfiliado().getInte()).append("</td>");
	    	                cuerpo.append("</tr>");
	    	            }
	    	        }
	    	        cuerpo.append("</table>");
	    	        cuerpo.append("<br/><br/>Ingrese al Portal Molineros, Módulo Preautorizaciones y seleccione el estado APP. Para que sigan su circuito deben completarla y modificar el estado a CARGADO.<br/>");
	    	        cuerpo.append("<br/><br/>Saludos<br/>");
	    	        
	    	        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());		    	       
	    	        String asunto = "APP - Preautorizaciones automáticas procesadas " + fechaHoy;

	    			ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = ReportesServiceUtil.getConfiguracion();

	    	        MailUtils.enviarMailGmailSinAdjHTML(
	    	            rac.getMailFrom(),
	    	            rac.getPass(),
	    	            emails,
	    	            asunto,
	    	            cuerpo.toString(),
	    	            3
	    	        );

	    	        logger.info("Email enviado a seccional ID " + idSeccional + " (" + emailSeccional + ")");

	    	    } catch (Exception e) {
	    	    	logger.error("Error al enviar email a la seccional " + idSeccional, e);
	    	    }
	    	}
			
			//procesar imagenes faltantes en preautorizaciones ya existentes
			for (Comprobante c : comprobantesErroneos) {
			    try {
			        PreAutorizacion p = c.getPreAutorizacion();
			        if (p != null && p.getIdPedidoApp() > 0) {
			            String titulo = "PREAUT_" + p.getId();
			            List<?> imagenes = PreAutorizacionServiceUtil.getImagenesPreautorizacion(titulo);

			            if (imagenes != null && !imagenes.isEmpty()) {
			                logger.info("Ya existen imágenes para la preautorización " + titulo);
			                continue;
			            }

			            //si no existen imágenes, las baja
			            PreAutorizacion pre = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(c.getIdPreautorizacion());
			            Integer idPedidoApp = PreAutorizacionServiceImpl.obtenerIdPreautorizacionAPP(pre.getId());

			            if (idPedidoApp != null && token != null) {
			                ClienteAppMobile.procesarDocumentosDePedido(idPedidoApp, token, sc);
			                logger.info("Imágenes recuperadas para preautorización existente: " + pre.getId());
			            }
			        }
			    } catch (Exception e) {
			        logger.error("Error al reintentar procesamiento de imágenes para: " + c.getIdPreautorizacion(), e);
			    }
			}			
			
			//REINTEGROS
	        logger.error("Entra a reintegros");

			String estadoRei = "IN";
			List<ReclamoPrestacional> reintegros = ClienteAppMobile.getReintegrosByEstado(estadoRei, null, null);			
			
			List<Comprobante> comps = ClienteAppMobileServiceUtil.procesarReintegros(reintegros);
			
			List<Comprobante> ok = new ArrayList<Comprobante>();
			List<Comprobante> err = new ArrayList<Comprobante>();
			for (Comprobante c : comps) {
			    if ("INSERTADO".equalsIgnoreCase(c.getEstado())) {
			        ok.add(c);
			    } else {
			        err.add(c);
			    }
			}
			
	    	Map<Integer, List<ReclamoPrestacional>> mSeccionalesR = new HashMap<Integer, List<ReclamoPrestacional>>();
	    	
			//procesa imagenes de los insertados
			for (Comprobante c : ok) {
			    try {
			    	 ReclamoPrestacional r = c.getReintegro();
			        if (r != null) {
			        	
			        	Integer idSeccional = r.getAfiliado().getSeccional().getId();
	    	            List<ReclamoPrestacional> l = mSeccionalesR.get(idSeccional);
	    	            if (l == null) l = new ArrayList<ReclamoPrestacional>();
	    	            l.add(r);
	    	            mSeccionalesR.put(idSeccional, l);
	    	            
	                    ClienteAppMobile.procesarDocumentosDeReintegro(c, sc);
	                    logger.info("Imágenes procesadas automáticamente para reintegro: " + r.getId_reclamo());
			        }
			    } catch (Exception e) {
			        logger.error("Error al procesar imágenes para reintegro");
			    }
			}
				    	
	    	for (Map.Entry<Integer, List<ReclamoPrestacional>> entry : mSeccionalesR.entrySet()) {
	    	    Integer idSeccional = entry.getKey();
	    	    List<ReclamoPrestacional> rp = entry.getValue();

	    	    try {
	    	        String emailSeccional = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSeccional).get(0).getContacto();

	    	        //String emailSeccional = "mauro.depascali@hotmail.com";
	    	        if (emailSeccional == null || emailSeccional.isEmpty()) {
	    	        	logger.warn("No se encontró email para la seccional " + idSeccional);
	    	            continue;
	    	        }

	    	        List<String> emails = new ArrayList<String>();
	    	        emails.add(emailSeccional);

	    	        StringBuilder cuerpo = new StringBuilder();
	    	        cuerpo.append("<p>Se procesaron automáticamente los siguientes pedidos de reintegros:</p>");
	    	        cuerpo.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse: collapse;'>");
	    	        cuerpo.append("<tr><th>ID</th><th>Nombre</th><th>CUIL</th></tr>");
	    	        for (ReclamoPrestacional r : rp) {
	    	            if (r.getAfiliado() != null) {
	    	                cuerpo.append("<tr>");
	    	                cuerpo.append("<td>").append(r.getId_reclamo()).append("</td>");
	    	                cuerpo.append("<td>").append(r.getAfiliado().getApellidoNombre()).append("</td>");
	    	                cuerpo.append("<td>").append(r.getAfiliado().getCuil_titular()).append("</td>");
	    	                cuerpo.append("</tr>");
	    	            }
	    	        }
	    	        cuerpo.append("</table>");
	    	        cuerpo.append("<br/><br/>Ingrese al Portal Molineros, Módulo Pre Carga Reintegros y seleccione el estado APP.");
	    	        cuerpo.append("<br/><br/>Saludos<br/>");

	    	        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());		    	       
	    	        String asunto = "APP - Reintegros automáticos procesados " + fechaHoy;

	    	        ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = ReportesServiceUtil.getConfiguracion();

	    	        MailUtils.enviarMailGmailSinAdjHTML(
	    	            rac.getMailFrom(),
	    	            rac.getPass(),
	    	            emails,
	    	            asunto,
	    	            cuerpo.toString(),
	    	            3
	    	        );

	    	        logger.info("Email enviado a seccional ID " + idSeccional + " (" + emailSeccional + ")");
	    	    } catch (Exception e) {
	    	    	logger.error("Error al enviar email a la seccional " + idSeccional, e);
	    	    }
	    	}
	    	
			for (Comprobante c : err) {
			    try {
			        ReclamoPrestacional r = c.getReintegro();
			        if (r != null) {
			            String titulo = "REINTEGRO_" + r.getId_reclamo();
			            if (ReclamoPrestacionServiceImpl.getImagenesReintegro(titulo).isEmpty()) {
			                Integer idApp = ReclamoPrestacionServiceImpl.obtenerIdReintegroAPP(r.getId_reclamo());
			                if (idApp != null) {
			                    JSONObject apiRei = ClienteAppMobile.getPedidoReintegroById(idApp, token);
			                    if (apiRei != null) {
			                        r.setIdReintegroApp(idApp);
			                        r.setUrlComprobante(apiRei.optString("url_comprobante", null));
			                        r.setUserCbu(apiRei.optString("user_cbu", null));
			                        r.setUserCbuConstUrl(apiRei.optString("user_cbu_const_url", null));
			                        r.setUrlDocExtra(apiRei.optString("url_doc_extra", null));

			                        Comprobante comp = new Comprobante();
			                        comp.setIdReintegro(r.getId_reclamo());
			                        comp.setUrlComprobante(r.getUrlComprobante());
			                        comp.setReintegro(r);

			                        ClienteAppMobile.procesarDocumentosDeReintegro(comp, sc);
			                        logger.info("Imágenes recuperadas para reintegro existente: " + r.getId_reclamo());
			                    }
			                }
			            }
			        }
			    } catch (Exception e) {
			        logger.error("Error al reintentar procesamiento de imágenes de reintegro", e);
			    }
			}

			//marcar ejecucion correcta
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);

		} catch (SystemException e) {
			logger.error("Error en sincronización automática", e);
		} catch (Exception e) {
			logger.error("Error general en sincronización automática", e);
		}
	}

	@Override
	public HSSFWorkbook getResultados() {
		return null;
	}
}
