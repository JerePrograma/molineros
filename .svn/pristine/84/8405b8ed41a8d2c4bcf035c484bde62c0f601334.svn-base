package ar.com.ospim.estudioisidro.action;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.AutoPrestacional;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.DemandaJudicial;
import ar.com.ospim.estudioisidro.service.DemandaJudicialServiceUtil;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;
import jcifs.smb.FileEntry;


public class DemandaJudicialAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	private PlanServiceUtil planService = new PlanServiceUtil();
	List<String>erroresActas = new ArrayList<String>();
	List<String>erroresConvenios = new ArrayList<String>();
	List<String>erroresCheques = new ArrayList<String>();

	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");
		
		
		User user = PortalUtil.getUser(actionRequest);
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			HttpServletRequest req =  PortalUtil.getHttpServletRequest(actionRequest);
			
			
			if (cmd.equals(Constants.MOVE) && esDatosTab){  
				DemandaJudicial demanda = (DemandaJudicial) session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION );
				actualizaDemanda(demanda,PortalUtil.getHttpServletRequest(actionRequest));
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
			}
		}	
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		if(cmd==null || "".equals(cmd)) {
			cmd = ParamUtil.getString(renderRequest, "accion", null);
		}
		
		DemandaJudicial demanda=null;
		Integer idDemanda = 0;
		String msg = "";
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		
		if (!StringUtils.checkEmpty(cmd)) {
			idDemanda = ParamUtil.getInteger(renderRequest,"id_demanda", 0);
			if(cmd.equals(Constants.WRITE) ){ 
				
				demanda = new DemandaJudicial();
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar"));
			}
			
            if(cmd.equals("buscarActa") ){ 
            	erroresActas.clear();
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				String actaNro=ParamUtil.getString(renderRequest,"acta",null);
				String cuit=ParamUtil.getString(renderRequest,"cuit",null);
				String entidad=ParamUtil.getString(renderRequest,"entidad",null);
				List<Acta> actas = new ArrayList<Acta>();
				if("O".equalsIgnoreCase(entidad)) {
				   actas=  ActaServiceUtil.getActas(actaNro.toUpperCase(), cuit, null);
				} else if("A".equalsIgnoreCase(entidad)) {
				   actas=  ActaNoOSServiceUtil.getActas("A.M.T.I.M.A.", actaNro.toUpperCase(), cuit, null, null);
					
				}else {
				   actas=  ActaNoOSServiceUtil.getActas("U.O.M.A.", actaNro.toUpperCase(), cuit, null, null);	
				}
				if(!actas.isEmpty()) {
					   Acta acta =null;
					   for(Acta a:actas) {
						   if(a.getNumero().equalsIgnoreCase(actaNro)) {
							  acta=a;
							  break;
						   }
					   }
					   if(acta!=null) {
				         Boolean existeActa= demanda.getActas().contains(acta);
				       
				         if(!existeActa) {
				            demanda.getActas().add(acta);
				         }
					   }else {
						 erroresActas.add("Acta no encontrada.");
		            	 renderRequest.setAttribute("erroresActas", erroresActas); 
					   }
				}else {
					
					erroresActas.add("Acta no encontrada.");
            		renderRequest.setAttribute("erroresActas", erroresActas);
				}
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				renderRequest.setAttribute("tab", tabSel);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_actas"));
			}
			
			
            if(cmd.equals("deleteActa") ){ 
				
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				Integer actaNro=ParamUtil.getInteger(renderRequest,"acta");
				Acta acta = new Acta();
				acta.setId(actaNro);
				demanda.getActas().remove(acta);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_actas"));
			}
            
            
            if(cmd.equals("buscarConvenio") ){ 
            	erroresConvenios.clear();
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				String actaNro=ParamUtil.getString(renderRequest,"convenio",null);
				String cuit=ParamUtil.getString(renderRequest,"cuit",null);
				String entidad=ParamUtil.getString(renderRequest,"entidad",null);
				List<Convenio> convenios = new ArrayList<Convenio>();
				if("O".equalsIgnoreCase(entidad)) {
				   convenios=  ConvenioServiceUtil.getConvenios(actaNro.toUpperCase(), cuit, null);
				} else if("A".equalsIgnoreCase(entidad)) {
				   convenios=  ConvenioNoOSServiceUtil.getConvenios(actaNro.toUpperCase(), cuit, null, "A.M.T.I.M.A.");
				}else {
				   convenios=ConvenioNoOSServiceUtil.getConvenios(actaNro.toUpperCase(), cuit, null, "U.O.M.A.");  
				}
				if(!convenios.isEmpty()) {
					   Convenio convenio =null;
					   for(Convenio a:convenios) {
						   if(a.getNumero().equalsIgnoreCase(actaNro)) {
							  convenio=a;
							  break;
						   }
					   }
					   if(convenio!=null) {
				         Boolean existeActa= demanda.getConvenios().contains(convenio);
				       
				         if(!existeActa) {
				          demanda.getConvenios().add(convenio);
				         }
					   }else {
						  erroresConvenios.add("Convenio no encontrada.");
		            	  renderRequest.setAttribute("erroresConvenios", erroresConvenios);  
					   }
				}else {
					
					erroresConvenios.add("Convenio no encontrada.");
            		renderRequest.setAttribute("erroresConvenios", erroresConvenios);
				}
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_convenios"));
			}
            
            if(cmd.equals("deleteConvenio") ){ 
				
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				Integer actaNro=ParamUtil.getInteger(renderRequest,"convenio");
				Convenio acta = new Convenio();
				acta.setId(actaNro);
				demanda.getConvenios().remove(acta);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_convenios"));
			}
            
            if(cmd.equals("buscarCheque") ){ 
            	erroresCheques.clear();
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				String chequeNro=ParamUtil.getString(renderRequest,"cheque",null);
				String cuit=ParamUtil.getString(renderRequest,"cuit",null);
				String banco=ParamUtil.getString(renderRequest,"banco",null);
				String entidad=ParamUtil.getString(renderRequest,"entidad",null);
				List<Cheque> cheques = new ArrayList<Cheque>();
				int ent=0;
				if("O".equalsIgnoreCase(entidad)) {
				   
				   ent=WebKeysGlobal.OSPIM;
				} else if("A".equalsIgnoreCase(entidad)) {
				   ent=WebKeysGlobal.AMTIMA;	
				}else {
				   ent=ent=WebKeysGlobal.UOMA;	
				}
					
				Cheque chequeFind= new Cheque();
				chequeFind.setCuit(cuit);
				chequeFind.setNumero(new BigDecimal(chequeNro));
				Banco b = new Banco(Integer.parseInt(banco));
				chequeFind.setBanco(b);
				cheques=ChequeServiceUtil.getCheques(chequeFind, ent) ;
				
				if(!cheques.isEmpty()) {
					Cheque cheque=cheques.get(0);
					Cheque chequeAux = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, ent);
					if(!chequeAux.getCuentaBancaria().getDescripcion().isEmpty()) {
					   cheque.getCuentaBancaria().setDescripcion(chequeAux.getCuentaBancaria().getDescripcion());
					}
					if(chequeAux.getBanco().getId_banco()!=0 && !chequeAux.getBanco().getDescripcion_banco().isEmpty()) {
					   cheque.getCuentaBancaria().setBanco(chequeAux.getBanco());
					}
					if(cheque!=null) {
				      Boolean existeActa= demanda.getCheques().contains(cheque);
				      if(!existeActa) {
				          demanda.getCheques().add(cheque);
				      }
					}else {
						erroresCheques.add("Cheque no encontrado.");
	            		renderRequest.setAttribute("erroresCheques", erroresCheques);	
					}
				}else {
					erroresCheques.add("Cheque no encontrado.");
            		renderRequest.setAttribute("erroresCheques", erroresCheques);
				}
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_cheques"));
			}
			
            if(cmd.equals("deleteCheque") ){ 
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				String chequeNro=ParamUtil.getString(renderRequest,"nro");
				String cuit=ParamUtil.getString(renderRequest,"cuit",null);
				Integer banc=ParamUtil.getInteger(renderRequest,"banco");
				Integer cta=ParamUtil.getInteger(renderRequest,"ctaBcria");
				Cheque cheque = new Cheque();
				cheque.setNumero(new BigDecimal(chequeNro));
				cheque.setCuit(cuit);
				Banco banco=new Banco(banc);
				cheque.setBanco(banco);
				CuentaBancaria cuenta = new CuentaBancaria(cta);
				cuenta.setBanco(banco);
				cheque.setCuentaBancaria(cuenta);
				demanda.getCheques().remove(cheque);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_cheques"));
			}
            
            
            if(cmd.equals("agregarEstado") ){ 
            	demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				String estadoId=ParamUtil.getString(renderRequest,"estado",null);
				String fechaDia = ParamUtil.getString(renderRequest,"dia");
				String fechaMes = ParamUtil.getString(renderRequest,"mes");
				String fechaAnio = ParamUtil.getString(renderRequest,"anio");
				String observacion=ParamUtil.getString(renderRequest,"observacion",null);
				
				Date fechaEstado = null;
				try {
					fechaEstado = formatoDeFechas.parse(fechaDia + "/"
							+ (Integer.parseInt(fechaMes) + 1) + "/"
							+ fechaAnio);
				} catch (Exception e) {	}
				
				double nAleatorio = (Math.random() * 1500) + 1;
				int nAleatorioEntero = (int) -nAleatorio;
				
				Estado estado = new Estado();
				estado.setId(estadoId);
				estado.setIdSerial(nAleatorioEntero);
				estado.setFecha(fechaEstado);
				estado.setObservacionesExternas(observacion);
					
				
				demanda.getEstados().add(estado);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_estados"));
			}
			
			
            if(cmd.equals("deleteEstado") ){ 
				
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				Integer id=ParamUtil.getInteger(renderRequest,"id");
				String fechaStr=ParamUtil.getString(renderRequest, "fecha");
				SimpleDateFormat formato = new SimpleDateFormat("yyyyMMdd");
				Date fecha =formato.parse(fechaStr);
				Estado estado = new Estado();
				List<Estado> es =new ArrayList<Estado>();
				for(Estado e:demanda.getEstados()) {
					if(e.getIdSerial()!=id && !e.getFecha().equals(fecha)) {
					  es.add(e); 	
					}
				}
				demanda.setEstados(es);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_estados"));
			}
            
            
            if(cmd.equals("agregarAsiento") ){ 
            	Asiento asi = new Asiento(0);
            	Detalle total = new Detalle();
            	total.setDebe(BigDecimal.ZERO);
            	total.setHaber(BigDecimal.ZERO);
            	asi.setDetalle(new ArrayList<Detalle>());
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION,asi);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES,total);
            	
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_asiento"));
		    }
            
            
            if(cmd.equals("agregarAsientoDetalle") ){ 
            	Asiento asiento = (Asiento)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION);
            	
				Integer cuentaId=ParamUtil.getInteger(renderRequest,"cuenta");
				String cuentaDesc = ParamUtil.getString(renderRequest,"cuentaDesc");
				Double debe = ParamUtil.getDouble(renderRequest,"debe_1");
				Double haber = ParamUtil.getDouble(renderRequest,"haber_1");
				
				Detalle detalle= new Detalle();
				PlanCuentas pc = new PlanCuentas();
				pc.setId(cuentaId);
				pc.setNumero(cuentaDesc);
				detalle.setCuenta(pc);
				detalle.setDebe(new BigDecimal(debe));
				detalle.setHaber(new BigDecimal(haber));
				detalle.setPase(asiento.getDetalle().size()+1);
				asiento.getDetalle().add(detalle);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION,asiento);
            	calculaTotales(asiento,session);
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_asiento_result"));
		    }
            
            
            if(cmd.equals("deleteAsientoDetalle") ){ 
            	Asiento asiento = (Asiento)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION);
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				Integer id=ParamUtil.getInteger(renderRequest,"id");
				Detalle detalle = new Detalle();
				List<Detalle> es =new ArrayList<Detalle>();
				for(Detalle e:asiento.getDetalle()) {
					if(e.getPase()!=id ) {
					  es.add(e); 	
					}
				}
				asiento.setDetalle(es);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION , asiento);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				calculaTotales(asiento,session);
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_asiento_result"));
			}
            
            
            if(cmd.equals("updateAsiento") ){ 
            	Asiento asiento = (Asiento)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION);
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				
				String fechaAsientoDia = ParamUtil.getString(renderRequest,"fechaAsientoDia");
				String fechaAsientoMes = ParamUtil.getString(renderRequest,"fechaAsientoMes");
				String fechaAsientoAnio = ParamUtil.getString(renderRequest,"fechaAsientoAnio");
				
				Date fechaAsiento = null;
				try {
					fechaAsiento = formatoDeFechas.parse(fechaAsientoDia + "/"
							+ (Integer.parseInt(fechaAsientoMes) + 1) + "/"
							+ fechaAsientoAnio);
				} catch (Exception e) {	}
				
				String descripcion= ParamUtil.getString(renderRequest,"descripcionAsiento");
				asiento.setFecha(fechaAsiento);
				asiento.setDescripcion(descripcion);
				
				if(asiento.getId()==0) {
				
				   Integer nro = DemandaJudicialServiceUtil.insertaAsiento(demanda.getId(),asiento, user.getScreenName());
				   asiento.setId(nro);
 				   demanda.getAsientos().add(asiento);
				}else {
				   DemandaJudicialServiceUtil.updateAsiento(demanda.getId(),asiento, user.getScreenName());
				   List <Asiento> aa = new ArrayList<Asiento>();
				   for(Asiento a:demanda.getAsientos()) {
					   if(a.equals(asiento)) {
						  aa.add(asiento);
					   }else {
						  aa.add(a);
					   }
				   }
				   demanda.setAsientos(aa);
				}
				asiento=new Asiento();
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				//Limpia Asiento para agregar uno nuevo
				Asiento asi = new Asiento(0);
            	Detalle total = new Detalle();
            	total.setDebe(BigDecimal.ZERO);
            	total.setHaber(BigDecimal.ZERO);
            	asi.setDetalle(new ArrayList<Detalle>());
            	
            	renderRequest.setAttribute("tab", tabSel);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES,total);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION , asi);
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
				
				SessionMessages.add(renderRequest, "insertCabOkAsi");
				renderRequest.setAttribute("msgCabOkAsi", "Registración contable actualizada con éxito");
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_asiento"));
			}
            
            if(cmd.equals("editarAsiento") ){
            	demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
            	Integer id=ParamUtil.getInteger(renderRequest, "asientoid");
            	Asiento asi = DemandaJudicialServiceUtil.getAsiento(demanda.getId(),id,demanda.getEntidad());
            	
            	Detalle total = new Detalle();
            	total.setDebe(BigDecimal.ZERO);
            	total.setHaber(BigDecimal.ZERO);
            	
            	BigDecimal totalDebe = BigDecimal.ZERO;
            	BigDecimal totalHaber= BigDecimal.ZERO;
            	
            	for(Detalle d:asi.getDetalle()) {
            		totalDebe=totalDebe.add(d.getDebe());
            		totalHaber=totalHaber.add(d.getHaber());
            	}
            	
            	total.setDebe(totalDebe);
            	total.setHaber(totalHaber);
            
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION,asi);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES,total);
            	
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar_asiento"));
		    }
            
            if(cmd.equals("deleteAsiento") ){ 
            	demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
            	Integer id=ParamUtil.getInteger(renderRequest, "asientoid");
            	DemandaJudicialServiceUtil.deleteAsiento(demanda.getId(),id);
            	List<Asiento>asis= DemandaJudicialServiceUtil.getAsientosByDemandaId(demanda.getId());
            	demanda.setAsientos(asis);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION,demanda);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
								
				return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_contabilidad_result"));
			}
            
            
            
            
            if(cmd.equals("buscar")){
				
		           filterDemandas(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.estudio_isidro.demandas.result.search"));	
			}
            
            if(cmd.equals(Constants.EDIT) ){
            	
            	demanda = DemandaJudicialServiceUtil.getDemandaById(idDemanda);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda_editar"));
			}

            if(cmd.equals(Constants.DELETE) ){
            	
            	DemandaJudicialServiceUtil.deleteDemanda(idDemanda, user.getScreenName());
            	DemandaJudicial filtro=(DemandaJudicial) session.getAttribute(WebKeysEstudioIsidro.DEMANDAS_FILTRO);
            	Integer pagina =(Integer) session.getAttribute(WebKeysEstudioIsidro.DEMANDAS_OFFSET_REG);
            	List<DemandaJudicial> lista = DemandaJudicialServiceUtil.getLista(filtro,pagina);
            	
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_RESULT,lista);
            	
            	//Seteos Paginadaor
            	Integer tRegistros=0;
            	if(lista!=null && !lista.isEmpty()) {
            		 tRegistros=lista.get(0).getTotalRegistros();
            	}	
            	session.removeAttribute(WebKeysEstudioIsidro.DEMANDAS_TOTAL_REGISTROS);
                session.removeAttribute( WebKeysEstudioIsidro.DEMANDAS_OFFSET_REG);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_TOTAL_REGISTROS, tRegistros );
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_OFFSET_REG, pagina);
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demandas.result.search"));	
            }

            if(cmd.equals("imagenes") ){
            	demanda = DemandaJudicialServiceUtil.getDemandaById(idDemanda);
            	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION , demanda);
            	return mapping.findForward(getForward(renderRequest,
						"portlet.estudio_isidro.demanda.imagenes_demanda"));
			}
            
            
            if(cmd.equals(Constants.MOVE)){
				String moverATab = ParamUtil.getString(renderRequest, "moverATab");
				String view = ParamUtil.getString(renderRequest, "view");
				renderRequest.setAttribute("view", view);
				tabSel = moverATab;
            }
            
            if(cmd.equals(Constants.UPDATE) ){
         	
				boolean validaOk = true;
				demanda = (DemandaJudicial)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
				actualizaDemanda(demanda,PortalUtil.getHttpServletRequest(renderRequest));
				if(demanda.getId()==0){ //Nuevo
					_log.debug("usuario demanda "  + user.getScreenName());
					idDemanda= insertDemanda(demanda,user.getScreenName());	
				   
				   demanda.setId((Integer) idDemanda);
				   
				   msg = "Inserta Demanda ";
					  msg = msg +" " +idDemanda;
					  SessionMessages.add(renderRequest, "insertCabOk");
					  renderRequest.setAttribute("msgCabOk", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id demanda: " + idDemanda
							);
					
				}else {
					
					_log.debug("usuario demanda "  + user.getScreenName());
					idDemanda= updateDemanda(demanda,user.getScreenName());	
				   
				   demanda.setId((Integer) idDemanda);
				   
				   msg = "Update Demanda ";
					  msg = msg +" " +idDemanda;
					  SessionMessages.add(renderRequest, "insertCabOk");
					  renderRequest.setAttribute("msgCabOk", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id demanda: " + idDemanda
							);
					
				}
				
				tabSel="datos";	
				session.setAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION, demanda);	
			 		
			}
			
			
		}
		renderRequest.setAttribute("tab", tabSel);
		return mapping.findForward("portlet.estudio_isidro.demanda_editar");
		
	}
	
	
private void actualizaDemanda(DemandaJudicial demanda,HttpServletRequest renderRequest) throws SystemException{
		
		String fechaDemandaDia = ParamUtil.getString(renderRequest,"fechaDemandaDia");
		String fechaDemandaMes = ParamUtil.getString(renderRequest,"fechaDemandaMes");
		String fechaDemandaAnio = ParamUtil.getString(renderRequest,"fechaDemandaAnio");
		
		Date fechaDemanda = null;
		try {
			fechaDemanda = formatoDeFechas.parse(fechaDemandaDia + "/"
					+ (Integer.parseInt(fechaDemandaMes) + 1) + "/"
					+ fechaDemandaAnio);
		} catch (Exception e) {	}
		
		String entidad= ParamUtil.getString(renderRequest,"entidad");
		
		String tipo= ParamUtil.getString(renderRequest,"tipoDemanda");
		
		Integer id=ParamUtil.getInteger(renderRequest, "nroDemanda");
		
		String expediente= ParamUtil.getString(renderRequest,"nroExpediente");
		String caratula= ParamUtil.getString(renderRequest,"caratula");
		String juzgado= ParamUtil.getString(renderRequest,"juzgado");
		String cuit= ParamUtil.getString(renderRequest,"cuit_entidad_dem");
		String sucursal= ParamUtil.getString(renderRequest,"sucursal_entidad_dem");
		String razonSoc= ParamUtil.getString(renderRequest,"entidad_dem");
		String importeStr=ParamUtil.getString(renderRequest, "importe");
		importeStr=importeStr.replace(",", ".");
		Double importe=Double.valueOf(importeStr);
		String observaciones=ParamUtil.getString(renderRequest,"observaciones",null);
		
		demanda.setFecha(fechaDemanda);
		demanda.setCaratula(caratula);
		demanda.setEntidad(entidad);
		demanda.setTipo(tipo);
		demanda.setId(id);
		demanda.setExpediente(expediente);
		demanda.setCaratula(caratula);
		demanda.setJuzgado(juzgado);
		demanda.setCuit(cuit);
		demanda.setSucursal(sucursal);
		demanda.setRazonSocial(razonSoc);
		demanda.setMontoOriginal(importe);
		demanda.setObservaciones(observaciones);
    }	
	
	
private Integer insertDemanda(DemandaJudicial demanda, String user) throws Exception{
	Integer id = 0;
	id = DemandaJudicialServiceUtil.insertaDemanda(demanda, user);
	return id;
}
	
private Integer updateDemanda(DemandaJudicial demanda, String user) throws Exception{
	Integer id = 0;
	id = DemandaJudicialServiceUtil.updateDemanda(demanda, user);
	return id;
}	
	


private void filterDemandas(RenderRequest renderRequest,HttpSession session) throws Exception{
	
	
	Integer id = ParamUtil.getInteger(renderRequest, "id");
	String tipo = ParamUtil.getString(renderRequest, "tipo");
	
	String fechaMesDde = ParamUtil.getString(renderRequest,
			"fechaMesDde");
	String fechaDiaDde = ParamUtil.getString(renderRequest,
			"fechaDiaDde");
	String fechaAnioDde = ParamUtil.getString(renderRequest,
			"fechaAnioDde");
	SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	Date fechaDde = null;
	try {
		fechaDde = formatoDeFecha.parse(fechaDiaDde
				+ "/" + (Integer.parseInt(fechaMesDde) + 1)
				+ "/" + fechaAnioDde);
	} catch (Exception e) {	}

	String fechaMesHta = ParamUtil.getString(renderRequest,
			"fechaMesHta");
	String fechaDiaHta = ParamUtil.getString(renderRequest,
			"fechaDiaHta");
	String fechaAnioHta = ParamUtil.getString(renderRequest,
			"fechaAnioHta");
	Date fechaHta = null;
	try {
		fechaHta = formatoDeFecha.parse(fechaDiaHta
				+ "/" + (Integer.parseInt(fechaMesHta) + 1)
				+ "/" + fechaAnioHta);
	} catch (Exception e) {}
	String entidad = ParamUtil.getString(renderRequest, "entidad");
	String expediente = ParamUtil.getString(renderRequest, "expediente");
	String caratula = ParamUtil.getString(renderRequest, "caratula");
	String estado=ParamUtil.getString(renderRequest,"estado",null);
	String cuit=ParamUtil.getString(renderRequest,"cuit",null);
	String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
	int pagina =ParamUtil.getInteger(renderRequest, "pagina",1);
	
	DemandaJudicial filtro = new DemandaJudicial();
	
	filtro.setId(id);
	filtro.setTipo(tipo);
	filtro.setFechaDde(fechaDde);
	filtro.setFechaHta(fechaHta);
	filtro.setEntidad(entidad);
	filtro.setExpediente(expediente);
	filtro.setCaratula(caratula);
	filtro.setCuit(cuit);
	filtro.setSucursal(sucursal);
	filtro.setUltimoEstado(estado);
	
	List<DemandaJudicial> lista = DemandaJudicialServiceUtil.getLista(filtro,pagina);
	
	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_FILTRO ,filtro);
	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_RESULT,lista);
	
	//Seteos Paginadaor
	Integer tRegistros=0;
	if(lista!=null && !lista.isEmpty()) {
		 tRegistros=lista.get(0).getTotalRegistros();
	}	

	session.removeAttribute(WebKeysEstudioIsidro.DEMANDAS_TOTAL_REGISTROS);
    session.removeAttribute( WebKeysEstudioIsidro.DEMANDAS_OFFSET_REG);
	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_TOTAL_REGISTROS, tRegistros );
	session.setAttribute(WebKeysEstudioIsidro.DEMANDAS_OFFSET_REG, pagina);
	
}

private void calculaTotales(Asiento asiento,HttpSession session) {
	Double totalD=0D;
	Double totalH=0D;
	
	for(Detalle d:asiento.getDetalle()) {
		totalD += d.getDebe().doubleValue();
		totalH += d.getHaber().doubleValue();
	}
	Detalle det = (Detalle)session.getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES);
	det.setDebe(new BigDecimal(totalD));
	det.setHaber(new BigDecimal(totalH));
	session.setAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES,det);
}
	
}