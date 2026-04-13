package ar.com.ospim.novedades.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class NovedadPadronConsolidadoAltas {
	
	
	private int idNovedad ;
	private String cuilTitular;
	private Integer inte;
	private String codigoMovimiento;
	private String sucu;
	private String apellido;
	private String nombre;
	private String tipoDocumento;
	private String numeroDocumento;
	private String sexo;
	private String cuit;
	private Date fechaNacimiento;
	private Date presSsaludFecha;
	private int EstadoCivil;
	private int nacionalidad;
	private int parentesco;
	private int seccional;
	private String numero;
	private String observaciones;
	private int discapacitado;
	private String tipoDomicilio;
	private String calle;
	private String piso;
	private String depto;
	private String oficina;
	private String codigoPostal;
	private String barrio;
	private String telefono;
	private String observacionesDom;
	private int provincia;
	private int localidad;
	private Date procesoFecha;
	private String procesoUsr ;
	private Date vigenFecha;
	private Date bajaFecha;
	private int idMotivo;
	private int idRevista;
	private int idCategoria;
	private Date idOspimBajaFecha;
	private Date idUomaBajaFecha;
	private Date idAmtimaBajaFecha;
	private String codAreaTelefono;
	private String codAreaCelular;
	private String celular;
	private int censo2013;
	private String codAreaTelLaboral;
	private String telLaboral;
	private String email;
	private int tieneAntecJudiciales;
	private int clientePreferencial;
	private String proyecto;
	private int idPlan;
	private String idTercerizadora;
	private int planes_laborales;
	private int continuidad;
	private String estadoCivilDesc;
	private String motivoBajaDesc;
	private String revistaDesc;
	private String categoria;
	private String nacionalidadDesc;
	private String parentescoDesc;
	private String provinciaDesc;
	private String localidadDesc;
	private String planDesc;
	private String tercerizadoraDesc;
	private String razonSocial;
	
	
	public int getIdNovedad() {
		return idNovedad;
	}
	public void setIdNovedad(int idNovedad) {
		this.idNovedad = idNovedad;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}
	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}
	public String getSucu() {
		return sucu;
	}
	public void setSucu(String sucu) {
		this.sucu = sucu;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public int getEstadoCivil() {
		return EstadoCivil;
	}
	public void setEstadoCivil(int estadoCivil) {
		EstadoCivil = estadoCivil;
	}
	public int getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(int nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public int getParentesco() {
		return parentesco;
	}
	public void setParentesco(int parentesco) {
		this.parentesco = parentesco;
	}
	public int getSeccional() {
		return seccional;
	}
	public void setSeccional(int seccional) {
		this.seccional = seccional;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public int getDiscapacitado() {
		return discapacitado;
	}
	public void setDiscapacitado(int discapacitado) {
		this.discapacitado = discapacitado;
	}
	public String getTipoDomicilio() {
		return tipoDomicilio;
	}
	public void setTipoDomicilio(String tipoDomicilio) {
		this.tipoDomicilio = tipoDomicilio;
	}
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getDepto() {
		return depto;
	}
	public void setDepto(String depto) {
		this.depto = depto;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getObservacionesDom() {
		return observacionesDom;
	}
	public void setObservacionesDom(String observacionesDom) {
		this.observacionesDom = observacionesDom;
	}
	public int getProvincia() {
		return provincia;
	}
	public void setProvincia(int provincia) {
		this.provincia = provincia;
	}
	public int getLocalidad() {
		return localidad;
	}
	public void setLocalidad(int localidad) {
		this.localidad = localidad;
	}
	public Date getProcesoFecha() {
		return procesoFecha;
	}
	public void setProcesoFecha(Date procesoFecha) {
		this.procesoFecha = procesoFecha;
	}
	public String getProcesoUsr() {
		return procesoUsr;
	}
	public void setProcesoUsr(String procesoUsr) {
		this.procesoUsr = procesoUsr;
	}
	public Date getVigenFecha() {
		return vigenFecha;
	}
	public void setVigenFecha(Date vigenFecha) {
		this.vigenFecha = vigenFecha;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public int getIdMotivo() {
		return idMotivo;
	}
	public void setIdMotivo(int idMotivo) {
		this.idMotivo = idMotivo;
	}
	public int getIdRevista() {
		return idRevista;
	}
	public void setIdRevista(int idRevista) {
		this.idRevista = idRevista;
	}
	public int getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}
	public Date getIdOspimBajaFecha() {
		return idOspimBajaFecha;
	}
	public void setIdOspimBajaFecha(Date idOspimBajaFecha) {
		this.idOspimBajaFecha = idOspimBajaFecha;
	}
	public Date getIdUomaBajaFecha() {
		return idUomaBajaFecha;
	}
	public void setIdUomaBajaFecha(Date idUomaBajaFecha) {
		this.idUomaBajaFecha = idUomaBajaFecha;
	}
	public Date getIdAmtimaBajaFecha() {
		return idAmtimaBajaFecha;
	}
	public void setIdAmtimaBajaFecha(Date idAmtimaBajaFecha) {
		this.idAmtimaBajaFecha = idAmtimaBajaFecha;
	}
	public String getCodAreaTelefono() {
		return codAreaTelefono;
	}
	public void setCodAreaTelefono(String codAreaTelefono) {
		this.codAreaTelefono = codAreaTelefono;
	}
	public String getCodAreaCelular() {
		return codAreaCelular;
	}
	public void setCodAreaCelular(String codAreaCelular) {
		this.codAreaCelular = codAreaCelular;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public int getCenso2013() {
		return censo2013;
	}
	public void setCenso2013(int censo2013) {
		this.censo2013 = censo2013;
	}
	public String getCodAreaTelLaboral() {
		return codAreaTelLaboral;
	}
	public void setCodAreaTelLaboral(String codAreaTelLaboral) {
		this.codAreaTelLaboral = codAreaTelLaboral;
	}
	public String getTelLaboral() {
		return telLaboral;
	}
	public void setTelLaboral(String telLaboral) {
		this.telLaboral = telLaboral;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getTieneAntecJudiciales() {
		return tieneAntecJudiciales;
	}
	public void setTieneAntecJudiciales(int tieneAntecJudiciales) {
		this.tieneAntecJudiciales = tieneAntecJudiciales;
	}
	public int getClientePreferencial() {
		return clientePreferencial;
	}
	public void setClientePreferencial(int clientePreferencial) {
		this.clientePreferencial = clientePreferencial;
	}
	public String getProyecto() {
		return proyecto;
	}
	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	public int getIdPlan() {
		return idPlan;
	}
	public void setIdPlan(int idPlan) {
		this.idPlan = idPlan;
	}
	public String getIdTercerizadora() {
		return idTercerizadora;
	}
	public void setIdTercerizadora(String idTercerizadora) {
		this.idTercerizadora = idTercerizadora;
	}
	public int getPlanes_laborales() {
		return planes_laborales;
	}
	public void setPlanes_laborales(int planes_laborales) {
		this.planes_laborales = planes_laborales;
	}
	public int getContinuidad() {
		return continuidad;
	}
	public void setContinuidad(int continuidad) {
		this.continuidad = continuidad;
	}
	
	public Date getPresSsaludFecha() {
		return presSsaludFecha;
	}
	public void setPresSsaludFecha(Date presSsaludFecha) {
		this.presSsaludFecha = presSsaludFecha;
	}
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getEstadoCivilDesc() {
		return estadoCivilDesc;
	}
	public void setEstadoCivilDesc(String estadoCivilDesc) {
		this.estadoCivilDesc = estadoCivilDesc;
	}
	
	public String getMotivoBajaDesc() {
		return motivoBajaDesc;
	}
	public void setMotivoBajaDesc(String motivoBajaDesc) {
		this.motivoBajaDesc = motivoBajaDesc;
	}
	
	
	public String getRevistaDesc() {
		return revistaDesc;
	}
	public void setRevistaDesc(String revistaDesc) {
		this.revistaDesc = revistaDesc;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getNacionalidadDesc() {
		return nacionalidadDesc;
	}
	public void setNacionalidadDesc(String nacionalidadDesc) {
		this.nacionalidadDesc = nacionalidadDesc;
	}
	public String getParentescoDesc() {
		return parentescoDesc;
	}
	public void setParentescoDesc(String parentescoDesc) {
		this.parentescoDesc = parentescoDesc;
	}
	public String getProvinciaDesc() {
		return provinciaDesc;
	}
	public void setProvinciaDesc(String provinciaDesc) {
		this.provinciaDesc = provinciaDesc;
	}
	public String getLocalidadDesc() {
		return localidadDesc;
	}
	public void setLocalidadDesc(String localidadDesc) {
		this.localidadDesc = localidadDesc;
	}
	public String getPlanDesc() {
		return planDesc;
	}
	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}
	public String getTercerizadoraDesc() {
		return tercerizadoraDesc;
	}
	public void setTercerizadoraDesc(String tercerizadoraDesc) {
		this.tercerizadoraDesc = tercerizadoraDesc;
	}
	
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	public static NovedadPadronConsolidadoAltas getMapping(ResultSet rs) throws SQLException{
		
		NovedadPadronConsolidadoAltas nov = new NovedadPadronConsolidadoAltas();
		
		nov.setIdNovedad(rs.getInt( "id_novedad"));
		nov.setCodigoMovimiento(rs.getString("codigo_movimiento"));
		nov.setProcesoFecha(rs.getDate("periodo_novedad"));
		nov.setInte(rs.getInt("inte"));
		nov.setCuilTitular(rs.getString("cuil_titular"));	
		nov.setVigenFecha(rs.getDate("vigen_fecha"));
		nov.setBajaFecha(rs.getDate("baja_fecha"));
		nov.setIdMotivo(rs.getInt("id_motivo"));
		nov.setIdRevista(rs.getInt("id_revista"));
		nov.setIdCategoria(rs.getInt("id_categoria"));
		nov.setApellido(rs.getString("apellido"));
		nov.setNombre(rs.getString("nombre"));
		nov.setSexo(rs.getString("sexo"));
		nov.setCuit(rs.getString("cuit"));
		nov.setFechaNacimiento(rs.getDate("naci_fecha"));
		nov.setEstadoCivil(rs.getInt("civil_esta"));
		nov.setNacionalidad(rs.getInt("nacionalidad"));
		nov.setParentesco(rs.getInt("parentesco"));
		nov.setSeccional(rs.getInt("id_seccional"));
		nov.setObservaciones(rs.getString("observaciones"));
		nov.setPresSsaludFecha(rs.getDate("pres_ssalud_fecha"));
		nov.setProcesoUsr(rs.getString("alta_usr"));
		nov.setDiscapacitado(rs.getInt("discapacitado"));
		nov.setNumeroDocumento(rs.getString("docu_numero"));
		nov.setTipoDocumento(rs.getString("documento_tipo"));
		nov.setCalle(rs.getString("calle"));
		nov.setPiso(rs.getString("piso"));
		nov.setDepto(rs.getString("depto"));
		nov.setOficina(rs.getString("oficina"));
		nov.setCodigoPostal(rs.getString("postal_codi"));
		nov.setBarrio(rs.getString("barrio"));
		nov.setTelefono(rs.getString("telefono"));
		nov.setObservacionesDom(rs.getString("observaciones_dom"));
		nov.setProvincia(rs.getInt("provincia"));
		nov.setLocalidad(rs.getInt("localidad"));
		nov.setNumero(rs.getString("numero"));
		nov.setIdOspimBajaFecha(rs.getDate("id_ospim_baja_fecha"));
		nov.setIdAmtimaBajaFecha(rs.getDate("id_amtima_baja_fecha"));
		nov.setIdUomaBajaFecha(rs.getDate("id_uoma_baja_fecha"));
		nov.setCodAreaTelefono(rs.getString("cod_area_telefono"));
		nov.setCodAreaCelular(rs.getString("cod_area_celular"));
		nov.setCelular(rs.getString("celular"));
		nov.setCenso2013(rs.getInt("censo2013"));
		nov.setEmail(rs.getString("email"));
		nov.setTieneAntecJudiciales(rs.getInt("tiene_antec_judiciales"));
		nov.setClientePreferencial(rs.getInt("cliente_preferencial"));
		nov.setProyecto(rs.getString("proyecto"));
		nov.setIdPlan(rs.getInt("id_plan"));
		nov.setIdTercerizadora(rs.getString("id_tercerizadora"));
		nov.setPlanes_laborales(rs.getInt("planes_laborales"));
		nov.setContinuidad(rs.getInt("continuidad"));
		nov.setEstadoCivilDesc(rs.getString("civil_esta_desc"));
		nov.setMotivoBajaDesc(rs.getString("motivo_baja_desc"));
		nov.setRevistaDesc(rs.getString("revista_desc"));
		nov.setCategoria(rs.getString("categoria"));
		nov.setNacionalidadDesc(rs.getString("nacionalidad_desc"));
		nov.setParentescoDesc(rs.getString("parentesco_desc"));
		nov.setProvinciaDesc(rs.getString("provincia_desc"));
		nov.setLocalidadDesc(rs.getString("localidad_desc"));
		nov.setPlanDesc(rs.getString("plan_desc"));
		nov.setTercerizadoraDesc(rs.getString("tercerizadora_desc"));
		nov.setRazonSocial(rs.getString("razon_soc"));
		
		return nov;
	}


	

}
