package ar.com.ospim.farmacia.action;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceImpl;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class CalcularValorMedicamentoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		int troquel = ParamUtil.getInteger(req, "troquel");
		String precio_publico = req.getParameter("precio");
		int id_plan = ParamUtil.getInteger(req, "id_plan");
		
		TraeListasServiceImpl buscaPlanes= new TraeListasServiceImpl();
		List<Plan> planes=buscaPlanes.getPlanes();
		
		Medicamento med=BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(troquel, 0, null, null, null, id_plan,null,planes, new BigDecimal(precio_publico)).get(0);
		
		BigDecimal monto_cober_amtima =(med!=null && med.getMonto_cober_amtima()!=null ?med.getMonto_cober_amtima():BigDecimal.ZERO);
		BigDecimal monto_cober_ospim =(null!=med && med.getMonto_cober_ospim()!=null?med.getMonto_cober_ospim():BigDecimal.ZERO) ;
		BigDecimal precio =(null!=med && med.getPrecio()!=null?med.getPrecio():BigDecimal.ZERO);
		return "{ \"precio_amtima\" : \"" + monto_cober_amtima + 
				"\",\"precio_ospim\" : \""+monto_cober_ospim +
				"\",\"precio_publico\" : \""+precio +
				"\",\"total_cobertura\" : \""+(null!=med?monto_cober_ospim.add(monto_cober_amtima):BigDecimal.ZERO) +"\"}";
	}
}
