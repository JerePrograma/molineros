package ar.com.ospim.tesoreria.actas.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.services.EmpresaServiceImpl;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class BuscarCtasBancariasPorBancoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String cuit = req.getParameter("cuit");
		String sucur = req.getParameter("sucur");
		Integer idBanco = Integer.parseInt(req.getParameter("idBanco"));
		String json = "{\"listaFiltrada\": ["; 
		String ctasBcrias="";
//		
		List<CuentaBancaria> lista = EmpresaServiceImpl.getInstance().getCuentasBancariasPorBanco(cuit, sucur, idBanco, null);
//		
		if(lista!=null && lista.size()>0){
			
//			json+=  "\""+"<option selected value='0'>Seleccione una Cta. Bancaria</option>" +"\"" + ",";
			
			for (CuentaBancaria cb : lista) {
				json +=  "\""+ "<option value='" +cb.getId_cuenta_bcria()+"'> "+cb.getDescripcion() +"</option>"+"\""+",";
			}
		}else{
			json+=  "\""+"<option selected value='0'>Ver ayuda para más información.</option>"+"\""+",";
		}
		int count = json.length();
		ctasBcrias = json.substring(0, count-1);
		ctasBcrias += "]}";


		return ctasBcrias;
		
	}
}