package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NovedadPadronConsolidado implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6963448643559872116L;
	
	
	private Integer id;
	private Integer idProceso;
	private Integer codOOSS;
	private String cuitEmpleador;
	private String cuilTitular;
	private Integer codigoParentesco;
	private String cuil;
	private String documentoTipo;
	private Integer documentoNumero;
	private String apellidoNombre;
	private String sexo;
	private Integer estadoCivil;
	private Date fechaNacimiento;
	private Integer nacionalidad;
	private String calle;
	private String numeroPuerta;
	private String piso;
	private String departamento;
	private String localidad;
	private String codigoPostal;
	private Integer provincia;
	private Integer tipoDomicilio;
	private String telefono;
	private Integer situacionRevista;
	private Integer incapacidad;
	private Integer tipoBeneficiarioTitular;
	private Date fechaAltaOOSS;
	private Date fechaCierrePresentacion;
	private String cuilInformadoPorOtraObraSocial;
	private String verificacionCUIL;
	private String tipoBeneficiarioSegunSIJP;
	private String CUITSegunSIJP;
	private Integer OSSegunSIJP;
	private String ultimoPeriodoInfomadoSIJP;
	private String obrasSocialOpcionVigente;
	private String  periodoOpcionAnterior;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCodOOSS() {
		return codOOSS;
	}
	public void setCodOOSS(Integer codOOSS) {
		this.codOOSS = codOOSS;
	}
	public String getCuitEmpleador() {
		return cuitEmpleador;
	}
	public void setCuitEmpleador(String cuitEmpleador) {
		this.cuitEmpleador = cuitEmpleador;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public Integer getCodigoParentesco() {
		return codigoParentesco;
	}
	public void setCodigoParentesco(Integer codigoParentesco) {
		this.codigoParentesco = codigoParentesco;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getDocumentoTipo() {
		return documentoTipo;
	}
	public void setDocumento_tipo(String documentoTipo) {
		this.documentoTipo = documentoTipo;
	}
	public Integer getDocumentoNumero() {
		return documentoNumero;
	}
	public void setDocumentoNumero(Integer documentoNumero) {
		this.documentoNumero = documentoNumero;
	}
	public String getApellidoNombre() {
		return apellidoNombre;
	}
	public void setApellidoNombre(String apellidoNombre) {
		this.apellidoNombre = apellidoNombre;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Integer getEstadoCivil() {
		return estadoCivil;
	}
	public void setEstadoCivil(Integer estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public Integer getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(Integer nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getNumeroPuerta() {
		return numeroPuerta;
	}
	public void setNumeroPuerta(String numeroPuerta) {
		this.numeroPuerta = numeroPuerta;
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
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	public Integer getProvincia() {
		return provincia;
	}
	public void setProvincia(Integer provincia) {
		this.provincia = provincia;
	}
	public Integer getTipoDomicilio() {
		return tipoDomicilio;
	}
	public void setTipoDomicilio(Integer tipoDomicilio) {
		this.tipoDomicilio = tipoDomicilio;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public Integer getSituacionRevista() {
		return situacionRevista;
	}
	public void setSituacionRevista(Integer situacionRevista) {
		this.situacionRevista = situacionRevista;
	}
	public Integer getIncapacidad() {
		return incapacidad;
	}
	public void setIncapacidad(Integer incapacidad) {
		this.incapacidad = incapacidad;
	}
	public Integer getTipoBeneficiarioTitular() {
		return tipoBeneficiarioTitular;
	}
	public void setTipoBeneficiarioTitular(Integer tipoBeneficiarioTitular) {
		this.tipoBeneficiarioTitular = tipoBeneficiarioTitular;
	}
	
	public Date getFechaAltaOOSS() {
		return fechaAltaOOSS;
	}
	public void setFechaAltaOOSS(Date fechaAltaOOSS) {
		this.fechaAltaOOSS = fechaAltaOOSS;
	}
	
	public Date getFechaCierrePresentacion() {
		return fechaCierrePresentacion;
	}
	public void setFechaCierrePresentacion(Date fechaCierrePresentacion) {
		this.fechaCierrePresentacion = fechaCierrePresentacion;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public Date getModiFecha() {
		return modiFecha;
	}
	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}
	public String getModiUsr() {
		return modiUsr;
	}
	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public NovedadPadronConsolidado() {
		super();
	}	
	
	
	private static Date convertDate(String fecha) {
		SimpleDateFormat formatoDeFechas = null;

		Date fechaArchivo = null;

		if (fecha.length() == 7) {
			formatoDeFechas = new SimpleDateFormat("dMMyyyy");
		} else {
			formatoDeFechas = new SimpleDateFormat("ddMMyyyy");
		}
		try {
			fechaArchivo = formatoDeFechas.parse(fecha);
		} catch (Exception e) {
			fechaArchivo = null;
		}

		return fechaArchivo;

	}
	
	public NovedadPadronConsolidado(String line) {
		super();
		String[] linea = line.split("\\|");

		this.codOOSS = linea[0] != null && linea[0].trim().length() > 0 ? Integer.parseInt(linea[0].trim()) : 0;
		this.cuitEmpleador = linea[1].trim();
		this.cuilTitular = linea[2].trim();
		this.codigoParentesco = linea[3] != null && linea[3].trim().length() > 0 ? Integer.parseInt(linea[3].trim()) : 0;
		this.cuil = linea[4].trim();
		this.documentoTipo = linea[5].trim();
		this.documentoNumero = linea[6] != null && linea[6].trim().length() > 0 ? Integer.parseInt(linea[6].trim()) : 0;
		this.apellidoNombre = linea[7].trim();
		this.sexo = linea[8].trim();
		this.estadoCivil = linea[9] != null && linea[9].trim().length() > 0 ? Integer.parseInt(linea[9].trim()) : 0;
		this.fechaNacimiento = linea[10] != null && linea[10].trim().length() > 0 ? convertDate(linea[10].trim()) : null;
		this.nacionalidad = linea[11] != null && linea[11].trim().length() > 0 ? Integer.parseInt(linea[11].trim()) : 0;
		this.calle = linea[12].trim();
		this.numeroPuerta = linea[13].trim();
		this.piso = linea[14].trim();
		this.departamento = linea[15].trim();
		this.localidad = linea[16].trim();
		this.codigoPostal = linea[17].trim();
		this.provincia = linea[18] != null && linea[18].trim().length() > 0 ? Integer.parseInt(linea[18].trim()) : 0;
		this.tipoDomicilio = linea[19] != null && linea[19].trim().length() > 0 ? Integer.parseInt(linea[19].trim()) : 0;
		this.telefono = linea[20].trim();
		this.situacionRevista = linea[21] != null && linea[21].trim().length() > 0 ? Integer.parseInt(linea[21].trim()) : 0;
		this.incapacidad = linea[22] != null && linea[22].trim().length() > 0 ? Integer.parseInt(linea[22].trim()) : 0;
		this.tipoBeneficiarioTitular = linea[23] != null && linea[23].trim().length() > 0 ? Integer.parseInt(linea[23].trim()) : 0;
		this.fechaAltaOOSS = linea[24] != null && linea[24].trim().length() > 0 ? convertDate(linea[24].trim()) : null;
		this.fechaCierrePresentacion = linea[25] != null && linea[25].trim().length() > 0 ? convertDate(linea[25].trim()) : null;
		this.verificacionCUIL =linea[26].trim();
		this.cuilInformadoPorOtraObraSocial =linea[27].trim();
		this.tipoBeneficiarioSegunSIJP = linea[28].trim();
		this.CUITSegunSIJP = linea[29].trim();
		this.OSSegunSIJP = linea[30] != null && linea[30].trim().length() > 0 ? Integer.parseInt(linea[30].trim()) : 0;
		this.ultimoPeriodoInfomadoSIJP = linea[31].trim();
		this.obrasSocialOpcionVigente =  linea[32].trim();
		this.periodoOpcionAnterior = linea[32].trim();
		
	}
	
	public static NovedadPadronConsolidado getMapping(String prefix, ResultSet rs) throws SQLException{
		NovedadPadronConsolidado nov = new NovedadPadronConsolidado();
		
		nov.setId(rs.getInt(prefix + "id"));
		nov.setIdProceso(rs.getInt(prefix + "id_proceso"));
		nov.setCodOOSS(rs.getInt(prefix + "codigo_ooss"));
		nov.setCuitEmpleador(rs.getString(prefix + "cuit_empleador"));
		nov.setCuilTitular(rs.getString(prefix + "cuil_titular"));
		nov.setCodigoParentesco(rs.getInt(prefix + "codigo_parentesco"));
		nov.setCuil(rs.getString(prefix + "cuil"));
		nov.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		nov.setDocumentoNumero(rs.getInt(prefix + "documento_numero"));
		nov.setApellidoNombre(rs.getString(prefix + "apellido_nombre"));
		nov.setSexo(rs.getString(prefix + "sexo"));
		nov.setEstadoCivil(rs.getInt(prefix + "estado_civil"));
		nov.setFechaNacimiento(rs.getDate(prefix + "fecha_nacimiento"));
		nov.setNacionalidad(rs.getInt(prefix + "nacionalidad"));
		nov.setCalle(rs.getString(prefix + "calle"));
		nov.setNumeroPuerta(rs.getString(prefix + "numero_puerta"));
		nov.setPiso(rs.getString(prefix + "piso"));
		nov.setDepartamento(rs.getString(prefix + "departamento"));
		nov.setLocalidad(rs.getString(prefix + "localidad"));
		nov.setCodigoPostal(rs.getString(prefix + "codigo_postal"));
		nov.setProvincia(rs.getInt(prefix + "provincia"));
		nov.setTipoDomicilio(rs.getInt(prefix + "tipo_domicilio"));
		nov.setTelefono(rs.getString(prefix + "telefono"));
		nov.setSituacionRevista(rs.getInt(prefix + "situacion_revista"));
		nov.setIncapacidad(rs.getInt(prefix + "incapacidad"));
		nov.setTipoBeneficiarioTitular(rs.getInt(prefix + "tipo_beneficiario_titular"));
		nov.setFechaAltaOOSS(rs.getDate(prefix + "fecha_alta_en_ooss"));
		nov.setFechaCierrePresentacion(rs.getDate(prefix + "fecha_cierre_presentacion"));
		nov.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		nov.setAltaUsr(rs.getString(prefix + "alta_usr"));
		nov.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		nov.setModiUsr(rs.getString(prefix + "modi_usr"));
		nov.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		nov.setBajaUsr(rs.getString(prefix + "baja_usr"));

		return nov;
	}
	public Integer getIdProceso() {
		return idProceso;
	}
	public void setIdProceso(Integer idProceso) {
		this.idProceso = idProceso;
	}
	public String getCuilInformadoPorOtraObraSocial() {
		return cuilInformadoPorOtraObraSocial;
	}
	public void setCuilInformadoPorOtraObraSocial(String cuilInformadoPorOtraObraSocial) {
		this.cuilInformadoPorOtraObraSocial = cuilInformadoPorOtraObraSocial;
	}
	public String getTipoBeneficiarioSegunSIJP() {
		return tipoBeneficiarioSegunSIJP;
	}
	public void setTipoBeneficiarioSegunSIJP(String tipoBeneficiarioSegunSIJP) {
		this.tipoBeneficiarioSegunSIJP = tipoBeneficiarioSegunSIJP;
	}
	public String getCUITSegunSIJP() {
		return CUITSegunSIJP;
	}
	public void setCUITSegunSIJP(String cUITSegunSIJP) {
		CUITSegunSIJP = cUITSegunSIJP;
	}
	public Integer getOSSegunSIJP() {
		return OSSegunSIJP;
	}
	public void setOSSegunSIJP(Integer oSSegunSIJP) {
		OSSegunSIJP = oSSegunSIJP;
	}
	public String getUltimoPeriodoInfomadoSIJP() {
		return ultimoPeriodoInfomadoSIJP;
	}
	public void setUltimoPeriodoInfomadoSIJP(String ultimoPeriodoInfomadoSIJP) {
		this.ultimoPeriodoInfomadoSIJP = ultimoPeriodoInfomadoSIJP;
	}
	public String getObrasSocialOpcionVigente() {
		return obrasSocialOpcionVigente;
	}
	public void setObrasSocialOpcionVigente(String obrasSocialOpcionVigente) {
		this.obrasSocialOpcionVigente = obrasSocialOpcionVigente;
	}
	public String getPeriodoOpcionAnterior() {
		return periodoOpcionAnterior;
	}
	public void setPeriodoOpcionAnterior(String periodoOpcionAnterior) {
		this.periodoOpcionAnterior = periodoOpcionAnterior;
	}
	public void setDocumentoTipo(String documentoTipo) {
		this.documentoTipo = documentoTipo;
	}
	public String getVerificacionCUIL() {
		return verificacionCUIL;
	}
	public void setVerificacionCUIL(String verificacionCUIL) {
		this.verificacionCUIL = verificacionCUIL;
	}

}

