package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteCobranzaConvenioBean extends ReporteConvenioBean {
	private BigDecimal pagado;

	public ReporteCobranzaConvenioBean() {
		super();
	}

	public static ReporteCobranzaConvenioBean getMapping(ResultSet rs)
			throws SQLException {
		ReporteCobranzaConvenioBean reporte = new ReporteCobranzaConvenioBean();
		map(rs, reporte, false);
		reporte.setPagado(rs.getBigDecimal("pagado"));
		return reporte;
	}

	public BigDecimal getPagado() {
		return pagado;
	}

	public void setPagado(BigDecimal pagado) {
		this.pagado = pagado;
	}
}
