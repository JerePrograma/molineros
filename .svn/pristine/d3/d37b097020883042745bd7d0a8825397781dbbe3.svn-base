package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EspecialidadPrestador implements Serializable {
			
	/**
	 * 
	 */
	private static final long serialVersionUID = -5243807312392962823L;
	
	private int idEspecialidad;
	private String descripcion;
	private boolean tituloEspecialidad;
	
	private int idProfesion;  // referencia al id profesion que se relacion en tabla autorizaciones.profesion_especialidad 
	private int idProfEspec;  // referencia a la pk de tabla autorizaciones.profesion_especialidad 
	private int idPrestProfEspec;  // referencia a la pk de tabla autorizaciones.prest_prof_especialidad (aca tememos la especialidad del prestador)
	
	private List<SubEspecialidadPrestador> subEspecialidades;
	
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};

	public EspecialidadPrestador(){
		super();
	}

    public EspecialidadPrestador(int idEspecialidad, String descripcion,
			boolean tituloEspecialidad, int idProfesion) {
		super();
		this.idEspecialidad = idEspecialidad;
		this.descripcion = descripcion;
		this.tituloEspecialidad = tituloEspecialidad;
		this.idProfesion = idProfesion;
	}

	public static EspecialidadPrestador getMappingSoloEspecialidad(String prefix, ResultSet rs)
			throws SQLException {
    	
    	EspecialidadPrestador esp = new EspecialidadPrestador();
    	esp.setIdEspecialidad(rs.getInt(prefix + "id"));
    	esp.setDescripcion(rs.getString(prefix + "descripcion"));
		return esp;
	}
    
    public static EspecialidadPrestador getMappingEspecialidadDelPrestador(String prefix, ResultSet rs)
 			throws SQLException {
     	
     	EspecialidadPrestador esp = new EspecialidadPrestador();
     	esp.setIdEspecialidad(rs.getInt(prefix + "id"));
     	esp.setDescripcion(rs.getString(prefix + "descripcion"));
     	esp.setTituloEspecialidad(rs.getBoolean(prefix + "titulo_especialidad"));
     	esp.setIdPrestProfEspec(rs.getInt(prefix + "id_prest_prof_espec"));
     	
 		return esp;
 	}
    
    public static EspecialidadPrestador getMapping(String prefix, ResultSet rs)
			throws SQLException {
    	
    	EspecialidadPrestador esp = new EspecialidadPrestador();
    	esp.setIdEspecialidad(rs.getInt(prefix+"id_especialidad"));
    	esp.setDescripcion(rs.getString(prefix+"descripcion"));
    	esp.setIdProfesion(rs.getInt(prefix+"id_profesion"));
    	esp.setIdProfEspec(rs.getInt(prefix+"id"));
		return esp;
	}

	public int getIdEspecialidad() {
		return idEspecialidad;
	}

	public void setIdEspecialidad(int idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isTituloEspecialidad() {
		return tituloEspecialidad;
	}

	public void setTituloEspecialidad(boolean tituloEspecialidad) {
		this.tituloEspecialidad = tituloEspecialidad;
	}

	public int getIdProfesion() {
		return idProfesion;
	}

	public void setIdProfesion(int idProfesion) {
		this.idProfesion = idProfesion;
	}

	public int getIdProfEspec() {
		return idProfEspec;
	}

	public void setIdProfEspec(int idProfEspec) {
		this.idProfEspec = idProfEspec;
	}

	public int getIdPrestProfEspec() {
		return idPrestProfEspec;
	}

	public void setIdPrestProfEspec(int idPrestProfEspec) {
		this.idPrestProfEspec = idPrestProfEspec;
	}
	
	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "EspecialidadPrestador [idEspecialidad=" + idEspecialidad
				+ ", descripcion=" + descripcion + "]";
	}

	public List<SubEspecialidadPrestador> getSubEspecialidades() {
		return subEspecialidades;
	}

	public void setSubEspecialidades(List<SubEspecialidadPrestador> subEspecialidades) {
		this.subEspecialidades = subEspecialidades;
	}
	
	
}
