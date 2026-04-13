package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.SystemException;

import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.global.beans.Plan;


public class NomencladorPlan {
    private int id;
	private int id_prestacion;
	private Plan plan;
	private ModalidadAtencion autorizacion;
	
	private Date vigencia_desde;
	private Date vigencia_hasta;
	private Double topeReintegro;
	
	private String alta_usr;
	private String baja_usr;
	private Date alta_fecha;
	private Date baja_fecha;
	
	static PlanServiceUtil planService = new PlanServiceUtil();
	static NomencladorServiceUtil nomencladorService = new NomencladorServiceUtil();
	
	public static NomencladorPlan getMapping(ResultSet rs) throws Exception {
		
		NomencladorPlan archivo = new NomencladorPlan();
		ModalidadAtencion modalidad= new ModalidadAtencion();
		modalidad = nomencladorService.buscarModalidadAtencionPorId(rs.getInt("autorizacion"));
		archivo.setAutorizacion(modalidad);
		
		Plan plan= new Plan();
		if(rs.getInt("plan")==9999){
			plan.setId(9999);
			plan.setDescripcion("TODOS LOS PLANES");
		}else{
		    plan = planService.buscaPlanPorId(rs.getInt("plan"));
		}    
		
		archivo.setPlan(plan);
		archivo.setId_prestacion(rs.getInt("id_prestacion"));
		return archivo;
	}
	
	public NomencladorPlan(int id_prestacion, int idplan, int autorizacion) {
		this(idplan, autorizacion);
		this.id_prestacion = id_prestacion;
	}

	public NomencladorPlan(int idplan, int idautorizacion) {
		Plan plan = new Plan();
		try {
			if(idplan==9999){
				plan.setId(9999);
				plan.setDescripcion("TODOS LOS PLANES");
				this.plan=plan;
			}else{
			   this.plan = planService.buscaPlanPorId(idplan);
			}   
			
			this.autorizacion = nomencladorService.buscarModalidadAtencionPorId(idautorizacion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public NomencladorPlan() {
	}


	public NomencladorPlan(int id, int id_prestacion,int idplan, int autorizacion) {
		this(id_prestacion, idplan, autorizacion);
		this.id = id;
	}

	public final Date getBaja_fecha() {
		return baja_fecha;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_fecha != null ? sdf.format(baja_fecha)
				: "";
		}

	public final void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public int getId_prestacion() {
		return id_prestacion;
	}

	public void setId_prestacion(int id_prestacion) {
		this.id_prestacion = id_prestacion;
	}
	
	public String getId_prestacion_string() {
		String id_prestacion = Integer.toString(getId_prestacion());
		return id_prestacion;
	}

	
	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public ModalidadAtencion getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(ModalidadAtencion autorizacion) {
		this.autorizacion = autorizacion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
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

	public Double getTopeReintegro() {
		return topeReintegro;
	}

	public void setTopeReintegro(Double topeReintegro) {
		this.topeReintegro = topeReintegro;
	}
	
public static NomencladorPlan getMappingTopes(ResultSet rs) throws Exception {
		
		NomencladorPlan archivo = new NomencladorPlan();
		
		Plan plan= new Plan();
		if(rs.getInt("plan")==9999){
			plan.setId(9999);
			plan.setDescripcion("TODOS LOS PLANES");
		}else{
		    plan = planService.buscaPlanPorId(rs.getInt("plan"));
		}    
		
		archivo.setPlan(plan);
		archivo.setId_prestacion(rs.getInt("id_prestacion"));
		archivo.setId(rs.getInt("id"));
		archivo.setTopeReintegro(rs.getDouble("importe"));
		archivo.setVigencia_desde(rs.getDate("vigencia_dde"));
		archivo.setVigencia_hasta(rs.getDate("vigencia_hta"));
		return archivo;
	}
	
	
}
