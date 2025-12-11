package ar.com.cgt.ddhh.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ar.com.cgt.ddhh.beans.Comentario;
import ar.com.cgt.ddhh.beans.LineaTrabajo;
import ar.com.cgt.ddhh.beans.Organismo;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarComentarioOrganismoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarComentarioOrganismoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando comentario a organismo");

		PortletSession portletSession = renderRequest.getPortletSession();

		boolean esArea = ParamUtil.getBoolean(renderRequest, "isArea");
		List<Comentario> list=null;

		if (esArea) {
			Area area = (Area) portletSession
					.getAttribute(WebKeysCGT.AREA_EN_EDICION);
			if (area == null) {
				area = new Area();
			}
			list = area.getComentario();
			if (list == null) {
				list = new ArrayList<Comentario>();
			}
			try {
				list.add(getComentario(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			area.setComentario(list);
			portletSession.setAttribute(WebKeysCGT.AREA_EN_EDICION, area);

		} else {
			Organismo organismo = (Organismo) portletSession
					.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);
			if (organismo == null) {
				organismo = new Organismo();
			}

			list = organismo.getComentario();
			if (list == null) {
				list = new ArrayList<Comentario>();
			}
			try {
				list.add(getComentario(renderRequest));
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			organismo.setComentario(list);
			portletSession.setAttribute(WebKeysCGT.ORGANISMO_EN_EDICION,
					organismo);
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		renderRequest.setAttribute("esArea",esArea );
		logger.debug("Saliendo de agregar comentario a organismo");
		return mapping.findForward("portlet.cgt_ddhh.agregar_comentario");
	}

	private Comentario getComentario(RenderRequest renderRequest)
			throws ParseException, SystemException {

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(renderRequest, "dia");
		String fechaMes = ParamUtil.getString(renderRequest, "mes");
		String fechaAnio = ParamUtil.getString(renderRequest, "anio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		String comentario_string = ParamUtil.getString(renderRequest,
				"comentario");
		Comentario comentario = new Comentario(fecha, comentario_string);

		return comentario;
	}

}
