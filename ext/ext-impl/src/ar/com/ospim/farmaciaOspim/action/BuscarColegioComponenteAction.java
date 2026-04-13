package ar.com.ospim.farmaciaOspim.action;
import java.math.BigDecimal;
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
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.services.BusquedaColegioFarmaciaServiceUtil;
import ar.com.ospim.global.beans.ColegioFarmacia;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarColegioComponenteAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ColegioFarmacia.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.colegio.result.search.popup");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		String popup = null;
				
		try {
			String codigoColegio  = null;
			String detalleColegio  = null;
		
			if (null != renderRequest.getParameter("codigoColegio")) {
				codigoColegio= renderRequest.getParameter("codigoColegio").trim().length() > 0 ? renderRequest
						.getParameter("codigoColegio")
						: null;
			}
			if (null != renderRequest.getParameter("detalleColegio")) {
				detalleColegio= renderRequest.getParameter("detalleColegio").trim().length() > 0 ? renderRequest.getParameter("detalleColegio")
						: null;
			}
			BusquedaColegioFarmaciaServiceUtil.getInstance();
			List<ColegioFarmacia> busqueda ;			
					busqueda = BusquedaColegioFarmaciaServiceUtil.getBusquedaColegio(codigoColegio, detalleColegio);
					renderRequest.removeAttribute(WebKeysFarmaciaOspim.BUSQUEDA_COLEGIO);
			     	renderRequest.setAttribute(WebKeysFarmaciaOspim.BUSQUEDA_COLEGIO,	busqueda);			
						
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}
		popup = ParamUtil.getString(renderRequest, "popup");			
		String origen= ParamUtil.getString(renderRequest, "origen");
		renderRequest.setAttribute("origen", origen);		
		return mapping.findForward("portlet.colegio.result.search.popup");

	}

}