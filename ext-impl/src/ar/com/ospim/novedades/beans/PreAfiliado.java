package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.util.Date;

import ar.com.ospim.util.StringUtils;

import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

public class PreAfiliado implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6040346880616697280L;
	
	  private Integer id;	
	  private String cuil_titular;
	  private Integer inte;
	  private String cuil;
	  private String apellido;
	  private String nombre;
	  private Integer id_parentesco_sss;
	  private Integer id_estado_civil_sss;
	  private String documento_tipo;
	  private String documento_numero;
	  private String sexo;
	  private String discapacitado;
	  private Integer nacionalidad;
	  private Date naci_fecha;
	  private Integer id_seccional;
	  private Date vigen_fecha;
	  private String observaciones;
	  private String email;
	  private String domi_tipo;
	  private String calle;
	  private String numero;
	  private String piso;
	  private String depto;
	//	  private String oficina;
	  private String postal_codi;
	  private String barrio;
	  private Integer id_provincia;
	  private Integer id_localidad;
	  private String cod_area_telefono;
	  private String telefono;
	  private String cod_area_celular;
	  private String celular;
	  private String cod_area_tel_laboral;
	  private String tel_laboral;
	  private String cuit;
	  private String sucursal;
	  private Date fecha_ingre;
	  private Integer id_revista;
	  private Integer id_categoria;
	  private String escala_salarial;
	  private boolean de_alta_portal;
	  private String tipo_novedad;
	  private Date alta_fecha;
	  private String alta_usr;
	  private int alta_empresa_usr;
	  private Date modi_fecha;
	  private String modi_usr;
	  private Date baja_fecha;
	  private String baja_usr;
	  private Integer id_plan;
	  private Date vigen_desde;
	  private Date vigen_hasta;
	  private int id_motivo_baja;
	  private String id_tercerizadora;
	  private Date fecha_inicio_prestacion;
	  private Date fecha_fin_prestacion;
	private int tieneAntecedentesJudiciales;

	  
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
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
	public Integer getId_parentesco_sss() {
		return id_parentesco_sss;
	}
	public void setId_parentesco_sss(Integer id_parentesco_sss) {
		this.id_parentesco_sss = id_parentesco_sss;
	}
	public Integer getId_estado_civil_sss() {
		return id_estado_civil_sss;
	}
	public void setId_estado_civil_sss(Integer id_estado_civil_sss) {
		this.id_estado_civil_sss = id_estado_civil_sss;
	}
	public String getDocumento_tipo() {
		return documento_tipo;
	}
	public void setDocumento_tipo(String documento_tipo) {
		this.documento_tipo = documento_tipo;
	}
	public String getDocumento_numero() {
		return documento_numero;
	}
	public void setDocumento_numero(String documento_numero) {
		this.documento_numero = documento_numero;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getDiscapacitado() {
		return discapacitado;
	}
	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}
	public Integer getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(Integer nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public Date getNaci_fecha() {
		return naci_fecha;
	}
	public void setNaci_fecha(Date naci_fecha) {
		this.naci_fecha = naci_fecha;
	}
	public Integer getId_seccional() {
		return id_seccional;
	}
	public void setId_seccional(Integer id_seccional) {
		this.id_seccional = id_seccional;
	}
	public Date getVigen_fecha() {
		return vigen_fecha;
	}
	public void setVigen_fecha(Date vigen_fecha) {
		this.vigen_fecha = vigen_fecha;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDomi_tipo() {
		return domi_tipo;
	}
	public void setDomi_tipo(String domi_tipo) {
		this.domi_tipo = domi_tipo;
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
//	public String getOficina() {
//		return oficina;
//	}
//	public void setOficina(String oficina) {
//		this.oficina = oficina;
//	}
	public String getPostal_codi() {
		return postal_codi;
	}
	public void setPostal_codi(String postal_codi) {
		this.postal_codi = postal_codi;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	public Integer getId_provincia() {
		return id_provincia;
	}
	public void setId_provincia(Integer id_provincia) {
		this.id_provincia = id_provincia;
	}
	public Integer getId_localidad() {
		return id_localidad;
	}
	public void setId_localidad(Integer id_localidad) {
		this.id_localidad = id_localidad;
	}
	public String getCod_area_telefono() {
		return cod_area_telefono;
	}
	public void setCod_area_telefono(String cod_area_telefono) {
		this.cod_area_telefono = cod_area_telefono;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCod_area_celular() {
		return cod_area_celular;
	}
	public void setCod_area_celular(String cod_area_celular) {
		this.cod_area_celular = cod_area_celular;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getCod_area_tel_laboral() {
		return cod_area_tel_laboral;
	}
	public void setCod_area_tel_laboral(String cod_area_tel_laboral) {
		this.cod_area_tel_laboral = cod_area_tel_laboral;
	}
	public String getTel_laboral() {
		return tel_laboral;
	}
	public void setTel_laboral(String tel_laboral) {
		this.tel_laboral = tel_laboral;
	}
	public String getCuit() {
		if(cuit == null) return "";
		
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public Date getFecha_ingre() {
		return fecha_ingre;
	}
	public void setFecha_ingre(Date fecha_ingre) {
		this.fecha_ingre = fecha_ingre;
	}
	public Integer getId_revista() {
		return id_revista;
	}
	public void setId_revista(Integer id_revista) {
		this.id_revista = id_revista;
	}
	public Integer getId_categoria() {
		return id_categoria;
	}
	public void setId_categoria(Integer id_categoria) {
		this.id_categoria = id_categoria;
	}
	public boolean isDe_alta_portal() {
		return de_alta_portal;
	}
	public void setDe_alta_portal(boolean de_alta_portal) {
		this.de_alta_portal = de_alta_portal;
	}
	public Date getAlta_fecha() {
		return alta_fecha;
	}
	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}
	public String getAlta_usr() {
		return alta_usr;
	}
	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}
	public Date getModi_fecha() {
		return modi_fecha;
	}
	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}
	public String getModi_usr() {
		return modi_usr;
	}
	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}
	public Date getBaja_fecha() {
		return baja_fecha;
	}
	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}
	public String getBaja_usr() {
		return baja_usr;
	}
	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}
	public String getEscala_salarial() {
		return escala_salarial;
	}
	public void setEscala_salarial(String escala_salarial) {
		this.escala_salarial = escala_salarial;
	}
	public int getAlta_empresa_usr() {
		return alta_empresa_usr;
	}
	public void setAlta_empresa_usr(int alta_empresa_usr) {
		this.alta_empresa_usr = alta_empresa_usr;
	}
	  
	public String getCuil_titularMasked() {
		return StringUtils.getCuilMask(cuil_titular);
	}

	@Override
	public String toString() {
		return "PreAfiliado [código=" + id + ", cuil_titular=" + cuil_titular
				+ ", inte=" + inte + ", apellido=" + apellido + ", nombre="
				+ nombre + ", tipo_novedad=" + (tipo_novedad!=null&&tipo_novedad.equalsIgnoreCase("add")?"ALTA":"MODIFICACION") + ", alta_fecha="
				+ alta_fecha + ", alta_usr=" + alta_usr + ", alta_empresa_usr="
				+ alta_empresa_usr + "]";
	}
	
	public Integer getId_plan() {
		return id_plan;
	}
	public void setId_plan(Integer id_plan) {
		this.id_plan = id_plan;
	}
	public Date getVigenDesde() {
		return vigen_desde;
	}
	public void setVigenDesde(Date vigen_desde) {
		this.vigen_desde = vigen_desde;
	}
	public Date getVigenHasta() {
		return vigen_hasta;
	}
	public void setVigenHasta(Date vigen_hasta) {
		this.vigen_hasta = vigen_hasta;
	}
	public String getId_tercerizadora() {
		return id_tercerizadora;
	}
	public void setId_tercerizadora(String id_tercerizadora) {
		this.id_tercerizadora = id_tercerizadora;
	}
	public Date getFecha_inicio_prestacion() {
		return fecha_inicio_prestacion;
	}
	public void setFecha_inicio_prestacion(Date fecha_inicio_prestacion) {
		this.fecha_inicio_prestacion = fecha_inicio_prestacion;
	}
	public Date getFecha_fin_prestacion() {
		return fecha_fin_prestacion;
	}
	public void setFecha_fin_prestacion(Date fecha_fin_prestacion) {
		this.fecha_fin_prestacion = fecha_fin_prestacion;
	}
	public int getId_motivo_baja() {
		return id_motivo_baja;
	}
	public void setId_motivo_baja(int id_motivo_baja) {
		this.id_motivo_baja = id_motivo_baja;
	}
	public String getTipo_novedad() {
		return tipo_novedad;
	}
	public void setTipo_novedad(String tipo_novedad) {
		this.tipo_novedad = tipo_novedad;
	}

	public int getTieneAntecedentesJudiciales() {
		return tieneAntecedentesJudiciales;
	}

	public void setTieneAntecedentesJudiciales(int tieneAntecedentesJudiciales) {
		this.tieneAntecedentesJudiciales = tieneAntecedentesJudiciales;
	}

	public String getNovedadPreCarga() {
		String retornoCarro =  "\n"; 
		
		Organization org = null;
		try {
			org = OrganizationLocalServiceUtil.getOrganization(alta_empresa_usr);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return " código: " + id + retornoCarro + 
			   " Cuil Titular: " + cuil_titular + retornoCarro +
			   " Inte: " + inte + retornoCarro +
			   " Apellido: " + apellido + retornoCarro +
			   " Nombre: "+ nombre + retornoCarro +
			   " Tipo Novedad: " + (tipo_novedad!=null&&tipo_novedad.equalsIgnoreCase("add")?"ALTA":"MODIFICACION") + retornoCarro + 
			   " Alta Fecha: " + alta_fecha + retornoCarro + 
			   " Alta Usuario: " + alta_usr +  retornoCarro + 
			   " Alta Seccional: " + org.getName();//getEdificioDescripcion() ;
	}
}
