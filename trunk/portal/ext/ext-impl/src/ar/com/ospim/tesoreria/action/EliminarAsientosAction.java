package ar.com.ospim.tesoreria.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

public class EliminarAsientosAction extends BusquedaAsientosBaseAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		try {
			String id = renderRequest.getParameter("id");
			Asiento asiento = new Asiento(Integer.parseInt(id));
			asiento = AsientoServiceUtil.buscarAsiento(asiento, entidad);
			if (DateUtils.compararFechasTruncarEnDia(
					ContabilidadServiceUtil.getFechaCierreAsientos(entidad),
					asiento.getFecha()) > 0) {
				throw new FechaMenorACierreContableException();
			}

			AsientoServiceUtil.eliminar(asiento, entidad);
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
		buscarAsientos(renderRequest, entidad);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.asientos_search_result"));
	}
}
