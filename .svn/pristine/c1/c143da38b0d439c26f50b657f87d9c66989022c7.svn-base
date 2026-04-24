package ar.com.ospim.liquidaciones.action; 



import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ConsultaMedicamentoActionJSON extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int nroTroquel= ParamUtil.getInteger(req,"nroTroquel");
		Medicamento medicamento=null;
		List<Medicamento> busqueda = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(nroTroquel, 0, null, null, null, 0, null, null,null);
		if(busqueda.size()>0) {
			medicamento=busqueda.get(0);
		}
		
		String droga= (medicamento.getDroga()!=null? medicamento.getDroga().toUpperCase() :"");
	    String resultado = "{}";		
	    
        if(medicamento!=null) {
	        resultado = "{ \"nombreMedicacion\" : \""
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
				    + "\",\"codBarras\" : \""
				    + medicamento.getCod_barra()
				    + "\",\"idMedicamento\" : \""
				    + medicamento.getId_medicamento()
				    + "\",\"droga\" : \""
			        +  droga + "\" }";
        }
		return resultado;
		
		
	}

}