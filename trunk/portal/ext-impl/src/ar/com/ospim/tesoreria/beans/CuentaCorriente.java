package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.StringUtils;

public class CuentaCorriente {
	private Empresa empresa;
	private List<Informacion> info;

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setInfo(List<Informacion> info) {
		this.info = info;
	}

	public List<Informacion> getInfo() {
		return info;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((empresa == null) ? 0 : empresa.hashCode());
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
		CuentaCorriente other = (CuentaCorriente) obj;
		if (empresa == null) {
			if (other.empresa != null)
				return false;
		} else if (!empresa.equals(other.empresa))
			return false;
		return true;
	}

	public class Informacion {
		private Date fecha;
		private Date periodo;
		private String debitoCredito;
		private String descripcion;
		private BigDecimal importe;
		private boolean deuda;
		private Date pagadaFecha;
		private int idPago;
		private String observacionCompro;

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public String getDebitoCredito() {
			return debitoCredito;
		}

		public void setDebitoCredito(String debitoCredito) {
			this.debitoCredito = debitoCredito;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public void setPeriodo(Date periodo) {
			this.periodo = periodo;
		}

		public Date getPeriodo() {
			return periodo;
		}

		public void setDeuda(boolean deuda) {
			this.deuda = deuda;
		}

		public boolean isDeuda() {
			return deuda;
		}

		public void setPagadaFecha(Date pagadaFecha) {
			this.pagadaFecha = pagadaFecha;
		}

		public Date getPagadaFecha() {
			return pagadaFecha;
		}

		public void setIdPago(int idPago) {
			this.idPago = idPago;
		}

		public int getIdPago() {
			return idPago;
		}

		public String getObservacionCompro() {
			return observacionCompro;
		}

		public void setObservacionCompro(String observacionCompro) {
			this.observacionCompro = observacionCompro;
		}

	}

	public static CuentaCorriente getMapping(ResultSet rs,
			boolean conSeccional, boolean conPeriodo, int entidad) throws SQLException {
		return getMapping(rs, "", conSeccional, conPeriodo, entidad);
	}

	public static CuentaCorriente getMapping(ResultSet rs, String prefix,
			boolean conSeccional, boolean conPeriodo, int entidad) throws SQLException {

		String seccional = "";
		CuentaCorriente cta = new CuentaCorriente();
		String sucu = rs.getString(prefix + "sucu");
		int id_seccional = 0;
		if (conSeccional) {
			id_seccional = rs.getInt(prefix + "id_seccional");
			if (id_seccional != 0) {
				sucu = String.valueOf(id_seccional);
			}
			seccional = rs.getString(prefix + "seccional");
			if (StringUtils.checkEmpty(seccional)) {
				seccional = "";
			}
		}
		Date periodo = null;
		if (conPeriodo) {
			periodo = rs.getDate(prefix + "periodo");
		}		
		Empresa empresa=new Empresa(rs.getString(prefix + "cuit"), sucu, rs
				.getString(prefix + "razon_social")
				+ " " + seccional);
		if(id_seccional!=0){
			empresa.setId_seccional(id_seccional);
		}
		cta.setEmpresa(empresa);
		Informacion info = cta.new Informacion();
		info.setPeriodo(periodo);
		info.setDebitoCredito(rs.getString(prefix + "debito_credito"));
		info.setDescripcion(rs.getString(prefix + "descripcion"));
		info.setFecha(rs.getDate(prefix + "fecha"));
		info.setImporte(rs.getBigDecimal(prefix + "importe"));
		try {
			info.setDeuda(rs.getBoolean(prefix + "es_deuda"));
			info.setPagadaFecha(rs.getDate(prefix + "fecha_pagado"));
			info.setIdPago(rs.getInt(prefix + "id_orden_pago_pagado"));
			if(entidad==WebKeysGlobal.UOMA){
				info.setObservacionCompro(rs.getString(prefix+"compro_obs"));
			}
		} catch (Exception e) {

		}
		cta.setInfo(new ArrayList<Informacion>());
		if (info.getFecha() != null) {
			cta.getInfo().add(info);
		}
		return cta;
	}
	
}
