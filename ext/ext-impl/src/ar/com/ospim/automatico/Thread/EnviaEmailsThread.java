package ar.com.ospim.automatico.Thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.mail.MailUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EnviaEmailsThread extends Thread implements Serializable { // EnviaEmailsThreadSafeSingleton
	
	static ScheduledExecutorService scheduler  = Executors.newSingleThreadScheduledExecutor();

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7789200251699511126L;

	private static Log logger = LogFactoryUtil.getLog(EnviaEmailsThread.class);

	private static EnviaEmailsThread instance;

	private ArrayList<String> destinatarios = new ArrayList<String>();
	private ArrayList<String> destinatariosCCO = new ArrayList<String>();

	private String asunto = "";
	private String mensaje = "";
	private HSSFWorkbook adjuntoXls = null;
	private List<MimeBodyPart> adjunto = null;
	private int prioridad = 3;
	private String nombreArchivo = "";
	
	
	private static String PDF = "application/pdf";

	private EnviaEmailsThread() {
		super();
		
//		instance = this;
	}

	// public static synchronized EnviaEmailsThreadSafeSingleton getInstance(){
	// logger.info("inicializando EnviaEmailsThreadSafeSingleton...");
	//
	// if(instance == null){
	// instance = new EnviaEmailsThreadSafeSingleton();
	// }
	// logger.debug("Instancia_:" + instance.toString());
	//
	// return instance;
	// }
	public static EnviaEmailsThread getInstance() {
		logger.info("inicializando EnviaEmailsThreadSafeSingleton...");
		// Siempre generamos un Thread Nuevo
		instance = new EnviaEmailsThread();

		logger.debug("Instancia_:" + instance.toString());

		return instance;
	}

	@Override
	public void run() {
	  
		synchronized(this){
			try {
				Thread.sleep(39000);
				super.run();
			} catch (InterruptedException e1) {
				logger.info("Error   Thread.sleep " + e1  );
	
			}
			logger.info("corriendo...");
	        List<String> lem=new ArrayList<String>();
			ReportesAutomaticosConfiguracion rac = null;
			try {
				rac = ReportesServiceUtil.getConfiguracion();
			} catch (SystemException e) {
				logger.error(e);
	
				instance.interrupt();
			}
			if (null != getAdjuntoXls()) {
				for(String em:getDestinatarios()) {
					lem.clear();
					lem.add(em);
				    MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
						lem, getAsunto(),
						getMensaje(), getAdjuntoXls(), getNombreArchivo());
				}    
			}else if (null != getAdjunto() && null != getDestinatariosCCO() && !getDestinatariosCCO().isEmpty() ) {
				for(String em:getDestinatarios()) {
					lem.clear();
					lem.add(em);
				    MailUtils.enviarMailGmailconAdjunto(rac.getMailFrom(),
						rac.getPass(), lem,getDestinatariosCCO(),
						getAsunto(), this.getMensaje(),getAdjunto()); 
				} 				    
			}else if (null != getAdjunto()) {
				for(String em:getDestinatarios()) {
					lem.clear();
					lem.add(em);
				    MailUtils.enviarMailGmailconAdjunto(rac.getMailFrom(),
						rac.getPass(), lem,
						getAsunto(), this.getMensaje(),getAdjunto());
				}    
			} else {
				for(String em:this.getDestinatarios()) {
					lem.clear();
					lem.add(em);
				    MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(),
						lem, this.getAsunto(),
						this.getMensaje(), this.getPrioridad());
				}    
			}
		}
	}

	public static void enviarMailDesatendido(String asunto, String mensaje,
			List<String> destinatarios, HSSFWorkbook wb, String nombreAdjunto) {

		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "
				+ mensaje);

		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	
		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios( new ArrayList<String>(destinatarios) );
		emt.setPrioridad(1);
		emt.setAdjunto(null);
		emt.setAdjuntoXls(wb);
		emt.setNombreArchivo(nombreAdjunto);

		if (emt.getState().equals(Thread.State.NEW)) {
			logger.info("Starting after new");
			emt.start();
		}

	}

	public static void enviarMailDesatendido(String asunto, String mensaje,
			List<String> destinatarios, int prioridad) {

		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "
				+ mensaje + " prioridad " + prioridad + " cantidad destinatarios " + destinatarios.size());

		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	
		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios( new ArrayList<String>(destinatarios) );
		emt.setPrioridad(prioridad);
		
		if (emt.getState().equals(Thread.State.NEW)) {
			logger.info("Starting after new");
			emt.start();
		}
	}
	
	
	public static void enviarMailDesatendido(String asunto, String mensaje,
			List<String> destinatarios, int prioridad, int delay,ScheduledExecutorService scheduler) {

		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "
				+ mensaje + " prioridad " + prioridad + " cantidad destinatarios " + destinatarios.size());

		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	
		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios( new ArrayList<String>(destinatarios) );
		emt.setPrioridad(prioridad);
		
		if (emt.getState().equals(Thread.State.NEW)) {
			logger.info("Starting after new");
			//emt.start();
			scheduler.schedule(emt, delay, TimeUnit.SECONDS);
		}
	}

	public static void enviarMailDesatendido(String asunto, String mensaje,
			List<String> destinatarios, List<byte[]> bytes) {

		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "+ mensaje);
		
		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	

		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios(new ArrayList<String>(destinatarios));
//		adjuntoXls=null;
		emt.setAdjunto(new ArrayList<MimeBodyPart>());
		// construct the pdf body part
		int cont = 0;
		for (byte[] by : bytes) {
			if (null != by) {
				cont++;
				DataSource dataSource = new ByteArrayDataSource(by, PDF);
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				try {
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName("adj" + cont + ".pdf");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				emt.getAdjunto().add(pdfBodyPart);
			}
		}

		if (emt.getState().equals(Thread.State.NEW)) {
			logger.info("Starting after new");
			emt.start();
		}

	}

	

	public static void enviarMailDesatendidoCCO(String asunto, String mensaje,
			List<String> destinatarios, List<String> destinatariosBCC , List<byte[]> bytes) {

		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "+ mensaje);
		
		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	

		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios(new ArrayList<String>(destinatarios));
		emt.setDestinatariosCCO(new ArrayList<String>(destinatariosBCC));
//		adjuntoXls=null;
		emt.setAdjunto(new ArrayList<MimeBodyPart>());
		// construct the pdf body part
		int cont = 0;
		for (byte[] by : bytes) {
			if (null != by) {
				cont++;
				DataSource dataSource = new ByteArrayDataSource(by, PDF);
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				try {
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName("adj" + cont + ".pdf");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				emt.getAdjunto().add(pdfBodyPart);
			}
		}

		if (emt.getState().equals(Thread.State.NEW)) {
			logger.info("Starting after new");
			emt.start();
		}

	}
	
	/***
	 * enviar con delay
	 * @param asunto
	 * @param mensaje
	 * @param destinatarios
	 * @param destinatariosBCC
	 * @param bytes
	 * @param delay
	 */
	public static void enviarMailDesatendidoCCO(String asunto, String mensaje,
			List<String> destinatarios, List<String> destinatariosBCC , List<byte[]> bytes, int delay,ScheduledExecutorService scheduler) {
		


		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "+ mensaje);
		
		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	

		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios(new ArrayList<String>(destinatarios));
		emt.setDestinatariosCCO(new ArrayList<String>(destinatariosBCC));
//		adjuntoXls=null;
		emt.setAdjunto(new ArrayList<MimeBodyPart>());
		// construct the pdf body part
		int cont = 0;
		for (byte[] by : bytes) {
			if (null != by) {
				cont++;
				DataSource dataSource = new ByteArrayDataSource(by, PDF);
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				try {
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName("adj" + cont + ".pdf");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				emt.getAdjunto().add(pdfBodyPart);
			}
		}

		if (emt.getState().equals(Thread.State.NEW)) {
			//emt.start();
			scheduler.schedule(emt, delay, TimeUnit.SECONDS);

			logger.info("Starting after new");
		}

	}
	
	
	public static void enviarMailDesatendidoCCO(String asunto, String mensaje,
			List<String> destinatarios, List<String> destinatariosBCC , List<byte[]> bytes, int delay,ScheduledExecutorService scheduler,List<String>extension) {
		


		logger.debug("Datos envio email: " + "asunto: " + asunto + " mensaje: "+ mensaje);
		
		EnviaEmailsThread emt = new EnviaEmailsThread(); //.getInstance();	

		emt.setAsunto(asunto);
		emt.setMensaje(mensaje);
		emt.setDestinatarios(new ArrayList<String>(destinatarios));
		emt.setDestinatariosCCO(new ArrayList<String>(destinatariosBCC));
//		adjuntoXls=null;
		emt.setAdjunto(new ArrayList<MimeBodyPart>());
		// construct the pdf body part
		int cont = 0;
		for (byte[] by : bytes) {
			if (null != by) {
				cont++;
				DataSource dataSource = new ByteArrayDataSource(by, PDF);
				MimeBodyPart pdfBodyPart = new MimeBodyPart();
				try {
					pdfBodyPart.setDataHandler(new DataHandler(dataSource));
					pdfBodyPart.setFileName("adj" + cont + "."+extension.get(cont-1));
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				emt.getAdjunto().add(pdfBodyPart);
			}
		}

		if (emt.getState().equals(Thread.State.NEW)) {
			//emt.start();
			scheduler.schedule(emt, delay, TimeUnit.SECONDS);

			logger.info("Starting after new");
		}

	}

	
	
	 public void shutdown(){
		 scheduler.shutdown();
	 }
	
	public ArrayList<String> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(ArrayList<String> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public ArrayList<String> getDestinatariosCCO() {
		return destinatariosCCO;
	}

	public void setDestinatariosCCO(ArrayList<String> destinatariosCCO) {
		this.destinatariosCCO = destinatariosCCO;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}

	public HSSFWorkbook getAdjuntoXls() {
		return adjuntoXls;
	}

	public void setAdjuntoXls(HSSFWorkbook adjuntoXls) {
		this.adjuntoXls = adjuntoXls;
	}

	public List<MimeBodyPart> getAdjunto() {
		return adjunto;
	}

	public void setAdjunto(List<MimeBodyPart> adjunto) {
		this.adjunto = adjunto;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
}
