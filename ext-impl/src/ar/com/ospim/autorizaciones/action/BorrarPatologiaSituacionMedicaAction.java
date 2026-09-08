package ar.com.ospim.autorizaciones.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.autorizaciones.beans.PrestacionesEquipoInterdisciplinario;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;


public class BorrarPatologiaSituacionMedicaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarPrestacionEquipoInterdisciplinarioAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
	//	HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		//int idPrestacion= ParamUtil.getInteger(renderRequest, "id_registro_sitmed");
				
		
		return mapping.findForward("portlet.autorizaciones.equipointerdisciplinario.prestacion_equipointer");
		                            
	}
	
}
