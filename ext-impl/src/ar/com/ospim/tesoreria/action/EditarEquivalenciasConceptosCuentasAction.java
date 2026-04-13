package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarEquivalenciasConceptosCuentasAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		try {
			PortletSession portletSession = req.getPortletSession();
			int entidad = WebKeysGlobal.OSPIM;

			if (actionResponse.getNamespace().equals("_FAR_1_")) {
				entidad = WebKeysGlobal.AMTIMA;
			} else if (actionResponse.getNamespace().equals("_UOM_1_")) {
				entidad = WebKeysGlobal.UOMA;
			}

			String ddOriginal = req.getParameter("ejercicio_desde_original");
			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			if (StringUtils.isBlank(dd) || StringUtils.isBlank(hta)
					&& !StringUtils.isBlank(req.getParameter("ejercicio"))) {
				String ejercicio = req.getParameter("ejercicio");
				portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);
				if (entidad == WebKeysGlobal.AMTIMA) {
					dd = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
					hta = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);
				} else {
					dd = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
					hta = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
				}

				ddOriginal = dd;
			}

			Date desdeOriginal = format.parse(ddOriginal);
			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);
			req.setAttribute("ejercicio_desde_original", ddOriginal);

			String liquidaciones = req.getParameter("liquidaciones");
			String egresos = req.getParameter("egreso");
			String ingresos = req.getParameter("ingreso");
			String sub_egresos = req.getParameter("sub_egreso");
			String sub_ingresos = req.getParameter("sub_ingreso");

			int id_seccional = 0;
			if (entidad == WebKeysGlobal.UOMA) {
				id_seccional = ParamUtil.getInteger(req,
						"id_seccional_afiliado");
				req.setAttribute("id_seccional", id_seccional);
			}

			Concepto concepto = new Concepto();
			concepto.setValidoDesde(format.parse(dd));
			if (entidad == WebKeysGlobal.AMTIMA) {
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActualAmtima().getTime()) == 0) {
					concepto.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					concepto.setValidoHasta(format.parse(hta));
				}

			} else {
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
					concepto.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					concepto.setValidoHasta(format.parse(hta));
				}
			}

			if (StringUtils.isNotBlank(liquidaciones)
					&& liquidaciones.trim().equals("true")) {
				concepto.setLiquidaciones(true);
			}
			if (StringUtils.isNotBlank(egresos)
					&& egresos.trim().equals("true")) {
				concepto.setEgreso(true);
			}
			if (StringUtils.isNotBlank(ingresos)
					&& ingresos.trim().equals("true")) {
				concepto.setIngreso(true);
			}
			if (StringUtils.isNotBlank(sub_egresos)
					&& sub_egresos.trim().equals("true")) {
				concepto.setSubEgreso(true);
			}
			if (StringUtils.isNotBlank(sub_ingresos)
					&& sub_ingresos.trim().equals("true")) {
				concepto.setSubIngreso(true);
			}
			concepto.setId(Integer.parseInt(req.getParameter("id")));
			concepto.setDescripcion(req.getParameter("concepto"));
			concepto.setIdSeccional(id_seccional);
			int id_secuencial = ParamUtil.getInteger(req, "id_secuencial");
			// Integer.parseInt(req.getParameter("id_secuencial"));

			if (StringUtils.isNotBlank(req.getParameter("cuenta_por_numero"))
					&& !req.getParameter("cuenta_por_numero").trim()
							.equals("0")) {
				PlanCuentas pc = new PlanCuentas(Integer.parseInt(req
						.getParameter("cuenta_por_numero")));
				PlanCuentas pcPasivo = new PlanCuentas(Integer.parseInt(req
						.getParameter("cuenta_por_numero_pasivo")));
				concepto.setPlanCuentas(pc);
				concepto.setPlanCuentasPasivo(pcPasivo);
			}

			User user = PortalUtil.getUser(req);
			concepto.setIdSeccional(id_seccional);
			if (concepto.getId() != 0) {
				if (entidad == WebKeysGlobal.UOMA) {
					ConceptoServiceUtil.update(concepto, user, desdeOriginal,
							entidad, id_secuencial);
				} else {
					ConceptoServiceUtil.update(concepto, user, desdeOriginal,
							entidad);
				}
			} else {
				ConceptoServiceUtil.guardar(concepto, user, entidad);
			}
			// si no fallo piso el dde original
			req.setAttribute("ejercicio_desde_original", dd);
			req.getPortletSession().removeAttribute(
					WebKeysLiquidaciones.CONCEPTOS_EGRESOS,
					PortletSession.APPLICATION_SCOPE);
			req.getPortletSession().removeAttribute(
					WebKeysLiquidaciones.CONCEPTOS_INGRESO,
					PortletSession.APPLICATION_SCOPE);
			req.setAttribute("id", concepto.getId());
		} catch (Exception e) {
			SessionErrors.add(req, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(req)) {
			String successMessage = ParamUtil.getString(req, "successMessage");
			SessionMessages.add(req, "request_processed", successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		String ddOriginal = renderRequest
				.getParameter("ejercicio_desde_original");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");

		if (renderRequest.getAttribute("ejercicio_desde_original") != null) {
			ddOriginal = (String) renderRequest
					.getAttribute("ejercicio_desde_original");
		}
		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (StringUtils.isBlank(dd)) {
			if (entidad == WebKeysGlobal.AMTIMA) {
				dd = format.format(DateUtils.getDesdeEjercicioActualAmtima()
						.getTime());
			}
			if (entidad == WebKeysGlobal.AMTIMA) {
				dd = format.format(DateUtils.getDesdeEjercicioActualUOMA()
						.getTime());
			} else {
				dd = format.format(DateUtils.getDesdeEjercicioActual()
						.getTime());
			}

		}
		if (StringUtils.isBlank(hta)) {
			if (entidad == WebKeysGlobal.AMTIMA) {
				hta = format.format(DateUtils.getHastaEjercicioActualAmtima()
						.getTime());
			} else {
				hta = format.format(DateUtils.getHastaEjercicioActual()
						.getTime());
			}

		}
		Date hasta = format.parse(hta);
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);
		renderRequest.setAttribute("ejercicio_desde_original", ddOriginal);

		List<PlanCuentas> planCuentasNumero = TraeListasServiceUtil
				.getPlanCuentasImputables(format.parse(dd), entidad);
		List<PlanCuentas> planCuentas = new ArrayList<PlanCuentas>();
		planCuentas.addAll(planCuentasNumero);
		Collections.sort(planCuentas, new Comparator<PlanCuentas>() {

			public int compare(PlanCuentas pc1, PlanCuentas pc2) {
				return pc1.getCuenta().compareTo(pc2.getCuenta());
			}

		});
		renderRequest.setAttribute("cuentas_por_nombre", planCuentas);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS,
				planCuentasNumero);
		String id = renderRequest.getParameter("id");
		int id_seccional = ParamUtil.getInteger(renderRequest,"id_seccional");
		if(id_seccional==0){
			id_seccional=renderRequest.getAttribute("id_seccional")!=null?((Integer)renderRequest.getAttribute("id_seccional")).intValue():0;
		}
		int id_secuencial = ParamUtil
				.getInteger(renderRequest, "id_secuencial");
		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}
		if (id != null) {
			List<Concepto> conceptos = TraeListasServiceUtil
					.getConceptosValidosDentroDe(format.parse(ddOriginal),
							hasta, entidad, null).getConceptos();
			Concepto concepto = null;
			if(id_seccional>0){
				Concepto conceptoNuevo=new Concepto(Integer
						.parseInt(id));
				conceptoNuevo.setIdSeccional(id_seccional);			
				if(id_seccional>0 && Integer.parseInt(id)>0){
					id_secuencial=0;
					concepto = conceptos.get(conceptos.indexOf(conceptoNuevo));
				}else{
					conceptoNuevo.setIdSecuencial(id_secuencial);
				}
					
				
			}else{
				concepto = conceptos.get(conceptos.indexOf(new Concepto(Integer
					.parseInt(id))));//, id_secuencial)));
			}

			renderRequest.setAttribute("concepto", concepto);
		} else {
			renderRequest.setAttribute("concepto", new Concepto());
		}
		if (entidad == WebKeysGlobal.UOMA) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.uoma.equivalencia.editar_concepto_cuenta"));
		} else if (entidad == WebKeysGlobal.AMTIMA) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.farmacia.equivalencia.editar_concepto_cuenta"));
		} else {
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.editar_concepto_cuenta"));
		}

	}
}
