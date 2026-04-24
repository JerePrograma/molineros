package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.util.List;

import ar.com.ospim.global.beans.Seccional;

public class ReintegroList {
	private int nroLista;
	
	private List<Reintegro> reintegros;

	private Seccional seccional;
	
	private String tipo;
	
	
	private String cbu;
	private String cuilCuenta;
	



	public ReintegroList() {
	}
	
	public ReintegroList(int nroLista) {
		this.nroLista = nroLista;
	}

	public int getNroLista() {
		return nroLista;
	}

	public void setNroLista(int nroLista) {
		this.nroLista = nroLista;
	}

	public List<Reintegro> getReintegros() {
		return reintegros;
	}

	public void setReintegros(List<Reintegro> reintegros) {
		this.reintegros = reintegros;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nroLista;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReintegroList other = (ReintegroList) obj;
		if (nroLista != other.nroLista)
			return false;
		return true;
	}

	public BigDecimal importeTotal() {
		BigDecimal total = new BigDecimal("0");
		if (reintegros != null){
			for (Reintegro r : reintegros){
				total = total.add(r.importeTotal());
			}
		}
		return total;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append(nroLista);
		if(tipo!=null){
			sb.append("|").append(tipo);
		}		
		return String.valueOf(sb.toString());
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getCuilCuenta() {
		return cuilCuenta;
	}

	public void setCuilCuenta(String cuilCuenta) {
		this.cuilCuenta = cuilCuenta;
	}
}
