package ar.com.ospim.tesoreria.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.actas.action.ActasBaseAction;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class EliminarEquivalenciasPrestacionesConceptosAction extends
		ActasBaseAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String id = renderRequest.getParameter("id");

		try {
			PrestacionConcepto pc = new PrestacionConcepto();
			pc.setPrestacion(new Prestacion(Integer.parseInt(id), ""));
			User user = PortalUtil.getUser(renderRequest);
			ConceptoServiceUtil.eliminar(pc, user);
		} catch (ConceptoUtilizadoException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.prestaciones_conceptos"));
	}
}
