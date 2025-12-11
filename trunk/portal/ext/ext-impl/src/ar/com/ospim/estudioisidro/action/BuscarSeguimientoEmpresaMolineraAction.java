package ar.com.ospim.estudioisidro.action;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarSeguimientoEmpresaMolineraAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarSeguimientoEmpresaMolineraAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		TraeListasServiceUtil.getBancos(renderRequest);
		TraeListasServiceUtil.getRamosEmpresa(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getLocalidades(renderRequest);
		
		PortletSession portletSession = renderRequest.getPortletSession();
		
		LlamadosEstudio llest = (LlamadosEstudio) portletSession
				.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
		String cuit=ParamUtil.getString(renderRequest,"cuit");
		String buscar=ParamUtil.getString(renderRequest,"buscar");
		
		
		if(null!=buscar && !buscar.trim().equals("") && buscar.trim().equals("deuda")){
			ActaServiceUtil.getDeudasSeguimiento(cuit, renderRequest);
			return mapping.findForward("portlet.seguimiento.deudas.result.search");
		}
		if(null!=buscar && !buscar.trim().equals("") && buscar.trim().equals("actasAcuerdos")){
			ActaServiceUtil.buscaActaAcuerdoSeguimiento(cuit, renderRequest);
			return mapping.findForward("portlet.seguimiento.actas.acuerdos.result.search");
		}
		
		if(null!=buscar && !buscar.trim().equals("") && buscar.trim().equals("cheques")){
			ContabilidadServiceUtil.listadoValoresSeguimiento(cuit,  renderRequest);			
			return mapping.findForward("portlet.seguimiento.cheques.result.search");
		}
		
		if(null!=buscar && !buscar.trim().equals("") && buscar.trim().equals("recibos")){
			ReciboServiceUtil.getRecibosSeguimiento(null, cuit, null, renderRequest);			
			return mapping.findForward("portlet.estudio_isidro.recibos.seguimiento.result.search");
		}
				
		if (null==llest || llest.getCuit()==null || !llest.getCuit().equals(cuit)) {		
			llest=new LlamadosEstudio();
			EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest,null);			
		}		
		
		portletSession.setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, llest.getEmpresa(),PortletSession.APPLICATION_SCOPE);
		
		portletSession.setAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,llest,PortletSession.APPLICATION_SCOPE);
		
		
		portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS);
		
		return mapping
				.findForward("portlet.estudio_isidro.seguimiento_empresa_molinera_result");

	}
	
}
