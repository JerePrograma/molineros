package ar.com.ospim.procesaArchivos.beans;

import java.io.Serializable;
/**
 * 
 * @author sergio
 * Valido para los formatos de los archivos de subsidios SUMA
 */
public class ArchivoSubsidioMitigacionAsimetricasSuma implements Serializable { //SUMA 
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4733088198387886290L;
	HeaderSumaXxxx header;
	DetalleSuma detalle;
	FooterSumaXxxx footer;
	
	public HeaderSumaXxxx getHeader() {
		return header;
	}
	public void setHeader(HeaderSumaXxxx header) {
		this.header = header;
	}
	public DetalleSuma getDetalle() {
		return detalle;
	}
	public void setDetalle(DetalleSuma detalle) {
		this.detalle = detalle;
	}
	public FooterSumaXxxx getFooter() {
		return footer;
	}
	public void setFooter(FooterSumaXxxx footer) {
		this.footer = footer;
	}

	
}
