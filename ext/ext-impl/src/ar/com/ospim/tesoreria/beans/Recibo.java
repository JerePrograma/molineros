package ar.com.ospim.tesoreria.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.util.DateUtils;

public class Recibo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String numero;
	private Empresa empresa;
	private Seccional seccional;
	private Date fecha;
	private String observaciones;
	private BigDecimal importe;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String entidad;
	private Afiliado afiliado;

	private List<ReciboIngreso> ingresos;
	private List<ReciboActa> actas;
	private List<ReciboConvenio> convenios;
	private List<ReciboCheque> chequesNoDepositados;
	private List<ReciboCheque> chequesRechazados;
	private List<ReciboOtroConcepto> otrosConceptos;
	private List<ReciboPrestamo>prestamos;

	public Recibo() {
	}

	public Recibo(int id) {
		this.id = id;
	}

	public Recibo(Recibo recibo) {
		this.id = recibo.getId();
		this.numero = recibo.getNumero();
		this.empresa = recibo.getEmpresa();
		this.fecha = recibo.getFecha();
		this.observaciones = recibo.getObservaciones();
		this.importe = recibo.getImporte();
		this.alta_fecha = recibo.getAlta_fecha();
		this.alta_usr = recibo.getAlta_usr();
		this.modi_fecha = recibo.getModi_fecha();
		this.modi_usr = recibo.getModi_usr();
		this.baja_fecha = recibo.getBaja_fecha();
		this.baja_usr = recibo.getBaja_usr();

		this.ingresos = recibo.getIngresos();
		this.actas = recibo.getActas();
		this.convenios = recibo.getConvenios();
		this.chequesNoDepositados = recibo.getChequesNoDepositados();
		this.chequesRechazados = recibo.getChequesRechazados();
		this.otrosConceptos = recibo.getOtrosConceptos();
		this.prestamos=recibo.getReciboPrestamos();
	}

	public List<ReciboActa> getActas() {
		return actas;
	}

	public void setActas(List<ReciboActa> actas) {
		this.actas = actas;
	}

	public List<ReciboConvenio> getConvenios() {
		return convenios;
	}

	public void setConvenios(List<ReciboConvenio> convenios) {
		this.convenios = convenios;
	}

	public List<ReciboCheque> getChequesNoDepositados() {
		return chequesNoDepositados;
	}

	public void setChequesNoDepositados(List<ReciboCheque> chequesNoDepositados) {
		this.chequesNoDepositados = chequesNoDepositados;
	}

	public List<ReciboCheque> getChequesRechazados() {
		return chequesRechazados;
	}

	public void setChequesRechazados(List<ReciboCheque> chequesRechazados) {
		this.chequesRechazados = chequesRechazados;
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

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getFechaAsString() {
		return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT) : "";
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public static Recibo getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Recibo getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Recibo recibo = new Recibo();

		recibo.setId(rs.getInt(prefix + "id"));
		recibo.setNumero(rs.getString(prefix + "numero"));
		recibo.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		recibo.setFecha(rs.getDate(prefix + "fecha"));
		recibo.setObservaciones(rs.getString(prefix + "descripcion"));
		recibo.setImporte(rs.getBigDecimal(prefix + "importe"));
		recibo.setAlta_usr(rs.getString(prefix + "alta_usr"));
		recibo.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		recibo.setModi_usr(rs.getString(prefix + "modi_usr"));
		recibo.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		recibo.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		recibo.setBaja_usr(rs.getString(prefix + "baja_usr"));
		
		int seccional = rs.getInt(prefix + "id_seccional");
		recibo.setSeccional(new Seccional(seccional, null));

		
		return recibo;
	}
	
	public static Recibo getMapping_no_os(ResultSet rs, String prefix)
			throws SQLException {
		Recibo recibo = new Recibo();

		recibo.setId(rs.getInt(prefix + "id"));
		recibo.setNumero(rs.getString(prefix + "numero"));
		recibo.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		recibo.setFecha(rs.getDate(prefix + "fecha"));
		recibo.setObservaciones(rs.getString(prefix + "descripcion"));
		recibo.setImporte(rs.getBigDecimal(prefix + "importe"));
		recibo.setAlta_usr(rs.getString(prefix + "alta_usr"));
		recibo.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		recibo.setModi_usr(rs.getString(prefix + "modi_usr"));
		recibo.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		recibo.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		recibo.setBaja_usr(rs.getString(prefix + "baja_usr"));
		recibo.setEntidad(rs.getString(prefix + "entidad"));
		
		int seccional = rs.getInt(prefix + "id_seccional");
		recibo.setSeccional(new Seccional(seccional, null));

		
		return recibo;
	}

	public void setOtrosConceptos(List<ReciboOtroConcepto> otrosConceptos) {
		this.otrosConceptos = otrosConceptos;
	}

	public List<ReciboOtroConcepto> getOtrosConceptos() {
		return otrosConceptos;
	}

	public void setIngresos(List<ReciboIngreso> ingresos) {
		this.ingresos = ingresos;
	}

	public List<ReciboIngreso> getIngresos() {
		return ingresos;
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
		Recibo other = (Recibo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public List<ReciboPrestamo> getReciboPrestamos() {
		return prestamos;
	}

	public void setReciboPrestamos(List<ReciboPrestamo> prestamos) {
		this.prestamos = prestamos;
	}
	
	
}
