package ar.com.ospim.autorizaciones.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;

public class PreautorizacionesVerificaARTJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String cuilTitular =ParamUtil.getString(req, "cuil_titular");
		Integer inte=ParamUtil.getInteger(req,"inte");
		boolean tieneART=false;	
		String mensaje="";
		try {
		
			HttpSession session = req.getSession();
			BusquedaPreautorizacionesFiltro filtro=new BusquedaPreautorizacionesFiltro(0,cuilTitular, inte, null, null,
					null, null, null, null, false,
					false, false, false, false, false,
					false, 0, false, true,0);
/*			
			BusquedaPreautorizacionesFiltro(Integer id, String cuil, Integer inte, Date fechaD, Date fechaH,
					String estado, Date fechaEmail, Date fechaEmailH, Integer seccional, boolean alertaRoja,
					boolean discapacidad, boolean supra, boolean cirugia, boolean medicamento, boolean sinReintento,
					boolean alojamiento, Integer idAutorizacion, boolean protesisOrt, boolean ART, int pagina)
*/					
		    List<PreAutorizacion>lista =PreAutorizacionServiceUtil.getListaPreAutorizacion(filtro);
			
			if( !lista.isEmpty()) {
				tieneART=true;
				mensaje="POSIBLE ART ---> " + 
						"Presione ACEPTAR si esta autorización se relaciona con la autorización anterior marcada como Posible Art. ";
			}
			
			
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"tieneART\" : \"" + tieneART +
				 "\",\"mensaje\" : \"" + mensaje +
				"\"}";
		
	}
	
}
