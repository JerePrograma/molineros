package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Plan;

public class PrestadorPlan implements Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = -7868204653337435199L;
	private Integer id;
	private Integer id_prestador;
	private Integer id_plan;
	private Date vigencia_desde;
	private Date vigencia_hasta;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	 
	private Plan plan;
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	
	public PrestadorPlan(){
		super();
	}
	
	public PrestadorPlan(Integer id_prestador, Integer id_plan,
			Date vigencia_desde, Date vigencia_hasta) {
		
		super();
		this.id_prestador = id_prestador;
		this.id_plan = id_plan;
		this.vigencia_desde = vigencia_desde;
		this.vigencia_hasta = vigencia_hasta;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId_prestador() {
		return id_prestador;
	}

	public void setId_prestador(Integer id_prestador) {
		this.id_prestador = id_prestador;
	}

	public Integer getId_plan() {
		return id_plan;
	}

	public void setId_plan(Integer id_plan) {
		this.id_plan = id_plan;
	}

	public Date getVigencia_desde() {
		return vigencia_desde;
	}

	public void setVigencia_desde(Date vigencia_desde) {
		this.vigencia_desde = vigencia_desde;
	}

	public Date getVigencia_hasta() {
		return vigencia_hasta;
	}

	public void setVigencia_hasta(Date vigencia_hasta) {
		this.vigencia_hasta = vigencia_hasta;
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

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	public static PrestadorPlan getMapping(String prefix, ResultSet rs) throws SQLException{
		
		PrestadorPlan pp = new PrestadorPlan();
		
		pp.setId(rs.getInt(prefix + "id"));
		pp.setId_prestador(rs.getInt(prefix + "id_prestador"));
		pp.setId_plan(rs.getInt(prefix + "id_plan"));
		pp.setVigencia_desde(rs.getDate(prefix + "vigencia_desde"));
		pp.setVigencia_hasta(rs.getDate(prefix + "vigencia_hasta"));
		pp.setAlta_usr(rs.getString(prefix + "alta_usr"));
		pp.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		pp.setBaja_usr(rs.getString(prefix + "baja_usr"));
		pp.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		Plan p = Plan.getMapping(rs, prefix);
		pp.setPlan(p);
		return pp;
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
		PrestadorPlan other = (PrestadorPlan) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
