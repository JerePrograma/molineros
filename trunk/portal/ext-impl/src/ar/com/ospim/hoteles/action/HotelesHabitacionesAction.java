package ar.com.ospim.hoteles.action;

import java.text.SimpleDateFormat;
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

import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesHabitacionesAction extends PortletAction {
	
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
		
		Habitacion habitacion=null;
		Integer idHabitacion = 0;
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idHabitacion = ParamUtil.getInteger(renderRequest,"id_habitacion", 0);
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			if(cmd.equals(Constants.WRITE) ){ 
				
				habitacion = new Habitacion();
				habitacion.setHotel(idHotel);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.HABITACION_EN_EDICION , habitacion);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.habitacion_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				habitacion = (Habitacion) session.getAttribute(WebKeysHoteles.HABITACION_EN_EDICION);
				actualizaHabitacion(habitacion,PortalUtil.getHttpServletRequest(renderRequest));
				
				Long idHabitacionL=updateHabitacion(habitacion, user.getScreenName());    
					
				msg = "Update Habitacion Hotel";
				msg = msg + " "+ idHabitacionL;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Habitacion: " + idHabitacionL
				);
					
				session.setAttribute(WebKeysHoteles.HABITACION_EN_EDICION, habitacion);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	habitacion = HotelesServiceUtil.getHabitacionesByNro(idHotel, idHabitacion);
            	session.setAttribute(WebKeysHoteles.HABITACION_EN_EDICION , habitacion);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.habitacion_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				habitacion= new Habitacion();
				habitacion.setHotel(idHotel);
				habitacion.setNumero(idHabitacion);
            	HotelesServiceUtil.deleteHabitacion(habitacion,user.getScreenName());
            	List<Habitacion> habitaciones =  HotelesServiceUtil.getHabitaciones(idHotel, null);
            	session.setAttribute(WebKeysHoteles.HABITACIONES_RESULT,habitaciones);
            	return mapping.findForward("portlet.hoteles.habitaciones_result");
			}
			
		}
		return mapping.findForward("portlet.hoteles.habitacion_editar");
   }
	
   private void actualizaHabitacion(Habitacion habitacion,HttpServletRequest renderRequest) throws SystemException{
	    Integer nro = ParamUtil.getInteger(renderRequest, "nro");
	    String descripcion = ParamUtil.getString(renderRequest,"descripcion");
	    String grupo = ParamUtil.getString(renderRequest,"grupo");
		habitacion.setDescripcion(descripcion);
		habitacion.setGrupo(grupo);
		habitacion.setNumero(nro);
   }

   private long updateHabitacion(Habitacion habitacion, String user) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.updateHabitacion(habitacion, user);
	return id;
   }

   private long deleteHabitacion(Habitacion habitacion, String user) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.deleteHabitacion(habitacion, user);
	return id;
   }

	
}