package ar.com.ospim.procesaArchivos.beans.farmaciaospim;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.farmaciaOspim.beans.ArchivoMedEspecial;

public class ArchivoDesglose {
	
	private String usuario;
	private Date fecha_importacion;	
	private Date periodo ;		
	private Integer totalrecords;	
	private Integer cantpacientes;			
	private double totalpvp;
	private double  totalentidad;  
	private double  totalospim;  
	private double  totaluoma;  
	private double  totalamtima;
	private double promedio;
	 
	  
private List<DetalleDesglose > detalle;


public List<DetalleDesglose > getDetalle() {
	return detalle;
}

public void setDetalle(List<DetalleDesglose > detalleList) {
	this.detalle = detalleList;
}


public static ArchivoDesglose   getMapping(ResultSet rs) throws SQLException {
	ArchivoDesglose  archivo = new ArchivoDesglose  ();
	archivo.setUsuario (rs.getString("arch_usuario"));
	archivo.setFecha_importacion(rs.getDate("arch_fecha_importacion"));
	archivo.setPeriodo(rs.getDate("arch_periodo")); 
	archivo.setTotalrecords(rs.getInt("arch_registros"));
	archivo.setTotalpvp(rs.getDouble("arch_totalpvp"));
	archivo.setTotalentidad(rs.getDouble("arch_totalentidad"));
	archivo.setTotalospim(rs.getDouble("arch_totalospim"));
	archivo.setTotaluoma(rs.getDouble("arch_totaluoma"));
	archivo.setTotalamtima(rs.getDouble("arch_totalamtima"));
		
	return archivo;
}

public String getUsuario() {
	return usuario;
}

public void setUsuario(String usuario) {
	this.usuario = usuario;
}

public Date getFecha_importacion() {
	return fecha_importacion;
}

public void setFecha_importacion(Date fecha_importacion) {
	this.fecha_importacion = fecha_importacion;
}

public Date getPeriodo() {
	return periodo;
}

public void setPeriodo(Date periodo) {
	this.periodo = periodo;
}

public Integer getTotalrecords() {
	return totalrecords;
}

public void setTotalrecords(Integer totalrecords) {
	this.totalrecords = totalrecords;
}

public Integer getCantpacientes() {
	return cantpacientes;
}

public void setCantpacientes(Integer cantpacientes) {
	this.cantpacientes = cantpacientes;
}

public double getTotalpvp() {
	return totalpvp;
}

public void setTotalpvp(double totalpvp) {
	this.totalpvp = totalpvp;
}

public double getTotalentidad() {
	return totalentidad;
}

public void setTotalentidad(double totalentidad) {
	this.totalentidad = totalentidad;
}

public double getTotalospim() {
	return totalospim;
}

public void setTotalospim(double totalospim) {
	this.totalospim = totalospim;
}

public double getTotaluoma() {
	return totaluoma;
}

public void setTotaluoma(double totaluoma) {
	this.totaluoma = totaluoma;
}

public double getTotalamtima() {
	return totalamtima;
}

public void setTotalamtima(double totalamtima) {
	this.totalamtima = totalamtima;
}

public double getPromedio() {
	return promedio;
}

public void setPromedio(double promedio) {
	this.promedio = promedio;
}






}
