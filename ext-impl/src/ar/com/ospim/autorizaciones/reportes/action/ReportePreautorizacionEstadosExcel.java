package ar.com.ospim.autorizaciones.reportes.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReportePreautorizacionEstadosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePreautorizacionEstadosExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		String fechaDia = ParamUtil.getString(renderRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(renderRequest, "fechaMes");
		fechaMes = String.valueOf(Integer.valueOf(fechaMes) + 1);
		String fechaAnio = ParamUtil.getString(renderRequest, "fechaAnio");
		
		String fechaHtaDia = ParamUtil.getString(renderRequest, "fechaDiaHta");
		String fechaHtaMes = ParamUtil.getString(renderRequest, "fechaMesHta");
		fechaHtaMes = String.valueOf(Integer.valueOf(fechaHtaMes) + 1);
		String fechaHtaAnio = ParamUtil.getString(renderRequest, "fechaAnioHta");
		
		Date fecha = new Date();
		Date fechaHta = new Date();
		try {
			fecha = format.parse(fechaDia + "-" + fechaMes
					+ "-" + fechaAnio);
			
			fechaHta = format.parse(fechaHtaDia + "-" + fechaHtaMes
					+ "-" + fechaHtaAnio);
			
		} catch (Exception e) {
			_log.error("Error al estadistico de preautorizaciones", e);
			return null;
		}

		List<Estado> list = new ArrayList<Estado>();
		List<PreAutorizacion> listP = new ArrayList<PreAutorizacion>();
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			 list = PreAutorizacionServiceUtil.getEstadisticoEstados(fecha,fechaHta);
			 wb=generaReporte(wb,list,fecha,fechaHta);
		} catch (SystemException e) {
			_log.debug("Error al generar Estadistico de Estados Preautorizaciones");
		}
		
		try {
			 listP = PreAutorizacionServiceUtil.getEstadisticoPorDia(fecha,fechaHta);
			 wb=generaReportePorDia(wb,listP,fecha,fechaHta);
		} catch (SystemException e) {
			_log.debug("Error al generar Estadistico de Dias Preautorizaciones");
		}
		
		try {
			 listP = PreAutorizacionServiceUtil.getEstadisticoPorSeccional(fecha,fechaHta);
			 wb=generaReportePorSeccional(wb,listP,fecha,fechaHta);
		} catch (SystemException e) {
			_log.debug("Error al generar Estadistico por Seccional Preautorizaciones");
		}
		
		
		try {
			 Calendar c1 = Calendar.getInstance();
	         c1.setTime(fechaHta);
	         c1.add(Calendar.MONTH, -13);
			 list = PreAutorizacionServiceUtil.getEstadisticoPorMes(c1.getTime(),fechaHta);
			 wb=generaReportePorMes(wb,list,fecha,fechaHta);
		} catch (SystemException e) {
			_log.debug("Error al generar Estadistico Mensual Preautorizaciones");
		}
		
		return wb;
	
	}

	public static HSSFWorkbook generaReporte(HSSFWorkbook wb,
			List<Estado> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Estados");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
		
        for(Estado estado: list){
			String desEstado="";
			if("CA".equalsIgnoreCase(estado.getId())){
				desEstado="CARGADO";
			}else if("NR".equalsIgnoreCase(estado.getId())){
				desEstado="NO REQUIERE";
			}else if("OB".equalsIgnoreCase(estado.getId())){
				desEstado="OBSERVADO";
			}else if("AU".equalsIgnoreCase(estado.getId())){
				desEstado="AUTORIZADO";
			}else if("RE".equalsIgnoreCase(estado.getId())){
				desEstado="RECHAZADO";
			}else if("GO".equalsIgnoreCase(estado.getId())){
				desEstado="GESTION OSPIM";
			}else if("DE".equalsIgnoreCase(estado.getId())){
				desEstado="DESESTIMADO";
			}
        	my_bar_chart_dataset.addValue(estado.getCantidadOcurrencias(),estado.getDescripcion(),desEstado);
//        	dataset.addValue(1.0, "Fila 1", "Columna 1");
        	
		}
        
		
        //JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones por Estados","Estados","Cantidad",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        JFreeChart BarChartObject=ChartFactory.createStackedBarChart("Estadística de Preautorizaciones por Estados desde el " +sdf.format(fecha) +
        		" hasta el " + sdf.format(fechaHta),"Estados","Cantidad",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        
            
            GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
            KeyToGroupMap map = new KeyToGroupMap("G1");
            map.mapKeyToGroup("AU", "G1");
            map.mapKeyToGroup("RE", "G2");
            map.mapKeyToGroup("NR", "G3");
            map.mapKeyToGroup("OB", "G4");
            map.mapKeyToGroup("CA", "G5");
            map.mapKeyToGroup("GO", "G6");
            map.mapKeyToGroup("DE", "G7");
            renderer.setSeriesToGroupMap(map); 
        
// Personalizar        
        final CategoryPlot plot = BarChartObject.getCategoryPlot();
        final CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
                
                
        CategoryItemRenderer renderer1 = ((CategoryPlot)BarChartObject.getPlot()).getRenderer();

        renderer1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer1.setBaseItemLabelsVisible(true);
                
// Fin Personalizar        
        int width=640; 
        int height=480;                
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
        try {
			ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
		
        int my_picture_id = wb.addPicture(chart_out.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG );
        chart_out.close();
        HSSFPatriarch drawing = sheet.createDrawingPatriarch();
        HSSFClientAnchor my_anchor = new HSSFClientAnchor();
        
        my_anchor.setCol1((short)4);
        my_anchor.setRow1((short)5);
        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        my_picture.resize();
        
        } catch (IOException e) {
        	_log.error(e);
		}
        
        
        
        		
		return wb;
	}

	public static HSSFWorkbook generaReportePorDia(HSSFWorkbook wb,
			List<PreAutorizacion> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Diario");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
		
        for(PreAutorizacion estado: list){
			
        	my_bar_chart_dataset.addValue(estado.getId(),"Dia",sdf.format(estado.getFecha()));
//        	dataset.addValue(1.0, "Fila 1", "Columna 1");
        	
		}
        
		
        //JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones por Estados","Estados","Cantidad",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones Diaria desde el " +sdf1.format(fecha) +
        		" hasta el " + sdf1.format(fechaHta),"Fecha","Cantidad Enviados",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        
            
           
// Personalizar        
        final CategoryPlot plot = BarChartObject.getCategoryPlot();
        final CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
                
                
        CategoryItemRenderer renderer1 = ((CategoryPlot)BarChartObject.getPlot()).getRenderer();

        renderer1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer1.setBaseItemLabelsVisible(true);
                
// Fin Personalizar        
        int width=640; 
        int height=480;                
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
        try {
			ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
		
        int my_picture_id = wb.addPicture(chart_out.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG );
        chart_out.close();
        HSSFPatriarch drawing = sheet.createDrawingPatriarch();
        HSSFClientAnchor my_anchor = new HSSFClientAnchor();
        
        my_anchor.setCol1((short)4);
        my_anchor.setRow1((short)5);
        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        my_picture.resize();
        
        } catch (IOException e) {
        	_log.error(e);
		}
	
		return wb;
	}


	public static HSSFWorkbook generaReportePorSeccional(HSSFWorkbook wb,
			List<PreAutorizacion> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Seccional");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
		
        for(PreAutorizacion estado: list){
			
        	my_bar_chart_dataset.addValue(estado.getId(),"Seccional",estado.getSeccionalDescripcionAltaUsr());
//        	dataset.addValue(1.0, "Fila 1", "Columna 1");
        	
		}
        
		
        //JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones por Estados","Estados","Cantidad",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones Por Seccional desde el " +sdf1.format(fecha) +
        		" hasta el " + sdf1.format(fechaHta),"Seccional","Cantidad Enviados",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        
            
           
// Personalizar        
        final CategoryPlot plot = BarChartObject.getCategoryPlot();
        final CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
                
                
        CategoryItemRenderer renderer1 = ((CategoryPlot)BarChartObject.getPlot()).getRenderer();

        renderer1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer1.setBaseItemLabelsVisible(true);
                
// Fin Personalizar        
        int width=640; 
        int height=480;                
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
        try {
			ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
		
        int my_picture_id = wb.addPicture(chart_out.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG );
        chart_out.close();
        HSSFPatriarch drawing = sheet.createDrawingPatriarch();
        HSSFClientAnchor my_anchor = new HSSFClientAnchor();
        
        my_anchor.setCol1((short)4);
        my_anchor.setRow1((short)5);
        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        my_picture.resize();
        
        } catch (IOException e) {
        	_log.error(e);
		}
  	    return wb;
	}

	
	public static HSSFWorkbook generaReportePorMes(HSSFWorkbook wb,
			List<Estado> list,Date fecha,Date fechaHta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd");
		SimpleDateFormat sdf1=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		
		HSSFSheet sheet = wb.createSheet("Mensual");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
		
        for(Estado estado: list){
			
        	my_bar_chart_dataset.addValue(estado.getCantidadOcurrencias(),"Mes",estado.getId());
//        	dataset.addValue(1.0, "Fila 1", "Columna 1");
        	
		}
        
		
        //JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones por Estados","Estados","Cantidad",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        JFreeChart BarChartObject=ChartFactory.createBarChart("Estadística de Preautorizaciones Mensual desde el " +sdf1.format(fecha) +
        		" hasta el " + sdf1.format(fechaHta),"Fecha","Cantidad Enviados",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);
        
            
           
// Personalizar        
        final CategoryPlot plot = BarChartObject.getCategoryPlot();
        final CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
                
                
        CategoryItemRenderer renderer1 = ((CategoryPlot)BarChartObject.getPlot()).getRenderer();

        renderer1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer1.setBaseItemLabelsVisible(true);
                
// Fin Personalizar        
        int width=640; 
        int height=480;                
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
        try {
			ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
		
        int my_picture_id = wb.addPicture(chart_out.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG );
        chart_out.close();
        HSSFPatriarch drawing = sheet.createDrawingPatriarch();
        HSSFClientAnchor my_anchor = new HSSFClientAnchor();
        
        my_anchor.setCol1((short)4);
        my_anchor.setRow1((short)5);
        HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        my_picture.resize();
        
        } catch (IOException e) {
        	_log.error(e);
		}
        		
		return wb;
	}

	
}


