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
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SeleccionComprobantesTratamientosDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SeleccionTratamientosDiscapacidadAction.class);

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
			String comprobantes = ParamUtil
					.getString(renderRequest, "comprobantes", null);
			SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			if(comprobantes.length()>0){
			 
			  
			  String compro[]=comprobantes.split(";");
			  if(compro.length>0){
				 String[]r= compro[0].split("\\|"); 
				 String idTratamiento=r[2];
				 Double importePresentado=0D;
				 for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
					 if(td.getId_tratamiento()== Integer.parseInt(idTratamiento)){
						 td.setComprobantes(new ArrayList<ComprobanteTratamientoDiscapacidad>());
						 for(int i=0;i<compro.length;i++){
							   String[] renglon=compro[i].split("\\|"); 
							   
							   ComprobanteTratamientoDiscapacidad ctd = new ComprobanteTratamientoDiscapacidad();
							   ctd.setTratamientoId(Integer.parseInt(idTratamiento));
							   LiquidacionPrestacion lp = new LiquidacionPrestacion();
							   lp.setId_liquidacion(Integer.parseInt(renglon[0]));
							   lp.setId_prestacion(Integer.parseInt(renglon[1]));
//							   lp.setCuil_titular(seguimiento.getCuilTitular());
//							   lp.setInte(seguimiento.getIntegrante());
							   lp.setOrden(Integer.parseInt(renglon[4]));
							   
							   Prestador pr= new Prestador();
							   pr.setId_prestador(Integer.parseInt(renglon[3]));
							   
							   ctd.setLiquidacionPrestacion(lp);
							   ctd.setPrestador(pr);
							   
							   td.getComprobantes().add(ctd);
						  }
					 }
				 }
				 
//DS
				 importePresentado=0D;	  
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
				 
			  }
			  
			}else{
				
				PortletSession portletSession = renderRequest.getPortletSession();
				List<ComprobanteTratamientoDiscapacidad> busqueda = new ArrayList<ComprobanteTratamientoDiscapacidad> ();
				busqueda= (ArrayList<ComprobanteTratamientoDiscapacidad>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD);
				if (busqueda == null || busqueda.size() == 0) {
					busqueda = (ArrayList<ComprobanteTratamientoDiscapacidad>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD, PortletSession.PORTLET_SCOPE);
				}
				
				if(busqueda.size()>0){
				   Double importePresentado=0D;	  	
				   Integer idTratamiento = busqueda.get(0).getTratamientoId();	
			       if(seguimiento.getTratamientos()!=null && seguimiento.getTratamientos().size()>0){
			    	   
			    	   for(TratamientoDiscapacidadSeguimiento tdt:seguimiento.getTratamientos()){
			    		   if(tdt.getId_tratamiento()!=idTratamiento){
			    		     for(ComprobanteTratamientoDiscapacidad t:tdt.getComprobantes() ){
								 try{
								   ComprobanteTratamientoDiscapacidad taux = SeguimientoSurServiceUtil.recuperaLiquidacionPrestacion(t.getLiquidacionPrestacion().getId_liquidacion(),
										   t.getLiquidacionPrestacion().getId_prestacion(),t.getLiquidacionPrestacion().getOrden()) ;
								   importePresentado += taux.getLiquidacionPrestacion().getImporteTotal().doubleValue();
								 }catch(Exception e){
									 importePresentado+=0;
								 }
							 }
			    		   }else{
			    			tdt.setComprobantes(new ArrayList<ComprobanteTratamientoDiscapacidad>());   
			    		   }
			    	   }
			    	   seguimiento.setImportePresentado(importePresentado);
			    	   
			       }
				}	   
			}
			session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.discapacidad.result.search");
		
	}
}