package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Concepto {
	public static int DEVOLUCION_ANTICIPO=985;
	
	private int idSecuencial;
	private int id;
	private String descripcion;
	private PlanCuentas planCuentas;
	private PlanCuentas planCuentasPasivo;
	private Date validoDesde;
	private Date validoHasta;
	private boolean liquidaciones;
	private boolean egreso;
	private boolean ingreso;
	private boolean subEgreso;
	private boolean subIngreso;
	private int idSeccional;
	private String seccional;
	private Anticipo anticipo;
	

	public Concepto() {
	}

	public Concepto(int id) {
		this.id = id;
	}
	
	public Concepto(int id, int idsec) {
		this.id = id;
		this.idSecuencial=idsec;
	}

	public Concepto(int id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		if (descripcion == null) {
			return "";
		}
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
		Concepto other = (Concepto) obj;
		if (idSecuencial == 0) {
			if (id != other.id){
				return false;
			}else{
				if(null!=other.getAnticipo() && null!=other.getAnticipo().getAnticipo() 
						&& null!=this.getAnticipo() && null!=this.getAnticipo().getAnticipo() &&
						other.getAnticipo().getAnticipo().getNroComprobante().equals(this.getAnticipo().getAnticipo().getNroComprobante())){
					return true;
				}else if(null==other.getAnticipo()&&null==this.getAnticipo() && other.getIdSeccional()==this.getIdSeccional()){
					return true;					
				}else{
					return false;
				}
			}
		} else {
			if (/*id != other.id ||*/ idSecuencial != other.idSecuencial)
				return false;
		}
		if(idSeccional>0 && idSeccional!=other.idSeccional){
			return false;
		}
		
		return true;
	}

	public static Concepto getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Concepto cc = new Concepto();
		cc.setId(rs.getInt(prefix + "id"));
		cc.setDescripcion(rs.getString(prefix + "descripcion"));
		try{
			cc.setIdSeccional(rs.getInt(prefix + "id_seccional"));
		}catch(Exception e){
			cc.setIdSeccional(0);
		}
		PlanCuentas pc = new PlanCuentas();
		pc.setNumero(rs.getString(prefix + "numero"));
		pc.setCuenta(rs.getString(prefix + "cuenta"));
		
		cc.setPlanCuentas(pc);
		return cc;
	}

	public static Concepto getFullMapping(ResultSet rs, String prefix)
			throws SQLException {
		Concepto cc = new Concepto();
		cc.setId(rs.getInt(prefix + "id"));
		cc.setDescripcion(rs.getString(prefix + "descripcion"));

		PlanCuentas pc = new PlanCuentas();
		pc.setNumero(rs.getString(prefix + "numero"));
		pc.setCuenta(rs.getString(prefix + "cuenta"));
		pc.setId(rs.getInt("id_plan_cuenta"));
		cc.setPlanCuentas(pc);

		PlanCuentas pcpasivo = new PlanCuentas();
		pcpasivo.setNumero(rs.getString(prefix + "numero_pasivo"));
		pcpasivo.setCuenta(rs.getString(prefix + "cuenta_pasivo"));
		pcpasivo.setId(rs.getInt("id_plan_cuenta_pasivo"));
		cc.setPlanCuentasPasivo(pcpasivo);

		cc.setLiquidaciones(rs.getBoolean("liquidaciones"));
		cc.setEgreso(rs.getBoolean("egreso"));
		cc.setIngreso(rs.getBoolean("ingreso"));
		cc.setSubEgreso(rs.getBoolean("sub_egreso"));
		cc.setSubIngreso(rs.getBoolean("sub_ingreso"));
		cc.setValidoDesde(rs.getDate("valido_desde"));
		cc.setValidoHasta(rs.getDate("valido_hasta"));

		return cc;
	}

	public static Concepto getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public String getNumero() {
		return planCuentas.getNumero();
	}

	public String getCuenta() {
		return planCuentas.getCuenta();
	}

	public void setPlanCuentas(PlanCuentas planCuentas) {
		this.planCuentas = planCuentas;
	}

	public PlanCuentas getPlanCuentas() {
		return planCuentas;
	}

	public void setPlanCuentasPasivo(PlanCuentas planCuentasPasivo) {
		this.planCuentasPasivo = planCuentasPasivo;
	}

	public PlanCuentas getPlanCuentasPasivo() {
		return planCuentasPasivo;
	}

	public Date getValidoDesde() {
		return validoDesde;
	}

	public String getValidoDesdeString() {
		if (validoDesde == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesde);
	}

	public void setValidoDesde(Date validoDesde) {
		this.validoDesde = validoDesde;
	}

	public Date getValidoHasta() {
		return validoHasta;
	}

	public String getValidoHastaString() {
		if (validoHasta == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHasta);
	}

	public void setValidoHasta(Date validoHasta) {
		this.validoHasta = validoHasta;
	}

	public boolean isLiquidaciones() {
		return liquidaciones;
	}

	public void setLiquidaciones(boolean liquidaciones) {
		this.liquidaciones = liquidaciones;
	}

	public boolean isEgreso() {
		return egreso;
	}

	public void setEgreso(boolean egreso) {
		this.egreso = egreso;
	}

	public boolean isIngreso() {
		return ingreso;
	}

	public void setIngreso(boolean ingreso) {
		this.ingreso = ingreso;
	}

	public boolean isSubEgreso() {
		return subEgreso;
	}

	public void setSubEgreso(boolean subEgreso) {
		this.subEgreso = subEgreso;
	}

	public boolean isSubIngreso() {
		return subIngreso;
	}

	public void setSubIngreso(boolean subIngreso) {
		this.subIngreso = subIngreso;
	}

	public int getIdSeccional() {
		return idSeccional;
	}

	public void setIdSeccional(int id_seccional) {
		this.idSeccional = id_seccional;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public int getIdSecuencial() {
		return idSecuencial;
	}

	public void setIdSecuencial(int idSec) {
		this.idSecuencial = idSec;
	}

	public Anticipo getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(Anticipo anticipo) {
		this.anticipo = anticipo;
	}
	
	public String getAnticipoComproNro(){
		if(this.anticipo!=null&&this.anticipo.getAnticipo()!=null){
			return this.anticipo.getAnticipo().getNroComprobante();
		}else{
			return null;
		}
	}
	

}
