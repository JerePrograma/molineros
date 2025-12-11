package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.global.beans.ContactoElectronico;

public class ContactoElectronicoPrestador extends ContactoElectronico {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2508534802675130762L;
	
	private String propio ; // D = LugarAt Directo, I = LugarAt Indirecto, P = Propio (sin asociar a LugarAt)

	public String getPropio() {
		return propio;
	}

	public void setPropio(String propio) {
		this.propio = propio;
	}
	
	public static ContactoElectronicoPrestador getMapping(ResultSet rs) throws SQLException {
		ContactoElectronicoPrestador contactoElectronico = new ContactoElectronicoPrestador();
		contactoElectronico.setId(rs.getInt("id_contacto_e"));
		contactoElectronico.setTipo(Tipo.getTipoById(rs.getString("tipo_contacto_e")));
		contactoElectronico.setVigenDesde(rs.getDate("vigen_desde"));
		contactoElectronico.setContacto(rs.getString("contacto"));
		contactoElectronico.setObservaciones(rs.getString("observaciones"));
		contactoElectronico.setPropio(rs.getString("propio"));
		contactoElectronico.setAltaFecha(rs.getDate("alta_fecha"));
		contactoElectronico.setAltaUsuario(rs.getString("alta_usr"));
		contactoElectronico.setModiFecha(rs.getDate("modi_fecha"));
		contactoElectronico.setModiUsuario(rs.getString("modi_usr"));
		contactoElectronico.setBajaFecha(rs.getDate("baja_fecha"));
		contactoElectronico.setBajaUsuario(rs.getString("baja_usr"));
		
		return contactoElectronico;
	}

	
}


