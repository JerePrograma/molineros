package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.tesoreria.services.CanjeChequePropioServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarCanjeChequesPropiosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;;
		}

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");

		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		String chequeNuevo = ParamUtil.getString(req, "cheque_nuevo");
		String chequeCanjeado = ParamUtil.getString(req, "cheque_canjeado");
		String op_generada = ParamUtil.getString(req, "op_generada");

		try {
			Date fechaIni = null;
			if (StringUtils.checkNotEmpty(fechaDesdeDia)
					&& StringUtils.checkNotEmpty(fechaDesdeMes)
					&& StringUtils.checkNotEmpty(fechaDesdeAnio)) {
				fechaDesdeMes = String
						.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
						+ "-" + fechaDesdeAnio);
			}
			Date fechaFin = null;
			if (StringUtils.checkNotEmpty(fechaHastaDia)
					&& StringUtils.checkNotEmpty(fechaHastaMes)
					&& StringUtils.checkNotEmpty(fechaHastaAnio)) {
				fechaHastaMes = String
						.valueOf(Integer.valueOf(fechaHastaMes) + 1);
				fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
						+ "-" + fechaHastaAnio);
			}
			if (StringUtils.checkEmpty(chequeNuevo)) {
				chequeNuevo = null;
			}

			if (StringUtils.checkEmpty(chequeCanjeado)) {
				chequeCanjeado = null;
			}
			Integer opGeneradaInteger = null;
			if (StringUtils.checkNotEmpty(op_generada)) {
				opGeneradaInteger = Integer.valueOf(op_generada);
			}
			BigDecimal chequeNuevoBD = null;
			BigDecimal chequeCanjeadoBD = null;

			if (StringUtils.checkNotEmpty(chequeNuevo)) {
				chequeNuevoBD = new BigDecimal(chequeNuevo);
			}
			if (StringUtils.checkNotEmpty(chequeCanjeado)) {
				chequeCanjeadoBD = new BigDecimal(chequeCanjeado);
			}
			List<CanjeChequePropio> buscar = CanjeChequePropioServiceUtil
					.buscar(fechaIni, fechaFin, chequeNuevoBD,
							chequeCanjeadoBD, opGeneradaInteger, entidad);
			req.setAttribute(WebKeysTesoreria.CANJE_CHEQUES_RESULT, buscar);
		} catch (Exception e) {

		}

		return mapping.findForward(getForward(req,
				"portlet.tesoreria.buscar.canje.cheques.propios.result"));
	}
}
