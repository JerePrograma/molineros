package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteIntegracionErroresExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteIntegracionErroresExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		
		Integer idLote = ParamUtil.getInteger(renderRequest, "id");
		String sError = ParamUtil.getString(renderRequest, "soloerrores");
		   
		List<IntegracionDetalleDS>lista = new ArrayList<IntegracionDetalleDS>();
		String conError="";
		if("SI".equalsIgnoreCase(sError)) {
			lista = IntegracionServiceUtil.detalleDS_Errores_By_IdLote(idLote);
			conError="Errores";
		}else if("NO".equalsIgnoreCase(sError)){		
			lista=IntegracionServiceUtil.detalleDSByIdLote(idLote);		   
		}else if("SSSDET".equalsIgnoreCase(sError)){		
			lista=IntegracionServiceUtil.detalleDSByIdLoteSSS(idLote);	
			conError=" Enviados FTP ";
		}else if("SSSDETERR".equalsIgnoreCase(sError)){		
			List<IntegracionDetalleDS>listaAux = new ArrayList<IntegracionDetalleDS>();
			listaAux=IntegracionServiceUtil.detalleDSByIdLoteSSS(idLote);	
			
			for(IntegracionDetalleDS d:listaAux) {
				if(d.getError()!=null && !"OK".equalsIgnoreCase(d.getError()) && !"".equalsIgnoreCase(d.getError())) {
					lista.add(d);
				}
			}
			conError=" Enviados FTP con Error";
		}
		
		return generaReporte(lista,idLote,conError);
		
	}

	private static HSSFWorkbook generaReporte(
			List<IntegracionDetalleDS> list,Integer idLote,String conError) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Detalle Integracion");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte " +conError+" Lote Integracion: "+idLote.toString() + " ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell015H = rowHeader.createCell(++col);
		cell015H.setCellValue(new HSSFRichTextString("Prestataria"));
		cell015H.setCellStyle(styleBold);
		
		HSSFCell cell016H = rowHeader.createCell(++col);
		cell016H.setCellValue(new HSSFRichTextString("Tipo"));
		cell016H.setCellStyle(styleBold);
		
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Id.Obra Social"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("CUIL"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Certificado"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Vencimiento"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Período"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Prestador"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Tipo Cpbte."));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Tipo Emisión"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Emisión"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell000H = rowHeader.createCell(++col);
		cell000H.setCellValue(new HSSFRichTextString("CAE/CAI"));
		cell000H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader.createCell(++col);
		cell003H.setCellValue(new HSSFRichTextString("Pto.Venta"));
		cell003H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Número"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell002H = rowHeader.createCell(++col);
		cell002H.setCellValue(new HSSFRichTextString("Importe"));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Importe Solicitado"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Prestación"));
		cell14H.setCellStyle(styleBold);
		
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell8_1H = rowHeader.createCell(++col);
		cell8_1H.setCellValue(new HSSFRichTextString("Provincia"));
		cell8_1H.setCellStyle(styleBold);
		
		HSSFCell cell9_1H = rowHeader.createCell(++col);
		cell9_1H.setCellValue(new HSSFRichTextString("Dependencia"));
		cell9_1H.setCellStyle(styleBold);
		
		HSSFCell cell10_1H = rowHeader.createCell(++col);
		cell10_1H.setCellValue(new HSSFRichTextString("Error"));
		cell10_1H.setCellStyle(styleBold); 
		
		index++;
		
		for(IntegracionDetalleDS det: list){
			index=crearDatos(sheet, det, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		sheet.autoSizeColumn((short) 11);
		sheet.autoSizeColumn((short) 12);
		sheet.autoSizeColumn((short) 13);
		sheet.autoSizeColumn((short) 14);
		sheet.autoSizeColumn((short) 15);
		sheet.autoSizeColumn((short) 16);
		sheet.autoSizeColumn((short) 17);
		sheet.autoSizeColumn((short) 18);
		sheet.autoSizeColumn((short) 19);
		sheet.autoSizeColumn((short) 20);
		sheet.autoSizeColumn((short) 21);
		sheet.autoSizeColumn((short) 22);
		sheet.autoSizeColumn((short) 23);
		sheet.autoSizeColumn((short) 24);
		sheet.autoSizeColumn((short) 25);
		sheet.autoSizeColumn((short) 26);
		sheet.autoSizeColumn((short) 27);
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);
		
		return wb;
	}

	private static int crearDatos(HSSFSheet sheet,IntegracionDetalleDS det, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		
		String codError="";
		try {
		  for(int xi=0;xi<WebKeysAutorizaciones.INTEGRACION_ERRORES_UPLOAD.length;xi++){
			if(det.getError().equalsIgnoreCase(WebKeysAutorizaciones.INTEGRACION_ERRORES_UPLOAD[xi][0])){
				codError=WebKeysAutorizaciones.INTEGRACION_ERRORES_UPLOAD[xi][1];
				break;
			}
		  }
		  if("".equalsIgnoreCase(codError) && !"".equalsIgnoreCase(det.getError())) {
		      codError=det.getError();
		  }    
		}catch(Exception e) {codError =det.getError();}
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell100 = rowHeader.createCell(++col);
		cell100.setCellValue(new HSSFRichTextString(det.getEntidad()));
		cell100.setCellStyle(styleAll);

		HSSFCell cell000 = rowHeader.createCell(++col);
		cell000.setCellValue(new HSSFRichTextString(det.getTipoArchivo()));
		cell000.setCellStyle(styleAll);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(det.getIdObraSocial());
		cell001.setCellStyle(styleAll);
		
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(det.getCuil()));
		cell020.setCellStyle(styleAll);
		
		HSSFCell cell022 = rowHeader.createCell(++col);
		cell022.setCellValue(new HSSFRichTextString(det.getAfiliado()));
		cell022.setCellStyle(styleAll);
		
		
		HSSFCell cell021 = rowHeader.createCell(++col);
		cell021.setCellValue(new HSSFRichTextString(det.getCertificadoCodigo()));
		cell021.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		if(det.getCertificadoVencimiento()!=null) {
		   cell002.setCellValue(new HSSFRichTextString(sdf.format(det.getCertificadoVencimiento())));
		}else {
		   cell002.setCellValue(new HSSFRichTextString(""));
		}
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(++col);
		cell004.setCellValue(det.getPeriodoPrestacion());
		cell004.setCellStyle(styleAll);
		
		HSSFCell cell023 = rowHeader.createCell(++col);
		cell023.setCellValue(new HSSFRichTextString(det.getCuitPrestador()));
		cell023.setCellStyle(styleAll);
		
		HSSFCell cell024 = rowHeader.createCell(++col);
		cell024.setCellValue(new HSSFRichTextString(det.getDescripcionPrestador()));
		cell024.setCellStyle(styleAll);

		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(det.getComprobanteTipo());
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		cell017.setCellValue(new HSSFRichTextString(det.getComprobanteTipoEmision()));
		cell017.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(++col);
		if(det.getComprobanteFechaEmision()!=null) {
		   cell005.setCellValue(new HSSFRichTextString(sdf.format(det.getComprobanteFechaEmision())));
		}else {
		   cell005.setCellValue(new HSSFRichTextString(""));
		}
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell019 = rowHeader.createCell(++col);
		cell019.setCellValue(new HSSFRichTextString(det.getComprobanteCAECAI()));
		cell019.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(++col);
		cell006.setCellValue(det.getComprobantePtoVta());
		cell006.setCellStyle(styleAll);

		HSSFCell cell007 = rowHeader.createCell(++col);
		cell007.setCellValue(det.getComprobanteNro());
		cell007.setCellStyle(styleAll);
				
		HSSFCell cell008 = rowHeader.createCell(++col);
		cell008.setCellValue(det.getComprobanteImporte());
		cell008.setCellStyle(styleAll);
		
		HSSFCell cell009 = rowHeader.createCell(++col);
		cell009.setCellValue(det.getImporteSolicitado());
		cell009.setCellStyle(styleAll);
		
		HSSFCell cell010 = rowHeader.createCell(++col);
		cell010.setCellValue(new HSSFRichTextString(det.getPrestacionCodigo()));
		cell010.setCellStyle(styleAll);
		
		HSSFCell cell011 = rowHeader.createCell(++col);
		cell011.setCellValue(det.getPrestacionCantidad());
		cell011.setCellStyle(styleAll);
		
		HSSFCell cell012 = rowHeader.createCell(++col);
		cell012.setCellValue(det.getProvincia());
		cell012.setCellStyle(styleAll);
		
		HSSFCell cell013 = rowHeader.createCell(++col);
		cell013.setCellValue(new HSSFRichTextString(det.getDependencia()));
		cell013.setCellStyle(styleAll);
		
		HSSFCell cell014 = rowHeader.createCell(++col);
		cell014.setCellValue(new HSSFRichTextString(codError));
		cell014.setCellStyle(styleAll);
		
        rowHeader.setHeight((short) 0);
		return index++;
	}
        
}


