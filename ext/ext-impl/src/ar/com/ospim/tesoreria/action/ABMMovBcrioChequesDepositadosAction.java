package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
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
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancoCheque;
import ar.com.ospim.tesoreria.recibos.action.ABMReciboOtrosConceptosAction;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMMovBcrioChequesDepositadosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboOtrosConceptosAction.class);

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

		if (movBcrio.getChequesDepositados() == null) {
			movBcrio
					.setChequesDepositados(new ArrayList<MovimientoBancoCheque>());
		}

		if (StringUtils.checkNotEmpty(renderRequest.getParameter("cuit"))
				|| StringUtils.checkNotEmpty(renderRequest
						.getParameter("numero"))) {
			buscarPorParametro(renderRequest, movBcrio, entidad);
		} else {
			String borrar = renderRequest.getParameter("borrar");
			String rechazar = renderRequest.getParameter("rechazar");
			if (borrar != null && borrar.equals("borrar")) {
				borrarCheque(renderRequest, movBcrio);
			} else if (rechazar != null && rechazar.equals("rechazar")) {
				rechazarCheque(renderRequest, movBcrio);
			} else if (rechazar != null && rechazar.equals("deshacer")) {
				deshacerRechazoCheque(renderRequest, movBcrio);
			} else {
				agregarCheque(renderRequest, movBcrio, entidad);
			}
		}

		Collections.sort(movBcrio.getChequesDepositados(),
				new Comparator<MovimientoBancoCheque>() {
					public int compare(MovimientoBancoCheque o1,
							MovimientoBancoCheque o2) {
						return o1.getCheque().getFecha().compareTo(
								o2.getCheque().getFecha());
					}
				});

		session.setAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION, movBcrio);

		return mapping
				.findForward("portlet.tesoreria.mov_bcrio.cheques_depositados.result");
	}

	private void deshacerRechazoCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesDepositados = movBcrio
				.getChequesDepositados();
		int indexOf = chequesDepositados.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		chequesDepositados.get(indexOf).getCheque().setEstado(
				TraeListasServiceUtil.getEstadoChequeDepositado(renderRequest));
	}

	private void rechazarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesDepositados = movBcrio
				.getChequesDepositados();
		int indexOf = chequesDepositados.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		chequesDepositados.get(indexOf).getCheque().setEstado(
				TraeListasServiceUtil.getEstadoChequeRechazado(renderRequest));
	}

	private void borrarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesDepositados = movBcrio
				.getChequesDepositados();

		int indexOf = chequesDepositados.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		if (chequesDepositados.get(indexOf).getId() < 0) {
			chequesDepositados.remove(indexOf);
		} else {
			chequesDepositados.get(indexOf).setBorradoLogico(true);
			chequesDepositados.get(indexOf).getCheque().setEstado(
					TraeListasServiceUtil
							.getEstadoChequeDepositado(renderRequest));
		}
	}

	private void agregarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio, int entidad) throws SystemException {

		for (MovimientoBancoCheque mb : movBcrio.getChequesDepositados()) {
			mb.setBorradoLogico(false);
		}

		int id = obtenerUltimoId(movBcrio.getChequesDepositados());
		List<Cheque> cheques = ChequeServiceUtil.getChequesDepositados(entidad);
		for (Cheque ch : cheques) {
			int index = listContains(movBcrio.getChequesDepositados(), ch);
			if (index == -1) {
				MovimientoBancoCheque mbch = new MovimientoBancoCheque();
				mbch.setCheque(ch);
				mbch.setId(--id);
				movBcrio.getChequesDepositados().add(mbch);
			}
		}
	}

	private int obtenerUltimoId(List<MovimientoBancoCheque> chequesDepositados) {
		int i = 0;
		for (MovimientoBancoCheque mbch : chequesDepositados) {
			if (i > mbch.getId()) {
				i = mbch.getId();
			}
		}
		return i;
	}

	private int listContains(List<MovimientoBancoCheque> chequesDepositados,
			Cheque ch) {
		if (chequesDepositados != null) {
			for (int i = 0; i < chequesDepositados.size(); i++) {
				if (chequesDepositados.get(i).getCheque().equals(ch)) {
					return i;
				}
			}
		}
		return -1;
	}

	private void buscarPorParametro(RenderRequest renderRequest,
			MovimientoBancario movBcrio, int entidad) throws SystemException {

		String cuit = null;
		String numero = null;

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit")
					: null;
		}

		if (null != renderRequest.getParameter("numero")) {
			numero = renderRequest.getParameter("numero").trim().length() > 0 ? renderRequest
					.getParameter("numero")
					: null;
		}
		BigDecimal numeroBigD = null;
		if (numero != null) {
			numeroBigD = new BigDecimal(numero);
		}
		int id = obtenerUltimoId(movBcrio.getChequesDepositados());
		Cheque cheque = new Cheque();
		cheque.setNumero(numeroBigD);
		cheque.setCuit(cuit);
		List<Cheque> lista = ChequeServiceUtil.getCheques(cheque, entidad);
		for (Cheque ch : lista) {
			int index = listContains(movBcrio.getChequesDepositados(), ch);
			if (index == -1 && ch.getBaja_fecha() == null
					&& ch.getEstado().getId() == Cheque.Estado.DEPOSITADO) {
				MovimientoBancoCheque mbch = new MovimientoBancoCheque();
				mbch.setCheque(ch);
				mbch.setId(--id);
				movBcrio.getChequesDepositados().add(mbch);
			} else if(index==-1) {}else {
				if (index != -1) {
					movBcrio.getChequesDepositados().get(index)
							.setBorradoLogico(false);
				}
			}
		}

	}
}
