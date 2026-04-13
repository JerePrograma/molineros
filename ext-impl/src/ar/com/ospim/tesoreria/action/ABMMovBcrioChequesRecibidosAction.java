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
import ar.com.ospim.tesoreria.ChequeRechazadoException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancoCheque;
import ar.com.ospim.tesoreria.recibos.action.ABMReciboOtrosConceptosAction;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMMovBcrioChequesRecibidosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboOtrosConceptosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
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

		if (movBcrio.getChequesRecibidos() == null) {
			movBcrio
					.setChequesRecibidos(new ArrayList<MovimientoBancoCheque>());
		}

		try {
			if (StringUtils.checkNotEmpty(renderRequest.getParameter("cuit"))
					|| StringUtils.checkNotEmpty(renderRequest
							.getParameter("numero"))) {
				buscarPorParametro(renderRequest, movBcrio, entidad);
			} else {
				String borrar = renderRequest.getParameter("borrar");
				String depositar = renderRequest.getParameter("depositar");
				if (borrar != null && borrar.equals("borrar")) {
					borrarCheque(renderRequest, movBcrio);
				} else if (depositar != null && depositar.equals("depositar")) {
					depositarCheque(renderRequest, movBcrio);
				} else if (depositar != null && depositar.equals("deshacer")) {
					deshacerDepositoCheque(renderRequest, movBcrio);
				} else {
					agregarCheque(renderRequest, movBcrio, entidad);
				}
			}

			Collections.sort(movBcrio.getChequesRecibidos(),
					new Comparator<MovimientoBancoCheque>() {
						public int compare(MovimientoBancoCheque o1,
								MovimientoBancoCheque o2) {
							return o1.getCheque().getFecha().compareTo(
									o2.getCheque().getFecha());
						}
					});
		} catch (ChequeRechazadoException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (Exception e) {
			throw e;
		}
		session.setAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION, movBcrio);

		return mapping
				.findForward("portlet.tesoreria.mov_bcrio.cheques_recibidos.result");
	}

	private void deshacerDepositoCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) throws ChequeRechazadoException {

		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesRecibidos = movBcrio
				.getChequesRecibidos();
		int indexOf = chequesRecibidos.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		MovimientoBancoCheque movimientoBancoCheque = chequesRecibidos
				.get(indexOf);
		if (movimientoBancoCheque.getCheque().getEstado().equals(
				TraeListasServiceUtil.getEstadoChequeRechazado(renderRequest))) {
			throw new ChequeRechazadoException();
		}
		movimientoBancoCheque.getCheque().setEstado(
				TraeListasServiceUtil.getEstadoChequeRecibido(renderRequest));
	}

	private void depositarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) {
		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesRecibidos = movBcrio
				.getChequesRecibidos();
		int indexOf = chequesRecibidos.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		chequesRecibidos.get(indexOf).getCheque().setEstado(
				TraeListasServiceUtil.getEstadoChequeDepositado(renderRequest));
	}

	private void borrarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio) throws ChequeRechazadoException {
		String id = renderRequest.getParameter("id");
		List<MovimientoBancoCheque> chequesRecibidos = movBcrio
				.getChequesRecibidos();
		int indexOf = chequesRecibidos.indexOf(new MovimientoBancoCheque(
				Integer.parseInt(id)));
		MovimientoBancoCheque movimientoBancoCheque = chequesRecibidos
				.get(indexOf);
		if (movimientoBancoCheque.getCheque().getEstado().equals(
				TraeListasServiceUtil.getEstadoChequeRechazado(renderRequest))) {
			throw new ChequeRechazadoException();
		}
		if (movimientoBancoCheque.getId() < 0) {
			chequesRecibidos.remove(indexOf);
		} else {
			movimientoBancoCheque.setBorradoLogico(true);
			movimientoBancoCheque.getCheque().setEstado(
					TraeListasServiceUtil
							.getEstadoChequeRecibido(renderRequest));
		}

	}

	private void agregarCheque(RenderRequest renderRequest,
			MovimientoBancario movBcrio, int entidad) throws SystemException {

		for (MovimientoBancoCheque mb : movBcrio.getChequesRecibidos()) {
			mb.setBorradoLogico(false);
		}

		int id = obtenerUltimoId(movBcrio.getChequesRecibidos());
		List<Cheque> cheques = ChequeServiceUtil.getChequesRecibidos(entidad);
		for (Cheque ch : cheques) {
			int index = listContains(movBcrio.getChequesRecibidos(), ch);
			if (index == -1) {
				MovimientoBancoCheque mbch = new MovimientoBancoCheque();
				mbch.setCheque(ch);
				mbch.setId(--id);
				movBcrio.getChequesRecibidos().add(mbch);
			}
		}
	}

	private int obtenerUltimoId(List<MovimientoBancoCheque> chequesRecibidos) {
		int i = 0;
		for (MovimientoBancoCheque mbch : chequesRecibidos) {
			if (i < mbch.getId()) {
				i = mbch.getId();
			}
		}
		return i+1;
	}

	private int listContains(List<MovimientoBancoCheque> chequesRecibidos,
			Cheque ch) {
		if (chequesRecibidos != null) {
			for (int i = 0; i < chequesRecibidos.size(); i++) {
				if (chequesRecibidos.get(i).getCheque().equals(ch)) {
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

		
		int id = obtenerUltimoId(movBcrio.getChequesRecibidos());
		List<Cheque> lista =ChequeServiceUtil.getCheques(cuit, Cheque.Estado.RECIBIDO, numeroBigD, null, entidad); 
				
		
		List<MovimientoBancoCheque> listaNueva=null;
		if(movBcrio.getChequesRecibidos()==null){
			listaNueva = new ArrayList<MovimientoBancoCheque>();
		}else{
			listaNueva=movBcrio.getChequesRecibidos();
		}
		for (Cheque ch : lista) {
			MovimientoBancoCheque mbch = new MovimientoBancoCheque();
			mbch.setId(id++);
			
// DS - Prueba de agragado de cheque			
			Integer idDetalle= (int) Math.floor(Math.random()*100)*-1;
			mbch.setId(idDetalle);
// DS - Fin			
			mbch.setCheque(ch);
			listaNueva.add(mbch);
		}
		movBcrio.setChequesRecibidos(listaNueva);

	}
}
