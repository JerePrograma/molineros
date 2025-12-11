package ar.com.uoma.actasNoOS.action;

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
import ar.com.ospim.estudioisidro.action.BuscarSeguimientoEmpresaAction;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.ActaSinPagosException;
import ar.com.ospim.tesoreria.DuplicateActaIdException;
import ar.com.ospim.tesoreria.FaltaFechaCierreActaException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.actas.action.AgregarInspectorAction;
import ar.com.ospim.tesoreria.actas.action.InspectorWrapper;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.InspectorServiceUtil;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;


public class EditarActasNoOSEntryAction extends ActasNoOSBaseAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarActasNoOSEntryAction.class);
	private static final String MOSTRAR_PAGOS = "MOSTRAR_PAGOS";
	public static final String CHEQUE_DUPLICADO = "CHEQUE_DUPLICADO";

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(actionResponse.getNamespace().equals("_EST_1_")){
			entidad=WebKeysGlobal.ESTUDIO;
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		String busqueda = ParamUtil.getString(actionRequest, "busqueda");
		actionRequest.setAttribute("busqueda", busqueda);
		
		actionRequest.setAttribute("tabs1", "actas");
		if(actionResponse.getNamespace().equals("_EST_1_")){
			setForward(actionRequest, "portlet.seguimiento.deudas.result.search");
		}else{
			setForward(actionRequest, "portlet.tesoreria.view");
		}

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Acta acta = updateActaEntry(actionRequest, cmd, entidad);
				actionRequest.setAttribute("acta_id",
						String.valueOf(acta.getId()));
				if (cerrarActa(actionRequest)) {
					
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
	private Acta updateActaEntry(ActionRequest actionRequest, String cmd, int entidad)
			throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

		if (acta == null) {
			acta = new Acta();
		}
		getActaFromRequest(PortalUtil.getHttpServletRequest(actionRequest),
				acta);

		if (acta != null) {
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		boolean cerrarActa = cerrarActa(actionRequest);

		if (cerrarActa && acta.getCierre_fecha() == null) {
			throw new FaltaFechaCierreActaException();
		}
		if(entidad==WebKeysGlobal.ESTUDIO){
			entidad=acta.getEntidad().equals("A.M.T.I.M.A.")?WebKeysGlobal.AMTIMA:WebKeysGlobal.UOMA;
		}
		List<InspectorWrapper> inspectores = (ArrayList<InspectorWrapper>) session
				.getAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);

		User user = PortalUtil.getUser(actionRequest);
		acta.setActaCerrada(cerrarActa);
		
		if (cmd.equals(Constants.ADD)) {
			ActaNoOSServiceUtil.save(acta, user, inspectores, cerrarActa, entidad);
		} else {
			ActaNoOSServiceUtil.update(acta, user, inspectores, cerrarActa, entidad);
		}
		session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return acta;
	}

	private boolean cerrarActa(ActionRequest actionRequest) {
		boolean cerrarActa = false;
		String cerrarActaStr = actionRequest.getParameter("cerrarActa");
		boolean fromDeuda=ParamUtil.getBoolean(actionRequest, "fromBusquedaDeuda" );
		if ((cerrarActaStr != null && cerrarActaStr.equals("cerrarActa")) /*|| !fromDeuda*/) {
			cerrarActa = true;
		}
		return cerrarActa;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String portlet_name = null;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			portlet_name = "farmacia";
		}else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			portlet_name = "uoma";
		}else if (renderResponse.getNamespace().equals("_EST_1_")) {
			portlet_name = "estudio";
		}

		String cuit = ParamUtil.getString(renderRequest, "cuit");
		String busqueda = ParamUtil.getString(renderRequest, "busqueda");
		renderRequest.setAttribute("busqueda", busqueda);
			
		InspectorServiceUtil.getInspectores(renderRequest);
		TraeListasServiceUtil.getBancos(renderRequest);
		
		boolean fromDeuda=ParamUtil.getBoolean(renderRequest, "fromBusquedaDeuda" );
		renderRequest.setAttribute("fromBusquedaDeuda", fromDeuda);

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
					getActaFromRequest(
							PortalUtil.getHttpServletRequest(renderRequest),
							acta);
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
			session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
			acta = getActaEntry(httpServletRequest);
			//ACA SI VIENE DE EDICION VERIFICAR FECHAS Y ABRIR CERRADA...
			
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

		String popupActa = ParamUtil.getString(renderRequest, "popupActa");
		String popupActaNoOS = ParamUtil.getString(renderRequest,
				"popupActaNoOS");

		if ((null != busqueda && !busqueda.trim().equals("true"))
				&& ((popupActa != null && popupActa.trim().length() > 0 && popupActa
						.trim().equals("true")) || (popupActaNoOS != null
						&& popupActaNoOS.trim().length() > 0 && popupActaNoOS
						.trim().equals("true")))) {			
			if (null != portlet_name && portlet_name.equals("farmacia")) {
				return mapping.findForward("portlet.estudio_isidro.actas_no_os.edit_actas_entry");
			} else if(null != portlet_name && portlet_name.equals("uoma")){
				return mapping.findForward("portlet.estudio_isidro.actas_no_os.edit_actas_entry");
			}else {			
				PortletSession portletSession = renderRequest.getPortletSession();
				LlamadosEstudio llest = (LlamadosEstudio) portletSession
						.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
				
				EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest, acta.getEmpresa().getCuit());	
				return mapping.findForward("portlet.empresas.tabla.resumen");	
			}

		} else {
			return mapping.findForward("portlet.estudio_isidro.actas_no_os.edit_actas_entry");
			//return mapping.findForward(getForward(renderRequest,"portlet.estudio_isidro.actas_no_os.edit_actas_entry"));
			
		}
	}
}