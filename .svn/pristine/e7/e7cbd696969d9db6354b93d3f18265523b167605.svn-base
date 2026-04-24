package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledExecutorService;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.OrdenPagoOspimCreacionNuevoAnticipoException;
import ar.com.ospim.liquidaciones.OrdenPagoOspimSinComprobantes;
import ar.com.ospim.liquidaciones.OrdenPagoOspimSinPagos;
import ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosMenorQueComprobantesException;
import ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException;
import ar.com.ospim.liquidaciones.PagoMayorQueComprobanteException;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.DateUtils;
//ACTIVAR
import ar.com.ospim.webservice.test.ClientARBA;
import ar.com.uoma.WebKeysUOMA;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public abstract class OrdenPago  implements Serializable,ItemSubdiarioEgreso {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log _log = LogFactoryUtil.getLog(OrdenPago.class);

	private Empresa acreedor;
	private Integer id;
	private BigDecimal importe;
	private Seccional seccional;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private List<OrdenPago.FormaPago> pagos;
	private List<Comprobante> comprobantes;
	private List<ReintegroList> reintegrosList;
	private String observaciones;
	private boolean farmacia;
	private boolean prestador;

	private Date fechaDesde;
	private Date fechaHasta;
	protected BigDecimal descuento;
	protected BigDecimal descuentoDrogueria;
	protected BigDecimal totalAnticipos;
	private String baseDescuentoFarmacia;
	protected List<ItemOrdenPago> items;
	private String aFavorDe;
	protected boolean mostrarEnCuadro;
	protected boolean mostrarComprobantesEnSubdiario = true;
	protected static Calendar fechaExcepcionIni;
	protected static Calendar fechaExcepcionFin;

	private int idLote;
	private String destino;
	private String obsInterna;
	private Date fechaFirma;
	private boolean tieneRetencion;
	private boolean tieneRetencionIIBB;
	private boolean tieneRetencionIVA;
	private String cbu;
	private String emailCBU;
	private List<Liquidacion> liquidacionesList;
		
	public static OrdenPago getMapping(ResultSet rs, String prefix)
			throws SQLException {
		OrdenPago op = new OrdenPagoAmtima();
		op.setId(rs.getInt(prefix + "id_orden_pago"));
		op.setImporte(rs.getBigDecimal(prefix + "importe"));
		op.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		op.setAlta_usr(rs.getString(prefix + "alta_usr"));
		op.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		op.setModi_usr(rs.getString(prefix + "modi_usr"));
		op.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		op.setBaja_usr(rs.getString(prefix + "baja_usr"));

		op.setObservaciones(rs.getString(prefix + "observaciones"));
		String cuitAcreedor = rs.getString(prefix + "cuit_acreedor");
		String sucuAcreedor = rs.getString(prefix + "sucu_acreedor");
		op.setAcreedor(new Empresa(cuitAcreedor, sucuAcreedor, null));
		int id_secc = rs.getInt(prefix + "id_seccional");

		op.setSeccional(new Seccional(id_secc, null));

		try {
			op.setFechaDesde(rs.getDate(prefix + "fecha_desde"));
			op.setFechaHasta(rs.getDate(prefix + "fecha_hasta"));
			op.setDescuento(rs.getBigDecimal(prefix + "descuento"));
			op.setDescuentoDrogueria(rs.getBigDecimal(prefix
					+ "descuento_por_drogueria"));

		} catch (Exception e) {
			// do nothing
		}

		return op;

	}

	public BigDecimal getImporteDeItems() {
		BigDecimal total = new BigDecimal("0");
		total.setScale(2, RoundingMode.HALF_UP);
		for (ItemOrdenPago item : items) {
			if (items != null) {
				total = total.add(item.getTotalOspim()).add(
						item.getTotalAmtima());
			}
		}
		return total;
	}

	public BigDecimal getImporteDeItemsPVP() {
		BigDecimal total = new BigDecimal("0");
		total.setScale(2, RoundingMode.HALF_UP);
		for (ItemOrdenPago item : items) {
			if (items != null) {
				total = total.add(item.getPvp());
			}
		}
		return total;
	}

	public BigDecimal getImporteDeItemsConDescuento() {

		BigDecimal total = getImporteDeItems();
		BigDecimal totalBase = BigDecimal.ZERO;

		if (baseDescuentoFarmacia.equals("PVP")) {
			totalBase = getImporteDeItemsPVP();
		} else {
			totalBase = getImporteDeItems();
		}

		if (descuento != null) {
			total = total.subtract(
					descuento.divide(new BigDecimal(100D)).multiply(totalBase))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		if (descuentoDrogueria != null) {
			total = total.subtract(descuentoDrogueria).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}
		/*
		 * if(totalAnticipos!=null){ total =
		 * total.subtract(totalAnticipos).setScale(2, BigDecimal.ROUND_HALF_UP);
		 * }
		 */
		return total;
	}

	public boolean isFarmacia() {
		return farmacia;
	}

	public void setFarmacia(boolean farmacia) {
		this.farmacia = farmacia;
	}

	public Integer getId() {
		return id != null ? id : 0;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setReintegrosList(List<ReintegroList> reintegrosList) {
		this.reintegrosList = reintegrosList;
	}

	public List<ReintegroList> getReintegrosList() {
		return reintegrosList;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public List<Comprobante> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(List<Comprobante> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public void setAcreedor(Empresa acreedor) {
		this.acreedor = acreedor;
	}

	public Empresa getAcreedor() {
		return acreedor;
	}

	public void setFormaPago(List<OrdenPago.FormaPago> pagos) {
		this.pagos = pagos;
	}

	public List<OrdenPago.FormaPago> getFormaPago() {
		return pagos;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public String getAlta_fechaAsString() {
		String ret = "";
		if (alta_fecha != null) {
			ret = DateUtils.format(alta_fecha, DateUtils.SHORT);
		}
		return ret;
	}

	public String getFechaAltaAsString() {
		return null != alta_fecha ? DateUtils.format(alta_fecha,
				DateUtils.SHORT) : "";
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

	public String getBaja_fechaAsString() {
		String ret = "";
		if (baja_fecha != null) {
			ret = DateUtils.format(baja_fecha, DateUtils.SHORT);
		}
		return ret;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OrdenPago other = (OrdenPago) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public List<Cheque> getSoloCheques() {
		List<Cheque> cheques = new ArrayList<Cheque>();
		if (getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : getFormaPago()) {
				if (fp.getPago() instanceof Cheque) {
					cheques.add((Cheque) fp.getPago());
				}
			}
		}
		return cheques;
	}

	public boolean isReActivable() {
		boolean isReactivable = true;
		if (getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : getFormaPago()) {
				if (fp.getOtraOpCheque() != null) {
					isReactivable = false;
					break;
				}
			}
		}
		return isReactivable;
	}

	/*
	 * public void validar(int entidad) throws Exception { if (getFormaPago() ==
	 * null && getFormaPago().size() == 0) { throw new OrdenPagoOspimSinPagos();
	 * }
	 * 
	 * if (getComprobantes() == null && getComprobantes().size() == 0) { throw
	 * new OrdenPagoOspimSinComprobantes(); }
	 * 
	 * List<Anticipo> anticipos = new ArrayList<Anticipo>(); BigDecimal
	 * totalPagosNoAnticipo = BigDecimal.ZERO; BigDecimal totalPagosAnticipo =
	 * BigDecimal.ZERO; for (OrdenPago.FormaPago fp : getFormaPago()) { if
	 * (fp.getPago() instanceof Anticipo) {
	 * 
	 * totalPagosAnticipo = totalPagosAnticipo.add(fp.getPago()
	 * .getImporte().compareTo(BigDecimal.ZERO)<0?fp.getPago()
	 * .getImporte().negate():fp.getPago() .getImporte());
	 * anticipos.add((Anticipo) fp.getPago()); } else { totalPagosNoAnticipo =
	 * totalPagosNoAnticipo.add(fp.getPago() .getImporte()); } }
	 * 
	 * BigDecimal totalpagos = totalPagosNoAnticipo.add(totalPagosAnticipo);
	 * 
	 * BigDecimal totalComprobante = BigDecimal.ZERO; for (Comprobante c :
	 * getComprobantes()) { if (c.isDebitoParaEgreso()) { totalComprobante =
	 * totalComprobante.subtract(c .getImporteComprobante()); } else {
	 * totalComprobante = totalComprobante.add(c .getImporteComprobante()); } }
	 * 
	 * // los pagos siempre deben ser >= que los comprobantes if
	 * (totalpagos.compareTo(totalComprobante) < 0) { throw new
	 * OrdenPagoOspimTotalPagosMenorQueComprobantesException(); }
	 * 
	 * // si el pago fuese > comprobante if
	 * (totalpagos.compareTo(totalComprobante) > 0) { // me debo fijar que solo
	 * pueda quedar pendiente saldo de anticipos if
	 * (totalPagosNoAnticipo.compareTo(totalComprobante) >= 0) { throw new
	 * OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException(); }
	 * else { BigDecimal tempAnticipos = new BigDecimal(
	 * totalPagosNoAnticipo.toString()); int cantAnticiposUsados = 0; for
	 * (Anticipo ant : anticipos) { cantAnticiposUsados++; tempAnticipos =
	 * tempAnticipos.add(ant.getImporte()); if
	 * (tempAnticipos.compareTo(totalComprobante) > 0 && cantAnticiposUsados !=
	 * anticipos.size()) { throw new OrdenPagoOspimAnticiposNoUsadosException();
	 * } }
	 * 
	 * Comprobante anticipoBalanceador = obtenerAnticipoBalanceador(
	 * totalpagos.subtract(totalComprobante), anticipos.get(0), entidad);
	 * getComprobantes().add(anticipoBalanceador); throw new
	 * OrdenPagoOspimCreacionNuevoAnticipoException(
	 * anticipos.get(0).getImporte(),
	 * anticipoBalanceador.getImporteComprobante()); } } }
	 */

	public void validarAnticipoParcial(int entidad, BigDecimal anticipoParcial)
			throws Exception {
		if (getFormaPago() == null && getFormaPago().size() == 0) {
			throw new OrdenPagoOspimSinPagos();
		}

		if (getComprobantes() == null && getComprobantes().size() == 0) {
			throw new OrdenPagoOspimSinComprobantes();
		}

		List<Anticipo> anticipos = new ArrayList<Anticipo>();
		BigDecimal totalPagosNoAnticipo = BigDecimal.ZERO;
		BigDecimal totalPagosAnticipo = BigDecimal.ZERO;

		// FORMA PAGO ANTICIPO
		for (OrdenPago.FormaPago fp : getFormaPago()) {
			if (fp.getPago() instanceof Anticipo) {
				totalPagosAnticipo = totalPagosAnticipo.add(fp.getPago()
						.getImporte());
				anticipos.add((Anticipo) fp.getPago());
			} else {
				totalPagosNoAnticipo = totalPagosNoAnticipo.add(fp.getPago()
						.getImporte());
			}
		}

		BigDecimal totalpagos = totalPagosNoAnticipo.add(totalPagosAnticipo);

		BigDecimal totalComprobante = BigDecimal.ZERO;
		for (Comprobante c : getComprobantes()) {
			if (c.isDebitoParaEgreso()) {
				totalComprobante = totalComprobante.subtract(c
						.getImporteComprobante());
			} else {
				totalComprobante = totalComprobante.add(c
						.getImporteComprobante());
			}
		}

		// los pagos siempre deben ser >= que los comprobantes
		if (totalpagos.compareTo(totalComprobante) < 0) {
			throw new OrdenPagoOspimTotalPagosMenorQueComprobantesException();
		}

		// si el pago fuese > comprobante
		if (totalpagos.compareTo(totalComprobante) > 0) {
			// me debo fijar que solo pueda quedar pendiente saldo de anticipos
			if (totalPagosNoAnticipo.compareTo(totalComprobante) >= 0) {
				throw new OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException();
			} else {				
				if (totalPagosAnticipo.subtract(anticipoParcial).compareTo(
						BigDecimal.ZERO) < 0) {
					throw new Exception();
				}
				// DEBO TOMAR EL BALANCEADOR QUE CORRESPONDA SEGUN EL ANTICIPO
				// INGRESADO.
				Comprobante anticipoBalanceador = obtenerAnticipoBalanceador(
						totalPagosAnticipo.abs().subtract(anticipoParcial.abs()),
						anticipos.get(0), entidad);
				getComprobantes().add(anticipoBalanceador);
				throw new OrdenPagoOspimCreacionNuevoAnticipoException(
						anticipos.get(0).getImporte(),
						anticipoBalanceador.getImporteComprobante());
			}
		}
	}

	/*
	 * private Comprobante obtenerAnticipoBalanceador(BigDecimal diferencia,
	 * Anticipo anticipoACopiar, int entidad) throws Exception { Comprobante
	 * anticipo = new Comprobante(); anticipo.setLetraComprobante("");
	 * anticipo.setTipoComprobante("ANT"); Comprobante comprobanteOrig =
	 * anticipoACopiar.getAnticipo();
	 * anticipo.setCuit(comprobanteOrig.getCuitEmisor());
	 * anticipo.setSucuComprobante(0); anticipo.setPtoVenta(1);
	 * anticipo.setCantCuotas(anticipoACopiar.getCantCuotas());
	 * 
	 * Comprobante c = ComprobanteServiceUtil.getComprobante(comprobanteOrig,
	 * entidad);
	 * 
	 * String sucu = c.getAcreedorEmpresa().getSucursal(); if (c.getSeccional()
	 * != null && c.getSeccional().getId() != 0) { sucu =
	 * String.valueOf(c.getSeccional().getId()); } String cuit =
	 * c.getAcreedorEmpresa().getCuit(); String obtenerNumero =
	 * BuscarUltimoComprobanteAction.obtenerNumero( "ANT", cuit, sucu, entidad);
	 * anticipo.setNroComprobante(cuit + "-" + sucu + "/" +
	 * (Integer.parseInt(obtenerNumero) + 1)); setearConceptos(diferencia,
	 * anticipo, c); anticipo.setImporteComprobante(diferencia);
	 * anticipo.setFechaEmision(new Date()); anticipo.setFechaRecepcion(new
	 * Date()); anticipo.setAcreedorEmpresa(c.getAcreedorEmpresa());
	 * anticipo.setSeccional(c.getSeccional()); return anticipo; }
	 */

	public void validar(int entidad) throws Exception {
		if (getFormaPago() == null && getFormaPago().size() == 0) {
			throw new OrdenPagoOspimSinPagos();
		}

		if (getComprobantes() == null && getComprobantes().size() == 0) {
			throw new OrdenPagoOspimSinComprobantes();
		}

		List<Anticipo> anticipos = new ArrayList<Anticipo>();
		BigDecimal totalPagosNoAnticipo = BigDecimal.ZERO;
		BigDecimal totalPagosAnticipo = BigDecimal.ZERO;		
		
		for (OrdenPago.FormaPago fp : getFormaPago()) {
			if (fp.getPago() instanceof Anticipo) {
		
				totalPagosAnticipo = totalPagosAnticipo.add(fp.getPago()
						.getImporte());
		
				anticipos.add((Anticipo) fp.getPago());
			} else {
				totalPagosNoAnticipo = totalPagosNoAnticipo.add(fp.getPago()
						.getImporte());
			}
		}

		BigDecimal totalpagos = totalPagosNoAnticipo.add(totalPagosAnticipo);

		BigDecimal totalComprobante = BigDecimal.ZERO;
		for (Comprobante c : getComprobantes()) {
			if (c.isDebitoParaEgreso()) {
				totalComprobante = totalComprobante.subtract(c
						.getImporteComprobante());
			} else {
				totalComprobante = totalComprobante.add(c
						.getImporteComprobante());
			}
		}

		// los pagos siempre deben ser >= que los comprobantes
		if (totalpagos.compareTo(totalComprobante) < 0) {
			throw new OrdenPagoOspimTotalPagosMenorQueComprobantesException();
		}
		if (totalpagos.compareTo(totalComprobante) > 0) {
			throw new PagoMayorQueComprobanteException();

		}

		List<Comprobante> comprobantesList = null;
		// si el pago fuese > comprobante
		if (totalpagos.compareTo(totalComprobante) > 0
				|| totalPagosAnticipo.compareTo(BigDecimal.ZERO) > 0) { // &&
																		// totalPagosAnticipo.compareTo(totalPagosAnticipoOriginal)!=0))
																		// {
			// me debo fijar que solo pueda quedar pendiente saldo de anticipos
			if (totalPagosNoAnticipo.compareTo(totalComprobante) >= 0) {
				throw new OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException();
			} else {

				comprobantesList = new ArrayList<Comprobante>();
				// ACA TENGO QUE OBTENER LOS BALANCEADORES...
				comprobantesList.addAll(obtenerAnticipoBalanceadorList(
						anticipos, entidad));

				getComprobantes().addAll(comprobantesList);

			}
		}
	}

	private List<Comprobante> obtenerAnticipoBalanceadorList(
			List<Anticipo> comprobantesAnticipos, int entidad) throws Exception {
		List<Comprobante> listaComproResult = new ArrayList<Comprobante>();
		for (Anticipo ant : comprobantesAnticipos) {
			Comprobante anticipo = new Comprobante();
			anticipo.setLetraComprobante("");
			anticipo.setTipoComprobante("ANT");
			Comprobante comprobanteOrig = ant.getAnticipo();
			anticipo.setCuit(comprobanteOrig.getCuitEmisor());
			anticipo.setSucuComprobante(1);
			anticipo.setPtoVenta(1);

			Comprobante c = ComprobanteServiceUtil.getComprobante(
					comprobanteOrig, entidad);
			anticipo.setCuit(comprobanteOrig.getCuitEmisor());
			anticipo.setSucuComprobante(c.getSucuComprobante());
			anticipo.setPtoVenta(c.getPtoVenta());
			anticipo.setObservaciones(c.getObservaciones());

			// NO DEBO CREAR ANTICIPO BALANCEADOR!!!
			if (c.getImporteComprobante().abs().compareTo(
					ant.getImporteAnticipoBalanceador()) <= 0) {
				anticipo.setImporteComprobante(BigDecimal.ZERO);
			} else {
				anticipo.setImporteComprobante(ant
						.getImporteAnticipoBalanceador());
			}
			StringTokenizer nroComprobanteToken = new StringTokenizer(
					c.getNroComprobante(), "|");

			String nroComprobanteNumero = nroComprobanteToken.nextToken();

			anticipo.setNroComprobante(nroComprobanteNumero + "|"
					+ (c.getNroAnticipo() + 1));
			anticipo.setNroAnticipo(c.getNroAnticipo() + 1);
			setearConceptos(ant.getImporteAnticipoBalanceador(), anticipo, c);

			anticipo.setFechaEmision(new Date());
			anticipo.setFechaRecepcion(new Date());
			anticipo.setAcreedorEmpresa(c.getAcreedorEmpresa());
			anticipo.setSeccional(c.getSeccional());
			anticipo.setCantCuotas(c.getCantCuotas());
			listaComproResult.add(anticipo);
		}
		return listaComproResult;
	}

	private Comprobante obtenerAnticipoBalanceador(BigDecimal diferencia,
			Anticipo anticipoACopiar, int entidad) throws Exception {
		Comprobante anticipo = new Comprobante();
		anticipo.setLetraComprobante("");
		anticipo.setTipoComprobante("ANT");
		Comprobante comprobanteOrig = anticipoACopiar.getAnticipo();
		anticipo.setCuit(comprobanteOrig.getCuitEmisor());
		anticipo.setSucuComprobante(1);
		anticipo.setPtoVenta(1);

		Comprobante c = ComprobanteServiceUtil.getComprobante(comprobanteOrig,
				entidad);
		anticipo.setCuit(comprobanteOrig.getCuitEmisor());
		anticipo.setSucuComprobante(c.getSucuComprobante());
		anticipo.setPtoVenta(c.getPtoVenta());

		// NO DEBO CREAR ANTICIPO BALANCEADOR!!!
		if (c.getImporteComprobante().abs().compareTo(diferencia) <= 0) {
			anticipo.setImporteComprobante(BigDecimal.ZERO);
		} else {
			anticipo.setImporteComprobante(diferencia);
		}
		StringTokenizer nroComprobanteToken = new StringTokenizer(
				c.getNroComprobante(), "|");

		String nroComprobanteNumero = nroComprobanteToken.nextToken();

		anticipo.setNroComprobante(nroComprobanteNumero + "|"
				+ (c.getNroAnticipo() + 1));
		anticipo.setNroAnticipo(c.getNroAnticipo() + 1);
		setearConceptos(diferencia, anticipo, c);

		anticipo.setFechaEmision(new Date());
		anticipo.setFechaRecepcion(new Date());
		anticipo.setAcreedorEmpresa(c.getAcreedorEmpresa());
		anticipo.setSeccional(c.getSeccional());
		anticipo.setCantCuotas(c.getCantCuotas());
		return anticipo;
	}

	private void setearConceptos(BigDecimal diferencia, Comprobante anticipo,
			Comprobante compOriginal) {

		BigDecimal total = compOriginal.getImporteComprobante();

		ArrayList<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		for (ComprobanteConcepto cc : compOriginal.getConceptos()) {
			Concepto concepto = cc.getConceptoComprobante();
			BigDecimal factor = cc.getImporte().divide(total);
			BigDecimal importe = diferencia.multiply(factor);
			conceptos.add(new ComprobanteConcepto(concepto, importe));
		}
		anticipo.setConceptos(conceptos);
	}

	public static class FormaPago implements SubdiarioEgresoColumna {
		transient private Date fechaOP;
		private int id;
		private boolean isBorradoLogicamente = false;
		private boolean nuevo = true;
		private Pago pago;
		transient private Integer otraOpCheque;

		public FormaPago() {
		}

		public FormaPago(Pago pago) {
			this.pago = pago;
		}

		public FormaPago(Pago pago, boolean nuevo) {
			this.pago = pago;
			this.nuevo = nuevo;
		}

		public CuentaBancaria getCuentaBancaria() {
			return pago.getCuentaBancaria() != null ? pago.getCuentaBancaria()
					: new CuentaBancaria();
		}

		public String getNumeroStr() {
			return pago.getNumeroStr();
		}

		public BigDecimal getImporte() {
			return pago.getImporte();
		}

		public String getDescripcion() {
			return pago.getDescripcion();
		}

		public String getANombreDe() {
			return pago.getANombreDe();
		}

		public void setPago(Pago pago) {
			this.pago = pago;
		}

		public Pago getPago() {
			return pago;
		}

		public String getTipo() {
			return pago != null ? pago.getTipo() : "";
		}

		public static FormaPago getMapping(ResultSet rs, String prefix,
				int entidad) throws SQLException {
			FormaPago formaPago = new FormaPago();
			formaPago.setFechaOP(rs.getDate("op_alta_fecha"));
			formaPago.setId(rs.getInt(prefix + "id"));

			Comprobante anticipo = new Comprobante();
			anticipo.setIdOp(rs.getInt(prefix + "id_orden_pago"));
			anticipo.setPtoVenta(rs.getInt(prefix + "id_punto_venta_antic"));
			anticipo.setTipoComprobante(rs.getString(prefix
					+ "compro_tipo_antic"));
			anticipo.setNroComprobante(rs
					.getString(prefix + "compro_nro_antic"));
			anticipo.setCuit(rs.getString(prefix + "cuit_antic"));
			anticipo.setLetraComprobante(rs.getString(prefix
					+ "compro_letra_antic"));
			anticipo.setSucuComprobante(rs.getInt(prefix + "compro_sucu_antic"));

			Caja caja = new Caja();
			caja.setImporte(rs.getBigDecimal(prefix + "importe_debito_bcrio"));
			
			
			Integer idTipoPago = rs.getInt("TPP__id_tipo_pago");
			PagoSinSalidaDeFondos pagoSinSalida =new PagoSinSalidaDeFondos();
			if(idTipoPago==pagoSinSalida.ID_PAGO_A_UOMA) {
				pagoSinSalida.setTipo_pago(idTipoPago);
				pagoSinSalida.setImporte(rs.getBigDecimal(prefix + "importe_debito_bcrio"));
			}
			

			Cheque cheque = new Cheque();
			cheque.setNumero(rs.getBigDecimal(prefix + "nro_cheque"));
			cheque.setBanco(new Banco(rs.getInt(prefix + "id_banco_cheque"),null));

			CuentaBancaria ctaBcriaCheque = new CuentaBancaria();
			ctaBcriaCheque.setId_cuenta_bcria(rs.getInt(prefix+ "id_cta_bcria_cheque"));
			ctaBcriaCheque.setNro_cuenta(rs.getInt("CB__nro_cuenta_cheque"));
			ctaBcriaCheque.setSucursal(rs.getInt("CB__sucursal_cheque"));
			ctaBcriaCheque.setDescripcion(rs.getString("CB__descripcion_cheque"));
			ctaBcriaCheque.setCuentaAsociada(new PlanCuentas(rs.getString("CB__numero_plan_cuenta_asociada_cheque"), 
					rs.getString("CB__cuenta_asociada_cheque")));
			ctaBcriaCheque.getCuentaAsociada().setId(rs.getInt("CB__cuenta_asociada_id"));
			cheque.setCuentaBancaria(ctaBcriaCheque);

			RetencionGanancias ret = new RetencionGanancias();
			ret.setCuentaBancaria(new CuentaBancaria(rs.getInt(prefix+ "id_cta_bcria_retencion")));
			ret.setImporte(rs.getBigDecimal(prefix + "importe_retencion"));

			PagoBancario pagoBcrio = new PagoBancario(); 
			CuentaBancaria ctaBcriaDeb = new CuentaBancaria();
			ctaBcriaDeb.setId_cuenta_bcria(rs.getInt(prefix+ "id_cta_bcria_debito_crio"));
			ctaBcriaDeb.setNro_cuenta(rs.getInt("CB2__nro_cuenta_debito"));
			ctaBcriaDeb.setSucursal(rs.getInt("CB2__sucursal_debito"));
			ctaBcriaDeb.setDescripcion(rs.getString("CB2__descripcion_debito"));
			ctaBcriaDeb.setCuentaAsociada(new PlanCuentas(rs.getString("CB2__numero_plan_cuenta_asociada_debito"), 
					rs.getString("CB2__cuenta_asociada_debito")));
			ctaBcriaDeb.getCuentaAsociada().setId(rs.getInt("CB2__cuenta_asociada_debito_id"));

			pagoBcrio.setCuentaBancaria(ctaBcriaDeb);
			pagoBcrio.setImporte(rs.getBigDecimal(prefix+ "importe_debito_bcrio"));
			pagoBcrio.setNumero(rs.getString(prefix + "nro_debito_bcrio"));
			pagoBcrio.setTipo_pago(rs.getInt("TPP__id_tipo_pago"));
			pagoBcrio.setDescripcionTipoPago(rs.getString("TPP__descripcion"));

			
			try {
			   pagoBcrio.setCuilCuenta(rs.getString(prefix + "cuil_cuenta"));
			   pagoBcrio.setEmailCuenta(rs.getString(prefix + "email_cuenta"));
			   pagoBcrio.setApellidoCuenta(rs.getString(prefix +  "apellido_cuenta"));
			   pagoBcrio.setNombreCuenta(rs.getString(prefix + "nombre_cuenta"));
			}catch(Exception e) {
				pagoBcrio.setCuilCuenta("");
				pagoBcrio.setEmailCuenta("");
				pagoBcrio.setApellidoCuenta("");
				pagoBcrio.setNombreCuenta("");
			}
			RetencionIIBB rIB = new RetencionIIBB();
			try {
				 String tipoRet =rs.getString( "tipo_retencion_vs");
				 if(tipoRet.equalsIgnoreCase(WebKeysUOMA.RET_IIBB)) {
					rIB.setCuentaBancaria(new CuentaBancaria(rs.getInt("id_cta_bcria_retencion_vs")));
					rIB.setImporte(rs.getBigDecimal("importe_retencion_vs"));
					rIB.setJurisdiccion(rs.getInt("jurisdiccion_retencion_vs")); 
					rIB.setAlicuota(rs.getDouble("alicuota_retencion_vs"));
				 }
			}catch(Exception e) {}
						
			
			RetencionIVA rIVA = new RetencionIVA();
			try {
				 String tipoRet =rs.getString( "tipo_retencion_vs");
				 if(tipoRet.equalsIgnoreCase(WebKeysUOMA.RET_IVA)) {
					rIVA.setCuentaBancaria(new CuentaBancaria(rs.getInt("id_cta_bcria_retencion_vs")));
					rIVA.setImporte(rs.getBigDecimal("importe_retencion_vs"));
				 }
			}catch(Exception e) {}
			if (anticipo.getTipoComprobante() != null) {
				Anticipo antic = new Anticipo(anticipo);
				antic.setOpOrigen(rs.getInt("opOrigenAnticipo"));
				antic.setFechaOPOrigen(rs.getDate("fechaOPOrigenAnticipo"));				
				anticipo.setNroAnticipo(rs.getInt("nro_cuota"));
				antic.setNroCuota(rs.getInt("nro_cuota"));
					// anticipo.setIdOp(rs.getInt("nro_cuota"));
				
				formaPago.setPago(antic);
			} else if (cheque.getNumero() != null) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				Banco b = Banco.getMapping(rs, "ba__");
				cheque.getCuentaBancaria().setBanco(b);
				chq.setCuentaBancaria(cheque.getCuentaBancaria());
				formaPago.setPago(chq);
			} else if (ret.getCuentaBancaria().getId_cuenta_bcria() != 0) {
				formaPago.setPago(ret);
			} else if (pagoBcrio.getCuentaBancaria().getId_cuenta_bcria() != 0) {
				formaPago.setPago(pagoBcrio);
			} else if (rIB.getCuentaBancaria()!=null && rIB.getCuentaBancaria().getId_cuenta_bcria() != 0) {
				formaPago.setPago(rIB);
			} else if (rIVA.getCuentaBancaria()!=null && rIVA.getCuentaBancaria().getId_cuenta_bcria() != 0) {
				formaPago.setPago(rIVA);	
			}else if (pagoSinSalida.getTipo_pago() != 0) {
				formaPago.setPago(pagoSinSalida);
			}else {
				caja.setTipo_pago(rs.getInt("TPP__id_tipo_pago"));
				caja.setCuentaAsociada(new PlanCuentas(rs.getString("CB2__numero_plan_cuenta_asociada_debito"),
						rs.getString("CB2__cuenta_asociada_debito")));
				caja.getCuentaAsociada().setId(rs.getInt("CB2__cuenta_asociada_debito_id"));
				formaPago.setPago(caja);
			}
			
			return formaPago;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pago == null) ? 0 : pago.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FormaPago other = (FormaPago) obj;
			if (pago == null) {
				if (other.pago != null)
					return false;
			} else if (!pago.equals(other.pago))
				return false;
			return true;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setNuevo(boolean nuevo) {
			this.nuevo = nuevo;
		}

		public boolean isNuevo() {
			return nuevo;
		}

		public void setBorradoLogicamente(boolean isBorradoLogicamente) {
			this.isBorradoLogicamente = isBorradoLogicamente;
		}

		public boolean isBorradoLogicamente() {
			return isBorradoLogicamente;
		}

		public String getCuenta() {
			return getCuenta(WebKeysGlobal.OSPIM);
		}

		// Para reporte SubdiarioEgresos
		public String getCuenta(int entidad) {
			try {
				if (pago == null) {
					return "";
				}
				if (getTipo().equals(RetencionGanancias.class.getSimpleName())) {
					return ConceptoServiceUtil.getCuentaRetencionGanancias(
							fechaOP, entidad).getNumero();
				}
				if (getTipo().equals(RetencionIIBB.class.getSimpleName())) {
					return ConceptoServiceUtil.getCuentaRetencionIIBB(
							fechaOP, entidad).getNumero();
				}
				
				if (getTipo().equals(RetencionIVA.class.getSimpleName())) {
					return ConceptoServiceUtil.getCuentaRetencionIVA(
							fechaOP, entidad).getNumero();
				}
				if (getTipo().equals(Anticipo.class.getSimpleName())) {
					return ((Anticipo) getPago()).getAnticipo().getConceptos()
							.get(0).getCuenta();
				}

				if (getTipo().equals(Anticipo.class.getSimpleName())) {
					return ((Anticipo) getPago()).getAnticipo().getConceptos()
							.get(0).getCuenta();
				}

				if (getTipo().equals(Caja.class.getSimpleName())
						&& entidad == WebKeysGlobal.AMTIMA) {
//					return "1.1.1.1000";
					return "1.1.1.0101";
				}
				
				if (getTipo().equals(Caja.class.getSimpleName())
						&& entidad == WebKeysGlobal.UOMA && (
					((Caja)pago).getTipo_pago()!=0 &&						
					((Caja)pago).getTipo_pago() ==7) ){
//					return "1.1.1.1000";
					String cta=TraeListasServiceUtil.getSystemConfig("CTA_NRO_CAJA_7");
					return cta;
				}
				
				if (getTipo().equals(PagoSinSalidaDeFondos.class.getSimpleName()) 
						&& entidad == WebKeysGlobal.OSPIM) {
					return ConceptoServiceUtil.getCuentaPagoSinSalidaDeFondos(
							fechaOP, entidad,((PagoSinSalidaDeFondos)pago).getTipo_pago()).getNumero();
				}

			} catch (Exception e) {
				_log.debug(e);
			}

			return getCuentaBancaria().getCuentaAsociada().getNumero();
		}

		public String getDescripcionPAraSubdiario() {
			if (pago == null) {
				return "";
			}
			return getTipo() + " " + getPago().getNumeroStr();
		}

		public Integer getOtraOpCheque() {
			return otraOpCheque;
		}

		public void setOtraOpCheque(Integer otraOpCheque) {
			if (otraOpCheque != null && otraOpCheque != 0) {
				this.otraOpCheque = otraOpCheque;
			}
		}

		public boolean isAnticipo() {
			if (getPago() != null) {
				return getPago() instanceof Anticipo;
			}
			return false;
		}

		public Date getFechaOP() {
			return fechaOP;
		}

		public void setFechaOP(Date fechaOP) {
			this.fechaOP = fechaOP;
		}

		public int getCuentaId() {
			return getCuentaId(WebKeysGlobal.OSPIM);
		}

		public int getCuentaId(int entidad) {
			if (pago == null) {
				return 0;
			}
			if (getTipo().equals(RetencionGanancias.class.getSimpleName())) {
				return ConceptoServiceUtil.getCuentaRetencionGanancias(fechaOP,
						entidad).getId();
			}
			if (getTipo().equals(RetencionIIBB.class.getSimpleName())) {
				return ConceptoServiceUtil.getCuentaRetencionIIBB(
						fechaOP, entidad).getId();
			}
			
			if (getTipo().equals(RetencionIVA.class.getSimpleName())) {
				return ConceptoServiceUtil.getCuentaRetencionIVA(
						fechaOP, entidad).getId();
			}
			if (getTipo().equals(Anticipo.class.getSimpleName())) {
				return ((Anticipo) getPago()).getAnticipo().getConceptos()
						.get(0).getCuentaId();
			}
			if (getTipo().equals(Caja.class.getSimpleName())
					&& entidad == WebKeysGlobal.AMTIMA) {
				return 5; //5 es para caja chica, 4 es para caja
			}
			
			if (getTipo().equals(Caja.class.getSimpleName())
					&& entidad == WebKeysGlobal.UOMA &&
					((Caja)pago).getTipo_pago()==7) {
				int cta=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CTA_ID_CAJA_7") );
				return cta;
			}
			
			if (getTipo().equals(PagoSinSalidaDeFondos.class.getSimpleName()) 
					&& entidad == WebKeysGlobal.OSPIM) {
				return ConceptoServiceUtil.getCuentaPagoSinSalidaDeFondos(
						fechaOP, entidad,((PagoSinSalidaDeFondos)pago).getTipo_pago()).getId();
			}
			
			return getCuentaBancaria().getCuentaAsociada().getId();
		}

	}

	public List<OrdenPago.FormaPago> getPagos() {
		return pagos;
	}

	public void setPagos(List<OrdenPago.FormaPago> pagos) {
		this.pagos = pagos;
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

	public BigDecimal getDescuento() {
		return descuento;
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}

	public BigDecimal getDescuentoDrogueria() {
		return descuentoDrogueria;
	}

	public void setDescuentoDrogueria(BigDecimal descuentoDrogueria) {
		this.descuentoDrogueria = descuentoDrogueria;
	}

	public List<ItemOrdenPago> getItems() {
		return items;
	}

	public void setItems(List<ItemOrdenPago> items) {
		this.items = items;
	}

	public String getAFavorDe() {
		return aFavorDe;
	}

	public void setAFavorDe(String aFavorDe) {
		this.aFavorDe = aFavorDe;
	}

	public String getFechaHastaMesAnio() {
		return null != fechaHasta ? DateUtils.format(fechaHasta,
				DateUtils.PERIODO) : "";
	}

	public void setPrestador(boolean prestador) {
		this.prestador = prestador;
	}

	public boolean isPrestador() {
		return prestador;
	}

	public void setMostrarEnCuadro(boolean boolean1) {
		mostrarEnCuadro = boolean1;
	}

	public boolean isMostrarEnCuadro() {
		return mostrarEnCuadro;
	}

	protected class ColumnaConceptosSubdiario implements SubdiarioEgresoColumna {
		private String cuenta;
		private String descripcion;
		private int cuentaId;
		private BigDecimal importe;
		private String tipo;
		private boolean isAnticipo;

		public ColumnaConceptosSubdiario(String cuenta, String descripcion,
				BigDecimal importe, String tipo, boolean isAnticipo,
				int cuentaId) {
			this.cuenta = cuenta;
			this.descripcion = descripcion;
			this.importe = importe;
			this.tipo = tipo;
			this.isAnticipo = isAnticipo;
			this.cuentaId = cuentaId;
		}

		public String getCuenta(int entidad) {
			return cuenta;
		}

		public String getCuenta() {
			return cuenta;
		}

		public String getDescripcionPAraSubdiario() {
			return descripcion;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public String getTipo() {
			return tipo;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((cuenta == null) ? 0 : cuenta.hashCode());
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
			ColumnaConceptosSubdiario other = (ColumnaConceptosSubdiario) obj;
			if (cuenta == null) {
				if (other.cuenta != null)
					return false;
			} else if (!cuenta.equals(other.cuenta))
				return false;
			return true;
		}

		public boolean isAnticipo() {
			return isAnticipo;
		}

		public void setAnticipo(boolean isAnticipo) {
			this.isAnticipo = isAnticipo;
		}

		public int getCuentaId(int entidad) {
			return cuentaId;
		}

		public int getCuentaId() {
			return cuentaId;
		}

		public void setCuentaId(int cuentaId) {
			this.cuentaId = cuentaId;
		}
	}

	public static class ItemOrdenPago {
		private Date fecha;
		private Date periodo;
		private Integer nroLiquidacion;
		private String codigoPrestador;
		private String prestador;
		private String farmacia;
		private Afiliado afiliado;
		private String nroRecetario;
		private String troquel;
		private String medicamento;
		private Integer cantidad;
		private BigDecimal pvp;
		private BigDecimal totalOspim;
		private BigDecimal totalAmtima;
		private String debito;
		private BigDecimal difOspim;
		private BigDecimal difAmtima;
		private Date alta_fecha;
		private String alta_usr;
		private String alta_ip;
		private Date modi_fecha;
		private String modi_usr;
		private String modi_ip;
		private Date baja_fecha;
		private String baja_usr;
		private String baja_ip;
		private Integer nroFarmacia;
		private Double porcentajeCubierto;
		private Double porcentajeOSPIM;
		private String pmi;
		private String cajaFarmacia;
		private String archivo;

		public Double getPorcentajeCubierto() {
			return porcentajeCubierto;
		}

		public void setPorcentajeCubierto(Double porcentajeCubierto) {
			this.porcentajeCubierto = porcentajeCubierto;
		}

		public Double getPorcentajeOSPIM() {
			return porcentajeOSPIM;
		}

		public void setPorcentajeOSPIM(Double porcentajeOSPIM) {
			this.porcentajeOSPIM = porcentajeOSPIM;
		}

		public String getPmi() {
			return pmi;
		}

		public void setPmi(String pmi) {
			this.pmi = pmi;
		}

		public Double getPorcentajeAmtima() {
			return porcentajeAmtima;
		}

		public void setPorcentajeAmtima(Double porcentajeAmtima) {
			this.porcentajeAmtima = porcentajeAmtima;
		}

		private Double porcentajeAmtima;

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

		public ItemOrdenPago() {

		}

		public ItemOrdenPago(String csv) throws Exception {
			int pos = 0;
			int index = 0;
			String[] partes = csv.split(",");
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			fecha = formatter.parse(partes[pos++]);
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMM");
			periodo = formatter2.parse(partes[pos++]);
			nroLiquidacion = Integer.valueOf(partes[pos++]);
			codigoPrestador = partes[pos++];
			prestador = partes[pos++];
			nroFarmacia = Integer.parseInt(partes[pos++]);
			farmacia = partes[pos++];// 6
			afiliado = new Afiliado();
			if (partes[pos].equals("OSP")) {// ESTA EN 7
				afiliado.setId_ospim(Integer.parseInt(partes[pos + 1]));
			} else if (partes[pos].equals("UOM")) {
				afiliado.setId_uoma(Integer.parseInt(partes[pos + 1]));
			} else if (partes[pos].equals("AMT")) {
				afiliado.setId_amtima(Integer.parseInt(partes[pos + 1]));
			}
			pos = pos + 2;// A 9
			afiliado.setInte(Integer.parseInt(partes[pos++]));
			afiliado.setApellido(partes[pos]);
			afiliado.setNombre(partes[pos++]);// ACA 10
			// VINO CON NOMBRE Y APELLIDO SEPARADO O NO
			try {
				Integer.parseInt(partes[pos]);
			} catch (NumberFormatException r) {
				afiliado.setApellido(partes[pos]); // ENTONCES 11 ES APELLIDO
				index++;
				pos += 1;
			}
			// afiliado.setNombre(partes[11]);
			nroRecetario = partes[pos++];// 12
			troquel = partes[pos++];
			medicamento = partes[pos++];
			pmi = partes[pos++];
			cantidad = Integer.parseInt(partes[pos++]);
			pvp = new BigDecimal(partes[pos++]);// 17
			// total = partes[17];
			pos += 1;
			porcentajeCubierto = Double.parseDouble(!partes[pos].trim().equals(
					"") ? partes[pos] : "0");
			pos += 1;// 19
			porcentajeOSPIM = Double
					.parseDouble(!partes[pos].trim().equals("") ? partes[pos]
							: "0");
			pos += 1;// 20
			totalOspim = new BigDecimal(partes[pos] != null
					&& !partes[pos].trim().equals("") ? partes[pos] : "0");
			pos += 1;// 21
			if (partes.length > 21 + index) {
				porcentajeAmtima = Double.parseDouble(partes[pos] != null
						&& !partes[pos].trim().equals("") ? partes[pos] : "0");
			}
			pos += 1;// 22
			if (partes.length > 22) {
				totalAmtima = new BigDecimal(partes[pos] != null
						&& !partes[pos].trim().equals("") ? partes[pos] : "0");
			}

			pos += 1;// 23
			if (partes.length > 24) {
				debito = partes[pos];
			}
			pos += 1;// 24
			if (partes.length > 25) {
				difOspim = new BigDecimal(partes[pos] != null
						&& !partes[pos].trim().equals("") ? partes[pos] : "0");
			}
			pos += 1;// 25
			if (partes.length > 26) {
				difAmtima = new BigDecimal(partes[pos] != null
						&& !partes[pos].trim().equals("") ? partes[pos] : "0");
			}
		}

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public Integer getNroLiquidacion() {
			return nroLiquidacion;
		}

		public void setNroLiquidacion(Integer nro) {
			this.nroLiquidacion = nro;
		}

		public String getCodigoPrestador() {
			return codigoPrestador;
		}

		public void setCodigoPrestador(String codigo) {
			this.codigoPrestador = codigo;
		}

		public String getFarmacia() {
			return farmacia;
		}

		public void setFarmacia(String farmacia) {
			this.farmacia = farmacia;
		}

		public String getNroRecetario() {
			return nroRecetario;
		}

		public void setNroRecetario(String nroRecetario) {
			this.nroRecetario = nroRecetario;
		}

		public String getTroquel() {
			return troquel;
		}

		public void setTroquel(String troquel) {
			this.troquel = troquel;
		}

		public String getMedicamento() {
			return medicamento;
		}

		public void setMedicamento(String medicamento) {
			this.medicamento = medicamento;
		}

		public Integer getCantidad() {
			return cantidad;
		}

		public void setCantidad(Integer cantidad) {
			this.cantidad = cantidad;
		}

		public BigDecimal getPvp() {
			return pvp;
		}

		public void setPvp(BigDecimal pvp) {
			this.pvp = pvp;
		}

		public BigDecimal getTotalOspim() {
			return totalOspim != null ? totalOspim : new BigDecimal("0");
		}

		public void setTotalOspim(BigDecimal totalOspim) {
			this.totalOspim = totalOspim;
		}

		public BigDecimal getTotalAmtima() {
			return totalAmtima != null ? totalAmtima : new BigDecimal("0");
		}

		public void setTotalAmtima(BigDecimal totalAmtima) {
			this.totalAmtima = totalAmtima;
		}

		public String getDebito() {
			return debito != null ? debito : "";
		}

		public void setDebito(String debito) {
			this.debito = debito;
		}

		public BigDecimal getDifOspim() {
			return difOspim != null ? difOspim : new BigDecimal("0");
		}

		public void setDifOspim(BigDecimal difOspim) {
			this.difOspim = difOspim;
		}

		public BigDecimal getDifAmtima() {
			return difAmtima != null ? difAmtima : new BigDecimal("0");
		}

		public void setDifAmtima(BigDecimal difAmtima) {
			this.difAmtima = difAmtima;
		}

		public void setPeriodo(Date periodo) {
			this.periodo = periodo;
		}

		public Date getPeriodo() {
			return periodo;
		}

		public void setAfiliado(Afiliado afiliado) {
			this.afiliado = afiliado;
		}

		public Afiliado getAfiliado() {
			return afiliado;
		}

		public void setNroFarmacia(Integer nroFarmacia) {
			this.nroFarmacia = nroFarmacia;
		}

		public Integer getNroFarmacia() {
			return nroFarmacia;
		}

		public void setPrestador(String prest) {
			this.prestador = prest;
		}

		public String getPrestador() {
			return prestador;
		}

		public static ItemOrdenPago getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static ItemOrdenPago getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ItemOrdenPago itemOrdenPago = new ItemOrdenPago();
			itemOrdenPago.setFecha(rs.getDate(prefix + "fecha"));
			itemOrdenPago.setPeriodo(rs.getDate(prefix + "periodo"));
			itemOrdenPago.setNroLiquidacion(rs.getInt(prefix
					+ "nro_liquidacion"));
			itemOrdenPago.setCodigoPrestador(rs.getString(prefix
					+ "nro_prestador"));
			itemOrdenPago.setPrestador(rs.getString(prefix + "prestador"));
			itemOrdenPago.setFarmacia(rs.getString(prefix + "farmacia"));
			itemOrdenPago.setNroRecetario(rs
					.getString(prefix + "nro_recetario"));
			itemOrdenPago.setTroquel(rs.getString(prefix + "nro_troquel"));
			itemOrdenPago.setMedicamento(rs.getString(prefix + "medicamento"));
			itemOrdenPago.setCantidad(rs.getInt(prefix + "cantidad"));
			itemOrdenPago.setPvp(rs.getBigDecimal(prefix + "pvp"));
			itemOrdenPago.setTotalOspim(rs
					.getBigDecimal(prefix + "total_ospim"));
			itemOrdenPago.setTotalAmtima(rs.getBigDecimal(prefix
					+ "total_amtima"));
			itemOrdenPago.setDebito(rs.getString(prefix + "debito"));
			itemOrdenPago.setDifOspim(rs.getBigDecimal(prefix + "dif_ospim"));
			itemOrdenPago.setDifAmtima(rs.getBigDecimal(prefix + "dif_amtima"));
			Afiliado afi = new Afiliado();
			afi.setId_ospim(rs.getInt(prefix + "id_ospim"));
			afi.setId_amtima(rs.getInt(prefix + "id_amtima"));
			afi.setId_uoma(rs.getInt(prefix + "id_uoma"));
			afi.setInte(rs.getInt(prefix + "inte"));
			afi.setNombre(rs.getString(prefix + "nombre_apellido"));
			itemOrdenPago.setAfiliado(afi);
			itemOrdenPago.setNroFarmacia(rs.getInt(prefix + "nro_farmacia"));
			itemOrdenPago.setPorcentajeOSPIM(rs.getDouble(prefix
					+ "porcentaje_ospim"));
			itemOrdenPago.setPorcentajeAmtima(rs.getDouble(prefix
					+ "porcentaje_amtima"));
			itemOrdenPago.setPmi(rs.getString(prefix + "pmi"));

			itemOrdenPago.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
			itemOrdenPago.setAlta_usr(rs.getString(prefix + "alta_usr"));
			itemOrdenPago.setAlta_ip(rs.getString(prefix + "alta_ip"));
			itemOrdenPago.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
			itemOrdenPago.setModi_usr(rs.getString(prefix + "modi_usr"));
			itemOrdenPago.setModi_ip(rs.getString(prefix + "modi_ip"));
			itemOrdenPago.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
			itemOrdenPago.setBaja_usr(rs.getString(prefix + "baja_usr"));
			itemOrdenPago.setBaja_ip(rs.getString(prefix + "baja_ip"));

			return itemOrdenPago;
		}

		public String getCajaFarmacia() {
			return cajaFarmacia;
		}

		public void setCajaFarmacia(String cajaFarmacia) {
			this.cajaFarmacia = cajaFarmacia;
		}

		public String getArchivo() {
			return archivo;
		}

		public void setArchivo(String archivo) {
			this.archivo = archivo;
		}

	}

	public BigDecimal getTotalAnticipos() {
		return totalAnticipos;
	}

	public String getTotalAnticiposAsString() {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return totalAnticipos != null ? formatter.format(totalAnticipos
				.doubleValue()) : "";
	}

	public void setTotalAnticipos(BigDecimal totalAnticipos) {
		this.totalAnticipos = totalAnticipos;
	}

	public String getBaseDescuentoFarmacia() {
		return baseDescuentoFarmacia;
	}

	public void setBaseDescuentoFarmacia(String baseDescuentoFarmacia) {
		this.baseDescuentoFarmacia = baseDescuentoFarmacia;
	}

	public int getIdLote() {
		return idLote;
	}

	public void setIdLote(int id_lote) {
		this.idLote = id_lote;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getObsInterna() {
		return obsInterna;
	}

	public void setObsInterna(String obsInterna) {
		this.obsInterna = obsInterna;
	}

	public Date getFechaFirma() {
		return fechaFirma;
	}

	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public String getNroChequeAsString() {
		String cadena = null;
		// FORMA PAGO ANTICIPO
		for (OrdenPago.FormaPago fp : getFormaPago()) {
			if (fp.getPago() instanceof Cheque) {
				cadena = fp.getPago().getNumeroStr();
			}
		}

		return cadena;
	}

	public String getCBUAsString() {
		String cadena = null;
		// FORMA PAGO ANTICIPO
		for (OrdenPago.FormaPago fp : getFormaPago()) {
			if (fp.getPago() instanceof PagoBancario
					&& ((PagoBancario) fp.getPago()).getTipo_pago() == PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA) {
				cadena = fp.getPago().getNumeroStr();
			}
		}

		return cadena;
	}

	public boolean isMostrarComprobantesEnSubdiario() {
		return mostrarComprobantesEnSubdiario;
	}

	public void setMostrarComprobantesEnSubdiario(
			boolean mostrarComprobantesEnSubdiario) {
		this.mostrarComprobantesEnSubdiario = mostrarComprobantesEnSubdiario;
	}

	public boolean isTieneRetencion() {
		return tieneRetencion;
	}

	public void setTieneRetencion(boolean tieneRetencion) {
		this.tieneRetencion = tieneRetencion;
	}

	public String getCBU() {
		return cbu;
	}

	public void setCBU(String cbu) {
		this.cbu = cbu;
	}

	public String getEmailCBU() {
		return emailCBU;
	}

	public void setEmailCBU(String emailCBU) {
		this.emailCBU = emailCBU;
	}

	public boolean isTieneRetencionIIBB() {
		return tieneRetencionIIBB;
	}

	public void setTieneRetencionIIBB(boolean tieneRetencionIIBB) {
		this.tieneRetencionIIBB = tieneRetencionIIBB;
	}
	
	public boolean isTieneRetencionIVA() {
		return tieneRetencionIVA;
	}

	public void setTieneRetencionIVA(boolean tieneRetencionIVA) {
		this.tieneRetencionIVA = tieneRetencionIVA;
	}

	public static void enviarMailTransferencia(String cbu, String email,
			List<byte[]> adjunto) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		EnviaEmailsThread.enviarMailDesatendido(
				"OSPIM - Aviso transferencia realizada", cbu, destinatarios,
				adjunto);
	}

	public String getCBUTransferencia() {
		StringBuffer leyenda = new StringBuffer("");
		for (OrdenPago.FormaPago fp : this.getPagos()) {
			if (fp.getPago() instanceof PagoBancario
					&& ((PagoBancario) fp.getPago()).getTipo_pago() == PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA) {
				leyenda.append("Le informamos que se realizará una transferencia en las próximas 24 hs., según el detalle de Orden de pago adjunto. Por favor, revise su estado de cuenta.\n\r");
				leyenda.append("Cualquier duda, quedamos  a su disposición.\n\r");
				leyenda.append("Atte,\n\r");
				leyenda.append("Obra Social del Personal de la Industria Molinera");
				leyenda.append("\n\r");
			}

		}
		return leyenda.length() > 0 ? leyenda.toString() : null;
	}

	public List<Liquidacion> getLiquidacionesList() {
		return liquidacionesList;
	}

	/**
	 * @param liquidacionesList
	 *            the liquidacionesList to set
	 */
	public void setLiquidacionesList(List<Liquidacion> liquidacionesList) {
		this.liquidacionesList = liquidacionesList;
	}

	public String getLiquidacionesListAsString() {
		StringBuffer cadena = new StringBuffer();
		if (null != liquidacionesList) {
			for (Liquidacion liq : liquidacionesList) {
				cadena.append(liq.getId_liquidacionString()).append("|");
			}
		}
		return cadena.length() > 0 ? cadena.toString() : null;
	}
	
	public String getCBUTransferenciaIntegracion() {
		StringBuffer leyenda = new StringBuffer("");
		for (OrdenPago.FormaPago fp : this.getPagos()) {
			if (fp.getPago() instanceof PagoBancario
					&& ((PagoBancario) fp.getPago()).getTipo_pago() == PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA) {
				leyenda.append("Estimado prestador/a:\n\r");
				leyenda.append("\n\r");
				
				leyenda.append("Nos dirigimos a Uds. desde la OBRA SOCIAL DEL PERSONAL DE LA INDUSTRIA MOLINERA (OSPIM) a fin de informarle\n\r");
				leyenda.append("que hemos iniciado el proceso de transferencia a la cuenta bancaria oportunamente informada por Ud. En concepto de\n\r");
				leyenda.append("prestaciones de discapacidad.");
				leyenda.append("\n\r");
				leyenda.append("Le rogamos que revise su cuenta bancaria a fin de verificar que el pago se encuentre acreditado dentro de las próximas 48/72hs hábiles.\n\r");
				leyenda.append("\n\r");
				
				leyenda.append("Le recordamos que Ud. debe enviarnos el recibo correspondiente a recibosintegracion@ospim.org.ar o completar el documento\n\r");
				leyenda.append("'Detalle de Liquidación de Pago' donde conste el pago de las facturas.\n\r");
				leyenda.append("Puede ingresar a dicho documento a través de este link\n\r");
				
				//leyenda.append("http://www.ospim.org.ar/PDF/DETALLE DE LIQUIDACIÓN DE PAGO A PRESTADORES DE DISCAPACIDAD Ver 0 08102018.pdf\n\r");
				leyenda.append("http://www.ospim.org.ar/PDF/DETALLE%20DE%20LIQUIDACI%C3%93N%20DE%20PAGO%20A%20PRESTADORES%20DE%20DISCAPACIDAD%20Ver%200%2008102018.pdf");
				
				leyenda.append("\n\r");
				
				leyenda.append("Por favor no responda este correo. Por cualquier consulta o aclaración referida a pagos contactese al 0810-345-0208 o a la casilla de mail portalproveedores@ospim.org.ar\n\r");
				leyenda.append("\n\r");
				
				
                
				
				leyenda.append("Atte,\n\r");
				leyenda.append("\n\r");
				leyenda.append("\n\r");
				leyenda.append("Integración");
				leyenda.append("\n\r");
			}

		}
		return leyenda.length() > 0 ? leyenda.toString() : null;
	}
	
	public static void enviarMailTransferenciaIntegracion(String cuit,String cbu, String email,
			List<String> destinatariosBCC,
			List<byte[]> adjunto) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		EnviaEmailsThread.enviarMailDesatendidoCCO(
				"OSPIM - Aviso transferencia realizada CUIT "+cuit, cbu, destinatarios,destinatariosBCC,
				adjunto);
	}

	
	public static void enviarMailTransferenciaBCC(String cbu, String email,List<String> destinatariosBCC,
			List<byte[]> adjunto) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		EnviaEmailsThread.enviarMailDesatendidoCCO(
				"OSPIM - Aviso transferencia realizada", cbu, destinatarios,destinatariosBCC,
				adjunto);
	}
	
	public static void enviarMailTransferenciaSeccional(String seccionalDesc,String text, String email,
			List<String> emailCCO, List<byte[]> adjunto, int delay) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		
		EnviaEmailsThread.enviarMailDesatendidoCCO("OSPIM - Aviso transferencia realizada Seccional "+seccionalDesc,
				text, destinatarios,emailCCO,adjunto);
	}
	
	

	public static void enviarMailTransferenciaAfiliado(String apeNom,String text, String email, String numerosReclamos, int delay) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		String numerosReclamosAux = numerosReclamos; 
		int cant =  numerosReclamosAux.split("-").length;
		//String ReclamoTitulo = cant == 1 ? "N° Reclamo" : "N° Reclamos";
		EnviaEmailsThread.enviarMailDesatendido(
				"N° Reclamo" + ": " + numerosReclamos +  " OSPIM - Aviso transferencia realizada a favor de "+ apeNom , text, destinatarios, 1);
	}

	
	public static void enviarMailTransferenciaSeccional(String seccionalDesc,String text, String email,
			List<String> emailCCO, List<byte[]> adjunto, int delay,ScheduledExecutorService scheduler) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		
		EnviaEmailsThread.enviarMailDesatendidoCCO("OSPIM - Aviso transferencia realizada Seccional "+seccionalDesc,
				text, destinatarios,emailCCO,adjunto,delay,scheduler);
	}
	
	public static void enviarMailTransferenciaSeccional(String seccionalDesc,String text, String email,
			List<String> emailCCO, List<byte[]> adjunto, int delay,ScheduledExecutorService scheduler,List<String>extension) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		
		EnviaEmailsThread.enviarMailDesatendidoCCO("OSPIM - Aviso transferencia realizada Seccional "+seccionalDesc,
				text, destinatarios,emailCCO,adjunto,delay,scheduler,extension);
	}
	
	public static void enviarMailTransferenciaAfiliado(String apeNom,String text, String email, String numerosReclamos, int delay,ScheduledExecutorService scheduler) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		String numerosReclamosAux = numerosReclamos; 
		int cant =  numerosReclamosAux.split("-").length;
		//String ReclamoTitulo = cant == 1 ? "N° Reclamo" : "N° Reclamos";
		EnviaEmailsThread.enviarMailDesatendido(
				"N° Reclamo" + ": " + numerosReclamos +  " OSPIM - Aviso transferencia realizada a favor de "+ apeNom , text, destinatarios, 1,delay,scheduler);
	}

	
	public static void enviarMailTransferenciaGral(String cuit,String cbu, String email,
			List<String> destinatariosBCC,
			List<byte[]> adjunto, int delay,ScheduledExecutorService scheduler) {
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(email);
		EnviaEmailsThread.enviarMailDesatendidoCCO(
				"OSPIM - Aviso transferencia realizada CUIT "+cuit, cbu, destinatarios,destinatariosBCC,
				adjunto,delay,scheduler);
	}
	
	@Override
	public String toString() {
		return "OrdenPago [id=" + id + "]";
	}

	public static List<RetencionIIBB> getRetencionIIBB(Empresa e,List<Comprobante> comprobantes) {
		List<RetencionIIBB> r =null;
		
		Map<Integer,Double>mJur=new HashMap<Integer,Double>();
		Map<Integer,Double>alicuotas=new HashMap<Integer,Double>();
		 
		String jur = TraeListasServiceUtil.getSystemConfig("IIBB_JURISDICCIONES_RETENER");
		String ctaStr = TraeListasServiceUtil.getSystemConfig("IIBB_CTA_BANCARIA_RETENCION");
		
            Double total=0D;  			
			for(Comprobante c:comprobantes) {
				for(ComprobanteConcepto cc:c.getConceptos()) {
					int resultado = cc.getJurisdiccionIIBB()==null ||cc.getJurisdiccionIIBB()==0?-1:jur.indexOf(cc.getJurisdiccionIIBB().toString());
					if(resultado != -1) {
                        Double alicuota = alicuotas.get(cc.getJurisdiccionIIBB()); 			        	
			        	if(alicuota==null && (RetencionIIBB.BSAS).equals(cc.getJurisdiccionIIBB())) {
			        		//Buscar si  empresa corresponde Retener 
			        		// empresa jurisdiccion
			        		// Fin
			        		ClientARBA cli=ClientARBA.getAlicuota(c.getCuit(),new Date());
			        		
			        		if(cli.getError()!=null && !"".equals(cli.getError())) {
			        			r=new ArrayList<RetencionIIBB>();
			        			RetencionIIBB ri = new RetencionIIBB();
			        			ri.setError(cli.getError());
			        			r.add(ri);
			        			return r;
			        		}
			        		
			        		alicuota=cli.getAlicuotaRetencion()/100;
			        		alicuotas.put(cc.getJurisdiccionIIBB(),alicuota);
			        	}
			        	total=mJur.get(cc.getJurisdiccionIIBB());
			        	if(total==null) total=0D;
			        	if("NCR".equals(c.getTipoComprobante())) {
			        	   total+= cc.getExento().negate().doubleValue();
			        	   total+=cc.getGravadoIVA().negate().doubleValue();
			        	}else {
			        	   total+= cc.getExento().add(cc.getGravadoIVA()).doubleValue();
			        	}   
			        	mJur.put(cc.getJurisdiccionIIBB(), total);
			        }
				}
			}
			
			if(!mJur.isEmpty()) {
				r=new ArrayList<RetencionIIBB>();
				for (Map.Entry<Integer,Double> entry : mJur.entrySet()) {
				    Integer jurisdiccion = entry.getKey();
				    total = entry.getValue();
				    Double alicuota=0D;
				    alicuota=alicuotas.get(jurisdiccion);
				    String minStr =""; 
				    Double minimo=0D;
				    try{
				    	minStr=TraeListasServiceUtil.getSystemConfig("IIBB_MINIMO_NO_IMPONIBLE_RETENCION_"+jurisdiccion.toString().trim());
				    	if(Double.valueOf(minStr)>0D) {
				    		minimo=Double.valueOf(minStr);
				    	}
				    }catch(Exception e1) {}
				    //Consulta minimo no imponible
				    
				    
				    if(total>minimo) {
				      Double iRet= total*alicuota;
					  RetencionIIBB ri = new RetencionIIBB();
					  ri.setImporte(new BigDecimal(iRet).setScale(2, RoundingMode.HALF_UP));
					  ri.setJurisdiccion(jurisdiccion);
					  ri.setAlicuota(alicuota);
					  int nroCta=Integer.parseInt(ctaStr);
					  CuentaBancaria cta = new CuentaBancaria(nroCta);					
					  ri.setCuentaBancaria(cta);
                      r.add(ri);
				    }  
				}
			}
		return r;
	}
	
	public static RetencionIVA getRetencionIVA(Empresa e,List<Comprobante> comprobantes) {
		RetencionIVA r =null;
		Double totalA=0D;
		Double totalM=0D;
		for(Comprobante c:comprobantes) {
			if(("FCP".equals(c.getTipoComprobante()) || "FOS".equals(c.getTipoComprobante()) ) && 
					("A".equals(c.getLetraComprobante()) || "M".equals(c.getLetraComprobante())) ) {
				for(ComprobanteConcepto cc:c.getConceptos()) {
					if("A".equals(c.getLetraComprobante())) {
						totalA+= cc.getIva().doubleValue();
					}else {
						totalM+= cc.getIva().doubleValue();
					}
				}
			}
		}
		
		if(totalA>0 || totalM>0) {
			Double iRet=0D;
			String aliA = TraeListasServiceUtil.getSystemConfig("IVA_ALICUOTA_RETENER_A");
			String aliM = TraeListasServiceUtil.getSystemConfig("IVA_ALICUOTA_RETENER_M");
			String ctaStr = TraeListasServiceUtil.getSystemConfig("IVA_CTA_BANCARIA_RETENCION");
			
			iRet=totalA*Double.parseDouble(aliA)+totalM*Double.parseDouble(aliM);
			if(iRet>0) {
				r = new RetencionIVA();
				r.setImporte(new BigDecimal(iRet).setScale(2,  RoundingMode.UP));
				int nroCta=Integer.parseInt(ctaStr);
				CuentaBancaria cta = new CuentaBancaria(nroCta);					
				r.setCuentaBancaria(cta);
			}
		}
		
		return r;
	}
	
	
	public static RetencionGanancias getRetencionGananciasEspecial(Empresa e,List<Comprobante> comprobantes) {
		RetencionGanancias r =null;
		Double totalA=0D;
		Double totalM=0D;
		for(Comprobante c:comprobantes) {
			if(("FCP".equals(c.getTipoComprobante()) || "FOS".equals(c.getTipoComprobante()) ) && 
					("A".equals(c.getLetraComprobante()) || "M".equals(c.getLetraComprobante())) ) {
//			if("FCP".equals(c.getTipoComprobante()) && ("A".equals(c.getLetraComprobante()) || "M".equals(c.getLetraComprobante())) ) {
				for(ComprobanteConcepto cc:c.getConceptos()) {
					if("A".equals(c.getLetraComprobante())) {
						totalA+=  cc.getExento().add(cc.getGravadoIVA()).doubleValue();
					}else {
						totalM+=  cc.getExento().add(cc.getGravadoIVA()).doubleValue();
					}
				}
			}
		}
		
		if(totalA>0 || totalM>0) {
			Double iRet=0D;
			String aliA = TraeListasServiceUtil.getSystemConfig("GANANCIAS_ALICUOTA_RETENER_A");
			String aliM = TraeListasServiceUtil.getSystemConfig("GANANCIAS_ALICUOTA_RETENER_M");
			String ctaStr = TraeListasServiceUtil.getSystemConfig("GANANCIAS_CTA_BANCARIA_RETENCION");
			
			iRet=totalA*Double.parseDouble(aliA)+totalM*Double.parseDouble(aliM);
			if(iRet>0) {
				r = new RetencionGanancias();
				r.setImporte(new BigDecimal(iRet).setScale(2, RoundingMode.UP));
				int nroCta=Integer.parseInt(ctaStr);
				CuentaBancaria cta = new CuentaBancaria(nroCta);					
				r.setCuentaBancaria(cta);
			}
		}
		
		return r;
	}

}
