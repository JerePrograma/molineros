package ar.com.uoma.beans;

import java.sql.ResultSet;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class SeguimientoIncidente {
	private Date fecha;
	private String detalle;
	
	public static SeguimientoIncidente getMapping(ResultSet rs) throws Exception{
		SeguimientoIncidente seguimientoIncidente=new SeguimientoIncidente();
		seguimientoIncidente.setFecha(rs.getDate("fecha"));
		seguimientoIncidente.setDetalle(rs.getString("detalle"));
		return seguimientoIncidente;		
	}
	
	
	public Date getFecha() {
		return fecha;
	}
	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha!=null?sdf.format(fecha):"";
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	
	

}
