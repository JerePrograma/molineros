package ar.com.ospim.farmacia.beans;

import java.math.BigDecimal;
import java.util.List;
import ar.com.ospim.global.beans.Seccional;

public class ReintegroFarmaciaList {
	private int nroLista;
	
	private List<ReintegroMedicamento> reintegros;

	private Seccional seccional;

	public ReintegroFarmaciaList() {
	}
	
	public ReintegroFarmaciaList(int nroLista) {
		this.nroLista = nroLista;
	}

	public int getNroLista() {
		return nroLista;
	}

	public void setNroLista(int nroLista) {
		this.nroLista = nroLista;
	}

	public List<ReintegroMedicamento> getReintegros() {
		return reintegros;
	}

	public void setReintegros(List<ReintegroMedicamento> reintegros) {
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
		ReintegroFarmaciaList other = (ReintegroFarmaciaList) obj;
		if (nroLista != other.nroLista)
			return false;
		return true;
	}

	public BigDecimal importeTotal() {
		BigDecimal total = new BigDecimal("0");
		if (reintegros != null){
			for (ReintegroMedicamento r : reintegros){
				total = total.add(r.getImporteTotal());
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
		return String.valueOf(nroLista);
	}
}