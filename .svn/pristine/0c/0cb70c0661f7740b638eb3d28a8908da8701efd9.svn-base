package ar.com.ospim.autorizaciones.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.afiliados.services.SituLaboralServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub;
import ar.com.ospim.preautorizaciones.ws.IAuthorizationProposalService_CreateAuthorizationProposal_ValidationFault_FaultMessage;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.PrevencionWSClient;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.ArrayOfAuthorizationProposalFileModel;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.AuthorizationProposalFileModel;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.CreateAuthorizationProposal;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.CreateAuthorizationProposalRequest;
import ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.CreateAuthorizationProposalResponse4;

public class SolicitudAutorizacionPS extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4981012379788743092L;
	
	private static Log logger = LogFactoryUtil.getLog(SolicitudAutorizacionPS.class);
	public static String ValidationToken = "ospimservice";
	public static final int filesLimit = 10;
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		Integer idAutorizacion = null;
		boolean esRoyalCanin = false, existeError = false;
		int idPreAutoriz = Integer.parseInt(ra.getTitulo().substring(35));
		logger.debug("Id recuperado del repo automatico: " + idPreAutoriz);
		String screenUser = null;
		String mensajeError = null;
		if (StringUtils.checkNotEmpty(ra.getCsvParameteres())) {
			String[] param = ra.getCsvParameteres().split(",");
			screenUser = param[0].trim().split("=")[0].trim();
		}
			
		PreAutorizacion pa = null;

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			
			pa = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreAutoriz);
			Afiliado a = pa.getAfiliado();
			List<SituacionLaboral> laboralList = SituLaboralServiceUtil.buscaSituLaboral(a.getCuil_titular(),a.getInte());
			for (Iterator<SituacionLaboral> iterator = laboralList.iterator(); iterator.hasNext();) {
				SituacionLaboral sl = iterator.next();
				if(sl.getEmpresa().getCuit().equalsIgnoreCase("30604871286") &&              //ROYAL CANIN ARGENTINA SA
						 (sl.getFecha_baja() == null 
						 	|| (sl.getFecha_baja() != null && sl.getFecha_baja().compareTo(new Date())  > 0 ))){
					esRoyalCanin = true;
					break;
				}
					
			}
			
//			Seteamos si corre o no con un archivo de propiedades 
			File configDir = new File(System.getProperty("catalina.base"), "conf");
			File configFile = new File(configDir, "liferay_schedulers.properties");
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String urlServicio = props.getProperty("ps_autorizacion_url_service");
			
			logger.info("Prevención Autorizacion WS url: " + urlServicio);
			
			AuthorizationProposalServiceStub apss = new AuthorizationProposalServiceStub(urlServicio);
			
			int timeout = 5 * 60 * 1000; // Five minutes;
			apss._getServiceClient().getOptions().setProperty(
	                 HTTPConstants.SO_TIMEOUT, new Integer(timeout));
			apss._getServiceClient().getOptions().setProperty(
	                 HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeout));
			
			CreateAuthorizationProposal createAuthorizationProposal0 = new CreateAuthorizationProposal();
			
			CreateAuthorizationProposalRequest request = new CreateAuthorizationProposalRequest();
			
			request.setAccountId(esRoyalCanin?PrevencionWSClient.ACCOUNT_ID_ROYALCANIN:PrevencionWSClient.ACCOUNT_ID);// 392 - 692
			request.setAuthorizationTypeId(1); //1 - Ambulatoria 2 - Internación
//			request.setCredentialNumber("10017600029"); //opcional
			request.setDocumentNumber(Long.parseLong(a.getDocu_numero()));
			request.setDocumentTypeId(a.getDocumento_tipo());
//			request.setEmail(buscarCorreoNotificacionSeccional(pa.getAlta_usr()));
			logger.info("Seccional de la Autorizacion: "+pa.getId() + " id seccional: " + pa.getSeccionalAltaUsr());
			request.setEmail(buscarCorreoNotificacionSeccional(pa.getSeccionalAltaUsr()));
			request.setEmailCC(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_WS")); 
//			request.setTributaryCodeNumber(Long.parseLong(a.getCuil())); //opcional o no?
					   
			
			ArrayOfAuthorizationProposalFileModel files = obtenerImagenes(idPreAutoriz);
		      
//			    try {
//					byte[] data = Files.readAllBytes(path);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}

			request.setFiles(files);
			request.setObservations(pa.getObservacionesTercerizadoras());
			request.setOrderDate(DateUtils.getCalendarGMTMenos3());
			request.setTransactionId(pa.getId());
			
			request.setValidationToken(ValidationToken);
			
			createAuthorizationProposal0.setRequest(request);
			
			try {
				CreateAuthorizationProposalResponse4 respuesta = apss.createAuthorizationProposal(createAuthorizationProposal0);
				idAutorizacion = respuesta.getCreateAuthorizationProposalResult().getAuthorizationProposalNumber();
				logger.info("Recibimos el idAutorizacion: "+ idAutorizacion);
				
				PreAutorizacionServiceUtil.saveEnvioEmail(idPreAutoriz, screenUser, true, idAutorizacion );
				
				ra.setUltimaEjecucion(new Date());
		
				ReportesServiceUtil.reporteEjecutado(ra);
			
				logger.debug("Fin de Envío de Solicitud Autorización PS - ID: " + idPreAutoriz);
				
				for (int i = 0; i < files.getAuthorizationProposalFileModel().length; i++) {
					
					AuthorizationProposalFileModel apfm = files.getAuthorizationProposalFileModel()[i];
					logger.debug("Marcando envío de Preautorizacion - ID: " + idPreAutoriz + " Documento: " + apfm.getFileName());
					
					PreAutorizacionServiceUtil.marcaEnvioWSSeguimientoDocumento(idPreAutoriz, apfm.getFileName());
				}
				
				
			} catch (RemoteException e) {
				logger.error(e);
				existeError = true;
				mensajeError = e.getMessage();
			} catch (IAuthorizationProposalService_CreateAuthorizationProposal_ValidationFault_FaultMessage e) {
				logger.error(e);
				existeError = true;
				mensajeError = e.getMessage();
			}

		} catch (NumberFormatException e) {
			logger.error(e);
			existeError = true;
			mensajeError = e.getMessage();
		} catch (AxisFault e) {
			logger.error(e);
			existeError = true;
			mensajeError = e.getMessage();
		} catch (SystemException e) {
			logger.error(e);
			existeError = true;
			mensajeError = e.getMessage();
		} catch (Exception e) {
			logger.error(e);
			existeError = true;
			mensajeError = e.getMessage();
		} finally {
			if(existeError) {
				List<String> emailsNotificaAlerta = new ArrayList<String>();
//				emailsNotificaAlerta.add(rac.getMailsDeError());
//				emailsNotificaAlerta.add("mcerfoglio@ospim.org.ar");
//				emailsNotificaAlerta.add("dsulfaro@uoma.org.ar");
				
				if(mensajeError.contains("El afiliado se encuentra de baja") ||  
					mensajeError.contains("El afiliado no está activo para la cuenta informada") ||  
					mensajeError.contains("No se ha encontrado un afiliado activo para el número de documento")){ 
					emailsNotificaAlerta.add("afiliaciones@ospim.org.ar");
				}else if(mensajeError.contains("Fault occurred while processing") ||
						mensajeError.contains("Exception has been thrown by the target of an invocation") ||
						mensajeError.contains("The operation is not valid for the state of the transaction") ||
						mensajeError.contains("Object reference not set to an instance of an object")) {
//					nada, dejamos solo el log
			    }else {
//					emailsNotificaAlerta.add(rac.getMailsDeError());
					emailsNotificaAlerta.add("mcerfoglio@ospim.org.ar");
					emailsNotificaAlerta.add("svalentini@ospim.org.ar");
				}
				if(emailsNotificaAlerta.size()>0) {
//					MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), 
//							rac.getPass(), 
//							emailsNotificaAlerta, 
//							"Error al solicitar autorización PS por WS - ID: " + idPreAutoriz,
//							mensajeError ,
//	//						+ " Verificar Reportes Automáticos con id de preaut, verificar Seguimiento Imágenes, y marca de envío de emails",
//							1);
					EnviaEmailsThread.enviarMailDesatendido("Error al solicitar autorización PS por WS - ID: " + idPreAutoriz, mensajeError, emailsNotificaAlerta, 1);
				}	
			}
		}
		
	}

	private ArrayOfAuthorizationProposalFileModel obtenerImagenes(int idPreAutoriz) {

		AuthorizationProposalFileModel fileAttachment = new AuthorizationProposalFileModel();
		ArrayOfAuthorizationProposalFileModel files = new ArrayOfAuthorizationProposalFileModel();
		  		
		DLFileEntry doc = null;
		String mimeTypes = null;
		InputStream is = null;
		byte[] byteArr = null;
		DataSource dataSource = null;
		DataHandler fileContent = null;
		
		try{
			
			DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
			
			long folderIdNew=f.getFolderId();
			
			List<String> seguimDocs = PreAutorizacionServiceUtil.buscarSeguimientoDocumentos(idPreAutoriz);
			
			List<DLFileEntryImpl> imagesAdj = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+idPreAutoriz+"-");
			
			if(imagesAdj.size()>0 && seguimDocs.size()>0){
				
				for (int i = 0; (i < imagesAdj.size() && i < this.filesLimit); i++) {
					
			        doc = imagesAdj.get(i);
			        
			        if(seguimDocs.contains(doc.getName())) {

				        logger.debug("Docum nro: " + i + " doc.getName(): " + doc.getName());
				        
				        mimeTypes = MimeTypesUtil.getContentType(doc.getName());
				        
				        is = DLFileEntryLocalServiceUtil.getFileAsStream(10112, doc.getUserId(), folderIdNew, doc.getName());
				        
				        byteArr = IOUtils.toByteArray(is);
				        
				        dataSource = new ByteArrayDataSource(byteArr, mimeTypes);
						
				        fileContent = new DataHandler(dataSource);
												
						fileAttachment = new AuthorizationProposalFileModel();
						
						fileAttachment.setFileName(doc.getName());
						
						fileAttachment.setFile(fileContent);
						
						files.addAuthorizationProposalFileModel(fileAttachment);
			        }	
				}	
			}
			
		}catch (Exception e) {
			logger.error("No pudo encontrar los adjuntos... ");
			logger.error(e);
		}

		return files;
	}

	private String buscarCorreoNotificacionSeccional(int idSecc) {
		
		String correoSeccional = "";
 		
 		try{
 			List<ContactoElectronico> contactos = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSecc);
 			
 			if(contactos.size()>0) {
 				correoSeccional = SeccionalServiceUtil.buscarContactosSeccionalEmail(idSecc).get(0).getContacto();
 			}
 			if(StringUtils.checkEmpty(correoSeccional)){
     			 
     			correoSeccional = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_OSPIM_AUTORIZACIONES");
     		}
			logger.debug("Solicitud Autorización PS - email: " + correoSeccional);

 		}catch(NumberFormatException e){
// 			logger.error("Error al parsear id seccional del usuario " + user.getScreenName());
 			logger.error(e);
 			correoSeccional = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_OSPIM_AUTORIZACIONES");
 		}catch (Exception e) {
 			logger.error(e);
 			correoSeccional = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_OSPIM_AUTORIZACIONES");
		}
 		
 		return correoSeccional;
 		
	}
	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

}
