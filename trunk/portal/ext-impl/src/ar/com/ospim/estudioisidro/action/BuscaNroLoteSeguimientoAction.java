package ar.com.ospim.estudioisidro.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.estudioisidro.beans.Llamado;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;

public class BuscaNroLoteSeguimientoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String cuit=ParamUtil.getString(req, "cuit","");
		
		
		Integer lote = null;
		String tipoLote="";
		
		Llamado l = LlamadoServiceUtil.getProponeNroLote(cuit,null);
		
		if(l!=null  && !"".equals(cuit)){
		//	msg="No se encuentra Convenio para la prestación solicitada";
			lote= l.getLote();
			tipoLote = l.getTipoLote();
		}else{
			
			
		}
		
		return "{ \"lote\" : \"" + lote + 
				"\",\"tipoLote\" : \"" + tipoLote + "\"}";
				
	}

}