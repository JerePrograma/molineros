package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ArchivoSubidoEstudio implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7459580730979412236L;
	
	private String tipo;
	private Integer nroLote;
	private String tipoLote;
	private Date fechaProceso;
	private int cantReg;
	private String usuario;
	
	
	public ArchivoSubidoEstudio(){
		super();
	};
	
	public ArchivoSubidoEstudio(String tipo, Integer nroLote, String tipoLote,
			Date fechaProceso, int cantReg, String usuario) {
		super();
		this.tipo = tipo;
		this.nroLote = nroLote;
		this.tipoLote = tipoLote;
		this.fechaProceso = fechaProceso;
		this.cantReg = cantReg;
		this.usuario = usuario;
	}



	public static ArchivoSubidoEstudio getMapping(ResultSet rs) throws SQLException {
		ArchivoSubidoEstudio archivo = new ArchivoSubidoEstudio();
		archivo.setTipo(rs.getString("tipo_archivo"));
		archivo.setFechaProceso(rs.getDate("fecha"));
		archivo.setCantReg(rs.getInt("cantidad"));
		archivo.setNroLote(rs.getInt("lote_nro"));
		archivo.setTipoLote(rs.getString("lote_tipo"));
		archivo.setUsuario(rs.getString("user_alta"));
		return archivo;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Date getFechaProceso() {
		return fechaProceso;
	}
	
	public String getFechaProcesoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fechaProceso);
	}
	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}
	public int getCantReg() {
		return cantReg;
	}
	public void setCantReg(int cantReg) {
		this.cantReg = cantReg;
	}

	public Integer getNroLote() {
		return nroLote;
	}

	public void setNroLote(Integer nroLote) {
		this.nroLote = nroLote;
	}

	public String getTipoLote() {
		return tipoLote;
	}

	public void setTipoLote(String tipoLote) {
		this.tipoLote = tipoLote;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	
		
}
