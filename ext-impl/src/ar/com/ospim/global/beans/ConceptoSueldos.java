package ar.com.ospim.global.beans;

import java.io.Serializable;

import ar.com.uoma.beans.CentroCosto;

public class ConceptoSueldos implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8970199356066064452L;
	private Integer id;
	private Integer codigo;
	private String descripcion;
	private PlanCuentas cuentaContable;
	private CentroCosto centroCosto;
    private String debeHaber;
    private Integer sectorLiquidado;
    private Double remunerativo;
    private Double noRemunerativo;
    private Double retencion;
    private Double contribucion;
    private boolean conProblema;
	private String error;
	private String entidad;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	
	public PlanCuentas getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(PlanCuentas cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getDebeHaber() {
		return debeHaber;
	}

	public void setDebeHaber(String debeHaber) {
		this.debeHaber = debeHaber;
	}

	public Integer getSectorLiquidado() {
		return sectorLiquidado;
	}

	public void setSectorLiquidado(Integer sectorLiquidado) {
		this.sectorLiquidado = sectorLiquidado;
	}

	public Double getRemunerativo() {
		return remunerativo;
	}

	public void setRemunerativo(Double remunerativo) {
		this.remunerativo = remunerativo;
	}

	public Double getNoRemunerativo() {
		return noRemunerativo;
	}

	public void setNoRemunerativo(Double noRemunerativo) {
		this.noRemunerativo = noRemunerativo;
	}

	public Double getRetencion() {
		return retencion;
	}

	public void setRetencion(Double retencion) {
		this.retencion = retencion;
	}

	public Double getContribucion() {
		return contribucion;
	}

	public void setContribucion(Double contribucion) {
		this.contribucion = contribucion;
	}
	
	
	public boolean isConProblema() {
		return conProblema;
	}

	public void setConProblema(boolean conProblema) {
		this.conProblema = conProblema;
	}

	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	
	
	public CentroCosto getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(CentroCosto centroCosto) {
		this.centroCosto = centroCosto;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConceptoSueldos other = (ConceptoSueldos) obj;
		
		if(id>0 && other.id==id) return true;
		if(other.codigo.equals(codigo) &&
		   other.entidad.equals(entidad) &&
		   other.sectorLiquidado.equals(sectorLiquidado)) return true;
		
		return false;
	}

	/*
	public static ConceptoSueldos getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ConceptoSueldos cc = new ConceptoSueldos();
		cc.setId(rs.getInt(prefix + "id"));
		cc.setDescripcion(rs.getString(prefix + "descripcion"));
		
		PlanCuentas pc = new PlanCuentas();
		pc.setNumero(rs.getString(prefix + "numero"));
		pc.setCuenta(rs.getString(prefix + "cuenta"));
		
		cc.setCuentaContable(pc);
		return cc;
	}
    */
	
		

}
