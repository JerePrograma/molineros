package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancoReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.recibos.action.ABMChequesASustituirAction;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMMovBcrioEfectivosRecibidosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMChequesASustituirAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;			
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		MovimientoBancario movBcrio = (MovimientoBancario) session
				.getAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION);
		if (movBcrio == null) {
			movBcrio = new MovimientoBancario();
		}

		if (movBcrio.getEfectivoRecibido() == null) {
			movBcrio
					.setEfectivoRecibido(new ArrayList<MovimientoBancoReciboIngreso>());
		}

		String borrar = renderRequest.getParameter("borrar");
		String depositar = renderRequest.getParameter("depositar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarEfectivo(renderRequest, movBcrio);
		} else if (depositar != null && depositar.equals("depositar")) {
			depositarEfectivo(renderRequest, movBcrio);
		} else if (depositar != null && depositar.equals("deshacer")) {
			deshacerDepositoEfectivo(renderRequest, movBcrio);
		} else {
			agregarEfectivo(renderRequest, movBcrio, entidad);
		}

		Collections.sort(movBcrio.getEfectivoRecibido(),
				new Comparator<MovimientoBancoReciboIngreso>() {
					public int compare(MovimientoBancoReciboIngreso o1,
							MovimientoBancoReciboIngreso o2) {
						return o1.getReciboIngreso().getIngreso().getFecha()
								.compareTo(
										o2.getReciboIngreso().getIngreso()
												.getFecha());
					}
				});

		session.setAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION, movBcrio);

		return mapping
				.findForward("portlet.tesoreria.mov_bcrio.efectivos_recibidos.result");
	}

	private void deshacerDepositoEfectivo(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");

		int indexOf = movBcrio.getEfectivoRecibido().indexOf(
				new MovimientoBancoReciboIngreso(Integer.parseInt(id)));
		MovimientoBancoReciboIngreso mbri = movBcrio.getEfectivoRecibido().get(
				indexOf);
		((Efectivo) mbri.getReciboIngreso().getIngreso())
				.setEstado(TraeListasServiceUtil
						.getEstadoEfectivoRecibido(renderRequest));

	}

	private void depositarEfectivo(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");

		int indexOf = movBcrio.getEfectivoRecibido().indexOf(
				new MovimientoBancoReciboIngreso(Integer.parseInt(id)));
		MovimientoBancoReciboIngreso mbri = movBcrio.getEfectivoRecibido().get(
				indexOf);
		((Efectivo) mbri.getReciboIngreso().getIngreso())
				.setEstado(TraeListasServiceUtil
						.getEstadoEfectivoDepositado(renderRequest));

	}

	private void borrarEfectivo(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");
		int indexOf = movBcrio.getEfectivoRecibido().indexOf(
				new MovimientoBancoReciboIngreso(Integer.parseInt(id)));
		if (movBcrio.getEfectivoRecibido().get(indexOf).getId() < 0) {
			movBcrio.getEfectivoRecibido().remove(indexOf);
		} else {
			movBcrio.getEfectivoRecibido().get(indexOf).setBorradoLogico(true);
			Efectivo ef = (Efectivo) movBcrio.getEfectivoRecibido()
					.get(indexOf).getReciboIngreso().getIngreso();
			ef.setEstado(TraeListasServiceUtil
					.getEstadoEfectivoRecibido(renderRequest));
		}

	}

	private void agregarEfectivo(RenderRequest renderRequest,
			MovimientoBancario movBcrio, int entidad) throws SystemException {
		List<ReciboIngreso> efectivos = ReciboServiceUtil
				.getEfectivosRecibidos(entidad);
		Estado estadoEfectivoRecibido = TraeListasServiceUtil
				.getEstadoEfectivoRecibido(renderRequest);
		for (MovimientoBancoReciboIngreso mb : movBcrio.getEfectivoRecibido()) {
			mb.setBorradoLogico(false);
		}

		int id = obtenerUltimoId(movBcrio.getEfectivoRecibido());
		for (ReciboIngreso ri : efectivos) {
			int index = listContains(movBcrio.getEfectivoRecibido(), ri);
			if (index == -1) {
				MovimientoBancoReciboIngreso mbri = new MovimientoBancoReciboIngreso();
				mbri.setReciboIngreso(ri);
				mbri.setId(--id);
				movBcrio.getEfectivoRecibido().add(mbri);
				((Efectivo) ri.getIngreso()).setEstado(estadoEfectivoRecibido);
			}
		}

	}

	private int obtenerUltimoId(
			List<MovimientoBancoReciboIngreso> efectivoRecibido) {
		int i = 0;
		for (MovimientoBancoReciboIngreso mbri : efectivoRecibido) {
			if (i > mbri.getId()) {
				i = mbri.getId();
			}
		}
		return i;
	}

	private int listContains(
			List<MovimientoBancoReciboIngreso> efectivoRecibido,
			ReciboIngreso ri) {
		if (efectivoRecibido != null) {
			for (int i = 0; i < efectivoRecibido.size(); i++) {
				if (efectivoRecibido.get(i).getReciboIngreso().getId() == ri
						.getId()) {
					return i;
				}
			}
		}
		return -1;
	}
}
