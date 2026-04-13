package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarActasPeriodosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarActasPeriodosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_EST_1_")){
			String entidadString=ParamUtil.getString(renderRequest,"entidad");
			if(entidadString!=null&&entidadString.equals("U.O.M.A.")){
				entidad = WebKeysGlobal.UOMA;
			}else if(entidadString!=null&&entidadString.equals("A.M.T.I.M.A.")){
				entidad = WebKeysGlobal.AMTIMA;	
			}
		}

		String esEdicion = renderRequest.getParameter("esEdicion");

		if (esEdicion != null && esEdicion.equals("esEdicion")) {

			renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
					WebKeysTesoreria.ACTAS_ACTION_EDICION);
		}
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}
		
		if(acta.getEntidad()!=null&&acta.getEntidad().equals("U.O.M.A.")){
			entidad = WebKeysGlobal.UOMA;
		}else if(acta.getEntidad()!=null&&acta.getEntidad().equals("A.M.T.I.M.A")){
			entidad = WebKeysGlobal.AMTIMA;	
		}
		

		String recalc = (String) renderRequest.getParameter("recalcular");
		if (recalc != null && recalc.equals("recalcular")) {
			String cuit = renderRequest.getParameter("cuit");
			if (acta.getEmpresa() == null
					|| StringUtils.checkEmpty(acta.getEmpresa().getCuit())
					|| !acta.getEmpresa().getCuit().equals(cuit.trim())) {
				acta.setEmpresa(new Empresa(cuit.trim(), "000", ""));
			}
			String obligD = renderRequest.getParameter("fechaObligDia");
			String obligM = renderRequest.getParameter("fechaObligMes");
			obligM = String.valueOf(Integer.valueOf(obligM) + 1);
			String obligA = renderRequest.getParameter("fechaObligAnio");
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			Calendar oblig = Calendar.getInstance();
			oblig.setTime(format.parse(obligD + "-" + obligM + "-" + obligA));

			List<InteresAfip> intereses = TraeListasServiceUtil
					.getInteresesAfip(renderRequest);
			acta.calcaularIntereses(cuit, intereses, oblig, renderRequest);

			if (acta.getActasRelacionadas() != null) {
				for (ActaRelacionada actaR : acta.getActasRelacionadas()) {
					BigDecimal interes = AfipServiceUtil.calculoInteres(actaR
							.getImporte(), actaR.getActaRelacionada()
							.getFechaPago(), oblig.getTime(), intereses);
					actaR.setSaldo(actaR.getImporte().add(interes));
				}
			}
		}

		_log.debug("Saliendo de render");
		if(entidad!=WebKeysGlobal.OSPIM ){
			return mapping.findForward("portlet.uoma.actas.periodos.view");
		}else{
			return mapping.findForward("portlet.tesoreria.actas.periodos.view");
		}
	}
}
