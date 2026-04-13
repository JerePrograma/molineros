package ar.com.ospim.crm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.crm.beans.BusquedaContactoFiltro;
import ar.com.ospim.crm.beans.BusquedaDocumLegalFiltro;
import ar.com.ospim.crm.beans.CRMEficacia;
import ar.com.ospim.crm.beans.CRMEstadistica;
import ar.com.ospim.crm.beans.CRMEstadisticaCierre;
import ar.com.ospim.crm.beans.CRMEstadisticaRendimiento;
import ar.com.ospim.crm.beans.CategoriaContacto;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.ContactoCRMTotal;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.beans.DerivacionSeguimiento;
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.crm.beans.DocumentoLegalCRMTotal;
import ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.TipoContacto;
import ar.com.ospim.crm.beans.TipoReclamo;
import ar.com.ospim.mail.MailUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.sql.Connection;

/**
 * @author sva
 * 
 */
public class CrmServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(CrmServiceUtil.class);
	
	private static CrmServiceImpl instance = null;

	public static CrmServiceImpl getInstance() {
		if (null == instance) {
			instance = new CrmServiceImpl();
		}
		return instance;
	}

	public static List<CategoriaContacto> buscarCategoriasContacto()
			throws SystemException {
		
		return getInstance().buscarCategoriasContacto();
	}

	public static List<TipoContacto> buscarTiposContacto()
			throws SystemException {
		
		return getInstance().buscarTiposContacto();
	}
	
	public static List<MotivoContacto> buscarMotivosContacto()
			throws SystemException {
		
		return getInstance().buscarMotivosContacto();
	}
	
	public static int insertaContacto(ContactoCRM contacto, String screenName, String sector ,Connection connectionParameter ) 
			throws SystemException {
		
		return getInstance().insertaContacto(contacto, screenName, sector , connectionParameter );
	}
	
	public static int actualizaContacto(ContactoCRM contacto, String screenName, String sector) 
			throws SystemException {
		
		return getInstance().actualizaContacto(contacto, screenName, sector);
	}
		
	public static ContactoCRM buscarContactoCRM(int id)
			throws SystemException {
		
		return getInstance().buscarContactoCRM(id);
	}
	
	public static ContactoCRM buscarContactoCRMbyIdContacto(int idContacto) 
			throws SystemException {
		
		return getInstance().buscarContactoCRMbyIdContacto(idContacto);
	}

	
	public static List<ContactoCRM> buscarUltimosContactosCRMxidReclamo(String cuilTitular, int inte, int id_reclamo ) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRM_idreclamo_cuil_inte(id_reclamo , cuilTitular, inte);
	}
	
	public static List<ContactoCRM> buscarUltimosContactosCRMconDataReclamo(String cuilTitular, int inte) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRMconDataReclamo(cuilTitular, inte);
		
	}
	public static List<ContactoCRM> buscarUltimosContactosCRMSoloAsociados(String cuilTitular, int inte, int idreclamoprestacional ) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRM_idreclamo_cuil_inte_solo_asociados(idreclamoprestacional ,cuilTitular, inte);
		
	}
	public static List<ContactoCRM> buscarUltimosContactosCRMconDataReclamo(String cuilTitular, int inte, int idreclamoprestacional ) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRM_idreclamo_cuil_inte(idreclamoprestacional ,cuilTitular, inte);
		
	}
	
	public static List<ContactoCRM> buscarUltimosContactosCRM(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRM(cuilTitular, inte, fechaDesde, fechaHasta);
	}
	
	public static List<ContactoCRM> buscarUltimosContactosCRMSeccional(Integer contactoseccional, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRMSeccional(contactoseccional, fechaDesde, fechaHasta);
	}
	
	public static List<ContactoCRM> buscarUltimosContactosCRM(EdificioSectorUsuarioLiferay companiero, String usuario, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarUltimosContactosCRM(companiero, usuario, fechaDesde, fechaHasta);
			
	}
	
	public static String insertaDerivacion(int idContacto, int importancia, EdificioSectorUsuarioLiferay derivacion, String observaciones, String screenName, String sector) {
		
		String sectorDescripcion = "";
		
		List<String> usuariosDestinatarios = new ArrayList<String>();
		List<String> responsableDestinatarios = new ArrayList<String>();

		String body = "Ud. tiene un contacto derivado a su Bandeja de Entrada ";
		String mensajeRespuesta= " - No se encontro datos para derivacion del usuario: "+ derivacion.getUsuario();
				mensajeRespuesta = mensajeRespuesta + " Comuníquese con Sistemas"	;
		
		DerivacionNotificacion dn = null;
		List<DerivacionNotificacion> notificaciones = new ArrayList<DerivacionNotificacion>();
		
//		insertamos derivacion y recuperamos los datos para notificar
		try {
			notificaciones = getInstance().insertaDerivacion(idContacto, derivacion, observaciones, screenName, sector);
			
			sectorDescripcion = UserGroupLocalServiceUtil.getUserGroup(Long.parseLong(derivacion.getGrupo())).getName();
			
		} catch (SystemException e) {
			_log.error(e);
			return mensajeRespuesta;
		} catch (NumberFormatException e) {
			_log.error(e);
			return mensajeRespuesta;
		} catch (PortalException e) {
			_log.error(e);
			return mensajeRespuesta;
		} 
		
		if(notificaciones!=null && notificaciones.size()>0){
		
			dn = notificaciones.get(0);
			
			if(dn.getDerivacionMensaje()!=null && dn.getDerivacionMensaje().trim().length()>0 
					&& !derivacion.getUsuario().equalsIgnoreCase("TODOS")){
		
				body = dn.getDerivacionMensaje();
				body = body + " " +idContacto; // ej: En su bandeja de entrada tiene derivado el contacto n° 1000
			}else{ // este mensaje es para una derivacion de sector (todos los usuarios del sector)
				body = "En la bandeja de entrada, todos los usuarios del sector "+ sectorDescripcion + 
						" tienen derivado el contacto n° ";
				body = body + idContacto; // ej: En su bandeja de entrada tiene derivado el contacto n° 1000
			}
//			Mandamos correos al usuario / todos los usuarios del grupo
			for (Iterator<DerivacionNotificacion> iterator = notificaciones.iterator(); iterator.hasNext();) {
				DerivacionNotificacion dnAux = iterator.next();
				
				if(!dn.getResponsableUsr().equalsIgnoreCase(dnAux.getDerivacionUsr())){  // para no mandar repetido al jefe del sector si esta en el grupo
					usuariosDestinatarios.add(dnAux.getDerivacionEmail());
				}
			}
						
//			MailUtils.enviarMailGmailSinAdj("info@ospim.org.ar", 
//					"ospim1234", 
//					destinatarios, 
//					importancia==1?"URGENTE! - Notificación de CRM":"Notificación de CRM",
//					body,
//					null);
			

//DS 20230307 - Subsanar error envio mail gmail			
			List<String> email=new ArrayList<String>();
			for(String s:usuariosDestinatarios) {
			   email.clear();
			   email.add(s);
			   EnviaEmailsThread.enviarMailDesatendido(importancia==1?"URGENTE! - Notificación de CRM":"Notificación de CRM",body,email,importancia);
			}   
//
			
//DS - 20230307 Comentado para subsanar error envio de mail gmail.
//			EnviaEmailsThread.enviarMailDesatendido(importancia==1?"URGENTE! - Notificación de CRM":"Notificación de CRM",body,usuariosDestinatarios,importancia);
			
			User userLiferay = null;
			try {
				 
//				long idUser =  UserServiceUtil.getUserIdByScreenName(Long.parseLong("10112"), dn.getDerivacionUsr());  // companyId =10112, para todos es la misma.
				long idUser =  UserLocalServiceUtil.getUserIdByScreenName(Long.parseLong("10112"), dn.getDerivacionUsr());  // companyId =10112, para todos es la misma.

//				userLiferay = UserServiceUtil.getUserById(idUser);
				userLiferay = UserLocalServiceUtil.getUserById(idUser);
				
				if(!derivacion.getUsuario().equalsIgnoreCase("TODOS")){
					mensajeRespuesta = " - Se notificó correctamente a: " + userLiferay.getFullName();
				}else{
					mensajeRespuesta = " - Se notificó correctamente a los usuarios del sector: " + sectorDescripcion.toUpperCase();
				}
			} catch (Exception e) {
				_log.error(e);
				mensajeRespuesta = " - Fallo la notificación a: " + dn.getDerivacionUsr();
			}
			
//mensaje al responsable del area
/* 		Comentado el 13/07/2022	
			String [] listas=null;
			if(null!=dn.getResponsableEmail()){
				listas=dn.getResponsableEmail().split(";");
			}
			for (int i = 0; i < listas.length; i++) {
				responsableDestinatarios.add(listas[i]);
			} 
			if(!derivacion.getUsuario().equalsIgnoreCase("TODOS")){
				body = "El usuario " + userLiferay.getFullName() + " tiene derivado el contacto n° " + idContacto;
			}else{
				body = "Todos los usuarios del sector "+ sectorDescripcion.toUpperCase() + 
						" tienen derivado el contacto n° " + idContacto;
			}
			
			MailUtils.enviarMailGmailSinAdj("info@ospim.org.ar", 
					"ospim12345", 
					responsableDestinatarios, 
					importancia==1?"URGENTE! - Notificación de CRM":"Notificación de CRM",
					body,
					null);
			
*/
		}
		
		return mensajeRespuesta;
	}
	
//	Bandeja de entrada de correspondencia
	public static void insertarNotificacionInbox(ContactoCRM contacto, EdificioSectorUsuarioLiferay derivaUsuario, User usuario){
		
		long numeroCorrespondencia = 0, idItemCorrespondencia=0;
		ItemCorrespondencia item = null;
		String tipoEnvio = WebKeysCorrespondencia.TIPOS_ENVIOS[0][0];
		String tipoRegistro = "ENTRADA";
		String tipoRemitente = null, cuilTitular = null, nombreAfiliado=null, apellidoAfiliado=null, descPrestador="", otros=null,
				cuitEntidad=null, sucursalEntidad=null, descEmpresa="";
		int idPrestador = 0, inte = 0;   
		
		if(contacto.getAfiliado() != null) { // ingreso por AFILIADO
			tipoRemitente = "AFILIADO";
			cuilTitular=contacto.getAfiliado().getCuil_titular();
			inte=contacto.getAfiliado().getInte();
			nombreAfiliado=contacto.getAfiliado().getNombre();
			apellidoAfiliado=contacto.getAfiliado().getApellido();
		}
		if(contacto.getNoAfiliado()!=null) { // ingreso por NO AFILIADO
			tipoRemitente = "OTROS";
			otros="NO Afiliado " +contacto.getNoAfiliado().getApellido() + " , " +contacto.getNoAfiliado().getNombre() + " " +
					contacto.getNoAfiliado().getDocumentoTipo()+ " N° " + contacto.getNoAfiliado().getDocumentoNumero();
		}
		if(contacto.getContactoSeccional()!=null) { // ingreso por CONTACTO DE LA SECIONAL
			tipoRemitente = "OTROS";
			otros = contacto.getContactoSeccional().getSeccional().getDescripcion() +" - " + contacto.getContactoSeccional().getNombreApe();
//			no utilizamos id_seccional porque sino la descripción no mostraría el contacto que llamo de la seccional y 
//			sería siempre visible la desc de la seccional
		}
		if(contacto.getPrestador()!=null) {
			tipoRemitente="PRESTADOR";
			idPrestador=contacto.getPrestador().getId_prestador();
			descPrestador=contacto.getPrestador().getDescripcion();
		}
		if(contacto.getEmpresa()!=null) {
			tipoRemitente="PROVEEDOR";
			cuitEntidad=contacto.getEmpresa().getCuit();
			sucursalEntidad=contacto.getEmpresa().getSucursal();
			descEmpresa=contacto.getEmpresa().getRazon_soc();
		}
		if(contacto.getCompaniero()!=null) {
			tipoRemitente = "OTROS";
			otros = "PEDIDO INTERNO";
		}
		
		try {
			numeroCorrespondencia = CorrespondenciaServiceUtil.insertaCabeceraCorrespondencia(String.valueOf(usuario.getOrganizations().get(0).getOrganizationId()), 
					new Date(), 0, tipoRegistro, tipoEnvio, "", usuario.getScreenName());
					
			item = new ItemCorrespondencia(numeroCorrespondencia, tipoRegistro, 0, tipoEnvio, tipoRemitente, 
					cuilTitular, inte, nombreAfiliado, apellidoAfiliado, "0", "0", "", otros, idPrestador, descPrestador, 
					cuitEntidad, sucursalEntidad, descEmpresa, 0, "", null, null, 0, null, null, null, derivaUsuario.getEdificio(), derivaUsuario.getUsuario() ,derivaUsuario.getGrupo(),  
					String.valueOf(usuario.getOrganizations().get(0).getOrganizationId()) , String.valueOf(usuario.getUserGroups().get(0).getUserGroupId()), 
					usuario.getScreenName() , contacto.getDescripcion(), new Date(), new Date(), null, contacto.getIdContacto());
			item.setEstado(WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[0]); // "INGRESADO"	
			
			idItemCorrespondencia = CorrespondenciaServiceUtil.cargaCorrespondenciaDetalleEntry(item,usuario);

		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	public static List<ContactoCRM> buscarHistoricoContactosAfi(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarHistoricoContactosAfi(cuilTitular, inte, fechaDesde, fechaHasta);
	}

	public static List<ContactoCRMTotal> busquedaContactosCRM(BusquedaContactoFiltro filtro, int pagina, User usuario) throws SystemException {
		
		return getInstance().busquedaContactosCRM(filtro, pagina, usuario);
		
	}
	
	public static List<ContactoCRM> busquedaContactosCRMxls(BusquedaContactoFiltro filtro, User usuario) throws SystemException {
		
		return getInstance().busquedaContactosCRMxls(filtro, usuario);
		
	}
	
	public static List<CRMEstadistica> estadisticaAgrupada(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		return getInstance().estadisticaAgrupada(fechaDesde, fechaHasta);
	}
	
	public static List<CRMEstadisticaRendimiento> estadisticaRendimiento(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		return getInstance().estadisticaRendimiento(fechaDesde, fechaHasta);
	}
	
	public static List<CRMEstadisticaCierre> estadisticaCierres(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		return getInstance().estadisticaCierres(fechaDesde, fechaHasta);
	}
	
	public static int insertaEficacia(CRMEficacia eficacia, String screenName, String sector) throws SystemException {
		
		return getInstance().insertaEficacia(eficacia, screenName, sector);
		
	}

	public static DerivacionNotificacion getNotificacionDerivacion(String screenName) throws SystemException {
		
		return getInstance().getNotificacionDerivacion(screenName);
		
	}

	public static List<DerivacionNotificacion> getNotificacionDerivacionSector(String sector) throws SystemException {
		
		return getInstance().getNotificacionDerivacionSector(sector);
		
	}
	
	public static List<DerivacionSeguimiento> buscarSeguimientoContactoCRMbyIdContacto(int idContacto) throws SystemException {
		
		return getInstance().buscarSeguimientoContactoCRMbyIdContacto(idContacto);
		
	}
	
	public static List<MotivoContacto> buscarMotivosDocumentoLegal()
			throws SystemException {
		
		return getInstance().buscarMotivosDocumentoLegal();
	}
	
	public static List<TipoReclamo> buscarTiposReclamo()
			throws SystemException {
		
		return getInstance().buscarTiposReclamo();
	}
	
	public static List<DocumentoLegalCRM> buscarUltimosReclamosCRM(String cuilTitular, int inte, Date fechaDesde, Date fechaHasta) 
			throws SystemException {

		return getInstance().buscarUltimosReclamosCRM(cuilTitular, inte, fechaDesde, fechaHasta);
	}

	public static int insertaDocumentoLegal(DocumentoLegalCRM reclamo, String screenName, String sector) 
			throws SystemException {
		
		return getInstance().insertaDocumentoLegal(reclamo, screenName, sector);
	}
	
	public static DocumentoLegalCRM buscarReclamoCRM(int id)
			throws SystemException {
		
		return getInstance().buscarReclamoCRM(id);
	}
	
	public static int actualizadocumentoLegal(DocumentoLegalCRM reclamo, String screenName, String sector) 
			throws SystemException {
		
		return getInstance().actualizaDocumentoLegal(reclamo, screenName, sector);
	}
	
	public static List<DocumentoLegalCRMTotal> busquedaReclamosCRM(BusquedaDocumLegalFiltro filtro, int pagina) throws SystemException {
		
		return getInstance().busquedaReclamosCRM(filtro, pagina);
		
	}
	
	public static List<DocumentoLegalCRM> busquedaReclamosCRMxls(BusquedaDocumLegalFiltro filtro) throws SystemException {
		
		return getInstance().busquedaReclamosCRMxls(filtro);
		
	}
	
}