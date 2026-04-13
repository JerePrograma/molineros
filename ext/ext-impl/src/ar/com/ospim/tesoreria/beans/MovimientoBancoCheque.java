package ar.com.ospim.tesoreria.beans;

import ar.com.ospim.global.beans.Cheque;

public class MovimientoBancoCheque extends MovimientoBancoItem {

	private Cheque cheque;
	private Cheque.Estado estadoViejo;
	private Cheque.Estado estadoNuevo;

	public MovimientoBancoCheque() {

	}

	public MovimientoBancoCheque(int id) {
		this.setId(id);
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setEstadoViejo(Cheque.Estado estadoViejo) {
		this.estadoViejo = estadoViejo;
	}

	public Cheque.Estado getEstadoViejo() {
		return estadoViejo;
	}

	public void setEstadoNuevo(Cheque.Estado estadoNuevo) {
		this.estadoNuevo = estadoNuevo;
	}

	public Cheque.Estado getEstadoNuevo() {
		return estadoNuevo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId();
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
		MovimientoBancoCheque other = (MovimientoBancoCheque) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

}