package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubidaFTPPadronPF {
	
	private String linea;
	private String totalPadron;

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}
	

	public String getTotalPadron() {
		return totalPadron;
	}

	public void setTotalPadron(String totalPadron) {
		this.totalPadron = totalPadron;
	}

	
	public static SubidaFTPPadronPF getMapping(ResultSet rs)
			throws SQLException {
		SubidaFTPPadronPF subida = new SubidaFTPPadronPF();
		subida.setLinea(rs.getString("linea"));
		
		return subida;
	}
	
}
