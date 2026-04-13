/**
 */

package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EliminaTratamientosDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EliminaTratamientosDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.discapacidad.popup.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		try {
			Integer idTratamiento = ParamUtil.getInteger(renderRequest, "tratamientoid",0);
					
			
			if(idTratamiento>0){
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  List<TratamientoDiscapacidadSeguimiento> ltd = new ArrayList<TratamientoDiscapacidadSeguimiento>();
			  
			  for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
				  if(td.getId_tratamiento()!=idTratamiento) ltd.add(td);
			  }
			  
			  seguimiento.setTratamientos(ltd);
			  
//DS			  
			  Double importePresentado=0D;	  
			  for(TratamientoDiscapacidadSeguimiento tdt:seguimiento.getTratamientos()){
				 for(ComprobanteTratamientoDiscapacidad t:tdt.getComprobantes() ){
					 try{
					   ComprobanteTratamientoDiscapacidad taux = SeguimientoSurServiceUtil.recuperaLiquidacionPrestacion(t.getLiquidacionPrestacion().getId_liquidacion(),
							   t.getLiquidacionPrestacion().getId_prestacion(),t.getLiquidacionPrestacion().getOrden()) ;
					   importePresentado += taux.getLiquidacionPrestacion().getImporteTotal().doubleValue();
					 }catch(Exception e){
						 importePresentado+=0;
					 }
				 }
			  }
			  seguimiento.setImportePresentado(importePresentado);
//DS			  
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.discapacidad.result.search");
		
	}
}