package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ReportePreautorizacionExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReportePreautorizacionExcel.class);

	public static HSSFWorkbook generaReportePreautorizacion(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String cuil=ParamUtil.getString(renderRequest,"cuil",null);
		String inteParam =  ParamUtil.getString(renderRequest, "inte",null);
		Integer inte = null;
		try {
			inte = Integer.parseInt(inteParam);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String estado=ParamUtil.getString(renderRequest,"estado",null);
		
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaDiaH + "/"
					+ (Integer.parseInt(fechaMesH) + 1) + "/"
					+ fechaAnioH);
		} catch (Exception e) {
			fechaH = null;
		}
		
		Integer id = ParamUtil.getInteger(renderRequest, "id",0);
		
		
//////////////////////		
		String fechaEmailDia = ParamUtil.getString(renderRequest,"fechadesdeemaildia");
		String fechaEmailMes = ParamUtil.getString(renderRequest,"fechadesdeemailmes");
		String fechaEmailAnio = ParamUtil.getString(renderRequest,"fechadesdeemailanio");
		
		String fechaEmailDiaH = ParamUtil.getString(renderRequest,"fechahastaemaildia");
		String fechaEmailMesH = ParamUtil.getString(renderRequest,"fechahastaemailmes");
		String fechaEmailAnioH = ParamUtil.getString(renderRequest,"fechahastaemailanio");
		String seccional = ParamUtil.getString(renderRequest,"seccional");
		boolean alertaRoja=ParamUtil.getBoolean(renderRequest, "alertaroja");
		boolean discapacidad=ParamUtil.getBoolean(renderRequest, "discapacidad");
		boolean supra=ParamUtil.getBoolean(renderRequest, "supra");
		boolean cirugia=ParamUtil.getBoolean(renderRequest, "cirugia");
		boolean medicamento=ParamUtil.getBoolean(renderRequest, "medicamento");
		boolean sinReintento=ParamUtil.getBoolean(renderRequest, "sin_reintento");
		boolean alojamiento=ParamUtil.getBoolean(renderRequest, "alojamiento");
		boolean protesisOrt=ParamUtil.getBoolean(renderRequest, "protesisOrtesis");
		boolean art=ParamUtil.getBoolean(renderRequest, "posibleart");
		boolean diabetes=ParamUtil.getBoolean(renderRequest, "diabetes");
        boolean baja=ParamUtil.getBoolean(renderRequest, "baja");
		
		Integer idAutorizacion = ParamUtil.getInteger(renderRequest, "idAutorizacion",0);
		Date fechaEmail = null;
		try {
			fechaEmail= formatoDeFechas.parse(fechaEmailDia + "/"
					+ (Integer.parseInt(fechaEmailMes) + 1) + "/"
					+ fechaEmailAnio);
		} catch (Exception e) {
			fechaEmail = null;
		}
		
		Date fechaEmailH = null;
		try {
			fechaEmailH = formatoDeFechas.parse(fechaEmailDiaH + "/"
					+ (Integer.parseInt(fechaEmailMesH) + 1) + "/"
					+ fechaEmailAnioH);
		} catch (Exception e) {
			fechaEmailH = null;
		}
		
		Integer idSeccional=null;
		if(seccional!=null && !"".equalsIgnoreCase(seccional)){
			try{
			  idSeccional=Integer.valueOf(seccional);
			}catch(Exception e){}  
		}
		
	    BusquedaPreautorizacionesFiltro filtro = new BusquedaPreautorizacionesFiltro(id, cuil, inte, fechaD, fechaH, estado, fechaEmail, 
				fechaEmailH, idSeccional, alertaRoja, discapacidad, supra, cirugia, medicamento, sinReintento, 
				alojamiento, idAutorizacion, protesisOrt,art,diabetes ,0, baja);
		
		
		List<PreAutorizacion> seguimientos = PreAutorizacionServiceUtil.getListaPreAutorizacionExtendido(filtro);
		
		Map<String,String>estadosPreautorizaciones = new HashMap<String,String>();
		for(int xi=0;xi<WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES.length;xi++){
			estadosPreautorizaciones.put(WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][0], WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][1]);
		}
		
		Map<String,String>tiposEntrega = new HashMap<String,String>();
		for(int xi=0;xi<WebKeysAutorizaciones.TIPOS_ENTREGA.length;xi++){
			tiposEntrega.put(WebKeysAutorizaciones.TIPOS_ENTREGA[xi][0], WebKeysAutorizaciones.TIPOS_ENTREGA[xi][1]);
	    }
		
		return generaReportePreautorizacion(filtro, seguimientos,estadosPreautorizaciones,tiposEntrega);
	}

	private static HSSFWorkbook generaReportePreautorizacion(BusquedaPreautorizacionesFiltro filtro, List<PreAutorizacion> list,Map<String,String>estadosPreautorizaciones,
			Map<String,String>tiposEntrega) {
				
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Preautorizaciones");

		HSSFPrintSetup ps = sheet.getPrintSetup();
//		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
//		ps.setFitHeight((short) 0);
//		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber=  getStyleNumber(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
//		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);

		if (list == null || list.isEmpty()) {
			return wb;
		}
		
		int index = createHeader(wb, sheet, filtro, styleBold);
		

		for(PreAutorizacion seguimiento: list){
			
// Buscar Prestaciones			
//			MovimientoBancario movimiento = new MovimientoBancario();
//			if(seguimiento.getNro_expediente()!=null && !"".equalsIgnoreCase(seguimiento.getNro_expediente())){
//				try {
//					movimiento = SeguimientoSurServiceUtil.traeMovimientoBancoSeguimientoSur(seguimiento.getNro_expediente());
//				} catch (SystemException e) {}
//			}
			
			index=crearDatosPreautorizacion(sheet, seguimiento, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber,estadosPreautorizaciones,tiposEntrega );
		}

		index++;
		sheet.createRow(index);
		
		for (int j = 0; j < 30; j++) {
			sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, BusquedaPreautorizacionesFiltro filtro, HSSFCellStyle styleBold) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		int index = 0;
		
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte Preautorizaciones"));
		cell.setCellStyle(styleHeaderEnca);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 27));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		Calendar hoy = DateUtils.getCalendarGMTMenos3();
		
		cell1.setCellValue(new HSSFRichTextString("Fecha: " +sdf.format(hoy.getTime() )));
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 27));
		
		HSSFRow rowSeparador = sheet.createRow(index++);
		
		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		StringBuffer aux = new StringBuffer("");
				
				if(filtro.getId() > 0) {
					aux.append(" ID: " + filtro.getId());
				}
				if(StringUtils.checkNotEmpty(filtro.getCuil())) {
					aux.append(" Cuil: " + filtro.getCuil());
				}
				if(filtro.getFechaD() != null && filtro.getFechaH() != null) {
					aux.append(" Entre Fechas: " + sdf1.format(filtro.getFechaD()) + " y " + sdf1.format(filtro.getFechaH()));
				}
				if(filtro.getFechaD() == null && filtro.getFechaH() != null) {
					aux.append(" Hasta fecha : " + sdf1.format(filtro.getFechaH()));
				}
				if(filtro.getFechaD() != null && filtro.getFechaH() == null) {
					aux.append(" Desde fecha: " + sdf1.format(filtro.getFechaD()));
				}
				if(StringUtils.checkNotEmpty(filtro.getEstado())) {
					aux.append(" Estado: ");
					if("CA".equalsIgnoreCase(filtro.getEstado())) { 
						aux.append(" CARGADO "); 
					  }else if("OB".equalsIgnoreCase(filtro.getEstado())){
						  aux.append(" OBSERVADO "); 
					  }else if("RE".equalsIgnoreCase(filtro.getEstado())){
						  aux.append(" RECHAZADO "); 
					  }else if("DE".equalsIgnoreCase(filtro.getEstado())){
						  aux.append(" DESESTIMADO "); 
					  }else if("GO".equalsIgnoreCase(filtro.getEstado())){
						  aux.append(" GESTION OSPIM ");	  
					  }
				}else {
					aux.append(" Estados: TODOS ");
				}
				
				if(filtro.getFechaEmail() != null && filtro.getFechaEmailH() != null) {
					aux.append(" Entre fechas email: " + sdf1.format(filtro.getFechaEmail()) + " y " + sdf1.format(filtro.getFechaEmailH()));
				}
				if(filtro.getFechaEmail() == null && filtro.getFechaEmailH() != null) {
					aux.append(" Hasta fecha email: " + sdf1.format(filtro.getFechaEmailH()));
				}
				if(filtro.getFechaEmail() != null && filtro.getFechaEmailH() == null) {
					aux.append(" Desde fecha email: " + sdf1.format(filtro.getFechaEmail()));
				}
				
				if(filtro.getIdAutorizacion() > 0) {
					aux.append(" ID Autorización : " + filtro.getIdAutorizacion());
				}
				
				if(filtro.isAlertaRoja()) {
					aux.append(" Sólo alertas rojas ");
				}
				
				if(filtro.isMedicamento()) {
					aux.append(" Sólo medicamentos ");
				}
				
				if(filtro.isCirugia()) {
					aux.append(" Sólo cirugías ");
				}
				
				if(filtro.isProtesisOrt()) {
					aux.append(" Sólo prótesis/órtesis ");
				}
				
				if(filtro.isART()) {
					aux.append(" Sólo posible ART ");
				}

                if(filtro.isDiabetes()) {
                    aux.append(" Sólo diabetes ");
                }

                if(filtro.isBaja()) {
                    aux.append(" Incluye dadas de baja ");
                }

		cell2.setCellValue(new HSSFRichTextString(aux.toString()));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 27));

		HSSFRow rowHeader = sheet.createRow(index++);

		int col = 0;

		HSSFCell cell16H = rowHeader.createCell(col++);
		cell16H.setCellValue(new HSSFRichTextString("Id"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(col++);
		cell20H.setCellValue(new HSSFRichTextString("Alerta"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(col++);
		cell13H.setCellValue(new HSSFRichTextString("Fecha"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(col++);
		cell18H.setCellValue(new HSSFRichTextString("Email"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(col++);
		cell19H.setCellValue(new HSSFRichTextString("Email 2"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(col++);
		cell6H.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(col++);
		cell3H.setCellValue(new HSSFRichTextString("Inte"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(col++);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(col++);
		cell17H.setCellValue(new HSSFRichTextString("Tipo Doc."));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell000H = rowHeader.createCell(col++);
		cell000H.setCellValue(new HSSFRichTextString("Nro.Doc"));
		cell000H.setCellStyle(styleBold);
		
		HSSFCell cell003H = rowHeader.createCell(col++);
		cell003H.setCellValue(new HSSFRichTextString("Seccional"));
		cell003H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(col++);
		cell12H.setCellValue(new HSSFRichTextString("Plan"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(col++);
		cell11H.setCellValue(new HSSFRichTextString("Estado"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell002H = rowHeader.createCell(col++);
		cell002H.setCellValue(new HSSFRichTextString("Respuesta Terc."));
		cell002H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(col++);
		cell2H.setCellValue(new HSSFRichTextString("Notif."));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(col++);
		cell14H.setCellValue(new HSSFRichTextString("Entrega"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(col++);
		cell8H.setCellValue(new HSSFRichTextString("Tipo Ent."));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell8_1H = rowHeader.createCell(col++);
		cell8_1H.setCellValue(new HSSFRichTextString("Prestacion"));
		cell8_1H.setCellStyle(styleBold);
		
		HSSFCell cell9_1H = rowHeader.createCell(col++);
		cell9_1H.setCellValue(new HSSFRichTextString("Discapacidad"));
		cell9_1H.setCellStyle(styleBold);
		
		HSSFCell cell9_2H = rowHeader.createCell(col++);
		cell9_2H.setCellValue(new HSSFRichTextString("Supra"));
		cell9_2H.setCellStyle(styleBold);
		
		HSSFCell cell9_3H = rowHeader.createCell(col++);
		cell9_3H.setCellValue(new HSSFRichTextString("Medicamento"));
		cell9_3H.setCellStyle(styleBold);
		
		HSSFCell cell9_4H = rowHeader.createCell(col++);
		cell9_4H.setCellValue(new HSSFRichTextString("Cirugía"));
		cell9_4H.setCellStyle(styleBold);
		
		HSSFCell cell10_4H = rowHeader.createCell(col++);
		cell10_4H.setCellValue(new HSSFRichTextString("Prótesis"));
		cell10_4H.setCellStyle(styleBold);
		
		HSSFCell cell10_4H1 = rowHeader.createCell(col++);
		cell10_4H1.setCellValue(new HSSFRichTextString("Posible ART"));
		cell10_4H1.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(col++);
		cell22H.setCellValue(new HSSFRichTextString("Solicitud Tercerizadora"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(col++);
		cell15H.setCellValue(new HSSFRichTextString("Motivo Rechazo"));
		cell15H.setCellStyle(styleBold);
		
		HSSFCell cell151H = rowHeader.createCell(col++);
		cell151H.setCellValue(new HSSFRichTextString("Prestador"));
		cell151H.setCellStyle(styleBold);

        HSSFCell cell152H = rowHeader.createCell(col++);
        cell152H.setCellValue(new HSSFRichTextString("Dada de baja"));
        cell152H.setCellStyle(styleBold);
		return index;
	}

		
	private static int crearDatosPreautorizacion(HSSFSheet sheet,PreAutorizacion pre, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,Map<String,String>estadosPreautorizaciones,
			Map<String,String>tiposEntrega) {
		
//		styleAll.setWrapText(true);
		String estado="";
		estado=estadosPreautorizaciones.get(pre.getUltimoEstado().getId());
		/*
		for(int xi=0;xi<WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES.length;xi++){
			if(pre.getUltimoEstado().getId().equalsIgnoreCase(WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][0])){
				estado=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[xi][1];
				break;
			}
		}
		*/
		
		String tipoEntrega="";
		if(pre.getTipoEntrega()!=null){
		  tipoEntrega=tiposEntrega.get(pre.getTipoEntrega());
		  /*
		  for(int xi=0;xi<WebKeysAutorizaciones.TIPOS_ENTREGA.length;xi++){
			if(pre.getTipoEntrega().equalsIgnoreCase(WebKeysAutorizaciones.TIPOS_ENTREGA[xi][0])){
				tipoEntrega=WebKeysAutorizaciones.TIPOS_ENTREGA[xi][1];
				break;
			}
		  }
		  */
		}
		
//		PreAutorizacion pAux;
		String prestaciones="";
		String discapacidad="";
		String cirugia="NO";
		String protesis="NO";
		/*
		try {
			pAux = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(pre.getId());
			discapacidad = pAux.isDiscapacidad()?"SI":"";

			for (Iterator<PreAutorizacionPrestacion> iterator = pAux.getCodigosPresentados().iterator(); iterator.hasNext();) {
				PreAutorizacionPrestacion p = iterator.next();
				
				prestaciones+=p.getNomenclador().getCodigo()+ " " +p.getNomenclador().getDescripcion() ;
				
				if(iterator.hasNext()) {		
					prestaciones+="\n";
				}
				
				cirugia = cirugia.equalsIgnoreCase("NO")&&p.getNomenclador().isCirugia()?"SI":"NO";
				protesis = protesis.equalsIgnoreCase("NO")&&pAux.isProtesisOrtesis() ?"SI":"NO";
			}
			
			if(pAux!=null && pAux.isAlojamiento()) {
				prestaciones="Solicitud de Alojamiento";
			}
			
		} catch (SystemException e) {	
		}
		
		*/
//Nuevo
        cirugia = cirugia.equalsIgnoreCase("NO")&& pre.isCirugia()?"SI":"NO";
        protesis = protesis.equalsIgnoreCase("NO")&&pre.isProtesisOrtesis() ?"SI":"NO";
        if(pre.isAlojamiento()) {
            prestaciones="Solicitud de Alojamiento";
        }else {
            prestaciones=pre.getPrestaciones()!=null?pre.getPrestaciones().replace(";", "\n"):"";
        }
//Fin Nuevo
		
		int col = 0;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell001 = rowHeader.createCell(col++);
		cell001.setCellValue(pre.getId());
		cell001.setCellStyle(styleAll);
		
		HSSFCell cell020 = rowHeader.createCell(col++);
		if(pre.isAlertaRoja()){
		   cell020.setCellValue(new HSSFRichTextString("Alerta Roja"));
		}else{
			cell020.setCellValue(new HSSFRichTextString(""));
		}
		cell020.setCellStyle(styleAll);
		
		
		
		HSSFCell cell002 = rowHeader.createCell(col++);
		cell002.setCellValue(new HSSFRichTextString(pre.getFecha_string()));
		cell002.setCellStyle(styleAll);
		
		HSSFCell cell017 = rowHeader.createCell(col++);
		cell017.setCellValue(new HSSFRichTextString(pre.getFechaEnvioMail_string()));
		cell017.setCellStyle(styleAll);
		
		HSSFCell cell019 = rowHeader.createCell(col++);
		cell019.setCellValue(new HSSFRichTextString(pre.getFechaEnvioMail2_string()));
		cell019.setCellStyle(styleAll);
		
		HSSFCell cell003 = rowHeader.createCell(col++);
		cell003.setCellValue(new HSSFRichTextString(pre.getAfiliado().getCuil_titular() ));
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(col++);
		cell004.setCellValue(pre.getAfiliado().getInte());
		cell004.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(col++);
		cell005.setCellValue(new HSSFRichTextString(pre.getAfiliado().getApeNombre()));
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(col++);
		cell006.setCellValue(new HSSFRichTextString(pre.getAfiliado().getDocumento_tipo()));
		cell006.setCellStyle(styleAll);
		
		HSSFCell cell007 = rowHeader.createCell(col++);
		cell007.setCellValue(new HSSFRichTextString(pre.getAfiliado().getDocu_numero() ));
		cell007.setCellStyle(styleAll);
		
		HSSFCell cell008 = rowHeader.createCell(col++);
		cell008.setCellValue(new HSSFRichTextString(pre.getAfiliado().getSeccional().getDescripcion() ));
		cell008.setCellStyle(styleAll);
		
		HSSFCell cell009 = rowHeader.createCell(col++);
		cell009.setCellValue(new HSSFRichTextString(pre.getAfiliado().getAfiPlan().getPlan().getDescripcion() ));
		cell009.setCellStyle(styleAll);
		
		HSSFCell cell010 = rowHeader.createCell(col++);
		cell010.setCellValue(new HSSFRichTextString(estado ));
		cell010.setCellStyle(styleAll);
		
		HSSFCell cell011 = rowHeader.createCell(col++);
		cell011.setCellValue(new HSSFRichTextString(pre.getFechaRespuestaPS_string() ));
		cell011.setCellStyle(styleAll);
		
		HSSFCell cell012 = rowHeader.createCell(col++);
		cell012.setCellValue(new HSSFRichTextString(pre.getFechaNotificacionAfiliado_string() ));
		cell012.setCellStyle(styleAll);
		
		HSSFCell cell013 = rowHeader.createCell(col++);
		cell013.setCellValue(new HSSFRichTextString(pre.getFechaEntregaRespuesta_string() ));
		cell013.setCellStyle(styleAll);
		
		HSSFCell cell014 = rowHeader.createCell(col++);
		cell014.setCellValue(new HSSFRichTextString(tipoEntrega ));
		cell014.setCellStyle(styleAll);
		
		HSSFCell cell015 = rowHeader.createCell(col++);
		cell015.setCellValue(new HSSFRichTextString(prestaciones ));
		cell015.setCellStyle(styleAll);
		
		HSSFCell cell016 = rowHeader.createCell(col++);
		cell016.setCellValue(new HSSFRichTextString(discapacidad));
		cell016.setCellStyle(styleAll);

		HSSFCell cell022 = rowHeader.createCell(col++);
		cell022.setCellValue(new HSSFRichTextString(pre.isSupra()?"SI":"NO"));
		cell022.setCellStyle(styleAll);
		
		HSSFCell cell023 = rowHeader.createCell(col++);
		cell023.setCellValue(new HSSFRichTextString(pre.isMedicamento()?"SI":"NO"));
		cell023.setCellStyle(styleAll);
		
		HSSFCell cell024 = rowHeader.createCell(col++);
		cell024.setCellValue(new HSSFRichTextString(cirugia));
		cell024.setCellStyle(styleAll);

		HSSFCell cell025 = rowHeader.createCell(col++);
		cell025.setCellValue(new HSSFRichTextString(protesis));
		cell025.setCellStyle(styleAll);
		
		HSSFCell cell026 = rowHeader.createCell(col++);
		cell026.setCellValue(new HSSFRichTextString(pre.isART()?"SI":"NO"));
		cell026.setCellStyle(styleAll);
		
		HSSFCell cell018 = rowHeader.createCell(col++);
		cell018.setCellValue(new HSSFRichTextString(String.valueOf(pre.getIdAutorizacionWS())));
		cell018.setCellStyle(styleAll);

		HSSFCell cell021 = rowHeader.createCell(col++);
        cell021.setCellValue(new HSSFRichTextString(
                pre.getUltimoEstado()!=null && pre.getUltimoEstado().getMotivoRechazo()!=null
                        ? pre.getUltimoEstado().getMotivoRechazo()
                        : ""
        ));
        cell021.setCellStyle(styleAll);
		
		HSSFCell cell027 = rowHeader.createCell(col++);
		cell027.setCellValue(new HSSFRichTextString(pre.getPrestador()!=null && pre.getPrestador().getId_prestador()>0?pre.getPrestador().getCuit() +
				"   " + pre.getPrestador().getDescripcion():""));
		cell027.setCellStyle(styleAll);

        HSSFCell cell028 = rowHeader.createCell(col++);
        cell028.setCellValue(new HSSFRichTextString(
                pre.getBaja_Fecha()!=null && pre.getBaja_Fecha().getTime()<System.currentTimeMillis() ? "SI" : "NO"
        ));
        cell028.setCellStyle(styleAll);
//        rowHeader.setHeight((short) 0);
		return index++;
	}
        
}


