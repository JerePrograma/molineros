package ar.com.ospim.procesaArchivos.beans.transferenciaexterna;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FooterTransferenciaExterna {
	private String debitoCredito;
	private String codigoOrganismo;
	private Date fechaProceso;

	public FooterTransferenciaExterna(String line) throws ParseException {
		codigoOrganismo = line.substring(12, 16);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		fechaProceso = sdf.parse(line.substring(22, 36));
		debitoCredito = line.substring(60, 61);
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setCodigoOrganismo(String codigoOrganismo) {
		this.codigoOrganismo = codigoOrganismo;
	}

	public String getCodigoOrganismo() {
		return codigoOrganismo;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}
}
