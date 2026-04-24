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
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SeleccionMedicamentosLiquidacionesAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SeleccionTratamientosDiscapacidadAction.class);

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
			String comprobantes = ParamUtil
					.getString(renderRequest, "comprobantes", null);
			Double valorUnitario= ParamUtil.getDouble(renderRequest, "valorunitario",0);
			
			if(comprobantes.length()>0){
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  
			  String compro[]=comprobantes.split(";");
			  if(compro.length>0){
				 String[]r= compro[0].split("\\|"); 
				 String idTratamiento=r[2];
				 Double importePresentado=0D;
				 List<ComprobanteTratamientoDiscapacidad> liquidaciones =new ArrayList<ComprobanteTratamientoDiscapacidad>();
				 
				 for(int i=0;i<compro.length;i++){
					   String[] renglon=compro[i].split("\\|"); 
					   
					   ComprobanteTratamientoDiscapacidad ctd = new ComprobanteTratamientoDiscapacidad();
					   ctd.setTratamientoId(Integer.parseInt(idTratamiento));
					   ctd = SeguimientoSurServiceUtil.recuperaLiquidacionPrestacion(Integer.parseInt(renglon[0]),
							   Integer.parseInt(renglon[1]), Integer.parseInt(renglon[4])) ;
							   
					   Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(Integer.parseInt(renglon[1]));
					   List<Medicamento>medicamentos = NomencladorServiceUtil.getBusquedaMedicamentos(Integer.parseInt(nomenclador.getCodigo()), "");
					   Medicamento medicamento = new Medicamento();
 				       if(medicamentos.size()>0){
							   medicamento=medicamentos.get(0);
 				       }else{
 				    	   medicamento.setDroga("");
 				    	   medicamento.setNombre(nomenclador.getDescripcion());
 				    	   try{
 				    	      medicamento.setTroquel(Integer.parseInt(nomenclador.getCodigo()));
 				    	   }catch(Exception e){}
 				       }
					   ctd.setMedicamento(medicamento);
					  
					   Boolean existe=false;
					   for(ComprobanteTratamientoDiscapacidad td:seguimiento.getLiquidaciones()){
						   if(td.getLiquidacionPrestacion().getId_liquidacion()==ctd.getLiquidacionPrestacion().getId_liquidacion() &&
							  td.getLiquidacionPrestacion().getId_prestacion()==ctd.getLiquidacionPrestacion().getId_prestacion() &&
							  td.getLiquidacionPrestacion().getOrden()==ctd.getLiquidacionPrestacion().getOrden()){
							   existe=true;
							   break;
						   }
					   }
			           if(valorUnitario==0){		   
					      importePresentado+= ctd.getLiquidacionPrestacion().getImporteTotal().doubleValue();
			           }else{
			        	  importePresentado+= ctd.getLiquidacionPrestacion().getCantidad().doubleValue() * valorUnitario;
			           }
					   
					   if(!existe)
					       seguimiento.getLiquidaciones().add(ctd);
				  }
				 
				  seguimiento.setImportePresentado(importePresentado);
			  }
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			}
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.medicamentosliquidaciones.result.search");
	}
}