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
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesProductosCategoriasAction extends PortletAction {
	
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
		
		ProductoCategoria categoria=null;
		String idCategoria = "";
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idCategoria = ParamUtil.getString(renderRequest,"id_categoria");
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			if(cmd.equals(Constants.WRITE) ){ 
				
				categoria = new ProductoCategoria();
				categoria.setHotel(idHotel);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.CATEGORIA_EN_EDICION , categoria);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.categoria_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				categoria = (ProductoCategoria) session.getAttribute(WebKeysHoteles.CATEGORIA_EN_EDICION);
				actualizaCategoria(categoria,PortalUtil.getHttpServletRequest(renderRequest));
				
				String idCategoriaL=updateCategoria(categoria, user.getScreenName());    
					
				msg = "Update Categoria Hotel";
				msg = msg + " "+ idCategoriaL;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Categoria: " + idCategoriaL
				);
					
				session.setAttribute(WebKeysHoteles.CATEGORIA_EN_EDICION, categoria);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	categoria = HotelesServiceUtil.getCategoriaByCodigo(idHotel, idCategoria);
            	session.setAttribute(WebKeysHoteles.CATEGORIA_EN_EDICION , categoria);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.categoria_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				categoria= new ProductoCategoria();
				categoria.setHotel(idHotel);
				categoria.setCodigo(idCategoria);
            	HotelesServiceUtil.deleteCategoria(categoria,user.getScreenName());
            	List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(idHotel);
            	session.setAttribute(WebKeysHoteles.CATEGORIAS_RESULT,categorias);
            	return mapping.findForward("portlet.hoteles.categorias_result");
			}
			
		}
		return mapping.findForward("portlet.hoteles.categoria_editar");
   }
	
   private void actualizaCategoria(ProductoCategoria categoria,HttpServletRequest renderRequest) throws SystemException{
	    String codigo = ParamUtil.getString(renderRequest, "codigo");
	    String descripcion = ParamUtil.getString(renderRequest,"descripcion");
	    Boolean mesas = ParamUtil.getBoolean(renderRequest, "paraMesas");
	    Boolean habitaciones = ParamUtil.getBoolean(renderRequest, "paraHabitaciones");
	    
	    String aplicaA="";
	    if(habitaciones) {
	    	aplicaA+="HABITACIONES";
	    }
	    
	    if(mesas) {
	    	if(aplicaA.length()>0) aplicaA += ",";
	    	aplicaA+="MESAS";
	    }
	    
	   
	    categoria.setAplicaA(aplicaA);
	    categoria.setCodigo(codigo);
	    categoria.setDescripcion(descripcion);
   }

   private String updateCategoria(ProductoCategoria categoria, String user) throws Exception{
	String id = "";
	
	id = HotelesServiceUtil.updateCategoria(categoria, user);
	return id;
   }

   private String deleteCategoria(ProductoCategoria categoria, String user) throws Exception{
	String id ="";
	
	id = HotelesServiceUtil.deleteCategoria(categoria, user);
	return id;
   }

	
}