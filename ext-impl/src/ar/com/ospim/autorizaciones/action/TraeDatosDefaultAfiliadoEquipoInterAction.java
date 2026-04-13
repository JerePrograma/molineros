package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.services.EquipoInterdisciplinarioServiceUtil;


import com.liferay.portal.struts.JSONAction;

public class TraeDatosDefaultAfiliadoEquipoInterAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String cuilTitular = req.getParameter("cuil");
		String inte  = req.getParameter("inte");		
		EquipoInterdisciplinario equipoInter = EquipoInterdisciplinarioServiceUtil.getInstance().buscarDatosAfiliadoEquipoInterInter( Integer.parseInt(inte),cuilTitular );    
		
		String resultado = "{}";
		
		
		if(equipoInter != null)
	    resultado = "{ \"calle\" : \"" 
			    + equipoInter.getAfiliado().getDomicilioDefault().getCalle()
			    + "\",\"numero\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getNumero() 
			    + "\",\"piso\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getPiso() 
			    + "\",\"dpto\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getDepto()
			    + "\",\"barrio\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getBarrio()
			    + "\",\"codareatelefono\" : \""
			    + equipoInter.getTelefonoContacto().getCodigoArea()
			    + "\",\"telefono\" : \""
			    + equipoInter.getTelefonoContacto().getNumero()
			    + "\",\"tipotelefono\" : \""
			    + equipoInter.getTelefonoContacto().getTipo()
			    + "\",\"email\" : \""
			    + equipoInter.getAfiliado().getEmail()
			    + "\",\"localidad\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getLocalidadId()
			    + "\",\"provincia\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getProvinciaId()
			    + "\",\"codcie10\" : \""
			    + equipoInter.getCodigoCie10()
			    + "\",\"diagnostico\" : \""
			    + equipoInter.getDiagnosticoAfiliado() 
			    + "\",\"tipodomi\" : \""
			    + equipoInter.getTipoDomicilio()
			    + "\",\"codpostal\" : \""
			    + equipoInter.getAfiliado().getDomicilioDefault().getPostal_codi()
		        + "\",\"localidadtexto\" : \""
		        + equipoInter.getAfiliado().getDomicilioDefault().getLocalidadAsString()  + "\" }";		
				
		return resultado;
	}
	

}