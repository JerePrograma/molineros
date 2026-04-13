/**
 */

package ar.com.ospim.crm.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.TelefonoServiceUtil;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.NoAfiliado;
import ar.com.ospim.crm.beans.TipoReclamo;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 * 
 */
public class EditarDocumentoLegalCRMAction extends PortletAction {
	
	private static Log logger = LogFactoryUtil.getLog(EditarDocumentoLegalCRMAction.class);
	private final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; 

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		User usuario = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		boolean esNoAfiliado = ParamUtil.getBoolean(renderRequest, "noAfiliado"); //, true);
		DocumentoLegalCRM dlCRM = null;
		
		String cuilTitular = null;
		Integer inte = null;
		Boolean cambiarAfiliado = false;
		Integer id = null;
		String msg = "";
//		String tabs1 = "informacion_general";
		String tabs1 = ParamUtil.getString(renderRequest, "tabs1");
		
		if(StringUtils.checkEmpty(tabs1)){
			tabs1 = "informacion_general";
		}
		renderRequest.setAttribute("tabs1", tabs1.toString());	
		
		cargarListas(session);
		
//		manejo de solapa imagenes
		if(tabs1.equalsIgnoreCase("imagenes_afiliados") && (cmd ==null || !cmd.equalsIgnoreCase(Constants.VIEW)) ){
			dlCRM = (DocumentoLegalCRM) session.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
			if(dlCRM==null){
				//no empezo a cargar nada...
				renderRequest.setAttribute("tabs1", "informacion_general");
				renderRequest.setAttribute(Constants.CMD, Constants.SAVE );
				cmd = Constants.ADD;
			}else{
				renderRequest.setAttribute(Constants.CMD, cmd );
			}
		}
//		fin manejo de solapa imagenes
		if(!ar.com.ospim.util.StringUtils.checkEmpty(cmd)){
			
			cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
			inte = ParamUtil.getInteger(renderRequest, "integ");
			
			
			//DS Agregado porque se pierde cuil titular con la navegación entre las pestañas
			if(cuilTitular==null || cuilTitular.isEmpty()) {
			    Afiliado afi= (Afiliado) renderRequest.getPortletSession().getAttribute(WebKeysCrm.CRM_AFILIADO);
			    if(afi!=null) {
			      cuilTitular=afi.getCuil_titular();
			      inte=afi.getInte();
			    }  
			}    
			//DS -- Fin Agregado
			
			
			
			try{
				logger.debug("CRM DL Afiliado: " + cuilTitular+"/"+inte);
				logger.debug("CRM DL usu carga: " + usuario.getScreenName());
			}catch (Exception e) {
				logger.debug("CRM DL usu carga: " + usuario.getScreenName());
				logger.debug("CRM DL afiliado no paso al render");

			}
			
			if(cmd.equalsIgnoreCase(Constants.ADD) ){ // prepara un reclamo en blanco (vacio)
				logger.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				renderRequest.setAttribute(WebKeysCrm.CRM_ES_AFILIADO, !esNoAfiliado); // parametro de alta nuevo reclamo
				
				cambiarAfiliado = ParamUtil.getBoolean(renderRequest, "cambiarAfiliado");

				// si la marca de cambiar afiliado es true, hay que redirigir a la busqueda de afiliados, 
				// sino, mantengo el mismo cuil_titular e inte que se selecciono previamente.
				if(cambiarAfiliado){
					renderRequest.setAttribute("tabs1", "afiliados");

					return mapping.findForward(getForward(renderRequest, "portlet.afiliados.view"));
				}
				renderRequest.setAttribute(Constants.CMD, Constants.ADD);
			}
			
			if(cmd.equalsIgnoreCase(Constants.SAVE) ){ // inserta nuevo 
				logger.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				 dlCRM = this.getDocumentoLegalFromRequest(renderRequest);
				 
				 id = CrmServiceUtil.insertaDocumentoLegal(dlCRM, usuario.getScreenName(),
						 String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()) );
						 
				 
				 dlCRM = CrmServiceUtil.buscarReclamoCRM(id);
				  
				 msg = LanguageUtil.get(defaultLocale, "insert-crm-doc-legal");
				 
				 msg = msg + " " + dlCRM.getId();
				 
				 SessionMessages.add(renderRequest, "insertReclamoOk");
				
				 logger.debug("Usuario: " + usuario.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + id);

				 esNoAfiliado = (dlCRM.getAfiliado() == null);
				 renderRequest.setAttribute(WebKeysCrm.CRM_ES_AFILIADO, !esNoAfiliado);
				 renderRequest.setAttribute("msgReclamoOk", msg);
				 
				 renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);

			}
	
			if(cmd.equalsIgnoreCase(Constants.UPDATE) 
					&& tabs1.equalsIgnoreCase("informacion_general") ){ // actualiza si no esta en imagenes
				logger.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				 dlCRM = this.getDocumentoLegalFromRequest(renderRequest);
				 //DS - 2024-08-20 Agregado porque traia id=0 y pinchaba el codigo
				 DocumentoLegalCRM dlCRMSession = (DocumentoLegalCRM) session.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
				 if(dlCRMSession.getId()!=0 &&dlCRM.getId()==0) {
					 dlCRM =dlCRMSession;
					 cuilTitular=dlCRM.getAfiliado().getCuil_titular();
					 inte=dlCRM.getAfiliado().getInte() ;
				 }
	             //DS - Fin agregado
				 id = dlCRM.getId();
				 
				 CrmServiceUtil.actualizadocumentoLegal(dlCRM, usuario.getScreenName(),
						 String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId())); 
						 
				 
				 dlCRM = CrmServiceUtil.buscarReclamoCRM(id);
 
				 msg = LanguageUtil.get(defaultLocale, "update-crm-doc-legal");
				 
				 msg = msg + " " + dlCRM.getId();
				 
				 SessionMessages.add(renderRequest, "updateReclamoOk");
				
				 logger.debug("Usuario: " + usuario.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + id);

				 esNoAfiliado = (dlCRM.getAfiliado() == null);
				 renderRequest.setAttribute(WebKeysCrm.CRM_ES_AFILIADO, !esNoAfiliado);
				 renderRequest.setAttribute("msgReclamoOk", msg);
				 
				 renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);

			}
			
			if(cmd.equalsIgnoreCase(Constants.EDIT) ){ // Preapara para edicion un reclamo. 
				logger.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
	
				id = ParamUtil.getInteger(renderRequest, "id");

    			dlCRM = CrmServiceUtil.buscarReclamoCRM(id);
    			
    			esNoAfiliado = (dlCRM.getAfiliado() == null);
    			
    			renderRequest.setAttribute(WebKeysCrm.CRM_ES_AFILIADO, !esNoAfiliado);
    			
				renderRequest.setAttribute(Constants.CMD, Constants.UPDATE);
			}
			
			if(cmd.equalsIgnoreCase(Constants.VIEW) ){ // Prepara popup view un reclamo.
				logger.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
	
				id = ParamUtil.getInteger(renderRequest, "id",0);
				
				String esPopupLlamada = ParamUtil.getString(renderRequest, "esPopup");
				
				renderRequest.setAttribute(Constants.CMD, Constants.VIEW);

				dlCRM = CrmServiceUtil.buscarReclamoCRM(id);
				
				esNoAfiliado = (dlCRM.getAfiliado() == null);
				
				renderRequest.setAttribute(WebKeysCrm.CRM_ES_AFILIADO, !esNoAfiliado);
				
				renderRequest.setAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW, dlCRM);
				
				renderRequest.setAttribute("desdePopup", esPopupLlamada);
				
				return mapping.findForward("portlet.crm.view_docum_legal");
			}
//				
//			if(cmd.equalsIgnoreCase(Constants.EDIT) ){
//				
//				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
//				inte = ParamUtil.getString(renderRequest, "inte");
////				
////				preAfi = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte));
//				id = ParamUtil.getInteger(renderRequest, "id");
//				if(id == 0){ // formazamos esto para afiliados del padron...
//					id = null;
//				}
//				dlCRM = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte),id);
//				
//				if(id == null || id==0){
//					renderRequest.setAttribute(Constants.CMD, Constants.SAVE );
//					renderRequest.setAttribute("tipo_novedad_pre_afi", Constants.UPDATE);
//				}else{
//					renderRequest.setAttribute(Constants.CMD, Constants.UPDATE );
//				}
//				
//		
//				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
//				session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, dlCRM);
//				
//			}
//			
//			if(cmd.equalsIgnoreCase(Constants.DELETE) ){
//				
//				cuil = ParamUtil.getString(renderRequest, "cuil_titular");
//				inte = ParamUtil.getString(renderRequest, "inte");
//				id = ParamUtil.getInteger(renderRequest, "idPreAfi");
//				boolean esCascada = ParamUtil.getBoolean(renderRequest, "esCascada");
//				
//				PreAfiliadoServiceUtil.borrarPreAfiliado(cuil, Integer.valueOf(inte), id, esCascada, user);
//				
////				vuelvo a aplicar la busqueda para que desaparezcan los de baja
//
//				BusquedaPreAfiliadosFiltro filtro = (BusquedaPreAfiliadosFiltro) 
//					session.getAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS);
//				
//				List<PreAfiliadoTotal> busqueda = PreAfiliadoServiceUtil.getBusquedaPreAfiliados(filtro);
//				
//				int cantResultados = busqueda.size()>0?busqueda.get(0).getTotal_registros():0;
//
//				session.removeAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO);
//				session.setAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO, busqueda);
//				
//				if(busqueda != null && busqueda.size() > 0){
//					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS, cantResultados);
//					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, 1); //pagina_sel
//				}else{
//					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_TOTAL_REGISTROS,0 );
//					session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_PREAFILIADOS_OFFSET_REG, 0);
//				}
//				return mapping.findForward("portlet.pre.carga.afiliados.result.search");
//
//			}
//			
//			if(cmd.equalsIgnoreCase(Constants.VIEW) ){
//				
//				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
//				inte = ParamUtil.getString(renderRequest, "inte");
//				id = ParamUtil.getInteger(renderRequest, "id");
//				
//				dlCRM = PreAfiliadoServiceUtil.buscarPreAfiliado(cuil_titular, Integer.parseInt(inte), id);
//
//				renderRequest.setAttribute(Constants.CMD, Constants.VIEW );
//		
//				session.removeAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
//				session.setAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION, dlCRM);
//				
//				return mapping.findForward(getForward(renderRequest, "portlet.novedades.view_pre_afiliado"));
//			}
			

			if(!esNoAfiliado && tabs1.equalsIgnoreCase("informacion_general") ){
				cargarAfiliadoyUltimosReclamos(renderRequest, cuilTitular, inte);
			}
//			renderRequest.setAttribute(WebKeysCrm.PORTLET_TAB_CRM_CONTACTO, "SI");
//			renderRequest.setAttribute("tabs1", "contactos");
			session.setAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION, dlCRM);

		}else{
//			renderRequest.setAttribute("tabs1", "afiliados");
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.crm.editar_docum_legal"));
		
	}
	
	private void cargarAfiliadoyUltimosReclamos(RenderRequest renderRequest, String cuilTitular, Integer inte) 
			throws Exception, SystemException {

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = null;
		Date fechaHasta = null;
		Calendar cal = Calendar.getInstance();
		Afiliado afiliado = null;
		List<DocumentoLegalCRM> ultimosReclamos;
		
		String fechaDesdeDia=ParamUtil.getString(renderRequest, "fechaDesdeDia");
		String fechaDesdeMes=ParamUtil.getString(renderRequest, "fechaDesdeMes");
		String fechaDesdeAnio=ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		String fechaHastaDia=ParamUtil.getString(renderRequest, "fechaHastaDia");
		String fechaHastaMes=ParamUtil.getString(renderRequest, "fechaHastaMes");
		String fechaHastaAnio=ParamUtil.getString(renderRequest, "fechaHastaAnio");

		try {
			fechaHasta = formatoDeFecha.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = cal.getTime();
		}
		try {
			fechaDesde = formatoDeFecha.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
//			cal.add(Calendar.DATE, -14);
//			cal.set(Calendar.DATE, 1);
//			cal.add(Calendar.DATE, -30);
			cal.add(Calendar.YEAR, -10);
			fechaDesde = cal.getTime();
		}
		Domicilio domicilio = null;
		try{
			User usuario = PortalUtil.getUser(renderRequest);

			logger.debug("CRM DL Afiliado: " + cuilTitular+"/"+inte);
			logger.debug("CRM DL usu carga: " + usuario.getScreenName());

			List<Afiliado> busqueda = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(cuilTitular, String.valueOf(inte), 
				null, null, 0, null, null, "0", 0, 0, new BigDecimal(0)); // antes entidad era null- el 0 corresponde a O.S.P.I.M.
			afiliado = busqueda.get(0); // siempre debe venir 1 al menos...
			List<Domicilio> domicilios = BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(cuilTitular, 0);			
			domicilio = domicilios.get(0);
		}catch (Exception e) {
			logger.error(e);

		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
//;		c.add(Calendar.DATE, -90);
//		long diferencia = (domicilio.getModi_fecha().getTime() - c.getTimeInMillis() )/MILLSECS_PER_DAY; 
		long diferencia = (c.getTimeInMillis() - domicilio.getModi_fecha().getTime() )/MILLSECS_PER_DAY;
		
		List<Telefono> telefonos = TelefonoServiceUtil.getTelefonos(cuilTitular, inte);

		boolean tieneFijo = false;
		boolean tieneCelular = false;

		for (Telefono t : telefonos) {
		    if ("F".equalsIgnoreCase(t.getTipo()) && StringUtils.checkNotEmpty(t.getNumero())) {
		        tieneFijo = true;
		    }
		    if ("C".equalsIgnoreCase(t.getTipo()) && StringUtils.checkNotEmpty(t.getNumero())) {
		        tieneCelular = true;
		    }
		}
		
		/*Solo muestro el domicilio si esta 90 dias sin verificar*/
		if(Math.abs(diferencia) > 90 || !tieneFijo || !tieneCelular){
			renderRequest.setAttribute(WebKeysCrm.CRM_AFILIADO_DOMICILIO, domicilio);
			renderRequest.setAttribute(WebKeysCrm.CRM_AFILIADO_EMAIL, afiliado.getEmail());
		}
		ultimosReclamos = CrmServiceUtil.buscarUltimosReclamosCRM(cuilTitular, inte, fechaDesde, fechaHasta);
		
		renderRequest.setAttribute(WebKeysCrm.CRM_AFILIADO, afiliado);
		renderRequest.setAttribute(WebKeysCrm.CRM_ULTIMOS_DOCUM_LEGAL, ultimosReclamos);
		
		
        renderRequest.getPortletSession().setAttribute(WebKeysCrm.CRM_AFILIADO, afiliado);
	}	
	
	private void cargarListas(HttpSession session){
		
		boolean estanPreCargadasLasListas = session.getAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO)!=null 
				&& session.getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS)!=null;
		
		if(!estanPreCargadasLasListas){
			try{
				session.setAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO, CrmServiceUtil.buscarTiposReclamo());
				session.setAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS, CrmServiceUtil.buscarMotivosDocumentoLegal());
			}catch (SystemException e) {
				logger.error("NO se pudieron cargas las listas ", e);
			}	
		}
//		no es en que momento sacarlas de memoria... pero son poquitos datos igualmete
	}
	
	private DocumentoLegalCRM getDocumentoLegalFromRequest(RenderRequest renderRequest){
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		DocumentoLegalCRM documLegal = new DocumentoLegalCRM();

		Afiliado afi = null;
		NoAfiliado noAfi = null;
		
		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
//		String inte = ParamUtil.getString(renderRequest, "inte");
		String inte = ParamUtil.getString(renderRequest, "integ");
		String nombre = ParamUtil.getString(renderRequest, "noafi_nombre");
		String apellido = ParamUtil.getString(renderRequest, "noafi_apellido");
		String docuTipo = ParamUtil.getString(renderRequest, "noafi_documento_tipo");
		String docuNumero = ParamUtil.getString(renderRequest, "noafi_documento_nro");
		String email = ParamUtil.getString(renderRequest, "noafi_email");
		String telefono = ParamUtil.getString(renderRequest, "noafi_telefono");
		
		int id = ParamUtil.getInteger(renderRequest, "id", 0);
		int idTipo = ParamUtil.getInteger(renderRequest, "tipo_reclamo", 0);
		int idMotivo = ParamUtil.getInteger(renderRequest, "motivo_reclamo", 0);
		
		String fechaNotifDia = ParamUtil.getString(renderRequest, "notificacionFechaDia");
		String fechaNotifMes = ParamUtil.getString(renderRequest, "notificacionFechaMes");
		String fechaNotifAnio = ParamUtil.getString(renderRequest, "notificacionFechaAnio");

		Date fechaNotificacion = null;
		try {
			fechaNotificacion = formatoDeFecha.parse(fechaNotifDia + "/"
					+ (Integer.parseInt(fechaNotifMes) + 1) + "/" + fechaNotifAnio);
		} catch (Exception e) {
			fechaNotificacion = null;
		}
		String descripcion = ParamUtil.getString(renderRequest, "descripcion_reclamo", ""); 

		String fechaVencDia = ParamUtil.getString(renderRequest, "vtoFechaDia");
		String fechaVencMes = ParamUtil.getString(renderRequest, "vtoFechaMes");
		String fechaVencAnio = ParamUtil.getString(renderRequest, "vtoFechaAnio");

		Date fechaVencimiento = null;
		try {
			fechaVencimiento = formatoDeFecha.parse(fechaVencDia + "/"
					+ (Integer.parseInt(fechaVencMes) + 1) + "/" + fechaVencAnio);
		} catch (Exception e) {
			fechaVencimiento = null;
		}
		String fechaRtaDia = ParamUtil.getString(renderRequest, "rtaFechaDia");
		String fechaRtaMes = ParamUtil.getString(renderRequest, "rtaFechaMes");
		String fechaRtaAnio = ParamUtil.getString(renderRequest, "rtaFechaAnio");

		Date fechaRespuesta = null;
		try {
			fechaRespuesta = formatoDeFecha.parse(fechaRtaDia + "/"
					+ (Integer.parseInt(fechaRtaMes) + 1) + "/" + fechaRtaAnio);
		} catch (Exception e) {
			fechaRespuesta = null;
		}
		String fechaAvisoDia = ParamUtil.getString(renderRequest, "avisoFechaDia");
		String fechaAvisoMes = ParamUtil.getString(renderRequest, "avisoFechaMes");
		String fechaAvisoAnio = ParamUtil.getString(renderRequest, "avisoFechaAnio");

		Date fechaAvisoEstudio = null;
		try {
			fechaAvisoEstudio = formatoDeFecha.parse(fechaAvisoDia + "/"
					+ (Integer.parseInt(fechaAvisoMes) + 1) + "/" + fechaAvisoAnio);
		} catch (Exception e) {
			fechaAvisoEstudio = null;
		}
		String fechaContacDia = ParamUtil.getString(renderRequest, "contactoFechaDia");
		String fechaContacMes = ParamUtil.getString(renderRequest, "contactoFechaMes");
		String fechaContacAnio = ParamUtil.getString(renderRequest, "contactoFechaAnio");

		Date fechaContactoPS_OM = null;
		try {
			fechaContactoPS_OM = formatoDeFecha.parse(fechaContacDia + "/"
					+ (Integer.parseInt(fechaContacMes) + 1) + "/" + fechaContacAnio);
		} catch (Exception e) {
			fechaContactoPS_OM = null;
		}
		String radicacion = ParamUtil.getString(renderRequest, "radicacion_reclamo", ""); 
		BigDecimal importeReclamado = null;
		try{
			importeReclamado = new BigDecimal(ParamUtil.getDouble(renderRequest, "importe_reclamo", 0)); 
		}catch (NumberFormatException e) {
//			nada, no hay importe
		}
		
		String nroTramite = ParamUtil.getString(renderRequest, "tramite_reclamo", ""); 
		String expediente = ParamUtil.getString(renderRequest, "expediente_reclamo", ""); 
		String resolucion = ParamUtil.getString(renderRequest, "resolucion_reclamo", ""); 
		String descripcionSolucion = ParamUtil.getString(renderRequest, "comentarios_cierre", "");
		String descripcionEstudio = ParamUtil.getString(renderRequest, "comentarios_estudio", "");
		boolean antecedente = false;
		if(ParamUtil.getString(renderRequest,"antecedente")!=null && ParamUtil.getString(renderRequest,"antecedente").equalsIgnoreCase("on")){
			antecedente = true;
		}
		boolean concluido = false;
		if(ParamUtil.getString(renderRequest,"concluido")!=null && ParamUtil.getString(renderRequest,"concluido").equalsIgnoreCase("on")){
			concluido = true;
		}
		Integer tramiteNumero = null;
		try{
			tramiteNumero = Integer.parseInt(nroTramite);
		}catch (NumberFormatException e) {
//			nada, no hay nro de tramite
		}
		
//		analizamos si se cargo un Afiliado o un NoAfiliado
		if(!StringUtils.checkEmpty(cuilTitular)){
			afi = new Afiliado(cuilTitular, Integer.parseInt(inte));
		}else{
			noAfi = new NoAfiliado(docuTipo, docuNumero, apellido, nombre, telefono, email, null, null);
		}
			
		MotivoContacto mot = new MotivoContacto(idMotivo, "");
		TipoReclamo tip = new TipoReclamo(idTipo, "");
		
		documLegal.setAfiliado(afi);
		documLegal.setNoAfiliado(noAfi);
		documLegal.setDescripcion(descripcion);
		documLegal.setDescripcionEstudio(descripcionEstudio);
		documLegal.setDescripcionSolucion(descripcionSolucion);
		documLegal.setExpediente(expediente);
		documLegal.setFechaAvisoAlEstudio(fechaAvisoEstudio);
		documLegal.setFechaContactoPSOM(fechaContactoPS_OM);
		documLegal.setFechaNotificacion(fechaNotificacion);
		documLegal.setFechaRespuesta(fechaRespuesta);
		documLegal.setFechaVencimiento(fechaVencimiento);
		documLegal.setId(id);
		documLegal.setImporteReclamado(importeReclamado);
		documLegal.setMotivo(mot);
		documLegal.setRadicacion(radicacion);
		documLegal.setResolucion(resolucion);
		documLegal.setTipo(tip);
		documLegal.setTramiteNumero(tramiteNumero);
		documLegal.setTieneAntecedentes(antecedente);
		documLegal.setConcluido(concluido);
		

		return documLegal;
	}
		
}