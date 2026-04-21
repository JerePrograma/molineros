package ar.com.ospim.prestadores.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.services.ConvenioPrestacionalServiceUtil;

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
		_log.info("[BUSCAR-CONV-PREST][ACTION][START] Inicio processAction");
		_log.debug("[BUSCAR-CONV-PREST][ACTION][FORWARD] forward=portlet.liquidaciones.conv_prestac.result.search");
		setForward(actionRequest, "portlet.liquidaciones.conv_prestac.result.search");
		_log.info("[BUSCAR-CONV-PREST][ACTION][END] Fin processAction");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
								PortletConfig portletConfig, RenderRequest renderRequest,
								RenderResponse renderResponse) throws Exception {

		_log.info("[BUSCAR-CONV-PREST][RENDER][START] Inicio render BuscarConveniosPrestacAction");

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		_log.debug("[BUSCAR-CONV-PREST][RENDER][SESSION] sessionId=" + (session != null ? session.getId() : "null"));

		try {

			String cuit = ParamUtil.getString(renderRequest, "cuit", null);
			String descripcion = ParamUtil.getString(renderRequest,"descripcion", null);
			int idPrestador = ParamUtil.getInteger(renderRequest, "id_prestador", 0);
			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);

			_log.debug("[BUSCAR-CONV-PREST][RENDER][PARAMS] cuit=" + cuit
					+ ", descripcion=" + descripcion
					+ ", id_prestador=" + idPrestador
					+ ", estado=" + estado);

			BusquedaConvenioPrestacionalFiltro filtro = new BusquedaConvenioPrestacionalFiltro(cuit, descripcion, idPrestador, estado, 1);
			_log.debug("[BUSCAR-CONV-PREST][RENDER][FILTRO] filtro=" + filtro);

			List<ConvenioPrestacional> busqueda = ConvenioPrestacionalServiceUtil.buscarConveniosPrestacionales(filtro);
			_log.debug("[BUSCAR-CONV-PREST][RENDER][SERVICE] resultadosBusqueda=" + (busqueda != null ? busqueda.size() : "null"));

			session.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO);
			session.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS);
			_log.debug("[BUSCAR-CONV-PREST][RENDER][SESSION] Se limpian atributos previos de búsqueda");

			session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_FILTRO, filtro);
			session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS, busqueda);
			_log.debug("[BUSCAR-CONV-PREST][RENDER][SESSION] Se guardan filtro y resultados en session");

		} catch (Exception e) {
			_log.error("[BUSCAR-CONV-PREST][RENDER][ERROR] Error en búsqueda de convenios prestacionales", e);
		}

		_log.info("[BUSCAR-CONV-PREST][RENDER][END] Fin render. Forward=portlet.liquidaciones.conv_prestac.result.search");
		return mapping.findForward("portlet.liquidaciones.conv_prestac.result.search");
	}
}