package ar.com.ospim.procesaArchivos.beans;

import java.util.List;

public class ArchivoOSAportes {
	
	HeaderOSAportes header;
	List<DetalleOSAportes> detalle;
	FooterOSAportes footer;
	List<FooterNomOSAportes> footerNom;
	
	public HeaderOSAportes getHeader() {
		return header;
	}
	public void setHeader(HeaderOSAportes header) {
		this.header = header;
	}
	public List<DetalleOSAportes> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<DetalleOSAportes> detalle) {
		this.detalle = detalle;
	}
	public FooterOSAportes getFooter() {
		return footer;
	}
	public void setFooter(FooterOSAportes footer) {
		this.footer = footer;
	}
	public List<FooterNomOSAportes> getFooterNom() {
		return footerNom;
	}
	public void setFooterNom(List<FooterNomOSAportes> footerNom) {
		this.footerNom = footerNom;
	}
	
	
}
