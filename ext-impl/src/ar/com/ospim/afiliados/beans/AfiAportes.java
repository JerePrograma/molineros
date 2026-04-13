package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


//Mapea tabla afi_aportes, ahora tienen id los aportes con relacion al plan del afiliado al cual pertenecen

public class AfiAportes implements Serializable {

	/**
	 * @author sergio
	 */
	private static final long serialVersionUID = 6582438679327282388L;
	
	private BigInteger id;
	private BigInteger id_plan_serial;
	private String cuil_titular;
	private int inte;
//	private int idAporte;
	private Date fechaIngre;
	private Date fechaEgre;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private MotivoBaja motivoBaja;
	private TipoAporte aporte;
	private int idSocio;
	private String tipoIdSocio;
	
	public static AfiAportes getMapping(String prefix, ResultSet rs) throws SQLException {
		
		TipoAporte aporte = null;
		MotivoBaja mb = null;
		
		aporte = TipoAporte.getMapping("aporte_",rs);
		mb = MotivoBaja.getMapping("motbaja_",rs);
		
		AfiAportes aa = new AfiAportes();
		aa.setAporte(aporte);
		aa.setMotivoBaja(mb);
		
		aa.setId(BigInteger.valueOf(rs.getLong(prefix+"id")) ); 
		aa.setId_plan_serial(BigInteger.valueOf(rs.getLong(prefix+"id_plan_serial")));
		aa.setCuil_titular(rs.getString(prefix+"cuil_titular"));
		aa.setInte(rs.getInt(prefix+"inte"));
		aa.setFechaIngre(rs.getDate(prefix+"fecha_ingre"));
		aa.setFechaEgre(rs.getDate(prefix+"fecha_egre"));
		aa.setIdSocio(rs.getInt(prefix+"id_socio"));
		aa.setTipoIdSocio(rs.getString(prefix+"tipo_aporte"));
		aa.setAlta_fecha(rs.getDate(prefix+"alta_fecha"));
		aa.setAlta_usr(rs.getString(prefix+"alta_usr"));
		aa.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		aa.setModi_usr(rs.getString(prefix+"modi_usr"));
		aa.setBaja_fecha(rs.getDate(prefix+"baja_fecha"));
		aa.setBaja_usr(rs.getString(prefix+"baja_usr"));
		
		return aa;
	}
	
	public AfiAportes(){
		super();
	}	
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public BigInteger getId_plan_serial() {
		return id_plan_serial;
	}
	public void setId_plan_serial(BigInteger id_plan_serial) {
		this.id_plan_serial = id_plan_serial;
	}
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
//	public int getIdAporte() {
//		return idAporte;
//	}
//	public void setIdAporte(int idAporte) {
//		this.idAporte = idAporte;
//	}
	public Date getFechaIngre() {
		return fechaIngre;
	}
	public void setFechaIngre(Date fechaIngre) {
		this.fechaIngre = fechaIngre;
	}
	public Date getFechaEgre() {
		return fechaEgre;
	}
	public void setFechaEgre(Date fechaEgre) {
		this.fechaEgre = fechaEgre;
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
	public MotivoBaja getMotivoBaja() {
		return motivoBaja;
	}
	public void setMotivoBaja(MotivoBaja motivoBaja) {
		this.motivoBaja = motivoBaja;
	}
	public TipoAporte getAporte() {
		return aporte;
	}
	public void setAporte(TipoAporte aporte) {
		this.aporte = aporte;
	}
	public int getIdSocio() {
		return idSocio;
	}
	public void setIdSocio(int idSocio) {
		this.idSocio = idSocio;
	}
	public String getTipoIdSocio() {
		return tipoIdSocio;
	}
	public void setTipoIdSocio(String tipoIdSocio) {
		this.tipoIdSocio = tipoIdSocio;
	}
	
	
}
