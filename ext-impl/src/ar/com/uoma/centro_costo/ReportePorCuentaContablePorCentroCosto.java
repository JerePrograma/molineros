package ar.com.uoma.centro_costo;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import org.apache.poi.ss.usermodel.*;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.reportes.ReporteConabilidad;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.CentroCosto;
import ar.com.uoma.centro_costo.CentroCostoServiceUtil;
import ar.com.uoma.centro_costo.CuentaAsientoDetalle;

public class ReportePorCuentaContablePorCentroCosto extends ReporteConabilidad {
	
	private static Log _log = LogFactoryUtil
			.getLog(ReportePorCuentaContablePorCentroCosto.class);
	
	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			
			boolean vista_cuenta = Boolean.parseBoolean(req.getParameter("vista_cuenta"));

			boolean vista_centro_costo = Boolean.parseBoolean(req.getParameter("vista_centro_costo"));
			
			boolean vista_cartesiano = Boolean.parseBoolean(req.getParameter("vista_cartesiano"));
			
			String entidad = req.getParameter("entidad");
		
			String cuentas  = req.getParameter("cuentas");
			
			String centros_costos = req.getParameter("centro_costo");
	
			String fechaDesde = req.getParameter("fecha_desde");
	
			String fechaHasta = req.getParameter("fecha_hasta");
		
			
			Date fechaDesdeUtil = format.parse(fechaDesde);
	
			Date fechaHastaUtil = format.parse(fechaHasta);
			
			List<CuentaAsientoDetalle> cuentaAsientoDetalles = AsientoServiceUtil
					.buscarCuentasAsientosDetalles(
					         cuentas,
					         centros_costos,
					         fechaDesdeUtil,
					         fechaHastaUtil,
					         Integer.parseInt(entidad)
			);

			
			List<PlanCuentas> cuentasContables=new ArrayList<PlanCuentas>();
			
			
			List<CentroCosto> centrosCosto = CentroCostoServiceUtil
					.getContables(fechaDesdeUtil,Integer.parseInt(entidad));
			
			
			
			if(cuentaAsientoDetalles.isEmpty()) {
				_log.error("No se encontraron resultados para los detalles ...");
				//return null;
			}
			
			// Creacion de workbook
			HSSFWorkbook wb = new HSSFWorkbook();
			// getStyleAllWithBorder(wb) es un metodo que devuelve un estilo de celda con bordes
			// HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb);
			
			HSSFCellStyle styleAllBorder = wb.createCellStyle();
			styleAllBorder.setBorderTop(BorderStyle.THIN);
			styleAllBorder.setBorderBottom(BorderStyle.THIN);
			styleAllBorder.setBorderLeft(BorderStyle.THIN);
			styleAllBorder.setBorderRight(BorderStyle.THIN);
			styleAllBorder.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleAllBorder.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleAllBorder.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleAllBorder.setRightBorderColor(IndexedColors.BLACK.getIndex());
			
			HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
			
			// Creacion de hoja
			HSSFSheet sheet = wb.createSheet("Hoja 1");
			// Creacion de fila
			sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
			// Creacion de fila
			sheet.setMargin(HSSFSheet.RightMargin, 0.2);
			// Creacion de fila
			sheet.setMargin(HSSFSheet.TopMargin, 1.3);

			// Creacion de fila - getPrintSetup() es un metodo que devuelve un objeto de configuracion de impresion
			HSSFPrintSetup ps = sheet.getPrintSetup();
			// Creacion de fila - setAutobreaks(true) es un metodo que activa el ajuste automatico de impresion
			sheet.setAutobreaks(true);
			// setPaperSize(HSSFPrintSetup.A4_PAPERSIZE) es un metodo que establece el tamaño de papel
			ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
			// setFitHeight((short) 0) es un metodo que establece el ajuste de altura
			ps.setFitHeight((short) 0);
			// setFitWidth((short) 1) es un metodo que establece el ajuste de ancho
			ps.setFitWidth((short) 1);
			// setLandscape(false) es un metodo que establece la orientacion de la hoja
			ps.setLandscape(false);
			
			int i = 0;
			
			Map<String, BigDecimal> saldosDebe = new HashMap<String, BigDecimal>();
			Map<String, BigDecimal> saldosHaber = new HashMap<String, BigDecimal>();

			// Probar esto mostrar la info por cuenta o por centro de costo
			if(vista_cuenta) {
					
				// createRow(i) crea una fila en la hoja de excel
				HSSFRow rowTitulo = sheet.createRow(i);
				// createCell(0) crea una celda en la fila
				HSSFCell cell = rowTitulo.createCell(0);
										
				SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
				// setCellValue() establece el valor de la celda
				cell.setCellValue(new HSSFRichTextString("Reporte Centros Costos. Desde: "
					+ formatFecha.format(fechaDesdeUtil)
					+ " hasta " + formatFecha.format(fechaHastaUtil) /*+
					(cuentaAsientoDetalles.size()>0?
					". Cuentas: "
					+ cuentaAsientoDetalles.get(0).getNumero_cuenta() + " al "
					+ cuentaAsientoDetalles.get(cuentaAsientoDetalles.size()-1).getNumero_cuenta():"")*/
					
						)
						
				);
				cell.setCellStyle(getStyleBoldUnderlined(wb));
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
							
				i += 2;
							
				int rowIndex = i; // contador de fila

				HashSet<String> seenAccounts = new HashSet<String>();
				
				for (CuentaAsientoDetalle cad : cuentaAsientoDetalles) {
					
					String numeroCuenta = cad.getNumero_cuenta();
					
					// Inicializar saldos si es la primera vez que se encuentra la cuenta
				    saldosDebe.putIfAbsent(numeroCuenta, BigDecimal.ZERO);
				    saldosHaber.putIfAbsent(numeroCuenta, BigDecimal.ZERO);
					
					String currentAccount = cad.getNumero_cuenta();
					
					if (seenAccounts.add(currentAccount)) {
						
						// PRIMER RENGLON
						HSSFRow row = sheet.createRow(rowIndex);
									
						HSSFCell cell0 = row.createCell(0);
						cell0.setCellValue(new HSSFRichTextString("Fecha"));
						cell0.setCellStyle(styleAllBorder);
									
						HSSFCell cell1 = row.createCell(1);
						cell1.setCellValue(new HSSFRichTextString("Asiento"));
						cell1.setCellStyle(styleAllBorder);
									
						HSSFCell cell2 = row.createCell(2);
						cell2.setCellValue(new HSSFRichTextString("Pase"));
						cell2.setCellStyle(styleAllBorder);
									
						HSSFCell cell3 = row.createCell(3);
						cell3.setCellValue(new HSSFRichTextString("Centro de costo"));
						cell3.setCellStyle(styleAllBorder);
									
						HSSFCell cell4 = row.createCell(5);
						cell4.setCellValue(new HSSFRichTextString("Comprobante"));
						cell4.setCellStyle(styleAllBorder);

						HSSFCell cell5 = row.createCell(6);
						cell5.setCellValue(new HSSFRichTextString("Observaciones"));
						cell5.setCellStyle(styleAllBorder);
									
						HSSFCell cell6 = row.createCell(7);
						cell6.setCellValue(new HSSFRichTextString("Debe"));
						cell6.setCellStyle(styleAllBorder);

						HSSFCell cell7 = row.createCell(8);
						cell7.setCellValue(new HSSFRichTextString("Haber"));
						cell7.setCellStyle(styleAllBorder);

						HSSFCell cell8 = row.createCell(9);
						cell8.setCellValue(new HSSFRichTextString("Saldo"));
						cell8.setCellStyle(styleAllBorder);
						
						// Fusionar las celdas de "Centro Costo"
						sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 4));
								
						rowIndex++;
						
						// SEGUNDO RENGLON
						HSSFRow row2 = sheet.createRow(rowIndex);
						
						HSSFCell cell2_0 = row2.createCell(0);
						cell2_0.setCellValue(
									new HSSFRichTextString(
										"Cuenta: " + cad.getNumero_cuenta() + " - " +
										cad.getDescripcion_cuenta()
									)
								);
						cell2_0.setCellStyle(styleAllBorder);
						
						sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));
				
						// Aca empieza el debe 
						HSSFCell cell2_6 = row2.createCell(6);
						cell2_6.setCellValue("");
						cell2_6.setCellStyle(styleAllBorder);

						HSSFCell cell2_7 = row2.createCell(7);
						cell2_7.setCellValue(0);
						cell2_7.setCellStyle(styleAllBorder);

						HSSFCell cell2_8 = row2.createCell(8);
						cell2_8.setCellValue(0);
						cell2_8.setCellStyle(styleAllBorder);
						
						HSSFCell cell2_9 = row2.createCell(9);
						cell2_9.setCellValue(0);
						cell2_9.setCellStyle(styleAllBorder);
						
						rowIndex++;		   
						
						
						for (CuentaAsientoDetalle cad2 : cuentaAsientoDetalles) {
							if (cad2.getNumero_cuenta().equals(currentAccount)) {
								
								// RENGLONES DE LOS ASIENTOS
								HSSFRow rowA = sheet.createRow(rowIndex);
									
								HSSFCell cellA_0 = rowA.createCell(0); 
								cellA_0.setCellValue(new HSSFRichTextString(cad2.getFecha_asiento().toString()));
								cellA_0.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_1 = rowA.createCell(1);
								cellA_1.setCellValue(cad2.getAsiento_id());
								cellA_1.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_2 = rowA.createCell(2);
								cellA_2.setCellValue(cad2.getPase());
								cellA_2.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_3 = rowA.createCell(3);
								cellA_3.setCellValue(new HSSFRichTextString(cad2.getCentro_costo()));
								cellA_3.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_5 = rowA.createCell(5);
								cellA_5.setCellValue(new HSSFRichTextString(cad2.getComprobante()));
								cellA_5.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_6 = rowA.createCell(6);
								cellA_6.setCellValue(new HSSFRichTextString(cad2.getObservaciones()));
								cellA_6.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_7 = rowA.createCell(7);
								cellA_7.setCellValue(cad2.getDebe().doubleValue());
								cellA_7.setCellStyle(styleMoney);
									
								HSSFCell cellA_8 = rowA.createCell(8);
								cellA_8.setCellValue(cad2.getHaber().doubleValue());
								cellA_8.setCellStyle(styleMoney);
								
								 // Calcular y actualizar los saldos acumulados para la cuenta actual
							    BigDecimal saldoDebe = saldosDebe.get(numeroCuenta).add(cad2.getDebe());
							    BigDecimal saldoHaber = saldosHaber.get(numeroCuenta).add(cad2.getHaber());
									
								HSSFCell cellA_9 = rowA.createCell(9);
								cellA_9.setCellValue((saldoDebe.subtract(saldoHaber)).doubleValue());
								cellA_9.setCellStyle(styleMoney);
								
								// Actualizar los saldos acumulados en el mapa
							    saldosDebe.put(numeroCuenta, saldoDebe);
							    saldosHaber.put(numeroCuenta, saldoHaber);
								
								// Fusionar las celdas de "Centro Costo"
								sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 4));
									
								rowIndex++;			
							}
						}
						rowIndex+=2;
					}
					
				}
				
			}else if(vista_centro_costo) {
				
				// createRow(i) crea una fila en la hoja de excel
				HSSFRow rowTitulo = sheet.createRow(i);
				// createCell(0) crea una celda en la fila
				HSSFCell cell = rowTitulo.createCell(0);
										
				SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
				// setCellValue() establece el valor de la celda
				cell.setCellValue(
						new HSSFRichTextString("Reporte Final por centro de costo. Desde: "
								+ formatFecha.format(fechaDesdeUtil)
								+ " hasta " + formatFecha.format(fechaHastaUtil) /*+ ". Centro de costo: "*/
								));
				cell.setCellStyle(getStyleBoldUnderlined(wb));
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
							
				i += 2;
				
				int rowIndex = i; // contador de fila
				
				// Ordenar la lista de cuentas por nombre de Centro de Costo
				Collections.sort(cuentaAsientoDetalles, new Comparator<CuentaAsientoDetalle>() {
				    public int compare(CuentaAsientoDetalle cad1, CuentaAsientoDetalle cad2) {
				        return cad1.getCentro_costo().compareTo(cad2.getCentro_costo());
				    }
				});
				
				HashSet<Integer> seenAccountsCC = new HashSet<Integer>();
				
				
				for(CuentaAsientoDetalle cad: cuentaAsientoDetalles) {
					
					String idCentroCosto = String.valueOf(cad.getId_centro_costo());
					
					// Inicializar saldos si es la primera vez que se encuentra la cuenta
				    saldosDebe.putIfAbsent(idCentroCosto, BigDecimal.ZERO);
				    saldosHaber.putIfAbsent(idCentroCosto, BigDecimal.ZERO);
					
					Integer currentAccountCC = cad.getId_centro_costo();	   
					
					if(seenAccountsCC.add(currentAccountCC)) {
						
						// PRIMER RENGLON
						HSSFRow row = sheet.createRow(rowIndex);
									
						HSSFCell cell0 = row.createCell(0);
						cell0.setCellValue(new HSSFRichTextString("Fecha"));
						cell0.setCellStyle(styleAllBorder);
									
						HSSFCell cell1 = row.createCell(1);
						cell1.setCellValue(new HSSFRichTextString("Asiento"));
						cell1.setCellStyle(styleAllBorder);
									
						HSSFCell cell2 = row.createCell(2);
						cell2.setCellValue(new HSSFRichTextString("Pase"));
						cell2.setCellStyle(styleAllBorder);
									
						HSSFCell cell3 = row.createCell(3);
						cell3.setCellValue(new HSSFRichTextString("Cuenta"));
						cell3.setCellStyle(styleAllBorder);
									
						HSSFCell cell6 = row.createCell(6);
						cell6.setCellValue(new HSSFRichTextString("Comprobante"));
						cell6.setCellStyle(styleAllBorder);

						HSSFCell cell7 = row.createCell(7);
						cell7.setCellValue(new HSSFRichTextString("Observaciones"));
						cell7.setCellStyle(styleAllBorder);
									
						HSSFCell cell8 = row.createCell(8);
						cell8.setCellValue(new HSSFRichTextString("Debe"));
						cell8.setCellStyle(styleAllBorder);

						HSSFCell cell9 = row.createCell(9);
						cell9.setCellValue(new HSSFRichTextString("Haber"));
						cell9.setCellStyle(styleAllBorder);

						HSSFCell cell10 = row.createCell(10);
						cell10.setCellValue(new HSSFRichTextString("Saldo"));
						cell10.setCellStyle(styleAllBorder);
						
						// Fusionar las celdas de "Cuentas"
						sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 4));
								
						rowIndex++;
						
						// SEGUNDO RENGLON
						HSSFRow row2 = sheet.createRow(rowIndex);
						
						HSSFCell cell2_0 = row2.createCell(0);
						cell2_0.setCellValue(
									new HSSFRichTextString(
										"Centro de costo: " + cad.getCentro_costo() + " - " +
										cad.getId_centro_costo()
									)
								);
						cell2_0.setCellStyle(styleAllBorder);
						
						sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));
				
						// Aca empieza el debe 
						HSSFCell cell2_6 = row2.createCell(6);
						cell2_6.setCellValue("");
						cell2_6.setCellStyle(styleAllBorder);

						HSSFCell cell2_7 = row2.createCell(7);
						cell2_7.setCellValue(0);
						cell2_7.setCellStyle(styleAllBorder);

						HSSFCell cell2_8 = row2.createCell(8);
						cell2_8.setCellValue(0);
						cell2_8.setCellStyle(styleAllBorder);
						
						HSSFCell cell2_9 = row2.createCell(9);
						cell2_9.setCellValue(0);
						cell2_9.setCellStyle(styleAllBorder);
						
						rowIndex++;	
						
						for (CuentaAsientoDetalle cad2 : cuentaAsientoDetalles) {
							
							if (Integer.valueOf(cad2.getId_centro_costo()).equals(currentAccountCC)) {
								
								// RENGLONES DE LOS ASIENTOS
								HSSFRow rowA = sheet.createRow(rowIndex);
									
								HSSFCell cellA_0 = rowA.createCell(0); 
								cellA_0.setCellValue(new HSSFRichTextString(cad2.getFecha_asiento().toString()));
								cellA_0.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_1 = rowA.createCell(1);
								cellA_1.setCellValue(cad2.getAsiento_id());
								cellA_1.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_2 = rowA.createCell(2);
								cellA_2.setCellValue(cad2.getPase());
								cellA_2.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_3 = rowA.createCell(3);
								cellA_3.setCellValue(new HSSFRichTextString(
										cad2.getDescripcion_cuenta() + " " + cad2.getNumero_cuenta()
										));
								cellA_3.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_6 = rowA.createCell(6); // 6
								cellA_6.setCellValue(new HSSFRichTextString(cad2.getComprobante()));
								cellA_6.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_7 = rowA.createCell(7);
								cellA_7.setCellValue(new HSSFRichTextString(cad2.getObservaciones()));
								cellA_7.setCellStyle(styleAllBorder);
									
								HSSFCell cellA_8 = rowA.createCell(8);
								cellA_8.setCellValue(cad2.getDebe().doubleValue());
								cellA_8.setCellStyle(styleMoney);
									
								HSSFCell cellA_9 = rowA.createCell(9);
								cellA_9.setCellValue(cad2.getHaber().doubleValue());
								cellA_9.setCellStyle(styleMoney);
								
								 // Calcular y actualizar los saldos acumulados para la cuenta actual
							    BigDecimal saldoDebe = saldosDebe.get(idCentroCosto).add(cad2.getDebe());
							    BigDecimal saldoHaber = saldosHaber.get(idCentroCosto).add(cad2.getHaber());
									
								HSSFCell cellA_10 = rowA.createCell(10);
								cellA_10.setCellValue((saldoDebe.subtract(saldoHaber)).doubleValue());
								cellA_10.setCellStyle(styleMoney);
								
								// Actualizar los saldos acumulados en el mapa
							    saldosDebe.put(idCentroCosto, saldoDebe);
							    saldosHaber.put(idCentroCosto, saldoHaber);
								
								// Fusionar las celdas de "Centro Costo"
								sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 5));
									
								rowIndex++;	
								
							}
						}
						rowIndex+=2;
					}
				}
			
			}else if(vista_cartesiano) {
				
				// Crear una lista para almacenar las cuentas que deben agregarse a cuentasContables
				List<PlanCuentas> cuentasAgregadas = new ArrayList<PlanCuentas>();

				// Recorrer cada cuenta en cuentaAsientoDetalles
				for (CuentaAsientoDetalle cuentaAsientoDetalle : cuentaAsientoDetalles) {
				    boolean existe = false;
				    
				    // Verificar si la cuenta está en cuentasContables
				    for (PlanCuentas cuentaContable : cuentasContables) {
				        if (cuentaAsientoDetalle.getId_cuenta() == cuentaContable.getId()) {
				            existe = true;
				            break;
				        }
				    }
				    
				    // Si la cuenta no existe en cuentasContables, agregarla a la lista de cuentasAgregadas
				    if (!existe) {
				        PlanCuentas nuevaCuenta = new PlanCuentas();
				        nuevaCuenta.setId(cuentaAsientoDetalle.getId_cuenta());
				        nuevaCuenta.setCuenta(cuentaAsientoDetalle.getDescripcion_cuenta());
				        nuevaCuenta.setNumero(cuentaAsientoDetalle.getNumero_cuenta());
				        // Setear los demás atributos de la cuenta si es necesario
				        if(!cuentasAgregadas.contains(nuevaCuenta)) {
				           cuentasAgregadas.add(nuevaCuenta);
				        }    
				    }
				    
				}

				// Agregar las cuentas de cuentasAgregadas a cuentasContables
				cuentasContables.addAll(cuentasAgregadas);
				
				
				// Crear una lista para almacenar los centros de costo que deben agregarse a centrosCosto
				List<CentroCosto> centrosAgregados = new ArrayList<CentroCosto>();

				// Recorrer cada centro de costo en centrosCosto
				for (CentroCosto centroCosto : centrosCosto) {
				    boolean existe = false;
				    
				    // Verificar si el centro de costo está en cuentasContables
				    for (CentroCosto centroExistente : centrosCosto) {
				        if (centroCosto.getId() == centroExistente.getId()) {
				            existe = true;
				            break;
				        }
				    }
				    
				    // Si el centro de costo no existe en centrosCosto, agregarlo a la lista de centrosAgregados
				    if (!existe) {
				        centrosAgregados.add(centroCosto);
				    }
				}

				// Agregar los centros de costo de centrosAgregados a centrosCosto
				centrosCosto.addAll(centrosAgregados);
				
				
				// Ordenal alfabeticamente las cuentas y los centros de costos
				// Ordenar alfabeticamente las cuentas contables
				Collections.sort(cuentasContables, new Comparator<PlanCuentas>() {
					@Override
					public int compare(PlanCuentas cuenta1, PlanCuentas cuenta2) {
						return cuenta1.getCuenta().compareTo(cuenta2.getCuenta());
					}
				});

				// Ordenar alfabeticamente los centros de costo
				Collections.sort(centrosCosto, new Comparator<CentroCosto>() {
					@Override
					public int compare(CentroCosto centro1, CentroCosto centro2) {
						return centro1.getDescripcion().compareTo(centro2.getDescripcion());
					}
				});
				
				// Total derecha y abajo tiene que tener el mismo monto
				
				
				// Crear la matriz para almacenar los valores
			    BigDecimal[][] matrizValores = new BigDecimal[cuentasContables.size()][centrosCosto.size()];

			    // Inicializar la matriz con ceros
			    for (int iterador = 0; iterador < cuentasContables.size(); iterador++) {
			        for (int j = 0; j < centrosCosto.size(); j++) {
			            matrizValores[iterador][j] = BigDecimal.ZERO;
			        }
			    }

			    // Llenar la matriz con los valores de cuentaAsientoDetalles
			    for (CuentaAsientoDetalle detalle : cuentaAsientoDetalles) {
			        int cuentaIndex = -1;
			        int centroCostoIndex = -1;

			        // Encontrar el índice de la cuenta contable en cuentasContables
			        for (int iterador = 0; iterador < cuentasContables.size(); iterador++) {
			            if (detalle.getId_cuenta() == cuentasContables.get(iterador).getId()) {
			                cuentaIndex = iterador;
			                break;
			            }
			        }

			        // Encontrar el índice del centro de costo en centrosCosto
			        for (int iterador = 0; iterador < centrosCosto.size(); iterador++) {
			            if (detalle.getId_centro_costo() == centrosCosto.get(iterador).getId()) {
			                centroCostoIndex = iterador;
			                break;
			            }
			        }

			        // Ahora Si se encontraron ambos índices, sumar el valor de debe o restar el valor delhaber a la celda correspondiente
			        if (cuentaIndex != -1 && centroCostoIndex != -1) {
			            BigDecimal valor = detalle.getDebe().compareTo(BigDecimal.ZERO) != 0
			                               ? detalle.getDebe()
			                               : detalle.getHaber().negate();
			            _log.info("Valor" + valor);
			            matrizValores[cuentaIndex][centroCostoIndex] = matrizValores[cuentaIndex][centroCostoIndex].add(valor);
			        }
			    }

			    
				
				// createRow(i) crea una fila en la hoja de excel
				HSSFRow rowTitulo = sheet.createRow(i);
				// createCell(0) crea una celda en la fila
				HSSFCell cellTitulo = rowTitulo.createCell(0);
										
				SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
				// setCellValue() establece el valor de la celda
				cellTitulo.setCellValue(
						new HSSFRichTextString("Reporte Final Centros de Costos. Desde: "
								+ formatFecha.format(fechaDesdeUtil)
								+ " hasta " + formatFecha.format(fechaHastaUtil) /*+ ". Centro de costo: "*/
								));
				cellTitulo.setCellStyle(getStyleBoldUnderlined(wb));
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
							
				i += 2;

			    // Crear la primera fila con los encabezados de los centros de costo
			    HSSFRow headerRow = sheet.createRow(i);
			    headerRow.createCell(0).setCellValue("Cuenta Contable");

			    for (int iterador = 0; iterador < centrosCosto.size(); iterador++) {
			        headerRow.createCell(iterador + 1).setCellValue(centrosCosto.get(iterador).getDescripcion());
			    }

			    // Llenar la matriz de valores en el archivo Excel
			    for (int iterador = 0; iterador < cuentasContables.size(); iterador++) {	
			        
			    	//HSSFRow dataRow = sheet.createRow(iterador + 1);
			    	HSSFRow dataRow = sheet.createRow(iterador + 1+i);
			        dataRow.createCell(0).setCellValue(cuentasContables.get(iterador).getCuenta() + " " + cuentasContables.get(iterador).getNumero());

			        BigDecimal filaTotal = BigDecimal.ZERO;  // Inicializar el total de la fila
			        
			        for (int j = 0; j < centrosCosto.size(); j++) {
			        	
			        	//dataRow.createCell(j + 1).setCellValue(matrizValores[iterador][j].doubleValue());
			        	HSSFCell cell = dataRow.createCell(j + 1);
			        	cell.setCellValue(matrizValores[iterador][j].doubleValue());
			        	cell.setCellStyle(styleMoney);
			            
			        	//TOTAL DERECHA
			        	filaTotal = filaTotal.add(matrizValores[iterador][j]); // Sumar valor a total de la fila
			        }
			        
			        // Crear la celda "Total" en la última columna
			        HSSFCell total = dataRow.createCell(centrosCosto.size() + 1 );
			        total.setCellValue(filaTotal.doubleValue());
			        total.setCellStyle(styleMoney);
			        /*
			        dataRow.createCell(centrosCosto.size() + 1).setCellValue(filaTotal.doubleValue());
			        dataRow.createCell(centrosCosto.size() + 1).setCellStyle(styleMoney);
			        */
			    }
			    
			    // Crear el encabezado de la columna "Total"
				headerRow.createCell(centrosCosto.size() + 1).setCellValue("Total");
			    
			    
			    // TOTAL DE ABAJO
			    
			    // Calcular los totales de cada columna de centro de costo
			    HSSFRow totalRow = sheet.createRow(cuentasContables.size() + 1 +i);
			    totalRow.createCell(0).setCellValue("Total");

			    for (int j = 0; j < centrosCosto.size(); j++) {
			        BigDecimal columnaTotal = BigDecimal.ZERO;  // Inicializar el total de la columna
			        
			        for (int iterador = 0; iterador < cuentasContables.size(); iterador++) {
			            columnaTotal = columnaTotal.add(matrizValores[iterador][j]);
			        }
			        
			        HSSFCell totalT =totalRow.createCell(j + 1);
			        totalT.setCellValue(columnaTotal.doubleValue());
			        totalT.setCellStyle(styleMoney);
			        /*
			        totalRow.createCell(j + 1).setCellValue(columnaTotal.doubleValue());
			        totalRow.createCell(j + 1).setCellStyle(styleMoney);
			        */
			    }

			    // Autoajustar el ancho de las columnas, incluyendo la columna "Total"
			    for (int iterador = 0; iterador <= centrosCosto.size() + 1; iterador++) {
			        sheet.autoSizeColumn(iterador);
			    }
			    
			    
			    double filaTotalValor = 0.0;
			    for (int iterador = 1; iterador <= centrosCosto.size(); iterador++) {
			        filaTotalValor += totalRow.getCell(iterador).getNumericCellValue();
			    }
			    
			    HSSFCell totalT1 = totalRow.createCell(centrosCosto.size() + 1);
			    
			    totalT1.setCellValue(filaTotalValor);
			    totalT1.setCellStyle(styleMoney);

			    

			    // Autoajustar el ancho de las columnas
			    for (int iterador = 0; iterador <= centrosCosto.size(); iterador++) {
			        sheet.autoSizeColumn(iterador);
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
			return wb;
			
		} catch (Exception e) {
			_log.error("Error al generar Informe Contable Centro de Costo", e);
			return null;
		}
	}
	
	public static HSSFWorkbook generarListado(HttpServletRequest req,
			HttpServletResponse res) {
	  try {	
		List<CentroCosto> pCentros = (List<CentroCosto>) req.getSession().getAttribute("centrosCostoEnSession");
		if(pCentros==null) pCentros=new ArrayList<CentroCosto>();
		
		// Creacion de workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = wb.createCellStyle();
		styleAllBorder.setBorderTop(BorderStyle.THIN);
		styleAllBorder.setBorderBottom(BorderStyle.THIN);
		styleAllBorder.setBorderLeft(BorderStyle.THIN);
		styleAllBorder.setBorderRight(BorderStyle.THIN);
		styleAllBorder.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setRightBorderColor(IndexedColors.BLACK.getIndex());
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
					
		// Creacion de hoja
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		// Creacion de fila
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);
		// Creacion de fila - getPrintSetup() es un metodo que devuelve un objeto de configuracion de impresion
		HSSFPrintSetup ps = sheet.getPrintSetup();
		// Creacion de fila - setAutobreaks(true) es un metodo que activa el ajuste automatico de impresion
		sheet.setAutobreaks(true);
		// setPaperSize(HSSFPrintSetup.A4_PAPERSIZE) es un metodo que establece el tamaño de papel
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		// setFitHeight((short) 0) es un metodo que establece el ajuste de altura
		ps.setFitHeight((short) 0);
		// setFitWidth((short) 1) es un metodo que establece el ajuste de ancho
		ps.setFitWidth((short) 1);
		// setLandscape(false) es un metodo que establece la orientacion de la hoja
		ps.setLandscape(false);
					
		
		int i = 0;
		
	    HSSFRow rowTitulo = sheet.createRow(i);
	    HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte Centros Costos Contables: "));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;
						
		int rowIndex = i; // contador de fila
		HSSFRow row = sheet.createRow(rowIndex);
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Id"));
		cell0.setCellStyle(styleAllBorder);
					
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1.setCellStyle(styleAllBorder);
		 
		rowIndex++;
		if(pCentros!=null) {
		  for(CentroCosto c:pCentros) {
			  HSSFRow rowA = sheet.createRow(rowIndex);
			  
			  HSSFCell cellA_1 = rowA.createCell(0);
			  cellA_1.setCellValue(c.getId());
			  cellA_1.setCellStyle(styleAllBorder);
				
			  HSSFCell cellA_0 = rowA.createCell(1); 
			  cellA_0.setCellValue(new HSSFRichTextString(c.getDescripcion()));
			  cellA_0.setCellStyle(styleAllBorder);
					
			  rowIndex++;	
		  }	
		}
		
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		
		return wb;
	  } catch (Exception e) {
			_log.error("Error al generar Listado Contable Centro de Costo", e);
			return null;
	  }	
	}

}
