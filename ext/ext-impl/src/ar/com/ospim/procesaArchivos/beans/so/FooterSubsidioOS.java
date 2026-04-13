package ar.com.ospim.procesaArchivos.beans.so;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;

public class FooterSubsidioOS {
	private String tipoRegistro;
	private String identificador;
	private String codigoOS;
	private BigInteger cantidadRegistrosDE;
	private BigInteger cantidadRegistrosTO;
	private BigInteger cantidadRegistrosDT;
	private BigInteger cantidadRegistros;
	private BigDecimal importeSubsidio;
	private String debitoCredito;
	private BigDecimal importeSubsidioReal;

	public FooterSubsidioOS(String line) throws ParseException {
		tipoRegistro = line.substring(0, 2);
		identificador = line.substring(15, 24);
		codigoOS = line.substring(24, 30);
		cantidadRegistrosDE = new BigInteger(line.substring(30, 40));
		cantidadRegistrosTO = new BigInteger(line.substring(40, 50));
		cantidadRegistrosDT = new BigInteger(line.substring(50, 60));
		cantidadRegistros = new BigInteger(line.substring(60, 70));
		importeSubsidio = new BigDecimal(line.substring(70, 80) + "."
				+ line.substring(80, 82));
		debitoCredito = line.substring(89, 90);
		if (debitoCredito != null
				&& debitoCredito.trim().toUpperCase().equals("D")) {
			importeSubsidio = importeSubsidio.negate();
		}
		if (line.length() > 102) {
			String subsParte1 = line.substring(90, 100);
			String subsParte2 = line.substring(100, 102);
			if (subsParte1 == null || subsParte1.trim().equals("")) {
				subsParte1 = "0";
			}
			if (subsParte2 == null || subsParte2.trim().equals("")) {
				subsParte2 = "0";
			}
			importeSubsidioReal = new BigDecimal(subsParte1 + "." + subsParte2);
		}
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getCodigoOS() {
		return codigoOS;
	}

	public void setCodigoOS(String codigoOS) {
		this.codigoOS = codigoOS;
	}

	public BigInteger getCantidadRegistrosDE() {
		return cantidadRegistrosDE;
	}

	public void setCantidadRegistrosDE(BigInteger cantidadRegistrosDE) {
		this.cantidadRegistrosDE = cantidadRegistrosDE;
	}

	public BigInteger getCantidadRegistrosTO() {
		return cantidadRegistrosTO;
	}

	public void setCantidadRegistrosTO(BigInteger cantidadRegistrosTO) {
		this.cantidadRegistrosTO = cantidadRegistrosTO;
	}

	public BigInteger getCantidadRegistrosDT() {
		return cantidadRegistrosDT;
	}

	public void setCantidadRegistrosDT(BigInteger cantidadRegistrosDT) {
		this.cantidadRegistrosDT = cantidadRegistrosDT;
	}

	public BigInteger getCantidadRegistros() {
		return cantidadRegistros;
	}

	public void setCantidadRegistros(BigInteger cantidadRegistros) {
		this.cantidadRegistros = cantidadRegistros;
	}

	public BigDecimal getImporteSubsidio() {
		return importeSubsidio;
	}

	public void setImporteSubsidio(BigDecimal importeSubsidio) {
		this.importeSubsidio = importeSubsidio;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public BigDecimal getImporteSubsidioReal() {
		return importeSubsidioReal;
	}

	public void setImporteSubsidioReal(BigDecimal importeSubsidioReal) {
		this.importeSubsidioReal = importeSubsidioReal;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\ntipoRegistro " + tipoRegistro);
		str.append("\nidentificador " + identificador);
		str.append("\ncodigoOS " + codigoOS);
		str.append("\ncantidadRegistrosDE " + cantidadRegistrosDE);
		str.append("\ncantidadRegistrosTO " + cantidadRegistrosTO);
		str.append("\ncantidadRegistrosDT " + cantidadRegistrosDT);
		str.append("\ncantidadRegistros " + cantidadRegistros);
		str.append("\nimporteSubsidio " + importeSubsidio);
		str.append("\ndebitoCredito " + debitoCredito);
		str.append("\nimporteSubsidioReal " + importeSubsidioReal);
		return str.toString();
	}

}
