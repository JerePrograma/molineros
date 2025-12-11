package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;

import ar.com.ospim.afiliados.beans.Afiliado;

/**
 * @author crivas
 * @version 1.0
 * @created 26-Nov-2010 02:25:46 p.m.
 */
public class ReporteOrdenPagoReintegros{

	private Afiliado afiliado;
	private Reintegro reintegro;
	private BigDecimal total_afiliado;
	
	/**
	 * @return the id_reintegro
	 */	
	public ReporteOrdenPagoReintegros() {
	}
    
	public ReporteOrdenPagoReintegros(BigDecimal total_afiliado) {
		this.total_afiliado = total_afiliado;
	}

	/**
	 * @return the total_afiliado
	 */
	public BigDecimal getTotal_afiliado() {
		return total_afiliado;
	}

	/**
	 * @param totalAfiliado the total_afiliado to set
	 */
	public void setTotal_afiliado(BigDecimal totalAfiliado) {
		total_afiliado = totalAfiliado;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return reintegro.getImporteTotal();
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setReintegro(Reintegro reintegro) {
		this.reintegro = reintegro;
	}

	public Reintegro getReintegro() {
		return reintegro;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((reintegro == null) ? 0 : reintegro.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReporteOrdenPagoReintegros other = (ReporteOrdenPagoReintegros) obj;
		if (reintegro == null) {
			if (other.reintegro != null)
				return false;
		} else if (!reintegro.equals(other.reintegro))
			return false;
		return true;
	}

	
	
}