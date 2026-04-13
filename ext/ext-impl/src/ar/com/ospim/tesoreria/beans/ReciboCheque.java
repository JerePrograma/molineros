package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.util.Date;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;

public class ReciboCheque extends ReciboConcepto {
	public enum Tipo {
		NO_DEPOSITADO("Canje Ch. no dep."), RECHAZADO("Canje Ch. rech.");

		private String desc;

		Tipo(String desc) {
			this.desc = desc;
		}

		public String getDescripcion() {
			return desc;
		}
	};

	private Tipo tipo;
	private Cheque chequeASustituir;

	public ReciboCheque() {
	}

	public ReciboCheque(Cheque chASustiuir) {
		this.chequeASustituir = chASustiuir;
	}

	// public ReciboCheque(Cheque chqASustituir, Cheque chqNuevo, Date
	// fechaPago,
	// BigDecimal importe) {
	// this.chequeASustituir = chqASustituir;
	// this.cheque = chqNuevo;
	// this.fechaPago = fechaPago;
	// this.importe = importe;
	// }

	public void setConvenio(Convenio convenio) {
	}

	public void setChequeASustituir(Cheque chequeASustituir) {
		this.chequeASustituir = chequeASustituir;
	}

	public Cheque getChequeASustituir() {
		return chequeASustituir;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((chequeASustituir == null) ? 0 : chequeASustituir.hashCode());
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
		ReciboCheque other = (ReciboCheque) obj;
		if (chequeASustituir == null) {
			if (other.chequeASustituir != null)
				return false;
		} else if (!chequeASustituir.equals(other.chequeASustituir))
			return false;
		return true;
	}

	@Override
	public BigDecimal getTotalAPagar() {
		BigDecimal total = BigDecimal.ZERO;
		if (chequeASustituir != null) {
			total = chequeASustituir.getImporte();
		}

		return total;
	}

	@Override
	public Date getFechaAPagar() {
		return chequeASustituir.getFecha();
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Tipo getTipo() {
		return tipo;
	}

	@Override
	public String getDescripcion() {
		return tipo.getDescripcion() + " " + chequeASustituir.getNumeroStr();
	}

	@Override
	public BigDecimal getImporte() {
		return chequeASustituir.getImporte();
	}

	@Override
	public BigDecimal getTotalAPagarNoOS() {
		
		BigDecimal total = BigDecimal.ZERO;
		if (chequeASustituir != null) {
			total = chequeASustituir.getImporte();
		}

		return total;
	}
}