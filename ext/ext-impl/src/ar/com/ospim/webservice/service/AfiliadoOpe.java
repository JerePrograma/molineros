package ar.com.ospim.webservice.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class AfiliadoOpe extends Afiliado {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2269028454322590553L;
	private int operacion;

	public int getOperacion() {
		return operacion;
	}

	public void setOperacion(int operacion) {
		this.operacion = operacion;
	}

	public Integer id_seccional;
	public Integer idNacionalidadSSS;
	public boolean farmaciaAmtima;
	public boolean farmaciaUoma; 
	public String planPrevencion;
	public String planFarmacia;
	public Integer idTransaccion;
	public String mensajeDesc;
	public Date fechaOspim;
	private Date fechaInforme;
	private String procesada;
	private boolean infoDatoHomologacionPS;

	public Integer getId_seccional() {
		return id_seccional;
	}

	public void setId_seccional(Integer id_seccional) {
		this.id_seccional = id_seccional;
	}

	public Integer getIdNacionalidadSSS() {
		return idNacionalidadSSS;
	}

	public void setIdNacionalidadSSS(Integer idNacionalidadSSS) {
		this.idNacionalidadSSS = idNacionalidadSSS;
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

	public String getPlanPrevencion() {
		return planPrevencion;
	}

	public void setPlanPrevencion(String planPrevencion) {
		this.planPrevencion = planPrevencion;
	}

	public String getPlanFarmacia() {
		return planFarmacia;
	}

	public void setPlanFarmacia(String planFarmacia) {
		this.planFarmacia = planFarmacia;
	}

	public Integer getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(Integer idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public String getMensajeDesc() {
		return mensajeDesc;
	}

	public void setMensajeDesc(String mensajeDesc) {
		this.mensajeDesc = mensajeDesc;
	}

	public Date getFechaOspim() {
		return fechaOspim;
	}

	public void setFechaOspim(Date fechaOspim) {
		this.fechaOspim = fechaOspim;
	}
	
//	id_transaction integer,
//	message_description character varying,
//	fecha_informe timestamp without time zone
	
public static AfiliadoOpe getMapping3(ResultSet rs) throws SQLException{
		
		Calendar gmtMenos3 = DateUtils.getCalendarGMTMenos3();
	
		AfiliadoOpe af = new AfiliadoOpe();
		af.setId_ospim(rs.getInt("id_ospim"));
		af.setId_amtima(rs.getInt("id_amtima"));
		af.setId_uoma(rs.getInt("id_uoma"));
		String[] secc = rs.getString("seccional").split("-");
		af.setSeccional(new Seccional(Integer.parseInt(secc[0].trim()),secc[1].toString()));
		af.setId_tercerizadora(rs.getString("id_tercerizadora"));
		af.setCuil_titular(StringUtils.getCuilMask(rs.getString("cuil_titular")));
		af.setCuil(StringUtils.getCuilMask(rs.getString("cuil")) );
		af.setInte(rs.getInt("inte"));
		af.setParentesco(rs.getString("parentesco"));
		af.setId_parentesco(rs.getInt("id_parentesco_sss"));
		af.setApellido(rs.getString("apellido"));
		af.setNombre(rs.getString("nombre"));
		af.setDocumento_tipo(rs.getString("documento_tipo"));
		af.setDocu_numero(rs.getString("docu_numero"));
		af.setEmail(rs.getString("email"));
//		af.setNaci_fecha(rs.getDate("naci_fecha"));
		af.setNaci_fecha(rs.getDate("naci_fecha",gmtMenos3));
		af.setSexo(rs.getString("sexo"));
		af.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
		af.setNacionalidad(rs.getInt("id_nacionalidad"));
		af.setIdNacionalidadSSS(rs.getInt("id_nacionalidad_sss"));
		Domicilio dom = new Domicilio();
		dom.setProvincia(new Provincia(rs.getInt("id_provincia"), rs.getString("provincia"),rs.getInt("id_provincia_sss")));
		dom.setLocalidad(new Localidad(rs.getInt("id_localidad"), rs.getString("localidad"),rs.getInt("id_provincia"), 
				rs.getInt("postal_codi"),null,rs.getInt("id_provincia_sss"),rs.getInt("id_localidad_sss")));			
		dom.setPostal_codi(rs.getString("postal_codi"));
		dom.setCalle(rs.getString("calle"));
		if(rs.getString("numero")!=null){
			try{ // porque a veces recibimos un S/N si no trae numero...
				Integer.parseInt(rs.getString("numero"));
			}catch (NumberFormatException e) {
				dom.setNumero("0");
			}
			
		}else{
			dom.setNumero("0");
		}
		
		dom.setPiso(rs.getString("piso"));
		dom.setDepto(rs.getString("depto"));
		dom.setCod_area_telefono(rs.getString("cod_area_telefono"));
		dom.setTelefono(rs.getString("telefono"));
		dom.setCod_area_tel_laboral(rs.getString("cod_area_tel_laboral"));
		dom.setTel_laboral(rs.getString("tel_laboral"));
		dom.setCod_area_celular(rs.getString("cod_area_celular"));
		dom.setCelular(rs.getString("celular"));
		dom.setBarrio(rs.getString("barrio"));
		Domicilio[] domicilios = new Domicilio[1];
		domicilios[0] = dom;
		af.setDomicilios(domicilios);
//		af.setIngre_fecha(rs.getDate("ingre_fecha"));
		af.setIngre_fecha(rs.getDate("ingre_fecha",gmtMenos3));
//		af.setBaja_fecha(rs.getDate("baja_fecha"));
		af.setBaja_fecha(rs.getDate("baja_fecha",gmtMenos3));
		af.setId_motivo_baja(rs.getInt("id_motivo_baja"));
		af.setCuit(rs.getString("cuit"));
//		af.setRazonSoc(rs.getString("razon_soc"));
		String razonSocialTruncada = rs.getString("razon_soc");
		if(razonSocialTruncada != null && razonSocialTruncada.length() > 50 ){
			razonSocialTruncada = rs.getString("razon_soc").substring(0, 49);
		}
		af.setRazonSoc(razonSocialTruncada);
		Plan p = new Plan(rs.getInt("id_plan"), rs.getString("plan"), 0, rs.getString("plan_omint"));
		af.setUltimo_plan(p);
		AfiPlan ap = new AfiPlan();
		ap.setVigenDesde(rs.getDate("plan_desde"));
		ap.setPlan(p);
		af.setAfiPlan(ap);

//		af.setId_categoria(rs.getInt("id_categoria"));
//		af.setFPP(rs.getDate("FPP")); 
		af.setFPP(rs.getDate("FPP",gmtMenos3)); 
		af.setDiscapacitado(rs.getString("discapacitado"));
//		af.setVigen_fecha(rs.getDate("vigen_fecha"));
		af.setVigen_fecha(rs.getDate("vigen_fecha",gmtMenos3));
//		af.setVigen_fecha(rs.getDate("fecha_ospim",gmtMenos3)); // para evitar cambios de plan, 
		af.setFechaOspim(rs.getDate("fecha_ospim",gmtMenos3));
//				que no tenian ospim y luego si, ponia una vieja fecha de vigencia del afiliado
		af.setClientePreferencial(rs.getInt("cliente_preferencial"));
		af.setOperacion(rs.getInt("operacion"));
		af.setFarmaciaAmtima(rs.getBoolean("farmacia_amtima"));
		af.setFarmaciaUoma(rs.getBoolean("farmacia_uoma"));
		af.setPlanFarmacia(rs.getString("plan_farmacia"));
		af.setPlanPrevencion(rs.getString("plan_prevencion"));
		af.setIdTransaccion(rs.getInt("id_transaction"));
		af.setMensajeDesc(rs.getString("message_description"));
		af.setProyecto(rs.getString("proyecto"));
		af.setFechaInforme(rs.getDate("fecha_informe" ,gmtMenos3));
		af.setCivil_esta(rs.getString("civil_esta"));
		af.setProcesada(rs.getString("procesado_ok"));
		return af;
	}

public Date getFechaInforme() {
	return fechaInforme;
}

public void setFechaInforme(Date fechaInforme) {
	this.fechaInforme = fechaInforme;
}

public String getFechaInformeToString() {
	return null != fechaInforme ? DateUtils.format(fechaInforme,
			DateUtils.SHORT) : "";
}

public String getProcesada() {
	return procesada;
}

public void setProcesada(String procesada) {
	this.procesada = procesada;
}

public boolean isInfoDatoHomologacionPS() {
	return infoDatoHomologacionPS;
}

public void setInfoDatoHomologacionPS(boolean infoDatoHomologacionPS) {
	this.infoDatoHomologacionPS = infoDatoHomologacionPS;
}
	
}