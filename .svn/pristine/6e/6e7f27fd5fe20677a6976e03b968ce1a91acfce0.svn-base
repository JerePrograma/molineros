package ar.com.uoma.facturacion.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.facturacion.exceptions.ImposibleObtenerCAEAFIPException;
import ar.com.ospim.facturacion.exceptions.ImposibleObtenerTokenAFIPLoginException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.BusquedaFacturasFiltro;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.LoginCmsResponse;
import ar.com.uoma.facturacion.afip_ws.AfipLoginCmsClient;
import ar.com.uoma.facturacion.afip_ws.AfipWSFEaxis2Client;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfErr;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfFECAEDetResponse;
import fev1.dif.afip.gov.ar.ServiceStub.ArrayOfObs;
import fev1.dif.afip.gov.ar.ServiceStub.Err;
import fev1.dif.afip.gov.ar.ServiceStub.FEAuthRequest;
import fev1.dif.afip.gov.ar.ServiceStub.FECAEDetResponse;
import fev1.dif.afip.gov.ar.ServiceStub.FECAEResponse;
import fev1.dif.afip.gov.ar.ServiceStub.Obs;


public class FacturacionAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	String cuit;
	File configDir = new File(System.getProperty("catalina.base"), "conf");
	File configFile = new File(configDir, "liferay_schedulers.properties");
	
//  private static final String cuit = "20181512831"; // MARCE p/ QA
//	private static final String cuit = "30531143856"; // UOMA p/ PRODUCCION
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		
		setForward(actionRequest, "portlet.uoma.facturacion_editar");
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		InputStream stream;
		try {
			stream = new FileInputStream(configFile);
			Properties props = new Properties();
			props.load(stream);
			cuit = props.getProperty("cuit_ws_afip");
		}catch(Exception e) {
			
			cuit="20181512831"; //cuit de Marce para QA
		}
		
		String portlet_name = null;
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			portlet_name = "farmacia";
		}else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			portlet_name = "uoma";
		}else if (renderResponse.getNamespace().equals("_HOT_1_")) {
			portlet_name = "hoteles";	
		}
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
//		EmpresaServiceUtil.getRegimenesRetencionGanancias(renderRequest);
		TraeListasServiceUtil.getBancos(renderRequest);
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		
		FacturacionServiceUtil.getProductos(renderRequest );
//		FacturacionServiceUtil.getClientes(renderRequest);
		
		Factura factura=null;
		Integer idFC = 0;
		
		
		if (StringUtils.checkNotEmpty(cmd)) {
			
			if ("reservas".equals(cmd)){
				
				Calendar fecha = CalendarFactoryUtil.getCalendar(); 		

				List<Reserva> reservas=HotelesServiceUtil.getReservasActivas(fecha.get(Calendar.YEAR), fecha.getTime());
				
				session.setAttribute(WebKeysHoteles.RESERVAS , reservas);

				
				 return mapping.findForward("portlet.ticket_reservas_habitacion");
				
			}
			
			idFC = ParamUtil.getInteger(renderRequest,"idFC", 0);
			
			if(cmd.equals(Constants.ADD) ){ 
				
				session.removeAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
				
				factura = new Factura();
				factura.setEstado(Factura.ESTADOS.ALTA);
								
				session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				
				
				renderRequest.setAttribute("esEdicion", "esEdicion");
				
				if("uoma".equalsIgnoreCase(portlet_name)) {
				    return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.facturacion_editar"));
				}
//				else {
//					return mapping.findForward(getForward(renderRequest,
//							"portlet.farmacia.facturacion_editar"));
//				}
			}
			
			if(cmd.equals("testws") ){
				factura=new Factura();
								
				String tipo = ParamUtil.getString(renderRequest,"tipo", null);
				String letra = ParamUtil.getString(renderRequest,"letra", null);
				String sucursal = ParamUtil.getString(renderRequest,"sucursal", null);
				factura.setSucursal(sucursal);	
				factura.setTipo(tipo);
				factura.setLetra(letra);
				
				AfipLoginCmsClient clienteWS = new AfipLoginCmsClient(user.getScreenName());
				
				LoginCmsResponse token = clienteWS.getTokenValido();
				FEAuthRequest autorization = null;

				if(token == null) {
					SessionErrors.add(renderRequest, ImposibleObtenerTokenAFIPLoginException.class.getName() );
					renderRequest.setAttribute("esEdicion", "esEdicion");
					return mapping.findForward(getForward(renderRequest,
							"portlet.uoma.facturacion_search_result"));
				}else {
					autorization = new FEAuthRequest();
					autorization.setToken(token.getToken());
					autorization.setSign(token.getSign());
					autorization.setCuit(Long.parseLong(cuit));
				}

				AfipWSFEaxis2Client wsfe = new AfipWSFEaxis2Client(factura, token);

				String nroFactura=wsfe.FEUltimaAutoriz(autorization, factura);
				
				_log.info("AFIP Ultimo nro Factura: " + nroFactura);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.facturacion_search_result"));
				
			}
			
            if(cmd.equals(Constants.SAVE) ){ 
				
				factura = (Factura) session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
				
				factura = getFacturaFromRequest(renderRequest, factura);
				
//				factura.recalcularImportes();
				
				boolean validaOK = true;
				
			   if (WebKeysGlobal.COMPROBANTE_NOTA_CREDITO.equalsIgnoreCase(factura.getTipo())){
					
				   List<Cliente> cliente =  FacturacionServiceUtil.getClientesPorAnio(factura.getCliente().getDocumentoNro(), factura.getCliente().getApellido(), factura.getCliente().getCuit()); 
				   
					if(cliente!=null && cliente.isEmpty()){
						SessionErrors.add(renderRequest, "error-cliente-sin-factura");
						validaOK=false;
					}
				   
				   
				   List<FacturaIngreso> facturas = new ArrayList<FacturaIngreso>();

				   for (FacturaDetalle detalle : factura.getDetalles()) {
						Efectivo efectivo = new Efectivo();
						efectivo.setImporte(detalle.getPrecio());
						efectivo.setFechaRecibo(new Date());
						efectivo.setFecha(new Date());
						FacturaIngreso fac = new FacturaIngreso();						
						fac.setIngreso(efectivo);
						facturas.add(fac);
						factura.setIngresos(facturas);	
				   }
				   
				}
				
				if(factura.getDetalles()==null || factura.getDetalles().isEmpty()){
					SessionErrors.add(renderRequest, "error-factura-sin-detalle");
					validaOK=false;
				}
				
				if(factura.getIngresos()==null || factura.getIngresos().isEmpty() ){
					SessionErrors.add(renderRequest, "error-factura-sin-fpago");
					validaOK=false;
				}
				BigDecimal totalFormaPagos = new BigDecimal(0);
				BigDecimal totalFormaPagosAux = null;
				BigDecimal totalAdelantos= new BigDecimal(0);
				
				for (Iterator<FacturaIngreso> iterator = factura.getIngresos().iterator(); iterator.hasNext();) {
					FacturaIngreso fi =  iterator.next();
					totalFormaPagosAux = totalFormaPagos; 
					totalFormaPagos =  totalFormaPagosAux.add(fi.getIngreso().getImporte());
				}
				
				if(factura.getRecibosAdelantos()!=null) {
				  for(Recibo r:factura.getRecibosAdelantos()) {
					totalAdelantos=totalAdelantos.add(BigDecimal.valueOf(r.getTotal()));
				  }
				}
				
				if(!(factura.getImporteTotal().compareTo(totalFormaPagos.add(totalAdelantos))==0)) {
					SessionErrors.add(renderRequest, "error-factura-total-fpago");
					validaOK=false;
				}
				
				if(!validaOK) {
					renderRequest.setAttribute("esEdicion", "esEdicion");
					return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_editar"));
				}
				
				if(!factura.isManual()) {
/*					
					AfipLoginCmsClient clienteWS = new AfipLoginCmsClient(user.getScreenName());
					
					LoginCmsResponse token = clienteWS.getTokenValido();
					FEAuthRequest autorization = null;

					if(token == null) {
						SessionErrors.add(renderRequest, ImposibleObtenerTokenAFIPLoginException.class.getName() );
						renderRequest.setAttribute("esEdicion", "esEdicion");
						return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_editar"));
					}else {
						autorization = new FEAuthRequest();
						autorization.setToken(token.getToken());
						autorization.setSign(token.getSign());
						autorization.setCuit(Long.parseLong(cuit));
					}
	
					AfipWSFEaxis2Client wsfe = new AfipWSFEaxis2Client(factura, token);
					factura.setNumero(wsfe.FEUltimaAutoriz(autorization, factura));
									
					FECAEResponse respuestaAfip = wsfe.FESolicitarCAE(autorization, factura);
					ArrayOfFECAEDetResponse detResp = respuestaAfip.getFeDetResp();
					
					if(respuestaAfip.isErrorsSpecified() || detResp.getFECAEDetResponse()[0] != null) {
						
	//					Errores
						ArrayOfErr arrayErrors = respuestaAfip.getErrors();
						if(arrayErrors!=null && arrayErrors.getErr()!=null && arrayErrors.getErr().length>0) {
							Err[] errores = arrayErrors.getErr();
							
							for (int i = 0; i < errores.length; i++) {
								_log.error(errores[i].getCode() );
								_log.error(errores[i].getMsg() );
							}
		//					Observaciones - tamb son bloqueantes
							ArrayOfObs arrayObs = detResp.getFECAEDetResponse()[0].getObservaciones();
							if(arrayObs!=null){
								_log.debug("Lista de Observaciones");
								Obs[] obs = arrayObs.getObs();
								if(obs!=null){
									for (int j = 0; j < obs.length; j++) {
										_log.debug(obs[j].getCode());
										_log.debug(obs[j].getMsg());
									}
								}
							}
						
						SessionErrors.add(renderRequest, ImposibleObtenerCAEAFIPException.class.getName() );
						}
					}
					
					FECAEDetResponse[] detalleRtaAfip = null;
					
					if(respuestaAfip.getFeDetResp()!=null) {
						detalleRtaAfip = respuestaAfip.getFeDetResp().getFECAEDetResponse();
						
						for (int i = 0; i < detalleRtaAfip.length; i++) {
							if(StringUtils.checkNotEmpty(detalleRtaAfip[i].getCAE())) {
								factura.setCae(detalleRtaAfip[i].getCAE());
							}else {
								SessionErrors.add(renderRequest, ImposibleObtenerCAEAFIPException.class.getName() );
								renderRequest.setAttribute("esEdicion", "esEdicion");
								return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_editar"));
							}
							try{
								factura.setFechaCae(sdf.parse(detalleRtaAfip[i].getCAEFchVto()));
							}catch(ParseException e){
								_log.error(e);
							}
							_log.debug(detalleRtaAfip[i].getResultado());
						}
						
					}
*/					
				}
				
				
				int idFacturaNueva = FacturacionServiceUtil.saveFactura(factura, user.getScreenName());
//				String numeroFacturaSeucursalLetra = FacturacionServiceUtil.saveFactura(factura, user.getScreenName());
//				
////				factura.setCae(cae);
//				if(numeroFacturaSeucursalLetra != null) {
//					factura.setNumero(numeroFacturaSeucursalLetra);
//				}else {
//					
////					ARROJAR EXCEPCION NO SE PUEDO GENERAR FACTURA
//					
//				}
				factura = FacturacionServiceUtil.getFactura(idFacturaNueva);
				
				if(SessionErrors.isEmpty(renderRequest)) {
					String msg = "Se generó correctamente la " + 
								(factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) || factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)?"Factura: ":"Nota de Crédito: ")
								+factura.getLetra()+" "+ factura.getSucursal() + "-" + factura.getNumero();
//					SessionMessages.add(renderRequest, "request_processed");
					SessionMessages.add(renderRequest, "insertFacturaOk");
					renderRequest.setAttribute("msgFacturaOk",msg);
					
					_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				}
				session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);
				
				
			}
			
			if(cmd.equals(Constants.SEARCH) ){ 
				
				BusquedaFacturasFiltro filtro = getFiltroFactura(renderRequest);
				
				int registrosTotalBusqueda = 0;
//				String llamada = ParamUtil.getString(renderRequest, "viene_de", "");
				int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
				pagina_sel--;
				
				filtro.setPagina(pagina_sel);
				List<Factura> busqueda = FacturacionServiceUtil.getFacturas(filtro);
				
				session.removeAttribute(WebKeysUOMA.BUSQUEDA_FACTURAS_RESULT);
				session.removeAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS);
				session.removeAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_TOTAL_REGISTROS);
				session.removeAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_OFFSET_REG);
				
				if(busqueda != null && busqueda.size() > 0){ 
					registrosTotalBusqueda = busqueda.get(0).getTotalRegistros();
					session.setAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_TOTAL_REGISTROS, registrosTotalBusqueda);
					session.setAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_OFFSET_REG , pagina_sel);
				}else{
					session.setAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_TOTAL_REGISTROS, 0);
					session.setAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS_OFFSET_REG , 0);
				}
				session.setAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS, filtro);
				session.setAttribute(WebKeysUOMA.BUSQUEDA_FACTURAS_RESULT, busqueda);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.facturacion_search_result"));
				
			}
			
			if(cmd.equals(Constants.VIEW) ){ 
				int idFactura = ParamUtil.getInteger(renderRequest, "idFactura");
				
				factura = FacturacionServiceUtil.getFactura(idFactura);

				session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);

			}
			
			
            if(cmd.equals(Constants.UPDATE) ){ 
				
				factura = (Factura) session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
				
				factura = getFacturaFromRequest(renderRequest, factura);
				
				//factura.recalcularImportes();
				
				boolean validaOK = true;
				
			   if (WebKeysGlobal.COMPROBANTE_NOTA_CREDITO.equalsIgnoreCase(factura.getTipo())){
					
				   List<Cliente> cliente =  FacturacionServiceUtil.getClientesPorAnio(factura.getCliente().getDocumentoNro(), factura.getCliente().getApellido(), factura.getCliente().getCuit()); 
				   
					if(cliente!=null && cliente.isEmpty()){
						SessionErrors.add(renderRequest, "error-cliente-sin-factura");
						validaOK=false;
					}
				   
				   
				   List<FacturaIngreso> facturas = new ArrayList<FacturaIngreso>();

				   for (FacturaDetalle detalle : factura.getDetalles()) {
						Efectivo efectivo = new Efectivo();
						efectivo.setImporte(detalle.getPrecio());
						efectivo.setFechaRecibo(new Date());
						efectivo.setFecha(new Date());
						FacturaIngreso fac = new FacturaIngreso();						
						fac.setIngreso(efectivo);
						facturas.add(fac);
						factura.setIngresos(facturas);	
				   }
				   
				}
				
				if(factura.getDetalles()==null || factura.getDetalles().isEmpty()){
					SessionErrors.add(renderRequest, "error-factura-sin-detalle");
					validaOK=false;
				}
				
				if(factura.getIngresos()==null || factura.getIngresos().isEmpty() ){
					SessionErrors.add(renderRequest, "error-factura-sin-fpago");
					validaOK=false;
				}
				BigDecimal totalFormaPagos = new BigDecimal(0);
				BigDecimal totalFormaPagosAux = null;
				BigDecimal totalAdelantos= new BigDecimal(0);
				
				for (Iterator<FacturaIngreso> iterator = factura.getIngresos().iterator(); iterator.hasNext();) {
					FacturaIngreso fi =  iterator.next();
					totalFormaPagosAux = totalFormaPagos; 
					totalFormaPagos =  totalFormaPagosAux.add(fi.getIngreso().getImporte());
				}
				
				if(factura.getRecibosAdelantos()!=null) {
				  for(Recibo r:factura.getRecibosAdelantos()) {
					totalAdelantos=totalAdelantos.add(BigDecimal.valueOf(r.getTotal()));
				  }
				}
				
				if(!(factura.getImporteTotal().compareTo(totalFormaPagos.add(totalAdelantos))==0)) {
					SessionErrors.add(renderRequest, "error-factura-total-fpago");
					validaOK=false;
				}
				
				if(!validaOK) {
					renderRequest.setAttribute("esEdicion", "esEdicion");
					return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_editar"));
				}
				
				int idFacturaNueva = FacturacionServiceUtil.updateFactura(factura, user.getScreenName());
				factura = FacturacionServiceUtil.getFactura(idFacturaNueva);
				
				if(SessionErrors.isEmpty(renderRequest)) {
					String msg = "Se generó correctamente la " + 
								(factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) || factura.getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA_CREDITO)?"Factura: ":"Nota de Crédito: ")
								+factura.getLetra()+" "+ factura.getSucursal() + "-" + factura.getNumero();
					SessionMessages.add(renderRequest, "insertFacturaOk");
					renderRequest.setAttribute("msgFacturaOk",msg);
					
					_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd );
				}
				session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);
				
				
			}


			
		}
		
		return mapping.findForward(getForward(renderRequest,"portlet.uoma.facturacion_editar"));
		
	}
	
	
	private Factura getFacturaFromRequest(RenderRequest renderRequest, Factura fc) throws SystemException{
		
		    SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
			Cliente cli = new Cliente();
			
			String clienteNroDoc= ParamUtil.getString(renderRequest, "cliente_nro_doc");
			String clienteApe= ParamUtil.getString(renderRequest, "cliente_apellido");
			String clienteNom= ParamUtil.getString(renderRequest, "cliente_nombre");
			String clienteTipo= ParamUtil.getString(renderRequest, "persfisica_tipo");
			String clienteEstado= ParamUtil.getString(renderRequest, "persfisica_estado");
			String cuit= ParamUtil.getString(renderRequest, "cuit_entidad");
			String sucursal= ParamUtil.getString(renderRequest, "sucursal_entidad");
			String razonSocial = ParamUtil.getString(renderRequest, "entidad");
			String iva = ParamUtil.getString(renderRequest, "iva");
			boolean presForm8001 = ParamUtil.getBoolean(renderRequest, "fc_form8001");
			String fcTipo = ParamUtil.getString(renderRequest, "fc_tipo");
			String fcLetra = ParamUtil.getString(renderRequest, "fc_letra");
			String fcSucursal = ParamUtil.getString(renderRequest, "fc_sucursal");
			
			String fechaEmisionDia = ParamUtil.getString(renderRequest,"fechaEmisionDia");
			String fechaEmisionMes = ParamUtil.getString(renderRequest,"fechaEmisionMes");
			String fechaEmisionAnio = ParamUtil.getString(renderRequest,"fechaEmisionAnio");
			Date fechaEmision = null;
			try {
				fechaEmision = formatoDeFecha.parse(fechaEmisionDia + "/"
						+ (Integer.parseInt(fechaEmisionMes) + 1) + "/"
						+ fechaEmisionAnio);
			} catch (Exception e) {
				fechaEmision = DateUtils.getCalendarGMTMenos3().getTime();
			}
			if(StringUtils.checkEmpty(clienteEstado)) {
				cli.setEstado(Cliente.ESTADOS.SELECCIONADO);
			}else {
				cli.setEstado(Cliente.ESTADOS.valueOf(clienteEstado));
			}
			if(StringUtils.checkEmpty(clienteTipo)) {
				cli.setTipo(Cliente.TIPOS_CLIENTE.EMPRESA);
			}else {
				cli.setTipo(Cliente.TIPOS_CLIENTE.valueOf(clienteTipo));
			}
			
			
			if(StringUtils.checkNotEmpty(cuit)) {
				
				cli.setTipo(Cliente.TIPOS_CLIENTE.EMPRESA);
				cli.setCuit(cuit);
				cli.setRazonSocial(razonSocial);
				cli.setSucursal(sucursal);
				
			}else if((cli.getEstado().compareTo(Cliente.ESTADOS.SELECCIONADO)==0 ||
					cli.getEstado().compareTo(Cliente.ESTADOS.ALTA)==0) &&
					cli.getTipo().compareTo(Cliente.TIPOS_CLIENTE.AFILIADO)==0){
				
				cli.setTipo(Cliente.TIPOS_CLIENTE.AFILIADO);
				
				cli.setDocumentoTipo("DU");
				cli.setDocumentoNro(clienteNroDoc);
				cli.setApellido(clienteApe);
				cli.setNombre(clienteNom);
			}else {
				cli.setTipo(Cliente.TIPOS_CLIENTE.VISITA);
				
				cli.setDocumentoTipo("DU");
				cli.setDocumentoNro(clienteNroDoc);
				cli.setApellido(clienteApe);
				cli.setNombre(clienteNom);
			}
			
			boolean facturaManual = ParamUtil.getBoolean(renderRequest, "fc_manual");
			
			
			if(facturaManual) {
				String fcNumero = ParamUtil.getString(renderRequest, "fc_numero");
				String fcCaeNro = ParamUtil.getString(renderRequest, "fc_cae_nro");
				String fechaEDia = ParamUtil.getString(renderRequest, "fechaEDia");
				String fechaEMes = ParamUtil.getString(renderRequest, "fechaEMes");
				String fechaEAnio = ParamUtil.getString(renderRequest, "fechaEAnio");

				Date fechaVtoCAE = null;
				try {
					fechaVtoCAE = formatoDeFecha.parse(fechaEDia + "/"
							+ (Integer.parseInt(fechaEMes) + 1) + "/" + fechaEAnio);
				} catch (Exception e) {
					fechaVtoCAE = Calendar.getInstance().getTime();
				}
				
				fc.setManual(true);
				fc.setNumero(fcNumero);
				fc.setCae(fcCaeNro);
				fc.setFechaCae(fechaVtoCAE);
			}
			
			cli.setCategoriaIVA(iva);
			
//			fc.setCae(cae);
			fc.setCliente(cli);
//			fc.setDetalle(detalles); // ya vienen del manejo de agregar/quitar producto
			fc.setEstado(Factura.ESTADOS.ALTA);
			fc.setFecha(fechaEmision);
//			fc.setFechaCae(fechaCae);
//			fc.setIva(new BigDecimal(iva));
			fc.setTipo(fcTipo);
			fc.setLetra(fcLetra);
//			fc.setNumero(numero);  // automatico x letra sucursal
			fc.setSucursal(fcSucursal);
			fc.setPresentaForm8001(presForm8001);
			
			
			String observaciones = ParamUtil.getString(renderRequest,"obs");
			fc.setObservaciones(observaciones);
			
			Double totalNeto=ParamUtil.getDouble(renderRequest,"fc_neto",0D);
			Double totalExento=ParamUtil.getDouble(renderRequest,"fc_exento",0D);
			Double total=ParamUtil.getDouble(renderRequest,"fc_total",0D);
			Double totalIVA=ParamUtil.getDouble(renderRequest,"fc_iva_21",0D);
			Double ivaReintegro=ParamUtil.getDouble(renderRequest,"fc_iva_21_reint",0D);
			Double percepcion=ParamUtil.getDouble(renderRequest, "fc_percepciones",0D);
			
			fc.setTotalNeto(BigDecimal.valueOf(totalNeto));
			fc.setTotalExento(BigDecimal.valueOf(totalExento));
			fc.setTotal(BigDecimal.valueOf(total));
			fc.setIva(BigDecimal.valueOf(totalIVA));
			fc.setIvaReintegro(BigDecimal.valueOf(ivaReintegro));
			fc.setPercepcion(BigDecimal.valueOf(percepcion));

			return fc;
			
	}
	
	private BusquedaFacturasFiltro getFiltroFactura(RenderRequest renderRequest) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesde", null);
		Date fechaDesde = null;
		try {
			fechaDesde = sdf.parse(fechaDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHasta", null);
		Date fechaHasta = null;
		try {
			fechaHasta = sdf.parse(fechaHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}
		String tipo = ParamUtil.getString(renderRequest,"tipo", null);
		String letra = ParamUtil.getString(renderRequest,"letra", null);
		String sucursal = ParamUtil.getString(renderRequest,"sucursal", null);
		String numero = ParamUtil.getString(renderRequest,"numero", null);
		int pagina = ParamUtil.getInteger(renderRequest,"pagina", 0);
		
		BusquedaFacturasFiltro filtro = new BusquedaFacturasFiltro(fechaDesde, fechaHasta, tipo, sucursal, letra, numero, pagina);
		
		return filtro;
	}

	
}