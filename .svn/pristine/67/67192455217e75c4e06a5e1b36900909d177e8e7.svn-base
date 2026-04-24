/**
 */
package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;
import ar.com.ospim.util.StringUtils;

public class ListaPrestacionesreclamosreintegrosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ListaPrestacionesreclamosreintegrosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.reintegros_detalle.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		    HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
			List<ReintegroMedicamentoItem> medicamentos = (ArrayList<ReintegroMedicamentoItem>) session
					.getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
			
			
		    
		    int farmacia=0;
		    
		    boolean viene_de_cuotas = ParamUtil.getBoolean(renderRequest, "viene_de_cuotas", false);
		    String findforwardjsp;
		    if (viene_de_cuotas) {
		    	findforwardjsp="portlet.liquidaciones.prestaciones_reclamos_reintegro_cuotas_entry" ;
		    } else {
		    	findforwardjsp="portlet.liquidaciones.prestaciones_reclamos_reintegro_entry" ;
		    }
		     
		    
		try {		
			    
			    boolean reintegro = ParamUtil.getBoolean(renderRequest, "reintegro", false);
			    int inte = ParamUtil.getInteger(renderRequest, "inte");
			    String cuil_titular = ParamUtil.getString(renderRequest,"cuil");
			    farmacia= ParamUtil.getInteger(renderRequest, "farmacia", 0);
			    String nroLoteFiltro = ParamUtil.getString(renderRequest,"nroLote_filtro");
			    int marca_rein_liq = 0;
			    marca_rein_liq = ParamUtil.getInteger(renderRequest, "marca_rein_liq", 0);			   
			    String planNombre = ParamUtil.getString(renderRequest,"plan");
			    
			    List<PrestacionesReclamo> busqueda = null;
			    
			     if(farmacia==1){ // es de farmacia 
		 				busqueda = ReintegroServiceUtil.buscarPrestacionesReclamosAfiliadoReintegroFarmacia(inte, cuil_titular);
		 				findforwardjsp="portlet.farmacia.prestaciones_reclamos_reintegro_entry" ;
			     }else{
			    	 if(nroLoteFiltro != null && !StringUtils.checkEmpty(nroLoteFiltro)) {//Busco por numero de lote los reclamos prestacionales
			    		 busqueda = ReintegroServiceUtil.buscarPrestacionesReclamosAfiliadoReintegroPorLote(inte, cuil_titular , reintegro  , nroLoteFiltro );	
			    		 findforwardjsp="portlet.liquidaciones.prestaciones_reclamos_reintegro_por_lote_entry" ; 
			    	 }else {
			    		 busqueda = ReintegroServiceUtil.buscarPrestacionesReclamosAfiliadoReintegro(inte, cuil_titular ,reintegro, marca_rein_liq, planNombre );			    	 			    		 
			    	 }
			     }
		
			    	  //Elimino elementos que ya estan en medicamentos farmacias agregados
				 if (medicamentos != null && !medicamentos.isEmpty() && medicamentos.get(0) instanceof ReintegroMedicamentoItem ) {
				 	if (busqueda != null && !busqueda.isEmpty()) {
						for (ReintegroMedicamentoItem medicamentoItem : medicamentos) {							
							busqueda.remove(medicamentoToPrestacionesReclamo(busqueda, medicamentoItem));
						}
					}
				}
			  
			  
			  
			    renderRequest.removeAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_RECLAMOS);
				renderRequest.setAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_RECLAMOS,busqueda);
						
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward(findforwardjsp);
		
	}
	
	
	
	private  PrestacionesReclamo medicamentoToPrestacionesReclamo(List<PrestacionesReclamo> listaReclamos, ReintegroMedicamentoItem medicamentoItem){
		PrestacionesReclamo prestaReclamo = null;
		for(PrestacionesReclamo reclamos : listaReclamos){
		      if(medicamentoItem.getIdPrestacionReclamo() == reclamos.getIdprestacionReclamo()){
		    	  prestaReclamo =  reclamos;
		       }
		  }
		  return prestaReclamo;
	}
	
	
	
	
	
	
}
