/**
 */

package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.services.FechaOpcionSSSUtil;

/**
 * <a href="AltaFechaPressOpcionAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza el alta de la proxima fecha de presentacion a la SSS
 * 
 * @author Conde Pablo
 * 
 */
public class AltaFechaPressOpcionAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AltaFechaPressOpcionAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		_log.debug("Entro");
		
		User user = PortalUtil.getUser(renderRequest);
		
	
		String fechaOpcionDia = (String) renderRequest.getParameter("fechaOpcionDia");
		String fechaOpcionMes = (String) renderRequest.getParameter("fechaOpcionMes");
		String fechaOpcionAnio = (String) renderRequest.getParameter("fechaOpcionAnio");
		
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaPress = null;
		try {
			fechaPress = formatoDeFechaV.parse(fechaOpcionDia + "/"
						+ (Integer.parseInt(fechaOpcionMes) + 1 ) + "/"
						+ fechaOpcionAnio);
		} catch (Exception e) {
			fechaPress = null;
		}
		
		FechaOpcionSSSUtil opc = new FechaOpcionSSSUtil();
		
		opc.insetarProximaFechaOpcionSSS(fechaPress, user.getScreenName());
		
		

		return mapping.findForward("portlet.novedades.view");
	}

}