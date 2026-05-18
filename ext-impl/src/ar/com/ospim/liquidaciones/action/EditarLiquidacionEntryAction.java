package ar.com.ospim.liquidaciones.action;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.BusquedaBandejaCorreoFiltro;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.PrestacionComprobanteExistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionPrestacionEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionAjuste;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarLiquidacionEntryAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class EditarLiquidacionEntryAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(actionRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String deletePrestaci = ParamUtil.getString(actionRequest,"deletePrestaci", null);
		
		int cambioEstadoNumero = ParamUtil.getInteger(actionRequest,"cambio_estado_numero", 0); 	
		int paga = ParamUtil.getInteger(actionRequest, "paga", 0);
		int idLiquidacion = 0;
		boolean errors = false;
		
		//  Pasa el dato si tiene reclamo prestacional el afiliado selecccionado par el reintegro
		String conReclamoPrestacional = ParamUtil.getString(actionRequest, "con_reclamo_prestacional");
		actionRequest.setAttribute("con_reclamo_prestacional", conReclamoPrestacional);
	
		//String origen=ParamUtil.getString(actionRequest, "origen", null);
	
		
		
		try {
			if (cmd != null) {
				
				if(cmd.equals("upload")){
					UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
					String fileName = uploadReq.getFileName("archivo").toLowerCase();
					_log.info("subiendo archivo :" + fileName);
					if (fileName != null) {
						File zip = uploadReq.getFile("archivo");
						String ss ="";
						if ( fileName.endsWith(".xls")) {
							
							FileInputStream file = new FileInputStream(zip);
							HSSFWorkbook workbook = new HSSFWorkbook(file);
							
							HSSFSheet sheet = workbook.getSheetAt(0);
							Iterator<Row> rowIterator = sheet.iterator();
							
					        while (rowIterator.hasNext()) {
					        	Row currentRow = rowIterator.next();
					        	Iterator<Cell> cellIterator = currentRow.iterator();
					        	while (cellIterator.hasNext()) {
					        		Cell currentCell = cellIterator.next();
					        		int cellIndex = currentCell.getColumnIndex();
					        		Double xval;
					        		switch (cellIndex) {
									case 0:
										xval= currentCell.getNumericCellValue();
										ss +=String.valueOf(xval.longValue()) +";";
										break;
					        		}	
					        	}
					        }	
							session.setAttribute("LIQUIDACIONES_PROCESAR_IMAGENES", ss );	
						}
					}	
				}
				
				
				
				if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
					if (!(paga == 1)) {
						idLiquidacion = updateLiquidacionEntry(actionRequest,
								cmd);
					} else {
						idLiquidacion = ajustarLiquidacionEntry(actionRequest,
								cmd);
						actionRequest.setAttribute("paga", "1");
					}
					actionRequest.setAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION, idLiquidacion);
					
//				Esto es para quitar el item de correspondencia ya liquidado, de los resultados de la busqueda
				List<ItemCorrespondencia> correspondencias = 
						(ArrayList<ItemCorrespondencia>) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT);
				session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT);
				
				Integer posicionItemCorrEnBusqResult =  (Integer) session.getAttribute("posicion_item_corresp_busq_result");
				if(correspondencias != null && correspondencias.size() >0 && posicionItemCorrEnBusqResult != null){
//					correspondencias.remove(posicionItemCorrEnBusqResult);
					ItemCorrespondencia i = correspondencias.get(posicionItemCorrEnBusqResult);
					correspondencias.remove(i);
					
					BusquedaBandejaCorreoFiltro f = (BusquedaBandejaCorreoFiltro) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
					session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
					
					f.setRegistrosTotal(f.getRegistrosTotal()-1);
					
					session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA, f);
					
					session.removeAttribute("posicion_item_corresp_busq_result");
					session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT, correspondencias);
				}
//				fin quitar el item correspondencia
				
				} else if (cmd.equals(Constants.DELETE)
						&& deletePrestaci == null) {
					borraLiquidacionEntry(actionRequest);
					setForward(actionRequest, "portlet.liquidaciones.view");
				} else if (cmd.equals(Constants.DELETE)
						&& deletePrestaci != null) {
					if (!(paga == 1)) {
						idLiquidacion = borraLiquidacionPrestacionEntry(actionRequest);
					} else {
						idLiquidacion = ajustarBorraLiquidacionPrestacionEntry(actionRequest);
						actionRequest.setAttribute("paga", "1");
					}
					actionRequest.setAttribute(
							WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION,
							idLiquidacion);
				}
			} else {
				if (cambioEstadoNumero != 0) {
					int estado = ParamUtil.getInteger(actionRequest,
							"estado_futuro", 1);
					if (estado == WebKeysLiquidaciones.LIQUIDACION_ESTADO_CERRADO) {
						if (EditarLiquidacionServiceUtil
								.validarCierreLiquidacion(cambioEstadoNumero,
										actionRequest)) {
							idLiquidacion = cambiarEstadoLiquidacionEntry(
									cambioEstadoNumero, actionRequest);
						} else {
							StringBuilder error = getErrorPorCierre();
							putError(actionRequest, error);
							errors = true;
						}
					} else if (estado == WebKeysLiquidaciones.LIQUIDACION_ESTADO_MODIFICAR_PAGA) {
						if (!EditarLiquidacionServiceUtil
								.validarCierreLiquidacionPaga(
										cambioEstadoNumero, actionRequest)) {
							StringBuilder error = getErrorPorCierre();
							putError(actionRequest, error);
							errors = true;
						}
					}
					actionRequest.setAttribute(
							WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION,
							cambioEstadoNumero);
				}
			}
		
//		unifico la excepcion del comprobante con la fecha de cierre contable	
//		} catch (ComprobanteExistenteException e) {
//			SessionErrors.add(actionRequest, e.getClass().getName());
//			LiquidacionPrestacion lPrestacion = getLiquidacionPrestacionFromRequest(actionRequest);
//			Liquidacion liquidacion = getLiquidacionFromRequest(actionRequest);
//			if(liquidacion.getId_liquidacion()>0) {
//				liquidacion.setPrestador_lugar_atencion(new PrestadorLugarAtencion());
//				liquidacion.getPrestador_lugar_atencion().setPrestador(new Prestador(liquidacion.getId_prestador()));
//				if(liquidacion.getLiquidacionPrestacion()==null) {
//					Liquidacion liq = EditarLiquidacionServiceUtil.getLiquidacionEntry(liquidacion.getId_liquidacion());
//					liquidacion.setLiquidacionPrestacion(liq.getLiquidacionPrestacion());
//					if(liquidacion.getImporte()==null) {
//						liquidacion.setImporte(liq.getImporte());
//					}
//				}
//				if(lPrestacion.getCargoPrestadora()==null) lPrestacion.setCargoPrestadora(BigDecimal.ZERO);
//				lPrestacion.setId_prestacion(0);
//			    session.setAttribute(
//						WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION,
//						liquidacion.getLiquidacionPrestacion());
//			}else {
//				liquidacion.setCompro_a_debitar_letra("");
//				liquidacion.setCompro_a_debitar_numero("");
//				liquidacion.setCompro_a_debitar_tipo("");
//				liquidacion.setSucu(0);
//			}
//			
//			actionRequest.setAttribute(
//					WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
//			actionRequest.setAttribute(
//					WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,
//					lPrestacion);
//			
//			session.setAttribute(
//					WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
//			session.setAttribute(
//					WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,
//					lPrestacion);
//			
//			setForward(actionRequest,
//					"portlet.liquidaciones.editar_liquidacion_entry");
		/* SE SOLICITA QUE SE PUEDA LIQUIDAR SIN QUE ESTE EN LA TABLA EMPRESA
		 * } catch (EmpresaNoExisteConTalCuitException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			putError(
					actionRequest,
					new StringBuilder(
							"Prestador inválido, verifique que el CUIT del prestador es válido"));
			LiquidacionPrestacion lPrestacion = getLiquidacionPrestacionFromRequest(actionRequest);
			Liquidacion liquidacion = getLiquidacionFromRequest(actionRequest);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,
					lPrestacion);
			setForward(actionRequest,
					"portlet.liquidaciones.editar_liquidacion_entry");*/
		}catch (PrestacionComprobanteExistenteException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			LiquidacionPrestacion lPrestacion = getLiquidacionPrestacionFromRequest(actionRequest);
			Liquidacion liquidacion = getLiquidacionFromRequest(actionRequest);
			if(liquidacion.getId_liquidacion()>0) {
				liquidacion.setPrestador_lugar_atencion(new PrestadorLugarAtencion());
				liquidacion.getPrestador_lugar_atencion().setPrestador(new Prestador(liquidacion.getId_prestador()));
				if(liquidacion.getLiquidacionPrestacion()==null) {
					Liquidacion liq = EditarLiquidacionServiceUtil.getLiquidacionEntry(liquidacion.getId_liquidacion());
					liquidacion.setLiquidacionPrestacion(liq.getLiquidacionPrestacion());
					if(liquidacion.getImporte()==null) {
						liquidacion.setImporte(liq.getImporte());
					}
				}
				if(lPrestacion.getCargoPrestadora()==null) lPrestacion.setCargoPrestadora(BigDecimal.ZERO);
				lPrestacion.setId_prestacion(0);
			    session.setAttribute(
						WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION,
						liquidacion.getLiquidacionPrestacion());
			}else {
				liquidacion.setCompro_a_debitar_letra("");
				liquidacion.setCompro_a_debitar_numero("");
				liquidacion.setCompro_a_debitar_tipo("");
				liquidacion.setSucu(0);
			}
			
			actionRequest.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,
					lPrestacion);
			
			session.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
			session.setAttribute(
					WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,
					lPrestacion);
			
			setForward(actionRequest,"portlet.liquidaciones.editar_liquidacion_entry");
			
		}catch (Exception e) {
			
			if (e instanceof FechaMenorACierreContableException || 
				e instanceof ComprobanteExistenteException) {
				
				SessionErrors.add(actionRequest, e.getClass().getName());
				
				LiquidacionPrestacion lPrestacion = getLiquidacionPrestacionFromRequest(actionRequest);
				Liquidacion liquidacion = getLiquidacionFromRequest(actionRequest);
				if(liquidacion.getId_liquidacion()>0) {
					liquidacion.setPrestador_lugar_atencion(new PrestadorLugarAtencion());
					liquidacion.getPrestador_lugar_atencion().setPrestador(new Prestador(liquidacion.getId_prestador()));
					if(liquidacion.getLiquidacionPrestacion()==null) {
						Liquidacion liq = EditarLiquidacionServiceUtil.getLiquidacionEntry(liquidacion.getId_liquidacion());
						liquidacion.setLiquidacionPrestacion(liq.getLiquidacionPrestacion());
						if(liquidacion.getImporte()==null) {
							liquidacion.setImporte(liq.getImporte());
						}
					}
					if(lPrestacion.getCargoPrestadora()==null) lPrestacion.setCargoPrestadora(BigDecimal.ZERO);
					lPrestacion.setId_prestacion(0);
				    session.setAttribute(
							WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION,
							liquidacion.getLiquidacionPrestacion());
				}else {
					liquidacion.setCompro_a_debitar_letra("");
					liquidacion.setCompro_a_debitar_numero("");
					liquidacion.setCompro_a_debitar_tipo("");
					liquidacion.setSucu(0);
				}
				
				actionRequest.setAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
				actionRequest.setAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,lPrestacion);
				
				session.setAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION, liquidacion);
				session.setAttribute(WebKeysLiquidaciones.LIQUIDACION_PRESTACION_EN_EDICION,lPrestacion);
				
				setForward(actionRequest,"portlet.liquidaciones.editar_liquidacion_entry");
			
			}else if (e instanceof NoSuchLiquidacionEntryException
					|| e instanceof DuplicateLiquidacionIdException
					|| e instanceof NoSuchLiquidacionPrestacionEntryException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
				setForward(actionRequest, "portlet.liquidaciones.error");
			} else {
				throw e;
			}
		}	
		if (SessionErrors.isEmpty(actionRequest) && !errors) {
			String successMessage = ParamUtil.getString(actionRequest,"successMessage");
			SessionMessages.add(actionRequest, "request_processed",successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		/*
		String origen=ParamUtil.getString(renderRequest, "origen", null);
		String accion = ParamUtil.getString(renderRequest,"accion", null);
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		if(origen==null) origen =(String) session.getAttribute("origen");
		session.setAttribute("origen", origen);
		*/
		if(cmd!=null && cmd.equals("upload")){
			 return mapping.findForward("portlet.liquidaciones.reporte.imagenes_liquidaciones");	
		}
		
		try {						 						
			TraeListasServiceUtil.getMotivosDebito(renderRequest);
			TraeListasServiceUtil.getConceptoLiquidacion(renderRequest);
			LiquidacionActionUtil.getLiquidacionEntry(renderRequest);
		} catch (Exception e) {
			if (e instanceof NoSuchLiquidacionPrestacionEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.liquidaciones.error");
			} else {
				throw e;
			}
		}
		return mapping.findForward(getForward(renderRequest,"portlet.liquidaciones.editar_liquidacion_entry"));
/*		
		if(("update".equalsIgnoreCase(cmd) && "hospitales".equalsIgnoreCase(origen)) 
				) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.comprobantes.comprobantes_hospitales_list"));
		}else {
		   return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_liquidacion_entry"));
		}
*/		
	}

	protected void borraLiquidacionEntry(ActionRequest actionRequest)
			throws Exception {
		int idLiquidacion = ParamUtil.getInteger(actionRequest, "numero", 0);
		User user = PortalUtil.getUser(actionRequest);
		EditarLiquidacionServiceUtil.borraLiquidacionEntry(idLiquidacion, user);
	}

	private void putError(ActionRequest actionRequest, StringBuilder error) {
		actionRequest.setAttribute(WebKeysLiquidaciones.ERROR_PARA_ALERT, error.toString());
	}

	private StringBuilder getErrorPorCierre() {
		StringBuilder error = new StringBuilder();
		error.append("El importe total de la factura debe ser igual al valor de la Suma de Débitos,\\n más el total de las Prestaciones más el Importe por Conceptos. Ajuste los importes antes de cerrarlo");
		return error;
	}

	protected int borraLiquidacionPrestacionEntry(ActionRequest actionRequest)
			throws Exception {
		int idLiquidacion = ParamUtil.getInteger(actionRequest,
				"borrar_numero", 0);
		int orden = ParamUtil.getInteger(actionRequest, "borrar_orden", 0);

		int idReclamo  = ParamUtil.getInteger(actionRequest, "borrar_id_reclamo_prestacion", 0);
		int idPrestacionReclamo= ParamUtil.getInteger(actionRequest, "borrar_id_prestacion_reclamo", 0);
		
		
		User user = PortalUtil.getUser(actionRequest);
		EditarLiquidacionServiceUtil.borraLiquidacionPrestacionEntry(
				idLiquidacion, orden, user,idReclamo ,idPrestacionReclamo );
		return idLiquidacion;
	}

	// obtiene la lista de ajustes de sesión,
	// si está el item seleccionado en la lista con marca add hacer remove del
	// objeto y punto
	// si está el item seleccionado en la lista con marca edit actualizar la
	// marca por marca delete
	// si no está el item entonces crear uno nuevo con marca delete
	protected int ajustarBorraLiquidacionPrestacionEntry(
			ActionRequest actionRequest) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(actionRequest);

		HttpSession session = (HttpSession) httpServletRequest.getSession();
		ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste = (ArrayList<LiquidacionPrestacionAjuste>) session
				.getAttribute("lista_ajustes_prestaciones");

		if (listaPrestacionAjuste == null) {
			listaPrestacionAjuste = new ArrayList<LiquidacionPrestacionAjuste>();
		}
		LiquidacionPrestacionAjuste liquidacionPrestacionAjuste = new LiquidacionPrestacionAjuste();

		int idLiquidacion = ParamUtil.getInteger(actionRequest,"borrar_numero", 0);
		liquidacionPrestacionAjuste.setId_liquidacion(idLiquidacion);

		int orden = ParamUtil.getInteger(actionRequest, "borrar_orden", 0);
		liquidacionPrestacionAjuste.setOrden(orden);

		boolean estaEnLista = false;
		for (LiquidacionPrestacionAjuste liquidacionPrestacionAjustei : listaPrestacionAjuste) {
			if (orden == liquidacionPrestacionAjustei.getOrden()) {
				estaEnLista = true;
				if (liquidacionPrestacionAjustei.getAjuste().equals("ADD")) {
					listaPrestacionAjuste.remove(liquidacionPrestacionAjustei);
					break;
				} else if (liquidacionPrestacionAjustei.getAjuste().equals("EDIT")) {
					listaPrestacionAjuste.remove(liquidacionPrestacionAjustei);
					liquidacionPrestacionAjuste.setAjuste("DELETE");
					listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
					break;
				}
			}
		}
		if (!estaEnLista) {
			liquidacionPrestacionAjuste.setAjuste("DELETE");
			listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
		}
		return idLiquidacion;
	}

	protected int updateLiquidacionEntry(ActionRequest actionRequest,
			String command) throws Exception {

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		
		
		//////////////
		// datos del reclamo y de a prestacion de existir 
		int idReclamo = ParamUtil.getInteger(actionRequest, "id_reclamo_prestacional", 0);
		int idPrestacionReclamo= ParamUtil.getInteger(actionRequest, "id_prestacion_reclamo_prestacional", 0);
	
	

		String fechaEDia = ParamUtil.getString(actionRequest, "fechaEDia");
		String fechaEMes = ParamUtil.getString(actionRequest, "fechaEMes");
		String fechaEAnio = ParamUtil.getString(actionRequest, "fechaEAnio");
		Date fechaE = null;
		try {
			fechaE = formatoDeFecha.parse(fechaEDia + "/"
					+ (Integer.parseInt(fechaEMes) + 1) + "/" + fechaEAnio);
		} catch (Exception e) {
			fechaE = null;
		}

		String fechaRDia = ParamUtil.getString(actionRequest, "fechaRDia");
		String fechaRMes = ParamUtil.getString(actionRequest, "fechaRMes");
		String fechaRAnio = ParamUtil.getString(actionRequest, "fechaRAnio");
		Date fechaR = null;
		try {
			fechaR = formatoDeFecha.parse(fechaRDia + "/"
					+ (Integer.parseInt(fechaRMes) + 1) + "/" + fechaRAnio);
		} catch (Exception e) {
			fechaR = null;
		}

		String fechaVDia = ParamUtil.getString(actionRequest, "fechaVDia");
		String fechaVMes = ParamUtil.getString(actionRequest, "fechaVMes");
		String fechaVAnio = ParamUtil.getString(actionRequest, "fechaVAnio");
		Date fechaV = null;
		try {
			fechaV = formatoDeFecha.parse(fechaVDia + "/"
					+ (Integer.parseInt(fechaVMes) + 1) + "/" + fechaVAnio);
		} catch (Exception e) {
			fechaV = null;
		}

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(actionRequest,
				"periodoMesAnio");
		Date periodo = null;
		try {
			periodo = formatoDePeriodos.parse(Integer.parseInt(periodoMesAnio
					.substring(0, 1))
					+ 1 + "/" + periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		if (periodo == null) {
			try {
				periodo = formatoDePeriodos.parse(Integer
						.parseInt(periodoMesAnio.substring(0, 2))
						+ 1 + "/" + periodoMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodo = null;
			}
		}

		int idPrestador = ParamUtil.getInteger(actionRequest, "id_prestador",0);
		int idDomicilio = ParamUtil.getInteger(actionRequest, "id_domicilio",0);
		String tipoCompro = ParamUtil.getString(actionRequest,
				"comprobante_tipo", null);
		String letraCompro = ParamUtil.getString(actionRequest,
				"comprobante_letra", null);
		int sucu = ParamUtil.getInteger(actionRequest, "sucu", 0);
		String nroCompro = ParamUtil.getString(actionRequest,
				"comprobante_nro", null);

		String observaciones = ParamUtil.getString(actionRequest,
				"observaciones", "");
		String importeTotal = ParamUtil.getString(actionRequest,
				"importe_total", "0");
		String debitado = ParamUtil
				.getString(actionRequest, "debitos_cab", "0");

		// Orden de Compra.
		String nroOC = ParamUtil.getString(actionRequest,"nro_oc", "");
		
		String tercerizadoCab = ParamUtil.getString(actionRequest,
				"tercerizado_cab", "0");

		String entidadLiquidacion = ParamUtil.getString(actionRequest,
				"entidad_liquidacion", null);

		// datos de la prestación

		String entidad = ParamUtil.getString(actionRequest, "entidad", null);

		String cuil_titular = ParamUtil.getString(actionRequest, "cuil", null);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		int seccional = ParamUtil.getInteger(actionRequest, "id_seccional", 0);
		int numero = ParamUtil.getInteger(actionRequest, "numero", 0);

		String idTercerizadora = ParamUtil.getString(actionRequest, "id_tercerizadora", "");

		if (StringUtils.checkEmpty(idTercerizadora)
		        || "null".equalsIgnoreCase(idTercerizadora)
		        || "undefined".equalsIgnoreCase(idTercerizadora)) {
		    idTercerizadora = null;
		}
		
		String prestacionFechaDia = ParamUtil.getString(actionRequest,
				"prestacionFechaDia");
		String prestacionFechaMes = ParamUtil.getString(actionRequest,
				"prestacionFechaMes");
		String prestacionFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionFechaAnio");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}

		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		String cuit_prestador = ParamUtil.getString(actionRequest,
				"cuit_prestador", "");

		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0");
		String importe = ParamUtil.getString(actionRequest, "importe", "0");
		String solicitado = ParamUtil.getString(actionRequest, "solicitado",
				"0");
		String resultado = ParamUtil.getString(actionRequest, "resultado", "0");

		String servicio = ParamUtil.getString(actionRequest, "servicio", "0");

		String tercerizado = ParamUtil.getString(actionRequest,
				"descontar_capitas", null);

		int id_concepto = ParamUtil.getInteger(actionRequest, "id_concepto", 0);
		String importe_concepto = ParamUtil.getString(actionRequest,
				"importe_concepto", "0");
				
		String periodoPrestacionMesAnio = ParamUtil.getString(actionRequest,
				"periodoPrestacionMesAnio");
		
		int motivoAltaDiscapacidad = ParamUtil.getInteger(actionRequest, "motivoAltaDiscapacidad");
		
		Date periodoPrestacion = null;
		try {
			periodoPrestacion = formatoDePeriodos.parse(Integer.parseInt(periodoPrestacionMesAnio
					.substring(0, 1))
					+ 1 + "/" + periodoPrestacionMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoPrestacion = null;
		}
		if (periodoPrestacion == null) {
			try {
				periodoPrestacion = formatoDePeriodos.parse(Integer
						.parseInt(periodoPrestacionMesAnio.substring(0, 2))
						+ 1 + "/" + periodoPrestacionMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodoPrestacion = null;
			}
		}
		
		String cargoOspim= "0";
		String cargoPrestadora = "0";
		String cargoOmint = "0";
		String cargoEnSalud = "0";
		String cargoCemic="0";
		String cargoImesa="0";
		String cargoCes="0";
		if ("1".equals(tercerizadoCab)) {
			cargoOspim =  StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest, "cargo_ospim_sin_detalle")) == true ? ParamUtil.getString(actionRequest, "cargo_ospim_sin_detalle") : "0.00";
			cargoPrestadora = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest, "cargo_prestadora_sin_detalle")) == true ? ParamUtil.getString(actionRequest, "cargo_prestadora_sin_detalle") : "0.00";
			importe_concepto = ParamUtil.getString(actionRequest,"importe_concepto_sin_detalle", "0");
			cargoOmint = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest,"cargo_omint_sin_detalle", "0")) == true ?  ParamUtil.getString(actionRequest,"cargo_omint_sin_detalle", "0") : "0.00";
			cargoEnSalud = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest,"cargo_prestadora_en_salud_sin_detalle", "0")) == true ?  ParamUtil.getString(actionRequest,"cargo_prestadora_en_salud_sin_detalle", "0") : "0.00";
			cargoCemic = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest,"cargo_cemic_sin_detalle", "0")) == true ?  ParamUtil.getString(actionRequest,"cargo_cemic_sin_detalle", "0") : "0.00";
			cargoImesa = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest,"cargo_imesa_sin_detalle", "0")) == true ?  ParamUtil.getString(actionRequest,"cargo_imesa_sin_detalle", "0") : "0.00";
			cargoCes = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest,"cargo_ces_sin_detalle", "0")) == true ?  ParamUtil.getString(actionRequest,"cargo_ces_sin_detalle", "0") : "0.00";

		}else {
			cargoOspim = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest, "cargo_ospim")) == true ? ParamUtil.getString(actionRequest, "cargo_ospim") : "0.00";
			cargoPrestadora = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest, "cargo_prestadora")) == true ? ParamUtil.getString(actionRequest, "cargo_prestadora") : "0.00";
			cargoImesa = StringUtils.checkNotEmpty(ParamUtil.getString(actionRequest, "cargo_imesa")) == true ? ParamUtil.getString(actionRequest, "cargo_imesa") : "0.00";			
		}
		


		// Futuro, liquidacion tipo prestacional
		// String tipo_liquidacion = WebKeysLiquidaciones.LIQUIDACION_PRE;
		String editPrestaci = ParamUtil.getString(actionRequest,
				"editPrestaci", "");
		
		Date fechaCierrePeriodo = ContabilidadServiceUtil.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM); //entidad_liquidacion es O.S.P.I.M
		if (fechaR.compareTo(fechaCierrePeriodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}
		
		
		User user = PortalUtil.getUser(actionRequest);
		if (command.equals(Constants.ADD)) {
			// Add afiliado entry
			HttpServletRequest httpServletRequest = PortalUtil
			.getHttpServletRequest(actionRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();

			session.removeAttribute("cuil_titular_servicio");
			session.removeAttribute("inte_servicio");
			session.removeAttribute("servicio");
			session.removeAttribute("fecha_prestacion_servicio");

			numero = EditarLiquidacionServiceUtil.cargaLiquidacionEntry(fecha,
					fechaE, fechaR, fechaV, periodo, entidadLiquidacion,
					idPrestador, idDomicilio, tipoCompro, letraCompro,
					sucu, nroCompro, entidad, cuil_titular, inte, seccional,
					prestacionFecha, id_prestacion,
					WebKeysLiquidaciones.LIQUIDACION_PRE,
					WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO, user,
					cantidad, importe, tercerizado, solicitado, "0", resultado,
					servicio, importeTotal, debitado, nroOC, observaciones,
					tercerizadoCab, cuit_prestador, id_concepto,
					importe_concepto, periodoPrestacion, motivoAltaDiscapacidad,
					idReclamo,idPrestacionReclamo , new BigDecimal(cargoOspim) ,
					new BigDecimal(cargoPrestadora), 	new BigDecimal(cargoOmint) , new BigDecimal(cargoEnSalud),new BigDecimal(cargoCemic),
					new BigDecimal(cargoImesa),new BigDecimal(cargoCes), idTercerizadora);
			//Borro primero posibles daots de la sesión 
			if (EditarLiquidacionServiceUtil.servicioEspecial(servicio)) {
				session.setAttribute("cuil_titular_servicio", cuil_titular);
				session.setAttribute("inte_titular_servicio", inte);
				session.setAttribute("servicio", servicio);
				session.setAttribute("fecha_prestacion_servicio", prestacionFecha);
			} else {
				session.removeAttribute("cuil_titular_servicio");
				session.removeAttribute("inte_servicio");
				session.removeAttribute("servicio");
				session.removeAttribute("fecha_prestacion_servicio");
			}
		} else {
			if (editPrestaci.length() == 0) {
				EditarLiquidacionServiceUtil.actualizaLiquidacionEntry(numero,
						fecha, fechaE, fechaR, fechaV, periodo, cuil_titular,
						inte, idPrestador, idDomicilio, id_prestacion,
						prestacionFecha, cantidad, importe, tipoCompro,
						letraCompro, sucu, nroCompro, tercerizado, user,
						servicio, solicitado, "0", resultado, importeTotal,
						debitado, nroOC, observaciones, tercerizadoCab,
						cuit_prestador, id_concepto, importe_concepto, actionRequest, 
						periodoPrestacion, motivoAltaDiscapacidad,idReclamo
						,idPrestacionReclamo , new BigDecimal(cargoOspim) ,
						new BigDecimal(cargoPrestadora) , new BigDecimal(cargoOmint), 
						new BigDecimal(cargoEnSalud),new BigDecimal(cargoCemic),
						new BigDecimal(cargoImesa),new BigDecimal(cargoCes), idTercerizadora);
			} else {
				int orden = ParamUtil.getInteger(actionRequest, "orden", 0);
				EditarLiquidacionServiceUtil
						.actualizaLiquidacionPrestacionEntry(numero, orden,
								prestacionFecha, servicio, cuil_titular, inte,
								id_prestacion, cantidad, importe, tercerizado,
								user, periodoPrestacion, motivoAltaDiscapacidad,idPrestador, idTercerizadora);
			}
		}
		return numero;
	}

	/*
	 * funcion que añade o edita prestacion EN SESSIONobtiene la lista
	 * de ajustes de sesión,crear el objeto con los datos del request y con la
	 * marca de add o de edit,si la marca es add obtener la cantidad de adds en
	 * la lista con número de item negativoy agregarlo a la lista de ajustes con
	 * esa marca addsi la marca es editverificar si está en la lista de ajustes
	 * con marca add, en ese caso, crear la instancia y reemplazarlos datos de
	 * la lista conservando la marca addsi está en la lista con marca edit,
	 * crear la instancia y reemplazar los datos conservando la marca edit /si
	 * no está en la lista agregarlo a la lista con marca edit
	 */
	protected int ajustarLiquidacionEntry(ActionRequest actionRequest,
			String command) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil
				.getHttpServletRequest(actionRequest);

		User user = PortalUtil.getUser(actionRequest);

		HttpSession session = (HttpSession) httpServletRequest.getSession();
		ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste = (ArrayList<LiquidacionPrestacionAjuste>) session
				.getAttribute("lista_ajustes_prestaciones");

		if (listaPrestacionAjuste == null) {
			listaPrestacionAjuste = new ArrayList<LiquidacionPrestacionAjuste>();
		}
		LiquidacionPrestacionAjuste liquidacionPrestacionAjuste = new LiquidacionPrestacionAjuste();

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");

		// datos de la prestación

		String cuil_titular = ParamUtil.getString(actionRequest, "cuil", null);
		liquidacionPrestacionAjuste.setCuil_titular(cuil_titular);

		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		liquidacionPrestacionAjuste.setInte(inte);

		String apellido = ParamUtil.getString(actionRequest, "apellido", "");
		String nombre = ParamUtil.getString(actionRequest, "nombre", "");

		Afiliado afiliado = new Afiliado(cuil_titular, inte, nombre, apellido);

		liquidacionPrestacionAjuste.setAfiliado(afiliado);

		int numero = ParamUtil.getInteger(actionRequest, "numero", 0);
		liquidacionPrestacionAjuste.setId_liquidacion(numero);
		
		String idTercerizadora = ParamUtil.getString(actionRequest, "id_tercerizadora", "");

		if (StringUtils.checkEmpty(idTercerizadora)
		        || "null".equalsIgnoreCase(idTercerizadora)
		        || "undefined".equalsIgnoreCase(idTercerizadora)) {
		    idTercerizadora = null;
		}

		liquidacionPrestacionAjuste.setIdTercerizadora(idTercerizadora);

		String prestacionFechaDia = ParamUtil.getString(actionRequest,
				"prestacionFechaDia");
		String prestacionFechaMes = ParamUtil.getString(actionRequest,
				"prestacionFechaMes");
		String prestacionFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionFechaAnio");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}
		liquidacionPrestacionAjuste.setFecha_prestacion(prestacionFecha);

		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		String codigo = ParamUtil.getString(actionRequest, "codigo", "");

		liquidacionPrestacionAjuste.setId_prestacion(id_prestacion);

		Prestacion prestacion = new Prestacion(id_prestacion, "");
		prestacion.setCodigo(codigo);

		liquidacionPrestacionAjuste.setPrestacion(prestacion);

		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0");
		liquidacionPrestacionAjuste.setCantidad(new BigDecimal(cantidad));

		String importe = ParamUtil.getString(actionRequest, "importe", "0");
		liquidacionPrestacionAjuste.setImporte(new BigDecimal(importe));
		liquidacionPrestacionAjuste.generateImporteTotal();

		String solicitado = ParamUtil.getString(actionRequest, "solicitado",
				"0");
		liquidacionPrestacionAjuste.setSolicitado(new BigDecimal(solicitado));

		String resultado = ParamUtil.getString(actionRequest, "resultado", "0");
		liquidacionPrestacionAjuste.setResultado(new BigDecimal(resultado));

		String servicio = ParamUtil.getString(actionRequest, "servicio", "0");
		liquidacionPrestacionAjuste.setServicio(servicio);

		String tercerizado = ParamUtil.getString(actionRequest,
				"descontar_capitas", null);
		liquidacionPrestacionAjuste.setTercerizado(tercerizado);
		int orden = ParamUtil.getInteger(actionRequest, "orden", 0);
		liquidacionPrestacionAjuste.setOrden(orden);

		String periodoPrestacionMesAnio = ParamUtil.getString(actionRequest,
				"periodoPrestacionMesAnio");
		
		Date periodoPrestacion = null;
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		
		try {
			periodoPrestacion = formatoDePeriodos.parse(Integer
					.parseInt(periodoPrestacionMesAnio.substring(0, 1))
					+ 1 + "/" + periodoPrestacionMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoPrestacion = null;
		}
		if (periodoPrestacion == null) {
			try {
				periodoPrestacion = formatoDePeriodos.parse(Integer
						.parseInt(periodoPrestacionMesAnio.substring(0, 2))
						+ 1 + "/" + periodoPrestacionMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodoPrestacion = null;
			}
		}
		
		liquidacionPrestacionAjuste.setPeriodo(periodoPrestacion);
		
		String editPrestaci = ParamUtil.getString(actionRequest,
				"editPrestaci", "");

		int motivoAltaDiscapacidad = ParamUtil.getInteger(actionRequest, "motivoAltaDiscapacidad");
		liquidacionPrestacionAjuste.setMotivoAltaDiscapacidad(motivoAltaDiscapacidad);
		
		if (editPrestaci.length() == 0) {
			// crear el objeto con los datos del request y con la marca de add o
			// de edit,
			// si la marca es add obtener la cantidad de adds en la lista con
			// número de item negativo
			// y agregarlo a la lista de ajustes con esa marca add
			liquidacionPrestacionAjuste.setAjuste("ADD");
			int item = -1;
			for (LiquidacionPrestacionAjuste liquidacionPrestacionAjustei : listaPrestacionAjuste) {
				if (liquidacionPrestacionAjustei.getOrden() < 0) {
					item--;
				}
			}
			liquidacionPrestacionAjuste.setOrden(item);
			listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
		} else {
			// verificar si está en la lista de ajustes con marca add, en ese
			// caso, crear la instancia y reemplazar
			// los datos de la lista conservando la marca add
			// si está en la lista con marca edit, crear la instancia y
			// reemplazar los datos conservando la marca edit
			// si no está en la lista agregarlo a la lista con marca edit
			boolean estaEnLista = false;
			for (LiquidacionPrestacionAjuste liquidacionPrestacionAjustei : listaPrestacionAjuste) {
				if (orden == liquidacionPrestacionAjustei.getOrden()) {
					estaEnLista = true;
					if (liquidacionPrestacionAjustei.getAjuste().equals("ADD")) {
						listaPrestacionAjuste
								.remove(liquidacionPrestacionAjustei);
						liquidacionPrestacionAjuste.setAjuste("ADD");
						listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
						break;
					} else if (liquidacionPrestacionAjustei.getAjuste().equals(
							"EDIT")) {
						listaPrestacionAjuste
								.remove(liquidacionPrestacionAjustei);
						liquidacionPrestacionAjuste.setAjuste("EDIT");
						listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
						break;
					}
				}
			}
			if (!estaEnLista) {
				liquidacionPrestacionAjuste.setAjuste("EDIT");
				listaPrestacionAjuste.add(liquidacionPrestacionAjuste);
			}
		}

		return numero;
	}

	protected int cambiarEstadoLiquidacionEntry(int id_liquidacion,
			ActionRequest actionRequest) throws Exception {
		User user = PortalUtil.getUser(actionRequest);
		int estado = ParamUtil.getInteger(actionRequest, "estado_futuro", 1);

		EditarLiquidacionServiceUtil.cambiarEstadoLiquidacionEntry(
				id_liquidacion, estado, user.getScreenName());
		return id_liquidacion;
	}

	private Liquidacion getLiquidacionFromRequest(ActionRequest actionRequest) {
		Liquidacion liquidacion = new Liquidacion();

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaAnio = ParamUtil.getString(actionRequest, "fechaAnio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}

		liquidacion.setFecha(fecha);
		String fechaEDia = ParamUtil.getString(actionRequest, "fechaEDia");
		String fechaEMes = ParamUtil.getString(actionRequest, "fechaEMes");
		String fechaEAnio = ParamUtil.getString(actionRequest, "fechaEAnio");
		Date fechaE = null;
		try {
			fechaE = formatoDeFecha.parse(fechaEDia + "/"
					+ (Integer.parseInt(fechaEMes) + 1) + "/" + fechaEAnio);
		} catch (Exception e) {
			fechaE = null;
		}
		liquidacion.setFecha_emitido(fechaE);
		String fechaRDia = ParamUtil.getString(actionRequest, "fechaRDia");
		String fechaRMes = ParamUtil.getString(actionRequest, "fechaRMes");
		String fechaRAnio = ParamUtil.getString(actionRequest, "fechaRAnio");
		Date fechaR = null;
		try {
			fechaR = formatoDeFecha.parse(fechaRDia + "/"
					+ (Integer.parseInt(fechaRMes) + 1) + "/" + fechaRAnio);
		} catch (Exception e) {
			fechaR = null;
		}
		liquidacion.setFecha_recibido(fechaR);
		String fechaVDia = ParamUtil.getString(actionRequest, "fechaVDia");
		String fechaVMes = ParamUtil.getString(actionRequest, "fechaVMes");
		String fechaVAnio = ParamUtil.getString(actionRequest, "fechaVAnio");
		Date fechaV = null;
		try {
			fechaV = formatoDeFecha.parse(fechaVDia + "/"
					+ (Integer.parseInt(fechaVMes) + 1) + "/" + fechaVAnio);
		} catch (Exception e) {
			fechaV = null;
		}
		liquidacion.setFecha_vencimiento(fechaV);
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(actionRequest,
				"periodoMesAnio");
		Date periodo = null;
		try {
			periodo = formatoDePeriodos.parse(Integer.parseInt(periodoMesAnio
					.substring(0, 1))
					+ 1 + "/" + periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		liquidacion.setPeriodo(periodo);
		int id_prestador = ParamUtil.getInteger(actionRequest, "id_prestador",
				0);
		// String cuit_prestador = ParamUtil.getString(actionRequest,
		// "cuit_prestador", "");
		liquidacion.setId_prestador(id_prestador);
		int id_domicilio = ParamUtil.getInteger(actionRequest, "id_domicilio",
				0);
		liquidacion.setId_domicilio(id_domicilio);
		String tipo_compro = ParamUtil.getString(actionRequest,
				"comprobante_tipo", null);
		liquidacion.setCompro_a_debitar_tipo(tipo_compro);
		String letra_compro = ParamUtil.getString(actionRequest,
				"comprobante_letra", null);
		liquidacion.setCompro_a_debitar_letra(letra_compro);
		int sucu = ParamUtil.getInteger(actionRequest, "sucu", 0);
		liquidacion.setSucu(sucu);
		String nro_compro = ParamUtil.getString(actionRequest,
				"comprobante_nro", null);
		liquidacion.setCompro_a_debitar_numero(nro_compro);
		String observaciones = ParamUtil.getString(actionRequest,
				"observaciones", "");
		liquidacion.setObservaciones(observaciones);
		String importe_total = ParamUtil.getString(actionRequest,
				"importe_total", "0");
		liquidacion.setImporte_total(new BigDecimal(importe_total));
		String debitado = ParamUtil.getString(actionRequest, "debitado", "0");
		liquidacion.setDebitado(new BigDecimal(debitado));
		String tercerizado_cab = ParamUtil.getString(actionRequest,
				"tercerizado_cab", "0");
		liquidacion.setTercerizado(tercerizado_cab);
		int numero = ParamUtil.getInteger(actionRequest, "numero", 0);
		liquidacion.setId_liquidacion(numero);
		String entidad_liquidacion = ParamUtil.getString(actionRequest,
				"entidad_liquidacion", null);
		liquidacion.setEntidad(entidad_liquidacion);
		return liquidacion;
	}

	private LiquidacionPrestacion getLiquidacionPrestacionFromRequest(
			ActionRequest actionRequest) {

		LiquidacionPrestacion liquidacionPrestacion = new LiquidacionPrestacion();
		// String entidad = ParamUtil.getString(actionRequest, "entidad", null);
		String cuil_titular = ParamUtil.getString(actionRequest, "cuil", null);
		liquidacionPrestacion.setCuil_titular(cuil_titular);
		int inte = ParamUtil.getInteger(actionRequest, "inte", 0);
		liquidacionPrestacion.setInte(inte);
		// int seccional = ParamUtil.getInteger(actionRequest, "id_seccional",
		// 0);
		String prestacionFechaDia = ParamUtil.getString(actionRequest,
				"prestacionFechaDia");
		String prestacionFechaMes = ParamUtil.getString(actionRequest,
				"prestacionFechaMes");
		String prestacionFechaAnio = ParamUtil.getString(actionRequest,
				"prestacionFechaAnio");
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date prestacionFecha;
		try {
			prestacionFecha = formatoDeFecha.parse(prestacionFechaDia + "/"
					+ (Integer.parseInt(prestacionFechaMes) + 1) + "/"
					+ prestacionFechaAnio);
		} catch (Exception e) {
			prestacionFecha = null;
		}
		liquidacionPrestacion.setFecha_prestacion(prestacionFecha);
		int id_prestacion = ParamUtil.getInteger(actionRequest,
				"id_prestacion", 0);
		liquidacionPrestacion.setId_prestacion(id_prestacion);
		String cantidad = ParamUtil.getString(actionRequest, "cantidad", "0");
		try {
			liquidacionPrestacion.setCantidad(new BigDecimal(cantidad));
		} catch (NumberFormatException nfe) {
		}
		String importe = ParamUtil.getString(actionRequest, "importe", "0");
		try {
			liquidacionPrestacion.setImporte(new BigDecimal(importe));
		} catch (NumberFormatException nfe) {
		}
		String solicitado = ParamUtil.getString(actionRequest, "solicitado",
				"0");
		try {
			liquidacionPrestacion.setSolicitado(new BigDecimal(solicitado));
		} catch (NumberFormatException nfe) {
		}
		String resultado = ParamUtil.getString(actionRequest, "resultado", "0");
		try {
			liquidacionPrestacion.setResultado(new BigDecimal(resultado));
		} catch (NumberFormatException nfe) {
		}
		String servicio = ParamUtil.getString(actionRequest, "servicio", "0");
		liquidacionPrestacion.setServicio(servicio);
		String tercerizado = ParamUtil.getString(actionRequest,
				"descontar_capitas", null);
		liquidacionPrestacion.setTercerizado(tercerizado);
		int orden = ParamUtil.getInteger(actionRequest, "orden", 0);
		
		String periodoPrestacionMesAnio = ParamUtil.getString(actionRequest,
		"periodoPrestacionMesAnio");
		Date periodoPrestacion = null;
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		
		try {
			periodoPrestacion = formatoDePeriodos.parse(Integer
					.parseInt(periodoPrestacionMesAnio.substring(0, 1))
					+ 1 + "/" + periodoPrestacionMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoPrestacion = null;
		}
		if (periodoPrestacion == null) {
			try {
				periodoPrestacion = formatoDePeriodos.parse(Integer
						.parseInt(periodoPrestacionMesAnio.substring(0, 2))
						+ 1 + "/" + periodoPrestacionMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodoPrestacion = null;
			}
		}
		
		liquidacionPrestacion.setPeriodo(periodoPrestacion);

		return liquidacionPrestacion;
	}

}