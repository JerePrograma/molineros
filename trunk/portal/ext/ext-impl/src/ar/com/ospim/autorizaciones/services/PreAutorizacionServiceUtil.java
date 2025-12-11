package ar.com.ospim.autorizaciones.services;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;

//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.model.Message;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

//import ar.com.global.services.GmailAPIUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.action.UploadArchivoPreautorizacionesAction.ArchivoPrevencion;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.EstadisticaPrestAutorizada;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionLoteProcesado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.beans.RespuestaPreAutorizPSDTO;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

public class PreAutorizacionServiceUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1235349772874991502L;

	private static Log _log = LogFactoryUtil
			.getLog(PreAutorizacionServiceUtil.class);

	private static PreAutorizacionServiceImpl instance = null;

	public static PreAutorizacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new PreAutorizacionServiceImpl();
		}
		return instance;
	}

	
	public static List<PreAutorizacion> getListaPreAutorizacion(BusquedaPreautorizacionesFiltro filtro)
			throws SystemException {
		return getInstance().getListaPreAutorizacion(filtro);
		
	}
	
	public static List<PreAutorizacion> getListaPreAutorizacionExtendido(BusquedaPreautorizacionesFiltro filtro)
			throws SystemException {
		return getInstance().getListaPreAutorizacionExtendido(filtro);
		
	}
	
	public static Integer insertaPreAutorizacion(PreAutorizacion preautorizacion, String screenName,String sector,Integer seccional) throws Exception {
		Integer idPreautorizacion = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Alta del Seguimiento
			idPreautorizacion= getInstance().insertaPreAutorizacion(preautorizacion, screenName,sector,seccional,connection);
			
			getInstance().insertaPreAutorizacionEstado(idPreautorizacion,preautorizacion, screenName, connection);
			
		    //Inserta Prestaciones
			for(PreAutorizacionPrestacion prest : preautorizacion.getCodigosPresentados()){
				getInstance().insertaPreAutorizacionPrestacion(idPreautorizacion, prest, screenName, connection);
			}
			
			 //Inserta Medicamentos
			for(PreAutorizacionMedicamento med : preautorizacion.getMedicamentosPresentados()){
				getInstance().insertaPreAutorizacionMedicamento(idPreautorizacion, med, screenName, connection);
			}
			
			//Actualiza Alojamiento
			getInstance().updatePreAutorizacionAlojamiento(idPreautorizacion,preautorizacion, screenName, connection);
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return idPreautorizacion;
	}
	
	public static  void updateEstadoPreautorizacion(Integer idPreautorizacion, String estado, String screenName) throws SystemException, SQLException{
		PreAutorizacion preautorizacion = new PreAutorizacion();
		
		Estado e =new Estado();
		e.setId(estado);
		preautorizacion.setUltimoEstado(e);
	
		getInstance().insertaPreAutorizacionEstado(idPreautorizacion,preautorizacion, screenName, null);
	}
	
	

	
    
	public static PreAutorizacion buscarPreautorizacionPorId(int id) throws SystemException{
 		return getInstance().buscarPreautorizacionPorId(id, null);
	}
	
    public static long updatePreautorizacion(PreAutorizacion preautorizacion, String screenName) throws Exception {
		Integer idPreautorizacion = preautorizacion.getId(); 
		Integer idPrestacion =0;
		Integer idPreAutMedic =0;
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			PreAutorizacion preautorizacionDB = getInstance().buscarPreautorizacionPorId(preautorizacion.getId(),connection);
			
			//Inserta Prestaciones
//			if(preautorizacion.getCodigosPresentados()!=null){
				//Analiza Altas - apareo con registros existentes en BD			
			if(preautorizacion.getCodigosPresentados()!=null && preautorizacion.getCodigosPresentados().size()>0) {	
				for(PreAutorizacionPrestacion preAutPres : preautorizacion.getCodigosPresentados()){
					Boolean existe=false;
					for(PreAutorizacionPrestacion preAutPresDB : preautorizacionDB.getCodigosPresentados()){
						if(preAutPres.getId().equals(preAutPresDB.getId())){
							existe=true;
							getInstance().updatePreautorizacionPrestacion(preAutPres.getId(), preAutPres, screenName, connection);
							break;
						}
					}
					if(!existe){
					   idPrestacion=getInstance().insertaPreAutorizacionPrestacion(preautorizacion.getId(), preAutPres, screenName, connection);
					   preAutPres.setId(idPrestacion);
			    	}
				}
			}else if(preautorizacion.getMedicamentosPresentados()!=null && preautorizacion.getMedicamentosPresentados().size()>0) {
				for(PreAutorizacionMedicamento preAutMed : preautorizacion.getMedicamentosPresentados()){
					Boolean existe=false;
					for(PreAutorizacionMedicamento preAutMedDB : preautorizacionDB.getMedicamentosPresentados()){
						if(preAutMed.getId().equals(preAutMedDB.getId())){
							existe=true;
							getInstance().updatePreautorizacionMedicamento(preAutMed.getId(), preAutMed, screenName, connection);
							break;
						}
					}
					if(!existe){
					   idPreAutMedic=getInstance().insertaPreAutorizacionMedicamento(preautorizacion.getId(), preAutMed, screenName, connection);
					   preAutMed.setId(idPreAutMedic);
			    	}
				}
			}
				//Analiza Bajas - apareo con registros existentes en BD
//VER
/*				
				for(PreAutorizacionPrestacion tdDB:preautorizacionDB.getCodigosPresentados()){
					Boolean existe=false;
					for(PreAutorizacionPrestacion td:preautorizacion.getCodigosPresentados()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().eliminaPreautorizacionPrestacion(tdDB.getId(), screenName, connection) ;
		    	    }
				}
*/				
//			}else{
//VER
/*				
				
				if(preautorizacionDB.getCodigosPresentados() != null && preautorizacionDB.getCodigosPresentados().size()>0){
					for(PreAutorizacionPrestacion tdDB:preautorizacionDB.getCodigosPresentados()){
						getInstance().eliminaPreautorizacionPrestacion(tdDB.getId(), screenName, connection) ;
					}
				}
*/				
//			}
			
	        //Estado
			if(!preautorizacion.getUltimoEstado().getId().equalsIgnoreCase(preautorizacionDB.getUltimoEstado().getId())){
				getInstance().insertaPreAutorizacionEstado(idPreautorizacion,preautorizacion, screenName, connection);			   	
			}
			
			
			//Inserta Gestion OSPIM -- Arreglar
			if("GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())) {
			  insertarGestionOspim(idPreautorizacion,preautorizacion,preautorizacionDB, screenName, connection);
			}
			
			
			
			//Update Alojamiento
			getInstance().updatePreAutorizacionAlojamiento(idPreautorizacion,preautorizacion, screenName, connection);
			
			
			// Modifica Seguimiento
			idPreautorizacion=getInstance().updatePreautorizacion(preautorizacion,screenName,connection);

/*			
			if(!esBaja && !"".equals(usrDestino)){
				enviaEmailSeguimiento(usrDestino,asunto,mensaje);
			}
*/			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idPreautorizacion;
	}
    
    
    
    public static long eliminaPreautorizacion(int idPreautorizacion, String screenName) throws Exception {
    	
	  try {			
		    // Baja del Seguimiento
			getInstance().eliminaPreautorizacion(idPreautorizacion, screenName,null);
	  } catch (Exception e) {
		 	_log.error("Error al Eliminar Preautorizacion");
		 	_log.error(e);
	  }  
	  return idPreautorizacion;
	}
    
    
    public static List<DLFileEntryImpl> getImagenesPreautorizacion(String titulo) throws SystemException{
		return getInstance().getImagenesPreautorizacion(titulo);
	}
    
    
    private static void enviaEmailSeguimiento(String usrDestino,String asunto,String mensaje) throws SystemException{
    	List<String> direc = new ArrayList<String>();
    	String[] usuarios =usrDestino.split(";");
    	if(usuarios.length>0){
    	   for(int i=0;i<usuarios.length;i++){	
    	      DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(usuarios[i]);
    	      String eMail="";
    	      if(dv!=null){
    		    eMail=dv.getDerivacionEmail();
    	      }
    	      direc.add(eMail);
    	   }   
    	}else{
    		DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(usrDestino);
    		String eMail="";
    		if(dv!=null){
    		  eMail=dv.getDerivacionEmail();
    		}
    		direc.add(eMail);
    	}
    	
    	if(direc.size()>0){
    	   EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, direc,1);
    	}
    	
    }
    
    public static long saveEnvioEmail(int idPreautorizacion, String screenName, boolean primeraVez, Integer idAutorizacionWS ) throws Exception {
    	
		try {			
		  getInstance().saveEnvioEmail(idPreautorizacion, screenName, primeraVez, idAutorizacionWS, null);
	  } catch (Exception e) {
		 	_log.error("Error al Grabar Envio Mail Preautorizacion");
		 	_log.error(e);
	  }  
	  return idPreautorizacion;
	}
    
    public static List<PreAutorizacion> getAlertaPreAutorizaciones()
			throws SystemException {
		return getInstance().getAlertaPreAutorizaciones();
		
	}
    
    public static long insertaPreAutorizacionEstado(Integer preautorizacionId,PreAutorizacion preautorizacion,String screenName,Connection connectionParameter) throws SystemException, SQLException {
    	return getInstance().insertaPreAutorizacionEstado(preautorizacionId, preautorizacion, screenName, connectionParameter);
    }
    
    public static void enviarSolicitudAutorizacionPorEmail(PreAutorizacion preAutorizacion, User user){
    	try{
        	ReportesAutomaticosConfiguracion rac = null;
    		rac = ReportesServiceUtil.getConfiguracion();
    		String from=rac.getMailFrom();
    		String to =TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO");
    		Seccional seccional =SeccionalServiceUtil.buscarSeccionalById(preAutorizacion.getAfiliado().getSeccional().getId());
    		List<String>emails = new ArrayList<String>();
    		emails.add(to);
//    		for(Contacto c:seccional.getContactos()){
//    			if(c.getContacto()!=null && c.getContacto().getTipo().equals(c.getContacto().getTipo().EMAIL)){
//    				from=c.getContacto().getContacto();
//    				break;
//    			}
//    		}
    		String subject="";
    		String emailUsr = StringUtils.checkNotEmpty(user.getEmailAddress())?user.getEmailAddress():"N/A" ;
    		subject = "DNI " + 
    				preAutorizacion.getAfiliado().getDocu_numero() +" "+ 
    				preAutorizacion.getAfiliado().getApeNombre() + " " +
    				"ID: " +preAutorizacion.getId();
    		String body= "Se envía adjunto orden para autorizar. " +"\n\n\n" +
    		seccional.getDescripcion()+"\n\n\n" +
    		"Email seccional: " + from + "\n\n\n" +
    		"Email usuario envío: " + emailUsr ;		
    		
    		List<MimeBodyPart> adjuntos=new ArrayList<MimeBodyPart>();
    		try{
        		DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
        		long folderIdNew=f.getFolderId();
        		//String PDF = "application/xls";
        		List<DLFileEntryImpl>list = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+preAutorizacion.getId()+"-");
        		if(list.size()>0){
        			for (int i = 0; i < list.size() ; i++) {
        		        DLFileEntry doc = list.get(i);
        		        String PDF = MimeTypesUtil.getContentType(doc.getName());
        		        InputStream is = DLFileEntryLocalServiceUtil.getFileAsStream(10112, doc.getUserId(), folderIdNew, doc.getName());
        		        byte[] by = IOUtils.toByteArray(is);
        		        DataSource dataSource = new ByteArrayDataSource(by, PDF);
						MimeBodyPart pdfBodyPart = new MimeBodyPart();
						try {
							pdfBodyPart.setDataHandler(new DataHandler(dataSource));
							pdfBodyPart.setFileName(doc.getName());
							adjuntos.add(pdfBodyPart);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							_log.error(e);
						}
        			}	
        		}
    		}catch (Exception e) {
        		_log.error("solo no pudo encontrar los adjuntos... ");
			}
/*    		
    		MimeMessage mm = GmailAPIUtil.createEmailWithAttachment(to, from, subject, body, adjuntos);
    		Gmail service = GmailAPIUtil.getGmailService("svalentini@ospim.org.ar");
			String userId = "svalentini@ospim.org.ar";
			GmailAPIUtil.sendMessage(service, userId , mm);
*/			
    		
//    		boolean rta=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, rac.getPass(), emails, subject, body, adjunto);
//    		if(rta){
//    		getInstance().saveEnvioEmail(preAutorizacion.getId(),user.getScreenName(), null);
    			
//   		}
    		
        }catch(Exception e){
        	_log.debug(e.getMessage());
        }
    }
    
    public static List<PreAutorizacion> getAlertaPreAutorizacionesRechazados()
			throws SystemException {
		return getInstance().getAlertaPreAutorizacionesRechazados();
		
	}
    
    
    public static long saveInformeRechazo(int idPreautorizacion) throws Exception {
    	
		try {			
		  getInstance().saveInformeRechazo(idPreautorizacion, null);
	  } catch (Exception e) {
		 	_log.error("Error al Grabar informe rechazo Preautorizacion");
		 	_log.error(e);
	  }  
	  return idPreautorizacion;
	}

    public static List<Estado> getEstadisticoEstados(Date fecha,Date fechaHta)
			throws SystemException {
		return getInstance().getEstadisticoEstados(fecha,fechaHta);
		
	}
    
    public static List<PreAutorizacion> getEstadisticoPorDia(Date fecha,Date fechaHta)
			throws SystemException {
		return getInstance().getEstadisticoPorDia(fecha,fechaHta);
		
	}
    
    public static List<PreAutorizacion> getEstadisticoPorSeccional(Date fecha,Date fechaHta)
			throws SystemException {
		return getInstance().getEstadisticoPorSeccional(fecha,fechaHta);
		
	}
    
    public static List<Estado> getEstadisticoPorMes(Date fecha,Date fechaHta)
			throws SystemException {
		return getInstance().getEstadisticoPorMes(fecha,fechaHta);
		
	}
    
    public static List<PreAutorizacion> getExistePrestacionPendiente(Integer idPrestacion,String cuil,Integer inte) throws SystemException{
    	return getInstance().getExistePrestacionPendiente(idPrestacion, cuil, inte);
    }
    
    public static Integer proximoNroLote(Connection connectionParameter) throws SystemException, SQLException {
    	return getInstance().proximoNroLote(connectionParameter);
    }
    
    
    public static void saveLote(List<ArchivoPrevencion> list,String screenName,String fileName) throws Exception {
    	Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
    	    Integer nroLote = PreAutorizacionServiceUtil.proximoNroLote(connection); 
    	    for(ArchivoPrevencion s:list){
               getInstance().saveLoteDetalle(nroLote,s.getFecha(),s.getNroDocumento(),s.getNroAutorizacion(),
            		   s.getAfiliado(),s.getPrestacion(),s.getPrestacionNombre(),s.getSeccionalId(),
            		   s.getSeccionalDescripcion(),s.getEstado(),s.getCantidad(), screenName, connection);
    	    }
    	    
    	    getInstance().saveLote(nroLote, list.size(), fileName, screenName, connection);
    	    
    		connection.commit();
		} catch (Exception e) {
		  _log.error(e);
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
		} finally {
			  ConnectionHelper.cerrar(connection);
		}       
    	
	}
    
	public static List<PreAutorizacionLoteProcesado> lotesProcesados()
			throws SystemException {
		return getInstance().lotesProcesados();
	}
	
	public static void saveLoteRespuestasWS(List<RespuestaPreAutorizPSDTO> list,String screenName,String fileName) throws Exception {
    	Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnectionForTransaction();
			
    	    Integer nroLote = getInstance().saveLoteRespuestaWS(list.size(), fileName, screenName, connection); 
    	    
    	    for(RespuestaPreAutorizPSDTO rta:list){
    	    	_log.info("repuesta id " + rta.getTransactionId()!=null?rta.getTransactionId():rta.getAfiliadoApeyNom());	
               getInstance().saveLoteDetalleRespuestaWS(nroLote, rta, screenName, connection);
               
    	    }
    	    
    		connection.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(connection);
		}       
    	
	}
   
	public static boolean getValidaPlanMolinero(String cuil, int inte)
			throws SystemException {
		return getInstance().validaPlanMolinero(cuil, inte);
	}
	
	public static PreAutorizacion buscarPreautorizacionGestionOspimPorId(int id) throws SystemException{
 		return getInstance().buscarPreautorizacionGestionOspimPorId(id, null);
	}
	
	
	private static void insertarGestionOspim(Integer idPreAutorizacion,PreAutorizacion preautorizacion,PreAutorizacion preautorizacionDB,String screenName,Connection connection) throws Exception {
		PreAutorizacion paux = buscarPreautorizacionGestionOspimPorId(preautorizacion.getId());
		Integer idReclamo=0;
		if(paux.getTipoPedidoGestionOSPIM()==null || "".equalsIgnoreCase(paux.getTipoPedidoGestionOSPIM())) {
			
		  getInstance().insertaPreAutorizacionGestionOspim(preautorizacion, screenName,  connection);
		
		  if(preautorizacion.getUltimoEstadoOSPIM()!=null && preautorizacion.getUltimoEstadoOSPIM().getId()!=null &&
				!"".equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())) {
		     getInstance().insertaPreAutorizacionGestionOspimEstado(idPreAutorizacion,preautorizacion, screenName, connection);
		     
		     
		  }
		}else {
			
			getInstance().updatePreAutorizacionGestionOspim(preautorizacion, screenName,  connection);
			
			if(preautorizacion.getUltimoEstadoOSPIM().getId()!=null && !"".equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()) &&
					!preautorizacion.getUltimoEstadoOSPIM().getId().equalsIgnoreCase(preautorizacionDB.getUltimoEstadoOSPIM().getId())){
				getInstance().insertaPreAutorizacionGestionOspimEstado(idPreAutorizacion,preautorizacion, screenName, connection);			   	
			}
			
		}
		
		
		if( (preautorizacion.getIdReclamoPrestacional()==null || preautorizacion.getIdReclamoPrestacional()==0) 
	        && (WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_RECHAZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())
	        		||	WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_AUTORIZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()))) {
	    	 
	    	 ReclamoPrestacional reclamo = new ReclamoPrestacional();
	    	 if(preautorizacion.getIdReclamoPrestacional()!=null) {
	    		 reclamo.setId(preautorizacion.getIdReclamoPrestacional());
	    	 }
	    	 reclamo.setPrestaciones(new ArrayList<PrestacionesReclamo>());
	    	 reclamo.setAfiliado(preautorizacion.getAfiliado());
	    	 reclamo.setTipoPedido(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_PEDIDO_EXCEPCION);
//	    	 reclamo.setSector(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECTOR_PRESTACIONES_MEDICAS);
	    	 reclamo.setOspim_fecha(new Date());
	    	 
	    	 
	    	 if(WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_RECHAZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()) && 
	    			 WebKeysAutorizaciones.GESTION_OSPIM_TIPO_GESTION_RECHAZADO.equalsIgnoreCase(preautorizacion.getTipoGestionOSPIM())) {
	    	     reclamo.setEstado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_RECHAZADO);
	    	     
                 reclamo.setTipo_gestion_cierre_reclamo(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_TIPO_GESTION_ESTADO_RECHAZADO);
                 reclamo.setFecha_cierre(new Date());
                 reclamo.setObservaciones("Rechazado desde Preautorizaciones");
                 
                 List<RevisionesReclamo>revisiones=new ArrayList<RevisionesReclamo>();
                 RevisionesReclamo revision = new RevisionesReclamo();
                 revision.setEstado( RevisionesReclamo.ESTADOS.NUEVO);  
                 revision.setUsr_resolucion(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_RESOLUCION_ESTADO_RECHAZADO);
                 revision.setFecha_revision(new Date());
                 
                 revision.setUsr_responsable_resolucion(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_RESOLUCION_RESPONSABLE);
                 revision.setUsr_presente(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_RESOLUCION_PRESENTES);
                 
                 revisiones.add(revision);
                 reclamo.setRevisiones(revisiones);
                 
                 

	    	 }else {
	    		 reclamo.setEstado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_CARGADO);
	    	 }
	    	 
	    	 if(!preautorizacion.isMedicamento()) {
	    		 
	    		 reclamo.setSector(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECTOR_PRESTACIONES_MEDICAS);
	    		 
		    	 for(PreAutorizacionPrestacion p:preautorizacion.getCodigosPresentados()) {
		    	    PrestacionesReclamo pr = new PrestacionesReclamo();
		    	    pr.setCodigoPrestacion(p.getNomenclador().getCodigo());
		    	    pr.setId_prestacion(p.getNomenclador().getId_prestacion());
		    	    pr.setCantidad( p.getCantidad());
		    	    pr.setImporte(p.getImporte());
		    	    pr.setFrecuencia(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_FRECUENCIA[0][0]); //Unica
		    	    pr.setEstado(PrestacionesReclamo.ESTADOS.NUEVO);
		    	    
		    	    if(!PrestacionesReclamo.ESTADOS.BAJA.equals(pr.getEstado()) &&  WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_RECHAZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()) && 
			    			 WebKeysAutorizaciones.GESTION_OSPIM_TIPO_GESTION_RECHAZADO.equalsIgnoreCase(preautorizacion.getTipoGestionOSPIM())) {
		    	        pr.setEstadoRechazoAprobado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_RECHAZADO);
		    	    }
		    	    
		    	    if(WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_AUTORIZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())) {
		    	        pr.setEstadoRechazoAprobado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_AUTORIZADO);
		    	    }
		    	    
		    	    pr.setRecuperable(2); //NO RECUPERABLE
		    	    pr.setReconocidoSSS(0D);
		    	    
		    	    reclamo.getPrestaciones().add(pr);
		    	 }
		     }else {
		    	 
		    	 reclamo.setSector(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECTOR_FARMACIA);
		    	 
		    	 for(PreAutorizacionMedicamento p:preautorizacion.getMedicamentosPresentados() ) {
			    	    PrestacionesReclamo pr = new PrestacionesReclamo();
			    	    pr.setId_medicamento(p.getMedicamento().getId_medicamento());
			    	    pr.setCodigoPrestacion(String.valueOf(p.getMedicamento().getTroquel()) );
			    	    pr.setCantidad( p.getCantidad().intValue());
			    	    pr.setImporte(p.getImporte());
			    	    pr.setFrecuencia(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_FRECUENCIA[0][0]); //Unica
			    	    pr.setEstado(PrestacionesReclamo.ESTADOS.NUEVO);
			    	    
			    	    if(WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_RECHAZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()) && 
				    			 WebKeysAutorizaciones.GESTION_OSPIM_TIPO_GESTION_RECHAZADO.equalsIgnoreCase(preautorizacion.getTipoGestionOSPIM())) {
			    	        pr.setEstadoRechazoAprobado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_RECHAZADO);
			    	    }
			    	    
			    	    if(WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_AUTORIZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())) {
			    	        pr.setEstadoRechazoAprobado(WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_ESTADO_PRESTACION_AUTORIZADO);
			    	    }
			    	    
			    	    pr.setRecuperable(2); //NO RECUPERABLE
			    	    pr.setReconocidoSSS(0D);
			    	    
			    	    reclamo.getPrestaciones().add(pr);
			     }
		    	 
		    	 
		     }
	    	 
	    	 if(preautorizacion.getIdReclamoPrestacional()!=null && preautorizacion.getIdReclamoPrestacional()!=0) {
	    		ReclamosPrestacionesServiceUtil.updateDesdePreautorizacion(reclamo, screenName); 
	    		
	    	 }else {
	    	    idReclamo=ReclamosPrestacionesServiceUtil.insertarDesdePreautorizacion(reclamo, screenName);
	    	    preautorizacion.setIdReclamoPrestacional(idReclamo);
		    	getInstance().updatePreAutorizacionGestionOspim(preautorizacion, screenName,  connection);
	    	 }   
	    	 
	    	 //DS - Agregado para cerrar Circuito de Gestión OSPIM
	    	 Estado estado= new Estado();
	    	 if( WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_RECHAZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())){
	    		 estado.setId("RE");
	    		 
	    	 }else if(WebKeysAutorizaciones.GESTION_OSPIM_ESTADO_AUTORIZADO.equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId())){
	    		 estado.setId("AU");
	    	 }
	    	 preautorizacion.setUltimoEstado(estado);
	    	 getInstance().insertaPreAutorizacionEstado(preautorizacion.getId(), preautorizacion, screenName, connection);
	    	 // DS - Fin Circuito Gestion Ospim
	    }
		
	}
	
	public static Integer buscarPreautorizacionPorIdReclamo(int id,Connection connectionParameter) throws SystemException {
		return getInstance().buscarPreautorizacionPorIdReclamo(id, connectionParameter);
	}
	
	public static void insertaSeguimientoDocumento(int idPreautorizacion, String fileName, String screenName) throws SystemException{
		getInstance().insertaSeguimientoDocumento(idPreautorizacion, fileName, screenName);
	}
	
	public static void eliminaSeguimientoDocumento(int idPreautorizacion, String fileName, String screenName) throws SystemException{
		getInstance().eliminaSeguimientoDocumento(idPreautorizacion, fileName, screenName);
	}
	
	public static List<String> buscarSeguimientoDocumentos(int idPreautorizacion) throws SystemException{
		return getInstance().buscarSeguimientoDocumentos(idPreautorizacion);
	}
	
	public static void marcaEnvioWSSeguimientoDocumento(int idPreautorizacion, String fileName) throws SystemException{
		getInstance().marcaEnvioWSSeguimientoDocumento(idPreautorizacion, fileName);
	}
	
	public static void desmarcaEnvioWSSeguimientoDocumento(int idPreautorizacion) throws SystemException{
		getInstance().desmarcaEnvioWSSeguimientoDocumento(idPreautorizacion);
	}
	
    public static List<PreAutorizacion> getExisteMedicamentoPendiente(Integer idMedicamento,String cuil,Integer inte) throws SystemException{
    	return getInstance().getExisteMedicamentoPendiente(idMedicamento, cuil, inte);
    }
    
    public static int clonarPreautorizacion(int idPreautOrigen, User user) throws SystemException{
    	
    	int idPreautorizacionClon = 0 ;
    	idPreautorizacionClon = getInstance().clonarPreautorizacion(idPreautOrigen, user.getScreenName(),
    			String.valueOf(UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId()),
    			Integer.parseInt(user.getExpandoBridge().getAttribute("id_seccional").toString()));
    	getInstance().moverImagenesPreautorizacion(idPreautOrigen, idPreautorizacionClon);
    	
    	return idPreautorizacionClon;
    	
    }
	
    public static List<EstadisticaPrestAutorizada> estadisticaPrestacionesAutorizadas(Date periodo) throws SystemException{
    	return getInstance().estadisticaPrestacionesAutorizadas(periodo);
    }
    
    public static boolean tieneDocumentacionSinEnviar(int idPreautorizacion) throws SystemException{
		List<String>list=getInstance().buscarSeguimientoDocumentos(idPreautorizacion);
		boolean wsPendiente =getInstance().existeWSPendiente(idPreautorizacion);
		return !list.isEmpty() && wsPendiente;
	}
    
    public static boolean tieneWSSinEnviar(int idPreautorizacion) throws SystemException{
		boolean wsPendiente =getInstance().existeWSPendiente(idPreautorizacion);
		return wsPendiente;
	}
    
    public static  void updatePrestadorPreautorizacion(Integer idPreautorizacion, Integer prestador, String screenName) throws SystemException, SQLException{
		getInstance().updatePrestadorPreAutorizacion(idPreautorizacion,prestador, screenName, null);
	}
    
    public static Integer obtenerIdPreautorizacionAPP(int idPedidoApp) throws SystemException {
        return getInstance().obtenerIdPreautorizacionAPP(idPedidoApp);
    }
}

