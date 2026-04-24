package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanCuentasSSS implements Comparable<PlanCuentasSSS>,Serializable {
	
	private static final long serialVersionUID = 5333306524077626598L;
	
	private int id;
	private String numero;
	private String cuenta;
	private String tipo;
	private String acumulaSobre;
	private int signo;
	private List<PlanCuentas> equivalencias;
	
	public PlanCuentasSSS(int id) {
		this.id = id;
	}

	public PlanCuentasSSS(String numero, String cuenta) {
		super();
		this.numero = numero;
		this.cuenta = cuenta;
	}

	public PlanCuentasSSS() {
		equivalencias=new ArrayList<PlanCuentas>();
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
		PlanCuentasSSS other = (PlanCuentasSSS) obj;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	public static PlanCuentasSSS getMapping(ResultSet rs) throws SQLException {
		PlanCuentasSSS pc = new PlanCuentasSSS();
		pc.setNumero(rs.getString("numero"));
		pc.setCuenta(rs.getString("descripcion"));
		
		
		try {
			pc.setTipo(rs.getString("tipo"));
		} catch (Exception e) {
		}
		
		try {
			pc.setAcumulaSobre(rs.getString("acumula_sobre"));
		} catch (Exception e) {
		}

		try {
			pc.setId(rs.getInt("id"));
		} catch (Exception e) {

		}
		
		try {
			pc.setSigno(rs.getInt("signo"));
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

	
	public int compareTo(PlanCuentasSSS o) {
		return numero.compareTo(o.numero);
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getAcumulaSobre() {
		return acumulaSobre;
	}

	public void setAcumulaSobre(String acumulaSobre) {
		this.acumulaSobre = acumulaSobre;
	}

	public int getSigno() {
		return signo;
	}

	public void setSigno(int signo) {
		this.signo = signo;
	}

	public List<PlanCuentas> getEquivalencias() {
		return equivalencias;
	}

	public void setEquivalencias(List<PlanCuentas> equivalencias) {
		this.equivalencias = equivalencias;
	}

	
}
