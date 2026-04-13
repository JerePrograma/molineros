package ar.com.ospim.estudioisidro.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class ReporteEstadisticoSeguimientoEmpresa {

	private Integer lote;
	private String loteTipo;
	private Date fecha;
	private Integer estadoId;
	private String estadoDescripcion;
	private Integer cantidad;
	private Double asignado;
	private Double recuperado;
	private Double cobrado;
	private Integer diasGestion;
	
	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public String getLoteTipo() {
		return loteTipo;
	}

	public void setLoteTipo(String loteTipo) {
		this.loteTipo = loteTipo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Integer getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(Integer estadoId) {
		this.estadoId = estadoId;
	}

	public String getEstadoDescripcion() {
		return estadoDescripcion;
	}

	public void setEstadoDescripcion(String estadoDescripcion) {
		this.estadoDescripcion = estadoDescripcion;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getAsignado() {
		return asignado;
	}

	public void setAsignado(Double asignado) {
		this.asignado = asignado;
	}

	public Double getRecuperado() {
		return recuperado;
	}

	public void setRecuperado(Double recuperado) {
		this.recuperado = recuperado;
	}

	public Integer getDiasGestion() {
		return diasGestion;
	}

	public void setDiasGestion(Integer diasGestion) {
		this.diasGestion = diasGestion;
	}

	
	public Double getCobrado() {
		return cobrado;
	}

	public void setCobrado(Double cobrado) {
		this.cobrado = cobrado;
	}

	public static ReporteEstadisticoSeguimientoEmpresa getMapping(ResultSet rs)	throws SQLException {
			ReporteEstadisticoSeguimientoEmpresa llamado = new ReporteEstadisticoSeguimientoEmpresa();
			
			try{
			  llamado.setLote(rs.getInt("lote_nro"));
			}catch(Exception e){}  
			try{
			  llamado.setFecha(rs.getTimestamp("fecha"));
			}catch(Exception e){}  
			try{
			   llamado.setLoteTipo(rs.getString("tipo_lote"));
			}catch(Exception e){}
			try{
			   llamado.setEstadoId(rs.getInt("estado_id"));
			}catch(Exception e){}
			try{
			   llamado.setEstadoDescripcion(rs.getString("estado_descripcion"));
			}catch(Exception e){}
			try{
			   llamado.setCantidad(rs.getInt("cantidad"));
			}catch(Exception e){}
			try{
			   llamado.setAsignado(rs.getDouble("asignado"));
			}catch(Exception e){}
			try{
			   llamado.setRecuperado(rs.getDouble("recuperado"));
			}catch(Exception e){}
			try{
			   llamado.setCobrado(rs.getDouble("cobrado"));
			}catch(Exception e){}
			try{
				llamado.setDiasGestion(rs.getInt("dias_gestion"));
			}catch(Exception e){}
			
			return llamado;
		
	}

	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		String ret = "";
		
		try{
			ret=sdf.format(fecha);
		}catch(Exception e){}
		
		return ret;
	}
	
	
}
