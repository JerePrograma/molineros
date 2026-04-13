package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.novedades.beans.BusquedaPreAfiliadosFiltro;
import ar.com.ospim.novedades.beans.PreAfiliadoTotal;
import ar.com.ospim.novedades.service.PreAfiliadoServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * Realiza la búsqueda de pre carga de afiliados según parámetros de entrada
 * 
 * @author SVA
 * 
 */
public class BuscarPreCargaAfiliadosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarPreCargaAfiliadosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.pre.carga.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

			BusquedaPreAfiliadosFiltro filtro = (BusquedaPreAfiliadosFiltro) 
				session.getAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS);
			
			if(filtro == null){
				filtro = new BusquedaPreAfiliadosFiltro();
			}
			Integer id = null;
			String cuil = null;
			String inte = null;
			String seccional = null;
			String seccional_nombre=null;
			int seccional_int = 0;
			String empresa = null;
			Integer empresa_usr = null;
			
			if (null != renderRequest.getParameter("id")) {
				String id_p = renderRequest.getParameter("id").trim().length() > 0 ? renderRequest
						.getParameter("id") : null;
				try{
					id = Integer.parseInt(id_p);
				} catch (NumberFormatException e) {
					seccional_int = 0;
				}
			}
			if (null != renderRequest.getParameter("cuil")) {
				cuil = renderRequest.getParameter("cuil").trim().length() > 0 ? renderRequest
						.getParameter("cuil") : null;
			}
			if (null != renderRequest.getParameter("inte")) {
				inte = renderRequest.getParameter("inte").trim().length() > 0 ? renderRequest
						.getParameter("inte") : null;
			}
			if (null != renderRequest.getParameter("seccional")) {
				seccional = renderRequest.getParameter("seccional").trim()
						.length() > 0 ? renderRequest.getParameter("seccional")
						: null;
			}
			if (null != renderRequest.getParameter("seccional_nombre")) {
				seccional_nombre = renderRequest.getParameter("seccional_nombre").trim()
						.length() > 0 ? renderRequest.getParameter("seccional_nombre")
						: null;
			}			
			
			if (null != seccional) {
				try {
					seccional_int = Integer.parseInt(seccional);
				} catch (NumberFormatException e) {
					seccional_int = 0;
				}
			}

			if (null != renderRequest.getParameter("origen_empresa")) {
				empresa = renderRequest.getParameter("origen_empresa").trim().length() > 0 ? renderRequest
						.getParameter("origen_empresa") : null;
				if(empresa != null){
					try{
						empresa_usr = Integer.parseInt(empresa);
					}catch (NumberFormatException e) {
						empresa_usr = null;
					}	
				}
			}
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
			Integer estado = ParamUtil.getInteger(renderRequest, "estado",0);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}		
			Date fechaHasta = null;
			try {
				fechaHasta = sdf.parse(fechaHastaFinal);
			} catch (Exception e) {
				fechaHasta = null;
			}			

			
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
	
			//ME FIJO SI TIENE UNA SECCIONAL FIJA
			User user = PortalUtil.getUser(renderRequest);
			String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString();
			List<PreAfiliadoTotal> busqueda=null;
			int cantResultados = 0;
			
			filtro.setId(id);
			filtro.setCuilTitular(cuil);
			filtro.setEmpresa(empresa);
			filtro.setEmpresa_usr(empresa_usr);
			filtro.setInte(inte);
			filtro.setPagina(pagina_sel);
			filtro.setRegistrosTotal(cantResultados);
			filtro.setSeccional_int(seccional_int);
			filtro.setSeccional_nombre(seccional_nombre);
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setEstado(estado);
			
			
		try {	
			//SI TIENE UNA SECCIONAL FIJADA LA SETEO
			seccional_int=seccionalDefecto!=null&&!seccionalDefecto.trim().equals("")?Integer.parseInt(seccionalDefecto):seccional_int;
			//Y LUEGO BUSCO
			busqueda = PreAfiliadoServiceUtil.getBusquedaPreAfiliados(filtro);
			
			cantResultados = busqueda.size()>0?busqueda.get(0).getTotal_registros():0;
			
			session.removeAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO);
			session.removeAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS);
			
			session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS, filtro);
			session.setAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO, busqueda);
			
			if(busqueda != null && busqueda.size() > 0){
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS, cantResultados);
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, pagina_sel);
			}else{
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS,0 );
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, 0);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.pre.carga.afiliados.result.search");
	}

}