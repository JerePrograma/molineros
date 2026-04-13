package ar.com.ospim.crm.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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
import ar.com.ospim.afiliados.beans.SubidaFTPPadronIGS;
import ar.com.ospim.afiliados.beans.TotalesPadronIGS;
import ar.com.ospim.afiliados.services.SubidaPadronIGSServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteSubidaFTPIGS;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ProcesarSubidaFTPPadronIGS extends AgendadoJava {

	private static final String FILE_SEPARATOR =  System.getProperty("file.separator");
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");
	
	private static Log _log = LogFactoryUtil
			.getLog(ProcesarSubidaFTPPadronIGS.class);
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		boolean error = false; 
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		
		Date fechaDesde = DateUtils.getFirstDateOfMonth(calendar.getTime(), true);
		Date fechaHasta = DateUtils.getLastDateOfMonth(calendar.getTime(), true);
		
		String periodo = null;
		
		periodo = DateUtils.getDateString(calendar.getTime(), "YYYYMM");
		
		List<SubidaFTPPadronIGS> padron =  null;
		List<TotalesPadronIGS> totales =  null;
		String nombreArchivo =  null;
		nombreArchivo = "OSP_" + periodo + "01" + "_nov.txt";
		try {
			SubidaPadronIGSServiceUtil.grabarReporte(fechaDesde, fechaHasta);
			padron = SubidaPadronIGSServiceUtil.generarArchivo(fechaDesde);
			totales = SubidaPadronIGSServiceUtil.generarTotales(fechaDesde);
		} catch (Exception e) {
			_log.error(e.getMessage());
			error= true;
		}

		crearTxtPadron(padron, nombreArchivo);
		
		try {
			try {
				subirNovedadesFTP(nombreArchivo);
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
		
		HSSFWorkbook wb = new HSSFWorkbook();	
		try {
			 wb= ReporteSubidaFTPIGS.generaReporte(totales,periodo);
	    } catch (Exception e) {
		   _log.debug("Error al generar subida padron IGS");
			error= true;
	    }
		if (error == false) {			
			enviarReporte(periodo, wb);
		}else {
			enviarAvisoError(periodo);
		}
	
		
			
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
private void crearTxtPadron(List<SubidaFTPPadronIGS> lista, String fileName) {
		
		ServletOutputStream out = null;
		
		PrintWriter writer = null;
		try {
			String disposition = TMPDIR + FILE_SEPARATOR +  fileName;
			writer = new PrintWriter(disposition, "UTF-8");
					
			String cadena;
			
			cadena ="";
			//cabecera
			cadena+= "afiliado"; //afiliado
			cadena +="|";
			cadena+=  "docu_tipo"; //docu_tipo
			cadena +="|";
			cadena+=  "docu_numero"; //docu_numero
			cadena +="|";
			cadena+=  "num_ospim";//num_ospim
			cadena +="|";
			cadena+=  "plan_igs";//plan_igs
			cadena +="|";
			cadena+=  "telefono";//telefono
			cadena +="|";
			cadena+=  "localidad";//localidad
			cadena +="|";
			cadena+=  "provincia";//provincia	
			cadena +="|";
			cadena+=  "movimiento";//movimiento	
			
						
			
			writer.println(cadena);
			
			
			for(SubidaFTPPadronIGS d:lista) {		
					
				cadena ="";
				//afiliados
				
				cadena+=  padRight(d.getAfiliado(), 30); // afiliado
				cadena +="|";
				cadena+=  padRight(d.getTipoDocumento(),2);          // docu_tipo
				cadena +="|";
				cadena+=  padRight(d.getDocumentoNumero(),8);  //docu_numero
				cadena +="|";
				cadena+=  padRight(d.getNumeroOSPIM(),7); //num_ospim
				cadena +="|";
				cadena+=  padRight(d.getPlanIGS(),12);//plan_igs
				cadena +="|";
				cadena+=  padRight(d.getTelefono(),40); //telefono
				cadena +="|";
				cadena+=  padRight(d.getLocalidad(), 30); //localidad
				cadena +="|";
				cadena+=  padRight(d.getProvincia(), 30); //provincia
				cadena +="|";
				cadena+=  padRight(d.getMovimito(),1);          // movimiento
		
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

	private static String padRight(String s, int n) {
	    return String.format("%1$-" + n + "s", s);  
	}
	
	private void subirNovedadesFTP(String file) throws IOException, JSchException, SftpException {
		String disposition = TMPDIR + FILE_SEPARATOR +  file;
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "subida_ftp_padron_igs.properties");

		
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String host = props.getProperty("host");
			String user = props.getProperty("user");
			String pass = props.getProperty("pass");
		    int port = Integer.parseInt(props.getProperty("port"));
		    JSch jsch = new JSch(); 
		    Session session;
		
			session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(pass);
			session.connect();
				
		    _log.debug("Connection established.");
		    _log.debug("Creating SFTP Channel.");
			
			ChannelSftp sftp = (ChannelSftp)session.openChannel("sftp");
		    sftp.connect();
			
		   // sftp.cd("/home/czuluaga/pruebasftp"); // el usuario me deja en el directorio no necesito navegar
		    sftp.cd("/archivos");
		   
		    _log.debug("Subiendo ..." + file);
	        sftp.put(disposition, file);
	 
	        _log.debug("Archivos subidos.");
	        
	        sftp.exit();
	        sftp.disconnect();
	        session.disconnect();
			
	
	}

	private void enviarReporte(String periodo, HSSFWorkbook wb) {
		
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
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_FTP_IGS");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
		
		MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
				emails, "SUBIDA FTP REPORTE IGS " + periodo,
				"SUBIDA FTP REPORTE IGS " + periodo, wb, "Reporte de padrón IGS " + periodo + ".xls");
		
	}
	private void enviarAvisoError(String periodo) {
		
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
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_FTP_IGS");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
		
		MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(),
				emails, "ERROR SUBIDA FTP REPORTE IGS " + periodo,
				"ERROR SUBIDA FTP REPORTE IGS " + periodo, null);
		
		
	}
	
	
}
