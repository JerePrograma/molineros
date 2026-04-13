package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.postgresql.util.PSQLException;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.IntegracionReglasValidacion;
import ar.com.ospim.autorizaciones.beans.ReglaValidacion;
import ar.com.ospim.autorizaciones.beans.ReglaValidacionParametros;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.util.StringUtils;


public class IntegracionAction extends PortletAction {
	
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
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		Integer id = 0;
		String msg = "";
		List<String>errores = new ArrayList<String>();
		
		if (!StringUtils.checkEmpty(cmd)) {
			id = ParamUtil.getInteger(renderRequest,"id_lote", 0);
			
            if(cmd.equals(Constants.DELETE) ){ 
            	IntegracionServiceUtil.eliminaLote(id);
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_result");
			}

            if(cmd.equals("verifica_lote")){
            	
            	verificarLote(id,user.getScreenName());
            	
            	msg = "Finalizó verificación de Errores";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_result");
			}
            
            if(cmd.equals("envio_ftp")){
            	return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_sss_result");
            }
            
            if(cmd.equals("liquidar_lote")){
            	liquidarLote(id,user.getScreenName());
            	
            	msg = "Finalizó Liquidación del Lote";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_sss_result");
			}
            
            if(cmd.equals("liquidar_lote_cabecera")){
            	try {
            	   liquidarLoteCab(id,user.getScreenName());
            	   msg = "Finalizó Liquidación del Lote";
				   SessionMessages.add(renderRequest, "insertCabOk");
				   renderRequest.setAttribute("msgCabOk", msg);
            	}catch(PSQLException e ) { 
            		String err= e.getMessage();
            		if(e.getMessage().indexOf("pk_comprobante")>0) {
            		  err="Comprobante Existente ("+ err.substring(err.indexOf("=(")+2,err.indexOf(").")) +")";
            		} else {
            		  err=err.substring(0,err.length()>80?80:err.length()-1);	
            		}
            		errores.add(err);
            		renderRequest.setAttribute("errores", errores);
            	}catch(Exception e ) {
            		String err= e.getMessage();
            		if(e.getMessage().indexOf("Socket")>0) {
            			err="Demasiados Registros ERROR de memoria";
            		} else {
              		  err=err.substring(0,err.length()>80?80:err.length()-1);	
              		}
            		
            		errores.add(err);
            		renderRequest.setAttribute("errores", errores);
            	}
				  
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_sss_result_cab");
			}
            
            if(cmd.equals("cerrar_lote")){
            	cerrarLote(id);
            	
            	msg = "Finalizó Cierre del Período";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo_sss_result");
			}
            
            if(cmd.equals("historico_lote")){
            	historicoLote(id,user.getScreenName());
            	
            	msg = "Finalizó el Pasaje a Historico del Período";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_procesa_archivo");
			}
            
            if(cmd.equals("impresion_op_lote")){
            	return mapping.findForward("portlet.autorizaciones.integracion_prepara_impresion_op");
			}
                    
            if(cmd.equals("impresion_op_lote_cabecera")){
            	return mapping.findForward("portlet.autorizaciones.integracion_prepara_impresion_op_cabecera");
			}
            
            if(cmd.equals("detalle_liquidacion_op_lote")){
            	return mapping.findForward("portlet.autorizaciones.integracion_detalle_liquidacion_op");
			}
            
            if(cmd.equals("detalle_liquidacion_op_lote_cabecera")){
            	return mapping.findForward("portlet.autorizaciones.integracion_detalle_liquidacion_op_cabecera");
			}
            
            if(cmd.equals("aviso_op_transferencia")){
            	return mapping.findForward("portlet.autorizaciones.integracion_op_aviso_transferencia");
			}
            if(cmd.equals("ingreso_recibo_op")){
            	return mapping.findForward("portlet.autorizaciones.integracion_op_ingreso_recibo");
			}
			if(cmd.equals("genera_aviso_transferencia_lote") ){
				Integer opDesde = ParamUtil.getInteger(renderRequest,"op_desde", 0);
				Integer opHasta = ParamUtil.getInteger(renderRequest,"op_hasta", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"id_lote", 0);
				if(opDesde>0 && opHasta>0) {
					generaAvisoTransferencia(opDesde,opHasta);
				}
				renderRequest.setAttribute("nrolote", idLote);
				return mapping.findForward("portlet.autorizaciones.integracion_op_aviso_transferencia");
			}
			if(cmd.equals("genera_archivo_transferencia_lote_interbanking") ){
				Integer opDesde = ParamUtil.getInteger(renderRequest,"op_desde", 0);
				Integer opHasta = ParamUtil.getInteger(renderRequest,"op_hasta", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"id_lote", 0);
				/*
				if(opDesde>0 && opHasta>0) {
					generaArchivoTransferenciaInterbanking(opDesde,opHasta);
					TxtServlet txt = new TxtServlet();
					
					
				}
				*/
				renderRequest.setAttribute("nrolote", idLote);
				return mapping.findForward("portlet.autorizaciones.integracion_op_aviso_transferencia");
			}
			if(cmd.equals("asociar_recibo_integracion") ){
				Integer opDesde = ParamUtil.getInteger(renderRequest,"op_desde", 0);
				String nroRecibo = ParamUtil.getString(renderRequest,"nro_recibo");
				Integer idLote = ParamUtil.getInteger(renderRequest,"id_lote", 0);
				if(opDesde>0 && nroRecibo!=null) {
				   IntegracionServiceUtil.asociarRecibo(opDesde, nroRecibo);
				}
				renderRequest.setAttribute("nrolote", idLote);
				return mapping.findForward("portlet.autorizaciones.integracion_op_ingreso_recibo");
			}
			
			if(cmd.equals("ajustes_lote")){
            	return mapping.findForward("portlet.autorizaciones.integracion_ajustes");
			}
			
			if(cmd.equals("debito_add") ){
				Integer idCpte = ParamUtil.getInteger(renderRequest,"idcpte", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"idLote", 0);
				Double debito = ParamUtil.getDouble(renderRequest,"debito");
				String motivo = ParamUtil.getString(renderRequest,"motivo");
				
				IntegracionServiceUtil.agregarDebito(idCpte, debito, motivo);
				renderRequest.setAttribute("nrolote", idLote);
				return mapping.findForward("portlet.autorizaciones.integracion_ajustes_debitos_result");
			}
			
			if(cmd.equals("debito_delete") ){
				Integer idCpte = ParamUtil.getInteger(renderRequest,"idcpte", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"idLote", 0);
				IntegracionServiceUtil.eliminarDebito(idCpte);
				renderRequest.setAttribute("nrolote", idLote);
				return mapping.findForward("portlet.autorizaciones.integracion_ajustes_debitos_result");
			}
			
			if(cmd.equals("generar_devolucion")){
				Integer periodo = ParamUtil.getInteger(renderRequest,"periodo", 0);
            	generarDevolucion(periodo,user.getScreenName());
            	
            	msg = "Finalizó generación de devolución";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_rendicion_result");
			}
			
			
			if(cmd.equals("editar_devolucion_periodo")){
				Integer periodo = ParamUtil.getInteger(renderRequest,"periodo", 0);
				//id = ParamUtil.getInteger(renderRequest,"id", 0);
				//Integer offset = ParamUtil.getInteger(renderRequest,"offset_reg", 0);
				int pagina =0;//ParamUtil.getInteger(renderRequest, "pagina");  
				IntegracionDetalleDR filtro =new IntegracionDetalleDR();
				filtro.setSoloErrores(false);
				filtro.setPeriodoPresentacion(periodo);
				filtro.setId(null);
				int totalrecords=0;	
				List<IntegracionDetalleDR> busqueda= IntegracionServiceUtil.traeListaDetalleDR(pagina,filtro);
				List<IntegracionDetalleDR> list = new ArrayList<IntegracionDetalleDR>(); 
			    if (busqueda.size()>0){
					totalrecords = busqueda.size();
					for(int i=0;i< (busqueda.size()>50?50:busqueda.size());i++) {
						list.add(busqueda.get(i));
					}
				}else{
					totalrecords =0;
				}
			    portletSession.removeAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO);
				portletSession.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO,	list);
				session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_TOTAL_REGISTROS, totalrecords );
				session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_OFFSET_REG, pagina);
				return mapping.findForward("portlet.autorizaciones.integracion_devolucion");
			}
			
			if(cmd.equals("editar_devolucion_periodo_pagina")){
				Integer periodo = ParamUtil.getInteger(renderRequest,"periodo", 0);
				int pagina =ParamUtil.getInteger(renderRequest, "pagina_sel"); 
				String clave = ParamUtil.getString(renderRequest,"clave");
				boolean conError = ParamUtil.getBoolean(renderRequest,"conError");
				Integer prestacion = ParamUtil.getInteger(renderRequest,"prestacion");
				String cuit = ParamUtil.getString(renderRequest,"cuit");
				String cuil = ParamUtil.getString(renderRequest,"cuil");
				
				clave="".equalsIgnoreCase(clave)?null:clave;
				cuit="".equalsIgnoreCase(cuit)?null:cuit;
				prestacion=prestacion==0?null:prestacion;
				cuil="".equalsIgnoreCase(cuil)?null:cuil;
				
				IntegracionDetalleDR filtro =new IntegracionDetalleDR();
				filtro.setSoloErrores(conError);
				filtro.setPeriodoPresentacion(periodo);
				filtro.setId(null);
				filtro.setPrestacionCodigo(prestacion);
				filtro.setClave(clave);
				filtro.setCuitPrestador(cuit);
				filtro.setCuil(cuil);
				int totalrecords=0;
				
				if(conError) pagina=0; // Agregado para que por los Errores siempre traiga todos y despues pagine 
				List<IntegracionDetalleDR> busqueda= IntegracionServiceUtil.traeListaDetalleDR(pagina,filtro);
				List<IntegracionDetalleDR> list = new ArrayList<IntegracionDetalleDR>();
				List<IntegracionDetalleDR> listConError = new ArrayList<IntegracionDetalleDR>();
				
				if(!conError) {
				   if(pagina==0) {
					totalrecords=0;
					if (busqueda.size()>0){
						totalrecords = busqueda.size();
						for(int i=0;i< (busqueda.size()>50?50:busqueda.size());i++) {
							list.add(busqueda.get(i));
						}
					}else{
						totalrecords =0;
					}
					session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_TOTAL_REGISTROS, totalrecords );
					busqueda=list;
				  }
				} else {
					totalrecords = 0;
					pagina =ParamUtil.getInteger(renderRequest, "pagina_sel");
					for(int i=0;i< busqueda.size();i++) {
						if(busqueda.get(i).isConProblema()) {
							totalrecords++;
							listConError.add(busqueda.get(i));
						}
					}
					session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_TOTAL_REGISTROS, totalrecords );
					
					 
					int ini=pagina*50-50>0?pagina*50-50:0;
					int fin=ini+50<=listConError.size()?ini+50:listConError.size();
					
					for(int i=ini;i< fin;i++) {
						list.add(listConError.get(i));
					}
					
					busqueda=list;
					
				}
				
				portletSession.removeAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO);
				portletSession.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_FILTRO,busqueda);
				session.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_OFFSET_REG, pagina);
				return mapping.findForward("portlet.autorizaciones.integracion_devolucion_pagina");
			}
			
			
			if(cmd.equals("editar_registro_dr")){
				portletSession.removeAttribute("Success");
				Integer idReg = ParamUtil.getInteger(renderRequest,"nroRegistro", 0);
				IntegracionDetalleDR registro= new IntegracionDetalleDR();
				IntegracionDetalleDR filtro =new IntegracionDetalleDR();
				filtro.setId(idReg);
				List<IntegracionDetalleDR> busqueda= IntegracionServiceUtil.traeListaDetalleDR(null,filtro);
				if (busqueda.size()>0){
				   registro=busqueda.get(0);
				}
				portletSession.removeAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION);
				portletSession.setAttribute(WebKeysAutorizaciones.INTEGRACION_DEVOLUCION_REGISTRO_EN_EDICION,registro);
				return mapping.findForward("portlet.autorizaciones.integracion_devolucion_registro");
			}
			if(cmd.equals("update")){
				Integer idDetalle = ParamUtil.getInteger(renderRequest,"idDetalle", 0);
				String cuit= ParamUtil.getString(renderRequest, "cuit");
				String cbu= ParamUtil.getString(renderRequest, "cbu");
				Integer ordenI = ParamUtil.getInteger(renderRequest,"ordenI");
				Integer ordenII = ParamUtil.getInteger(renderRequest,"ordenII");
				
				
				String fechaTransIDia = ParamUtil.getString(renderRequest,"fechaTransIDia");
				String fechaTransIMes = ParamUtil.getString(renderRequest,"fechaTransIMes");
				String fechaTransIAnio = ParamUtil.getString(renderRequest,"fechaTransIAnio");
				Date fechaTransI = null;
				try {
					fechaTransI = formatoDeFechas.parse(fechaTransIDia + "/"
							+ (Integer.parseInt(fechaTransIMes) + 1) + "/"
							+ fechaTransIAnio);
				} catch (Exception e) {
					fechaTransI = null;
				}
				
				
				String fechaTransIIDia = ParamUtil.getString(renderRequest,"fechaTransIIDia");
				String fechaTransIIMes = ParamUtil.getString(renderRequest,"fechaTransIIMes");
				String fechaTransIIAnio = ParamUtil.getString(renderRequest,"fechaTransIIAnio");
				Date fechaTransII = null;
				try {
					fechaTransII = formatoDeFechas.parse(fechaTransIIDia + "/"
							+ (Integer.parseInt(fechaTransIIMes) + 1) + "/"
							+ fechaTransIIAnio);
				} catch (Exception e) {
					fechaTransII = null;
				}
				
				String cheque = ParamUtil.getString(renderRequest, "cheque");
				Double importeTransferido = ParamUtil.getDouble(renderRequest,"importeTransferido");
				Double retGcias = ParamUtil.getDouble(renderRequest,"retGcias");
				Double retIIBB = ParamUtil.getDouble(renderRequest,"retIIBB");
				Double otrasRet = ParamUtil.getDouble(renderRequest,"otrasRet");
				Double importeAplicado = ParamUtil.getDouble(renderRequest,"importeAplicado");
				Double importeFondosPropios = ParamUtil.getDouble(renderRequest,"importeFondosPropios");
				Double importeOtraCuenta = ParamUtil.getDouble(renderRequest,"importeOtraCuenta");
				Integer recibo = ParamUtil.getInteger(renderRequest, "recibo");
				Double importeTrasladado = ParamUtil.getDouble(renderRequest,"importeTrasladado");
				Double importeDevuelto = ParamUtil.getDouble(renderRequest,"importeDevuelto");
				Double importeNoAplicado = ParamUtil.getDouble(renderRequest,"importeNoAplicado");
				Double importeRecupero = ParamUtil.getDouble(renderRequest,"importeRecupero");
				String observaciones=ParamUtil.getString(renderRequest,"observaciones");
				
				IntegracionDetalleDR detalle = new IntegracionDetalleDR();
				detalle.setId(idDetalle);
				detalle.setCbu(cbu);
				detalle.setCbuCuit(cuit);
				detalle.setCheque(cheque);
				detalle.setFechaTransferenciaI(fechaTransI);
				detalle.setFechaTransferenciaII(fechaTransII);
				detalle.setFondosPropiosDiscapacidad(importeFondosPropios);
				detalle.setFondosPropiosOtraCuenta(importeOtraCuenta);
				detalle.setImporteAplicado(importeAplicado);
				detalle.setImporteDevuelto(importeDevuelto);
				detalle.setImporteTransferido(importeTransferido);
				detalle.setImporteTrasladado(importeTrasladado);
				detalle.setNroRecibo(recibo);
				detalle.setObservaciones(observaciones);
				detalle.setOrdenPagoI(ordenI);
				detalle.setOrdenPagoII(ordenII);
				detalle.setOtrasRetenciones(otrasRet);
				detalle.setRecuperoFondosPropios(importeRecupero);
				detalle.setRetencionGanancias(retGcias);
				detalle.setRetencionIIBB(retIIBB);
				detalle.setSaldoNoAplicado(importeNoAplicado);
				IntegracionServiceUtil.updateDetalleDR(detalle,user.getScreenName(),false);
				if (SessionErrors.isEmpty(renderRequest)) {
					portletSession.setAttribute("Success", "Success");
				}
				return mapping.findForward("portlet.autorizaciones.integracion_devolucion_registro");
			}
			
			if(cmd.equals("eliminar_rendicion_periodo")){
				Integer periodo = ParamUtil.getInteger(renderRequest,"periodo", 0);
				IntegracionServiceUtil.eliminarRendicionPeriodo(periodo);
            	
            	msg = "Finalizó la eliminación del Período";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
				 return mapping.findForward("portlet.autorizaciones.integracion_rendicion");
			}
			
			if(cmd.equals("cerrar_periodo_rendicion")){
				Integer periodo = ParamUtil.getInteger(renderRequest,"periodo", 0);
				IntegracionServiceUtil.cerrarRendicionPeriodo(periodo);
            	
            	msg = "Finalizó Cierre del Período";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.autorizaciones.integracion_rendicion_result");
			}
			
			if(cmd.equals("excluir_liquidacion") ){
				Integer idCpte = ParamUtil.getInteger(renderRequest,"idcpte", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"idLote", 0);
				IntegracionServiceUtil.excluirLiquidacion(idCpte);
				renderRequest.setAttribute("nrolote", idLote);
				msg = "Se excluyó el comprobante del proceso de liquidacion";
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				return mapping.findForward("portlet.autorizaciones.integracion_ajustes_debitos_result");
			}
			
			if(cmd.equals("incluir_liquidacion") ){
				Integer idCpte = ParamUtil.getInteger(renderRequest,"idcpte", 0);
				Integer idLote = ParamUtil.getInteger(renderRequest,"idLote", 0);
				IntegracionServiceUtil.incluirLiquidacion(idCpte);
				renderRequest.setAttribute("nrolote", idLote);
				msg = "Se incluyó el comprobante del proceso de liquidacion";
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				return mapping.findForward("portlet.autorizaciones.integracion_ajustes_debitos_result");
			}
			
		}
		return mapping.findForward("portlet.autorizaciones.integracion_editar");
		
	}
	
	
	private void verificarLote(Integer id,String screenName) throws Exception {
			List<IntegracionDetalleDS> lista = IntegracionServiceUtil.detalleDS_Errores_By_IdLote(id);
			
			
			Map<String,IntegracionCabeceraDS> presNoSolas= new HashMap<String,IntegracionCabeceraDS>();
			String prestacionesInvalidasSolas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_PRESTACIONES_INVALIDAS_SOLAS");
			String codError="";
			IntegracionReglasValidacion reglas = IntegracionServiceUtil.getReglasValidacion();
			
//Nuevas validaciones reglas para buscar dentro del mismo archivo
			String prestacionesInconsistentesReglas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_PRESTACIONES_INCONSISTENTES_REGLAS");
			String[] reglasValidar = prestacionesInconsistentesReglas.split(";");
			String codPrestacion="";
			String codPrestacionesInconsistentes="";
			List <String> listReglas = new ArrayList<String>();
			Map<String,IntegracionCabeceraDS> presInconsistentes= new HashMap<String,IntegracionCabeceraDS>();
        	for(int xi=0; xi<=reglasValidar.length-1; xi++) {
        	   for(ReglaValidacion r:reglas.getReglas()) {
        		   if(reglasValidar[xi].equalsIgnoreCase(r.getId())) {
        			   for(ReglaValidacionParametros p: r.getParametros()) {
        				   if("__prestaciones".equalsIgnoreCase(p.getNombre())) {
        					   codPrestacion = p.getValor();
        				   }
        				   if("__prestaciones_in".equalsIgnoreCase(p.getNombre())) {
        					   codPrestacionesInconsistentes = p.getValor();
        				   }
        			   }
        			   listReglas.add(r.getId()+"-"+codPrestacion+"-"+codPrestacionesInconsistentes);
        		   }
        	   }
        	}
//Fin nueva validacion			
			
			for(IntegracionDetalleDS d:lista) {
				codError = IntegracionServiceUtil.validaDetalle(d,false,reglas);
				d.setError(codError);
				IntegracionServiceUtil.updateErrorDetalleDS(d, screenName);
				
				if("".equalsIgnoreCase(codError) || "OK".equalsIgnoreCase(codError)){
				    int resultado = prestacionesInvalidasSolas.indexOf(d.getPrestacionCodigo());
			        if(resultado != -1) {
			            presNoSolas.put(d.getCuil()+";" + d.getPeriodoPrestacion().toString(), new IntegracionCabeceraDS());
			        }
//Nueva validacion				        	
			       	for(String cad:listReglas){
			       		String[] vcad = cad.split("-");
			       		resultado = vcad[1].indexOf(d.getPrestacionCodigo());
					    if(resultado != -1) {
					       presInconsistentes.put(d.getCuil()+";" + d.getPeriodoPrestacion().toString()+";"+vcad[0], new IntegracionCabeceraDS());
					    }
			       	}
//Fin nueva validacion		
			    }
			}
			
			
			// Valido existencia de otras prestaciones
			  if(prestacionesInvalidasSolas.length()>0) {
				  for(IntegracionDetalleDS s:lista){
					  IntegracionCabeceraDS c = presNoSolas.get(s.getCuil()+";" + s.getPeriodoPrestacion().toString());
					  if(c!=null) {
					    c.getItems().add(s);
					    presNoSolas.put(s.getCuil()+";" + s.getPeriodoPrestacion().toString(),c);
					  }  
				  }
				  
				  for (Map.Entry<String, IntegracionCabeceraDS> entry : presNoSolas.entrySet()) {
					    String key = entry.getKey();
					    IntegracionCabeceraDS value = entry.getValue();
					    boolean ret =false;
					    for(IntegracionDetalleDS d:value.getItems()) {
					    	int resultado = prestacionesInvalidasSolas.indexOf(d.getPrestacionCodigo());
					        if(resultado == -1) {
					        	ret=true;
					        	break;
					        }
					    }
					    if(!ret) {
					    	for(IntegracionDetalleDS d:lista) {
					    		if(key.equalsIgnoreCase(d.getCuil()+";"+d.getPeriodoPrestacion().toString())) {
					    			d.setError("PS");
					    			IntegracionServiceUtil.updateErrorDetalleDS(d, screenName);
					    		}
					    	}
					    }
				  }
			  }
			  
			  
			//Nueva validaciones
			  if(!presInconsistentes.isEmpty()) {
			      for(IntegracionDetalleDS d:lista) {
			    		
			    		for(String cad:listReglas){  
				        	  String[] vcad = cad.split("-"); 
						      IntegracionCabeceraDS c = presInconsistentes.get(d.getCuil()+";" + d.getPeriodoPrestacion().toString()+";"+vcad[0]);
						      if(c!=null) {
						    	  Integer cod=Integer.parseInt(d.getPrestacionCodigo());
						    	  int resultado = vcad[2].indexOf(cod.toString().trim());
							      if(resultado != -1) {
						             d.setError("IC");
						             IntegracionServiceUtil.updateErrorDetalleDS(d, screenName);
							      }   
						      }
			    	    }
			      }	
			  }
// fin nueva validaciones		
			  
	}
	
	private void liquidarLote(Integer id,String screenName) throws Exception {
		IntegracionServiceUtil.liquidarLoteSSS(id);
    }
	
	private void liquidarLoteCab(Integer id,String screenName) throws Exception {
		IntegracionServiceUtil.liquidarLoteSSSCab(id);
    }
	
	private void cerrarLote(Integer id) throws Exception {
		IntegracionServiceUtil.cerrarLoteSSS(id);
    }
	
	private void historicoLote(Integer id,String usr) throws Exception {
		IntegracionServiceUtil.historicoLoteSSS(id,usr);
    }
	
	private void generaAvisoTransferencia(Integer opDesde,Integer opHasta) throws Exception {
			
		for(Integer idOp=opDesde;idOp<=opHasta;idOp++) {
			
		  if(!IntegracionServiceUtil.existeAvisoTransferencia(idOp) ) {
			
			_log.debug("ORDEN PAGO A ENVIAR AVISO DE TRANSFERENCIA: " + idOp);
			
			OrdenPagoOspim op =OrdenPagoServiceUtil.getOrdenPagoOspim(idOp);
			
			String[] razonDestino = OrdenPagoServiceUtil.getUltimaRazonSocialChequeYDestinoOP(op.getCuit(),"000", null, 2); //seccional 0 no va mas... SVA 02/10/2019
			String email=razonDestino[3];
			
			if(StringUtils.checkEmpty(email)) {
				email = "sistemas@ospim.org.ar";
			}
//			String email="dsulfaro@uoma.org.ar";
		
			_log.debug(email);
			if(null!=op.getCBUTransferencia()){
				_log.debug("Por enviar Reporte de OP .pdf a " + op.getEmailCBU());
				ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
				PdfServlet pdfServlet=new PdfServlet();
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("ID_ORDEN_PAGO",String.valueOf(op.getId()));		
				byte[] pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.ORDEN_PAGO_OSPIM, hm, PdfServlet.ORDEN_PAGO_OSPIM_PDF_FILENAME);
				_log.debug("Reporte de OP "+String.valueOf(op.getId())+".pdf size:"+pdfOp.length);
				pdfs.add(pdfOp);
				hm=new HashMap<String, String>();
				hm.put("id_op_p", String.valueOf(op.getId()));		
				hm.put("entidad_p", String.valueOf(WebKeysGlobal.OSPIM));		
				byte[] pdfRet=pdfServlet.crearPdfComoAdjunto(PdfServlet.COMPROBANTE_RETEN_GANANCIAS, hm, PdfServlet.COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME);
				
				if(null!=pdfRet && pdfRet.length>914){  // con 914 sale en blanco el reporte... mayor q 914 hay algo...
					_log.debug("Reporte de Ret.Gcias "+String.valueOf(op.getId())+".pdf size:"+pdfRet.length);
					pdfs.add(pdfRet);
				}	
				
				if(StringUtils.checkNotEmpty(op.getLiquidacionesListAsString())){
					byte[] pdfDeb=pdfServlet.crearPdfsNotaDebito(op.getLiquidacionesListAsString());
					if(null!=pdfDeb){
						pdfs.add(pdfDeb);
					}
				}
				

//email="dsulfaro@uoma.org.ar";	
				
				List<String> emailCCO;
        		String destinos;
           		
        		emailCCO = new ArrayList<String>();
        		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_INTEGRACION_CCO");
        		String[] auxDestinos = destinos.split(";");
        		for (String to : auxDestinos) {
        			emailCCO.add(to);
        		}
				

				if(StringUtils.checkNotEmpty(email) ){
					OrdenPago.enviarMailTransferenciaIntegracion(op.getCuit(),op.getCBUTransferenciaIntegracion(), email,emailCCO, pdfs);
 				    IntegracionServiceUtil.avisoTransferenciaOP(idOp);
				}else{
					_log.error("No se encontró el destinatario de correo para enviar comprobantes por pdf para la OP: " + String.valueOf(op.getId()) );
				}

			}	
		}
	  }	
	}
	
	private void generarDevolucion(Integer periodo,String screenName) throws Exception {
		IntegracionServiceUtil.generarDevolucion(periodo,screenName);
    }
	
}
