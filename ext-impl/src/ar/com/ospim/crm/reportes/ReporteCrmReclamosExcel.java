package ar.com.ospim.crm.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import  org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.crm.beans.BusquedaDocumLegalFiltro;
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.TipoReclamo;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteCrmReclamosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteCrmReclamosExcel.class);

	private static List<TipoReclamo> tiposReclamo;
	private static List<MotivoContacto> tiposMotivos;
	
	public static HSSFWorkbook generaReporteSeguimientoReclamosCRM(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			tiposReclamo = CrmServiceUtil.buscarTiposReclamo();
			tiposMotivos = CrmServiceUtil.buscarMotivosContacto();
		
			int motivo = ParamUtil.getInteger(req, "motivo",0);
			int tipoReclamo = ParamUtil.getInteger(req, "tipoReclamo",0);
			String cuilTitular = ParamUtil.getString(req, "cuil_titular",null);
			String inte = ParamUtil.getString(req, "inte",null);
			int incluirA = ParamUtil.getInteger(req, "incluirA", 0);
			int idDocLegal = ParamUtil.getInteger(req, "nro_doc_legal",0);
			int idPlan = ParamUtil.getInteger(req, "plan",0);
			int idPlanOmint = ParamUtil.getInteger(req, "planOmint",0);
			boolean tieneAntec = ParamUtil.getBoolean(req, "antecedente");
			boolean concluido = ParamUtil.getBoolean(req, "concluido");
			boolean noconcluido = ParamUtil.getBoolean(req, "noconcluido");
			
			String fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
			
			BusquedaDocumLegalFiltro filtro = new BusquedaDocumLegalFiltro();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}		
			Date fechaHasta = null;
			try {
				fechaHasta = sdf.parse(fechaHastaFinal);
			} catch (Exception e) {
				fechaHasta = null;
			}			
		
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setCuil_titular(cuilTitular);
			filtro.setInte(inte);
			filtro.setIncluirA(incluirA);
			filtro.setMotivo(motivo);
			filtro.setTipoReclamo(tipoReclamo);
			filtro.setIdDocumLegal(idDocLegal);
			filtro.setIdPlan(idPlan);
			filtro.setIdPlanOmint(idPlanOmint);
			filtro.setAntecedente(tieneAntec);
			filtro.setConcluido(concluido);
			filtro.setNoConcluido(noconcluido);

			List<DocumentoLegalCRM> busqueda = CrmServiceUtil.busquedaReclamosCRMxls(filtro);

			return generarReporte(filtro, busqueda);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte reclamos crm", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(BusquedaDocumLegalFiltro filtro, List<DocumentoLegalCRM> reclamos) {
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleAllWithBorderWrapped = getStyleAllWithBorder(wb, 10);
		styleAllWithBorderWrapped.setWrapText(true);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, filtro);
			index++;
			for (DocumentoLegalCRM dlcrm : reclamos) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getId())));
				cell0.setCellStyle(styleAllWithBorder);
				
				
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getAltaFecha())));
				cell1.setCellStyle(styleAllWithBorder);
				//cell1.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				cell1.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(dlcrm.getTipo().getDescripcion()));
				cell2.setCellStyle(styleAllWithBorder);
				cell2.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getFechaNotificacion())));
				cell3.setCellStyle(styleAllWithBorder);
				cell3.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(dlcrm.getMotivo().getDescripcion()));
				cell4.setCellStyle(styleAllWithBorder);
				cell4.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(dlcrm.getAltaUsr()));
				cell5.setCellStyle(styleAllWithBorder);
				cell5.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell6 = row.createCell(column++);
				if(dlcrm.getAfiliado() != null){
					cell6.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getAfiliado().getApeNombre().trim())));
				}else{
					cell6.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getNoAfiliado().getApellido().trim() + ", "+
							dlcrm.getNoAfiliado().getNombre().trim() )));
				}
				cell6.setCellStyle(styleAllWithBorderWrapped);
				cell6.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell7 = row.createCell(column++);
				if(dlcrm.getAfiliado() != null){
					cell7.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getAfiliado().getCuil_titular() 
							+ " / " + dlcrm.getAfiliado().getInte())));
				}else{
					cell7.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getNoAfiliado().getDocumentoTipo() 
							+ " "+ dlcrm.getNoAfiliado().getDocumentoNumero())));
				}
				cell7.setCellStyle(styleAllWithBorderWrapped);
				cell7.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell8 = row.createCell(column++);
				if(dlcrm.getAfiliado() != null ){
					cell8.setCellValue(new HSSFRichTextString("SI"));
				}else{
					cell8.setCellValue(new HSSFRichTextString("NO"));
				}
				cell8.setCellStyle(styleAllWithBorderWrapped);
				cell8.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell9 = row.createCell(column++);
				if(dlcrm.getAfiliado() != null && dlcrm.getAfiliado().getUltimo_plan() != null){
					cell9.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getAfiliado().getUltimo_plan().getDescripcion() )));
				}else{
					cell9.setCellValue(new HSSFRichTextString(String.valueOf("-")));
				}
				cell9.setCellStyle(styleAllWithBorderWrapped);
				cell9.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell10 = row.createCell(column++);
				if(dlcrm.getAfiliado() != null && dlcrm.getAfiliado().getUltimo_plan() != null && dlcrm.getAfiliado().getUltimo_plan().getDescripcionOmint() != null){
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getAfiliado().getUltimo_plan().getDescripcionOmint())));
				}else{
					cell10.setCellValue(new HSSFRichTextString(String.valueOf("-")));
				}
				cell10.setCellStyle(styleAllWithBorderWrapped);
				cell10.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getDescripcion().trim())));
				cell11.setCellStyle(styleAllWithBorderWrapped);
				cell11.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				sheet.setColumnWidth(11, 6000); 
				
				HSSFCell cell12 = row.createCell(column++);
				if(dlcrm.getFechaVencimiento() != null){
					cell12.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getFechaVencimiento())));
				}else{
					cell12.setCellValue(new HSSFRichTextString(""));
				}
				cell12.setCellStyle(styleAllWithBorder);
				cell12.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell13 = row.createCell(column++);
				if(dlcrm.getFechaRespuesta() != null){
					cell13.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getFechaRespuesta())));
				}else{
					cell13.setCellValue(new HSSFRichTextString(""));
				}
				cell13.setCellStyle(styleAllWithBorder);
				cell13.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell14 = row.createCell(column++);
				if(dlcrm.getFechaAvisoAlEstudio() != null){
					cell14.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getFechaAvisoAlEstudio())));
				}else{
					cell14.setCellValue(new HSSFRichTextString(""));
				}
				cell14.setCellStyle(styleAllWithBorder);
				cell14.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell15 = row.createCell(column++);
				if(dlcrm.getFechaContactoPSOM() != null){
					cell15.setCellValue(new HSSFRichTextString(sdf2.format(dlcrm.getFechaContactoPSOM())));
				}else{
					cell15.setCellValue(new HSSFRichTextString(""));
				}
				cell15.setCellStyle(styleAllWithBorder);
				cell15.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getExpediente())));
				cell16.setCellStyle(styleAllWithBorderWrapped);
				cell16.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getResolucion())));
				cell17.setCellStyle(styleAllWithBorderWrapped);
				cell17.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);				
				
				HSSFCell cell18 = row.createCell(column++);
				if(dlcrm.getTramiteNumero() != null && dlcrm.getTramiteNumero() > 0){
					cell18.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getTramiteNumero())));
				}else{
					cell18.setCellValue(new HSSFRichTextString(""));
				}
				cell18.setCellStyle(styleAllWithBorder);
				cell18.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell19 = row.createCell(column++);
				if(dlcrm.getRadicacion() != null){
					cell19.setCellValue(new HSSFRichTextString(dlcrm.getRadicacion()));
				}else{
					cell19.setCellValue(new HSSFRichTextString(""));
				}
				cell19.setCellStyle(styleAllWithBorder);
				cell19.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell20 = row.createCell(column++);
				if(dlcrm.getImporteReclamado() != null && !dlcrm.getImporteReclamado().equals(new BigDecimal(0))){
					cell20.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getImporteReclamado())));
				}else{
					cell20.setCellValue(new HSSFRichTextString(""));
				}
				cell20.setCellStyle(styleAllWithBorder);
				cell20.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
						
				HSSFCell cell21 = row.createCell(column++);
				if(dlcrm.getDescripcionSolucion() != null){
					cell21.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getDescripcionSolucion().trim())));
				}else{
					cell21.setCellValue(new HSSFRichTextString(""));
				}
				cell21.setCellStyle(styleAllWithBorderWrapped);
				cell21.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				sheet.setColumnWidth(21, 6000); 

				HSSFCell cell22 = row.createCell(column++);
				if(dlcrm.getDescripcionSolucion() != null){
					cell22.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getDescripcionEstudio().trim())));
				}else{
					cell22.setCellValue(new HSSFRichTextString(""));
				}
				cell22.setCellStyle(styleAllWithBorderWrapped);
				cell22.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				sheet.setColumnWidth(22, 6000); 
				
				HSSFCell cell23 = row.createCell(column++);
				cell23.setCellValue(new HSSFRichTextString(String.valueOf(dlcrm.getModiSector()+"/"+dlcrm.getModiUsr())));
				cell23.setCellStyle(styleAllWithBorderWrapped);
				cell23.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				
//				vamos a calcular x la long de la descripcion y/o comentarios cierre, si corresponde ajustar alto de fila.
//				dividimos x la cant de caracteres q creemos q entra en el ancho y revisamos el resto de division p sumar 1 toke mas
				int rowHeight = 0;
				int descripcionHeight = (dlcrm.getDescripcion().length() / 25)+(
						(dlcrm.getDescripcion().length() % 25)>0?1:0) ;
						
//				int comentariosCierreHeight = (ccrm.getComentarioCierre().length() / 25)+(
//						(ccrm.getComentarioCierre().length() % 25)>0?1:0) ;
				int comentariosCierreHeight = 1;
				if(dlcrm.getDescripcionSolucion() != null){
					comentariosCierreHeight = (dlcrm.getDescripcionSolucion().length() / 25)+(
						(dlcrm.getDescripcionSolucion().length() % 25)>0?1:0) ;
				}	
				
				if(descripcionHeight >= comentariosCierreHeight){
					rowHeight = descripcionHeight;
				}else{
					rowHeight = comentariosCierreHeight ;
				}
						
				row.setHeight((short)(row.getHeight() * rowHeight)); 
			}

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
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, BusquedaDocumLegalFiltro filtro) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Seguimiento de Reclamos CRM"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 22));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer(" Motivo: " ).append(filtro.getMotivo()==0?"TODOS":tiposMotivos.get(filtro.getMotivo()-1).getDescripcion());
				aux.append(" Tipo: "); 
				aux.append(filtro.getTipoReclamo()==0?"TODOS":tiposReclamo.get(filtro.getTipoReclamo()-1).getDescripcion());

				try{
					aux.append(" Plan: ");
					aux.append(filtro.getIdPlan()==0?"TODOS":PlanServiceUtil.getInstance().buscaPlanPorId(filtro.getIdPlan()).getDescripcion());
					aux.append(" Plan Omint: ");
					switch (filtro.getIdPlanOmint()) {
					case 0:
						aux.append("TODOS");
						break;
					case 1:
						aux.append("OSPIM_1");
						break;
					case 2:
						aux.append("OSPIM_2");
						break;
					case 3:
						aux.append("OSPIM_0");
						break;	
					case 4:
						aux.append("OSPIM_2A");
						break;		
					default:
						aux.append("TODOS");
						break;
					}
				}catch (Exception e) {
					_log.error(e);
				}	
				aux.append(" Período: " + sdf1.format(filtro.getFechaDesde().getTime()) + " al " +  sdf1.format(filtro.getFechaHasta().getTime()));
				aux.append(filtro.isTieneAntecedente()?"Tienen Antecedente":"");
				aux.append(filtro.isConcluido()?"Están concluídos":"");

		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 22));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("N° Reclamo"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Fecha"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Tipo Reclamo"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row3a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Fecha Notificación"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Motivo"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Usuario Alta"));
		cell25.setCellStyle(styleHeaderEnca2);

		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Datos Contacto"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell27 = row3a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Cuil/Inte o TipoDoc/NroDoc"));
		cell27.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell28 = row3a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("Es Afiliado"));
		cell28.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell29 = row3a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("Plan"));
		cell29.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Plan Omint"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Descripción"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha de Vto."));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Fecha de Respuesta"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Fecha Aviso al Estudio"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Fecha contacto PS/OM"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Expediente"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Resolución"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Nro. Trámite"));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell39 = row3a.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Radicación"));
		cell39.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell40 = row3a.createCell(column++);
		cell40.setCellValue(new HSSFRichTextString("Importe Reclamado"));
		cell40.setCellStyle(styleHeaderEnca2);		
		
		HSSFCell cell41 = row3a.createCell(column++);
		cell41.setCellValue(new HSSFRichTextString("Descripción Solución"));
		cell41.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell42 = row3a.createCell(column++);
		cell42.setCellValue(new HSSFRichTextString("Descripción Estudio"));
		cell42.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell43 = row3a.createCell(column++);
		cell43.setCellValue(new HSSFRichTextString("Usuario Cierre"));
		cell43.setCellStyle(styleHeaderEnca2);
		
		return index;
	}
}
