package ar.com.ospim.farmaciaOspim.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleAdmifarm;

public class ReporteArchivoAdmifarmMonotributo extends ReporteXLS {

    private static Log _log = LogFactoryUtil.getLog(ReporteArchivoAdmifarmMonotributo.class);

    public static HSSFWorkbook generaReporteAdmifarmMonotributo(
            HttpServletRequest renderRequest, HttpServletResponse res) {

        //toma el periodo del JSP
        String periodoArchivo = renderRequest.getParameter("periodo");

        SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaPeriodo = null;

        try {
            fechaPeriodo = formatoDeFechas.parse(periodoArchivo);
        } catch (Exception e) {
            fechaPeriodo = null;
        }

        if (fechaPeriodo == null) {
            _log.error("Periodo inválido para reporte Admifarm Monotributo: " + periodoArchivo);
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaPeriodo);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        //nombre de la tabla por período
        //admifarm_monotributo_MMYYYY
        String nombreTablaAdmifarm = "admifarm_monotributo_" + String.format("%02d", month) + year;

        //trae los registros desde el service
        List<DetalleAdmifarm> registros = new ArrayList<DetalleAdmifarm>();

        try {
            registros = FarmaciaServiceUtil.getListaDetalleAdmifarm(nombreTablaAdmifarm);
        } catch (Exception e) {
            _log.error("Error al generar reporte Admifarm Monotributo", e);
            return null;
        }

        return generaReporte(registros);
    }

    private static HSSFWorkbook generaReporte(List<DetalleAdmifarm> list) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date hoy = new Date();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Admifarm Monotributo");
        HSSFPrintSetup ps = sheet.getPrintSetup();
        sheet.setAutobreaks(true);
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        ps.setFitHeight((short) 0);
        ps.setFitWidth((short) 1);

        HSSFCellStyle styleAll = getStyleAll(wb);
        HSSFCellStyle styleBold = getStyleBold(wb);
        HSSFCellStyle styleMoney = getStyleMoney(wb);

        if (list == null || list.isEmpty()) {
            return wb;
        }
        
        //titulo
        StringBuffer titulo1 = new StringBuffer("Reporte Admifarm Monotributo: ").append(sdf.format(hoy));

        //combina celdas para titulo "Reporte Admifarm Monotributo"
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

        int index = 0;
        int col = -1;

        HSSFRow rowHeaderANT = sheet.createRow(index);
        HSSFCell cell0HA = rowHeaderANT.createCell(0);

        cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
        cell0HA.setCellStyle(styleBold);
        
        //encabezados
        index++;
        HSSFRow rowHeader = sheet.createRow(index);
        col = -1;

        HSSFCell cell0H = rowHeader.createCell(++col);
        cell0H.setCellValue(new HSSFRichTextString("Hasta"));
        cell0H.setCellStyle(styleBold);

        HSSFCell cell1H = rowHeader.createCell(++col);
        cell1H.setCellValue(new HSSFRichTextString("Cod Plan"));
        cell1H.setCellStyle(styleBold);

        HSSFCell cell2H = rowHeader.createCell(++col);
        cell2H.setCellValue(new HSSFRichTextString("Desc Plan"));
        cell2H.setCellStyle(styleBold);

        HSSFCell cell3H = rowHeader.createCell(++col);
        cell3H.setCellValue(new HSSFRichTextString("DNI Benef"));
        cell3H.setCellStyle(styleBold);

        HSSFCell cell4H = rowHeader.createCell(++col);
        cell4H.setCellValue(new HSSFRichTextString("Nombre Benef"));
        cell4H.setCellStyle(styleBold);

        HSSFCell cell5H = rowHeader.createCell(++col);
        cell5H.setCellValue(new HSSFRichTextString("Fecha"));
        cell5H.setCellStyle(styleBold);

        HSSFCell cell6H = rowHeader.createCell(++col);
        cell6H.setCellValue(new HSSFRichTextString("Dispensa"));
        cell6H.setCellStyle(styleBold);

        HSSFCell cell7H = rowHeader.createCell(++col);
        cell7H.setCellValue(new HSSFRichTextString("Tipo Matrícula"));
        cell7H.setCellStyle(styleBold);

        HSSFCell cell8H = rowHeader.createCell(++col);
        cell8H.setCellValue(new HSSFRichTextString("Matrícula"));
        cell8H.setCellStyle(styleBold);

        HSSFCell cell9H = rowHeader.createCell(++col);
        cell9H.setCellValue(new HSSFRichTextString("Profesional"));
        cell9H.setCellStyle(styleBold);

        HSSFCell cell10H = rowHeader.createCell(++col);
        cell10H.setCellValue(new HSSFRichTextString("Registro"));
        cell10H.setCellStyle(styleBold);

        HSSFCell cell11H = rowHeader.createCell(++col);
        cell11H.setCellValue(new HSSFRichTextString("Troquel"));
        cell11H.setCellStyle(styleBold);

        HSSFCell cell12H = rowHeader.createCell(++col);
        cell12H.setCellValue(new HSSFRichTextString("Nombre Comercial"));
        cell12H.setCellStyle(styleBold);

        HSSFCell cell13H = rowHeader.createCell(++col);
        cell13H.setCellValue(new HSSFRichTextString("Pot"));
        cell13H.setCellStyle(styleBold);

        HSSFCell cell14H = rowHeader.createCell(++col);
        cell14H.setCellValue(new HSSFRichTextString("Acción"));
        cell14H.setCellStyle(styleBold);

        HSSFCell cell15H = rowHeader.createCell(++col);
        cell15H.setCellValue(new HSSFRichTextString("Principio"));
        cell15H.setCellStyle(styleBold);

        HSSFCell cell16H = rowHeader.createCell(++col);
        cell16H.setCellValue(new HSSFRichTextString("Nro Lote"));
        cell16H.setCellStyle(styleBold);

        HSSFCell cell17H = rowHeader.createCell(++col);
        cell17H.setCellValue(new HSSFRichTextString("Orden"));
        cell17H.setCellStyle(styleBold);

        HSSFCell cell18H = rowHeader.createCell(++col);
        cell18H.setCellValue(new HSSFRichTextString("Receta"));
        cell18H.setCellStyle(styleBold);

        HSSFCell cell19H = rowHeader.createCell(++col);
        cell19H.setCellValue(new HSSFRichTextString("Nro Ítem"));
        cell19H.setCellStyle(styleBold);

        HSSFCell cell20H = rowHeader.createCell(++col);
        cell20H.setCellValue(new HSSFRichTextString("Env"));
        cell20H.setCellStyle(styleBold);

        HSSFCell cell21H = rowHeader.createCell(++col);
        cell21H.setCellValue(new HSSFRichTextString("Precio Unitario"));
        cell21H.setCellStyle(styleBold);

        HSSFCell cell22H = rowHeader.createCell(++col);
        cell22H.setCellValue(new HSSFRichTextString("PVP"));
        cell22H.setCellStyle(styleBold);

        HSSFCell cell23H = rowHeader.createCell(++col);
        cell23H.setCellValue(new HSSFRichTextString("Porcentaje"));
        cell23H.setCellStyle(styleBold);

        HSSFCell cell24H = rowHeader.createCell(++col);
        cell24H.setCellValue(new HSSFRichTextString("Entidad"));
        cell24H.setCellStyle(styleBold);

        HSSFCell cell25H = rowHeader.createCell(++col);
        cell25H.setCellValue(new HSSFRichTextString("% Bonif"));
        cell25H.setCellStyle(styleBold);

        HSSFCell cell26H = rowHeader.createCell(++col);
        cell26H.setCellValue(new HSSFRichTextString("Imp Bonif"));
        cell26H.setCellStyle(styleBold);

        HSSFCell cell27H = rowHeader.createCell(++col);
        cell27H.setCellValue(new HSSFRichTextString("Imp Neto"));
        cell27H.setCellStyle(styleBold);

        HSSFCell cell28H = rowHeader.createCell(++col);
        cell28H.setCellValue(new HSSFRichTextString("Cod Farmacia"));
        cell28H.setCellStyle(styleBold);

        HSSFCell cell29H = rowHeader.createCell(++col);
        cell29H.setCellValue(new HSSFRichTextString("Farmacia"));
        cell29H.setCellStyle(styleBold);

        HSSFCell cell30H = rowHeader.createCell(++col);
        cell30H.setCellValue(new HSSFRichTextString("Localidad"));
        cell30H.setCellStyle(styleBold);

        HSSFCell cell31H = rowHeader.createCell(++col);
        cell31H.setCellValue(new HSSFRichTextString("Provincia"));
        cell31H.setCellStyle(styleBold);

        HSSFCell cell32H = rowHeader.createCell(++col);
        cell32H.setCellValue(new HSSFRichTextString("Región"));
        cell32H.setCellStyle(styleBold);

        HSSFCell cell33H = rowHeader.createCell(++col);
        cell33H.setCellValue(new HSSFRichTextString("Laboratorio"));
        cell33H.setCellStyle(styleBold);

        HSSFCell cell34H = rowHeader.createCell(++col);
        cell34H.setCellValue(new HSSFRichTextString("Autorización"));
        cell34H.setCellStyle(styleBold);

        HSSFCell cell35H = rowHeader.createCell(++col);
        cell35H.setCellValue(new HSSFRichTextString("Id Cabecera"));
        cell35H.setCellStyle(styleBold);

        HSSFCell cell36H = rowHeader.createCell(++col);
        cell36H.setCellValue(new HSSFRichTextString("PMI"));
        cell36H.setCellStyle(styleBold);

        HSSFCell cell37H = rowHeader.createCell(++col);
        cell37H.setCellValue(new HSSFRichTextString("Monto OSPIM"));
        cell37H.setCellStyle(styleBold);

        HSSFCell cell38H = rowHeader.createCell(++col);
        cell38H.setCellValue(new HSSFRichTextString("Monto UOMA"));
        cell38H.setCellStyle(styleBold);

        HSSFCell cell39H = rowHeader.createCell(++col);
        cell39H.setCellValue(new HSSFRichTextString("Monto AMTIMA"));
        cell39H.setCellStyle(styleBold);

        HSSFCell cell40H = rowHeader.createCell(++col);
        cell40H.setCellValue(new HSSFRichTextString("Plan"));
        cell40H.setCellStyle(styleBold);

        HSSFCell cell41H = rowHeader.createCell(++col);
        cell41H.setCellValue(new HSSFRichTextString("Inte"));
        cell41H.setCellStyle(styleBold);

        HSSFCell cell42H = rowHeader.createCell(++col);
        cell42H.setCellValue(new HSSFRichTextString("Id Ospim"));
        cell42H.setCellStyle(styleBold);

        HSSFCell cell43H = rowHeader.createCell(++col);
        cell43H.setCellValue(new HSSFRichTextString("Id UOMA"));
        cell43H.setCellStyle(styleBold);

        HSSFCell cell44H = rowHeader.createCell(++col);
        cell44H.setCellValue(new HSSFRichTextString("Id AMTIMA"));
        cell44H.setCellStyle(styleBold);

        HSSFCell cell45H = rowHeader.createCell(++col);
        cell45H.setCellValue(new HSSFRichTextString("Id Seccional"));
        cell45H.setCellStyle(styleBold);

        HSSFCell cell46H = rowHeader.createCell(++col);
        cell46H.setCellValue(new HSSFRichTextString("Seccional"));
        cell46H.setCellStyle(styleBold);

        HSSFCell cell47H = rowHeader.createCell(++col);
        cell47H.setCellValue(new HSSFRichTextString("Comentario"));
        cell47H.setCellStyle(styleBold);

        HSSFCell cell48H = rowHeader.createCell(++col);
        cell48H.setCellValue(new HSSFRichTextString("Cuil Titular"));
        cell48H.setCellStyle(styleBold);

        HSSFCell cell49H = rowHeader.createCell(++col);
        cell49H.setCellValue(new HSSFRichTextString("Cuit Farmacia"));
        cell49H.setCellStyle(styleBold);

        index++;
        
        for (DetalleAdmifarm d : list) {
            index = crearDatosFicha(sheet, d, index, styleAll, styleMoney);
        }
        
        //ajusta el ancho de las columnas
        int totalCols = col + 1;
        for (int c = 0; c < totalCols; c++) {
            sheet.autoSizeColumn((short) c);
        }

        return wb;
    }

    private static int crearDatosFicha(HSSFSheet sheet, DetalleAdmifarm d,
                                       int index, HSSFCellStyle styleAll,
                                       HSSFCellStyle styleMoney) {

        int col = -1;
        HSSFRow row = sheet.createRow(index++);

        try {

            //periodo (dd/MM/yyyy)
        	// hasta
        	HSSFCell c0 = row.createCell(++col);
        	if (d.getHasta() != null) {
        	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        	    c0.setCellValue(new HSSFRichTextString(sdf.format(d.getHasta())));
        	} else {
        	    c0.setCellValue(new HSSFRichTextString(""));
        	}
        	c0.setCellStyle(styleAll);

        	HSSFCell c1 = row.createCell(++col);
        	c1.setCellValue(new HSSFRichTextString(d.getCod_plan() == null ? "" : d.getCod_plan()));
        	c1.setCellStyle(styleAll);

        	HSSFCell c2 = row.createCell(++col);
        	c2.setCellValue(new HSSFRichTextString(d.getDesc_plan() == null ? "" : d.getDesc_plan()));
        	c2.setCellStyle(styleAll);

        	HSSFCell c3 = row.createCell(++col);
        	c3.setCellValue(new HSSFRichTextString(d.getDni_benef() == null ? "" : d.getDni_benef()));
        	c3.setCellStyle(styleAll);

        	HSSFCell c4 = row.createCell(++col);
        	c4.setCellValue(new HSSFRichTextString(d.getNombre_benef() == null ? "" : d.getNombre_benef()));
        	c4.setCellStyle(styleAll);

        	HSSFCell c5 = row.createCell(++col);
        	c5.setCellValue(new HSSFRichTextString(d.getFecha() == null ? "" : d.getFecha()));
        	c5.setCellStyle(styleAll);

        	HSSFCell c6 = row.createCell(++col);
        	c6.setCellValue(new HSSFRichTextString(d.getDispensa() == null ? "" : d.getDispensa()));
        	c6.setCellStyle(styleAll);

        	HSSFCell c7 = row.createCell(++col);
        	c7.setCellValue(new HSSFRichTextString(d.getTipo_matricula() == null ? "" : d.getTipo_matricula()));
        	c7.setCellStyle(styleAll);

        	HSSFCell c8 = row.createCell(++col);
        	c8.setCellValue(new HSSFRichTextString(d.getMatricula() == null ? "" : d.getMatricula()));
        	c8.setCellStyle(styleAll);

        	HSSFCell c9 = row.createCell(++col);
        	c9.setCellValue(new HSSFRichTextString(d.getProfesional() == null ? "" : d.getProfesional()));
        	c9.setCellStyle(styleAll);

        	HSSFCell c10 = row.createCell(++col);
        	c10.setCellValue(new HSSFRichTextString(d.getRegistro() == null ? "" : d.getRegistro()));
        	c10.setCellStyle(styleAll);

        	HSSFCell c11 = row.createCell(++col);
        	c11.setCellValue(new HSSFRichTextString(d.getTroquel() == null ? "" : d.getTroquel()));
        	c11.setCellStyle(styleAll);

        	HSSFCell c12 = row.createCell(++col);
        	c12.setCellValue(new HSSFRichTextString(d.getNombre_comercial() == null ? "" : d.getNombre_comercial()));
        	c12.setCellStyle(styleAll);

        	HSSFCell c13 = row.createCell(++col);
        	c13.setCellValue(new HSSFRichTextString(d.getPot() == null ? "" : d.getPot()));
        	c13.setCellStyle(styleAll);

        	HSSFCell c14 = row.createCell(++col);
        	c14.setCellValue(new HSSFRichTextString(d.getAccion() == null ? "" : d.getAccion()));
        	c14.setCellStyle(styleAll);

        	HSSFCell c15 = row.createCell(++col);
        	c15.setCellValue(new HSSFRichTextString(d.getPrincipio() == null ? "" : d.getPrincipio()));
        	c15.setCellStyle(styleAll);

        	HSSFCell c16 = row.createCell(++col);
        	c16.setCellValue(new HSSFRichTextString(d.getNro_lote() == null ? "" : d.getNro_lote()));
        	c16.setCellStyle(styleAll);

        	HSSFCell c17 = row.createCell(++col);
        	c17.setCellValue(new HSSFRichTextString(d.getOrden() == null ? "" : d.getOrden()));
        	c17.setCellStyle(styleAll);

        	HSSFCell c18 = row.createCell(++col);
        	c18.setCellValue(new HSSFRichTextString(d.getReceta() == null ? "" : d.getReceta()));
        	c18.setCellStyle(styleAll);

        	HSSFCell c19 = row.createCell(++col);
        	c19.setCellValue(new HSSFRichTextString(d.getNro_item() == null ? "" : d.getNro_item()));
        	c19.setCellStyle(styleAll);

        	// env
        	HSSFCell c20 = row.createCell(++col);
        	if (d.getEnv() != null) {
        	    c20.setCellValue(d.getEnv());
        	    c20.setCellType(CellType.NUMERIC);
        	} else {
        	    c20.setCellValue(0);
        	}
        	c20.setCellStyle(styleMoney);

        	// precio_unitario
        	HSSFCell c21 = row.createCell(++col);
        	if (d.getPrecio_unitario() != null) {
        	    c21.setCellValue(d.getPrecio_unitario());
        	    c21.setCellType(CellType.NUMERIC);
        	} else {
        	    c21.setCellValue(0);
        	}
        	c21.setCellStyle(styleMoney);

        	// pvp
        	HSSFCell c22 = row.createCell(++col);
        	if (d.getPvp() != null) {
        	    c22.setCellValue(d.getPvp());
        	    c22.setCellType(CellType.NUMERIC);
        	} else {
        	    c22.setCellValue(0);
        	}
        	c22.setCellStyle(styleMoney);

        	// porcentaje
        	HSSFCell c23 = row.createCell(++col);
        	if (d.getPorcentaje() != null) {
        	    c23.setCellValue(d.getPorcentaje());
        	    c23.setCellType(CellType.NUMERIC);
        	} else {
        	    c23.setCellValue(0);
        	}
        	c23.setCellStyle(styleMoney);

        	// entidad
        	HSSFCell c24 = row.createCell(++col);
        	if (d.getEntidad() != null) {
        	    c24.setCellValue(d.getEntidad());
        	    c24.setCellType(CellType.NUMERIC);
        	} else {
        	    c24.setCellValue(0);
        	}
        	c24.setCellStyle(styleMoney);

        	// porc_bonif
        	HSSFCell c25 = row.createCell(++col);
        	if (d.getPorc_bonif() != null) {
        	    c25.setCellValue(d.getPorc_bonif());
        	    c25.setCellType(CellType.NUMERIC);
        	} else {
        	    c25.setCellValue(0);
        	}
        	c25.setCellStyle(styleMoney);

        	// imp_bonif
        	HSSFCell c26 = row.createCell(++col);
        	if (d.getImp_bonif() != null) {
        	    c26.setCellValue(d.getImp_bonif());
        	    c26.setCellType(CellType.NUMERIC);
        	} else {
        	    c26.setCellValue(0);
        	}
        	c26.setCellStyle(styleMoney);

        	// imp_neto
        	HSSFCell c27 = row.createCell(++col);
        	if (d.getImp_neto() != null) {
        	    c27.setCellValue(d.getImp_neto());
        	    c27.setCellType(CellType.NUMERIC);
        	} else {
        	    c27.setCellValue(0);
        	}
        	c27.setCellStyle(styleMoney);

        	HSSFCell c28 = row.createCell(++col);
        	c28.setCellValue(new HSSFRichTextString(d.getCod_farmacia() == null ? "" : d.getCod_farmacia()));
        	c28.setCellStyle(styleAll);

        	HSSFCell c29 = row.createCell(++col);
        	c29.setCellValue(new HSSFRichTextString(d.getFarmacia() == null ? "" : d.getFarmacia()));
        	c29.setCellStyle(styleAll);

        	HSSFCell c30 = row.createCell(++col);
        	c30.setCellValue(new HSSFRichTextString(d.getLocalidad() == null ? "" : d.getLocalidad()));
        	c30.setCellStyle(styleAll);

        	HSSFCell c31 = row.createCell(++col);
        	c31.setCellValue(new HSSFRichTextString(d.getProvincia() == null ? "" : d.getProvincia()));
        	c31.setCellStyle(styleAll);

        	HSSFCell c32 = row.createCell(++col);
        	c32.setCellValue(new HSSFRichTextString(d.getRegion() == null ? "" : d.getRegion()));
        	c32.setCellStyle(styleAll);

        	HSSFCell c33 = row.createCell(++col);
        	c33.setCellValue(new HSSFRichTextString(d.getLaboratorio() == null ? "" : d.getLaboratorio()));
        	c33.setCellStyle(styleAll);

        	HSSFCell c34 = row.createCell(++col);
        	c34.setCellValue(new HSSFRichTextString(d.getAutorizacion() == null ? "" : d.getAutorizacion()));
        	c34.setCellStyle(styleAll);

        	// id_cabecera
        	HSSFCell c35 = row.createCell(++col);
        	if (d.getId_cabecera() != null) {
        	    c35.setCellValue(d.getId_cabecera());
        	    c35.setCellType(CellType.NUMERIC);
        	} else {
        	    c35.setCellValue(0);
        	}
        	c35.setCellStyle(styleAll);

        	HSSFCell c36 = row.createCell(++col);
        	c36.setCellValue(new HSSFRichTextString(d.getPmi() == null ? "" : d.getPmi()));
        	c36.setCellStyle(styleAll);

        	// montos
        	HSSFCell c37 = row.createCell(++col);
        	if (d.getMonto_ospim() != null) {
        	    c37.setCellValue(d.getMonto_ospim());
        	    c37.setCellType(CellType.NUMERIC);
        	} else {
        	    c37.setCellValue(0);
        	}
        	c37.setCellStyle(styleMoney);

        	HSSFCell c38 = row.createCell(++col);
        	if (d.getMonto_uoma() != null) {
        	    c38.setCellValue(d.getMonto_uoma());
        	    c38.setCellType(CellType.NUMERIC);
        	} else {
        	    c38.setCellValue(0);
        	}
        	c38.setCellStyle(styleMoney);

        	HSSFCell c39 = row.createCell(++col);
        	if (d.getMonto_amtima() != null) {
        	    c39.setCellValue(d.getMonto_amtima());
        	    c39.setCellType(CellType.NUMERIC);
        	} else {
        	    c39.setCellValue(0);
        	}
        	c39.setCellStyle(styleMoney);

        	HSSFCell c40 = row.createCell(++col);
        	c40.setCellValue(new HSSFRichTextString(d.getPlan() == null ? "" : d.getPlan()));
        	c40.setCellStyle(styleAll);

        	HSSFCell c41 = row.createCell(++col);
        	c41.setCellValue(new HSSFRichTextString(d.getInte() == null ? "" : d.getInte()));
        	c41.setCellStyle(styleAll);

        	HSSFCell c42 = row.createCell(++col);
        	c42.setCellValue(new HSSFRichTextString(d.getId_ospim() == null ? "" : d.getId_ospim()));
        	c42.setCellStyle(styleAll);

        	HSSFCell c43 = row.createCell(++col);
        	c43.setCellValue(new HSSFRichTextString(d.getId_uoma() == null ? "" : d.getId_uoma()));
        	c43.setCellStyle(styleAll);

        	HSSFCell c44 = row.createCell(++col);
        	c44.setCellValue(new HSSFRichTextString(d.getId_amtima() == null ? "" : d.getId_amtima()));
        	c44.setCellStyle(styleAll);

        	HSSFCell c45 = row.createCell(++col);
        	c45.setCellValue(new HSSFRichTextString(d.getId_seccional() == null ? "" : d.getId_seccional()));
        	c45.setCellStyle(styleAll);

        	HSSFCell c46 = row.createCell(++col);
        	c46.setCellValue(new HSSFRichTextString(d.getSeccional() == null ? "" : d.getSeccional()));
        	c46.setCellStyle(styleAll);

        	HSSFCell c47 = row.createCell(++col);
        	c47.setCellValue(new HSSFRichTextString(d.getComentario() == null ? "" : d.getComentario()));
        	c47.setCellStyle(styleAll);

        	HSSFCell c48 = row.createCell(++col);
        	c48.setCellValue(new HSSFRichTextString(d.getCuil_titular() == null ? "" : d.getCuil_titular()));
        	c48.setCellStyle(styleAll);

        	HSSFCell c49 = row.createCell(++col);
        	c49.setCellValue(new HSSFRichTextString(d.getCuit_farmacia() == null ? "" : d.getCuit_farmacia()));
        	c49.setCellStyle(styleAll);

            
        } catch (Exception e) {
            _log.error("Error al generar fila Excel Admifarm Monotributo", e);
        }

        return index;
    }

}
