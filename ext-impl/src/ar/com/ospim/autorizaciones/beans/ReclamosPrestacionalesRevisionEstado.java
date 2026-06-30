package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class ReclamosPrestacionalesRevisionEstado
		implements Serializable {

	private static final long serialVersionUID = 1L;

	public int id;
	public String descripcion;

	public ReclamosPrestacionalesRevisionEstado(
			int id,
			String descripcion) {

		this.id = id;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(
			String descripcion) {

		this.descripcion = descripcion;
	}
}