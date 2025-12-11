package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Catastro;
import ar.com.ospim.liquidaciones.services.CatastroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarCatastroAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba registro de Catastro
 * 
 * @author Carlos Rivas
 * 
 */
public class GrabarCatastroAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarCatastroAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.catastro.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User user = PortalUtil.getUser(renderRequest);

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String prestacionFechaDia = ParamUtil
				.getString(renderRequest, "diaPer");
		String prestacionFechaMes = ParamUtil
				.getString(renderRequest, "mesPer");
		String prestacionFechaAnio = ParamUtil.getString(renderRequest,
				"anioPer");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}

		int id_codigo = ParamUtil.getInteger(renderRequest, "id_codigo", 0);
		String codigo = ParamUtil.getString(renderRequest, "codigo", "");
		String pieza1 = ParamUtil.getString(renderRequest, "pieza1", "");
		String pieza2 = ParamUtil.getString(renderRequest, "pieza2", "");
		String pieza3 = ParamUtil.getString(renderRequest, "pieza3", "");
		String pieza4 = ParamUtil.getString(renderRequest, "pieza4", "");
		String pieza5 = ParamUtil.getString(renderRequest, "pieza5", "");
		String pieza6 = ParamUtil.getString(renderRequest, "pieza6", "");
		String pieza7 = ParamUtil.getString(renderRequest, "pieza7", "");
		String pieza8 = ParamUtil.getString(renderRequest, "pieza8", "");
		String pieza9 = ParamUtil.getString(renderRequest, "pieza9", "");
		String pieza10 = ParamUtil.getString(renderRequest, "pieza10", "");		
		String cara1 = ParamUtil.getString(renderRequest, "cara1", "");
		String cara2 = ParamUtil.getString(renderRequest, "cara2", "");
		String cara3 = ParamUtil.getString(renderRequest, "cara3", "");
		String cara4 = ParamUtil.getString(renderRequest, "cara4", "");
		String cara5 = ParamUtil.getString(renderRequest, "cara5", "");
		String cara6 = ParamUtil.getString(renderRequest, "cara6", "");
		String cara7 = ParamUtil.getString(renderRequest, "cara7", "");
		String cara8 = ParamUtil.getString(renderRequest, "cara8", "");
		String cara9 = ParamUtil.getString(renderRequest, "cara9", "");
		String cara10 = ParamUtil.getString(renderRequest, "cara10", "");
		
		String borrarCat = ParamUtil.getString(renderRequest, "borrarCat", "");
		int id = ParamUtil.getInteger(renderRequest, "id", 0);
		String cuil_titular = ParamUtil.getString(renderRequest,
				"cuil_titular", "");
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int todas = ParamUtil.getInteger(renderRequest, "todas", 0);
		
		try {
			List<Catastro> catastral = null;
			if (todas == 0){				
				if (borrarCat.length() > 0 && borrarCat.trim().equals("true")) {
					catastral = CatastroServiceUtil.borraCatastroRetornaLista(id,
							cuil_titular, inte, user);
				} else {
					catastral = CatastroServiceUtil.grabaCatastroRetornaLista(
							prestacionFecha, id_codigo, codigo, pieza1, pieza2,
							pieza3, pieza4, pieza5, pieza6, pieza7, pieza8, pieza9, pieza10 ,cara1, cara2, cara3, cara4, cara5, cara6, cara7,
							cara8, cara9, cara10,
							cuil_titular, inte, user);
				}
			} else {
				catastral = CatastroServiceUtil.grabaCatastroCompletoRetornaLista(prestacionFecha, cuil_titular, inte, id_codigo, codigo, user);
			}
			
			renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CATASTRAL,
					catastral);
			String view = ParamUtil.getString(renderRequest, "view");

			if (null != view && view.equals("true")) {
				renderRequest.setAttribute("view", view);
			}

		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}
		return mapping.findForward("portlet.liquidaciones.catastro.result");
	}

}