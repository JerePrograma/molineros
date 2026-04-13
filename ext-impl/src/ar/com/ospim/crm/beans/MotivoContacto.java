package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MotivoContacto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5970002049496914525L;
	private Integer id;
	private String descripcion;
	private String descripcionPredeterminada;
	private String cierrePredeterminado;
	
	
	public MotivoContacto (Integer id, String descripcion){
		
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public MotivoContacto (Integer id, String descripcion, String descripcionPredeterm, String cierrePredeterm){
		
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.descripcionPredeterminada=descripcionPredeterm;
		this.cierrePredeterminado=cierrePredeterm;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getDescripcionPredeterminada() {
		return descripcionPredeterminada;
	}

	public void setDescripcionPredeterminada(String descripcionPredeterminada) {
		this.descripcionPredeterminada = descripcionPredeterminada;
	}

	public String getCierrePredeterminado() {
		return cierrePredeterminado;
	}

	public void setCierrePredeterminado(String cierrePredeterminado) {
		this.cierrePredeterminado = cierrePredeterminado;
	}

	public String toString(){
		return this.descripcion;
	}
	
	public static MotivoContacto getMapping(String prefix, ResultSet rs) throws SQLException{
		
		MotivoContacto cc = new MotivoContacto(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion"));
		
		return cc;
	}
	
	public static MotivoContacto getMappingConPredeterm(String prefix, ResultSet rs) throws SQLException{
		
		MotivoContacto cc = new MotivoContacto(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion"), 
				rs.getString(prefix + "descripcion_predeterminada"), rs.getString(prefix + "cierre_predeterminado"));
		
		return cc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MotivoContacto other = (MotivoContacto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
