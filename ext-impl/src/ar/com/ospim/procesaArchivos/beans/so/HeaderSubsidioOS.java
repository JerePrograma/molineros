package ar.com.ospim.procesaArchivos.beans.so;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderSubsidioOS {
	private String tipoRegistro;
	private String identificador;
	private String codigoOS;
	private Date fechaProceso;

	public HeaderSubsidioOS(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tipoRegistro = line.substring(0, 2); // 2
		setIdentificador(line.substring(15, 24)); // 8
		setCodigoOS(line.substring(24, 30)); // 13
		fechaProceso = sdf.parse(line.substring(30, 40)); // 10
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setCodigoOS(String codigoOS) {
		this.codigoOS = codigoOS;
	}

	public String getCodigoOS() {
		return codigoOS;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\ntipoRegistro " + tipoRegistro);
		str.append("\nidentificador " + identificador);
		str.append("\ncodigoOS " + codigoOS);
		str.append("\nfechaProceso " + fechaProceso);
		return str.toString();
	}
}
