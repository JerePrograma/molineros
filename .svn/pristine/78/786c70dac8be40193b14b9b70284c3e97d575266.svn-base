package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TotalesPadronIGS {

	private String nombreIGS;
	private int cantidad;
	
	public String getNombreIGS() {
		return nombreIGS;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setNombreIGS(String nombreIGS) {
		this.nombreIGS = nombreIGS;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	
	public static TotalesPadronIGS getMapping(ResultSet rs)
			throws SQLException {
		TotalesPadronIGS totales = new TotalesPadronIGS();
		totales.setNombreIGS(rs.getString("plan_igs"));
		totales.setCantidad(rs.getInt("cantidad"));
	
		return totales;
	}
	
	
	
	
}
