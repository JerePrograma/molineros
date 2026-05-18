package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;

/**
 * @author sistema-09
 * @version 1.0
 * @created 13-Sep-2010 04:29:37 p.m.
 */
public class LiquidacionPrestacionAjuste extends LiquidacionPrestacion {

	private String ajuste; //ADD, EDIT, DELETE	
	
	private String idTercerizadora;
	
	public LiquidacionPrestacionAjuste(){

	}

	public LiquidacionPrestacionAjuste(Liquidacion liquidacion, BigDecimal cantidad, BigDecimal importe){		
		this.liquidacion = liquidacion;
		this.cantidad = cantidad;
		this.importe = importe;
		this.importeTotal = importe != null ? importe.multiply(this.cantidad != null ? this.cantidad : new BigDecimal(0)) : new BigDecimal(0);
	}

	/**
	 * @return the ajuste
	 */
	public String getAjuste() {
		return ajuste;
	}

	/**
	 * @param ajuste the ajuste to set
	 */
	public void setAjuste(String ajuste) {
		this.ajuste = ajuste;
	}
	
	public String getIdTercerizadora() {
	    return idTercerizadora;
	}

	public void setIdTercerizadora(String idTercerizadora) {
	    this.idTercerizadora = idTercerizadora;
	}
}