package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.DetalleActaInspectores;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarDetalleActaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregarDetalleActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		String accionOriginal = renderRequest.getParameter("accionOriginal");
		if (accionOriginal != null) {
			renderRequest.setAttribute("accionOriginal", accionOriginal);
		}
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		List<Acta.DetalleActaInspectores> lista = acta.getDetallesActas();
		if (lista == null) {
			lista = new ArrayList<Acta.DetalleActaInspectores>();
			acta.setDetallesActas(lista);
		}
		String desdeD = renderRequest.getParameter("fechaDesdeDia");
		String desdeM = renderRequest.getParameter("fechaDesdeMes");
		desdeM = String.valueOf(Integer.valueOf(desdeM) + 1);
		String desdeA = renderRequest.getParameter("fechaDesdeAnio");

		String hastaD = renderRequest.getParameter("fechaHastaDia");
		String hastaM = renderRequest.getParameter("fechaHastaMes");
		hastaM = String.valueOf(Integer.valueOf(hastaM) + 1);
		String hastaA = renderRequest.getParameter("fechaHastaAnio");

		String capital = renderRequest.getParameter("capital");
		String intereses = renderRequest.getParameter("intereses");
		DetalleActaInspectores detalle = new DetalleActaInspectores();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		detalle.setDesde(format.parse(desdeD + "-" + desdeM + "-" + desdeA));
		detalle.setHasta(format.parse(hastaD + "-" + hastaM + "-" + hastaA));
		detalle.setCapital(new BigDecimal(capital));
		detalle.setInteres(new BigDecimal(intereses));

		// seteo un id negativo para poder diferenciar dos detalles diferentes
		// sin haber guardado en la base de datos
		int newId = 0;
		for (Acta.DetalleActaInspectores det : lista) {
			if (det.getId() < newId) {
				newId = det.getId();
			}
		}
		newId--;
		detalle.setId(newId);
		lista.add(detalle);

		acta.setCapital(acta.getCapitalFromDetalle());
		acta.setInteres(acta.getInteresFromDetalle());
		
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.actas.detalle.acta.search.result");
	}

}
