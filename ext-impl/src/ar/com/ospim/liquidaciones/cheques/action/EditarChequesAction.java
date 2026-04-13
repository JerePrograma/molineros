package ar.com.ospim.liquidaciones.cheques.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarChequesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Martin Moreyra
 * 
 */
public class EditarChequesAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}


		Cheque cheque = getChequeFromRequest(actionRequest);
		User user = PortalUtil.getUser(actionRequest);
		try {
			ChequeServiceUtil.save(cheque, user, entidad);
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute(WebKeysLiquidaciones.CHEQUE_EN_EDICION,
					cheque);
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
			actionRequest.setAttribute(WebKeysLiquidaciones.CHEQUE_A_IMPRIMIR,
					cheque);
		}
	}

	private Cheque getChequeFromRequest(ActionRequest actionRequest)
			throws ParseException {
		String cuit = ParamUtil.getString(actionRequest, "cuit");
		String importeStr = ParamUtil.getString(actionRequest, "importe");
		BigDecimal importe = new BigDecimal(importeStr);
		String aNombre = ParamUtil.getString(actionRequest, "aNombreDe");
		BigDecimal nroCheque = new BigDecimal(ParamUtil.getString(actionRequest, "numero"));

		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		fechaMes = String.valueOf(Integer.valueOf(fechaMes) + 1);
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");

		String concepto = ParamUtil.getString(actionRequest, "concepto");

		Cheque cheque = new Cheque();
		String prestador = ParamUtil
				.getString(actionRequest, "prestador", null);
		if (prestador != null && prestador.equals("prestador")) {
			cheque.setPrestador(true);
		}

		int ctaBcria = ParamUtil.getInteger(actionRequest, "cta_bcria");

		cheque.setCuit(cuit);
		cheque.setImporte(importe);
		cheque.setANombreDe(aNombre);
		cheque.setNumero(nroCheque);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		cheque.setFecha(format.parse(fechaDia + "-" + fechaMes + "-"
				+ fechaAnio));
		cheque.setConcepto(concepto);
		List<CuentaBancaria> ctas = TraeListasServiceUtil.getCtasBcrias(actionRequest);
		CuentaBancaria cuentaBancaria = ctas.get(ctas.indexOf(new CuentaBancaria(ctaBcria)));
		cheque.setCuentaBancaria(cuentaBancaria);
		cheque.setEstado(TraeListasServiceUtil.getEstadoChequeEmitido(actionRequest));
		cheque.setBanco(cuentaBancaria.getBanco());
		cheque.setDebitoCredito(Cheque.Tipo.DEBITO);
		return cheque;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		return mapping.findForward("portlet.liquidaciones.alta_cheque_entry");
	}
}