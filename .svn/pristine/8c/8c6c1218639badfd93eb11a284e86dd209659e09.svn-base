package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LoginCmsResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4371197492575675511L;
	
	private String source;
	private String destination;
	private String uniqueId;
	private Date generationTime;
	private Date expirationTime;
	private String token;
	private String sign;
	private String altaUsr;
	private Date altaFecha;
	
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public Date getGenerationTime() {
		return generationTime;
	}
	public void setGenerationTime(Date generationTime) {
		this.generationTime = generationTime;
	}
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	
	public static LoginCmsResponse getMapping(String prefix, ResultSet rs) throws SQLException {
		
		LoginCmsResponse l = new LoginCmsResponse();
		l.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		l.setAltaUsr(rs.getString(prefix + "alta_usr"));
		l.setDestination(rs.getString(prefix + "destination"));
		l.setExpirationTime(rs.getTimestamp(prefix + "expiration_time"));
		l.setGenerationTime(rs.getTimestamp(prefix + "generation_time"));
		l.setSign(rs.getString(prefix + "sign"));
		l.setSource(rs.getString(prefix + "source_"));
		l.setToken(rs.getString(prefix + "token_"));
		l.setUniqueId(rs.getString(prefix+ "unique_id"));
		
		return l;
	}
	
	public LoginCmsResponse() {
		super();
	}
	
	public LoginCmsResponse(String source, String destination, String uniqueId, Date generationTime,
			Date expirationTime, String token, String sign) {
		super();
		this.source = source;
		this.destination = destination;
		this.uniqueId = uniqueId;
		this.generationTime = generationTime;
		this.expirationTime = expirationTime;
		this.token = token;
		this.sign = sign;
	}
	
	
	
}
