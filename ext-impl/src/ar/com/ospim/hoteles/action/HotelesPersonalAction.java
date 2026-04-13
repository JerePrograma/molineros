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

import ar.com.ospim.hoteles.beans.Personal;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.beans.ProductoConfiteria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesPersonalAction extends PortletAction {
	
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
		
		Personal empleado=null;
		Integer idPersonal = 0;
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idPersonal = ParamUtil.getInteger(renderRequest,"id_personal");
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			
			if(cmd.equals("filtrar") ){
				String categoria = ParamUtil.getString(renderRequest,"categoria");
				List<Personal> personal = HotelesServiceUtil.getPersonal(idHotel, categoria, null);
				session.setAttribute(WebKeysHoteles.PERSONAL_RESULT,personal);
				return mapping.findForward("portlet.hoteles.personal_result");
			}
			
			if(cmd.equals(Constants.WRITE) ){ 
				
				empleado = new Personal();
				empleado.setHotel(idHotel);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.PERSONAL_EN_EDICION , empleado);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.personal_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				empleado = (Personal) session.getAttribute(WebKeysHoteles.PERSONAL_EN_EDICION);
				actualizaPersonal(empleado,PortalUtil.getHttpServletRequest(renderRequest));
				
				String idPersonalL=updatePersonal(empleado, user.getScreenName());    
					
				msg = "Se dio de alta correctamente. Personal Nro ";
				msg = msg + " "+ idPersonalL;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Personal: " + idPersonalL
				);
					
				session.setAttribute(WebKeysHoteles.PERSONAL_EN_EDICION, empleado);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	empleado = HotelesServiceUtil.getPersonalById(idHotel, idPersonal);
            	session.setAttribute(WebKeysHoteles.PERSONAL_EN_EDICION , empleado);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.personal_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				empleado= new Personal();
				empleado.setHotel(idHotel);
				empleado.setId(idPersonal);
            	HotelesServiceUtil.deletePersonal(empleado,user.getScreenName());
            	String categoria = ParamUtil.getString(renderRequest,"categoria");
				List<Personal> personal = HotelesServiceUtil.getPersonal(idHotel, categoria, null);
            	session.setAttribute(WebKeysHoteles.PERSONAL_RESULT,personal);
            	return mapping.findForward("portlet.hoteles.personal_result");
			}
			
		}
		return mapping.findForward("portlet.hoteles.personal_editar");
   }
	
   private void actualizaPersonal(Personal empleado,HttpServletRequest renderRequest) throws SystemException{
	    Integer codigo = ParamUtil.getInteger(renderRequest, "codigo");
	    String apellido = ParamUtil.getString(renderRequest,"apellido");
	    String nombre = ParamUtil.getString(renderRequest, "nombre");
	    String categoria = ParamUtil.getString(renderRequest, "categoria");
	    String password = ParamUtil.getString(renderRequest, "password");
	    
	    empleado.setId(codigo);
	    empleado.setApellido(apellido);
	    empleado.setNombre(nombre);
	    empleado.setCategoria(categoria);
	    empleado.setPassword(password);
	    
   }

   private String updatePersonal(Personal empleado, String user) throws Exception{
	Integer id = 0;
	
	id = HotelesServiceUtil.updatePersonal(empleado, user);
	return id.toString();
   }

   private String deletePersonal(Personal personal, String user) throws Exception{
	Integer id =0;
	
	id = HotelesServiceUtil.deletePersonal(personal, user);
	return id.toString();
   }

	
}