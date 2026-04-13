package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;

import java.util.List;

import com.liferay.portal.struts.JSONAction;


public class TraeImportePrestacionSeleccionadaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String codigoPrestacion = req.getParameter("codigoPrestacion");
		int idPrestacion=0;
		
		List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(8,"",0, codigoPrestacion ,false,"");
		   for(Nomenclador nom:nomencladores){			   				   
			   idPrestacion= nom.getId_prestacion();
		   }
		
		Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(idPrestacion);
		
		String resultado = "{}";
		
		if(n != null)
	    resultado = "{ \"importe\" : \"" 
			    + n.getImporte()
			    + "\",\"importeGastos\" : \""
			    + n.getImporteGastos() 
			    + "\",\"importeHonorarios\" : \""
		        + n.getImporteHonorarios()  + "\" }";		
				
		return resultado;
	}
	

}