package ar.com.ospim.crm.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.pdfbox.exceptions.COSVisitorException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.Splitter;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.NombreArchivo;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteImportadorImagenesErrorExcel;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ProcesarSubidaImagenesAfiliados extends AgendadoJava {

	
	private static Log _log = LogFactoryUtil.getLog(ProcesarSubidaImagenesAfiliados.class);
	
	File configDir = new File(System.getProperty("catalina.base"), "conf");
	File configFile = new File(configDir, "subida_imagenes_afiliaciones.properties");
	Properties props = new Properties();
	private static String  fecha = null;

	
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		_log.debug("Inicio ProcesarSubidaImagenesAfiliados");
		
	//	del();
		
		boolean hayArchivos = false;
		List<String> cuilsError = new ArrayList<String>();
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 

		fecha = DateUtils.getDateString(calendar.getTime(),  DateUtils.SHORT_MID);


		InputStream stream;
		try {
			stream = new FileInputStream(configFile);
			props.load(stream);
		} catch (FileNotFoundException e1) {
			_log.debug(e1);
		} catch (IOException e) {
			_log.debug(e);
		}
		
		
		String carpeta = props.getProperty("path");
		
		
		File dir = new File(carpeta);
	    String[] ficheros = dir.list(); // lista archivos
	    _log.debug("Path  " + dir != null ? dir.getPath() : "No hay Path" ) ;
	    if (ficheros == null){
	    	_log.debug("No hay ficheros en el directorio especificado");
	    }else { 
	    	//Corto Pdf por hoja con tamaño mayor a 5 megas
			split(carpeta);
		   ficheros = dir.list();// lista archivos
		   ficheros = ordenarPorHoja(ficheros);
	        for (int x=0;x<ficheros.length;x++){
	        	  hayArchivos = true;
	        	  Afiliado  afi = null;
	        	  String cuilTitular = null;
		          File fichero = new File  (carpeta + ficheros[x] );
		          cuilTitular = ficheros[x].substring(0,11);
		          try {
		        	  afi = EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuilTitular, 0);
				  } catch (SystemException e1) {
					  _log.debug(e1);
				  }  
		          if (afi != null){
		        	  subirImagen(cuilTitular ,fichero, cuilTitular); 
		          }else{
		        	  cuilsError.add(cuilTitular);
		          }
		          
	       }
	
	   }
	   //Si hay afiliados que no existen
	   if (!cuilsError.isEmpty()){
		   enviarAvisoError(cuilsError);
	   }
	    //borro archivos
	   ficheros = dir.list(); // lista archivos
	   delete(carpeta);
	   if (hayArchivos ){
		   enviarProcesado();
	   }
	   
	   _log.debug("Fin ProcesarSubidaImagenesAfiliados");
			
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String[] ordenarPorHoja(String[] ficheros){
		
		ArrayList<NombreArchivo> arrayCuis = new ArrayList<NombreArchivo>(); 
		 
		 for (int i = 0; i < ficheros.length; i++) {
			String n = ficheros[i];
			NombreArchivo nombre = new NombreArchivo(n);
			arrayCuis.add(nombre);
			
		}
		
		 Collections.sort(arrayCuis, NombreArchivo.COMPARE_BY_NUMBER); 
		 String[] ficherosAux = new String[arrayCuis.size() ];  


		 int i = 0;
		 for (NombreArchivo nombreArchivo : arrayCuis) {
			 ficherosAux[i] = nombreArchivo.getNombre();
			 i++; 
		}
		
		return ficherosAux;
	}

	
	private static void enviarAvisoError(List<String> cuils) {
		
		ReportesAutomaticosConfiguracion rac = null;
		
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		List<String> emails;
		String subject;
		String body;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_IMAGENES_AFILIADOS");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
 		   
		 HSSFWorkbook wb = new HSSFWorkbook();	
		 try {
		      wb= ReporteImportadorImagenesErrorExcel.generaReporteAfiNoExiste(cuils);
		 } catch (Exception e) {
		  _log.debug("Error al generar  ReporteImportadorImagenesErrorExcel");
		  }
		 		
		
 		subject = "Error alta imágenes proceso afiliaciones   ";
 		body= "Importador masivo de imágenes   " + fecha        +"\n\n\n\n\n" ;
 		
		 				
		MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
				emails, subject ,
				body , wb, "Reporte importador de imagenes automatico " + fecha +  " .xls");
 		
		
		//MailUtils.enviarMailGmailConAdj(rac.getMailFrom(), rac.getPass(), emails, subject, body, 3);
		
		
	}
	
	
	private static void enviarProcesado() {
		
		ReportesAutomaticosConfiguracion rac = null;
		
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		List<String> emails;
		String subject;
		String body;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_SUBIDA_IMAGENES_AFILIADOS");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.add(to);
		}
 		
		 		
		
 		subject = "Proceso alta imágenes afiliaciones   ";
 		body= "La importacion masiva de imágenes   " + fecha        +"\n\n\n\n\n" ;
 		
		body = body + "Se proceso correctamente ";
		
 				
 		
 		MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(), emails, subject, body, 3);
		
		
	}
	
	
	
	private static void subirImagen(String cuilTitular , File path , String name){
		
		File file;
		String filename = "";
	    String description = "Importador automatico  "  + fecha; //ParamUtil.getString(uploadReq, "descripcionFile");
		file = path; // uploadReq.getFile("importa_imagenes");	
		filename = name + ".pdf"; // uploadReq.getFileName("importa_imagenes");
		String mimeType =  MimeTypesUtil.getContentType(file);
				
		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setScopeGroupId(10136);
		serviceContext.setUserId(10808);
			
		uploadImagenAfiliado(filename, cuilTitular, description, file, serviceContext);
		
      
	}
	
	
	private static void uploadImagenAfiliado(String filename ,String cuilTitular , String description ,File file ,ServiceContext serviceContext ) {
		 
		Random rnd = new Random();

		DLFolder f = null;
			try {
				f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Afiliaciones");
			} catch (PortalException e1) {
				_log.debug(e1);
			} catch (SystemException e1) {
				_log.debug(e1);
			}
	        
		    long folderId = f.getFolderId();

	      	String title="";
	      	DLFileEntry dl=null;
	      	do {
	      		title=cuilTitular +"-" +(int)(rnd.nextDouble()*100);
	      		try{
	      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
	      		} catch(Exception e){ /* no hace falta, siempre arroja NoSuchFileException cuando no existe por duplicado.*/
	      			dl =  null;
	      		}   
	      	} while (dl!=null);    
	      	
	      	if(!"".equalsIgnoreCase(filename)){
	      		try{
		          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(10808, folderId, filename,
			        		filename, title, description, "", file, serviceContext);
		          
		          _log.debug("AGREGAR IMAGEN AL AFILIADO: " + entry.getDescription());
		          
	      		}catch(FileSizeException e){
	      			_log.error("FileSizeException   " + filename);
					_log.error(e);
	      		}catch(FileNameException e){
	      			_log.error("FileNameException   " + filename);
					_log.error(e);
	      		}catch(DuplicateFileException e){	
	      			_log.error("DuplicateFileException   " + filename);
	      			_log.error(e);
	      			//Si esta duplicado lo llamo de forma recusiva hasta que lo pueda resolver
	      			_log.error("llamo de nuevo a  uploadImagenAfiliado para que intente de nuevo ");
	      			uploadImagenAfiliado(filename, cuilTitular, description, file, serviceContext);
	      		}catch(Exception e){
	      			_log.error("Exception   " + filename);
	      			_log.error(e);
	      		}
	      	}
	}
	
	
	private static void split(String carpeta) {
	
	    File dir = new File(carpeta);
	    String[] ficheros = dir.list();
	    long longitud = 0;
	    
	    if (ficheros == null){
	    	_log.debug("No hay ficheros en el directorio especificado");
	    }else { 
	        for (int x=0;x<ficheros.length;x++){
	        
		          File fichero = new File  (carpeta + ficheros[x] );
		          longitud = (fichero.length()/1024000);
		          //System.out.println(longitud);
		          if (longitud > 4.1 ){
			          splitPdf(fichero.getPath());
			          fichero.delete();
		          }
	          }
	    }
	}
	
	private static void delete(String carpeta) {
		
	    File dir = new File(carpeta);
	    String[] ficheros = dir.list();
    
	    if (ficheros == null){
	    	_log.debug("No hay ficheros en el directorio especificado");
	    }else { 
	        for (int x=0;x<ficheros.length;x++){
	        
		          File fichero = new File  (carpeta + ficheros[x] );
		          fichero.delete();
		       
	        }
	    }
	}

		
	
	private static void splitPdf(String dir) {
		String aux;

		// Loading an existing PDF document
		File file = new File(dir);
		PDDocument document;
		try {
			document = PDDocument.load(file);

			// Instantiating Splitter class
			Splitter splitter = new Splitter();

			// splitting the pages of a PDF document
			List<PDDocument> Pages = splitter.split(document);

			// Creating an iterator
			Iterator<PDDocument> iterator = Pages.listIterator();
			aux = dir.replace(".pdf", "");
			
			// Saving each page as an individual document
			int i = 1;
			while (iterator.hasNext()) {
				PDDocument pd = iterator.next();
				pd.save(aux + "_" + i++ + ".pdf");
			}
			_log.debug("Multiple PDFs created");
			
			if (document != null){
			  System.out.println("close " + dir);	
		      document.close();
		    }
			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
	}
	
	/*private static void del (){
		try {
		
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234798.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234797.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234796.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234795.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234794.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234793.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234792.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234791.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234790.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234789.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234788.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234787.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234786.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234785.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234784.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234783.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234782.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234781.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234780.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234779.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234778.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234777.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234776.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234775.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234774.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234773.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234772.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234771.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234770.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234769.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234768.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234767.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234766.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234765.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234764.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234763.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234762.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234761.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234760.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234759.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234758.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234757.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234756.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234755.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234754.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234753.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234752.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234751.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234750.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234749.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234748.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234747.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234746.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234745.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234744.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234743.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234742.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234741.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234740.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234739.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234738.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234737.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234736.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234735.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234734.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234733.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234732.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234731.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234730.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234729.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234728.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234727.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234726.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234725.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234724.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234723.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234722.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234721.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234720.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234719.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234718.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234717.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234716.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234715.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234714.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234713.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234712.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234711.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234710.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234709.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234708.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234707.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234706.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234705.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234704.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234703.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234702.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234701.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234700.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234699.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234698.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234697.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234696.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234695.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234694.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234693.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234692.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234691.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234690.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234689.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234688.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234687.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234686.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234685.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234684.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234683.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234682.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234681.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234680.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234679.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234678.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234677.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234676.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234675.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234674.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234673.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234672.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234671.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234670.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234669.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234668.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234667.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234666.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234665.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234664.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234663.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234662.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234661.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234660.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234659.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234658.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234657.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234656.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234655.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234654.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234653.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234652.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234651.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234650.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234649.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234648.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234647.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234646.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234645.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234644.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234643.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234642.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234641.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234640.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234639.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234638.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234637.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234636.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234635.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234634.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234633.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234632.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234631.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234630.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234629.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234628.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234627.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234626.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234625.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234624.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234623.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234622.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234621.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234620.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234619.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234618.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234617.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234616.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234615.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234614.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234613.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234612.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234611.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234610.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234609.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234608.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234607.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234606.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234605.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234604.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234603.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234602.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234601.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234600.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234599.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234598.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234597.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234596.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234595.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234594.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234593.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234592.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234591.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234590.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234589.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234588.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234587.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234586.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234585.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234584.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234583.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234582.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234581.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234580.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234579.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234578.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234577.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234576.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234575.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234574.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234573.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234572.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234571.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234570.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234569.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234568.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234567.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234566.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234565.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234564.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234563.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234562.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234561.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234560.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234559.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234558.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234557.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234556.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234555.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234554.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234553.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234552.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234551.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234550.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234549.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234548.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234547.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234546.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234545.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234544.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234543.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234542.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234541.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234540.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234539.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234538.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234537.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234536.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234535.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234534.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234533.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234532.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234531.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234530.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234529.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234528.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234527.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234526.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234525.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234524.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234523.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234522.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234521.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234520.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234519.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234518.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234517.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234516.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234515.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234514.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234513.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234512.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234511.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234510.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234509.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234508.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234507.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234506.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234505.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234504.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234503.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234502.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234501.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234500.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234499.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234498.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234497.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234496.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234495.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234494.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234493.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234492.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234491.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234490.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234489.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234488.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234487.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234486.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234485.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234484.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234483.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234482.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234481.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234480.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234479.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234478.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234477.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234476.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234475.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234474.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234473.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234472.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234471.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234470.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234469.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234468.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234467.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234466.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234465.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234464.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234463.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234462.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234461.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234460.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234459.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234458.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234457.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234456.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234455.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234454.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234453.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234452.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234451.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234450.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234449.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234448.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234447.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234446.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234445.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234444.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234443.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234442.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234441.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234440.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234439.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234438.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234437.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234436.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234435.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234434.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234433.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234432.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234431.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234430.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234429.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234428.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234427.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234426.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234425.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234424.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234423.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234422.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234421.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234420.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234419.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234418.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234417.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234416.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234415.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234414.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234413.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234412.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234411.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234410.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234409.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234408.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234407.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234406.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234405.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234404.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234403.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234402.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234401.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234400.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234399.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234398.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234397.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234396.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234395.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234394.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234393.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234392.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234391.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234390.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234389.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234388.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234387.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234386.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234385.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234384.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234383.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234382.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234381.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234380.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234379.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234378.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234377.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234376.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234375.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234374.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234373.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234372.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234371.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234370.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234369.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234368.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234367.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234366.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234365.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234364.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234363.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234362.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234361.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234360.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234359.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234358.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234357.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234356.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234355.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234354.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234353.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234352.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234351.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234350.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234349.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234348.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234347.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234346.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234345.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234344.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234343.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234342.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234341.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234340.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234339.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234338.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234337.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234336.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234335.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234334.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234333.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234332.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234331.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234330.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234329.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234328.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234327.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234326.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234325.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234324.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234323.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234322.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234321.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234320.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234319.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234318.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234317.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234316.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234315.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234314.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234313.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234312.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234311.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234310.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234309.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234308.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234307.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234306.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234305.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234304.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234303.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234302.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234301.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234300.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234299.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234298.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234297.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234296.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234295.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234294.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234293.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234292.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234291.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234290.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234289.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234288.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234287.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234286.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234285.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234284.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234283.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234282.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234281.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234280.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234279.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234278.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234277.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234276.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234275.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234274.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234273.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234272.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234271.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234270.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234269.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234268.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234267.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234266.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234265.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234264.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234263.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234262.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234261.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234260.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234259.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234258.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234257.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234256.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234255.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234254.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234253.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234252.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234251.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234250.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234249.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234248.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234247.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234246.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234245.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234244.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234243.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234242.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234241.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234240.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234239.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234238.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234237.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234236.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234235.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234234.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234233.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234232.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234231.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234230.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234229.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234228.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234227.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234226.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234225.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234224.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234223.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234222.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234221.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234220.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234219.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234218.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234217.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234216.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234215.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234214.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234213.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234212.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234211.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234210.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234209.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234208.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234207.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234206.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234205.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234204.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234203.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234202.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234201.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234200.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234199.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234198.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234197.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234196.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234195.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234194.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234193.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234192.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234191.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234190.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234189.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234188.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234187.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234186.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234185.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234184.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234183.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234182.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234181.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234180.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234179.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234178.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234177.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234176.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234175.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234174.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234173.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234172.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234171.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234170.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234169.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234168.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234167.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234166.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234165.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234164.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234163.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234162.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234161.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234160.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234159.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234158.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234157.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234156.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234155.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234154.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234153.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234152.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234151.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234150.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234149.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234148.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234147.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234146.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234145.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234144.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234143.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234142.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234141.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234140.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234139.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234138.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234137.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234136.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234135.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234134.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234133.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234132.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234131.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234130.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234129.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234128.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234127.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234126.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234125.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234124.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234123.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234122.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234121.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234120.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234119.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234118.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234117.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234116.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234115.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234114.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234113.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234112.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234111.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234110.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234109.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234108.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234107.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234106.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234105.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234104.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234103.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234102.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234101.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234100.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234099.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234098.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234097.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234096.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234095.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234094.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234093.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234092.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234091.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234090.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234089.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234088.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234087.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234086.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234085.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234084.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234083.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234082.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234081.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234080.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234079.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234078.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234077.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234076.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234075.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234074.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234073.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234072.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234071.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234070.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234069.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234068.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234067.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234066.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234065.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234064.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234063.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234062.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234061.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234060.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234059.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234058.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234057.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234056.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234055.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234054.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234053.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234052.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234051.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234050.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234049.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234048.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234047.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234046.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234045.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234044.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234043.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234042.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234041.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234040.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234039.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234038.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234037.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234036.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234035.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234034.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234033.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234032.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234031.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234030.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234029.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234028.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234027.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234026.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234025.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234024.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234023.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234022.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234021.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234020.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234019.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234018.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234017.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234016.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234015.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234014.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234013.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234012.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234011.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234010.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234009.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234008.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234007.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234006.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234005.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234004.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234003.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234002.pdf");
			DLFileEntryLocalServiceUtil.deleteFileEntry(16308, "DLFE-234001.pdf");



		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
