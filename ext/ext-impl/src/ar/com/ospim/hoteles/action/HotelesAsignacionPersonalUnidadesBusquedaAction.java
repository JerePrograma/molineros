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

public class HotelesAsignacionPersonalUnidadesBusquedaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		List<Mesa>mesas=new ArrayList<Mesa>();
		String tipo = req.getParameter("tipo");
		String ptovta = req.getParameter("ptovta");
		String idPersonal= req.getParameter("idpersonal");
		
		String resultado = "{}";
		Integer unidadesPorFila=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("HOTEL_" + ptovta +"_UNIDADES_POR_FILA") );
 		Integer total=0;
 		
 		String valor="";
 		String unidades="";
 		
 		if("MESAS".equalsIgnoreCase(tipo)) {
 			
 			mesas=HotelesServiceUtil.getMesas(ptovta, null);
			total=mesas.size();
			List<Mesa>mesasHabilitadas =HotelesServiceUtil.getMesasByPersonal(ptovta, Integer.parseInt(idPersonal));
 			
 			int columnas=0;
 			int inicioRenglon=0;
 			
 			unidades+=  "<fielset><legend>MESAS</legend><table style='border: 3px solid #000000;font-size: 13px;width: 32%;display: inline-block;'>";
 			columnas=0;
 			inicioRenglon=0;
 	        for(int i = 0; i < total; i++) {
 	        	if(columnas==0) {
 	        		unidades += "<tr>";
 	        	}
 	        	columnas++;
 	        	valor=String.format("%02d", mesas.get(i).getNumero());
 	        	
 	        	boolean mesaOk=false;
 	        	for(Mesa m:mesasHabilitadas) {
 	        		if(m.getNumero()==mesas.get(i).getNumero()) {
 	        		  mesaOk=true;
 	        		  break;
 	        		}  
 	        	}
 	        	
 	        	unidades +=  "<td style='border: 1px solid black;padding: 10px;'>";
 	        	unidades += "<label>Mesa " + valor +"</label>";
 	        	unidades +=  "<input type='checkbox' value='"+ptovta+"_" + valor + "' name='unidades[]'" ;
 	        	
 	        	if(mesaOk) unidades+= "checked='checked'";
 	        	
 	        	unidades +="/>";
 	        	unidades +=  "</td>";
 	        	if(columnas==unidadesPorFila) {
 	        		unidades += "</tr>";
 	        		columnas=0;
 	        	}
 			}	
 	        
 	        unidades +=  "</table></fieldset>";
 			
 			
 		}
 		resultado = "{\"unidades\" : \""
				    + unidades
			        + "\" }";
		
		return resultado;
		
		
	}
}