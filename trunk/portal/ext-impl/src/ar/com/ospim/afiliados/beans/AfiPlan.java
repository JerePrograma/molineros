package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.crm.beans.ContactoCRM.ESTADOS;
import ar.com.ospim.global.beans.Plan;

//Mapea la tabla Afi_Plan
public class AfiPlan implements Serializable {

	private static final long serialVersionUID = 6144366385811263710L;
	
	private BigInteger id; 
	private String cuil_titular;
	private int inte;
//	private int id_plan;
	private int idTarifa; 
	private Date vigenDesde;
	private Date vigenHasta;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	private MotivoBaja motivoBaja;
	private Plan plan;
	private int id_plan_omint;
	private List<AfiAportes> aportes;
	
	private ESTADOS estado;  
	public enum ESTADOS {
		ALTA, MODIFICADO, BAJA
	};
	
	public AfiPlan(){
		super();
	}
	
	public static AfiPlan getMapping(ResultSet rs) throws Exception{
		AfiPlan ap = new AfiPlan();
		
		ap.setCuil_titular(rs.getString("cuil_titular"));
		ap.setInte(rs.getInt("inte"));
		ap.setId(BigInteger.valueOf(rs.getLong("id")) ); 
		ap.setIdTarifa(rs.getInt("id_tarifa"));
		ap.setVigenDesde(rs.getDate("vigen_desde"));
		ap.setVigenHasta(rs.getDate("vigen_hasta"));
		ap.setAltaUsr(rs.getString("alta_usr"));
		ap.setAltaFecha(rs.getTimestamp("alta_fecha"));
		ap.setModiUsr(rs.getString("modi_usr"));
		ap.setModiFecha(rs.getTimestamp("modi_fecha"));
		ap.setBajaFecha(rs.getDate("baja_fecha"));
		ap.setBajaUsr(rs.getString("baja_usr"));

		ap.setId_plan_omint(rs.getInt("id_plan_omint"));
		
		return ap;
	}
	
	public static AfiPlan getMappingConMotivoBaja(ResultSet rs, String prefix) throws SQLException {
		
		AfiPlan ap = new AfiPlan();
		MotivoBaja mb = new MotivoBaja();
		
		ap.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		ap.setInte(rs.getInt(prefix + "inte"));
		ap.setId(BigInteger.valueOf(rs.getLong(prefix + "id")) ); 
		ap.setIdTarifa(rs.getInt(prefix + "id_tarifa"));
		ap.setVigenDesde(rs.getDate(prefix + "vigen_desde"));
		ap.setVigenHasta(rs.getDate(prefix + "vigen_hasta"));
		ap.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ap.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ap.setModiUsr(rs.getString(prefix + "modi_usr"));
		ap.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ap.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		ap.setBajaUsr(rs.getString(prefix + "baja_usr"));

		ap.setId_plan_omint(rs.getInt(prefix + "id_plan_omint"));
		
		mb = MotivoBaja.getMapping("motbaja_",rs);
		
		ap.setMotivoBaja(mb);
		
		return ap;
	}
	
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
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
	public int getIdTarifa() {
		return idTarifa;
	}
	public void setIdTarifa(int idTarifa) {
		this.idTarifa = idTarifa;
	}
	public Date getVigenDesde() {
		return vigenDesde;
	}
	public void setVigenDesde(Date vigenDesde) {
		this.vigenDesde = vigenDesde;
	}
	public Date getVigenHasta() {
		return vigenHasta;
	}
	public void setVigenHasta(Date vigenHasta) {
		this.vigenHasta = vigenHasta;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public Date getModiFecha() {
		return modiFecha;
	}
	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}
	public String getModiUsr() {
		return modiUsr;
	}
	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	public MotivoBaja getMotivoBaja() {
		return motivoBaja;
	}
	public void setMotivoBaja(MotivoBaja motivoBaja) {
		this.motivoBaja = motivoBaja;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public int getId_plan_omint() {
		return id_plan_omint;
	}
	public void setId_plan_omint(int id_plan_omint) {
		this.id_plan_omint = id_plan_omint;
	}
	public List<AfiAportes> getAportes() {
		return aportes;
	}
	public void setAportes(List<AfiAportes> aportes) {
		this.aportes = aportes;
	}
	
	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AfiPlan other = (AfiPlan) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
