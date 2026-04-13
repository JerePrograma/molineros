package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.TipoAporte;

//Mapea la tabla plan , left join tabla plan_omint

public class Plan implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// de plan
	private int id;
	private String descripcion;
	private String observaciones;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	private int id_plan_base;
	private String descripcionTarjeta;
	
	private List<TipoAporte> aportes;
	
	//de plan_omint
	private int id_plan_omint;
	private String descripcion_omint;
	private String planIgs;
	@Deprecated
	private String descripcionPrevencion;
	@Deprecated
	private String farmaciaPrevencion;
	
	private String descripcionEnsalud;
	private String farmaciaEnsalud;
	
	private boolean uoma;
	
	


	private boolean ospim;	
	private boolean amtima;
	private boolean molinero;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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

	public int getId_plan_base() {
		return id_plan_base;
	}

	public void setId_plan_base(int id_plan_base) {
		this.id_plan_base = id_plan_base;
	}

	public String getDescripcionTarjeta() {
		return descripcionTarjeta;
	}

	public void setDescripcionTarjeta(String descripcionTarjeta) {
		this.descripcionTarjeta = descripcionTarjeta;
	}

	public int getId_plan_omint() {
		return id_plan_omint;
	}

	public void setId_plan_omint(int id_plan_omint) {
		this.id_plan_omint = id_plan_omint;
	}

	public String getDescripcionOmint() {
		return descripcion_omint;
	}

	public void setDescripcionOmint(String descripcion_omint) {
		this.descripcion_omint = descripcion_omint;
	}

	public List<TipoAporte> getAportes() {
		return aportes;
	}

	public void setAportes(List<TipoAporte> aportes) {
		this.aportes = aportes;
	}
	
	public Plan() {
	}
	
	public Plan(int id, String descripcion, boolean uoma, boolean ospim, boolean amtima, boolean molinero){
		super();
		this.id = id;
		this.descripcion=descripcion;
		this.uoma=uoma;
		this.ospim=ospim;
		this.amtima=amtima;
		this.molinero=molinero;
	}

	public Plan(int id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public Plan(String descripcion) {
		this.descripcion = descripcion;
	}

	public Plan(int id, int id_plan_omint, String descripcionOmint, String descripcionPrevencion, String farmaciaPrevencion) {
		super();
		this.id = id;
		this.id_plan_omint = id_plan_omint;
		this.descripcion_omint = descripcionOmint;
		this.descripcionPrevencion = descripcionPrevencion;
		this.farmaciaPrevencion = farmaciaPrevencion;
	}

	public Plan(int id, String descripcion, int id_plan_omint,
			String descripcionOmint) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.id_plan_omint = id_plan_omint;
		this.descripcion_omint = descripcionOmint;
	}

//	public Plan(int idd) {
//		this.id = idd;
//	}

	public static Plan getMappingPlanOmint(ResultSet rs, String prefix) throws SQLException {
		Plan p = new Plan();
		
		p.setId(rs.getInt(prefix + "id_plan"));
		p.setDescripcion(rs.getString(prefix + "descripcion"));
		p.setObservaciones(rs.getString(prefix + "observaciones"));
		p.setAltaUsr(rs.getString(prefix + "alta_usr"));
		p.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		p.setModiUsr(rs.getString(prefix + "modi_usr"));
		p.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		p.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		p.setBajaUsr(rs.getString(prefix + "baja_usr"));
		p.setAmtima(rs.getBoolean(prefix + "amtima"));
		p.setOspim(rs.getBoolean(prefix + "ospim"));
		p.setUoma(rs.getBoolean(prefix + "uoma"));
		p.setId_plan_base(rs.getInt(prefix + "id_plan_base"));
		p.setDescripcionTarjeta(rs.getString(prefix + "descripcion_tarjeta"));  
		
		p.setId_plan_omint(rs.getInt(prefix + "id_plan_omint"));
		p.setDescripcionOmint(rs.getString(prefix + "descripcion_omint"));
		p.setDescripcionPrevencion(rs.getString(prefix + "descripcion_prevencion"));
		p.setFarmaciaPrevencion(rs.getString(prefix + "farmacia_prevencion"));
		p.setPlanIgs(rs.getString(prefix + "plan_igs"));
		
		p.setDescripcionEnsalud(rs.getString(prefix + "descripcion_ensalud"));
		p.setFarmaciaEnsalud(rs.getString(prefix + "farmacia_ensalud"));

		
		return p;
	}
	
	public static Plan getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Plan getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Plan plan = new Plan(rs.getInt(prefix + "id_plan"), rs.getString(prefix
				+ "descripcion"));
		try {
			plan.setAltaUsr(rs.getString(prefix + "alta_usr"));
		} catch (Exception e) {
		}
		return plan;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Plan other = (Plan) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

	public boolean isUoma() {
		return uoma;
	}

	public void setUoma(boolean uoma) {
		this.uoma = uoma;
	}

	public boolean isOspim() {
		return ospim;
	}

	public void setOspim(boolean ospim) {
		this.ospim = ospim;
	}

	public boolean isAmtima() {
		return amtima;
	}

	public void setAmtima(boolean amtima) {
		this.amtima = amtima;
	}

	public boolean isMolinero() {
		return molinero;
	}

	public void setMolinero(boolean molinero) {
		this.molinero = molinero;
	}

	public String getPlanIgs() {
		return planIgs;
	}

	public void setPlanIgs(String planIgs) {
		this.planIgs = planIgs;
	}
    @Deprecated
	public String getDescripcionPrevencion() {
		return getHealthPlan(descripcionPrevencion);
//		return descripcionPrevencion;
	}
	@Deprecated
	public void setDescripcionPrevencion(String descripcionPrevencion) {
		this.descripcionPrevencion = descripcionPrevencion;
	}
	@Deprecated
	public String getFarmaciaPrevencion() {
		return farmaciaPrevencion;
	}
	@Deprecated
	public void setFarmaciaPrevencion(String farmaciaPrevencion) {
		this.farmaciaPrevencion = farmaciaPrevencion;
	}
	
	public String getDescripcionEnsalud() {
		return descripcionEnsalud;
	}

	public void setDescripcionEnsalud(String descripcionEnsalud) {
		this.descripcionEnsalud = descripcionEnsalud;
	}

	public String getFarmaciaEnsalud() {
		return farmaciaEnsalud;
	}

	public void setFarmaciaEnsalud(String farmaciaEnsalud) {
		this.farmaciaEnsalud = farmaciaEnsalud;
	}

	public static String getHealthPlan(String planDesc) {
//		if(planDesc.equalsIgnoreCase("AG")){
//			return "A GENERAL"; 
//		}else{
//			return planDesc;
//		}
		if(planDesc!=null && planDesc.equalsIgnoreCase("AG")){
			return "AG MOLIN"; 
		}else if (planDesc!=null && planDesc.equalsIgnoreCase("A1")){
			return "A Molinero";
		}else{
			return planDesc;
		}
	}
			
}