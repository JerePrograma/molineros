package ar.com.ospim.farmaciaOspim.action; 



import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ConsultaNroRegistroMedicamento extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Medicamento> busqueda;
		int nroRegistro= ParamUtil.getInteger(req,"nroRegistro");
		int nroTroquel= ParamUtil.getInteger(req,"nroTroquel");
		int idMedicacion = ParamUtil.getInteger(req,"idMedicamento",0);		 
		boolean esTroquel= ParamUtil.getBoolean(req,"esTroquel",false);
		boolean esVademecum= ParamUtil.getBoolean(req,"esVademecum",false);
		boolean respuesta = false;
		Medicamento medicamento=null;
		if (esVademecum || !esTroquel ){
			busqueda = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentosxRegistrooxTroquel(nroRegistro,0);
				if (busqueda!=null && busqueda.size() >0){
					if (idMedicacion >0 ){ // medicamento por registro
						if( !busqueda.get(0).getNombre().equals("") && idMedicacion !=busqueda.get(0).getId_medicamento() ){
						medicamento = busqueda.get(0);
						respuesta=true;
						}	
					}else{ // vademecum por registro
						medicamento = busqueda.get(0);
						if (medicamento.getRegistro() != 0  ){
							medicamento.setPmo(true);// esta en vademecum 
							respuesta=false;
						}else{
							respuesta=!medicamento.isPmo() ;	
						}	
					}	
				}
		}else{ // medicamento por nro de troquel 
			busqueda = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentosxRegistrooxTroquel(0,nroTroquel);
			if (esTroquel){
				if (nroTroquel>0){
					if (busqueda!=null  && busqueda.size()>0 ){
						medicamento = busqueda.get(0);
						if( !busqueda.get(0).getNombre().equals("") && idMedicacion !=busqueda.get(0).getId_medicamento() ){
							respuesta=true;
						}	
					}
				}	
			}
		}
		
				
        String resultado = "{}";		
	    if (medicamento ==null || busqueda.size()==0){
	    	resultado = "{ \"nroRegistroSoloEnMedicamentos\" : \"" 
				    + respuesta    + "\" }";
	    	
	    }else{
	    	resultado = "{ \"nroRegistroSoloEnMedicamentos\" : \"" 
				    + respuesta
				    + "\",\"nombreMedicacion\" : \""
				    + medicamento.getNombre().toUpperCase() 
				    + "\",\"presentacion\" : \""
				    + medicamento.getPresentacion().toUpperCase() 
				    + "\",\"laboratorio\" : \""
				    + medicamento.getLaboratorio().toUpperCase()
				    + "\",\"estaEnVademecum\" : \""
				    + medicamento.isPmo()
				    + "\",\"troquel\" : \""
				    + medicamento.getTroquel()
				    + "\",\"accion\" : \""
				    + medicamento.getAccion().toUpperCase()
				    + "\",\"droga\" : \""
			        + medicamento.getDroga().toUpperCase()  + "\" }";
	    }
	    
		return resultado;
		
		
	}

}