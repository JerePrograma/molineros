package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Localidad implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2941645751060338390L;
	private int id;
	private String descripcion;
	private int id_provincia;
	private int cpostal;
	private String codAreaTelefono;
	private int id_provinciasss; 
	private int id_localidadesss;

	public Localidad(){
		super();
	}
	
	public Localidad(int id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
	}
	
	public Localidad(int id, String descripcion, int id_provincia){
		this.id=id;
		this.descripcion=descripcion;
		this.id_provincia = id_provincia;
	}
	
	public Localidad(int id, String descripcion, int id_provincia, int cpostal){
		this.id=id;
		this.descripcion=descripcion;
		this.id_provincia = id_provincia;
		this.cpostal = cpostal;
	}
	
	public Localidad(int id, String descripcion, int id_provincia, int cpostal, String codAreaTel){
		this.id=id;
		this.descripcion=descripcion;
		this.id_provincia = id_provincia;
		this.cpostal = cpostal;
		this.codAreaTelefono=codAreaTel;
	}
	
	public Localidad(int id, String descripcion, int id_provincia, int cpostal, String codAreaTel, int idProvSSS, int idLocSSS){
		this.id=id;
		this.descripcion=descripcion;
		this.id_provincia = id_provincia;
		this.cpostal = cpostal;
		this.codAreaTelefono=codAreaTel;
		this.id_provinciasss = idProvSSS;
		this.id_localidadesss = idLocSSS;
	}
	
	public static Localidad getMapping(ResultSet rs) throws SQLException{
		
		Localidad loc = new Localidad(rs.getInt("id_localidad"), 
							rs.getString("detalle"), 
							rs.getInt("id_provincia"), 
							rs.getInt("cod_postal"),
							rs.getString("cod_area_telefono"));
		
		return loc;
	}
	
	public static Localidad getMappingSSS(ResultSet rs) throws SQLException{
		
		Localidad loc = new Localidad(rs.getInt("id_localidad"), 
							rs.getString("detalle"), 
							rs.getInt("id_provincia"), 
							rs.getInt("cod_postal"),
							rs.getString("cod_area_telefono"),
							rs.getInt("id_provinciasss"),
							rs.getInt("id_localidadesss"));
		
		return loc;
	}
		
		
	public Localidad(int localidadId) {
		this.id = localidadId;
		this.descripcion = "";
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}



	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	/**
	 * @return the id_provincia
	 */
	public int getId_provincia() {
		return id_provincia;
	}


	/**
	 * @param idProvincia the id_provincia to set
	 */
	public void setId_provincia(int idProvincia) {
		id_provincia = idProvincia;
	}

	/**
	 * @return the cod_postal
	 */
	public int getCod_postal() {
		return cpostal;
	}

	/**
	 * @param codPostal the cod_postal to set
	 */
	public void setCod_postal(int cpostal) {
		this.cpostal = cpostal;
	}

	public String getCodAreaTelefono() {
		return codAreaTelefono;
	}

	public void setCodAreaTelefono(String codAreaTelefono) {
		this.codAreaTelefono = codAreaTelefono;
	}

	public int getCpostal() {
		return cpostal;
	}

	public void setCpostal(int cpostal) {
		this.cpostal = cpostal;
	}

	public int getId_provinciasss() {
		return id_provinciasss;
	}

	public void setId_provinciasss(int id_provinciasss) {
		this.id_provinciasss = id_provinciasss;
	}

	public int getId_localidadesss() {
		return id_localidadesss;
	}

	public void setId_localidadesss(int id_localidadesss) {
		this.id_localidadesss = id_localidadesss;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Localidad other = (Localidad) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}