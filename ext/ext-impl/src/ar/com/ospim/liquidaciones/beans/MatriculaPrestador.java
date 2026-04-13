package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Provincia;

public class MatriculaPrestador implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -886930017592267419L;
	private int idMatricula;
	private String tipo;
    private int numero;
//    private int provincia;
    private boolean presentaCopia;
    private Date fechaVto;
    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;
    private Provincia provincia;
    private Prestador prestador;
    private ESTADOS estado;
    
    public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
    
    
    public MatriculaPrestador(){
    	super();
    }
    
    public MatriculaPrestador(String tipo, int numero, boolean presentaCopia,
			Date fechaVto, Provincia provincia, Prestador prestador) {
    	
		super();
		this.tipo = tipo;
		this.numero = numero;
		this.presentaCopia = presentaCopia;
		this.fechaVto = fechaVto;
		this.provincia = provincia;
		this.prestador = prestador;
	}


	public static MatriculaPrestador getMapping(String prefix, ResultSet rs)
			throws SQLException {
    	
    	MatriculaPrestador mat = new MatriculaPrestador();
		mat.setIdMatricula(rs.getInt(prefix+"id_matricula"));
		mat.setTipo(rs.getString(prefix+"tipo_matricula"));
		mat.setNumero(rs.getInt(prefix+"numero_matricula"));
//		mat.setProvincia(rs.getInt("id_provincia"));
		mat.setPresentaCopia(rs.getBoolean(prefix+"presenta_copia"));
		mat.setFechaVto(rs.getDate(prefix+"fecha_vto"));
		mat.setAltaFecha(rs.getDate(prefix+"alta_fecha"));
		mat.setAltaUsr(rs.getString(prefix+"alta_usr"));
		mat.setModiFecha(rs.getDate(prefix+"modi_fecha"));
		mat.setModiUsr(rs.getString(prefix+"modi_usr"));
		mat.setBajaFecha(rs.getDate(prefix+"baja_fecha"));
		mat.setBajaUsr(rs.getString(prefix+"baja_usr"));
		
		mat.setProvincia(Provincia.getMapping("provincia_", rs));
		
		return mat;
	}
    
    public static MatriculaPrestador getMappingConPrestador(String prefix, ResultSet rs)
			throws SQLException {
    	
    	MatriculaPrestador mat = new MatriculaPrestador();
		mat.setIdMatricula(rs.getInt("id_matricula"));
		mat.setTipo(rs.getString("tipo_matricula"));
		mat.setNumero(rs.getInt("numero_matricula"));
//		mat.setProvincia(rs.getInt("id_provincia"));
		mat.setPresentaCopia(rs.getBoolean("presenta_copia"));
		mat.setFechaVto(rs.getDate("fecha_vto"));
		mat.setAltaFecha(rs.getDate("alta_fecha"));
		mat.setAltaUsr(rs.getString("alta_usr"));
		mat.setModiFecha(rs.getDate("modi_fecha"));
		mat.setModiUsr(rs.getString("modi_usr"));
		mat.setBajaFecha(rs.getDate("baja_fecha"));
		mat.setBajaUsr(rs.getString("baja_usr"));
		mat.setProvincia(Provincia.getMapping(rs));
		mat.setPrestador(Prestador.getMapping(rs, "pres_"));
		
		return mat;
	}
    

	public boolean isPresentaCopia() {
		return presentaCopia;
	}

	public void setPresentaCopia(boolean presentaCopia) {
		this.presentaCopia = presentaCopia;
	}

	public int getIdMatricula() {
		return idMatricula;
	}

	public void setIdMatricula(int idMatricula) {
		this.idMatricula = idMatricula;
	}

	public String getTipo() {
		return tipo;
	}

	public int getNumero() {
		return numero;
	}
	
	public String getNumeroToString() {
		return Integer.toString(getNumero());
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public Date getFechaVto() {
		return fechaVto;
	}
	
	public String getFechaVtoTostring() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaVto != null ? sdf.format(fechaVto): "";
		}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setFechaVto(Date fechaVto) {
		this.fechaVto = fechaVto;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}


	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaVtoStr = "";
		String provinciaStr = "";
		String idPrestStr ="0";
		if(fechaVto !=null){
			fechaVtoStr=sdf.format(fechaVto);
		}
		if(getProvincia() != null){
			provinciaStr = getProvincia().getDescripcion();
		}
		if(prestador != null){
			idPrestStr = prestador.getId_prestadorString();
		}
		return "MatriculaPrestador [idMatricula=" + idMatricula + ", tipo="
				+ tipo + ", numero=" + numero + ", presentaCopia="
				+ presentaCopia + ", fechaVto=" + fechaVtoStr + ", provincia="+ provinciaStr
				+ ", prestador=" + idPrestStr + ", estado=" + estado
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idMatricula;
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
		MatriculaPrestador other = (MatriculaPrestador) obj;
		if (idMatricula != other.idMatricula)
			return false;
		return true;
	}
	
	

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((altaFecha == null) ? 0 : altaFecha.hashCode());
//		result = prime * result
//				+ ((altaUsr == null) ? 0 : altaUsr.hashCode());
//		result = prime * result + (bajaEnBase ? 1231 : 1237);
//		result = prime * result
//				+ ((bajaFecha == null) ? 0 : bajaFecha.hashCode());
//		result = prime * result
//				+ ((bajaUsr == null) ? 0 : bajaUsr.hashCode());
//		result = prime * result
//				+ ((fechaVto == null) ? 0 : fechaVto.hashCode());
//		result = prime * result + idMatricula;
//		result = prime * result
//				+ ((modiFecha == null) ? 0 : modiFecha.hashCode());
//		result = prime * result
//				+ ((modiUsr == null) ? 0 : modiUsr.hashCode());
//		result = prime * result + numero;
//		result = prime * result + (presentaCopia ? 1231 : 1237);
//		result = prime * result + provincia;
//		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
//		MatriculaPrestador other = (MatriculaPrestador) obj;
//		if (altaFecha == null) {
//			if (other.altaFecha != null)
//				return false;
//		} else if (!altaFecha.equals(other.altaFecha))
//			return false;
//		if (altaUsr == null) {
//			if (other.altaUsr != null)
//				return false;
//		} else if (!altaUsr.equals(other.altaUsr))
//			return false;
//		if (bajaEnBase != other.bajaEnBase)
//			return false;
//		if (bajaFecha == null) {
//			if (other.bajaFecha != null)
//				return false;
//		} else if (!bajaFecha.equals(other.bajaFecha))
//			return false;
//		if (bajaUsr == null) {
//			if (other.bajaUsr != null)
//				return false;
//		} else if (!bajaUsr.equals(other.bajaUsr))
//			return false;
//		if (fechaVto == null) {
//			if (other.fechaVto != null)
//				return false;
//		} else if (!fechaVto.equals(other.fechaVto))
//			return false;
//		if (idMatricula != other.idMatricula)
//			return false;
//		if (modiFecha == null) {
//			if (other.modiFecha != null)
//				return false;
//		} else if (!modiFecha.equals(other.modiFecha))
//			return false;
//		if (modiUsr == null) {
//			if (other.modiUsr != null)
//				return false;
//		} else if (!modiUsr.equals(other.modiUsr))
//			return false;
//		if (numero != other.numero)
//			return false;
//		if (presentaCopia != other.presentaCopia)
//			return false;
//		if (provincia != other.provincia)
//			return false;
//		if (tipo == null) {
//			if (other.tipo != null)
//				return false;
//		} else if (!tipo.equals(other.tipo))
//			return false;
//		return true;
//	}
}
