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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.action.ActionUtil;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.AfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.BusquedaReporteReclamoFiltro;
import ar.com.ospim.autorizaciones.beans.EstadosReclamosPrestacionales;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalExcel;
import ar.com.ospim.autorizaciones.beans.TiposDeGestionReclamosPrestacionales;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.StringUtils;

public class ReporteReclamosPrestacionales  extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteReclamosPrestacionales.class);

	static List<EstadosReclamosPrestacionales> listaestados = TraeListasServiceUtil.getEstadosReclamos();
	static List<TiposDeGestionReclamosPrestacionales> listatipogestionreclamos = TraeListasServiceUtil.getTiposGestionReclamosPrestacionales();
	static List<Provincia>provincias =TraeListasServiceUtil.getProvincias();
	static List<Localidad>localidades=TraeListasServiceUtil.getLocalidades();
	
	
	
	public static HSSFWorkbook generaReporteReclamosPrestacionales(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaOspimDia = ParamUtil.getString(renderRequest,"fechaOspimDia");
		String fechaOspimMes = ParamUtil.getString(renderRequest,"fechaOspimMes");
		String fechaOspimAnio = ParamUtil.getString(renderRequest,"fechaOspimAnio");
		Date fechaOspim = null;
		
		try {
			fechaOspim = formatoDeFechas.parse(fechaOspimDia + "/"
					+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
					+ fechaOspimAnio);
		} catch (Exception e) {
			fechaOspim = null;
		}
		String fechaOspimDia1 = ParamUtil.getString(renderRequest,"fechaOspimDiaHta");
		String fechaOspimMes1 = ParamUtil.getString(renderRequest,"fechaOspimMesHta");
		String fechaOspimAnio1 = ParamUtil.getString(renderRequest,	"fechaOspimAnioHta");
		Date fechaOspim1 = null;
		
		try {
			fechaOspim1= formatoDeFechas.parse(fechaOspimDia1 + "/" + (Integer.parseInt(fechaOspimMes1) + 1) + "/" 	+ fechaOspimAnio1);
		} catch (Exception e) {
			fechaOspim1= null;
		}
		// fechas cierre reclamo 
		
		String fechaCierreReclamoDia = ParamUtil.getString(renderRequest, "fechaCierreReclamoDia");
		String fechaCierreReclamoMes = ParamUtil.getString(renderRequest, "fechaCierreReclamoMes");
		String fechaCierreReclamoAnio  = ParamUtil.getString(renderRequest ,"fechaCierreReclamoAnio");
		Date fechaCierreReclamo= null;
		
		try {
			fechaCierreReclamo= formatoDeFechas.parse(fechaCierreReclamoDia + "/" + (Integer.parseInt(fechaCierreReclamoMes) + 1) + "/"
					+ fechaCierreReclamoAnio);
		} catch (Exception e) {
			fechaCierreReclamo = null;
		}
		
		String fechaCierreReclamoDia1 = ParamUtil.getString(renderRequest,	"fechaCierreReclamoDiaHta");
		String fechaCierreReclamoMes1 = ParamUtil.getString(renderRequest, 	"fechaCierreReclamoMesHta");
		String fechaCierreReclamoAnio1  = ParamUtil.getString(renderRequest,"fechaCierreReclamoAnioHta");
		Date fechaCierreReclamo1= null;
		
		try {
			fechaCierreReclamo1= formatoDeFechas.parse(fechaCierreReclamoDia1 + "/" + (Integer.parseInt(fechaCierreReclamoMes1) + 1) + "/"
					+ fechaCierreReclamoAnio1);
		} catch (Exception e) {
			fechaCierreReclamo1 = null;
		}
		
	// resto de parametros de la busqueda
		/*
		int numero = ParamUtil.getInteger(renderRequest, "numero", 0);
		String codPrest = ParamUtil.getString(renderRequest, "codPrest", null);
		String codPrestaci = ParamUtil.getString(renderRequest, "codPrestaci", null);
		String prestador = ParamUtil.getString(renderRequest, "prestador",	null);
		boolean antiguos= ParamUtil.getBoolean(renderRequest,"antiguos");
	    Integer nroAutorizacion=ParamUtil.getInteger(renderRequest, "nroautorizacion", 0);
		
*/
		int estado = ParamUtil.getInteger(renderRequest, "estado", 0);

		//PortletSession portletSession = renderRequest.getPortletSession();

		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int nroReclamo = ParamUtil.getInteger(renderRequest, "nroReclamo", 0);
		int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
		int tipoPrestacion= ParamUtil.getInteger(renderRequest, "tipoprestacion", 0);
		String code_prestacion = ParamUtil.getString(renderRequest, "code_prestacion", "0");
		int tiponomnecladorprestacion= ParamUtil.getInteger(renderRequest, "tiponomnecladorprestacion");
		int tipoNomencladorBuscado= ParamUtil.getInteger(renderRequest, "tiponomencladorbuscado"); 			
		String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular", null);
		int idPrestacion = 0;
		
		String codigotipogestion = ParamUtil.getString (renderRequest, "codigotipogestion", "0");
		String resolucion= ParamUtil.getString (renderRequest, "resolucion");
		
		String sectorSeleccionado =ParamUtil.getString (renderRequest, "sectorSeleccionado");
		String tipoPedido = ParamUtil.getString (renderRequest, "tipoPedido");
		Integer nroLote= ParamUtil.getInteger(renderRequest, "nrolote");
		
		
		//inicio Datos del Comprobante  
		String frecuencia = ParamUtil.getString(renderRequest, "frecuencia", null);
		String comprobanteTipo = ParamUtil.getString(renderRequest, "comprobante_tipo", null);
		String comprobanteSuc = ParamUtil.getString(renderRequest, "comprobante_suc", null);
		String comprobanteNro = ParamUtil.getString(renderRequest, "comprobante_nro", null);

		String fechaComprobanteDia = ParamUtil.getString(renderRequest,
				"fechaComprobanteDia");
		String fechaComprobanteMes = ParamUtil.getString(renderRequest,
				"fechaComprobanteMes");
		String fechaComprobanteAnio  = ParamUtil.getString(renderRequest,
				"fechaComprobanteAnio");
		Date fechaComprobante= null;
		
		try {
			fechaComprobante= formatoDeFechas.parse(fechaComprobanteDia + "/"
					+ (Integer.parseInt(fechaComprobanteMes) + 1) + "/"
					+ fechaComprobanteAnio);
		} catch (Exception e) {
			fechaComprobante = null;
		}			
		
		String cuitEntidad = ParamUtil.getString(renderRequest, "cuit_entidad", null);
		//fin Datos del Comprobante  
		
		
		int seccional = ParamUtil.getInteger(renderRequest, "seccional", 0);

		
		int codintegracion = ParamUtil.getInteger(renderRequest, "integracion", 0);
		
		int recuperableSur = ParamUtil.getInteger(renderRequest, "recuperable_sur", 0);
	    
	    // *************************************************************		
		//  fin  de carga de variables de la JSP 
	    // *************************************************************		
		
		List<ReclamoPrestacionalExcel> reclamosPrestacionales= new ArrayList<ReclamoPrestacionalExcel>();

		
		BusquedaReporteReclamoFiltro filtro = new BusquedaReporteReclamoFiltro(fechaOspim, fechaOspim1,inte,cuilTitular,nroReclamo,code_prestacion,tipoPrestacion ,estado,fechaCierreReclamo, 
																				fechaCierreReclamo1,  codigotipogestion,  resolucion, sectorSeleccionado ,tipoPedido,nroLote , 	 
																				frecuencia, comprobanteTipo, comprobanteSuc, comprobanteNro,fechaComprobante,cuitEntidad, seccional,
																				codintegracion, recuperableSur);
		
		try {
			reclamosPrestacionales= AutorizacionesServiceUtil.getListaReclamosPrestacionales (filtro);
			for(ReclamoPrestacionalExcel archivo:reclamosPrestacionales) {
			    try {
				   Afiliado a = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(archivo.getAfiliado().getCuil(), archivo.getAfiliado().getInte());
				   archivo.getAfiliado().setEmail(a.getEmail());
				   archivo.getAfiliado().setDomicilios(a.getDomicilios());
				   if(archivo.getAfiliado().getDomicilios()!=null) {
				      int indice = provincias.indexOf(archivo.getAfiliado().getDomicilioDefault().getProvincia());
				      if(indice!=-1) {
				        archivo.getAfiliado().getDomicilioDefault().setProvincia(provincias.get(indice));
				      }
				      indice = localidades.indexOf(archivo.getAfiliado().getDomicilioDefault().getLocalidad());
				      if(indice!=-1) {
				        archivo.getAfiliado().getDomicilioDefault().setLocalidad(localidades.get(indice));
				      }
				   }else {
					  Domicilio[] domicilios = null; 
					  archivo.getAfiliado().setDomicilios( domicilios); 
				   }
				}catch(Exception e) {
					_log.error("Error al generar reporte de reclamos prestacionales domicilios",e);
				}   
			}    
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de reclamos prestacionales",e);
			return null;
		}
		return generaReporteReclamosPrestacionales(reclamosPrestacionales, filtro);
	}

	private static HSSFWorkbook generaReporteReclamosPrestacionales(
			List<ReclamoPrestacionalExcel> list, BusquedaReporteReclamoFiltro filtro) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Ficha");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		//HSSFCellStyle styleBoldSize = getStyleBoldWithSize(wb,12);
		HSSFCellStyle styleNumber= getStyleNumber(wb);
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		//StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales: ").append(sdf.format(hoy));
		StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales ");
	
		//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 41));

		index++;
		HSSFRow row1 = sheet.createRow(index);
		HSSFCell cell1 = row1.createCell(0);

		StringBuffer fechaImpresion=new StringBuffer("Fecha impresión: " ).append(sdf2.format(hoy));

		
		cell1.setCellValue(new HSSFRichTextString(fechaImpresion.toString()));
		//cell1HA.setCellStyle(styleBold);
	
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 41));

		index++;
		HSSFRow rowSeparador = sheet.createRow(index++);

		
		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		StringBuffer aux = new StringBuffer("");
				
				if(filtro.getNroReclamo() > 0) {
					aux.append(" Nro Reclamo: " + filtro.getNroReclamo());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getTipoPedido())) {
					aux.append(" Tipo Pedido: " + filtro.getTipoPedido());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getSectorSeleccionado())) {
					aux.append(" Sector: " + filtro.getSectorSeleccionado());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getResolucion())) {
					aux.append(" getResolucion: " + filtro.getResolucion());
				}
		
				if(filtro.getEstado() == -1) {
					aux.append(" Estados: " + "TODOS");
				}else{
					for (EstadosReclamosPrestacionales estado : listaestados) {
						if (filtro.getEstado() == estado.getId()){
							aux.append(" Estados: " + estado.getDescripcion());
						}
					}
				}
				
				if("0".equals(filtro.getCodigoTipoGestion())) {
					aux.append(" Tipo Gestión: " + "TODOS");
				}else{
					 for (TiposDeGestionReclamosPrestacionales tipoGestion  : listatipogestionreclamos) {
						 if(filtro.getCodigoTipoGestion().equals(tipoGestion.getId())){
								aux.append(" Tipo Gestión: " + tipoGestion.getDescripcion());
						 }
					 }
				}
				
				if(filtro.getNroLote() > 0){
					aux.append(" Nro.Lote: " + filtro.getNroLote());
				}
				

				if(filtro.getSeccional() > 0){
					Seccional seccional = null;
					try {
						seccional = SeccionalServiceUtil.buscarSeccionalById(filtro.getSeccional());
						aux.append(" Seccional: " + seccional.getDescripcion());
					} catch (Exception e) {
						_log.debug("Error  al obtener seccional");
					}
				}
				
				if(filtro.getFechaOspim()!= null && filtro.getFechaOspim1() != null) {
					aux.append(" Fecha. Ospim : " + sdf.format(filtro.getFechaOspim()) + " y " + sdf.format(filtro.getFechaOspim1()));
				}
				
				if(filtro.getFechaCierre() == null && filtro.getFechaCierre1() != null) {
					aux.append(" Fecha. Cierre : " + sdf.format(filtro.getFechaCierre()) + " y " + sdf.format(filtro.getFechaCierre1()));		
				}
				
				//afiliado
				if(StringUtils.checkNotEmpty(filtro.getCuilTitular())) {
					aux.append(" Cuil Titular: " + filtro.getCuilTitular());
				}
				
				if(filtro.getInte() > 0) {
					aux.append(" Integrante: " + filtro.getInte());
				}
				//comprobante
			
				
				if(StringUtils.checkNotEmpty(filtro.getFrecuencia())) {
					aux.append(" Frecuencia: ");
					if("SELECCIONE".equalsIgnoreCase(filtro.getFrecuencia())) { 
						aux.append(" TODOS "); 
					  }else if("UNICA".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" UNICA "); 
					  }else if("SEMANAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMANAL "); 
					  }else if("TRIMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" TRIMESTRAL "); 
					  }else if("MENSUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" MENSUAL ");	  
					  }else if("SEMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMESTRAL ");	
					  }else if("ANUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" ANUAL "); 
					  }
					
					if(StringUtils.checkNotEmpty(filtro.getComprobanteTipo())) {
						aux.append(" Comprobante: ");
						if("Seleccione".equalsIgnoreCase(filtro.getComprobanteTipo())) { 
							aux.append(" TODOS "); 
						  }else if("FCP".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" FCP "); 
						  }else if("RCB".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" RCB "); 
						  }else if("OTR".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" OTRO "); 
						  }else if("AUT".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" AUTORIZACION ");	  
						  }
					}
						
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 
				}
				
				
				if(StringUtils.checkNotEmpty(filtro.getNumeroComprobante())) {
					  aux.append(" Nro " + filtro.getNumeroComprobante());	 
				}
				
			
				if(filtro.getFechaComprobante() != null) {
					aux.append(" F.Emision: " + sdf.format(filtro.getFechaComprobante()) );
				}
				
				if(StringUtils.checkNotEmpty(filtro.getCuitEntidadComprobante())) {
					  aux.append(" CUIT " + filtro.getCuitEntidadComprobante());	 

				}
				
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 

				}
				
		}
	
				

		cell2.setCellValue(new HSSFRichTextString(aux.toString()));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 41));
		
		
		//index++;
		HSSFRow rowHeader = sheet.createRow(index);
	
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Nro Reclamo"));
		cell0H.setCellStyle(styleBold);
				
		HSSFCell cell01H = rowHeader.createCell(++col);
		cell01H.setCellValue(new HSSFRichTextString("Fecha OSPIM"));
		cell01H.setCellStyle(styleBold);
		
		HSSFCell cell02H = rowHeader.createCell(++col);
		cell02H.setCellValue(new HSSFRichTextString("Fecha Seccional"));
		cell02H.setCellStyle(styleBold);
		
		HSSFCell cell03H = rowHeader.createCell(++col);
		cell03H.setCellValue(new HSSFRichTextString("Sector"));
		cell03H.setCellStyle(styleBold);
		
		
		HSSFCell cell04H = rowHeader.createCell(++col);
		cell04H.setCellValue(new HSSFRichTextString("Tipo Pedido"));
		cell04H.setCellStyle(styleBold);
		

		HSSFCell cell05H = rowHeader.createCell(++col);
		cell05H.setCellValue(new HSSFRichTextString("Estado"));
		cell05H.setCellStyle(styleBold);
		
		HSSFCell cell06H = rowHeader.createCell(++col);
		cell06H.setCellValue(new HSSFRichTextString("Baja Fecha"));
		cell06H.setCellStyle(styleBold);
		
		
		HSSFCell cell07H = rowHeader.createCell(++col);
		cell07H.setCellValue(new HSSFRichTextString("Amparo"));
		cell07H.setCellStyle(styleBold);
		
		/*HSSFCell cell08H = rowHeader.createCell(++col);
		cell08H.setCellValue(new HSSFRichTextString("En Tramite"));
		cell08H.setCellStyle(styleBold);*/
		
		
	/*	HSSFCell cell09H = rowHeader.createCell(++col);
		cell09H.setCellValue(new HSSFRichTextString("Recuperable"));
		cell09H.setCellStyle(styleBold);
		*/
		

		//HSSFCell cell10H = rowHeader.createCell(++col);
		//cell10H.setCellValue(new HSSFRichTextString("Superintendencia"));
		//cell10H.setCellStyle(styleBold);
		
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Caso Asociado"));
		cell11H.setCellStyle(styleBold);
		
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Inte"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Nro Doc"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Apellido Afiliado"));
		cell15H.setCellStyle(styleBold);

		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Nombre Afiliado"));
		cell16H.setCellStyle(styleBold);		

		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Seccional Afiliado"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell17H1 = rowHeader.createCell(++col);
		cell17H1.setCellValue(new HSSFRichTextString("Provincia"));
		cell17H1.setCellStyle(styleBold);
		
		HSSFCell cell17H2 = rowHeader.createCell(++col);
		cell17H2.setCellValue(new HSSFRichTextString("Localidad"));
		cell17H2.setCellStyle(styleBold);
		
		HSSFCell cell17H3 = rowHeader.createCell(++col);
		cell17H3.setCellValue(new HSSFRichTextString("Domicilio"));
		cell17H3.setCellStyle(styleBold);
		
		HSSFCell cell17H4 = rowHeader.createCell(++col);
		cell17H4.setCellValue(new HSSFRichTextString("Email"));
		cell17H4.setCellStyle(styleBold);
	
		HSSFCell cell17H5 = rowHeader.createCell(++col);
		cell17H5.setCellValue(new HSSFRichTextString("Teléfono"));
		cell17H5.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Plan Molineros"));
		cell18H.setCellStyle(styleBold);

		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Plan Tercerizadora"));
		cell19H.setCellStyle(styleBold);
		
		

		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Código"));
		cell20H.setCellStyle(styleBold);
	
	

		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Prestación"));
		cell21H.setCellStyle(styleBold);
	
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Est. Prestación"));
		cell22H.setCellStyle(styleBold);		
	
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Frecuencia"));
		cell23H.setCellStyle(styleBold);
		
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell24H.setCellStyle(styleBold);
	
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("Importe"));
		cell25H.setCellStyle(styleBold);
		//cell22H.setCellStyle(styleMoneyRight);
		
		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Total"));
		cell26H.setCellStyle(styleBold);
		//cell23H.setCellStyle(styleMoneyRight);
		
		
		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Cargo OSPIM"));
		cell27H.setCellStyle(styleBold);		
		//cell24H.setCellStyle(styleMoneyRight);
		
		
		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Cargo Terc."));
		cell28H.setCellStyle(styleBold);
		//cell25H.setCellStyle(styleMoneyRight);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Revisión Res."));
		cell29H.setCellStyle(styleBold);
		
		HSSFCell cell30H = rowHeader.createCell(++col);
		cell30H.setCellValue(new HSSFRichTextString("Resp Revisión"));
		cell30H.setCellStyle(styleBold);
		

		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Observaciones Auditoría Médica"));
		cell31H.setCellStyle(styleBold);
		
		HSSFCell cell310H = rowHeader.createCell(++col);
		cell310H.setCellValue(new HSSFRichTextString("Observaciones Revisión"));
		cell310H.setCellStyle(styleBold);
		
		HSSFCell cell311H = rowHeader.createCell(++col);
		cell311H.setCellValue(new HSSFRichTextString("Observaciones Cierre"));
		cell311H.setCellStyle(styleBold);
		
		HSSFCell cell312H = rowHeader.createCell(++col);
		cell312H.setCellValue(new HSSFRichTextString("Justificación Médica"));
		cell312H.setCellStyle(styleBold);
		
		HSSFCell cell313H = rowHeader.createCell(++col);
		cell313H.setCellValue(new HSSFRichTextString("Dictamen Comisión"));
		cell313H.setCellStyle(styleBold);
		
		HSSFCell cell32H = rowHeader.createCell(++col);
		cell32H.setCellValue(new HSSFRichTextString("Fecha Cierre"));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33H = rowHeader.createCell(++col);
		cell33H.setCellValue(new HSSFRichTextString("Incluido Convenio"));
		cell33H.setCellStyle(styleBold);
		

		
		HSSFCell cell34H = rowHeader.createCell(++col);
		cell34H.setCellValue(new HSSFRichTextString("2 % "));
		cell34H.setCellStyle(styleBold);

		
		HSSFCell cell35H = rowHeader.createCell(++col);
		cell35H.setCellValue(new HSSFRichTextString("Débito Prestadora"));
		cell35H.setCellStyle(styleBold);
		
		HSSFCell cell36H = rowHeader.createCell(++col);
		cell36H.setCellValue(new HSSFRichTextString("Tipo Gestión"));
		cell36H.setCellStyle(styleBold);
		

		HSSFCell cell37H = rowHeader.createCell(++col);
		cell37H.setCellValue(new HSSFRichTextString("Nro OP"));
		cell37H.setCellStyle(styleBold);
		
		HSSFCell cell371H = rowHeader.createCell(++col);
		cell371H.setCellValue(new HSSFRichTextString("Fecha OP"));
		cell371H.setCellStyle(styleBold);
		

		HSSFCell cell38H = rowHeader.createCell(++col);
		cell38H.setCellValue(new HSSFRichTextString("Lote"));
		cell38H.setCellStyle(styleBold);
		
		HSSFCell cell39H = rowHeader.createCell(++col);
		cell39H.setCellValue(new HSSFRichTextString("Permanencia"));
		cell39H.setCellStyle(styleBold);
		
		
		HSSFCell cell40H = rowHeader.createCell(++col);
		cell40H.setCellValue(new HSSFRichTextString("Fecha Envio Seccional"));
		cell40H.setCellStyle(styleBold);
		
		HSSFCell cell41H = rowHeader.createCell(++col);
		cell41H.setCellValue(new HSSFRichTextString("Descripción Seccional"));
		cell41H.setCellStyle(styleBold);
		

		HSSFCell cell42H = rowHeader.createCell(++col);
		cell42H.setCellValue(new HSSFRichTextString("Recupero"));
		cell42H.setCellStyle(styleBold);
		
		HSSFCell cell43H = rowHeader.createCell(++col);
		cell43H.setCellValue(new HSSFRichTextString("Integración"));
		cell43H.setCellStyle(styleBold);
		
		HSSFCell cell44H = rowHeader.createCell(++col);
		cell44H.setCellValue(new HSSFRichTextString("Reconocido SSS"));
		cell44H.setCellStyle(styleBold);
		
		HSSFCell cell45H = rowHeader.createCell(++col);
		cell45H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell45H.setCellStyle(styleBold);
		
		index++;
		
		for(ReclamoPrestacionalExcel  autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
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
		sheet.autoSizeColumn((short) 191);
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
		sheet.autoSizeColumn((short) 36);
		sheet.autoSizeColumn((short) 37);
		sheet.autoSizeColumn((short) 38);
		sheet.autoSizeColumn((short) 39);
		sheet.autoSizeColumn((short) 40);
		sheet.autoSizeColumn((short) 41);
		sheet.autoSizeColumn((short) 42);
		sheet.autoSizeColumn((short) 43);
		sheet.autoSizeColumn((short) 44);
		sheet.autoSizeColumn((short) 45);
		sheet.autoSizeColumn((short) 46);
		sheet.autoSizeColumn((short) 47);
		sheet.autoSizeColumn((short) 48);
		sheet.autoSizeColumn((short) 49);
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,ReclamoPrestacionalExcel   autorizaciones, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		Integer difDia=0;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell01 = rowHeader.createCell(++col);
		cell01.setCellValue(new HSSFRichTextString(String.valueOf(autorizaciones.getNroReclamo() )));
		cell01.setCellStyle(styleAll);
		
		
		HSSFCell cell02 = rowHeader.createCell(++col);
		cell02.setCellValue(new HSSFRichTextString(autorizaciones.getOspim_fechaAsString() ));
		cell02.setCellStyle(styleDate);
		
		HSSFCell cell03 = rowHeader.createCell(++col);
		cell03.setCellValue(new HSSFRichTextString(autorizaciones.getSeccional_fechaAsString() ));
		cell03.setCellStyle(styleDate);
		
		
		HSSFCell cell04 = rowHeader.createCell(++col);
		cell04.setCellValue(new HSSFRichTextString(autorizaciones.getSector() ));
		cell04.setCellStyle(styleAll);
		
		HSSFCell cell05 = rowHeader.createCell(++col);
		cell05.setCellValue(new HSSFRichTextString(autorizaciones.getTipoPedido()));
		cell05.setCellStyle(styleAll);
		
		
		HSSFCell cell06 = rowHeader.createCell(++col);
		cell06.setCellValue(new HSSFRichTextString(autorizaciones.getEstadoReclamoPrestacion() ));
		cell06.setCellStyle(styleNumber);
		
			
		HSSFCell cell07 = rowHeader.createCell(++col);
		cell07.setCellValue(new HSSFRichTextString(autorizaciones.getBaja_fechaAsString()));
		cell07.setCellStyle(styleAll);
		
		HSSFCell cell08 = rowHeader.createCell(++col);
		cell08.setCellValue(new HSSFRichTextString(autorizaciones.getAmparoTexto()));
		cell08.setCellStyle(styleAll);
		

		/*HSSFCell cell09 = rowHeader.createCell(++col);
		cell09.setCellValue(new HSSFRichTextString(autorizaciones.getReclamoEnTramite() ));
		cell09.setCellStyle(styleAll);*/
		

		
	/*	HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(autorizaciones.getReclamoRecuperable()  ));
		cell10.setCellStyle(styleAll);*/
		
		
		//HSSFCell cell11 = rowHeader.createCell(++col);
		//cell11.setCellValue(new HSSFRichTextString(autorizaciones.getReclamoSuperIntendencia() ));
		//cell11.setCellStyle(styleAll);
		
		

		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString( String.valueOf( autorizaciones.getCaso_vinculado()) ));
		cell12.setCellStyle(styleNumber);
		
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getCuil()   ));
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getInteAsString()  ));
		cell14.setCellStyle(styleNumber);
	
		HSSFCell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getDocu_numero()  ));
		cell15.setCellStyle(styleNumber);

		
		HSSFCell cell16 = rowHeader.createCell(++col);
		cell16.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getApellido() ));
		cell16.setCellStyle(styleNumber);
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getNombre()  ));
		cell17.setCellStyle(styleAll);
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(autorizaciones.getTextoSeccional()   ));
		cell18.setCellStyle(styleAll);
		
		HSSFCell cell181 = rowHeader.createCell(++col);
		cell181.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado()!=null && autorizaciones.getAfiliado().getDomicilios()!=null &&
				autorizaciones.getAfiliado().getDomicilioDefault().getProvinciaAsString()!=null ? autorizaciones.getAfiliado().getDomicilioDefault().getProvinciaAsString():"" ));
		cell181.setCellStyle(styleAll);
		
		HSSFCell cell182 = rowHeader.createCell(++col);
		cell182.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado()!=null && autorizaciones.getAfiliado().getDomicilios()!=null && autorizaciones.getAfiliado().getDomicilioDefault().getLocalidadAsString()!=null &&
				autorizaciones.getAfiliado().getDomicilioDefault().getLocalidadAsString()!=null ? autorizaciones.getAfiliado().getDomicilioDefault().getLocalidadAsString():""));
		cell182.setCellStyle(styleAll);
		
		String strDomicilio="";
		if(autorizaciones.getAfiliado().getDomicilios()!=null) {
			if(autorizaciones.getAfiliado().getDomicilioDefault().getCalle()!=null) {
				strDomicilio +=autorizaciones.getAfiliado().getDomicilioDefault().getCalle();
			}
			if(autorizaciones.getAfiliado().getDomicilioDefault().getNumero()!=null) {
				strDomicilio+= " " + autorizaciones.getAfiliado().getDomicilioDefault().getNumero();
			}
			if(autorizaciones.getAfiliado().getDomicilioDefault().getPiso()!=null) {
				strDomicilio += " " + autorizaciones.getAfiliado().getDomicilioDefault().getPiso();
			}
			if(autorizaciones.getAfiliado().getDomicilioDefault().getDepto()!=null) {
				strDomicilio += " " +autorizaciones.getAfiliado().getDomicilioDefault().getDepto();
			}
		}
		HSSFCell cell183 = rowHeader.createCell(++col);
		cell183.setCellValue(new HSSFRichTextString(strDomicilio));
		cell183.setCellStyle(styleAll);
		
		HSSFCell cell184 = rowHeader.createCell(++col);
		cell184.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getEmail()!=null ?autorizaciones.getAfiliado().getEmail():""));
		cell184.setCellStyle(styleAll);
		
		String strTelefono=""; 
		if(autorizaciones.getAfiliado().getDomicilios()!=null) {
		   if(	autorizaciones.getAfiliado().getDomicilioDefault().getCod_area_telefono() !=null) {
			   strTelefono+=autorizaciones.getAfiliado().getDomicilioDefault().getCod_area_telefono();
		   }
		   if(autorizaciones.getAfiliado().getDomicilioDefault().getTelefono()!=null) {
			   strTelefono += autorizaciones.getAfiliado().getDomicilioDefault().getTelefono();
		   }  
		   
		   if(	autorizaciones.getAfiliado().getDomicilioDefault().getCod_area_celular() !=null) {
			   strTelefono+="  "+autorizaciones.getAfiliado().getDomicilioDefault().getCod_area_celular();
		   }
		   if(autorizaciones.getAfiliado().getDomicilioDefault().getCelular()!=null) {
			   strTelefono += autorizaciones.getAfiliado().getDomicilioDefault().getCelular();
		   } 
		}       
		HSSFCell cell185 = rowHeader.createCell(++col);
		cell185.setCellValue(new HSSFRichTextString(strTelefono));
		cell185.setCellStyle(styleAll);
		
		
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(autorizaciones.getAfiliado().getNombrePlan() ));
		cell19.setCellStyle(styleAll);
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(new HSSFRichTextString(autorizaciones.getPlanPrevencion()  ));
		cell20.setCellStyle(styleAll);
		


		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString( String.valueOf( autorizaciones.getTroquel() ) ));
		cell21.setCellStyle(styleNumber);
	
		
		
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionTexto()  ));
		cell22.setCellStyle(styleAll);
	
			
		
		HSSFCell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionEstado() ));
		cell23.setCellStyle(styleAll);
	
	
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionFrecuencia() ));
		cell24.setCellStyle(styleAll);
		
		HSSFCell cell25 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionCantidad() >0){
			   cell25.setCellValue(autorizaciones.getPrestacionCantidad() );
			   cell25.setCellStyle(styleMoneyRight);
		 }else{
		       cell25.setCellValue(0);
		       cell25.setCellStyle(styleMoneyRight);
		 }
		
	
		HSSFCell cell26 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionImporte() >0){
			   cell26.setCellValue(autorizaciones.getPrestacionImporte()  );
			   cell26.setCellStyle(styleMoneyRight);
		    }else{
		       cell26.setCellValue(0);
		       cell26.setCellStyle(styleMoneyRight);
		}
		
		HSSFCell cell27 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionTotalImporte() >0){
			   cell27.setCellValue(autorizaciones.getPrestacionTotalImporte()   );
			   cell27.setCellStyle(styleMoneyRight);
		    }else{
		       cell27.setCellValue(0);
		       cell27.setCellStyle(styleMoneyRight);
			}
		
		

		HSSFCell cell28 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionCargoOspim() >0){
			   cell28.setCellValue(autorizaciones.getPrestacionCargoOspim() );
			   cell28.setCellStyle(styleMoneyRight);
		    }else{
		       cell28.setCellValue(0);
		       cell28.setCellStyle(styleMoneyRight);
			}
	
		
		HSSFCell cell29 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionCargoPs() >0){
			     cell29.setCellValue(autorizaciones.getPrestacionCargoPs() );
			     cell29.setCellStyle(styleMoneyRight);
		    }else{
		    	 cell29.setCellValue(0);
		    	 cell29.setCellStyle(styleMoneyRight);
			}
	
		

		HSSFCell cell30= rowHeader.createCell(++col);
		cell30.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion()  ));
		cell30.setCellStyle(styleAll);

		HSSFCell cell31= rowHeader.createCell(++col);
		cell31.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResponsable()));
		cell31.setCellStyle(styleAll);
		

		HSSFCell cell32= rowHeader.createCell(++col);
		cell32.setCellValue(new HSSFRichTextString(autorizaciones.getObsAuditoriaMedica()));
		cell32.setCellStyle(styleAll);
		

		HSSFCell cell321= rowHeader.createCell(++col);
		cell321.setCellValue(new HSSFRichTextString(autorizaciones.getObsRevision()));
		cell321.setCellStyle(styleAll);
		
		HSSFCell cell322= rowHeader.createCell(++col);
		cell322.setCellValue(new HSSFRichTextString(autorizaciones.getObsCierre()));
		cell322.setCellStyle(styleAll);
		
		HSSFCell cell323= rowHeader.createCell(++col);
		cell323.setCellValue(new HSSFRichTextString(autorizaciones.getJustificacionMedica()));
		cell323.setCellStyle(styleAll);
		
		HSSFCell cell324= rowHeader.createCell(++col);
		cell324.setCellValue(new HSSFRichTextString(autorizaciones.getDictamenComision()));
		cell324.setCellStyle(styleAll);
		
		
		HSSFCell cell33= rowHeader.createCell(++col);		
		if (autorizaciones.getPrestacionRevisionResolucion()!=null){
			cell33.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion().equals("RECHAZADO")?"":autorizaciones.getFecha_cierre_Texto() ));	
		}else{
			cell33.setCellValue(new HSSFRichTextString(""));
		}	
				
		cell33.setCellStyle(styleDate);

		
	
	
		
		HSSFCell cell34= rowHeader.createCell(++col);
		if (autorizaciones.getPrestacionRevisionResolucion()!=null){
		    cell34.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion().equals("RECHAZADO")?"":autorizaciones.getCierreIncluidoGerenciadoraTexto()  ));	
		}else{
			cell34.setCellValue(new HSSFRichTextString(""));
		}
		
		cell34.setCellStyle(styleAll);

		HSSFCell cell35= rowHeader.createCell(++col);
		if (autorizaciones.getPrestacionRevisionResolucion()!=null){
			cell35.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion().equals("RECHAZADO")?"":autorizaciones.getCierreDosPorCientoTexto() ));	
		}else{
			cell35.setCellValue(new HSSFRichTextString(""));
		}			
		
		cell35.setCellStyle(styleAll);

		HSSFCell cell36= rowHeader.createCell(++col);
		if (autorizaciones.getPrestacionRevisionResolucion()!=null){
			cell36.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion().equals("RECHAZADO")?"":autorizaciones.getCierreDebitoPrestadoraTexto()   ));	
		}else{
			cell36.setCellValue(new HSSFRichTextString(""));
		}		
		cell36.setCellStyle(styleAll);
		
		HSSFCell cell37= rowHeader.createCell(++col);
		if (autorizaciones.getPrestacionRevisionResolucion()!=null){
			cell37.setCellValue(new HSSFRichTextString(autorizaciones.getPrestacionRevisionResolucion().equals("RECHAZADO")?"":autorizaciones.getCierreTipoGestion()  ));	
		}
		else{
			cell37.setCellValue(new HSSFRichTextString(""));
		}
		
		cell37.setCellStyle(styleAll);
		
		HSSFCell cell38= rowHeader.createCell(++col);
		cell38.setCellValue(new HSSFRichTextString(autorizaciones.getNroOpLiquidacionReintegro()   ));
		cell38.setCellStyle(styleAll);
		
		
		HSSFCell cell381= rowHeader.createCell(++col);
		cell381.setCellValue(new HSSFRichTextString(autorizaciones.getfechaOPAsString()  ));
		cell381.setCellStyle(styleAll);
		
		
		HSSFCell cell39= rowHeader.createCell(++col);
		if (autorizaciones.getNroLote() !=null){
			cell39.setCellValue(autorizaciones.getNroLote());
		}
		else{
			cell39.setCellValue(new HSSFRichTextString(""));
		}
		cell39.setCellStyle(styleAll);
		
		//inicio permanencia
		String msgPermanencia="";
		try {
		   difDia=AfiliadoServiceUtil.permanenciaDesdeUltimoLaboral(autorizaciones.getAfiliado().getCuil(), 0, "8,10,12",autorizaciones.getOspim_fecha());
		}catch (Exception e) {
		   difDia=0;
		}
 		if(difDia>0) {
 		  if(difDia<181) {
			   msgPermanencia="Entre 3 y 6 meses";
			   if(difDia<91) {
				   msgPermanencia="Menor a 3 meses";
			   }
		   }
 		} 
 		HSSFCell cell41= rowHeader.createCell(++col);
 		cell41.setCellValue(msgPermanencia);
		cell41.setCellStyle(styleAll);
		//fin permanencia

		HSSFCell cell42 = rowHeader.createCell(++col);
		cell42.setCellValue(new HSSFRichTextString(autorizaciones.getFechaMailSeccional_fechaAsString() ));
		cell42.setCellStyle(styleDate);
		
		
		HSSFCell cell43= rowHeader.createCell(++col);
		if (autorizaciones.getDescAltaSeccional() !=null){
			cell43.setCellValue(autorizaciones.getDescAltaSeccional());
		}
		else{
			cell43.setCellValue(new HSSFRichTextString(""));
		}
		cell43.setCellStyle(styleAll);
	
		
		HSSFCell cell44= rowHeader.createCell(++col);
		cell44.setCellValue(autorizaciones.getRecuperableSur());
		cell44.setCellStyle(styleAll);
		
		
		HSSFCell cell45= rowHeader.createCell(++col);
		if (autorizaciones.getDescIntegracion() !=null){
			cell45.setCellValue(autorizaciones.getDescIntegracion());
		}
		else{
			cell45.setCellValue(new HSSFRichTextString(""));
		}
		cell45.setCellStyle(styleAll);
		
		HSSFCell cell46 = rowHeader.createCell(++col);		
		if( autorizaciones.getReconocidoSSS() >0){
			     cell46.setCellValue(autorizaciones.getReconocidoSSS() );
			     cell46.setCellStyle(styleMoneyRight);
		    }else{
		    	 cell46.setCellValue(0);
		    	 cell46.setCellStyle(styleMoneyRight);
			}
	
	
		HSSFCell cell47= rowHeader.createCell(++col);
		if (autorizaciones.getDiscapacitado() !=null){
			cell47.setCellValue(autorizaciones.getDiscapacitado());
		}
		else{
			cell47.setCellValue(new HSSFRichTextString(""));
		}
		cell47.setCellStyle(styleAll);
		
		return index++;
	}	
	
	
/////////////////////////////
////////////////////////////

	public static HSSFWorkbook generaReporteReclamosPrestacionales(
			List<ReclamoPrestacionalExcel> list, BusquedaReporteReclamoFiltro filtro,HSSFWorkbook wb) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Date hoy=new Date();
		
		HSSFSheet sheet = wb.createSheet("Reclamos Prestacionales");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		//HSSFCellStyle styleBoldSize = getStyleBoldWithSize(wb,12);
		HSSFCellStyle styleNumber= getStyleNumber(wb);
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		//StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales: ").append(sdf.format(hoy));
		StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales ");
	
		//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 41));

		index++;
		HSSFRow row1 = sheet.createRow(index);
		HSSFCell cell1 = row1.createCell(0);

		StringBuffer fechaImpresion=new StringBuffer("Fecha impresión: " ).append(sdf2.format(hoy));

		
		cell1.setCellValue(new HSSFRichTextString(fechaImpresion.toString()));
		//cell1HA.setCellStyle(styleBold);
	
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 41));

		index++;
		HSSFRow rowSeparador = sheet.createRow(index++);

		
		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		StringBuffer aux = new StringBuffer("");
				
				if(filtro.getNroReclamo() > 0) {
					aux.append(" Nro Reclamo: " + filtro.getNroReclamo());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getTipoPedido())) {
					aux.append(" Tipo Pedido: " + filtro.getTipoPedido());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getSectorSeleccionado())) {
					aux.append(" Sector: " + filtro.getSectorSeleccionado());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getResolucion())) {
					aux.append(" getResolucion: " + filtro.getResolucion());
				}
		
				if(filtro.getEstado() == -1) {
					aux.append(" Estados: " + "TODOS");
				}else{
					for (EstadosReclamosPrestacionales estado : listaestados) {
						if (filtro.getEstado() == estado.getId()){
							aux.append(" Estados: " + estado.getDescripcion());
						}
					}
				}
				
				if("0".equals(filtro.getCodigoTipoGestion())) {
					aux.append(" Tipo Gestión: " + "TODOS");
				}else{
					 for (TiposDeGestionReclamosPrestacionales tipoGestion  : listatipogestionreclamos) {
						 if(filtro.getCodigoTipoGestion().equals(tipoGestion.getId())){
								aux.append(" Tipo Gestión: " + tipoGestion.getDescripcion());
						 }
					 }
				}
				
				if(filtro.getNroLote()!=null && filtro.getNroLote() > 0){
					aux.append(" Nro.Lote: " + filtro.getNroLote());
				}
				

				if(filtro.getSeccional() > 0){
					Seccional seccional = null;
					try {
						seccional = SeccionalServiceUtil.buscarSeccionalById(filtro.getSeccional());
						aux.append(" Seccional: " + seccional.getDescripcion());
					} catch (Exception e) {
						_log.debug("Error  al obtener seccional");
					}
				}
				
				if(filtro.getFechaOspim()!= null && filtro.getFechaOspim1() != null) {
					aux.append(" Fecha. Ospim : " + sdf.format(filtro.getFechaOspim()) + " y " + sdf.format(filtro.getFechaOspim1()));
				}
				
				if(filtro.getFechaCierre() == null && filtro.getFechaCierre1() != null) {
					aux.append(" Fecha. Cierre : " + sdf.format(filtro.getFechaCierre()) + " y " + sdf.format(filtro.getFechaCierre1()));		
				}
				
				//afiliado
				if(StringUtils.checkNotEmpty(filtro.getCuilTitular())) {
					aux.append(" Cuil Titular: " + filtro.getCuilTitular());
				}
				
				if(filtro.getInte() > 0) {
					aux.append(" Integrante: " + filtro.getInte());
				}
				//comprobante
			
				
				if(StringUtils.checkNotEmpty(filtro.getFrecuencia())) {
					aux.append(" Frecuencia: ");
					if("SELECCIONE".equalsIgnoreCase(filtro.getFrecuencia())) { 
						aux.append(" TODOS "); 
					  }else if("UNICA".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" UNICA "); 
					  }else if("SEMANAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMANAL "); 
					  }else if("TRIMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" TRIMESTRAL "); 
					  }else if("MENSUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" MENSUAL ");	  
					  }else if("SEMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMESTRAL ");	
					  }else if("ANUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" ANUAL "); 
					  }
					
					if(StringUtils.checkNotEmpty(filtro.getComprobanteTipo())) {
						aux.append(" Comprobante: ");
						if("Seleccione".equalsIgnoreCase(filtro.getComprobanteTipo())) { 
							aux.append(" TODOS "); 
						  }else if("FCP".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" FCP "); 
						  }else if("RCB".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" RCB "); 
						  }else if("OTR".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" OTRO "); 
						  }else if("AUT".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" AUTORIZACION ");	  
						  }
					}
						
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 
				}
				
				
				if(StringUtils.checkNotEmpty(filtro.getNumeroComprobante())) {
					  aux.append(" Nro " + filtro.getNumeroComprobante());	 
				}
				
			
				if(filtro.getFechaComprobante() != null) {
					aux.append(" F.Emision: " + sdf.format(filtro.getFechaComprobante()) );
				}
				
				if(StringUtils.checkNotEmpty(filtro.getCuitEntidadComprobante())) {
					  aux.append(" CUIT " + filtro.getCuitEntidadComprobante());	 

				}
				
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 

				}
				
		}
	
				

		cell2.setCellValue(new HSSFRichTextString(aux.toString()));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 41));
		
		
		//index++;
		HSSFRow rowHeader = sheet.createRow(index);
	
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Nro Reclamo"));
		cell0H.setCellStyle(styleBold);
				
		HSSFCell cell01H = rowHeader.createCell(++col);
		cell01H.setCellValue(new HSSFRichTextString("Fecha OSPIM"));
		cell01H.setCellStyle(styleBold);
		
		HSSFCell cell02H = rowHeader.createCell(++col);
		cell02H.setCellValue(new HSSFRichTextString("Fecha Seccional"));
		cell02H.setCellStyle(styleBold);
		
		HSSFCell cell03H = rowHeader.createCell(++col);
		cell03H.setCellValue(new HSSFRichTextString("Sector"));
		cell03H.setCellStyle(styleBold);
		
		
		HSSFCell cell04H = rowHeader.createCell(++col);
		cell04H.setCellValue(new HSSFRichTextString("Tipo Pedido"));
		cell04H.setCellStyle(styleBold);
		

		HSSFCell cell05H = rowHeader.createCell(++col);
		cell05H.setCellValue(new HSSFRichTextString("Estado"));
		cell05H.setCellStyle(styleBold);
		
		HSSFCell cell06H = rowHeader.createCell(++col);
		cell06H.setCellValue(new HSSFRichTextString("Baja Fecha"));
		cell06H.setCellStyle(styleBold);
		
		
		HSSFCell cell07H = rowHeader.createCell(++col);
		cell07H.setCellValue(new HSSFRichTextString("Amparo"));
		cell07H.setCellStyle(styleBold);
		/*
		HSSFCell cell08H = rowHeader.createCell(++col);
		cell08H.setCellValue(new HSSFRichTextString("En Tramite"));
		cell08H.setCellStyle(styleBold);
		
		
		HSSFCell cell09H = rowHeader.createCell(++col);
		cell09H.setCellValue(new HSSFRichTextString("Recuperable"));
		cell09H.setCellStyle(styleBold);
		
		

		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Superintendencia"));
		cell10H.setCellStyle(styleBold);
		*/
		
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Caso Asociado"));
		cell11H.setCellStyle(styleBold);
		
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Inte"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Nro Doc"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Apellido Afiliado"));
		cell15H.setCellStyle(styleBold);

		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Nombre Afiliado"));
		cell16H.setCellStyle(styleBold);		

		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Seccional Afiliado"));
		cell17H.setCellStyle(styleBold);
		
	
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Plan Molineros"));
		cell18H.setCellStyle(styleBold);

		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Plan Tercerizadora"));
		cell19H.setCellStyle(styleBold);
		
		

		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Código"));
		cell20H.setCellStyle(styleBold);
	
	

		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Prestación"));
		cell21H.setCellStyle(styleBold);
	
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Est. Prestación"));
		cell22H.setCellStyle(styleBold);		
	
		
		HSSFCell cell23H = rowHeader.createCell(++col);
		cell23H.setCellValue(new HSSFRichTextString("Frecuencia"));
		cell23H.setCellStyle(styleBold);
		
		
		HSSFCell cell24H = rowHeader.createCell(++col);
		cell24H.setCellValue(new HSSFRichTextString("Cantidad"));
		cell24H.setCellStyle(styleBold);
	
		HSSFCell cell25H = rowHeader.createCell(++col);
		cell25H.setCellValue(new HSSFRichTextString("Importe"));
		cell25H.setCellStyle(styleBold);
		//cell22H.setCellStyle(styleMoneyRight);
		
		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Total"));
		cell26H.setCellStyle(styleBold);
		//cell23H.setCellStyle(styleMoneyRight);
		
		
		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Cargo OSPIM"));
		cell27H.setCellStyle(styleBold);		
		//cell24H.setCellStyle(styleMoneyRight);
		
		
		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Cargo Terc."));
		cell28H.setCellStyle(styleBold);
		//cell25H.setCellStyle(styleMoneyRight);
		
		HSSFCell cell29H = rowHeader.createCell(++col);
		cell29H.setCellValue(new HSSFRichTextString("Revisión Res."));
		cell29H.setCellStyle(styleBold);
		
		HSSFCell cell30H = rowHeader.createCell(++col);
		cell30H.setCellValue(new HSSFRichTextString("Resp Revisión"));
		cell30H.setCellStyle(styleBold);
		

		HSSFCell cell31H = rowHeader.createCell(++col);
		cell31H.setCellValue(new HSSFRichTextString("Observaciones Auditoría Médica"));
		cell31H.setCellStyle(styleBold);
		
		HSSFCell cell310H = rowHeader.createCell(++col);
		cell310H.setCellValue(new HSSFRichTextString("Observaciones Revisión"));
		cell310H.setCellStyle(styleBold);
		
		HSSFCell cell311H = rowHeader.createCell(++col);
		cell311H.setCellValue(new HSSFRichTextString("Observaciones Cierre"));
		cell311H.setCellStyle(styleBold);
		
		HSSFCell cell312H = rowHeader.createCell(++col);
		cell312H.setCellValue(new HSSFRichTextString("Justificación Médica"));
		cell312H.setCellStyle(styleBold);
		
		HSSFCell cell313H = rowHeader.createCell(++col);
		cell313H.setCellValue(new HSSFRichTextString("Dictamen Comisión"));
		cell313H.setCellStyle(styleBold);
		
		
		HSSFCell cell32H = rowHeader.createCell(++col);
		cell32H.setCellValue(new HSSFRichTextString("Fecha Cierre"));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33H = rowHeader.createCell(++col);
		cell33H.setCellValue(new HSSFRichTextString("Incluido Convenio"));
		cell33H.setCellStyle(styleBold);
		

		
		HSSFCell cell34H = rowHeader.createCell(++col);
		cell34H.setCellValue(new HSSFRichTextString("2 % "));
		cell34H.setCellStyle(styleBold);

		
		HSSFCell cell35H = rowHeader.createCell(++col);
		cell35H.setCellValue(new HSSFRichTextString("Débito Prestadora"));
		cell35H.setCellStyle(styleBold);
		
		HSSFCell cell36H = rowHeader.createCell(++col);
		cell36H.setCellValue(new HSSFRichTextString("Tipo Gestión"));
		cell36H.setCellStyle(styleBold);
		

		HSSFCell cell37H = rowHeader.createCell(++col);
		cell37H.setCellValue(new HSSFRichTextString("Nro OP"));
		cell37H.setCellStyle(styleBold);
		
		HSSFCell cell371H = rowHeader.createCell(++col);
		cell371H.setCellValue(new HSSFRichTextString("Fecha OP"));
		cell371H.setCellStyle(styleBold);
		
		HSSFCell cell38H = rowHeader.createCell(++col);
		cell38H.setCellValue(new HSSFRichTextString("Lote"));
		cell38H.setCellStyle(styleBold);
		
		HSSFCell cell39H = rowHeader.createCell(++col);
		cell39H.setCellValue(new HSSFRichTextString("Permanencia"));
		cell39H.setCellStyle(styleBold);
		
		
		HSSFCell cell40H = rowHeader.createCell(++col);
		cell40H.setCellValue(new HSSFRichTextString("Fecha Envio Seccional"));
		cell40H.setCellStyle(styleBold);
		
		HSSFCell cell41H = rowHeader.createCell(++col);
		cell41H.setCellValue(new HSSFRichTextString("Descripción Seccional"));
		cell41H.setCellStyle(styleBold);
		
		HSSFCell cell42H = rowHeader.createCell(++col);
		cell42H.setCellValue(new HSSFRichTextString("Recupero Sur"));
		cell42H.setCellStyle(styleBold);
		
		HSSFCell cell43H = rowHeader.createCell(++col);
		cell43H.setCellValue(new HSSFRichTextString("Integración"));
		cell43H.setCellStyle(styleBold);
		
		HSSFCell cell44H = rowHeader.createCell(++col);
		cell44H.setCellValue(new HSSFRichTextString("Reconocido SSS"));
		cell44H.setCellStyle(styleBold);
		
		
		HSSFCell cell45H = rowHeader.createCell(++col);
		cell45H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell45H.setCellStyle(styleBold);
	
		
		index++;
		
		for(ReclamoPrestacionalExcel  autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
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
		sheet.autoSizeColumn((short) 191);
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
		sheet.autoSizeColumn((short) 36);
		sheet.autoSizeColumn((short) 37);
		sheet.autoSizeColumn((short) 38);
		sheet.autoSizeColumn((short) 39);
		sheet.autoSizeColumn((short) 40);
		sheet.autoSizeColumn((short) 41);
		sheet.autoSizeColumn((short) 42);
		sheet.autoSizeColumn((short) 43);
		return wb;
	}

////
////
	
	public static HSSFWorkbook generaReporteReclamosPrestacionalesAgrupado(
			List<ReclamoPrestacionalExcel> list, BusquedaReporteReclamoFiltro filtro,HSSFWorkbook wb) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Date hoy=new Date();
		
		HSSFSheet sheet = wb.createSheet("Reclamos Prestacionales Agrupados");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		//HSSFCellStyle styleBoldSize = getStyleBoldWithSize(wb,12);
		HSSFCellStyle styleNumber= getStyleNumber(wb);
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		//StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales: ").append(sdf.format(hoy));
		StringBuffer titulo1=new StringBuffer("Reporte Reclamos Prestacionales Agrupados");
	
		//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

		index++;
		HSSFRow row1 = sheet.createRow(index);
		HSSFCell cell1 = row1.createCell(0);

		StringBuffer fechaImpresion=new StringBuffer("Fecha impresión: " ).append(sdf2.format(hoy));

		
		cell1.setCellValue(new HSSFRichTextString(fechaImpresion.toString()));
		//cell1HA.setCellStyle(styleBold);
	
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

		index++;
		HSSFRow rowSeparador = sheet.createRow(index++);

		
		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		StringBuffer aux = new StringBuffer("");
				
				if(filtro.getNroReclamo() > 0) {
					aux.append(" Nro Reclamo: " + filtro.getNroReclamo());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getTipoPedido())) {
					aux.append(" Tipo Pedido: " + filtro.getTipoPedido());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getSectorSeleccionado())) {
					aux.append(" Sector: " + filtro.getSectorSeleccionado());
				}
				
				if(StringUtils.checkNotEmpty(filtro.getResolucion())) {
					aux.append(" getResolucion: " + filtro.getResolucion());
				}
		
				if(filtro.getEstado() == -1) {
					aux.append(" Estados: " + "TODOS");
				}else{
					for (EstadosReclamosPrestacionales estado : listaestados) {
						if (filtro.getEstado() == estado.getId()){
							aux.append(" Estados: " + estado.getDescripcion());
						}
					}
				}
				
				if("0".equals(filtro.getCodigoTipoGestion())) {
					aux.append(" Tipo Gestión: " + "TODOS");
				}else{
					 for (TiposDeGestionReclamosPrestacionales tipoGestion  : listatipogestionreclamos) {
						 if(filtro.getCodigoTipoGestion().equals(tipoGestion.getId())){
								aux.append(" Tipo Gestión: " + tipoGestion.getDescripcion());
						 }
					 }
				}
				
				if(filtro.getNroLote()!=null && filtro.getNroLote() > 0){
					aux.append(" Nro.Lote: " + filtro.getNroLote());
				}
				

				if(filtro.getSeccional() > 0){
					Seccional seccional = null;
					try {
						seccional = SeccionalServiceUtil.buscarSeccionalById(filtro.getSeccional());
						aux.append(" Seccional: " + seccional.getDescripcion());
					} catch (Exception e) {
						_log.debug("Error  al obtener seccional");
					}
				}
				
				if(filtro.getFechaOspim()!= null && filtro.getFechaOspim1() != null) {
					aux.append(" Fecha. Ospim : " + sdf.format(filtro.getFechaOspim()) + " y " + sdf.format(filtro.getFechaOspim1()));
				}
				
				if(filtro.getFechaCierre() == null && filtro.getFechaCierre1() != null) {
					aux.append(" Fecha. Cierre : " + sdf.format(filtro.getFechaCierre()) + " y " + sdf.format(filtro.getFechaCierre1()));		
				}
				
				//afiliado
				if(StringUtils.checkNotEmpty(filtro.getCuilTitular())) {
					aux.append(" Cuil Titular: " + filtro.getCuilTitular());
				}
				
				if(filtro.getInte() > 0) {
					aux.append(" Integrante: " + filtro.getInte());
				}
				//comprobante
			
				
				if(StringUtils.checkNotEmpty(filtro.getFrecuencia())) {
					aux.append(" Frecuencia: ");
					if("SELECCIONE".equalsIgnoreCase(filtro.getFrecuencia())) { 
						aux.append(" TODOS "); 
					  }else if("UNICA".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" UNICA "); 
					  }else if("SEMANAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMANAL "); 
					  }else if("TRIMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" TRIMESTRAL "); 
					  }else if("MENSUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" MENSUAL ");	  
					  }else if("SEMESTRAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" SEMESTRAL ");	
					  }else if("ANUAL".equalsIgnoreCase(filtro.getFrecuencia())){
						  aux.append(" ANUAL "); 
					  }
					
					if(StringUtils.checkNotEmpty(filtro.getComprobanteTipo())) {
						aux.append(" Comprobante: ");
						if("Seleccione".equalsIgnoreCase(filtro.getComprobanteTipo())) { 
							aux.append(" TODOS "); 
						  }else if("FCP".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" FCP "); 
						  }else if("RCB".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" RCB "); 
						  }else if("OTR".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" OTRO "); 
						  }else if("AUT".equalsIgnoreCase(filtro.getComprobanteTipo())){
							  aux.append(" AUTORIZACION ");	  
						  }
					}
						
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 
				}
				
				
				if(StringUtils.checkNotEmpty(filtro.getNumeroComprobante())) {
					  aux.append(" Nro " + filtro.getNumeroComprobante());	 
				}
				
			
				if(filtro.getFechaComprobante() != null) {
					aux.append(" F.Emision: " + sdf.format(filtro.getFechaComprobante()) );
				}
				
				if(StringUtils.checkNotEmpty(filtro.getCuitEntidadComprobante())) {
					  aux.append(" CUIT " + filtro.getCuitEntidadComprobante());	 

				}
				
				if(StringUtils.checkNotEmpty(filtro.getSucursalComprobante())) {
					  aux.append(" Suc " + filtro.getSucursalComprobante());	 

				}
				
		}
	
				

		cell2.setCellValue(new HSSFRichTextString(aux.toString()));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));
		
		
		//index++;
		HSSFRow rowHeader = sheet.createRow(index);
	
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Período"));
		cell0H.setCellStyle(styleBold);
			
		HSSFCell cell04H = rowHeader.createCell(++col);
		cell04H.setCellValue(new HSSFRichTextString("Tipo Pedido"));
		cell04H.setCellStyle(styleBold);
		
		HSSFCell cell03H = rowHeader.createCell(++col);
		cell03H.setCellValue(new HSSFRichTextString("Sector"));
		cell03H.setCellStyle(styleBold);
		
		HSSFCell cell26H = rowHeader.createCell(++col);
		cell26H.setCellValue(new HSSFRichTextString("Total"));
		cell26H.setCellStyle(styleBold);
		//cell23H.setCellStyle(styleMoneyRight);
		
		
		
		HSSFCell cell27H = rowHeader.createCell(++col);
		cell27H.setCellValue(new HSSFRichTextString("Cargo OSPIM"));
		cell27H.setCellStyle(styleBold);		
		//cell24H.setCellStyle(styleMoneyRight);
		
		
		HSSFCell cell28H = rowHeader.createCell(++col);
		cell28H.setCellValue(new HSSFRichTextString("Cargo Terc."));
		cell28H.setCellStyle(styleBold);
		//cell25H.setCellStyle(styleMoneyRight);
		
				

	
		
		index++;
		
		for(ReclamoPrestacionalExcel  autorizaciones: list){
			index=crearDatosFichaAgrupado(sheet, autorizaciones, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
		}

		index++;
		sheet.createRow(index);
		
		
        HSSFRow rowTotales=sheet.createRow(index);
		
		HSSFCell cell17F = rowTotales.createCell(1);
		cell17F.setCellValue(new HSSFRichTextString("TOTALES"));
		cell17F.setCellStyle(styleBold);
		
		HSSFCell cell3F = rowTotales.createCell(3);
		cell3F.setCellFormula("SUM(D"+Integer.toString(6)  +":D"+ Integer.toString(index-1) +")");
		cell3F.setCellStyle(styleMoneyRight);
		
		HSSFCell cell3F1 = rowTotales.createCell(4);
		cell3F1.setCellFormula("SUM(E"+Integer.toString(6)  +":E"+ Integer.toString(index-1) +")");
		cell3F1.setCellStyle(styleMoneyRight);
		
		HSSFCell cell3F2 = rowTotales.createCell(5);
		cell3F2.setCellFormula("SUM(F"+Integer.toString(6)  +":F"+ Integer.toString(index-1) +")");
		cell3F2.setCellStyle(styleMoneyRight);
		
		
		
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
		sheet.autoSizeColumn((short) 191);
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
		sheet.autoSizeColumn((short) 36);
		sheet.autoSizeColumn((short) 37);
		sheet.autoSizeColumn((short) 38);
		sheet.autoSizeColumn((short) 39);
		sheet.autoSizeColumn((short) 40);
		sheet.autoSizeColumn((short) 41);
		return wb;
	}

	private static int crearDatosFichaAgrupado(HSSFSheet sheet,ReclamoPrestacionalExcel   autorizaciones, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		int col = -1;
		Integer difDia=0;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell01 = rowHeader.createCell(++col);
		cell01.setCellValue(new HSSFRichTextString(sdf.format(autorizaciones.getFecha_cierre()) ));
		cell01.setCellStyle(styleAll);
		
		HSSFCell cell05 = rowHeader.createCell(++col);
		cell05.setCellValue(new HSSFRichTextString(autorizaciones.getTipoPedido()));
		cell05.setCellStyle(styleAll);
		
		HSSFCell cell04 = rowHeader.createCell(++col);
		cell04.setCellValue(new HSSFRichTextString(autorizaciones.getSector() ));
		cell04.setCellStyle(styleAll);
						
		HSSFCell cell27 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionTotalImporte() >0){
			   cell27.setCellValue(autorizaciones.getPrestacionTotalImporte()   );
			   cell27.setCellStyle(styleMoneyRight);
		    }else{
		       cell27.setCellValue(0);
		       cell27.setCellStyle(styleMoneyRight);
			}
		
		

		HSSFCell cell28 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionCargoOspim() >0){
			   cell28.setCellValue(autorizaciones.getPrestacionCargoOspim() );
			   cell28.setCellStyle(styleMoneyRight);
		    }else{
		       cell28.setCellValue(0);
		       cell28.setCellStyle(styleMoneyRight);
			}
	
		
		HSSFCell cell29 = rowHeader.createCell(++col);		
		if( autorizaciones.getPrestacionCargoPs() >0){
			     cell29.setCellValue(autorizaciones.getPrestacionCargoPs() );
			     cell29.setCellStyle(styleMoneyRight);
		    }else{
		    	 cell29.setCellValue(0);
		    	 cell29.setCellStyle(styleMoneyRight);
			}
	
		
		
		return index++;
	}	

	
	

}
