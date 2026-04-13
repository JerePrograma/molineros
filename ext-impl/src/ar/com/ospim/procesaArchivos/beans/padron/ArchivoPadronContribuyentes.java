package ar.com.ospim.procesaArchivos.beans.padron;

import java.util.List;

import ar.com.ospim.procesaArchivos.padron.FooterPadronContribuyentes;
import ar.com.ospim.procesaArchivos.padron.HeaderPadronContribuyentes;

public class ArchivoPadronContribuyentes {

	private HeaderPadronContribuyentes header;
	private FooterPadronContribuyentes footer;
	private List<DetallePadronContribuyentes> detalle;

	public HeaderPadronContribuyentes getHeader() {
		return header;
	}

	public void setHeader(HeaderPadronContribuyentes header) {
		this.header = header;
	}

	public FooterPadronContribuyentes getFooter() {
		return footer;
	}

	public void setFooter(FooterPadronContribuyentes footer) {
		this.footer = footer;
	}

	public List<DetallePadronContribuyentes> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<DetallePadronContribuyentes> detalleList) {
		this.detalle = detalleList;
	}
}
