package ar.com.ospim.crm.reportes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.BusquedaContactoFiltro;
import ar.com.ospim.crm.beans.CRMEstadistica;
import ar.com.ospim.crm.beans.CRMEstadisticaCierre;
import ar.com.ospim.crm.beans.CRMEstadisticaRendimiento;
import ar.com.ospim.crm.beans.CategoriaContacto;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.TipoContacto;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

public class ReporteCrmContactoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteCrmContactoExcel.class);

	private static List<TipoContacto> tiposContactos;
	private static List<MotivoContacto> tiposMotivos;
	private static List<CategoriaContacto> categoriasContactos;
	private static final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
	
	public static HSSFWorkbook generaReporteResolucionContactoCRM(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			com.liferay.portal.model.User user = PortalUtil.getUser(req);
			
			tiposContactos = CrmServiceUtil.buscarTiposContacto();
			tiposMotivos = CrmServiceUtil.buscarMotivosContacto();
			categoriasContactos = CrmServiceUtil.buscarCategoriasContacto();
		
			int motivo = ParamUtil.getInteger(req, "motivo",0);
			int categoria = ParamUtil.getInteger(req, "categoria",0);
			int tipo = ParamUtil.getInteger(req, "tipo",0);
			String estado = ParamUtil.getString(req, "estado",null);
			String cuilTitular = ParamUtil.getString(req, "cuil_titular",null);
			String inte = ParamUtil.getString(req, "inte",null);
			int incluirA = ParamUtil.getInteger(req, "incluirA", 0);
			int nroContacto = ParamUtil.getInteger(req, "nro_contacto",0);
			String sectorSel = ParamUtil.getString(req, "sector",null);
			String usuarioSel = ParamUtil.getString(req, "usuario",null);
			if(StringUtils.checkEmpty(sectorSel)){
				sectorSel = null;
			}
			if(StringUtils.checkEmpty(usuarioSel)){
				usuarioSel = null;
			}
			int idPlan = ParamUtil.getInteger(req, "plan",0);
			int idPlanOmint = ParamUtil.getInteger(req, "planOmint",0);
			int importancia = ParamUtil.getInteger(req, "importancia",99);
			int incumpContrato = ParamUtil.getInteger(req, "incumplimientoContrato",99);
			int eficaciaConformidad = ParamUtil.getInteger(req, "eficaciaConform",99);
			
			String fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
			
			String noAfiliadoDocNumero = ParamUtil.getString(req,"noAfiliadoDocNumero", null);
			int situacionMedica = ParamUtil.getInteger(req, "situacion_medica",0);

			
			BusquedaContactoFiltro filtro = new BusquedaContactoFiltro();
			
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
			int seccional = ParamUtil.getInteger(req, "seccional",0);
			
			filtro.setEstado(estado);
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setCategoria(categoria);
			filtro.setCuil_titular(cuilTitular);
			filtro.setInte(inte);
			filtro.setIncluirA(incluirA);
			filtro.setMotivo(motivo);
			filtro.setTipo(tipo);
			filtro.setNro_contacto(nroContacto);
			filtro.setSector(sectorSel);
			filtro.setUsuario(usuarioSel);
			filtro.setIdPlan(idPlan);
			filtro.setIdPlanOmint(idPlanOmint);
			filtro.setImportancia(importancia);
			filtro.setIncumplimientoContacto(incumpContrato);
			filtro.setEficaciaConformidad(eficaciaConformidad);
			filtro.setSeccional(seccional);
			filtro.setNoAfiliadoDocNumero(noAfiliadoDocNumero);
			filtro.setSituacionMedica(situacionMedica);

			List<ContactoCRM> busqueda = CrmServiceUtil.busquedaContactosCRMxls(filtro, user);

			return generarReporte(filtro, busqueda);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte contactos crm", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(BusquedaContactoFiltro filtro, List<ContactoCRM> contactos) {
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
		HSSFCellStyle styleAllWithBorderWrapped = getStyleAllWithBorder(wb, 10);
		styleAllWithBorderWrapped.setWrapText(true);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, filtro);
			index++;
			for (ContactoCRM ccrm : contactos) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getIdContacto())));
				cell0.setCellStyle(styleAllWithBorder);
				cell0.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(sdf2.format(ccrm.getAltaFecha())));
				cell1.setCellStyle(styleAllWithBorder);
				cell1.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell1a = row.createCell(column++);
				cell1a.setCellValue(new HSSFRichTextString(ccrm.getImportanciaDescripcion()));
				cell1a.setCellStyle(styleAllWithBorder);
				cell1a.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell1b = row.createCell(column++);
				cell1b.setCellValue(new HSSFRichTextString(ccrm.getIncumplimientoDelContrato()==0?"NO":"SI"));
				cell1b.setCellStyle(styleAllWithBorder);
				cell1b.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(ccrm.getTipo().getDescripcion()));
				cell2.setCellStyle(styleAllWithBorder);
				cell2.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(ccrm.getCategoria().getDescripcion()));
				cell3.setCellStyle(styleAllWithBorder);
				cell3.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(ccrm.getMotivo().getDescripcion()));
				cell4.setCellStyle(styleAllWithBorder);
				cell4.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(ccrm.getEstado().name()));
				cell5.setCellStyle(styleAllWithBorder);
				cell5.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
		
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(ccrm.getAltaUsr()));
				cell6.setCellStyle(styleAllWithBorder);
				cell6.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getTiempoResolucion())));
				cell7.setCellStyle(styleAllWithBorder);
				cell7.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell10 = row.createCell(column++);
				if(ccrm.getAfiliado() != null){
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getAfiliado().getApeNombre().trim())));
				}else if(ccrm.getContactoSeccional() != null){	
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getContactoSeccional().getNombreApe() )));
				}else if(ccrm.getPrestador() != null){	
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getPrestador().getDescripcion() )));
				}else if(ccrm.getEmpresa() != null){	
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getEmpresa().getRazon_soc() )));
				}else if(ccrm.getCompaniero() != null){	
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getCompaniero().getUsuario() )));	
				}else{
					cell10.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getNoAfiliado().getApellido().trim() + ", "+
							ccrm.getNoAfiliado().getNombre().trim() )));
				}
				cell10.setCellStyle(styleAllWithBorderWrapped);
				cell10.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell11 = row.createCell(column++);
				if(ccrm.getAfiliado() != null){
					cell11.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getAfiliado().getCuil_titular() 
							+ " / " + ccrm.getAfiliado().getInte())));
				}else if(ccrm.getContactoSeccional() != null){	
					cell11.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getContactoSeccional().getSeccional().getDescripcion() )));
				}else{
					cell11.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getNoAfiliado().getDocumentoTipo() 
							+ " "+ ccrm.getNoAfiliado().getDocumentoNumero())));
				}
				cell11.setCellStyle(styleAllWithBorderWrapped);
				cell11.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell12 = row.createCell(column++);
				if(ccrm.getAfiliado() != null ){
					cell12.setCellValue(new HSSFRichTextString("SI"));
				}else if(ccrm.getContactoSeccional() != null ){	
					cell12.setCellValue(new HSSFRichTextString("SECCIONAL"));
				}else if(ccrm.getPrestador() != null ){	
					cell12.setCellValue(new HSSFRichTextString("PRESTADOR"));
				}else if(ccrm.getEmpresa() != null ){	
					cell12.setCellValue(new HSSFRichTextString("EMPRESA"));
				}else if(ccrm.getCompaniero() != null ){	
					cell12.setCellValue(new HSSFRichTextString("COMPAÑERO"));	
				}else{
					cell12.setCellValue(new HSSFRichTextString("NO"));
				}
				cell12.setCellStyle(styleAllWithBorderWrapped);
				cell12.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell13 = row.createCell(column++);
				if(ccrm.getAfiliado() != null && ccrm.getAfiliado().getUltimo_plan() != null){
					cell13.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getAfiliado().getUltimo_plan().getDescripcion() )));
				}else{
					cell13.setCellValue(new HSSFRichTextString(String.valueOf("-")));
				}
				cell13.setCellStyle(styleAllWithBorderWrapped);
				cell13.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell14 = row.createCell(column++);
				if(ccrm.getAfiliado() != null && ccrm.getAfiliado().getUltimo_plan() != null && ccrm.getAfiliado().getUltimo_plan().getDescripcionOmint() != null){
					cell14.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getAfiliado().getUltimo_plan().getDescripcionOmint())));
				}else{
					cell14.setCellValue(new HSSFRichTextString(String.valueOf("-")));
				}
				cell14.setCellStyle(styleAllWithBorderWrapped);
				cell14.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getDescripcion().trim())));
				cell15.setCellStyle(styleAllWithBorderWrapped);
				cell15.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				sheet.setColumnWidth(15, 6000); 

				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(ccrm.getEstado().equals(ContactoCRM.ESTADOS.DERIVADO)?
						String.valueOf(ccrm.getDerivacion().getEdificio()+"/"+
								ccrm.getDerivacion().getGrupo()+"/"+
								ccrm.getDerivacion().getUsuario()):""));
				cell16.setCellStyle(styleAllWithBorderWrapped);
				cell16.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell17 = row.createCell(column++);
				if(ccrm.getComentarioCierre() != null){
					cell17.setCellValue(new HSSFRichTextString(String.valueOf(ccrm.getComentarioCierre().trim())));
				}else{
					cell17.setCellValue(new HSSFRichTextString(""));
				}
				cell17.setCellStyle(styleAllWithBorderWrapped);
				cell17.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				sheet.setColumnWidth(17, 6000); 

				HSSFCell cell18 = row.createCell(column++);
				cell18.setCellValue(new HSSFRichTextString(ccrm.getEstado().equals(ContactoCRM.ESTADOS.CERRADO)?String.valueOf(ccrm.getModiSector()+"/"+ccrm.getModiUsr()):""));
				cell18.setCellStyle(styleAllWithBorderWrapped);
				cell18.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);

				HSSFCell cell19 = row.createCell(column++);
				cell19.setCellValue(new HSSFRichTextString(ccrm.getEficacia()!=null&&ccrm.getEficacia().getId()>0?(ccrm.getEficacia().isConforme()?"SI":"NO"):""));
				cell19.setCellStyle(styleAllWithBorderWrapped);
				cell19.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell20 = row.createCell(column++);
				cell20.setCellValue(new HSSFRichTextString(ccrm.getEficacia()!=null&&ccrm.getEficacia().getAltaUsr()!=null?String.valueOf(ccrm.getEficacia().getAltaSector()+"/"+ccrm.getEficacia().getAltaUsr()):""));
				cell20.setCellStyle(styleAllWithBorderWrapped);
				cell20.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				HSSFCell cell21 = row.createCell(column++);
				cell21.setCellValue(new HSSFRichTextString(ccrm.getEficacia()!=null&&ccrm.getEficacia().getObservaciones()!=null?String.valueOf(ccrm.getEficacia().getObservaciones().trim()):""));
				cell21.setCellStyle(styleAllWithBorderWrapped);
				cell21.getCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
				
				
//				vamos a calcular x la long de la descripcion y/o comentarios cierre, si corresponde ajustar alto de fila.
//				dividimos x la cant de caracteres q creemos q entra en el ancho y revisamos el resto de division p sumar 1 toke mas
				int rowHeight = 0;
				int descripcionHeight = (ccrm.getDescripcion().length() / 25)+(
						(ccrm.getDescripcion().length() % 25)>0?1:0) ;
						
//				int comentariosCierreHeight = (ccrm.getComentarioCierre().length() / 25)+(
//						(ccrm.getComentarioCierre().length() % 25)>0?1:0) ;
				int comentariosCierreHeight = 1;
				if(ccrm.getComentarioCierre() != null){
					comentariosCierreHeight = (ccrm.getComentarioCierre().length() / 25)+(
						(ccrm.getComentarioCierre().length() % 25)>0?1:0) ;
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
			sheet.autoSizeColumn((short) 16);
			sheet.autoSizeColumn((short) 18);
//			sheet.autoSizeColumn((short) 15);
//			sheet.autoSizeColumn((short) 17);
			sheet.autoSizeColumn((short) 19);
			sheet.autoSizeColumn((short) 20);
//			sheet.autoSizeColumn((short) 21);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, BusquedaContactoFiltro filtro) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Contactos CRM"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer(" Estado: ").append(filtro.getEstado().equalsIgnoreCase("")?"TODOS":filtro.getEstado());
				aux.append(" Motivo: " );
//				aux.append(filtro.getMotivo()==0?"TODOS":tiposMotivos.get(filtro.getMotivo()-1).getDescripcion());
				aux.append(filtro.getMotivo()==0?"TODOS":tiposMotivos.get(tiposMotivos.indexOf(new MotivoContacto(filtro.getMotivo(),null))).getDescripcion());
				aux.append(" Tipo: "); 
				aux.append(filtro.getTipo()==0?"TODOS":tiposContactos.get(tiposContactos.indexOf(new TipoContacto(filtro.getTipo(),""))).getDescripcion());
				aux.append(" Categoría: ");
				aux.append(filtro.getCategoria()==0?"TODAS":categoriasContactos.get(categoriasContactos.indexOf(new CategoriaContacto(filtro.getCategoria(),""))).getDescripcion());
				aux.append(" Importancia: ");
				aux.append(filtro.getImportancia()==99?"TODAS":WebKeysCrm.CRM_IMPORTANCIA[filtro.getImportancia()][1]);
				aux.append(" Incump Contrato: ");
				aux.append(filtro.getIncumplimientoContacto()==99?"TODOS":filtro.getIncumplimientoContacto()==0?"Con Cumplimiento":"Sin Cumplimiento");
				aux.append(" Conformidad Eficacia: ");
				aux.append(filtro.getEficaciaConformidad()==99?"TODOS":filtro.getEficaciaConformidad()==1?"Con Conformidad":"Sin Conformidad");

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
				

		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 17));

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
		cell20.setCellValue(new HSSFRichTextString("N° contacto"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Fecha"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell211 = row3a.createCell(column++);
		cell211.setCellValue(new HSSFRichTextString("Importancia"));
		cell211.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell212 = row3a.createCell(column++);
		cell212.setCellValue(new HSSFRichTextString("Incump. Contrato"));
		cell212.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Tipo"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row3a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Categoría"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Motivo"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Estado"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Usuario Alta"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell27 = row3a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Resolución"));
		cell27.setCellStyle(styleHeaderEnca2);	

		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Datos Contacto"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Cuil/Inte o TipoDoc/NroDoc"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Es Afiliado"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Plan"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Plan Omint"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Descripción"));
		cell35.setCellStyle(styleHeaderEnca2);

		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Derivado a"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Comentarios de Cierre"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row3a.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Usuario Cierre"));
		cell38.setCellStyle(styleHeaderEnca2);

		HSSFCell cell39 = row3a.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Conformidad"));
		cell39.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell41 = row3a.createCell(column++);
		cell41.setCellValue(new HSSFRichTextString("Usuario Auditoria"));
		cell41.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell40 = row3a.createCell(column++);
		cell40.setCellValue(new HSSFRichTextString("Observaciones de Auditoria"));
		cell40.setCellStyle(styleHeaderEnca2);
		
		return index;
	}
	
	public static HSSFWorkbook generaEstadisticaAgrupadoContactoCRM(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {

			String fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
						
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
			
			List<CRMEstadistica> resultados = CrmServiceUtil.estadisticaAgrupada(fechaDesde, fechaHasta);

			return generarEstadisticaAgrupado(fechaDesde, fechaHasta, resultados);
			
		} catch (Exception e) {
			_log.error("Error al generar estadisticas agrupado contactos crm", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarEstadisticaAgrupado(Date fechaDesde, Date fechaHasta, List<CRMEstadistica> resultados) {
		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeaderEstadAgrupado(wb, sheet, fechaDesde, fechaHasta);
			index++;
			for (CRMEstadistica cest : resultados) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(cest.getSector()));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTipo_llamado_entrante())));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTipo_llamado_saliente())));
				cell2.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTipo_atencion_seccional())));
				cell3.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell40 = row.createCell(column++);
				cell40.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTipo_whatsapp_entrante())));
				cell40.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTipo_otros())));
				cell4.setCellStyle(styleAllWithBorder);
				/*
				HSSFCell cell4bis = row.createCell(column++);
				cell4bis.setCellValue(new HSSFRichTextString(String.valueOf(cest.getMotivo_otros())));
				cell4bis.setCellStyle(styleAllWithBorder);
				*/
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(String.valueOf(cest.getCategoria_consulta())));
				cell5.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(String.valueOf(cest.getCategoria_reclamo())));
				cell6.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(String.valueOf(cest.getCategoria_queja())));
				cell7.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(String.valueOf(cest.getCategoria_sugerencia())));
				cell8.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(String.valueOf(cest.getCategoria_felicitacion())));
				cell9.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(String.valueOf(cest.getEstado_pendiente())));
				cell10.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(String.valueOf(cest.getEstado_derivado())));
				cell11.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(String.valueOf(cest.getEstado_cerrado())));
				cell12.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTotal())));
				cell13.setCellStyle(styleAllWithBorder);
				
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
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int createHeaderEstadAgrupado(HSSFWorkbook wb, HSSFSheet sheet, Date fechaDesde, Date fechaHasta) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Estadística por Tipos de Contactos CRM"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Período: ").append(sdf1.format(fechaDesde.getTime()) + " al " +  sdf1.format(fechaHasta.getTime()));


		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 10));

		index = index + 2;
		
		/*Sub fila de tipos de agrupamientos, para que se entiendan las columnas de abajo...*/
		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell3bcde = row3.createCell(1);
		cell3bcde.setCellValue(new HSSFRichTextString(" Tipos ") );
		cell3bcde.setCellStyle(styleHeaderEnca2);
		sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 1, 5));
/*
		HSSFCell cell3fg = row3.createCell(5);
		cell3fg.setCellValue(new HSSFRichTextString(" Motivos ") );
		cell3fg.setCellStyle(styleHeaderEnca2);
		sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 5, 6));
*/		
		HSSFCell cell3hijk = row3.createCell(6);
		cell3hijk.setCellValue(new HSSFRichTextString(" Categorías ") );
		cell3hijk.setCellStyle(styleHeaderEnca2);
		sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 6, 10));
		
		HSSFCell cell3lmn = row3.createCell(11);
		cell3lmn.setCellValue(new HSSFRichTextString(" Estados ") );
		cell3lmn.setCellStyle(styleHeaderEnca2);
		sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 11, 13));
		
//		index = index + 2;
//		index = index + 1;
		
		HSSFRow row4a = sheet.createRow(index);

		int column = 0;
	    
		HSSFCell cell20 = row4a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Sector"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row4a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Llamado Entrante"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row4a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Llamado Saliente"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row4a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Atención Seccional"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell230 = row4a.createCell(column++);
		cell230.setCellValue(new HSSFRichTextString("Whatsapp Entrante"));
		cell230.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row4a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Otros"));
		cell24.setCellStyle(styleHeaderEnca2);
		/*
		HSSFCell cell25 = row4a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Otros"));
		cell25.setCellStyle(styleHeaderEnca2);
		*/
		
		HSSFCell cell26 = row4a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Consulta"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell27 = row4a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Reclamo"));
		cell27.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell28 = row4a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("Queja"));
		cell28.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell29 = row4a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("Sugerencia"));
		cell29.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row4a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Felicitación"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row4a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Pendiente"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row4a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Derivado"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row4a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("Cerrado"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row4a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Total"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		return index;
	}

	public static HSSFWorkbook generaEstadisticaRendimientoContactoCRM(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {

			String fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
						
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
			
			List<CRMEstadisticaRendimiento> resultados = CrmServiceUtil.estadisticaRendimiento(fechaDesde, fechaHasta);

			return generarEstadisticaRendimiento(fechaDesde, fechaHasta, resultados);
			
		} catch (Exception e) {
			_log.error("Error al generar estadisticas rendimiento contactos crm", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarEstadisticaRendimiento(Date fechaDesde, Date fechaHasta, List<CRMEstadisticaRendimiento> resultados) {
		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeaderEstadRendimiento(wb, sheet, fechaDesde, fechaHasta);
//			index++;
			for (CRMEstadisticaRendimiento cest : resultados) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(cest.getSector()));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTotalEstadoPendiente()+cest.getTotalEstadoDerivado())));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTotalEstadoCerrado() )));
				cell2.setCellStyle(styleAllWithBorder);	
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(String.valueOf(cest.getPromedioResolucion() )));
				cell3.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf((cest.getTotalContactosSector()*100)/ cest.getTotal()) ));
				cell4.setCellStyle(styleAllWithBorder);	
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTotalContactosSector() )));
				cell5.setCellStyle(styleAllWithBorder);	
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(String.valueOf(cest.getTotal() )));
				cell6.setCellStyle(styleAllWithBorder);	
				
				
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);

			
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int createHeaderEstadRendimiento(HSSFWorkbook wb, HSSFSheet sheet, Date fechaDesde, Date fechaHasta) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Estadística Rendimiento de Contactos CRM"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Período: ").append(sdf1.format(fechaDesde.getTime()) + " al " +  sdf1.format(fechaHasta.getTime()));


		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		
		/*Sub fila de tipos de agrupamientos, para que se entiendan las columnas de abajo...*/
		HSSFRow row3 = sheet.createRow(index++);

		int column = 0;
	    
		HSSFCell cell20 = row3.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Sector"));
		cell20.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell21 = row3.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Abierto"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row3.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Cerrado"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row3.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Promedio Resolución (días)"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Influencia (%)"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Total por Sector"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row3.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Total de Contactos"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		return index;
	}
	
	public static HSSFWorkbook generaEstadisticaCierresContactoCRM(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		try {

			String fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
						
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
			
			List<CRMEstadisticaCierre> resultados = CrmServiceUtil.estadisticaCierres(fechaDesde, fechaHasta);

			return generarEstadisticaCierres(fechaDesde, fechaHasta, resultados);
			
		} catch (Exception e) {
			_log.error("Error al generar estadisticas cierres contactos crm", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarEstadisticaCierres(Date fechaDesde, Date fechaHasta, List<CRMEstadisticaCierre> resultados) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {
			List<Feriado> feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
			
			int index = createHeaderEstadCierre(wb, sheet, fechaDesde, fechaHasta);
			
			int totalCRM = resultados.size();
			int totalCRMenTermino = 0;
			int totalCRMfueraTermino = 0;
			
//			Revisamos los contactos que cumplen la condicion de busqueda sql.
//			Despues evaluamos simplemente aquellos que superan 2 dias, revisar si fue culpa de feriados o finde
//			los demas estan en Termino
			
			for (CRMEstadisticaCierre cest : resultados) {
				
				long diferencia = ( cest.getModiFecha().getTime() - cest.getAltaFecha().getTime() )/MILLSECS_PER_DAY; 
				
				if(diferencia > 2){
					
					int diasHabiles = 0;
					Calendar fechaAltaContacto = DateUtils.getCalendarGMTMenos3(); 
					Calendar fechaCierreContacto = DateUtils.getCalendarGMTMenos3(); 
					fechaAltaContacto.setTime(DateUtils.getMismoDia_00_00hs(cest.getAltaFecha()));
					fechaCierreContacto.setTime(DateUtils.getMismoDia_00_00hs(cest.getModiFecha()));
					
					while(!fechaAltaContacto.equals(fechaCierreContacto) && diasHabiles < 3){
						
						if(DateUtils.esFeriadoOFinde(fechaCierreContacto.getTime(), feriados)){
							fechaCierreContacto.add(Calendar.DATE, -1);
						}else{
							diasHabiles++;
						}
					}
					if(diasHabiles > 2){
						totalCRMfueraTermino++;
					}else{
						totalCRMenTermino++;
					}
					
				}else{
					totalCRMenTermino++;
				}
			}

			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			int column = 0;
			HSSFRow row = sheet.createRow(index++);
			
			HSSFCell cell0 = row.createCell(column++);
			cell0.setCellValue(new HSSFRichTextString(String.valueOf(totalCRM)));
			cell0.setCellStyle(styleAllWithBorder);
			
			HSSFCell cell1 = row.createCell(column++);
			cell1.setCellValue(new HSSFRichTextString(String.valueOf(totalCRMenTermino)));
			cell1.setCellStyle(styleAllWithBorder);
			
			HSSFCell cell2 = row.createCell(column++);
			cell2.setCellValue(new HSSFRichTextString(String.valueOf(decimalFormat.format( ((double) totalCRMenTermino*100)/totalCRM ))) );
			cell2.setCellStyle(styleAllWithBorder);	
			
			HSSFCell cell3 = row.createCell(column++);
			cell3.setCellValue(new HSSFRichTextString(String.valueOf(totalCRMfueraTermino )));
			cell3.setCellStyle(styleAllWithBorder);
			
			HSSFCell cell4 = row.createCell(column++);
			cell4.setCellValue(new HSSFRichTextString(String.valueOf(decimalFormat.format( ((double) totalCRMfueraTermino*100)/ totalCRM) )) );
			cell4.setCellStyle(styleAllWithBorder);	
			
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int createHeaderEstadCierre(HSSFWorkbook wb, HSSFSheet sheet, Date fechaDesde, Date fechaHasta) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Estadística Cierres de Contactos normales CRM"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Período: ").append(sdf1.format(fechaDesde.getTime()) + " al " +  sdf1.format(fechaHasta.getTime()));


		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		
		/*Sub fila de tipos de agrupamientos, para que se entiendan las columnas de abajo...*/
		HSSFRow row3 = sheet.createRow(index++);

		int column = 0;
	    
		HSSFCell cell20 = row3.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Total Contactos"));
		cell20.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell21 = row3.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Total Cerrados en Término"));
		cell21.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell22 = row3.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Porcentaje Cerrados en Término (%)"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row3.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Total Cerrados Fuera de Término"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Porcentaje Cerrados Fuera de Término (%)"));
		cell24.setCellStyle(styleHeaderEnca2);		
		

		
		return index;
	}

}
