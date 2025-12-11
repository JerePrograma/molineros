package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubEspecialidadPrestador implements Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = -8051512779485429168L;

	private int id;
	private String descripcion;
	private int idEspecialidad;  // referencia al id de tabla autorizaciones.especialidad_subespecialidad
	private int idPrestProfEspecSubEspec;  // referencia a la pk de tabla autorizaciones.prest_prof_esp_subesp (es la subEspecialidad del prestador)
	
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	public SubEspecialidadPrestador(){
		super();
	}
	
	public SubEspecialidadPrestador(int id, String descripcion,
			int idEspecialidad) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.idEspecialidad = idEspecialidad;
	}

	public static SubEspecialidadPrestador getMappingSoloSubEspecialidad(String prefix, ResultSet rs)
			throws SQLException {
		
		SubEspecialidadPrestador subEspe = new SubEspecialidadPrestador();
		subEspe.setId(rs.getInt(prefix + "id"));
		subEspe.setDescripcion(rs.getString(prefix + "descripcion"));
		return subEspe;
	}
	
	public static SubEspecialidadPrestador getMappingSubEspecialidadDelPrestador(String prefix, ResultSet rs)
			throws SQLException {
		
		SubEspecialidadPrestador subEspe = new SubEspecialidadPrestador();
		subEspe.setId(rs.getInt(prefix + "id"));
		subEspe.setDescripcion(rs.getString(prefix + "descripcion"));
		subEspe.setIdPrestProfEspecSubEspec(rs.getInt(prefix + "id_prest_prof_espec_subesp"));

		return subEspe;
	}
	
	public static SubEspecialidadPrestador getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		SubEspecialidadPrestador subEspe = new SubEspecialidadPrestador();
		subEspe.setId(rs.getInt(prefix + "id_subespecialidad"));
		subEspe.setDescripcion(rs.getString(prefix + "descripcion"));
		subEspe.setIdEspecialidad(rs.getInt(prefix + "id_especialidad"));

		return subEspe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public final String getDescripcion() {
		return descripcion;
	}

	public final void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
		SubEspecialidadPrestador other = (SubEspecialidadPrestador) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(int idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public int getIdPrestProfEspecSubEspec() {
		return idPrestProfEspecSubEspec;
	}

	public void setIdPrestProfEspecSubEspec(int idPrestProfEspecSubEspec) {
		this.idPrestProfEspecSubEspec = idPrestProfEspecSubEspec;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "SubEspecialidadPrestador [id=" + id + ", descripcion="
				+ descripcion + ", idEspecialidad=" + idEspecialidad + "]";
	}

	
}
