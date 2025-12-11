package ar.com.ospim.procesaArchivos.beans.extraccionbancaria;

import java.util.List;

public class ArchivoExtraccionBancaria {

	private List<DetalleExtraccionBancaria> detalleList;
	private HeaderExtraccionBancaria headerExtraccionBancaria;

	public List<DetalleExtraccionBancaria> getDetalleList() {
		return detalleList;
	}

	public void setDetalleList(List<DetalleExtraccionBancaria> detalleList) {
		this.detalleList = detalleList;
	}

	public HeaderExtraccionBancaria getHeaderExtraccionBancaria() {
		return headerExtraccionBancaria;
	}

	public void setHeaderExtraccionBancaria(
			HeaderExtraccionBancaria headerExtraccionBancaria) {
		this.headerExtraccionBancaria = headerExtraccionBancaria;
	}

}
