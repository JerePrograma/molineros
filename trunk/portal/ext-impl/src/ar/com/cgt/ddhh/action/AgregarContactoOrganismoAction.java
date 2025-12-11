package ar.com.cgt.ddhh.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.Contacto;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarContactoOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarContactoOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a acta");

		PortletSession portletSession =  renderRequest.getPortletSession();
		boolean esArea=ParamUtil.getBoolean(renderRequest, "isArea");
		
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}		
				
		List<Contacto> list =null;
		if(esArea){
			Area area=(Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			if (area == null) {
				area = new Area();			
			}
			list = area.getContactos();
			if (list == null) {
				list = new ArrayList<Contacto>();			
			}
			try {
				list.add(getContacto(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			area.setContactos(list);
			portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION, area);
			
			
		}else{
			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			if (organismo == null) {
				organismo = new Organismo();			
			}
			list = organismo.getContactos();
			if (list == null) {
				list = new ArrayList<Contacto>();			
			}
			try {
				list.add(getContacto(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			organismo.setContactos(list);
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION, organismo);			
		}		
		renderRequest.setAttribute("esArea",esArea );
		
		return mapping.findForward("portlet.cgt_ddhh.agregar_contacto");

		
		
	}

	private Contacto getContacto(RenderRequest renderRequest)
			throws ParseException,
			SystemException {
		Contacto contacto = new Contacto();		

		String cargoContacto = ParamUtil.getString(renderRequest, "id_cargo");
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String apellido = ParamUtil.getString(renderRequest, "apellido");
		String email = ParamUtil.getString(renderRequest, "email");
		String telefono = ParamUtil.getString(renderRequest, "telefono_contacto");
		String tratamiento = ParamUtil.getString(renderRequest, "tratamiento");

		contacto.setCargo(cargoContacto);
		contacto.setNombre(nombre);
		contacto.setApellido(apellido);
		contacto.setEmail(email);
		contacto.setTelefono(telefono);
		contacto.setTratamiento(tratamiento);
		return contacto;
	}

}
