package ar.com.ospim.liquidaciones.action;


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.liquidaciones.services.BusquedaDebitosTercerizadorasServiceUtil;
import ar.com.ospim.util.DateUtils;

public class HayReporteTercerizadoraAction extends JSONAction {


	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String idTercerizadora  = ParamUtil.getString(req, "tipo_debitos_tercerizadoras");
		
		Date fechaDesde = null;

		try {
			fechaDesde = formatoDeFechas.parse(01 + "/" + (Integer.parseInt(fechaDesdeMes) + 1) + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		Date fechaHasta = null;
		fechaHasta = DateUtils.getLastDateOfMonth(fechaDesde, false);

		boolean result = BusquedaDebitosTercerizadorasServiceUtil.existeReporteDebitoTercerizadoras(fechaDesde,  fechaHasta, idTercerizadora);
		

		if (result) {
			return "{ \"resultado\" : \"" + "1" + "\"}";
		} else {
			return "{ \"resultado\" : \"" + "0" + "\"}";

		}

	}

}