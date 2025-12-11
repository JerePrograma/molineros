package ar.com.ospim.farmacia.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.farmacia.beans.ReintegroFarmaciaList;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.util.StringUtils;

public class CrearListaParaPagoReintegros extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(CrearListaParaPagoReintegros.class);

	
	ReintegroFarmaciaList rList = new ReintegroFarmaciaList();

	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		try {
			User user = PortalUtil.getUser(actionRequest);
			ReintegroFarmaciaList reintegrosList = getReintegroListFromRequest(actionRequest);
			if(!this.validaReiPagoTransferencias(rList)){
				int id = OrdenPagoServiceUtil.saveReintegroFarmaciaListParaPago(reintegrosList, user);
				actionRequest.setAttribute("listaId", id);				
			}
		} catch (Exception e) {
			_log.error("Error al crear lista para op de reintegros de farmacia", e);
			throw e;
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		
		if(this.validaReiPagoTransferencias(rList)){
			SessionErrors.add(renderRequest, "error-lista-reintegros-cuenta");
		}

		return mapping.findForward("portlet.liquidaciones.view");
	}

	@SuppressWarnings("unchecked")
	private ReintegroFarmaciaList getReintegroListFromRequest(
			ActionRequest actionRequest) {
		//ReintegroFarmaciaList rList = new ReintegroFarmaciaList();
		List<ReintegroMedicamento> reintegrosList = new ArrayList<ReintegroMedicamento>();
		Enumeration parameters = actionRequest.getParameterNames();
		// String tipo_reintegro = ParamUtil.getString(actionRequest,
		// "tipo_reintegro");
		// if
		// (!tipo_reintegro.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)){
		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();

			if (paramName.indexOf("pagarRein") != -1) {
				String numero = paramName.substring("pagarRein".length(),paramName.length());
				//String importe = ParamUtil.getString(actionRequest, paramName);
				//reintegro.setTipo_reintegro("");
				String cbu =  null;
				String valores = ParamUtil.getString(actionRequest, paramName);
				String[] arrValores = valores.split(",");
				String importe = arrValores[0];
				try {					
					cbu = arrValores[1];
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				ReintegroMedicamento reintegro = new ReintegroMedicamento(Integer.valueOf(numero),new BigDecimal(importe));

				if (StringUtils.checkNotEmpty(cbu)){
					reintegro.setTransferenciaBancaria(true);						
				}else{
					reintegro.setTransferenciaBancaria(false);
				}
				
				reintegrosList.add(reintegro);
			}
		}
		// }
		rList.setReintegros(reintegrosList);
		int idSeccional = Integer.parseInt(actionRequest.getParameter("seccional_op"));
		rList.setSeccional(new Seccional(idSeccional, ""));

		return rList;
	}
	
	/**
	 * 
	 * validamos que todos los Reintegros sean pagos por transferencias
	 * @param reintegrosList
	 * @return
	 */
	private boolean validaReiPagoTransferencias(ReintegroFarmaciaList reintegrosList){
		boolean error = false;
		boolean marca = false;
		
		for (ReintegroMedicamento reintegro : reintegrosList.getReintegros()) {
			if (reintegro.isTransferenciaBancaria() == true){
				marca = true;
			}
		}
		if(marca == true){
			for (ReintegroMedicamento reintegro : reintegrosList.getReintegros()) {
				if (reintegro.isTransferenciaBancaria() == false){
					return true;
				}
				
			}
		}		
		return error;
		
	}

}