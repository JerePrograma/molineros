package ar.com.ospim.afiliados.empleadores.action;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.struts.PortletAction;


public class ReporteAfiliadosAnsesAction  extends PortletAction {
		public ActionForward render(ActionMapping mapping, ActionForm form,
				PortletConfig portletConfig, RenderRequest renderRequest,
				RenderResponse renderResponse) throws Exception {			
			return mapping.findForward("portlet.afiliados.reporte_afiliados_anses");			
		}
	}

	
