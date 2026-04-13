package ar.com.ospim.global.beans;

import java.util.Date;
import java.util.List;

public interface ItemSubdiarioEgreso {
	Date getFecha();

	Date getBaja_fecha();

	String getNumeroOP();

	String getCuit();

	String getRazonSocial();
	
	int getId_seccional();

	List<? extends SubdiarioComprobante> getComprobantesSubdiario();
	//ESTE ES EL DEBE
	List<? extends SubdiarioEgresoColumna> getDesde();

	/**
	 * Utiliza la fecha de pago para devolver la cuenta de activo, o la cuenta
	 * de pasivo en caso de que el pago se haya realizado en un mes posterior
	 * 
	 * @param fechaPago
	 * @return
	 */
	//ESTE ES EL HABER
	List<? extends SubdiarioEgresoColumna> getHacia();
	
	boolean isMostrarEnCuadro();
	boolean isMostrarComprobantesEnSubdiario();
	String getObservaciones();
}
