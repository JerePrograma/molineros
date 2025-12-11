package ar.com.ospim.padronentidades.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex;
import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex.Result;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadPadronUnificado;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class PadronEntidadesUnificadoAfipAction extends PortletAction {

//	public static final String PADRON_ENTIDADES = "PADRON_ENTIDADES";
	public static final String PADRON_EMPRESAS_AFIP = "PADRON_EMPRESAS_AFIP";
	
	private static Log _log = LogFactoryUtil.getLog(PadronEntidadesUnificadoAfipAction.class);
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int currentPage = 1;
		String cur = StringUtils.getValueOrNull(renderRequest
				.getParameter(SearchContainer.DEFAULT_CUR_PARAM));
		if (cur != null) {
			currentPage = Integer.parseInt(cur);
		}

		String cuit = StringUtils.getValueOrNull(renderRequest
				.getParameter("cuit_entidad"));
		renderRequest.setAttribute("cuit_entidad", cuit);
		String entidad = StringUtils.getValueOrNull(renderRequest
				.getParameter("entidad"));
		renderRequest.setAttribute("entidad", entidad);
		String sucursal = StringUtils.getValueOrNull(renderRequest
				.getParameter("sucursal"));
		renderRequest.setAttribute("sucursal", sucursal);

		String soloOP = StringUtils.getValueOrNull(renderRequest
				.getParameter("soloOP"));
		renderRequest.setAttribute("soloOP", soloOP);

		String soloIngresos = StringUtils.getValueOrNull(renderRequest
				.getParameter("soloIngresos"));
		renderRequest.setAttribute("soloIngresos", soloIngresos);
		String suf = StringUtils.getValueOrNull(renderRequest
				.getParameter("suf"));
		renderRequest.setAttribute("suf", suf);
		
		int id_seccional=ParamUtil.getInteger(renderRequest, "id_seccional");

		int id_prestador = ParamUtil.getInteger(renderRequest, "id_prestador");
		boolean buscarDestino=ParamUtil.getBoolean(renderRequest, "buscarDestino");
		_log.debug("consultando empresas - entidad: " + (StringUtils.checkNotEmpty(entidad)?entidad:"null"));
		List<Empresa> empresas = null;
//		if (null != soloOP && soloOP.equals("true")) {
//			_log.debug("consultando empresas 1");
//			empresas = TraeListasServiceUtil.getEmpleadoresDeOP(cuit,
//					entidad, sucursal, id_prestador);
//			renderRequest.setAttribute("total", empresas.size());
//		} else if (soloIngresos != null && soloIngresos.equals("true")) {
//			_log.debug("consultando empresas 2");
//			empresas = TraeListasServiceUtil.getEmpresasIngreso(cuit,
//					entidad, sucursal);
//			renderRequest.setAttribute("total", empresas.size());
//		} else {
//			_log.debug("consultando empresas 3");
//			if (EmpresasIndex.isListoParaUsar() && StringUtils.checkEmpty(cuit)) {
//				_log.debug("consultando empresas 4");
//				Result res = EmpresasIndex.buscar(entidad,
//						SearchContainer.DEFAULT_DELTA, currentPage - 1);
//				empresas = res.getEmpresas();
//				renderRequest.setAttribute("total", res.getTotal());
//			} else {
				_log.debug("consultando empresas 5");
				empresas = TraeListasServiceUtil.getEmpleadoresAfip(cuit,
						entidad, sucursal);
				if(empresas.size()==1&&id_seccional>0){
					empresas.get(0).setId_seccional(id_seccional);
				}
				renderRequest.setAttribute("total", empresas.size());
//			}
//		}

//		List<EntidadPadronUnificado> entidades = new ArrayList<EntidadPadronUnificado>();
//		if (empresas != null) {
//			entidades.addAll(empresas);
//		}
//		if (cuit != null) {
//			List<Seccional> seccionales = null;
//			/*Integer seccional = null;
//			if (sucursal != null) {
//				try {
//					seccional = Integer.valueOf(sucursal);
//				} catch (Exception e) {
//				}
//			}*/
//
//			if (cuit.equals(WebKeysGlobal.CUIT_AMTIMA)
//					|| cuit.equals(WebKeysGlobal.CUIT_OSPIM)
//					|| cuit.equals(WebKeysGlobal.CUIT_UOMA)) {
//				seccionales = TraeListasServiceUtil.getSeccionales(id_seccional,
//						entidad, cuit);
//				entidades.addAll(seccionales);
//			}
//			renderRequest.setAttribute("buscarDestino", buscarDestino);
//			renderRequest.setAttribute("cuit_entidad", cuit);
//		}
		
		if(renderResponse!=null && renderResponse.getNamespace()!=null &&renderResponse.getNamespace().equals("_EST_1_")){
//			renderRequest.getPortletSession().setAttribute(PADRON_EMPRESAS_AFIP, entidades,PortletSession.APPLICATION_SCOPE);
			renderRequest.getPortletSession().setAttribute(PADRON_EMPRESAS_AFIP, empresas,PortletSession.APPLICATION_SCOPE);
		}else{		
//			renderRequest.setAttribute(PADRON_EMPRESAS_AFIP, entidades);
			renderRequest.setAttribute(PADRON_EMPRESAS_AFIP, empresas);
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.utils.padron.entidad.afip.view"));
	}
}
