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
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class HotelesConfiguracionPersonalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		List<Mesa>mesas=new ArrayList<Mesa>();
		String tipo = req.getParameter("tipo");
		String ptovta = req.getParameter("ptovta");
		String login = req.getParameter("login");
		String idGrupo = req.getParameter("idgrupo");
		
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
 		String idPersonal="0";
 		String password="";
 		
 		if(login!=null && !login.isEmpty() && login.length()>3) {
 		   idPersonal=login.substring(0,3);
 		   password=login.substring(3);
 		}
 		
 		String resLogin="false";
 		String empleado="";
 		String unidades="";
 		
 		if("MESAS".equalsIgnoreCase(tipo)) {
 			
 			mesas=HotelesServiceUtil.getMesas(ptovta, null);
			total=mesas.size();
			titulo="Mesas";
			claseCSS="unidades";
			funcion="mostrarMesa";
			idNbe=tipo;
			tipoCategoria="MESAS";
 			
			List<Personal>mozos = HotelesServiceUtil.getPersonal(ptovta, "MOZO", Integer.parseInt(idPersonal));
 			List<Mesa>mesasHabilitadas =HotelesServiceUtil.getMesasByPersonal(ptovta, Integer.parseInt(idPersonal));
 			Personal mozo=new Personal();
 			if(mozos.size()>0 && mozos.get(0).getPassword().equals(password) ) {
 				resLogin="true";
 				empleado= mozos.get(0).getApellido() + " " +mozos.get(0).getNombre();
 				mozo= mozos.get(0);
 			}else if(mozos.size()>0) {
 				if(Integer.parseInt(idPersonal)==0) {
 				  mozos = HotelesServiceUtil.getPersonal(ptovta, "MOZO", Integer.parseInt(login));
 				 if(mozos.size()>0){
 				  empleado= mozos.get(0).getApellido() + " " +mozos.get(0).getNombre();
 				 } 
 				}  
 			}
 			
 			int columnas=0;
 			int inicioRenglon=0;
 			
 			unidades+=  "<fielset><legend>" + titulo +"</legend><table class='unidades'>";
 			columnas=0;
 			inicioRenglon=0;
 	        for(int i = 0; i < total; i++) {
 	        	if(columnas==0) {
 	        		unidades += "<tr>";
 	        	}
 	        	columnas++;
 	        	valor=String.format("%02d", mesas.get(i).getNumero());
 	        	parametroFunction="this";
 	        	
 	        	
 	        	boolean mesaOk=false;
 	        	for(Mesa m:mesasHabilitadas) {
 	        		if(m.getNumero()==mesas.get(i).getNumero()) {
 	        		  mesaOk=true;
 	        		  break;
 	        		}  
 	        	}
 	        	
 	        	
 	        	if(mesaOk) {
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
 	        		
 	        	}else { 	        	
 	        	   claseCSS="unidades_disabled";
 	        	}
 	        	
 	        	unidades +=  "<td>";
 	        	unidades +=  "<input type='button' id='unidad_"+idNbe+"_"+ptovta+"_" + valor + "' class='" +claseCSS +"' value='" + valor +"'" ;
 	        	
 	        	if(!mesaOk) unidades+= "disabled='disabled'";
 	        	
 	        	unidades +=  " onclick='javascript:"+funcion+"("+ parametroFunction +");'/>";
 	        	unidades +=  "</td>";
 	        	if(columnas==unidadesPorFila) {
 	        		unidades += "</tr>";
 	        		columnas=0;
 	        	}
 			}	
 	        
 	        unidades +=  "</table></fieldset>";
 			
 			
 		}else if("HABITACIONESGRUPOS".equalsIgnoreCase(tipo)){
/* 			
 			List<Personal>mucamas = HotelesServiceUtil.getPersonal(ptovta, "HABI", Integer.parseInt(idPersonal));
 			Personal mucama=new Personal();
 			if(mucamas.size()>0 && mucamas.get(0).getPassword().equals(password) ) {
 				resLogin="true";
 				empleado= mucamas.get(0).getApellido() + " " +mucamas.get(0).getNombre();
 				mucama= mucamas.get(0);
 			}
*/ 			
 			
 		}
		resultado = "{ \"resultado\" : \"" 
				    + resLogin 
				    + "\",\"empleado_str\" : \""
				    + empleado
				    + "\",\"empleado_id\" : \""
				    + idPersonal
				    
				    + "\",\"unidades\" : \""
				    + unidades
			        + "\" }";
		
		return resultado;
		
		
	}
}