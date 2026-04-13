package ar.com.uoma.cuentacorrienteempresa.action;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.empresas.beans.Actividad;
import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.beans.SolicitudAutorizacionPS;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.CuentaCorriente.Informacion;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente.SaldoInicial;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.Proveedor;
import ar.com.uoma.beans.CuentaCorrienteEmpresa;
import ar.com.uoma.cuentacorrienteempresa.services.CuentaCorrienteEmpresaServiceUtil;

import ar.com.ospim.tesoreria.reportes.ReporteCuentaCorriente;
import ar.com.ospim.tesoreria.reportes.ReporteCuentasCorrienteActasYConveniosExcel;
import ar.com.ospim.tesoreria.reportes.ReporteCuentasCorrientesExcel;

public class CuentaCorrienteEmpresaAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String portlet_name = null;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			portlet_name = "uoma";
		}
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		int vista = ParamUtil.getInteger(renderRequest, "vista");
		
		String msg = "";
		String accion="";		
		String cuit=ParamUtil.getString(renderRequest,"cuit_entidad",null);
		accion =ParamUtil.getString(renderRequest,"accion", "");
		int qryConsolidado=ParamUtil.getInteger(renderRequest,"consolidado");

		if (("exp1".equals(accion)) || ("exp2".equals(accion))) {
			String suc=ParamUtil.getString(renderRequest,"sucursal",null);
			procQueryExportActas(renderRequest,session, vista);			
			
			return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_export_view"));
		} else {
			procQueryCuentaCorriente(renderRequest,session, vista);
		}
		
		if (vista == 0) {
	        if (((cuit == null) || (cuit == "")) && (qryConsolidado == 1)){
	    	    return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_v0"));
	        } else {
	            return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_cuit_v0"));		        	   
	        }	           			
		}
		if (vista == 1) {
            return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_cuit_v1"));		        	   
/*
	        if (((cuit == null) || (cuit == "")) && (qryConsolidado == 1)){
	    	    return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_v1"));
	        } else {
	            return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_cuit_v1"));		        	   
	        }
*/
		}
		if (vista == 2) {
            return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_cuit_v2"));
/*            
	        if (((cuit == null) || (cuit == "")) && (qryConsolidado == 1)){
	    	    return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_v2"));
	        } else {
	            return mapping.findForward(getForward(renderRequest, 
	        		   "portlet.uoma.cuentacorriente_search_result_cuit_v2"));		        	   
	        }
*/
		}	
		
		return mapping.findForward(getForward(renderRequest, 
     		   "portlet.uoma.cuentacorriente_search_result_v0"));
	}	
	
	private static BigDecimal calcularRowSaldoInicial(
			List<SaldoInicial> saldosIniciales, Date fechaIni, Empresa empresa,
			List<Informacion> info, int entidad) {
	
		BigDecimal saldoInicial = BigDecimal.ZERO;
		SaldoInicial minSaldoIni = null;
		if (saldosIniciales != null && saldosIniciales.size() > 0) {
			minSaldoIni = (SaldoInicial) Collections.min(saldosIniciales);
			if (DateUtils.compararFechasTruncarEnDia(minSaldoIni.getFecha(),
					fechaIni) < 0) {
				saldoInicial = minSaldoIni.getImporte();
			}
		}
		
		Iterator<Informacion> it = info.iterator();
		boolean stop = false;
		while (it.hasNext() && !stop) {
			Informacion l = it.next();
			// el saldo inicial a la fecha XX es al ppio de ese dia.
			// (el saldo inicial no incluye los movimientos del dia XX)
			if (DateUtils.compararFechasTruncarEnDia(l.getFecha(), fechaIni) < 0) {
				
//DS - Agregado para corregir no contemplacion de saldo forzado con registros menores a fecha Inicial				
				if(minSaldoIni!=null) {
					if (DateUtils.compararFechasTruncarEnDia(l.getFecha(), minSaldoIni.getFecha()) < 0) {
						saldoInicial=minSaldoIni.getImporte();
					}else {
						if (l.getDebitoCredito().equals("D")) {
							saldoInicial = saldoInicial.subtract(l.getImporte());
						} else {
							saldoInicial = saldoInicial.add(l.getImporte());
						}
					}
				}else {
				
				  if (l.getDebitoCredito().equals("D")) {
					saldoInicial = saldoInicial.subtract(l.getImporte());
				  } else {
					saldoInicial = saldoInicial.add(l.getImporte());
				  }
				
				}
//DS - Fin				
			} else {
				stop = true;
			}
		}

		/*
		getRowSaldoInicial(fechaIni, saldoInicial, styleMoney, sheet,
				styleDate, styleAll, i, mostrarPeriodo, mostrarMasInfo,
				"Saldo Inicial Calculado", entidad);
		*/

		return saldoInicial;

	}
	
	private void procQueryExportActas(RenderRequest renderRequest,
			HttpSession session, 
			int vista) throws SystemException{
		String cuit=ParamUtil.getString(renderRequest,"cuit_entidad",null);
		String sucursal=ParamUtil.getString(renderRequest,"suc_entidad",null);
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeMes=ParamUtil.getString(renderRequest,"desde_mes",null);
		String fechaDesdeAnio=ParamUtil.getString(renderRequest,"desde_anio",null);
		String fechaHastaMes=ParamUtil.getString(renderRequest,"hasta_mes",null);
		String fechaHastaAnio=ParamUtil.getString(renderRequest,"hasta_anio",null);
		String accion=ParamUtil.getString(renderRequest,"accion",null);
		
		Date fechaDesde= null;
		try {
			fechaDesde = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		Date fechaHasta= null;
		try {
			fechaHasta = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaHastaMes) ) + 1)  + "/" + fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		session.removeAttribute(WebKeysUOMA.CTACTE_RESULT_EXPORT);
		try {	
			int id = 0;
			String tipoReporte = ""; 
			String titulo_periodo = "";
			int entidad = WebKeysGlobal.UOMA;
			String titulo_accion = "UOMA";
			if ("exp2".equals(accion)) {
				entidad = WebKeysGlobal.AMTIMA;
				titulo_accion = "AMTIMA";
			}
			List<CuentaCorriente> ctas = null;
			List<Informacion> list_info = new ArrayList<Informacion>();
						
			ctas = ContabilidadServiceUtil.cuentaCorrienteActasYConvenios(
					fechaDesde, fechaHasta, cuit, sucursal, 0, id,
					tipoReporte, entidad);
			if (StringUtils.checkNotEmpty(cuit) && ctas.size() == 0) {
				CuentaCorriente e = new CuentaCorriente();
				//DS - Agregado porque para las empresas sin moviviento no aparece la Razon Social en el listado 25/08/2020				
				Empresa emp = EmpresaServiceUtil.getEmpleadorCompleto(cuit, sucursal);
				e.setEmpresa(emp);			
				e.setInfo(new ArrayList<Informacion>());
				ctas.add(e);
			}
				
			List<EstadoInicialCuentaCorriente> saldoIni = null;
			saldoIni = ContabilidadServiceUtil
					.saldoInicialCorrienteActasYConvenios(cuit, sucursal,
							0, fechaDesde, entidad);
			
			List<SaldoInicial> saldosIniciales = new ArrayList<SaldoInicial>();			
			EstadoInicialCuentaCorriente est = new EstadoInicialCuentaCorriente();
			est.setEmpresa(ctas.get(0).getEmpresa());

			for (EstadoInicialCuentaCorriente estado : saldoIni) {
				if (estado.getEmpresa().getCuit()
						.equals(ctas.get(0).getEmpresa().getCuit())
						&&
					estado.getEmpresa().getSucursal().equals(ctas.get(0).getEmpresa().getSucursal())
					) {
					for (SaldoInicial saldoInicialEmpresa : estado
							.getSaldosIniciales()) {
						saldosIniciales.add(saldoInicialEmpresa);
					}
				}
				Collections.sort(saldosIniciales);
			}
			
			BigDecimal saldo = BigDecimal.ZERO;
			saldo = calcularRowSaldoInicial(saldosIniciales, fechaDesde,
					ctas.get(0).getEmpresa(), ctas.get(0).getInfo(), entidad);
			
			Informacion _info = ctas.get(0).new Informacion();
			_info.setPeriodo(fechaDesde);
			_info.setDebitoCredito("");
			_info.setDescripcion("Saldo Inicial Calculado");
			_info.setFecha(fechaDesde);
			_info.setImporte(saldo);
			list_info.add(_info);
			
			for (CuentaCorriente cta : ctas) {
				for (Informacion info : cta.getInfo()) { 
					
					if (info.getFecha().compareTo(fechaDesde) >= 0 && info.getFecha().compareTo(fechaHasta)<=0) {
						//Informacion _info = cta.new Informacion();					
						list_info.add(info);						
					}
				}
			}
			
			titulo_periodo = "Cuentas Corrientes - Desde: "; 
			titulo_periodo += "01" + "/" + ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio;
			titulo_periodo += " Hasta: "; 
			titulo_periodo += "01" + "/" + ((Integer.parseInt(fechaHastaMes) ) + 1)  + "/" + fechaHastaAnio;
			
			
			session.removeAttribute(WebKeysUOMA.CTACTE_RESULT_EXPORT);
		    session.removeAttribute(WebKeysUOMA.CTACTE_RESULT_EXPORT_SALDOINI);
		    session.removeAttribute(WebKeysUOMA.CTACTE_RESULT_TIT_PERIODO);
		    session.removeAttribute(WebKeysUOMA.CTACTE_RESULT_TIT_ACCION);
		    
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_EXPORT, list_info );
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_EXPORT_SALDOINI, saldo.toString());	
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TIT_PERIODO, titulo_periodo);	
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TIT_ACCION, titulo_accion);				
			
		} catch (Exception e) {
			_log.error("Error al generar Cuenta Corriente Empresa", e);
		}
	}
	
	private void procQueryCuentaCorriente(
			RenderRequest renderRequest,
			HttpSession session, 
			int vista) throws SystemException{
		
		String cuit=ParamUtil.getString(renderRequest,"cuit_entidad",null);
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		
		String fechaDesdeMes=ParamUtil.getString(renderRequest,"desde_mes",null);
		String fechaDesdeAnio=ParamUtil.getString(renderRequest,"desde_anio",null);
		String fechaHastaMes=ParamUtil.getString(renderRequest,"hasta_mes",null);
		String fechaHastaAnio=ParamUtil.getString(renderRequest,"hasta_anio",null);
		boolean procesarConsulta=ParamUtil.getBoolean(renderRequest,"procesar_consulta",false);
		int tipoBoleta=ParamUtil.getInteger(renderRequest,"tipo_boleta");
		int qrySoloUoma=ParamUtil.getInteger(renderRequest,"solo_uoma");
		int qrySoloAmtima=ParamUtil.getInteger(renderRequest,"solo_amtima");
		int qryConsolidado=ParamUtil.getInteger(renderRequest,"consolidado");
		String periodo=ParamUtil.getString(renderRequest,"periodo",null);
		
		int pagina =ParamUtil.getInteger(renderRequest, "pagina",1);
		
		BigDecimal total_saldo_ini = new BigDecimal(0);
		BigDecimal aux = new BigDecimal(0);
		
		int modo = 0;
		
		if (vista == 0) {
		  modo = qryConsolidado;	
		}	
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		Date fechaDesde= null;
		try {
			fechaDesde = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		Date fechaHasta= null;
		try {
			fechaHasta = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaHastaMes) ) + 1)  + "/" + fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		session.removeAttribute(WebKeysUOMA.CTACTE_RESULT);
		List<CuentaCorrienteEmpresa>cuentacorriente= CuentaCorrienteEmpresaServiceUtil.getCuentaCorriente(
				cuit, sucursal, fechaDesde, fechaHasta, procesarConsulta, modo, 
				tipoBoleta, qrySoloUoma, qrySoloAmtima,
				vista, periodo, pagina);
		/*
		for (int i = 0; i < cuentacorriente.size(); i++) {														
				
			if (i==0) {
				aux = new BigDecimal(cuentacorriente.get(i).getSaldoAnt());
				total_saldo_ini = total_saldo_ini.add(aux);
			}			
		}
		*/
		
		aux = new BigDecimal(0);
		total_saldo_ini = total_saldo_ini.add(aux);
		try {			
			aux = new BigDecimal(cuentacorriente.get(0).getSaldoAnt());
			total_saldo_ini = total_saldo_ini.add(aux);
		} catch (Exception e) {
		}
		
		try {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_CUIT,cuentacorriente.get(0).getCuit());
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_RAZSOC,cuentacorriente.get(0).getRazSoc());
		} catch (Exception e) {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_CUIT, "");
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_RAZSOC, "");
		}                      
		
		try {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_TIPOCTA,cuentacorriente.get(0).getCuentaNombre());
		} catch (Exception e) {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_TIPOCTA, "");
		}                      
		                                 
		session.setAttribute(WebKeysUOMA.CTACTE_RESULT,cuentacorriente);
		//session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT,Math.round(total * 100));
		//session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_DDJJ,Math.round(total_ddjj * 100));
		//session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_BOLETAS,Math.round(total_boletas * 100));

		if (cuentacorriente.size() > 1) {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT, cuentacorriente.get(0).getTotHdSaldo().toString());
		} else {
			session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT, cuentacorriente.get(0).getSaldo().toString());	
		}
		
		
		session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_DDJJ, cuentacorriente.get(0).getTotHdDdjj().toString());
		session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_BOLETAS, cuentacorriente.get(0).getTotHdBoletas().toString());
		session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_ACTAS, cuentacorriente.get(0).getTotHdActas().toString());
		session.setAttribute(WebKeysUOMA.CTACTE_RESULT_TOT_SALDO_INI, total_saldo_ini.toString());
		
		//Seteos Paginadaor
		Integer tRegistros=0;
		if(cuentacorriente!=null && !cuentacorriente.isEmpty()) {
			 tRegistros=cuentacorriente.get(0).getTotReg();
		}	

		session.removeAttribute(WebKeysUOMA.CTACTE_EMPRESAS_TOTAL_REGISTROS);
	    session.removeAttribute(WebKeysUOMA.CTACTE_EMPRESAS_OFFSET_REG);
		session.setAttribute(WebKeysUOMA.CTACTE_EMPRESAS_TOTAL_REGISTROS, tRegistros );
		session.setAttribute(WebKeysUOMA.CTACTE_EMPRESAS_OFFSET_REG, pagina);		
	}

	
}