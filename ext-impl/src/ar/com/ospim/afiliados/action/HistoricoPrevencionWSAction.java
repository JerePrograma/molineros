package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.NoHayMensajeErrorException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.EmailHomologacionPS;
import ar.com.ospim.afiliados.beans.FiltroBusquedaHisPrevencionWS;
import ar.com.ospim.afiliados.services.PrevencionServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.webservice.service.AfiliadoOpe;

public class HistoricoPrevencionWSAction extends PortletAction {
	
	private static Log logger = LogFactoryUtil.getLog(HistoricoPrevencionWSAction.class);
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		
		try {
			
			
			int operacion = ParamUtil.getInteger(renderRequest, "operacion");
			Integer idTransaccion = ParamUtil.getInteger(renderRequest, "idTransaccion");
			boolean accion = ParamUtil.getBoolean(renderRequest, "accion");
			
			Integer inte = ParamUtil.getInteger(renderRequest, "inte_select");
			String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
			
			String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
			if (cmd !=null && "PROCESAR".equalsIgnoreCase(cmd)) {
				PrevencionServiceUtil.procesar(operacion, idTransaccion, accion);
			}else if (cmd !=  null && "ENVIAR_MAIL_HOMOLOGACION_PS".equals(cmd)) {
				EmailHomologacionPS eh = null;
				eh = PrevencionServiceUtil.obtenerDatosEmailPrevencion(cuil_titular, inte);
				
				List<String> emails;
				String destinos;
				
				emails = new ArrayList<String>();
				destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_ERROR_HOMOLOGACION_PS");
				String[] auxDestinos = destinos.split(";");
				for (String to : auxDestinos) {
					emails.add(to);
				}
				if  (mensaje(eh)!= null){
					EnviaEmailsThread.enviarMailDesatendido("Alta homologación para intercambio novedades padrón ws OSPIM", mensaje(eh), emails, 1);				
				}else {
					SessionErrors.add(renderRequest, NoHayMensajeErrorException.class.getName());
					return mapping.findForward("portlet.prevencion.ws.result.search");
				}
				
			}
			
			try {
				buscarHistorialPrevencionWS(renderRequest);
			} catch (Exception e) {
				setForward(renderRequest, "portlet.afiliados.error");
			}
			
		} catch (Exception e) {
			logger.debug(e);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.prevencion.ws.result.search"));
	}
	
	
	
	
	
	private static void buscarHistorialPrevencionWS ( RenderRequest renderRequest) throws SystemException {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		FiltroBusquedaHisPrevencionWS fb = new FiltroBusquedaHisPrevencionWS();
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		
		int desdeDia = ParamUtil.getInteger(renderRequest, "desde_dia");
		int desdeMes = ParamUtil.getInteger(renderRequest, "desde_mes");
		int desdeAnio = ParamUtil.getInteger(renderRequest, "desde_anio");
		GregorianCalendar fechaDesde = null;
		if (desdeDia != 0 && desdeAnio != 0) {
			fechaDesde = new GregorianCalendar(desdeAnio, desdeMes,desdeDia);
		}
		int hastaDia = ParamUtil.getInteger(renderRequest, "hasta_dia");
		int hastaMes = ParamUtil.getInteger(renderRequest, "hasta_mes");
		int hastaAnio = ParamUtil.getInteger(renderRequest, "hasta_anio");
		GregorianCalendar fechaHasta = null;
		if (hastaDia != 0 && hastaAnio != 0) {
			fechaHasta = new GregorianCalendar(hastaAnio, hastaMes,hastaDia);
		}
		
		if (cuilTitular != null && !cuilTitular.isEmpty() && !cuilTitular.contains("-")) {
			fb.setCuilTitular(cuilTitular);
			fb.setFechaDesde(fechaDesde);
			fb.setFechaHasta(fechaHasta);
			renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.BUSQUEDA_FILSTRO_PREVENCION_WS, PortletSession.APPLICATION_SCOPE);			
			renderRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.BUSQUEDA_FILSTRO_PREVENCION_WS, fb,
					PortletSession.APPLICATION_SCOPE);
		}
		
		
		fb = (FiltroBusquedaHisPrevencionWS) session.getAttribute(WebKeysAfiliados.BUSQUEDA_FILSTRO_PREVENCION_WS);
		
		List<AfiliadoOpe> novedades = null;
		
		novedades = PrevencionServiceUtil.buscarHistoricoPrevencionAfi(fb.getCuilTitular(),  fb.getFechaDesde().getTime(), fb.getFechaHasta().getTime());
		
		renderRequest.setAttribute(WebKeysAfiliados.NOVEDADES_PREVENCION_WS, novedades);
		
	}
	
	
	
	
	private static String mensaje (EmailHomologacionPS eh) {
		String out =  null;
		String mensaje = eh.getMensaje().toUpperCase();
		
		if (mensaje.contains("TIPO O")){
			out = "Buenos días\r\n" + 
					"Solicitamos homologar para el web service de novedades de padrón con Ospim los siguientes datos de Localidad   \n"
					+ "  Provincia:      " +  eh.getProvinciaDesc()    +  "\n"
					+ "  Id Provincia:   " +  eh.getIdProvinciaSuper() +  "\n"
					+ "  Localidad:      " +  eh.getLocalidadDesc()    +  "\n"
					+ "  Id Localidad:   " +  eh.getIdLocalidad_sss()  +  "\n"
					+ "  Código Postal:   " +  eh.getCodigoPostal()  +  "\n"
					+ "\n"
					+ "Gracias"
					+ "\n";
		}else if (mensaje.contains("TIPO S")) {
			out = "Solicitamos homologar para el web service de novedades de padrón con Ospim los siguientes datos de Nacionalidad   \n"
					+ "  Nacionalidad:      " +  eh.getNacionalidad()        +  "\n"
					+ "  Id Nacionalidad:   " +  eh.getIdNacionalidadSuper() +  "\n"
					+ "\n"
					+ "Gracias"
					+ "\n";
//		}else if (mensaje.contains("motivo de baja")) {
//			out = "Solicitamos homologar para el web service de novedades de padrón con Ospim los siguientes datos de Motivo de Baja   \n"
//					+ "  Motivo baja:      " +  eh.get       +  "\n"
//					+ "  Id Motivo baja:   " +  eh.get +  "\n"
//					+ "\n"
//					+ "Gracias"
//					+ "\n";
		}
		
		
		return out;
	}
	
	
}