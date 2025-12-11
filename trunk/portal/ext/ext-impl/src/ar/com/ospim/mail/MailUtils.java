package ar.com.ospim.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
 
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.sun.mail.smtp.SMTPSendFailedException;


public class MailUtils {

   /**
	 * 			Priority (Urgent);
	 *			Importance (low, normal, high)
	 *			X-Priority (1, 2, 3, 4, 5)
	 *			X-MSMail-Priority
	 */
	private static Log logger = LogFactoryUtil.getLog(MailUtils.class);
	
	public static Properties getMailServerProperties() {
		
		Properties props = new Properties();
		// Nombre del host de correo, es smtp.gmail.com
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		// TLS si está disponible
		props.setProperty("mail.smtp.starttls.enable", "true");
		// Puerto de gmail para envio de correos
		props.setProperty("mail.smtp.port", "587");
		// Nombre del usuario
		props.setProperty("mail.smtp.user", "info@ospim.org.ar");
		// Si requiere o no usuario y password para conectarse.
		props.setProperty("mail.smtp.auth", "true");
		
		props.setProperty("mail.debug", "true");
		
		props.setProperty("mail.smtp.connectiontimeout", "1000");
		
		System.setProperty("java.net.preferIPv6Addresses", "true");
		
		return props;
	}

	public static void enviarMailGmailSinAdjHTML(String from, String pass,
			List<String> emails, String subject, String texto, Integer prioridad) {
	
		String laPrioridad = "", laUrgencia="";
		
		if(prioridad == null){
			prioridad=3;
		}
		
		switch (prioridad) {
		case 1:
			laPrioridad="Alta";
			laUrgencia = "Urgente";
			break;
		case 3:
			laPrioridad="Normal";
			break;
		case 5:
			laPrioridad="Baja";
			break;	
		}
		
		Properties props = getMailServerProperties();

		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);

		Transport t = null;
		// Quien envia el correo
		try {
			// create and fill the first message part
			
			t = session.getTransport("smtp");
			t.connect(from, pass);

			MimeMessage message = new MimeMessage(session);
			message.setContent(texto, "text/html");
			
			for (int k = 0; k < emails.size(); k++) {
				String email = emails.get(k).trim();
				logger.debug("email " + k + ": " + email);
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
			}	
				
//			TODO No anda muy bien lo de la prioridad...

			message.addHeader("X-Priority", String.valueOf(prioridad));
			message.addHeader("Importancia", laPrioridad);
			message.addHeader("X-MSMail-Priority", laPrioridad);
			message.addHeader("Priority", laUrgencia);
			message.addHeader("Prioridad", laPrioridad);
			
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			t.sendMessage(message, message.getAllRecipients());
			

		} catch (AddressException e) {
			logger.error(e);
		} catch (MessagingException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				t.close();
			} catch (MessagingException e) {
				logger.error(e);
			}
		}

	}
	
	public static void enviarMailGmailSinAdj(String from, String pass,
			List<String> emails, String subject, String texto, Integer prioridad) {
	
		String laPrioridad = "", laUrgencia="";
		
		if(prioridad == null){
			prioridad=3;
		}
		
		switch (prioridad) {
		case 1:
			laPrioridad="Alta";
			laUrgencia = "Urgente";
			break;
		case 3:
			laPrioridad="Normal";
			break;
		case 5:
			laPrioridad="Baja";
			break;	
		}
		
		Properties props = getMailServerProperties();

		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);

		Transport t = null;
		// Quien envia el correo
		try {
			// create and fill the first message part
//			MimeBodyPart mbp1 = new MimeBodyPart();
		    BodyPart bp = new MimeBodyPart();
		    bp.setText(texto);
			// mbp1.setText(texto);
//			mbp1.setContent(texto, "text/html; charset=utf-8");
			
			Multipart mp = new MimeMultipart();
//			mp.addBodyPart(mbp1);
			mp.addBodyPart(bp);
			
			t = session.getTransport("smtp");
			t.connect(from, pass);

			MimeMessage message = new MimeMessage(session);
			
			for (int k = 0; k < emails.size(); k++) {
				String email = emails.get(k).trim();
				logger.debug("email " + k + ": " + email);
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
			}	
				
//			TODO No anda muy bien lo de la prioridad...
		
//			message.addHeader("X-Priority", String.valueOf(prioridad));
//			message.addHeader("Importance", "high");
//			message.addHeader("X-MSMail-Priority", "High");
//			message.addHeader("Priority", "Urgent");
//			message.addHeader("Priority", "High");

			message.addHeader("X-Priority", String.valueOf(prioridad));
			message.addHeader("Importancia", laPrioridad);
			message.addHeader("X-MSMail-Priority", laPrioridad);
			message.addHeader("Priority", laUrgencia);
			message.addHeader("Prioridad", laPrioridad);
//				message.setHeader("Prioridad", laPrioridad);
			
			message.setContent(mp);
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			t.sendMessage(message, message.getAllRecipients());
			

		} catch (AddressException e) {
			logger.error(e);
		} catch (MessagingException e) {
			logger.error("Error envio de mail " + subject);
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				t.close();
			} catch (MessagingException e) {
				logger.error(e);
			}
		}

	}

	/**	@author Sergio
	 *  
	 * @param from
	 * @param pass
	 * @param emails
	 * @param subject
	 * @param body
	 * @param ds
	 * @param fileName
	 */
	 public static void enviarMailGmailconXls(String from, String pass,
				List<String> emails, String subject, String body, HSSFWorkbook wb, String fileName) {

			Transport t = null;
			
			FileDataSource fds = generarDataSourceFromHSSWorkbook(wb, fileName);
			
			Properties props = getMailServerProperties();
			
			try{
				
			    if(StringUtils.checkEmpty(from)){
			    	from = props.getProperty("mail.smtp.user");
			    }
			    Session mailSession = Session.getDefaultInstance(props, null);
			    mailSession.setDebug(true);
		
		        // Se compone la parte del texto
		        BodyPart texto = new MimeBodyPart();
		        texto.setText(body);
		
		        // Se compone el adjunto con la imagen
		        BodyPart adjunto = new MimeBodyPart();
		        adjunto.setDataHandler(new DataHandler(fds));
		        adjunto.setFileName(fileName);
		
		        // Una MultiParte para agrupar texto e imagen.
		        MimeMultipart multiParte = new MimeMultipart();
		        multiParte.addBodyPart(texto);
		        multiParte.addBodyPart(adjunto);
		
		        // Se compone el correo, dando to, from, subject y el
		        // contenido.
		        Message msg = new MimeMessage(mailSession);
			    
			    msg.setFrom(new InternetAddress(from));
			    
			    for (int i = 0; i < emails.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(i)));
				} 

			    msg.setSubject(subject);
		        msg.setContent(multiParte);
		
		        // Se envia el correo.
		        t = mailSession.getTransport("smtp");
			    t.connect(from, pass);
			    t.sendMessage(msg, msg.getAllRecipients());
		        
			} catch (Exception e) {
				logger.error("Error al enviar correos de Gmail, asunto: " + subject);
				logger.error(e);
			} finally {
				try {
					t.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
	}
	 
	 public static void enviarMailGmailconAdjunto(String from, String pass,
				List<String> emails, String subject, String body, List<MimeBodyPart> adjunto) {

			Transport t = null;
			
			Properties props = getMailServerProperties();
			
			try{
				
			    if(StringUtils.checkEmpty(from)){
			    	from = props.getProperty("mail.smtp.user");
			    }
			    Session mailSession = Session.getDefaultInstance(props, null);
		        mailSession.setDebug(true);
		
		        // Se compone la parte del texto
		        BodyPart texto = new MimeBodyPart();
		        texto.setText(body);
				    
		
		        // Una MultiParte para agrupar texto e imagen.
		        MimeMultipart multiParte = new MimeMultipart();
		        multiParte.addBodyPart(texto);
		        for(MimeBodyPart adj:adjunto){
		        	multiParte.addBodyPart(adj);
		        }
		
		        // Se compone el correo, dando to, from, subject y el
		        // contenido.
		        Message msg = new MimeMessage(mailSession);
			    
			    msg.setFrom(new InternetAddress(from));
			    
			    for (int i = 0; i < emails.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(i)));
				} 

			    msg.setSubject(subject);
		        msg.setContent(multiParte);
		
		        // Se envia el correo.
		        t = mailSession.getTransport("smtp");
			    t.connect(from, pass);
			    t.sendMessage(msg, msg.getAllRecipients());
		        
			} catch (Exception e) {
				logger.error("Error al enviar correos de Gmail, asunto: " + subject);
				logger.error(e);
			} finally {
				try {
					t.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
	}

	 
	 public static void enviarMailGmailconAdjunto(String from, String pass,
			 List<String> emails, List<String> emailsCCO, String subject, String body, List<MimeBodyPart> adjunto) {

			Transport t = null;
			
			Properties props = getMailServerProperties();
			
			try{
				
			    if(StringUtils.checkEmpty(from)){
			    	from = props.getProperty("mail.smtp.user");
			    }
			    Session mailSession = Session.getDefaultInstance(props, null);
		        mailSession.setDebug(true);
		
		        // Se compone la parte del texto
		        BodyPart texto = new MimeBodyPart();
		        texto.setText(body);
				    
		
		        // Una MultiParte para agrupar texto e imagen.
		        MimeMultipart multiParte = new MimeMultipart();
		        multiParte.addBodyPart(texto);
		        for(MimeBodyPart adj:adjunto){
		        	multiParte.addBodyPart(adj);
		        }
		
		        // Se compone el correo, dando to, from, subject y el
		        // contenido.
		        Message msg = new MimeMessage(mailSession);
			    
			    msg.setFrom(new InternetAddress(from));
			    
			    for (int i = 0; i < emails.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(i)));
				} 
			    
			    for (int i = 0; i < emailsCCO.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailsCCO.get(i)));
				} 
			    

			    msg.setSubject(subject);
		        msg.setContent(multiParte);
		
		        // Se envia el correo.
		        t = mailSession.getTransport("smtp");
			    t.connect(from, pass);
			    t.sendMessage(msg, msg.getAllRecipients());
		        
			} catch (Exception e) {
				logger.error("Error al enviar correos de Gmail, asunto: " + subject);
				logger.error(e);
			} finally {
				try {
					t.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
	}
	 
	public static FileDataSource generarDataSourceFromHSSWorkbook(
			HSSFWorkbook excel, String name) {
		
		FileDataSource fds = null;
		try {
			File file = new File(name);
			FileOutputStream fos = new FileOutputStream(file);
			excel.write(fos);
			fos.close();
			fds = new FileDataSource(file);
		} catch (Exception e) {
			logger.error(e);
		}
		return fds;
	}

	/**	@author Sergio
	 * 
	 * @param from
	 * @param pass
	 * @param emails
	 * @param subject
	 * @param body
	 * @param pathFileName
	 * @param fileNameAdjunt
	 */
	
	 public static void enviarMailGmailconPdf(String from, String pass,
				List<String> emails, String subject, String body, String pathFileName, String fileNameAdjunt) {

			Transport t = null;
			
			FileDataSource fds = new FileDataSource(pathFileName);
			
			Properties props = getMailServerProperties();
			
			try{
				
			    if(StringUtils.checkEmpty(from)){
			    	from = props.getProperty("mail.smtp.user");
			    }
			    Session mailSession = Session.getDefaultInstance(props, null);
		        mailSession.setDebug(true);
		
		        // Se compone la parte del texto
		        BodyPart texto = new MimeBodyPart();
		        texto.setText(body);
		
		        // Se compone el adjunto con la imagen
		        BodyPart adjunto = new MimeBodyPart();
		        adjunto.setDataHandler(new DataHandler(fds));
		        adjunto.setFileName(fileNameAdjunt);
		
		        // Una MultiParte para agrupar texto e imagen.
		        MimeMultipart multiParte = new MimeMultipart();
		        multiParte.addBodyPart(texto);
		        multiParte.addBodyPart(adjunto);
		
		        // Se compone el correo, dando to, from, subject y el
		        // contenido.
		        Message msg = new MimeMessage(mailSession);
			    
			    msg.setFrom(new InternetAddress(from));
			    
			    for (int i = 0; i < emails.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(emails.get(i)));
				} 

			    msg.setSubject(subject);
		        msg.setContent(multiParte);
		
		        // Se envia el correo.
		        t = mailSession.getTransport("smtp");
			    t.connect(from, pass);
			    t.sendMessage(msg, msg.getAllRecipients());
		        
			} catch (Exception e) {
				logger.error("Error al enviar correos de Gmail, asunto: " + subject);
				logger.error(e);
			} finally {
				try {
					t.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
	 }
	 
	 public static boolean enviarMailGmailconAdjuntoYRespuesta(String from, String pass,
				List<String> emails, String subject, String body, List<MimeBodyPart> adjunto) {
            boolean ret=true;
			Transport t = null;
			
			Properties props = getMailServerProperties();
			String fromAutentication = null;
			try{
				
				fromAutentication = props.getProperty("mail.smtp.user");
				
			    if(StringUtils.checkEmpty(from)){
			    	from = props.getProperty("mail.smtp.user");
			    }
			    Session mailSession = Session.getDefaultInstance(props, null);
		        // session.setDebug(true);
		
		        // Se compone la parte del texto
		        BodyPart texto = new MimeBodyPart();
		        texto.setText(body);
				    
		
		        // Una MultiParte para agrupar texto e imagen.
		        MimeMultipart multiParte = new MimeMultipart();
		        multiParte.addBodyPart(texto);
		        for(MimeBodyPart adj:adjunto){
		        	multiParte.addBodyPart(adj);
		        }
		
		        // Se compone el correo, dando to, from, subject y el
		        // contenido.
		        Message msg = new MimeMessage(mailSession);
			    
			    msg.setFrom(new InternetAddress(from));
			    
			    for (int i = 0; i < emails.size(); i++) {
			    	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(i)));
				} 
			    logger.debug(subject);
			    logger.debug("Cantidad de destinatarios: " + msg.getAllRecipients().length);
			    msg.setSubject(subject);
		        msg.setContent(multiParte);
		
		        // Se envia el correo.
		        t = mailSession.getTransport("smtp");
			    t.connect(fromAutentication, pass);
			    t.sendMessage(msg, msg.getAllRecipients());
		        
			} catch (SMTPSendFailedException e) {
				logger.error("Error al enviar correos de Gmail(supera 25Mb), asunto: " + subject);
				logger.error(e);
				ret=false;
			}catch (Exception e) {
					logger.error("Error al enviar correos de Gmail, asunto: " + subject);
					logger.error(e);
					ret=false;
				
			} finally {
				try {
					t.close();
//					ret=true;
				} catch (SMTPSendFailedException e) {
					logger.error("Error al enviar correos de Gmail(supera 25Mb), asunto: " + subject);
					logger.error(e);
					ret=false;	
				} catch (Exception e) {
					logger.error(e);
					ret=false;	
				}
			}
			return ret;
	}

		
}