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

import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.beans.ProductoConfiteria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;


public class HotelesProductosAction extends PortletAction {
	
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
		
		ProductoConfiteria producto=null;
		String idProducto = "";
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idProducto = ParamUtil.getString(renderRequest,"id_producto");
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			
			if(cmd.equals("filtrar") ){
				String categoria = ParamUtil.getString(renderRequest,"categoria");
				List<ProductoConfiteria> productos = HotelesServiceUtil.getProductos(idHotel, categoria, null);
				session.setAttribute(WebKeysHoteles.PRODUCTOS_RESULT,productos);
				return mapping.findForward("portlet.hoteles.productos_result");
			}
			
			if(cmd.equals(Constants.WRITE) ){ 
				
				producto = new ProductoConfiteria();
				producto.setHotel(idHotel);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.PRODUCTO_EN_EDICION , producto);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.producto_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				producto = (ProductoConfiteria) session.getAttribute(WebKeysHoteles.PRODUCTO_EN_EDICION);
				actualizaProducto(producto,PortalUtil.getHttpServletRequest(renderRequest));
				
				String idProductoL=updateProducto(producto, user.getScreenName());    
					
				msg = "Actualización Producto Hotel";
				msg = msg + " "+ idProductoL;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Producto: " + idProductoL
				);
					
				session.setAttribute(WebKeysHoteles.PRODUCTO_EN_EDICION, producto);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	producto = HotelesServiceUtil.getProductoByCodigo(idHotel, idProducto);
            	session.setAttribute(WebKeysHoteles.PRODUCTO_EN_EDICION , producto);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.producto_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				producto= new ProductoConfiteria();
				producto.setHotel(idHotel);
				producto.setCodigo(idProducto);
            	HotelesServiceUtil.deleteProducto(producto,user.getScreenName());
            	String categoria = ParamUtil.getString(renderRequest,"categoria");
				List<ProductoConfiteria> productos = HotelesServiceUtil.getProductos(idHotel, categoria, null);
            	session.setAttribute(WebKeysHoteles.PRODUCTOS_RESULT,productos);
            	return mapping.findForward("portlet.hoteles.productos_result");
			}
			
		}
		return mapping.findForward("portlet.hoteles.producto_editar");
   }
	
   private void actualizaProducto(ProductoConfiteria producto,HttpServletRequest renderRequest) throws SystemException{
	    String codigo = ParamUtil.getString(renderRequest, "codigo");
	    String descripcion = ParamUtil.getString(renderRequest,"descripcion");
	    String descripcionCorta = ParamUtil.getString(renderRequest, "descripcion_corta");
	    String categoriaStr = ParamUtil.getString(renderRequest, "categoria");
	    Double precio = ParamUtil.getDouble(renderRequest, "precio");
	    Boolean habitaciones = ParamUtil.getBoolean(renderRequest, "paraHabitaciones");
	    	    
	    producto.setCodigo(codigo);
	    producto.setDescripcion(descripcion);
	    producto.setDescripcionCorta(descripcionCorta);
	    ProductoCategoria categoria=new ProductoCategoria();
	    categoria.setCodigo(categoriaStr);
	    producto.setCategoria(categoria);
	    producto.setHabilitadoHabitaciones(habitaciones);
	    producto.setPrecio(precio);
	    
   }

   private String updateProducto(ProductoConfiteria producto, String user) throws Exception{
	String id = "";
	
	id = HotelesServiceUtil.updateProducto(producto, user);
	return id;
   }

   private String deleteProducto(ProductoConfiteria producto, String user) throws Exception{
	String id ="";
	
	id = HotelesServiceUtil.deleteProducto(producto, user);
	return id;
   }

	
}