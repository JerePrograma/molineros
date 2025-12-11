package ar.com.ospim.login.coordenadas.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TarjetaCoordenadas implements Serializable  {
	private static final long serialVersionUID = 6066716295032834762L;
	public static final long CANT_COORDENADAS = 8;
	public static final String SEPARADOR_FILAS = "!";
	public static final String SEPARADOR_COLUMNAS = "-";
	// Es un array de numeros, cada fila separada por un signo de exclamacion
	// ("!") y cada columna separada por un guion ("-")
	private String coordenadas;
	private int serialTarjetaCoordenadas;
	private Date fechaVencimiento;
	private String ipSinCoord;
	
	public TarjetaCoordenadas() {
	}

	public TarjetaCoordenadas(String coordenadas, int serial, String ipSinCoord) {
		this.coordenadas = coordenadas;
		this.serialTarjetaCoordenadas = serial;
		this.ipSinCoord=ipSinCoord;
	}

	public String getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}

	public int getSerialTarjetaCoordenadas() {
		return serialTarjetaCoordenadas;
	}

	public void setSerialTarjetaCoordenadas(int serialTarjetaCoordenadas) {
		this.serialTarjetaCoordenadas = serialTarjetaCoordenadas;
	}

	public boolean validar(int coordenadaX, int coordenadaY,
			String valorCoordenada) {
		coordenadaY--;
		if (coordenadaX >= CANT_COORDENADAS || coordenadaY >= CANT_COORDENADAS) {
			return false;
		}
		String[] filasCoordenadas = getCoordenadas().split(SEPARADOR_FILAS);		
		String valorRealCoordenada1 = filasCoordenadas[coordenadaY]
				.split(SEPARADOR_COLUMNAS)[coordenadaX];

		return valorRealCoordenada1.equals(valorCoordenada);
	}

	
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public String getFechaVencimientoAsString() {
		if (fechaVencimiento == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(fechaVencimiento);
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public void setFechaVencimientoAsString(String fecha) throws ParseException {
		if (fecha != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			fechaVencimiento = format.parse(fecha);
		}
	}

	public String getIpSinCoord() {
		return ipSinCoord;
	}

	public void setIpSinCoord(String ipSinCoord) {
		this.ipSinCoord = ipSinCoord;
	}
	
	
}
