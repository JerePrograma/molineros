package ar.com.ospim.tesoreria.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.util.DateUtils;

public class AjustePlanSuperador implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String descripcion;
	private Date fechaDesde;
	private Date fechaHasta;
	private Integer edadDesde;
	private Integer edadHasta;
	private BigDecimal importe;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String planesString;
	private String parentescosString;
	private String provinciasString;
	private String afiliadosString;
	
	private List<Plan> planes;
	private List<Parentesco> parentescos;
	private List<Provincia> provincias;
	private List<Afiliado> afiliados;
    private Double porcentaje;
    private Boolean soloUsoPersonalizado;
    	
	public AjustePlanSuperador() {
		planes =new ArrayList<Plan>();
		parentescos=new ArrayList<Parentesco>();
		provincias=new ArrayList<Provincia>();
		afiliados=new ArrayList<Afiliado>();
		
	}

	public AjustePlanSuperador(Integer id) {
		this.id = id;
	}

	

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	
	public String getFechaDesdeAsString() {
		return null != fechaDesde ? DateUtils.format(fechaDesde, DateUtils.SHORT) : "";
	}
	
	public String getFechaHastaAsString() {
		return null != fechaHasta ? DateUtils.format(fechaHasta, DateUtils.SHORT) : "";
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Integer getEdadDesde() {
		return edadDesde;
	}

	public void setEdadDesde(Integer edadDesde) {
		this.edadDesde = edadDesde;
	}

	public Integer getEdadHasta() {
		return edadHasta;
	}

	public void setEdadHasta(Integer edadHasta) {
		this.edadHasta = edadHasta;
	}

	public List<Plan> getPlanes() {
		return planes;
	}

	public void setPlanes(List<Plan> planes) {
		this.planes = planes;
	}
	
	public List<Parentesco> getParentescos() {
		return parentescos;
	}

	public void setParentescos(List<Parentesco> parentescos) {
		this.parentescos = parentescos;
	}

	public List<Provincia> getProvincias() {
		return provincias;
	}

	public void setProvincias(List<Provincia> provincias) {
		this.provincias = provincias;
	}
	
	public String getPlanesString() {
		return planesString;
	}

	public void setPlanesString(String planesString) {
		this.planesString = planesString;
	}

	public String getParentescosString() {
		return parentescosString;
	}

	public void setParentescosString(String parentescosString) {
		this.parentescosString = parentescosString;
	}

	public String getProvinciasString() {
		return provinciasString;
	}

	public void setProvinciasString(String provinciasString) {
		this.provinciasString = provinciasString;
	}

	
	public List<Afiliado> getAfiliados() {
		return afiliados;
	}

	public void setAfiliados(List<Afiliado> afiliados) {
		this.afiliados = afiliados;
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}
	
	public Boolean getSoloUsoPersonalizado() {
		return soloUsoPersonalizado;
	}

	public void setSoloUsoPersonalizado(Boolean soloUsoPersonalizado) {
		this.soloUsoPersonalizado = soloUsoPersonalizado;
	}

	public static AjustePlanSuperador getMapping(ResultSet rs, String prefix) throws SQLException {
		AjustePlanSuperador p = new AjustePlanSuperador();
		
		p.setId(rs.getInt(prefix + "id"));
		p.setDescripcion(rs.getString(prefix + "descripcion"));
		p.setFechaDesde( rs.getDate(prefix + "vigente_desde"));
		p.setFechaHasta(rs.getDate(prefix + "vigente_hasta"));
		p.setEdadDesde(rs.getInt(prefix+"edad_desde"));
		p.setEdadHasta(rs.getInt(prefix+"edad_hasta"));
		p.setPorcentaje(rs.getDouble(prefix +"porcentaje"));
		p.setImporte(rs.getBigDecimal(prefix +"importe"));
		
		try {
		  p.setPlanesString(rs.getString(prefix+"planes"));
		}catch(Exception e) {}  
		
		try {
		  p.setParentescosString(rs.getString(prefix+"parentescos"));
		}catch(Exception e) {}  
		
		
		try {
			  p.setProvinciasString(rs.getString(prefix+"provincias"));
		}catch(Exception e) {}  
		
		try {
			  p.setAfiliadosString(rs.getString(prefix+"cuiles"));
		}catch(Exception e) {}  
		
		try {
		   p.setSoloUsoPersonalizado(rs.getBoolean(prefix + "solo_uso_personalizado"));
		}catch(Exception e) {}    
		return p;
	}

	
	public String getAfiliadosString() {
		return afiliadosString;
	}

	private void setAfiliadosString(String afiliadosString) {
		this.afiliadosString=afiliadosString;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AjustePlanSuperador other = (AjustePlanSuperador) obj;
		if (id != other.id)
			return false;
		return true;
	}

		
	
}
