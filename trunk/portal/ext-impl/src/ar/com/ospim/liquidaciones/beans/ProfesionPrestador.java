package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProfesionPrestador implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -753007388444001315L;
	private int idProfesion;
	private String descripcion;
	private boolean tituloProfesional;
	private String categoriaProfOspim;
	
	private int idPrestProf; // referencia a la pk de tabla autorizaciones.prestador_profesion (aca tememos la profesion del prestador)
	
	private List<EspecialidadPrestador> especialidades;
	
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	
	public ProfesionPrestador(){
		super();
	}
	
	public ProfesionPrestador(int idProfesion, String descripcion,
			boolean tituloProfesional, String categoriaProfOspim) {
		
		super();
		this.idProfesion = idProfesion;
		this.descripcion = descripcion;
		this.tituloProfesional = tituloProfesional;
		this.categoriaProfOspim = categoriaProfOspim;
	}

	public static ProfesionPrestador getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		ProfesionPrestador prof = new ProfesionPrestador();
		prof.setIdProfesion(rs.getInt(prefix + "id_profesion"));
		prof.setDescripcion(rs.getString(prefix + "descripcion"));
		
		return prof;
	}
	
	public static ProfesionPrestador getMappingProfesionDelPrestador(String prefix, ResultSet rs)
			throws SQLException {
		
		ProfesionPrestador prof = new ProfesionPrestador();
		prof.setIdProfesion(rs.getInt(prefix + "id_profesion"));
		prof.setDescripcion(rs.getString(prefix + "descripcion"));
		prof.setTituloProfesional(rs.getBoolean(prefix + "titulo_profesional"));
		prof.setCategoriaProfOspim(rs.getString(prefix + "cat_prof_ospim"));
		prof.setIdPrestProf(rs.getInt(prefix + "id_prest_prof"));

		return prof;
	}

	public final void setIdProfesion(int idProfesion) {
		this.idProfesion = idProfesion;
	}

	public final void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public final int getIdProfesion() {
		return idProfesion;
	}

	public final String getDescripcion() {
		return descripcion;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + idProfesion;
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ProfesionPrestador other = (ProfesionPrestador) obj;
//		if (idProfesion != other.idProfesion)
//			return false;
//		return true;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idPrestProf;
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
		ProfesionPrestador other = (ProfesionPrestador) obj;
		if (idPrestProf != other.idPrestProf)
			return false;
		return true;
	}
	
	public boolean isTituloProfesional() {
		return tituloProfesional;
	}

	public void setTituloProfesional(boolean tituloProfesional) {
		this.tituloProfesional = tituloProfesional;
	}

	public String getCategoriaProfOspim() {
		return categoriaProfOspim;
	}

	public void setCategoriaProfOspim(String categoriaProfOspim) {
		this.categoriaProfOspim = categoriaProfOspim;
	}

	public int getIdPrestProf() {
		return idPrestProf;
	}

	public void setIdPrestProf(int idPrestProf) {
		this.idPrestProf = idPrestProf;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "ProfesionPrestador [idProfesion=" + idProfesion
				+ ", descripcion=" + descripcion + ", tituloProfesional="
				+ tituloProfesional + ", categoriaProfOspim="
				+ categoriaProfOspim + "]";
	}

	public List<EspecialidadPrestador> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(List<EspecialidadPrestador> especialidades) {
		this.especialidades = especialidades;
	}
	
	
	
}
