package ar.com.ospim.global.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
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
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarComprobanteEmbebidoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(SacarComprobanteEmbebidoAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;			
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		List<Comprobante> comprobantes = (ArrayList<Comprobante>) session
				.getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		
		BigDecimal sumaImportes=BigDecimal.ZERO;

		if (comprobantes == null) {
			comprobantes = new ArrayList<Comprobante>();
		}

		String todos = ParamUtil.getString(renderRequest, "todos");
		if (todos != null && todos.equals("todos")) {
			comprobantes.clear();
			//Limpiamos cualquier calculo de retenciones realizado mientras habian comprobantes seleccionados
			OrdenPago ordenPago = (OrdenPago) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
//			ordenPago.getFormaPago().clear();
//			List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
//			Iterator<OrdenPago.FormaPago> iter=list.iterator();
//			while(iter.hasNext()){					
//				if(((OrdenPago.FormaPago)iter.next()).getTipo().contains(RetencionGanancias.class.getSimpleName())){				
//					iter.remove();
//				}
//			}
			renderRequest.setAttribute("refreshPagosReten", "true");

			ordenPago.setPagos(null);
			session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
					ordenPago);
			
		} else {
			int pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
			String tipo_comprobante = ParamUtil.getString(renderRequest,
					"tipo_comprobante");
			String nro_comprobante = ParamUtil.getString(renderRequest,
					"nro_comprobante");
			String cuit = ParamUtil.getString(renderRequest, "cuit");
			String letra = ParamUtil.getString(renderRequest, "letra", " ");
			String sucu = ParamUtil.getString(renderRequest, "sucursal", "0");

			int i = comprobantes.indexOf(new Comprobante(pto_venta,
					tipo_comprobante, nro_comprobante, letra, Integer
							.parseInt(sucu), cuit));
			comprobantes.remove(i);
			sumaImportes=sumaImportesOrden(comprobantes);
			if(i>=0){
				Integer fechaAltaOPMes = 0;
				Integer fechaAltaOPAnio = 0;
				
				if(entidad == WebKeysGlobal.OSPIM) {
					fechaAltaOPMes = ParamUtil.getInteger(renderRequest,"fechaAltaOPMes");
					fechaAltaOPAnio = ParamUtil.getInteger(renderRequest,"fechaAltaOPAnio");
				}	
				sacaRetencion(cuit,sumaImportes, entidad, fechaAltaOPMes, fechaAltaOPAnio, session);
				sacaRetencionIIBB(cuit,comprobantes, entidad,session);
			}
		}
		
		session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
				comprobantes);
		session.setAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION,
				sumaImportesOrden(comprobantes));

		renderRequest.setAttribute("esEditable", "true");
		
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.utils.comprobante.embebido.result.search");
	}

	public BigDecimal sumaImportesOrden(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		BigDecimal signo = new BigDecimal(1);
		
		for (Comprobante comprobante : comprobantes) {
			signo=BigDecimal.ONE;
			if("NCR".equalsIgnoreCase(comprobante.getTipoComprobante())) {
				signo=BigDecimal.ONE.negate();
			}
			suma = suma.add(comprobante.getImporteComprobante().multiply(signo));
		}
		return suma;
	}
	
	private void sacaRetencion(String cuit, BigDecimal sumaComprobantes, int entidad, 
			Integer fechaAltaOPMes, Integer fechaAltaOPAnio, HttpSession session) throws SystemException {		
		
		Empresa proveedor = TraeListasServiceUtil.getEmpleadores(cuit,null, null).get(0);
		
		if(proveedor!=null&&proveedor.getImpGanancias()!=null&&proveedor.getImpGanancias().equals("AC")){
			
			//1ro BUSCO TODOS LOS COMPROBANTES DEL MES EN CURSO				
			Calendar periodoCalendar=Calendar.getInstance();
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 1);
			if(entidad == WebKeysGlobal.OSPIM) {
				
				try {
//					periodoCalendar.set(java.util.Calendar.DATE, 1); //fechaAltaOPDia
					periodoCalendar.set(java.util.Calendar.MONTH, fechaAltaOPMes);
					periodoCalendar.set(java.util.Calendar.YEAR, fechaAltaOPAnio);

				} catch (Exception e) {
					//dejamos el Calendar del dia en curso.
				}
			}
			
			BigDecimal retencion=AfipServiceUtil.getRetencionGanancias(proveedor.getCuit(),sumaComprobantes, periodoCalendar.getTime(), entidad);
			OrdenPago ordenPago = (OrdenPago) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
			if(retencion.compareTo(BigDecimal.ZERO)>0){
				if (list == null) {
					list = new ArrayList<OrdenPago.FormaPago>();
					ordenPago.setFormaPago(list);
				}				
				for(OrdenPago.FormaPago fp:list){
					if(fp.getTipo().contains(RetencionGanancias.class.getSimpleName())){				
						((RetencionGanancias)fp.getPago()).setImporte(retencion);		
					}
				}				
			}else{
				Iterator<OrdenPago.FormaPago> iter=list.iterator();
				while(iter.hasNext()){					
					if(((OrdenPago.FormaPago)iter.next()).getTipo().contains(RetencionGanancias.class.getSimpleName())){				
						iter.remove();
					}
				}				
			}
			ordenPago.setPagos(list);
			session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
					ordenPago);
		}
	}
	
	
	
	private void sacaRetencionIIBB(String cuit, List<Comprobante>comprobantes, int entidad, 
			HttpSession session) throws SystemException {		
		
		Empresa proveedor = TraeListasServiceUtil.getEmpleadores(cuit,null, null).get(0);
		
		if(entidad == WebKeysGlobal.UOMA) {
			OrdenPago ordenPago = (OrdenPago) session
					.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
			Iterator<OrdenPago.FormaPago> iter=list.iterator();
			while(iter.hasNext()){					
				if(((OrdenPago.FormaPago)iter.next()).getTipo().contains(RetencionIIBB.class.getSimpleName())){				
					iter.remove();
				}
			}
			
			/* ACTIVAR
			List<RetencionIIBB> retIIBB = OrdenPago.getRetencionIIBB(proveedor, comprobantes);
			if(retIIBB!=null && !retIIBB.isEmpty() ) {
				
				if (list == null) {
					list = new ArrayList<OrdenPago.FormaPago>();
					ordenPago.setFormaPago(list);
				}
				
				for(RetencionIIBB ret:retIIBB) {
				   if (!list.contains(new OrdenPago.FormaPago(ret))) {
					list.add(new OrdenPago.FormaPago(ret));
				   }
				}   
				ordenPago.setPagos(list);
				session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
						ordenPago);
			}
			
			*/
		}
	}

}
