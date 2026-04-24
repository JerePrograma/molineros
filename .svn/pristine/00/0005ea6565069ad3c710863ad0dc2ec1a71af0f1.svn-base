package ar.com.ospim.afiliados.reportes;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteListadosTercerizadoras extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteListadosTercerizadoras.class);

	public static HSSFWorkbook getReporteVigentesTercerizadora(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
  		SimpleDateFormat sdf2 = new SimpleDateFormat("MMM/yyyy",  new Locale("es", "ES"));
		
		ReportesAfiliadoServiceImpl reporteService = new ReportesAfiliadoServiceImpl();
		String id_terc=ParamUtil.getString(req, "id_terc");//ParamUtil.getString(req, "id_terc");
		String fechaDesdeDia = req.getParameter("fechaDesdeDia");
		String fechaDesdeMes = req.getParameter("fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = req.getParameter("fechaDesdeAnio");
		//String fechaDesde=ParamUtil.getString(req, "periodoDesde");
		boolean informar=ParamUtil.getBoolean(req,"informar");
		boolean verCodigoSeccionales=ParamUtil.getBoolean(req,"verCodigoSeccionales");
		int tipoInforme=ParamUtil.getInteger(req,"tipoInforme");
		
		List<TercerizadoraServicio> tercerizadorasServicio=TraeListasServiceUtil.getTercerizadoraServicio();
		int indexOf = tercerizadorasServicio.indexOf(new TercerizadoraServicio(id_terc));
		TercerizadoraServicio tercerizadora = tercerizadorasServicio.get(indexOf);
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "/" + fechaDesdeMes
					+ "/" + fechaDesdeAnio);
			
			List<Afiliado> reporte = reporteService
					.getListadoVigentesTercerizadoras(id_terc,informar, tipoInforme, fechaIni);

			String fechaProceso = format.format(new Date());
						
			return generarReporteTercerizadora(fechaProceso, sdf2.format(fechaIni), tercerizadora.getDescripcion(), reporte, tipoInforme,verCodigoSeccionales);

		} catch (Exception e) {
			_log.error("Error al generar reporte inconsistencias", e);
			return null;
		}
	}
	
	public static HSSFWorkbook getReporteVigentesTercerizadoraHistorico(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		ReportesAfiliadoServiceImpl reporteService = new ReportesAfiliadoServiceImpl();
		String id_terc=ParamUtil.getString(req, "id_terc");
		String tipoInformeDescripcion=ParamUtil.getString(req,"tipoInforme");  
		boolean verCodigoSeccionales=ParamUtil.getBoolean(req,"verCodigoSeccionales");
		String fechaInforme = ParamUtil.getString(req,"fecha");
		String fechaVigInforme = ParamUtil.getString(req,"fechaVigencia");
		int tipoInforme=0;
		try {						
			if (tipoInformeDescripcion.equals("DIFERENCIAS")){
				tipoInforme=2;				
			}else if (tipoInformeDescripcion.trim().equals("4")){
				tipoInforme=4;
			}else {
				tipoInforme=1;
			}
			List<Afiliado> reporte = reporteService
					.getListadoVigentesTercerizadorasHistorico(id_terc,tipoInforme, format.parse(fechaInforme));
			//String fecha = format.format(fechaIni);
			
			return generarReporteTercerizadora(fechaInforme, fechaVigInforme, id_terc, reporte, tipoInforme,verCodigoSeccionales);

		} catch (Exception e) {
			_log.error("Error al generar reporte inconsistencias", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarReporteTercerizadora(String fechaProceso, String periodoVigencia, String tercerizadora, List<Afiliado> lista, int tipo , boolean verCodigoSeccionales ) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleTotalesMoneyR = getStyleMoneyBold(wb);
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
		
		int index_column=0;
		double totalValorCapitas=0;
		try {

			createHeaderReporteTercerizadoras(wb, sheet, periodoVigencia, fechaProceso ,tipo, tercerizadora, lista.size(), new BigDecimal(totalValorCapitas));

			int index = 4;			
			for (Afiliado afi : lista) {
				index_column=0;
				HSSFRow row = sheet.createRow(index++);
				
				if(tipo == 5){
					HSSFCell cell0 = row.createCell(index_column++);
					cell0.setCellValue(afi.getId_ospim());
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell2 = row.createCell(index_column++);
					cell2.setCellValue(new HSSFRichTextString(afi.getCuil_titular()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell211 = row.createCell(index_column++);
					cell211.setCellValue(new HSSFRichTextString(afi.getCuil()));
					cell211.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(index_column++);
					cell3.setCellValue(afi.getInte());
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(index_column++);
					cell4.setCellValue(new HSSFRichTextString(afi.getParentesco()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(index_column++);
					cell5.setCellValue(new HSSFRichTextString(afi.getApellido()));				
					cell5.setCellStyle(styleAllWithBorder);
					HSSFCell cell6 = row.createCell(index_column++);
					cell6.setCellValue(new HSSFRichTextString(afi.getNombre()));
					cell6.setCellStyle(styleAllWithBorder);
					HSSFCell cell7 = row.createCell(index_column++);
					cell7.setCellValue(new HSSFRichTextString(afi.getDocumento_tipo()));
					cell7.setCellStyle(styleAllWithBorder);
					HSSFCell cell8 = row.createCell(index_column++);
					cell8.setCellValue(new HSSFRichTextString(afi.getDocu_numero()));
					cell8.setCellStyle(styleAllWithBorder);
					HSSFCell cell9 = row.createCell(index_column++);
					cell9.setCellValue(new HSSFRichTextString(format.format(afi.getNaci_fecha())));
					cell9.setCellStyle(styleAllWithBorder);
					HSSFCell cell22 = row.createCell(index_column++);
					cell22.setCellValue(new HSSFRichTextString(afi.getNombrePlan()));
					cell22.setCellStyle(styleAllWithBorder);
					HSSFCell cell23 = row.createCell(index_column++);				
//					cell23.setCellValue(new HSSFRichTextString(format.format(afi.getIngre_fecha())));
					Calendar vigencia = getVigenciaCorrespondiente(afi.getVigen_fecha());
					cell23.setCellValue(new HSSFRichTextString(format.format(vigencia.getTime()))); // en el historico puede fallar...
					cell23.setCellStyle(styleAllWithBorder);
					HSSFCell cell27 = row.createCell(index_column++);
					if(afi.getBaja_fecha() != null){
						cell27.setCellValue(new HSSFRichTextString(format.format(afi.getBaja_fecha())));
					}else{
						cell27.setCellValue(new HSSFRichTextString(""));
					}
					cell27.setCellStyle(styleAllWithBorder);
					HSSFCell cell28 = row.createCell(index_column++);
					if(afi.getTitle() != null){
						cell28.setCellValue(new HSSFRichTextString(afi.getTitle())); //chanchada
					}else{
						cell28.setCellValue(new HSSFRichTextString(""));
					}
					cell28.setCellStyle(styleAllWithBorder);

					HSSFCell cell30 = row.createCell(index_column++);
					cell30.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().getDescripcionPrevencion().
							equalsIgnoreCase("AG")?"A GENERAL":afi.getUltimo_plan().getDescripcionPrevencion()));
					
					HSSFCell cell31 = row.createCell(index_column++);
					cell31.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().getFarmaciaPrevencion()));
					cell31.setCellStyle(styleAllWithBorder);

				}else{
					HSSFCell cell2 = row.createCell(index_column++);
					cell2.setCellValue(new HSSFRichTextString(afi.getCuil_titular()));
					cell2.setCellStyle(styleAllWithBorder);
					HSSFCell cell211 = row.createCell(index_column++);
					cell211.setCellValue(new HSSFRichTextString(afi.getCuil()));
					cell211.setCellStyle(styleAllWithBorder);
					HSSFCell cell3 = row.createCell(index_column++);
					cell3.setCellValue(afi.getInte());
					cell3.setCellStyle(styleAllWithBorder);
					HSSFCell cell0 = row.createCell(index_column++);
					cell0.setCellValue(afi.getId_ospim());
					cell0.setCellStyle(styleAllWithBorder);
					HSSFCell cell1 = row.createCell(index_column++);
					if (!verCodigoSeccionales){
						String[] partes = afi.getSeccional().getDescripcion().split("-");
						cell1.setCellValue(new HSSFRichTextString(partes[1].trim() ));
					}else{
						cell1.setCellValue(new HSSFRichTextString(afi.getSeccional().getDescripcion()));	
					}					
					cell1.setCellStyle(styleAllWithBorder);
					HSSFCell cell4 = row.createCell(index_column++);
					cell4.setCellValue(new HSSFRichTextString(afi.getParentesco()));
					cell4.setCellStyle(styleAllWithBorder);
					HSSFCell cell5 = row.createCell(index_column++);
					cell5.setCellValue(new HSSFRichTextString(afi.getApellido()));				
					cell5.setCellStyle(styleAllWithBorder);
					HSSFCell cell6 = row.createCell(index_column++);
					cell6.setCellValue(new HSSFRichTextString(afi.getNombre()));
					cell6.setCellStyle(styleAllWithBorder);
					HSSFCell cell7 = row.createCell(index_column++);
					cell7.setCellValue(new HSSFRichTextString(afi.getDocumento_tipo()));
					cell7.setCellStyle(styleAllWithBorder);
					HSSFCell cell8 = row.createCell(index_column++);
					cell8.setCellValue(new HSSFRichTextString(afi.getDocu_numero()));
					cell8.setCellStyle(styleAllWithBorder);
					HSSFCell cell9 = row.createCell(index_column++);
					cell9.setCellValue(new HSSFRichTextString(format.format(afi.getNaci_fecha())));
					cell9.setCellStyle(styleAllWithBorder);
					HSSFCell cell10 = row.createCell(index_column++);
					cell10.setCellValue(new HSSFRichTextString(afi.getSexo().toUpperCase()));
					cell10.setCellStyle(styleAllWithBorder);
					HSSFCell cell11 = row.createCell(index_column++);
					cell11.setCellValue(new HSSFRichTextString(afi.getCivil_esta()));
					cell11.setCellStyle(styleAllWithBorder);
					HSSFCell cell12 = row.createCell(index_column++);
					cell12.setCellValue(new HSSFRichTextString(afi.getNacionalidad_string()));
					cell12.setCellStyle(styleAllWithBorder);
					HSSFCell cell13 = row.createCell(index_column++);
					cell13.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getProvincia().getDescripcion()));
					cell13.setCellStyle(styleAllWithBorder);
					HSSFCell cell14 = row.createCell(index_column++);
					cell14.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getLocalidad().getDescripcion()));
					cell14.setCellStyle(styleAllWithBorder);
					HSSFCell cell15 = row.createCell(index_column++);
					cell15.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getPostal_codi()));
					cell15.setCellStyle(styleAllWithBorder);
					HSSFCell cell16 = row.createCell(index_column++);
					cell16.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getCalle()));
					cell16.setCellStyle(styleAllWithBorder);
					HSSFCell cell17 = row.createCell(index_column++);
					cell17.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getNumero()));
					cell17.setCellStyle(styleAllWithBorder);
					HSSFCell cell18 = row.createCell(index_column++);
					cell18.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getPiso()));
					cell18.setCellStyle(styleAllWithBorder);
					HSSFCell cell19 = row.createCell(index_column++);
					cell19.setCellValue(new HSSFRichTextString(afi.getDomicilioDefault().getDepto()));
					cell19.setCellStyle(styleAllWithBorder);
					
					
					String dataCodeTelefono="";
					String dataTelefono="";
					String dataCodeTelefonoLaboral="";
					String dataTelefonoLaboral="";
					String dataCodeCelular="" ;
					String dataCelular="" ;				 
					
					dataTelefono =   afi.getDomicilioDefault().getTelefono()==null || afi.getDomicilioDefault().getTelefono().equals("")  ? "" :afi.getDomicilioDefault().getTelefono();					
					dataCodeTelefono =  afi.getDomicilioDefault().getCod_area_telefono()==null || afi.getDomicilioDefault().getCod_area_telefono().equals("")   ? "" :afi.getDomicilioDefault().getCod_area_telefono() + "-";
					dataTelefono = !dataTelefono.trim().equals("") ? dataCodeTelefono + dataTelefono:"" ;
							
					dataCodeTelefonoLaboral= afi.getDomicilioDefault().getCod_area_tel_laboral() == null || afi.getDomicilioDefault().getCod_area_tel_laboral().equals("")  ? "" :afi.getDomicilioDefault().getCod_area_tel_laboral() + "-";					
					dataTelefonoLaboral=  afi.getDomicilioDefault().getTel_laboral()==null || afi.getDomicilioDefault().getTel_laboral().equals("")  ?"":afi.getDomicilioDefault().getTel_laboral() ;					
					dataTelefonoLaboral=!dataTelefonoLaboral.equals("") ? dataCodeTelefonoLaboral+dataTelefonoLaboral:""; 
					dataTelefonoLaboral= !dataTelefono.trim().equals("") && !dataTelefonoLaboral.trim().equals("") ?"|" + dataTelefonoLaboral:dataTelefonoLaboral;
					
					dataCodeCelular = afi.getDomicilioDefault().getCod_area_celular() == null ? "" :afi.getDomicilioDefault().getCod_area_celular() + "-";
					dataCelular =  afi.getDomicilioDefault().getCelular() == null || afi.getDomicilioDefault().getCelular().equals("")  ? "" :afi.getDomicilioDefault().getCelular();
					dataCelular = !dataCelular.equals("")?	dataCodeCelular +dataCelular:"" ;				
					dataCelular= !dataTelefonoLaboral.trim().equals("")  && !dataCelular.trim().equals("")    ?"|" + dataCelular:dataCelular;
					
					HSSFCell cell20 = row.createCell(index_column++);					
					cell20.setCellValue(new HSSFRichTextString(dataTelefono  + dataTelefonoLaboral  + dataCelular ));
					cell20.setCellStyle(styleAllWithBorder);	
					HSSFCell cell21 = row.createCell(index_column++);
					cell21.setCellValue(new HSSFRichTextString(afi.getCategoriaSituLaboral(0)));
					cell21.setCellStyle(styleAllWithBorder);
					HSSFCell cell22 = row.createCell(index_column++);
					cell22.setCellValue(new HSSFRichTextString(afi.getNombrePlan()));
					cell22.setCellStyle(styleAllWithBorder);
					HSSFCell cell30 = row.createCell(index_column++);
					cell30.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().getDescripcionPrevencion()));
					cell30.setCellStyle(styleAllWithBorder);
					HSSFCell cell31 = row.createCell(index_column++);
					cell31.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().getFarmaciaPrevencion()));
					cell31.setCellStyle(styleAllWithBorder);
					HSSFCell cell23 = row.createCell(index_column++);				
					cell23.setCellValue(new HSSFRichTextString(format.format(afi.getIngre_fecha())));
					cell23.setCellStyle(styleAllWithBorder);
					HSSFCell cell27 = row.createCell(index_column++);
					if(afi.getBaja_fecha() != null){
						cell27.setCellValue(new HSSFRichTextString(format.format(afi.getBaja_fecha())));
					}else{
						cell27.setCellValue(new HSSFRichTextString(""));
					}
					cell27.setCellStyle(styleAllWithBorder);
					HSSFCell cell24 = row.createCell(index_column++);
					cell24.setCellValue(new HSSFRichTextString(afi.getCuitSituLaboral(0)));
					cell24.setCellStyle(styleAllWithBorder);
// os anterior				cambio a discapacidad 	
					HSSFCell cell25 = row.createCell(index_column++);					
					cell25.setCellValue(new HSSFRichTextString(afi.getDiscapacitado())); //
					cell25.setCellStyle(styleAllWithBorder);
// discapacidad cambio a motivo baja
					HSSFCell cell26 = row.createCell(index_column++);
					if(afi.getTitle() != null){
						cell26.setCellValue(new HSSFRichTextString(afi.getTitle())); //chanchada
					}else{
						cell26.setCellValue(new HSSFRichTextString(""));
					}					
					cell26.setCellStyle(styleAllWithBorder);
					
					//pertenece a la organizacion 
					HSSFCell cell34 = row.createCell(index_column++);
					cell34.setCellValue(new HSSFRichTextString(afi.getClientePreferencial()==1?"SI":"NO"));
					cell34.setCellStyle(styleAllWithBorder);
					
					// motivo baja cambio a obra social anterior   
					HSSFCell cell28 = row.createCell(index_column++);
					cell28.setCellValue(new HSSFRichTextString(String.valueOf(afi.getAnterior_os())));
					cell28.setCellStyle(styleAllWithBorder);	
					
					
					/*  uoma y amtima se ocultan  */
					HSSFCell cell32 = row.createCell(index_column++);
					cell32.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().isAmtima()?"SI":"NO"));
					cell32.setCellStyle(styleAllWithBorder);
					
					HSSFCell cell33 = row.createCell(index_column++);
					cell33.setCellValue(new HSSFRichTextString(afi.getUltimo_plan().isUoma()?"SI":"NO"));
					cell33.setCellStyle(styleAllWithBorder);
					

					HSSFCell cell35 = row.createCell(index_column++);
					cell35.setCellValue(new HSSFRichTextString(afi.getTipoOperacion()));
					cell35.setCellStyle(styleAllWithBorder);
					
					if(tipo==4){					
						HSSFCell cell36 = row.createCell(index_column++);
						cell36.setCellValue(afi.getValorCapita()!=null?afi.getValorCapita().doubleValue():0);
						cell36.setCellStyle(styleTotalesMoneyR);					
						cell36.setCellStyle(styleAllWithBorder);
					}
					totalValorCapitas=totalValorCapitas+(null!=afi.getValorCapita()?afi.getValorCapita().doubleValue():0);
				}
				
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
			sheet.autoSizeColumn((short) 32);
			sheet.autoSizeColumn((short) 33);
			sheet.autoSizeColumn((short) 34);
			sheet.autoSizeColumn((short) 35);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}

	
	private static int createHeaderReporteTercerizadoras(HSSFWorkbook wb, HSSFSheet sheet,
			String periodo, String fechaProceso, int tipo, String tercerizadora, int capitas, BigDecimal total) {

		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		
		NumberFormat formatter = new DecimalFormat("$#,###,##0.00");
		String descripcionTipo = "";
		switch (tipo) {
		case 1:
			descripcionTipo = "Padrón Completo";
			break;
		case 2:
			descripcionTipo = "Diferencias";
			break;
		case 3:
			descripcionTipo = "Titulares";
			break;
		case 4:
			descripcionTipo = "Valoriz. por cápita";
			break;
		case 5:
			descripcionTipo = "Facturación";
			break;	
		}
		int index = 0;
		
		HSSFRow row0= sheet.createRow(0);
//		HSSFCellStyle styleBold = getStyleBold(wb);
		
		HSSFCell cell01= row0.createCell(0);
//		cell01.setCellValue(new HSSFRichTextString("Reporte de cápitas "+tercerizadora+" al "+ fecha +" con vigencia "+fechaVig));
		cell01.setCellValue(new HSSFRichTextString("Reporte de cápitas para "+tercerizadora+ 
												   " Tipo " + descripcionTipo + 
												   " Vigencia " +periodo + 
												   " Informado al "+ fechaProceso) );
//		cell01.setCellStyle(styleBold);
		cell01.setCellStyle(getStyleHeader(wb,12));
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
		
		HSSFRow row02= sheet.createRow(1);
		
		HSSFCell cell02= row02.createCell(0);

		if(tipo==4){			
			cell02.setCellValue(new HSSFRichTextString("Total "+capitas+" cápitas por un total de: "+formatter.format(total.doubleValue())));
			cell02.setCellStyle(getStyleHeader(wb,12));		
		}

		HSSFRow row03= sheet.createRow(2);
		
		HSSFCell cell03= row03.createCell(0);
		
		HSSFRow row3 = sheet.createRow(3);
		
		if(tipo==5){
			HSSFCell cell1 = row3.createCell(index++);
			cell1.setCellValue(new HSSFRichTextString("N° Ospim"));
			cell1.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell3 = row3.createCell(index++);
			cell3.setCellValue(new HSSFRichTextString("Cuil titular"));
			cell3.setCellStyle(styleHeaderEnca3);

			HSSFCell cell4 = row3.createCell(index++);
			cell4.setCellValue(new HSSFRichTextString("Cuil"));
			cell4.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell5 = row3.createCell(index++);
			cell5.setCellValue(new HSSFRichTextString("Inte"));
			cell5.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell6 = row3.createCell(index++);
			cell6.setCellValue(new HSSFRichTextString("Parentesco"));
			cell6.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell7 = row3.createCell(index++);
			cell7.setCellValue(new HSSFRichTextString("Apellido"));
			cell7.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell8 = row3.createCell(index++);
			cell8.setCellValue(new HSSFRichTextString("Nombre"));
			cell8.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell9 = row3.createCell(index++);
			cell9.setCellValue(new HSSFRichTextString("Tipo doc."));
			cell9.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell10 = row3.createCell(index++);
			cell10.setCellValue(new HSSFRichTextString("Número doc."));
			cell10.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell11 = row3.createCell(index++);
			cell11.setCellValue(new HSSFRichTextString("Fecha nac."));
			cell11.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell23 = row3.createCell(index++);
			cell23.setCellValue(new HSSFRichTextString("Plan"));
			cell23.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell24 = row3.createCell(index++);
			cell24.setCellValue(new HSSFRichTextString("Fecha Proceso"));
			cell24.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell28 = row3.createCell(index++);
			cell28.setCellValue(new HSSFRichTextString("Baja fecha"));
			cell28.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell29 = row3.createCell(index++);
			cell29.setCellValue(new HSSFRichTextString("Motivo baja"));
			cell29.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell30 = row3.createCell(index++);
			cell30.setCellValue(new HSSFRichTextString("Plan Prevención"));
			cell30.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell31 = row3.createCell(index++);
			cell31.setCellValue(new HSSFRichTextString("Cobertura Farmacia"));
			cell31.setCellStyle(styleHeaderEnca3);
			
		}else{


			HSSFCell cell3 = row3.createCell(index++);
			cell3.setCellValue(new HSSFRichTextString("Cuil titular"));
			cell3.setCellStyle(styleHeaderEnca3);
	
			HSSFCell cell4 = row3.createCell(index++);
			cell4.setCellValue(new HSSFRichTextString("Cuil"));
			cell4.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell5 = row3.createCell(index++);
			cell5.setCellValue(new HSSFRichTextString("Inte"));
			cell5.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell1 = row3.createCell(index++);
			cell1.setCellValue(new HSSFRichTextString("N° Ospim"));
			cell1.setCellStyle(styleHeaderEnca3);
	
			HSSFCell cell2 = row3.createCell(index++);
			cell2.setCellValue(new HSSFRichTextString("Seccional"));
			cell2.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell6 = row3.createCell(index++);
			cell6.setCellValue(new HSSFRichTextString("Parentesco"));
			cell6.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell7 = row3.createCell(index++);
			cell7.setCellValue(new HSSFRichTextString("Apellido"));
			cell7.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell8 = row3.createCell(index++);
			cell8.setCellValue(new HSSFRichTextString("Nombre"));
			cell8.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell9 = row3.createCell(index++);
			cell9.setCellValue(new HSSFRichTextString("Tipo doc."));
			cell9.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell10 = row3.createCell(index++);
			cell10.setCellValue(new HSSFRichTextString("Número doc."));
			cell10.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell11 = row3.createCell(index++);
			cell11.setCellValue(new HSSFRichTextString("Fecha nac."));
			cell11.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell12 = row3.createCell(index++);
			cell12.setCellValue(new HSSFRichTextString("Sexo"));
			cell12.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell121 = row3.createCell(index++);
			cell121.setCellValue(new HSSFRichTextString("Estado civil"));
			cell121.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell13 = row3.createCell(index++);
			cell13.setCellValue(new HSSFRichTextString("Nacionalidad"));
			cell13.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell14 = row3.createCell(index++);
			cell14.setCellValue(new HSSFRichTextString("Provincia"));
			cell14.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell15 = row3.createCell(index++);
			cell15.setCellValue(new HSSFRichTextString("Localidad"));
			cell15.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell16 = row3.createCell(index++);
			cell16.setCellValue(new HSSFRichTextString("Código postal"));
			cell16.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell17 = row3.createCell(index++);
			cell17.setCellValue(new HSSFRichTextString("Calle"));
			cell17.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell18 = row3.createCell(index++);
			cell18.setCellValue(new HSSFRichTextString("Número"));
			cell18.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell19 = row3.createCell(index++);
			cell19.setCellValue(new HSSFRichTextString("Piso"));
			cell19.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell20 = row3.createCell(index++);
			cell20.setCellValue(new HSSFRichTextString("Depto"));
			cell20.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell21 = row3.createCell(index++);
			cell21.setCellValue(new HSSFRichTextString("Teléfono"));
			cell21.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell22 = row3.createCell(index++);
			cell22.setCellValue(new HSSFRichTextString("Categoría"));
			cell22.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell23 = row3.createCell(index++);
			cell23.setCellValue(new HSSFRichTextString("Plan"));
			cell23.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell30 = row3.createCell(index++);
			cell30.setCellValue(new HSSFRichTextString("Plan Prevención"));
			cell30.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell31 = row3.createCell(index++);
			cell31.setCellValue(new HSSFRichTextString("Farmacia Prev."));
			cell31.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell24 = row3.createCell(index++);
			cell24.setCellValue(new HSSFRichTextString("Fecha Proceso"));
			cell24.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell28 = row3.createCell(index++);
			cell28.setCellValue(new HSSFRichTextString("Baja fecha"));
			cell28.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell25 = row3.createCell(index++);
			cell25.setCellValue(new HSSFRichTextString("Cuit"));
			cell25.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell26 = row3.createCell(index++);
			cell26.setCellValue(new HSSFRichTextString("Discapacidad")); // cambio era OS anterior
			cell26.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell27 = row3.createCell(index++);
			cell27.setCellValue(new HSSFRichTextString("Motivo baja")); // cambio era Discapacidad
			cell27.setCellStyle(styleHeaderEnca3);
	
			HSSFCell cell29 = row3.createCell(index++);
			cell29.setCellValue(new HSSFRichTextString("Pertenece a la Org.")); // cambio era Motivo baja
			cell29.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell32 = row3.createCell(index++);
			cell32.setCellValue(new HSSFRichTextString("OS anterior")); // cambio era  Amtima
			cell32.setCellStyle(styleHeaderEnca3);
			
			
			HSSFCell cell33 = row3.createCell(index++);
			cell33.setCellValue(new HSSFRichTextString("UOMA"));
			cell33.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell34 = row3.createCell(index++);
			cell34.setCellValue(new HSSFRichTextString("AMTIMA"));
			cell34.setCellStyle(styleHeaderEnca3);
			
			HSSFCell cell35 = row3.createCell(index++);
			cell35.setCellValue(new HSSFRichTextString("TIPO OPERACION")); 
			cell35.setCellStyle(styleHeaderEnca3);
			
		}
		
		if(tipo==4){
			HSSFCell cell36 = row3.createCell(index++);
			cell36.setCellValue(new HSSFRichTextString("Valor capita"));
			cell36.setCellStyle(styleHeaderEnca3);
		}

		return 1;
	}

	private static Calendar getVigenciaCorrespondiente(Date vigenciaReal){
		
		Calendar vigencia = Calendar.getInstance();
		
		Calendar primeroDelMes = Calendar.getInstance();
		
		primeroDelMes.set(Calendar.DATE,1);
//		Calendar hoy = Calendar.getInstance();
		
//		intentamos no mandar vigencias anteriores al dia en curso para no pagar capitas retroactivas
		if(vigenciaReal!=null && !vigenciaReal.before(primeroDelMes.getTime())){
			vigencia.setTime(vigenciaReal);
		}else{
			vigencia.setTime(primeroDelMes.getTime());
		}
		
		return vigencia;
		
	}

	private static String retornaTelefonosAfiliado(){
		String dataCodeTelefono="";
		String dataTelefono="";
		String dataCodeTelefonoLaboral="";
		String dataTelefonoLaboral="";
		String dataCodeCelular="" ;
		String dataCelular="" ;				 
		
		/*  
		dataTelefono =   afi.getDomicilioDefault().getTelefono()==null  ? "" :afi.getDomicilioDefault().getTelefono();					
		dataCodeTelefono =  afi.getDomicilioDefault().getCod_area_telefono()==null    ? "" :afi.getDomicilioDefault().getCod_area_telefono() + "-";
		dataTelefono =  dataCodeTelefono + dataTelefono ;
				
		dataCodeTelefonoLaboral= afi.getDomicilioDefault().getCod_area_tel_laboral() == null  ? "" :afi.getDomicilioDefault().getCod_area_tel_laboral() + "-";					
		dataTelefonoLaboral=  afi.getDomicilioDefault().getTel_laboral()==null ?"":afi.getDomicilioDefault().getTel_laboral() ;					
		dataTelefonoLaboral=dataCodeTelefonoLaboral+dataTelefonoLaboral; 
		dataTelefonoLaboral= !dataTelefono.equals("")?"|" + dataTelefonoLaboral:dataTelefonoLaboral;
		
		dataCodeCelular = afi.getDomicilioDefault().getCod_area_celular() == null ? "" :afi.getDomicilioDefault().getCod_area_celular() + "-";
		dataCelular =  afi.getDomicilioDefault().getCelular() == null ? "" :afi.getDomicilioDefault().getCelular();
		dataCelular =	dataCodeCelular +dataCelular ;				
		dataCelular= !dataTelefonoLaboral.equals("")?"|" + dataCelular:dataCelular;
		*/
		return  dataTelefono  + dataTelefonoLaboral  + dataCelular  ;
	}
	
}
