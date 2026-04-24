package ar.com.ospim.tesoreria.actas.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.ActaSinPagosException;
import ar.com.ospim.tesoreria.DuplicateActaIdException;
import ar.com.ospim.tesoreria.FaltaFechaCierreActaException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.InspectorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;


public class EditarActasEntryAction extends ActasBaseAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarActasEntryAction.class);
	private static final String MOSTRAR_PAGOS = "MOSTRAR_PAGOS";
	public static final String CHEQUE_DUPLICADO = "CHEQUE_DUPLICADO";

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		String popup = actionRequest.getParameter("popupActa");
		String popupSeguimiento = actionRequest.getParameter("popupActaSeguimiento");

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Acta acta = updateActaEntry(actionRequest, cmd);
				actionRequest.setAttribute("acta_id",
						String.valueOf(acta.getId()));
				if (cerrarActa(actionRequest)) {
					actionRequest.setAttribute("tabs1", "actas");
					if (actionResponse != null
							&& actionResponse.getNamespace() != null
							&& actionResponse.getNamespace().equals("_EST_1_")) {
						if ((null != popup && popup.trim().equals("true"))||(null != popupSeguimiento && popupSeguimiento.trim().equals("true"))) {
							setForward(actionRequest, "portlet.tesoreria.tabla.resumen");
						}
					} else {
						setForward(actionRequest, "portlet.tesoreria.view");
					}
				}
			}
		} catch (DuplicateActaIdException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			if (cerrarActa(actionRequest)) {
				actionRequest.setAttribute(MOSTRAR_PAGOS, MOSTRAR_PAGOS);
			}
		} catch (ActaSinPagosException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			if (cerrarActa(actionRequest)) {
				actionRequest.setAttribute(MOSTRAR_PAGOS, MOSTRAR_PAGOS);
			}
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute(CHEQUE_DUPLICADO, e.getCheque());
			actionRequest.setAttribute(MOSTRAR_PAGOS, MOSTRAR_PAGOS);
		} catch (FaltaFechaCierreActaException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			if (cerrarActa(actionRequest)) {
				actionRequest.setAttribute(MOSTRAR_PAGOS, MOSTRAR_PAGOS);
			}
		} catch (Exception e) {
			logger.debug("Error al guardar acta", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (cerrarActa(actionRequest)) {
			actionRequest.setAttribute(MOSTRAR_PAGOS, MOSTRAR_PAGOS);
		}

		if (!cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
			if (SessionErrors.isEmpty(actionRequest)) {
				String successMessage = ParamUtil.getString(actionRequest,
						"successMessage");
				SessionMessages.add(actionRequest, "request_processed",
						successMessage);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Acta updateActaEntry(ActionRequest actionRequest, String cmd)
			throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

		if (acta == null) {
			acta = new Acta();
		}
		getActaFromRequest(actionRequest,
				acta);

		if (acta != null) {
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		boolean cerrarActa = cerrarActa(actionRequest);

		if (cerrarActa && acta.getCierre_fecha() == null) {
			throw new FaltaFechaCierreActaException();
		}

		List<InspectorWrapper> inspectores = (ArrayList<InspectorWrapper>) session
				.getAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);

		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			ActaServiceUtil.save(acta, user, inspectores, cerrarActa);
		} else {
			ActaServiceUtil.update(acta, user, inspectores, cerrarActa);
		}
		session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return acta;
	}

	private boolean cerrarActa(ActionRequest actionRequest) {
		boolean cerrarActa = false;
		String cerrarActaStr = actionRequest.getParameter("cerrarActa");
		if (cerrarActaStr != null && cerrarActaStr.equals("cerrarActa")) {
			cerrarActa = true;
		}
		return cerrarActa;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad = WebKeysGlobal.OSPIM;
		
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_EMP_1_")) {
			entidad = WebKeysGlobal.EMPRESA;
		} else if (renderResponse.getNamespace().equals("_EST_1_")) {
			entidad = WebKeysGlobal.ESTUDIO;
		}

		String cuit = ParamUtil.getString(renderRequest, "cuit");
		if(null==cuit || cuit.trim().equals("")){
			cuit=ParamUtil.getString(renderRequest, "cuit_entidadacta_");
		}
		boolean molinera= ParamUtil.getBoolean(renderRequest, "molinera");
		String popupActa = renderRequest.getParameter("popupActa");
		String popupActaSeguimiento = renderRequest.getParameter("popupActaSeguimiento");

		if (renderRequest.getParameter("fromActa") != null
				&& renderRequest.getParameter("fromActa").equals("fromActa")) {
			renderRequest.setAttribute("fromActa", "fromActa");
		}

		InspectorServiceUtil.getInspectores(renderRequest);
		TraeListasServiceUtil.getBancos(renderRequest);

		Acta acta = null;
		String cambioSolapa = renderRequest.getParameter("cambioSolapa");
		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(renderRequest);
		if ((cambioSolapa != null && cambioSolapa.equals("cambioSolapa"))
				|| !SessionErrors.isEmpty(renderRequest)) {
			// vengo de un cambio de solapa dentro de la pantalla de
			// edicion/alta
			String accionOriginal = renderRequest
					.getParameter("accionOriginal");
			if (accionOriginal != null) {
				renderRequest.setAttribute("accionOriginal", accionOriginal);
			}

			acta = (Acta) httpServletRequest.getSession().getAttribute(
					WebKeysTesoreria.ACTA_EN_EDICION);
			if (acta == null) {
				acta = new Acta();
			}
			if (renderRequest.getParameter("tabs1") != null) {
				if (renderRequest.getParameter("tabs1").equals(
						"detalle-acta-inspectores")) {
					getActaFromRequest(renderRequest,acta);
				} else {
					getOtrosDatosFromRequest(
							PortalUtil.getHttpServletRequest(renderRequest),
							acta);
				}
			}
		} else {
			// recien entro a la edicion/alta
			HttpSession session = PortalUtil.getHttpServletRequest(
					renderRequest).getSession();
			session.removeAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
			session.removeAttribute("cuit");
			session.setAttribute("cuit", cuit);
			session.removeAttribute("molinera");
			session.setAttribute("molinera", molinera);
			session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
			acta = getActaEntry(httpServletRequest);
			if (acta != null && acta.getInspectoresFirmantes() != null) {
				List<InspectorWrapper> inspectorWrapperList = AgregarInspectorAction
						.getInspectorWrapperList(acta.getInspectoresFirmantes());
				session.setAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS,
						inspectorWrapperList);
				session.setAttribute("cuit", cuit);
			}
		}

		if (acta != null) {
			httpServletRequest.getSession().setAttribute(
					WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		
		if (entidad==WebKeysGlobal.ESTUDIO && (null != popupActa && popupActa.trim().equals("true")) || (null != popupActaSeguimiento && popupActaSeguimiento.trim().equals("true"))) {
			
			PortletSession portletSession = renderRequest.getPortletSession();
			LlamadosEstudio llest = (LlamadosEstudio) portletSession
					.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
			
			EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest, acta.getEmpresa().getCuit());
			return mapping.findForward("portlet.tesoreria.tabla.resumen");
			
		} else {
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.actas.editar_actas_entry"));
		}
	}
}