package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class ValidaTipoDocumentoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Afiliado> listaAfi = null;
		
		int result=0;

		String tipoDoc = req.getParameter("tipodoc");
		String nroDoc =  req.getParameter("nrodoc");
		
		listaAfi = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(nroDoc, tipoDoc);

		if(listaAfi != null && listaAfi.size() > 0){
			result=2; // existe en el padron
		}
		
		return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
	}
}