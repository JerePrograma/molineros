package ar.com.ospim.procesaArchivos.beans.desempleo;

import java.util.List;

public class ArchivoDesempleo {
	private List<DetalleDesempleo> detalleDesempleo;

	public List<DetalleDesempleo> getDetalleDesempleo() {
		return detalleDesempleo;
	}

	public void setDetalleDesempleo(List<DetalleDesempleo>detalleDesemplo) {
		this.detalleDesempleo = detalleDesemplo;
	}
}