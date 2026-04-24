package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.OrdenPago.ColumnaConceptosSubdiario;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.OrdenPagoOspimAnticiposNoUsadosException;
import ar.com.ospim.liquidaciones.OrdenPagoOspimSinComprobantes;
import ar.com.ospim.liquidaciones.OrdenPagoOspimSinPagos;
import ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosMenorQueComprobantesException;
import ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException;
import ar.com.ospim.liquidaciones.PagoMayorQueComprobanteException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class OrdenPagoUoma extends OrdenPago {
	private static Log _log = LogFactoryUtil.getLog(OrdenPagoUoma.class);
	public static OrdenPagoUoma getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}
	
	static {
		try {
			fechaExcepcionIni = Calendar.getInstance();
			fechaExcepcionIni.setTime(DateUtils.parse(
					WebKeysLiquidaciones.EXCEPCION_SUBDIARIO_CUENTA_PASIVO_INI,
					DateUtils.SHORT));
			fechaExcepcionFin = Calendar.getInstance();
			fechaExcepcionFin.setTime(DateUtils.parse(
					WebKeysLiquidaciones.EXCEPCION_SUBDIARIO_CUENTA_PASIVO_FIN,
					DateUtils.SHORT));
		} catch (ParseException e) {
		}
	}

	public static OrdenPagoUoma getMapping(ResultSet rs, String prefix)
			throws SQLException {
		OrdenPagoUoma op = new OrdenPagoUoma();
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

	public static OrdenPagoUoma getMappingRepo(ResultSet rs, String prefix)
			throws SQLException {
		OrdenPagoUoma op = new OrdenPagoUoma();
		op.setId(rs.getInt(prefix + "id_orden_pago"));
		op.setImporte(rs.getBigDecimal(prefix + "importe"));
		op.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		op.setAlta_usr(rs.getString(prefix + "alta_usr"));
		op.setAlta_ip(rs.getString(prefix + "alta_ip"));
		op.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		op.setModi_usr(rs.getString(prefix + "modi_usr"));
		op.setModi_ip(rs.getString(prefix + "modi_ip"));
		op.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		op.setBaja_usr(rs.getString(prefix + "baja_usr"));
		op.setBaja_ip(rs.getString(prefix + "baja_ip"));

		op.setPrestador(rs.getBoolean(prefix + "prestador"));
		op.setFarmacia(rs.getBoolean(prefix + "farmacia"));
		op.setObservaciones(rs.getString(prefix + "observaciones"));
		String cuitAcreedor = rs.getString(prefix + "cuit_acreedor");
		String sucuAcreedor = rs.getString(prefix + "sucu_acreedor");
		int seccional = rs.getInt(prefix + "id_seccional");

		op.setAcreedor(new Empresa(cuitAcreedor, sucuAcreedor, null));
		op.setSeccional(new Seccional(seccional, null));

		return op;
	}

	public OrdenPagoUoma() {
	}

	public OrdenPagoUoma(int idOpOspim) {
		setId(idOpOspim);
	}

	/**
	 * Obtiene todos los comprobantes agrupados por concepeto para el subdiario
	 * de egreso
	 */
	private List<? extends SubdiarioEgresoColumna> getHaciaOriginal() {
		List<ColumnaConceptosSubdiario> cuentas = new ArrayList<ColumnaConceptosSubdiario>();
		if (getComprobantes() != null) {
			for (Comprobante c : getComprobantes()) {
				Calendar pago = Calendar.getInstance();
				pago.setTime(c.getFechaPrimerPago());
				pago.set(Calendar.DATE, 1);

				if (getFormaPago() != null
						&& getFormaPago().indexOf(
								new OrdenPago.FormaPago(new Anticipo(c))) != -1) {
					continue;
				}

				Calendar recepcion = Calendar.getInstance();
				if (c.getFechaRecepcion() != null) {
					recepcion.setTime(c.getFechaRecepcion());
				} else {
					recepcion.setTime(c.getFechaEmision());
				}
				recepcion.set(Calendar.DATE, 1);
				boolean pasivo = false;
				if (pago.compareTo(recepcion) > 0
						&& !excepcionCuentasPasivo(c.getFechaPrimerPago())) {
					pasivo = true;
				}else if(pago.compareTo(recepcion) == 0 && debeMostrarCuentaPasivo(c.getFechaEmision())) {
					pasivo = true;
				}
				if (c.getConceptos() != null) {
					List<ComprobanteConcepto> ccList = c.getConceptos();
					for (ComprobanteConcepto cc : ccList) {
						BigDecimal importe = cc.getImporte();
						if (c.isDebitoParaEgreso()) {
							importe = importe.negate();
						}
						
						PlanCuentas pc = cc.getConceptoComprobante()
								.getPlanCuentas();
						if (pasivo
								|| cc.getConceptoComprobante().getId() == ConceptoServiceUtil
										.getIdSueldosSeccionales(c
												.getFechaRecepcion())
								|| cc.getConceptoComprobante().getId() == ConceptoServiceUtil
										.getIdSueldosSedeCentral(c
												.getFechaRecepcion())) {
							pc = cc.getConceptoComprobante()
									.getPlanCuentasPasivo();
						}
						
						ColumnaConceptosSubdiario col = new ColumnaConceptosSubdiario(
								pc.getNumero(), "", BigDecimal.ZERO, "", false,
								pc.getId());
						int indexOf = cuentas.indexOf(col);
						if (indexOf == -1) {
							cuentas.add(new ColumnaConceptosSubdiario(pc
									.getNumero(), pc.getCuenta(), importe, "",
									false, pc.getId()));
						} else {
							ColumnaConceptosSubdiario comprobanteConcepto = cuentas
									.get(indexOf);
							comprobanteConcepto.setImporte(comprobanteConcepto
									.getImporte().add(importe));
						}
					}
				}
			}
		}
		return cuentas;
	}

	// EXCEPCION QUE SE UTILIZO PARA QUE NO APAREZCAN CUENTAS DE PASIVO
	private boolean excepcionCuentasPasivo(Date pago) {
		Calendar pagoC = Calendar.getInstance();
		pagoC.setTime(pago);
		if (fechaExcepcionIni.compareTo(pagoC) <= 0
				&& fechaExcepcionFin.compareTo(pagoC) >= 0) {
			return true;
		}
		return false;
	}

	// ESTE ES EL DEBE
	public List<? extends SubdiarioEgresoColumna> getDesde() {
		// SI ES UNA ANULACION HAY QUE INVERTIR COLUMNAS
		if (getBaja_fecha() == null) {
			return getDesdeOriginal();
		} else {
			return getHaciaOriginal();
		}
	}

	// ESTE ES EL HABER
	public List<? extends SubdiarioEgresoColumna> getHacia() {
		// SI ES UNA ANULACION HAY QUE INVERTIR COLUMNAS
		if (getBaja_fecha() == null) {
			return getHaciaOriginal();
		} else {
			return getDesdeOriginal();
		}
	}

	
	
	/**
	 * Obtiene todas las formas de pago que no sean anticipos, y agrupa los
	 * anticipos segun su concepto para el subdiario de egreso
	 */
	/*
	private List<? extends SubdiarioEgresoColumna> getDesdeOriginal() {
		List<SubdiarioEgresoColumna> desde = new ArrayList<SubdiarioEgresoColumna>();
		List<ColumnaConceptosSubdiario> cuentas = new ArrayList<ColumnaConceptosSubdiario>();
		if (getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : getFormaPago()) {
				if (!fp.getTipo().equals(Anticipo.class.getSimpleName())) {
					desde.add(fp);
				} else {
					List<ComprobanteConcepto> conceptos = ((Anticipo) fp
							.getPago()).getAnticipo().getConceptos();

					for (ComprobanteConcepto concepto : conceptos) {
						PlanCuentas pc = concepto.getConceptoComprobante()
								.getPlanCuentas();
						ColumnaConceptosSubdiario col = new ColumnaConceptosSubdiario(
								pc.getNumero(), "", BigDecimal.ZERO, "", true,
								pc.getId());
						int indexOf = cuentas.indexOf(col);
						if (indexOf == -1) {
							cuentas.add(new ColumnaConceptosSubdiario(pc
									.getNumero(), pc.getCuenta(), concepto
									.getImporte(), "", true, pc.getId()));
						} else {
							ColumnaConceptosSubdiario comprobanteConcepto = cuentas
									.get(indexOf);
							comprobanteConcepto.setImporte(comprobanteConcepto
									.getImporte().add(concepto.getImporte()));
						}
					}
				}
			}
		}
		desde.addAll(cuentas);
		return desde;
	}
	
	*/
	
	private List<? extends SubdiarioEgresoColumna> getDesdeOriginal() {
		List<SubdiarioEgresoColumna> desde = new ArrayList<SubdiarioEgresoColumna>();
		List<ColumnaConceptosSubdiario> cuentas = new ArrayList<ColumnaConceptosSubdiario>();
		OrdenPago.FormaPago aux=null; 
		try {
			if (getFormaPago() != null) {
				for (OrdenPago.FormaPago fp : getFormaPago()) {
					aux=fp;
					if (!fp.getTipo().equals(Anticipo.class.getSimpleName())) {
						desde.add(fp);
					} else {
						
						List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
						if(((Anticipo) fp.getPago()).getAnticipo().getConceptos()==null && 
								"ABA".equalsIgnoreCase( ((Anticipo) fp.getPago()).getAnticipo().getTipoComprobante()) ){
							Comprobante a = ((Anticipo) fp.getPago()).getAnticipo();
//							FIXME detectar la entidad correctamente, que pasa si es AMtima
							
							conceptos=ComprobanteServiceUtil.getConceptos(a, WebKeysGlobal.UOMA); 
							List<PlanCuentas> cc =TraeListasServiceUtil.getPlanCuentas(fp.getFechaOP() ,  WebKeysGlobal.UOMA);
							int idCuenta=0;
							for(ComprobanteConcepto c:conceptos){
								if(c.getCuentaId()==0){
									for(PlanCuentas p:cc){
										if(p.getNumero().equalsIgnoreCase(c.getConceptoComprobante().getPlanCuentas().getNumero()) ){
											c.getConceptoComprobante().getPlanCuentas().setId(p.getId());
											break;
										}
									}
								}
							}
							
						}else{
							conceptos = ((Anticipo) fp.getPago()).getAnticipo().getConceptos();
						}
						
						for (ComprobanteConcepto concepto : conceptos) {
							PlanCuentas pc = concepto.getConceptoComprobante()
									.getPlanCuentas();
							ColumnaConceptosSubdiario col = new ColumnaConceptosSubdiario(
									pc.getNumero(), "", BigDecimal.ZERO, "",
									true, pc.getId());
							int indexOf = cuentas.indexOf(col);
							if (indexOf == -1) {
								cuentas.add(new ColumnaConceptosSubdiario(pc
										.getNumero(), pc.getCuenta(), concepto
										.getImporte(), "", true, pc.getId()));
							} else {
								ColumnaConceptosSubdiario comprobanteConcepto = cuentas
										.get(indexOf);
								comprobanteConcepto
										.setImporte(comprobanteConcepto
												.getImporte().add(
														concepto.getImporte()));
							}
						}
					}
				}
			}
			desde.addAll(cuentas);
		} catch (Exception e) {			
			_log.error("NUMERO ANTICIPO: "+((Anticipo)aux.getPago()).getAnticipo().getNroAnticipo());
			_log.error(e);			
		}
		return desde;
	}


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
		BigDecimal totalPagosAnticipoOriginal = BigDecimal.ZERO;
		int contAnti = 0;
		for (OrdenPago.FormaPago fp : getFormaPago()) {
			if (fp.getPago() instanceof Anticipo) {
				contAnti++;
				totalPagosAnticipo = totalPagosAnticipo.add(fp.getPago()
						.getImporte());
				totalPagosAnticipoOriginal = ((Anticipo) fp.getPago())
						.getImporteOriginal();

				anticipos.add((Anticipo) fp.getPago());
			} else {
				totalPagosNoAnticipo = totalPagosNoAnticipo.add(fp.getPago()
						.getImporte());
			}
		}

		/*
		 * if(contAnti>1){ throw new OrdenPagoUOMAPluriAnticipo(); }
		 */

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

       //Valida total del comprobante contra suma de conceptos del mismo
		
		for (Comprobante c	 : getComprobantes()) {
			BigDecimal totComp = BigDecimal.ZERO;
			for(ComprobanteConcepto cc:c.getConceptos()){
				totComp = totComp.add(cc.getImporte());
			}
			if(totComp.compareTo(c.getImporte())!=0){
				throw new Exception("Comprobante Erroneo " + c.toString() );
			}
		}
		
		// los pagos siempre deben ser >= que los comprobantes
		if (totalpagos.compareTo(totalComprobante) < 0) {
			throw new OrdenPagoOspimTotalPagosMenorQueComprobantesException();
		}
		if (entidad==WebKeysGlobal.UOMA && totalpagos.compareTo(totalComprobante) > 0) {
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
				if (entidad != WebKeysGlobal.UOMA) {
					BigDecimal tempAnticipos = BigDecimal.ZERO;
					int cantAnticiposUsados = 0;
					comprobantesList = new ArrayList<Comprobante>();
					for (Anticipo ant : anticipos) {
						cantAnticiposUsados++;
						tempAnticipos = tempAnticipos.add(ant.getImporte().abs());
						if (tempAnticipos.compareTo(totalComprobante) > 0
								&& cantAnticiposUsados != anticipos.size()) {
							throw new OrdenPagoOspimAnticiposNoUsadosException();
						}
						// ESTO ES PARA EL CASO EN QUE ADEMAS SE PAGUE CON OTRA
						// FP
						// QUE NO SEA ANTI
						if (totalpagos.compareTo(totalComprobante) >= 0) {
							tempAnticipos = totalComprobante
									.subtract(totalPagosNoAnticipo);
						}
						// ACA TENGO QUE OBTENER LOS BALANCEADORES...
						comprobantesList
								.add(obtenerAnticipoBalanceador(
										ant.getImporteOriginal().abs().subtract(
												tempAnticipos.abs()),
										anticipos.get(cantAnticiposUsados - 1),
										entidad));

					}
				} else {
					
					comprobantesList = new ArrayList<Comprobante>();
					// ACA TENGO QUE OBTENER LOS BALANCEADORES...
					comprobantesList
								.addAll(obtenerAnticipoBalanceadorList(anticipos,entidad));

					

				}

				getComprobantes().addAll(comprobantesList);

			}
		}
	}

	private List<Comprobante> obtenerAnticipoBalanceadorList(
			List<Anticipo> comprobantesAnticipos, int entidad)
			throws Exception {
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
			if (c.getImporteComprobante().abs().compareTo(ant.getImporteAnticipoBalanceador().abs()) <= 0) {
				anticipo.setImporteComprobante(BigDecimal.ZERO);
			} else {
				anticipo.setImporteComprobante(ant.getImporteAnticipoBalanceador());
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
		if (c.getImporteComprobante().compareTo(diferencia) <= 0) {
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

		ArrayList<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		for (ComprobanteConcepto cc : compOriginal.getConceptos()) {
			Concepto concepto = cc.getConceptoComprobante();
			conceptos.add(new ComprobanteConcepto(concepto, diferencia));
		}
		anticipo.setConceptos(conceptos);
	}

	public void setMostrarEnCuadro(boolean boolean1) {
		mostrarEnCuadro = boolean1;
	}

	public boolean isMostrarEnCuadro() {
		return mostrarEnCuadro;
	}

	public String getNumeroOP() {
		return String.valueOf(getId());
	}

	public List<? extends SubdiarioComprobante> getComprobantesSubdiario() {
		return getComprobantes();
	}

	public String getRazonSocial() {
		return getAcreedor().getRazon_soc();
	}

	public int getId_seccional() {
		return getSeccional().getIdSeccional();
	}

	// Para el reporte de subdiario de egresos
	public String getCuit() {
		return getAcreedor().getCuit();
	}

	public Date getFecha() {
		return getAlta_fecha();
	}
	
    private boolean debeMostrarCuentaPasivo(Date fecha) {
		
		String fStr = TraeListasServiceUtil.getSystemConfig("COMPROBANTES_FECHA_DEVENGADO_UOMA");
		String[] fechas= fStr.split("-");
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        try {
        	Calendar fechaC = Calendar.getInstance();
        	Calendar fechaD = Calendar.getInstance();
        	Calendar fechaH = Calendar.getInstance();
        	
    		fechaC.setTime(fecha);
			Date fechaDde = fmt.parse(fechas[0]);
			Date fechaHta = fmt.parse(fechas[1]);
			fechaD.setTime(fechaDde);
			fechaH.setTime(fechaHta);
			if(fechaD.compareTo(fechaC) <= 0
					&& fechaH.compareTo(fechaC) >= 0) {
				return true;
			}
		} catch (ParseException e) {
			
		}
		
		return false;
	}

}
