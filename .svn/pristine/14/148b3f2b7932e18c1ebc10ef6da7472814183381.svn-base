package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarPeriodoManualActaAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(SacarPeriodoManualActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		int id = ParamUtil.getInteger(renderRequest, "id");
		String periodo = ParamUtil.getString(renderRequest, "periodo");
		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		if(null!=cuil_titular && cuil_titular.trim().length()>0){
			id=-1;
		}

		try {
			renderRequest.setAttribute("mostrar_periodo", periodo);
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

			periodo = "01-" + periodo;
			Date periodoDate = format.parse(periodo);

			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris == null) {
				peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
			}

			Iterator<ActaPeriodoDeudaEmpresa> iterator = peris.iterator();
			
			while (iterator.hasNext()) {
				boolean coincide=false;
				ActaPeriodoDeudaEmpresa peri = iterator.next();
				coincide=peri.getCuil().equals(cuil_titular)&&peri.getPeriodo().equals(periodoDate);
				if (peri.getDetalle() != null) {
					Iterator<Detalle> iterator2 = peri.getDetalle().iterator();
					while (iterator2.hasNext()) {
						Detalle det = iterator2.next();
						if (id == det.getId()|| coincide) {
							if (det.getId() <= 0) {
								iterator2.remove();
							} else {
								det.setBorradoLogico(true);
							}
						}
					}
					if (peri.getDetalle().size() == 0) {
						iterator.remove();
					} else if (borrarLogico(peri)) {
						peri.setBorradoLogico(true);
					}
				}
			}
			BigDecimal subtotalDeActa = BigDecimal.ZERO;
			BigDecimal interesDeActa = BigDecimal.ZERO;
			List<ActaPeriodoDeudaEmpresa> periodos = new ArrayList<ActaPeriodoDeudaEmpresa>();
			for (ActaPeriodoDeudaEmpresa actaPeri : peris) {
				if (actaPeri.getPeriodo().equals(periodoDate)) {
					periodos.add(actaPeri);
				}
				subtotalDeActa = subtotalDeActa.add(actaPeri.getSubtotal());
				interesDeActa = interesDeActa.add(actaPeri.getInteres());
			}
			renderRequest.setAttribute(WebKeysTesoreria.ACTA_PERIODOS_SUBTOTAL,
					subtotalDeActa);
			renderRequest.setAttribute(WebKeysTesoreria.ACTA_PERIODOS_INTERES,
					interesDeActa);
			renderRequest.setAttribute(WebKeysTesoreria.ACTAS_PERIODOS,
					periodos);

		} catch (Exception e) {
			_log.error("Error al agregar periodo", e);
			return null;
		}

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.actas.editar.periodos.view");
	}

	private boolean borrarLogico(ActaPeriodoDeudaEmpresa peri) {
		if (peri.getDetalle() != null) {
			int borrados = 0;
			for (Detalle det : peri.getDetalle()) {
				if (det.isBorradoLogico()) {
					borrados++;
				}
			}
			return borrados == peri.getDetalle().size();
		}
		return true;
	}
}
