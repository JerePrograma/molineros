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
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EliminaMedicamentoLiquidacionAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EliminaMedicamentoLiquidacionAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.medicamentosliquidaciones.popup.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		try {
			Integer idLiquidacion = ParamUtil.getInteger(renderRequest, "idLiquidacion",0);
			Integer idPrestacion = ParamUtil.getInteger(renderRequest, "idPrestacion",0);
			Integer orden=ParamUtil.getInteger(renderRequest, "orden",0);
			Double valorUnitario=ParamUtil.getDouble(renderRequest, "valorunitario", 0);
			List<ComprobanteTratamientoDiscapacidad> ltd = new ArrayList<ComprobanteTratamientoDiscapacidad>();		
			
			if(idLiquidacion>0 && idPrestacion>0 && orden>0){
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  
			  
			  Double importePresentado=0D;
			  for(ComprobanteTratamientoDiscapacidad td:seguimiento.getLiquidaciones() ){
				  if(td.getLiquidacionPrestacion().getId_liquidacion() !=idLiquidacion ||
					   td.getLiquidacionPrestacion().getId_prestacion() != idPrestacion ||
					   td.getLiquidacionPrestacion().getOrden()!=orden){
					  ltd.add(td);
					  
					  if(valorUnitario==0){
						if(td.getLiquidacionPrestacion().getImporteTotal()==null) td.getLiquidacionPrestacion().generateImporteTotal(); 
					    importePresentado+= td.getLiquidacionPrestacion().getImporteTotal().doubleValue();
					    
					  }else{
						importePresentado+= td.getLiquidacionPrestacion().getCantidad().doubleValue()*valorUnitario;  
					  }
				  }	  
			  }
			  
			  seguimiento.setLiquidaciones(ltd);
			  seguimiento.setImportePresentado(importePresentado);
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.medicamentosliquidaciones.result.search");
		
	}
}