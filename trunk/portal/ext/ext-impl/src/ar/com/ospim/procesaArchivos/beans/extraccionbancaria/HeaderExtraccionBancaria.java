package ar.com.ospim.procesaArchivos.beans.extraccionbancaria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderExtraccionBancaria {
	private Date fecha;
	private String tipo;
	private String codigoOS;
	
	public HeaderExtraccionBancaria(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fecha = sdf.parse(line.substring(10, 20));// 10
		tipo = line.substring(7,10);
		codigoOS = line.substring(20,24);
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setCodigoOS(String codigoOS) {
		this.codigoOS = codigoOS;
	}

	public String getCodigoOS() {
		return codigoOS;
	}

}
