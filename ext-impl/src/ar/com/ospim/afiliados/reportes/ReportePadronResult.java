package ar.com.ospim.afiliados.reportes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReportePadronResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4655183568819242206L;
	private int id_ospim;
	private int id_amtima;
	private int id_uoma;
	private String unifica;
	private Date alta_fecha;
	private int idSeccional;
	private String seccional;
	private String id_tercerizadora;
	private String cuil_titular;
	private String cuil;
	private int inte;
	private int id_parentesco_sss;
	private String parentesco;
	private String apellido;
	private String nombre;
	private String documento_tipo;
	private String docu_numero;
	private Date naci_fecha;
	private String sexo;
	private int id_estado_civil_sss;
	private String civil_esta;
	private int idNacionalidad;
	private int idNacionalidadSSS;
	private String nacionalidad;
	private int idProvincia;
	private int idProvinciaSss;
	private String provincia;
	private int idLocalidad;
	private int idLocalidadSss;
	private String localidad;
	private String postal_codi;
	private String calle;
	private String numero;
	private String piso;
	private String depto;
	private String barrio;
	private String telefono;
	private String email;
	private String categoria;
	private String ramo;
	private int id_plan;
	private String plan;
	private Date ingre_fecha;
	private Date baja_fecha;;
	private String cuit;
	private String razon_soc;
	private Date fecha_ospim;
	private Date fecha_uoma;
	private Date fecha_amtima;	
	private String escala_salarial;
	private String planOmint;
	private String planPrevencion;
	private String farmaciaPrevencion;
	private String discapacitado;
	private Integer idMotivoBaja;
	private String motivoBaja;
	private Date fecha_proceso;
	private int perteneceAlaOrganizacion;
	private int tieneAntecedentesJudiciales;
	private Date fpp;
	private boolean farmaciaAmtima;
    private boolean farmaciaUoma;
    private Date vigenFecha;
    private String codAreaTelefono;
    private String telefono1;
    private String codAreaTelLaboral;
    private String telLaboral;
    private String codAreaCelular;
    private String celular;
    private String proyecto;
    private String obraSocAnterior;
    private int nroSocio;
    private BigDecimal nroCredencial;
    private String unsuscribeEmail;
    private String planTercerizadora;
	private String farmaciaTercerizadora;
	private String copago;
    
	private String planAfiliado;
	private String pmi;
	private String aco;
	
	public static ReportePadronResult getMapping(ResultSet rs)
			throws SQLException {
		
		String telefonosConcatenados = "";
		String telefonoCompleto = " ";
		
		ReportePadronResult res = new ReportePadronResult();
   		res.setId_ospim(rs.getInt("id_ospim"));
		res.setId_amtima(rs.getInt("id_amtima"));
		res.setUnifica(rs.getString("unifica"));
		res.setAlta_fecha(rs.getDate("alta_fecha"));
		res.setIdSeccional(rs.getInt("id_seccional"));
		res.setSeccional(rs.getString("seccional"));
		res.setId_tercerizadora(rs.getString("id_tercerizadora"));
		res.setCuil_titular(rs.getString("cuil_titular"));
		res.setCuil(rs.getString("cuil"));
		res.setInte(rs.getInt("inte"));
		res.setId_parentesco_sss(rs.getInt("id_parentesco_sss"));
		res.setParentesco(rs.getString("parentesco"));
		res.setApellido(rs.getString("apellido"));
		res.setNombre(rs.getString("nombre"));
		res.setDocumento_tipo(rs.getString("documento_tipo"));
		res.setDocu_numero(rs.getString("docu_numero"));
		res.setNaci_fecha(rs.getDate("naci_fecha"));
		res.setSexo(rs.getString("sexo"));
		res.setId_estado_civil_sss(rs.getInt("id_estado_civil_sss"));
		res.setCivil_esta(rs.getString("civil_esta"));
		res.setIdNacionalidad(rs.getInt("id_nacionalidad"));
		res.setIdNacionalidadSSS(rs.getInt("id_nacionalidad_sss"));
		res.setNacionalidad(rs.getString("nacionalidad"));
		res.setIdProvincia(rs.getInt("id_provincia"));
		res.setIdProvinciaSss(rs.getInt("id_provincia_sss"));
		res.setProvincia(rs.getString("provincia"));
		res.setIdLocalidad(rs.getInt("id_localidad"));
		res.setIdLocalidadSss(rs.getInt("id_localidad_sss"));
		res.setLocalidad(rs.getString("localidad"));
		res.setPostal_codi(rs.getString("postal_codi"));
		res.setCalle(rs.getString("calle"));
		res.setNumero(rs.getString("numero"));
		res.setPiso(rs.getString("piso"));
		res.setDepto(rs.getString("depto"));
		res.setBarrio(rs.getString("barrio"));
		res.setEmail(rs.getString("email"));
		res.setCategoria(rs.getString("categoria"));
		res.setRamo(rs.getString("ramo"));
		res.setId_plan(rs.getInt("id_plan"));
		res.setPlan(rs.getString("plan"));
		res.setPlanOmint(rs.getString("plan_omint"));
		res.setPlanPrevencion(rs.getString("plan_prevencion"));
		res.setFarmaciaPrevencion(rs.getString("plan_farmacia"));
		res.setIngre_fecha(rs.getDate("ingre_fecha"));
		res.setBaja_fecha(rs.getDate("baja_fecha"));
		res.setId_uoma(rs.getInt("id_uoma"));
		res.setCuit(rs.getString("cuit"));
		res.setRazon_soc(rs.getString("razon_soc"));
		res.setFecha_ospim(rs.getDate("fecha_ospim"));
		res.setIdMotivoBaja(rs.getInt("id_motivo_baja"));
		res.setMotivoBaja(rs.getString("motivo_baja"));
		res.setFecha_uoma(rs.getDate("fecha_uoma"));
		res.setFecha_amtima(rs.getDate("fecha_amtima"));
		if(rs.getString("escala_salarial")==null) {
			res.setEscala_salarial("");
		} else {
			res.setEscala_salarial(rs.getString("escala_salarial"));			
		}
		res.setDiscapacitado(rs.getString("discapacitado"));
		res.setPerteneceAlaOrganizacion(rs.getInt("cliente_preferencial"));
		res.setTieneAntecedentesJudiciales(rs.getInt("tiene_antecedentes_judiciales"));
		res.setFpp(rs.getDate("fpp"));
		res.setFarmaciaAmtima(rs.getBoolean("farmacia_amtima"));
		res.setFarmaciaUoma(rs.getBoolean("farmacia_uoma"));
		res.setVigenFecha(rs.getDate("vigen_fecha"));
		res.setProyecto(rs.getString("proyecto"));
		res.setObraSocAnterior(rs.getString("obra_soc_anterior"));
	
		try {
			res.setCodAreaTelefono(rs.getString("cod_area_telefono"));
			res.setTelefono1(rs.getString("telefono"));
			
			telefonoCompleto = (res.getCodAreaTelefono()!=null&&!res.getCodAreaTelefono().isEmpty()?(String.format("%05d",Integer.parseInt(res.getCodAreaTelefono()))+"-"):"").concat(res.getTelefono1()!=null&&!res.getTelefono1().isEmpty()?res.getTelefono1():null);
		} catch (Exception e) {
//			logger.error("parseando telefono");
//			logger.error(e);
		}
				
		String telefonoLaboralCompleto = " ";
		try {
			res.setCodAreaTelLaboral(rs.getString("cod_area_tel_laboral"));
			res.setTelLaboral(rs.getString("tel_laboral"));

			telefonoLaboralCompleto = (res.getCodAreaTelLaboral()!=null&&!res.getCodAreaTelLaboral().isEmpty()?(String.format("%05d",Integer.parseInt(res.getCodAreaTelLaboral()))+"-"):"").concat(res.getTelLaboral()!=null&&!res.getTelLaboral().isEmpty()?res.getTelLaboral():null);
		} catch (Exception e) {
//			logger.error("parseando telefono laboral");
//			logger.error(e);
		}
		
		String celularCompleto = " ";
		try {
			res.setCodAreaCelular(rs.getString("cod_area_celular"));
			res.setCelular(rs.getString("celular"));

			celularCompleto = (res.getCodAreaCelular()!=null&&!res.getCodAreaCelular().isEmpty()?(String.format("%05d",Integer.parseInt(res.getCodAreaCelular()))+"-"):"").concat(res.getCelular()!=null&&!res.getCelular().isEmpty()?res.getCelular():null);
		} catch (Exception e) {
//			logger.error("parseando celular");
//			logger.error(e);
		}
		telefonosConcatenados = telefonoCompleto + " " + telefonoLaboralCompleto + " " + celularCompleto;
		res.setTelefono(telefonosConcatenados);
		//res.setNroSocio(rs.getInt("nro_socio"));
		//res.setNroCredencial(rs.getBigDecimal("nro_credencial"));
		res.setUnsuscribeEmail(rs.getString("unsuscribe_email"));
		
		res.setPlanTercerizadora(rs.getString("plan_tercerizadora"));
		res.setFarmaciaTercerizadora(rs.getString("plan_farmacia_tercerizadora"));
		res.setCopago(rs.getString("copago"));
		
		return res;
		
	}

	public static ReportePadronResult getMapping2(ResultSet rs)
			throws SQLException {
		
		ReportePadronResult res = new ReportePadronResult();
   		res.setId_ospim(rs.getInt("id_ospim"));
		res.setId_amtima(rs.getInt("id_amtima"));
		res.setId_uoma(rs.getInt("id_uoma"));
		res.setUnifica(rs.getString("unifica"));
//		res.setAlta_fecha(rs.getDate("alta_fecha"));		
		res.setSeccional(rs.getString("seccional"));
		res.setId_tercerizadora(rs.getString("id_tercerizadora"));
		res.setCuil_titular(rs.getString("cuil_titular"));
		res.setCuil(rs.getString("cuil"));
		res.setInte(rs.getInt("inte"));
		res.setId_parentesco_sss(rs.getInt("id_parentesco_sss"));
		res.setParentesco(rs.getString("parentesco"));
		res.setApellido(rs.getString("apellido"));
		res.setNombre(rs.getString("nombre"));
		res.setDocumento_tipo(rs.getString("documento_tipo"));
		res.setDocu_numero(rs.getString("docu_numero"));
		res.setNaci_fecha(rs.getDate("naci_fecha"));
		res.setSexo(rs.getString("sexo"));
		res.setId_estado_civil_sss(rs.getInt("id_estado_civil_sss"));
		res.setCivil_esta(rs.getString("civil_esta"));
		res.setNacionalidad(rs.getString("nacionalidad"));
		res.setProvincia(rs.getString("provincia"));
		res.setLocalidad(rs.getString("localidad"));
		res.setPostal_codi(rs.getString("postal_codi"));
		res.setCalle(rs.getString("calle"));
		res.setNumero(rs.getString("numero"));
		res.setPiso(rs.getString("piso"));
		res.setDepto(rs.getString("depto"));
		res.setTelefono(rs.getString("telefono"));
		res.setEmail(rs.getString("email"));
		res.setCategoria(rs.getString("categoria"));
		res.setRamo(rs.getString("ramo"));
		res.setId_plan(rs.getInt("id_plan"));
		res.setPlan(rs.getString("plan"));
		res.setPlanOmint(rs.getString("plan_omint"));
		res.setIngre_fecha(rs.getDate("ingre_fecha"));
		res.setBaja_fecha(rs.getDate("baja_fecha"));
		res.setCuit(rs.getString("cuit"));
		res.setRazon_soc(rs.getString("razon_soc"));
		res.setFecha_ospim(rs.getDate("fecha_ospim"));
		res.setFecha_uoma(rs.getDate("fecha_uoma"));
		res.setFecha_amtima(rs.getDate("fecha_amtima"));

		res.setMotivoBaja(rs.getString("motivo_baja"));
		if(rs.getString("escala_salarial")==null) {
			res.setEscala_salarial("");
		} else {
			res.setEscala_salarial(rs.getString("escala_salarial"));			
		}
		res.setDiscapacitado(rs.getString("discapacitado"));
		res.setFecha_proceso(rs.getDate("fecha_proceso"));		
		res.setProyecto(rs.getString("proyecto"));
		res.setObraSocAnterior(rs.getString("obra_soc_anterior"));
		res.setNroSocio(rs.getInt("nro_socio"));
		res.setNroCredencial(rs.getBigDecimal("nro_credencial"));
		res.setUnsuscribeEmail(rs.getString("unsuscribe_email"));
		return res;
	}

	
	public int getId_ospim() {
		return id_ospim;
	}

	public void setId_ospim(int id_ospim) {
		this.id_ospim = id_ospim;
	}

	public int getId_amtima() {
		return id_amtima;
	}

	public void setId_amtima(int id_amtima) {
		this.id_amtima = id_amtima;
	}

	public int getId_uoma() {
		return id_uoma;
	}

	public void setId_uoma(int id_uoma) {
		this.id_uoma = id_uoma;
	}

	public String getUnifica() {
		return unifica;
	}

	public void setUnifica(String unifica) {
		this.unifica = unifica;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public int getIdSeccional() {
		return idSeccional;
	}

	public void setIdSeccional(int idSeccional) {
		this.idSeccional = idSeccional;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public String getId_tercerizadora() {
		return id_tercerizadora;
	}

	public void setId_tercerizadora(String id_tercerizadora) {
		this.id_tercerizadora = id_tercerizadora;
	}

	public String getCuil_titular() {
		return cuil_titular;
	}

	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public int getId_parentesco_sss() {
		return id_parentesco_sss;
	}

	public void setId_parentesco_sss(int id_parentesco_sss) {
		this.id_parentesco_sss = id_parentesco_sss;
	}

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
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

	public String getDocumento_tipo() {
		return documento_tipo;
	}

	public void setDocumento_tipo(String documento_tipo) {
		this.documento_tipo = documento_tipo;
	}

	public String getDocu_numero() {
		return docu_numero;
	}

	public void setDocu_numero(String docu_numero) {
		this.docu_numero = docu_numero;
	}

	public Date getNaci_fecha() {
		return naci_fecha;
	}

	public void setNaci_fecha(Date naci_fecha) {
		this.naci_fecha = naci_fecha;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public int getId_estado_civil_sss() {
		return id_estado_civil_sss;
	}

	public void setId_estado_civil_sss(int id_estado_civil_sss) {
		this.id_estado_civil_sss = id_estado_civil_sss;
	}

	public String getCivil_esta() {
		return civil_esta;
	}

	public void setCivil_esta(String civil_esta) {
		this.civil_esta = civil_esta;
	}

	public int getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(int idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public int getIdNacionalidadSSS() {
		return idNacionalidadSSS;
	}

	public void setIdNacionalidadSSS(int idNacionalidadSSS) {
		this.idNacionalidadSSS = idNacionalidadSSS;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public int getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(int idProvincia) {
		this.idProvincia = idProvincia;
	}

	public int getIdProvinciaSss() {
		return idProvinciaSss;
	}

	public void setIdProvinciaSss(int idProvinciaSss) {
		this.idProvinciaSss = idProvinciaSss;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public int getIdLocalidad() {
		return idLocalidad;
	}

	public void setIdLocalidad(int idLocalidad) {
		this.idLocalidad = idLocalidad;
	}

	public int getIdLocalidadSss() {
		return idLocalidadSss;
	}

	public void setIdLocalidadSss(int idLocalidadSss) {
		this.idLocalidadSss = idLocalidadSss;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getPostal_codi() {
		return postal_codi;
	}

	public void setPostal_codi(String postal_codi) {
		this.postal_codi = postal_codi;
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

	public String getDepto() {
		return depto;
	}

	public void setDepto(String depto) {
		this.depto = depto;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getRamo() {
		return ramo;
	}

	public void setRamo(String ramo) {
		this.ramo = ramo;
	}

	public int getId_plan() {
		return id_plan;
	}

	public void setId_plan(int id_plan) {
		this.id_plan = id_plan;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public Date getIngre_fecha() {
		return ingre_fecha;
	}

	public void setIngre_fecha(Date ingre_fecha) {
		this.ingre_fecha = ingre_fecha;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razon_soc) {
		this.razon_soc = razon_soc;
	}

	public Date getFecha_ospim() {
		return fecha_ospim;
	}

	public void setFecha_ospim(Date fecha_ospim) {
		this.fecha_ospim = fecha_ospim;
	}

	public Date getFecha_uoma() {
		return fecha_uoma;
	}

	public void setFecha_uoma(Date fecha_uoma) {
		this.fecha_uoma = fecha_uoma;
	}

	public Date getFecha_amtima() {
		return fecha_amtima;
	}

	public void setFecha_amtima(Date fecha_amtima) {
		this.fecha_amtima = fecha_amtima;
	}

	public String getEscala_salarial() {
		return escala_salarial;
	}

	public void setEscala_salarial(String escala_salarial) {
		this.escala_salarial = escala_salarial;
	}

	public String getPlanOmint() {
		return planOmint;
	}

	public void setPlanOmint(String planOmint) {
		this.planOmint = planOmint;
	}

	public String getPlanPrevencion() {
		return planPrevencion;
	}

	public void setPlanPrevencion(String planPrevencion) {
		this.planPrevencion = planPrevencion;
	}

	public String getFarmaciaPrevencion() {
		return farmaciaPrevencion;
	}

	public void setFarmaciaPrevencion(String farmaciaPrevencion) {
		this.farmaciaPrevencion = farmaciaPrevencion;
	}

	public String getDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}

	public Integer getIdMotivoBaja() {
		return idMotivoBaja;
	}

	public void setIdMotivoBaja(Integer idMotivoBaja) {
		this.idMotivoBaja = idMotivoBaja;
	}

	public String getMotivoBaja() {
		return motivoBaja;
	}

	public void setMotivoBaja(String motivoBaja) {
		this.motivoBaja = motivoBaja;
	}

	public Date getFecha_proceso() {
		return fecha_proceso;
	}

	public void setFecha_proceso(Date fecha_proceso) {
		this.fecha_proceso = fecha_proceso;
	}

	public int getPerteneceAlaOrganizacion() {
		return perteneceAlaOrganizacion;
	}

	public void setPerteneceAlaOrganizacion(int perteneceAlaOrganizacion) {
		this.perteneceAlaOrganizacion = perteneceAlaOrganizacion;
	}

	public Date getFpp() {
		return fpp;
	}

	public void setFpp(Date fpp) {
		this.fpp = fpp;
	}

	public boolean isFarmaciaAmtima() {
		return farmaciaAmtima;
	}

	public void setFarmaciaAmtima(boolean farmaciaAmtima) {
		this.farmaciaAmtima = farmaciaAmtima;
	}

	public boolean isFarmaciaUoma() {
		return farmaciaUoma;
	}

	public void setFarmaciaUoma(boolean farmaciaUoma) {
		this.farmaciaUoma = farmaciaUoma;
	}

	public Date getVigenFecha() {
		return vigenFecha;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setVigenFecha(Date vigenFecha) {
		this.vigenFecha = vigenFecha;
	}

	public String getCodAreaTelefono() {
		return codAreaTelefono;
	}

	public void setCodAreaTelefono(String codAreaTelefono) {
		this.codAreaTelefono = codAreaTelefono;
	}

	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
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

	public String getCodAreaCelular() {
		return codAreaCelular;
	}

	public  void setCodAreaCelular(String codAreaCelular) {
		this.codAreaCelular = codAreaCelular;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
	
	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public String getObraSocAnterior() {
		return obraSocAnterior;
	}

	public void setObraSocAnterior(String obraSocAnterior) {
		this.obraSocAnterior = obraSocAnterior;
	}

	public int getTieneAntecedentesJudiciales() {
		return tieneAntecedentesJudiciales;
	}

	public void setTieneAntecedentesJudiciales(int tieneAntecedentesJudiciales) {
		this.tieneAntecedentesJudiciales = tieneAntecedentesJudiciales;
	}

	public int getNroSocio() {
		return nroSocio;
	}

	public void setNroSocio(int nroSocio) {
		this.nroSocio = nroSocio;
	}

	public BigDecimal getNroCredencial() {
		return nroCredencial;
	}

	public void setNroCredencial(BigDecimal nroCredencial) {
		this.nroCredencial = nroCredencial;
	}

	public String getUnsuscribeEmail() {
		return unsuscribeEmail;
	}

	public void setUnsuscribeEmail(String unsuscribeEmail) {
		this.unsuscribeEmail = unsuscribeEmail;
	}

	public String getPlanTercerizadora() {
		return planTercerizadora;
	}

	public void setPlanTercerizadora(String planTercerizadora) {
		this.planTercerizadora = planTercerizadora;
	}

	public String getFarmaciaTercerizadora() {
		return farmaciaTercerizadora;
	}

	public void setFarmaciaTercerizadora(String farmaciaTercerizadora) {
		this.farmaciaTercerizadora = farmaciaTercerizadora;
	}
	
	public String getCopago() {
		return copago;
	}

	public void setCopago(String copago) {
		this.copago = copago;
	}
	
	public String getPlanAfiliado() {
	    return planAfiliado;
	}

	public void setPlanAfiliado(String planAfiliado) {
	    this.planAfiliado = planAfiliado;
	}

	public String getPmi() {
	    return pmi;
	}

	public void setPmi(String pmi) {
	    this.pmi = pmi;
	}

	public String getAco() {
	    return aco;
	}

	public void setAco(String aco) {
	    this.aco = aco;
	}
	
	public static ReportePadronResult getMappingAdmifarm(ResultSet rs)
	        throws SQLException {

	    ReportePadronResult res = getMapping(rs);

	    res.setPlanAfiliado(res.getPlan());
	    res.setPmi(res.getPlanOmint());
	    res.setAco(res.getUnsuscribeEmail());

	    return res;
	}
	
}
