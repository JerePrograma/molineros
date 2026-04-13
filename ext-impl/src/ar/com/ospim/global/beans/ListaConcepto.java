package ar.com.ospim.global.beans;

import java.util.List;

public class ListaConcepto {
	List<Concepto> conceptos; 
	private int totalConceptos;

	public ListaConcepto() {
	}

	
	
	public ListaConcepto(List<Concepto> conceptos) {
		this.conceptos = conceptos;		
	}



	public List<Concepto> getConceptos() {
		return conceptos;
	}



	public void setConceptos(List<Concepto> conceptos) {
		this.conceptos = conceptos;
	}



	public int getTotalConceptos() {
		return totalConceptos;
	}



	public void setTotalConceptos(int totalConceptos) {
		this.totalConceptos = totalConceptos;
	}

		

}
