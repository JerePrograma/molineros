package ar.com.ospim.tesoreria.beans;

public abstract class MovimientoBancoItem {
	private transient boolean isBorradoLogico = false;
	private int id;

	public void setBorradoLogico(boolean isBorradoLogico) {
		this.isBorradoLogico = isBorradoLogico;
	}

	public boolean isBorradoLogico() {
		return isBorradoLogico;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
