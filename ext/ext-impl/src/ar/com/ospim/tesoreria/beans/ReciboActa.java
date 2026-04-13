package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.util.Date;

public class ReciboActa extends ReciboConcepto {
	private Acta acta;
	private BigDecimal importeAdicional;
	private BigDecimal importePorCheques;

	public ReciboActa() {
	}

	public ReciboActa(Acta acta, BigDecimal importePorCheques) {
		this.acta = acta;
		this.importePorCheques = importePorCheques;
	}

	public ReciboActa(Acta acta) {
		this.acta = acta;
	}

	public Acta getActa() {
		return acta;
	}

	public void setActa(Acta acta) {
		this.acta = acta;
	}

	public BigDecimal getImporteAdicional() {
		return importeAdicional;
	}

	public void setImporteAdicional(BigDecimal importeAdicional) {
		this.importeAdicional = importeAdicional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acta == null) ? 0 : acta.hashCode());
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
		ReciboActa other = (ReciboActa) obj;
		if (acta == null) {
			if (other.acta != null)
				return false;
		} else if (!acta.equals(other.acta))
			return false;
		return true;
	}

	@Override
	public BigDecimal getTotalAPagar() {
		BigDecimal total = BigDecimal.ZERO;
		/*if (acta != null) {
			total = total.add(acta.getTotalActaPagosChequeNoIngresados());
		}*/
		
		total = total.add(importeAdicional).add(importePorCheques);
		
		return total;
	}
	
	public BigDecimal getTotalAPagarNoOS() {
		BigDecimal total = BigDecimal.ZERO;
		if (acta != null) {
			total = total.add(acta.getTotalActaPagosIngresados());
		}
		if (importeAdicional != null) {
			total = total.add(importeAdicional);
		}
		return total;
	}

	@Override
	public Date getFechaAPagar() {
		return acta.getFechaPago();
	}

	public void setImportePorCheques(BigDecimal importePorCheques) {
		this.importePorCheques = importePorCheques;
	}

	public BigDecimal getImportePorCheques() {
		return importePorCheques;
	}

	@Override
	public String getDescripcion() {
		return "Acta " + acta.getNumero();
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