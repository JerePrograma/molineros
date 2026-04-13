package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class Anticipo implements Pago {
	private Comprobante anticipo;
	private int opOrigen;
	private Date fechaOPOrigen;
	private int cantCuotas;
	private int nroCuota;
	private BigDecimal importeOriginal;

	public Anticipo(Comprobante anticipo) {
		this.anticipo = anticipo;
	}

	public Anticipo() {

	}

	public CuentaBancaria getCuentaBancaria() {
		return null;
	}

	public BigDecimal getImporte() {
		return anticipo.getImporteComprobante();
	}
	
	public void setImporte(BigDecimal importe) {
		anticipo.setImporteComprobante(importe);
	}

	public String getNumeroStr() {
		return anticipo.getNroComprobante();
	}

	public void setAnticipo(Comprobante anticipo) {
		this.anticipo = anticipo;
	}

	public Comprobante getAnticipo() {
		return anticipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((anticipo == null) ? 0 : anticipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anticipo other = (Anticipo) obj;
		if (anticipo == null) {
			if (other.anticipo != null)
				return false;
		} else if (!anticipo.getNroComprobante().equals(
				other.anticipo.getNroComprobante())) {
			return false;
		} else if (!anticipo.getTipoComprobante().equals(
				other.anticipo.getTipoComprobante())) {
			return false;
		}
		return true;
	}

	public String getDescripcion() {
		if (anticipo.getSeccional() != null
				&& anticipo.getSeccional().getId() != 0) {
			return "Cuit: " + anticipo.getCuit() + " - Suc.: "
					+ anticipo.getSeccional().getId();
		} else {
			return "Cuit: " + anticipo.getCuit() + " - Suc.: "
					+ anticipo.getSucuComprobante();
		}
	}

	public String getANombreDe() {
		return "";
	}

	public void savePago(OrdenPagoOspim op, String screenName, Connection con, int entidad)
			throws Exception {
		OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
	}

	public Date getBaja_fecha() {
		return null;
	}

	public String getTipo() {
		return this.getClass().getSimpleName();
	}

	public String getIdTipo() {
		return "";
	}

	public void savePago(OrdenPago op, String screenName, Connection con,
			int entidad) throws Exception {
		OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
	}

	public int getOpOrigen() {
		return opOrigen;
	}

	public void setOpOrigen(int opOrigen) {
		this.opOrigen = opOrigen;
	}

	public Date getFechaOPOrigen() {
		return fechaOPOrigen;
	}

	public String getFechaOPOrigenAsString() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (fechaOPOrigen == null) {
			return "";
		}
		return format.format(fechaOPOrigen);
	}

	public void setFechaOPOrigen(Date fechaOPOrigen) {
		this.fechaOPOrigen = fechaOPOrigen;
	}

	public int getCantCuotas() {
		return cantCuotas;
	}

	public void setCantCuotas(int cantCuotas) {
		this.cantCuotas = cantCuotas;
	}

	public BigDecimal getImporteOriginal() {
		return importeOriginal;
	}

	public void setImporteOriginal(BigDecimal importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	public int getNroCuota() {
		return nroCuota;
	}

	public void setNroCuota(int nroCuota) {
		this.nroCuota = nroCuota;
	}
	
	public BigDecimal getImporteAnticipoBalanceador(){
		if (this.getImporteOriginal()!=null) {
		   return this.getImporteOriginal().abs().subtract(this.getImporte().abs());
		}else{
			return BigDecimal.ZERO;
		}
	}

	@Override
	public PagoBancario getPagoBancario() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
