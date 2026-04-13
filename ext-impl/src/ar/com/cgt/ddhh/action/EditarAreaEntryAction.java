package ar.com.cgt.ddhh.action;

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

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.cgt.ddhh.services.OrganismoServiceImpl;
import ar.com.cgt.ddhh.services.OrganismoServiceUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarActasEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class EditarAreaEntryAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarAreaEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		int id_organismo = ParamUtil.getInteger(actionRequest, "id_organismo");
		PortletSession portletSession = actionRequest.getPortletSession();
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Organismo organismo = OrganismoServiceImpl.getInstance()
						.getOrganismo(id_organismo);
				Area area=updateAreaEntry(actionRequest,cmd);
				portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION,
						area);
				portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,
						organismo);
				setForward(actionRequest, "portlet.cgt_ddhh.editar_area_entry");
			}
		} catch (Exception e) {
			logger.debug("Error al guardar area", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	private Area updateAreaEntry(ActionRequest actionRequest,
			String cmd) throws Exception {

		PortletSession portletSession = actionRequest.getPortletSession();

		Area area = (Area) portletSession
				.getAttribute(WebKeysCGT.AREA_EN_EDICION);

		if (area == null) {
			area = new Area();
		}
		String nombre = ParamUtil.getString(actionRequest, "nombre_area");	
		String telefono = ParamUtil.getString(actionRequest, "telefono_area");
		String web = ParamUtil.getString(actionRequest, "web_area");
		String observaciones = ParamUtil.getString(actionRequest,"observaciones_area");
		String email=ParamUtil.getString(actionRequest,"email_organismo");
		int id_organismo = ParamUtil.getInteger(actionRequest, "id_organismo");
		
		area.setId_organismo(id_organismo);
		area.setNombre(nombre);		
		area.setTelefono(telefono);
		area.setWeb(web);
		area.setObservaciones(observaciones);
		area.setEmail(email);
		
		int provincia=ParamUtil.getInteger(actionRequest, "provincia");
		int pais=ParamUtil.getInteger(actionRequest, "pais");
		int localidad=ParamUtil.getInteger(actionRequest, "localidad");
		String cod_postal=ParamUtil.getString(actionRequest, "cod_postal");
		String numero= ParamUtil.getString(actionRequest, "numero");
		String piso= ParamUtil.getString(actionRequest, "piso");
		String departamento=ParamUtil.getString(actionRequest, "departamento");
		String calle=ParamUtil.getString(actionRequest, "calle");
		
		Domicilio domicilio=new Domicilio();
		
		domicilio.setPaisId(pais);
		domicilio.setProvinciaId(provincia);
		domicilio.setLocalidadId(localidad);
		domicilio.setPostal_codi(cod_postal);
		domicilio.setNumero(numero);
		domicilio.setPiso(piso);
		domicilio.setDepto(departamento);
		domicilio.setCalle(calle);
		
		area.setDomicilio(domicilio);
		
		if (area != null) {
			portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION,
					area);
		}

		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			OrganismoServiceUtil.saveArea(area, user);
		} else {
			OrganismoServiceUtil.updateArea(area, user);
		}
		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
		// session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return area;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		PortletSession portletSession = renderRequest.getPortletSession();

		int id_organismo = ParamUtil.getInteger(renderRequest, "id_organismo");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getPaises(renderRequest);
		
		
		try {
			Organismo organismo = OrganismoServiceImpl.getInstance().getOrganismo(id_organismo);	
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,organismo);			

		} catch (Exception e) {
			logger.debug("Error al guardar area", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		
		if(cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)){
			portletSession.removeAttribute(WebKeysCGT.AREA_EN_EDICION);
		}


		return mapping.findForward(getForward(renderRequest,
				"portlet.cgt_ddhh.editar_area_entry"));

	}
}