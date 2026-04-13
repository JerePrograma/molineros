package ar.com.ospim.liquidaciones.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.DuplicatePrestadorExternoIdException;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.services.PrestadorExternoServiceUtil;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
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
 * <a href="GrabarPrestadorExternoAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba registro de Catastro
 * 
 * @author Carlos Rivas
 * 
 */
public class GrabarPrestadorExternoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarPrestadorExternoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.prestador_externo.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, "accionOriginal");
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updatePrestadorEntry(renderRequest, cmd);
			}
		} catch (DuplicatePrestadorExternoIdException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());	
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			 String successMessage = ParamUtil.getString(renderRequest,
			 "successMessage");
			 SessionMessages.add(renderRequest, "request_processed",
			 successMessage);
		}
		return mapping.findForward("portlet.liquidaciones.prestador_externo.result");
	}
	
	private void updatePrestadorEntry(RenderRequest actionRequest, String cmd)
			throws PortalException, SystemException,
			DuplicatePrestadorIdException, DuplicatePrestadorExternoIdException {
		
		String cuit = ParamUtil.getString(actionRequest, "cuit");
		String desc = ParamUtil.getString(actionRequest, "desc");
		int iva = ParamUtil.getInteger(actionRequest, "iva");
		String matriculaTipo = ParamUtil.getString(actionRequest, "mat_tipo");
		int matriculaNro = ParamUtil.getInteger(actionRequest, "mat_numero");
		int matriculaProvincia = ParamUtil.getInteger(actionRequest,
				"mat_provincia");
		String matriculaCategoria = ParamUtil.getString(actionRequest,
				"mat_categoria");		

		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			int idPrestador = PrestadorExternoServiceUtil.save(cuit, desc, iva, matriculaTipo,
					matriculaNro, matriculaProvincia, matriculaCategoria, user);
			actionRequest.setAttribute("prestador_id", String
					.valueOf(idPrestador));
		} else {
			int id_prestador_ext = ParamUtil.getInteger(actionRequest,
					"id_prestador_ext");
			PrestadorExternoServiceUtil.update(id_prestador_ext, cuit, desc,
					iva, matriculaTipo, matriculaNro,
					matriculaProvincia, matriculaCategoria, user);
		}
	}

}