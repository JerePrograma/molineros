/**
 */

package ar.com.ospim.afiliados.action;

import java.math.BigDecimal;
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

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.global.beans.Empresa;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarAfiliadosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la b�squeda de afiliados seg�n par�metros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarAfiliadosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarAfiliadosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
								PortletConfig portletConfig, RenderRequest renderRequest,
								RenderResponse renderResponse) throws Exception {

		String popup = null;
		
		try {
			String cuil = null;
			String inte = null;
			String tipoDoc = null;
			String nroDoc = null;
			String seccional = null;
			int seccional_int = 0;
			String apellido = null;
			String nombre = null;
			String entidad = null;
			int nroAfiliado = 0;
			int nroSocioPrev = 0;
			int pagReintegroConReclamo = 1;

			BigDecimal nroCredenPrev = new BigDecimal(0);

			if (null != renderRequest.getParameter("cuil")) {
				cuil = renderRequest.getParameter("cuil").trim().length() > 0
						? renderRequest.getParameter("cuil")
						: null;
			}
			if (null != renderRequest.getParameter("inte")) {
				inte = renderRequest.getParameter("inte").trim().length() > 0
						? renderRequest.getParameter("inte")
						: null;
			}
			if (null != renderRequest.getParameter("tipoDoc")) {
				tipoDoc = renderRequest.getParameter("tipoDoc").trim().length() > 0
						? renderRequest.getParameter("tipoDoc")
						: null;
			}
			if (null != renderRequest.getParameter("nroDoc")) {
				nroDoc = renderRequest.getParameter("nroDoc").trim().length() > 0
						? renderRequest.getParameter("nroDoc")
						: null;
			}
			if (null != renderRequest.getParameter("seccional")) {
				seccional = renderRequest.getParameter("seccional").trim().length() > 0
						? renderRequest.getParameter("seccional")
						: null;
			}
			if (null != seccional) {
				try {
					seccional_int = Integer.parseInt(seccional);
				} catch (NumberFormatException e) {
					seccional_int = 0;
				}
			} 

			if (null != renderRequest.getParameter("apellido")) {
				apellido = renderRequest.getParameter("apellido").trim().length() > 0
						? renderRequest.getParameter("apellido")
						: null;
			}
			if (null != renderRequest.getParameter("nombre")) {
				nombre = renderRequest.getParameter("nombre").trim().length() > 0
						? renderRequest.getParameter("nombre")
						: null;
			}

			entidad = ParamUtil.getString(renderRequest, "entidad", null);
			nroAfiliado = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			pagReintegroConReclamo = ParamUtil.getInteger(renderRequest, "reintegro_reclamo", 0);

			if (null != renderRequest.getParameter("nroSocioPrevencion")) {
				try {
					nroSocioPrev = renderRequest.getParameter("nroSocioPrevencion").trim().length() > 0
							? Integer.parseInt(renderRequest.getParameter("nroSocioPrevencion"))
							: 0;
				} catch (NumberFormatException e) {
					nroSocioPrev = 0;
				}
			}

			if (null != renderRequest.getParameter("nroCredencialPrevencion")) {
				try {
					nroCredenPrev = renderRequest.getParameter("nroCredencialPrevencion").trim().length() > 0
							? new BigDecimal(renderRequest.getParameter("nroCredencialPrevencion"))
							: new BigDecimal(0);
				} catch (NumberFormatException e) {
					nroCredenPrev = new BigDecimal(0);
				}
			}

			SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			String prestacion_fecha = ParamUtil.getString(renderRequest, "fecha_referencia", null);
			Date prestacionFecha;

			try {
				prestacionFecha = formatoDeFecha.parse(prestacion_fecha);
			} catch (Exception e) {
				prestacionFecha = null;
			}

			BusquedaAfiliadoServiceUtil.getInstance();

			List<Afiliado> busqueda;

			if (prestacionFecha == null) {
				if (pagReintegroConReclamo == 1) {
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteReintegro(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, nroSocioPrev, nroCredenPrev);

				} else {
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, nroSocioPrev, nroCredenPrev);
				}
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda);
				renderRequest.getPortletSession().setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda, PortletSession.APPLICATION_SCOPE);
				
				
			} else {
				if (pagReintegroConReclamo == 1) {
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteReintegro(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, prestacionFecha, nroCredenPrev, nroSocioPrev);
				} else {
					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, prestacionFecha, nroSocioPrev, nroCredenPrev);
				}
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda);
				renderRequest.getPortletSession().setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda, PortletSession.APPLICATION_SCOPE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		popup = ParamUtil.getString(renderRequest, "popup");
		String origen = ParamUtil.getString(renderRequest, "origen");
		renderRequest.setAttribute("origen", origen);


		if (null != popup && !popup.trim().equals("")) {
			return mapping.findForward("portlet.afiliados.result.search.popup");
		} else {
			return mapping.findForward("portlet.afiliados.result.search");
		}
	}

}