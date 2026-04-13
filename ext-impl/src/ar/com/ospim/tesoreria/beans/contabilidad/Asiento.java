package ar.com.ospim.tesoreria.beans.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.CuentaCorriente;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.FinanciacionTurismo;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Retencion;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroCuenta;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.ReporteConvenioBean;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel.ColumnaListadoDeuda;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel.ItemListadoDeuda;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class Asiento implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6575106373286131211L;

	private static Log logger = LogFactoryUtil.getLog(Asiento.class);

	public static final String EGRESOS_POR_REINTEGROS = "Egresos por reintegros del mes - Automático";
	public static final String EGRESOS_POR_PROVEEDORES = "Egresos por proveedores del mes - Automático";
	public static final String EGRESOS_POR_PRESTADORES = "Egresos por prestadores del mes - Automático";
	public static final String EGRESOS_POR_MOVIMIENTOS_BANCARIOS = "Egresos por movimientos bancarios del mes - Automático";
	public static final String INGRESOS_POR_MOVIMIENTOS_BANCARIOS = "Ingresos por movimientos bancarios del mes - Automático";
	public static final String INGRESOS_POR_RECIBOS = "Ingresos por recibos del mes - Automático";
	public static final String INGRESOS_AFIP = "Ingresos AFIP del mes - Automático";
	public static final String ACTAS_Y_CONVENIOS = "Actas y convenios del mes - Automático";
	public static final String COMPROBANTES_A_PAGAR = "Comprobantes a pagar del mes - Automático";
	public static final String BOLETAS_AMTIMA = "Ingresos por boletas AMTIMA - Automático";
	public static final String BOLETAS_UOMA = "Ingresos por boletas UOMA - Automático";
	public static final String BOLETAS_AMTIMA_DEVENGADO = "Devengado por boletas AMTIMA - Automático";
	public static final String BOLETAS_UOMA_DEVENGADO = "Devengado por boletas UOMA - Automático";
	public static final String COMPROBANTES_AMTIMA_DEVENGADO = "Devengado comprobantes AMTIMA - Automático";
	public static final String COMPROBANTES_UOMA_DEVENGADO = "Devengado comprobantes UOMA - Automático";

	private String descripcion;
	private Date fecha;
	private int nro;
	private int id;
	private boolean automatico;
	private List<Detalle> detalle;
	private Date ejercicioDesde;
	private Date ejercicioHasta;

	private List<Detalle> detalleBajasLogicas;
	private static DecimalFormat myFormatter;

	public Asiento() {
		myFormatter = new DecimalFormat("###,###,##0.00");
	}

	public Asiento(int id) {
		this.id = id;
	}

	public static Asiento buildAsientoFromIngresos(
			List<ItemSubdiarioIngreso> items, Date fecha, Date desde,
			Date hasta, String descripcion) {
		if (items == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());

		Map<Integer, BigDecimal> resumenDesde = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHacia = new HashMap<Integer, BigDecimal>();
		for (ItemSubdiarioIngreso col : items) {
			if (resumenDesde.get(col.getCuentaId()) != null) {
				resumenDesde.put(
						col.getCuentaId(),
						resumenDesde.get(col.getCuentaId()).add(
								col.getImporte()));
			} else {
				resumenDesde.put(col.getCuentaId(), col.getImporte());
			}

			if (resumenHacia.get(col.getCuentaIdFormaPago()) != null) {
				resumenHacia.put(
						col.getCuentaIdFormaPago(),
						resumenHacia.get(col.getCuentaIdFormaPago()).add(
								col.getImporte()));
			} else {
				resumenHacia.put(col.getCuentaIdFormaPago(), col.getImporte());
			}
		}

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenHacia, pase);
		pase = setearDetalleHaber(asiento, resumenDesde, pase);

		return asiento;
	}

	public static Asiento buildAsientoFromEgresos(
			List<? extends ItemSubdiarioEgreso> items, Date fecha, Date desde,
			Date hasta, String descripcion, int entidad) {
		if (items == null) {
			return null;
		}

		Asiento asiento = new Asiento();
		Map<Integer, BigDecimal> resumenDesde = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHacia = new HashMap<Integer, BigDecimal>();
		for (ItemSubdiarioEgreso item : items) {
			// armarDatos(item.getDesde(), resumenDesde, entidad);
			armarDatos(item.getHacia(), resumenHacia, entidad);
			armarDatos(item.getDesde(), item.getHacia(), resumenDesde,
					resumenHacia, entidad);
			// armarDatos(item.getHacia(), item.getDesde(), resumenHacia,
			// resumenDesde, entidad);
		}

		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenHacia, pase);
		pase = setearDetalleHaber(asiento, resumenDesde, pase);
		
		verificar(asiento);
		
		return asiento;

	}

	public static Asiento buildAsientoFromActas(
			List<ReporteActaBean> reporteActas,
			List<ReporteConvenioBean> reporteConvenios, Date fecha, Date desde,
			Date hasta, String descripcion, int entidad) {
		if (reporteActas == null && reporteConvenios == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());

		BigDecimal debe = BigDecimal.ZERO;
		BigDecimal debeConvenio = BigDecimal.ZERO;
		BigDecimal haberCapital = BigDecimal.ZERO;
		BigDecimal haberCapitalConvenio = BigDecimal.ZERO;
		BigDecimal haberInteres = BigDecimal.ZERO;
		BigDecimal haberInteresConvenio = BigDecimal.ZERO;

		BigDecimal haberCapitalSocialUsu = BigDecimal.ZERO;
		BigDecimal haberCapitalArt46 = BigDecimal.ZERO;
		BigDecimal haberCapitalSolidario = BigDecimal.ZERO;

		if (reporteActas != null) {
			for (ReporteActaBean repo : reporteActas) {
				debe = debe.add(repo.getCapital().add(repo.getInteres()));
				if (null != repo.getSocialUsufructo()
						&& repo.getSocialUsufructo().compareTo(BigDecimal.ZERO) != 0) {
					haberCapitalSocialUsu = haberCapitalSocialUsu.add(repo
							.getSocialUsufructo());
				}
				if (null != repo.getArt46()
						&& repo.getArt46().compareTo(BigDecimal.ZERO) != 0) {
					haberCapitalArt46 = haberCapitalArt46.add(repo.getArt46());
				}
				if (null != repo.getSolidario()
						&& repo.getSolidario().compareTo(BigDecimal.ZERO) != 0) {
					haberCapitalSolidario = haberCapitalSolidario.add(repo
							.getSolidario());
				}
				haberCapital = haberCapital.add(repo.getCapital());
				haberInteres = haberInteres.add(repo.getInteres());
				if (repo.getOtros() != null) {
					debe = debe.add(repo.getOtros());
					haberCapital = haberCapital.add(repo.getOtros());
				}
			}
		}

		if (reporteConvenios != null) {
			for (int j = 0; j < reporteConvenios.size(); j++) {
				ReporteConvenioBean repo = reporteConvenios.get(j);
				boolean mostrarDatosConvenio = true;
				if (j > 0
						&& reporteConvenios.get(j - 1).getNumero()
								.equals(repo.getNumero())) {
					mostrarDatosConvenio = false;
				}
				if (mostrarDatosConvenio) {
					if (entidad != WebKeysGlobal.UOMA) {
						debe = debe.add(repo.getInteres() != null ? repo
								.getInteres() : BigDecimal.ZERO);
					} else {
						debeConvenio = debeConvenio
								.add(repo.getInteres() != null ? repo
										.getInteres() : BigDecimal.ZERO);
					}
					haberInteres = haberInteres
							.add(repo.getInteres() != null ? repo.getInteres()
									: BigDecimal.ZERO);
					if (repo.getAjusteCapital() != null && entidad != WebKeysGlobal.UOMA) {
						debe = debe.add(repo.getAjusteCapital());
						haberCapital = haberCapital.add(repo.getAjusteCapital());
						debeConvenio = debeConvenio.add(repo.getAjusteCapital());
					}
                    if (repo.getAjusteCapital() != null && entidad == WebKeysGlobal.UOMA) {
						haberCapital = haberCapital.add(repo.getAjusteCapital());
						haberCapitalConvenio = haberCapitalConvenio.add(repo.getAjusteCapital()); //Agregado DS
						debeConvenio = debeConvenio.add(repo.getAjusteCapital());
					}
					if (repo.getAjusteInteres() != null && entidad != WebKeysGlobal.UOMA) {
						debe = debe.add(repo.getAjusteInteres());
//20181004				haberInteres.add(repo.getAjusteCapital() != null ? repo.getAjusteCapital() : BigDecimal.ZERO);
						haberInteres.add(repo.getAjusteInteres() != null ? repo.getAjusteInteres() : BigDecimal.ZERO);
					} else if (repo.getAjusteInteres() != null && entidad == WebKeysGlobal.UOMA) {
						debeConvenio = debe.add(repo.getAjusteInteres());
						haberInteres.add(repo.getAjusteInteres() != null ? repo.getAjusteInteres() : BigDecimal.ZERO);
					}
					if (repo.getAjusteInteres() != null && entidad == WebKeysGlobal.UOMA) {
						haberCapitalConvenio = haberCapitalConvenio.add(repo.getCapital());
						haberInteresConvenio = haberInteresConvenio.add(repo.getInteres());
					}
				}
			}
		}
		PlanCuentas cuentaDeudoresActas = ConceptoServiceUtil
				.getCuentaDeudoresActasYConvenios(desde, entidad);
		PlanCuentas cuentaInteresPorAportesYContrib = ConceptoServiceUtil
				.getCuentaInteresesPorAportesYContrib(desde, entidad);
		PlanCuentas cuentaActasConvenios = ConceptoServiceUtil
				.getCuentaActasYConvenios(desde, entidad);
		PlanCuentas cuentaSocialUsufructo = null;
		PlanCuentas cuentaArt46 = null;
		PlanCuentas cuentaSolidario = null;

		if (entidad == WebKeysGlobal.UOMA) {
			cuentaSocialUsufructo = ConceptoServiceUtil
					.getCuentaSocialUsufructo(desde, entidad);
			cuentaArt46 = ConceptoServiceUtil.getCuentaArt46(desde, entidad);
			cuentaSolidario = ConceptoServiceUtil.getCuentaSolidario(desde,
					entidad);
		}
		/*
		 * asiento.getDetalle().add( new Detalle(cuentaDeudoresActasYConvenios,
		 * "", debe, BigDecimal.ZERO, "", 1)); asiento.getDetalle().add( new
		 * Detalle(cuentaInteresPorAportesYContrib, "", BigDecimal.ZERO,
		 * haberInteres, "", 2)); asiento.getDetalle().add( new
		 * Detalle(cuentaActasYConvenios, "", BigDecimal.ZERO, haberCapital, "",
		 * 3));
		 */
		BigDecimal haberConvenio = haberCapitalConvenio
				.add(haberInteresConvenio);
		if (entidad != WebKeysGlobal.UOMA) {
			int cont = 1;
			//asiento.getDetalle().add(new Detalle(cuentaDeudoresActas, "", BigDecimal.ZERO, debe,"", cont++));
			// DEBE
			asiento.getDetalle().add(new Detalle(cuentaDeudoresActas, "", debe, BigDecimal.ZERO,
										"", cont++));
			asiento.getDetalle().add(
					new Detalle(cuentaInteresPorAportesYContrib, "",
							 BigDecimal.ZERO,haberInteres, "", cont++));
			asiento.getDetalle().add(
					new Detalle(cuentaActasConvenios, "", BigDecimal.ZERO, haberCapital, "", cont++));
		} else {
			int cont = 1;
			// DEBE
			asiento.getDetalle().add(
					new Detalle(cuentaDeudoresActas, "", debe, BigDecimal.ZERO,
							"", cont++));
			if (haberConvenio.compareTo(BigDecimal.ZERO) > 0) {
				asiento.getDetalle().add(
						new Detalle(cuentaActasConvenios, "", haberConvenio,
								BigDecimal.ZERO, "", cont++));
			}
			// HABER
			asiento.getDetalle().add(
					new Detalle(cuentaInteresPorAportesYContrib, "",
							BigDecimal.ZERO, haberInteres, "", cont++));
			asiento.getDetalle().add(
					new Detalle(cuentaSocialUsufructo, "", BigDecimal.ZERO,
							haberCapitalSocialUsu, "", cont++));
			asiento.getDetalle().add(
					new Detalle(cuentaArt46, "", BigDecimal.ZERO,
							haberCapitalArt46, "", cont++));
			asiento.getDetalle().add(
					new Detalle(cuentaSolidario, "", BigDecimal.ZERO,
							haberCapitalSolidario, "", cont++));
			if (haberConvenio.compareTo(BigDecimal.ZERO) > 0) {
				asiento.getDetalle().add(
						new Detalle(cuentaDeudoresActas, "", BigDecimal.ZERO,
								haberCapitalConvenio, "", cont++));
			}

		}
		return asiento;
	}

	public static Asiento builAsientoFromListadoDeDeudas(
			List<ItemListadoDeuda> listado, Date fecha, Date desde, Date hasta,
			String descripcion) {

		if (listado == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());

		Map<Integer, BigDecimal> resumenDesde = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHacia = new HashMap<Integer, BigDecimal>();
		for (ItemListadoDeuda item : listado) {
			armarDatosDeuda(item.getDesdeAgrupado(), resumenDesde);
			armarDatosDeuda(item.getHastaAgrupado(), resumenHacia);
		}

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDesde, pase);
		pase = setearDetalleHaber(asiento, resumenHacia, pase);
		return asiento;
	}

	private static void armarDatosDeuda(List<ColumnaListadoDeuda> repo,
			Map<Integer, BigDecimal> resumen) {
		if (repo != null) {
			for (ColumnaListadoDeuda col : repo) {
				if (resumen.get(col.getCuentaId()) != null) {
					resumen.put(col.getCuentaId(),
							resumen.get(col.getCuentaId())
									.add(col.getImporte()));
				} else {
					resumen.put(col.getCuentaId(), col.getImporte());
				}
			}
		}
	}

	private static int setearDetalleHaber(Asiento asiento,
			Map<Integer, BigDecimal> resumenHacia, int pase) {
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(resumenHacia.keySet());
		Collections.sort(keys);
		for (Integer cuenta : keys) {
			Detalle detalle = new Detalle();
			BigDecimal importe = resumenHacia.get(cuenta);
			detalle.setCuenta(new PlanCuentas(cuenta));
			detalle.setComprobante("");
			detalle.setHaber(importe);
			detalle.setDebe(BigDecimal.ZERO);
			detalle.setPase(pase);
			asiento.getDetalle().add(detalle);
			pase++;
		}
		return pase;
	}

	private static int setearDetalleDebe(Asiento asiento,
			Map<Integer, BigDecimal> resumenDesde, int pase) {
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(resumenDesde.keySet());
		Collections.sort(keys);
		for (Integer cuenta : keys) {
			Detalle detalle = new Detalle();
			BigDecimal importe = resumenDesde.get(cuenta);
			detalle.setCuenta(new PlanCuentas(cuenta));
			detalle.setComprobante("");
			detalle.setDebe(importe);
			detalle.setHaber(BigDecimal.ZERO);
			detalle.setPase(pase);
			asiento.getDetalle().add(detalle);
			pase++;
		}
		return pase;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getFechaString() {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(fecha);
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setFechaString(String fecha) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		this.fecha = format.parse(fecha);
	}

	public int getNro() {
		return nro;
	}

	public void setNro(int nro) {
		this.nro = nro;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAutomatico() {
		return automatico;
	}

	public void setAutomatico(boolean automatico) {
		this.automatico = automatico;
	}

	public static Asiento getMapping(ResultSet rs) throws SQLException {
		Asiento asiento = getMapping(rs, "");
		return asiento;
	}

	private static Asiento getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Asiento asiento = new Asiento();
		asiento.setFecha(rs.getDate(prefix + "fecha"));
		asiento.setDescripcion(rs.getString(prefix + "descripcion"));
		asiento.setId(rs.getInt(prefix + "id"));
		asiento.setAutomatico(rs.getBoolean(prefix + "automatico"));
		asiento.setNro(rs.getInt(prefix + "numero"));
		asiento.setEjercicioDesde(rs.getDate(prefix + "ejercicio_desde"));
		asiento.setEjercicioHasta(rs.getDate(prefix + "ejercicio_hasta"));
		return asiento;
	}

	public List<Detalle> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<Detalle> detalle) {
		this.detalle = detalle;
	}

	public Date getEjercicioDesde() {
		return ejercicioDesde;
	}

	public void setEjercicioDesde(Date ejercicioDesde) {
		this.ejercicioDesde = ejercicioDesde;
	}

	public Date getEjercicioHasta() {
		return ejercicioHasta;
	}

	public void setEjercicioHasta(Date ejercicioHasta) {
		this.ejercicioHasta = ejercicioHasta;
	}

	public String getEjercicioDesdeString() {
		if (ejercicioDesde == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(ejercicioDesde);
	}

	public void setEjercicioDesdeString(String ejercicioDesde)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		this.ejercicioDesde = format.parse(ejercicioDesde);
	}

	public String getEjercicioHastaString() {
		if (ejercicioHasta == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(ejercicioHasta);
	}

	public void setEjercicioHastaString(String ejercicioHasta)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		this.ejercicioHasta = format.parse(ejercicioHasta);
	}

	public List<Detalle> getDetalleBajasLogicas() {
		return detalleBajasLogicas;
	}

	public void setDetalleBajasLogicas(List<Detalle> detalleBajasLogicas) {
		this.detalleBajasLogicas = detalleBajasLogicas;
	}

	private static void armarDatos(
			List<? extends SubdiarioEgresoColumna> repoDebe,
			List<? extends SubdiarioEgresoColumna> repoHaber,
			Map<Integer, BigDecimal> resumenDebe,
			Map<Integer, BigDecimal> resumenHaber, int entidad) {
		if (repoDebe != null) {
			/*
			 * for (SubdiarioEgresoColumna colHaber : repoHaber) {
			 * if(colHaber.getImporte().compareTo(BigDecimal.ZERO) < 0){ if
			 * (resumenHaber.get(colHaber.getCuentaId(entidad)) != null) {
			 * resumenDebe.put( colHaber.getCuentaId(entidad),
			 * resumenHaber.get(colHaber.getCuentaId(entidad)).add(
			 * colHaber.getImporte().negate())); } else {
			 * resumenDebe.put(colHaber.getCuentaId(entidad),
			 * colHaber.getImporte().negate()); } }
			 * 
			 * }
			 */
			for (SubdiarioEgresoColumna col : repoDebe) {
				try {
					if (col.getCuentaId(entidad) == 0
							&& col.getImporte().compareTo(BigDecimal.ZERO) == 0) {
						// es concepto de una NDB: AJUSTE (que no tiene cuenta
						// asociada)
						continue;
						// no puede haber asientos negativos, tiene que ir del
						// otro lado sumando
					}
					if (resumenDebe.get(col.getCuentaId(entidad)) != null) {
						resumenDebe.put(
								col.getCuentaId(entidad),
								resumenDebe.get(col.getCuentaId(entidad)).add(
										col.getImporte()));
					} else {
						resumenDebe.put(col.getCuentaId(entidad),
								col.getImporte());
					}
				} catch (Exception e) {
					logger.debug("EN ARMAR DATOS CMOPLEJO EXCEPTION: "
							+ col.getImporte());
					return;
				}
			}
		}
	}

	private static void armarDatos(List<? extends SubdiarioEgresoColumna> repo,
			Map<Integer, BigDecimal> resumen, int entidad) {
		if (repo != null) {
			for (SubdiarioEgresoColumna col : repo) {
				try {
					if (col.getCuentaId(entidad) == 0
							&& col.getImporte().compareTo(BigDecimal.ZERO) == 0) {
						// es concepto de una NDB: AJUSTE (que no tiene cuenta
						// asociada)
						continue;
						// no puede haber asientos negativos, tiene que ir del
						// otro lado sumando
					}/*
					 * else if(col.getImporte().compareTo(BigDecimal.ZERO) < 0){
					 * logger.debug("en armar datos: -"+col.getImporte());
					 * continue; }
					 */
					if (resumen.get(col.getCuentaId(entidad)) != null) {
						resumen.put(
								col.getCuentaId(entidad),
								resumen.get(col.getCuentaId(entidad)).add(
										col.getImporte()));
					} else {
						resumen.put(col.getCuentaId(entidad), col.getImporte());
					}
				} catch (Exception e) {
					logger.debug("en armar datos EXCEPTION: "
							+ col.getImporte());
					return;
				}
			}
		}
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
		Asiento other = (Asiento) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static class Detalle {
		private int id;
		private PlanCuentas cuenta;
		private String comprobante;
		private BigDecimal debe;
		private BigDecimal haber;
		private String observaciones;
		private int pase;

		public Detalle(PlanCuentas cuenta, String comprobante, BigDecimal debe,
				BigDecimal haber, String observaciones, int pase) {
			super();
			this.cuenta = cuenta;
			this.comprobante = comprobante;
			this.debe = debe;
			this.haber = haber;
			this.observaciones = observaciones;
			this.pase = pase;
		}

		public Detalle() {
		}

		public Detalle(int id) {
			this.id = id;
		}

		public PlanCuentas getCuenta() {
			return cuenta;
		}

		public void setCuenta(PlanCuentas cuenta) {
			this.cuenta = cuenta;
		}

		public String getComprobante() {
			return comprobante;
		}

		public void setComprobante(String comprobante) {
			this.comprobante = comprobante;
		}

		public BigDecimal getDebe() {
			return debe;
		}

		public String getDebeAsString() {
			return debe != null ? myFormatter.format(debe) : null;
		}

		public void setDebe(BigDecimal debe) {
			this.debe = debe;
		}

		public BigDecimal getHaber() {
			return haber;
		}

		public String getHaberAsString() {
			return haber != null ? myFormatter.format(haber) : null;
		}

		public void setHaber(BigDecimal haber) {
			this.haber = haber;
		}

		public String getObservaciones() {
			return observaciones;
		}

		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}

		public static Detalle getMapping(ResultSet rs) throws SQLException {
			Detalle detalle = getMapping(rs, "");
			return detalle;
		}

		private static Detalle getMapping(ResultSet rs, String prefix)
				throws SQLException {
			Detalle detalle = new Detalle();
			detalle.setComprobante(rs.getString(prefix + "comprobante"));
			detalle.setDebe(rs.getBigDecimal(prefix + "debe"));
			detalle.setHaber(rs.getBigDecimal(prefix + "haber"));
			detalle.setObservaciones(rs.getString(prefix + "observaciones"));
			detalle.setPase(rs.getInt("pase"));
			detalle.setCuenta(new PlanCuentas(rs.getInt(prefix
					+ "id_plan_cuentas")));
			detalle.getCuenta().setNumero(
					rs.getString(prefix + "plan_cuentas_numero"));
			detalle.getCuenta().setCuenta(
					rs.getString(prefix + "plan_cuentas_cuenta"));
			detalle.setId(rs.getInt(prefix + "id"));
			return detalle;
		}

		public void setPase(int pase) {
			this.pase = pase;
		}

		public int getPase() {
			return pase;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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
			Detalle other = (Detalle) obj;
			if (id != other.id)
				return false;
			return true;
		}
	}

	public BigDecimal getTotalDebe() {
		BigDecimal ret = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				ret = ret.add(det.getDebe());
			}
		}
		return ret;
	}

	public String getTotalDebeAsString() {
		BigDecimal ret = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				ret = ret.add(det.getDebe());
			}
		}
		return myFormatter.format(ret);
	}

	public String getTotalHaberAsString() {
		BigDecimal ret = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				ret = ret.add(det.getHaber());
			}
		}
		return myFormatter.format(ret);
	}

	public BigDecimal getTotalHaber() {
		BigDecimal ret = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				ret = ret.add(det.getHaber());
			}
		}
		return ret;
	}

	public String getAnioEjercicioHastaString() {
		if (ejercicioHasta == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(ejercicioHasta);
	}

	public String getAnioEjercicioDesdeString() {
		if (ejercicioDesde == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(ejercicioDesde);
	}
	
	public static Asiento buildAsientoFromDevengadoBoletas(
			List<FichaBoletaPortal> items, Date fecha, Date desde,
			Date hasta, String descripcion,Integer entidad) {
		if (items == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());

		Map<Integer, BigDecimal> resumenDebe = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHaber = new HashMap<Integer, BigDecimal>();
		BigDecimal interes =BigDecimal.ZERO;
		BigDecimal importe=BigDecimal.ZERO;
		for (FichaBoletaPortal col : items) {
			if (resumenDebe.get(col.getCuentaDevengado().getId()) != null) {
/*				
				resumenDebe.put(						
						col.getCuentaDevengado().getId(),
						resumenDebe.get(col.getCuentaDevengado().getId()).add(
								col.getCapital().add(col.getInteres().add(col.getAjusteCapital()))));
*/				
				
				resumenDebe.put(						
						col.getCuentaDevengado().getId(),
						resumenDebe.get(col.getCuentaDevengado().getId()).add(
								col.getCapital().add(col.getAjusteCapital())));
				
			} else {
//				resumenDebe.put(col.getCuentaDevengado().getId(), col.getCapital().add(col.getInteres().add(col.getAjusteCapital())) );
				
				resumenDebe.put(col.getCuentaDevengado().getId(), col.getCapital().add(col.getAjusteCapital()) );
			}
			
			if (resumenHaber.get(col.getCuenta().getId()) != null) {
				resumenHaber.put(
						col.getCuenta().getId(),
						resumenHaber.get(col.getCuenta().getId()).add(
								col.getCapital().add(col.getAjusteCapital())));
			} else {
				resumenHaber.put(col.getCuenta().getId(), col.getCapital().add(col.getAjusteCapital()) );
			}
// Calculaba Interes			
//			interes=interes.add(col.getInteres());
			
		}
		
		Integer cuentaInteresAportes=0;
		if(entidad==WebKeysGlobal.AMTIMA) {
			ParametroCuenta deudores=ConceptoServiceUtil.getParametroCuenta("CUENTA_INTERES_APORTES_AMTIMA", fecha,entidad);
			cuentaInteresAportes=deudores.getPlanCuentas().getId();
//			cuentaInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CUENTA_INTERES_APORTES_AMTIMA"));
		}else {
			ParametroCuenta deudores=ConceptoServiceUtil.getParametroCuenta("CUENTA_INTERES_APORTES_UOMA", fecha,entidad);
			cuentaInteresAportes=deudores.getPlanCuentas().getId();
//			cuentaInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CUENTA_INTERES_APORTES_UOMA"));
		}
		
	
		
		if(interes.compareTo(BigDecimal.ZERO)>0 ) {
			resumenHaber.put(cuentaInteresAportes, interes);
		}

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDebe, pase);
		pase = setearDetalleHaber(asiento, resumenHaber, pase);

		return asiento;
	}


	public static Asiento buildAsientoFromDevengadoComprobantes(
			List<Comprobante> items, Date fecha, Date desde,
			Date hasta, String descripcion,Integer entidad) {
		if (items == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());
/*
		Integer cuentaDevengado=0;
		if(entidad==WebKeysGlobal.AMTIMA) {
			cuentaDevengado=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CUENTA_DEVENGADO_COMPROBANTES_AMTIMA"));
		}else {
			cuentaDevengado=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CUENTA_DEVENGADO_COMPROBANTES_UOMA"));
		}
*/		
		
		Map<Integer, BigDecimal> resumenDebe = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHaber = new HashMap<Integer, BigDecimal>();
		BigDecimal importe=BigDecimal.ZERO;
		for(Comprobante c:items) {
		  for (ComprobanteConcepto col : c.getConceptos()) {
			
			if(col.getBaja_fecha()==null && !"NCR".equalsIgnoreCase( c.getTipoComprobante())
					&& !"ABA".equalsIgnoreCase( c.getTipoComprobante())) {
				if (resumenDebe.get(col.getConceptoComprobante().getPlanCuentas().getId()) != null) {
					resumenDebe.put(						
							col.getConceptoComprobante().getPlanCuentas().getId(),
							resumenDebe.get(col.getConceptoComprobante().getPlanCuentas().getId()).add(
									col.getImporte()));
				} else {
					resumenDebe.put(col.getConceptoComprobante().getPlanCuentas().getId(), col.getImporte() );
				}
				
				if (resumenHaber.get(col.getConceptoComprobante().getPlanCuentasPasivo().getId()) != null) {
					resumenHaber.put(
							col.getConceptoComprobante().getPlanCuentasPasivo().getId(),
							resumenHaber.get(col.getConceptoComprobante().getPlanCuentasPasivo().getId()).add(
									col.getImporte()));
				} else {
					resumenHaber.put(col.getConceptoComprobante().getPlanCuentasPasivo().getId(), col.getImporte() );
				}
				
			}else {
				
				if (resumenHaber.get(col.getConceptoComprobante().getPlanCuentas().getId()) != null) {
					resumenHaber.put(						
							col.getConceptoComprobante().getPlanCuentas().getId(),
							resumenHaber.get(col.getConceptoComprobante().getPlanCuentas().getId()).add(
									col.getImporte()));
				} else {
					resumenHaber.put(col.getConceptoComprobante().getPlanCuentas().getId(), col.getImporte() );
				}
				
				if (resumenDebe.get(col.getConceptoComprobante().getPlanCuentasPasivo().getId()) != null) {
					resumenDebe.put(
							col.getConceptoComprobante().getPlanCuentasPasivo().getId(),
							resumenDebe.get(col.getConceptoComprobante().getPlanCuentasPasivo().getId()).add(
									col.getImporte()));
				} else {
					resumenDebe.put(col.getConceptoComprobante().getPlanCuentasPasivo().getId(), col.getImporte() );
				}
	        }
			
		   }
	    }

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDebe, pase);
		pase = setearDetalleHaber(asiento, resumenHaber, pase);

		return asiento;
	}


	public static Asiento buildAsientoFromDevengadoFacturasVentas(
			List<Factura> items, Date fecha, Date desde,
			Date hasta, String descripcion,Integer entidad) {
		if (items == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());
		Integer cuentaDebeDeudora=0;
		Integer cuentaHaberVentas=0;
		Integer cuentaHaberIva=0;
		Integer cuentaHaberPercepcion=0;
		
		ParametroCuenta deudores=null;
		ParametroCuenta debitoIva=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_DEBITO_FISCAL_IVA", fecha,entidad);
		ParametroCuenta percepIIBB=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_PERCEPCION_IIBB_ARBA", fecha,entidad);
		ParametroCuenta ventas=null;
		
		
		cuentaHaberIva=debitoIva.getPlanCuentas().getId();
	    cuentaHaberPercepcion=percepIIBB.getPlanCuentas().getId();
		
		Map<Integer, BigDecimal> resumenDebe = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHaber = new HashMap<Integer, BigDecimal>();
		BigDecimal importe=BigDecimal.ZERO;
		for(Factura c:items) {
			
			deudores=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_DEUDORES_POR_VENTAS_"+c.getSucursal(), fecha,entidad);
			ventas=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_VENTAS_"+c.getSucursal(), fecha,entidad);
			cuentaDebeDeudora=deudores.getPlanCuentas().getId();
			cuentaHaberVentas=ventas.getPlanCuentas().getId();
			
			BigDecimal totcpbte=BigDecimal.ZERO;
			totcpbte=c.getImporteTotalCalculado();
			totcpbte=totcpbte.subtract(c.getIva());
			totcpbte=totcpbte.subtract(c.getPercepcion());
			totcpbte=totcpbte.add(c.getIvaReintegro().abs());
			
			if( !"NCR".equalsIgnoreCase( c.getTipo()) &&
					!"NCE".equalsIgnoreCase( c.getTipo())	) {
				if (resumenDebe.get(cuentaDebeDeudora) != null) {
					resumenDebe.put(cuentaDebeDeudora,resumenDebe.get(cuentaDebeDeudora).add(
							c.getImporteTotalCalculado()    ));
				} else {
					resumenDebe.put(cuentaDebeDeudora, c.getImporteTotalCalculado() );
				}
				if(c.getIva().compareTo(BigDecimal.ZERO)>0) {
					if (resumenHaber.get(cuentaHaberIva) != null) {
						resumenHaber.put(
								cuentaHaberIva,
								resumenHaber.get(cuentaHaberIva).add(c.getIva()));
					} else {
						resumenHaber.put(cuentaHaberIva, c.getIva() );
					}
				}
				
				if(c.getPercepcion().compareTo(BigDecimal.ZERO)>0) {
					if (resumenHaber.get(cuentaHaberPercepcion) != null) {
						resumenHaber.put(
								cuentaHaberPercepcion,
								resumenHaber.get(cuentaHaberPercepcion).add(c.getPercepcion()));
					} else {
						resumenHaber.put(cuentaHaberPercepcion, c.getPercepcion() );
					}
				}
				
				
				if (resumenHaber.get(cuentaHaberVentas) != null) {
					resumenHaber.put(
							cuentaHaberVentas,
							resumenHaber.get(cuentaHaberVentas).add(totcpbte));
				} else {
					resumenHaber.put(cuentaHaberVentas, totcpbte);
				}
				
				if(c.getIvaReintegro().abs().compareTo(BigDecimal.ZERO)>0) {
					if (resumenDebe.get(cuentaHaberIva) != null) {
						resumenDebe.put(
								cuentaHaberIva,
								resumenDebe.get(cuentaHaberIva).add( c.getIvaReintegro().abs()));
					} else {
						resumenDebe.put(cuentaHaberIva, c.getIvaReintegro().abs() );
					}
				}
				
			}else {
				if (resumenHaber.get(cuentaDebeDeudora) != null) {
					resumenHaber.put(cuentaDebeDeudora,resumenHaber.get(cuentaDebeDeudora).add(
							c.getImporteTotalCalculado() ));
				} else {
					
					resumenHaber.put(cuentaDebeDeudora,c.getImporteTotalCalculado() );
				}
				if(c.getIva().compareTo(BigDecimal.ZERO)>0) {
					if (resumenDebe.get(cuentaHaberIva) != null) {
						resumenDebe.put(
								cuentaHaberIva,
								resumenDebe.get(cuentaHaberIva).add(c.getIva()));
					} else {
						resumenDebe.put(cuentaHaberIva, c.getIva() );
					}
				}
				
				if(c.getIvaReintegro().abs().compareTo(BigDecimal.ZERO)>0) {
					if (resumenHaber.get(cuentaHaberIva) != null) {
						resumenHaber.put(
								cuentaHaberIva,
								resumenHaber.get(cuentaHaberIva).add( c.getIvaReintegro().abs()));
					} else {
						resumenHaber.put(cuentaHaberIva, c.getIvaReintegro().abs() );
					}
				}
				
				if(c.getPercepcion().compareTo(BigDecimal.ZERO)>0) {
					if (resumenDebe.get(cuentaHaberPercepcion) != null) {
						resumenDebe.put(
								cuentaHaberPercepcion,
								resumenDebe.get(cuentaHaberPercepcion).add(c.getPercepcion()));
					} else {
						resumenDebe.put(cuentaHaberPercepcion, c.getPercepcion() );
					}
				}
				
				if (resumenDebe.get(cuentaHaberVentas) != null) {
					resumenDebe.put(
							cuentaHaberVentas,
							resumenDebe.get(cuentaHaberVentas).add(totcpbte));
					
				} else {
					resumenDebe.put(cuentaHaberVentas,totcpbte);
				}
	        }
	    }

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDebe, pase);
		pase = setearDetalleHaber(asiento, resumenHaber, pase);

		return asiento;
	}

	public static Asiento buildAsientoFromCobranzasFacturasVentas(
			List<FacturaIngreso> items, Date fecha, Date desde,
			Date hasta, String descripcion,Integer entidad) {
		if (items == null) {
			return null;
		}
		
		Map<String,Integer>mp= new HashMap<String,Integer>();
		Asiento asiento = new Asiento();
		asiento.setFecha(fecha);
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(new ArrayList<Detalle>());
		Integer cuentaHaberDeudora=0;
		ParametroCuenta deudores=null;
		
		
		Integer cuentaDebeEfectivo=0;
		Integer cuentaDebeCheque=0;
		Integer cuentaDebeTarjeta=0;
		Integer cuentaDebePagare=0;
		Integer cuentaDebeDeposito=0;
		Integer cuentaDebeTurismo=0;
		
		Integer cuentaDebe=0;
		
		Map<Integer, BigDecimal> resumenDebe = new HashMap<Integer, BigDecimal>();
		Map<Integer, BigDecimal> resumenHaber = new HashMap<Integer, BigDecimal>();
		BigDecimal importess=BigDecimal.ZERO;
		for(FacturaIngreso c:items) {
			 deudores=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_DEUDORES_POR_VENTAS_"+c.getFactura().getSucursal(), fecha,entidad);
			 cuentaHaberDeudora=deudores.getPlanCuentas().getId();
			if(c.getIngreso() instanceof Cheque) {
				cuentaDebeCheque = mp.get("CUENTA_FACTURACION_CHEQUE_"+c.getFactura().getSucursal());
				if(cuentaDebeCheque==null || cuentaDebeCheque==0) {
					ParametroCuenta cheque=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_CHEQUE_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_CHEQUE_"+c.getFactura().getSucursal(), cheque.getPlanCuentas().getId());
					cuentaDebeCheque = mp.get("CUENTA_FACTURACION_CHEQUE_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebeCheque;
			}else if(c.getIngreso() instanceof Pagare) {
				cuentaDebePagare = mp.get("CUENTA_FACTURACION_PAGARE_"+c.getFactura().getSucursal());
				if(cuentaDebePagare==null || cuentaDebePagare==0) {
					ParametroCuenta pagare=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_PAGARE_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_PAGARE_"+c.getFactura().getSucursal(), pagare.getPlanCuentas().getId());
					cuentaDebePagare = mp.get("CUENTA_FACTURACION_PAGARE_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebePagare;
			}else if(c.getIngreso() instanceof DepositoBancario) {
				cuentaDebeDeposito=0;
				CuentaBancaria cb =TraeListasServiceUtil.getCtasBcriasById(c.getIngreso().getCuentaBancaria().getId_cuenta_bcria());
				if(cb!=null && cb.getCuentaAsociada()!=null && cb.getCuentaAsociada().getId()>0) {
				  cuentaDebeDeposito=cb.getCuentaAsociada().getId();	
				}
				if(cuentaDebeDeposito==0) {
				  cuentaDebeDeposito = mp.get("CUENTA_FACTURACION_DEPOSITO_"+c.getFactura().getSucursal());
				  if(cuentaDebeDeposito==null || cuentaDebeDeposito==0) {
					ParametroCuenta deposito=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_DEPOSITO_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_DEPOSITO_"+c.getFactura().getSucursal(), deposito.getPlanCuentas().getId());
					cuentaDebeDeposito = mp.get("CUENTA_FACTURACION_DEPOSITO_"+c.getFactura().getSucursal());
				  }
				}  
				cuentaDebe=cuentaDebeDeposito;
			}else if(c.getIngreso() instanceof TarjetaDebitoCredito) {
				cuentaDebeTarjeta = mp.get("CUENTA_FACTURACION_TARJETA_"+c.getFactura().getSucursal());
				if(cuentaDebeTarjeta==null || cuentaDebeTarjeta==0) {
					ParametroCuenta tarjeta=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_TARJETA_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_TARJETA_"+c.getFactura().getSucursal(), tarjeta.getPlanCuentas().getId());
					cuentaDebeTarjeta = mp.get("CUENTA_FACTURACION_TARJETA_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebeTarjeta;
			}else if(c.getIngreso() instanceof FinanciacionTurismo) {
				cuentaDebeTurismo = mp.get("CUENTA_FACTURACION_FINANCIACION_TURISMO_"+c.getFactura().getSucursal());
				if(cuentaDebeTurismo==null || cuentaDebeTurismo==0) {
					ParametroCuenta turismo=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_FINANCIACION_TURISMO_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_FINANCIACION_TURISMO_"+c.getFactura().getSucursal(), turismo.getPlanCuentas().getId());
					cuentaDebeTurismo = mp.get("CUENTA_FACTURACION_FINANCIACION_TURISMO_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebeTurismo;	
			}else if(c.getIngreso() instanceof CuentaCorriente) {
				cuentaDebeTurismo = mp.get("CUENTA_FACTURACION_CUENTA_CORRIENTE_"+c.getFactura().getSucursal());
				if(cuentaDebeTurismo==null || cuentaDebeTurismo==0) {
					ParametroCuenta turismo=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_CUENTA_CORRIENTE_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_CUENTA_CORRIENTE_"+c.getFactura().getSucursal(), turismo.getPlanCuentas().getId());
					cuentaDebeTurismo = mp.get("CUENTA_FACTURACION_CUENTA_CORRIENTE_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebeTurismo;	
			}else if(c.getIngreso() instanceof Retencion) {
				cuentaDebePagare = mp.get("CUENTA_FACTURACION_RET_GRAL_"+c.getFactura().getSucursal());
				if(cuentaDebePagare==null || cuentaDebePagare==0) {
					ParametroCuenta pagare=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_RET_GRAL_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_RET_GRAL_"+c.getFactura().getSucursal(), pagare.getPlanCuentas().getId());
					cuentaDebePagare = mp.get("CUENTA_FACTURACION_RET_GRAL_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebePagare;
			}else {
				cuentaDebeEfectivo = mp.get("CUENTA_FACTURACION_EFECTIVO_"+c.getFactura().getSucursal());
				if(cuentaDebeEfectivo==null || cuentaDebeEfectivo==0) {
					ParametroCuenta efectivo=ConceptoServiceUtil.getParametroCuenta("CUENTA_FACTURACION_EFECTIVO_"+c.getFactura().getSucursal(), c.getIngreso().getFecha(),entidad);
					mp.put("CUENTA_FACTURACION_EFECTIVO_"+c.getFactura().getSucursal(), efectivo.getPlanCuentas().getId());
					cuentaDebeEfectivo = mp.get("CUENTA_FACTURACION_EFECTIVO_"+c.getFactura().getSucursal());
				}
				cuentaDebe=cuentaDebeEfectivo;
			}
			
			if( !"NCR".equalsIgnoreCase( c.getFactura().getTipo()) 
				&& !"NCE".equalsIgnoreCase( c.getFactura().getTipo() )	) {
			
			  if (resumenDebe.get(cuentaDebe) != null) {
				resumenDebe.put(cuentaDebe,resumenDebe.get(cuentaDebe).add(
						c.getIngreso().getImporte()   ));
			  } else {
				resumenDebe.put(cuentaDebe, c.getIngreso().getImporte());
			  }
			
			  if (resumenHaber.get(cuentaHaberDeudora) != null) {
				resumenHaber.put(
						cuentaHaberDeudora,
						resumenHaber.get(cuentaHaberDeudora).add(
								c.getIngreso().getImporte()  ));
			  } else {
				resumenHaber.put(cuentaHaberDeudora, c.getIngreso().getImporte()  );
		 	  }
			}else {
				if (resumenHaber.get(cuentaDebe) != null) {
					resumenHaber.put(cuentaDebe,resumenHaber.get(cuentaDebe).add(
							c.getIngreso().getImporte()   ));
				} else {
					resumenHaber.put(cuentaDebe, c.getIngreso().getImporte());
				}
				
				if (resumenDebe.get(cuentaHaberDeudora) != null) {
					resumenDebe.put(
							cuentaHaberDeudora,
							resumenDebe.get(cuentaHaberDeudora).add(
									c.getIngreso().getImporte()  ));
				} else {
					resumenDebe.put(cuentaHaberDeudora, c.getIngreso().getImporte()  );
			 	}	
			}
			
	    }

		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDebe, pase);
		pase = setearDetalleHaber(asiento, resumenHaber, pase);

		return asiento;
	}

	
	private static void verificar(Asiento asiento) {
		BigDecimal deb = BigDecimal.ZERO;
		BigDecimal hab = BigDecimal.ZERO;
		for (Detalle det : asiento.getDetalle()) {
				deb = deb.add(det.getDebe().setScale(2, RoundingMode.HALF_DOWN) );
				hab = hab.add(det.getHaber().setScale(2, RoundingMode.HALF_DOWN) );
				
				det.setDebe( det.getDebe().setScale(2, RoundingMode.HALF_DOWN));
				det.setHaber(det.getHaber().setScale(2, RoundingMode.HALF_DOWN));
				
		}
		Double dif =deb.doubleValue()-hab.doubleValue();
		if(Math.abs(dif)>0 && Math.abs(dif)<=1) {
		   if(dif>0) {
			   for(Detalle d:asiento.getDetalle()) {
				   if(d.getHaber().compareTo(BigDecimal.ZERO)>0) {
					   d.setHaber(d.getHaber().add(new BigDecimal(Math.abs(dif))));
					   break;
				   }
			   }
		   }else {
			   for(Detalle d:asiento.getDetalle()) {
				   if(d.getDebe().compareTo(BigDecimal.ZERO)>0) {
					   d.setDebe(d.getDebe().add(new BigDecimal(Math.abs(dif))));
					   break;
				   }
			   }
		   }
		}
	}
	
	
	public static Asiento buildAsientoFromJudiciales(
			Asiento  asientoJudicial, Date desde, Date hasta, 
			String descripcion,String entidad) {
		if (asientoJudicial == null) {
			return null;
		}
		Asiento asiento = new Asiento();
		asiento.setFecha(asientoJudicial.getFecha());
		asiento.setDescripcion(descripcion);
		asiento.setEjercicioDesde(desde);
		asiento.setEjercicioHasta(hasta);
		asiento.setAutomatico(true);
		asiento.setDetalle(asientoJudicial.getDetalle());

/*
		int pase = 1;
		pase = setearDetalleDebe(asiento, resumenDebe, pase);
		pase = setearDetalleHaber(asiento, resumenHaber, pase);
*/
		return asiento;
	}
	
}
