package ar.com.ospim.procesaArchivos.padron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FooterPadronContribuyentes {

	private String tipoRegistro;
	private String codigoRegistro;
	private String indicadorDeProceso;
	private Date fechaProceso;
	private String cantRegistros;

	public FooterPadronContribuyentes(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		tipoRegistro = line.substring(0, 2).trim(); // 2
		codigoRegistro = line.substring(2, 10).trim(); // 8
		indicadorDeProceso = line.substring(10, 23).trim(); // 13
		fechaProceso = sdf.parse(line.substring(23, 33)); // 10
		cantRegistros = line.substring(33, 45).trim(); // 10
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

	public String getCantRegistros() {
		return cantRegistros;
	}

	public void setCantRegistros(String cantRegistros) {
		this.cantRegistros = cantRegistros;
	}

	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("\ntipoRegistro " + tipoRegistro);
		str.append("\ncodigoRegistro " + codigoRegistro);
		str.append("\nindicadorDeProceso " + indicadorDeProceso);
		str.append("\nfechaProceso " + fechaProceso);
		str.append("\ncantRegistros " + cantRegistros);
		return str.toString();
	}
}
