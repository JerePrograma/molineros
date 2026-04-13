package ar.com.ospim.autorizaciones.action;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.BusquedaSituacionMedicaFiltro;
import ar.com.ospim.autorizaciones.beans.ItemSituacionMedicaTotal;




import ar.com.ospim.autorizaciones.services.SituacionesMedicasServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarSituacionMedicaAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarSituacionMedicaAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.situacionesmedicas.result.search");		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
			
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
				fechaDesde = null;  
			}
			String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
			Date fechaHasta = null;
			int  pagina =ParamUtil.getInteger(renderRequest,"pagina_sel"); 
			
			try {
				fechaHasta= formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta= null;  
			}
			// resto de parametros de la busqueda
			int tipoSituMedica  = ParamUtil.getInteger(renderRequest, "situacionMedica", 0);  
             
			PortletSession portletSession = renderRequest.getPortletSession();

			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);			  	 			
			String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular", "");
		    //int totalrecords=0;
		    
		    BusquedaSituacionMedicaFiltro  busquedaSituacionFiltro = new BusquedaSituacionMedicaFiltro(fechaDesde, fechaHasta, inte, cuilTitular, tipoSituMedica, pagina);
		//, fechaDesde, fechaHasta, inte, cuilTitular, tipoSituMedica, pagina    
		    List<ItemSituacionMedicaTotal> busqueda = SituacionesMedicasServiceUtil.buscarSituacionesMedicasTotales(busquedaSituacionFiltro ) ;
			if (busqueda.size()>0){
				//totalrecords = busqueda.get(0).getTotal_registros();
				busquedaSituacionFiltro.setRegistrosTotal(busqueda.get(0).getTotal_registros());
			}else{
				//totalrecords =0;
				busquedaSituacionFiltro.setRegistrosTotal(0);
			}			
			busquedaSituacionFiltro.setPagina(pagina);
				
			portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS);
			portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS,	busqueda);
			session.removeAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_SITUACIONMEDICA);
			session.setAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_SITUACIONMEDICA, busquedaSituacionFiltro);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.situacionesmedicas.result.search");
		                      		                      
	}
}




