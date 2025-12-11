package ar.com.ospim.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;

import ar.com.ospim.afiliados.reportes.NovedadesProcesadasOmintWSExcel;
import ar.com.ospim.afiliados.reportes.NovedadesProcesadasPrevencionWSExcel;
import ar.com.ospim.automatico.service.AgendaReporteUtil;
import ar.com.ospim.webservice.service.AfiliadoOpe;

public class TestXLSNovedadesProcesadas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HSSFWorkbook wb = null;
		
		try {	
			Calendar c = Calendar.getInstance();
//			c.set(2019, 9, 3, 15, 0);
			System.out.println(c.getTime());
			AgendaReporteUtil arUtil = new AgendaReporteUtil();

			List<AfiliadoOpe> resultados = arUtil.getNovedadesProcesadasPrevencion(new Date()); //c.getTime()
		
			wb = NovedadesProcesadasPrevencionWSExcel.generaPlanillaNovedadesProcesadas(resultados);
			
		} catch (SystemException e) {
			System.out.println(e);
		}
		
//		HSSFWorkbook wb =  NovedadesProcesadasOmintWSExcel.generaPlanillaNovedadesProcesadas(null);
		
		try {
			
			File homedir = new File(System.getProperty("user.home"));
//			File fileToRead = new File(homedir, "java/ex.txt");
			
		    FileOutputStream out = new FileOutputStream(new File(homedir, "new.xls"));
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
