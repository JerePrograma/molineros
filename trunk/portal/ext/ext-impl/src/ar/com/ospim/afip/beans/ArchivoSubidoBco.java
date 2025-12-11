package ar.com.ospim.afip.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class ArchivoSubidoBco {

	private String descripcion;
	private Date fecha_rendicion;
	private BigDecimal sum;
	
	public ArchivoSubidoBco(){};
	
	public static ArchivoSubidoBco getMapping(ResultSet rs) throws SQLException {
		ArchivoSubidoBco archivo = new ArchivoSubidoBco();
		archivo.setDescripcion(rs.getString("descripcion"));
		archivo.setFecha_rendicion(rs.getDate("fecha_rendicion"));
		archivo.setSum(rs.getBigDecimal("sum"));
		return archivo;
	}


	public final String getDescripcion() {
		return descripcion;
	}

	public final void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public final Date getFecha_rendicion() {
		return fecha_rendicion;
	}

	public final BigDecimal getSum() {
		return sum;
	}
	
	public String getFecha_rendicionAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha_rendicion);
	}

	public final void setFecha_rendicion(Date fecha_rendicion) {
		this.fecha_rendicion = fecha_rendicion;
	}
	
	public String getSumAsString(){
		DecimalFormat nf=new DecimalFormat("#,##0.00");
		return sum!=null?nf.format(sum):"****";
	}

	public final void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	
}
