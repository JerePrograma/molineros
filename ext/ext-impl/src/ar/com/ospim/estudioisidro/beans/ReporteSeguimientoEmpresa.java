package ar.com.ospim.estudioisidro.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class ReporteSeguimientoEmpresa {

	private String razonSocial;
	private String estado;
	private Date fechaEstado;
	private boolean molinera;
	private String cartaDoc;
	private String ubicacionCarpeta;
	private Date fechaLlamado;
	private Date fecha;	 
	private String user;
	private String cuit;
	private String razon;
	private String observaciones;
	private Integer loteNro;
	private String loteTipo;
	
	public Integer getLoteNro() {
		return loteNro;
	}

	public void setLoteNro(Integer loteNro) {
		this.loteNro = loteNro;
	}

	public String getLoteTipo() {
		return loteTipo;
	}

	public void setLoteTipo(String loteTipo) {
		this.loteTipo = loteTipo;
	}

	public ReporteSeguimientoEmpresa() {
	}
	
	public ReporteSeguimientoEmpresa(String cuit, String user, Date fecha, String observaciones) {
		this.cuit = cuit;
		this.user = user;
		this.fecha = fecha;
		this.observaciones = observaciones;
	}

		
	public static ReporteSeguimientoEmpresa getMapping(ResultSet rs)	throws SQLException {
			ReporteSeguimientoEmpresa llamado = new ReporteSeguimientoEmpresa();
			llamado.setCuit(rs.getString("cuit"));
			llamado.setRazonSocial(rs.getString("razon_soc"));
			llamado.setEstado(rs.getString("estado"));
			llamado.setFechaEstado(rs.getTimestamp("fecha_estado"));
			llamado.setMolinera(rs.getBoolean("molinera"));
			llamado.setCartaDoc(rs.getString("carta_doc"));
			llamado.setUbicacionCarpeta(rs.getString("ubicacion_carpeta"));
			llamado.setFechaLlamado(rs.getTimestamp("fecha_llamado"));
			llamado.setObservaciones(rs.getString("observaciones"));
			llamado.setUser(rs.getString("usuario"));
			llamado.setLoteNro(rs.getInt("lote"));
			llamado.setLoteTipo(rs.getString("tipo_lote"));
			
			return llamado;
		
	}

	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
		return sdf.format(fecha);
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	public boolean isMolinera() {
		return molinera;
	}

	public void setMolinera(boolean molinera) {
		this.molinera = molinera;
	}

	public String getCartaDoc() {
		return cartaDoc;
	}

	public void setCartaDoc(String cartaDoc) {
		this.cartaDoc = cartaDoc;
	}

	public String getUbicacionCarpeta() {
		return ubicacionCarpeta;
	}

	public void setUbicacionCarpeta(String ubicacionCarpeta) {
		this.ubicacionCarpeta = ubicacionCarpeta;
	}

	public Date getFechaLlamado() {
		return fechaLlamado;
	}

	public void setFechaLlamado(Date fechaLlamado) {
		this.fechaLlamado = fechaLlamado;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}
	
	

	
}
