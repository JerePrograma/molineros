package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class PreAutorizacionLoteProcesado implements Serializable{
	
	private static final long serialVersionUID = -6837561823680862193L;
	private Integer nroLote;
	private Date fechaProceso;
	private Integer totalRegistros;
	private String fileName;
	
	
	public static PreAutorizacionLoteProcesado getMapping(ResultSet rs) throws SQLException {
		PreAutorizacionLoteProcesado archivo = new PreAutorizacionLoteProcesado();
		
		archivo.setFechaProceso(rs.getDate("fecha_proceso"));
		archivo.setNroLote(rs.getInt("nro_lote"));
		archivo.setTotalRegistros(rs.getInt("cantidad_registros"));
		archivo.setFileName(rs.getString("nombre_archivo"));
		
		return archivo;
		
	}
	
		
	public String getFechaProceso_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaProceso != null ? sdf.format(fechaProceso)
				: "";
	}


	public Integer getNroLote() {
		return nroLote;
	}


	public void setNroLote(Integer nroLote) {
		this.nroLote = nroLote;
	}


	public Date getFechaProceso() {
		return fechaProceso;
	}


	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}


	public Integer getTotalRegistros() {
		return totalRegistros;
	}


	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
