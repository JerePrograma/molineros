package ar.com.ospim.procesaArchivos.beans.dj;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FooterDJ {
	private String tipoRegistro;
	private String codigoRegistro;
	private String indicadorDeProceso;
	private Date fechaProceso;
	private BigInteger cantEmpleadosPOrig;
	private BigInteger cantEmpleadosPRect;
	private String cantRegistros;

	public FooterDJ(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tipoRegistro = line.substring(0, 2).trim(); // 2
		codigoRegistro = line.substring(2, 10).trim(); // 8
		indicadorDeProceso = line.substring(10, 23).trim(); // 13
		fechaProceso = sdf.parse(line.substring(23, 33)); // 10
		cantEmpleadosPOrig = new BigInteger(line.substring(43, 53)); // 10
		cantEmpleadosPRect = new BigInteger(line.substring(63, 73)); // 10
		cantRegistros = line.substring(83, 93).trim(); // 10
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getCodigoRegistro() {
		return codigoRegistro;
	}

	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}

	public String getIndicadorDeProceso() {
		return indicadorDeProceso;
	}

	public void setIndicadorDeProceso(String indicadorDeProceso) {
		this.indicadorDeProceso = indicadorDeProceso;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public BigInteger getCantEmpleadosPOrig() {
		return cantEmpleadosPOrig;
	}

	public void setCantEmpleadosPOrig(BigInteger cantEmpleadosPOrig) {
		this.cantEmpleadosPOrig = cantEmpleadosPOrig;
	}

	public BigInteger getCantEmpleadosPRect() {
		return cantEmpleadosPRect;
	}

	public void setCantEmpleadosPRect(BigInteger cantEmpleadosPRect) {
		this.cantEmpleadosPRect = cantEmpleadosPRect;
	}

	public String getCantRegistros() {
		return cantRegistros;
	}

	public void setCantRegistros(String cantRegistros) {
		this.cantRegistros = cantRegistros;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\ntipoRegistro " + tipoRegistro);
		str.append("\ncodigoRegistro " + codigoRegistro);
		str.append("\nindicadorDeProceso " + indicadorDeProceso);
		str.append("\nfechaProceso " + fechaProceso);
		str.append("\ncantEmpleadosPOrig " + cantEmpleadosPOrig);
		str.append("\ncantEmpleadosPRect " + cantEmpleadosPRect);
		str.append("\ncantRegistros " + cantRegistros);
		return str.toString();
	}

}
