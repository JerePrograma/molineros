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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;

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
		final String rid = "BAC#" + System.currentTimeMillis() + "-" + Math.abs((int)(Math.random() * 100000));
		final boolean dbg = _log.isDebugEnabled();

		if (dbg) {
			_log.debug("[" + rid + "][RENDER][START] Inicio render BuscarAfiliadosComponenteAction");
		}

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

			if (dbg) {
				_log.debug("[" + rid + "][PARAMS][RAW] cuil=" + renderRequest.getParameter("cuil")
						+ ", inte=" + renderRequest.getParameter("inte")
						+ ", tipoDoc=" + renderRequest.getParameter("tipoDoc")
						+ ", nroDoc=" + renderRequest.getParameter("nroDoc")
						+ ", seccional=" + renderRequest.getParameter("seccional")
						+ ", apellido=" + renderRequest.getParameter("apellido")
						+ ", nombre=" + renderRequest.getParameter("nombre")
						+ ", entidad=" + renderRequest.getParameter("entidad")
						+ ", numero_afi=" + renderRequest.getParameter("numero_afi")
						+ ", reintegro_reclamo=" + renderRequest.getParameter("reintegro_reclamo")
						+ ", nroSocioPrevencion=" + renderRequest.getParameter("nroSocioPrevencion")
						+ ", nroCredencialPrevencion=" + renderRequest.getParameter("nroCredencialPrevencion")
						+ ", fecha_referencia=" + renderRequest.getParameter("fecha_referencia")
						+ ", popup=" + renderRequest.getParameter("popup")
						+ ", origen=" + renderRequest.getParameter("origen"));
			}

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
					if (dbg) {
						_log.debug("[" + rid + "][PARSE][SECCIONAL] Parse OK. seccional=" + seccional + " -> seccional_int=" + seccional_int);
					}
				} catch (NumberFormatException e) {
					seccional_int = 0;
					_log.warn("[" + rid + "][PARSE][SECCIONAL] No se pudo parsear seccional='" + seccional + "'. Se usará 0", e);
				}
			} else {
				if (dbg) {
					_log.debug("[" + rid + "][PARSE][SECCIONAL] No vino seccional. Se usa seccional_int=0");
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
					if (dbg) {
						_log.debug("[" + rid + "][PARSE][NRO_SOCIO_PREV] Parse OK. nroSocioPrev=" + nroSocioPrev);
					}
				} catch (NumberFormatException e) {
					nroSocioPrev = 0;
					_log.warn("[" + rid + "][PARSE][NRO_SOCIO_PREV] No se pudo parsear nroSocioPrevencion='"
							+ renderRequest.getParameter("nroSocioPrevencion") + "'. Se usará 0", e);
				}
			}

			if (null != renderRequest.getParameter("nroCredencialPrevencion")) {
				try {
					nroCredenPrev = renderRequest.getParameter("nroCredencialPrevencion").trim().length() > 0
							? new BigDecimal(renderRequest.getParameter("nroCredencialPrevencion"))
							: new BigDecimal(0);
					if (dbg) {
						_log.debug("[" + rid + "][PARSE][NRO_CREDEN_PREV] Parse OK. nroCredenPrev=" + nroCredenPrev);
					}
				} catch (NumberFormatException e) {
					nroCredenPrev = new BigDecimal(0);
					_log.warn("[" + rid + "][PARSE][NRO_CREDEN_PREV] No se pudo parsear nroCredencialPrevencion='"
							+ renderRequest.getParameter("nroCredencialPrevencion") + "'. Se usará 0", e);
				}
			}

			if (dbg) {
				_log.debug("[" + rid + "][PARAMS][NORMALIZED] cuil=" + cuil
						+ ", inte=" + inte
						+ ", tipoDoc=" + tipoDoc
						+ ", nroDoc=" + nroDoc
						+ ", seccional=" + seccional
						+ ", seccional_int=" + seccional_int
						+ ", apellido=" + apellido
						+ ", nombre=" + nombre
						+ ", entidad=" + entidad
						+ ", nroAfiliado=" + nroAfiliado
						+ ", nroSocioPrev=" + nroSocioPrev
						+ ", nroCredenPrev=" + nroCredenPrev
						+ ", pagReintegroConReclamo=" + pagReintegroConReclamo);
			}

			SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
			String prestacion_fecha = ParamUtil.getString(renderRequest, "fecha_referencia", null);
			Date prestacionFecha;

			try {
				prestacionFecha = formatoDeFecha.parse(prestacion_fecha);
				if (dbg) {
					_log.debug("[" + rid + "][PARSE][FECHA_REFERENCIA] Parse OK. prestacion_fecha=" + prestacion_fecha
							+ " -> prestacionFecha=" + prestacionFecha);
				}
			} catch (Exception e) {
				prestacionFecha = null;
				if (dbg) {
					_log.debug("[" + rid + "][PARSE][FECHA_REFERENCIA] No se pudo parsear fecha_referencia='"
							+ prestacion_fecha + "'. Se usará prestacionFecha=null");
				}
			}

			if (dbg) {
				_log.debug("[" + rid + "][SERVICE][INIT] Invocando BusquedaAfiliadoServiceUtil.getInstance()");
			}
			BusquedaAfiliadoServiceUtil.getInstance();

			List<Afiliado> busqueda;

			if (prestacionFecha == null) {
				if (dbg) {
					_log.debug("[" + rid + "][FLOW] prestacionFecha == null -> flujo SIN fecha de referencia");
				}

				if (pagReintegroConReclamo == 1) {
					if (dbg) {
						_log.debug("[" + rid + "][FLOW] pagReintegroConReclamo == 1 -> invoca getBusquedaAfiliadosComponenteReintegro(...) SIN fecha");
					}

					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteReintegro(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, nroSocioPrev, nroCredenPrev);

				} else {
					if (dbg) {
						_log.debug("[" + rid + "][FLOW] pagReintegroConReclamo != 1 -> invoca getBusquedaAfiliadosComponente(...) SIN fecha");
					}

					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, nroSocioPrev, nroCredenPrev);
				}

				if (dbg) {
					_log.debug("[" + rid + "][REQUEST][SET] Limpiando y seteando attribute BUSQUEDA_AFILIADO");
				}
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda);

			} else {
				if (dbg) {
					_log.debug("[" + rid + "][FLOW] prestacionFecha != null -> flujo CON fecha de referencia");
				}

				if (pagReintegroConReclamo == 1) {
					if (dbg) {
						_log.debug("[" + rid + "][FLOW] pagReintegroConReclamo == 1 -> invoca getBusquedaAfiliadosComponenteReintegro(...) CON fecha");
					}

					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteReintegro(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, prestacionFecha, nroCredenPrev, nroSocioPrev);

				} else {
					if (dbg) {
						_log.debug("[" + rid + "][FLOW] pagReintegroConReclamo != 1 -> invoca getBusquedaAfiliadosComponente(...) CON fecha");
					}

					busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
							cuil, inte, tipoDoc, nroDoc, seccional_int, apellido, nombre,
							entidad, nroAfiliado, prestacionFecha, nroSocioPrev, nroCredenPrev);
				}

				if (dbg) {
					_log.debug("[" + rid + "][REQUEST][SET] Limpiando y seteando attribute BUSQUEDA_AFILIADO");
				}
				renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
				renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO, busqueda);
			}

			if (dbg) {
				_log.debug("[" + rid + "][RESULT] busqueda size=" + (busqueda != null ? busqueda.size() : -1));
				if (busqueda != null && !busqueda.isEmpty()) {
					Afiliado first = busqueda.get(0);
					_log.debug("[" + rid + "][RESULT][FIRST] cuil=" + first.getCuil_titular()
							+ ", inte=" + first.getInte()
							+ ", apellido=" + first.getApellido()
							+ ", nombre=" + first.getNombre()
							+ ", antecedentes=" + first.getTieneAntecedentesJudiciales());
				}
			}

		} catch (Exception e) {
			_log.error("[" + rid + "][ERROR] Error en render BuscarAfiliadosComponenteAction", e);
			e.printStackTrace();
		}

		popup = ParamUtil.getString(renderRequest, "popup");
		String origen = ParamUtil.getString(renderRequest, "origen");
		renderRequest.setAttribute("origen", origen);

		if (dbg) {
			_log.debug("[" + rid + "][FORWARD] popup=" + popup + ", origen=" + origen);
		}

		if (null != popup && !popup.trim().equals("")) {
			if (dbg) {
				_log.debug("[" + rid + "][FORWARD] -> portlet.afiliados.result.search.popup");
			}
			return mapping.findForward("portlet.afiliados.result.search.popup");
		} else {
			if (dbg) {
				_log.debug("[" + rid + "][FORWARD] -> portlet.afiliados.result.search");
			}
			return mapping.findForward("portlet.afiliados.result.search");
		}
	}

}