package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CrearListaParaPagoReintegros extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(CrearListaParaPagoReintegros.class);
	
	ReintegroList rList = new ReintegroList();


	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		try {
			User user = PortalUtil.getUser(actionRequest);
			
			PortletSession portletSession = actionRequest.getPortletSession();
			portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
			portletSession.removeAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE);
			
			ReintegroList reintegrosListFromRequest = getReintegroListFromRequest(actionRequest);
			if(!this.validaReiPagoTransferencias(rList)){				
				int id = OrdenPagoServiceUtil.saveReintegroListParaPago(reintegrosListFromRequest, user);
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
	private ReintegroList getReintegroListFromRequest(
			ActionRequest actionRequest) {
		List<Reintegro> reintegrosList = new ArrayList<Reintegro>();

		Enumeration parameters = actionRequest.getParameterNames();
		String tipo_reintegro = ParamUtil.getString(actionRequest, "tipo_reintegro");
		
				

		if (!tipo_reintegro.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)){
		
			while (parameters.hasMoreElements()) {
				String paramName = (String) parameters.nextElement();
				
				if (paramName.indexOf("pagarRein") != -1) {
					String numero = paramName.substring("pagarRein".length(),paramName.length());
					//String importe = ParamUtil.getString(actionRequest, paramName);
					String cbu =  null;
					String valores = ParamUtil.getString(actionRequest, paramName);
					String[] arrValores = valores.split(",");
					String importe = arrValores[0];
					try {
						cbu = arrValores[1];
					}catch (Exception e) {
						// TODO: handle exception
					}	
						
					Reintegro reintegro = new Reintegro(Integer.valueOf(numero),new BigDecimal(importe));
					if (StringUtils.checkNotEmpty(cbu)){
						reintegro.setTransferenciaBancaria(true);						
					}else{
						reintegro.setTransferenciaBancaria(false);
					}
					
					reintegro.setTipo_reintegro("");
					reintegrosList.add(reintegro);				
				}
			}
		}
		if (tipo_reintegro.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA))  { 
			
			while (parameters.hasMoreElements()) {
				String paramName = (String) parameters.nextElement();
				
				if (paramName.indexOf("pagarRein") != -1) {
					String numero = paramName.substring("pagarRein".length(),paramName.length());
					//String importe2 = ParamUtil.getString(actionRequest, paramName);
					String cbu =  null;
					String valores = ParamUtil.getString(actionRequest, paramName);
					String[] arrValores = valores.split(",");
					String importe = arrValores[0];
					try {
						cbu = arrValores[2];
					}catch (Exception e) {
						// TODO: handle exception
					}	
					
							
					Reintegro reintegro = new Reintegro(Integer.valueOf(numero),new BigDecimal(importe));
					if (StringUtils.checkNotEmpty(cbu)){
						reintegro.setTransferenciaBancaria(true);						
					}else{
						reintegro.setTransferenciaBancaria(false);
					}
					reintegro.setTipo_reintegro(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA);
					reintegrosList.add(reintegro);
					
				}
			}
		}
	//	rList.setCbu(cbu);
		//rList.setCuilCuenta(cuilCuenta);

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
	private boolean validaReiPagoTransferencias(ReintegroList reintegrosList){
		boolean error = false;
		boolean marca = false;
		
		for (Reintegro reintegro : reintegrosList.getReintegros()) {
			if (reintegro.isTransferenciaBancaria() == true){
				marca = true;
			}
		}
		if(marca == true){
			for (Reintegro reintegro : reintegrosList.getReintegros()) {
				if (reintegro.isTransferenciaBancaria() == false){
					return true;
				}
				
			}
		}		
		return error;
		
	}

}