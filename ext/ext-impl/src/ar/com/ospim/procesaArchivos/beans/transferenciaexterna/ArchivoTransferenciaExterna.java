package ar.com.ospim.procesaArchivos.beans.transferenciaexterna;

import java.util.List;

public class ArchivoTransferenciaExterna {
	private HeaderTransferenciaExterna header;
	private List<DetalleTransferenciaExterna> detalleList;
	private FooterTransferenciaExterna footer;

	public void setHeader(HeaderTransferenciaExterna header) {
		this.header = header;
	}

	public HeaderTransferenciaExterna getHeader() {
		return header;
	}

	public void setDetalleList(List<DetalleTransferenciaExterna> detalleList) {
		this.detalleList = detalleList;
	}

	public List<DetalleTransferenciaExterna> getDetalleList() {
		return detalleList;
	}

	public void setFooter(FooterTransferenciaExterna footer) {
		this.footer = footer;
	}

	public FooterTransferenciaExterna getFooter() {
		return footer;
	}

}
