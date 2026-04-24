package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.ReporteConvenioBean;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel.ItemListadoDeuda;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class GenerarAsientosAutomaticosAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		entidad = ParamUtil.getInteger(req, "entidad");

		Calendar desdeEjercicioC = DateUtils.getDesdeEjercicio(req, entidad);
		Calendar hastaEjercicioC = DateUtils.getHastaEjercicio(req, entidad);

		Calendar desdeC = DateUtils.getDesdeEjercicio(req, entidad);
		Calendar hastaC = DateUtils.getHastaEjercicio(req, entidad);

		String periodo = req.getParameter("periodo");
		String periodoHasta = req.getParameter("periodoHasta");
		int periodoInt = Integer.parseInt(periodo);
		int periodoIntHasta = Integer.parseInt(periodoHasta);
		periodoInt--;
		if (periodoIntHasta > 0) {
			periodoIntHasta--;
		} else {
			periodoIntHasta = periodoInt;
		}
		desdeC.set(Calendar.MONTH, periodoInt);

		// debo setear el dia maximo para el mes elegido
		hastaC.set(Calendar.DATE, 1);
		hastaC.set(Calendar.MONTH, periodoIntHasta);
		hastaC.set(Calendar.DATE, hastaC.getActualMaximum(Calendar.DATE));

		if (periodoInt > Calendar.JULY && entidad != WebKeysGlobal.AMTIMA) {
			hastaC.set(Calendar.YEAR, desdeC.get(Calendar.YEAR));
		} else if (periodoInt > Calendar.JUNE
				&& entidad == WebKeysGlobal.AMTIMA) {
			hastaC.set(Calendar.YEAR, desdeC.get(Calendar.YEAR));
		}

		if (periodoInt <= Calendar.JULY && entidad != WebKeysGlobal.AMTIMA) {
			desdeC.set(Calendar.YEAR, hastaC.get(Calendar.YEAR));
		} else if (periodoInt <= Calendar.JUNE
				&& entidad == WebKeysGlobal.AMTIMA) {
			desdeC.set(Calendar.YEAR, hastaC.get(Calendar.YEAR));
		}

		//Date desdeReporte = desdeC.getTime();
		//Date hastaReporte = hastaC.getTime();
		Date desdeEjercicio = desdeEjercicioC.getTime();
		Date hastaEjercicio = hastaEjercicioC.getTime();

		User user = PortalUtil.getUser(req);

		try {
			String tipo = req.getParameter("tipo");
			if (tipo.equals("ingresos_afip")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoIngresosPorAfip(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);					
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
				
			} else if (tipo.equals("ingresos_boletas")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));					
					getAsientoIngresosPorBoletas(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}				
			} else if (tipo.equals("ingresos_recibo")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoIngresosPorRecibo(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, true, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			} else if (tipo.equals("ingresos_mov_bcrio")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoIngresosPorMovBcrio(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			} else if (tipo.equals("egresos_mov_bcrios")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));					
					getAsientoEgresosPorMovBcrios(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}				
			} else if (tipo.equals("egresos_reintegros")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoEgresosPorReintegros(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			} else if (tipo.equals("egresos_prestaciones")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));					
					getAsientoEgresosPorPrestaciones(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			} else if (tipo.equals("egresos_proveedores")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoEgresosPorProveedores(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}				
			} else if (tipo.equals("ingresos_actas")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));					
					getAsientoIngresosPorActas(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			} else if (tipo.equals("comprobantes")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoComprobantesAPagar(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}				
			}else if (tipo.equals("devengado_boletas")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoDevengadoBoletas(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}
			}else if (tipo.equals("devengado_comprobantes")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoDevengadoComprobantes(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}		
			}else if (tipo.equals("devengado_facturas_ventas")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoDevengadoFacturasVentas(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}		
			}else if (tipo.equals("cobranzas_facturas_ventas")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoCobranzasFacturasVentas(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}		
			}else if (tipo.equals("judiciales")) {
				Calendar aux = (Calendar) desdeC.clone();
				while (aux.compareTo(hastaC) <= 0) {
					aux.set(Calendar.DATE,
							aux.getActualMaximum(Calendar.DATE));
					getAsientoDemandasJudiciales(desdeC.getTime(), aux.getTime(),
							desdeEjercicio, hastaEjercicio, user, entidad);
					desdeC.add(Calendar.MONTH, 1);
					aux=(Calendar)desdeC.clone();
				}		
			}			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}

		return "{\"status\":\"exito\"}";
	}

	private Asiento getAsientoComprobantesAPagar(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		Asiento asientoComprobantesAPagar = null;
		boolean vacio = false;
		List<ItemListadoDeuda> listado = ContabilidadServiceUtil
				.listadoDeDeudas(desde, hasta, null, null, null, hasta, true,
						true, true, entidad);
		vacio = listado.size() <= 0;

		asientoComprobantesAPagar = Asiento.builAsientoFromListadoDeDeudas(
				listado, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.COMPROBANTES_A_PAGAR);

		AsientoServiceUtil.saveAutomatico(asientoComprobantesAPagar, user,
				entidad, vacio);
		return asientoComprobantesAPagar;
	}

	private Asiento getAsientoIngresosPorActas(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<ReporteActaBean> reporteActas = ActaServiceUtil.reporteActas(
				desde, hasta, entidad);
		List<ReporteConvenioBean> reporteConvenios = ConvenioServiceUtil
				.reporteConvenios(desde, hasta, entidad);

		vacio = reporteActas.size() <= 0 && reporteConvenios.size() <= 0;

		Asiento asientoIngresosPorActas = Asiento.buildAsientoFromActas(
				reporteActas, reporteConvenios, hasta, desdeEjercicio,
				hastaEjercicio, Asiento.ACTAS_Y_CONVENIOS, entidad);

		AsientoServiceUtil.saveAutomatico(asientoIngresosPorActas, user,
				entidad, vacio);
		return asientoIngresosPorActas;
	}

	private Asiento getAsientoIngresosPorAfip(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		List<ItemSubdiarioIngreso> reporteIngresosAfip = ContabilidadServiceUtil
				.subdiarioIngresos(desde, hasta, new Empresa(), false, false,
						true, entidad == WebKeysGlobal.UOMA ? true : false,
						entidad);
		Asiento asientoIngresosPorAfip = Asiento.buildAsientoFromIngresos(
				reporteIngresosAfip, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.INGRESOS_AFIP);
		AsientoServiceUtil.saveAutomatico(asientoIngresosPorAfip, user,
				entidad, false);
		return asientoIngresosPorAfip;
	}

	private Asiento getAsientoIngresosPorRecibo(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user,
			boolean contabilidad, int entidad) throws Exception {
		boolean vacio = false;
		List<ItemSubdiarioIngreso> reporteIngresosRecibo = ContabilidadServiceUtil
				.subdiarioIngresos(desde, hasta, new Empresa(), false, true,
						false, contabilidad, entidad);
		Asiento asientoIngresosPorRecibo = Asiento.buildAsientoFromIngresos(
				reporteIngresosRecibo, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.INGRESOS_POR_RECIBOS);
		vacio = reporteIngresosRecibo.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoIngresosPorRecibo, user,
				entidad, vacio);
		return asientoIngresosPorRecibo;
	}

	// VER COMO DEBE SER ESTE ASIENTO PARA AMTIMA...
	private Asiento getAsientoIngresosPorBoletas(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<ItemSubdiarioIngreso> reporteBoletas = ContabilidadServiceUtil
				.subdiarioIngresosBoleta(desde, hasta, entidad);
		Asiento asientoIngresosPorRecibo = Asiento.buildAsientoFromIngresos(
				reporteBoletas, hasta, desdeEjercicio, hastaEjercicio,
				entidad == WebKeysGlobal.AMTIMA ? Asiento.BOLETAS_AMTIMA
						: Asiento.BOLETAS_UOMA);
		vacio = reporteBoletas.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoIngresosPorRecibo, user,
				entidad, vacio);
		return asientoIngresosPorRecibo;
	}

	private Asiento getAsientoIngresosPorMovBcrio(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<ItemSubdiarioIngreso> reporteIngresosBanco = ContabilidadServiceUtil
				.subdiarioIngresos(desde, hasta, new Empresa(), true, false,
						false, entidad == WebKeysGlobal.UOMA ? true : false,
						entidad);
		Asiento asientoIngresosPorMovBcrios = Asiento.buildAsientoFromIngresos(
				reporteIngresosBanco, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.INGRESOS_POR_MOVIMIENTOS_BANCARIOS);
		vacio = reporteIngresosBanco.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoIngresosPorMovBcrios, user,
				entidad, vacio);
		return asientoIngresosPorMovBcrios;
	}

	private Asiento getAsientoEgresosPorMovBcrios(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<? extends ItemSubdiarioEgreso> reporteMovimientosBcrios = MovimientoBancarioServiceUtil
				.reporteParaSubdiario(desde, hasta, entidad);
		Asiento asientoMovBcrios = Asiento.buildAsientoFromEgresos(
				reporteMovimientosBcrios, hasta, desdeEjercicio,
				hastaEjercicio, Asiento.EGRESOS_POR_MOVIMIENTOS_BANCARIOS,
				entidad);
		vacio = reporteMovimientosBcrios.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoMovBcrios, user, entidad,
				vacio);
		return asientoMovBcrios;
	}

	private Asiento getAsientoEgresosPorPrestaciones(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		List<? extends ItemSubdiarioEgreso> reportePrestaciones = null;
		if (entidad == WebKeysGlobal.AMTIMA) {
			reportePrestaciones = OrdenPagoServiceUtil
					.reporteOrdenPagoCompletoParaSubdiario(desde, hasta, false,
							true, false, false, entidad, 0);
		} else if (entidad == WebKeysGlobal.OSPIM) {
			reportePrestaciones = OrdenPagoServiceUtil
					.reporteOrdenPagoOspimCompletoParaSubdiario(desde, hasta,
							false, true, false);
		}

		Asiento asientoPrestaciones = Asiento.buildAsientoFromEgresos(
				reportePrestaciones, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.EGRESOS_POR_PRESTADORES, entidad);
		AsientoServiceUtil.saveAutomatico(asientoPrestaciones, user, entidad,
				false);
		return asientoPrestaciones;
	}

	private Asiento getAsientoEgresosPorProveedores(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<? extends ItemSubdiarioEgreso> reporteProveedores = null;
		if (entidad != WebKeysGlobal.OSPIM) {
			reporteProveedores = OrdenPagoServiceUtil
					.reporteOrdenPagoCompletoParaSubdiario(desde, hasta, true,
							false, false, entidad == WebKeysGlobal.UOMA ? true
									: false, entidad, 0);
			vacio = reporteProveedores.size() <= 0;
		} else {
			/* SACAR */
			/*
			 * Calendar calDesde=Calendar.getInstance(); calDesde.set(2014, 10,
			 * 05); //PROBLEMA DEL 01 AL 07 05-07 Calendar
			 * calHasta=Calendar.getInstance(); calHasta.set(2014, 10, 07);
			 */
			/* HASTA ACA */
			reporteProveedores = OrdenPagoServiceUtil
					.reporteOrdenPagoOspimCompletoParaSubdiario(desde, hasta,
							true, false, false);
		}
		Asiento asientoProveedores = Asiento.buildAsientoFromEgresos(
				reporteProveedores, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.EGRESOS_POR_PROVEEDORES, entidad);
		AsientoServiceUtil.saveAutomatico(asientoProveedores, user, entidad,
				vacio);
		return asientoProveedores;
	}

	private Asiento getAsientoEgresosPorReintegros(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		List<? extends ItemSubdiarioEgreso> reporteReintegros = null;
		if (entidad == WebKeysGlobal.AMTIMA) {
			reporteReintegros = OrdenPagoServiceUtil
					.reporteOrdenPagoCompletoParaSubdiario(desde, hasta, false,
							false, true, false, entidad, 0);
		} else if (entidad == WebKeysGlobal.OSPIM) {
			reporteReintegros = OrdenPagoServiceUtil
					.reporteOrdenPagoOspimCompletoParaSubdiario(desde, hasta,
							false, false, true);
		}

		Asiento asientoReintegros = Asiento.buildAsientoFromEgresos(
				reporteReintegros, hasta, desdeEjercicio, hastaEjercicio,
				Asiento.EGRESOS_POR_REINTEGROS, entidad);
		AsientoServiceUtil.saveAutomatico(asientoReintegros, user, entidad,
				false);
		return asientoReintegros;
	}
	
	
	private Asiento getAsientoDevengadoBoletas(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<FichaBoletaPortal> reporteBoletas = ContabilidadServiceUtil
				.devengadoBoleta(desde, hasta, entidad);
		Asiento asientoDevengadoBoletas = Asiento.buildAsientoFromDevengadoBoletas(
				reporteBoletas, hasta, desdeEjercicio, hastaEjercicio,
				entidad == WebKeysGlobal.AMTIMA ? Asiento.BOLETAS_AMTIMA_DEVENGADO
						: Asiento.BOLETAS_UOMA_DEVENGADO,entidad);
		vacio = reporteBoletas.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoDevengadoBoletas, user,
				entidad, vacio);
		return asientoDevengadoBoletas;
	}
	
	private Asiento getAsientoDevengadoComprobantes(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<Comprobante> reporteBoletas = ContabilidadServiceUtil
				.devengadoComprobantes(desde, hasta, entidad);
		Asiento asientoDevengadoComprobantes = Asiento.buildAsientoFromDevengadoComprobantes(
				reporteBoletas, hasta, desdeEjercicio, hastaEjercicio,
				entidad == WebKeysGlobal.AMTIMA ? Asiento.COMPROBANTES_AMTIMA_DEVENGADO
						: Asiento.COMPROBANTES_UOMA_DEVENGADO,entidad);
		vacio = reporteBoletas.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoDevengadoComprobantes, user,
				entidad, vacio);
		return asientoDevengadoComprobantes;
	}
	
	private Asiento getAsientoDevengadoFacturasVentas(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<Factura> facturas =FacturacionServiceUtil.getFacturasPeriodo(desde, hasta);
		
		Asiento asientoFacturas = Asiento.buildAsientoFromDevengadoFacturasVentas(
				facturas, hasta, desdeEjercicio, hastaEjercicio,
				"Facturación del Mes",entidad);
		vacio = facturas.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoFacturas, user,
				entidad, vacio);
		return asientoFacturas;
	}
	
	
	private Asiento getAsientoCobranzasFacturasVentas(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, int entidad)
			throws Exception {
		boolean vacio = false;
		List<FacturaIngreso> facturas =FacturacionServiceUtil.getPagosFacturasPeriodo(desde,hasta);
		
		Asiento asientoFacturas = Asiento.buildAsientoFromCobranzasFacturasVentas(
		        facturas, hasta, desdeEjercicio, hastaEjercicio,
				"Cobranzas Facturas del Mes",entidad);
		vacio = facturas.size() <= 0;
		AsientoServiceUtil.saveAutomatico(asientoFacturas, user,
				entidad, vacio);
		return asientoFacturas;
	}
	
	
	private List<Asiento> getAsientoDemandasJudiciales(Date desde, Date hasta,
			Date desdeEjercicio, Date hastaEjercicio, User user, Integer entidad)
			throws Exception {
		boolean vacio = false;
		String e="";
		if(WebKeysGlobal.AMTIMA==entidad) {
			e="A";
		}else if(WebKeysGlobal.OSPIM==entidad) {
			e="O";
		}else if(WebKeysGlobal.UOMA==entidad) {
			e="U";
		}
		List<Asiento>ast=new ArrayList<Asiento>();
		List<Asiento> asientos =DemandaJudicialServiceUtil.getAsientosByFechas(desde, hasta,e);
		vacio = asientos.size() <= 0;
		for(Asiento a : asientos) {
		   Asiento asientoDemanda = Asiento.buildAsientoFromJudiciales(
				a,desdeEjercicio, hastaEjercicio,a.getDescripcion(),e);
		   
		   AsientoServiceUtil.saveAutomatico(asientoDemanda, user,
				    entidad, vacio);
		   ast.add(asientoDemanda);
		}   
		return ast;
	}
}
