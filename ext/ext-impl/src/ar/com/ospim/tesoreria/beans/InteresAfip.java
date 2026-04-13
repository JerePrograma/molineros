package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class InteresAfip {
	private Date fin;
	private Date ini;
	private BigDecimal interesDiario;

	public void setIni(Date ini) {
		this.ini = ini;
	}

	public Date getIni() {
		return ini;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	public Date getFin() {
		return fin;
	}

	public BigDecimal getInteresDiario() {
		return interesDiario;
	}

	public void setInteresDiario(BigDecimal interes) {
		this.interesDiario = interes;
	}

	public static InteresAfip getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static InteresAfip getMapping(ResultSet rs, String prefix)
			throws SQLException {
		InteresAfip interes = new InteresAfip();
		interes.setFin(rs.getDate(prefix + "fecha_fin"));
		interes.setIni(rs.getDate(prefix + "fecha_inicio"));
		interes.setInteresDiario(rs.getBigDecimal(prefix + "interes_por_dia"));
		return interes;
	}

}
