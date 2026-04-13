package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarPeriodoManualActaAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(AgregarPeriodoManualActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");
		
		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}


		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		String cuil = ParamUtil.getString(renderRequest, "cuil");
		String remuneracion_declarada = ParamUtil.getString(renderRequest,
				"remuneracion_declarada", "0");
		String calculado = ParamUtil.getString(renderRequest, "calculado", "0");
		String subtotal = ParamUtil.getString(renderRequest, "subtotal", "0");
		String interes = ParamUtil.getString(renderRequest, "interes", "0");
		String pagado = ParamUtil.getString(renderRequest, "pagado", "0");
		String apellido = ParamUtil.getString(renderRequest, "apellido");
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String periodo = ParamUtil.getString(renderRequest, "periodo");

		try {
			renderRequest.setAttribute("mostrar_periodo", periodo);
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			periodo = periodo.replaceAll("_", "-");
			periodo = "01-" + periodo;
			Date periodoDate = format.parse(periodo);

			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris == null) {
				peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
				acta.setPeriodos(peris);
			}
			ActaPeriodoDeudaEmpresa peri = new ActaPeriodoDeudaEmpresa();
			peri.setCuil(cuil);
			peri.setRemuneracionDeclarada(new BigDecimal(remuneracion_declarada));
			peri.setCalculado(new BigDecimal(calculado));
			peri.setApellido(apellido);
			peri.setNombre(nombre);
			peri.setPeriodo(periodoDate);

			List<Detalle> pagos = new ArrayList<Detalle>();
			String fechaPagoDia = ParamUtil.getString(renderRequest,
					"fechaPagoDia");
			String fechaPagoMes = ParamUtil.getString(renderRequest,
					"fechaPagoMes");
			fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
			String fechaPagoAnio = ParamUtil.getString(renderRequest,
					"fechaPagoAnio");
			Date fechaPago = format.parse(fechaPagoDia + "-" + fechaPagoMes
					+ "-" + fechaPagoAnio);

			BigDecimal montoPagado = null;
			if (StringUtils.checkNotEmpty(pagado)
					&& !(new BigDecimal(pagado).equals(BigDecimal.ZERO))) {
				montoPagado = new BigDecimal(pagado);
			} else {
				fechaPago = null;
			}

			int id = getProximoId(peris);
			Detalle detalle = new Detalle(fechaPago, montoPagado,
					new BigDecimal(subtotal), new BigDecimal(interes), 0);
			detalle.setId(id);
			detalle.setAgregadoManual(true);
			pagos.add(detalle);
			peri.setDetalle(pagos);
			peris.add(peri);

			Collections.sort(peris, new Comparator<ActaPeriodoDeudaEmpresa>() {
				public int compare(ActaPeriodoDeudaEmpresa o1,
						ActaPeriodoDeudaEmpresa o2) {
					int compareTo = o1.getPeriodo().compareTo(o2.getPeriodo());
					if (compareTo == 0) {
						compareTo = o1.getCuil().compareTo(o2.getCuil());
					}
					return compareTo;
				}
			});

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

			renderRequest.setAttribute(WebKeysTesoreria.ACTAS_PERIODOS,
					periodos);
			renderRequest.setAttribute(WebKeysTesoreria.ACTA_PERIODOS_SUBTOTAL,
					subtotalDeActa);
			renderRequest.setAttribute(WebKeysTesoreria.ACTA_PERIODOS_INTERES,
					interesDeActa);

		} catch (Exception e) {
			_log.error("Error al agregar periodo", e);
			return null;
		}

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		if(entidad==WebKeysGlobal.UOMA){
			return mapping.findForward("portlet.uoma.actas.editar.periodos.view");
		}else{
			return mapping.findForward("portlet.tesoreria.actas.editar.periodos.view");
		}
	}

	private int getProximoId(List<ActaPeriodoDeudaEmpresa> peris) {
		int id = 0;
		for (ActaPeriodoDeudaEmpresa peri : peris) {
			if (peri.getDetalle() != null) {
				for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
					if (det.getId() <= 0 && det.getId() < id) {
						id = det.getId();
					}
				}
			}
		}
		return --id;
	}
}
