package ar.com.ospim.farmaciaOspim.action;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil;
import ar.com.ospim.procesaArchivos.beans.ArchivoVademecum;


import com.liferay.portal.struts.JSONAction;

public class BuscarPeriodoVademecum extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		List<ArchivoVademecum> lista = FarmaciaServiceUtil.getArchivosSubidosVademecum();
		
		String resultado = "";
		try{
			resultado=sdf.format(lista.get(0).getPeriodo());
		}catch(Exception e){}
		if(lista != null)
	     resultado = "{ \"fechaPeriodo\" : \"" 
				    + resultado 
			        + "\",\"periodo\" : \""
			        + lista.get(0).getPeriodo()+ "\" }";
				
		return resultado;
	}

}