/**
 */

package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AfiAportes;
import ar.com.ospim.afiliados.beans.AfiObservacion;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.services.AfiObservacionServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.SituLaboralServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ViewAfiliadoEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * @modif sva
 */
public class ViewAfiliadoEntryAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
				renderRequest).getSession();
		TraeListasServiceUtil.getMotivosBaja(renderRequest);
		try {
//			ActionUtil.getAfiliadoEntryInclusoDadoBaja(renderRequest);
			String cuil_titular = ParamUtil.getString(renderRequest,
					"cuil_titular");
			int inte = ParamUtil.getInteger(renderRequest, "inte");
			Afiliado afiliado = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(cuil_titular, inte);
			session.setAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION, afiliado);
//			Observaciones internas
			List<AfiObservacion> obsInternasGrupoFliar =  AfiObservacionServiceUtil.getObservaciones(cuil_titular, inte);
			renderRequest.setAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR, obsInternasGrupoFliar);
			
//			Buscamos el plan vigente del afiliado si es que tiene...
			AfiPlan afiPlan = PlanServiceUtil.getInstance().buscarUltimoPlanAportes(cuil_titular);
		    afiliado.setAfiPlan(afiPlan);
//			List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().buscaUltimosIdsSocio(cuil_titular) ;
			List<AfiAportes> afiAportes = PlanServiceUtil.getInstance().consultaUltimosComponentesPlanVigente(cuil_titular);
			//almaceno la lista en sesion
			renderRequest.setAttribute("IdsSocio", afiAportes); 
				
			List<AfiTercerizadoraServicio> tercerizAfi = TercerizadoraServiceUtil.buscarUltimasTercerizadorasContinuidadDelAfiliado(null, cuil_titular);
			session.setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION, tercerizAfi);
			
			List<SituacionLaboral> laboralList = SituLaboralServiceUtil.buscaSituLaboral(cuil_titular,inte);
			
			session.setAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL,laboralList);
			
			// Quito los aportes de session se trata de un nuevo afiliado
			PortletSession portletSession = renderRequest.getPortletSession();
//			portletSession.removeAttribute(WebKeysAfiliados.BUSQUEDA_APORTES,
//					PortletSession.APPLICATION_SCOPE);
			portletSession.removeAttribute(WebKeysAfiliados.PLAN_NUEVO_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);
			portletSession.removeAttribute(
					WebKeysAfiliados.TERCERIZADORAS_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);
//			session.removeAttribute(WebKeysAfiliados.BUSQUEDA_SITU_LABORAL);

			if (afiliado == null) {
				throw new NoSuchAfiliadoEntryException();
			}
		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof PrincipalException) {

				SessionErrors.add(renderRequest, e.getClass().getName());

				return mapping.findForward("portlet.afiliados.error");
			} else {
				throw e;
			}
		}
		return mapping.findForward("portlet.afiliados.view_afiliado_entry");
	}

}