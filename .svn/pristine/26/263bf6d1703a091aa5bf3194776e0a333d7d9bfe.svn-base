package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboCheque;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteRecibosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteRecibosExcel.class);

	private static int CONCEPTO_ACTA = 990;
	private static int CONCEPTO_CONVENIO = 991;
	private static int CONCEPTO_CHEQUE_RECHAZADO = 992;
	private static int CONCEPTO_CHEQUE_NO_DEPOSITADO = 993;

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		String conceptos = ParamUtil.getString(req, "conceptos");

		String[] conceptos_array = conceptos.split(",");

		List<Concepto> conceptos_maestro = null;
		if (!conceptos_array[0].equals("null")) {
			conceptos_maestro = TraeListasServiceUtil
					.getConceptoIngreso(entidad);
			conceptos_maestro.add(new Concepto(990, "ACTAS"));
			conceptos_maestro.add(new Concepto(991, "CONVENIOS"));
			conceptos_maestro.add(new Concepto(992, "CH/ RECHAZADOS"));
			conceptos_maestro.add(new Concepto(993, "CH/ NO DEPOSITADOS"));
		}

		boolean filtrar_0001 = false;
		boolean filtrar_0002 = false;
		boolean filtrar_0003 = false;
		boolean filtrar_rend = false;
		boolean filtrar_bcap = false;
		boolean filtrar_otro = false;
		boolean exportacion = false;

		if (entidad == WebKeysGlobal.UOMA) {
			filtrar_0001 = ParamUtil.getBoolean(req, "opc0001");
			filtrar_0002 = ParamUtil.getBoolean(req, "opc0002");
			filtrar_0003 = ParamUtil.getBoolean(req, "opc0003");
			filtrar_rend = ParamUtil.getBoolean(req, "rend");
			filtrar_bcap = ParamUtil.getBoolean(req, "bcap");
			filtrar_otro = ParamUtil.getBoolean(req, "otro");
		}
		exportacion = ParamUtil.getBoolean(req, "export");

		Empresa empresa = new Empresa(cuit, sucu, "");
		empresa.setId_seccional(seccional);

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			List<Recibo> recibos = ReciboServiceUtil.getReporteRecibos(
					fechaIni, fechaFin, empresa, filtrar_0001, filtrar_0002,
					filtrar_0003, filtrar_rend, filtrar_bcap, filtrar_otro,
					entidad);

			Collections.sort(recibos, new Comparator<Recibo>() {
				public int compare(Recibo arg0, Recibo arg1) {
					int compareTo = arg0.getFecha().compareTo(arg1.getFecha());
					if (compareTo == 0) {
						if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() != null) {
							compareTo = arg0.getBaja_fecha().compareTo(
									arg1.getBaja_fecha());
						} else if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() == null) {
							compareTo = 1;
						} else if (arg0.getBaja_fecha() == null
								&& arg1.getBaja_fecha() != null) {
							compareTo = -1;
						}

						if (compareTo == 0) {
							arg0.getNumero().compareTo(arg1.getNumero());
						}
					}
					return compareTo;
				}

			});

			return generarReporte(fechaIni, fechaFin, recibos,
					conceptos != null && conceptos.split(",").length > 0
							&& !conceptos.equals("") ? conceptos.split(",")
							: null, conceptos_maestro, empresa, exportacion,entidad);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<Recibo> reporte, String[] conceptos,
			List<Concepto> maestroConcepto, Empresa empresa, boolean exportacion,Integer entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setBorderLeft(BorderStyle.THIN);
		styleHeaderLeft.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setBorderRight(BorderStyle.THIN);
		styleHeaderRight.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		styleHeader.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllLeftTop = getStyleAll(wb);
		styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
		styleAllLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllLeft = getStyleAll(wb);
		styleAllLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
		styleFechaLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
		styleFechaLeftTop.setBorderLeft(BorderStyle.THIN);
		styleFechaLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleFechaTop = getStyleDate(wb);
		styleFechaTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
		styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
		styleMoneyRightBold.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoney = getStyleMoney(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		addDefaultHeader(sheet);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer("Listado Recibos - Desde:");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT) + " - Hasta:");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		if (empresa != null && empresa.getCuit() != null
				&& !empresa.getCuit().trim().equals("")) {
			sb.append(" - CUIT: " + empresa.getCuit());
		}

		sb.append(" - Conceptos: ");

		if (null != conceptos && !conceptos[0].equals("null")) {

			for (String conc : conceptos) {
				for (Concepto mae_conc : maestroConcepto) {
					if (Integer.parseInt(conc) == mae_conc.getId()) {
						sb.append(mae_conc.getDescripcion() + " - ");
					}

				}
			}
		} else {
			sb.append("Todos");
		}

		cell.setCellValue(new HSSFRichTextString(sb.toString()));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

		int i = 1;
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb, entidad);
		TotalesRecibo totales = new TotalesRecibo();
		Map<String, BigDecimal> resumenPagos = new HashMap<String, BigDecimal>();
		for (Recibo repo : reporte) {
			i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
					styleMoneyRight, styleFechaLeftTop, styleAllTop,
					styleMoneyRightTop, styleAllLeftTop, styleAllLeft,
					styleFechaTop, totales, conceptos, resumenPagos, exportacion,entidad);
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAllTop);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 9));

		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

		i++;
		HSSFRow rowTotales1 = sheet.createRow(i);
		HSSFCell cellConc1 = rowTotales1.createCell(0);
		cellConc1.setCellValue(new HSSFRichTextString("Total Conceptos"));
		cellConc1.setCellStyle(styleBold);

		HSSFCell cellConc2 = rowTotales1.createCell(1);
		cellConc2.setCellValue(totales.getImporteConceptos().doubleValue());
		cellConc2.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowTotales2 = sheet.createRow(i);
		HSSFCell cellPag1 = rowTotales2.createCell(0);
		cellPag1.setCellValue(new HSSFRichTextString("Total Pagos"));
		cellPag1.setCellStyle(styleBold);

		HSSFCell cellPag2 = rowTotales2.createCell(1);
		cellPag2.setCellValue(totales.getImportePagos().doubleValue());
		cellPag2.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowTotales3 = sheet.createRow(i);
		HSSFCell cellDetPag1 = rowTotales3.createCell(0);
		cellDetPag1.setCellValue(new HSSFRichTextString("Detalle Pagos"));
		cellDetPag1.setCellStyle(styleBold);

		for (String tipo : resumenPagos.keySet()) {
			i++;
			HSSFRow rowTotalesDetalle = sheet.createRow(i);
			HSSFCell cellDetPag = rowTotalesDetalle.createCell(0);
			cellDetPag.setCellValue(new HSSFRichTextString(tipo));
			cellDetPag.setCellStyle(styleAll);

			HSSFCell cellDetPagVal = rowTotalesDetalle.createCell(1);
			cellDetPagVal.setCellValue(resumenPagos.get(tipo).doubleValue());
			cellDetPagVal.setCellStyle(styleMoney);
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
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i, Recibo repo,
			HSSFCellStyle stylefechaLeft, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoneyRight, HSSFCellStyle styleFechaLeftTop,
			HSSFCellStyle styleAllTop, HSSFCellStyle styleMoneyRightTop,
			HSSFCellStyle styleAllLeftTop, HSSFCellStyle styleAllLeft,
			HSSFCellStyle styleFechaTop, TotalesRecibo totales,
			String[] conceptos, Map<String, BigDecimal> resumenPagos,
			boolean exportacion,Integer entidad) {

		List<ReciboConcepto> rconceptos = new ArrayList<ReciboConcepto>();
		if (repo.getActas() != null) {
			rconceptos.addAll(repo.getActas());
		}

		if (repo.getConvenios() != null) {
			rconceptos.addAll(repo.getConvenios());
		}

		if (repo.getChequesNoDepositados() != null) {
			rconceptos.addAll(repo.getChequesNoDepositados());
		}

		if (repo.getChequesRechazados() != null) {
			rconceptos.addAll(repo.getChequesRechazados());
		}

		if (repo.getOtrosConceptos() != null) {
			rconceptos.addAll(repo.getOtrosConceptos());
		}

		boolean tiene_concepto = tieneConcepto(conceptos, rconceptos);

		int max = 0;
		int indexIngreso = 0;
		// VERIFICO SI INGRESARON FILTRO DE CONCEPTOS.

		if (tiene_concepto) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell0 = row.createCell(0);
			String anulado = "";
			if (repo.getBaja_fecha() != null
					&& repo.getBaja_fecha().compareTo(repo.getFecha()) == 0) {
				anulado = "ANULAMISMODIA";
			} else if (repo.getBaja_fecha() != null) {
				anulado = "ANULACION - ";
			}
			if (!anulado.equals("ANULAMISMODIA")) {
				cell0.setCellValue(new HSSFRichTextString(anulado
						+ repo.getNumero()));
			} else {
				cell0.setCellValue(new HSSFRichTextString(repo.getNumero()));
			}
			cell0.setCellStyle(styleAllTop);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(repo.getFecha());
			cell1.setCellStyle(styleFechaTop);

			HSSFCell cell2 = row.createCell(2);
			if (!anulado.equals("ANULAMISMODIA")) {
				cell2.setCellValue(new HSSFRichTextString(repo.getEmpresa()
						.getCuit()));
			} else {
				cell2.setCellValue(new HSSFRichTextString(""));
			}
			cell2.setCellStyle(styleAllTop);

			HSSFCell cell3 = row.createCell(3);
			if (!anulado.equals("ANULAMISMODIA")) {
				cell3.setCellValue(new HSSFRichTextString(repo.getEmpresa()
						.getRazon_soc()));
			} else {
				cell3.setCellValue(new HSSFRichTextString("RECIBO ANULADO"));
			}
			cell3.setCellStyle(styleAllTop);

			int indexComprobantes = i;
			if (!anulado.equals("ANULAMISMODIA")) {
				for (ReciboConcepto rc : rconceptos) {
					HSSFRow rowComp = null;
					if (indexComprobantes == i) {
						rowComp = row;
					} else {
						rowComp = sheet.createRow(indexComprobantes);
					}
					if (exportacion) {
						HSSFCell cell00 = rowComp.createCell(0);
						if (!anulado.equals("ANULAMISMODIA")) {
							cell00.setCellValue(new HSSFRichTextString(anulado
									+ repo.getNumero()));
						} else {
							cell00.setCellValue(new HSSFRichTextString(repo
									.getNumero()));
						}
						cell00.setCellStyle(styleAllTop);

						HSSFCell cell01 = rowComp.createCell(1);
						cell01.setCellValue(repo.getFecha());
						cell01.setCellStyle(styleFechaTop);

						HSSFCell cell02 = rowComp.createCell(2);
						if (!anulado.equals("ANULAMISMODIA")) {
							cell02.setCellValue(new HSSFRichTextString(repo
									.getEmpresa().getCuit()));
						} else {
							cell02.setCellValue(new HSSFRichTextString(""));
						}
						cell02.setCellStyle(styleAllTop);

						HSSFCell cell03 = rowComp.createCell(3);
						if (!anulado.equals("ANULAMISMODIA")) {
							cell03.setCellValue(new HSSFRichTextString(repo
									.getEmpresa().getRazon_soc()));
						} else {
							cell03.setCellValue(new HSSFRichTextString(
									"RECIBO ANULADO"));
						}
						cell03.setCellStyle(styleAllTop);

					}
					indexComprobantes++;
					HSSFCell cellConc = rowComp.createCell(4);
					cellConc.setCellValue(new HSSFRichTextString(rc
							.getDescripcion()));
					if (indexComprobantes - 1 == i) {
						cellConc.setCellStyle(styleAllLeftTop);
					} else {
						cellConc.setCellStyle(styleAllLeft);
					}
					
					HSSFCell cellPeriodo = rowComp.createCell(5);
					HSSFCell cellBoleta = rowComp.createCell(6);
					if(WebKeysGlobal.UOMA==entidad) {
						
						if(rc instanceof ReciboOtroConcepto) {
						   cellPeriodo.setCellValue( (DateUtils.format(((ReciboOtroConcepto) rc).getPeriodo(), DateUtils.SHORT)));
						   if(((ReciboOtroConcepto) rc).getBoletaNro()!=0) {
						      cellBoleta.setCellValue(((ReciboOtroConcepto) rc).getBoletaNro() );
						   }else {
							  cellBoleta.setCellValue(new HSSFRichTextString("")); 
						   }
						} else {
						   cellPeriodo.setCellValue(new HSSFRichTextString(""));
						   cellBoleta.setCellValue(new HSSFRichTextString(""));	
						}
					}else {
						cellPeriodo.setCellValue(new HSSFRichTextString(""));
						cellBoleta.setCellValue(new HSSFRichTextString(""));
					}
					
					if (indexComprobantes - 1 == i) {
						cellPeriodo.setCellStyle(styleAllTop);
						cellBoleta.setCellStyle(styleAllTop);
					} else {
						cellPeriodo.setCellStyle(styleAll);
						cellBoleta.setCellStyle(styleAll);
					}
					
					HSSFCell cellImp = rowComp.createCell(7);
					if (repo.getBaja_fecha() == null) {
						cellImp.setCellValue(null != rc
								&& rc.getImporte() != null ? rc.getImporte()
								.doubleValue() : 0);
						totales.setImporteConceptos(totales
								.getImporteConceptos()
								.add(null != rc && rc.getImporte() != null ? rc
										.getImporte() : BigDecimal.ZERO));
					} else {
						cellImp.setCellValue(null != rc
								&& rc.getImporte() != null ? rc.getImporte()
								.negate().doubleValue() : 0);
						totales.setImporteConceptos(totales
								.getImporteConceptos()
								.subtract(
										null != rc && rc.getImporte() != null ? rc
												.getImporte() : BigDecimal.ZERO));
					}

					if (indexComprobantes - 1 == i) {
						cellImp.setCellStyle(styleMoneyRightTop);
					} else {
						cellImp.setCellStyle(styleMoneyRight);
					}

				}

				indexIngreso = i;
				if (repo.getIngresos() != null) {
					for (ReciboIngreso ri : repo.getIngresos()) {
						String tipo = ri.getIngreso().getTipo();
						if (tipo.equals("Anticipo")) {
							continue;
						}
						HSSFRow rowIng = null;
						if (indexIngreso == i) {
							rowIng = row;
						} else {
							rowIng = sheet.getRow(indexIngreso);
							if (rowIng == null) {
								rowIng = sheet.createRow(indexIngreso);
							}
						}
						indexIngreso++;
						if (exportacion) {
							HSSFCell cell00 = rowIng.createCell(0);
							if (!anulado.equals("ANULAMISMODIA")) {
								cell00.setCellValue(new HSSFRichTextString(anulado
										+ repo.getNumero()));
							} else {
								cell00.setCellValue(new HSSFRichTextString(repo
										.getNumero()));
							}
							cell00.setCellStyle(styleAllTop);

							HSSFCell cell01 = rowIng.createCell(1);
							cell01.setCellValue(repo.getFecha());
							cell01.setCellStyle(styleFechaTop);

							HSSFCell cell02 = rowIng.createCell(2);
							if (!anulado.equals("ANULAMISMODIA")) {
								cell02.setCellValue(new HSSFRichTextString(repo
										.getEmpresa().getCuit()));
							} else {
								cell02.setCellValue(new HSSFRichTextString(""));
							}
							cell02.setCellStyle(styleAllTop);

							HSSFCell cell03 = rowIng.createCell(3);
							if (!anulado.equals("ANULAMISMODIA")) {
								cell03.setCellValue(new HSSFRichTextString(repo
										.getEmpresa().getRazon_soc()));
							} else {
								cell03.setCellValue(new HSSFRichTextString(
										"RECIBO ANULADO"));
							}
							cell03.setCellStyle(styleAllTop);

						}
						HSSFCell cellConc = rowIng.createCell(8);
						cellConc.setCellValue(new HSSFRichTextString(tipo + " "
								+ ri.getIngreso().getNumeroStr()));
						if (indexIngreso - 1 == i) {
							cellConc.setCellStyle(styleAllLeftTop);
						} else {
							cellConc.setCellStyle(styleAllLeft);
						}

						if (resumenPagos.get(tipo) == null) {
							resumenPagos.put(tipo, BigDecimal.ZERO);
						}
						HSSFCell cellImp = rowIng.createCell(9);
						if (repo.getBaja_fecha() == null) {
							cellImp.setCellValue(ri.getIngreso().getImporte()
									.doubleValue());
							totales.setImportePagos(totales.getImportePagos()
									.add(ri.getIngreso().getImporte()));

							resumenPagos.put(
									tipo,
									resumenPagos.get(tipo).add(
											ri.getIngreso().getImporte()));
						} else {
							cellImp.setCellValue(ri.getIngreso().getImporte()
									.negate().doubleValue());
							totales.setImportePagos(totales.getImportePagos()
									.subtract(ri.getIngreso().getImporte()));

							resumenPagos.put(tipo, resumenPagos.get(tipo)
									.subtract(ri.getIngreso().getImporte()));
						}
						if (indexIngreso - 1 == i) {
							cellImp.setCellStyle(styleMoneyRightTop);
						} else {
							cellImp.setCellStyle(styleMoneyRight);
						}
					}
				}
			}

			max = Math.max(indexComprobantes, indexIngreso);
			
			max = completarFilasVacias(sheet, i, indexComprobantes,
					indexIngreso, styleAllLeft, styleMoneyRight);

			if (max == i) {
				max++;
			}
		} else {
			max = i++;
		}
		return max;
	}

	private static boolean tieneConcepto(String[] conceptos,
			List<ReciboConcepto> rconceptos) {
		boolean tiene_concepto = false;
		if (null != conceptos && !conceptos[0].equals("null")) {
			for (String cpto : conceptos) {
				// Primero veo si el recibo tiene algï¿½n concepto buscado
				for (ReciboConcepto rcc : rconceptos) {
					if (rcc instanceof ReciboActa
							&& Integer.parseInt(cpto) == CONCEPTO_ACTA) {
						tiene_concepto = true;
						break;
					} else if (rcc instanceof ReciboConvenio
							&& Integer.parseInt(cpto) == CONCEPTO_CONVENIO) {
						tiene_concepto = true;
						break;
					} else if (rcc instanceof ReciboCheque
							&& (Integer.parseInt(cpto) == CONCEPTO_CHEQUE_NO_DEPOSITADO || Integer
									.parseInt(cpto) == CONCEPTO_CHEQUE_RECHAZADO)) {
						ReciboCheque reciboCheque = (ReciboCheque) rcc;
						if (reciboCheque.getTipo().equals(
								ReciboCheque.Tipo.NO_DEPOSITADO)
								&& Integer.parseInt(cpto) == CONCEPTO_CHEQUE_NO_DEPOSITADO) {
							tiene_concepto = true;
							break;
						} else if (reciboCheque.getTipo().equals(
								ReciboCheque.Tipo.RECHAZADO)
								&& Integer.parseInt(cpto) == CONCEPTO_CHEQUE_RECHAZADO) {
							tiene_concepto = true;
							break;
						}
					} else if (rcc instanceof ReciboOtroConcepto) {
						ReciboOtroConcepto reciboOtroConcepto = (ReciboOtroConcepto) rcc;
						if (reciboOtroConcepto.getConcepto().getId() == Integer
								.parseInt(cpto)) {
							tiene_concepto = true;
							break;
						}
					}
				}
			}
		} else {
			tiene_concepto = true;
		}
		return tiene_concepto;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb,Integer entidad) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Recibo"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Fecha"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Razon Social"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Concepto"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell8 = row.createCell(5);
		cell8.setCellValue(new HSSFRichTextString("Período"));
		cell8.setCellStyle(styleHeaderL);
		
		HSSFCell cell9 = row.createCell(6);
		cell9.setCellValue(new HSSFRichTextString("Boleta"));
		cell9.setCellStyle(styleHeaderL);

		HSSFCell cell5 = row.createCell(7);
		cell5.setCellValue(new HSSFRichTextString("Importe"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(8);
		cell6.setCellValue(new HSSFRichTextString("Forma de Pago"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(9);
		cell7.setCellValue(new HSSFRichTextString("Importe"));
		cell7.setCellStyle(styleHeaderL);

		
		if(WebKeysGlobal.UOMA==entidad) {
			
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 7, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return ++i;
	}

	private static int completarFilasVacias(HSSFSheet sheet, int primerFila,
			int indexComprobantes, int indexConceptos,
			HSSFCellStyle styleAllLeft, HSSFCellStyle styleMoneyRight) {
		int max = Math.max(indexComprobantes, indexConceptos);
		int fin = max - 1;

		if (indexComprobantes <= fin) {
			for (int i = indexComprobantes; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(4) == null) {
					HSSFCell createCell = row.createCell(4);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					
					HSSFCell createCell5 = row.createCell(5);
					createCell5.setCellValue(new HSSFRichTextString(" "));
					
					HSSFCell createCell6 = row.createCell(6);
					createCell6.setCellValue(new HSSFRichTextString(" "));
					
					HSSFCell createCell2 = row.createCell(7);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleMoneyRight);
				}
			}
		}
		if (indexConceptos <= fin) {
			for (int i = indexConceptos; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(8) == null) {
					HSSFCell createCell = row.createCell(8);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					HSSFCell createCell2 = row.createCell(9);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleMoneyRight);
				}
			}
		}

		return (fin + 1);
	}

	private static class TotalesRecibo {
		private BigDecimal importeConceptos = BigDecimal.ZERO;
		private BigDecimal importePagos = BigDecimal.ZERO;

		public void setImporteConceptos(BigDecimal importeConceptos) {
			this.importeConceptos = importeConceptos;
		}

		public BigDecimal getImporteConceptos() {
			return importeConceptos;
		}

		public void setImportePagos(BigDecimal importePagos) {
			this.importePagos = importePagos;
		}

		public BigDecimal getImportePagos() {
			return importePagos;
		}
	}
}
