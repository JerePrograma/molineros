package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.global.beans.ConceptoSueldos;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.FechaCierre;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.LibroBanco;
import ar.com.ospim.tesoreria.beans.LibroCaja;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion;
import ar.com.ospim.tesoreria.reportes.ReporteEstadoComprobantesExcel.EstadoComprobante;
import ar.com.ospim.tesoreria.reportes.ReporteLibroBancoExcel.EstadoInicialLibroBanco;
import ar.com.ospim.tesoreria.reportes.ReporteLibroCajaExcel.EstadoInicialLibroCaja;
import ar.com.ospim.tesoreria.reportes.ReporteListadoValoresExcel.ReporteListadoValores;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel.ItemListadoDeuda;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ContabilidadServiceUtil {
	private static Log logger = LogFactoryUtil
			.getLog(ContabilidadServiceUtil.class);

	public static List<LibroBanco> libroBanco(Date fechaInicio, Date fechaFin,
			Integer ctaBcria, int entidad) throws SystemException {
		logger.debug("buscando libro banco dde: " + fechaInicio + " hasta : "
				+ fechaFin + " cta: " + ctaBcria);
		return ContabilidadServiceImpl.getInstance().libroBanco(fechaInicio,
				fechaFin, ctaBcria, entidad);
	}

	public static EstadoInicialLibroBanco getSaldoInicialBanco(int idCtaBcria,
			Date fechaIni) throws SystemException {
		logger.debug("getSaldoInicial " + idCtaBcria);
		return ContabilidadServiceImpl.getInstance().getSaldoInicialBanco(
				idCtaBcria, fechaIni);
	}

	public static List<LibroCaja> libroCaja(Date fechaInicio, Date fechaFin, int entidad)
			throws SystemException {
		logger.debug("buscando libro banco dde: " + fechaInicio + " hasta : "
				+ fechaFin);
		return ContabilidadServiceImpl.getInstance().libroCaja(fechaInicio,
				fechaFin, entidad);
	}

	public static EstadoInicialLibroCaja getSaldoInicialCaja(Date fechaIni, int entidad)
			throws SystemException {
		return ContabilidadServiceImpl.getInstance().getSaldoInicialCaja(
				fechaIni, entidad);
	}

	public static List<CuentaCorriente> cuentaCorrienteAcreedores(
			Date fechaIni, Date fechaFin, String cuit, String sucu,
			Integer seccional, Date fechaPagoHasta, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros, boolean incluirLiquidacionesFarmacia, boolean incluirReintegrosFarmacia, int entidad)
			throws SystemException {
		
			return ContabilidadServiceImpl.getInstance()
					.cuentaCorrienteAcreedores(cuit, sucu, seccional, fechaIni,
							fechaFin, fechaPagoHasta, incluirProveedores,
							incluirLiquidaciones, incluirReintegros, incluirLiquidacionesFarmacia,incluirReintegrosFarmacia, entidad);		
	}

	public static List<EstadoComprobante> listadoEstadoComprobantes(
			Date fechaIni, Date fechaFin, Date fechaPagoFin, String cuit,
			String sucu, Integer seccional, boolean soloConSaldo,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros,  Date fechaEmiIni, Date fechaEmiFin, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().listadoEstadoComprobantes(
				fechaIni, fechaFin, fechaPagoFin, cuit, sucu, seccional,
				soloConSaldo, incluirProveedores, incluirLiquidaciones,
				incluirReintegros,  fechaEmiIni, fechaEmiFin,entidad);
	}

	public static List<CuentaCorriente> cuentaCorrienteActasYConvenios(
			Date fechaIni, Date fechaFin, String cuit, String sucu,
			Integer seccional, int id, String tipoReporte, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance()
				.cuentaCorrienteActasYConvenios(fechaIni, fechaFin, cuit, sucu,
						seccional, id, tipoReporte, entidad);
	}

	public static List<CuentaCorriente> cuentaCorrienteActasYConveniosConApoContrib(
			Date fechaIni, Date fechaFin, String cuit, String sucu,
			Integer seccional) throws SystemException {
		return ContabilidadServiceImpl.getInstance()
				.cuentaCorrienteActasYConveniosConApoContrib(fechaIni,
						fechaFin, cuit, sucu, seccional);
	}

	public static List<EstadoInicialCuentaCorriente> getSaldoInicialCtasCtes(
			String cuit, String sucu, Integer seccional, Date fechaIni, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros, boolean incluirLiquidaciones_farmacia, boolean incluirReintegros_farmacia, int entidad)
			throws SystemException {
		return ContabilidadServiceImpl.getInstance().getSaldoInicialCtasCtes(
				cuit, sucu, seccional, fechaIni, incluirProveedores,
				incluirLiquidaciones, incluirReintegros, incluirLiquidaciones_farmacia, incluirReintegros_farmacia, entidad);
	}

	public static List<ItemSubdiarioIngreso> subdiarioIngresos(Date fechaIni,
			Date fechaFin, Empresa empresa, boolean incluirBcrios,
			boolean incluirRecibos, boolean incluirAfip, boolean contabilidad, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().subdiarioIngresos(
				fechaIni, fechaFin, empresa, incluirBcrios, incluirRecibos,
				incluirAfip, contabilidad, entidad);
	}

	public static List<ReporteListadoValores> listadoValores(
			Date fechaVtoInicio, Date fechaVtoFin, Date fechaDptoInicio,
			Date fechaDptoFin, Date fechaRechInicio, Date fechaRechFin,
			Date fechaReemInicio, Date fechaReemFin, String cuit,
			Integer idBanco, Integer depositados, Integer reemplazados,
			Integer rechazados, Integer cta_bcria, int entidad, int nro_cheque, Date fechaReciIni, Date fechaReciFin) throws SystemException {
		logger.debug("buscando listado valores: " + fechaVtoInicio
				+ " hasta : " + fechaVtoFin);
		return ContabilidadServiceImpl.getInstance()
				.listadoValores(fechaVtoInicio, fechaVtoFin, fechaDptoInicio,
						fechaDptoFin, fechaRechInicio, fechaRechFin,
						fechaReemInicio, fechaReemFin, cuit, idBanco,
						depositados, reemplazados, rechazados, cta_bcria, entidad, nro_cheque, fechaReciIni, fechaReciFin);
	}
	
	public static List<ReporteListadoValores> listadoValores(
			Date fechaVtoInicio, Date fechaVtoFin, Date fechaDptoInicio,
			Date fechaDptoFin, Date fechaRechInicio, Date fechaRechFin,
			Date fechaReemInicio, Date fechaReemFin, String cuit,
			Integer idBanco, Integer depositados, Integer reemplazados,
			Integer rechazados, Integer cta_bcria, int entidad, int nro_cheque, Date fechaReciIni, Date fechaReciFin,
			Integer judicializados, Date fechaJudiInicio,Date fechaJudiFin) throws SystemException {
		logger.debug("buscando listado valores: " + fechaVtoInicio
				+ " hasta : " + fechaVtoFin);
		return ContabilidadServiceImpl.getInstance()
				.listadoValores(fechaVtoInicio, fechaVtoFin, fechaDptoInicio,
						fechaDptoFin, fechaRechInicio, fechaRechFin,
						fechaReemInicio, fechaReemFin, cuit, idBanco,
						depositados, reemplazados, rechazados, cta_bcria, entidad, nro_cheque, fechaReciIni, fechaReciFin,
						judicializados,fechaJudiInicio,fechaJudiFin);
	}
	
	public static void listadoValoresSeguimiento(
			String cuit, PortletRequest request) throws SystemException {		
		List<ReporteListadoValores> chequesRechazados=null;
		List<ReporteListadoValores> chequesReemplazoRechazo=null;
		List<ReporteListadoValores> chequesCanjeadosSinDepo=null;
		List<ReporteListadoValores> chequesCartera=null;
			
		List<ReporteListadoValores> lista=ContabilidadServiceImpl.getInstance()
				.listadoValoresSeguimiento(null,null, null, null, null, null, null,
						null, cuit, null, null, null, null,	null, 0, null, null);
		
		
		for(ReporteListadoValores repo: lista){
			if(null!=repo.getFechaRechazado()&&null==repo.getFechaReemplazo()){
				if(null==chequesRechazados){
					chequesRechazados=new ArrayList<ReporteListadoValores>();
				}
				chequesRechazados.add(repo);
			}
			if(null!=repo.getFechaRechazado()&&null!=repo.getFechaReemplazo()){
				if(null==chequesReemplazoRechazo){
					chequesReemplazoRechazo=new ArrayList<ReporteListadoValores>();
				}
				chequesReemplazoRechazo.add(repo);
			}
			if(null!=repo.getFechaReemplazo()&&null==repo.getFechaDeposito()){
				if(null==chequesCanjeadosSinDepo){
					chequesCanjeadosSinDepo=new ArrayList<ReporteListadoValores>();
				}
				chequesCanjeadosSinDepo.add(repo);
			}
			if(null==repo.getFechaReemplazo()&&null==repo.getFechaDeposito()){
				if(null==chequesCartera){
					chequesCartera=new ArrayList<ReporteListadoValores>();
				}
				chequesCartera.add(repo);
			}			
		}
		
		PortletSession portletSession = request.getPortletSession();
		portletSession.removeAttribute(WebKeysEstudioIsidro.CHEQUES_RECHAZADOS);
		portletSession.setAttribute(WebKeysEstudioIsidro.CHEQUES_RECHAZADOS, chequesRechazados);
		
		portletSession.removeAttribute(WebKeysEstudioIsidro.CHEQUES_REEMP_RECHAZADOS);
		portletSession.setAttribute(WebKeysEstudioIsidro.CHEQUES_REEMP_RECHAZADOS, chequesReemplazoRechazo);
		
		portletSession.removeAttribute(WebKeysEstudioIsidro.CHEQUES_CANJEADOS_SIN_DEPO);
		portletSession.setAttribute(WebKeysEstudioIsidro.CHEQUES_CANJEADOS_SIN_DEPO, chequesCanjeadosSinDepo);
		
		portletSession.removeAttribute(WebKeysEstudioIsidro.CHEQUES_CARTERA);
		portletSession.setAttribute(WebKeysEstudioIsidro.CHEQUES_CARTERA, chequesCartera);
		
	}

	public static List<ItemListadoDeuda> listadoDeDeudas(Date fechaIni,
			Date fechaFin, String cuit, String sucu, Integer seccional,
			Date fechaPagoHasta, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros, int entidad)
			throws SystemException {
		logger.debug("buscando listado deudas: ");
		return ContabilidadServiceImpl.getInstance().listadoDeDeudas(fechaIni,
				fechaFin, cuit, sucu, seccional, fechaPagoHasta,
				incluirProveedores, incluirLiquidaciones, incluirReintegros, entidad);
	}

	public static List<EstadoInicialCuentaCorriente> saldoInicialCorrienteActasYConvenios(
			String cuit, String sucu, Integer seccional, Date fechaIni, int entidad)
			throws SystemException {
		return ContabilidadServiceImpl.getInstance()
				.saldoInicialCorrienteActasYConvenios(cuit, sucu, seccional,
						fechaIni, entidad);
	}

	public static boolean isAsientosOrdenados(Date ejercicioIni,
			Date ejercicioFin, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().isAsientosOrdenados(
				ejercicioIni, ejercicioFin, entidad);
	}

	public static void ordenarAsientos(Date ejercicioIni, Date ejercicioFin, int entidad)
			throws SystemException {
		ContabilidadServiceImpl.getInstance().ordenarAsientos(ejercicioIni,
				ejercicioFin, entidad);
	}

	public static List<FechaCierre> getFechasCierreContable(int entidad)
			throws SystemException {
		return ContabilidadServiceImpl.getInstance().getFechasCierreContable(entidad);
	}

	public static List<FechaCierre> getFechasCierreAsientos(int entidad)
			throws SystemException {
		return ContabilidadServiceImpl.getInstance().getFechasCierreAsientos(entidad);
	}

	public static void guardarFechaCierreContableGestion(
			FechaCierre fechacierre, User user, int entidad) throws SystemException {
		ContabilidadServiceImpl.getInstance()
				.guardarFechaCierreContableGestion(fechacierre,
						user.getScreenName(), entidad);

	}

	public static void guardarFechaCierreContableAsientos(
			FechaCierre fechacierre, User user, int entidad) throws SystemException {
		ContabilidadServiceImpl.getInstance()
				.guardarFechaCierreContableAsientos(fechacierre,
						user.getScreenName(), entidad);

	}

	public static void eliminarFechaCierreContableGestion(
			FechaCierre fechacierre, User user, int entidad) throws SystemException {
		ContabilidadServiceImpl.getInstance()
				.eliminarFechaCierreContableGestion(fechacierre,
						user.getScreenName(), entidad);

	}

	public static void eliminarFechaCierreContableAsientos(
			FechaCierre fechacierre, User user, int entidad) throws SystemException {
		ContabilidadServiceImpl.getInstance()
				.eliminarFechaCierreContableAsientos(fechacierre,
						user.getScreenName(), entidad);

	}

	public static Date getFechaUltimoPeriodoContable(int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance()
				.getFechaUltimoPeriodoContable(entidad);
	}

	public static Date getFechaCierreAsientos(int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().getFechaCierreAsientos(entidad);
	}
	
	public static List<ItemSubdiarioIngreso> subdiarioIngresosBoleta(Date fechaIni,
			Date fechaFin, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().subdiarioIngresosBoleta(fechaIni,fechaFin, entidad);
	}

	public static PlanCuentasSSS getEquivalenciaPlanCuentaSSS(String cta, int entidad,String tipo) throws SystemException {
		return ContabilidadServiceImpl.getInstance().getEquivalenciaPlanCuentaSSS(cta, entidad,tipo) ;
	}
	
	public static List<PlanCuentas> getCuentasAsociadasSSS(String cta, int entidad,String tipo) throws SystemException {
		return ContabilidadServiceImpl.getInstance().getCuentasAsociadasSSS(cta, entidad,tipo) ;
	}
	
	public static long addCuentaSSS(PlanCuentasSSS cuenta, int entidad,String screenName) throws Exception {

		long idCuenta = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCuenta=ContabilidadServiceImpl.getInstance().addCuentaSSS(cuenta, entidad,screenName,connection);
			for(PlanCuentas d:cuenta.getEquivalencias()){
				ContabilidadServiceImpl.getInstance().addCuentaSSSAsociacion(cuenta.getNumero(),entidad,d,screenName,connection);
			}
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCuenta;
	}
	
	public static long updateCuentaSSS(PlanCuentasSSS cuenta, int entidad,String screenName) throws Exception {
		long idCuenta = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCuenta=ContabilidadServiceImpl.getInstance().updateCuentaSSS(cuenta, entidad,screenName,connection);
		    
		    ContabilidadServiceImpl.getInstance().deleteCuentaSSSAsociacion(cuenta.getNumero(),entidad,connection);
		    
			for(PlanCuentas d:cuenta.getEquivalencias()){
				ContabilidadServiceImpl.getInstance().addCuentaSSSAsociacion(cuenta.getNumero(),entidad,d,screenName,connection);
			}
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCuenta;
	}
	
	public static long deleteCuentaSSS(PlanCuentasSSS cuenta, int entidad) throws Exception {
		long idCuenta = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCuenta=ContabilidadServiceImpl.getInstance().deleteCuentaSSS(cuenta, entidad,connection);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCuenta;
	}
	
	public static Map<Integer,BigDecimal> getCoeficientesAjustesInflacion(Integer entidad,Integer periodoDde ,Integer periodoHta){
		return ContabilidadServiceImpl.getInstance()
				.getCoeficientesAjusteInflacion(entidad,periodoDde,periodoHta);
		
	}
	
	
	public static long  updateCoeficienteAjusteInflacion(CoeficienteAjusteInflacion c) throws SystemException, SQLException{
		return ContabilidadServiceImpl.getInstance()
				.updateCoeficienteAjusteInflacion(c,null);
	}
	
	public static long  deleteCoeficienteAjusteInflacion(CoeficienteAjusteInflacion c) throws SystemException, SQLException{
		return ContabilidadServiceImpl.getInstance()
				.deleteCoeficienteAjusteInflacion(c,null);
	}
	
	public static List<FichaBoletaPortal> devengadoBoleta(Date fechaIni,
			Date fechaFin, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().devengadoBoleta(fechaIni,fechaFin, entidad);
	}
	
	public static List<Comprobante> devengadoComprobantes(Date fechaIni,
			Date fechaFin, int entidad) throws SystemException {
		return ContabilidadServiceImpl.getInstance().devengadoComprobantes(fechaIni,fechaFin, entidad);
	}
	
	public static List<ConceptoSueldos> equivalenciasSueldos(String entidad,Integer sector,Integer codigo) 
			throws SystemException {
		return ContabilidadServiceImpl.getInstance().equivalenciasSueldos(entidad,sector,codigo) ;
	}
	
	public static Integer updateEquivalenciasSueldos(ConceptoSueldos concepto) throws Exception {
		Integer idConcepto= 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idConcepto=ContabilidadServiceImpl.getInstance().updateEquivalenciasSueldos(concepto);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idConcepto;
	}
	
	public static Integer deleteEquivalenciasSueldos(Integer id) throws Exception {
		Integer idConcepto= 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idConcepto=ContabilidadServiceImpl.getInstance().deleteEquivalenciasSueldos(id);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idConcepto;
	}
	
	public static Asiento buildAsientoSueldos(String entidad,Asiento asiento,List<ConceptoSueldos>lista,Integer idCuentaNeteo) throws Exception {
		Integer ent=0;
		Double debe=0D;
		Double haber=0D;
		Double tdebe=0D;
		Double thaber=0D;
		
		if(lista==null) {
			return asiento;
		}
		//if(asiento.getDetalle()==null)
		asiento.setDetalle(new ArrayList<Detalle>());
		BigDecimal detDebe=BigDecimal.ZERO;
		BigDecimal detHaber=BigDecimal.ZERO;
		Map<String,Detalle> map = new HashMap();
		if("A".equals(entidad)) {
				ent=WebKeysGlobal.AMTIMA;
		}else if("O".equals(entidad)) {
			    ent=WebKeysGlobal.OSPIM;
		}else {
			ent=WebKeysGlobal.UOMA;
		}
		
		for(ConceptoSueldos c:lista) {
			Detalle detalle = null;
			if(c.getCuentaContable()!=null) {
			   detalle =map.get(String.valueOf(c.getCuentaContable().getId()).trim()+c.getDebeHaber());
			   if(detalle==null) {
				   detalle = new Detalle();
				   detalle.setDebe(BigDecimal.ZERO);
				   detalle.setHaber(BigDecimal.ZERO);
				   detalle.setCuenta(c.getCuentaContable());
			   }
			
			   detDebe= detalle.getDebe();
			   detHaber= detalle.getHaber();
			
			   if("D".equals(c.getDebeHaber())) {
				//debe = Math.abs(c.getRemunerativo()) + Math.abs(c.getNoRemunerativo()) + 
				//		Math.abs(c.getRetencion()) + Math.abs(c.getContribucion());
				
				debe = c.getRemunerativo() + c.getNoRemunerativo() + c.getRetencion() + c.getContribucion();
				detalle.setDebe(detDebe.add(new BigDecimal(debe)));
				detalle.setHaber(BigDecimal.ZERO);
				tdebe += debe;
			   }else if("H".equals(c.getDebeHaber())) {
				haber = c.getRemunerativo() + c.getNoRemunerativo() + c.getRetencion() + c.getContribucion();
				
				//haber = Math.abs(c.getRemunerativo()) + Math.abs(c.getNoRemunerativo()) + 
				//		Math.abs(c.getRetencion()) + Math.abs(c.getContribucion());
				detalle.setHaber(detHaber.add(new BigDecimal(haber)));
				detalle.setDebe(BigDecimal.ZERO);
				thaber += haber;
			   }
			
			   map.put(String.valueOf(c.getCuentaContable().getId()).trim()+c.getDebeHaber(), detalle);
			}   
		}
		
		
		 Map<String, Detalle> sortedMap = new TreeMap<String, Detalle>();
		 for (Detalle value : map.values()) {
			   sortedMap.put((value.getDebe().compareTo(BigDecimal.ZERO)>0?"D":"H")+ value.getCuenta().getNumero(),value);
		 }
		
		Integer pase=1;
		for (Detalle value : sortedMap.values()) {
			value.setPase(pase++);
		    asiento.getDetalle().add(value);
		}
		
		/*
		for (Detalle value : map.values()) {
			value.setPase(pase++);
		    asiento.getDetalle().add(value);
		}
		*/
		
		PlanCuentas neteo = TraeListasServiceUtil.getCuentaById(idCuentaNeteo,new Date(), ent);
		Detalle dneteo =new Detalle();
		Double diferencia =tdebe-thaber;
		dneteo.setCuenta(neteo);
		
		if(diferencia>0) {
			dneteo.setHaber(new BigDecimal(diferencia));
			dneteo.setDebe(BigDecimal.ZERO);
			thaber+=diferencia;
		}else {
			dneteo.setDebe(new BigDecimal(Math.abs(diferencia)));
			dneteo.setHaber(BigDecimal.ZERO);
			tdebe += Math.abs(diferencia);
		}
		dneteo.setPase(pase++);
		asiento.getDetalle().add(dneteo);
		
	  return asiento;
	}
		
}
