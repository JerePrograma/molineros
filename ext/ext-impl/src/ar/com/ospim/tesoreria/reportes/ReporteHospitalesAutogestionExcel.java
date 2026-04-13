package ar.com.ospim.tesoreria.reportes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.HospitalAutogestion;
import ar.com.ospim.tesoreria.service.ReporteHospitalesAutogestionServiceImpl;
import ar.com.ospim.util.DateUtils;

public class ReporteHospitalesAutogestionExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteHospitalesAutogestionExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		int idCajaChica = ParamUtil.getInteger(req, "id_caja_chica");
		int entidad = ParamUtil.getInteger(req, "entidad");
		
		try {
			Date fechaIni = null;
			Date fechaFin = null;
			Date fechaImpre = null;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			try {
				String fechaIniDia = ParamUtil.getString(req,
						"fechaDesdeDia");
				String fechaIniMes = ParamUtil.getString(req,
						"fechaDesdeMes");
				fechaIniMes = String
						.valueOf(Integer.valueOf(fechaIniMes) + 1);
				String fechaIniAnio = ParamUtil.getString(req,
						"fechaDesdeAnio");
				fechaIni = format.parse(fechaIniDia + "-" + fechaIniMes
						+ "-" + fechaIniAnio);

			} catch (Exception e) {
				fechaIni = new Date();
			}


						
			List<HospitalAutogestion>reporte = ReporteHospitalesAutogestionServiceImpl.getListaHospitalesAutogestion(fechaIni);
			
			
			return generarReporte(fechaIni,reporte);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Hospitales Autogestion", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(Date fechaIni,List<HospitalAutogestion> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
	
		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
	
		HSSFCellStyle styleAllTop = getStyleAll(wb);
	
		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
	
		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
	
		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
	
		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
	
		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
	
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i, fechaIni);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
				
		for (HospitalAutogestion repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		i++;
		
		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			HospitalAutogestion repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		if(repo.getFecha_proceso_transf()!=null){
		  cell0.setCellValue(repo.getFecha_proceso_transf() );
		  cell0.setCellStyle(styleFechaLeftTop);
		}else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getCodigo_organismo_transf()!=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getCodigo_organismo_transf()));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
	
		HSSFCell cell2 = row.createCell(2);
		if(repo.getDebito_credito_transf()!=null){
		  cell2.setCellValue(new HSSFRichTextString(repo.getDebito_credito_transf()));
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell3 = row.createCell(3);
		if(repo.getCodigo_organismo()!=null){
		  cell3.setCellValue(new HSSFRichTextString(repo.getCodigo_organismo()));
		  cell3.setCellStyle(styleAll);
	    }else{
		  cell3.setCellValue(new HSSFRichTextString(""));
		  cell3.setCellStyle(styleAll);
		}
	
		HSSFCell cell4 = row.createCell(4);
		if(repo.getNumero_expediente()!=null){
		  cell4.setCellValue(new HSSFRichTextString(repo.getNumero_expediente()));
		  cell4.setCellStyle(styleAll);
	    }else{
		  cell4.setCellValue(new HSSFRichTextString(""));
		  cell4.setCellStyle(styleAll);
		}
		
		HSSFCell cell5 = row.createCell(5);
		if(repo.getFecha_proceso()!=null){
		  cell5.setCellValue(repo.getFecha_proceso() );
		  cell5.setCellStyle(styleFechaLeftTop);
		}else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);	
		}
		
		HSSFCell cell6 = row.createCell(6);
		if(repo.getFecha_transferencia()!=null){
		 cell6.setCellValue(repo.getFecha_transferencia() );
		 cell6.setCellStyle(styleFechaLeftTop);
		}else{
		 cell6.setCellValue(new HSSFRichTextString(""));
		 cell6.setCellStyle(styleAll);
		}
		
		HSSFCell cell7 = row.createCell(7);
		if(repo.getClasif_expediente()!=null){
		  cell7.setCellValue(new HSSFRichTextString(repo.getClasif_expediente() ));
		  cell7.setCellStyle(styleAll);
	    }else{
		  cell7.setCellValue(new HSSFRichTextString(""));
		  cell7.setCellStyle(styleAll);
		}
		
		HSSFCell cell8 = row.createCell(8);
		if(repo.getImporte_total()!=null){
		  cell8.setCellValue(repo.getImporte_total().doubleValue() );
		  cell8.setCellStyle(styleMoneyRight);
	    }else{
		  cell8.setCellValue(new HSSFRichTextString(""));
		  cell8.setCellStyle(styleAll);
		}
	
		HSSFCell cell9 = row.createCell(9);
		if(repo.getNro_cuota()!=null){
		  cell9.setCellValue(repo.getNro_cuota());
		  cell9.setCellStyle(styleAll);
	    }else{
		  cell9.setCellValue(new HSSFRichTextString(""));
		  cell9.setCellStyle(styleAll);
		}
		
		HSSFCell cell10 = row.createCell(10);
		if(repo.getImporte_transferencia()!=null){
		  cell10.setCellValue(repo.getImporte_transferencia().doubleValue());
		  cell10.setCellStyle(styleMoneyRight);
	    }else{
		  cell10.setCellValue(new HSSFRichTextString(""));
		  cell10.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell11 = row.createCell(11);
		if(repo.getDebito_credito()!=null){
		  cell11.setCellValue(new HSSFRichTextString(repo.getDebito_credito()));
		  cell11.setCellStyle(styleAll);
	    }else{
		  cell11.setCellValue(new HSSFRichTextString(""));
		  cell11.setCellStyle(styleAll);
		}

		
		HSSFCell cell12 = row.createCell(12);
		if(repo.getNro_expediente_original()!=null){
		  cell12.setCellValue(new HSSFRichTextString(repo.getNro_expediente_original()));
		  cell12.setCellStyle(styleAll);
	    }else{
		  cell12.setCellValue(new HSSFRichTextString(""));
		  cell12.setCellStyle(styleAll);
		}
	    
		HSSFCell cell13 = row.createCell(13);
		if(repo.getCodigo_htal()!=null){
		  cell13.setCellValue(new HSSFRichTextString(repo.getCodigo_htal() ));
		  cell13.setCellStyle(styleAll);
	    }else{
		  cell13.setCellValue(new HSSFRichTextString(""));
		  cell13.setCellStyle(styleAll);
		}
		
		HSSFCell cell14 = row.createCell(14);
		if(repo.getNro_expediente_anssal()!=null){
		  cell14.setCellValue(new HSSFRichTextString(repo.getNro_expediente_anssal() ));
		  cell14.setCellStyle(styleAll);
	    }else{
		  cell14.setCellValue(new HSSFRichTextString(""));
		  cell14.setCellStyle(styleAll);
		}
		 
		HSSFCell cell15 = row.createCell(15);
		if(repo.getObservacion()!=null){
		  cell15.setCellValue(new HSSFRichTextString(repo.getObservacion()));
		  cell15.setCellStyle(styleAll);
	    }else{
		  cell15.setCellValue(new HSSFRichTextString(""));
		  cell15.setCellStyle(styleAll);
		}
		
		HSSFCell cell16 = row.createCell(16);
		if(repo.getDetalle_juzgado()!=null){
		  cell16.setCellValue(new HSSFRichTextString(repo.getDetalle_juzgado() ));
		  cell16.setCellStyle(styleAll);
	    }else{
		  cell16.setCellValue(new HSSFRichTextString(""));
		  cell16.setCellStyle(styleAll);
		}
		
		HSSFCell cell17 = row.createCell(17);
		if(repo.getDetalle_secretaria()!=null){
		  cell17.setCellValue(new HSSFRichTextString(repo.getDetalle_secretaria()));
		  cell17.setCellStyle(styleAll);
	    }else{
		  cell17.setCellValue(new HSSFRichTextString(""));
		  cell17.setCellStyle(styleAll);
		}
		
		HSSFCell cell18 = row.createCell(18);
		if(repo.getAutos()!=null){
		  cell18.setCellValue(new HSSFRichTextString(repo.getAutos()));
		  cell18.setCellStyle(styleAll);
	    }else{
		  cell18.setCellValue(new HSSFRichTextString(""));
		  cell18.setCellStyle(styleAll);
		}
		
		HSSFCell cell19 = row.createCell(19);
		if(repo.getNumero_factura()!=null){
		  cell19.setCellValue(new HSSFRichTextString(repo.getNumero_factura() ));
		  cell19.setCellStyle(styleAll);
	    }else{
		  cell19.setCellValue(new HSSFRichTextString(""));
		  cell19.setCellStyle(styleAll);
		}
		
		HSSFCell cell20 = row.createCell(20);
		if(repo.getId_punto_venta()!=null){
		  cell20.setCellValue(repo.getId_punto_venta());
		  cell20.setCellStyle(styleAll);
	    }else{
		  cell20.setCellValue(new HSSFRichTextString(""));
		  cell20.setCellStyle(styleAll);
		}
		
		HSSFCell cell21 = row.createCell(21);
		if(repo.getCompro_tipo()!=null){
		  cell21.setCellValue(new HSSFRichTextString(repo.getCompro_tipo()));
		  cell21.setCellStyle(styleAll);
	    }else{
		  cell21.setCellValue(new HSSFRichTextString(""));
		  cell21.setCellStyle(styleAll);
		}
		
		HSSFCell cell22 = row.createCell(22);
		if(repo.getCompro_nro()!=null){
		  cell22.setCellValue(new HSSFRichTextString(repo.getCompro_nro()));
		  cell22.setCellStyle(styleAll);
	    }else{
		  cell22.setCellValue(new HSSFRichTextString(""));
		  cell22.setCellStyle(styleAll);
		}
		
		HSSFCell cell23 = row.createCell(23);
		if(repo.getFactu_perio()!=null){
		  cell23.setCellValue(repo.getFactu_perio());
		  cell23.setCellStyle(styleFechaLeftTop);
	    }else{
		 cell23.setCellValue(new HSSFRichTextString(""));
		 cell23.setCellStyle(styleAll);
		}
		
		HSSFCell cell24 = row.createCell(24);
		if(repo.getFecha()!=null){
		 cell24.setCellValue(repo.getFecha());
		 cell24.setCellStyle(styleFechaLeftTop);
	    }else{
		 cell24.setCellValue(new HSSFRichTextString(""));
		 cell24.setCellStyle(styleAll);
		}
		
		HSSFCell cell25 = row.createCell(25);
		if(repo.getImpre_fecha()!=null){
		  cell25.setCellValue(repo.getImpre_fecha());
		  cell25.setCellStyle(styleFechaLeftTop);
		}else{
		  cell25.setCellValue(new HSSFRichTextString(""));
		  cell25.setCellStyle(styleAll);
		}
		
		HSSFCell cell26 = row.createCell(26);
		if(repo.getCuil_titular()!=null){
		  cell26.setCellValue(new HSSFRichTextString(repo.getCuil_titular()));
		  cell26.setCellStyle(styleAll);
	    }else{
		  cell26.setCellValue(new HSSFRichTextString(""));
		  cell26.setCellStyle(styleAll);
		}
		
		HSSFCell cell27 = row.createCell(27);
		if(repo.getInte()!=null){
		  cell27.setCellValue(repo.getInte());
		  cell27.setCellStyle(styleAll);
	    }else{
		  cell27.setCellValue(new HSSFRichTextString(""));
		  cell27.setCellStyle(styleAll);
		}
	
		HSSFCell cell28 = row.createCell(28);
		if(repo.getVto()!=null){
		 cell28.setCellValue(repo.getVto());
		 cell28.setCellStyle(styleFechaLeftTop);
	    }else{
		 cell28.setCellValue(new HSSFRichTextString(""));
		 cell28.setCellStyle(styleAll);
		}

		HSSFCell cell29 = row.createCell(29);
		if(repo.getVto2()!=null){
		  cell29.setCellValue(repo.getVto2());
		  cell29.setCellStyle(styleFechaLeftTop);
		}else{
		  cell29.setCellValue(new HSSFRichTextString(""));
		  cell29.setCellStyle(styleAll);
		}
		
		HSSFCell cell30 = row.createCell(30);
		if(repo.getExen()!=null){
		  cell30.setCellValue(repo.getExen().doubleValue());
		  cell30.setCellStyle(styleMoneyRight);
	    }else{
		  cell30.setCellValue(new HSSFRichTextString(""));
		  cell30.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell31 = row.createCell(31);
		if(repo.getGrava()!=null){
		  cell31.setCellValue(repo.getGrava().doubleValue());
		  cell31.setCellStyle(styleMoneyRight);
	    }else{
		  cell31.setCellValue(new HSSFRichTextString(""));
		  cell31.setCellStyle(styleAll);
		}
		
		HSSFCell cell32 = row.createCell(32);
		if(repo.getIva_total()!=null){
		  cell32.setCellValue(repo.getIva_total().doubleValue());
		  cell32.setCellStyle(styleMoneyRight);
	    }else{
		  cell32.setCellValue(new HSSFRichTextString(""));
		  cell32.setCellStyle(styleAll);
		}
		
		HSSFCell cell33 = row.createCell(33);
		if(repo.getIvan_total()!=null){
		  cell33.setCellValue(repo.getIvan_total().doubleValue());
		  cell33.setCellStyle(styleMoneyRight);
	    }else{
		  cell33.setCellValue(new HSSFRichTextString(""));
		  cell33.setCellStyle(styleAll);
		}
	
		HSSFCell cell34 = row.createCell(34);
		if(repo.getTotal()!=null){
		  cell34.setCellValue(repo.getTotal().doubleValue());
		  cell34.setCellStyle(styleMoneyRight);
	    }else{
		  cell34.setCellValue(new HSSFRichTextString(""));
		  cell34.setCellStyle(styleAll);
		}
		
		HSSFCell cell35 = row.createCell(35);
		if(repo.getCance()!=null){
		  cell35.setCellValue(new HSSFRichTextString(repo.getCance()));
		  cell35.setCellStyle(styleAll);
	    }else{
		  cell35.setCellValue(new HSSFRichTextString(""));
		  cell35.setCellStyle(styleAll);
		}
            
		HSSFCell cell36 = row.createCell(36);
		if(repo.getAnu_moti()!=null){
		  cell36.setCellValue(repo.getAnu_moti());
		  cell36.setCellStyle(styleAll);
	    }else{
		  cell36.setCellValue(new HSSFRichTextString(""));
		  cell36.setCellStyle(styleAll);
		}
				
		HSSFCell cell37 = row.createCell(37);
		if(repo.getAnu_fecha()!=null){
		  cell37.setCellValue(repo.getAnu_fecha());
		  cell37.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell37.setCellValue(new HSSFRichTextString(""));
		  cell37.setCellStyle(styleAll);
		}
		
		HSSFCell cell38 = row.createCell(38);
		if(repo.getAnu_usu()!=null){
		  cell38.setCellValue(new HSSFRichTextString(repo.getAnu_usu()));
		  cell38.setCellStyle(styleAll);
	    }else{
		  cell38.setCellValue(new HSSFRichTextString(""));
		  cell38.setCellStyle(styleAll);
		}
		
		HSSFCell cell39 = row.createCell(39);
		if(repo.getObservaciones()!=null){
		  cell39.setCellValue(new HSSFRichTextString(repo.getObservaciones()));
		  cell39.setCellStyle(styleAll);
	    }else{
		  cell39.setCellValue(new HSSFRichTextString(""));
		  cell39.setCellStyle(styleAll);
		}  
		
		HSSFCell cell40 = row.createCell(40);
		if(repo.getAlta_fecha()!=null){
		  cell40.setCellValue(repo.getAlta_fecha());
		  cell40.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell40.setCellValue(new HSSFRichTextString(""));
		  cell40.setCellStyle(styleAll);
		}
		
		HSSFCell cell41 = row.createCell(41);
		if(repo.getAlta_usr()!=null){
		  cell41.setCellValue(new HSSFRichTextString(repo.getAlta_usr()));
	 	  cell41.setCellStyle(styleAll);
	    }else{
		  cell41.setCellValue(new HSSFRichTextString(""));
		  cell41.setCellStyle(styleAll);
		}
	
		HSSFCell cell42 = row.createCell(42);
		if(repo.getModi_fecha()!=null){
		  cell42.setCellValue(repo.getModi_fecha());
		  cell42.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell42.setCellValue(new HSSFRichTextString(""));
		  cell42.setCellStyle(styleAll);
		}
		
		HSSFCell cell43 = row.createCell(43);
		if(repo.getModi_usr()!=null){
		  cell43.setCellValue(new HSSFRichTextString(repo.getModi_usr()));
		  cell43.setCellStyle(styleAll);
	    }else{
		  cell43.setCellValue(new HSSFRichTextString(""));
		  cell43.setCellStyle(styleAll);
		}
		
		HSSFCell cell44 = row.createCell(44);
		if(repo.getBaja_fecha()!=null){
		  cell44.setCellValue(repo.getBaja_fecha());
		  cell44.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell44.setCellValue(new HSSFRichTextString(""));
		  cell44.setCellStyle(styleAll);
		}
		
		HSSFCell cell45 = row.createCell(45);
		if(repo.getBaja_usr()!=null){
		  cell45.setCellValue(new HSSFRichTextString(repo.getBaja_usr()));
		  cell45.setCellStyle(styleAll);
	    }else{
		  cell45.setCellValue(new HSSFRichTextString(""));
		  cell45.setCellStyle(styleAll);
		}
	
		HSSFCell cell46 = row.createCell(46);
		if(repo.getFecha_recepcion()!=null){
		  cell46.setCellValue(repo.getFecha_recepcion());
		  cell46.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell46.setCellValue(new HSSFRichTextString(""));
		  cell46.setCellStyle(styleAll);
		}
		
		HSSFCell cell47 = row.createCell(47);
		if(repo.getFecha_emision()!=null){
		  cell47.setCellValue(repo.getFecha_emision());
		  cell47.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell47.setCellValue(new HSSFRichTextString(""));
		  cell47.setCellStyle(styleAll);
		}
		
		HSSFCell cell48 = row.createCell(48);
		if(repo.getCuit()!=null){
		  cell48.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell48.setCellStyle(styleAll);
	    }else{
		  cell48.setCellValue(new HSSFRichTextString(""));
		  cell48.setCellStyle(styleAll);
		}
	
		HSSFCell cell49 = row.createCell(49);
		if(repo.getCompro_letra()!=null){
		  cell49.setCellValue(new HSSFRichTextString(repo.getCompro_letra()));
		  cell49.setCellStyle(styleAll);
	    }else{
		  cell49.setCellValue(new HSSFRichTextString(""));
		  cell49.setCellStyle(styleAll);
		}
	    
		HSSFCell cell50 = row.createCell(50);
		if(repo.getCompro_sucu()!=null){
		  cell50.setCellValue(repo.getCompro_sucu());
		  cell50.setCellStyle(styleAll);
	    }else{
		  cell50.setCellValue(new HSSFRichTextString(""));
		  cell50.setCellStyle(styleAll);
		}
		
		HSSFCell cell51 = row.createCell(51);
		if(repo.getDebito_para_egreso()!=null){
		  cell51.setCellValue(new HSSFRichTextString(repo.getDebito_para_egreso().toString()));
		  cell51.setCellStyle(styleAll);
	    }else{
		  cell51.setCellValue(new HSSFRichTextString(""));
		  cell51.setCellStyle(styleAll);
		}
		
		HSSFCell cell52 = row.createCell(52);
		if(repo.getSeccional()!=null){
		  cell52.setCellValue(repo.getSeccional());
		  cell52.setCellStyle(styleAll);
	    }else{
		  cell52.setCellValue(new HSSFRichTextString(""));
		  cell52.setCellStyle(styleAll);
		}
		
		HSSFCell cell53 = row.createCell(53);
		if(repo.getCuit_acreedor()!=null){
		  cell53.setCellValue(new HSSFRichTextString(repo.getCuit_acreedor()));
		  cell53.setCellStyle(styleAll);
	    }else{
		  cell53.setCellValue(new HSSFRichTextString(""));
		  cell53.setCellStyle(styleAll);
		}
		
		HSSFCell cell54 = row.createCell(54);
		if(repo.getSucu_acreedor()!=null){
		  cell54.setCellValue(new HSSFRichTextString(repo.getSucu_acreedor()));
		  cell54.setCellStyle(styleAll);
	    }else{
		  cell54.setCellValue(new HSSFRichTextString(""));
		  cell54.setCellStyle(styleAll);
		}
		
		HSSFCell cell55 = row.createCell(55);
		if(repo.getPeriodo_prestacion()!=null){
		  cell55.setCellValue(repo.getPeriodo_prestacion());
		  cell55.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell55.setCellValue(new HSSFRichTextString(""));
		  cell55.setCellStyle(styleAll);
		}
		
		HSSFCell cell56 = row.createCell(56);
		if(repo.getAnulado_fecha()!=null){
		  cell56.setCellValue(repo.getAnulado_fecha());
		  cell56.setCellStyle(styleFechaLeftTop);
	    }else{
		  cell56.setCellValue(new HSSFRichTextString(""));
		  cell56.setCellStyle(styleAll);
		}
		
		HSSFCell cell57 = row.createCell(57);
		if(repo.getAnulado_usr()!=null){
		  cell57.setCellValue(new HSSFRichTextString(repo.getAnulado_usr() ));
		  cell57.setCellStyle(styleAll);
	    }else{
		  cell57.setCellValue(new HSSFRichTextString(""));
		  cell57.setCellStyle(styleAll);
		}
		
		HSSFCell cell58 = row.createCell(58);
		if(repo.getImporte_original()!=null){
		  cell58.setCellValue(repo.getImporte_original().doubleValue());
		  cell58.setCellStyle(styleMoneyRight);
	    }else{
		  cell58.setCellValue(new HSSFRichTextString(""));
		  cell58.setCellStyle(styleAll);
		}
		
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha Proceso"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Código Organismo Transf"));
		cell1.setCellStyle(styleHeader);
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Déb/Cred"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Código Organismo"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Nro.Exp."));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Fecha Proceso"));
		cell5.setCellStyle(styleHeaderL);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Fecha Transf"));
		cell6.setCellStyle(styleHeaderL);
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Clase Exp."));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Importe Total"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(new HSSFRichTextString("Nro.Cuota"));
		cell9.setCellStyle(styleHeader);
		
		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(new HSSFRichTextString("Importe Transf."));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(11);
		cell11.setCellValue(new HSSFRichTextString("Deb/Cred"));
		cell11.setCellStyle(styleHeader);
		
		HSSFCell cell12 = row.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Exp. Original"));
		cell12.setCellStyle(styleHeader);
		
		HSSFCell cell13 = row.createCell(13);
		cell13.setCellValue(new HSSFRichTextString("Cód.Htal"));
		cell13.setCellStyle(styleHeader);
		
		HSSFCell cell14 = row.createCell(14);
		cell14.setCellValue(new HSSFRichTextString("Exp.ANSSAL"));
		cell14.setCellStyle(styleHeader);
		
		HSSFCell cell15 = row.createCell(15);
		cell15.setCellValue(new HSSFRichTextString("Observación"));
		cell15.setCellStyle(styleHeader);
		
		HSSFCell cell16 = row.createCell(16);
		cell16.setCellValue(new HSSFRichTextString("Juzgado"));
		cell16.setCellStyle(styleHeader);
		
		HSSFCell cell17 = row.createCell(17);
		cell17.setCellValue(new HSSFRichTextString("Secretaría"));
		cell17.setCellStyle(styleHeader);
		
		HSSFCell cell18 = row.createCell(18);
		cell18.setCellValue(new HSSFRichTextString("Autos"));
		cell18.setCellStyle(styleHeader);
		
		HSSFCell cell19 = row.createCell(19);
		cell19.setCellValue(new HSSFRichTextString("Nro.Factura"));
		cell19.setCellStyle(styleHeader);
		
		HSSFCell cell20 = row.createCell(20);
		cell20.setCellValue(new HSSFRichTextString("Pto.Vta"));
		cell20.setCellStyle(styleHeader);
		
		HSSFCell cell21 = row.createCell(21);
		cell21.setCellValue(new HSSFRichTextString("Comprob.Tipo"));
		cell21.setCellStyle(styleHeader);
		
		HSSFCell cell22 = row.createCell(22);
		cell22.setCellValue(new HSSFRichTextString("Comprob.Nro"));
		cell22.setCellStyle(styleHeader);
		
		HSSFCell cell23 = row.createCell(23);
		cell23.setCellValue(new HSSFRichTextString("Período"));
		cell23.setCellStyle(styleHeader);
		
		HSSFCell cell24 = row.createCell(24);
		cell24.setCellValue(new HSSFRichTextString("Fecha"));
		cell24.setCellStyle(styleHeader);
		
		HSSFCell cell25 = row.createCell(25);
		cell25.setCellValue(new HSSFRichTextString("Imp.Fecha"));
		cell25.setCellStyle(styleHeader);
		
		HSSFCell cell26 = row.createCell(26);
		cell26.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell26.setCellStyle(styleHeader);
		
		HSSFCell cell27 = row.createCell(27);
		cell27.setCellValue(new HSSFRichTextString("Inte"));
		cell27.setCellStyle(styleHeader);
		
		HSSFCell cell28 = row.createCell(28);
		cell28.setCellValue(new HSSFRichTextString("Vto."));
		cell28.setCellStyle(styleHeader);
		
		HSSFCell cell29 = row.createCell(29);
		cell29.setCellValue(new HSSFRichTextString("Vto 2"));
		cell29.setCellStyle(styleHeader);
		
		HSSFCell cell30 = row.createCell(30);
		cell30.setCellValue(new HSSFRichTextString("Exen"));
		cell30.setCellStyle(styleHeader);
		
		HSSFCell cell31 = row.createCell(31);
		cell31.setCellValue(new HSSFRichTextString("Gravado"));
		cell31.setCellStyle(styleHeader);
		
		HSSFCell cell32 = row.createCell(32);
		cell32.setCellValue(new HSSFRichTextString("IVA"));
		cell32.setCellStyle(styleHeader);
		
		HSSFCell cell33 = row.createCell(33);
		cell33.setCellValue(new HSSFRichTextString("IVA N"));
		cell33.setCellStyle(styleHeader);
		
		HSSFCell cell34 = row.createCell(34);
		cell34.setCellValue(new HSSFRichTextString("Total"));
		cell34.setCellStyle(styleHeader);
		
		HSSFCell cell35 = row.createCell(35);
		cell35.setCellValue(new HSSFRichTextString("Cancel."));
		cell35.setCellStyle(styleHeader);
		
		HSSFCell cell36 = row.createCell(36);
		cell36.setCellValue(new HSSFRichTextString("Motivo Anul."));
		cell36.setCellStyle(styleHeader);
		
		HSSFCell cell37 = row.createCell(37);
		cell37.setCellValue(new HSSFRichTextString("Fecha Anul."));
		cell37.setCellStyle(styleHeader);
		
		HSSFCell cell38 = row.createCell(38);
		cell38.setCellValue(new HSSFRichTextString("Usuario Anul"));
		cell38.setCellStyle(styleHeader);
		
		HSSFCell cell39 = row.createCell(39);
		cell39.setCellValue(new HSSFRichTextString("Observaciones"));
		cell39.setCellStyle(styleHeader);
		
		HSSFCell cell40 = row.createCell(40);
		cell40.setCellValue(new HSSFRichTextString("Fecha Alta"));
		cell40.setCellStyle(styleHeader);
		
		HSSFCell cell41 = row.createCell(41);
		cell41.setCellValue(new HSSFRichTextString("Usuario Alta"));
		cell41.setCellStyle(styleHeader);
		
		HSSFCell cell42 = row.createCell(42);
		cell42.setCellValue(new HSSFRichTextString("Fecha Modif."));
		cell42.setCellStyle(styleHeader);
		
		HSSFCell cell43 = row.createCell(43);
		cell43.setCellValue(new HSSFRichTextString("Usuario Modif"));
		cell43.setCellStyle(styleHeader);
		
		HSSFCell cell44 = row.createCell(44);
		cell44.setCellValue(new HSSFRichTextString("Fecha Baja"));
		cell44.setCellStyle(styleHeader);
		
		HSSFCell cell45 = row.createCell(45);
		cell45.setCellValue(new HSSFRichTextString("Usuario Baja"));
		cell45.setCellStyle(styleHeader);
		
		HSSFCell cell46 = row.createCell(46);
		cell46.setCellValue(new HSSFRichTextString("Fecha Recepción"));
		cell46.setCellStyle(styleHeader);
		
		HSSFCell cell47 = row.createCell(47);
		cell47.setCellValue(new HSSFRichTextString("Fecha Emisión"));
		cell47.setCellStyle(styleHeader);
		
		HSSFCell cell48 = row.createCell(48);
		cell48.setCellValue(new HSSFRichTextString("Cuit"));
		cell48.setCellStyle(styleHeader);
		
		HSSFCell cell49 = row.createCell(49);
		cell49.setCellValue(new HSSFRichTextString("Comp.Letra"));
		cell49.setCellStyle(styleHeader);
		
		HSSFCell cell50 = row.createCell(50);
		cell50.setCellValue(new HSSFRichTextString("Comp.Sucursal"));
		cell50.setCellStyle(styleHeader);
		
		HSSFCell cell51 = row.createCell(51);
		cell51.setCellValue(new HSSFRichTextString("Déb.Egreso"));
		cell51.setCellStyle(styleHeader);
		
		HSSFCell cell52 = row.createCell(52);
		cell52.setCellValue(new HSSFRichTextString("Seccional"));
		cell52.setCellStyle(styleHeader);
		
		HSSFCell cell53 = row.createCell(53);
		cell53.setCellValue(new HSSFRichTextString("Cuil Acreedor"));
		cell53.setCellStyle(styleHeader);
		
		HSSFCell cell54 = row.createCell(54);
		cell54.setCellValue(new HSSFRichTextString("Suc.Acreedor"));
		cell54.setCellStyle(styleHeader);
		
		HSSFCell cell55 = row.createCell(55);
		cell55.setCellValue(new HSSFRichTextString("Período Prest."));
		cell55.setCellStyle(styleHeader);
		
		HSSFCell cell56 = row.createCell(56);
		cell56.setCellValue(new HSSFRichTextString("Fecha Anulación"));
		cell56.setCellStyle(styleHeader);
		
		HSSFCell cell57 = row.createCell(57);
		cell57.setCellValue(new HSSFRichTextString("Usuario Anulación"));
		cell57.setCellStyle(styleHeader);
		
		HSSFCell cell58 = row.createCell(58);
		cell58.setCellValue(new HSSFRichTextString("Importe Original"));
		cell58.setCellStyle(styleHeader);
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, Date fechaIni) {

		String tituloReporte = "Reporte Hospitales Autogestión";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		HSSFRow rowTitulo2 = sheet.createRow(fila);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		return fila;
	}
}
