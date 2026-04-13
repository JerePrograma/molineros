package ar.com.empresas.action;

import java.sql.SQLException;

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
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class EditarSeccionalEntryAction extends EmpleadoresBaseAction {

	private static Log logger = LogFactoryUtil
			.getLog(EditarSeccionalEntryAction.class);
	
	protected boolean isCheckMethodOnProcessAction() {
		return _CHECK_METHOD_ON_PROCESS_ACTION;
	}

	private static final boolean _CHECK_METHOD_ON_PROCESS_ACTION = false;

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		String flag = actionRequest.getParameter("flag");
		String flagEstudio = actionRequest.getParameter("popupSeguimiento");
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		actionRequest.setAttribute("popupSeguimiento", flagEstudio);
		try {

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)
					|| cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
				this.updateEmpresaEntry(actionRequest, cmd);
				if (null != flagEstudio && flagEstudio.equals("true")) {
					setForward(actionRequest,
							"portlet.estudio_isidro.seguimiento_empresa_result");
				} else if (flag != null && flag.equals("true")) {
					actionRequest.setAttribute("cuit",
							ParamUtil.getString(actionRequest, "cuit"));
					setForward(actionRequest,
							"portlet.afiliados.empleadores.editar_empleadores_popup_entry");
					actionRequest.setAttribute("empresa_grabada", "true");
				}

			} else if (cmd.equals(Constants.DELETE)) {
				this.borraEmpresaEntry(actionRequest);
				setForward(actionRequest, "portlet.afiliados.view");
			}
		} catch (DuplicateEmpresaIdException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			actionRequest.setAttribute("empresa_ya_existe", "true");
			if (flag != null && flag.equals("true")) {
				setForward(actionRequest,
						"portlet.afiliados.empleadores.editar_empleadores_popup_entry");
			}
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

	private void borraEmpresaEntry(ActionRequest actionRequest) {
		// TODO Auto-generated method stub

	}

	private void updateEmpresaEntry(ActionRequest actionRequest, String cmd)
			throws Exception {
		Seccional empresa = (Seccional) actionRequest.getPortletSession()
				.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,
						PortletSession.APPLICATION_SCOPE);		
		
		String cuit = ParamUtil.getString(actionRequest, "cuit");		
		String desc = ParamUtil.getString(actionRequest, "desc");		
		String obs = ParamUtil.getString(actionRequest, "observaciones");
		int id_seccional = ParamUtil.getInteger(actionRequest,"id_seccional");
		String destino=ParamUtil.getString(actionRequest, "destino");		
		String cheque=ParamUtil.getString(actionRequest, "cheque");
		String cbu=ParamUtil.getString(actionRequest, "cbu");
		
		empresa.setCuitEntidad(cuit);				
		empresa.setDescripcion(desc);		
		empresa.setObservaciones(obs);
		empresa.setId_seccional(id_seccional);
		empresa.setDestino(destino);		
		empresa.setCheque_a_la_orden(cheque);
		empresa.setCBU(cbu);
		
		User user = PortalUtil.getUser(actionRequest);
		
		if (cmd.equals(Constants.UPDATE)){
			EmpresaServiceUtil.updateSeccional(empresa, user.getScreenName());
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getSeccionales(renderRequest);		
				
		
		Seccional empresa = null;
		String cambioSolapa = renderRequest.getParameter("cambioSolapa");
		
		if (cambioSolapa != null && cambioSolapa.equals("cambioSolapa")
				|| !SessionErrors.isEmpty(renderRequest)) {

			String accionOriginal = renderRequest
					.getParameter("accionOriginal");
			if (accionOriginal != null) {
				renderRequest.setAttribute("accionOriginal", accionOriginal);
			}

			empresa = (Seccional) renderRequest.getPortletSession().getAttribute(
					WebKeysEmpresas.EMPRESA_EN_EDICION,
					PortletSession.APPLICATION_SCOPE);
			if (empresa == null) {
				empresa = new Seccional();
			}
			
		} else {
			renderRequest.getPortletSession().removeAttribute(
					WebKeysEmpresas.EMPRESA_EN_EDICION,
					PortletSession.APPLICATION_SCOPE);
			empresa = getSeccionalEntryCompleto(PortalUtil
					.getHttpServletRequest(renderRequest));
		}

		renderRequest.getPortletSession().setAttribute(
				WebKeysEmpresas.EMPRESA_EN_EDICION, empresa,
				PortletSession.APPLICATION_SCOPE);


		return mapping.findForward(getForward(renderRequest,
				"portlet.afiliados.empleadores.editar_seccionales_entry"));
	}
}