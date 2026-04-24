package ar.com.ospim.tesoreria.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.PortletRequest;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Acta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2039399846177240447L;

	private int id;
	private String numero;
	private Date fechaInicio;
	private Date fechaPago;
	private Empresa empresa;
	private List<Inspector> inspectoresFirmantes;
	private List<ActaRelacionada> actasRelacionadas;
	private List<DetalleActaInspectores> detallesActas;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private BigDecimal otros;
	private BigDecimal interes;
	private BigDecimal capital;
	private BigDecimal deudaActasRelacionadas;
	private Date cierre_fecha;
	private String cierre_usr;
	private boolean inspector = false;
	private List<ActaPeriodoDeudaEmpresa> periodos;
	private List<ActaPago> pagos;
	private List<ActaPagoIngresado> pagosIngresados;
	private boolean actaCerrada = false;
	transient private Date periodoInicial;
	transient private Date periodoFinal;
	private boolean molinera;
	private String entidad;
	private String estado;
	private ActaEstadoSeguimiento estadoSeguimiento ;

	// UOMA
	private BigDecimal calculadoSindicato;
	private BigDecimal capitalSindicato;
	private BigDecimal interesSindicato;
	private BigDecimal calculadoSolidario;
	private BigDecimal capitalSolidario;
	private BigDecimal interesSolidario;
	private BigDecimal calculadoUsufructo;
	private BigDecimal capitalUsufructo;
	private BigDecimal interesUsufructo;
	private BigDecimal calculadoArt46;
	private BigDecimal capitalArt46;
	private BigDecimal interesArt46;

	// AMTIMA
	private BigDecimal calculadoAmtima;
	private BigDecimal capitalAmtima;
	private BigDecimal interesAmtima;

	private List<TotalActaNoOS> totalesActas;

	public Acta() {
	}

	public Acta(int actaIdInt) {
		this.id = actaIdInt;
	}

	public Acta(int actaIdInt, String actaNro, Date fechaPago, String cuit) {
		this.id = actaIdInt;
		this.numero = actaNro;
		this.fechaPago = fechaPago;
		this.empresa = new Empresa(cuit);
	}

	public BigDecimal getTotalCapital() {
		BigDecimal total = new BigDecimal("0");
		if (null != capitalSindicato) {
			total = total.add(capitalSindicato);
		}
		if (null != capitalSolidario) {
			total = total.add(capitalSolidario);
		}
		if (null != capitalUsufructo) {
			total = total.add(capitalUsufructo);
		}
		if (null != capitalArt46) {
			total = total.add(capitalArt46);
		}
		// if (null != capital) { // viene acumulado de la BD
		// total = total.add(capital);
		// }
		return total;
	}

	public BigDecimal getTotalInteres() {
		BigDecimal total = new BigDecimal("0");
		if (null != interesSindicato) {
			total = total.add(interesSindicato);
		}
		if (null != interesSolidario) {
			total = total.add(interesSolidario);
		}
		if (null != interesUsufructo) {
			total = total.add(interesUsufructo);
		}
		if (null != interesArt46) {
			total = total.add(interesArt46);
		}
		// if (null != interes) { // viene acumulado de la BD
		// total = total.add(interes);
		// }
		return total;
	}

	public BigDecimal getCapitalSindicato() {
		return capitalSindicato != null ? capitalSindicato : BigDecimal.ZERO;
	}

	public void setCapitalSindicato(BigDecimal capitalSindicato) {
		this.capitalSindicato = capitalSindicato;
	}

	public BigDecimal getInteresSindicato() {
		return interesSindicato != null ? interesSindicato : BigDecimal.ZERO;
	}

	public void setInteresSindicato(BigDecimal interesSindicato) {
		this.interesSindicato = interesSindicato;
	}

	public BigDecimal getCapitalSolidario() {
		return capitalSolidario != null ? capitalSolidario : BigDecimal.ZERO;
	}

	public void setCapitalSolidario(BigDecimal capitalSolidario) {
		this.capitalSolidario = capitalSolidario;
	}

	public BigDecimal getInteresSolidario() {
		return interesSolidario != null ? interesSolidario : BigDecimal.ZERO;
	}

	public void setInteresSolidario(BigDecimal interesSolidario) {
		this.interesSolidario = interesSolidario;
	}

	public BigDecimal getCapitalUsufructo() {
		return capitalUsufructo != null ? capitalUsufructo : BigDecimal.ZERO;
	}

	public void setCapitalUsufructo(BigDecimal capitalUsufructo) {
		this.capitalUsufructo = capitalUsufructo;
	}

	public BigDecimal getInteresUsufructo() {
		return interesUsufructo != null ? interesUsufructo : BigDecimal.ZERO;
	}

	public void setInteresUsufructo(BigDecimal interesUsufructo) {
		this.interesUsufructo = interesUsufructo;
	}

	public BigDecimal getCapitalArt46() {
		return capitalArt46 != null ? capitalArt46 : BigDecimal.ZERO;
	}

	public void setCapitalArt46(BigDecimal capitalArt46) {
		this.capitalArt46 = capitalArt46;
	}

	public BigDecimal getInteresArt46() {
		return interesArt46 != null ? interesArt46 : BigDecimal.ZERO;
	}

	public void setInteresArt46(BigDecimal interesArt46) {
		this.interesArt46 = interesArt46;
	}

	public BigDecimal getOtros() {
		return otros != null ? otros : BigDecimal.ZERO;
	}

	public void setOtros(BigDecimal otros) {
		this.otros = otros;
	}

	public BigDecimal getInteres() {
		if (null == entidad || entidad.contains("OSPIM")
				|| entidad.contains("A.M.T.I.M.A")) {
			return interes != null ? interes : BigDecimal.ZERO;
		} else {
			return (interesArt46 != null ? interesArt46 : BigDecimal.ZERO)
					.add(interesSindicato != null ? interesSindicato
							: BigDecimal.ZERO)
					.add(interesSolidario != null ? interesSolidario
							: BigDecimal.ZERO)
					.add(interesUsufructo != null ? interesUsufructo
							: BigDecimal.ZERO);

		}
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getCapital() {
		if (null == entidad || entidad.contains("OSPIM")
				|| entidad.contains("A.M.T.I.M.A")) {
			return capital != null ? capital : BigDecimal.ZERO;
		} else {
			return (capitalArt46 != null ? capitalArt46 : BigDecimal.ZERO)
					.add(capitalSindicato != null ? capitalSindicato
							: BigDecimal.ZERO)
					.add(capitalSolidario != null ? capitalSolidario
							: BigDecimal.ZERO)
					.add(capitalUsufructo != null ? capitalUsufructo
							: BigDecimal.ZERO);
		}

	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getCapitalAmtima() {
		return capitalAmtima != null ? capitalAmtima : BigDecimal.ZERO;
	}

	public void setCapitalAmtima(BigDecimal capitalAmtima) {
		this.capitalAmtima = capitalAmtima;
	}

	public BigDecimal getInteresAmtima() {
		return interesAmtima != null ? interesAmtima : BigDecimal.ZERO;
	}

	public void setInteresAmtima(BigDecimal interesAmtima) {
		this.interesAmtima = interesAmtima;
	}

	public BigDecimal getDeudaActasRelacionadas() {
		return deudaActasRelacionadas;
	}

	public void setDeudaActasRelacionadas(BigDecimal saldo) {
		this.deudaActasRelacionadas = saldo;
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
	
	public String getModi_fechaAsString() {
		if(modi_fecha!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(modi_fecha);
		}else{
			return "";
		}
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

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public String getFechaInicioAsString() {
		return null != fechaInicio ? DateUtils.format(fechaInicio,
				DateUtils.SHORT) : "";
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public String getFechaPagoAsString() {
		return null != fechaPago ? DateUtils.format(fechaPago, DateUtils.SHORT)
				: "";
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<Inspector> getInspectoresFirmantes() {
		return inspectoresFirmantes;
	}

	public void setInspectoresFirmantes(List<Inspector> inspectoresFirmantes) {
		this.inspectoresFirmantes = inspectoresFirmantes;
	}

	public List<ActaRelacionada> getActasRelacionadas() {
		return actasRelacionadas;
	}

	public void setActasRelacionadas(List<ActaRelacionada> actasRelacionadas) {
		this.actasRelacionadas = actasRelacionadas;
	}

	public List<DetalleActaInspectores> getDetallesActas() {
		return detallesActas;
	}

	public void setDetallesActas(List<DetalleActaInspectores> detallesActas) {
		this.detallesActas = detallesActas;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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
		Acta other = (Acta) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static Acta getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Acta getMappingNoOS(ResultSet rs, String prefix)
			throws SQLException {
		Acta acta = new Acta();
		acta.setId(rs.getInt(prefix + "id"));
		acta.setNumero(rs.getString(prefix + "numero"));
		acta.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		acta.setFechaInicio(rs.getDate(prefix + "fecha_inicio"));
		acta.setFechaPago(rs.getDate(prefix + "fecha_pago"));
		acta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		acta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		acta.setAlta_ip(rs.getString(prefix + "alta_ip"));
		acta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		acta.setModi_usr(rs.getString(prefix + "modi_usr"));
		acta.setModi_ip(rs.getString(prefix + "modi_ip"));
		acta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		acta.setBaja_usr(rs.getString(prefix + "baja_usr"));
		acta.setBaja_ip(rs.getString(prefix + "baja_ip"));
		acta.setInteres(rs.getBigDecimal(prefix + "interes"));
		acta.setCapital(rs.getBigDecimal(prefix + "capital"));
		acta.setDeudaActasRelacionadas(rs.getBigDecimal(prefix
				+ "deuda_actas_asociadas"));
		acta.setOtros(rs.getBigDecimal(prefix + "otros"));
		acta.setCierre_fecha(rs.getDate(prefix + "cierre_fecha"));
		acta.setCierre_usr(rs.getString(prefix + "cierre_usr"));
		acta.setActaCerrada(rs.getBoolean(prefix + "acta_cerrada"));
		acta.setMolinera(rs.getBoolean(prefix + "molinera"));
		acta.setEntidad(rs.getString(prefix + "entidad"));
		acta.setEstado(rs.getString(prefix + "estado"));
		acta.setPeriodoInicial(rs.getDate(prefix + "periodo_ini"));
		acta.setPeriodoFinal(rs.getDate(prefix + "periodo_fin"));
		acta.setCapitalSindicato(rs.getBigDecimal(prefix + "capital_sindicato"));
		acta.setInteresSindicato(rs.getBigDecimal(prefix + "interes_sindicato"));
		acta.setCapitalSolidario(rs.getBigDecimal(prefix + "capital_solidario"));
		acta.setInteresSolidario(rs.getBigDecimal(prefix + "interes_solidario"));
		acta.setCapitalUsufructo(rs.getBigDecimal(prefix + "capital_usufructo"));
		acta.setInteresUsufructo(rs.getBigDecimal(prefix + "interes_usufructo"));
		acta.setCapitalArt46(rs.getBigDecimal(prefix + "capital_art46"));
		acta.setInteresArt46(rs.getBigDecimal(prefix + "interes_art46"));
		
		ActaEstadoSeguimiento eas = null;
		try{
			int idEstado = rs.getInt(prefix + "id_estado");
			eas = new ActaEstadoSeguimiento(idEstado, "");
		}catch (Exception e) {
			//nada dejo el null;
		}
		acta.setEstadoSeguimiento(eas);
		
		
		return acta;
	}

	public static Acta getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Acta acta = new Acta();
		acta.setId(rs.getInt(prefix + "id"));
		acta.setNumero(rs.getString(prefix + "numero"));
		acta.setEmpresa(new Empresa(rs.getString(prefix + "cuit"), rs
				.getString(prefix + "sucursal"), ""));
		acta.setFechaInicio(rs.getDate(prefix + "fecha_inicio"));
		acta.setFechaPago(rs.getDate(prefix + "fecha_pago"));
		acta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		acta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		acta.setAlta_ip(rs.getString(prefix + "alta_ip"));
		acta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		acta.setModi_usr(rs.getString(prefix + "modi_usr"));
		acta.setModi_ip(rs.getString(prefix + "modi_ip"));
		acta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		acta.setBaja_usr(rs.getString(prefix + "baja_usr"));
		acta.setBaja_ip(rs.getString(prefix + "baja_ip"));
		acta.setInteres(rs.getBigDecimal(prefix + "interes"));
		acta.setCapital(rs.getBigDecimal(prefix + "capital"));
		acta.setDeudaActasRelacionadas(rs.getBigDecimal(prefix
				+ "deuda_actas_asociadas"));
		acta.setOtros(rs.getBigDecimal(prefix + "otros"));
		acta.setCierre_fecha(rs.getDate(prefix + "cierre_fecha"));
		acta.setCierre_usr(rs.getString(prefix + "cierre_usr"));
		acta.setActaCerrada(rs.getBoolean(prefix + "acta_cerrada"));
		acta.setMolinera(rs.getBoolean(prefix + "molinera"));
		ActaEstadoSeguimiento eas = null;
		try{
			int idEstado = rs.getInt(prefix + "id_estado");
			eas = new ActaEstadoSeguimiento(idEstado, "");
		}catch (Exception e) {
			//nada dejo el null;
		}
		acta.setEstadoSeguimiento(eas);
		
		return acta;
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

	public BigDecimal getTotalPagadoIngresado() {
		BigDecimal total = new BigDecimal("0");
		if (getPagosIngresados() != null) {
			for (ActaPagoIngresado ap : getPagosIngresados()) {
				total = total.add(ap.getImporte());
			}
		}
		return total;
	}

	public BigDecimal getTotalPagadoPorConvenioYActas() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ActaPago ap : pagos) {
				if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
						&& (ap.getConvenioCancelatorio() != null || ap
								.getActaCancelatoria() != null)) {
					total = total.add(ap.getImporte().add(
							ap.getInteres() != null ? ap.getInteres()
									: BigDecimal.ZERO));
				}
			}
		}
		return total;
	}

	public BigDecimal getTotalActaPagosChequeNoIngresados() {
		BigDecimal total = BigDecimal.ZERO;
		if (pagos != null) {
			for (ActaPago ap : pagos) {
				if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
						&& ap.getIngreso() != null
						&& ((ap.getIngreso() instanceof Cheque) || (ap
								.getIngreso() instanceof Pagare) ||
								(ap.getIngreso() instanceof Efectivo) ||
								(ap.getIngreso() instanceof DepositoBancario))
						&& ap.getRecibo() == null) {
					total = total.add(ap.getIngreso().getImporte());
				}
			}
			/*if(total.compareTo(BigDecimal.ZERO)==0){
				for (ActaPago ap : pagos) {
					if (ap.getTipo().equals(ActaPago.Tipo.CUOTA) && ap.getIngreso() != null){
						total=	total.add(ap.getIngreso().getImporte());
					}
				}
				
			}*/
			
		}
		return total;
	}

	public BigDecimal getTotalActaPagosIngresados() {
		BigDecimal total = new BigDecimal("0");
		if (pagos != null) {
			for (ActaPago ap : pagos) {
				if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
						&& ap.getIngreso() != null && ap.getRecibo() == null) {
					total = total.add(ap.getIngreso().getImporte());
				}
			}
		}
		return total;
	}

	public void setTotalActaPagosChequeNoIngresados(BigDecimal importePorCheques) {
		// agrego un cheque ficticio en representacion de todos los cheques
		// individuales que conforman el total
		// esto lo utilizo en la pagina de recibos/ingresos
		if (importePorCheques != null) {
			ActaPago cp = new ActaPago();
			cp.setTipo(ActaPago.Tipo.PAGO);
			List<ActaPago> cps = new ArrayList<ActaPago>(1);
			Cheque cheque = new Cheque();
			cheque.setImporte(importePorCheques);
			cp.setIngreso(cheque);
			cps.add(cp);
			pagos = cps;
		}
	}

	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal("0");
		// if (otros != null) {
		// total = total.add(otros);
		// }
		if (capital != null) { // esta columna es acumulado de calculo x BD
			total = total.add(capital);
		}
		if (interes != null) { // esta columna es acumulado de calculo x BD
			total = total.add(interes);
		}
		// ya estan acumulados x BD en capital e intereses
		// if (deudaActasRelacionadas != null) {
		// total = total.add(deudaActasRelacionadas);
		// }
		// if (capitalSindicato != null) {
		// total = total.add(capitalSindicato);
		// }
		// if (capitalSolidario != null) {
		// total = total.add(capitalSolidario);
		// }
		// if (capitalUsufructo != null) {
		// total = total.add(capitalUsufructo);
		// }
		// if (capitalArt46 != null) {
		// total = total.add(capitalArt46);
		// }
		// if (interesSindicato != null) {
		// total = total.add(interesSindicato);
		// }
		// if (interesSolidario != null) {
		// total = total.add(interesSolidario);
		// }
		// if (interesUsufructo != null) {
		// total = total.add(interesUsufructo);
		// }
		// if (interesArt46 != null) {
		// total = total.add(interesArt46);
		// }

		return total;
	}

	public BigDecimal getInteresFromDetalle() {
		BigDecimal interes = new BigDecimal("0");
		if (detallesActas != null) {
			for (DetalleActaInspectores detalle : detallesActas) {
				if (detalle.isBorradoLogico()) {
					continue;
				}
				interes = interes.add(detalle.getInteres());
			}
		}
		return interes;
	}

	public BigDecimal getCapitalFromDetalle() {
		BigDecimal capital = new BigDecimal("0");
		if (detallesActas != null) {
			for (DetalleActaInspectores detalle : detallesActas) {
				if (detalle.isBorradoLogico()) {
					continue;
				}
				capital = capital.add(detalle.getCapital());
			}
		}
		return capital;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setCierre_fecha(Date cierre_fecha) {
		this.cierre_fecha = cierre_fecha;
	}

	public Date getCierre_fecha() {
		return cierre_fecha;
	}

	public String getCierre_fechaAsString() {
		return null != cierre_fecha ? DateUtils.format(cierre_fecha,
				DateUtils.SHORT) : "";
	}

	public void setCierre_usr(String cierre_usr) {
		this.cierre_usr = cierre_usr;
	}

	public String getCierre_usr() {
		return cierre_usr;
	}

	public void setInspector(boolean inspector) {
		this.inspector = inspector;
	}

	public boolean isInspector() {
		return inspector;
	}

	public void setPeriodos(List<ActaPeriodoDeudaEmpresa> periodos) {
		this.periodos = periodos;
	}

	public List<ActaPeriodoDeudaEmpresa> getPeriodos() {
		return periodos;
	}

	public List<ActaPeriodoDeudaEmpresa> getPeriodos(Date periodo) {
		List<ActaPeriodoDeudaEmpresa> aux = new ArrayList<ActaPeriodoDeudaEmpresa>();

		for (ActaPeriodoDeudaEmpresa peri : periodos) {
			if (peri.getPeriodo().equals(periodo)) {
				aux.add(peri);
			}
		}

		return aux;
	}

	public void setPagos(List<ActaPago> pagos) {
		this.pagos = pagos;
	}

	public List<ActaPago> getPagos() {
		return pagos;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public String getPeriodoInicialAsString() {
		return null != periodoInicial ? DateUtils.format(periodoInicial,
				DateUtils.SHORT) : "";
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public String getPeriodoFinalAsString() {
		return null != periodoFinal ? DateUtils.format(periodoFinal,
				DateUtils.SHORT) : "";
	}

	public void setPagosIngresados(List<ActaPagoIngresado> pagosIngresados) {
		this.pagosIngresados = pagosIngresados;
	}

	public List<ActaPagoIngresado> getPagosIngresados() {
		return pagosIngresados;
	}

	public void setActaCerrada(boolean actaCerrada) {
		this.actaCerrada = actaCerrada;
	}

	public boolean isActaCerrada() {
		return actaCerrada;
	}

	public HashMap<Date, TotalActaNoOS> generarTotalesAgrupados() {
		HashMap<Date, TotalActaNoOS> listaPeriodosAgrupados = null;
		borrarTotalesActa();
		if (null != periodos) {
			listaPeriodosAgrupados = new HashMap<Date, TotalActaNoOS>();
			for (ActaPeriodoDeudaEmpresa periodo : periodos) {
				TotalActaNoOS periodoEnLista = listaPeriodosAgrupados
						.get(periodo.getPeriodo());
				if (null != periodoEnLista && !periodo.isBorradoLogico()) {
					periodoEnLista.addTotal(periodo.getTipoAporte(),
							periodo.getCapitalTotal(),
							periodo.getMontoPagadoTotal(),
							periodo.getInteresTotal(), periodo.getCalculado(),
							periodo.getCamara());
				} else if (!periodo.isBorradoLogico()) {
					TotalActaNoOS nuevoTotal = new TotalActaNoOS();
					nuevoTotal.setPeriodo(periodo.getPeriodo());
					nuevoTotal.addTotal(periodo.getTipoAporte(),
							periodo.getCapitalTotal(),
							periodo.getMontoPagadoTotal(),
							periodo.getInteresTotal(), periodo.getCalculado(),
							periodo.getCamara());
					nuevoTotal.setCantTotalAfiliados(periodo.getCantTotalAfi());
					nuevoTotal.setRemuneracionTotalDeclarada(periodo
							.getRemuneracionTotal());
					listaPeriodosAgrupados
							.put(periodo.getPeriodo(), nuevoTotal);
				}
				addTotal(periodo.getTipoAporte(), periodo.getCapitalTotal(),
						periodo.getInteresTotal(), periodo.getCalculado());
			}
		}
		return listaPeriodosAgrupados;
	}

	public String getCamaras() {		
		HashSet <String> camarasHM=new HashSet<String>();
		if (periodos != null) {
			for (ActaPeriodoDeudaEmpresa peri : periodos) {				
				camarasHM.add(peri.getCamara());				
			}
		}		
		return Arrays.toString(camarasHM.toArray(new String[camarasHM.size()]));
	}

	public void borrarTotalesActa() {
		this.capitalAmtima = BigDecimal.ZERO;
		this.interesAmtima = BigDecimal.ZERO;
		this.capitalSindicato = BigDecimal.ZERO;
		this.interesSindicato = BigDecimal.ZERO;
		this.capitalUsufructo = BigDecimal.ZERO;
		this.interesUsufructo = BigDecimal.ZERO;
		this.capitalArt46 = BigDecimal.ZERO;
		this.interesArt46 = BigDecimal.ZERO;
		this.capitalSolidario = BigDecimal.ZERO;
		this.interesSolidario = BigDecimal.ZERO;
	}

	public void calcaularIntereses(String cuit, List<InteresAfip> intereses,
			Calendar oblig, PortletRequest request) throws SystemException {

		if (getPeriodos() != null) {
			Date vencimientoOriginal = null;
			for (ActaPeriodoDeudaEmpresa peri : getPeriodos()) {
				// AMTIMA, SOLIDARIO TIENEN VTO 15 o SGTE DIA HABIL.
				if (peri.getTipoAporte() == 1 || peri.getTipoAporte() == 5) {
					FeriadosServiceUtil feri = new FeriadosServiceUtil();
					Calendar periodoCalendar = Calendar.getInstance();
					periodoCalendar.setTime(peri.getPeriodo());
					// EL VTO DEL PERIODO ES EL MES SGTE.
					periodoCalendar.add(Calendar.MONTH, 1);
					periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);
					vencimientoOriginal = feri.obtenerSiguienteDiaHabil(
							periodoCalendar, request).getTime();
				} else {
					vencimientoOriginal = AfipServiceUtil
							.getVencimientoOriginalAFIP(cuit, peri.getPeriodo(), request);
				}
				peri.calcularSaldoConInteres(vencimientoOriginal, intereses,
						oblig.getTime());
			}
		}
		if (entidad != null && !entidad.equals("O.S.P.I.M.")) {
			quitarDetallesNegativos();
		}
		quitarPeriodosNegativos();
	}

	public void quitarDetallesNegativos() throws SystemException {
		if (getPeriodos() != null) {
			List<ActaPeriodoDeudaEmpresa> aux = new ArrayList<ActaPeriodoDeudaEmpresa>();
			for (ActaPeriodoDeudaEmpresa peri : getPeriodos()) {
				if (peri.getSubtotal().compareTo(BigDecimal.ZERO) > 0) {
					aux.add(peri);
				}
			}
			this.setPeriodos(aux);
		}

	}

	public void quitarPeriodosNegativos() throws SystemException {
		HashMap<Date, BigDecimal> subtotales = new HashMap<Date, BigDecimal>();		
		if (getPeriodos() != null) {
			for (ActaPeriodoDeudaEmpresa peri : getPeriodos()) {
				BigDecimal auxSubTotal = subtotales.get(peri.getPeriodo());
				if (null != auxSubTotal) {
					for (int i = 0; i < peri.getDetalle().size(); i++) {
						if (entidad != null && !entidad.equals("O.S.P.I.M.")) {
							if (peri.getSubtotalNoOS().compareTo(
									BigDecimal.ZERO) > 0) {
								subtotales.put(
										peri.getPeriodo(),
										auxSubTotal.add(peri
												.getDetalle()
												.get(i)
												.getCapital()
												.add(peri.getDetalle().get(i)
														.getInteres())));

							}
						} else {
							subtotales.put(
									peri.getPeriodo(),
									auxSubTotal.add(peri
											.getDetalle()
											.get(i)
											.getCapital()
											.add(peri.getDetalle().get(i)
													.getInteres())));
						}
					}
				} else {
					for (int i = 0; i < peri.getDetalle().size(); i++) {
						subtotales.put(
								peri.getPeriodo(),
								peri.getDetalle()
										.get(i)
										.getCapital()
										.add(peri.getDetalle().get(i)
												.getInteres()));
					}
				}
			}
			List<ActaPeriodoDeudaEmpresa> aux = new ArrayList<ActaPeriodoDeudaEmpresa>();
			if (null != subtotales) {
				for (ActaPeriodoDeudaEmpresa peri : getPeriodos()) {
				  if(peri.getPeriodo()!=null) {	
					if (subtotales.get(peri.getPeriodo()).compareTo(
							BigDecimal.ZERO) > 0) {
						aux.add(peri);
					}
				  }	
				}
			}
			this.setPeriodos(aux);
		}
	}

	public boolean isMolinera() {
		return molinera;
	}

	public void setMolinera(boolean molinera) {
		this.molinera = molinera;
	}

	public List<TotalActaNoOS> getTotalesActas() {
		return totalesActas;
	}

	public void setTotalesActas(List<TotalActaNoOS> totalesActas) {
		this.totalesActas = totalesActas;
	}

	public static class DetalleActaInspectores {
		private boolean borradoLogico = false;
		private int id;
		private String tipo;
		private Date desde;
		private Date hasta;
		private BigDecimal capital;
		private BigDecimal interes;

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

		public Date getDesde() {
			return desde;
		}

		public void setDesde(Date desde) {
			this.desde = desde;
		}

		public Date getHasta() {
			return hasta;
		}

		public void setHasta(Date hasta) {
			this.hasta = hasta;
		}

		public BigDecimal getTotal() {
			BigDecimal total = new BigDecimal("0");
			if (getCapital() != null) {
				total = total.add(getCapital());
			}
			if (getInteres() != null) {
				total = total.add(getInteres());
			}
			return total;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
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
			DetalleActaInspectores other = (DetalleActaInspectores) obj;
			if (id != other.id)
				return false;
			return true;
		}

		public static DetalleActaInspectores getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static DetalleActaInspectores getMapping(ResultSet rs,
				String prefix) throws SQLException {
			DetalleActaInspectores acta = new DetalleActaInspectores();
			acta.setId(rs.getInt(prefix + "id"));
			acta.setCapital(rs.getBigDecimal(prefix + "capital"));
			acta.setDesde(rs.getDate(prefix + "desde"));
			acta.setHasta(rs.getDate(prefix + "hasta"));
			acta.setInteres(rs.getBigDecimal(prefix + "interes"));
			return acta;
		}

		public void setCapital(BigDecimal capital) {
			this.capital = capital;
		}

		public BigDecimal getCapital() {
			return capital;
		}

		public void setInteres(BigDecimal interes) {
			this.interes = interes;
		}

		public BigDecimal getInteres() {
			return interes;
		}

		public void setBorradoLogico(boolean borradoLogico) {
			this.borradoLogico = borradoLogico;
		}

		public boolean isBorradoLogico() {
			return borradoLogico;
		}

	}

	public static class ActaRelacionada {
		private Acta acta;
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

		public Acta getActa() {
			return acta;
		}

		public void setActa(Acta acta) {
			this.acta = acta;
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

		public BigDecimal calcularSaldoConInteres(
				List<InteresAfip> listaIntereses) throws SystemException {

			BigDecimal importeCuota = null;
			for (ActaPago ap : actaRelacionada.getPagos()) {
				if (ap.getTipo().equals(ActaPago.Tipo.CUOTA)) {
					importeCuota = ap.getImporte();
				}
			}

			List<ActaPagoIngresado> pagos = new ArrayList<ActaPagoIngresado>();
			pagos.addAll(actaRelacionada.getPagosIngresados());

			Collections.sort(pagos, new Comparator<ActaPagoIngresado>() {
				public int compare(ActaPagoIngresado api, ActaPagoIngresado apd) {
					return api.getFechaPagado().compareTo(apd.getFechaPagado());
				}
			});
			if (pagos.size() == 0) {
				BigDecimal interes = AfipServiceUtil.calculoInteres(
						importeCuota, actaRelacionada.getFechaPago(),
						acta.getFechaPago(), listaIntereses);
				saldo = importeCuota.add(interes);
			} else {
				BigDecimal capitalActualizado = null;
				for (int i = 0; i < pagos.size(); i++) {
					ActaPagoIngresado ap = pagos.get(i);
					Date fechaPagoAux = null;
					Date fechaVenc = null;
					if (i == 0) {
						capitalActualizado = importeCuota;
						fechaPagoAux = ap.getFechaPagado();
						fechaVenc = actaRelacionada.getFechaPago();
					} else {
						Calendar aux = Calendar.getInstance();
						aux.setTime(pagos.get(i - 1).getFechaPagado());
						aux.add(Calendar.DATE, 1);
						fechaVenc = aux.getTime();

						if (fechaVenc.before(actaRelacionada.getFechaPago())) {
							fechaVenc = actaRelacionada.getFechaPago();
						}

						fechaPagoAux = ap.getFechaPagado();
					}

					BigDecimal interesAFechaPagada = AfipServiceUtil
							.calculoInteres(capitalActualizado, fechaVenc,
									fechaPagoAux, listaIntereses);
					capitalActualizado = capitalActualizado.add(
							interesAFechaPagada).subtract(ap.getImporte());

					if (i == (pagos.size() - 1)) {
						Calendar aux = Calendar.getInstance();
						aux.setTime(fechaPagoAux);
						aux.add(Calendar.DATE, 1);

						if (aux.getTime()
								.before(actaRelacionada.getFechaPago())) {
							aux.setTime(actaRelacionada.getFechaPago());
						}

						BigDecimal interesAFechaPagoActa = AfipServiceUtil
								.calculoInteres(capitalActualizado,
										aux.getTime(), acta.getFechaPago(),
										listaIntereses);
						saldo = capitalActualizado.add(interesAFechaPagoActa);
					}
				}
			}

			return saldo;
		}

		public static ActaRelacionada getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static ActaRelacionada getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ActaRelacionada actaRelacionada = new ActaRelacionada();
			actaRelacionada.setId(rs.getInt(prefix + "id"));
			actaRelacionada.setActa(new Acta(rs.getInt(prefix + "acta_id")));
			actaRelacionada.setActaRelacionada(new Acta(rs.getInt(prefix
					+ "acta_relacionada_id")));
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

	public static class ActaPagoIngresado {
		private Recibo recibo;
		private BigDecimal importe;
		private Date fechaPagado;
		private Cheque cheque;
		
		public ActaPagoIngresado() {

		}

		public ActaPagoIngresado(Recibo recibo, BigDecimal importeDelActa) {
			this.recibo = recibo;
			this.importe = importeDelActa;
		}
		
		public ActaPagoIngresado(Recibo recibo,
				BigDecimal importeDelConvenio, Date fechaPagado, BigDecimal nroCheque, int idBanco) {
			this.recibo = recibo;
			this.importe = importeDelConvenio;
			this.fechaPagado=fechaPagado;
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

		public Cheque getCheque() {
			return cheque;
		}

		public void setCheque(Cheque cheque) {
			this.cheque = cheque;
		}
		
		
	}

	public BigDecimal getCalculadoSindicato() {
		return calculadoSindicato;
	}

	public void setCalculadoSindicato(BigDecimal calculadoSindicato) {
		this.calculadoSindicato = calculadoSindicato;
	}

	public BigDecimal getCalculadoSolidario() {
		return calculadoSolidario;
	}

	public void setCalculadoSolidario(BigDecimal calculadoSolidario) {
		this.calculadoSolidario = calculadoSolidario;
	}

	public BigDecimal getCalculadoUsufructo() {
		return calculadoUsufructo;
	}

	public void setCalculadoUsufructo(BigDecimal calculadoUsufructo) {
		this.calculadoUsufructo = calculadoUsufructo;
	}

	public BigDecimal getCalculadoArt46() {
		return calculadoArt46;
	}

	public void setCalculadoArt46(BigDecimal calculadoArt46) {
		this.calculadoArt46 = calculadoArt46;
	}

	public BigDecimal getCalculadoAmtima() {
		return calculadoAmtima;
	}

	public void setCalculadoAmtima(BigDecimal calculadoAmtima) {
		this.calculadoAmtima = calculadoAmtima;
	}

	public class TotalActaNoOS {

		private BigDecimal capitalSindicato;
		private BigDecimal capitalAmtima;
		private BigDecimal capitalArt46;
		private BigDecimal capitalSolidario;
		private BigDecimal capitalUsufructo;

		private BigDecimal pagadoSindicato;
		private BigDecimal pagadoAmtima;
		private BigDecimal pagadoArt46;
		private BigDecimal pagadoSolidario;
		private BigDecimal pagadoUsufructo;

		private BigDecimal interesSindicato;
		private BigDecimal interesAmtima;
		private BigDecimal interesArt46;
		private BigDecimal interesSolidario;
		private BigDecimal interesUsufructo;

		private BigDecimal calculadoSindicato;
		private BigDecimal calculadoAmtima;
		private BigDecimal calculadoArt46;
		private BigDecimal calculadoSolidario;
		private BigDecimal calculadoUsufructo;

		private int cantTotalAfiliados;
		private BigDecimal remuneracionTotalDeclarada;

		private Date periodo;
		private List<String> camaras;

		public TotalActaNoOS() {
			this.capitalSindicato = BigDecimal.ZERO;
			this.capitalAmtima = BigDecimal.ZERO;
			this.capitalArt46 = BigDecimal.ZERO;
			this.capitalSolidario = BigDecimal.ZERO;
			this.capitalUsufructo = BigDecimal.ZERO;

			this.pagadoSindicato = BigDecimal.ZERO;
			this.pagadoAmtima = BigDecimal.ZERO;
			this.pagadoArt46 = BigDecimal.ZERO;
			this.pagadoSolidario = BigDecimal.ZERO;
			this.pagadoUsufructo = BigDecimal.ZERO;

			this.interesSindicato = BigDecimal.ZERO;
			this.interesAmtima = BigDecimal.ZERO;
			this.interesArt46 = BigDecimal.ZERO;
			this.interesSolidario = BigDecimal.ZERO;
			this.interesUsufructo = BigDecimal.ZERO;

			this.calculadoSindicato = BigDecimal.ZERO;
			this.calculadoAmtima = BigDecimal.ZERO;
			this.calculadoArt46 = BigDecimal.ZERO;
			this.calculadoSolidario = BigDecimal.ZERO;
			this.calculadoUsufructo = BigDecimal.ZERO;
			this.camaras = new ArrayList<String>();
		}

		public void addTotal(int tipoAporte, BigDecimal capital,
				BigDecimal interes) {
			switch (tipoAporte) {
			case 1:
				this.capitalAmtima = this.capitalAmtima.add(capital);
				this.interesAmtima = this.interesAmtima.add(interes);
				break;
			case 2:
				this.capitalSindicato = this.capitalSindicato.add(capital);
				this.interesSindicato = this.interesSindicato.add(interes);
				break;
			case 3:
				this.capitalUsufructo = this.capitalUsufructo.add(capital);
				this.interesUsufructo = this.interesUsufructo.add(interes);
				break;
			case 4:
				this.capitalArt46 = this.capitalArt46.add(capital);
				this.interesArt46 = this.interesArt46.add(interes);
				break;
			case 5:
				this.capitalSolidario = this.capitalSolidario.add(capital);
				this.interesSolidario = this.interesSolidario.add(interes);
				break;
			}

		}

		public void addTotal(int tipoAporte, BigDecimal capital,
				BigDecimal pagado, BigDecimal interes, BigDecimal calculado,
				String camara) {

			this.camaras.add(camara);

			switch (tipoAporte) {
			case 1:
				this.capitalAmtima = this.capitalAmtima.add(capital);
				this.pagadoAmtima = this.pagadoAmtima.add(pagado);
				this.interesAmtima = this.interesAmtima.add(interes);
				this.calculadoAmtima = this.calculadoAmtima.add(calculado);
				break;
			case 2:
				this.capitalSindicato = this.capitalSindicato.add(capital);
				this.pagadoSindicato = this.pagadoSindicato.add(pagado);
				this.interesSindicato = this.interesSindicato.add(interes);
				this.calculadoSindicato = this.calculadoSindicato
						.add(calculado);
				break;
			case 3:
				this.capitalUsufructo = this.capitalUsufructo.add(capital);
				this.pagadoUsufructo = this.pagadoUsufructo.add(pagado);
				this.interesUsufructo = this.interesUsufructo.add(interes);
				this.calculadoUsufructo = this.calculadoUsufructo
						.add(calculado);
				break;
			case 4:
				this.capitalArt46 = this.capitalArt46.add(capital);
				this.pagadoArt46 = this.pagadoArt46.add(pagado);
				this.interesArt46 = this.interesArt46.add(interes);
				this.calculadoArt46 = this.calculadoArt46.add(calculado);
				break;
			case 5:
				this.capitalSolidario = this.capitalSolidario.add(capital);
				this.pagadoSolidario = this.pagadoSolidario.add(pagado);
				this.interesSolidario = this.interesSolidario.add(interes);
				this.calculadoSolidario = this.calculadoSolidario
						.add(calculado);
				break;
			}

		}

		public BigDecimal getCapitalSindicato() {
			return capitalSindicato;
		}

		public void setTotalSindicato(BigDecimal totalSindicato) {
			this.capitalSindicato = totalSindicato;
		}

		public BigDecimal getCapitalAmtima() {
			return capitalAmtima;
		}

		public void setCapitalAmtima(BigDecimal totalAmtima) {
			this.capitalAmtima = totalAmtima;
		}

		public BigDecimal getCapitalArt46() {
			return capitalArt46;
		}

		public void setCapitalArt46(BigDecimal totalArt46) {
			this.capitalArt46 = totalArt46;
		}

		public BigDecimal getCapitalSolidario() {
			return capitalSolidario;
		}

		public void setCapitalSolidario(BigDecimal totalSolidario) {
			this.capitalSolidario = totalSolidario;
		}

		public BigDecimal getCapitalUsufructo() {
			return capitalUsufructo;
		}

		public void setCapitalUsufructo(BigDecimal totalUsufructo) {
			this.capitalUsufructo = totalUsufructo;
		}

		public BigDecimal getInteresSindicato() {
			return interesSindicato;
		}

		public void setInteresSindicato(BigDecimal interesSindicato) {
			this.interesSindicato = interesSindicato;
		}

		public BigDecimal getInteresAmtima() {
			return interesAmtima;
		}

		public void setInteresAmtima(BigDecimal interesAmtima) {
			this.interesAmtima = interesAmtima;
		}

		public BigDecimal getInteresArt46() {
			return interesArt46;
		}

		public void setInteresArt46(BigDecimal interesArt46) {
			this.interesArt46 = interesArt46;
		}

		public BigDecimal getInteresSolidario() {
			return interesSolidario;
		}

		public void setInteresSolidario(BigDecimal interesSolidario) {
			this.interesSolidario = interesSolidario;
		}

		public BigDecimal getInteresUsufructo() {
			return interesUsufructo;
		}

		public void setInteresUsufructo(BigDecimal interesUsufructo) {
			this.interesUsufructo = interesUsufructo;
		}

		public BigDecimal getPagadoSindicato() {
			return pagadoSindicato;
		}

		public void setPagadoSindicato(BigDecimal pagadoSindicato) {
			this.pagadoSindicato = pagadoSindicato;
		}

		public BigDecimal getPagadoAmtima() {
			return pagadoAmtima;
		}

		public void setPagadoAmtima(BigDecimal pagadoAmtima) {
			this.pagadoAmtima = pagadoAmtima;
		}

		public BigDecimal getPagadoArt46() {
			return pagadoArt46;
		}

		public void setPagadoArt46(BigDecimal pagadoArt46) {
			this.pagadoArt46 = pagadoArt46;
		}

		public BigDecimal getPagadoSolidario() {
			return pagadoSolidario;
		}

		public void setPagadoSolidario(BigDecimal pagadoSolidario) {
			this.pagadoSolidario = pagadoSolidario;
		}

		public BigDecimal getPagadoUsufructo() {
			return pagadoUsufructo;
		}

		public void setPagadoUsufructo(BigDecimal pagadoUsufructo) {
			this.pagadoUsufructo = pagadoUsufructo;
		}

		public BigDecimal getTotal() {
			return capitalArt46.add(capitalSindicato).add(capitalSolidario)
					.add(capitalUsufructo).add(interesArt46)
					.add(interesSindicato).add(interesSolidario)
					.add(interesUsufructo).add(capitalAmtima)
					.add(interesAmtima);
		}

		public Date getPeriodo() {
			return periodo;
		}

		public void setPeriodo(Date periodo) {
			this.periodo = periodo;
		}

		public int getCantTotalAfiliados() {
			return cantTotalAfiliados;
		}

		public void setCantTotalAfiliados(int cantTotalAfiliados) {
			this.cantTotalAfiliados = cantTotalAfiliados;
		}

		public BigDecimal getRemuneracionTotalDeclarada() {
			return remuneracionTotalDeclarada;
		}

		public void setRemuneracionTotalDeclarada(
				BigDecimal remuneracionTotalDeclarada) {
			this.remuneracionTotalDeclarada = remuneracionTotalDeclarada;
		}

		public void setCapitalSindicato(BigDecimal capitalSindicato) {
			this.capitalSindicato = capitalSindicato;
		}

		public BigDecimal getCalculadoSindicato() {
			return calculadoSindicato;
		}

		public void setCalculadoSindicato(BigDecimal calculadoSindicato) {
			this.calculadoSindicato = calculadoSindicato;
		}

		public BigDecimal getCalculadoAmtima() {
			return calculadoAmtima;
		}

		public void setCalculadoAmtima(BigDecimal calculadoAmtima) {
			this.calculadoAmtima = calculadoAmtima;
		}

		public BigDecimal getCalculadoArt46() {
			return calculadoArt46;
		}

		public void setCalculadoArt46(BigDecimal calculadoArt46) {
			this.calculadoArt46 = calculadoArt46;
		}

		public BigDecimal getCalculadoSolidario() {
			return calculadoSolidario;
		}

		public void setCalculadoSolidario(BigDecimal calculadoSolidario) {
			this.calculadoSolidario = calculadoSolidario;
		}

		public BigDecimal getCalculadoUsufructo() {
			return calculadoUsufructo;
		}

		public void setCalculadoUsufructo(BigDecimal calculadoUsufructo) {
			this.calculadoUsufructo = calculadoUsufructo;
		}

		public List<String> getCamaras() {
			return camaras;
		}

		public void setCamara(List<String> camara) {
			this.camaras = camara;
		}

	}

	public void addTotal(int tipoAporte, BigDecimal capital,
			BigDecimal interes, BigDecimal calculado) {

		switch (tipoAporte) {
		case 1:
			this.calculadoAmtima = this.calculadoAmtima != null ? this.calculadoAmtima
					.add(calculado) : BigDecimal.ZERO.add(calculado);
			this.capitalAmtima = this.capitalAmtima != null ? this.capitalAmtima
					.add(capital) : BigDecimal.ZERO.add(capital);
			this.interesAmtima = this.interesAmtima != null ? this.interesAmtima
					.add(interes) : BigDecimal.ZERO.add(interes);
			break;
		case 2:
			this.calculadoSindicato = this.calculadoSindicato != null ? this.calculadoSindicato
					.add(calculado) : BigDecimal.ZERO.add(calculado);
			this.capitalSindicato = this.capitalSindicato != null ? this.capitalSindicato
					.add(capital) : BigDecimal.ZERO.add(capital);
			this.interesSindicato = this.interesSindicato != null ? this.interesSindicato
					.add(interes) : BigDecimal.ZERO.add(interes);
			break;
		case 3:
			this.calculadoUsufructo = this.calculadoUsufructo != null ? this.calculadoUsufructo
					.add(calculado) : BigDecimal.ZERO.add(calculado);
			this.capitalUsufructo = this.capitalUsufructo != null ? this.capitalUsufructo
					.add(capital) : BigDecimal.ZERO.add(capital);
			this.interesUsufructo = this.interesUsufructo != null ? this.interesUsufructo
					.add(interes) : BigDecimal.ZERO.add(interes);
			break;
		case 4:
			this.calculadoSindicato = this.calculadoSindicato != null ? this.calculadoSindicato
					.add(calculado) : BigDecimal.ZERO.add(calculado);
			this.capitalArt46 = this.capitalArt46 != null ? this.capitalArt46
					.add(capital) : BigDecimal.ZERO.add(capital);
			this.interesArt46 = this.interesArt46 != null ? this.interesArt46
					.add(interes) : BigDecimal.ZERO.add(interes);
			break;
		case 5:
			this.calculadoSolidario = this.calculadoSolidario != null ? this.calculadoSolidario
					.add(calculado) : BigDecimal.ZERO.add(calculado);
			this.capitalSolidario = this.capitalSolidario != null ? this.capitalSolidario
					.add(capital) : BigDecimal.ZERO.add(capital);
			this.interesSolidario = this.interesSolidario != null ? this.interesSolidario
					.add(interes) : BigDecimal.ZERO.add(interes);
			break;
		}

	}
	
	public BigDecimal getTotalPagosIngresados() {
		BigDecimal result = BigDecimal.ZERO;
		if (pagosIngresados != null) {
			for (ActaPagoIngresado pago : getPagosIngresados()) {
				result = result.add(pago.getImporte());
			}
		}
		return result;
	}
	
	public boolean containsChequeIngresado(Cheque ch) {
		if (null != getPagosIngresados()) {
			for (ActaPagoIngresado co : getPagosIngresados()) {
				if(null!=co.getCheque()&&co.getCheque().getBanco().getId_banco()==ch.getBanco().getId_banco()){
					if(co.getCheque().getNumero().compareTo(ch.getNumero())==0){
						return true;
					}
				}
			}
		}
		return false;
	}

	public ActaEstadoSeguimiento getEstadoSeguimiento() {
		return estadoSeguimiento;
	}

	public void setEstadoSeguimiento(ActaEstadoSeguimiento estadoSeguimiento) {
		this.estadoSeguimiento = estadoSeguimiento;
	}	
	
}
