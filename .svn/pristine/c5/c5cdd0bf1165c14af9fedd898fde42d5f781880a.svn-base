package ar.com.uoma.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.Correspondencia;
import ar.com.uoma.correspondencia.WebKeysCorrespondencia;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;

public class ReporteCorrespondenciaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCorrespondenciaExcel.class);

	static HashMap<String, String> empresaHM = new HashMap<String,String>();
	static HashMap<String, String> grupoHM = new HashMap<String,String>();
	
	public static HSSFWorkbook generaReporteCorrespondencia(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		try {
			ParamUtil.getString(req, "destino");
			ParamUtil.getString(req, "edificio");
//			String fecha = format.format(new Date(System.currentTimeMillis()));
			ArrayList<Correspondencia> correspondencia = (ArrayList<Correspondencia>) req
					.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
			
			String destino = ParamUtil.getString(req, "destino");
			String lugarRecepcion = ParamUtil.getString(req, "edificio");
			String desdeFinal = ParamUtil.getString(req, "desde_final");
			String hastaFinal = ParamUtil.getString(req, "hasta_final");

			String razon_prestador = ParamUtil
					.getString(req, "razon_prestador");

			int provincia = ParamUtil.getInteger(req, "provinciaremi");
			int localidad = ParamUtil.getInteger(req, "localidadremi");

			int id_seccional_remi = ParamUtil.getInteger(req, "id_seccional_r");

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date envioRecepDesde = sdf.parse(desdeFinal);
			Date envioRecepHasta = sdf.parse(hastaFinal);

			return generarReporte(destino, lugarRecepcion, envioRecepDesde,
					envioRecepHasta, razon_prestador, provincia, localidad,
					id_seccional_remi, correspondencia);
		} catch (Exception e) {
			_log.error("Error al generar reporte correspondencia", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(String destino,
			String lugarRecepcion, Date desde, Date hasta,
			String razonPrestador, int provincia, int localidad, int seccional,
			ArrayList<Correspondencia> correspondencia) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, destino, lugarRecepcion, desde,
					hasta, razonPrestador, provincia, localidad, seccional);
			index++;
			for (Correspondencia corr : correspondencia) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(corr.getIdCorrespondencia());
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(corr.getDestino()));
				cell1.setCellStyle(styleAllWithBorder);

				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(corr
						.getFechaEnvioRecepcionAsString()));
				cell2.setCellStyle(styleAllWithBorder);

				if (destino.equals("ENTRANTE")) {
					HSSFCell cell3 = row.createCell(column++);
					cell3.setCellValue(new HSSFRichTextString(corr
							.getLugarRecepcion()));
					cell3.setCellStyle(styleAllWithBorder);
				}

				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(null != corr
						.getSeccionalRemitente() ? corr.getSeccionalRemitente()
						.getDescripcion() : ""));
				cell4.setCellStyle(styleAllWithBorder);

				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(corr
						.getEdificioRemitente()));
				cell5.setCellStyle(styleAllWithBorder);
				
				if (destino.equals("SALIENTE")) {
					HSSFCell cell6 = row.createCell(column++);
					cell6.setCellValue(new HSSFRichTextString(corr
							.getEdificioDestinatario()));
					cell6.setCellStyle(styleAllWithBorder);
				}

				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(null != corr
						.getDomicilioRemitente() ? corr.getDomicilioRemitente()
						.getProvincia().getDescripcion() : ""));
				cell7.setCellStyle(styleAllWithBorder);

				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(null != corr
						.getDomicilioRemitente() ? corr.getDomicilioRemitente()
						.getLocalidad().getDescripcion() : ""));
				cell8.setCellStyle(styleAllWithBorder);

				if (destino.equals("SALIENTE")) {
					HSSFCell cell10 = row.createCell(column++);
					cell10.setCellValue(new HSSFRichTextString(corr
							.getRazonPrestadorDestinatario()));
					cell10.setCellStyle(styleAllWithBorder);

				} else if (destino.equals("ENTRANTE")) {
					HSSFCell cell9 = row.createCell(column++);
					cell9.setCellValue(new HSSFRichTextString(corr
							.getRazonPrestadorRemitente()));
					cell9.setCellStyle(styleAllWithBorder);
					HSSFCell cell91 = row.createCell(column++);
					cell91.setCellValue(new HSSFRichTextString(corr
							.getCodFarmacia()));
					cell91.setCellStyle(styleAllWithBorder);
					HSSFCell cell92 = row.createCell(column++);
					cell92.setCellValue(new HSSFRichTextString(corr
							.getFarmacia()));
					cell92.setCellStyle(styleAllWithBorder);
				}

				if (destino.equals("SALIENTE")) {
					HSSFCell cell11 = row.createCell(column++);
					cell11.setCellValue(new HSSFRichTextString(corr
							.getTipoEnvio()));
					cell11.setCellStyle(styleAllWithBorder);

					HSSFCell cell12 = row.createCell(column++);
					cell12.setCellValue(new HSSFRichTextString(corr.getOblea()));
					cell12.setCellStyle(styleAllWithBorder);
				}

				if (destino.equals("ENTRANTE")) {
					HSSFCell cell14 = row.createCell(column++);
					cell14.setCellValue(new HSSFRichTextString(corr
							.isGastoSeccional() ? "X" : ""));
					cell14.setCellStyle(styleAllWithBorder);
					HSSFCell cell15 = row.createCell(column++);
					cell15.setCellValue(new HSSFRichTextString(corr
							.isReintegro() ? "X" : ""));
					cell15.setCellStyle(styleAllWithBorder);
					HSSFCell cell16 = row.createCell(column++);
					cell16.setCellValue(new HSSFRichTextString(corr
							.isPadrones() ? "X" : ""));
					cell16.setCellStyle(styleAllWithBorder);
					HSSFCell cell17 = row.createCell(column++);
					cell17.setCellValue(new HSSFRichTextString(corr
							.isDiscapacidad() ? "X" : ""));
					cell17.setCellStyle(styleAllWithBorder);
					HSSFCell cell18 = row.createCell(column++);
					cell18.setCellValue(new HSSFRichTextString(
							corr.isOtros() ? "X" : ""));
					cell18.setCellStyle(styleAllWithBorder);
					HSSFCell cell19 = row.createCell(column++);
					cell19.setCellValue(new HSSFRichTextString(corr
							.isFacturacion() ? "X" : ""));
					cell19.setCellStyle(styleAllWithBorder);
					HSSFCell cell20 = row.createCell(column++);
					cell20.setCellValue(new HSSFRichTextString(corr
							.isDocumentacion() ? "X" : ""));
					cell20.setCellStyle(styleAllWithBorder);
				}

				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(corr.getAltaUsr()));
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
			sheet.autoSizeColumn((short) 14);

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			String destino, String lugarRecepcion, Date desde, Date hasta,
			String razonPrestador, int provincia, int localidad, int seccional) {
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte de Correspondencia del " + sdf.format(desde) + " al "
						+ sdf.format(hasta)));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Destino: ");
		aux.append(destino).append(" Lugar Recepción: ").append(lugarRecepcion)
				.append("Razón Soc. Prestador: " + razonPrestador);
		aux.append(" Provincia: ").append(provincia).append(" Localidad: ")
				.append(localidad).append(" Seccional: ").append(seccional);
		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("ID"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("DESTINO"));
		cell21.setCellStyle(styleHeaderEnca2);

		if (destino.equals("SALIENTE")) {
			HSSFCell cell22 = row3a.createCell(column++);
			cell22.setCellValue(new HSSFRichTextString("FECHA ENVIO"));
			cell22.setCellStyle(styleHeaderEnca2);
		} else if (destino.equals("ENTRANTE")) {
			HSSFCell cell23 = row3a.createCell(column++);
			cell23.setCellValue(new HSSFRichTextString("FECHA RECEPCION"));
			cell23.setCellStyle(styleHeaderEnca2);
			HSSFCell cell24 = row3a.createCell(column++);
			cell24.setCellValue(new HSSFRichTextString("EDIFICIO RECEPCION"));
			cell24.setCellStyle(styleHeaderEnca2);
		}

		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("SECCIONAL REMITENTE"));
		cell25.setCellStyle(styleHeaderEnca2);

		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("TIPO REMITENTE"));
		cell26.setCellStyle(styleHeaderEnca2);

		if (destino.equals("SALIENTE")) {
			HSSFCell cell27 = row3a.createCell(column++);
			cell27.setCellValue(new HSSFRichTextString("EDIFICIO DESTINO"));
			cell27.setCellStyle(styleHeaderEnca2);
		}

		HSSFCell cell28 = row3a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("PROVINCIA"));
		cell28.setCellStyle(styleHeaderEnca2);

		HSSFCell cell29 = row3a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		cell29.setCellStyle(styleHeaderEnca2);

		if (destino.equals("SALIENTE")) {
			HSSFCell cell31 = row3a.createCell(column++);
			cell31.setCellValue(new HSSFRichTextString(
					"RAZON PRESTADOR DESTINATARIO"));
			cell31.setCellStyle(styleHeaderEnca2);
		} else if (destino.equals("ENTRANTE")) {
			HSSFCell cell30 = row3a.createCell(column++);
			cell30.setCellValue(new HSSFRichTextString("RAZON PRESTADOR RTTE"));
			cell30.setCellStyle(styleHeaderEnca2);
			HSSFCell cell301 = row3a.createCell(column++);
			cell301.setCellValue(new HSSFRichTextString("COD. FARMACIA"));
			cell301.setCellStyle(styleHeaderEnca2);
			HSSFCell cell302 = row3a.createCell(column++);
			cell302.setCellValue(new HSSFRichTextString("FARMACIA"));
			cell302.setCellStyle(styleHeaderEnca2);
		}

		if (destino.equals("SALIENTE")) {
			HSSFCell cell32 = row3a.createCell(column++);
			cell32.setCellValue(new HSSFRichTextString("TIPO ENVIO"));
			cell32.setCellStyle(styleHeaderEnca2);
			HSSFCell cell33 = row3a.createCell(column++);
			cell33.setCellValue(new HSSFRichTextString("CODIGO OBLEA"));
			cell33.setCellStyle(styleHeaderEnca2);
		}

		if (destino.equals("ENTRANTE")) {
			HSSFCell cell35 = row3a.createCell(column++);
			cell35.setCellValue(new HSSFRichTextString("GASTOS SECC."));
			cell35.setCellStyle(styleHeaderEnca2);
			HSSFCell cell36 = row3a.createCell(column++);
			cell36.setCellValue(new HSSFRichTextString("REINTEGROS"));
			cell36.setCellStyle(styleHeaderEnca2);
			HSSFCell cell37 = row3a.createCell(column++);
			cell37.setCellValue(new HSSFRichTextString("PADRONES"));
			cell37.setCellStyle(styleHeaderEnca2);
			HSSFCell cell38 = row3a.createCell(column++);
			cell38.setCellValue(new HSSFRichTextString("DISCAPACIDAD"));
			cell38.setCellStyle(styleHeaderEnca2);
			HSSFCell cell39 = row3a.createCell(column++);
			cell39.setCellValue(new HSSFRichTextString("OTROS"));
			cell39.setCellStyle(styleHeaderEnca2);
			HSSFCell cell40 = row3a.createCell(column++);
			cell40.setCellValue(new HSSFRichTextString("FACTURACION"));
			cell40.setCellStyle(styleHeaderEnca2);
			HSSFCell cell41 = row3a.createCell(column++);
			cell41.setCellValue(new HSSFRichTextString("DOCUMENTACION"));
			cell41.setCellStyle(styleHeaderEnca2);

		}

		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("ALTA USR"));
		cell34.setCellStyle(styleHeaderEnca2);

		return index;
	}

	public static HSSFWorkbook generaReporteEntradasSalidasCorrespondencia(
			HttpServletRequest req, HttpServletResponse res, String tipoReporte) {

		HSSFWorkbook excel = null;

		HttpSession session = (HttpSession) req.getSession();
		
		completarEmpresasGrupos();
		
		List<ItemCorrespondencia> searchResult=null;

//		List<ItemCorrespondencia> searchResult =  (List<ItemCorrespondencia>) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
		
		String edificio = null, fechaDesdeFinal = null, fechaHastaFinal = null, tipo_registro = null, tipo_envio = null, tipo_remitente = null, cuil = null , id_farmacia = null,
			   otros = null, cuit_entidad = null, sucursal_entidad = null, tipo_compro = null, letra_compro = null, nro_compro = null, importe_total = null, edificio_destino = null,
			   usuario_destino = null, sector_destino = null, contenido = null, oblea = null, estado_item = null;
			   
		long numero_correspondencia = 0, paquete = 0, seguim_paquete = 0;
		int inte = 0, id_prestador = 0, id_seccional = 0, sucu = 0 ;
		Date fechaDesde = null; Date fechaHasta = null;

		edificio = ParamUtil.getString(req, "edificio",null);
		fechaDesdeFinal = ParamUtil.getString(req,"fechaDesdeFinal", null);
		fechaHastaFinal = ParamUtil.getString(req,"fechaHastaFinal", null);
		numero_correspondencia = ParamUtil.getLong(req,"numero_correspondencia", 0);
		tipo_registro = ParamUtil.getString(req,"tipo_registro", null);
		paquete = ParamUtil.getInteger(req, "paquete", 0);
		seguim_paquete = ParamUtil.getInteger(req, "seguim_paquete", 0);
		tipo_envio = ParamUtil.getString(req,"tipo_envio", null);
		tipo_remitente = ParamUtil.getString(req,"tipo_remitente", null);
		cuil = ParamUtil.getString(req, "cuil", null);
		inte = ParamUtil.getInteger(req, "inte", 0);
		id_farmacia = ParamUtil.getString(req,"id_farmacia", null);
		otros = ParamUtil.getString(req, "otros", null);
		id_prestador = ParamUtil.getInteger(req,"id_prestador", 0);
		cuit_entidad = ParamUtil.getString(req,"cuit_entidad", null);
		sucursal_entidad = ParamUtil.getString(req,"sucursal_entidad", null);
		id_seccional = ParamUtil.getInteger(req,"id_seccional", 0);
		tipo_compro = ParamUtil.getString(req,"comprobante_tipo", null);
		letra_compro = ParamUtil.getString(req,"comprobante_letra", null);
		sucu = ParamUtil.getInteger(req, "sucu", 0);
		nro_compro = ParamUtil.getString(req,"comprobante_nro", null);
		importe_total = ParamUtil.getString(req,"importe_total", null);
		edificio_destino = ParamUtil.getString(req,"edificio_destino", null);
		usuario_destino = ParamUtil.getString(req,"usuario_destino", null);
		sector_destino = ParamUtil.getString(req,"sector_destino", null);
		contenido = ParamUtil.getString(req, "contenido",null);
		oblea = ParamUtil.getString(req, "nro_oblea",null);
		estado_item = ParamUtil.getString(req, "estado_item",null);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			fechaDesde = sdf.parse(fechaDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		try {
			fechaHasta = sdf.parse(fechaHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}				
//		if(searchResult == null || searchResult.size() == 0){
				try {
					searchResult = CorrespondenciaServiceImpl.buscarCorrespondencia(edificio, fechaDesde, fechaHasta,
									numero_correspondencia, tipo_registro, paquete, seguim_paquete, tipo_envio, tipo_remitente, cuil, 
									inte, id_farmacia, otros, id_prestador, cuit_entidad, sucursal_entidad, id_seccional, 
									tipo_compro, letra_compro, sucu, nro_compro, importe_total, edificio_destino, 
									usuario_destino, sector_destino, contenido, oblea, estado_item);

				} catch (SystemException e) {
					_log.error("Error al generar reporte Entradas/Salidas", e);
					return excel;
				}
//		}
		// con los datos sean recuperados de la session o armando una busqueda con lso filtros seleccionados
		// armamos el Excel.
		try {
			
//			Recuperar las descripciones de elementos de Liferay
			if(!StringUtils.checkEmpty(edificio)){
				edificio = empresaHM.get(edificio);
			}
			if(!StringUtils.checkEmpty(edificio_destino)){
				edificio_destino = empresaHM.get(edificio_destino);
			}
			if(!StringUtils.checkEmpty(sector_destino)){
				sector_destino = grupoHM.get(sector_destino);
			}
			if(tipoReporte.equalsIgnoreCase("NORMAL")){
				excel = armarXlsEntradasSalidasCorrespondencia(edificio, fechaDesde, fechaHasta,
						numero_correspondencia, tipo_registro, paquete, tipo_envio, tipo_remitente, cuil, 
						inte, id_farmacia, otros, id_prestador, cuit_entidad, sucursal_entidad, id_seccional, 
						tipo_compro, letra_compro, sucu, nro_compro, importe_total, edificio_destino, 
						usuario_destino, sector_destino, contenido, oblea, estado_item, searchResult);
			}else if(tipoReporte.equalsIgnoreCase("EMPAQUETADO")){
				excel = armarXlsCorreoEmpaquetado(edificio, tipo_registro, paquete, tipo_envio, tipo_remitente, edificio_destino, 
						usuario_destino, sector_destino, contenido, estado_item, searchResult);

			}	
		} catch (Exception e) {
			_log.error("Error al generar reporte Entradas/Salidas", e);
			return excel; 
		}
		
		return excel;
	}
	private static HSSFWorkbook armarXlsEntradasSalidasCorrespondencia(String edificio, Date fechaDesde, Date fechaHasta,
			long numeroCorrespondencia, String tipoRegistro, long paquete, String tipoEnvio, String tipoRemitente, String cuil, 
			int inte, String idFarmacia, String otros, int idPrestador, String cuitEntidad, String sucursalEntidad, int idSeccional, 
			String tipoCompro,String letraCompro, int sucu, String nroCompro, String importeTotal, String edificioDestino, 
			String usuarioDestino, String sectorDestino, String contenido, String oblea, String estadoItem,
			List<ItemCorrespondencia> searchResult) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = crearHeaderEntradasSalidasCorrespondencia(wb, sheet, edificio, fechaDesde, fechaHasta,
					numeroCorrespondencia, tipoRegistro, paquete, tipoEnvio, tipoRemitente, cuil, 
					inte, idFarmacia, otros, idPrestador, cuitEntidad, sucursalEntidad, idSeccional, 
					tipoCompro, letraCompro, sucu, nroCompro, importeTotal, edificioDestino, 
					usuarioDestino, sectorDestino, contenido, oblea, estadoItem);
			
			index++;
			
			for (Iterator<ItemCorrespondencia> iterator = searchResult.iterator(); iterator.hasNext();) {
				ItemCorrespondencia ic = iterator.next();
				
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(ic.getId());
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(ic.getCabecera().getFechaAsString()));
				cell1.setCellStyle(styleAllWithBorder);

				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(ic.getCabecera().getTipoRegistro()));
				cell2.setCellStyle(styleAllWithBorder);

				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(ic.getCabecera().getTipoEnvio()));
				cell3.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(ic.getEstado()));
				cell4.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(ic.getContenido()));
				cell5.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(ic.getTipoRemitenteDestinatario()));
				cell6.setCellStyle(styleAllWithBorder);

				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(ic.getEdificioDescripcion()));
				cell7.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(ic.getSectorDescripcion()));
				cell8.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(ic.getUsuario()));
				cell9.setCellStyle(styleAllWithBorder);

				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(ic.getAfiliado().getNombre()));
				cell10.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(ic.getAfiliado().getApellido()));
				cell11.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(ic.getFarmacia().getCodigoFarmacia()));
				cell12.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(ic.getFarmacia().getDescripcion()));
				cell13.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(ic.getOtro()));
				cell14.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(ic.getPrestador().getCuit()));
				cell15.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(ic.getPrestador().getDescripcion()));
				cell16.setCellStyle(styleAllWithBorder);

				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(new HSSFRichTextString(ic.getProveedor().getCuit()));
				cell17.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell18 = row.createCell(column++);
				cell18.setCellValue(new HSSFRichTextString(ic.getProveedor().getDescripcion()));
				cell18.setCellStyle(styleAllWithBorder);

				HSSFCell cell19 = row.createCell(column++);
				cell19.setCellValue(new HSSFRichTextString(String.valueOf(ic.getSeccional().getIdSeccional())));
				cell19.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell20 = row.createCell(column++);
				cell20.setCellValue(new HSSFRichTextString(ic.getSeccional().getDescripcion()));
				cell20.setCellStyle(styleAllWithBorder);

				HSSFCell cell21 = row.createCell(column++);
				cell21.setCellValue(new HSSFRichTextString(ic.getEmpresa_rem_Descripcion()));
				cell21.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell22 = row.createCell(column++);
				cell22.setCellValue(new HSSFRichTextString(ic.getUsuario_remite()));
				cell22.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell23 = row.createCell(column++);
				cell23.setCellValue(new HSSFRichTextString(String.valueOf(ic.getCompro_sucu())));
				cell23.setCellStyle(styleAllWithBorder);

				HSSFCell cell24 = row.createCell(column++);
				cell24.setCellValue(new HSSFRichTextString(ic.getCompro_tipo()));
				cell24.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell25 = row.createCell(column++);
				cell25.setCellValue(new HSSFRichTextString(ic.getCompro_letra()));
				cell25.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell26 = row.createCell(column++);
				cell26.setCellValue(new HSSFRichTextString(ic.getCompro_nro()));
				cell26.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell27 = row.createCell(column++);
				cell27.setCellValue(new HSSFRichTextString(String.valueOf(ic.getImporte())));
				cell27.setCellStyle(styleAllWithBorder);

				HSSFCell cell28 = row.createCell(column++);
				cell28.setCellValue(new HSSFRichTextString(String.valueOf(ic.getFecha_emision())));
				cell28.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell29 = row.createCell(column++);
				cell29.setCellValue(new HSSFRichTextString(String.valueOf(ic.getFecha_vencimiento())));
				cell29.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell30 = row.createCell(column++);
				cell30.setCellValue(new HSSFRichTextString(String.valueOf(ic.getAlta_fecha())));
				cell30.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell31 = row.createCell(column++);
				cell31.setCellValue(new HSSFRichTextString(String.valueOf(ic.getAlta_usr())));
				cell31.setCellStyle(styleAllWithBorder);
				
				
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
			sheet.autoSizeColumn((short) 24);
			sheet.autoSizeColumn((short) 25);
			sheet.autoSizeColumn((short) 26);
			sheet.autoSizeColumn((short) 27);
			sheet.autoSizeColumn((short) 28);
			sheet.autoSizeColumn((short) 29);
			sheet.autoSizeColumn((short) 30);
			sheet.autoSizeColumn((short) 31);

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int crearHeaderEntradasSalidasCorrespondencia(HSSFWorkbook wb, HSSFSheet sheet,
			String edificio, Date fechaDesde, Date fechaHasta,
			long numeroCorrespondencia, String tipoRegistro, long paquete, String tipoEnvio, String tipoRemitente, String cuil, 
			int inte, String idFarmacia, String otros, int idPrestador, String cuitEntidad, String sucursalEntidad, int idSeccional, 
			String tipoCompro,String letraCompro, int sucu, String nroCompro, String importeTotal, String edificioDestino, 
			String usuarioDestino, String sectorDestino, String contenido, String oblea, String estadoItem) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte de Entradas/Salidas de Correspondencia del " + 
				sdf.format(fechaDesde) + " al " + sdf.format(fechaHasta)));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer parametrosBusqueda = new StringBuffer("Parametros:");

		if(!StringUtils.checkEmpty(edificio)){
			parametrosBusqueda.append(" Edificio: "+edificio);
		}
		if(!StringUtils.checkEmpty(tipoRegistro)){
			parametrosBusqueda.append(" T.Registro: "+tipoRegistro);
		}
		if(!StringUtils.checkEmpty(tipoEnvio)){
			parametrosBusqueda.append(" T.Envio: "+tipoEnvio);
		}
		if(!StringUtils.checkEmpty(tipoRemitente)){
			parametrosBusqueda.append(" T.Remitente: "+tipoRemitente);
		}
		if(!StringUtils.checkEmpty(cuil)){
			parametrosBusqueda.append(" Cuil: "+cuil);
		}
		if(!StringUtils.checkEmpty(idFarmacia)){
			parametrosBusqueda.append(" Farmacia ID: "+idFarmacia);
		}
		if(!StringUtils.checkEmpty(otros)){
			parametrosBusqueda.append(" Otros: "+otros);
		}
		if(!StringUtils.checkEmpty(cuitEntidad)){
			parametrosBusqueda.append(" Entidad Cuit: "+cuitEntidad);
		}
		if(!StringUtils.checkEmpty(sucursalEntidad)){
			parametrosBusqueda.append(" Entidad Sucursal: "+sucursalEntidad);
		}
		if(!StringUtils.checkEmpty(tipoCompro) || !StringUtils.checkEmpty(nroCompro) || !StringUtils.checkEmpty(letraCompro) || !StringUtils.checkEmpty(importeTotal) ){
			parametrosBusqueda.append(" Comprobante: "+tipoCompro+" "+letraCompro + " " + sucu + " " + nroCompro + " " + importeTotal);
		}
		if(!StringUtils.checkEmpty(edificioDestino) || !StringUtils.checkEmpty(usuarioDestino) || !StringUtils.checkEmpty(sectorDestino) ){
			parametrosBusqueda.append(" Destino: "+edificioDestino+"/"+sectorDestino+"/"+usuarioDestino);
		}
		if(!StringUtils.checkEmpty(contenido)){
			parametrosBusqueda.append(" Contenido: "+contenido);
		}
		if(!StringUtils.checkEmpty(estadoItem)){
			parametrosBusqueda.append(" Estado Item: "+estadoItem);
		}
		if(!StringUtils.checkEmpty(oblea)){
			parametrosBusqueda.append(" Oblea: "+oblea);
		}
		if(paquete!= 0){
			parametrosBusqueda.append(" Paquete N°: "+paquete);
		}
		
		cell1.setCellValue(new HSSFRichTextString(parametrosBusqueda.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;
		
		HSSFCell cell200 = row3a.createCell(column++);
		cell200.setCellValue(new HSSFRichTextString("Nro."));
		cell200.setCellStyle(styleHeaderEnca2);

		HSSFCell cell201 = row3a.createCell(column++);
		cell201.setCellValue(new HSSFRichTextString("F.Recepción"));
		cell201.setCellStyle(styleHeaderEnca2);

		HSSFCell cell202 = row3a.createCell(column++);
		cell202.setCellValue(new HSSFRichTextString("Tipo Reg."));
		cell202.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell203 = row3a.createCell(column++);
		cell203.setCellValue(new HSSFRichTextString("Tipo Envío"));
		cell203.setCellStyle(styleHeaderEnca2);

		HSSFCell cell204 = row3a.createCell(column++);
		cell204.setCellValue(new HSSFRichTextString("Estado"));
		cell204.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell205 = row3a.createCell(column++);
		cell205.setCellValue(new HSSFRichTextString("Contenido"));
		cell205.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell206 = row3a.createCell(column++);
		cell206.setCellValue(new HSSFRichTextString("Tipo Dest."));
		cell206.setCellStyle(styleHeaderEnca2);

		HSSFCell cell207 = row3a.createCell(column++);
		cell207.setCellValue(new HSSFRichTextString("Edificio Dest."));
		cell207.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell208 = row3a.createCell(column++);
		cell208.setCellValue(new HSSFRichTextString("Sector Dest."));
		cell208.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell209 = row3a.createCell(column++);
		cell209.setCellValue(new HSSFRichTextString("Usuario Dest."));
		cell209.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell210 = row3a.createCell(column++);
		cell210.setCellValue(new HSSFRichTextString("Apellido Afiliado"));
		cell210.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell211 = row3a.createCell(column++);
		cell211.setCellValue(new HSSFRichTextString("Nombre Afiliado"));
		cell211.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell212 = row3a.createCell(column++);
		cell212.setCellValue(new HSSFRichTextString("Cod. Farmacia"));
		cell212.setCellStyle(styleHeaderEnca2);

		HSSFCell cell213 = row3a.createCell(column++);
		cell213.setCellValue(new HSSFRichTextString("Descrip. Farmacia"));
		cell213.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell214 = row3a.createCell(column++);
		cell214.setCellValue(new HSSFRichTextString("Descrip. Otros"));
		cell214.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell215 = row3a.createCell(column++);
		cell215.setCellValue(new HSSFRichTextString("CUIT Prestador"));
		cell215.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell216 = row3a.createCell(column++);
		cell216.setCellValue(new HSSFRichTextString("Nombre Prestador"));
		cell216.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell217 = row3a.createCell(column++);
		cell217.setCellValue(new HSSFRichTextString("CUIT Proveedor"));
		cell217.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell218 = row3a.createCell(column++);
		cell218.setCellValue(new HSSFRichTextString("Nombre Proveedor"));
		cell218.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell219 = row3a.createCell(column++);
		cell219.setCellValue(new HSSFRichTextString("Id. Seccional"));
		cell219.setCellStyle(styleHeaderEnca2);

		HSSFCell cell220 = row3a.createCell(column++);
		cell220.setCellValue(new HSSFRichTextString("Nombre Seccional"));
		cell220.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell221 = row3a.createCell(column++);
		cell221.setCellValue(new HSSFRichTextString("Edificio Remit."));
		cell221.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell222 = row3a.createCell(column++);
		cell222.setCellValue(new HSSFRichTextString("Usuario Remit."));
		cell222.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell223 = row3a.createCell(column++);
		cell223.setCellValue(new HSSFRichTextString("Sucursal"));
		cell223.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell224 = row3a.createCell(column++);
		cell224.setCellValue(new HSSFRichTextString("Tipo"));
		cell224.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell225 = row3a.createCell(column++);
		cell225.setCellValue(new HSSFRichTextString("Letra"));
		cell225.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell226 = row3a.createCell(column++);
		cell226.setCellValue(new HSSFRichTextString("Número"));
		cell226.setCellStyle(styleHeaderEnca2);
	
		HSSFCell cell227 = row3a.createCell(column++);
		cell227.setCellValue(new HSSFRichTextString("Importe"));
		cell227.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell228 = row3a.createCell(column++);
		cell228.setCellValue(new HSSFRichTextString("F.Emisión"));
		cell228.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell229 = row3a.createCell(column++);
		cell229.setCellValue(new HSSFRichTextString("F.Vencimiento"));
		cell229.setCellStyle(styleHeaderEnca2);

		HSSFCell cell230 = row3a.createCell(column++);
		cell230.setCellValue(new HSSFRichTextString("F. Alta"));
		cell230.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell231 = row3a.createCell(column++);
		cell231.setCellValue(new HSSFRichTextString("Usuario Alta"));
		cell231.setCellStyle(styleHeaderEnca2);
		
		return index;
	}

	private static void completarEmpresasGrupos() {
		empresaHM = new HashMap<String,String>();
		grupoHM = new HashMap<String,String>();
		List<Organization> empresas = null;
		List<UserGroup> grupos = null;
		try {
			empresas = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for (Iterator<Organization> iterator = empresas.iterator(); iterator.hasNext();) {
			Organization org = iterator.next();
			empresaHM.put(String.valueOf(org.getOrganizationId()), org.getName());
		}
		
		try {
			grupos = UserGroupLocalServiceUtil.getUserGroups(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for (Iterator<UserGroup> iterator = grupos.iterator(); iterator.hasNext();) {
			UserGroup usrGrp = (UserGroup) iterator.next();
			grupoHM.put(String.valueOf(usrGrp.getUserGroupId()), usrGrp.getName());
		}
	}	
	
//	private static HSSFWorkbook armarXlsCorreoEmpaquetado(String edificio, Date fechaDesde, Date fechaHasta,
//			long numero_correspondencia, String tipo_registro, long paquete, String tipo_envio, String tipo_remitente, String cuil, 
//			int inte, String id_farmacia, String otros, int id_prestador, String cuit_entidad, String sucursal_entidad, int id_seccional, 
//			String tipo_compro,String letra_compro, int sucu, String nro_compro, String importe_total, String edificio_destino, 
//			String usuario_destino, String sector_destino, String contenido, String oblea, String estado_item,
//			List<ItemCorrespondencia> searchResult) {
	private static HSSFWorkbook armarXlsCorreoEmpaquetado(String edificio, String tipoRegistro, long paquete, String tipoEnvio, 
			String tipoRemitente, String edificioDestino, String usuarioDestino, String sectorDestino, String contenido, 
			String estadoItem, List<ItemCorrespondencia> searchResult) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 8);
		HSSFCellStyle styleAllWithBorderWrap = getStyleAllWithBorder(wb, 8);
//		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);
//		HSSFCellStyle styleAllWithBorderWrap = getStyleAllWithBorder(wb, 10);
		styleAllWithBorderWrap.setWrapText(true);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
//		sheet.setDefaultColumnWidth(55);
		
		String paqDescripcion = "";
		
		try {
			paqDescripcion = searchResult.get(0).getListaPaquete().getPaq_descripcion();
			
			paqDescripcion = StringUtils.checkEmpty(paqDescripcion)?"-":paqDescripcion;
			
			int index = crearHeaderCorreoEmpaquetado(wb, sheet, edificio, null, null, 0, tipoRegistro, paquete, tipoEnvio, 
					tipoRemitente, "", 0, "", "", 0, "", "", 0, "", "", 0, "", "", edificioDestino, usuarioDestino, sectorDestino, 
					contenido, "", estadoItem, paqDescripcion);
			
			index++;
			
			for (Iterator<ItemCorrespondencia> iterator = searchResult.iterator(); iterator.hasNext();) {
				ItemCorrespondencia ic = iterator.next();
				
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(ic.getId());
				cell0.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(0, 1500); 

				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(ic.getCabecera().getFechaAsString()));
				cell1.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(1, 2300); 

//				HSSFCell cell2 = row.createCell(column++);
//				cell2.setCellValue(new HSSFRichTextString(ic.getCabecera().getTipoRegistro()));
//				cell2.setCellStyle(styleAllWithBorder);

//				HSSFCell cell3 = row.createCell(column++);
//				cell3.setCellValue(new HSSFRichTextString(ic.getCabecera().getTipoEnvio()));
//				cell3.setCellStyle(styleAllWithBorder);
				
//				HSSFCell cell4 = row.createCell(column++);
//				cell4.setCellValue(new HSSFRichTextString(ic.getEstado()));
//				cell4.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell5 = row.createCell(column++);
//	            sheet.setColumnWidth(column-1, 6000); //Set column width, you'll probably want to tweak the second int

				cell5.setCellValue(new HSSFRichTextString(ic.getContenido()));
				cell5.setCellStyle(styleAllWithBorderWrap);
				sheet.setColumnWidth(2, 10000); 

//				HSSFCell cell6 = row.createCell(column++);
//				cell6.setCellValue(new HSSFRichTextString(ic.getTipoRemitenteDestinatario()));
//				cell6.setCellStyle(styleAllWithBorder);
//
//				HSSFCell cell7 = row.createCell(column++);
//				cell7.setCellValue(new HSSFRichTextString(ic.getEdificioDescripcion()));
//				cell7.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell8 = row.createCell(column++);
//				cell8.setCellValue(new HSSFRichTextString(ic.getSectorDescripcion()));
//				cell8.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell9 = row.createCell(column++);
//				cell9.setCellValue(new HSSFRichTextString(ic.getUsuario()));
//				cell9.setCellStyle(styleAllWithBorder);
//
//				HSSFCell cell10 = row.createCell(column++);
//				cell10.setCellValue(new HSSFRichTextString(ic.getAfiliado().getNombre()));
//				cell10.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell11 = row.createCell(column++);
//				cell11.setCellValue(new HSSFRichTextString(ic.getAfiliado().getApellido()));
//				cell11.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(ic.getFarmacia().getCodigoFarmacia()));
				cell12.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(3, 1300); 

				HSSFCell cell13 = row.createCell(column++);
//				sheet.setColumnWidth(column-1, 3000);
				cell13.setCellValue(new HSSFRichTextString(ic.getFarmacia().getDescripcion()));
				cell13.setCellStyle(styleAllWithBorderWrap);
				sheet.setColumnWidth(4, 8000); 

//				HSSFCell cell14 = row.createCell(column++);
//				cell14.setCellValue(new HSSFRichTextString(ic.getOtro()));
//				cell14.setCellStyle(styleAllWithBorder);
				
//				HSSFCell cell15 = row.createCell(column++);
//				cell15.setCellValue(new HSSFRichTextString(ic.getPrestador().getCuit()));
//				cell15.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell16 = row.createCell(column++);
//				cell16.setCellValue(new HSSFRichTextString(ic.getPrestador().getDescripcion()));
//				cell16.setCellStyle(styleAllWithBorder);
//
//				HSSFCell cell17 = row.createCell(column++);
//				cell17.setCellValue(new HSSFRichTextString(ic.getProveedor().getCuit()));
//				cell17.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell18 = row.createCell(column++);
//				cell18.setCellValue(new HSSFRichTextString(ic.getProveedor().getDescripcion()));
//				cell18.setCellStyle(styleAllWithBorder);
//
//				HSSFCell cell19 = row.createCell(column++);
//				cell19.setCellValue(new HSSFRichTextString(String.valueOf(ic.getSeccional().getIdSeccional())));
//				cell19.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell20 = row.createCell(column++);
//				cell20.setCellValue(new HSSFRichTextString(ic.getSeccional().getDescripcion()));
//				cell20.setCellStyle(styleAllWithBorder);
//
//				HSSFCell cell21 = row.createCell(column++);
//				cell21.setCellValue(new HSSFRichTextString(ic.getEmpresa_rem_Descripcion()));
//				cell21.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell22 = row.createCell(column++);
//				cell22.setCellValue(new HSSFRichTextString(ic.getUsuario_remite()));
//				cell22.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell23 = row.createCell(column++);
				cell23.setCellValue(new HSSFRichTextString(String.valueOf(ic.getCompro_sucu())));
				cell23.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(5, 1200); 

				HSSFCell cell24 = row.createCell(column++);
				cell24.setCellValue(new HSSFRichTextString(ic.getCompro_tipo()));
				cell24.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(6, 1200); 
				
				HSSFCell cell25 = row.createCell(column++);
				cell25.setCellValue(new HSSFRichTextString(ic.getCompro_letra()));
				cell25.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(7, 500); 

				HSSFCell cell26 = row.createCell(column++);
				cell26.setCellValue(new HSSFRichTextString(ic.getCompro_nro()));
				cell26.setCellStyle(styleAllWithBorder);
				sheet.setColumnWidth(8, 2000); 

				HSSFCell cell27 = row.createCell(column++);
				cell27.setCellValue(new HSSFRichTextString(String.valueOf(ic.getImporte())));
				cell27.setCellStyle(styleAllWithBorder);

//				HSSFCell cell28 = row.createCell(column++);
//				cell28.setCellValue(new HSSFRichTextString(String.valueOf(ic.getFecha_emision())));
//				cell28.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell29 = row.createCell(column++);
//				cell29.setCellValue(new HSSFRichTextString(String.valueOf(ic.getFecha_vencimiento())));
//				cell29.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell30 = row.createCell(column++);
//				cell30.setCellValue(new HSSFRichTextString(String.valueOf(ic.getAlta_fecha())));
//				cell30.setCellStyle(styleAllWithBorder);
//				
//				HSSFCell cell31 = row.createCell(column++);
//				cell31.setCellValue(new HSSFRichTextString(String.valueOf(ic.getAlta_usr())));
//				cell31.setCellStyle(styleAllWithBorder);
				
//				vamos a calcular x la long de la descripcion, si corresponde ajustar alto de fila.
//				dividimos x la cant de caracteres q creemos q entra en el ancho y revisamos el resto de division p sumar 1 toke mas
				int rowHeight = 0;
				int contenidoHeight = (ic.getContenido().length() / 50)+(
						(ic.getContenido().length() % 50)>0?1:0) ;
						
				int descFarmaciaHeight = (ic.getFarmacia().getDescripcion().length() / 35)+(
						(ic.getFarmacia().getDescripcion().length() % 35)>0?1:0) ;
						
				
				if(contenidoHeight >= descFarmaciaHeight){
					rowHeight = contenidoHeight;
				}else{
					rowHeight = descFarmaciaHeight ;
				}
						
				row.setHeight((short)(row.getHeight() * rowHeight)); 
				
			}

//			sheet.autoSizeColumn((short) 0);
//			sheet.autoSizeColumn((short) 1);
////			sheet.autoSizeColumn((short) 2); no la ajustamos xq el contenido es largo y le metemos wrap text
//			sheet.autoSizeColumn((short) 3);
////			sheet.autoSizeColumn((short) 4);
//			sheet.autoSizeColumn((short) 5); 
//			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);
			sheet.autoSizeColumn((short) 8); 
			sheet.autoSizeColumn((short) 9);
			sheet.autoSizeColumn((short) 10);
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}
	
	private static int crearHeaderCorreoEmpaquetado(HSSFWorkbook wb, HSSFSheet sheet,
			String edificio, Date fechaDesde, Date fechaHasta,
			long numero_correspondencia, String tipo_registro, long paquete, String tipo_envio, String tipo_remitente, String cuil, 
			int inte, String id_farmacia, String otros, int id_prestador, String cuit_entidad, String sucursal_entidad, int id_seccional, 
			String tipo_compro,String letra_compro, int sucu, String nro_compro, String importe_total, String edificio_destino, 
			String usuario_destino, String sector_destino, String contenido, String oblea, String estado_item, String paq_descripcion) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte de Correspondencias Empaquetados " /*+ 
				sdf.format(fechaDesde) + " al " + sdf.format(fechaHasta)*/));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer parametrosBusqueda = new StringBuffer("Parametros:");

		if(!StringUtils.checkEmpty(edificio)){
			parametrosBusqueda.append(" Edificio: "+edificio);
		}
		if(!StringUtils.checkEmpty(tipo_registro)){
			parametrosBusqueda.append(" T.Registro: "+tipo_registro);
		}
		if(!StringUtils.checkEmpty(tipo_envio)){
			parametrosBusqueda.append(" T.Envio: "+tipo_envio);
		}
		if(!StringUtils.checkEmpty(tipo_remitente)){
			parametrosBusqueda.append(" T.Remitente: "+tipo_remitente);
		}
		if(!StringUtils.checkEmpty(cuil)){
			parametrosBusqueda.append(" Cuil: "+cuil);
		}
		if(!StringUtils.checkEmpty(id_farmacia)){
			parametrosBusqueda.append(" Farmacia ID: "+id_farmacia);
		}
		if(!StringUtils.checkEmpty(otros)){
			parametrosBusqueda.append(" Otros: "+otros);
		}
		if(!StringUtils.checkEmpty(cuit_entidad)){
			parametrosBusqueda.append(" Entidad Cuit: "+cuit_entidad);
		}
		if(!StringUtils.checkEmpty(sucursal_entidad)){
			parametrosBusqueda.append(" Entidad Sucursal: "+sucursal_entidad);
		}
		if(!StringUtils.checkEmpty(tipo_compro) || !StringUtils.checkEmpty(nro_compro) || !StringUtils.checkEmpty(letra_compro) || !StringUtils.checkEmpty(importe_total) ){
			parametrosBusqueda.append(" Comprobante: "+tipo_compro+" "+letra_compro + " " + sucu + " " + nro_compro + " " + importe_total);
		}
		if(!StringUtils.checkEmpty(edificio_destino) || !StringUtils.checkEmpty(usuario_destino) || !StringUtils.checkEmpty(sector_destino) ){
			parametrosBusqueda.append(" Destino: "+edificio_destino+"/"+sector_destino+"/"+usuario_destino);
		}
		if(!StringUtils.checkEmpty(contenido)){
			parametrosBusqueda.append(" Contenido: "+contenido);
		}
		if(!StringUtils.checkEmpty(estado_item)){
			parametrosBusqueda.append(" Estado Item: "+estado_item);
		}
		if(!StringUtils.checkEmpty(oblea)){
			parametrosBusqueda.append(" Oblea: "+oblea);
		}
		if(paquete!= 0){
			parametrosBusqueda.append(" Paquete N°: "+paquete);
		}
		if(!StringUtils.checkEmpty(paq_descripcion)){
			parametrosBusqueda.append(" Descripción: "+paq_descripcion);
		}
		
		cell1.setCellValue(new HSSFRichTextString(parametrosBusqueda.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

//		index = index + 2;
		index++;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;
		
		HSSFCell cell200 = row3a.createCell(column++);
		cell200.setCellValue(new HSSFRichTextString("Nro."));
		cell200.setCellStyle(styleHeaderEnca2);

		HSSFCell cell201 = row3a.createCell(column++);
		cell201.setCellValue(new HSSFRichTextString("F.Recep.")); //F.Recepción
		cell201.setCellStyle(styleHeaderEnca2);

//		HSSFCell cell202 = row3a.createCell(column++);
//		cell202.setCellValue(new HSSFRichTextString("Tipo Reg."));
//		cell202.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell203 = row3a.createCell(column++);
//		cell203.setCellValue(new HSSFRichTextString("Tipo Envío"));
//		cell203.setCellStyle(styleHeaderEnca2);
//
//		HSSFCell cell204 = row3a.createCell(column++);
//		cell204.setCellValue(new HSSFRichTextString("Estado"));
//		cell204.setCellStyle(styleHeaderEnca2);
//		
		HSSFCell cell205 = row3a.createCell(column++);
		cell205.setCellValue(new HSSFRichTextString("Contenido"));
		cell205.setCellStyle(styleHeaderEnca2);
		
//		HSSFCell cell206 = row3a.createCell(column++);
//		cell206.setCellValue(new HSSFRichTextString("Tipo Dest."));
//		cell206.setCellStyle(styleHeaderEnca2);
//
//		HSSFCell cell207 = row3a.createCell(column++);
//		cell207.setCellValue(new HSSFRichTextString("Edificio Dest."));
//		cell207.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell208 = row3a.createCell(column++);
//		cell208.setCellValue(new HSSFRichTextString("Sector Dest."));
//		cell208.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell209 = row3a.createCell(column++);
//		cell209.setCellValue(new HSSFRichTextString("Usuario Dest."));
//		cell209.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell210 = row3a.createCell(column++);
//		cell210.setCellValue(new HSSFRichTextString("Apellido Afiliado"));
//		cell210.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell211 = row3a.createCell(column++);
//		cell211.setCellValue(new HSSFRichTextString("Nombre Afiliado"));
//		cell211.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell212 = row3a.createCell(column++);
		cell212.setCellValue(new HSSFRichTextString("Cod.")); //Farmacia
		cell212.setCellStyle(styleHeaderEnca2);

		HSSFCell cell213 = row3a.createCell(column++);
		cell213.setCellValue(new HSSFRichTextString("Descrip. Farmacia"));
		cell213.setCellStyle(styleHeaderEnca2);
		
//		HSSFCell cell214 = row3a.createCell(column++);
//		cell214.setCellValue(new HSSFRichTextString("Descrip. Otros"));
//		cell214.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell215 = row3a.createCell(column++);
//		cell215.setCellValue(new HSSFRichTextString("CUIT Prestador"));
//		cell215.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell216 = row3a.createCell(column++);
//		cell216.setCellValue(new HSSFRichTextString("Nombre Prestador"));
//		cell216.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell217 = row3a.createCell(column++);
//		cell217.setCellValue(new HSSFRichTextString("CUIT Proveedor"));
//		cell217.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell218 = row3a.createCell(column++);
//		cell218.setCellValue(new HSSFRichTextString("Nombre Proveedor"));
//		cell218.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell219 = row3a.createCell(column++);
//		cell219.setCellValue(new HSSFRichTextString("Id. Seccional"));
//		cell219.setCellStyle(styleHeaderEnca2);
//
//		HSSFCell cell220 = row3a.createCell(column++);
//		cell220.setCellValue(new HSSFRichTextString("Nombre Seccional"));
//		cell220.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell221 = row3a.createCell(column++);
//		cell221.setCellValue(new HSSFRichTextString("Edificio Remit."));
//		cell221.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell222 = row3a.createCell(column++);
//		cell222.setCellValue(new HSSFRichTextString("Usuario Remit."));
//		cell222.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell223 = row3a.createCell(column++);
//		cell223.setCellValue(new HSSFRichTextString("Sucursal"));
		cell223.setCellValue(new HSSFRichTextString("Suc."));
		cell223.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell224 = row3a.createCell(column++);
		cell224.setCellValue(new HSSFRichTextString("Tipo"));
		cell224.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell225 = row3a.createCell(column++);
		cell225.setCellValue(new HSSFRichTextString("L")); //Letra
		cell225.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell226 = row3a.createCell(column++);
		cell226.setCellValue(new HSSFRichTextString("Número"));
		cell226.setCellStyle(styleHeaderEnca2);
	
		HSSFCell cell227 = row3a.createCell(column++);
		cell227.setCellValue(new HSSFRichTextString("Importe"));
		cell227.setCellStyle(styleHeaderEnca2);
		
//		HSSFCell cell228 = row3a.createCell(column++);
//		cell228.setCellValue(new HSSFRichTextString("F.Emisión"));
//		cell228.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell229 = row3a.createCell(column++);
//		cell229.setCellValue(new HSSFRichTextString("F.Vencimiento"));
//		cell229.setCellStyle(styleHeaderEnca2);
//
//		HSSFCell cell230 = row3a.createCell(column++);
//		cell230.setCellValue(new HSSFRichTextString("F. Alta"));
//		cell230.setCellStyle(styleHeaderEnca2);
//		
//		HSSFCell cell231 = row3a.createCell(column++);
//		cell231.setCellValue(new HSSFRichTextString("Usuario Alta"));
//		cell231.setCellStyle(styleHeaderEnca2);
//		
		return index;
	}
}
