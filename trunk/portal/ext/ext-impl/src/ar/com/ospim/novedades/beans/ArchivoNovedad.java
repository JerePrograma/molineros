package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ArchivoNovedad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7449247619602497709L;
	private static Log _log = LogFactoryUtil.getLog(ArchivoNovedad.class);

	private Date fechaArchivo; 
	private String descripcion; 
	private int cantRegistros; 
	private String importUsr; 
	private Date  importFecha;
	
	public ArchivoNovedad(Date fechaArchivo, String descripcion, int cantRegistros, String importUsr, Date importFecha){
		super();
		this.fechaArchivo = fechaArchivo;
		this.descripcion = descripcion;
		this.cantRegistros = cantRegistros;
		this.importUsr = importUsr;
		this.importFecha = importFecha;
	}
	
	public ArchivoNovedad(){
		super();
	}
	public Date getFechaArchivo() {
		return fechaArchivo;
	}
	public void setFechaArchivo(Date fechaArchivo) {
		this.fechaArchivo = fechaArchivo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getCantRegistros() {
		return cantRegistros;
	}
	public void setCantRegistros(int cantRegistros) {
		this.cantRegistros = cantRegistros;
	}
	public String getImportUsr() {
		return importUsr;
	}
	public void setImportUsr(String importUsr) {
		this.importUsr = importUsr;
	}
	public Date getImportFecha() {
		return importFecha;
	}
	public void setImportFecha(Date importFecha) {
		this.importFecha = importFecha;
	}
	
	public static ArchivoNovedad getMapping(ResultSet rs){
		ArchivoNovedad an;
		try {
			an = new ArchivoNovedad(
					rs.getDate("fecha_archivo"), 
					rs.getString("descripcion"), 
					rs.getInt("cant_registros"), 
					rs.getString("import_usr"), 
					rs.getTimestamp("import_fecha"));
			
		} catch (SQLException e) {
			_log.error(e);
			return null;
		}
		
		return an;	
	}
}
