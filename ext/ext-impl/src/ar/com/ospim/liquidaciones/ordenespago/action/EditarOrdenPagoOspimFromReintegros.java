package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.ParseException;
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
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class EditarOrdenPagoOspimFromReintegros extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarOrdenPagoOspimFromReintegros.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		
	}

	private List<Comprobante> setearComprobante(String cuitAcreedor,
			OrdenPagoOspim op, BigDecimal total, Seccional seccional)
			throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Date current = new Date();
		String nro_comp = "";
		if (seccional.getIdSeccional() != 9999) {
			nro_comp = seccional.getDescripcion() + " ";
		}
		nro_comp += DateUtils.format(current, DateUtils.PERIODO) + " -";

		Comprobante comp = new Comprobante(0, "REI", nro_comp,
				WebKeysGlobal.CUIT_OSPIM, current, current, total, " ", 0, null);
		comp.setAcreedorEmpresa(new Empresa(cuitAcreedor, "000", null));
		comp.setSucuComprobante(0);
		if (seccional.getIdSeccional() != 9999) {
			comp.setSeccional(seccional);
		}

		// crea concepto asociado al Comprobante reitnegro
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		Concepto concepto = new Concepto(
				ConceptoServiceUtil.getIdReintegros(current));
		concepto.setDescripcion(WebKeysGlobal.DESCRIPCION_CONCEPTO_REINTEGROS);
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				concepto, comp.getImporteComprobante());
		conceptos.add(comprobanteConcepto);
		comp.setConceptos(conceptos);

		List<Comprobante> compsEnBase = ComprobanteServiceUtil
				.getComprobantesLikeNro(comp, true);
		int seq = 1;
		if (compsEnBase != null && !compsEnBase.isEmpty()) {
			for (Comprobante c : compsEnBase) {
				int indexOf = c.getNroComprobante().indexOf("-");
				if (indexOf != -1) {
					int indice = Integer.parseInt(c.getNroComprobante()
							.substring(indexOf + 1));
					if (indice > seq) {
						seq = indice;
					}
				}
			}
			seq++;
		}
		comp.setNroComprobante(comp.getNroComprobante() + seq);

		comprobantes.add(comp);
		op.setComprobantes(comprobantes);
		return comprobantes;
	}

	private List<Comprobante> setearComprobanteFarmacia(String cuitAcreedor,
			OrdenPagoOspim op, BigDecimal total, Seccional seccional)
			throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Date current = new Date();
		StringBuffer nro_comp = new StringBuffer("FAR-");
		if (seccional.getIdSeccional() != 9999) {
			nro_comp.append(seccional.getDescripcion() + " ");
		}
		nro_comp.append(DateUtils.format(current, DateUtils.PERIODO) + " -");

		Comprobante comp = new Comprobante(0, "REI", nro_comp.toString(),
				WebKeysGlobal.CUIT_OSPIM, current, current, total, " ", 0, null);
		comp.setAcreedorEmpresa(new Empresa(cuitAcreedor, "000", null));
		comp.setSucuComprobante(0);

		if (seccional.getIdSeccional() != 9999) {
			comp.setSeccional(seccional);
		}

		// crea concepto asociado al Comprobante reitnegro
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		Concepto concepto = new Concepto(
				ConceptoServiceUtil.getIdReintegrosFarmaciaOSPIM(current));
		concepto.setDescripcion(WebKeysGlobal.DESCRIPCION_CONCEPTO_REINTEGROS_FARMACIA); // ESTO
																							// BUSCA
																							// EL
																							// CONCEPTO
																							// DE
																							// REINTEGROS
																							// FARMACIA
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				concepto, comp.getImporteComprobante());
		conceptos.add(comprobanteConcepto);
		comp.setConceptos(conceptos);

		List<Comprobante> compsEnBase = ComprobanteServiceUtil
				.getComprobantesLikeNro(comp, true);
		int seq = 1;
		if (compsEnBase != null && !compsEnBase.isEmpty()) {
			for (Comprobante c : compsEnBase) {
				int indexOf = c.getNroComprobante().replaceAll("FAR-", "").indexOf("-");
				if (indexOf != -1) {
					int indice = Integer.parseInt(c.getNroComprobante().replaceAll("FAR-", "")
							.substring(indexOf + 1));
					if (indice > seq) {
						seq = indice;
					}
				}
			}
			seq++;
		}
		comp.setNroComprobante(comp.getNroComprobante() + seq);

		comprobantes.add(comp);
		op.setComprobantes(comprobantes);
		return comprobantes;
	}
	
	

	private List<ReintegroList> getReintegroAgrupadoSumadoList(RenderRequest renderRequest, String in)
			throws SystemException, ParseException {

		int idSeccional = Integer.parseInt(renderRequest
				.getParameter("id_seccional"));
		String fechaDesdeDia = renderRequest.getParameter("fechaDesdeDia1");
		String fechaDesdeMes = renderRequest.getParameter("fechaDesdeMes1");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = renderRequest.getParameter("fechaDesdeAnio1");
		String fechaHastaDia = renderRequest.getParameter("fechaHastaDia2");
		String fechaHastaMes = renderRequest.getParameter("fechaHastaMes2");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = renderRequest.getParameter("fechaHastaAnio2");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-"
				+ fechaDesdeAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		return OrdenPagoServiceUtil.getReintegroAgrupadoSumadoList(idSeccional, fechaIni,
				fechaFin, in);
	}

	

	private List<ReintegroList> getReintegroList(RenderRequest renderRequest)
			throws SystemException, ParseException {

		int idSeccional = Integer.parseInt(renderRequest
				.getParameter("id_seccional"));
		String fechaDesdeDia = renderRequest.getParameter("fechaDesdeDia1");
		String fechaDesdeMes = renderRequest.getParameter("fechaDesdeMes1");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = renderRequest.getParameter("fechaDesdeAnio1");
		String fechaHastaDia = renderRequest.getParameter("fechaHastaDia2");
		String fechaHastaMes = renderRequest.getParameter("fechaHastaMes2");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = renderRequest.getParameter("fechaHastaAnio2");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-"
				+ fechaDesdeAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		return OrdenPagoServiceUtil.getReintegrosLists(idSeccional, fechaIni,
				fechaFin);
	}

	private List<ReintegroList> getReintegroListFarmacia(
			RenderRequest renderRequest) throws SystemException, ParseException {

		int idSeccional = Integer.parseInt(renderRequest
				.getParameter("id_seccional"));
		String fechaDesdeDia = renderRequest.getParameter("fechaDesdeDia1");
		String fechaDesdeMes = renderRequest.getParameter("fechaDesdeMes1");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = renderRequest.getParameter("fechaDesdeAnio1");
		String fechaHastaDia = renderRequest.getParameter("fechaHastaDia2");
		String fechaHastaMes = renderRequest.getParameter("fechaHastaMes2");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = renderRequest.getParameter("fechaHastaAnio2");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes + "-"
				+ fechaDesdeAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		return OrdenPagoServiceUtil.getReintegrosFarmaciaLists(idSeccional,
				fechaIni, fechaFin);
	}
	
	
		
	
	
	
	


	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		try {
			String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
			
			String addFormaPago = ParamUtil.getString(renderRequest, Constants.ACTION);
			
			int lote_actual=OrdenPagoServiceUtil.getLoteOrdenPago();
			String tipo = ParamUtil.getString(renderRequest, "tipo_lista");
			HttpSession session = PortalUtil.getHttpServletRequest(renderRequest).getSession();

			session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LISTA_ORDEN_PAGO_EDICION);

			
			List<ReintegroList> reintegrosList = new ArrayList<ReintegroList>();
			List<ReintegroList> reintegrosListFarmacia = new ArrayList<ReintegroList>();
			
			
			List<ReintegroList> reintegrosAll = new ArrayList<ReintegroList>();
			List<ReintegroList> reintegrosListAll = new ArrayList<ReintegroList>();

			
			if (tipo == null || tipo.equals("PRESTACIONAL")
					|| tipo.equals("TODOS")) {
				reintegrosList = getReintegroList(renderRequest);
			}
			

			// SI ES FARMACIA...
			if (tipo.equals("FARMACIA") || tipo.equals("TODOS")) {
				reintegrosListFarmacia = getReintegroListFarmacia(renderRequest);
			}

			if ((reintegrosList == null || reintegrosList.isEmpty())
					&& (reintegrosListFarmacia == null || reintegrosListFarmacia.isEmpty())) {
				
				throw new ListasReintegrosNoEncontradasException();
				
			} else {
		
				renderRequest.setAttribute(WebKeysLiquidaciones.LISTAS_PRESTACIONES_REINTEGROS_RESULTADOS, reintegrosList);
				renderRequest.setAttribute(WebKeysLiquidaciones.LISTAS_FARMACIAS_REINTEGROS_RESULTADOS, reintegrosListFarmacia);
			
			}
			
			
					
			renderRequest.setAttribute("tabs1", "ordenes-pago-ospim");
			
			renderRequest.setAttribute(WebKeysLiquidaciones.FROM_REINTEGROS,WebKeysLiquidaciones.FROM_REINTEGROS);
					
			
			if(StringUtils.checkNotEmpty(cmd) && cmd.equalsIgnoreCase(Constants.SEARCH) ) {
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.liquidaciones.reintegros_lista_results_para_op"));
				
			}else if(StringUtils.checkNotEmpty(cmd) && cmd.equalsIgnoreCase(Constants.ADD)) {
				
				String paramChecked = "";
				ArrayList<ReintegroList> reintegrosExcluidos = new ArrayList<ReintegroList>();
				
				for (Iterator<ReintegroList> iterator = reintegrosList.iterator(); iterator.hasNext();) {
					ReintegroList rl = iterator.next();
					
//					asi esta armado el nombre del checkbox que esta en la pagina reintegros_lista_para_OP_search_result.jsp
					paramChecked = "reint_"+rl.getTipo()+"-"+rl.getNroLista();
					String valorCheckBox = ParamUtil.getString(renderRequest, paramChecked);
					
					_log.debug("reintegro lista: " + paramChecked + " " + valorCheckBox);
					
//					if(paramChecked != null && valorCheckBox !=null && valorCheckBox.equalsIgnoreCase("on")){
					if(paramChecked != null && valorCheckBox !=null && !valorCheckBox.equalsIgnoreCase("on")){
						reintegrosExcluidos.add(rl);
					}
				}
				reintegrosList.removeAll(reintegrosExcluidos);
				
				reintegrosExcluidos.clear();
				
				for (Iterator<ReintegroList> iterator = reintegrosListFarmacia.iterator(); iterator.hasNext();) {
					ReintegroList rl = iterator.next();
					
//					asi esta armado el nombre del checkbox que esta en la pagina reintegros_lista_para_OP_search_result.jsp
					paramChecked = "reint_"+rl.getTipo()+"-"+rl.getNroLista();
					String valorCheckBox = ParamUtil.getString(renderRequest, paramChecked);
					
					_log.debug("reintegro lista: " + paramChecked + " " + valorCheckBox);
					
//					if(paramChecked != null && valorCheckBox !=null && valorCheckBox.equalsIgnoreCase("on")){
					if(paramChecked != null && valorCheckBox !=null && !valorCheckBox.equalsIgnoreCase("on")){
						reintegrosExcluidos.add(rl);
					}
				}
				reintegrosListFarmacia.removeAll(reintegrosExcluidos);
			}
			
			
			if(StringUtils.checkNotEmpty(addFormaPago) 
					&& WebKeysLiquidaciones.ADD_FORMA_PAGO.equalsIgnoreCase(addFormaPago)) {
			
				
				String listRei = "";
	
				reintegrosAll.clear();				
				reintegrosAll.addAll(reintegrosListFarmacia);
				reintegrosAll.addAll(reintegrosList);

			
				for (ReintegroList reintegroAux : reintegrosAll) {
					List<Reintegro>  reintegros = reintegroAux.getReintegros();
					
					for (Reintegro reintegro : reintegros) {
						listRei =  listRei + reintegro.getId_reintegroString() + ",";
						
					}
				}
				if (StringUtils.checkNotEmpty(listRei)){					
					listRei =  listRei.substring(0, listRei.length() - 1);
					reintegrosAll = getReintegroAgrupadoSumadoList(renderRequest, listRei);
					
				}
				session.setAttribute(WebKeysLiquidaciones.LISTA_ORDEN_PAGO_EDICION, reintegrosAll);
				renderRequest.setAttribute("formaPagoAfiliado", "AFI");

				
			}
			
			renderRequest.setAttribute(
					WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,
					WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
			
				OrdenPagoOspim op = new OrdenPagoOspim();
				
				try {
					int proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();
					renderRequest.setAttribute("PROXIMOIDORDENPAGO",proximoIdOP);
					
				} catch (SystemException e) {
					_log.error(e);
				}
				
				session.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
				renderRequest.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
				renderRequest.setAttribute(
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION,
						WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
				session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);

				BigDecimal totalPrestacion = new BigDecimal("0");
				BigDecimal totalFarmacia = new BigDecimal("0");
				if (reintegrosList != null && !reintegrosList.isEmpty()) {
					for (ReintegroList reint : reintegrosList) {
						totalPrestacion = totalPrestacion.add(reint
								.importeTotal());
					}
				}
				// SI ES FARMACIA TAMBIEN
				if (reintegrosListFarmacia != null
						&& !reintegrosListFarmacia.isEmpty()) {
					
					for (ReintegroList reintf : reintegrosListFarmacia) {
						totalFarmacia = totalFarmacia.add(reintf.importeTotal());
					}
				//	if(reintegrosList==null){
				//		reintegrosList=new ArrayList<ReintegroList>(); 
				//	}
				//	reintegrosList.addAll(reintegrosListFarmacia);
				}

				List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales(renderRequest);
				List<Comprobante> comprobantesFarmacia = null;
				List<Comprobante> comprobantes = null;
				Seccional seccional = null;
				String cuitAcreedor = WebKeysGlobal.CUIT_OSPIM;
				StringBuffer observaciones = new StringBuffer("REINTEGROS ");
				if (reintegrosList != null && !reintegrosList.isEmpty()) {
					seccional = reintegrosList.get(0).getSeccional();
					seccional = seccionales.get(seccionales.indexOf(seccional));
					if (seccional.getIdSeccional() != 9999) {
						op.setSeccional(seccional);
					}
					if (seccional.getIdSeccional() == 9999) {
						cuitAcreedor = reintegrosList.get(0).getReintegros()
								.get(0).getAfiliado().getCuil_titular();
					}
					observaciones.append(" - PRESTAC. MEDICAS.");
					if (tipo == null || tipo.equals("PRESTACIONAL")
							|| tipo.equals("TODOS")) {
						comprobantes = setearComprobante(cuitAcreedor, op,
								totalPrestacion, seccional);
					}

				} 
			
			
				
				if (reintegrosListFarmacia != null
						&& !reintegrosListFarmacia.isEmpty()) {
					seccional = reintegrosListFarmacia.get(0).getSeccional();
					seccional = seccionales.get(seccionales.indexOf(seccional));
					if (seccional.getIdSeccional() != 9999) {
						op.setSeccional(seccional);
					}
					if (observaciones.length() == 0) {
						observaciones.append("- FARMACIA");
					} else {
						observaciones.append(" / FARMACIA  ");
					}
					comprobantesFarmacia = setearComprobanteFarmacia(
							cuitAcreedor, op, totalFarmacia, seccional);
					if(null==comprobantes){
						comprobantes=new ArrayList<Comprobante>();
					}
					comprobantes.addAll(comprobantesFarmacia);
				}
				
				if (seccional != null) {
					observaciones.append(seccional.getId() != 9999 ? seccional
							.getDescripcion() : "");
				}

				if (reintegrosList != null && !reintegrosList.isEmpty()) {
					observaciones.append("\n");
					observaciones.append("\n");
					observaciones.append("Lista de Reintegros Nro:\n");
					int xi=0;
					for (ReintegroList reint : reintegrosList) {
						xi++;
						observaciones.append(reint.getNroLista());
						if(xi<reintegrosList.size())
						    observaciones.append(" - ");
					}
				}
				
				if (reintegrosListFarmacia != null
						&& !reintegrosListFarmacia.isEmpty()) {
					observaciones.append("\n");
					observaciones.append("\n");
					observaciones.append("Lista de Reintegros Farmacia Nro:\n");
					int xi=0;
					for (ReintegroList reintf : reintegrosListFarmacia) {
						xi++;
						observaciones.append(reintf.getNroLista());
						if(xi<reintegrosListFarmacia.size())
						    observaciones.append(" - ");
					}
				}
				
				
				User user = PortalUtil.getUser(renderRequest);
				EmpresaServiceUtil.insertarAfiliadoComoEmpresaSiNoExiste(cuitAcreedor, user);

				reintegrosListAll.clear();
				reintegrosListAll.addAll(reintegrosList);
				reintegrosListAll.addAll(reintegrosListFarmacia);

				op.setReintegrosList(reintegrosListAll);

				op.setAcreedor(new Empresa(cuitAcreedor, "000", null));	

				op.setObservaciones(observaciones.toString());
				op.setIdLote(lote_actual);

				session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
						comprobantes);

				op.setImporte(totalPrestacion.add(totalFarmacia));
				//BUSCO DESTINO Y RAZON SOC CHEQUE
				String[] razonDestino = OrdenPagoServiceUtil.getUltimaRazonSocialChequeYDestinoOP(op.getCuit(),
						"000", seccional.getId(), WebKeysGlobal.OSPIM);
				op.setDestino(razonDestino[OrdenPagoServiceUtil.DESTINO_POS]);
				op.setAFavorDe(razonDestino[OrdenPagoServiceUtil.A_NOMBRE_DE_POS]);
				op.setCBU(razonDestino[OrdenPagoServiceUtil.CBU_POS]);
				op.getAcreedor().setRazon_soc(razonDestino[OrdenPagoServiceUtil.RAZON_SOC_POS]);
			
		} catch (ListasReintegrosNoEncontradasException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (Exception e) {
			_log.error("Error al crear op de reintegros", e);
			throw e;
		}

		if (!SessionErrors.isEmpty(renderRequest)) {
//			setForward(renderRequest, "portlet.liquidaciones.view");
			setForward(renderRequest, "portlet.liquidaciones.reintegros_lista_results_para_op");
			renderRequest.setAttribute("tabs1", "ordenes-pago-ospim");
			
//			renderRequest.setAttribute(WebKeysLiquidaciones.FROM_REINTEGROS,
//					WebKeysLiquidaciones.FROM_REINTEGROS);
//			return mapping.findForward(getForward(renderRequest,
//					"portlet.liquidaciones.editar_orden_pago_ospim_entry"));
		}
		renderRequest.setAttribute(
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
		
		renderRequest.setAttribute(WebKeysLiquidaciones.FROM_REINTEGROS,
				WebKeysLiquidaciones.FROM_REINTEGROS);
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_orden_pago_ospim_entry"));
	}

}