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
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarComprobantesTratamientosDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarTratamientosDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.comprobantes.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			int id = ParamUtil.getInteger(renderRequest, "idtratamiento", 0);
			PortletSession portletSession = renderRequest.getPortletSession();
			
			TratamientoDiscapacidad td = TratamientoDiscapacidadServiceUtil.getTratamientoDiscapacidad(id);
			
			
			//-Inicio Fechas			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			String fechaDesdeDia = ParamUtil.getString(renderRequest,
					"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,
					"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,
					"fechaDesdeAnio");
			Date fechaDesde = null;
			try {
				fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechaDesde = td.getPeriodo_desde();
			}
			String fechaHastaDia = ParamUtil.getString(renderRequest,
					"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,
					"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,
					"fechaHastaAnio");
			Date fechaHasta = null;
			try {
				fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta = td.getPeriodo_hasta();
			}
//-- Fin Fechas

			List<ComprobanteTratamientoDiscapacidad>busRet = SeguimientoSurServiceUtil.recuperaComprobantesTratamientos(td.getAfiliado().getCuil_titular(),
					td.getAfiliado().getInte(),td.getPrestacion().getId() ,fechaDesde,fechaHasta,td.getAcreedor().getCuit());
			
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			SeguimientoSur seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);	
			List<ComprobanteTratamientoDiscapacidad>busqueda=new ArrayList<ComprobanteTratamientoDiscapacidad>();
			for(ComprobanteTratamientoDiscapacidad c:busRet){
				c.setTratamientoId(id);
				if(c.getSeguimientoId()==null || c.getSeguimientoId()==0 || c.getSeguimientoId().equals(seguimiento.getId())){
					busqueda.add(c);
				}
			}
			
			//Agrega comprobantes ya cargados al seguimiento en el caso de no haber sido levantados por el filtro de fechas
			for(TratamientoDiscapacidadSeguimiento t:seguimiento.getTratamientos() ){
				if(t.getId_tratamiento()==id ){
					for(ComprobanteTratamientoDiscapacidad c:t.getComprobantes()){
						Boolean existe=false;
						for(ComprobanteTratamientoDiscapacidad cb:busqueda){
							if(cb.getLiquidacionPrestacion().getId_liquidacion()==c.getLiquidacionPrestacion().getId_liquidacion() &&
							   cb.getLiquidacionPrestacion().getOrden()==c.getLiquidacionPrestacion().getOrden() ){
								existe=true;
								break;
							}
						}
						if(!existe){
							ComprobanteTratamientoDiscapacidad taux = SeguimientoSurServiceUtil.recuperaLiquidacionPrestacion(c.getLiquidacionPrestacion().getId_liquidacion(),
									   c.getLiquidacionPrestacion().getId_prestacion(),c.getLiquidacionPrestacion().getOrden()) ;
							c.setLiquidacionPrestacion(taux.getLiquidacionPrestacion());
							c.setPrestador(taux.getPrestador());
							busqueda.add(c);
						}
					}
					break;
				}
			}
			//Fin Agrega comprobantes
			
			renderRequest
					.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD);
			renderRequest.setAttribute(
					WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD,
					busqueda);

			portletSession
					.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD);
			portletSession.setAttribute(
					WebKeysAutorizaciones.BUSQUEDA_COMPROBANTES_TRATAMIENTOS_DISCAPACIDAD,
					busqueda);

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.comprobantes.discapacidad.popup.result.search");
	}
}