package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.util.Date;

import ar.com.ospim.tesoreria.beans.convenio.Convenio;

public class ReciboConvenio extends ReciboConcepto {
	private Convenio convenio;
	private BigDecimal importeAdicional;
	private BigDecimal importePorCheques;

	public ReciboConvenio() {
	}

	public ReciboConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	public ReciboConvenio(Convenio convenio, BigDecimal importePorCheques) {
		this.convenio = convenio;
		this.importePorCheques = importePorCheques;
	}

	public BigDecimal getImporteAdicional() {
		return importeAdicional;
	}

	public void setImporteAdicional(BigDecimal importeAdicional) {
		this.importeAdicional = importeAdicional;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((convenio == null) ? 0 : convenio.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReciboConvenio other = (ReciboConvenio) obj;
		if (convenio == null) {
			if (other.convenio != null)
				return false;
		} else if (!convenio.equals(other.convenio))
			return false;
		return true;
	}

	@Override
	public BigDecimal getTotalAPagar() {
		BigDecimal total = BigDecimal.ZERO;
		if (convenio != null) {
			total = total.add(convenio
					.getTotalConvenioPagosChequeNoIngresados());
		}
		if (importeAdicional != null) {
			total = total.add(importeAdicional);
		}
		return total;
	}
	
	public BigDecimal getTotalAPagarNoOS() {
		BigDecimal total = BigDecimal.ZERO;
		if (convenio != null) {
			total = total.add(convenio
					.getTotalConvenioPagosIngresados());
		}
		if (importeAdicional != null) {
			total = total.add(importeAdicional);
		}
		return total;
	}

	public void setImportePorCheques(BigDecimal importePorCheques) {
		this.importePorCheques = importePorCheques;
	}

	public BigDecimal getImportePorCheques() {
		return importePorCheques;
	}

	@Override
	public Date getFechaAPagar() {
		return convenio.getFechaInicio();
	}

	@Override
	public String getDescripcion() {
		return "Convenio " + convenio.getNumero();
	}

	@Override
	public BigDecimal getImporte() {
		BigDecimal total = BigDecimal.ZERO;
		if (importeAdicional != null) {
			total = total.add(importeAdicional);
		}
		if (importePorCheques != null) {
			total = total.add(importePorCheques);
		}
		return total;
	}
}