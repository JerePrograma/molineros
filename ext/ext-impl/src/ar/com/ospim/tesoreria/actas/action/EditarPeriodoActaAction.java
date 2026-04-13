package ar.com.ospim.tesoreria.actas.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.services.EscalaSalarialServiceImpl;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarPeriodoActaAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}else if (renderResponse.getNamespace().equals("_EST_1_")) {
			String entidadString=ParamUtil.getString(renderRequest,"entidad");
			if(entidadString!=null&&entidadString.equals("U.O.M.A.")){
				entidad = WebKeysGlobal.UOMA;
			}else if(entidadString!=null&&entidadString.equals("A.M.T.I.M.A.")){
				entidad = WebKeysGlobal.AMTIMA;	
			}
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}
		String fechaStr = renderRequest.getParameter("periodoEnEdicion");
		Date fecha = null;
		if (null != fechaStr) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			fechaStr = fechaStr.replaceAll("_", "-");
			fechaStr = "01-" + fechaStr;
			fecha = format.parse(fechaStr);
			Calendar aux = Calendar.getInstance();
			aux.setTime(fecha);
			aux.add(Calendar.MONTH, 1);
			fecha = aux.getTime();
			renderRequest.setAttribute("mostrar_periodo", format.format(fecha)
					.substring(3));
			renderRequest.setAttribute("periodoEnEdicion", format.format(fecha));
		}

		List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
		if (peris == null) {
			peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
		}

		List<ActaPeriodoDeudaEmpresa> periodos = new ArrayList<ActaPeriodoDeudaEmpresa>();
		if (null != fecha) {
			for (ActaPeriodoDeudaEmpresa actaPeri : peris) {
				if (actaPeri.getPeriodo().equals(fecha)) {
					periodos.add(actaPeri);
				}
			}
		}
		
		if (entidad != WebKeysGlobal.OSPIM) {
			TraeListasServiceUtil.getConvenioNac(renderRequest);
		}

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_PERIODOS, periodos);

		if (entidad != WebKeysGlobal.OSPIM) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.uoma.actas.editar.periodos.view"));
		} else {
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.actas.editar.periodos.view"));
		}		
	}
}
