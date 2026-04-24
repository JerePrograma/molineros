package ar.com.cgt.ddhh.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
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
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Pais;
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
public class EditarOrganismoEntryAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarOrganismoEntryAction.class);	

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);		
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Organismo organismo = updateOrganismoEntry(actionRequest, cmd);
				setForward(actionRequest, "portlet.cgt_ddhh.editar_organismo_entry");
			}
		} catch (Exception e) {
			logger.debug("Error al guardar acta", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	private Organismo updateOrganismoEntry(ActionRequest actionRequest, String cmd)
			throws Exception {

		PortletSession portletSession =  actionRequest.getPortletSession();
		
		Organismo organismo = (Organismo) portletSession
				.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);

		if (organismo == null) {
			organismo = new Organismo();
		}
		String nombre=ParamUtil.getString(actionRequest,"nombre_organismo");
		String sigla=ParamUtil.getString(actionRequest,"sigla");
		String ambito=ParamUtil.getString(actionRequest,"ambito");
		String telefono=ParamUtil.getString(actionRequest,"telefono");
		String web=ParamUtil.getString(actionRequest,"web");
		String observaciones=ParamUtil.getString(actionRequest,"observaciones");
		String orbita=ParamUtil.getString(actionRequest,"orbita");
		String email=ParamUtil.getString(actionRequest,"email_organismo");
		int id_organismo=ParamUtil.getInteger(actionRequest, "id_organismo");
		
		organismo.setNombre(nombre);
		organismo.setSigla(sigla);
		organismo.setAmbito(ambito);
		organismo.setTelefono(telefono);
		organismo.setWeb(web);
		organismo.setObservaciones(observaciones);
		organismo.setId_organismo(id_organismo);
		organismo.setOrbita(orbita);
		organismo.setEmail(email);
		
		int pais=ParamUtil.getInteger(actionRequest, "pais");
		int provincia=ParamUtil.getInteger(actionRequest, "provincia");
		int localidad=ParamUtil.getInteger(actionRequest, "localidad");
		String cod_postal=ParamUtil.getString(actionRequest, "cod_postal");
		String numero= ParamUtil.getString(actionRequest, "numero");
		String piso= ParamUtil.getString(actionRequest, "piso");
		String departamento=ParamUtil.getString(actionRequest, "departamento");
		String calle=ParamUtil.getString(actionRequest, "calle");
		
		
		Domicilio domicilio=new Domicilio();
		
		domicilio.setProvinciaId(provincia);
		domicilio.setLocalidadId(localidad);
		domicilio.setPostal_codi(cod_postal);
		domicilio.setNumero(numero);
		domicilio.setPiso(piso);
		domicilio.setDepto(departamento);
		domicilio.setCalle(calle);
		domicilio.setPais(new Pais(pais));
		
		organismo.setDomicilio(domicilio);

		if (organismo != null) {
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION, organismo);
		}
		
		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			OrganismoServiceUtil.save(organismo, user);
		} else {
			OrganismoServiceUtil.update(organismo, user);
		}
		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");
		SessionMessages.add(actionRequest, "request_processed",
				successMessage);
		//session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return organismo;
	}

	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta
					
		PortletSession portletSession =  renderRequest.getPortletSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);	
		if(cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)){
			portletSession.removeAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
		}
		
		TraeListasServiceUtil.getPaises(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getLocalidades(renderRequest);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.cgt_ddhh.editar_organismo_entry"));

	}
}