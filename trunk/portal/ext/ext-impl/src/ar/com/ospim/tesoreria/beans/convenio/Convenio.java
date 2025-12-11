package ar.com.ospim.tesoreria.beans.convenio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.Acta.ActaPagoIngresado;
import ar.com.ospim.util.DateUtils;

public class Convenio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8087035559246519446L;
	private int id;
	private String numero;
	private Empresa empresa;
	private Date fechaInicio;
	private Date fechaPago;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private BigDecimal interes;
	private BigDecimal ajusteCapital;
	private BigDecimal ajusteInteres;
	private List<ConvenioPago> pagos;
	private List<ActaRelacionada> actasRelacionadas;
	private BigDecimal deudaActasRelacionadas;
	private BigDecimal deudaConveniosRelacionados;
	private List<ConvenioPagoIngresado> pagosIngresados;
	private String entidad;
	private ConvenioEstadoSeguimiento estadoSeguimiento;

	public Convenio(int id) {
		this.id = id;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public BigDecimal getDeudaActasRelacionadas() {
		return deudaActasRelacionadas;
	}

	public void setDeudaActasRelacionadas(BigDecimal saldo) {
		this.deudaActasRelacionadas = saldo;
	}

	public Convenio() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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

	public String getAlta_ip() {
		return alta_ip;
	}

	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
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

	public String getModi_ip() {
		return modi_ip;
	}

	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
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

	public String getBaja_ip() {
		return baja_ip;
	}

	public void setBaja_ip(String bajaIp) {
		baja_ip = bajaIp;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getAjusteCapital() {
		return ajusteCapital;
	}

	public void setAjusteCapital(BigDecimal ajuste) {
		this.ajusteCapital = ajuste;
	}

	public BigDecimal getDeudaFromActasRelacionadas() {
		BigDecimal deudaActas = new BigDecimal("0");
		if (actasRelacionadas != null) {
			for (ActaRelacionada acta : actasRelacionadas) {
				if (acta.getId() < 0) {
					continue;
				}
				BigDecimal saldo = acta.getSaldo();
				if (saldo != null) {
					deudaActas = deudaActas.add(saldo);
				}
			}
		}
		return deudaActas;
	}

	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal("0");
		if (interes != null) {
			total = total.add(interes);
		}
		if (deudaActasRelacionadas != null) {
			total = total.add(deudaActasRelacionadas);
		}

		if (ajusteInteres != null) {
			total = total.add(ajusteInteres);
		}

		if (ajusteCapital != null) {
			total = total.add(ajusteCapital);
		}
		return total;
	}

	public String getFechaInicioAsString() {
		return null != fechaInicio ? DateUtils.format(fechaInicio,
				DateUtils.SHORT) : "";
	}

	public String getFechaPagoAsString() {
		return null != fechaPago ? DateUtils.format(fechaPago, DateUtils.SHORT)
				: "";
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	public static Convenio getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs);
	}

	public static Convenio getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Convenio conv = new Convenio();
		conv.setId(rs.getInt(prefix + "id"));
		conv.setNumero(rs.getString(prefix + "numero"));
		conv.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		conv.setFechaInicio(rs.getDate(prefix + "fecha_inicio"));
		conv.setFechaPago(rs.getDate(prefix + "fecha_pago"));
		conv.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		conv.setAlta_usr(rs.getString(prefix + "alta_usr"));
		conv.setAlta_ip(rs.getString(prefix + "alta_ip"));
		conv.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		conv.setModi_usr(rs.getString(prefix + "modi_usr"));
		conv.setModi_ip(rs.getString(prefix + "modi_ip"));
		conv.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		conv.setBaja_usr(rs.getString(prefix + "baja_usr"));
		conv.setBaja_ip(rs.getString(prefix + "baja_ip"));
		conv.setInteres(rs.getBigDecimal(prefix + "interes"));
		conv.setAjusteCapital(rs.getBigDecimal(prefix + "ajuste_capital"));
		conv.setAjusteInteres(rs.getBigDecimal(prefix + "ajuste_interes"));
		conv.setDeudaConveniosRelacionados(rs.getBigDecimal(prefix
				+ "deuda_convenios_asociados"));
		conv.setDeudaActasRelacionadas(rs.getBigDecimal(prefix
				+ "deuda_actas_asociadas"));
		
		ConvenioEstadoSeguimiento ces = null;
		try{
			int idEstado = rs.getInt(prefix + "id_estado");
			ces = new ConvenioEstadoSeguimiento(idEstado, "");
		}catch (Exception e) {
			//nada dejo el null;
		}
		conv.setEstadoSeguimiento(ces);
		return conv;
	}

	public static Convenio getMappingNoOS(ResultSet rs, String prefix)
			throws SQLException {
		Convenio conv = new Convenio();
		conv.setId(rs.getInt(prefix + "id"));
		conv.setNumero(rs.getString(prefix + "numero"));
		conv.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		conv.setFechaInicio(rs.getDate(prefix + "fecha_inicio"));
		conv.setFechaPago(rs.getDate(prefix + "fecha_pago"));
		conv.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		conv.setAlta_usr(rs.getString(prefix + "alta_usr"));
		conv.setAlta_ip(rs.getString(prefix + "alta_ip"));
		conv.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		conv.setModi_usr(rs.getString(prefix + "modi_usr"));
		conv.setModi_ip(rs.getString(prefix + "modi_ip"));
		conv.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		conv.setBaja_usr(rs.getString(prefix + "baja_usr"));
		conv.setBaja_ip(rs.getString(prefix + "baja_ip"));
		conv.setInteres(rs.getBigDecimal(prefix + "interes"));
		conv.setAjusteCapital(rs.getBigDecimal(prefix + "ajuste_capital"));
		conv.setAjusteInteres(rs.getBigDecimal(prefix + "ajuste_interes"));
		conv.setDeudaConveniosRelacionados(rs.getBigDecimal(prefix
				+ "deuda_convenios_asociados"));
		conv.setDeudaActasRelacionadas(rs.getBigDecimal(prefix
				+ "deuda_actas_asociadas"));
		conv.setEntidad(rs.getString(prefix + "entidad"));
		
		ConvenioEstadoSeguimiento ces = null;
		try{
			int idEstado = rs.getInt(prefix + "id_estado");
			ces = new ConvenioEstadoSeguimiento(idEstado, "");
		}catch (Exception e) {
			//nada dejo el null;
		}
		conv.setEstadoSeguimiento(ces);
		
		return conv;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setPagos(List<ConvenioPago> pagos) {
		this.pagos = pagos;
	}

	public List<ConvenioPago> getPagos() {
		return pagos;
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
		Convenio other = (Convenio) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static class ActaRelacionada {
		private Convenio convenio;
		private Acta actaRelacionada;
		private BigDecimal importe;
		private BigDecimal saldo;
		private Date alta_fecha;
		private String alta_usr;
		private String alta_ip;
		private Date modi_fecha;
		private String modi_usr;
		private String modi_ip;
		private Date baja_fecha;
		private String baja_usr;
		private String baja_ip;
		private int id;
		private boolean borradoLogico = false;

		public ActaRelacionada() {
		}

		public ActaRelacionada(int idInt) {
			this.id = idInt;
		}

		public ActaRelacionada(Convenio convenio2, BigDecimal importe, Acta acta) {
			this.convenio = convenio2;
			this.importe = importe;
			this.actaRelacionada = acta;
		}

		public Convenio getConvenio() {
			return convenio;
		}

		public void setConvenio(Convenio convenio) {
			this.convenio = convenio;
		}

		public Acta getActaRelacionada() {
			return actaRelacionada;
		}

		public void setActaRelacionada(Acta actaRelacionada) {
			this.actaRelacionada = actaRelacionada;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public BigDecimal getSaldo() {
			return saldo;
		}

		public void setSaldo(BigDecimal saldo) {
			this.saldo = saldo;
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

		public String getAlta_ip() {
			return alta_ip;
		}

		public void setAlta_ip(String altaIp) {
			alta_ip = altaIp;
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

		public String getModi_ip() {
			return modi_ip;
		}

		public void setModi_ip(String modiIp) {
			modi_ip = modiIp;
		}

		public Date getBaja_fecha() {
			return baja_fecha;
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

		public String getBaja_ip() {
			return baja_ip;
		}

		public void setBaja_ip(String bajaIp) {
			baja_ip = bajaIp;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setBorradoLogico(boolean borradoLogico) {
			this.borradoLogico = borradoLogico;
		}

		public boolean isBorradoLogico() {
			return borradoLogico;
		}

		public static ActaRelacionada getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static ActaRelacionada getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ActaRelacionada actaRelacionada = new ActaRelacionada();
			actaRelacionada.setId(rs.getInt(prefix + "id"));
			actaRelacionada.setConvenio(new Convenio(rs.getInt(prefix
					+ "convenio_id")));
			actaRelacionada.setActaRelacionada(new Acta(rs.getInt(prefix
					+ "acta_id")));
			actaRelacionada.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
			actaRelacionada.setAlta_usr(rs.getString(prefix + "alta_usr"));
			actaRelacionada.setAlta_ip(rs.getString(prefix + "alta_ip"));
			actaRelacionada.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
			actaRelacionada.setModi_usr(rs.getString(prefix + "modi_usr"));
			actaRelacionada.setModi_ip(rs.getString(prefix + "modi_ip"));
			actaRelacionada.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
			actaRelacionada.setBaja_usr(rs.getString(prefix + "baja_usr"));
			actaRelacionada.setBaja_ip(rs.getString(prefix + "baja_ip"));
			actaRelacionada.setImporte(rs.getBigDecimal(prefix + "importe"));
			actaRelacionada.setSaldo(rs.getBigDecimal(prefix + "saldo"));
			return actaRelacionada;
		}
	}

	public void setActasRelacionadas(List<ActaRelacionada> actas) {
		this.actasRelacionadas = actas;

	}

	public List<ActaRelacionada> getActasRelacionadas() {
		return this.actasRelacionadas;

	}

	public void setDeudaConveniosRelacionados(
			BigDecimal deudaConveniosRelacionados) {
		this.deudaConveniosRelacionados = deudaConveniosRelacionados;
	}

	public BigDecimal getDeudaConveniosRelacionados() {
		return deudaConveniosRelacionados;
	}

	public BigDecimal getInteresFromPagos() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ConvenioPago p : pagos) {
				if (p.getTipo().equals(ConvenioPago.Tipo.PAGO)
						&& p.getInteres() != null) {
					total = total.add(p.getInteres());
				}
			}
		}
		return total;
	}

	public BigDecimal getCapitalFromPagos() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ConvenioPago p : pagos) {
				if (p.getTipo().equals(ConvenioPago.Tipo.PAGO)
						&& p.getImporte() != null) {
					total = total.add(p.getImporte());
				}
			}
		}
		return total;
	}

	public BigDecimal getTotalPagadoPorConvenios() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ConvenioPago ap : pagos) {
				if (ap.getTipo().equals(ConvenioPago.Tipo.PAGO)
						&& ap.getConvenioCancelatorio() != null) {
					total = total.add(ap.getImporte().add(
							ap.getInteres() != null ? ap.getInteres()
									: BigDecimal.ZERO));
				}
			}
		}
		return total;
	}

	public BigDecimal getTotalPagadoIngresado() {
		BigDecimal total = new BigDecimal("0");
		if (getPagosIngresados() != null) {
			for (ConvenioPagoIngresado ap : getPagosIngresados()) {
				total = total.add(ap.getImporte());
			}
		}
		return total;
	}

	public BigDecimal getTotalConvenioPagosChequeNoIngresados() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ConvenioPago ap : pagos) {
				if (ap.getTipo().equals(ConvenioPago.Tipo.PAGO) && ap.getRecibo() == null) {
						//&& ap.getCheque() != null && ap.getRecibo() == null) {
					total = total.add(ap.getImporte()==null?ap.getCheque().getImporte():ap.getImporte());
				}
			}
			total = total.subtract(getTotalPagosIngresados());
		}
		return total;
	}

	public BigDecimal getTotalConvenioPagosIngresados() {
		/*
		 * BigDecimal total = new BigDecimal("0"); if (pagos != null) { for
		 * (ConvenioPago ap : pagos) { if
		 * (ap.getTipo().equals(ConvenioPago.Tipo.PAGO) && ap.getRecibo() ==
		 * null) { total = total.add(ap.getImporte()); } } } return total;
		 */
		return getTotalPagosIngresados();
	}

	public void setTotalConvenioPagosChequeNoIngresados(
			BigDecimal importePorCheques) {
		// agrego un cheque ficticio en representacion de todos los cheques
		// individuales que conforman el total
		// esto lo utilizo en la pagina de recibos/ingresos
		if (importePorCheques != null) {
			ConvenioPago cp = new ConvenioPago();
			cp.setTipo(ConvenioPago.Tipo.PAGO);
			List<ConvenioPago> cps = new ArrayList<ConvenioPago>(1);
			Cheque cheque = new Cheque();
			cheque.setImporte(importePorCheques);
			cp.setCheque(cheque);
			cps.add(cp);
			pagos = cps;
		}
	}

	public void setPagosIngresados(List<ConvenioPagoIngresado> pagosIngresados) {
		this.pagosIngresados = pagosIngresados;
	}

	public BigDecimal getTotalPagosIngresados() {
		BigDecimal result = BigDecimal.ZERO;
		if (pagosIngresados != null) {
			for (ConvenioPagoIngresado pago : getPagosIngresados()) {
				result = result.add(pago.getImporte());
			}
		}
		return result;
	}

	public List<ConvenioPagoIngresado> getPagosIngresados() {
		return pagosIngresados;

	}

	public void setAjusteInteres(BigDecimal ajusteInteres) {
		this.ajusteInteres = ajusteInteres;
	}

	public BigDecimal getAjusteInteres() {
		return ajusteInteres;
	}

	public static class ConvenioPagoIngresado {
		private Recibo recibo;
		private BigDecimal importe;
		private Date fechaPagado;
		private Cheque cheque;

		public ConvenioPagoIngresado() {

		}

		public ConvenioPagoIngresado(Recibo recibo,
				BigDecimal importeDelConvenio) {
			this.recibo = recibo;
			this.importe = importeDelConvenio;
		}

		public ConvenioPagoIngresado(Recibo recibo,
				BigDecimal importeDelConvenio, BigDecimal nroCheque, int idBanco) {
			this.recibo = recibo;
			this.importe = importeDelConvenio;
			if (null != nroCheque) {
				this.cheque = new Cheque(nroCheque, idBanco);
			}
		}

		public Recibo getRecibo() {
			return recibo;
		}

		public void setRecibo(Recibo recibo) {
			this.recibo = recibo;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public void setFechaPagado(Date fechaPagado) {
			this.fechaPagado = fechaPagado;
		}

		public Date getFechaPagado() {
			return fechaPagado;
		}
		

		public Cheque getCheque() {
			return cheque;
		}

		public void setCheque(Cheque cheque) {
			this.cheque = cheque;
		}

		public static ActaPagoIngresado getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static ActaPagoIngresado getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ActaPagoIngresado acta = new ActaPagoIngresado();
			acta.setRecibo(new Recibo(rs.getInt(prefix + "recibo_id")));
			acta.setImporte(rs.getBigDecimal(prefix + "importe"));
			acta.setFechaPagado(rs.getDate(prefix + "fecha_pagado"));
			return acta;
		}
	}

	public boolean containsChequeIngresado(Cheque ch) {
		if (null != getPagosIngresados()) {
			for (ConvenioPagoIngresado co : getPagosIngresados()) {
				if(null!=co.getCheque()&&co.getCheque().getBanco().getId_banco()==ch.getBanco().getId_banco()){
					if(co.getCheque().getNumero().compareTo(ch.getNumero())==0){
						return true;
					}
				}
			}
		}
		return false;
	}

	public ConvenioEstadoSeguimiento getEstadoSeguimiento() {
		return estadoSeguimiento;
	}

	public void setEstadoSeguimiento(ConvenioEstadoSeguimiento estadoSeguimiento) {
		this.estadoSeguimiento = estadoSeguimiento;
	}

}
