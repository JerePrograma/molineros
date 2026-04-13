package ar.com.ospim.afiliados.reportes;


public class ReportePosiblesInconsistenciasResult {

	private String cuil_titular;
	private int inte;
	private String tercerizadora;
	private String observaciones;
	
	public ReportePosiblesInconsistenciasResult(String cuil_titular, int inte, String tercerizadora, String observaciones){
		this.cuil_titular=cuil_titular;
		this.inte=inte;
		this.tercerizadora=tercerizadora;
		this.observaciones=observaciones;
	}
	
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getTercerizadora() {
		return tercerizadora;
	}
	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
}
