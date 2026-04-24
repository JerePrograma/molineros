package ar.com.ospim.hoteles.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;



import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesGestionAdministrativaAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			
			if(cmd.equals("filtrar") ){
				
				String diaDesde = ParamUtil.getString(renderRequest, "fechadesdedia");
				String mesDesde = ParamUtil.getString(renderRequest, "fechadesdemes");
				String anioDesde = ParamUtil.getString(renderRequest, "fechadesdeanio");
				
				String diaHasta = ParamUtil.getString(renderRequest, "fechahastadia");
				String mesHasta = ParamUtil.getString(renderRequest, "fechahastames");
				String anioHasta = ParamUtil.getString(renderRequest, "fechahastaanio");
				
				Date fechaDde = formatoDeFechas.parse(diaDesde +"/"+(Integer.parseInt(mesDesde) + 1)+"/"+anioDesde);
				Date fechaHta = formatoDeFechas.parse(diaHasta +"/"+(Integer.parseInt(mesHasta) + 1)+"/"+anioHasta);
				
				String habitacion = ParamUtil.getString(renderRequest, "habitacion");
				Integer reserva = ParamUtil.getInteger(renderRequest, "reserva");
				Integer anio = ParamUtil.getInteger(renderRequest, "anio");
				
				
				List<Reserva> reservas = HotelesServiceUtil.getReservasByFechaFin(idHotel, anio,reserva,habitacion,fechaDde,fechaHta);
				session.setAttribute(WebKeysHoteles.RESERVAS_RESULT,reservas);
				return mapping.findForward("portlet.hoteles.reservas_result");
				
			}
		}
		return mapping.findForward("portlet.hoteles.gestion_administrativa");
   }
	
   

	
}