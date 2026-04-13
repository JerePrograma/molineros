package ar.com.ospim.procesaArchivos.beans.extraccionbancaria;

import java.math.BigDecimal;

public class DetalleExtraccionBancaria {
	private String codigoMovimiento;
	private String debitoCredito;
	private String codigoOS;
	private BigDecimal importe;
	private BigDecimal importeRechazado;

	public DetalleExtraccionBancaria(String line) {
		debitoCredito = line.substring(0, 1).trim();// 1
		codigoMovimiento = line.substring(1, 3).trim();// 2
		codigoOS = line.substring(3, 7).trim();// 4
		importe = new BigDecimal(line.substring(7, 20).trim() + '.'
				+ line.substring(20, 22).trim());// 13 y 2
		if (debitoCredito.trim().equals("D")){
			importe = importe.negate();	
		}
		if (line.length() > 37) {
			importeRechazado = new BigDecimal(line.substring(22, 35).trim()
					+ '.' + line.substring(35, 37).trim());
		}
	}

	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}

	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public String getCodigoOS() {
		return codigoOS;
	}

	public void setCodigoOS(String codigoOS) {
		this.codigoOS = codigoOS;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public void setImporteRechazado(BigDecimal importeRechazado) {
		this.importeRechazado = importeRechazado;
	}

	public BigDecimal getImporteRechazado() {
		return importeRechazado;
	}

}
