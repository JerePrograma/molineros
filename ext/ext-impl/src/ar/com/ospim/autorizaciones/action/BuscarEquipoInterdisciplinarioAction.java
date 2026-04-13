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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.autorizaciones.services.EquipoInterdisciplinarioServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarEquipoInterdisciplinarioAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarEquipoInterdisciplinarioAction.class);

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
			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			
			
			String fechaDia = ParamUtil.getString(renderRequest,
					"fechaDia");
			String fechaMes = ParamUtil.getString(renderRequest,
					"fechaMes");
			String fechaAnio = ParamUtil.getString(renderRequest,
					"fechaAnio");
			Date fecha = null;
			
			
			try {
				fecha= formatoDeFechas.parse(fechaDia + "/"
						+ (Integer.parseInt(fechaMes) + 1) + "/"
						+ fechaAnio);
			} catch (Exception e) {
				fecha= null;
			}
			
			PortletSession portletSession = renderRequest.getPortletSession();

			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			String cuil = ParamUtil.getString(renderRequest, "cuil", null);			
			String estado = ParamUtil.getString(renderRequest, "estado", null);
			int nroRegistro= ParamUtil.getInteger(renderRequest, "nroRegistro",0);
			String motivo = ParamUtil.getString(renderRequest, "motivo", null);
			
			List<EquipoInterdisciplinario> busqueda = EquipoInterdisciplinarioServiceUtil.buscarEquipoInterRegistros(estado , fecha,  inte, cuil, nroRegistro,motivo );
			
			renderRequest.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS );
			renderRequest.setAttribute(	WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS ,	busqueda);
			
				
			portletSession.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS );
			portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_EQUIPOS_INTERDISCIPLINARIOS ,	busqueda);
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.equipointerdisciplinario.result.search");
		                      		                      
	}
}




