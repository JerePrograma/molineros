package ar.com.ospim.global.actions;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.DetalleDiscapacidad;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarDetalleDiscapacidadAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba registro de Detalle discapacidad
 * 
 * @author Carlos Rivas
 * @modif SVA
 */
public class GrabarDetalleDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarDetalleDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.utils.detalle_discapacidad.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {				

		User user = PortalUtil.getUser(renderRequest);
		
		TraeListasServiceUtil.getListadoCieDiez(renderRequest);
		
		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		String diagnostico = ParamUtil.getString(renderRequest, "diagnostico");		
		boolean dependencia = Boolean.valueOf(ParamUtil.getString(renderRequest, "dependencia"));
		String telefono_contacto = ParamUtil.getString(renderRequest, "telefono_contacto");
		String cie_diez = ParamUtil.getString(renderRequest, "cie_diez");
		String tiposDiscapacidades = ParamUtil.getString(renderRequest, "tiposDiscSel");
		
		try {
			DetalleDiscapacidad detalleDiscapacidad = new DetalleDiscapacidad(cuil_titular, inte, diagnostico, dependencia, telefono_contacto, cie_diez, tiposDiscapacidades); 											
			
			EditarAfiliadoServiceUtil.actualizaDetalleDiscapacidad(detalleDiscapacidad, user);

		} catch (Exception e) {
			_log.error(e);
//			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}
		return mapping.findForward("portlet.utils.detalle_discapacidad.result");
	
	}

}