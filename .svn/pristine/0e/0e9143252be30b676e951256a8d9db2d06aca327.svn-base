package ar.com.ospim.crm.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.SubidaFTPPadronPF;
import ar.com.ospim.afiliados.services.SubidaPadronPFServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ProcesarSubidaFTPPadronPF extends AgendadoJava {

	private static final String FILE_SEPARATOR =  System.getProperty("file.separator");
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");
	
	private static Log _log = LogFactoryUtil
			.getLog(ProcesarSubidaFTPPadronPF.class);
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		boolean error = false; 
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
				
		String fecha = null;
		String total = null;
		
		fecha = DateUtils.getDateString(calendar.getTime(), "YYYYMMdd");
		
		List<SubidaFTPPadronPF> padron =  null;
		String nombreArchivo =  null;
		nombreArchivo = "90062588_" + fecha;
		try {
			SubidaPadronPFServiceUtil.generarPadronPagoFacil();
			
			padron = SubidaPadronPFServiceUtil.generarArchivo();
			
			total = SubidaPadronPFServiceUtil.generarReporte();
		} catch (Exception e) {
			_log.error(e.getMessage());
			error= true;
		}

		crearTxtPadron(padron, nombreArchivo + ".ONL");
		
		createZip(nombreArchivo);
		
		try {
			try {
				subirNovedadesFTP(nombreArchivo + ".zip");
			} catch (SftpException e) {
				_log.debug(e);
				error= true;
			}
		} catch (IOException e1) {
			 _log.debug(e1);
				error= true;
		} catch (JSchException e1) {
			 _log.debug(e1);
				error= true;
		}
		
		
		if (error == false) {			
			enviarReporte(fecha, total);
		}else {
			enviarAvisoError(fecha);
		}
	
		
			
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
private void crearTxtPadron(List<SubidaFTPPadronPF> lista, String fileName) {
		
		ServletOutputStream out = null;
		
		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR +  fileName;
			writer = new PrintWriter(disposition, "UTF-8");
					
			String cadena;		
			for(SubidaFTPPadronPF d:lista) {		
				cadena ="";
				//Linea
				cadena=  d.getLinea();
				writer.println(cadena);		
			}
			
			writer.close();
            
		} catch (Exception e) {
			_log.error(e);
		} finally {
			try {
				if (out != null){
					out.close();
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		
	}


	private void subirNovedadesFTP(String file) throws IOException, JSchException, SftpException {
		String disposition = TMPDIR + FILE_SEPARATOR +  file;
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "subida_ftp_padron_pf.properties");
		File key = new File(configDir, "u90062588.ppk");

		
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String host = props.getProperty("host");
			String user = props.getProperty("user");
			//String pass = props.getProperty("pass");// No lo usa pago Facil
		    int port = Integer.parseInt(props.getProperty("port"));
		    JSch jsch = new JSch(); 
		    Session session;
		
			session = jsch.getSession(user, host, port);
			jsch.addIdentity(key.getPath());
			
			session.setConfig("StrictHostKeyChecking", "no");
			//session.setPassword(pass);
			
			
			
			session.connect();
				
		    _log.debug("Connection established.");
		    _log.debug("Creating SFTP Channel.");
			
			ChannelSftp sftp = (ChannelSftp)session.openChannel("sftp");
		    sftp.connect();
			
		   // sftp.cd("/home/czuluaga/pruebasftp"); // el usuario me deja en el directorio no necesito navegar
		    sftp.cd("/IN");
		   
		    _log.debug("Subiendo ..." + file);
	        sftp.put(disposition, file);
	 
	        _log.debug("Archivos subidos.");
	        
	        sftp.exit();
	        sftp.disconnect();
	        session.disconnect();
			
	
	}

	private void enviarReporte(String fecha , String total) {
		
		// obtener configuracion del reporte_automatico
		ReportesAutomaticosConfiguracion rac = null;
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<String> emails;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_PF");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
		
		MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(),
				emails, "SUBIDA FTP REPORTE PAGO FACIL " + fecha,
				"SUBIDA FTP REPORTE PAGO FACIL " + fecha + "  TOTAL PADRON ENVIADO " + total , 5);
		
	}
	private void enviarAvisoError(String fecha) {
		
		// obtener configuracion del reporte_automatico
		ReportesAutomaticosConfiguracion rac = null;
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<String> emails;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_PF");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
		
		MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(),
				emails, "ERROR SUBIDA FTP REPORTE PAGO FACIL " + fecha,
				"ERROR SUBIDA FTP REPORTE PAGO FACIL " + fecha, null);
		
		
	}
	
	

	private void createZip(String nombreArchivo){
		String disposition = TMPDIR + FILE_SEPARATOR;
		
		try {
			FileOutputStream fos = new FileOutputStream(disposition + nombreArchivo + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			String file1Name =  nombreArchivo + ".ONL" ;
		

			addToZipFile(file1Name,disposition , zos);
	

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	
	public static void addToZipFile(String fileName ,String disposition,   ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(disposition + fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}


	
	
	
}
