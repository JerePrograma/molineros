package ar.com.ospim.farmaciaOspim.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;


public class ArchivoMedEspecial {
	
	private String usuario;
	private Date fecha_importacion;	
	private Date periodo ;		
	private Integer totalrecords;
	private double totalconiva;
	private Integer cantpacientes;			
	private double totalsiniva;
	private double promedio;

	
	public ArchivoMedEspecial(){};
	
	public static ArchivoMedEspecial getMapping(ResultSet rs) throws SQLException {
		ArchivoMedEspecial  archivo = new ArchivoMedEspecial ();
		archivo.setUsuario (rs.getString("usuario"));
		archivo.setfecha_importacion(rs.getDate("fecha_importacion"));
		archivo.setPeriodo(rs.getDate("periodo")); 
		archivo.setTotalrecords(rs.getInt("registros"));		
		archivo.setTotalconiva(rs.getDouble("totalconiva"));
		archivo.setTotalsiniva(rs.getDouble("totalsiniva"));
		archivo.setCantpacientes(rs.getInt("cantpacientes"));
		archivo.setPromedio(rs.getDouble("promedio"));
		return archivo;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public double  getTotalconiva() {
		return totalconiva;
	}

	public String getTotalconivaString() {
		DecimalFormat df = new DecimalFormat("####0.00"); 
		return (df.format(totalconiva));		 
	}
	
	public void setTotalconiva(double  totalconiva) {		
		this.totalconiva =  totalconiva ;
	}

	public double getTotalsiniva() {
		return totalsiniva;
	}

	public String getTotalsinivaString() {
		
		DecimalFormat df = new DecimalFormat("####0.00"); 
		return (df.format(totalsiniva));
	}
	
	public void setTotalsiniva(double  totalsiniva) {
		this.totalsiniva = totalsiniva;
	}

	public final String getUsuario() {
		return usuario;
	}

	public final void setUsuario (String usuario) {
		this.usuario = usuario;
	}

	public final Date getfecha_importacion() {
		return fecha_importacion;
	}
		
	public String getfecha_importacionAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha_importacion);
	}
	public String getfecha_periodoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(periodo).substring(3,5) + sdf.format(periodo).substring(5,10);
	}

	public final void setfecha_importacion (Date fecha_importacion) {
		this.fecha_importacion= fecha_importacion;
	}	

	public Date getFecha_importacion() {
		return fecha_importacion;
	}

	public void setFecha_importacion(Date fecha_importacion) {
		this.fecha_importacion = fecha_importacion;
	}

	public Integer getTotalrecords() {
		return totalrecords;
	}
	
	public String  getTotalrecordsString() {
		return totalrecords.toString() ;
	}

	public void setTotalrecords(Integer totalrecords) {
		this.totalrecords = totalrecords;
	}

	public Integer getCantpacientes() {
		return cantpacientes;
	}
	
	public String getCantpacientesString() {
		return cantpacientes.toString() ;
	}
	
	public void setCantpacientes(Integer cantpacientes) {
		this.cantpacientes = cantpacientes;
	}
	
	public double getPromedio() {
		return promedio;
	}
	public String getPromedioString(){
		return   String.valueOf(promedio);  
	}

	public void setPromedio(double promedio) {
		this.promedio = promedio;
	}
		
}
