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

import ar.com.ospim.autorizaciones.beans.BusquedaSeguimientoSurFiltro;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;

public class ReporteSeguimientoSurExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSeguimientoSurExcel.class);

	public static HSSFWorkbook generaReporteSeguimientoSur(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		BusquedaSeguimientoSurFiltro filtro =  (BusquedaSeguimientoSurFiltro) renderRequest.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACIONES_FILTRO);
		
//		List<SeguimientoSur> seguimientos=(List<SeguimientoSur>)renderRequest.getSession().getAttribute("SeguimientosSUR");
		
		List<SeguimientoSur> seguimientos = new ArrayList<SeguimientoSur>();
		try {
			seguimientos = SeguimientoSurServiceUtil.getListaSeguimientoSurXls(filtro);
		} catch (SystemException e) {
			_log.error(e);
		}
				
		return generaReporteSeguimientoSur(seguimientos);
	}

	private static HSSFWorkbook generaReporteSeguimientoSur(
			List<SeguimientoSur> list) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Expedientes");

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
				
		StringBuffer titulo1=new StringBuffer("Reporte Expedientes SUR: ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
//		HSSFCell cell160H = rowHeader.createCell(++col);
//		cell160H.setCellValue(new HSSFRichTextString("Alta Fecha Exp.Sur."));
//		cell160H.setCellStyle(styleBold);
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Ingreso Área SUR"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("FC Tercerizadora"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Beneficiario"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("CUIL"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Plan"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Codigo HIV"));
		cell17H.setCellStyle(styleBold);
		
//		HSSFCell cell000H = rowHeader.createCell(++col);
//		cell000H.setCellValue(new HSSFRichTextString("Tipo de Expediente"));
//		cell000H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader.createCell(++col);
		cell003H.setCellValue(new HSSFRichTextString("Patología"));
		cell003H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Periodicidad"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Año"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell002H = rowHeader.createCell(++col);
		cell002H.setCellValue(new HSSFRichTextString("Nro de Solicitud"));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Expediente"));
		cell2H.setCellStyle(styleBold);

//		HSSFCell cell14_H = rowHeader.createCell(++col);
//		cell14_H.setCellValue(new HSSFRichTextString("Alta Fecha Estado"));
//		cell14_H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Estado"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Observación"));
		cell29H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Importe Presentado"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell8_1H = rowHeader.createCell(++col);
		cell8_1H.setCellValue(new HSSFRichTextString("Importe Reconocido"));
		cell8_1H.setCellStyle(styleBold);
		
//----------------------
//----------------------
		
/*		
		
		HSSFCell cell001H = rowHeader.createCell(++col);
		cell001H.setCellValue(new HSSFRichTextString("Usuario de Carga"));
		cell001H.setCellStyle(styleBold);
*/		
		
//		HSSFCell cell9H = rowHeader.createCell(++col);
//		cell9H.setCellValue(new HSSFRichTextString("Ingreso Bancario"));
//		cell9H.setCellStyle(styleBold);
//		
//		HSSFCell cell10H = rowHeader.createCell(++col);
//		cell10H.setCellValue(new HSSFRichTextString("%"));
//		cell10H.setCellStyle(styleBold);
//		
//		HSSFCell cell0H = rowHeader.createCell(++col);
//		cell0H.setCellValue(new HSSFRichTextString("Fecha Movimiento Bancario"));
//		cell0H.setCellStyle(styleBold);
//		
//		HSSFCell cell004H = rowHeader.createCell(++col);
//		cell004H.setCellValue(new HSSFRichTextString("Período"));
//		cell004H.setCellStyle(styleBold);
//		
//		HSSFCell cell005H = rowHeader.createCell(++col);
//		cell005H.setCellValue(new HSSFRichTextString("Banco"));
//		cell005H.setCellStyle(styleBold);
//		
//		HSSFCell cell1H = rowHeader.createCell(++col);
//		cell1H.setCellValue(new HSSFRichTextString("Comprobante Movimiento"));
//		cell1H.setCellStyle(styleBold);
//		
//		HSSFCell cell7H = rowHeader.createCell(++col);
//		cell7H.setCellValue(new HSSFRichTextString("Tope Recupero"));
//		cell7H.setCellStyle(styleBold);
//		
//		HSSFCell cell15H = rowHeader.createCell(++col);
//		cell15H.setCellValue(new HSSFRichTextString("Ingreso Edificio"));
//		cell15H.setCellStyle(styleBold);
		
	/*	HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Importe Proporcional"));
		cell18H.setCellStyle(styleBold);

		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Fecha Pago Prop."));
		cell19H.setCellStyle(styleBold);*/
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Importe Ospim "));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Monto Ensalud "));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Monto Omint "));
		cell20H.setCellStyle(styleBold);
		
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Monto Prevencion  "));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell211H = rowHeader.createCell(++col);
		cell211H.setCellValue(new HSSFRichTextString("Monto Cemic  "));
		cell211H.setCellStyle(styleBold);
		
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Prestación"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("DDJJ"));
		cell23H.setCellStyle(styleBold);
		
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Id Seguimiento"));
		cell24H.setCellStyle(styleBold);
		
		index++;
		
		MovimientoBancario movimiento = new MovimientoBancario();
		
		for(SeguimientoSur seguimiento: list){
//			MovimientoBancario movimiento = new MovimientoBancario();
//			if(seguimiento.getNro_expediente()!=null && !"".equalsIgnoreCase(seguimiento.getNro_expediente())){
//				try {
//					movimiento = SeguimientoSurServiceUtil.traeMovimientoBancoSeguimientoSur(seguimiento.getNro_expediente());
//				} catch (SystemException e) {}
//			}
			
			index=crearDatosSeguimiento(sheet, seguimiento,movimiento, index, styleAll,
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

	private static int crearDatosSeguimiento(HSSFSheet sheet,SeguimientoSur seguimiento, MovimientoBancario movimiento,
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
//		HSSFCell cell037_1 = rowHeader.createCell(++col);
//		cell037_1.setCellValue(new HSSFRichTextString(
//				seguimiento.getAlta_fecha()!=null?seguimiento.getAlta_Fecha_string():"" ));
//		cell037_1.setCellStyle(styleAll);
		
		HSSFCell cell035 = rowHeader.createCell(++col);
		cell035.setCellValue(new HSSFRichTextString(
				seguimiento.getFecha_ingreso_area_sur()!=null?new SimpleDateFormat("dd/MM/yyyy").format(seguimiento.getFecha_ingreso_area_sur()):"" ));
		cell035.setCellStyle(styleAll);
		
		HSSFCell cell32 = rowHeader.createCell(++col);
		String tercerizadora ="";
		if(seguimiento.getId_tipo_expediente_tercerizadora()!=0 ){
			tercerizadora =WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[seguimiento.getId_tipo_expediente_tercerizadora()-1][1];
		}else{
			tercerizadora =WebKeysAutorizaciones.TIPOS_EXPEDIENTES[seguimiento.getId_tipo_expediente()-1][1];
		}
		cell32.setCellValue(new HSSFRichTextString(tercerizadora));
		cell32.setCellStyle(styleAll);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(""));
//		if("OMINT".equalsIgnoreCase(tercerizadora) ){
//            String cpbte="";
//            try {
//            	List<SeguimientoSurComprobante>comprobantes =SeguimientoSurServiceUtil.buscarComprobantesLiquidadosSeguimientoSurPorId(seguimiento.getId(), null);
//				for(SeguimientoSurComprobante sc:comprobantes){
//	            	cpbte +=  sc.getTipoComprobante() + " "+ sc.getLetraComprobante() + " " +
//	                         sc.getPtoVenta()+ " "+sc.getNroComprobante() +";";
//	            	
//	            	//sc.getCuit() + " " +
//	            }
//				
//			} catch (SystemException e) {}
//            
//			cell6.setCellValue(new HSSFRichTextString(cpbte));
//	    }
		cell6.setCellValue(new HSSFRichTextString(seguimiento.getComprobanteNumero()));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(seguimiento.getAfiliadoNombre()));
		cell3.setCellStyle(styleAll);
		
//		Afiliado afi=new Afiliado();
//		try {
//			 afi=EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(seguimiento.getCuilTitular(), seguimiento.getIntegrante());
//		} catch (Exception e1) {}
		
		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(seguimiento.getCuil()));
//		cell4.setCellValue(new HSSFRichTextString(seguimiento.getCuilTitular() + "/" + seguimiento.getIntegrante()));
		cell4.setCellStyle(styleAll);
		
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(seguimiento.getAfiliadoPlan()));
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(new HSSFRichTextString(seguimiento.getCodigoHIV()));
		cell17.setCellStyle(styleAll);
		
//		HSSFCell cell000 = rowHeader.createCell(++col);
//		cell000.setCellValue(new HSSFRichTextString(seguimiento.getClaseExpediente()));
//		cell000.setCellStyle(styleDate);
		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(new HSSFRichTextString(seguimiento.getPatologiaDescripcion() ));
		cell003.setCellStyle(styleDate);
		
		HSSFCell cell31 = rowHeader.createCell(++col);
		String periodicidad ="";
		if(seguimiento.getId_bimestre()!=0 &&  (!"HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) && !"HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ) ){
		  periodicidad=TraeListasServiceUtil.getBimestresPorId(seguimiento.getId_bimestre());	
		  String[] vPeriodo= periodicidad.split("\\|");
		  periodicidad=vPeriodo[1];
		}else{
			periodicidad=seguimiento.getPeriodicidadHemofilia();
		}
		cell31.setCellValue(new HSSFRichTextString( periodicidad ));
		cell31.setCellStyle(styleAll);
		
		HSSFCell cell30 = rowHeader.createCell(++col);
		cell30.setCellValue(seguimiento.getAnio());
		cell30.setCellStyle(styleNumber);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(seguimiento.getNro_solicitud_sur() ));
		cell002.setCellStyle(styleDate);
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(seguimiento.getNro_expediente()));
		cell2.setCellStyle(styleNumber);
		
//		HSSFCell cell037_ = rowHeader.createCell(++col);
//		cell037_.setCellValue(new HSSFRichTextString(
//				seguimiento.getUltimoEstadoAltaFecha()!=null?new SimpleDateFormat("dd/MM/yyyy").format(seguimiento.getUltimoEstadoAltaFecha()):"" ));
//		cell037_.setCellStyle(styleAll);
		
		HSSFCell cell33 = rowHeader.createCell(++col);
		cell33.setCellValue(new HSSFRichTextString(seguimiento.getUltimoEstadoDescripcion()));
		cell33.setCellStyle(styleAll);
		
		HSSFCell cell39 = rowHeader.createCell(++col);
		cell39.setCellValue(new HSSFRichTextString(seguimiento.getObservacionUltimoEstado()  ));
		cell39.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(seguimiento.getImportePresentado());
		cell8.setCellStyle(styleMoney);
		
		HSSFCell cell8_1 = rowHeader.createCell(++col);
		cell8_1.setCellValue(seguimiento.getImporteReconocido());
		cell8_1.setCellStyle(styleMoney);
		
//------------------
//------------------		
			
/*		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(seguimiento.getAlta_usr()));
		cell001.setCellStyle(styleDate);
*/		
		
//		HSSFCell cell9 = rowHeader.createCell(++col);
//		if(movimiento.getImporte()==null){
//			cell9.setCellValue(new HSSFRichTextString(""));
//		}else{
//		    cell9.setCellValue(movimiento.getImporte().doubleValue());
//		}
//		cell9.setCellStyle(styleMoney);
//		
//		
//		HSSFCell cell10 = rowHeader.createCell(++col);
//		if( movimiento.getImporte()!= null && seguimiento.getImportePresentado()!=null &&  seguimiento.getImportePresentado()!=0){
//		   cell10.setCellValue(movimiento.getImporte().doubleValue() *100D/ seguimiento.getImportePresentado());
//		}else{
//			cell10.setCellValue(new HSSFRichTextString(""));	
//		}
//		cell10.setCellStyle(styleMoney);
//		
//		HSSFCell cell0 = rowHeader.createCell(++col);
//		cell0.setCellValue(new HSSFRichTextString(movimiento.getFecha_movimientoAsString()));
//		cell0.setCellStyle(styleDate);
//		
//		
//		HSSFCell cell004 = rowHeader.createCell(++col);
//		cell004.setCellValue(new HSSFRichTextString(
//				movimiento.getFecha_movimiento()!=null?new SimpleDateFormat("MM-yyyy").format(movimiento.getFecha_movimiento()):"" ));
//		cell004.setCellStyle(styleAll);
//		
//		HSSFCell cell005 = rowHeader.createCell(++col);
//		if(movimiento.getCta_bcria()!=null && movimiento.getCta_bcria().getBanco()!=null ){
//		
//		   cell005.setCellValue(new HSSFRichTextString(movimiento.getCta_bcria().getBanco().getDescripcion_banco()!=null?movimiento.getCta_bcria().getBanco().getDescripcion_banco():""));
//		} else{
//			 cell005.setCellValue(new HSSFRichTextString(""));	
//		}
//		cell005.setCellStyle(styleAll);
//		
//		HSSFCell cell1 = rowHeader.createCell(++col);
//		cell1.setCellValue(new HSSFRichTextString( movimiento.getNro_comprobante()!=null?movimiento.getNro_comprobante():""));
//		cell1.setCellStyle(styleNumber);
//		
//		HSSFCell cell7 = rowHeader.createCell(++col);
//		if(seguimiento.getTopeRecupero()!=null && seguimiento.getTopeRecupero()!=0){
//			   cell7.setCellValue(seguimiento.getTopeRecupero());
//		}else{
//			 cell7.setCellValue(new HSSFRichTextString(""));
//		}
//		cell7.setCellStyle(styleMoney);
		
//		if(!"".equalsIgnoreCase(seguimiento.getNro_correspondencia_sur())){
//		  try {
//			CabeceraCorrespondencia corr = CorrespondenciaServiceImpl.buscarCabeceraCorrespondenciaPorId(Integer.parseInt(seguimiento.getNro_correspondencia_sur()));
//			HSSFCell cell34 = rowHeader.createCell(++col);
//			cell34.setCellValue(new HSSFRichTextString(corr.getFechaAsString()));
//			cell34.setCellStyle(styleDate);
//		  } catch (Exception e) {}  
//		}else{
//			col++;
//		}
		
		HSSFCell cell36 = rowHeader.createCell(++col);
		if(seguimiento.getImporteOspim()!=null &&  seguimiento.getImporteOspim()!=0){
		   cell36.setCellValue(seguimiento.getImporteOspim());
		}else{
			cell36.setCellValue(new HSSFRichTextString(""));	
		}
		cell36.setCellStyle(styleMoney);
		
		HSSFCell cell037 = rowHeader.createCell(++col);
		if(seguimiento.getImporteEnSalud()!=null &&  seguimiento.getImporteEnSalud()!=0){
			cell037.setCellValue(seguimiento.getImporteEnSalud());
		}else{
			cell037.setCellValue(new HSSFRichTextString(""));
		}
		cell037.setCellStyle(styleMoney);

		
		HSSFCell cell038 = rowHeader.createCell(++col);
		if(seguimiento.getImporteOmint()!=null &&  seguimiento.getImporteOmint()!=0){
			cell038.setCellValue(seguimiento.getImporteOmint());
		}else{
			cell038.setCellValue(new HSSFRichTextString(""));
		}
		cell038.setCellStyle(styleMoney);
		
		
		HSSFCell cell039 = rowHeader.createCell(++col);
		if(seguimiento.getImportePrevencion()!=null &&  seguimiento.getImportePrevencion()!=0){
			cell039.setCellValue(seguimiento.getImportePrevencion());
		}else{
			cell039.setCellValue(new HSSFRichTextString(""));
		}
		cell039.setCellStyle(styleMoney);
		
		HSSFCell cell0390 = rowHeader.createCell(++col);
		if(seguimiento.getImporteCemic()!=null &&  seguimiento.getImporteCemic()!=0){
			cell0390.setCellValue(seguimiento.getImporteCemic());
		}else{
			cell0390.setCellValue(new HSSFRichTextString(""));
		}
		cell0390.setCellStyle(styleMoney);
		
		HSSFCell cell040 = rowHeader.createCell(++col);
		cell040.setCellValue(new HSSFRichTextString(seguimiento.getCodigoPresentado() + " - " +seguimiento.getDescripcionPresentado()));
		cell040.setCellStyle(styleAll);
		
		HSSFCell cell041 = rowHeader.createCell(++col);
		cell041.setCellValue(new HSSFRichTextString(
				seguimiento.getDdjj()!=null?new SimpleDateFormat("MM/yyyy").format(seguimiento.getDdjj()):"" ));
		cell041.setCellStyle(styleAll);
		
		HSSFCell cell042 = rowHeader.createCell(++col);
		cell042.setCellValue(new HSSFRichTextString(String.valueOf(seguimiento.getId())));
		cell042.setCellStyle(styleDate);
	
		
		return index++;
	}
	
	
//////////////////////////////////
//////////////////////////////////
//////////////////////////////////

    public static HSSFWorkbook generaReporteSeguimientoSurDetalleLote(
       HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
    	
       int nroLote = ParamUtil.getInteger(renderRequest, "nrolote");
       String tipo =ParamUtil.getString(renderRequest, "tipo");
       
       List<SeguimientoSur> seguimientos=SeguimientoSurServiceUtil.lotesProcesadosAdelantosDetalle(nroLote, tipo);

       return generaReporteSeguimientoSurLotePagoDetalle(seguimientos,nroLote,tipo);
    }
    
    
    
    private static HSSFWorkbook generaReporteSeguimientoSurLotePagoDetalle(
			List<SeguimientoSur> list,Integer nroLote,String tipo) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Expedientes");

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
				
		StringBuffer titulo1=new StringBuffer("Reporte Expedientes SUR Lote : ").append(nroLote);
		if("IMP".equalsIgnoreCase(tipo)){
		   titulo1 .append(" - Imputados ");	
		}
	
		if("NOE".equalsIgnoreCase(tipo)){
			   titulo1 .append(" - Existentes en lote y no encontrados en Sistema ");	
		}
		
		if("VEN".equalsIgnoreCase(tipo)){
			   titulo1 .append(" - Existentes en Sistema y no encontrados en lote ");	
		}
		
		if("EXI".equalsIgnoreCase(tipo)){
			   titulo1 .append(" - Existentes en Sistema y en lote ");	
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Nro.Solicitud"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Nro.Expediente"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Fecha"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Cuil"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Importe"));
		cell17H.setCellStyle(styleBold);
		
		
        index++;
		
		for(SeguimientoSur seguimiento: list){
			index=crearDatosSeguimientoLoteDetalle(sheet, seguimiento,index, styleAll,
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


    private static int crearDatosSeguimientoLoteDetalle(HSSFSheet sheet,SeguimientoSur seguimiento,
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(seguimiento.getNro_solicitud_sur() ));
		cell002.setCellStyle(styleDate);
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(seguimiento.getNro_expediente()));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell035 = rowHeader.createCell(++col);
		cell035.setCellValue(new HSSFRichTextString(
				seguimiento.getBaja_fecha()!=null?new SimpleDateFormat("dd/MM/yyyy").format(seguimiento.getBaja_fecha()):"" ));
		cell035.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(seguimiento.getCuilTitular() ));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(seguimiento.getAfiliadoNombre()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(seguimiento.getProporcionalAdelantado());
		cell8.setCellStyle(styleMoney);
				
		return index++;
	}
    
}


