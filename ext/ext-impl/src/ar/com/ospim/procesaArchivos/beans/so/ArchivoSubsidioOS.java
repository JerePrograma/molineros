package ar.com.ospim.procesaArchivos.beans.so;

import java.util.List;

public class ArchivoSubsidioOS {

	private HeaderSubsidioOS header;
	private List<DetalleSubsidioOS> detalle;
	private FooterSubsidioOS footer;

	public HeaderSubsidioOS getHeader() {
		return header;
	}

	public void setHeader(HeaderSubsidioOS header) {
		this.header = header;
	}

	public List<DetalleSubsidioOS> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<DetalleSubsidioOS> detalle) {
		this.detalle = detalle;
	}

	public FooterSubsidioOS getFooter() {
		return footer;
	}

	public void setFooter(FooterSubsidioOS footer) {
		this.footer = footer;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		if (header != null) {
			str.append(header);
			str.append("\n");
		}
		if (detalle != null) {
			// for (DetalleSubsidioOS det : detalle) {
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
