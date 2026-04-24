package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.OrdenPago.FormaPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;

public class ReporteIntegracionLiquidacionSuperintendenciaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteIntegracionLiquidacionSuperintendenciaExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		
		/*
		 User ur = null;
		 Seccional seccional=new Seccional();
			try {
				ur = UserLocalServiceUtil.getUserByScreenName(10112, "rpierotti");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			  //128003 -- TableId User_
			  //128004 -- Campo Añadido id_seccional de la tabla user_
			  //10112  -- Company
			  ExpandoValue ep =ExpandoValueLocalServiceUtil.getValue(128003, 128004, ur.getUserId());
			  String idSeccional = ep.getData();
			  if(idSeccional!=null && !"".equalsIgnoreCase(idSeccional) && !"0".equalsIgnoreCase(idSeccional)){
				  try {
					seccional=SeccionalServiceUtil.buscarSeccionalById(Integer.valueOf(idSeccional));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			 
		*/
		
		String periodoStr = ParamUtil.getString (renderRequest,"periodo");
     	String[]periodoV=periodoStr.split("_");
    	Integer periodo =Integer.parseInt(periodoV[1])*100+Integer.parseInt(periodoV[0])+1;
    	
		List<IntegracionDetalleDS>lista = new ArrayList<IntegracionDetalleDS>();
		lista=IntegracionServiceUtil.detalleDSByPeriodo(periodo);
		return generaReporte(lista);
	}

	private static HSSFWorkbook generaReporte(
			List<IntegracionDetalleDS> lista) {
		
		List<IntegracionDetalleDS>list = new ArrayList<IntegracionDetalleDS>();
		for(IntegracionDetalleDS d:lista) {
			if("OK".equalsIgnoreCase(d.getErrorSSS()) && d.getLiquidacion()!=null && d.getLiquidacion()>0) {
				list.add(d);
			}
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Liquidacion SuperIntendencia");

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
				
		StringBuffer titulo1=new StringBuffer("Liquidación Superintendencia " + " ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader00 = sheet.createRow(index);
		
		HSSFCell cell020H = rowHeader00.createCell(0);
		cell020H.setCellValue(new HSSFRichTextString("LIQUIDACION SSS SALUD"));
		cell020H.setCellStyle(styleBold);
		sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 5));
		
		HSSFCell cell021H = rowHeader00.createCell(6);
		cell021H.setCellValue(new HSSFRichTextString("INF.PRESENTADA POR LA OS EN CADA PERIODO"));
		cell021H.setCellStyle(styleBold);
		sheet.addMergedRegion(new CellRangeAddress(index, index, 6, 12));
		
		HSSFCell cell022H = rowHeader00.createCell(13);
		cell022H.setCellValue(new HSSFRichTextString("INFORMACION ADICIONAL"));
		cell022H.setCellStyle(styleBold);
		sheet.addMergedRegion(new CellRangeAddress(index, index, 13, 25));

		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("MES LIQUIDADO"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("MES PRESTAC"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("CUIL BENEF"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("COD PREST"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("PRESTACION"));
		cell13H.setCellStyle(styleBold);
		
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("IMPORTE LIQUIDADO"));
		cell23H.setCellStyle(styleBold);
		
		HSSFCell cell001H = rowHeader.createCell(++col);
		cell001H.setCellValue(new HSSFRichTextString("PERIODO FC"));
		cell001H.setCellStyle(styleBold);
		
		HSSFCell cell002H = rowHeader.createCell(++col);
		cell002H.setCellValue(new HSSFRichTextString("CUIT"));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader.createCell(++col);
		cell003H.setCellValue(new HSSFRichTextString("TIPO"));
		cell003H.setCellStyle(styleBold);
		
		HSSFCell cell004H = rowHeader.createCell(++col);
		cell004H.setCellValue(new HSSFRichTextString("LETRA"));
		cell004H.setCellStyle(styleBold);
		
		HSSFCell cell005H = rowHeader.createCell(++col);
		cell005H.setCellValue(new HSSFRichTextString("P/V"));
		cell005H.setCellStyle(styleBold);
		
		HSSFCell cell006H = rowHeader.createCell(++col);
		cell006H.setCellValue(new HSSFRichTextString("NRO FC"));
		cell006H.setCellStyle(styleBold);
		
		HSSFCell cell007H = rowHeader.createCell(++col);
		cell007H.setCellValue(new HSSFRichTextString("IMPORTE SOLICITADO"));
		cell007H.setCellStyle(styleBold);
		
		HSSFCell cell008H = rowHeader.createCell(++col);
		cell008H.setCellValue(new HSSFRichTextString("LIQ.DE PAGO"));
		cell008H.setCellStyle(styleBold);
		
		HSSFCell cell009H = rowHeader.createCell(++col);
		cell009H.setCellValue(new HSSFRichTextString("FECHA TRANSF."));
		cell009H.setCellStyle(styleBold);
		
		HSSFCell cell010H = rowHeader.createCell(++col);
		cell010H.setCellValue(new HSSFRichTextString("CBU"));
		cell010H.setCellStyle(styleBold);
		
		HSSFCell cell011H = rowHeader.createCell(++col);
		cell011H.setCellValue(new HSSFRichTextString("IMPORTE TRANSF."));
		cell011H.setCellStyle(styleBold);
		
		HSSFCell cell012H = rowHeader.createCell(++col);
		cell012H.setCellValue(new HSSFRichTextString("RETENCION GCIAS"));
		cell012H.setCellStyle(styleBold);
		
		HSSFCell cell013H = rowHeader.createCell(++col);
		cell013H.setCellValue(new HSSFRichTextString("RETENCION INGRESOS BRUTOS"));
		cell013H.setCellStyle(styleBold);
		
		HSSFCell cell014H = rowHeader.createCell(++col);
		cell014H.setCellValue(new HSSFRichTextString("IMPORTE APLICADO SSS"));
		cell014H.setCellStyle(styleBold);
		
		HSSFCell cell015H = rowHeader.createCell(++col);
		cell015H.setCellValue(new HSSFRichTextString("IMPORTE FONDOS PROPIOS"));
		cell015H.setCellStyle(styleBold);
		
		HSSFCell cell016H = rowHeader.createCell(++col);
		cell016H.setCellValue(new HSSFRichTextString("IMPORTE FONDOS OTRA CUENTA"));
		cell016H.setCellStyle(styleBold);
		
		HSSFCell cell017H = rowHeader.createCell(++col);
		cell017H.setCellValue(new HSSFRichTextString("NRO. RECIBO"));
		cell017H.setCellStyle(styleBold);
		
		HSSFCell cell018H = rowHeader.createCell(++col);
		cell018H.setCellValue(new HSSFRichTextString("IMPORTE(REVERSION)"));
		cell018H.setCellStyle(styleBold);
		
		HSSFCell cell019H = rowHeader.createCell(++col);
		cell019H.setCellValue(new HSSFRichTextString("IMPORTE DEVUELTO CTA SSS"));
		cell019H.setCellStyle(styleBold);
   	    index++;
   	    Map<Integer,String>opRet=new HashMap<Integer,String>();
   		for(IntegracionDetalleDS det: list){
			index=crearDatos(sheet, det,opRet, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		
		HSSFRow rowTotales = sheet.createRow(index);
		HSSFCell cell0 = rowTotales.createCell(0);
		Integer rowIni=3;
		
		HSSFCell cell1 = rowTotales.createCell(5);
		cell1.setCellFormula("SUM(F"+Integer.toString(rowIni)  +":F"+ Integer.toString(index-1) +")");
		cell1.setCellStyle(styleMoney);
		
		HSSFCell cell12 = rowTotales.createCell(12);
		cell12.setCellFormula("SUM(M"+Integer.toString(rowIni)  +":M"+ Integer.toString(index-1) +")");
		cell12.setCellStyle(styleMoney);
		
		HSSFCell cell16 = rowTotales.createCell(16);
		cell16.setCellFormula("SUM(Q"+Integer.toString(rowIni)  +":Q"+ Integer.toString(index-1) +")");
		cell16.setCellStyle(styleMoney);
		
		HSSFCell cell17 = rowTotales.createCell(17);
		cell17.setCellFormula("SUM(R"+Integer.toString(rowIni)  +":R"+ Integer.toString(index-1) +")");
		cell17.setCellStyle(styleMoney);
		
		HSSFCell cell19 = rowTotales.createCell(19);
		cell19.setCellFormula("SUM(T"+Integer.toString(rowIni)  +":T"+ Integer.toString(index-1) +")");
		cell19.setCellStyle(styleMoney);
		
		HSSFCell cell20 = rowTotales.createCell(20);
		cell20.setCellFormula("SUM(U"+Integer.toString(rowIni)  +":U"+ Integer.toString(index-1) +")");
		cell20.setCellStyle(styleMoney);
		
		
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

	private static int crearDatos(HSSFSheet sheet,IntegracionDetalleDS det, Map<Integer,String>opRet,
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		styleAll.setWrapText(true);
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(det.getPeriodo());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(det.getPeriodoPrestacion());
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(new HSSFRichTextString(det.getCuil()));
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(++col);
		cell004.setCellValue(new HSSFRichTextString(det.getPrestacionCodigo()));
		cell004.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(++col);
		cell005.setCellValue(new HSSFRichTextString(det.getPrestacionDescripcion() ));
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(++col);
		if(det.getImporteSubsidiado()!=null) {
		  cell006.setCellValue(det.getImporteSubsidiado()/100 );
		} else {
		  cell006.setCellValue(new HSSFRichTextString(""));
		}
		cell006.setCellStyle(styleMoney);		
		
		HSSFCell cell007 = rowHeader.createCell(++col);
		cell007.setCellValue(det.getPeriodoPrestacion());
		cell007.setCellStyle(styleAll);
		
		HSSFCell cell008 = rowHeader.createCell(++col);
		cell008.setCellValue(new HSSFRichTextString(det.getCuitPrestador()));
		cell008.setCellStyle(styleAll);
		
		String cTipo ="";
		String cLetra="";
		
		cTipo= WebKeysAutorizaciones.COMPROBANTES_INTEGRACION[det.getComprobanteTipo()][0];
		cLetra= WebKeysAutorizaciones.COMPROBANTES_INTEGRACION[det.getComprobanteTipo()][1];
		
		HSSFCell cell009 = rowHeader.createCell(++col);
		cell009.setCellValue(new HSSFRichTextString(cTipo));
		cell009.setCellStyle(styleAll);
		
		HSSFCell cell010 = rowHeader.createCell(++col);
		cell010.setCellValue(new HSSFRichTextString(cLetra));
		cell010.setCellStyle(styleAll);
		
		HSSFCell cell011 = rowHeader.createCell(++col);
		cell011.setCellValue(det.getComprobantePtoVta());
		cell011.setCellStyle(styleAll);
		
		HSSFCell cell012 = rowHeader.createCell(++col);
		cell012.setCellValue(det.getComprobanteNro());
		cell012.setCellStyle(styleAll);
		
		HSSFCell cell013 = rowHeader.createCell(++col);
		if(det.getImporteSolicitado()!=null) {
		  cell013.setCellValue(det.getImporteSolicitadoSSS()/100 );
		} else {
		  cell013.setCellValue(new HSSFRichTextString(""));
		}
		cell013.setCellStyle(styleMoney);
		
		HSSFCell cell014 = rowHeader.createCell(++col);
		if(det.getOrdenPago() !=null) {
		  cell014.setCellValue(det.getOrdenPago() );
		} else {
		  cell014.setCellValue(new HSSFRichTextString(""));
		}
		cell014.setCellStyle(styleAll);
		
		HSSFCell cell015 = rowHeader.createCell(++col);
		if(det.getFechaAvisoTransferencia() !=null) {
			cell015.setCellValue(new HSSFRichTextString(sdf.format(det.getFechaAvisoTransferencia())));
		} else {
		  cell015.setCellValue(new HSSFRichTextString(""));
		}
		cell015.setCellStyle(styleAll);
		
		
		HSSFCell cell016 = rowHeader.createCell(++col);
		if(det.getCbu()!=null) {
		   cell016.setCellValue(new HSSFRichTextString(det.getCbu()));
		}else {
		   cell016.setCellValue(new HSSFRichTextString(""));
		}
		cell016.setCellStyle(styleAll);
		
		
		String existeRet=opRet.get(det.getOrdenPago());
		Double importeRetencion =0D;
		if(existeRet==null || existeRet.isEmpty() ) {
		   try {
			  String conRet="NO"; 
			  OrdenPagoOspim op = OrdenPagoServiceUtil.getOrdenPagoOspim(det.getOrdenPago());
			  for(FormaPago f:op.getFormaPago()) {
				  if(f.getPago().getTipo().equals(RetencionGanancias.class.getSimpleName())) {
					 importeRetencion=f.getImporte().doubleValue();
					 conRet="SI";
					 break;
				  }
			  }
			  opRet.put(op.getId(), conRet);
		   } catch (Exception e) {
			
		   }
		}
		
		
		
		HSSFCell cell017 = rowHeader.createCell(++col);
		Double importeLiquidado=0D;
		if(det.getOrdenPago() !=null) {
		  try {
			Liquidacion liquidacion=  EditarLiquidacionServiceUtil.getLiquidacionEntry(det.getLiquidacion());
			importeLiquidado=liquidacion.getImporte().doubleValue() - importeRetencion;
			cell017.setCellValue(liquidacion.getImporte().doubleValue());
		  } catch (Exception e) {
			  cell017.setCellValue(new HSSFRichTextString(""));  
		  }
		  
		}else {
		  cell017.setCellValue(new HSSFRichTextString(""));
		}
		cell017.setCellStyle(styleMoney);
		
		
		HSSFCell cell018 = rowHeader.createCell(++col);
		if(importeRetencion>0D) {
		   cell018.setCellValue(importeRetencion);
		   cell018.setCellStyle(styleMoney);		
		}
		
		HSSFCell cell019 = rowHeader.createCell(++col);
	
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(det.getImporteSubsidiado()/100);
		
//		cell020.setCellValue(importeLiquidado+importeRetencion);
		
		cell020.setCellStyle(styleMoney);	
		
		HSSFCell cell021 = rowHeader.createCell(++col);
		
		if((importeLiquidado+importeRetencion) -  (det.getImporteSubsidiado()==null?0D:det.getImporteSubsidiado()/100)>0) {
		  cell021.setCellValue((importeLiquidado+importeRetencion) -  (det.getImporteSubsidiado()==null?0D:det.getImporteSubsidiado()/100) );
		  cell021.setCellStyle(styleMoney);	
		}
		
		HSSFCell cell022 = rowHeader.createCell(++col);
		
		HSSFCell cell023 = rowHeader.createCell(++col);
		if(det.getNroRecibo() !=null) {
		   cell023.setCellValue(new HSSFRichTextString(det.getNroRecibo()));  
		}else {
		  cell023.setCellValue(new HSSFRichTextString(""));
		}
		
		rowHeader.setHeight((short) 0);
		
		
		return index++;
	}
        
}


