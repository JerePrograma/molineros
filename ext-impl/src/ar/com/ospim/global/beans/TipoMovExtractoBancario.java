package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.tesoreria.beans.TipoMovBcrio;

public class TipoMovExtractoBancario {
	private int codigoMovimiento;
	private String descripcionMovimiento;
	private TipoMovBcrio tipoMovimientoBancario;

	public int getCodigoMovimiento() {
		return codigoMovimiento;
	}

	public void setCodigoMovimiento(int codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}

	public String getDescripcionMovimiento() {
		return descripcionMovimiento;
	}

	public void setDescripcionMovimiento(String descripcionMovimiento) {
		this.descripcionMovimiento = descripcionMovimiento;
	}

	public TipoMovBcrio getTipoMovimientoBancario() {
		return tipoMovimientoBancario;
	}

	public void setTipoMovimientoBancario(TipoMovBcrio tipoMovimientoBancario) {
		this.tipoMovimientoBancario = tipoMovimientoBancario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigoMovimiento;
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
		TipoMovExtractoBancario other = (TipoMovExtractoBancario) obj;
		if (codigoMovimiento != other.codigoMovimiento)
			return false;
		return true;
	}

	public static TipoMovExtractoBancario getMapping(ResultSet rs)
			throws SQLException {
		TipoMovExtractoBancario tipoMovExt = new TipoMovExtractoBancario();
		tipoMovExt.setCodigoMovimiento(rs.getInt("codigo_movimiento"));
		tipoMovExt.setDescripcionMovimiento(rs
				.getString("descripcion_movimiento"));
		tipoMovExt
				.setTipoMovimientoBancario(new TipoMovBcrio(rs
						.getInt("id_tipo_mov"), rs
						.getString("descripcion_movimiento")));
		return tipoMovExt;
	}
}
