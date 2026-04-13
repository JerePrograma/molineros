package ar.com.ospim.autorizaciones.beans;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import java.util.Date;

import com.liferay.portal.kernel.util.Validator;

import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;

//import ar.com.ospim.liquidaciones.beans.ProfesionPrestador.ESTADOS;

public class PatologiaSituacionMedica implements Serializable {
	
	private static final long serialVersionUID = -753007388444001315L;
	private int idSituacionMedica ; // referencia a la pk de tabla afi_situ_medica 
	private int idPatologiaSituMedica; // referencia a la pk de tabla de prestaciones de los reclamos
	private boolean discapacitado;
	private String tipo_situ_medica;
	private Date fechaDesde;
	private Date fechaHasta;
	private Date fechaBaja;
    private String diagnostico; 		
    private String codigoCieDiez ;
	
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	
	public PatologiaSituacionMedica () {
		super();
		}



	public static PatologiaSituacionMedica getMapping(String prefix, ResultSet rs)
			throws SQLException {
		
		PatologiaSituacionMedica patologiasSituacionMedica = new PatologiaSituacionMedica();
		
		patologiasSituacionMedica.setIdSituacionMedica(rs.getInt (prefix + "id"));
		patologiasSituacionMedica.setDiscapacitado(rs.getBoolean(prefix + "discapacitado"));		 
		patologiasSituacionMedica.setFechaBaja(rs.getDate (prefix + "baja_fecha"));
		patologiasSituacionMedica.setFechaDesde(rs.getDate (prefix + "fecha_desde"));
		patologiasSituacionMedica.setFechaHasta(rs.getDate (prefix + "fecha_hasta"));
		patologiasSituacionMedica.setTipo_situ_medica(rs.getString (prefix + "tipo_situ_medica"));
		patologiasSituacionMedica.setDiagnostico(rs.getString (prefix + "diagnostico"));
		patologiasSituacionMedica.setCodigoCieDiez(rs.getString (prefix + "cie_diez"));
		
		
		if ( rs.getString(prefix + "baja_fecha") != null) {
			patologiasSituacionMedica.setEstado( PatologiaSituacionMedica.ESTADOS.BAJA  );		
			}			
		
		return patologiasSituacionMedica ;
	}
	
	

	public int getIdSituacionMedica() {
		return idSituacionMedica;
	}

	public void setIdSituacionMedica(int idSituacionMedica) {
		this.idSituacionMedica = idSituacionMedica;
	}

	public int getIdPatologiaSituMedica() {
		return idPatologiaSituMedica;
	}

	public void setIdPatologiaSituMedica(int idPatologiaSituMedica) {
		this.idPatologiaSituMedica = idPatologiaSituMedica;
	}

	public boolean isDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(boolean discapacitado) {
		this.discapacitado = discapacitado;
	}

	public String getTipo_situ_medica() {
		return tipo_situ_medica;
	}

	public void setTipo_situ_medica(String tipo_situ_medica) {
		this.tipo_situ_medica = tipo_situ_medica;
	}

	public String getCodigoCieDiez (){
		return this.codigoCieDiez;
	}
	
	public void  setCodigoCieDiez(String codigoCieDiez){
		this.codigoCieDiez=codigoCieDiez;
	}
	
	public String getDiagnostico (){
		return this.diagnostico;
	}
	
	public void  setDiagnostico (String diagnostico){
		this.diagnostico=diagnostico;
	}
	
	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	
	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "" ; 
		//ProfesionPrestador [id_prestacion =" + idPrestacion 	+ ", descripcion=" + descripcion + ", frecuencia="	+ frecuencia + ", observaciones="				+ observaciones + "]";
	}

		
	
	public int getIdprestacionReclamo () {
		return idPatologiaSituMedica;
	}

	public void setIdprestacionReclamo (int idPrestacionReclamo ) {
		this.idPatologiaSituMedica= idPrestacionReclamo ;
	}
	
	public int getIdreclamoprestacional() {
		return idSituacionMedica;
	}

	public void setIdreclamoprestacional(int idReclamoPrestacional) {
		this.idSituacionMedica = idReclamoPrestacional;
	}
	

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		PrestacionesReclamo other = (PrestacionesReclamo) obj;
//		if (idSituacionMedica != other.idRegistro)
//			return false;
//		return true;
//	}
	

	
}


