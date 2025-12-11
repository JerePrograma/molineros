package ar.com.ospim.afiliados.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.ContactoElectronico.Tipo;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

public class EditarABMSeccionalAction extends PortletAction {
	
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
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		cmd= ((cmd==null ||"".equalsIgnoreCase(cmd))?ParamUtil.getString(renderRequest, "accion", null):cmd);
		
		Seccional seccional=null;
		long idSeccional = 0;
		String msg = "";
		if (!StringUtils.checkEmpty(cmd)) {
			idSeccional = ParamUtil.getInteger(renderRequest,"id_seccional", 0);
			if(cmd.equals(Constants.ADD) ){
				
				session.removeAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
				
				seccional = new Seccional();
				Provincia provincia = new Provincia();
				Localidad localidad = new Localidad();
				Domicilio domicilio = new Domicilio();
				domicilio.setProvincia(provincia);
				domicilio.setLocalidad(localidad);
				seccional.setDomicilio(domicilio);
				ArrayList<Delegacion>delegaciones = new  ArrayList<Delegacion>();
				seccional.setDelegaciones(delegaciones);
				ArrayList<Contacto>contactos = new  ArrayList<Contacto>();
				seccional.setContactos(contactos);
				ArrayList<Contacto>plantel = new  ArrayList<Contacto>();
				seccional.setPlantel(plantel);
				session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, seccional);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seccional: " + idSeccional
						);
				return mapping.findForward(getForward(renderRequest,
						"portlet.afiliados.editar_abm_seccional"));
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	
            	seccional = SeccionalServiceUtil.buscarSeccionalById((int)idSeccional); 
                seccional.setModo("ED");
            	session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, seccional);
            	
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seccional: " + idSeccional
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.afiliados.editar_abm_seccional"));
			}
            
            
            if (cmd.equalsIgnoreCase("ADDCONTACTO")) { 
            	
            	Contacto contacto = getContacto(renderRequest);
            	Seccional sec = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
    			Integer pos =0;
    			
    			if(sec.getContactos()!=null){
    					pos= sec.getContactos().indexOf(contacto);
    			}
    			if(contacto.getIdContacto() > 0){
    				contacto.setEstado(Contacto.ESTADOS.MODIF);
    			}else{
    				contacto.setEstado(Contacto.ESTADOS.NUEVO);
//    				solo si edito un contacto, tendre el contacto con id, sino le genero uno aleatorio para el borrado de contacto
//    				me aseguro sea un numero negativo para no confundir con IDs de BD
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
    				
    				List<Contacto> contactos = new ArrayList<Contacto>();
    				for(int i=0;i< sec.getContactos().size();i++){
    					if(i!=pos) contactos.add(sec.getContactos().get(i));
    				}
    				sec.setContactos(contactos);
    				//sec.getContactos().remove(pos); Comentado porque no eliminaba el elemento
    				
    			}
    			sec.getContactos().add(contacto);
            	
    			  
      			session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, sec);
            	
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos.result"));
            }

            if (cmd.equalsIgnoreCase("EDITCONTACTO")) { 
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos.result"));
            }
            
            if (cmd.equalsIgnoreCase("DELETECONTACTO")) { 
            	
            	Contacto contacto = getContacto(renderRequest);
            	Seccional sec = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
            	
            	List<Contacto>contactos=new ArrayList<Contacto>();
    			for(Contacto c:sec.getContactos()){
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
            	sec.setContactos(contactos);
                session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, sec);
            	
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos.result"));
            }
            
            
            if(cmd!=null && "asociardelegacion".equalsIgnoreCase(cmd) ){
    			Seccional pc = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
    			Delegacion delegacion = new Delegacion();
    			String numero =renderRequest.getParameter("delegacionid");
    			String descripcion =renderRequest.getParameter("delegaciondescripcion");
    			delegacion.setDescripcion(descripcion);
    			delegacion.setId_delegacion(Integer.parseInt(numero));
    			Boolean existe=false;
    			if(pc.getDelegaciones() !=null){
    			  for(Delegacion ds: pc.getDelegaciones() ){
    				  if(ds.getId() == Integer.parseInt(numero)){
    					  existe=true; 
    					  break;
    				  }
    			  }
    			} 
    			if(!existe){
    				
    				List<Delegacion> ld = TraeListasServiceUtil.getDelegaciones();
        			for(Delegacion d:ld){
        				if(d.getId()==Integer.parseInt(numero)){
        					delegacion.setEsCentral(d.isEsCentral());
        				    delegacion.setLibro(d.getLibro());
        				    delegacion.setRubrica(d.getRubrica());
        				    delegacion.setTomo(d.getTomo());
        				    break;
        				}
        			}
    				
    				
    			  pc.getDelegaciones().add(delegacion);
    			}  
                
    			session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, pc);
    			return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_delegaciones.result"));
    		}
            
            if(cmd!=null && cmd.equals("desasociardelegacion") ){
    			
    			Seccional pc = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
    			String numero =renderRequest.getParameter("delegacionid");
    			List<Delegacion> l = new ArrayList<Delegacion>();
    			
    			for(Delegacion ds: pc.getDelegaciones() ){
    				  if( ds.getId() != Integer.parseInt(numero)){
    					  l.add(ds);
    				  }
    			}
    			pc.setDelegaciones(l);
    			
    			session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, pc);
    			return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_delegaciones.result"));
    		}
            
            
            if (cmd.equals(WebKeysGlobal.CAMBIO_SOLAPA)) {
            	Seccional pc = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
            	if(null!= renderRequest.getParameter("tabs1") &&renderRequest.getParameter("tabs1").equals("datos")){
//            	   actualizaSeccional(seccional,renderRequest);	
            	}
            	
            	if(null!= renderRequest.getParameter("tabs1") &&renderRequest.getParameter("tabs1").equals("datos-contactos")){
            	   getSeccionalFromRequest(pc,renderRequest);	
            	}
            	session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, pc);
            	
            	return mapping.findForward(getForward(renderRequest,
						"portlet.afiliados.editar_abm_seccional"));
            }
            
  
			if(cmd.equals(Constants.UPDATE) ){
				seccional = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
				getSeccionalFromRequest(seccional,renderRequest);
				
				if(!"ED".equalsIgnoreCase(seccional.getModo())){ //Nuevo
					
					if(!SeccionalServiceUtil.existeNumeroSeccional(seccional.getId())) {
					   addSeccional(seccional, user.getScreenName());

					   msg = LanguageUtil.get(defaultLocale, "update-seccional");
					   msg = msg + " Seccional "+ seccional.getId();
					   SessionMessages.add(renderRequest, "updateCabOk");
					   renderRequest.setAttribute("msgCabOk", msg);
					
					
				    }else{
					   msg = LanguageUtil.get(defaultLocale, "Ya existe el Nro de Seccional");
				       SessionErrors.add(renderRequest, "avisoSeccionalDuplicado");
					   renderRequest.setAttribute("msgInsertError",msg );
					   _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id seccional: " + seccional.getId()
							);   
				    }
					
				}else if("ED".equalsIgnoreCase(seccional.getModo())){
					updateSeccional(seccional, user.getScreenName());
					
					msg = LanguageUtil.get(defaultLocale, "update-seccional");
					msg = msg + " Seccional "+ seccional.getId();
					SessionMessages.add(renderRequest, "updateCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
				}
				session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, seccional);
				
			}
			
			
            if (cmd.equalsIgnoreCase("ADDCONTACTOPERSONAL")) { 
            	
            	Contacto contacto = getContactoPersonal(renderRequest);
            	Seccional sec = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
    			Integer pos =0;
    			
    			if(sec.getPlantel()!=null){
    					pos= sec.getPlantel().indexOf(contacto);
    			}
    			if(contacto.getIdContacto() > 0){
    				contacto.setEstado(Contacto.ESTADOS.MODIF);
    			}else{
    				contacto.setEstado(Contacto.ESTADOS.NUEVO);
    				Random r = new Random(System.currentTimeMillis());
    				int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
    				if(idAux > 0){
    					idAux = (-1)*idAux;
    				}
    				if(contacto.getTelefono()!=null){
    					contacto.getTelefono().setId(idAux);
    					contacto.getContacto().setId(idAux);
    				}else{
    					contacto.getContacto().setId(idAux);
    					contacto.getTelefono().setId(idAux);
    				}
    				
    			}
    			if(pos >= 0){ // lo encontro, para update remuevo el viejo, para insert el pos da -1
    				
    				List<Contacto> plantel = new ArrayList<Contacto>();
    				for(int i=0;i< sec.getPlantel().size();i++){
    					if(i!=pos) plantel.add(sec.getPlantel().get(i));
    				}
    				sec.setPlantel(plantel);
    				//sec.getContactos().remove(pos); Comentado porque no eliminaba el elemento
    				
    			}
    			sec.getPlantel().add(contacto);
            	
    			  
      			session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, sec);
            	
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos_personal.result"));
            }

            if (cmd.equalsIgnoreCase("DELETECONTACTOPERSONAL")) { 
            	
            	Contacto contacto = getContactoPersonal(renderRequest);
            	Seccional sec = (Seccional) session.getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
            	
            	List<Contacto>contactos=new ArrayList<Contacto>();
    			for(Contacto c:sec.getPlantel()){
    				if(c.getIdContacto()==contacto.getIdContacto()){
    					if(c.getIdContacto()>0    /*!=0*/){
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
            	sec.setPlantel(contactos);
                session.setAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION, sec);
            	
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos_personal.result"));
            }
			
            if (cmd.equalsIgnoreCase("EDITCONTACTOPERSONAL")) { 
            	return mapping.findForward(getForward(renderRequest,
    					"portlet.afiliados.editar_seccional_contactos_personal.result"));
            }
		}
		
		return mapping.findForward("portlet.afiliados.editar_abm_seccional");
		
	}
	
	
	
//-------------------------------
//------------------------------
	
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
				tipo="S";
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
			if("TELEFONO".equalsIgnoreCase(tipo)){
				tipo="T";
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
	
	
	
	private void getSeccionalFromRequest(Seccional seccional,RenderRequest renderRequest) throws SystemException{
		
		String descripcion =ParamUtil.getString(renderRequest,"descripcionSeccional",null);
		String tipo =ParamUtil.getString(renderRequest,"tipoSeccional",null);
		boolean imaginaria = ParamUtil.getBoolean(renderRequest,"imaginariaSeccional",false);
		Integer provinciaId=ParamUtil.getInteger(renderRequest,"provincia",0);
		Integer localidadId=ParamUtil.getInteger(renderRequest,"localidad",0);
		String calle =ParamUtil.getString(renderRequest,"calle",null);
		String numero =ParamUtil.getString(renderRequest,"numero",null);
		String piso =ParamUtil.getString(renderRequest,"piso",null);
		String dpto =ParamUtil.getString(renderRequest,"dpto",null);
		String codPostal =ParamUtil.getString(renderRequest,"cod_postal",null);	
		String barrio =ParamUtil.getString(renderRequest,"barrio",null);
		String chequeOrden =ParamUtil.getString(renderRequest,"chequeOrdenSeccional",null);
		String contacto= ParamUtil.getString(renderRequest, "contactoSeccional",null);
		String destinoCorreo= ParamUtil.getString(renderRequest, "destinoCorreoSeccional",null);
		String cbu= ParamUtil.getString(renderRequest, "cbuSeccional",null);
		String observaciones= ParamUtil.getString(renderRequest, "observacionSeccional",null);
		String horarioAtencion = ParamUtil.getString(renderRequest, "horarioAtencionSeccional",null);
		String fechaVigenciaDia = ParamUtil.getString(renderRequest,"fechaVigenciaSeccionalDia");
		String fechaVigenciaMes = ParamUtil.getString(renderRequest,"fechaVigenciaSeccionalMes");
		String fechaVigenciaAnio = ParamUtil.getString(renderRequest,"fechaVigenciaSeccionalAnio");
		
		Integer idSeccional = null;
//		analizamos si esta editando o si es uno nuevo (va el id sugerido)
//		if(seccional!=null 
//				&& seccional.getModo() != null && seccional.getModo().equalsIgnoreCase("ED") 
//				&& seccional.getIdSeccional() > 0 ){
//			idSeccional = ParamUtil.getInteger(renderRequest,"id_seccional", 0); 
//		}else{
//			idSeccional = ParamUtil.getInteger(renderRequest,"idSeccional", 0);
//		}
		idSeccional = ParamUtil.getInteger(renderRequest,"id_seccional");
		if(idSeccional==null || idSeccional == 0){
			idSeccional = ParamUtil.getInteger(renderRequest,"idSeccional");
		}
		
		
		Date fechaVigencia = null;
		try {
			fechaVigencia = formatoDeFechas.parse(fechaVigenciaDia + "/"
					+ (Integer.parseInt(fechaVigenciaMes) + 1) + "/"
					+ fechaVigenciaAnio);
		} catch (Exception e) {
			fechaVigencia = null;
		}
		boolean ospim = ParamUtil.getBoolean(renderRequest,"ospimSeccional",false);
		boolean uoma = ParamUtil.getBoolean(renderRequest,"uomaSeccional",false);
		boolean amtima = ParamUtil.getBoolean(renderRequest,"amtimaSeccional",false);
		
		String descripcionUOMA = ParamUtil.getString(renderRequest,"descripcionUOMASeccional");
		String descripcionAMTIMA = ParamUtil.getString(renderRequest,"descripcionAMTIMASeccional");
		String nroTarjeta= ParamUtil.getString(renderRequest, "tarjetaSeccional",null);
		
		seccional.setAmtima(amtima);
		seccional.setCBU(cbu);
		seccional.setCheque_a_la_orden(chequeOrden);
		seccional.setContacto(contacto);
		seccional.setDescripcion(descripcion);
		seccional.setDescripcion_amtima(descripcionAMTIMA);
		seccional.setDescripcion_uoma(descripcionUOMA);
		seccional.setDestino(destinoCorreo);
		seccional.setHorarioAtencion(horarioAtencion );
		seccional.setNroTarjetaRecargable(nroTarjeta);
		
		Domicilio domicilio = new Domicilio();
		if(seccional.getDomicilio()!=null){
			domicilio=seccional.getDomicilio();
		}
		domicilio.setBarrio(barrio);
		domicilio.setCalle(calle);
		domicilio.setDepto(dpto);
		Localidad localidad = new Localidad();
		localidad.setId(localidadId);
		domicilio.setLocalidad(localidad);
		domicilio.setLocalidadId(localidadId);
        domicilio.setNumero(numero);
        domicilio.setPiso(piso);
        domicilio.setPostal_codi(codPostal);
        Provincia provincia=new Provincia();
        provincia.setId(provinciaId);
        domicilio.setProvincia(provincia);
        seccional.setDomicilio(domicilio);
		seccional.setImaginaria(imaginaria?1:0);
		seccional.setObservaciones(observaciones);
		seccional.setOspim(ospim);
		seccional.setTipo(tipo);
		seccional.setUoma(uoma);
		seccional.setVigen_fecha(fechaVigencia);
		if(idSeccional!=0)
	 	    seccional.setId_seccional(idSeccional);
		
		
		
	}
	
	
	private long updateSeccional(Seccional seccional, String user) throws Exception{
		long id = 0;
		
		id = SeccionalServiceUtil.update(seccional, user);
		return id;
	}
	
	private long addSeccional(Seccional seccional, String user) throws Exception{
		long id = 0;
		
		id = SeccionalServiceUtil.add(seccional, user);
		return id;
	}
	
	
	
	private Contacto getContactoPersonal(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Contacto contacto = new Contacto();

		int idContacto= ParamUtil.getInteger(renderRequest, "idContactoC");
		String tel_numero = ParamUtil.getString(renderRequest, "tel_numero");
		String tel_tipo = ParamUtil.getString(renderRequest, "tel_tipo");
		String cargoC = ParamUtil.getString(renderRequest, "cargoC");
		String cargoD = ParamUtil.getString(renderRequest, "cargoD");
		String nomyape = ParamUtil.getString(renderRequest, "nomyape");
		String esEdicion = ParamUtil.getString(renderRequest, "edicion_contacto_personal");
		String tel_cod_area = ParamUtil.getString(renderRequest, "codarea");
		
		contacto.setProfesion(esEdicion);
		
		Telefono tel = new Telefono();
		tel.setNumero(tel_numero);
		tel.setTipo(tel_tipo);
		tel.setId(idContacto);
		tel.setCodigoArea(tel_cod_area);
		contacto.setTelefono(tel);
		
		ContactoElectronico ce = new ContactoElectronico();
		ce.setId(idContacto);
		contacto.setContacto(ce);

		contacto.setCargo(cargoC);
		contacto.setCargoDescripcion(cargoD);
		if("null".equalsIgnoreCase(nomyape) || "".equalsIgnoreCase(nomyape)){
			contacto.setNombreApe(null);
		}else{
		    contacto.setNombreApe(nomyape);
		}

		return contacto;
	}	

}
