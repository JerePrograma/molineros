package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.services.ConvenioPrestacionalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class CambioVistaListaPrestacionesConvenioPrestAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(CambioVistaListaPrestacionesConvenioPrestAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		ConvenioPrestacionalDetalle cpd = null;		
		
		String tipoVistaSelec = ParamUtil.getString(renderRequest, "tipoVistaSelec");

		List<ConvenioPrestacionalDetalle> detallesPorRango = (List<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
		
		_log.debug("Cambio vista detalles de prestaciones del convenio prest.: " + tipoVistaSelec);	
		
		if(tipoVistaSelec.equalsIgnoreCase("RANGO")){
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);
		}
		if(tipoVistaSelec.equalsIgnoreCase("CODIGO")){
			if(!detallesPorRango.isEmpty() && detallesPorRango.size()>0){
				cpd = detallesPorRango.get(0); // tomo alguna prestacion para sacar el id del conv. prest.
			}
			List<ConvenioPrestacionalDetalle> detallesPorCodigo = ConvenioPrestacionalServiceUtil.getPrestacionesDetallesPorCodigo(cpd.getIdConvenioPrestacional());
			
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE, detallesPorCodigo);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lista_convenio_prest_detalle"));
	}
	
}