package ar.com.ospim.global.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.global.beans.Concepto;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public abstract class TraeConceptosJSONAction extends JSONAction {

	protected Date getDesde(HttpServletRequest req) throws ParseException {
		Date fechaDesde = null;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		if (req.getParameter("ejercicio") == null) {
			String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
			String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
			fechaDesde = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-"
					+ fechaDesdeAnio);
		} else {
			String ej = req.getParameter("ejercicio");
			String dd = "01-08-" + Integer.valueOf(ej.split("-")[0]);
			fechaDesde = format.parse(dd);
		}
		return fechaDesde;
	}

	protected Date getHasta(HttpServletRequest req) throws ParseException {
		Date fechaFin = null;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		if (req.getParameter("ejercicio") == null) {
			String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
			fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
			String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
			fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
					+ fechaHastaAnio);
		} else {
			String ej = req.getParameter("ejercicio");
			String hta = "31-07-" + Integer.valueOf(ej.split("-")[1]);
			fechaFin = format.parse(hta);
		}
		return fechaFin;
	}

	protected Date getFecha(HttpServletRequest req) throws ParseException {
		Date fecha = null;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDia = ParamUtil.getString(req, "fechaDia");
		String fechaMes = ParamUtil.getString(req, "fechaMes");
		fechaMes = String.valueOf(Integer.valueOf(fechaMes) + 1);
		String fechaAnio = ParamUtil.getString(req, "fechaAnio");
		fecha = format.parse(fechaDia + "-" + fechaMes + "-" + fechaAnio);
		return fecha;
	}

	protected String getConceptosJSON(List<Concepto> conceptos,
			boolean mostrarValidez) {
		boolean primero = true;
		SimpleDateFormat formatConc = new SimpleDateFormat("MM/yyyy");
		StringBuilder sb = new StringBuilder();
		sb.append("{\"conceptos\":[");
		for (Concepto c : conceptos) {
			if (!primero) {
				sb.append(",");
			}
			sb.append("{\"id\":\"" + c.getId() + "\",\"descripcion\":\""
					+ c.getDescripcion() + "\",\"id_seccional\":\""
					+ c.getIdSeccional());
			if (mostrarValidez) {
				sb.append(" ");
				sb.append(formatConc.format(c.getValidoDesde()) + "-"
						+ formatConc.format(c.getValidoHasta()));
			}
			sb.append("\"}");
			primero = false;
		}
		sb.append("]}");
		return sb.toString();
	}

	protected String getConceptoJSON(Concepto concepto, boolean mostrarValidez) {

		StringBuilder sb = new StringBuilder();

		sb.append("{\"id\":\"" + concepto.getId() + "\",\"descripcion\":\""
				+ concepto.getDescripcion() + "\",\"id_seccional\":\""
				+ concepto.getIdSeccional());
		sb.append("\"}");

		return sb.toString();
	}
}
