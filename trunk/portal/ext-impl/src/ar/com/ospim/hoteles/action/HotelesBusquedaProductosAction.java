package ar.com.ospim.hoteles.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.beans.ProductoConfiteria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class HotelesBusquedaProductosAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		String ptovta = req.getParameter("ptovta");
		String categoria = req.getParameter("categoria");
		String tipo = req.getParameter("tipo");
		
		String resultado = "{}";
		String titulo="";
		String claseCSS="";
        String funcion="";
        String valor="";
        String parametroFunction="";
        String idNbe="";
		Integer unidadesPorFila=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("HOTEL_" + ptovta +"_UNIDADES_POR_FILA_PRODUCTOS") );
		
 		Integer total=0;
 		
 		String tipoCategoria="";
 		
		int columnas=0;
		int inicioRenglon=0;
		String cadena ="";
		

		   List<ProductoConfiteria> cats = HotelesServiceUtil.getProductos(ptovta, categoria,null);
		   if("HABITACIONES".equalsIgnoreCase(tipo)) {
			   List<ProductoConfiteria> catsAux = new ArrayList<ProductoConfiteria>();
			   for(ProductoConfiteria p:cats) {
				   if(p.isHabilitadoHabitaciones()) {
					   catsAux.add(p);
				   }
			   }
			   cats=catsAux;
		   }
		   
		   cadena ="<table class='unidades'>";
		   
		   for(ProductoConfiteria c:cats) {
			  if(columnas==0) {
        		 cadena += "<tr>";
        	  }
        	  columnas++;
        	  cadena +=  "<td>";
        	  cadena +=  "<input type='button' id='producto_"+ptovta+"_" + c.getCodigo() + "' class='productos' value='" + c.getDescripcionCorta() +
        			        "'  onclick='javascript:agregar_producto(this);'/>";
        	  cadena +=  "</td>";
        	  if(columnas==unidadesPorFila) {
        		cadena += "</tr>";
        		columnas=0;
        	  }
		  }
		  cadena +=  "</table>";

//-----------		
		
		
		
       		
			 
		resultado = "{ \"productos\" : \"" 
				    + cadena 
			        + "\" }";
		
		return resultado;
		
		
	}
}