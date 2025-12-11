package ar.com.uoma.beans;

import java.io.Serializable;

public class EscalaSueldosBasicos  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String antiguedad;
	private String porcentaje;
	private String catA;
	private String catB;
	private String catC;
	private String catD;
	private String catE;
	
	
	
	public String getAntiguedad() {
		return antiguedad;
	}
	public void setAntiguedad(String antiguedad) {
		this.antiguedad = antiguedad;
	}

	public String getCatA() {
		return catA;
	}
	public void setCatA(String catA) {
		this.catA = catA;
	}
	public String getCatB() {
		return catB;
	}
	public void setCatB(String catB) {
		this.catB = catB;
	}
	public String getCatC() {
		return catC;
	}
	public void setCatC(String catC) {
		this.catC = catC;
	}
	public String getCatD() {
		return catD;
	}
	public void setCatD(String catD) {
		this.catD = catD;
	}
	public String getCatE() {
		return catE;
	}
	public void setCatE(String catE) {
		this.catE = catE;
	}
	public String getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(String porcentaje) {
		this.porcentaje = porcentaje;
	}
	
	
	
	
}
