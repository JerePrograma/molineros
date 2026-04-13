package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.beans.ConceptoAfip;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarEquivalenciasConceptosAfipAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		try {

			ConceptoAfip cAfip = new ConceptoAfip();
			String id = req.getParameter("id");
			if (StringUtils.isNotBlank(id)) {
				cAfip.setId(Integer.parseInt(id));
			}

			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");
			if (StringUtils.isBlank(dd) || StringUtils.isBlank(hta)
					&& !StringUtils.isBlank(req.getParameter("ejercicio"))) {
				String ejercicio = req.getParameter("ejercicio");
				dd = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
				hta = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
			}
			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			String descripcion = req.getParameter("descripcion");
			String codigoConcepto = req.getParameter("codigoConcepto");
			String codigoContraConcepto = req
					.getParameter("codigoContraConcepto");
			String concepto_id = req.getParameter("concepto_id");
			String liquidable = req.getParameter("liquidable");
			String debitoCredito = req.getParameter("debitoCredito");

			if (StringUtils.isNotBlank(liquidable)
					&& liquidable.trim().equals("true")) {
				cAfip.setLiquidable(true);
			}
			cAfip.setDescripcion(descripcion);
			cAfip.setCodigoConcepto(codigoConcepto);
			cAfip.setCodigoContraConcepto(codigoContraConcepto);
			cAfip.setConcepto(new Concepto(Integer.parseInt(concepto_id)));
			cAfip.setDebitoCredito(debitoCredito);
			cAfip.setValidoDesde(format.parse(dd));

			if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
					DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
				cAfip.setValidoHasta(DateUtils.getInfinito().getTime());
			} else {
				cAfip.setValidoHasta(format.parse(hta));
			}

			User user = PortalUtil.getUser(req);
			if (cAfip.getId() != 0) {
				ConceptoServiceUtil.update(cAfip, user);
			} else {
				ConceptoServiceUtil.guardar(cAfip, user);
			}
			req.setAttribute("id", cAfip.getId());
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
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}


		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");

		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (StringUtils.isBlank(dd)) {
			dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
		}
		if (StringUtils.isBlank(hta)) {
			hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
		}
		Date desde = format.parse(dd);
		Date hasta = format.parse(hta);
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);

		List<Concepto> conceptos = TraeListasServiceUtil.getConceptos(desde, entidad);
		renderRequest.setAttribute("conceptos", conceptos);

		String id = renderRequest.getParameter("id");
		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}
		if (id != null) {
			List<ConceptoAfip> conceptosAfip = ConceptoServiceUtil
					.getConceptosAfip(desde, hasta);
			ConceptoAfip concepto = conceptosAfip.get(conceptosAfip
					.indexOf(new ConceptoAfip(Integer.parseInt(id))));
			renderRequest.setAttribute("conceptoAfip", concepto);
		} else {
			renderRequest.setAttribute("conceptoAfip", new ConceptoAfip());
		}

		return mapping
				.findForward(getForward(renderRequest,
						"portlet.tesoreria.equivalencia.editar_equivalencias_conceptos_afip"));

	}

}
