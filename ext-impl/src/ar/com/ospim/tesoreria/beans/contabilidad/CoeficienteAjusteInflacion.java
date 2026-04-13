package ar.com.ospim.tesoreria.beans.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class CoeficienteAjusteInflacion implements Serializable {
	
	private static final long serialVersionUID = -8033332881017590912L;

	private static Log logger = LogFactoryUtil.getLog(CoeficienteAjusteInflacion.class);
	
	
	private Integer periodo;
	private BigDecimal coeficiente;
	private int entidad;
	
	public Integer getPeriodo() {
		return periodo;
	}


	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}


	public BigDecimal getCoeficiente() {
		return coeficiente;
	}


	public void setCoeficiente(BigDecimal coeficiente) {
		this.coeficiente = coeficiente;
	}


	public int getEntidad() {
		return entidad;
	}


	public void setEntidad(int entidad) {
		this.entidad = entidad;
	}


	private static CoeficienteAjusteInflacion getMapping(ResultSet rs, String prefix)
			throws SQLException {
		CoeficienteAjusteInflacion coeficiente = new CoeficienteAjusteInflacion();
		coeficiente.setEntidad(rs.getInt(prefix +"entidad"));
		coeficiente.setCoeficiente(rs.getBigDecimal(prefix +"coeficiente"));
		coeficiente.setPeriodo(rs.getInt(prefix + "periodo"));
		return coeficiente;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (periodo!=null?periodo:0) + entidad;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoeficienteAjusteInflacion other = (CoeficienteAjusteInflacion) obj;
		if (entidad != other.entidad) return false;
		if (periodo.compareTo(other.periodo)!=0 ) return false;
		return true;
	}


}
