package ar.com.ospim.tesoreria.actas.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.action.BuscarSeguimientoEmpresaAction;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarActaAction extends PortletAction {
	public static final String ACTA_A_ANULAR = "acta_a_anular";
	private static Log logger = LogFactoryUtil.getLog(BorrarActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int id = 0;

		id = ParamUtil.getInteger(renderRequest, "id");
		User user = PortalUtil.getUser(PortalUtil
				.getHttpServletRequest(renderRequest));

		String accion = ParamUtil.getString(renderRequest, "accion", "");
		String from = ParamUtil.getString(renderRequest, "from", "");
		String popupActa = ParamUtil.getString(renderRequest, "popupActa", "");
		
		if (accion.equals("borrar")) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String fechaBajaDia = ParamUtil.getString(renderRequest,
						"fechaBajaDia");
				String fechaBajaMes = ParamUtil.getString(renderRequest,
						"fechaBajaMes");
				String fechaBajaAnio = ParamUtil.getString(renderRequest,
						"fechaBajaAnio");

				Date fechaBaja = new Date();
				if (StringUtils.checkNotEmpty(fechaBajaDia)) {
					fechaBajaMes = String
							.valueOf(Integer.valueOf(fechaBajaMes) + 1);
					fechaBaja = format.parse(fechaBajaDia + "-" + fechaBajaMes
							+ "-" + fechaBajaAnio);
				}

				ActaServiceUtil.borrar(id, fechaBaja, user);
			} catch (Exception e) {
				logger.debug("No se pudo borrar acta", e);
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
			if (SessionErrors.isEmpty(renderRequest)) {
				String successMessage = ParamUtil.getString(renderRequest,
						"successMessage");
				SessionMessages.add(renderRequest, "request_processed",
						successMessage);
			}
		}
		Acta acta = ActaServiceUtil.getActa(id,0);
		renderRequest.setAttribute(ACTA_A_ANULAR, acta);
		renderRequest.setAttribute("id", String.valueOf(id));
		
		if (StringUtils.checkNotEmpty(from) && from.trim().equals("deuda")) {
			if (renderResponse != null
					&& renderResponse.getNamespace() != null
					&& renderResponse.getNamespace().equals("_EST_1_")) {
				renderRequest.setAttribute("popupActa", popupActa);
				
				BuscarSeguimientoEmpresaAction buscar = new BuscarSeguimientoEmpresaAction();
				buscar.buscarActas(renderRequest);

				return mapping
//						.findForward("portlet.estudio_isidro.seguimiento_empresa_result");
						.findForward("portlet.empresas.tabla.resumen");
				
			}else{
				return mapping.findForward("portlet.tesoreria.view");
			}
		} else {
			if (renderResponse != null
					&& renderResponse.getNamespace() != null
					&& renderResponse.getNamespace().equals("_EST_1_") && popupActa.trim().equals("true")) {
				PortletSession portletSession = renderRequest.getPortletSession();
				LlamadosEstudio llest = (LlamadosEstudio) portletSession
						.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
				
				EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest, acta.getEmpresa().getCuit());	
				return mapping.findForward("portlet.empresas.tabla.resumen");	
			}else{
				return mapping.findForward("portlet.tesoreria.anular.acta");
			}
		}
	}
}
