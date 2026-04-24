package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DesempleoSS {
	
	String cuil;
	String cuil_titular;	
	String docu_numero;
	String nombreApe;
	Date fecha_nac;
	String sexo;
	Date fecha_vig;
	Date acredita;
	BigDecimal importe;
	BigDecimal neto;
	String idTerc;
	
	public DesempleoSS() {
	}

	
	public static DesempleoSS getMapping(ResultSet rs)
				throws SQLException {
			DesempleoSS egre = new DesempleoSS();
			egre.setCuil_titular(rs.getString("cuil_titular"));
			egre.setCuil(rs.getString("cuil"));
			egre.setDocu_numero(rs.getString("docu_numero"));
			egre.setNombreApe(rs.getString("nombre"));
			egre.setFecha_nac(rs.getDate("fecha_nac"));
			egre.setSexo(rs.getString("sexo"));
			egre.setFecha_vig(rs.getDate("fecha_vig"));
			egre.setAcredita(rs.getDate("acredita"));
			egre.setImporte(rs.getBigDecimal("importe"));
			egre.setNeto(rs.getBigDecimal("neto"));
			egre.setIdTerc(rs.getString("prestadora"));
			return egre;
	}
	

	public String getCuil() {
		return cuil;
	}


	public void setCuil(String cuil) {
		this.cuil = cuil;
	}


	public String getCuil_titular() {
		return cuil_titular;
	}


	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}


	public String getDocu_numero() {
		return docu_numero;
	}


	public void setDocu_numero(String docu_numero) {
		this.docu_numero = docu_numero;
	}


	public String getNombreApe() {
		return nombreApe;
	}


	public void setNombreApe(String nombreApe) {
		this.nombreApe = nombreApe;
	}


	public Date getFecha_nac() {
		return fecha_nac;
	}


	public void setFecha_nac(Date fecha_nac) {
		this.fecha_nac = fecha_nac;
	}


	public String getSexo() {
		return sexo;
	}


	public void setSexo(String sexo) {
		this.sexo = sexo;
	}


	public Date getFecha_vig() {
		return fecha_vig;
	}


	public void setFecha_vig(Date fecha_vig) {
		this.fecha_vig = fecha_vig;
	}


	public Date getAcredita() {
		return acredita;
	}


	public void setAcredita(Date acredita) {
		this.acredita = acredita;
	}


	public BigDecimal getNeto() {
		return neto;
	}


	public void setNeto(BigDecimal neto) {
		this.neto = neto;
	}


	public BigDecimal getImporte() {
		return importe;
	}


	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}


	public String getIdTerc() {
		return idTerc;
	}


	public void setIdTerc(String idTerc) {
		this.idTerc = idTerc;
	}
	
	

	
		
}

