package ar.com.ospim.afiliados.reportes;

public class ReporteCantBonosSeccional {
	private String seccional;
	private int total_beneficiarios;
	private int cant_bonos1;
	private int cant_bonos2;
	private int cant_bonos3;
	
	public ReporteCantBonosSeccional(String seccional, int total_benef, int cant1, int cant2, int cant3){
		this.seccional=seccional;
		this.total_beneficiarios=total_benef;
		this.cant_bonos1=cant1;
		this.cant_bonos2=cant2;
		this.cant_bonos3=cant3;		
	}
	
	public String getSeccional() {
		return seccional;
	}
	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}
	public int getTotal_beneficiarios() {
		return total_beneficiarios;
	}
	public void setTotal_beneficiarios(int totalBeneficiarios) {
		total_beneficiarios = totalBeneficiarios;
	}
	public int getCant_bonos1() {
		return cant_bonos1;
	}
	public void setCant_bonos1(int cantBonos1) {
		cant_bonos1 = cantBonos1;
	}
	public int getCant_bonos2() {
		return cant_bonos2;
	}
	public void setCant_bonos2(int cantBonos2) {
		cant_bonos2 = cantBonos2;
	}
	public int getCant_bonos3() {
		return cant_bonos3;
	}
	public void setCant_bonos3(int cantBonos3) {
		cant_bonos3 = cantBonos3;
	}
	
}
