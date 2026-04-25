package ar.com.ospim.liquidaciones.action;

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

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.liquidaciones.services.ConvenioPrestacionalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * Realiza la b�squeda de contratos seg�n par�metros de entrada
 * 
 * @author Carlos Rivas
 * @modif SVA
 * 
 */
public class BuscarConveniosPrestacAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarConveniosPrestacAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.conv_prestac.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		try {

			String cuit = ParamUtil.getString(renderRequest, "cuit", null);
			String descripcion = ParamUtil.getString(renderRequest,"descripcion", null);
			int idPrestador = ParamUtil.getInteger(renderRequest, "id_prestador", 0);
			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);

			BusquedaConvenioPrestacionalFiltro filtro = new BusquedaConvenioPrestacionalFiltro(cuit, descripcion, idPrestador, estado, 1);
			
			List<ConvenioPrestacional> busqueda = ConvenioPrestacionalServiceUtil.buscarConveniosPrestacionales(filtro);

			session.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO);
			session.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS);
			
			session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO, filtro);
			session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS, busqueda);

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.liquidaciones.conv_prestac.result.search");
	}
}