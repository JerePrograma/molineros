package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class BorrarProfesionEspSubEspAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarProfesionEspSubEspAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idPrestProfesion = ParamUtil.getInteger(renderRequest, "idPrestProf");
		
		ProfesionPrestador prof = new ProfesionPrestador();
		prof.setIdPrestProf(idPrestProfesion);
		
		_log.debug("Borrando profesion id: " + idPrestProfesion);
		
		List<ProfesionPrestador> listaProfEspSubEspPrestador = (ArrayList<ProfesionPrestador>) session.getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
		
		int pos = listaProfEspSubEspPrestador.indexOf(prof);
//		reemplazo por el objeto de la lista
		prof = listaProfEspSubEspPrestador.get(pos);
		if(prof.getEstado()==null){ // esta prof-esp-sub esta en BD
			prof.setEstado(ProfesionPrestador.ESTADOS.BAJA);
		}else{
			listaProfEspSubEspPrestador.remove(pos);
		}	
		session.removeAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, listaProfEspSubEspPrestador);

		return mapping.findForward("portlet.liquidaciones.profesion.prestador");
	}
		
}