package ar.com.ospim.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.crm.beans.ReportePreCargaMasReclamosExcel;
import ar.com.ospim.util.DateUtils;

public class TestXLSPreCargaMasReclamosExcel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HSSFWorkbook wb = null;
		
		
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		
		String fecha = null;
		
		fecha = DateUtils.getDateString(calendar.getTime(), "dd-MM-yyyy");
		
		
	//	wb = new HSSFWorkbook();	
	
		
		wb= ReportePreCargaMasReclamosExcel.generaReporte();
			
	
		
		try {
			
			File homedir = new File(System.getProperty("user.home"));
//			File fileToRead = new File(homedir, "java/ex.txt");
			
		    FileOutputStream out = new FileOutputStream(new File(homedir, "Reporte Reclamos Prestacional.xls"));
		    wb.write(out);
		    out.close();
		    System.out.println("Excel written successfully..");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
