/**
 */

package ar.com.ospim.afiliados.action;

import java.math.BigDecimal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarAfiliadosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de afiliados según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarAfiliadosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarAfiliadosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getMotivosBaja(renderRequest);

		String popup = null;
		String checkbox = null;
		String opciones=null;

		try {
			String cuil = null;
			String inte = null;
			String tipoDoc = null;
			String nroDoc = null;
			String seccional = null;
			String seccional_nombre=null;
			int seccional_int = 0;
			String apellido = null;
			String nombre = null;
			String entidad = null;
			int nroAfiliado = 0;
			int nroSocioPrev = 0;	
			BigDecimal nroCredenPrev = new BigDecimal(0);	
			
			int libro = 0;	
			int nroFormulario = 0;
			
			if (null != renderRequest.getParameter("cuil")) {
				cuil = renderRequest.getParameter("cuil").trim().length() > 0 ? renderRequest
						.getParameter("cuil") : null;
			}
			if (null != renderRequest.getParameter("inte")) {
				inte = renderRequest.getParameter("inte").trim().length() > 0 ? renderRequest
						.getParameter("inte") : null;
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
			if (null != renderRequest.getParameter("seccional_nombre")) {
				seccional_nombre = renderRequest.getParameter("seccional_nombre").trim()
						.length() > 0 ? renderRequest.getParameter("seccional_nombre")
						: null;
			}			
			
			if (null != seccional) {
				try {
					seccional_int = Integer.parseInt(seccional);
				} catch (NumberFormatException e) {
					seccional_int = 0;
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
			if (null != renderRequest.getParameter("libro")) {
				libro = renderRequest.getParameter("libro").trim().length() > 0 ? Integer.parseInt(renderRequest
						.getParameter("libro")) : 0;
			}
			
			if (null != renderRequest.getParameter("nroFormulario")) {
				nroFormulario = renderRequest.getParameter("nroFormulario").trim().length() > 0 ? Integer.parseInt(renderRequest
						.getParameter("nroFormulario")) : 0;
			}
			
			opciones=ParamUtil.getString(renderRequest, "opciones");

			entidad = ParamUtil.getString(renderRequest, "entidad", null);
			nroAfiliado = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
//			nroSocioPrev = ParamUtil.getInteger(renderRequest, "nroSocioPrevencion", 0);
//			nroCredenPrev = ParamUtil.getInteger(renderRequest, "nroCredencialPrevencion", 0);
			if (StringUtils.checkNotEmpty(renderRequest.getParameter("nroSocioPrevencion"))) {
				nroSocioPrev = renderRequest.getParameter("nroSocioPrevencion").trim().length() > 0 ? Integer.parseInt(renderRequest
						.getParameter("nroSocioPrevencion")) : 0;
			}

			if (StringUtils.checkNotEmpty(renderRequest.getParameter("nroCredencialPrevencion"))) {
				nroCredenPrev = renderRequest.getParameter("nroCredencialPrevencion").trim().length() > 0 ? new BigDecimal(renderRequest
						.getParameter("nroCredencialPrevencion")) : new BigDecimal(0);
			}
			
			boolean incluyeBajas=ParamUtil.getBoolean(renderRequest, "incluyeBajas");
			
			List<Afiliado> busqueda=null;
			//ME FIJO SI TIENE UNA SECCIONAL FIJA
			User user = PortalUtil.getUser(renderRequest);
			String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString();
			String cred = ParamUtil.getString(renderRequest, "cred", null);
			
			if(opciones!=null && opciones.trim().equals("true")){
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosOpciones(cuil, seccional_nombre, apellido, nombre, libro, nroFormulario, incluyeBajas);				
			}else{
			  //SI TIENE UNA SECCIONAL FIJADA LA SETEO
			  seccional_int=seccionalDefecto!=null&&!seccionalDefecto.trim().equals("")&&!seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):seccional_int;
			  //Y LUEGO BUSCO
				
			  if(cred==null || !"uoma".equals(cred)) {	  
					
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(cuil, inte, tipoDoc,
							nroDoc, seccional_int, apellido, nombre, entidad,
							nroAfiliado, nroSocioPrev, nroCredenPrev);
			  }else if("uoma".equals(cred)) {
				  busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteCredencialUOMA(cuil, inte, tipoDoc,
							nroDoc, seccional_int, apellido, nombre, entidad,
							nroAfiliado, nroSocioPrev, nroCredenPrev);
			  }
			  
			}
			
			//almaceno la lista en sesion
			if(opciones!=null && opciones.trim().equals("true")){
				renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, PortletSession.APPLICATION_SCOPE);
				renderRequest.getPortletSession().setAttribute(
						WebKeysAfiliados.LISTA_AFILIADOS_OPCIONES_EN_SESSION, busqueda,
						PortletSession.APPLICATION_SCOPE);
			}else{
				renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);			
				renderRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION, busqueda,
					PortletSession.APPLICATION_SCOPE);
			}
			
			//la lista en el request
			if(opciones!=null && opciones.trim().equals("true")){
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO_OPCIONES);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO_OPCIONES, busqueda);				
			}else{
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda);
			}
		} catch (Exception e) {
			_log.error(e);
		}

		popup = ParamUtil.getString(renderRequest, "popup");
		// Busqueda con selección de checkbox
		checkbox = ParamUtil.getString(renderRequest, "checkbox");
		renderRequest.setAttribute("checkbox", checkbox);

		if (null != popup && !popup.trim().equals("")) {
			return mapping.findForward("portlet.afiliados.result.search.popup");
		} else if(null!=opciones && opciones.trim().equals("true")) {
			return mapping.findForward("portlet.afiliados.opciones.result.search");
		} else{	
			return mapping.findForward("portlet.afiliados.result.search");
		}

	}

}