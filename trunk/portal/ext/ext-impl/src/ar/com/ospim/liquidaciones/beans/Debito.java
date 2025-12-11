package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;

import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.global.beans.Motivo;

public class Debito implements Serializable{

	private static final long serialVersionUID = 1L;
	private ComprobanteItem comproItem;
	private Motivo motivoDebito;
	/**
	 * @return the comproItem
	 */
	public ComprobanteItem getComproItem() {
		return comproItem;
	}
	/**
	 * @param comproItem the comproItem to set
	 */
	public void setComproItem(ComprobanteItem comproItem) {
		this.comproItem = comproItem;
	}
	/**
	 * @return the motivoDebito
	 */
	public Motivo getMotivoDebito() {
		return motivoDebito;
	}
	/**
	 * @param motivoDebito the motivoDebito to set
	 */
	public void setMotivoDebito(Motivo motivoDebito) {
		this.motivoDebito = motivoDebito;
	}
	
}