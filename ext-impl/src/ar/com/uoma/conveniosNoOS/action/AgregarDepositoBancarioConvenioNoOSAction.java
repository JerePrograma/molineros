package ar.com.uoma.conveniosNoOS.action;

import java.math.BigDecimal;
import java.text.ParseException;
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

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarDepositoBancarioConvenioNoOSAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarDepositoBancarioConvenioNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando depo banc a acta");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		if (convenio == null) {
			convenio = new Convenio();
			session
					.setAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION,
							convenio);
		}

		List<ConvenioPago> list = convenio.getPagos();
		if (list == null) {
			list = new ArrayList<ConvenioPago>();
			convenio.setPagos(list);
		}
		ConvenioPago convenioPago = getConvenioPago(renderRequest);
		if (!existeCuota(list, convenioPago)) {
			list.add(convenioPago);
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		

		Collections.sort(list, new Comparator<ConvenioPago>() {
			public int compare(ConvenioPago o1, ConvenioPago o2) {
				if (o1.getNroCuota() < o2.getNroCuota()) {
					return -1;
				} else if (o1.getNroCuota() > o2.getNroCuota()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		logger.debug("Saliendo de agregar depo banc a acta");
		return mapping
				.findForward("portlet.tesoreria.convenios.depositos.bcrios.view");
	}

	private boolean existeCuota(List<ConvenioPago> list,
			ConvenioPago convenioPago) {
		for (ConvenioPago cp : list){
			if (cp.getNroCuota() == convenioPago.getNroCuota()){
				return true;
			}
		}
		return false;
	}

	private ConvenioPago getConvenioPago(RenderRequest renderRequest)
			throws ParseException {
		ConvenioPago convenioPago = new ConvenioPago();
		convenioPago.setTipo(ConvenioPago.Tipo.PAGO);

		int cuotaNro = ParamUtil.getInteger(renderRequest,
				"cuota_nro_cta_bcria");
		String importe = renderRequest.getParameter("capital_cta_bcria");
		String interes = renderRequest.getParameter("interes_cta_bcria");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaPagoDia = ParamUtil.getString(renderRequest,
				"fechaPagoDiaCtaBcria");
		String fechaPagoMes = ParamUtil.getString(renderRequest,
				"fechaPagoMesCtaBcria");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(renderRequest,
				"fechaPagoAnioCtaBcria");
		Date fechaPago = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);

		BigDecimal importeBigD = new BigDecimal(importe);
		convenioPago.setImporte(importeBigD);
		BigDecimal interesBigD = new BigDecimal(interes);
		convenioPago.setInteres(interesBigD);
		convenioPago.setFechaPago(fechaPago);
		convenioPago.setNroCuota(cuotaNro);
		return convenioPago;
	}

}
