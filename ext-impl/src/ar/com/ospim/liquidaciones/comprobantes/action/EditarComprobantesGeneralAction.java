package ar.com.ospim.liquidaciones.comprobantes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.reportes.ReporteHistoricoMovimientosAfiliadoExcel;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.AnticipoComprobantePluralException;
import ar.com.ospim.liquidaciones.ComprobanteConceptoIVAInvalidoException;
import ar.com.ospim.liquidaciones.ComprobanteConceptoInvalidoException;
import ar.com.ospim.liquidaciones.ComprobanteImoprteConceptoInvalidoException;
import ar.com.ospim.liquidaciones.ComprobanteSinConceptosException;
import ar.com.ospim.liquidaciones.ComprobanteSinImporteException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.comprobantes.ConceptoConsolidarException;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarComprobantesGeneralAction extends PortletAction {
	public static final String VIEW = "VIEW";

	@SuppressWarnings("unchecked")
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;
		if (actionResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (actionResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(req)
				.getSession();

		Comprobante comp = getComprobanteFromRequest(req);
		User user = PortalUtil.getUser(req);
		String nuevo = ParamUtil.getString(req,
				WebKeysLiquidaciones.COMPROBANTE_NUEVO);
		boolean nuevoBoolean = StringUtils.checkNotEmpty(nuevo)
				&& nuevo.equals("true");
		try {

			Date fecha_cierre_periodo = ContabilidadServiceUtil
					.getFechaUltimoPeriodoContable(entidad);
			if (comp.getFechaRecepcion().compareTo(fecha_cierre_periodo) <= 0) {
				throw new FechaMenorACierreContableException();
			}

			List<ComprobanteConcepto> conceptos = (List<ComprobanteConcepto>) session
					.getAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);
			comp.setConceptos(conceptos);

			validarComprobante(comp, req, entidad);

			if (nuevoBoolean) {
				comp.setPtoVenta(comp.getPtoVenta());
				if(entidad==WebKeysGlobal.UOMA) {
				  ComprobanteServiceUtil.saveExtendido(comp, user, entidad);
				} else {
				  ComprobanteServiceUtil.save(comp, user, entidad);	
				}
			} else {
				if(entidad==WebKeysGlobal.UOMA) {
					  ComprobanteServiceUtil.updateExtendido(comp, user, entidad);
				} else {
					  ComprobanteServiceUtil.update(comp, user, entidad);	
				}
			}
			req.setAttribute("pto_venta", Integer.valueOf(comp.getPtoVenta()));
			req.setAttribute("tipo_comprobante", comp.getTipoComprobante());
			req.setAttribute("nro_comprobante", comp.getNroComprobante());
			req.setAttribute("letra", comp.getLetraComprobante());
			req.setAttribute("sucursal", comp.getSucuComprobante());
			req.setAttribute("cuit_compr_emisor", comp.getCuit());

		} catch (ComprobanteExistenteException e) {
			req.setAttribute(WebKeysLiquidaciones.COMPROBANTE_NUEVO, true);		
			//LO PONGO NUEVAMENTE EN LA SESSION?
			req.setAttribute(WebKeysLiquidaciones.COMPROBANTE_EN_EDICION, comp);
			req.setAttribute("pto_venta", Integer.valueOf(comp.getPtoVenta()));
			req.setAttribute("tipo_comprobante", comp.getTipoComprobante());
			req.setAttribute("nro_comprobante", comp.getNroComprobante());
			req.setAttribute("letra", comp.getLetraComprobante());
			req.setAttribute("sucursal", comp.getSucuComprobante());
			req.setAttribute("cuit_compr_emisor", comp.getCuit());
			SessionErrors.add(req, e.getClass().getName());
		} catch (Exception e) {
			if (nuevoBoolean) {
				req.setAttribute(WebKeysLiquidaciones.COMPROBANTE_NUEVO, true);
			}
			SessionErrors.add(req, e.getClass().getName());
			//LO PONGO NUEVAMENTE EN LA SESSION?
			req.setAttribute(WebKeysLiquidaciones.COMPROBANTE_EN_EDICION, comp);
			req.setAttribute("pto_venta", Integer.valueOf(comp.getPtoVenta()));
			req.setAttribute("tipo_comprobante", comp.getTipoComprobante());
			req.setAttribute("nro_comprobante", comp.getNroComprobante());
			req.setAttribute("letra", comp.getLetraComprobante());
			req.setAttribute("sucursal", comp.getSucuComprobante());
			req.setAttribute("cuit_compr_emisor", comp.getCuit());
		}

		if (SessionErrors.isEmpty(req)) {
			
			if(comp.getNroComprobante().contains("/") && comp.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA) && 
					entidad == WebKeysGlobal.OSPIM) {
				enviarNotificacionComprobanteDuplicado(comp.toString(), user.getScreenName());
			}
			
			String successMessage = ParamUtil.getString(req, "successMessage");
			SessionMessages.add(req, "request_processed", successMessage);
		}

	}

	private void validarComprobante(Comprobante comp, ActionRequest req,
			int entidad) throws ComprobanteImoprteConceptoInvalidoException,
			ComprobanteSinConceptosException, ComprobanteSinImporteException,
			ComprobanteConceptoInvalidoException, ConceptoConsolidarException, AnticipoComprobantePluralException,
			ComprobanteConceptoIVAInvalidoException {
		
		if (comp.getImporteComprobante() == null) {
			throw new ComprobanteSinImporteException();
		}
		if (comp.getConceptos() == null
				|| obtenerCantidadConceptos(comp.getConceptos()) == 0) {
			throw new ComprobanteSinConceptosException();
		}
		if (comp.getImporteComprobante().compareTo(
				obtenerImporte(comp.getConceptos())) != 0) {
				throw new ComprobanteImoprteConceptoInvalidoException();
		}

		List<Concepto> conceptos = null;

		conceptos = TraeListasServiceUtil.getConceptoEgresos(req, new Date(), entidad);

		if (comp.getTipoComprobante().equals("ANT")
				&& entidad == WebKeysGlobal.OSPIM) {
			for (ComprobanteConcepto cc : comp.getConceptos()) {
				int index = conceptos.indexOf(cc.getConceptoComprobante());
				if (!conceptos.get(index).getNumero().startsWith("1.1.3.6")) {
					throw new ComprobanteConceptoInvalidoException();
				}
			}
		}
		if (comp.getTipoComprobante().equals("ANT")
				&& entidad == WebKeysGlobal.AMTIMA) {
			for (ComprobanteConcepto cc : comp.getConceptos()) {
				int index = conceptos.indexOf(cc.getConceptoComprobante());
				if (!conceptos.get(index).getNumero().startsWith("1.1.4.0202")
						&& !conceptos.get(index).getNumero().startsWith("1.1.3.0204")	
						&& !conceptos.get(index).getNumero().startsWith("1.1.3.0205")
				   ) {
					throw new ComprobanteConceptoInvalidoException();
				}
			}
		}
		
		if (comp.getTipoComprobante().equals("ANT")
				&& entidad == WebKeysGlobal.UOMA) {
			if(comp.getConceptos().size()>1){
				throw new AnticipoComprobantePluralException();
			}
			for (ComprobanteConcepto cc : comp.getConceptos()) {
				int index = conceptos.indexOf(cc.getConceptoComprobante());
				if (!conceptos.get(index).getNumero().startsWith("1.1.2.0")&&!conceptos.get(index).getNumero().startsWith("5.1.2.0")) {
					throw new ComprobanteConceptoInvalidoException();
				}
			}
		}

		boolean utilizaConveniosGlobalesNoLiquidacion = false;
		for (ComprobanteConcepto cc : comp.getConceptos()) {
			if (cc.getConceptoComprobante().getId() == ConceptoServiceUtil
					.getIdConveniosGlobalesNoLiquidacion(comp
							.getFechaRecepcion())) {
				utilizaConveniosGlobalesNoLiquidacion = true;
			}
		}

		if (utilizaConveniosGlobalesNoLiquidacion
				&& !comp.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.CONSOLIDAR_CUIT) 
				&& !comp.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.OMINT_CUIT) 
				&& !comp.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.PREVENCION_CUIT)
				&& !comp.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.ENSALUD_CUIT)
				
			) {
			
			throw new ConceptoConsolidarException();
		}
		
		if (!comp.getLetraComprobante().equals("A") &&
				!comp.getLetraComprobante().equals("M")	
				&& entidad == WebKeysGlobal.UOMA) {
			
			for (ComprobanteConcepto cc : comp.getConceptos()) {
				if ( cc.getIva().compareTo(BigDecimal.ZERO)>0) {
					throw new ComprobanteConceptoIVAInvalidoException("El comprobante no puede tener IVA discriminado");
				}
			}
		}

	}

	private int obtenerCantidadConceptos(List<ComprobanteConcepto> conceptos) {
		int i = 0;
		if (conceptos != null) {
			for (ComprobanteConcepto cc : conceptos) {
				if (!cc.isBorradoLogicamente()) {
					i++;
				}
			}
		}
		return i;
	}

	private BigDecimal obtenerImporte(List<ComprobanteConcepto> conceptos) {
		BigDecimal total = BigDecimal.ZERO;
		if (conceptos != null) {
			for (ComprobanteConcepto cc : conceptos) {
				if (!cc.isBorradoLogicamente()) {
					total = total.add(cc.getImporte());
				}
			}
		}
		return total;
	}

	public static Comprobante getComprobanteFromRequest(
			PortletRequest renderRequest) {

		int pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
		String tipoC = ParamUtil.getString(renderRequest, "tipo_comprobante");
		String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
		String letra = ParamUtil.getString(renderRequest, "letra", " ");
//		int cantCuotas = ParamUtil.getInteger(renderRequest, "cant_cuotas", 1);

		String fechaEmisionComprobanteMes = ParamUtil.getString(renderRequest,
				"fechaEmisionComprobanteMes");
		String fechaEmisionComprobanteDia = ParamUtil.getString(renderRequest,
				"fechaEmisionComprobanteDia");
		String fechaEmisionComprobanteAnio = ParamUtil.getString(renderRequest,
				"fechaEmisionComprobanteAnio");
/*		
		String cuit = ParamUtil.getString(renderRequest, "cuit_compr_emisor");
		int sucu = ParamUtil.getInteger(renderRequest, "sucursal", pto_venta);
*/		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaEmisionC = null;
		try {
			fechaEmisionC = formatoDeFecha.parse(fechaEmisionComprobanteDia
					+ "/" + (Integer.parseInt(fechaEmisionComprobanteMes) + 1)
					+ "/" + fechaEmisionComprobanteAnio);
		} catch (Exception e) {
			fechaEmisionC = null;
		}

/*
		String fechaRecepcionComprobanteMes = ParamUtil.getString(
				renderRequest, "fechaRecepcionComprobanteMes");
		String fechaRecepcionComprobanteDia = ParamUtil.getString(
				renderRequest, "fechaRecepcionComprobanteDia");
		String fechaRecepcionComprobanteAnio = ParamUtil.getString(
				renderRequest, "fechaRecepcionComprobanteAnio");

		Date fechaRecepcionC = null;
		try {
			fechaRecepcionC = formatoDeFecha.parse(fechaRecepcionComprobanteDia
					+ "/"
					+ (Integer.parseInt(fechaRecepcionComprobanteMes) + 1)
					+ "/" + fechaRecepcionComprobanteAnio);
		} catch (Exception e) {
			fechaRecepcionC = null;
		}

		String fechaVencimientoComprobanteMes = ParamUtil.getString(
				renderRequest, "fechaVencimientoComprobanteMes");
		String fechaVencimientoComprobanteDia = ParamUtil.getString(
				renderRequest, "fechaVencimientoComprobanteDia");
		String fechaVencimientoComprobanteAnio = ParamUtil.getString(
				renderRequest, "fechaVencimientoComprobanteAnio");

		Date fechaVencimientoC = null;
		try {
			fechaVencimientoC = formatoDeFecha
					.parse(fechaVencimientoComprobanteDia
							+ "/"
							+ (Integer.parseInt(fechaVencimientoComprobanteMes) + 1)
							+ "/" + fechaVencimientoComprobanteAnio);
		} catch (Exception e) {
			fechaVencimientoC = null;
		}

		String periodoMesAnio = ParamUtil.getString(renderRequest,
				"periodoMesAnio");

		Date peri = null;
		try {
			peri = formatoDeFecha.parse("01/"
					+ (Integer.parseInt(periodoMesAnio.split("_")[0]) + 1)
					+ "/" + periodoMesAnio.split("_")[1]);
		} catch (Exception e) {
			peri = null;
		}

		Date altaFecha = null;
		Calendar cal = DateUtils.getCalendarGMTMenos3();
		
		String fechaHastaDia=ParamUtil.getString(renderRequest, "altaFechaDia");
		String fechaHastaMes=ParamUtil.getString(renderRequest, "altaFechaMes");
		String fechaHastaAnio=ParamUtil.getString(renderRequest, "altaFechaAnio");

		try {
			altaFecha = formatoDeFecha.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			altaFecha = cal.getTime();
		}
*/		
		String importeC = ParamUtil.getString(renderRequest,
				"importe_comprobante");

//		String obs = ParamUtil.getString(renderRequest, "obs");
		
		Comprobante comprobante = new Comprobante(pto_venta, tipoC, nroC, null,
				fechaEmisionC,null,
				StringUtils.checkNotEmpty(importeC) ? new BigDecimal(importeC)
						: null, letra, 0, null, null, null);

		String cuitAcreedor = renderRequest.getParameter("cuit_entidad");
		String sucuAcreedor = renderRequest.getParameter("sucursal_entidad");
		String idSeccional = renderRequest.getParameter("id_seccional");
		
		//ES UN AFILIADO
		if(!StringUtils.checkNotEmpty(cuitAcreedor)){
			cuitAcreedor=renderRequest.getParameter("cuil_compro");
			sucuAcreedor="000";
			String nombre=renderRequest.getParameter("nombre_compro");
			String apellido=renderRequest.getParameter("apellido_compro");
			Afiliado afiliado=new Afiliado(cuitAcreedor,0,nombre,apellido);
			comprobante.setAfiliado(afiliado);
		}
			
		

		if ((StringUtils.checkNotEmpty(idSeccional) && !idSeccional.equals("0"))   
				|| (null!=cuitAcreedor && (cuitAcreedor.equals(WebKeysGlobal.UOMA) || cuitAcreedor.equals(WebKeysGlobal.AMTIMA) || cuitAcreedor.equals(WebKeysGlobal.OSPIM)))) {
			comprobante.setSeccional(new Seccional(Integer
					.parseInt(idSeccional), null, cuitAcreedor));
			sucuAcreedor = "000";
		}

		Empresa empresa = null;
		if (StringUtils.checkNotEmpty(cuitAcreedor)) {
			empresa = new Empresa(cuitAcreedor, sucuAcreedor, null);
			empresa.setId_seccional(Integer.parseInt(idSeccional));
		}
		
		comprobante.setAcreedorEmpresa(empresa);
//		comprobante.setObservaciones(obs);
//		comprobante.setCantCuotas(cantCuotas);
//		comprobante.setNroAnticipo(0);
		
//		comprobante.setAlta_fecha(altaFecha);
		
		return comprobante;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		}
		Date recepcion = new Date();

		if (renderRequest.getParameter(VIEW) != null
				&& renderRequest.getParameter(VIEW).equals(VIEW)) {
			renderRequest.setAttribute(VIEW, VIEW);
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			HttpSession session = PortalUtil.getHttpServletRequest(
					renderRequest).getSession();
			session.removeAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);

			Comprobante comp = getComprobante(renderRequest);
			if (comp != null) {
				comp = ComprobanteServiceUtil.getComprobante(comp, entidad);
				if (comp != null && comp.getFechaRecepcion() != null) {
					recepcion = comp.getFechaRecepcion();
				}
				if (comp!= null && comp.isPagado()) {
					renderRequest.setAttribute(VIEW, VIEW);
				}
				if(null!=comp.getSeccional()&& comp.getSeccional().getId_seccional()>0){
					comp.getAcreedorEmpresa().setSucursal(String.valueOf(comp.getSeccional().getId_seccional()));
					
				}
				renderRequest.setAttribute(
						WebKeysLiquidaciones.COMPROBANTE_EN_EDICION, comp);
				if (comp != null && comp.getConceptos() != null) {
					session.setAttribute(
							WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS,
							comp.getConceptos());
				}
				
				String permitirEdicion = ParamUtil.getString(renderRequest, "permitirEdicion");
				if("editar".equalsIgnoreCase(permitirEdicion)) {
				   renderRequest.setAttribute("VIEW",permitirEdicion);
				}   
			} else {
				renderRequest.setAttribute(
						WebKeysLiquidaciones.COMPROBANTE_NUEVO, true);
			}
		}

		Comprobante comp = obtenerComprobanteDeRedireccion(renderRequest);
		if (comp != null) {
			renderRequest.setAttribute(
					WebKeysLiquidaciones.COMPROBANTE_EN_EDICION, comp);
		}

		
		TraeListasServiceUtil.getConceptoEgresos(renderRequest, recepcion, entidad);
		

		if (entidad == WebKeysGlobal.UOMA) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.editar_comprobante_extendido");
		}else if (entidad == WebKeysGlobal.AMTIMA) {
			return mapping
					.findForward("portlet.farmacia.comprobantes.editar_comprobante");
		}else {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.editar_comprobante_global");
		}
	}

	private Comprobante obtenerComprobanteDeRedireccion(
			RenderRequest renderRequest) {
		Comprobante comp = null;

		String cuit = ParamUtil.getString(renderRequest, "cuit_compr_emisor");

		String redirigirNuevoComprobante = ParamUtil.getString(renderRequest,
				"redirigirNuevoComprobante");
		if (redirigirNuevoComprobante != null
				&& redirigirNuevoComprobante
						.equals("redirigirNuevoComprobante")) {
			comp = new Comprobante();
			String cuitAcreedor = renderRequest.getParameter("cuit_entidad");
			String sucuAcreedor = renderRequest
					.getParameter("sucursal_entidad");
			String idSeccional = renderRequest.getParameter("id_seccional");
			String entidad = renderRequest.getParameter("entidad");
			Empresa empresa = null;
			if (StringUtils.checkNotEmpty(cuitAcreedor)) {
				empresa = new Empresa(cuitAcreedor, sucuAcreedor, entidad);
			}
			comp.setAcreedorEmpresa(empresa);
			if (StringUtils.checkNotEmpty(idSeccional)
					&& Integer.parseInt(idSeccional) != 0) {
				comp.setSeccional(new Seccional(Integer.parseInt(idSeccional),
						null, cuitAcreedor));
				sucuAcreedor = "000";
				comp.getAcreedorEmpresa().setSucursal(idSeccional);
			}
			
			comp.setCuit(cuit);
		}
		return comp;
	}

	private Comprobante getComprobante(RenderRequest renderRequest)
			throws SystemException {
		String redirigirNuevoComprobante = ParamUtil.getString(renderRequest,
				"redirigirNuevoComprobante");
		if (redirigirNuevoComprobante != null
				&& redirigirNuevoComprobante
						.equals("redirigirNuevoComprobante")) {
			return null;
		}
		String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
		if (StringUtils.checkNotEmpty(nroC)) {
			int pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
			String tipoC = ParamUtil.getString(renderRequest,
					"tipo_comprobante");
			String letra = renderRequest.getParameter("letra");
			if (letra == null) {
				letra = "";
			}
			String cuit = ParamUtil.getString(renderRequest,
					"cuit_compr_emisor");
			String sucu = ParamUtil.getString(renderRequest, "sucursal");
			return new Comprobante(pto_venta, tipoC, nroC, letra,
					Integer.parseInt(sucu), cuit);
		} else if (StringUtils.checkNotEmpty(renderRequest
				.getAttribute("nro_comprobante"))) {
			int pto_venta = (Integer) renderRequest.getAttribute("pto_venta");
			String tipoC = (String) renderRequest
					.getAttribute("tipo_comprobante");
			String letra = (String) renderRequest.getAttribute("letra");
			String cuit = (String) renderRequest
					.getAttribute("cuit_compr_emisor");
			String sucu = (String) renderRequest.getAttribute("sucursal");
			return new Comprobante(pto_venta, tipoC,
					(String) renderRequest.getAttribute("nro_comprobante"),
					letra, Integer.parseInt(sucu), cuit);
		}
		return null;
	}
	
	private void enviarNotificacionComprobanteDuplicado(String comprobante, String usuario){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.COMPROBANTE_DUPLICADO);
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso comprobante duplicado (/)", "Comprobante: " + comprobante + " Usuario Carga: "+usuario, destinatarios, 1);
		
	}

}
