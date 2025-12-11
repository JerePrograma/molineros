package ar.com.ospim.tesoreria.beans;

public class MovimientoBancoReciboIngreso extends MovimientoBancoItem {
	private ReciboIngreso reciboIngreso;

	public MovimientoBancoReciboIngreso() {
	}

	public MovimientoBancoReciboIngreso(int id) {
		this.setId(id);
	}

	public void setReciboIngreso(ReciboIngreso reciboIngreso) {
		this.reciboIngreso = reciboIngreso;
	}

	public ReciboIngreso getReciboIngreso() {
		return reciboIngreso;
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
		MovimientoBancoReciboIngreso other = (MovimientoBancoReciboIngreso) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

}