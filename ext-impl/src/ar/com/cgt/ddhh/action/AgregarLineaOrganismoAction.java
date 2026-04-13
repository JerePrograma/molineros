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
import ar.com.cgt.ddhh.beans.LineaTrabajo;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarLineaOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarLineaOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando linea a contacto");

		PortletSession portletSession = renderRequest.getPortletSession();
		List<LineaTrabajo> list =null;

		boolean esArea = ParamUtil.getBoolean(renderRequest, "isArea");

		if (esArea) {
			Area area = (Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			if (area == null) {
				area = new Area();
			}
			list = area.getLineasTrabajo();
			if (list == null) {
				list = new ArrayList<LineaTrabajo>();
			}
			try {
				list.add(getLineaTrabajo(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			area.setLineasTrabajo(list);
			portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION, area);

		} else {

			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			if (organismo == null) {
				organismo = new Organismo();
			}

			list = organismo.getLineasTrabajo();
			if (list == null) {
				list = new ArrayList<LineaTrabajo>();
			}
			try {
				list.add(getLineaTrabajo(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			organismo.setLineasTrabajo(list);

			if (renderRequest.getParameter("esEdicion") != null) {
				renderRequest.setAttribute("esEdicion", "esEdicion");
			}
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,
					organismo);
		}
		renderRequest.setAttribute("esArea",esArea );
		logger.debug("Saliendo de agregar contacto a organismo");
		return mapping.findForward("portlet.cgt_ddhh.agregar_linea");
	}

	private LineaTrabajo getLineaTrabajo(RenderRequest renderRequest)
			throws ParseException, SystemException {

		String id_linea = ParamUtil.getString(renderRequest, "id_linea");
		String tipo_linea = ParamUtil.getString(renderRequest, "tipo_linea");
		LineaTrabajo linea = new LineaTrabajo(tipo_linea, id_linea);

		return linea;
	}

}
