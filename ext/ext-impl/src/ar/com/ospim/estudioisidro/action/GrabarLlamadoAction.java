package ar.com.ospim.estudioisidro.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.global.services.GoogleCalendarUtil;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.EstadoGestion;
import ar.com.ospim.estudioisidro.beans.Llamado;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class GrabarLlamadoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarLlamadoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();

		String accion = ParamUtil.getString(renderRequest, "accion");

		LlamadosEstudio llest = (LlamadosEstudio) portletSession.getAttribute(
				WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,
				PortletSession.APPLICATION_SCOPE);
		User user = PortalUtil.getUser(renderRequest);
		int cursor = 0;
		int size = 0;
		try {
			size = Integer.parseInt((String) renderRequest.getAttribute("total"));
		} catch (NumberFormatException nfe) {
			size = 0;
		}

		if (null != renderRequest.getParameter("cur")
				&& !"".equals(renderRequest.getParameter("cur"))) {
			cursor = Integer.parseInt(renderRequest.getParameter("cur"));
		}
		String[] email = null;
		boolean molinera = ParamUtil.getBoolean(renderRequest, "molinera");
		int id = ParamUtil.getInteger(renderRequest, "id");
		try {
			if (accion.equals("borrar")) {
				Llamado llamado = borrarLlamadoLista(llest, id,
						user.getScreenName());
				if (null != llamado.getFechaAgenda()) {
					email = new String[] { (CrmServiceUtil
							.getNotificacionDerivacion(user.getScreenName())
							.getDerivacionEmail()) };
					com.google.api.services.calendar.Calendar calendar = GoogleCalendarUtil
							.getCalendarService(email[0]);
					GoogleCalendarUtil.deleteEvents(calendar,
							llamado.getGoogleEvent());
				}
			} else {

				Llamado llamado = getLlamado(renderRequest,
						user.getScreenName());

				if (null != llamado) {
					Llamado llamadoEditar = null;
					if (id != 0 && accion.equals("borrar")) {
						llamadoEditar = borrarLlamadoLista(llest, id, user.getScreenName());
					}
					if (null != llamado.getFechaAgenda()) {
						
						String agendaEvent = null;
						email = new String[] { (CrmServiceUtil
								.getNotificacionDerivacion(user.getScreenName())
								.getDerivacionEmail()) };
						_log.debug("Instanciando GoogleCalendarUtil");
						email[0] = "info@ospim.org.ar";

//						com.google.api.services.calendar.Calendar calendar = GoogleCalendarUtil
//								.getCalendarService(email[0]);
//						_log.debug("getApplicationName: " + calendar.getApplicationName());
//
//						
//						if (id != 0) {
//							agendaEvent = GoogleCalendarUtil
//									.updateOrCreateEvent(calendar,
//											llamado.getGoogleEvent(),
//											llamado.getFechaAgenda(),
//											llamado.getObservaciones(),
//											"OSPIM",
//											llamado.getObservaciones(), email);
//						} else {
//							agendaEvent = GoogleCalendarUtil.createEvent(
//									calendar, llamado.getFechaAgenda(),
//									llamado.getObservaciones(), "OSPIM",
//									llamado.getObservaciones(), email);
//						}
//						llamado.setGoogleEvent(agendaEvent);
						
						
						
					}
					if (id != 0) {
						LlamadoServiceUtil.actualizaLlamado(llamado);
					} else {
						llamado.setId(LlamadoServiceUtil.grabaLlamado(llamado, molinera));
					}

					llest.setLlamados(LlamadoServiceUtil.getLlamadosList(llest.getEmpresa().getCuit(), cursor, null));

				}

				if (size == 0) {
					size = llest.getLlamados().size();
				}

				renderRequest.setAttribute("total", size);
				renderRequest.setAttribute("cur", cursor);
			}

			portletSession.setAttribute(
					WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO, llest,
					PortletSession.APPLICATION_SCOPE);

		} catch (Exception e) {
			_log.error(e);
			return mapping.findForward("portlet.estudio_isidro.error");
		}
		return mapping.findForward("portlet.estudio_isidro.result.search");
	}

	public Llamado borrarLlamadoLista(LlamadosEstudio llest, int id, String user)
			throws Exception {
		List<Llamado> llamados = llest.getLlamados();
		Llamado result = null;
		if (null != llamados) {
			for (Iterator<Llamado> it = llamados.iterator(); it.hasNext();) {
				Llamado llamado = it.next();
				if (llamado.getId() == id) {
					LlamadoServiceUtil.bajaLlamado(id, user);
					result = llamado;
					it.remove();
				}
			}
		}
		return result;
	}

	public Llamado getLlamado(PortletRequest renderRequest, String user)
			throws Exception {

		Llamado llamado = null;
		String fechaLlamadoString = renderRequest.getParameter("fechaLlamado");
		String fechaAgendaString = renderRequest.getParameter("fechaAgenda");
		int horaAgenda = ParamUtil.getInteger(renderRequest, "horaAgenda");
		int minutoAgenda = ParamUtil.getInteger(renderRequest, "minutoAgenda");
		int id = ParamUtil.getInteger(renderRequest, "id");
		String idEvent = ParamUtil.getString(renderRequest, "googleEvent");

		Date fechaLlamado = null;
		Date fechaAgenda = null;
		String cuit = null;
		if (null != fechaLlamadoString) {
			fechaLlamado = DateUtils.parse(fechaLlamadoString, DateUtils.SHORT);
		}
		if (null != fechaAgendaString) {
			try {
				fechaAgenda = DateUtils.parse(fechaAgendaString,
						DateUtils.SHORT);
			} catch (Exception e) {

			}
		}
		Calendar fechaCalendar = DateUtils.getCalendarGMTMenos3();
		Calendar fechaCalendarFinal = DateUtils.getCalendarGMTMenos3();
		fechaCalendar.setTime(fechaLlamado);
		fechaCalendarFinal.set(fechaCalendar.get(Calendar.YEAR),
				fechaCalendar.get(Calendar.MONTH),
				fechaCalendar.get(Calendar.DATE));
		if (null != fechaAgenda) {
			
			Calendar fechaAgendaCal =  DateUtils.getCalendarGMTMenos3();
			fechaAgendaCal.setTime(fechaAgenda);
			fechaAgendaCal.set(Calendar.HOUR_OF_DAY, horaAgenda);
			fechaAgendaCal.set(Calendar.MINUTE, minutoAgenda);
			fechaAgenda = fechaAgendaCal.getTime();
			
		}

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit") : null;
		}
		String observaciones = renderRequest.getParameter("observaciones");

		String estado=ParamUtil.getString(renderRequest, "estadoLlamada", "ABIERTO");
		Integer idEstadoGestion=ParamUtil.getInteger(renderRequest, "estadoGestion");
		String tipoContacto = renderRequest.getParameter("tipoContacto");
		String tipoLote = ParamUtil.getString(renderRequest, "tipoLote");
		
		String numLoteExiste = ParamUtil.getString(renderRequest,"nroLote", null) ;
//		Integer nroLote = ParamUtil.getInteger(renderRequest,"nroLote");
		Integer nroLote = null;
		try{
			nroLote = Integer.parseInt(numLoteExiste);
		}catch(NumberFormatException e){
			nroLote = null;
			tipoLote = "";
		}
				
		String cartaDoc = ParamUtil.getString(renderRequest, "cartaDoc");
		String ubicacionCarpeta = renderRequest
				.getParameter("ubicacionCarpeta");

		if (null != cuit && null != fechaLlamado && null != observaciones) {
			llamado = new Llamado();
			llamado.setObservaciones(observaciones);
			llamado.setCuit(cuit);
			llamado.setEstado(estado);
			llamado.setEstadoGestion(new EstadoGestion(idEstadoGestion, null));
			llamado.setTipoContacto(tipoContacto);
			llamado.setCartaDocumento(cartaDoc);
			llamado.setUbicacionCarpeta(ubicacionCarpeta);
			llamado.setFecha(fechaCalendarFinal.getTime());
			llamado.setUser(user);
			llamado.setFechaAgenda(fechaAgenda);
			llamado.setId(id);
			llamado.setGoogleEvent(idEvent);
			llamado.setLote(nroLote);
			llamado.setTipoLote(tipoLote);
			llamado.setTipoContacto("TELEFONICO");
		}
		return llamado;

	}

}
