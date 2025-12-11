package ar.com.ospim.rrhh.action ;

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

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.rrhh.WebKeysRrhh;
import ar.com.ospim.rrhh.beans.BusquedaTarjetasFiltro;
import ar.com.ospim.rrhh.beans.ItemTarjetasTotal;
import ar.com.ospim.rrhh.services.TarjetasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarTarjetasAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarTarjetasAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.rrhh.tarjetas.result.search");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
			
			int pagina  = ParamUtil.getInteger(renderRequest, "pagina_sel", 0);
			int nroCard = ParamUtil.getInteger(renderRequest, "nroCard", 0);
			String nombre = ParamUtil.getString(renderRequest, "nombre", null);			
			String apellido = ParamUtil.getString(renderRequest, "apellido", null);
			String entidad = ParamUtil.getString(renderRequest, "entidad", null);
			String sector= ParamUtil.getString(renderRequest, "sector", null);
			int  legajo = ParamUtil.getInteger(renderRequest, "legajopersona", 0);
			
			BusquedaTarjetasFiltro busquedaTarjetaFiltro = new BusquedaTarjetasFiltro (nroCard , nombre , apellido, entidad , sector , pagina , legajo);
			     
			List<ItemTarjetasTotal> busqueda = TarjetasServiceUtil.buscarTarjetasTotales(busquedaTarjetaFiltro );
			
			if (busqueda.size()>0){
				busquedaTarjetaFiltro.setTotalRegistros(busqueda.get(0).getTotal_registros() );
			}else{
				busquedaTarjetaFiltro.setTotalRegistros(0);
			}			
			busquedaTarjetaFiltro.setPagina(pagina);			
			
			renderRequest.removeAttribute(WebKeysRrhh.BUSQUEDA_REGISTROS_TARJETAS);
			renderRequest.setAttribute(	WebKeysRrhh.BUSQUEDA_REGISTROS_TARJETAS,	busqueda);
			session.removeAttribute(WebKeysRrhh.FILTRO_BUSQUEDA_REGISTROS_TARJETAS);	
			session.setAttribute(WebKeysRrhh.FILTRO_BUSQUEDA_REGISTROS_TARJETAS, busquedaTarjetaFiltro );
				
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.rrhh.tarjetas.result.search");
		                      		                      
	}
}




