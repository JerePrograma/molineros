package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdenPagoConError {
	
	private int ordenPago;
	private String cuitPrestador;
	
	
	public int getOrdenPago() {
		return ordenPago;
	}
	public String getCuitPrestador() {
		return cuitPrestador;
	}
	public void setOrdenPago(int ordenPago) {
		this.ordenPago = ordenPago;
	}
	public void setCuitPrestador(String cuitPrestador) {
		this.cuitPrestador = cuitPrestador;
	}
	
	
	
	public static OrdenPagoConError getMapping(ResultSet rs) throws SQLException {
		OrdenPagoConError pago = new OrdenPagoConError();
		pago.setOrdenPago(rs.getInt("orden_pago"));
		pago.setCuitPrestador(rs.getString("cuit"));
		
		return pago;
	}
	
	

}
