package ar.com.ospim.procesaArchivos.beans.dj;

import java.util.List;

public class ArchivoDJ {

	private HeaderDJ header;
	private List<DetalleDJ> detalle;
	private FooterDJ footer;

	public HeaderDJ getHeader() {
		return header;
	}

	public void setHeader(HeaderDJ header) {
		this.header = header;
	}

	public List<DetalleDJ> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<DetalleDJ> detalle) {
		this.detalle = detalle;
	}

	public FooterDJ getFooter() {
		return footer;
	}

	public void setFooter(FooterDJ footer) {
		this.footer = footer;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		if (header != null) {
			str.append(header);
			str.append("\n");
		}
		if (detalle != null) {
			// for (DetalleDJ det : detalle) {
			// System.out.println(det);
			str.append("\n");
			// }
		}
		if (footer != null) {
			str.append(footer);
		}
		return str.toString();
	}

}
