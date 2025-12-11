package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.tesoreria.beans.convenio.Convenio;

public class ReporteActaBean {
	private Integer id;
	private String numero;
	private Date fechaActa;
	private Date fechaActualizacion;
	private Date fechaRecepcion;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private String entidad;
	private BigDecimal capital;
	private BigDecimal interes;
	private BigDecimal otros;
	private BigDecimal deudaActasAsociadas;
	private String numeroActaAsociada;
	private BigDecimal total;
	private Boolean molinera;
	private int periodos;
	private int promedioEmpleados;
	private int promedioPagados;
	private BigDecimal totalRemuneraciones;
	private BigDecimal totalDeuda;
	private BigDecimal totalCalculado;
	private BigDecimal totalPagado;
	private BigDecimal totalInteres;
	private String inspectores;
	private String estado;
	private BigDecimal socialUsufructo;
	private BigDecimal art46;
	private BigDecimal solidario;
	
	//Agregado para reporte estadistico entre actas y convenios
	private String cobrado; // SI - NO - CONVENIO
	private Convenio convenio;
	private Double convenioCobrado;
	private Double convenioNoCobrado;
	private Double convenioAVencer;
	
	private Double cantidadConvenioCobrado;
	private Double cantidadConvenioNoCobrado;
	private Double cantidadConvenioAVencer;
	
	

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaActa() {
		return fechaActa;
	}

	public void setFechaActa(Date fechaActa) {
		this.fechaActa = fechaActa;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getOtros() {
		return otros;
	}

	public void setOtros(BigDecimal otros) {
		this.otros = otros;
	}

	public BigDecimal getDeudaActasAsociadas() {
		return deudaActasAsociadas;
	}

	public void setDeudaActasAsociadas(BigDecimal deudaActasAsociadas) {
		this.deudaActasAsociadas = deudaActasAsociadas;
	}

	public String getNumeroActaAsociada() {
		return numeroActaAsociada;
	}

	public void setNumeroActaAsociada(String numeroActaAsociada) {
		this.numeroActaAsociada = numeroActaAsociada;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Boolean getMolinera() {
		return molinera;
	}

	public void setMolinera(Boolean molinera) {
		this.molinera = molinera;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static ReporteActaBean getMapping(ResultSet rs, boolean seguimiento)
			throws SQLException {
		ReporteActaBean reporte = new ReporteActaBean();
		map(rs, reporte, seguimiento);
		return reporte;
	}

	protected static void map(ResultSet rs, ReporteActaBean reporte,
			boolean seguimiento) throws SQLException {
		reporte.setNumero(rs.getString("numero"));
		reporte.setFechaActa(rs.getDate("fecha_acta"));
		reporte.setFechaActualizacion(rs.getDate("fecha_actualizacion"));
		reporte.setFechaRecepcion(rs.getDate("fecha_recepcion"));
		reporte.setCuit(rs.getString("cuit"));
		reporte.setSucursal(rs.getString("sucursal"));
		reporte.setRazonSocial(rs.getString("razon_soc"));
		reporte.setCapital(rs.getBigDecimal("capital"));
		reporte.setInteres(rs.getBigDecimal("interes"));
		reporte.setOtros(rs.getBigDecimal("otros"));
		reporte.setDeudaActasAsociadas(rs
				.getBigDecimal("deuda_actas_asociadas"));
		reporte.setNumeroActaAsociada(rs.getString("numero_acta_asoc"));
		reporte.setTotal(rs.getBigDecimal("acta_total"));
		reporte.setMolinera(rs.getBoolean("molinera"));
		try {
			reporte.setPeriodos(rs.getInt("periodos"));
			reporte.setPromedioEmpleados(rs.getInt("promedio_empleados"));
			reporte.setPromedioPagados(rs.getInt("promedio_pagados"));
			reporte.setTotalRemuneraciones(rs
					.getBigDecimal("total_remuneracion"));

			reporte.setTotalDeuda(rs.getBigDecimal("total_deuda"));
			reporte.setTotalCalculado(rs.getBigDecimal("total_calculado"));
			reporte.setTotalPagado(rs.getBigDecimal("total_pagado"));

			if (reporte.getTotalPagado() == null) {
				reporte.setTotalPagado(BigDecimal.ZERO);
			}
			reporte.setTotalInteres(rs.getBigDecimal("total_interes"));
			if (seguimiento) {
				reporte.setEntidad(rs.getString("entidad"));
			} else {
				reporte.setEntidad("OSPIM");
			}
			reporte.setInspectores(rs.getString("inspectores"));
			if (reporte.getEntidad().trim().equals("U.O.M.A.")
					|| reporte.getEntidad().trim().equals("A.M.T.I.M.A.")) {
				reporte.setEstado(rs.getString("estado"));
				if (reporte.getEntidad().trim().equals("U.O.M.A.")) {
					reporte.setSocialUsufructo(rs
							.getBigDecimal("social_usufructo"));
					reporte.setArt46(rs.getBigDecimal("art_46"));
					reporte.setSolidario(rs.getBigDecimal("solidario"));
				}
			}

		} catch (Exception e) {

		}
		try{
			reporte.setId(rs.getInt("id"));
		} catch (Exception e) {

		}

	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public int getPeriodos() {
		return periodos;
	}

	public void setPeriodos(int periodos) {
		this.periodos = periodos;
	}

	public int getPromedioEmpleados() {
		return promedioEmpleados;
	}

	public void setPromedioEmpleados(int promedioEmpleados) {
		this.promedioEmpleados = promedioEmpleados;
	}

	public int getPromedioPagados() {
		return promedioPagados;
	}

	public void setPromedioPagados(int promedioPagados) {
		this.promedioPagados = promedioPagados;
	}

	public BigDecimal getTotalRemuneraciones() {
		return totalRemuneraciones;
	}

	public void setTotalRemuneraciones(BigDecimal totalRemuneraciones) {
		this.totalRemuneraciones = totalRemuneraciones;
	}

	public BigDecimal getTotalDeuda() {
		return totalDeuda;
	}

	public void setTotalDeuda(BigDecimal totalDeuda) {
		this.totalDeuda = totalDeuda;
	}

	public BigDecimal getTotalCalculado() {
		return totalCalculado;
	}

	public void setTotalCalculado(BigDecimal totalCalculado) {
		this.totalCalculado = totalCalculado;
	}

	public BigDecimal getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(BigDecimal totalPagado) {
		this.totalPagado = totalPagado;
	}

	public BigDecimal getTotalInteres() {
		return totalInteres;
	}

	public void setTotalInteres(BigDecimal totalInteres) {
		this.totalInteres = totalInteres;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getInspectores() {
		return inspectores;
	}

	public void setInspectores(String inspectores) {
		this.inspectores = inspectores;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public BigDecimal getSocialUsufructo() {
		return socialUsufructo;
	}

	public void setSocialUsufructo(BigDecimal socialUsufructo) {
		this.socialUsufructo = socialUsufructo;
	}

	public BigDecimal getArt46() {
		return art46;
	}

	public void setArt46(BigDecimal art46) {
		this.art46 = art46;
	}

	public BigDecimal getSolidario() {
		return solidario;
	}

	public void setSolidario(BigDecimal solidario) {
		this.solidario = solidario;
	}

	public String getCobrado() {
		return cobrado;
	}

	public void setCobrado(String cobrado) {
		this.cobrado = cobrado;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	public Double getConvenioCobrado() {
		return convenioCobrado;
	}

	public void setConvenioCobrado(Double convenioCobrado) {
		this.convenioCobrado = convenioCobrado;
	}

	public Double getConvenioNoCobrado() {
		return convenioNoCobrado;
	}

	public void setConvenioNoCobrado(Double convenioNoCobrado) {
		this.convenioNoCobrado = convenioNoCobrado;
	}

	public Double getConvenioAVencer() {
		return convenioAVencer;
	}

	public void setConvenioAVencer(Double convenioAVencer) {
		this.convenioAVencer = convenioAVencer;
	}

	public Double getCantidadConvenioCobrado() {
		return cantidadConvenioCobrado;
	}

	public void setCantidadConvenioCobrado(Double cantidadConvenioCobrado) {
		this.cantidadConvenioCobrado = cantidadConvenioCobrado;
	}

	public Double getCantidadConvenioNoCobrado() {
		return cantidadConvenioNoCobrado;
	}

	public void setCantidadConvenioNoCobrado(Double cantidadConvenioNoCobrado) {
		this.cantidadConvenioNoCobrado = cantidadConvenioNoCobrado;
	}

	public Double getCantidadConvenioAVencer() {
		return cantidadConvenioAVencer;
	}

	public void setCantidadConvenioAVencer(Double cantidadConvenioAVencer) {
		this.cantidadConvenioAVencer = cantidadConvenioAVencer;
	}
	
	

}
