package ar.com.uoma.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.RenderRequest;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.uoma.facturacion.BusquedaFacturasFiltro;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class ReporteFacturasExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil.getLog(ReporteFacturasExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) throws SystemException {
		
		
		BusquedaFacturasFiltro filtro = getCreaFiltroFactura(renderRequest);
		List<Factura> busqueda = FacturacionServiceUtil.getFacturas(filtro);
		
		
		
		/*
		Integer entidad = ParamUtil.getInteger(renderRequest, "entidad");
		String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechadesdedia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechadesdemes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechadesdeanio");
		String fechaHastaDia = ParamUtil.getString(renderRequest, "fechahastadia");
		String fechaHastaMes = ParamUtil.getString(renderRequest, "fechahastames");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(renderRequest, "fechahastaanio");
		String libro=ParamUtil.getString(renderRequest,"libro");
		String cuitEntidad="";
		
		
		Date fechaIni=new Date();
		Date fechaFin=new Date();
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
		} catch (Exception e) {
			_log.error("Error al generar reporte Libro de Iva", e);
			return null;
		}
		
		*/
		
	    return generaReporteFacturas(busqueda,filtro.getFechaDesde(),filtro.getFechaHasta());
	}

	private static HSSFWorkbook generaReporteFacturas(
			List<Factura> list,Date fechaIni,Date fechaFin) throws SystemException {
	
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
	
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("COMPROBANTES");

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
				
		StringBuffer titulo1=new StringBuffer("REPORTE COMPROBANTES EMITIDOS "
				+ "- Desde el "+sdf.format(fechaIni)+" hasta el "+sdf.format(fechaFin));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle( getStyleBoldAligned(wb, HorizontalAlignment.CENTER));
		
        index ++;
        HSSFRow rowHeaderANT1 = sheet.createRow(index);
		HSSFCell cell9HA = rowHeaderANT1.createCell(9);
		cell9HA.setCellValue(new HSSFRichTextString("Impreso: "+ sdf.format(hoy)));
		cell9HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Fecha"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell19H.setCellStyle(styleBold);
		
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Sucursal"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Tipo"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Letra"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Número"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Importe"));
		cell31H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("CAE"));
		cell22H.setCellStyle(styleBold);
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Forma de Pago"));
		cell23H.setCellStyle(styleBold);
				
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Importe"));
		cell24H.setCellStyle(styleBold);
		
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("Fecha"));
		cell25H.setCellStyle(styleBold);
		
		index++;
		
		for(Factura factura: list){
			index=crearDatos(sheet, factura, index, styleAll,
					styleNumber, styleNumber, styleMoney, styleNumber );
		}

		index++;
		
		
		sheet.createRow(++index);
		
		for (int i = 0; i < 42; i++) {
			sheet.autoSizeColumn((short) i);
		}
		
		return wb;
	}

	private static int crearDatos(HSSFSheet sheet,Factura cbte, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) throws SystemException {
		
		styleAll.setWrapText(true);
		SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell002 = rowHeader.createCell(++col);
		cell002.setCellValue(new HSSFRichTextString(sdf.format( cbte.getFecha() )));
		cell002.setCellStyle(styleAll);
		
		
		HSSFCell cell020 = rowHeader.createCell(++col);
		cell020.setCellValue(new HSSFRichTextString(cbte.getCliente().getRazonSocial()!=null?cbte.getCliente().getRazonSocial():
			cbte.getCliente().getApellido()!=null && cbte.getCliente().getNombre()!=null?cbte.getCliente().getApellido()+ " "+
		    cbte.getCliente().getNombre():
				""));
		cell020.setCellStyle(styleAll);
		
		
		HSSFCell cell003 = rowHeader.createCell(++col);
		cell003.setCellValue(new HSSFRichTextString(cbte.getSucursal()));
		cell003.setCellStyle(styleAll);
		
		HSSFCell cell004 = rowHeader.createCell(++col);
		cell004.setCellValue(new HSSFRichTextString(cbte.getTipo()));
		cell004.setCellStyle(styleAll);
		
		HSSFCell cell005 = rowHeader.createCell(++col);
		cell005.setCellValue(new HSSFRichTextString(cbte.getLetra()));
		cell005.setCellStyle(styleAll);
		
		HSSFCell cell006 = rowHeader.createCell(++col);
		cell006.setCellValue(new HSSFRichTextString(cbte.getNumero()));
		cell006.setCellStyle(styleAll);
		
		HSSFCell cell022= rowHeader.createCell(++col);
		cell022.setCellValue(cbte.getImporteTotal()==null?0:cbte.getImporteTotalCalculado().doubleValue() *
				("NCE".equals(cbte.getTipo()) || "NCP".equals(cbte.getTipo()) || "NCR".equals(cbte.getTipo())?-1:1));
		cell022.setCellStyle(styleMoney);
		
		HSSFCell cell001 = rowHeader.createCell(++col);
		cell001.setCellValue(new HSSFRichTextString(cbte.getCae()));
		cell001.setCellStyle(styleAll);
		
		Integer colPagos=col;
		Integer rowAux=index;
		Factura factura =FacturacionServiceUtil.getFactura(cbte.getId());
		
		for(FacturaIngreso fi:factura.getIngresos()) {
			col=colPagos;
			if(rowAux!=index) {
		      rowHeader = sheet.createRow(index++);
		      
//Agregado 09/08/2022 a pedido de Ignacio
		        HSSFCell cell002a = rowHeader.createCell(0);
				cell002a.setCellValue(new HSSFRichTextString(sdf.format( cbte.getFecha() )));
				cell002a.setCellStyle(styleAll);
				
				
				HSSFCell cell020a = rowHeader.createCell(1);
				cell020a.setCellValue(new HSSFRichTextString(cbte.getCliente().getRazonSocial()!=null?cbte.getCliente().getRazonSocial():
					cbte.getCliente().getApellido()!=null && cbte.getCliente().getNombre()!=null?cbte.getCliente().getApellido()+ " "+
				    cbte.getCliente().getNombre():
						""));
				cell020a.setCellStyle(styleAll);
				
				
				HSSFCell cell003a = rowHeader.createCell(2);
				cell003a.setCellValue(new HSSFRichTextString(cbte.getSucursal()));
				cell003a.setCellStyle(styleAll);
				
				HSSFCell cell004a = rowHeader.createCell(3);
				cell004a.setCellValue(new HSSFRichTextString(cbte.getTipo()));
				cell004a.setCellStyle(styleAll);
				
				HSSFCell cell005a = rowHeader.createCell(4);
				cell005a.setCellValue(new HSSFRichTextString(cbte.getLetra()));
				cell005a.setCellStyle(styleAll);
				
				HSSFCell cell006a = rowHeader.createCell(5);
				cell006a.setCellValue(new HSSFRichTextString(cbte.getNumero()));
				cell006a.setCellStyle(styleAll);
//Fin Agregado		      
			}  
			HSSFCell cell001P = rowHeader.createCell(++col);
			cell001P.setCellValue(new HSSFRichTextString(fi.getIngreso().getTipo().trim()));
			cell001P.setCellStyle(styleAll);
			
			
			HSSFCell cell002P = rowHeader.createCell(++col);
			cell002P.setCellValue(fi.getIngreso().getImporte().doubleValue());
			cell002P.setCellStyle(styleMoney);
			
			HSSFCell cell003P = rowHeader.createCell(++col);
			cell003P.setCellValue(new HSSFRichTextString(sdf.format( fi.getIngreso().getFecha())));
			cell003P.setCellStyle(styleAll);
			
			
			HSSFCell cell004P = rowHeader.createCell(++col);
			if(fi.getIngreso().getCuentaBancaria()!=null && fi.getIngreso().getCuentaBancaria().getDescripcion()!=null) {
			    cell004P.setCellValue(new HSSFRichTextString(fi.getIngreso().getCuentaBancaria().getDescripcion()));
			} else {
				cell004P.setCellValue(new HSSFRichTextString(""));
			}
			cell004P.setCellStyle(styleAll);
			
			if(rowAux==index) {
				rowAux=0;
			}
		}
		
		for(Recibo ri:factura.getRecibosAdelantos()) {
			col=colPagos;
			if(rowAux!=index) {
		      rowHeader = sheet.createRow(index++);
			}  
			HSSFCell cell001P = rowHeader.createCell(++col);
			cell001P.setCellValue(new HSSFRichTextString("Adelanto"));
			cell001P.setCellStyle(styleAll);
			
			HSSFCell cell002P = rowHeader.createCell(++col);
			cell002P.setCellValue(ri.getTotal());
			cell002P.setCellStyle(styleMoney);
			
			HSSFCell cell003P = rowHeader.createCell(++col);
			cell003P.setCellValue(new HSSFRichTextString(sdf.format( ri.getFecha())));
			cell003P.setCellStyle(styleAll);
			
			if(rowAux==index) {
				rowAux=0;
			}
		}
		
		
//		rowHeader.setHeight((short) 0);
		return index++;
	}
	
	
	
    private static BusquedaFacturasFiltro getCreaFiltroFactura(HttpServletRequest renderRequest) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesde", null);
		Date fechaDesde = null;
		try {
			fechaDesde = sdf.parse(fechaDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHasta", null);
		Date fechaHasta = null;
		try {
			fechaHasta = sdf.parse(fechaHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}
		String tipo = ParamUtil.getString(renderRequest,"tipo", null);
		String letra = ParamUtil.getString(renderRequest,"letra", null);
		String sucursal = ParamUtil.getString(renderRequest,"sucursal", null);
		String numero = ParamUtil.getString(renderRequest,"numero", null);
		BusquedaFacturasFiltro filtro = new BusquedaFacturasFiltro(fechaDesde, fechaHasta, tipo, sucursal, letra, numero, null);
		
		return filtro;
	}
}


