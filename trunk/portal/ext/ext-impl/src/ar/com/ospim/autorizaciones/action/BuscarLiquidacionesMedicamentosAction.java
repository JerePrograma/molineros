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
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarLiquidacionesMedicamentosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarLiquidacionesMedicamentosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.medicamentosliquidaciones.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {

			String entidad = ParamUtil
					.getString(renderRequest, "entidad", null);
			String cuilTitular = ParamUtil
					.getString(renderRequest, "cuil_titular", null);
			Integer inte = ParamUtil.getInteger(renderRequest,"inte");
			Integer idPrestacion=ParamUtil.getInteger(renderRequest, "id_prestacion");
			String cuitPrestador = ParamUtil
					.getString(renderRequest, "cuit_prestador", null);
			String descPrestador = ParamUtil
					.getString(renderRequest, "desc_prestador", null);
			
			Integer idDroga=ParamUtil.getInteger(renderRequest, "droga",0);
			
			Integer periodicidad = ParamUtil.getInteger(renderRequest, "periodicidad");
			Integer ejercicio = ParamUtil.getInteger(renderRequest, "ejercicio");
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
			Date[] fechas ={formatoDeFechas.parse( "01/01/"+ejercicio),formatoDeFechas.parse("31/12/"+ejercicio)};;
			
            if(periodicidad!=null && periodicidad!=0){
			    fechas = SeguimientoSurServiceUtil.traeFechasBimestreSeguimientoSur(periodicidad);
            }			
			
            String clase=ParamUtil.getString(renderRequest, "clase","");

/*			
			if("undefined".equalsIgnoreCase(codPrestaci)) codPrestaci=null;
			if("undefined".equalsIgnoreCase(codPrest)) codPrest=null;
			if("undefined".equalsIgnoreCase(prestador)) prestador=null;
*/			
			
			PortletSession portletSession = renderRequest.getPortletSession();

			List<ComprobanteTratamientoDiscapacidad>busRet = SeguimientoSurServiceUtil.recuperaComprobantesTratamientos(cuilTitular,
					inte,idPrestacion,fechas[0],fechas[1],cuitPrestador,descPrestador,idDroga);
			
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();

			
			SeguimientoSur seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);	
			List<ComprobanteTratamientoDiscapacidad>busqueda=new ArrayList<ComprobanteTratamientoDiscapacidad>();
			for(ComprobanteTratamientoDiscapacidad c:busRet){
				c.setTratamientoId(-1);
				if(c.getSeguimientoId()==null || c.getSeguimientoId()==0 || c.getSeguimientoId()==seguimiento.getId()){
					Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(c.getLiquidacionPrestacion().getId_prestacion());
					if("ME".equalsIgnoreCase(clase) && n !=null && n.getId_tipo_nomenclador()==9){
					   busqueda.add(c);
					} else if(!"ME".equalsIgnoreCase(clase) && n !=null && n.getId_tipo_nomenclador()!=9){
					   busqueda.add(c);	
					}
				}
			}
			
			
			//Agrega comprobantes ya cargados al seguimiento en el caso de no haber sido levantados por el filtro de fechas
			for(ComprobanteTratamientoDiscapacidad c:seguimiento.getLiquidaciones()){
				Boolean existe=false;
				for(ComprobanteTratamientoDiscapacidad cb:busqueda){
					if(cb.getLiquidacionPrestacion().getId_liquidacion()==c.getLiquidacionPrestacion().getId_liquidacion() &&
					   cb.getLiquidacionPrestacion().getOrden()==c.getLiquidacionPrestacion().getOrden() ){
					   existe=true;
					   break;
					}
				}
				if(!existe){
					
					if(c.getLiquidacionPrestacion().getPrestacion()==null){
						
					  ComprobanteTratamientoDiscapacidad taux = SeguimientoSurServiceUtil.recuperaLiquidacionPrestacion(c.getLiquidacionPrestacion().getId_liquidacion(),
								   c.getLiquidacionPrestacion().getId_prestacion(),c.getLiquidacionPrestacion().getOrden()) ;
							
					  Nomenclador n= NomencladorServiceUtil.buscarNomencladorPorId(c.getLiquidacionPrestacion().getId_prestacion());
					  if(n!=null){
					     Prestacion p= new Prestacion();
					     p.setId_prestacion(c.getLiquidacionPrestacion().getId_prestacion());
					     p.setDescripcion(n.getDescripcion());
					     taux.getLiquidacionPrestacion().setPrestacion(p);
					  }
					  c.setLiquidacionPrestacion(taux.getLiquidacionPrestacion());
					  c.setPrestador(taux.getPrestador());
					  
				    }

					busqueda.add(c);
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
			
			
			renderRequest.removeAttribute("clase_expediente");
	        renderRequest.setAttribute("clase_expediente",clase);

	        portletSession.removeAttribute("clase_expediente");
	        portletSession.setAttribute("clase_expediente",clase);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.medicamentosliquidaciones.popup.result.search");
	}
}