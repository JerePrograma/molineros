package ar.com.ospim.procesaArchivos.beans.transferenciaexterna;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderTransferenciaExterna {

	private String codigoOrganismo;
	private Date fechaProceso;

	public HeaderTransferenciaExterna(String line) throws ParseException {
		codigoOrganismo = line.substring(12,16);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		fechaProceso = sdf.parse(line.substring(22,36));
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
