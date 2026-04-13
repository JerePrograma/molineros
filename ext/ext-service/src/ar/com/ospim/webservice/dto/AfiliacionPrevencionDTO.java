package ar.com.ospim.webservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AfiliacionPrevencionDTO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3965524334063952050L;
	private static Log _log = LogFactoryUtil.getLog(AfiliacionPrevencionDTO.class);
	
	protected Integer nroSocio;
	protected String nroDocumento;
	protected String cuil;
	protected BigDecimal nroCredencial;
	protected String cuilTitular;
	protected Integer intePrevencion;
	
	public static final String VALIDA_OK = "OK";
	public static final String FAIL_NRO_SOCIO = "EL NUMERO DE SOCIO NO ES VALIDO";
	public static final String FAIL_NRO_DOCUMENTO = "EL NUMERO DE DOCUMENTO NO ES VALIDO";
	public static final String FAIL_CUIL = "EL CUIL NO ES VALIDO";
	public static final String FAIL_CUIL_TITULAR = "EL CUIL TITULAR NO ES VALIDO";
	public static final String FAIL_NRO_CREDENCIAL = "EL NUMERO DE CREDENCIAL NO ES VALIDO";
	public static final String FAIL_NRO_INTEGRANTE = "EL NUMERO DE INTEGRANTE NO ES VALIDO";

	
	
	public Integer getNroSocio() {
		return nroSocio;
	}
	public void setNroSocio(Integer nroSocio) {
		this.nroSocio = nroSocio;
	}
	public String getNroDocumento() {
		return nroDocumento;
	}
	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public BigDecimal getNroCredencial() {
		return nroCredencial;
	}
	public void setNroCredencial(BigDecimal nroCredencial) {
		this.nroCredencial = nroCredencial;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public Integer getIntePrevencion() {
		return intePrevencion;
	}
	public void setIntePrevencion(Integer intePrevencion) {
		this.intePrevencion = intePrevencion;
	}
	
	public String validaCredencial(){
				
		if(this.cuil != null && this.cuil.length()!=11 && this.getIntegerOrNull(cuil)==null){
			return FAIL_CUIL;
		}
		if(this.cuilTitular != null && this.cuilTitular.length()!=11 && this.getIntegerOrNull(cuilTitular)==null){
			return FAIL_CUIL_TITULAR;
		}
		if(this.nroDocumento != null && (this.nroDocumento.length()<7 || this.nroDocumento.length()>8) 
				&& this.getIntegerOrNull(nroDocumento)==null){
			return FAIL_NRO_DOCUMENTO;
		}
		if(this.intePrevencion != null && this.getIntegerOrNull(String.valueOf(intePrevencion))==null){
			return FAIL_NRO_INTEGRANTE;
		}
		if(this.nroCredencial !=null && this.getBigDecimalOrNull(String.valueOf(this.nroCredencial))==null){
			return FAIL_NRO_CREDENCIAL; 
		}
		if(this.nroSocio != null && this.getIntegerOrNull(String.valueOf(nroSocio))==null){
			return FAIL_NRO_SOCIO;
		}	
	
		return VALIDA_OK;	
	}
	
	public AfiliacionPrevencionDTO(){
		super();
	}
	
	public AfiliacionPrevencionDTO(String line, String cuilTitularAnt) {
		super();
		_log.debug(line);
		String[] linea = line.split("\\,");
//		prepaga,contra,inte,nombre,ape,paren,sexo,ingre_fecha,docu_tipos,docu_nro,cuil,plan_codi,identif_ext,creden_nro
//		1,37087,0,ENZO PAUL,CHAILE,Titular,M,01/09/2015,DU,36037530,20360375308,A1,66848/0,3708700019
		
		this.intePrevencion = linea[2] != null && linea[2].trim().length() > 0 ? Integer.parseInt(linea[2].trim()) : null;
		this.cuil = linea[10].trim();
//		Como el cuil titular no lo manda Prevencion, si es inte 0 ponemos el cuil = cuil titular, desp lo guardamos para que los 
//		integrantes lo reciban en las siguientes registros
//		DEBE VENIR ORDENADO POR CONTRA/INTE  contrato-integrante de Prevencion 
		this.cuilTitular = linea[2] != null && linea[2].trim().equalsIgnoreCase("0")?this.cuil:cuilTitularAnt;
		this.nroCredencial = linea[13] != null && linea[13].trim().length() > 0 ? new BigDecimal(linea[13].trim()) : null;
		this.nroDocumento = linea[9].trim();
		this.nroSocio = linea[1] != null && linea[1].trim().length() > 0 ? Integer.parseInt(linea[1].trim()) : null;
		
	}
	
	public static AfiliacionPrevencionDTO getMapping(String prefix, ResultSet rs) throws Exception{
		
		AfiliacionPrevencionDTO dto = new AfiliacionPrevencionDTO();
		
		dto.setCuilTitular(rs.getString(prefix + "cuil_titular"));
		dto.setCuil(rs.getString(prefix + "cuil"));
		dto.setIntePrevencion(rs.getInt(prefix + "inte_prev"));	
		dto.setNroCredencial(rs.getBigDecimal(prefix + "nro_creden"));
		dto.setNroDocumento(rs.getString(prefix + "nro_doc"));
		dto.setNroSocio(rs.getInt(prefix + "nro_socio"));
		
		return dto;
	}
	
	/*Estos methodos son de StringUtils*/
	private boolean checkNotEmpty(Object valor) {
		return valor != null;
	}
	private boolean checkEmpty(String valor) {
		return !checkNotEmpty(valor);
	}
	private Integer getIntegerOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		}
		try{
			return Integer.valueOf(o);
		}catch(NumberFormatException e){
			return null;
		}
	}
	private BigDecimal getBigDecimalOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		}
		try{
			return BigDecimal.valueOf(Long.parseLong(o));
		}catch(NumberFormatException e){
			return null;
		}
	}
	/*fin StringUtils*/
	@Override
	public String toString() {
		return "AfiliacionPrevencionDTO [nroSocio=" + nroSocio
				+ ", nroDocumento=" + nroDocumento + ", cuil=" + cuil
				+ ", nroCredencial=" + nroCredencial + ", cuilTitular="
				+ cuilTitular + ", intePrevencion=" + intePrevencion + "]";
	}
	
	
}
