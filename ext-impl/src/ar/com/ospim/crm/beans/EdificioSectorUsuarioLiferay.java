package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EdificioSectorUsuarioLiferay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -707324507345758753L;
	
	private String edificio;
	private String empresaDescripcion;
	private String grupo;
	private String sectorDescripcion;
	private String usuario;
	private String usuarioApeyNom;
	
	public String getEdificio() {
		return edificio;
	}
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}
	public String getEmpresaDescripcion() {
		return empresaDescripcion;
	}
	public void setEmpresaDescripcion(String empresaDescripcion) {
		this.empresaDescripcion = empresaDescripcion;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getSectorDescripcion() {
		return sectorDescripcion;
	}
	public void setSectorDescripcion(String sectorDescripcion) {
		this.sectorDescripcion = sectorDescripcion;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getUsuarioApeyNom() {
		return usuarioApeyNom;
	}
	public void setUsuarioApeyNom(String usuarioApeyNom) {
		this.usuarioApeyNom = usuarioApeyNom;
	}
	
	@Override
	public String toString() {
		return "UsuarioLiferay [edificio=" + edificio + ", grupo=" + grupo
				+ ", usuario=" + usuario + "]";
	}
	
	public EdificioSectorUsuarioLiferay(String edi, String sec, String usu){
		super();
		this.edificio = edi;
		this.grupo = sec;
		this.usuario = usu;
	}
	
	public static EdificioSectorUsuarioLiferay getMapping(String prefix, ResultSet rs) throws SQLException {
		
		EdificioSectorUsuarioLiferay ul = new EdificioSectorUsuarioLiferay(
								rs.getString(prefix + "edificio"), 
								rs.getString(prefix + "sector"),
								rs.getString(prefix + "usuario"));
		return ul;
		
	}
	
}

