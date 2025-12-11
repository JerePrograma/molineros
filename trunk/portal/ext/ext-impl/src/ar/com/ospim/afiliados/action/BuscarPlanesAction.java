/**
 */

package ar.com.ospim.afiliados.action;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

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

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAporte;
import ar.com.ospim.afiliados.beans.AfiAporteList;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.AporteServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarPlanesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de afiliados según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarPlanesAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarPlanesAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			PortletSession portletSession = renderRequest.getPortletSession();
			
//			SVA
//			solo para la reincorporacion de afiliado sin recuperar planes ni sus viejos aportes
			String noRecuperaPlanNiAporte = ParamUtil.getString(renderRequest, "recuperarPlanes");
			
			String id_plan = ParamUtil.getString(renderRequest, "id_plan");
			String cuil = ParamUtil.getString(renderRequest, "cuil_titular");
			int inte = ParamUtil.getInteger(renderRequest, "inte");
			
//			TODO REvisar esto
//			if(noRecuperaPlanNiAporte != null && noRecuperaPlanNiAporte.equalsIgnoreCase("norecuperar")){
//				portletSession.removeAttribute(WebKeysAfiliados.BUSQUEDA_APORTES);
//				
//				Afiliado afiTemp = new Afiliado(cuil);
//				
//				AfiAporteList planAportes = AporteServiceUtil.buscaAportesPorPlan(id_plan, "", 0);
//				Set<Integer> aporte = planAportes.getListaAportes().keySet();
//				for (Iterator<Integer> iterator = aporte.iterator(); iterator.hasNext();) {
//					Integer a = iterator.next();
//					AfiAporte aa = planAportes.getListaAportes().get(a);
//					aa.setAfiliado(afiTemp);
//				}
//				
//				portletSession.setAttribute(WebKeysAfiliados.BUSQUEDA_APORTES,
//						planAportes, PortletSession.APPLICATION_SCOPE);
//				
//				return mapping.findForward("portlet.afiliados.result.planes");
//			}
			
			
			
			
			String fechaEgreso = ParamUtil.getString(renderRequest,"fechaEgreso");
			String opciones= ParamUtil.getString(renderRequest, "opciones");
			String id_motivo_baja = ParamUtil.getString(renderRequest,"motivo_baja");
			renderRequest.setAttribute("id_motivo_baja", id_motivo_baja);
			boolean isPlusTres = ParamUtil.getBoolean(renderRequest,"isPlusTres");			
			
			if(null!=opciones && opciones.equals("true")){
				HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
				Afiliado afiliado = (Afiliado) session.getAttribute((WebKeysAfiliados.AFILIADO_EN_EDICION));
				Calendar fechaVigen= Calendar.getInstance();
				fechaVigen.setTime(afiliado.getVigen_fecha());
				renderRequest.setAttribute("vigenFechaOpciones", fechaVigen);
				renderRequest.setAttribute("opciones", opciones);
			}
			
//			TODO Revisar
//			portletSession.setAttribute(WebKeysAfiliados.BUSQUEDA_APORTES,
//					AporteServiceUtil.buscaAportesPorPlan(id_plan, cuil, inte,
//							fechaEgreso, id_motivo_baja, isPlusTres),
//					PortletSession.APPLICATION_SCOPE);

		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.afiliados.result.planes");

	}

}