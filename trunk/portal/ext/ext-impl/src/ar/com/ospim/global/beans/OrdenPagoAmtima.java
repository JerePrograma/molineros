package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class OrdenPagoAmtima extends OrdenPago implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(OrdenPagoAmtima.class);
	public final static String CONCEPTO_FARMACIA = "CHEQUE PARA EL PAGO DEL SERVICIO DE MEDICAMENTOS AMBULATORIOS";

	public static OrdenPagoAmtima getMapping(ResultSet rs) throws SQLException {
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

	public static OrdenPagoAmtima getMapping(ResultSet rs, String prefix)
			throws SQLException {
		OrdenPagoAmtima op = new OrdenPagoAmtima();
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

	public static OrdenPagoAmtima getMappingRepo(ResultSet rs, String prefix)
			throws SQLException {
		OrdenPagoAmtima op = new OrdenPagoAmtima();
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
		op.setAFavorDe(rs.getString("a_nombre_de"));
		op.setAcreedor(new Empresa(cuitAcreedor, sucuAcreedor, null));
		op.setSeccional(new Seccional(seccional, null));

		return op;
	}

	public OrdenPagoAmtima() {
	}

	public OrdenPagoAmtima(int idOpOspim) {
		setId(idOpOspim);
	}

	/**
	 * Obtiene todos los comprobantes agrupados por concepeto para el subdiario
	 * de egreso
	 */
	private List<? extends SubdiarioEgresoColumna> getHaciaOriginal() {
		List<ColumnaConceptosSubdiario> cuentas = new ArrayList<ColumnaConceptosSubdiario>();
		try {
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
									pc.getNumero(), "", BigDecimal.ZERO, "",
									false, pc.getId());
							int indexOf = cuentas.indexOf(col);
							if (indexOf == -1) {
								cuentas.add(new ColumnaConceptosSubdiario(pc
										.getNumero(), pc.getCuenta(), importe,
										"", false, pc.getId()));
							} else {
								ColumnaConceptosSubdiario comprobanteConcepto = cuentas
										.get(indexOf);
								comprobanteConcepto
										.setImporte(comprobanteConcepto
												.getImporte().add(importe));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
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
		try {

			if (getBaja_fecha() == null) {
				return getDesdeOriginal();
			} else {
				return getHaciaOriginal();
			}
		} catch (Exception e) {
			// getId();
			e.printStackTrace();
			return null;
		}

	}

	// ESTE ES EL HABER
	public List<? extends SubdiarioEgresoColumna> getHacia() {
		// SI ES UNA ANULACION HAY QUE INVERTIR COLUMNAS
		try {
			if (getBaja_fecha() == null) {
				return getHaciaOriginal();
			} else {
				return getDesdeOriginal();
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Obtiene todas las formas de pago que no sean anticipos, y agrupa los
	 * anticipos segun su concepto para el subdiario de egreso
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
						
						/*
						List<ComprobanteConcepto> conceptos = ((Anticipo) fp
								.getPago()).getAnticipo().getConceptos();
                        */
						
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
		
		String fStr = TraeListasServiceUtil.getSystemConfig("COMPROBANTES_FECHA_DEVENGADO_AMTIMA");
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
