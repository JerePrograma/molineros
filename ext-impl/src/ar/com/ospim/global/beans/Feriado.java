package ar.com.ospim.global.beans;

import java.util.Date;

public class Feriado {

	private Date fecha;

	public Feriado() {

	}

	public Feriado(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
