package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.CanjeChequesTotalesDiferentesException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio.ChequeACanjear;
import ar.com.ospim.tesoreria.services.CanjeChequePropioServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CanjeChequesPropiosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}


		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		if (SessionErrors.isEmpty(renderRequest)) {

			session.removeAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
			int id = getCanjeId(renderRequest);
			CanjeChequePropio canjeChequePropio = null;
			if (id != 0) {
				canjeChequePropio = CanjeChequePropioServiceUtil.get(id, entidad);
			}
			if (canjeChequePropio == null) {
				canjeChequePropio = new CanjeChequePropio();
			}
			session.setAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION,
					canjeChequePropio);
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.canje.cheques.propios"));
	}

	private int getCanjeId(RenderRequest renderRequest) {
		String idStr = renderRequest.getParameter("canje_id");
		int id = 0;
		if (StringUtils.checkNotEmpty(idStr)) {
			id = Integer.parseInt(idStr);
		}
		if (id == 0) {
			Integer idInteger = (Integer) renderRequest
					.getAttribute("canje_id");
			if (idInteger != null) {
				id = idInteger.intValue();
			}
		}
		return id;
	}

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		try {
			CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
					.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
			getCanjeChequePropio(actionRequest, canjeChequePropio);
			if (!canjeChequePropio.validarTotales()) {
				throw new CanjeChequesTotalesDiferentesException();
			}
			User user = PortalUtil.getUser(actionRequest);
			CanjeChequePropioServiceUtil.save(canjeChequePropio, user, entidad);
			actionRequest.setAttribute("canje_id",
					Integer.valueOf(canjeChequePropio.getId()));
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	private CanjeChequePropio getCanjeChequePropio(ActionRequest actionRequest,
			CanjeChequePropio cpp) {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(actionRequest);

		int cantidadCheques = ParamUtil.getInteger(actionRequest,
				"cantidad_cheques");
		getChequesParaReutilizar(actionRequest, ctasBcrias, cantidadCheques,
				cpp);
		int id = ParamUtil.getInteger(actionRequest, "nro_op_final");
		cpp.setOrdenPago(new OrdenPagoOspim(id));
		return cpp;
	}

	private void getChequesParaReutilizar(ActionRequest renderRequest,
			List<CuentaBancaria> ctasBcrias, int cantidad_cheques,
			CanjeChequePropio cpp) {
		
		CuentaBancaria cb = null;
		Banco b = null;
		
		String key = "canjear_cheque_";
		for (ChequeACanjear cac : cpp.getChequesViejos()) {
			cac.setCanjeado(false);
		}

		for (int i = 0; i < cantidad_cheques; i++) {
			if (renderRequest.getParameter(key + i) != null) {
				String aBorrar = renderRequest.getParameter(key + i);
				String numeroCheque = aBorrar.substring(15,
						aBorrar.indexOf("_", 15));
				String idCta = aBorrar.substring(aBorrar.indexOf("_", 15) + 1,
						aBorrar.length());
				int indexOf = ctasBcrias.indexOf(new CuentaBancaria(Integer
						.valueOf(idCta)));
				b = new Banco(ctasBcrias.get(indexOf).getBanco().getId_banco());
				cb = new CuentaBancaria(Integer.parseInt(idCta));
				cb.setBanco(b);
						
				Cheque cheque = new Cheque();
				cheque.setCuentaBancaria(cb);	
				cheque.setNumero(new BigDecimal(numeroCheque));
				
				int indexCheque = cpp.getChequesViejos().indexOf(
						new CanjeChequePropio.ChequeACanjear(cheque));
				cpp.getChequesViejos().get(indexCheque).setCanjeado(true);
			}
		}
	}
}
