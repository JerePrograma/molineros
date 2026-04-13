package ar.com.uoma.proveedores.action;

import java.io.InputStream;
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
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.Proveedor;
import ar.com.uoma.proveedores.services.ProveedoresServiceUtil;


public class ProveedoresAction extends PortletAction {
	
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
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			portlet_name = "farmacia";
		}else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			portlet_name = "uoma";
		}
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		EmpresaServiceUtil.getRegimenesRetencionGanancias(renderRequest);
		TraeListasServiceUtil.getBancos(renderRequest);
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		
		Proveedor proveedor=null;
		Integer idProveedor = 0;
		String msg = "";
		String accion="";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idProveedor = ParamUtil.getInteger(renderRequest,"idPrv", 0);
			accion =ParamUtil.getString(renderRequest,"accion", "");
			if(cmd.equals(Constants.WRITE) ){ 
				
				proveedor = new Proveedor();
				session.setAttribute(WebKeysUOMA.PROVEEDOR_EN_EDICION , proveedor);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				if("uoma".equalsIgnoreCase(portlet_name)) {
				    return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.proveedores_editar"));
				}else {
					return mapping.findForward(getForward(renderRequest,
							"portlet.farmacia.proveedores_editar"));
				}
			}
			
			if(cmd.equals("filter")){
		           filterProveedor(renderRequest,session);		   	
					
		           if("uoma".equalsIgnoreCase(portlet_name)) {
					    return mapping.findForward(getForward(renderRequest,
							"portlet.uoma.proveedores_search_result"));
					}else {
						return mapping.findForward(getForward(renderRequest,
								"portlet.farmacia.proveedores_search_result"));
					}
			}
			
			
            if(cmd.equals(Constants.EDIT) ){
            	
            	List<Proveedor> proveedores =ProveedoresServiceUtil.getProveedores(null, null, null, idProveedor);
            	
            	if(!proveedores.isEmpty()) {
            		proveedor=proveedores.get(0);
            	}
            	session.setAttribute(WebKeysUOMA.PROVEEDOR_EN_EDICION , proveedor);
            	_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				String tipoEdicion = ParamUtil.get(renderRequest, "accion", "E");
				
				renderRequest.setAttribute("view", "E".equalsIgnoreCase(tipoEdicion)?"EDIT":"VIEW");
				if("uoma".equalsIgnoreCase(portlet_name)) {
					return mapping.findForward(getForward(renderRequest,"portlet.uoma.proveedores_editar"));
				}else {
					return mapping.findForward(getForward(renderRequest,"portlet.farmacia.proveedores_editar"));
				}
				
				
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				proveedor = (Proveedor) session.getAttribute(WebKeysUOMA.PROVEEDOR_EN_EDICION);
				actualizaProveedor(proveedor,PortalUtil.getHttpServletRequest(renderRequest));
				if(idProveedor==0){ //Nuevo
					idProveedor= insertProveedor(proveedor,user.getScreenName());	
					proveedor.setId(idProveedor);
					session.setAttribute(WebKeysUOMA.PROVEEDOR_EN_EDICION , proveedor);
					msg = LanguageUtil.get(defaultLocale, "insert-proveedor");
					  msg = msg +" " +idProveedor;
					  SessionMessages.add(renderRequest, "request_processed");
					  renderRequest.setAttribute("grabar-exitoso", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Proveedor: " + idProveedor
							);
					   
				}else {
						updateProveedor(proveedor, user.getScreenName());
						
						msg = LanguageUtil.get(defaultLocale, "insert-proveedor");
						msg = msg + " "+ idProveedor;
						SessionMessages.add(renderRequest, "request_processed");
						renderRequest.setAttribute("grabar-exitoso", msg);
						_log.debug("Usuario: " + user.getScreenName() 
								+ " cmd: " + cmd 
								+ " id Proveedor: " + idProveedor
								);
				}
			}
			
		}
		
		if("uoma".equalsIgnoreCase(portlet_name)) {
			return mapping.findForward(getForward(renderRequest,"portlet.uoma.proveedores_editar"));
		}else {
			return mapping.findForward(getForward(renderRequest,"portlet.farmacia.proveedores_editar"));
		}
		
	}
	
	
private void actualizaProveedor(Proveedor proveedor,HttpServletRequest renderRequest) throws SystemException{
		Integer idProveedor = ParamUtil.getInteger(renderRequest, "idPrv");
		String cuit= ParamUtil.getString(renderRequest, "cuit");
		String sucursal= ParamUtil.getString(renderRequest, "sucursal");
		String razonSocial = ParamUtil.getString(renderRequest, "desc");
		String iva = ParamUtil.getString(renderRequest, "iva");
		String categoriaMonotributo = ParamUtil.getString(renderRequest, "categoriamonotributo");
		Integer regimen = ParamUtil.getInteger(renderRequest, "regimen");
		boolean agenteRetencion = ParamUtil.getBoolean(renderRequest, "agenteRetencion");
		Integer actividadPrincipal =ParamUtil.getInteger(renderRequest, "cod_actividadempre_");
		Integer actividadSecundaria =ParamUtil.getInteger(renderRequest, "cod_actividad_secempre_");
		String tipoPago=ParamUtil.getString(renderRequest, "tipoPago");
		
		Integer idBcoCtaBcria = ParamUtil.getInteger(renderRequest,"id_banco_cta_bcria");
		String desCtaBcria =ParamUtil.getString(renderRequest, "descripcion_cta_bcria");
		String cbuCtaBcria =ParamUtil.getString(renderRequest, "cbu_cta_bcria");
		String tipoDomicilio =ParamUtil.getString(renderRequest, "tipo_domicilio_empre");
		Integer idDomicilio=ParamUtil.getInteger(renderRequest, "id_domicilio");
		
		Integer provincia=ParamUtil.getInteger(renderRequest, "provincia");
		Integer localidad=ParamUtil.getInteger(renderRequest, "localidad");
		String codPostal =ParamUtil.getString(renderRequest, "cod_postal");
		String calle =ParamUtil.getString(renderRequest, "calle");
		String numero =ParamUtil.getString(renderRequest, "numero");
		String piso =ParamUtil.getString(renderRequest, "piso");
		String departamento =ParamUtil.getString(renderRequest, "departamento");
		String email =ParamUtil.getString(renderRequest, "email_prv");
		Integer idCtaBcria = ParamUtil.getInteger(renderRequest,"id_cta_bcria");
		
		proveedor.setId(idProveedor);
		proveedor.setCuit(cuit);
		proveedor.setSucursal(sucursal);
		proveedor.setRazon_soc(razonSocial);
		proveedor.setImpIva(iva);
		proveedor.setMonotributo(categoriaMonotributo);
		
		Regimen regimenGcia = new Regimen();
		regimenGcia.setCodigoRegimen(regimen);
		proveedor.setRegimen(regimenGcia);
		
		proveedor.setAgenteRetencion(agenteRetencion);
		Actividad principal=new Actividad();
		principal.setCodigo(actividadPrincipal);
		proveedor.setActividadPrincipal(principal);
		
		Actividad secundaria=new Actividad();
		secundaria.setCodigo(actividadSecundaria);
		proveedor.setActividadSecundaria(secundaria);
		
		proveedor.setFormaPago(tipoPago);
		
		Banco banco = new Banco();
		banco.setId_banco(idBcoCtaBcria);
		CuentaBancaria cuenta= new CuentaBancaria();
		cuenta.setId_cuenta_bcria(idCtaBcria);
		cuenta.setBanco(banco);
		cuenta.setDescripcion(desCtaBcria);
		cuenta.setCBU(cbuCtaBcria);
		proveedor.setCuentaBcria(cuenta);
		
		Domicilio domicilio= new Domicilio();
		domicilio.setCalle(calle);
		domicilio.setId_domicilio(idDomicilio);
		domicilio.setPostal_codi(codPostal);
		domicilio.setNumero(numero);
		domicilio.setPiso(piso);
		domicilio.setDepto(departamento);
		domicilio.setDomi_tipo(tipoDomicilio);
		
		Provincia pcia= new Provincia();
		pcia.setId(provincia);
		domicilio.setProvincia(pcia);
		
		Localidad local =new Localidad();
		local.setId(localidad);
		domicilio.setLocalidad(local);
		
		proveedor.setDomicilio(domicilio);
		
		ContactoElectronico contacto = new ContactoElectronico();
		List<ContactoElectronico> contactos = new ArrayList<ContactoElectronico>();
		if(email!=null) {
			contacto.setTipo(ContactoElectronico.Tipo.EMAIL);
			contacto.setContacto(email);
		}
		contactos.add(contacto);
		proveedor.setContactosElectronicos(contactos);
		
}

private void filterProveedor(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String cuit=ParamUtil.getString(renderRequest,"cuit",null);
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		String razonSocial =  ParamUtil.getString(renderRequest, "descripcion",null);
		session.removeAttribute(WebKeysUOMA.PROVEEDORES_RESULT);
		List<Proveedor>proveedores= ProveedoresServiceUtil.getProveedores((cuit.length()>0?cuit:null ), (sucursal.length()>0?sucursal:null ),
				(razonSocial.length()>0?razonSocial:null ), null);
		session.setAttribute(WebKeysUOMA.PROVEEDORES_RESULT,proveedores);
}


private Integer insertProveedor(Proveedor proveedor, String user) throws Exception{
	Integer id = 0;
	id = ProveedoresServiceUtil.insertaProveedor(proveedor,user);
	return id;
}
	

private long updateProveedor(Proveedor proveedor, String user) throws Exception{
		long id = 0;
		
		id = ProveedoresServiceUtil.updateProveedor(proveedor, user);
		return id;
}

	
}