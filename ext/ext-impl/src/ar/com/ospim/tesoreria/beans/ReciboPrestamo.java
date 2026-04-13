package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.hoteles.beans.Prestamo;

public class ReciboPrestamo extends ReciboConcepto {
	private Prestamo prestamo;
	private BigDecimal importe;
	private Concepto concepto;
	
	public ReciboPrestamo() {
	}

	
	public Prestamo getPrestamo() {
		return prestamo;
	}


	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReciboPrestamo other = (ReciboPrestamo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static ReciboPrestamo getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static ReciboPrestamo getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReciboPrestamo oc = new ReciboPrestamo();
		
		Prestamo pre = new Prestamo();
		pre.setId(rs.getLong(prefix + "prestamo_id"));
		pre.setAcuerdoFecha(rs.getDate(prefix + "prestamo_fecha"));
		pre.setMonto(rs.getDouble(prefix +"prestamo_importe"));
		pre.setTotal(rs.getDouble(prefix +"prestamo_total"));
		oc.setPrestamo(pre);
		
		return oc;
	}
	

	@Override
	public BigDecimal getTotalAPagar() {
		return BigDecimal.valueOf( prestamo.getMonto());
	}


	@Override
	public BigDecimal getTotalAPagarNoOS() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Date getFechaAPagar() {
		return new Date();
	}


	@Override
	public String getDescripcion() {
		return concepto.getDescripcion();
	}


	@Override
	public BigDecimal getImporte() {
		// TODO Auto-generated method stub
		return BigDecimal.valueOf( prestamo.getMonto());
	}

		
	
}
