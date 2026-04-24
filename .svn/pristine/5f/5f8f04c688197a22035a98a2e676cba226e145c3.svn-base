package ar.com.ospim.desarrolloAppMobile.action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletConfig;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.AgendadoJava30minScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceImpl;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.ConnectionHelper;
import jcifs.smb.FileEntry;

public class ClienteAppMobileAction extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());

	public ActionForward render(ActionMapping mapping, ActionForm form,
								PortletConfig portletConfig, RenderRequest renderRequest,
								RenderResponse renderResponse) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, "cmd", null);
	    String tipo = ParamUtil.getString(renderRequest, "tipo", "preautorizacion");

		if ("importarDatosApp".equals(cmd)) {
		    String fechaEstadoComprobanteMesDde = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteMesDde");
		    String fechaEstadoComprobanteDiaDde = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteDiaDde");
		    String fechaEstadoComprobanteAnioDde = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteAnioDde");

		    String fechaEstadoComprobanteMesHta = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteMesHta");
		    String fechaEstadoComprobanteDiaHta = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteDiaHta");
		    String fechaEstadoComprobanteAnioHta = ParamUtil.getString(renderRequest, "fechaEstadoComprobanteAnioHta");

		    SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		    Date fechaDesde = null;
		    Date fechaHasta = null;

		    try {
		        fechaDesde = formatoDeFecha.parse(fechaEstadoComprobanteDiaDde + "/" + (Integer.parseInt(fechaEstadoComprobanteMesDde) + 1) + "/" + fechaEstadoComprobanteAnioDde);
		        fechaHasta = formatoDeFecha.parse(fechaEstadoComprobanteDiaHta + "/" + (Integer.parseInt(fechaEstadoComprobanteMesHta) + 1) + "/" + fechaEstadoComprobanteAnioHta);
		    } catch (Exception e) {
		        _log.error("Error parseando fechas", e);
		    }

		    String estado = ParamUtil.getString(renderRequest, "estado", null);
		    User user = PortalUtil.getUser(renderRequest);

		    if ("pre".equalsIgnoreCase(tipo)) {
		    	
		    	String token = ClienteAppMobile.obtenerToken();
		    	
		    	if (token == null) {
		    	    _log.error("No se pudo obtener token");
		    	    return mapping.findForward("portlet.comprobantes.download.app");
		    	}
		    	
		    	List<PreAutorizacion> list = ClienteAppMobile.getPreAutorizacionessByEstado(estado, fechaDesde, fechaHasta);    	
		
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), renderRequest);		    	
		    	
		    	List<Comprobante> comprobantes = ClienteAppMobileServiceUtil.procesarPreautorizaciones(list);
		    	List<Comprobante> comprobantesProcesados = new ArrayList<Comprobante>();
		    	List<Comprobante> comprobantesErroneos = new ArrayList<Comprobante>();
		    	
		    	for (Comprobante c : comprobantes) {
		    	    if ("INSERTADO".equalsIgnoreCase(c.getEstado())) {
		    	        comprobantesProcesados.add(c);
		    	    } else {
		    	        comprobantesErroneos.add(c);
		    	    }
		    	}
		    	Map<Integer, List<PreAutorizacion>> mSeccionales = new HashMap<Integer, List<PreAutorizacion>>();
		    	//solo se procesan imagenes de los que se insertan nuevos
		        for (Comprobante c : comprobantesProcesados) {
		        	//c.getPreAutorizacion().getSeccionalAltaUsr();
		        	
		        	List<PreAutorizacion> l= mSeccionales.get(c.getAfiliado().getSeccional().getId());
					if(l==null) l=new ArrayList<PreAutorizacion>();
					l.add(c.getPreAutorizacion());
					mSeccionales.put(c.getAfiliado().getSeccional().getId(), l);							            
					
					try {
		            	PreAutorizacion p = c.getPreAutorizacion();
		                if (p != null) {
		                    ClienteAppMobile.procesarDocumentosDePedido(p.getIdPedidoApp(), token, serviceContext);
				            _log.info("Imágenes procesadas automáticamente para preautorización: " + p.getId());
		                }
		            } catch (Exception e) {
		                _log.error("Error al procesar documentos para pedido: " + c.getIdPreautorizacion(), e);
		            }
		        }
		    	
		    	renderRequest.getPortletSession().setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS, comprobantesProcesados);
		    	renderRequest.getPortletSession().setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS, comprobantesErroneos);		    	
		    	
		    	for (Map.Entry<Integer, List<PreAutorizacion>> entry : mSeccionales.entrySet()) {
		    	    Integer idSeccional = entry.getKey();
		    	    List<PreAutorizacion> preautorizaciones = entry.getValue();

		    	    try {
		    	        String emailSeccional = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSeccional).get(0).getContacto();
		    	    	
		    	    	//String emailSeccional = "mauro.depascali@hotmail.com";
		    	    	
		    	        if (emailSeccional == null || emailSeccional.isEmpty()) {
		    	            _log.warn("No se encontró email para la seccional " + idSeccional);
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

		    	        _log.info("Email enviado a seccional ID " + idSeccional + " (" + emailSeccional + ")");

		    	    } catch (Exception e) {
		    	        _log.error("Error al enviar email a la seccional " + idSeccional, e);
		    	    }
		    	}
		    
		    }else if ("rei".equalsIgnoreCase(tipo)) {
		    	String token = ClienteAppMobile.obtenerToken();
		    	
		    	if (token == null) {
		    	    _log.error("No se pudo obtener token");
		    	    return mapping.findForward("portlet.comprobantes.download.app");
		    	}
		    	
		    	List<ReclamoPrestacional> list = ClienteAppMobile.getReintegrosByEstado(estado, fechaDesde, fechaHasta);    	
		    	_log.info("Reintegros obtenidos desde la API: " + list.size());

				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), renderRequest);		    	
		    	
		    	List<Comprobante> comprobantes = ClienteAppMobileServiceUtil.procesarReintegros(list);
		    	_log.info("[ACT] total devuelto por procesarReintegros=" + (comprobantes!=null ? comprobantes.size() : -1));

		    	List<Comprobante> comprobantesProcesados = new ArrayList<Comprobante>();
		    	List<Comprobante> comprobantesErroneos = new ArrayList<Comprobante>();
		    	
		    	
		    	for (Comprobante c : comprobantes) {
		    	    _log.info("[ACT] comp estado=" + c.getEstado() + " idReint=" + c.getIdReintegro());

		    	    if ("INSERTADO".equalsIgnoreCase(c.getEstado())) {
		    	        comprobantesProcesados.add(c);
		    	    } else {
		    	        comprobantesErroneos.add(c);
		    	    }
		    	}
		    	
		    	_log.info("Comprobantes procesados: " + comprobantesProcesados.size());
		    	_log.info("Comprobantes con error: " + comprobantesErroneos.size());
		    	
		    	Map<Integer, List<ReclamoPrestacional>> mSeccionales = new HashMap<Integer, List<ReclamoPrestacional>>();

		    	//solo se procesan imagenes de los que se insertan nuevos
		    	for (Comprobante c : comprobantesProcesados) {
		    	    try {
		    	        ReclamoPrestacional r = c.getReintegro();
		    	        if (r != null) {		    	        			    	        	
		    	        	
		    	        	Integer idSeccional = r.getAfiliado().getSeccional().getId();
		    	            List<ReclamoPrestacional> lista = mSeccionales.get(idSeccional);
		    	            if (lista == null) lista = new ArrayList<ReclamoPrestacional>();
		    	            lista.add(r);
		    	            mSeccionales.put(idSeccional, lista);
		    	            
		    	            ClienteAppMobile.procesarDocumentosDeReintegro(c, serviceContext);
		    	            _log.info("Imágenes procesadas automáticamente para reintegro: " + r.getId_reclamo());
		    	        }
		    	    } catch (Exception e) {
		    	        _log.error("Error al procesar documentos para reintegro: " + c.getIdReintegro(), e);
		    	    }
		    	}

		    	for (Map.Entry<Integer, List<ReclamoPrestacional>> entry : mSeccionales.entrySet()) {
		    	    Integer idSeccional = entry.getKey();
		    	    List<ReclamoPrestacional> reintegros = entry.getValue();

		    	    try {
		    	        String emailSeccional = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSeccional).get(0).getContacto();

		    	        //String emailSeccional = "mauro.depascali@hotmail.com";
		    	        if (emailSeccional == null || emailSeccional.isEmpty()) {
		    	            _log.warn("No se encontró email para la seccional " + idSeccional);
		    	            continue;
		    	        }

		    	        List<String> emails = new ArrayList<String>();
		    	        emails.add(emailSeccional);

		    	        StringBuilder cuerpo = new StringBuilder();
		    	        cuerpo.append("<p>Se procesaron automáticamente los siguientes pedidos de reintegros:</p>");
		    	        cuerpo.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse: collapse;'>");
		    	        cuerpo.append("<tr><th>ID</th><th>Nombre</th><th>CUIL</th></tr>");
		    	        for (ReclamoPrestacional r : reintegros) {
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

		    	        _log.info("Email enviado a seccional ID " + idSeccional + " (" + emailSeccional + ")");
		    	    } catch (Exception e) {
		    	        _log.error("Error al enviar email a la seccional " + idSeccional, e);
		    	    }
		    	}
		    	
		    	renderRequest.getPortletSession().setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS, comprobantesProcesados);
		    	renderRequest.getPortletSession().setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS, comprobantesErroneos);
		    
		    }
		    
		    return mapping.findForward("portlet.comprobantes.download.app");
		    
		}else if ("descargarImagenes".equals(cmd)) {
			
			if ("pre".equalsIgnoreCase(tipo)) {
			    String titulo = ParamUtil.getString(renderRequest, "titulo");
			    Integer idPreautorizacion = null;
	
			    try {
			        idPreautorizacion = Integer.parseInt(titulo.replace("PREAUT_", "").replace("-", ""));
			    } catch (Exception e) {
			        _log.error("No se pudo extraer el ID de preautorización: " + titulo, e);
			        return mapping.findForward("portlet.comprobantes.download.app");
			    }
	
			    try {
			        //verifica si ya existen imagenes asociadas
			        List<DLFileEntryImpl> imagenes = PreAutorizacionServiceUtil.getImagenesPreautorizacion(titulo);
	
			        if (imagenes != null && !imagenes.isEmpty()) {
			            _log.info("Ya existen imágenes para la preautorización " + titulo);
			            return mapping.findForward("portlet.comprobantes.download.app");
			        }
	
			        //si no existen imagenes, procesa los documentos
			        PreAutorizacion pre = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
			        Integer idPedidoApp = PreAutorizacionServiceImpl.obtenerIdPreautorizacionAPP(pre.getId());
	
			        if (idPedidoApp != null) {
			            String token = ClienteAppMobile.obtenerToken();
			            if (token != null) {
			                ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), renderRequest);
			                ClienteAppMobile.procesarDocumentosDePedido(idPedidoApp, token, serviceContext);
			            }
			        }
			    } catch (Exception e) {
			        _log.error("Error procesando documentos para: " + idPreautorizacion, e);
			    }
			}else if ("rei".equalsIgnoreCase(tipo)) {
				
				String titulo = ParamUtil.getString(renderRequest, "titulo");
				Integer idReintegroLocal;

				try {
				    idReintegroLocal = Integer.parseInt(titulo.replace("REINTEGRO_", "").replace("-", ""));
				} catch (Exception e) {
				    _log.error("No se pudo extraer el ID de reintegro: " + titulo, e);
				    return mapping.findForward("portlet.comprobantes.download.app");
				}

				try {
				    //valida si ya tiene imagenes
				    List<DLFileEntryImpl> imagenes = ReclamoPrestacionServiceImpl.getImagenesReintegro(titulo);
				    if (imagenes != null && !imagenes.isEmpty()) {
				        _log.info("Ya existen imágenes para el reintegro " + titulo);
				        return mapping.findForward("portlet.comprobantes.download.app");
				    }

				    //toma id APP
				    Integer idReintegroApp = ReclamoPrestacionServiceImpl.obtenerIdReintegroAPP(idReintegroLocal);
				    if (idReintegroApp == null) {
				        _log.warn("No se pudo obtener id_reintegro_app para el reintegro local " + idReintegroLocal);
				        return mapping.findForward("portlet.comprobantes.download.app");
				    }
				    
				    String token = ClienteAppMobile.obtenerToken();
				    if (token == null) {
				        _log.error("No se pudo obtener token");
				        return mapping.findForward("portlet.comprobantes.download.app");
				    }

				    JSONObject apiRei = ClienteAppMobile.getPedidoReintegroById(idReintegroApp, token);
				    
				    if (apiRei == null) {
				        _log.warn("La API no devolvió datos para el reintegro app id=" + idReintegroApp);
				        return mapping.findForward("portlet.comprobantes.download.app");
				    }

				    //arma reclamo prestacional
				    ReclamoPrestacional r = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReintegroLocal);
				    if (r == null) {
				        _log.warn("No existe el reintegro local id=" + idReintegroLocal);
				        return mapping.findForward("portlet.comprobantes.download.app");
				    }

				    //setea url desde la API
				    r.setIdReintegroApp(idReintegroApp);
				    r.setUrlComprobante(apiRei.optString("url_comprobante", null));
				    r.setUserCbu(apiRei.optString("cbu_reintegro", null));
				    r.setUserCbuConstUrl(apiRei.optString("cbu_const_url", null));
				    r.setUrlDocExtra(apiRei.optString("url_doc_extra", null));
	                r.setCbuAutorizante(apiRei.optString("cbu_aut_url", null));

				    if (r.getReintegroCuil() == null || r.getReintegroCuil().trim().isEmpty()) {
				        r.setReintegroCuil(apiRei.optString("cuil_titular_cuenta", null));
				    }
				    
				    ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), renderRequest);

				    Comprobante comprobante = new Comprobante();
				    comprobante.setIdReintegro(r.getId_reclamo());
				    comprobante.setUrlComprobante(r.getUrlComprobante());
				    comprobante.setReintegro(r);

				    ClienteAppMobile.procesarDocumentosDeReintegro(comprobante, serviceContext);

				} catch (Exception e) {
				    _log.error("Error procesando documentos para reintegro: " + idReintegroLocal, e);
				}

				return mapping.findForward("portlet.comprobantes.download.app");
			}
			return mapping.findForward("portlet.comprobantes.download.app");
		}
		return mapping.findForward("");
	}
}


