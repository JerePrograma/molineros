package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubidaFTPPadronIGS {
	
	private String afiliado;
	private String tipoDocumento;
	private String documentoNumero;
	private String numeroOSPIM;
	private String planIGS;
	private String telefono;
	private String localidad;
	private String provincia;
	private String movimito;
	
	
	
	public String getAfiliado() {
		return afiliado;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public String getDocumentoNumero() {
		return documentoNumero;
	}
	public String getNumeroOSPIM() {
		return numeroOSPIM;
	}
	public String getPlanIGS() {
		return planIGS;
	}
	public String getTelefono() {
		return telefono;
	}
	public String getLocalidad() {
		return localidad;
	}
	public String getProvincia() {
		return provincia;
	}
	public String getMovimito() {
		return movimito;
	}
	public void setAfiliado(String afiliado) {
		this.afiliado = afiliado;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public void setDocumentoNumero(String documentoNumero) {
		this.documentoNumero = documentoNumero;
	}
	public void setNumeroOSPIM(String numeroOSPIM) {
		this.numeroOSPIM = numeroOSPIM;
	}
	public void setPlanIGS(String planIGS) {
		this.planIGS = planIGS;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public void setMovimito(String movimito) {
		this.movimito = movimito;
	}
	
	public static SubidaFTPPadronIGS getMapping(ResultSet rs)
			throws SQLException {
		SubidaFTPPadronIGS subida = new SubidaFTPPadronIGS();
		subida.setAfiliado(rs.getString("afiliado"));
		subida.setTipoDocumento(rs.getString("docu_tipo"));
		subida.setDocumentoNumero(rs.getString("docu_numero"));
		subida.setNumeroOSPIM(rs.getString("num_ospim"));
		subida.setPlanIGS(rs.getString("plan_igs"));
		subida.setTelefono(rs.getString("telefono"));
		subida.setLocalidad(rs.getString("localidad"));
		subida.setProvincia(rs.getString("provincia"));
		subida.setMovimito(rs.getString("movimiento"));
		

		return subida;
	}
	

}
