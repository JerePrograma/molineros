package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FirmaAutorizante implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private long idUsuario;
	private int tipoDictamen;
	private int idEqInterdisciplinario;
	private String altaUsr;  
	private Date altaFecha;
	private String bajaUsr;
	private Date bajaFecha;
	private String path;
	
	public FirmaAutorizante() {
	}
	
	public FirmaAutorizante(long idUsuario, int tipoDictamen, String altaUsr) {
		this.idUsuario = idUsuario;
		this.tipoDictamen = tipoDictamen;
		this.altaUsr = altaUsr;
	}
	
	
	public int getId() {
		return id;
	}
	public long getIdUsuario() {
		return idUsuario;
	}
	public int getTipoDictamen() {
		return tipoDictamen;
	}
	public int getIdEqInterdisciplinario() {
		return idEqInterdisciplinario;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}
	public void setTipoDictamen(int tipoDictamen) {
		this.tipoDictamen = tipoDictamen;
	}
	public void setIdEqInterdisciplinario(int idEqInterdisciplinario) {
		this.idEqInterdisciplinario = idEqInterdisciplinario;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	
	public static FirmaAutorizante getMapping(ResultSet rs)
			throws SQLException {
		
		FirmaAutorizante  firmaAutorizante = new FirmaAutorizante();
		
		
		firmaAutorizante.setIdUsuario(rs.getInt("id_usuario"));
		firmaAutorizante.setTipoDictamen(rs.getInt("tipo_dictamen"));
		firmaAutorizante.setIdEqInterdisciplinario(rs.getInt("id_eq_interdisciplinario"));
		firmaAutorizante.setAltaUsr(rs.getString("alta_usr"));
		firmaAutorizante.setAltaFecha(rs.getDate("baja_usr"));
		firmaAutorizante.setPath(rs.getString("path"));

		

		return firmaAutorizante ;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
