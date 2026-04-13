package ar.com.ospim.automatico.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import ar.com.ospim.webservice.omint.Beneficiario;

public class MensajeEnvioyRespuestaWSOmint extends Beneficiario {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8814842715538857509L;
	
	private Integer id_transaction;
	private String messageErrorCode;
	private String messageDescription;
	private int operacion;
	private String estadoCivilDesc;
	private String nacionalidadDesc;
	private String categoriaDesc;
	private String planDesc;
	private String cuitLaboral;
	private String razonSocLaboral;
	private int idOspim;
	
	public MensajeEnvioyRespuestaWSOmint(){
		super();
	}

	public Integer getId_transaction() {
		return id_transaction;
	}

	public void setId_transaction(Integer id_transaction) {
		this.id_transaction = id_transaction;
	}

	public String getMessageErrorCode() {
		return messageErrorCode;
	}

	public void setMessageErrorCode(String messageErrorCode) {
		this.messageErrorCode = messageErrorCode;
	}

	public String getMessageDescription() {
		return messageDescription;
	}

	public void setMessageDescription(String messageDescription) {
		this.messageDescription = messageDescription;
	}

	public int getOperacion() {
		return operacion;
	}

	public void setOperacion(int operacion) {
		this.operacion = operacion;
	}
	
	
	public String getEstadoCivilDesc() {
		return estadoCivilDesc;
	}

	public void setEstadoCivilDesc(String estadoCivilDesc) {
		this.estadoCivilDesc = estadoCivilDesc;
	}

	public String getNacionalidadDesc() {
		return nacionalidadDesc;
	}

	public void setNacionalidadDesc(String nacionalidadDesc) {
		this.nacionalidadDesc = nacionalidadDesc;
	}

	public String getCategoriaDesc() {
		return categoriaDesc;
	}

	public void setCategoriaDesc(String categoriaDesc) {
		this.categoriaDesc = categoriaDesc;
	}

	public String getPlanDesc() {
		return planDesc;
	}

	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}

	public String getCuitLaboral() {
		return cuitLaboral;
	}

	public void setCuitLaboral(String cuitLaboral) {
		this.cuitLaboral = cuitLaboral;
	}

	public String getRazonSocLaboral() {
		return razonSocLaboral;
	}

	public void setRazonSocLaboral(String razonSocLaboral) {
		this.razonSocLaboral = razonSocLaboral;
	}

	public int getIdOspim() {
		return idOspim;
	}

	public void setIdOspim(int idOspim) {
		this.idOspim = idOspim;
	}

	public static MensajeEnvioyRespuestaWSOmint getMapping(ResultSet rs) throws SQLException{
		
		Calendar ci = Calendar.getInstance();
		Calendar cn = Calendar.getInstance();
		Calendar cb = Calendar.getInstance();
		Calendar cp = Calendar.getInstance();

		
		MensajeEnvioyRespuestaWSOmint msg = new MensajeEnvioyRespuestaWSOmint();
		
		msg.setApellido(rs.getString("apellido"));
		msg.setCalle(rs.getString("calle"));
		msg.setCategoriaDesc(rs.getString("categoria"));
		msg.setCP(rs.getString("postal_codi"));
		msg.setCUIL(rs.getString("cuil"));
		msg.setCUILTitular(rs.getString("cuil_titular"));
		msg.setCuitLaboral(rs.getString("cuit"));
		msg.setDiscapacidad(rs.getString("discapacitado"));
		msg.setEstadoCivilDesc(rs.getString("civil_esta") );
		Date bajaFecha = rs.getDate("baja_fecha");
		if(bajaFecha != null){
			cb.setTime(bajaFecha);
			msg.setFecBaja(cb);
		}	
		Date naciFecha = rs.getDate("naci_fecha"); 
		if(naciFecha != null){
			cn.setTime(naciFecha);
			msg.setFecNac(cn);
		}	
		Date ingreFecha = rs.getDate("ingre_fecha"); 
		if(ingreFecha != null){
			ci.setTime(ingreFecha);
			msg.setFecVig(ci);
		}
		Date fpp = rs.getDate("fpp");
		if(fpp != null){
			cp.setTime(fpp);
			msg.setFPP(cp);
		}	
		msg.setId_transaction(rs.getInt("id_transaction"));
		msg.setIdOspim(rs.getInt("id_ospim"));
		msg.setInte(rs.getInt("inte"));
		msg.setLocalidad(rs.getString("localidad"));
		msg.setMessageDescription(rs.getString("message_description"));
		msg.setMessageErrorCode(rs.getString("message_code"));
		msg.setNacionalidadDesc(rs.getString("nacionalidad"));
		msg.setNombre(rs.getString("nombre"));
		msg.setNroCalle(rs.getString("numero"));
		msg.setNroDoc(rs.getString("docu_numero"));
		msg.setNroIntegrante(rs.getInt("inte"));
		msg.setOperacion(rs.getInt("operacion"));
		msg.setParentesco(rs.getString("parentesco"));
		msg.setProvincia(rs.getString("provincia"));
		msg.setPlanDesc(rs.getString("plan"));
		msg.setRazonSocLaboral(rs.getString("razon_soc"));
		msg.setResto(rs.getString("piso") + rs.getString("depto"));
		msg.setSeccional(rs.getString("seccional"));
//		msg.setSessionID(sessionID);
		msg.setSexo(rs.getString("sexo"));
		msg.setTelefono(rs.getString("telefono"));
		msg.setTipoDoc(rs.getString("documento_tipo"));
		
		
		return msg;
		
	}
}
