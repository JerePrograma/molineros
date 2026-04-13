package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;

public interface SubdiarioComprobante {
	
	BigDecimal getImporte();

	boolean isDebitoParaEgreso();

	String getDescripcion();
	
	String getNroComprobante();
	
	Date getFechaEmision();

	List<ComprobanteConcepto> getConceptos();

	Date getFechaRecepcion();

	Date getPeriodoPrestacion();
	
	String getTipoMovDescripcion();
	
	int getIdTipoMov();
	
	String getTipoComprobante();
	
	String getObservaciones();
}
