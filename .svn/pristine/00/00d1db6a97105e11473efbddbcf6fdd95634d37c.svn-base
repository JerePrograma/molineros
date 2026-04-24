/**
 */

package ar.com.ospim.autorizaciones.action;

import java.math.BigDecimal;
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
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SeleccionComprobantesLiquidadosSeguimientoSURAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SeleccionTratamientosDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.comprobantesliquidados.popup.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		try {
			String comprobantes = ParamUtil
					.getString(renderRequest, "comprobantes", null);
			int entidad=ParamUtil.getInteger(renderRequest, "entidad");		
			
			
			if(comprobantes.length()>0){
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  
			  String compro[]=comprobantes.split(";");
			  if(compro.length>0){
				  
				 List<SeguimientoSurComprobante> liquidaciones =new ArrayList<SeguimientoSurComprobante>();
				 
				 for(int i=0;i<compro.length;i++){
					   String[] renglon=compro[i].split("\\|"); 
					   
					   SeguimientoSurComprobante comprobBusq = new SeguimientoSurComprobante(Integer.parseInt(renglon[3]),
								renglon[1],
								renglon[5],
								renglon[0],
								null,
								null,
								null,
								renglon[2], 
								Integer.parseInt(renglon[4]), 
								null);
					   
					   
					   Comprobante cp = SeguimientoSurServiceUtil.getComprobante(comprobBusq, entidad);
					   Prestador prestador = PrestadorServiceUtil.getPrestador(Integer.parseInt(renglon[6]));
					   Empresa empresa = new Empresa(renglon[0],renglon[6],prestador.getDescripcion());
					   
					   cp.setAcreedorEmpresa(empresa); 
					   SeguimientoSurComprobante comprobante = new SeguimientoSurComprobante(cp.getPtoVenta(),cp.getTipoComprobante(),
								cp.getNroComprobante(), cp.getCuit(),cp.getFechaEmision(),
								cp.getFechaRecepcion(),cp.getImporteComprobante(),
								cp.getLetraComprobante(),cp.getSucuComprobante(),cp.getFechaVencimiento(),cp.getAcreedorEmpresa());
					   
					   Boolean existe=false;
					   for(SeguimientoSurComprobante td:seguimiento.getComprobantes()){
						   if(td.getCuit().equalsIgnoreCase(comprobante.getCuit()) &&
							  td.getTipoComprobante().equalsIgnoreCase(comprobante.getTipoComprobante()) &&
							  td.getLetraComprobante().equalsIgnoreCase(comprobante.getLetraComprobante()) &&
							  td.getPtoVenta()==comprobante.getPtoVenta() &&
							  td.getSucuComprobante()==comprobante.getSucuComprobante() &&
							  td.getNroComprobante().equalsIgnoreCase(comprobante.getNroComprobante())){
							   existe=true;
							   break;
						   }
					   }
			           
					   if(!existe)
					       seguimiento.getComprobantes().add(comprobante);
				  }
				  
			  }
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			}
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.comprobantesliquidados.result.search");
	}
}