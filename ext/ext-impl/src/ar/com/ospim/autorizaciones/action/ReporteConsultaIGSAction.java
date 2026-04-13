package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.BusquedaConsultasIGSFiltro;
import ar.com.ospim.autorizaciones.beans.ConsultaIGSTotal;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ReporteConsultaIGSAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		Boolean paginaFiltros = ParamUtil.getBoolean(renderRequest, "ir_a_filtro"); 
		
		if(paginaFiltros){
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.consultas_igs.search"));
		}
		
		String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
		String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
		
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
		
		BusquedaConsultasIGSFiltro filtro = new BusquedaConsultasIGSFiltro(fechaDesde, fechaHasta, pagina_sel);
		
		List<ConsultaIGSTotal> busqueda = BusquedaAfiliadoServiceUtil.buscarConsultasIGS(filtro);
		
		// la lista en el request
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		session.removeAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS);
		session.removeAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS_FILTRO);
		
		session.setAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS, busqueda);
		session.setAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS_FILTRO, filtro);
		
		if(busqueda != null && busqueda.size() > 0){
			session.setAttribute("total_registros", busqueda.get(0).getTotal_registros());
			session.setAttribute("offset_reg", pagina_sel);
		}else{
			session.setAttribute("total_registros",0 );
			session.setAttribute("offset_reg", 0);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.autorizaciones.reportes.consultas_igs"));
	}
}