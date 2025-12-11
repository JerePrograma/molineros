package ar.com.ospim.procesaArchivos.beans.opcionesss;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetalleBajasOpcionesSS implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String tipoExportacion;	
	private int nroFormulario;	
	private String cuil;
	private String apeNom;
	private String calle;
	private String numero;
	private String piso;
	private String departamento;
	private String telParticular;
	private String localidad;
	private String codPostal;
	private String provincia;	
	private String cuit;
	private String razonSoc;
	private String noSe;
	private Date fechaElecc;
	private int osSelecci;	
	
	public DetalleBajasOpcionesSS(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String[] linea = line.split("\\|");

		this.tipoExportacion = linea[0].trim();
		this.nroFormulario = linea[1] != null && linea[1].trim().length() > 0 ? Integer
				.parseInt(linea[1].trim()) : 0;
		this.cuil = linea[2].trim();		
		this.apeNom = linea[3].trim();		
		this.calle = linea[4].trim();
		this.numero = linea[5].trim();
		this.piso = linea[6] .trim();
		this.departamento = linea[7].trim();
		this.telParticular = linea[8].trim();
		this.localidad = linea[9].trim();		
		this.codPostal = linea[10].trim();
		this.provincia = linea[11].trim();
		this.cuit = linea[12].trim();
		this.razonSoc = linea[13].trim();
		this.noSe = linea[14].trim();		
		this.fechaElecc = linea[15] != null && linea[15].trim().length() > 0 ? sdf
				.parse(linea[15].trim()) : null;
		this.osSelecci = linea[16] != null && linea[16].trim().length() > 0 ? Integer
						.parseInt(linea[16].trim()) : 0;		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipoExportacion() {
		return tipoExportacion;
	}

	public void setTipoExportacion(String tipoExportacion) {
		this.tipoExportacion = tipoExportacion;
	}

	public int getNroFormulario() {
		return nroFormulario;
	}

	public void setNroFormulario(int nroFormulario) {
		this.nroFormulario = nroFormulario;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getApeNom() {
		return apeNom;
	}

	public void setApeNom(String apeNom) {
		this.apeNom = apeNom;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getTelParticular() {
		return telParticular;
	}

	public void setTelParticular(String telParticular) {
		this.telParticular = telParticular;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSoc() {
		return razonSoc;
	}

	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}

	public String getNoSe() {
		return noSe;
	}

	public void setNoSe(String noSe) {
		this.noSe = noSe;
	}

	public Date getFechaElecc() {
		return fechaElecc;
	}

	public void setFechaElecc(Date fechaElecc) {
		this.fechaElecc = fechaElecc;
	}

	public int getOsSelecci() {
		return osSelecci;
	}

	public void setOsSelecci(int osSelecci) {
		this.osSelecci = osSelecci;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
