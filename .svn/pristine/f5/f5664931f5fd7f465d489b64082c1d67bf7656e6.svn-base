package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PlanCuentas implements Comparable<PlanCuentas>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5062051902891002165L;
	
	private int id;
//	private int idCuentaMaestro; //todavia no se utiliza, se usa como id en uoma y ospim
	private String numero;
	private String cuenta;
	private boolean imputable;
	private Date validoDesde;
	private Date validoHasta;
	private String tipo;
	private boolean ajustaInflacion;
	private boolean ajustaInflacionConPeriodoEjercicioAnterior;

	public PlanCuentas(int id) {
		this.id = id;
	}

	public PlanCuentas(String numero, String cuenta) {
		super();
		this.numero = numero;
		this.cuenta = cuenta;
	}

	public PlanCuentas() {
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getCuenta() {
		return cuenta;
	}
	
	public boolean isAjustaInflacionConPeriodoEjercicioAnterior() {
		return ajustaInflacionConPeriodoEjercicioAnterior;
	}

	public void setAjustaInflacionConPeriodoEjercicioAnterior(boolean ajustaInflacionConPeriodoEjercicioAnterior) {
		this.ajustaInflacionConPeriodoEjercicioAnterior = ajustaInflacionConPeriodoEjercicioAnterior;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		PlanCuentas other = (PlanCuentas) obj;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	public static PlanCuentas getMapping(ResultSet rs) throws SQLException {
		PlanCuentas pc = new PlanCuentas();
		pc.setNumero(rs.getString("numero"));
		pc.setCuenta(rs.getString("cuenta"));
		pc.setImputable(rs.getBoolean("imputable"));
		
		try {
			pc.setTipo(rs.getString("tipo"));
		} catch (Exception e) {
		}
		
		try {
			pc.setValidoDesde(rs.getDate("valido_desde"));
			pc.setValidoHasta(rs.getDate("valido_hasta"));
		} catch (Exception e) {
		}

		try {
			pc.setId(rs.getInt("id"));
		} catch (Exception e) {

		}
		try {
			pc.setAjustaInflacion(rs.getBoolean("ajusta_inflacion"));
		} catch (Exception e) {

		}
		
		try {
			pc.setAjustaInflacionConPeriodoEjercicioAnterior(rs.getBoolean("ajusta_inflacion_con_periodo_ej_anterior"));
		} catch (Exception e) {

		}
		return pc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isImputable() {
		return imputable;
	}

	public void setImputable(boolean imputable) {
		this.imputable = imputable;
	}

	public int compareTo(PlanCuentas o) {
		return numero.compareTo(o.numero);
	}

	public Date getValidoDesde() {
		return validoDesde;
	}

	public void setValidoDesde(Date validoDesde) {
		this.validoDesde = validoDesde;
	}

	public Date getValidoHasta() {
		return validoHasta;
	}

	public void setValidoHasta(Date validoHasta) {
		this.validoHasta = validoHasta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
/*
	public boolean isAjustaInflacion() {
		return ajustaInflacion;
	}
*/
	public void setAjustaInflacion(boolean ajustaInflacion) {
		this.ajustaInflacion = ajustaInflacion;
	}

	public boolean getAjustaInflacion() {
		return ajustaInflacion;
	}
}
