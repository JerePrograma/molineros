package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NovedadEmpleadorTotal implements Serializable{ /* extends Novedad */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8860509716267452684L;

	private String plan_actual_desc; 
	private Integer plan_actual_id;
	private String cuil_titular;
	private Integer inte;
	private String apellido;
	private String nombre;
	private Date periodo;
	private BigDecimal importeaportesocialuoma;
	private BigDecimal importearticulo46;
	private BigDecimal importecuotaamtima;
	private BigDecimal importecuotasocialuoma;
	private BigDecimal importecuotausufructo; 
	private BigDecimal importeadherenteamtima;
	private String empresa_cuit;
	private String empresa_razon_social;
	private String empresa_provincia;
	private String empresa_localidad;
	private String empresa_planta;
	private String empresa_calle;
	private String empresa_numero;
	private String empresa_piso;
	private String empresa_depto;
	private String empresa_codigo_postal;
	private String empresa_telefono;
	private boolean ospim;
	private boolean uoma;
	private boolean amtimaadherente;
	private boolean amtimacuota;
	private boolean usufructo;
	private String plan_que_corresponde_desc;
	private Integer plan_que_corresponde_id;
	private int total_registros;
	private String novedad_desc;
	//Datos de la seccional
	private int idSeccional;
	private String descSeccional;
	
	public String getPlan_actual_desc() {
		return plan_actual_desc;
	}
	public void setPlan_actual_desc(String plan_actual_desc) {
		this.plan_actual_desc = plan_actual_desc;
	}
	public Integer getPlan_actual_id() {
		return plan_actual_id;
	}
	public void setPlan_actual_id(Integer plan_actual_id) {
		this.plan_actual_id = plan_actual_id;
	}
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public String getPeriodo_As_Str() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(periodo);
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public BigDecimal getImporteaportesocialuoma() {
		return importeaportesocialuoma;
	}
	public void setImporteaportesocialuoma(BigDecimal importeaportesocialuoma) {
		this.importeaportesocialuoma = importeaportesocialuoma;
	}
	public BigDecimal getImportearticulo46() {
		return importearticulo46;
	}
	public void setImportearticulo46(BigDecimal importearticulo46) {
		this.importearticulo46 = importearticulo46;
	}
	public BigDecimal getImportecuotaamtima() {
		return importecuotaamtima;
	}
	public void setImportecuotaamtima(BigDecimal importecuotaamtima) {
		this.importecuotaamtima = importecuotaamtima;
	}
	public BigDecimal getImportecuotasocialuoma() {
		return importecuotasocialuoma;
	}
	public void setImportecuotasocialuoma(BigDecimal importecuotasocialuoma) {
		this.importecuotasocialuoma = importecuotasocialuoma;
	}
	public BigDecimal getImportecuotausufructo() {
		return importecuotausufructo;
	}
	public void setImportecuotausufructo(BigDecimal importecuotausufructo) {
		this.importecuotausufructo = importecuotausufructo;
	}
	public BigDecimal getImporteadherenteamtima() {
		return importeadherenteamtima;
	}
	public void setImporteadherenteamtima(BigDecimal importeadherenteamtima) {
		this.importeadherenteamtima = importeadherenteamtima;
	}
	public String getEmpresa_cuit() {
		return empresa_cuit;
	}
	public void setEmpresa_cuit(String empresa_cuit) {
		this.empresa_cuit = empresa_cuit;
	}
	public String getEmpresa_razon_social() {
		return empresa_razon_social;
	}
	public void setEmpresa_razon_social(String empresa_razon_social) {
		this.empresa_razon_social = empresa_razon_social;
	}
	public String getEmpresa_provincia() {
		return empresa_provincia;
	}
	public void setEmpresa_provincia(String empresa_provincia) {
		this.empresa_provincia = empresa_provincia;
	}
	public String getEmpresa_localidad() {
		return empresa_localidad;
	}
	public void setEmpresa_localidad(String empresa_localidad) {
		this.empresa_localidad = empresa_localidad;
	}
	public String getEmpresa_planta() {
		return empresa_planta;
	}
	public void setEmpresa_planta(String empresa_planta) {
		this.empresa_planta = empresa_planta;
	}
	public String getEmpresa_calle() {
		return empresa_calle;
	}
	public void setEmpresa_calle(String empresa_calle) {
		this.empresa_calle = empresa_calle;
	}
	public String getEmpresa_numero() {
		return empresa_numero;
	}
	public void setEmpresa_numero(String empresa_numero) {
		this.empresa_numero = empresa_numero;
	}
	public String getEmpresa_piso() {
		return empresa_piso;
	}
	public void setEmpresa_piso(String empresa_piso) {
		this.empresa_piso = empresa_piso;
	}
	public String getEmpresa_depto() {
		return empresa_depto;
	}
	public void setEmpresa_depto(String empresa_depto) {
		this.empresa_depto = empresa_depto;
	}
	public String getEmpresa_codigo_postal() {
		return empresa_codigo_postal;
	}
	public void setEmpresa_codigo_postal(String empresa_codigo_postal) {
		this.empresa_codigo_postal = empresa_codigo_postal;
	}
	public String getEmpresa_telefono() {
		return empresa_telefono;
	}
	public void setEmpresa_telefono(String empresa_telefono) {
		this.empresa_telefono = empresa_telefono;
	}
	public boolean isOspim() {
		return ospim;
	}
	public void setOspim(boolean ospim) {
		this.ospim = ospim;
	}
	public boolean isUoma() {
		return uoma;
	}
	public void setUoma(boolean uoma) {
		this.uoma = uoma;
	}
	public boolean isAmtimaadherente() {
		return amtimaadherente;
	}
	public void setAmtimaadherente(boolean amtimaadherente) {
		this.amtimaadherente = amtimaadherente;
	}
	public boolean isAmtimacuota() {
		return amtimacuota;
	}
	public void setAmtimacuota(boolean amtimacuota) {
		this.amtimacuota = amtimacuota;
	}
	public boolean isUsufructo() {
		return usufructo;
	}
	public void setUsufructo(boolean usufructo) {
		this.usufructo = usufructo;
	}
	public String getPlan_que_corresponde_desc() {
		return plan_que_corresponde_desc;
	}
	public void setPlan_que_corresponde_desc(String plan_que_corresponde_desc) {
		this.plan_que_corresponde_desc = plan_que_corresponde_desc;
	}
	public Integer getPlan_que_corresponde_id() {
		return plan_que_corresponde_id;
	}
	public void setPlan_que_corresponde_id(Integer plan_que_corresponde_id) {
		this.plan_que_corresponde_id = plan_que_corresponde_id;
	}
	public int getTotal_registros() {
		return total_registros;
	}
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
	public String getNovedad_desc() {
		return novedad_desc;
	}
	public void setNovedad_desc(String novedad_desc) {
		this.novedad_desc = novedad_desc;
	}
	
	
	public int getIdSeccional() {
		return idSeccional;
	}
	public void setIdSeccional(int idSeccional) {
		this.idSeccional = idSeccional;
	}
	public String getDescSeccional() {
		return descSeccional;
	}
	public void setDescSeccional(String descSeccional) {
		this.descSeccional = descSeccional;
	}
	
	public static NovedadEmpleadorTotal getMapping(String prefix, ResultSet rs) throws SQLException{
		
		NovedadEmpleadorTotal nov = new NovedadEmpleadorTotal();
		
//		nov.setAmtimaadherente(rs.getBoolean(prefix + "amtimaadherente"));
//		nov.setAmtimacuota(rs.getBoolean(prefix + "amtimacuota"));
		nov.setCuil_titular(rs.getString(prefix+"cuil_titular"));
		nov.setInte(rs.getInt(prefix + "inte"));
		nov.setApellido(rs.getString(prefix + "apellido"));
		nov.setNombre(rs.getString(prefix + "nombre"));
		nov.setEmpresa_cuit(rs.getString(prefix+"empresa_cuit"));
		nov.setEmpresa_razon_social(rs.getString(prefix+"razon_social"));
		nov.setEmpresa_localidad(rs.getString(prefix+"localidad"));
		nov.setEmpresa_provincia(rs.getString(prefix+"provincia"));
		nov.setEmpresa_planta(rs.getString(prefix+"planta"));
		nov.setEmpresa_calle(rs.getString(prefix+"calle"));
		nov.setEmpresa_numero(rs.getString(prefix+"numero"));
		nov.setEmpresa_depto(rs.getString(prefix+"depto"));
		nov.setEmpresa_piso(rs.getString(prefix+"piso"));
		nov.setEmpresa_codigo_postal(rs.getString(prefix+"postal_codi"));
		nov.setEmpresa_telefono(rs.getString(prefix+"telefono"));
		/*nov.setImporteadherenteamtima(rs.getBigDecimal(prefix+"importeadherenteamtima"));
		nov.setImporteaportesocialuoma(rs.getBigDecimal(prefix+"importeaportesocialuoma"));
		nov.setImportearticulo46(rs.getBigDecimal(prefix+"importearticulo46"));
		nov.setImportecuotaamtima(rs.getBigDecimal(prefix+"importecuotaamtima"));
		nov.setImportecuotasocialuoma(rs.getBigDecimal(prefix+"importecuotasocialuoma"));
		nov.setImportecuotausufructo(rs.getBigDecimal(prefix +"importecuotausufructo"));*/
//		nov.setNovedad_desc(prefix +"novedad_desc");
//		nov.setOspim(rs.getBoolean(prefix + "ospim"));
//		nov.setPeriodo(rs.getDate(prefix + "periodo"));
		nov.setPlan_actual_desc(rs.getString(prefix + "plan_actual_desc"));
		nov.setPlan_actual_id(rs.getInt(prefix+"plan_actual_id"));
		nov.setPlan_que_corresponde_desc(rs.getString(prefix + "plan_que_corresponde_desc"));
		nov.setPlan_que_corresponde_id(rs.getInt(prefix + "plan_que_corresponde_id"));
//		nov.setUoma(rs.getBoolean(prefix + "uoma"));
//		nov.setUsufructo(rs.getBoolean(prefix + "usufructo"));
		nov.setTotal_registros(rs.getInt("total_registros_v"));
//		nov.setTotal_registros(50);
		nov.setIdSeccional(rs.getInt("id_seccional"));
		nov.setDescSeccional(rs.getString("desc_seccional"));
		
		
		return nov;
	}

	public String toString(){
		String detalle = "";
		detalle = "Novedad: " + getNovedad_desc() + " CuilTitular / inte: " + getCuil_titular() + " / " + getInte(); 
		return detalle;
	}
	
	/***
	 * Sobreescribo Equals y hashCode porque necesito hacer una busqueda de este objeto en la lista de resultados y no posee ID.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cuil_titular == null) ? 0 : cuil_titular.hashCode());
		result = prime * result + ((inte == null) ? 0 : inte.hashCode());
//		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
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
		NovedadEmpleadorTotal other = (NovedadEmpleadorTotal) obj;
		if (cuil_titular == null) {
			if (other.cuil_titular != null)
				return false;
		} else if (!cuil_titular.equals(other.cuil_titular))
			return false;
		if (inte == null) {
			if (other.inte != null)
				return false;
		} else if (!inte.equals(other.inte))
			return false;
//		if (periodo == null) {
//			if (other.periodo != null)
//				return false;
//		} else if (!periodo.equals(other.periodo))
//			return false;
		return true;
	}
	
	
	
	
}
