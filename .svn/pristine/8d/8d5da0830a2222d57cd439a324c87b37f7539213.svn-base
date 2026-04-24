package ar.com.ospim.procesaArchivos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoListadoSSSalud;
import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoManualDat;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleListadoSSSalud;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleManualDat;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosFarmaciaServiceImpl;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class ProcesaArchivosFarmacia {
	private static Log _log = LogFactoryUtil.getLog(ProcesaArchivos.class);


	public void procesarArchivoManualDat(BufferedReader scanner)
			throws IOException, SQLException {
		ArchivoManualDat nuevoArchivo = new ArchivoManualDat();
		List<DetalleManualDat> detalleList = new ArrayList<DetalleManualDat>();
		String line = null;
		int linea = 0;
		DetalleManualDat deta = null;
		
		while ((line = scanner.readLine()) != null) {
			_log.debug("manual data linea: " + linea);
			if (null != line && !line.trim().equals("")) {
				
				try {
					deta = new DetalleManualDat(line);
				} catch (ParseException e) {
					
					_log.error("Medicamento registro: " +  deta.getNro_registro());
					
					_log.error(e);
					
					throw new IOException(e.getMessage());
				}
				
				detalleList.add(deta);
			}
			linea++;
		}
		nuevoArchivo.setDetalle(detalleList);
		ProcesaArchivosFarmaciaServiceImpl servicio = new ProcesaArchivosFarmaciaServiceImpl();
		servicio.grabaArchivo(nuevoArchivo);
	}

	public void procesarArchivoListadoSSSalud(FileInputStream file)
			throws IOException, ParseException, SQLException {
		
		ArchivoListadoSSSalud archivo = new ArchivoListadoSSSalud();
		List<DetalleListadoSSSalud> filas = new ArrayList<DetalleListadoSSSalud>();
		DetalleListadoSSSalud detalleFila = null;
//		String line = null;
//		for(int i=0; i<5;i++){
//			scanner.readLine();
//		}
//		while ((line = scanner.readLine()) != null) {
//			System.out.println("LINE: "+line);
//			if (null != line) {
//				DetalleListadoSSSalud deta = new DetalleListadoSSSalud(line);				
//				detalleList.add(deta);
//			}
//		}
//		nuevoArchivo.setDetalle(detalleList);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
//logger.debug(qRow);		    
		    _log.debug("pmo data fila: " + qRow);
		    if(qRow>3){//if(qRow>0){
//				id	atc	generico	nombre	presentacion	pvp	acargoos	acargoafil	laboratorio	registro	pr	grupoter	obser
//		    	0	1	2			3		4				5	6			7			8			9			10	11			12
		    	
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       detalleFila = new DetalleListadoSSSalud();
		       
		       while (cellIterator.hasNext()){
		    	   
				celda = cellIterator.next();

				try{
					Double xval;
					if(qCel==0){
						xval = celda.getNumericCellValue();
						detalleFila.setId(xval.intValue());
						
					}else if(qCel==1){
						detalleFila.setAtc(celda.toString());
					}else if(qCel==2){
						detalleFila.setGenerico(celda.toString());
					}else if(qCel==3){
						detalleFila.setNombre(celda.toString());
					}else if(qCel==4){
						detalleFila.setPresentacion(celda.toString());
					}else if(qCel==5){
						detalleFila.setPvp(new BigDecimal(celda.toString().replace("$", "").replace(".", "").replace(",", ".").trim()));
					}else if(qCel==6){	
						detalleFila.setAcargoos(new BigDecimal(celda.toString().replace("$", "").replace(".", "").replace(",", ".").trim()));
					}else if(qCel==7){	
						detalleFila.setAcargoafil(new BigDecimal(celda.toString().replace("$", "").replace(".", "").replace(",", ".").trim()));
					}else if(qCel==8){
						detalleFila.setLaboratorio(celda.toString());	
					}else if(qCel==9){
						xval = celda.getNumericCellValue();
						detalleFila.setRegistro(xval.intValue());
					}else if(qCel==10){	
//						detalleFila.setCober(new BigDecimal(celda.getNumericCellValue())); pr						
					}else if(qCel==11){
						xval = celda.getNumericCellValue();
						detalleFila.setGrupoter(xval.intValue());
					}else if(qCel==12){
						detalleFila.setObservaciones(celda.toString());	
					}	
				}catch(Exception e){
					_log.error(e);
				}
				
				qCel++;
				
				
			  }
		      filas.add(detalleFila); 
		     
		   }
		   qRow++; 
		} 
		
		archivo.setDetalle(filas);
		
		ProcesaArchivosFarmaciaServiceImpl servicio = new ProcesaArchivosFarmaciaServiceImpl();
		servicio.grabaArchivo(archivo);

	}
	
	public void actualizarVademecum(Date periodoArchivo , User user)throws SQLException {
		ProcesaArchivosFarmaciaServiceImpl servicio = new ProcesaArchivosFarmaciaServiceImpl();
		servicio.actualizaVademecum(periodoArchivo,user);
		
	}

}
