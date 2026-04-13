package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class SeguimientoSurLoteProcesado implements Serializable{
	private static final long serialVersionUID = -822754247327690134L;
	
	
	private Integer nroLote;
	private String tipoArchivo;
	private Date fechaProceso;
	private Integer totalRegistros;
	private Integer imputados;
	private Integer noEncontrados;
	private Integer vencidos;
	private Integer existentes;
	
	public static SeguimientoSurLoteProcesado getMapping(ResultSet rs) throws SQLException {
		SeguimientoSurLoteProcesado archivo = new SeguimientoSurLoteProcesado();
		
		archivo.setExistentes(rs.getInt("existentes"));
		archivo.setFechaProceso(rs.getDate("fecha_proceso"));
		archivo.setImputados(rs.getInt("imputados"));
		archivo.setNoEncontrados(rs.getInt("noencontrados"));
		archivo.setNroLote(rs.getInt("nro_lote"));
		archivo.setTipoArchivo(rs.getString("tipo_archivo"));
		archivo.setTotalRegistros(rs.getInt("existentes")+rs.getInt("imputados")+rs.getInt("noencontrados"));
		archivo.setVencidos(rs.getInt("vencidos"));
		
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


	public Integer getImputados() {
		return imputados;
	}


	public void setImputados(Integer imputados) {
		this.imputados = imputados;
	}


	public Integer getNoEncontrados() {
		return noEncontrados;
	}


	public void setNoEncontrados(Integer noEncontrados) {
		this.noEncontrados = noEncontrados;
	}


	public Integer getVencidos() {
		return vencidos;
	}


	public void setVencidos(Integer vencidos) {
		this.vencidos = vencidos;
	}


	public Integer getExistentes() {
		return existentes;
	}

	
	public void setExistentes(Integer existentes) {
		this.existentes = existentes;
	}

	
	public String getTipoArchivo() {
		return tipoArchivo;
	}

	
	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}	
	
}
