package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:56 p.m.
 */
public class ReintegroPrestacionOdo extends ReintegroPrestacion{
	private String pieza;
	private String cara;
	private BigDecimal honorarios;
	private BigDecimal gastos;
	
	public ReintegroPrestacionOdo() {

	}

	public ReintegroPrestacionOdo(Reintegro reintegro, int idPrestacion,
			String cuit, String descripcion, BigDecimal importeTotal) {
		super(reintegro, idPrestacion, cuit, descripcion, importeTotal);
		setImporte(importeTotal);
	}

	/**
	 * @return the pieza
	 */
	public String getPieza() {
		return pieza;
	}

	/**
	 * @param pieza
	 *            the pieza to set
	 */
	public void setPieza(String pieza) {
		this.pieza = pieza;
	}

	/**
	 * @return the cara
	 */
	public String getCara() {
		return cara;
	}

	/**
	 * @param cara
	 *            the cara to set
	 */
	public void setCara(String cara) {
		this.cara = cara;
	}

	/**
	 * @return the honorarios
	 */
	public BigDecimal getHonorarios() {
		return honorarios;
	}

	/**
	 * @param honorarios
	 *            the honorarios to set
	 */
	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}

	/**
	 * @return the gastos
	 */
	public BigDecimal getGastos() {
		return gastos;
	}

	/**
	 * @param gastos
	 *            the gastos to set
	 */
	public void setGastos(BigDecimal gastos) {
		this.gastos = gastos;
	}

}