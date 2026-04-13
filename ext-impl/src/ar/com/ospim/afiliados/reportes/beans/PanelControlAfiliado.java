package ar.com.ospim.afiliados.reportes.beans;

import java.math.BigDecimal;
import java.util.Date;


public class PanelControlAfiliado {

	private Date periodo;
	private String descripcion;
	private int titulares;
	private int adherentes;
	private BigDecimal promedio;
	
	public PanelControlAfiliado(){		
	}
	
	public PanelControlAfiliado(Date periodo, String descripcion, int titulares, int adherentes){
		this.periodo=periodo;
		this.descripcion=descripcion;
		this.titulares=titulares;
		this.adherentes=adherentes;
	}
	
	public PanelControlAfiliado(Date periodo, int titulares, BigDecimal promedio){
		this.periodo=periodo;		
		this.titulares=titulares;
		this.promedio=promedio;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getTitulares() {
		return titulares;
	}

	public void setTitulares(int titulares) {
		this.titulares = titulares;
	}

	public int getAdherentes() {
		return adherentes;
	}

	public void setAdherentes(int adherentes) {
		this.adherentes = adherentes;
	}

	public BigDecimal getPromedio() {
		return promedio;
	}

	public void setPromedio(BigDecimal promedio) {
		this.promedio = promedio;
	}
	
	
		
}
