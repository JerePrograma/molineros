package ar.com.uoma.actasNoOS.action;

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
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarActaNoOSAction extends PortletAction {
	public static final String ACTA_A_ANULAR = "acta_a_anular";
	private static Log logger = LogFactoryUtil.getLog(BorrarActaNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int id = 0;
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		id = ParamUtil.getInteger(renderRequest, "id");
		User user = PortalUtil.getUser(PortalUtil
				.getHttpServletRequest(renderRequest));

		String accion = ParamUtil.getString(renderRequest, "accion", "");
		String popupActa=ParamUtil.getString(renderRequest, "popupActa", "");
				
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

				ActaNoOSServiceUtil.borrar(id, fechaBaja, user, entidad);
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
		Acta acta = ActaNoOSServiceUtil.getActa(id,0);
		renderRequest.setAttribute(ACTA_A_ANULAR, acta);
		renderRequest.setAttribute("id", String.valueOf(id));

		if (null!=popupActa&&popupActa.trim().equals("true")) {			
			PortletSession portletSession = renderRequest.getPortletSession();
			LlamadosEstudio llest = (LlamadosEstudio) portletSession
					.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
			
			EmpresaServiceUtil.buscarDatosEmpresaSeguimientoMolinera(llest, renderRequest, acta.getEmpresa().getCuit());	
			return mapping.findForward("portlet.empresas.tabla.resumen");	
		} else {
			return mapping.findForward("portlet.estudio_isidro.anular.acta");
		}
	}
}
