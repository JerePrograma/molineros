package ar.com.ospim.autorizaciones.services;

import java.util.List;

import ar.com.ospim.autorizaciones.beans.OrdenPagoConError;
import ar.com.ospim.autorizaciones.beans.PagosInterbanking;

public class OrdenesPagoInterbanking {
        
	private List<PagosInterbanking> listaPagos;
	private List<OrdenPagoConError> odenConError;
	
	
	
	public List<OrdenPagoConError> getOdenConError() {
		return odenConError;
	}
	
	public void setOdenConError(List<OrdenPagoConError> odenConError) {
		this.odenConError = odenConError;
	}

	public List<PagosInterbanking> getListaPagos() {
		return listaPagos;
	}

	public void setListaPagos(List<PagosInterbanking> listaPagos) {
		this.listaPagos = listaPagos;
	}
	
	
	
	
}
