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

import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesMesasAction extends PortletAction {
	
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
		
		Mesa mesa=null;
		Integer idMesa = 0;
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idMesa = ParamUtil.getInteger(renderRequest,"id_mesa", 0);
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			if(cmd.equals(Constants.WRITE) ){ 
				
				mesa = new Mesa();
				mesa.setHotel(idHotel);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.MESA_EN_EDICION , mesa);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.mesa_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				mesa = (Mesa) session.getAttribute(WebKeysHoteles.MESA_EN_EDICION);
				actualizaMesa(mesa,PortalUtil.getHttpServletRequest(renderRequest));
				
				Long idMesaL=updateMesa(mesa, user.getScreenName());    
					
				msg = "Se dio de alta correctamente la Mesa N° ";
				msg = msg + " "+ idMesaL;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Mesa: " + idMesaL
				);
					
				session.setAttribute(WebKeysHoteles.MESA_EN_EDICION, mesa);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	mesa = HotelesServiceUtil.getMesasByNro(idHotel, idMesa);
            	session.setAttribute(WebKeysHoteles.MESA_EN_EDICION , mesa);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.mesa_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				mesa= new Mesa();
				mesa.setHotel(idHotel);
				mesa.setNumero(idMesa);
            	HotelesServiceUtil.deleteMesa(mesa,user.getScreenName());
            	List<Mesa> mesas =  HotelesServiceUtil.getMesas(idHotel, null);
            	session.setAttribute(WebKeysHoteles.MESAS_RESULT,mesas);
            	return mapping.findForward("portlet.hoteles.mesas_result");
			}
			
		}
		return mapping.findForward("portlet.hoteles.mesa_editar");
   }
	
   private void actualizaMesa(Mesa mesa,HttpServletRequest renderRequest) throws SystemException{
	    Integer nro = ParamUtil.getInteger(renderRequest, "nro");
	    String descripcion = ParamUtil.getString(renderRequest,"descripcion");
	    String grupo = ParamUtil.getString(renderRequest,"grupo");
		mesa.setDescripcion(descripcion);
		mesa.setGrupo(grupo);
		mesa.setNumero(nro);
   }

   private long updateMesa(Mesa mesa, String user) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.updateMesa(mesa, user);
	return id;
   }

   private long deleteMesa(Mesa mesa, String user) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.deleteMesa(mesa, user);
	return id;
   }

	
}