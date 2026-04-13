package ar.com.ospim.hoteles.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.facturacion.exceptions.ImposibleObtenerCAEAFIPException;
import ar.com.ospim.facturacion.exceptions.ImposibleObtenerTokenAFIPLoginException;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
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

public class HotelesFacturacionAction extends JSONAction {
	
	private Logger _log = Logger.getLogger(this.getClass());

	String cuit;
	File configDir = new File(System.getProperty("catalina.base"), "conf");
	File configFile = new File(configDir, "liferay_schedulers.properties");
	
	
	
	
//	private static final String cuit = "20181512831"; // MARCE p/ QA
//	private static final String cuit = "30531143856"; // UOMA p/ PRODUCCION
	
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		InputStream stream;
		try {
			stream = new FileInputStream(configFile);
			Properties props = new Properties();
			props.load(stream);
			cuit = props.getProperty("cuit_ws_afip");
		}catch(Exception e) {
			
			cuit="";
		}
		
		Factura factura=null;
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		User user = PortalUtil.getUser(req);
		String error ="";
		String resultado = "{}";
		
		factura = (Factura)  req.getSession().getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
		
		factura = getFacturaFromRequest(req, factura);
		
		factura.recalcularImportes();
		
		boolean validaOK = true;
		
		if(factura.getCliente() ==null || (factura.getCliente()!=null && factura.getCliente().getApellido().isEmpty()) ){
			validaOK=false;
			error="Debe ingresar un cliente.";
		}
		
		if(factura.getDetalles()==null || factura.getDetalles().isEmpty()){
			SessionErrors.add(req, "error-factura-sin-detalle");
			validaOK=false;
			error="Debe cargar al menos un detalle en la factura";
		}
		
		if(factura.getIngresos()==null || factura.getIngresos().isEmpty() ){
			SessionErrors.add(req, "error-factura-sin-fpago");
			validaOK=false;
			error="Debe cargar al menos una forma de pago en la factura";
		}
		BigDecimal totalFormaPagos = new BigDecimal(0);
		BigDecimal totalFormaPagosAux = null;
		
		if(factura.getIngresos()!=null) {
		  for (Iterator<FacturaIngreso> iterator = factura.getIngresos().iterator(); iterator.hasNext();) {
			FacturaIngreso fi =  iterator.next();
			totalFormaPagosAux = totalFormaPagos; 
			totalFormaPagos =  totalFormaPagosAux.add(fi.getIngreso().getImporte());
		  }
		}
		if(!(factura.getImporteTotal().compareTo(totalFormaPagos)==0)) {
			SessionErrors.add(req, "error-factura-total-fpago");
			error="La forma de pago debe ser igual al importe total en la factura";
			validaOK=false;
		}
		
		if(!validaOK) {
			req.setAttribute("esEdicion", "esEdicion");
		}
		
		if(validaOK) {

		  AfipLoginCmsClient clienteWS = new AfipLoginCmsClient(user.getScreenName());
		
		  LoginCmsResponse token = clienteWS.getTokenValido();
		  FEAuthRequest autorization = null;
		
		  if(token == null) {
			SessionErrors.add(req, ImposibleObtenerTokenAFIPLoginException.class.getName() );
			error="Imposible obtener Token AFIP";
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
			
//			Errores
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
			
			    SessionErrors.add(req, ImposibleObtenerCAEAFIPException.class.getName() );
			    error="Imposible obtener CAE AFIP(a)";
			}
		  }
		
		  FECAEDetResponse[] detalleRtaAfip = null;
		
		  if(respuestaAfip.getFeDetResp()!=null) {
			detalleRtaAfip = respuestaAfip.getFeDetResp().getFECAEDetResponse();
			
			for (int i = 0; i < detalleRtaAfip.length; i++) {
				
				if(StringUtils.checkNotEmpty(detalleRtaAfip[i].getCAE())) {
					factura.setCae(detalleRtaAfip[i].getCAE());
				}else {
					SessionErrors.add(req, ImposibleObtenerCAEAFIPException.class.getName() );
					error="Imposible obtener CAE AFIP(b)";
					req.setAttribute("esEdicion", "esEdicion");
					break;
//					return mapping.findForward(getForward(req,"portlet.uoma.facturacion_editar"));
				}
				
				
				
				try{
					factura.setFechaCae(sdf.parse(detalleRtaAfip[i].getCAEFchVto()));
				}catch(ParseException e){
					_log.error(e);
				}
				_log.debug(detalleRtaAfip[i].getResultado());
				
			}
			
		  }
		
		  if(SessionErrors.isEmpty(req)) {
		     int idFacturaNueva = FacturacionServiceUtil.saveFactura(factura, user.getScreenName());
		     factura = FacturacionServiceUtil.getFactura(idFacturaNueva);
		  }   
		}	
		if(SessionErrors.isEmpty(req)) {
			String msg = "Se generó correctamente la Factura: "+factura.getLetra()+" "+ factura.getSucursal() + "-" + factura.getNumero();
			SessionMessages.add(req, "insertFacturaOk");
			req.setAttribute("msgFacturaOk",msg);
			
			_log.debug("Usuario: " + user.getScreenName() 
				+ " cmd: " + "save");
		}
		req.getSession().setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);

 
	    String numero="";
	    String cae ="";
	    String caefecha ="";
	   
	    if(factura.getCae()!=null) {
	    	numero=factura.getNumero();
	    	cae =factura.getCae();
		    caefecha =formatoDeFechas.format(factura.getFechaCae());
	    }
			 
		resultado = "{ \"numero\" : \"" 
				    + numero 
				    + "\",\"cae\" : \""
				    + cae
				    + "\",\"caefecha\" : \""
				    + caefecha
				    + "\",\"idfactura\" : \""
				    + factura.getId()
				    + "\",\"error\" : \""
				    + error
				    + "\" }";
		
		return resultado;
		
		
	}
	
	
	private Factura getFacturaFromRequest(HttpServletRequest renderRequest, Factura fc) throws SystemException{
		
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
	
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
			fechaEmision = formatoDePeriodo.parse(fechaEmisionDia + "/"
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
		
		cli.setCategoriaIVA(iva);
		
//		fc.setCae(cae);
		fc.setCliente(cli);
//		fc.setDetalle(detalles); // ya vienen del manejo de agregar/quitar producto
		fc.setEstado(Factura.ESTADOS.ALTA);
		fc.setFecha(fechaEmision);
//		fc.setFechaCae(fechaCae);
//		fc.setIva(new BigDecimal(iva));
		fc.setTipo(fcTipo);
		fc.setLetra(fcLetra);
//		fc.setNumero(numero);  // automatico x letra sucursal
		fc.setSucursal(fcSucursal);
		fc.setPresentaForm8001(presForm8001);
		
		
		
		return fc;
		
   }
	
	
	
}