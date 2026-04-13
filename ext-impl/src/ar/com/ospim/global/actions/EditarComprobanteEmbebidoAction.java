package ar.com.ospim.global.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarComprobanteEmbebidoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarConceptoEmbebidoAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		List<Comprobante> comprobantes = (ArrayList<Comprobante>) session
				.getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		Comprobante c=null;

		if (comprobantes == null) {
			comprobantes = new ArrayList<Comprobante>();
		}

		String todos = ParamUtil.getString(renderRequest, "todos");
		if (todos != null && todos.equals("todos")) {
			comprobantes.clear();
		} else {
			int pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
			String tipo_comprobante = ParamUtil.getString(renderRequest,
					"tipo_comprobante");
			String nro_comprobante = ParamUtil.getString(renderRequest,
					"nro_comprobante");
			String cuit = ParamUtil.getString(renderRequest, "cuit");
			String letra = ParamUtil.getString(renderRequest, "letra", " ");
			String sucu = ParamUtil.getString(renderRequest, "sucursal", "0");

			int i = comprobantes.indexOf(new Comprobante(pto_venta,
					tipo_comprobante, nro_comprobante, letra, Integer
							.parseInt(sucu), cuit));
			c=comprobantes.get(i);
		}
		
		session.setAttribute(WebKeysGlobal.COMPROBANTE_EN_EDICION,
				c);		

		renderRequest.setAttribute("esEditable", "true");
		
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.utils.concepto_comprobante.embebido.result.search");
	}

	public BigDecimal sumaImportesOrden(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		for (Comprobante comprobante : comprobantes) {
			suma = suma.add(comprobante.getImporteComprobante());
		}
		return suma;
	}
}
