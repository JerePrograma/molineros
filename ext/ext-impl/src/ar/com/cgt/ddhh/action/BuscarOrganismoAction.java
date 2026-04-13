package ar.com.cgt.ddhh.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.cgt.ddhh.services.OrganismoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarOrganismoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
		
		String nombre=ParamUtil.getString(renderRequest, "nombre");
		String ambito = ParamUtil.getString(renderRequest,"ambito");
		String linea = ParamUtil.getString(renderRequest,"linea");
		String sigla = ParamUtil.getString(renderRequest,"sigla");
		String orbita = ParamUtil.getString(renderRequest,"orbita");
		int id_organismo= ParamUtil.getInteger(renderRequest, "id_organismo");		
				
		if(id_organismo!=0){ //Para la edición
			TraeListasServiceUtil.getPaises(renderRequest);
			TraeListasServiceUtil.getProvincias(renderRequest);
			TraeListasServiceUtil.getLocalidades(renderRequest);
			Organismo organismo=OrganismoServiceUtil.getOrganismo(id_organismo);
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,organismo);			
			renderRequest.setAttribute("cmd", Constants.UPDATE);
			
			return mapping.findForward("portlet.cgt_ddhh.editar_organismo_entry");
		}else{ //Búsqueda organismos
			List<Organismo> organismos=null;
			organismos = OrganismoServiceUtil.getOrganismos(nombre,ambito,linea, sigla, orbita);
			renderRequest.getPortletSession().removeAttribute(WebKeysCGT.BUSQUEDA_ORGANISMOS,PortletSession.APPLICATION_SCOPE);
			renderRequest.getPortletSession().setAttribute(WebKeysCGT.BUSQUEDA_ORGANISMOS, organismos,
					PortletSession.APPLICATION_SCOPE);
			return mapping
					.findForward("portlet.cgt_ddhh.organismos_search_result");
		}
		

	}

	
}
