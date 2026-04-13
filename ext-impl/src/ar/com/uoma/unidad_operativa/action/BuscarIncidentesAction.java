package ar.com.uoma.unidad_operativa.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.uoma.beans.IncidenteTotal;
import ar.com.uoma.unidad_operativa.BusquedaIncidentesUnidadOpeFiltro;
import ar.com.uoma.unidad_operativa.WebKeysUnidadOperativa;
import ar.com.uoma.unidad_operativa.services.UnidadOperativaServiceUtil;

/**
 * <a href="BuscarIncidentesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de incidentes según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarIncidentesAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarIncidentesAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		BusquedaIncidentesUnidadOpeFiltro filtro = (BusquedaIncidentesUnidadOpeFiltro) session.getAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES);
			
		if(filtro == null){
			filtro = new BusquedaIncidentesUnidadOpeFiltro();
		}

		try {			
			String cuil = null;
			int inte = 0;
			String tipoDoc = null;
			String nroDoc = null;
			String seccional = null;
			String seccional_afiliado = null;
			int seccional_int = 0;
			int seccional_afiliado_int = 0;
			String apellido = null;
			String nombre = null;
			String entidad = null;
			int nroAfiliado = 0;
			Date fechaDesde=null;
			Date fechaHasta=null;
			
			String fecha_desde=ParamUtil.getString(renderRequest, "fecha_desde");
			String fecha_hasta=ParamUtil.getString(renderRequest, "fecha_hasta");
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			
			if(null!=fecha_desde&&!fecha_desde.equals("")){
				fechaDesde=sdf.parse(fecha_desde);
			}
			
			if(null!=fecha_hasta&&!fecha_hasta.equals("")){
				fechaHasta=sdf.parse(fecha_hasta);
			}		
			
			if (null != renderRequest.getParameter("cuil")) {
				cuil = renderRequest.getParameter("cuil").trim().length() > 0 ? renderRequest
						.getParameter("cuil") : null;
			}
			if (null != renderRequest.getParameter("inte")) {
				inte = ParamUtil.getInteger(renderRequest, "inte");
			}
			if (null != renderRequest.getParameter("tipoDoc")) {
				tipoDoc = renderRequest.getParameter("tipoDoc").trim().length() > 0 ? renderRequest
						.getParameter("tipoDoc") : null;
			}
			if (null != renderRequest.getParameter("nroDoc")) {
				nroDoc = renderRequest.getParameter("nroDoc").trim().length() > 0 ? renderRequest
						.getParameter("nroDoc") : null;
			}
			if (null != renderRequest.getParameter("seccional")) {
				seccional = renderRequest.getParameter("seccional").trim()
						.length() > 0 ? renderRequest.getParameter("seccional")
						: null;
			}
			if (null != seccional) {
				try {
					seccional_int = Integer.parseInt(seccional);
				} catch (NumberFormatException e) {
					seccional_int = 0;
				}
			}
			
			if (null != renderRequest.getParameter("seccional_afiliado")) {
				seccional_afiliado = renderRequest.getParameter("seccional_afiliado").trim()
						.length() > 0 ? renderRequest.getParameter("seccional_afiliado")
						: null;
			}
			if (null != seccional_afiliado) {
				try {
					seccional_afiliado_int = Integer.parseInt(seccional_afiliado);
				} catch (NumberFormatException e) {
					seccional_afiliado_int = 0;
				}
			}
			
			if (null != renderRequest.getParameter("apellido")) {
				apellido = renderRequest.getParameter("apellido").trim()
						.length() > 0 ? renderRequest.getParameter("apellido")
						: null;
			}
			if (null != renderRequest.getParameter("nombre")) {
				nombre = renderRequest.getParameter("nombre").trim().length() > 0 ? renderRequest
						.getParameter("nombre") : null;
			}

			entidad = ParamUtil.getString(renderRequest, "entidad", null);
			nroAfiliado = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			
			int registrosTotalBusqueda = 0;
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
			
			filtro.setApellido(apellido);
			filtro.setCuil(cuil);
			filtro.setEntidad(entidad);
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setInte(inte);
			filtro.setNombre(nombre);
			filtro.setNroAfiliado(nroAfiliado);
			filtro.setNroDoc(nroDoc);
			filtro.setPagina(pagina_sel);
			filtro.setSeccional(seccional);
			filtro.setSeccional_int(seccional_int);
			filtro.setSeccional_afiliado(seccional_afiliado);
			filtro.setSeccional_afiliado_int(seccional_afiliado_int);
			filtro.setTipoDoc(tipoDoc);
			
			
			List<IncidenteTotal> busqueda = UnidadOperativaServiceUtil.buscarIncidentes(filtro);
			
			session.removeAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES_RESULT);
			session.removeAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES);
			
			session.setAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES, filtro);
			session.setAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES_RESULT, busqueda);
			
			if(busqueda != null && busqueda.size() > 0){
				registrosTotalBusqueda = busqueda.get(0).getTotal_registros();
				filtro.setRegistrosTotal(registrosTotalBusqueda);
				
				session.setAttribute("total_registros", registrosTotalBusqueda);
				session.setAttribute("offset_reg", pagina_sel);
			}else{
				filtro.setRegistrosTotal(registrosTotalBusqueda);
				session.setAttribute("total_registros",0 );
				session.setAttribute("offset_reg", 0);
			}
			
			//almaceno la lista en sesion
			renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);
			//renderRequest.getPortletSession().setAttribute(	WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, busqueda,	PortletSession.APPLICATION_SCOPE);			

		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping.findForward("portlet.uoma.incidente.result.search");
		

	}

}