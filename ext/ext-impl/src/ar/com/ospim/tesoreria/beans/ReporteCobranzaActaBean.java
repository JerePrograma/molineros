package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteCobranzaActaBean extends ReporteActaBean {
	private BigDecimal pagado;

	public ReporteCobranzaActaBean() {
		super();
	}

	public static ReporteCobranzaActaBean getMapping(ResultSet rs)
			throws SQLException {
		ReporteCobranzaActaBean reporte = new ReporteCobranzaActaBean();
		map(rs, reporte,false);
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
