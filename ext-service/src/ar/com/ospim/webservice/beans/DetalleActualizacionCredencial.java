package ar.com.ospim.webservice.beans;

import java.io.Serializable;

import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

public class DetalleActualizacionCredencial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2006671764237083817L;
	
	private AfiliacionPrevencionDTO afiliacion;

	public AfiliacionPrevencionDTO getAfiliacion() {
		return afiliacion;
	}

	public void setAfiliacion(AfiliacionPrevencionDTO afiliacion) {
		this.afiliacion = afiliacion;
	} 
	
}
