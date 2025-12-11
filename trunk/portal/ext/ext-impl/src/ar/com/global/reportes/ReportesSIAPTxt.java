package ar.com.global.reportes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.global.services.ReportesSIAPTxtImpl;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReportesSIAPTxt extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReportesSIAPTxt.class);
	

	public static List<String> generaArchivoRetenGanancias(HttpServletRequest req,
			HttpServletResponse res) {
		Calendar periodo=Calendar.getInstance();
		periodo.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
		periodo.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
		periodo.set(Calendar.DAY_OF_MONTH, ParamUtil.getInteger(req, "fechaDesdeDia"));
		int entidad=ParamUtil.getInteger(req, "entidad");
		try {
			ReportesSIAPTxtImpl repo=new ReportesSIAPTxtImpl();
			ArrayList<String> lista = (ArrayList<String>) repo.getReporteRetencionGanancias(periodo.getTime(), entidad);
			return lista;
		} catch (Exception e) {
			_log.error("Error al generar archivo Deriva", e);
			return null;
		}
	}

	
}
