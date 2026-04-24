package ar.com.ospim.liquidaciones.ordenespago.reportes;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.DateUtils;

public abstract class ReporteXLS {
	protected static int crearHeaderPrincipal(HSSFWorkbook wb, HSSFSheet sheet, int cantCols, int entidad) {
		StringBuilder header = new StringBuilder();
		if (entidad == WebKeysGlobal.OSPIM) {
			header.append("OBRA SOCIAL DEL PERSONAL DE LA INDUSTRIA MOLINERA");
			header.append("\n");
			header.append("San Juan 2670 - CAP. FED. CP(1232)");
			header.append("\n");
			header.append("Tel.: 5238-3900");
			header.append("\n");
			header.append("R.N.O.S. 11260/8");
		} else if (entidad == WebKeysGlobal.UOMA) {
			header.append("UNION OBRERA MOLINERA ARGENTINA");
			header.append("\n");
			header.append("México 2070 - C.A.B.A.");
			header.append("\n");
			header.append("Personería gremial Nº 193");
		} else if (entidad == WebKeysGlobal.AMTIMA) {
			header.append("ASOCIACION MUTUAL DE TRABAJADORES DE LA INDUSTRIA MOLINERA ARGENTINA");
			header.append("\n");
			header.append("México 2070 - C.A.B.A.");
			header.append("\n");
			header.append("Personería gremial Nº 193");
		}
		sheet.getHeader().setLeft(header.toString());
		addDefaultHeader(sheet);

		return 0;
	}

	protected static int crearHeaderPrincipalUoma(HSSFWorkbook wb, HSSFSheet sheet, int cantCols, Date fechaImpre) {

		StringBuilder header = new StringBuilder();
		header.append("UNION OBRERA MOLINERA ARGENTINA");
		header.append("\n");
		header.append("México 2070 - C.A.B.A.");
		header.append("\n");
		// header.append("Tel.: 5238-3900");
		// header.append("\n");
		header.append("Personería gremial Nº 193");
		sheet.getHeader().setLeft(header.toString());

		addDefaultHeaderUoma(sheet, fechaImpre);

		return 0;
	}

	protected static HSSFCellStyle getStyleInt(HSSFWorkbook wb) {
		HSSFCellStyle style = getStyleAll(wb);
		style.setDataFormat((short) 2);
		return style;
	}

	protected static HSSFCellStyle getStyleAll(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		return styleAll;
	}
	
	protected static CellStyle getStyleAllWbs(Workbook wb) {
		CellStyle styleAll = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		return styleAll;
	}
	
	protected static CellStyle getStyleAllWbs(SXSSFWorkbook wb) {
		CellStyle styleAll = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		return styleAll;
	}


	protected static HSSFCellStyle getStyleAll(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		styleAll.setFont(font);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleAllWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		setThinBorders(styleAll);
		return styleAll;
	}
	
	protected static CellStyle getStyleAllWithBorderWbs(SXSSFWorkbook wb) {
		CellStyle styleAll = getStyleAllWbs(wb);
		setThinBordersWbs(styleAll);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleAllWithBorder(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleAll = getStyleAll(wb, size);
		setThinBorders(styleAll);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleBold(HSSFWorkbook wb) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}
	
	protected static HSSFCellStyle getStyleBoldWithSize(HSSFWorkbook wb,  int size) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}
	
	protected static CellStyle getStyleBoldWbs(Workbook wb) {
		CellStyle styleBold = wb.createCellStyle();
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}

	protected static HSSFCellStyle getStyleBoldAligned(HSSFWorkbook wb, HorizontalAlignment aligned) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		// styleBold.setAlignment(aligned);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);
		styleBold.setFont(fontBold);
		return styleBold;
	}
	
	protected static HSSFCellStyle getStyleBoldAlignedWbs(HSSFWorkbook wb, HorizontalAlignment aligned) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		// styleBold.setAlignment(aligned);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);
		styleBold.setFont(fontBold);
		return styleBold;
	}


	protected static CellStyle getStyleBoldAlignedWbs(SXSSFWorkbook wb, HorizontalAlignment aligned) {
		CellStyle styleBold = wb.createCellStyle();
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		// styleBold.setAlignment(aligned);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);
		styleBold.setFont(fontBold);
		return styleBold;
	}
	
	protected static HSSFCellStyle getStyleBold(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}
	
	protected static CellStyle getStyleBoldWbs(SXSSFWorkbook wb, int size) {
		CellStyle styleBold = wb.createCellStyle();
		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}

	protected static HSSFCellStyle getStyleBoldWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle style = getStyleBold(wb);
		setThinBorders(style);
		return style;
	}
	
	protected static CellStyle getStyleBoldWithBorderWbs(SXSSFWorkbook wb) {
		CellStyle style = getStyleBoldWbs(wb);
		setThinBordersWbs(style);
		return style;
	}


	protected static HSSFCellStyle getStyleBoldWithBorder(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle style = getStyleBold(wb, fontSize);
		setThinBorders(style);
		return style;
	}

	protected static HSSFCellStyle getStyleBoldUnderlined(HSSFWorkbook wb) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		// fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);
		return styleBold;
	}

	protected static HSSFCellStyle getStyleAllCenter(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		styleAll.setVerticalAlignment(VerticalAlignment.TOP);
		;
		return styleAll;
	}

	protected static HSSFCellStyle getStyleDate(HSSFWorkbook wb) {
		return getStyleDate(wb, 8);
	}
	
	protected static CellStyle getStyleDateWbs(Workbook wb) {
		return getStyleDateWbs(wb, 8);
	}
	
	protected static CellStyle getStyleDateWbs(SXSSFWorkbook wb) {
		return getStyleDateWbs(wb, 8);
	}

	protected static HSSFCellStyle getStylePeriodo(HSSFWorkbook wb) {
		HSSFCellStyle styleDate = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleDate.setFont(font);
		styleDate.setDataFormat(wb.createDataFormat().getFormat("MM/yyyy"));
		return styleDate;
	}

	protected static HSSFCellStyle getStyleDateWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleDate = getStyleDate(wb, 8);
		setThinBorders(styleDate);
		return styleDate;
	}
	
	protected static CellStyle getStyleDateWithBorderWbs(SXSSFWorkbook wb) {
		CellStyle styleDate = getStyleDateWbs(wb, 8);
		setThinBordersWbs(styleDate);
		return styleDate;
	}

	protected static HSSFCellStyle getStyleDateWithBorder(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleDate = getStyleDate(wb, fontSize);
		setThinBorders(styleDate);
		return styleDate;
	}

	protected static HSSFCellStyle getStyleTime(HSSFWorkbook wb) {
		HSSFCellStyle styleDate = getStyleTime(wb, 8);
		return styleDate;
	}

	protected static HSSFCellStyle getStyleTimeWithBorders(HSSFWorkbook wb) {
		HSSFCellStyle styleDate = getStyleTime(wb, 8);
		setThinBorders(styleDate);
		return styleDate;
	}

	protected static HSSFCellStyle getStyleDate(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleDate = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		styleDate.setFont(font);
		styleDate.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy"));

		return styleDate;
	}
	
	protected static CellStyle getStyleDateWbs(Workbook wb, int fontSize) {
		CellStyle styleDate = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		styleDate.setFont(font);
		styleDate.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy"));

		return styleDate;
	}

	protected static HSSFCellStyle getStyleTime(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleDate = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		styleDate.setFont(font);
		styleDate.setDataFormat(wb.createDataFormat().getFormat("hh:mm"));

		return styleDate;
	}

	protected static HSSFCellStyle getStyleBoldUnderlinedHeader(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		styleBold.setVerticalAlignment(VerticalAlignment.TOP);

		return styleBold;
	}

	protected static HSSFCellStyle getStyleWhiteHeaderWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleHeader = getStyleBold(wb);
		setThinBorders(styleHeader);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}


	protected static HSSFCellStyle getStyleHeaderWithBorder(HSSFWorkbook wb,
			int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
//		styleHeader.setFillPattern(FillPatternType.BIG_SPOTS);
		styleHeader.setAlignment(HorizontalAlignment.CENTER);
		return styleHeader;
	}
	
	
	protected static HSSFCellStyle getStyleHeaderWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleHeader = getStyleBold(wb);
		setThinBorders(styleHeader);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
//		styleHeader.setFillPattern(FillPatternType.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}
	
	protected static CellStyle getStyleHeaderWithBorderWbs(SXSSFWorkbook wb) {
		CellStyle styleHeader = getStyleBoldWbs(wb);
		setThinBordersWbs(styleHeader);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
//		styleHeader.setFillPattern(FillPatternType.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static CellStyle getStyleHeaderWithBorderWbs(SXSSFWorkbook wb, int size) {
		CellStyle styleHeader = getStyleBoldWbs(wb, size);
		setThinBordersWbs(styleHeader);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
//		styleHeader.setFillPattern(FillPatternType.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static HSSFCellStyle getStyleHeaderWithBorderNoColor(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static HSSFCellStyle getStyleHeaderWithBorderLeft(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
//		styleHeader.setFillPattern(FillPatternType.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static HSSFCellStyle getStyleHeaderWithBorderLeftNoColor(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);;
		return styleHeader;
	}

	protected static HSSFCellStyle getStyleRight(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setVerticalAlignment(VerticalAlignment.TOP);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleHeader(HSSFWorkbook wb) {
		HSSFCellStyle styleHeader = getStyleBold(wb);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
		// styleHeader.setFillPattern(HSSFCellStyle.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static HSSFCellStyle getStyleHeader(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleHeader = getStyleBold(wb);
//		styleHeader.setFillBackgroundColor(HSSFColorPredefined.LIGHT_BLUE.getIndex());
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		styleHeader.setFont(font);
		// styleHeader.setFillPattern(HSSFCellStyle.BIG_SPOTS);
		styleHeader.setVerticalAlignment(VerticalAlignment.TOP);
		return styleHeader;
	}

	protected static void setThinBorders(HSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}

	protected static void setThinBordersWbs(CellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}
	
	protected static HSSFCellStyle getStyleMoneyWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}
	
	protected static CellStyle getStyleMoneyWithBorderWbs(SXSSFWorkbook wb) {
		CellStyle styleAll = getStyleAllWithBorderWbs(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}

	protected static HSSFCellStyle getBorderTop(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		return style;
	}

	protected static HSSFCellStyle getStyleNumber6DWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFDataFormat df = wb.createDataFormat();
		styleAll.setDataFormat(df.getFormat("#,##0.000000"));
		return styleAll;
	}

	protected static HSSFCellStyle getStyleNumber(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setDataFormat((short) 1);
		return styleAll;
	}

	
	protected static CellStyle getStyleNumberWbs(Workbook wb) {
		CellStyle styleAll = getStyleAllWbs(wb);
		styleAll.setDataFormat((short) 1);
		return styleAll;
	}
	
	protected static HSSFCellStyle getStyleMoneyWithBorder(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb, 10);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleIntWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		styleAll.setDataFormat((short) 2);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleMoney(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}
	
	protected static CellStyle getStyleMoneyWbs(Workbook wb) {
		CellStyle styleAll = getStyleAllWbs(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}
	

	protected static HSSFCellStyle getStyleMoney(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		styleAll.setFont(font);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleMoneyBoldWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleBoldWithBorder(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleMoneyBoldWithBorder(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle styleAll = getStyleBoldWithBorder(wb, fontSize);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleMoneyBold(HSSFWorkbook wb) {
		HSSFCellStyle st = getStyleBold(wb);
		st.setDataFormat((short) 4);
		return st;
	}

	protected static HSSFCellStyle getStyleMoneyBold(HSSFWorkbook wb, int fontSize) {
		HSSFCellStyle st = getStyleBold(wb, fontSize);
		st.setDataFormat((short) 4);
		return st;
	}

	protected static HSSFCellStyle getStyleFondoGris(HSSFWorkbook wb) {
		HSSFCellStyle st = wb.createCellStyle();
		st.setDataFormat((short) 6);
		st.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		st.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return st;
	}

	protected static HSSFCellStyle getStyleFondoGrisWithBorder(HSSFWorkbook wb) {
		HSSFCellStyle st = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		st.setDataFormat((short) 6);
		st.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		st.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		fontBold.setFontHeightInPoints((short) 8);
		fontBold.setBold(true);
		st.setFont(fontBold);
		st.setAlignment(HorizontalAlignment.CENTER);
		setThinBorders(st);
		return st;
	}

	protected static void addDefaultHeader(HSSFSheet sheet) {
		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());
	}

	protected static void addDefaultHeaderUoma(HSSFSheet sheet, Date fechaImpresion) {
		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		// headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		// headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		// headerRight.append(DateUtils.format(new Date(), DateUtils.SHORT));
		headerRight.append(DateUtils.format(fechaImpresion, DateUtils.SHORT));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());
	}

	protected static Date getDesde(HttpServletRequest req) {
		return DateUtils.getFechaDesde(req);
	}

	protected static Date getHasta(HttpServletRequest req) {
		return DateUtils.getFechaHasta(req);
	}

	protected static HSSFCellStyle getStyleMoneyFondoGris(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setDataFormat((short) 4);
		styleAll.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		styleAll.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		styleAll.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return styleAll;
	}

	protected static HSSFCellStyle getStyleAllFondoGris(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		styleAll.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		styleAll.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		styleAll.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return styleAll;
	}

	/**
	 * 
	 * @param wb
	 * @return
	 */
	protected static HSSFCellStyle getStyleAlignVerticalCenter(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		styleAll.setVerticalAlignment(VerticalAlignment.TOP);
		styleAll.setAlignment(HorizontalAlignment.CENTER);
		setThinBorders(styleAll);
		styleAll.setWrapText(true);

		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		styleAll.setFont(font);
	    font.setBold(true);
		setThinBorders(styleAll);
		return styleAll;
	}
}
