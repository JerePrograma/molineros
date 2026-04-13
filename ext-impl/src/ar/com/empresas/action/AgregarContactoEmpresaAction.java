package ar.com.empresas.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.empresas.beans.Contacto;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.ContactoElectronico.Tipo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadPadronUnificado;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarContactoEmpresaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarContactoEmpresaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a empresa");
		
		PortletSession session=renderRequest.getPortletSession();
		
		EntidadPadronUnificado empresa=(EntidadPadronUnificado)session.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, PortletSession.APPLICATION_SCOPE);;
		
		LlamadosEstudio llest=null;
		Contacto contacto = null;
		boolean esSeguientoEmpresa=false;
		
		try{
			llest=(LlamadosEstudio)session.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
			
			if(llest!=null){
	   		   esSeguientoEmpresa=true;
			}
			
		}catch (Exception e) {
			// Para zafar en portlet Empresas...
		}
		
/*		
		
		if(empresa==null){
			llest=(LlamadosEstudio)session.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
			empresa=llest.getEmpresa();
			if(empresa!=null){
				esSeguientoEmpresa=true;
			}
		}
*/
		String accion = ParamUtil.getString(renderRequest, "accion");
		List<Contacto> contactosEmpresa = null;
		int pos = -1;
		
			if (null == empresa) {
				empresa = new Empresa();
			}
			if (null != empresa.getContactos()
					&& empresa.getContactos().size() > 0) {
				contactosEmpresa = empresa.getContactos();
			} else {
				contactosEmpresa = new ArrayList<Contacto>();
			}
		if (accion.equals("ADD")) {
			contacto = getContacto(renderRequest);
			
			pos = contactosEmpresa.indexOf(contacto);
			
			if(contacto.getIdContacto() > 0){
				contacto.setEstado(Contacto.ESTADOS.MODIF);
			}else{
				contacto.setEstado(Contacto.ESTADOS.NUEVO);
//				solo si edito un contacto, tendre el contacto con id, sino le genero uno aleatorio para el borrado de contacto
//				me aseguro sea un numero negativo para no confundir con IDs de BD
				Random r = new Random(System.currentTimeMillis());
				int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
				if(idAux > 0){
					idAux = (-1)*idAux;
				}
				if(contacto.getTelefono()!=null){
					contacto.getTelefono().setId(idAux);
				}else{
					contacto.getContacto().setId(idAux);
				}
				
			}
			if(pos >= 0){ // lo encontro, para update remuevo el viejo, para insert el pos da -1
				contactosEmpresa.remove(pos);
			}
			contactosEmpresa.add(contacto);
			
		}else if (accion.equals("DELETE")) {			
			
			contacto = getContacto(renderRequest);
			
			/*
			pos = contactosEmpresa.indexOf(contacto);
			if(contacto.getTelefono().getId() >= 0 ||
					contacto.getContacto().getId() >= 0 ){ // lo encontro, para update remuevo el viejo, para insert el pos da -1
				contacto.setEstado(Contacto.ESTADOS.BAJA);
				contacto.setBajaFecha(new Date());
				contactosEmpresa.remove(pos);
				contactosEmpresa.add(contacto);
				
			}else{ // se quita un nuevo domicilio que no fue insertado a la BD
				contactosEmpresa.remove(pos);
			}
			*/
			
			//DS
			
			List<Contacto>contactos=new ArrayList<Contacto>();
			for(Contacto c:contactosEmpresa){
				if(c.getIdContacto()==contacto.getIdContacto()){
					if(c.getIdContacto()!=0){
					  c.setEstado(Contacto.ESTADOS.BAJA);
					  c.setBajaFecha(new Date());
					  contactos.add(c);
					}else if(c.getIdContacto()==0 && !contacto.equals(c) ){
						contactos.add(c);	
					}
				}else if(!contacto.equals(c) ){
					contactos.add(c);
				}
			}
			contactosEmpresa=contactos;
			
			//DS
			
		}
		
		empresa.setContactos(contactosEmpresa);
		
		
//		if(llest!=null){
//			llest.setEmpresa(empresa);
//			session.setAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);	
//		}else{
//			renderRequest.getPortletSession().setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, empresa,PortletSession.APPLICATION_SCOPE);
//		}
		
		if(!esSeguientoEmpresa){
			renderRequest.getPortletSession().setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, empresa, PortletSession.APPLICATION_SCOPE);
		}else{
			renderRequest.getPortletSession().setAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO, empresa, PortletSession.APPLICATION_SCOPE);
		}
		
		if(contacto.getNombreApe()!=null && !"".equalsIgnoreCase(contacto.getNombreApe())){
			return mapping.findForward("portlet.empresas.agregar_contacto_personas");
		}
		
		return mapping.findForward("portlet.empresas.agregar_contacto");

	}

	private Contacto getContacto(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Contacto contacto = new Contacto();

		String tipo = ParamUtil.getString(renderRequest, "tipo");

		String tel_pais = ParamUtil.getString(renderRequest, "tel_pais");
		String tel_area = ParamUtil.getString(renderRequest, "tel_area");
		String tel_numero = ParamUtil.getString(renderRequest, "tel_numero");
		String tel_ext = ParamUtil.getString(renderRequest, "tel_ext");
		String contactoC = ParamUtil.getString(renderRequest, "contactoC");
		String obs = ParamUtil.getString(renderRequest, "observacionesC");
		String cargoC = ParamUtil.getString(renderRequest, "cargoC");
		String nomyape = ParamUtil.getString(renderRequest, "nomyape");
		String profesionC = ParamUtil.getString(renderRequest, "profesionC");
		int idContacto= ParamUtil.getInteger(renderRequest, "idContactoC");

		Telefono tel = new Telefono();
		tel.setCodigoPais(tel_pais);
		tel.setCodigoArea(tel_area);
		tel.setNumero(tel_numero);
		tel.setExtension(tel_ext);
		tel.setObservaciones(obs);
		tel.setId(idContacto);
		contacto.setTelefono(tel);

		ContactoElectronico contactoE = new ContactoElectronico();
        
		if(tipo.length()>2){
			if("PERSONAL".equalsIgnoreCase(tipo)){
				tipo="P";
			}
			if("SITIOWEB".equalsIgnoreCase(tipo)){
				tipo="s";
			}
			if("FAX".equalsIgnoreCase(tipo)){
				tipo="F";
			}
			if("EMAIL".equalsIgnoreCase(tipo)){
				tipo="E";
			}
			if("EMAILCBU".equalsIgnoreCase(tipo)){
				tipo="EC";
			}
		}
		
		contactoE.setTipo(Tipo.getTipoById(tipo));
		contactoE.setContacto(contactoC);
		contactoE.setObservaciones(obs);
		contactoE.setId(idContacto);
		contacto.setContacto(contactoE);
		
		contacto.setCargo(cargoC);
		if("null".equalsIgnoreCase(nomyape) || "".equalsIgnoreCase(nomyape)){
			contacto.setNombreApe(null);
		}else{
		    contacto.setNombreApe(nomyape);
		}
		contacto.setProfesion(profesionC);

		return contacto;
	}
	
//	private void removeContactoFromList(List<Contacto> list, Contacto ap) {
//		Iterator<Contacto> it = list.iterator();
//		while (it.hasNext()) {
//			Contacto aContactoEnLista = it.next();
//			if (aContactoEnLista.equals(ap)) {			
//				if(aContactoEnLista.getIdContacto()!=0){
//					aContactoEnLista.setBajaFecha(new Date());
//				}else{
//					it.remove();
//				}
//					
//			}
//		}
//	}

}
