package ar.com.ospim.hoteles.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Consumo;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.beans.Personal;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

public class HotelesConfiguracionAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		List<Mesa>mesas=new ArrayList<Mesa>();
		String tipo = req.getParameter("tipo");
		String ptovta = req.getParameter("ptovta");
		String idGrupo = req.getParameter("idgrupo");
		String idEmpleado = req.getParameter("idempleado");
		
		String resultado = "{}";
		String titulo="";
		String claseCSS="";
        String funcion="";
        String valor="";
        String parametroFunction="";
        String idNbe="";
		Integer unidadesPorFila=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("HOTEL_" + ptovta +"_UNIDADES_POR_FILA") );
		Integer unidadesPorFilaCategorias=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("HOTEL_" + ptovta +"_UNIDADES_POR_FILA_CATEGORIAS") );
 		Integer total=0;
 		
 		String tipoCategoria="";
 		String empleado="";
 		String idPersonal="";
 		String necesitaLogin="true";

 		
 		List<Personal>mozos = HotelesServiceUtil.getPersonal(ptovta, "MOZO", null);
// 		List<Personal>mucamas = HotelesServiceUtil.getPersonal(ptovta, "HABI", null);
 		if ("MESAS".equalsIgnoreCase(tipo) && mozos.size()<=1) {
 			if(mozos.size()==1) {
    		   empleado= mozos.get(0).getApellido() + " " +mozos.get(0).getNombre();
    		   idPersonal=mozos.get(0).getId().toString();
 			}   
    		necesitaLogin="false";
    	}
 		
 		if (("HABITACIONES".equalsIgnoreCase(tipo) || "HABITACIONESGRUPOS".equalsIgnoreCase(tipo)) /*&& mucamas.size()<=1*/) {
/* 			
 			if(mucamas.size()==1) {
    		  empleado= mucamas.get(0).getApellido() + " " +mucamas.get(0).getNombre();
    		  idPersonal=mucamas.get(0).getId().toString();
 			}
*/
    		necesitaLogin="false";
    	}
 		 		
 		
		if("MESAS".equalsIgnoreCase(tipo)) {
//			total=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("HOTEL_" + ptovta +"_MESAS") );
			mesas=HotelesServiceUtil.getMesas(ptovta, null);
			total=mesas.size();
			titulo="Mesas";
			claseCSS="unidades";
			funcion="mostrarMesa";
			idNbe=tipo;
			tipoCategoria="MESAS";
			
		}else if("HABITACIONESGRUPOS".equalsIgnoreCase(tipo)) {
			grupos = HotelesServiceUtil.getGrupos(ptovta);
			total=grupos.size();
			titulo="Pisos";
			claseCSS="grupos";
			funcion="mostrarUnidadesGrupo";
			idNbe="HABITACIONES";
			tipoCategoria="HABITACIONES";
		}else if("HABITACIONES".equalsIgnoreCase(tipo)) {
			grupos = HotelesServiceUtil.getHabitaciones(ptovta,idGrupo);
			total=grupos.size();
			titulo="Habitaciones";
			claseCSS="unidades";
			funcion="mostrarHabitacion";
			idNbe="HABITACION";
			tipoCategoria="";
		}
		
		int columnas=0;
		int inicioRenglon=0;
		String categorias ="";
		
//      Busco Categorías de Productos
		if(!tipoCategoria.isEmpty()) {
		   List<ProductoCategoria> cats = HotelesServiceUtil.getProductosCategoriasHabilitados(ptovta, tipoCategoria);
		   categorias ="<table class='unidades'>";
		   
		   for(ProductoCategoria c:cats) {
			  if(columnas==0) {
        		 categorias += "<tr>";
        	  }
        	  columnas++;
        	  categorias +=  "<td>";
        	  categorias +=  "<input type='button' id='categoria_"+idNbe+"_"+ptovta+"_" + c.getCodigo() + "' class='productos_categorias' value='" + c.getDescripcion() +
        			        "'  onclick='javascript:mostrar_productos(this);'/>";
        	  categorias +=  "</td>";
        	  if(columnas==unidadesPorFilaCategorias) {
        		categorias += "</tr>";
        		columnas=0;
        	  }
		  }
		  categorias +=  "</table>";
		}
//-----------		
		
		
		
        String json = ""; 
		
		json+=  "<fielset><legend>" + titulo +"</legend><table class='unidades'>";
		columnas=0;
		inicioRenglon=0;
        for(int i = 0; i < total; i++) {
        	if(columnas==0) {
        		json += "<tr>";
        	}
        	columnas++;
        	
        	if("MESAS".equalsIgnoreCase(tipo)) {/// -- Analisis de Estados de Mesas
        		valor=String.format("%02d", mesas.get(i).getNumero());
 //       		parametroFunction= String.valueOf(i+1);
        		parametroFunction="this";
        		
        		List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,valor);
        		if(consumos.isEmpty()) {
        			claseCSS="unidades";
        		}else {
        			if("PCU".equalsIgnoreCase(consumos.get(0).getEstado())){
        				claseCSS="unidades_con_cuenta";
        			}else {
        				claseCSS="unidades_ocupadas";
        			}
        		}
        		
        		if(mozos.size()>1) claseCSS="unidades_disabled";
        		
        	}else if("HABITACIONESGRUPOS".equalsIgnoreCase(tipo)) {
        		valor=grupos.get(i).getGrupo();
//        		parametroFunction="'"+tipo+"','"+ptovta+"','"+grupos.get(i).getGrupo()+"'";
        		parametroFunction="this";
        		
        	}else if("HABITACIONES".equalsIgnoreCase(tipo)) {
//        		valor=grupos.get(i).getDescripcion();
        		
//REVISAR         		
        		valor=String.format("%02d", grupos.get(i).getNumero());
        		
//        		parametroFunction=String.valueOf(grupos.get(i).getNumero());
        		parametroFunction="this";
        		
        		
        		List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, "HABITACION",valor);
        		if(consumos.isEmpty()) {
        			claseCSS="unidades";
        		}else {
        			claseCSS="unidades_ocupadas";
        		}
/*        		
        		if(mucamas.size()>1) {  ///REVISAR
        		  if(idEmpleado==null || "".equals(idEmpleado)) {	
        			claseCSS="unidades_disabled";
        		  }	
        		}
*/        		
        	}
        	
        	json +=  "<td>";
        	json +=  "<input type='button' id='unidad_"+idNbe+"_"+ptovta+"_" + valor + "' class='" +claseCSS +"' value='" + valor +"'" ;
        	if("MESAS".equalsIgnoreCase(tipo) && mozos.size()>1) {
        		json+= "disabled='disabled'";
        	}
/*
        	if("HABITACIONES".equalsIgnoreCase(tipo) && mucamas.size()>1) { /// REVISAR Modificar para bloquear habitaciones no asignadas
        		json+= "disabled='disabled'";
        	}
*/       	
        	json +=  " onclick='javascript:"+funcion+"("+ parametroFunction +");'/>";
        	json +=  "</td>";
        	if(columnas==unidadesPorFila) {
        		json += "</tr>";
        		columnas=0;
        	}
		}	
        
        int count = json.length();
		json +=  "</table></fieldset>";
		
			 
		resultado = "{ \"cadena\" : \"" 
				    + json 
				    + "\",\"categorias\" : \""
				    + categorias
				    + "\",\"empleado_str\" : \""
				    + empleado
				    + "\",\"empleado_id\" : \""
				    + idPersonal
				    + "\",\"necesitaLogin\" : \""
				    + necesitaLogin
			        + "\" }";
		
		return resultado;
		
		
	}
}