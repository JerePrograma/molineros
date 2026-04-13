package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReporteOrdenesPagoAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		Date fechaInicio = format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		List<ReporteOrdenPagoOspim> reporte = OrdenPagoServiceUtil
				.reporteOrdenPagoOspim(fechaInicio, fechaFin);

		req.setAttribute("ops", reporte);

		return mapping
				.findForward("portlet.liquidaciones.reporte.ordenes.pago.result");
	}

	public static class ReporteOrdenPagoOspim {
		private Date fecha;
		private int idOrdenPago;
		private BigDecimal importeOp;
		private Date fechaBajaOP;
		private Empresa acreedor;
		private Seccional seccional;
		private String aFavorDe;

		public Date getFecha() {
			return fecha;
		}

		public String getFechaAsString() {
			return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT)
					: "";
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public int getIdOrdenPago() {
			return idOrdenPago;
		}

		public void setIdOrdenPago(int idOrdenPago) {
			this.idOrdenPago = idOrdenPago;
		}

		public BigDecimal getImporteOp() {
			return importeOp;
		}

		public void setImporteOp(BigDecimal importeOp) {
			this.importeOp = importeOp;
		}

		public String getaFavorDe() {
			return aFavorDe;
		}

		public void setaFavorDe(String aFavorDe) {
			this.aFavorDe = aFavorDe;
		}

		public static ReporteOrdenPagoOspim getMapping(ResultSet rs)
				throws SQLException {
			ReporteOrdenPagoOspim repo = new ReporteOrdenPagoOspim();

			String razon = rs.getString("razon_soc");

			repo.setFecha(rs.getDate("fecha"));
			repo.setIdOrdenPago(rs.getInt("id_orden_pago"));
			repo.setImporteOp(rs.getBigDecimal("importe_op"));
			repo.setFechaBajaOP(rs.getDate("op_baja_fecha"));
			repo.setaFavorDe(rs.getString("a_favor_de"));

			String cuitAcreedor = rs.getString("cuit_acreedor");
			String sucuAcreedor = rs.getString("sucu_acreedor");
			int seccional = rs.getInt("id_seccional");

			repo.setAcreedor(new Empresa(cuitAcreedor, sucuAcreedor, razon));
			repo.setSeccional(new Seccional(seccional, null));
			return repo;
		}

		public void setFechaBajaOP(Date fechaBajaOP) {
			this.fechaBajaOP = fechaBajaOP;
		}

		public Date getFechaBajaOP() {
			return fechaBajaOP;
		}

		public String getFechaBajaOPAsString() {
			return null != fechaBajaOP ? DateUtils.format(fechaBajaOP,
					DateUtils.SHORT) : "";
		}

		public void setAcreedor(Empresa acreedor) {
			this.acreedor = acreedor;
		}

		public Empresa getAcreedor() {
			return acreedor;
		}

		public void setSeccional(Seccional seccional) {
			this.seccional = seccional;
		}

		public Seccional getSeccional() {
			return seccional;
		}
	}
}
