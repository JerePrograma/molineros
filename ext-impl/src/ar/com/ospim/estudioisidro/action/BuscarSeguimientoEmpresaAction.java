package ar.com.ospim.estudioisidro.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarSeguimientoEmpresaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarSeguimientoEmpresaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}


		PortletSession portletSession = renderRequest.getPortletSession();
		
		LlamadosEstudio llest = (LlamadosEstudio) portletSession
				.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
		String cuit=renderRequest.getParameter("cuit");
		String popupActa = renderRequest.getParameter("popupActa");
		String popupConvenio = renderRequest.getParameter("popupConvenio");
		
		if (null==llest || llest.getCuit()==null || !llest.getCuit().equals(cuit)) {			
			llest=buscarLlamadosCuit(renderRequest);			
			buscarConvenios(renderRequest);
			buscarRecibos(renderRequest, entidad);
			buscarActas(renderRequest);
		}else if(null!=popupActa && popupActa.equals("true")){			
			buscarActas(renderRequest);	
		}else if(null!=popupConvenio && popupConvenio.equals("true")){
			buscarConvenios(renderRequest);			
		}
				
		return mapping.findForward("portlet.estudio_isidro.seguimiento_empresa_result");

	}

	public List<Recibo> buscarRecibos(PortletRequest renderRequest, int entidad)
			throws Exception {

		String empresa = null;
		String cuit = null;
		String actaNroStr = null;

		if (renderRequest.getParameter("recibo") != null) {
			actaNroStr = renderRequest.getParameter("recibo").trim().length() > 0 ? renderRequest
					.getParameter("recibo") : null;
		}

		if (null != renderRequest.getParameter("empresa")) {
			empresa = renderRequest.getParameter("empresa").trim().length() > 0 ? renderRequest
					.getParameter("empresa") : null;
		}

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit") : null;
		}
		List<Recibo> recibos = null;
		try {
			ReciboServiceUtil.getRecibosSeguimiento(actaNroStr, cuit, empresa, renderRequest);			
			
		} catch (Exception e) {
			_log.error(e);
		}
		return recibos;
	}

	public List<Convenio> buscarConvenios(PortletRequest renderRequest)
			throws Exception {

		String empresa = null;
		String cuit = null;
		String convenioNroStr = null;
		
		PortletSession portletSession = renderRequest.getPortletSession();

		if (renderRequest.getParameter("convenio") != null) {
			convenioNroStr = renderRequest.getParameter("convenio").trim()
					.length() > 0 ? renderRequest.getParameter("convenio")
					: null;
		}

		if (null != renderRequest.getParameter("empresa")) {
			empresa = renderRequest.getParameter("empresa").trim().length() > 0 ? renderRequest
					.getParameter("empresa") : null;
		}

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit") : null;
		}
				
		if(null==cuit || cuit.trim().equals("")){
			cuit=((LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa().getCuit();
		}
		
		List<Convenio> convenios = null;
		try {
			convenios = ConvenioServiceUtil.getConveniosSeguimiento(convenioNroStr, cuit,
					empresa);

			// renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS);
			// renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS,
			// convenios);
			
			portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS);
			portletSession.setAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS,
					convenios);
		} catch (Exception e) {
			_log.error(e);
		}
		return convenios;
	}

	public List<Acta> buscarActas(PortletRequest renderRequest) throws Exception {
		String empresa = null;
		String cuit = null;
		String actaNroStr = null;
		
		PortletSession portletSession = renderRequest.getPortletSession();
		
		if (renderRequest.getParameter("acta") != null) {
			actaNroStr = renderRequest.getParameter("acta").trim().length() > 0 ? renderRequest
					.getParameter("acta") : null;
		}

		if (null != renderRequest.getParameter("empresa")) {
			empresa = renderRequest.getParameter("empresa").trim().length() > 0 ? renderRequest
					.getParameter("empresa") : null;
		}
		if(null != renderRequest.getParameter("cuit")){
			cuit=renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest.getParameter("cuit") : null;
		}
		
		if(null==cuit || cuit.trim().equals("")){
			try{
				cuit=((LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa().getCuit();
			}catch (Exception e){
				LlamadosEstudio llest = (LlamadosEstudio) portletSession.getAttribute(
						WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,
						PortletSession.APPLICATION_SCOPE);
				cuit = llest.getCuit();
			}
		}
		
		List<Acta> actas = null;
		List<Acta> deudas = null;
		try {

			actas = ActaServiceUtil.getActasSeguimiento(actaNroStr, cuit,
					empresa);

			deudas = ActaServiceUtil.getDeudaSeguimiento(cuit, empresa,null);

			/*
			 * renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS);
			 * renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS,
			 * actas);
			 * renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS);
			 * renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS,
			 * deudas);
			 */

			portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS);
			portletSession.setAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS, actas);
			portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS);
			portletSession.setAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS,
					deudas);
		} catch (Exception e) {
			_log.error(e);
		}
		return actas;
	}

	public LlamadosEstudio buscarLlamadosCuit(PortletRequest renderRequest)
			throws Exception {

		String cuit = null;
		String razon = null;

		int cursor = 0;
		int size = 0;
		
//		String estado = renderRequest.getParameter("estado");
		Integer idEstado = ParamUtil.getInteger(renderRequest, "estado",0);
		_log.debug("que estado trae " + idEstado);
		
		cuit = ParamUtil.getString(renderRequest, "cuit");
		if(null==cuit || cuit.trim().length()==0){
			renderRequest.getPortletSession().getAttribute("cuit");
		}
		ParamUtil.getString(renderRequest, "_EST_1_cuit_entidadacta_");
		razon = ParamUtil.getString(renderRequest, "razon");
		
		Empresa empresaEnEdicion = null;
		LlamadosEstudio llamadosEstudio = null;
		
		if(StringUtils.checkNotEmpty(cuit)) {
//			empresaEnEdicion = EmpresaServiceUtil.getInstance().getEmpleadorCompleto(cuit,"000",null);
			empresaEnEdicion = (Empresa) renderRequest.getPortletSession().getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, PortletSession.APPLICATION_SCOPE);
		
//			estado = EmpresaServiceUtil.getEstadoEmpleador(cuit);
			idEstado = empresaEnEdicion!=null&&empresaEnEdicion.getEstado()!=null?empresaEnEdicion.getEstado().getId():0;
			try {
				size = Integer.parseInt((String) renderRequest.getAttribute("total"));
			} catch (NumberFormatException nfe) {
				size = 0;
			}

			if (null != renderRequest.getParameter("cur")
					&& !"".equals(renderRequest.getParameter("cur"))) {
				cursor = Integer.parseInt(renderRequest.getParameter("cur"));
			}

			try {
				if (size == 0) {
					size = LlamadoServiceUtil.getTotalLlamados(cuit);
				}
				llamadosEstudio = LlamadoServiceUtil.getLlamados(cuit, cursor);
				llamadosEstudio.setCuit(cuit);
				llamadosEstudio.setRazon(razon);
				llamadosEstudio.setEmpresa(empresaEnEdicion);
				renderRequest.setAttribute("total", size);
				renderRequest.setAttribute("cur", cursor);
				/*
				 * renderRequest
				 * .removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
				 * renderRequest
				 * .setAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO,llamadosEstudio
				 * ); renderRequest.setAttribute("estado", estado);
				 */
				PortletSession portletSession = renderRequest.getPortletSession();
				portletSession.removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
				portletSession.setAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO,llamadosEstudio);
				
//				portletSession.setAttribute("estado", estado);
				portletSession.setAttribute("estado", idEstado);
				
			} catch (Exception e) {
				_log.error(e);
				// return mapping.findForward("portlet.estudioisidro.error");
			}
		}
		

		return llamadosEstudio;
	}

}
