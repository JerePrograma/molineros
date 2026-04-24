package ar.com.ospim.tesoreria.service;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Inspector;

public class InspectorServiceUtil {
	@SuppressWarnings("unchecked")
	public static List<Inspector> getInspectores(PortletRequest portletRequest)
			throws Exception {
		List<Inspector> inspectores = (List<Inspector>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysTesoreria.INSPECTORES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (inspectores == null) {
			inspectores = InspectorServiceImpl.getInstance().getInspectores();
			portletRequest.getPortletSession().setAttribute(
					WebKeysTesoreria.INSPECTORES_EN_SESSION, inspectores,
					PortletSession.APPLICATION_SCOPE);
		}
		return inspectores;
	}
}
