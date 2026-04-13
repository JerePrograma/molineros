package ar.com.ospim.comprobantesPortalProveedores.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteHospital;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.proveedoresLPA.ClienteProveedoresLPA;


public class ComprobantesProveedoresGerenciadorasAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	private List<String> errores = new ArrayList<String>();
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest,Constants.CMD);
		if(cmd==null || "".equals(cmd)) {
			cmd = ParamUtil.getString(renderRequest,"accion");
		}
		
		String entidad = "O";

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad ="A";
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = "U";
		}

		ComprobanteHospital comprobante =null;
		
		Integer idComprobante = 0;
		String msg = "";
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		
		if (!StringUtils.checkEmpty(cmd)) {
			idComprobante = ParamUtil.getInteger(renderRequest,"id_comprobante", 0);
			
			if(cmd.equals("buscar")){
				
		           filterComprobantes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_gerenciadoras"));		
	   			  	
			}
			
			
			if(cmd.equals("imagenes") ){
				           	
				 String desdeResult = ParamUtil.get(renderRequest, "desde_result", "");
				 String cuit = ParamUtil.get(renderRequest, "cuit", "");
				 String tipo = ParamUtil.get(renderRequest, "tipo", "");
				 String letra = ParamUtil.get(renderRequest, "letraComprobante", "");
				 String ptovta = ParamUtil.get(renderRequest, "ptovta", "");
				 String nro = ParamUtil.get(renderRequest, "nro", "");
				 session.setAttribute("esPopUp","N");
				 session.setAttribute("desde_result",desdeResult);
				 Comprobante c =new Comprobante();
				 c.setCuit(cuit);
				 c.setTipoComprobante(tipo);
				 c.setLetraComprobante(letra);
				 c.setPtoVenta(Integer.parseInt(ptovta));
				 c.setNroComprobante(nro);
				 
				 
				 //String idFacturaImg = c.getCuit()+"-"+c.getTipoComprobante()+"-"+
				 //		 c.getLetraComprobante()+String.format("%05d",c.getPtoVenta())+c.getNroComprobante();
				 //BORRAR
				 //idFacturaImg = c.getCuit()+"-"+c.getTipoComprobante()+"-"+
				 //		c.getSucuComprobante()+c.getLetraComprobante()+c.getNroComprobante();
				 //FIN - BORRAR
				 
				 
				 session.setAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW, c);
				 return mapping.findForward(getForward(renderRequest,
										"portlet.comprobantes.imagenes"));
			}
			
			if(cmd.equals("verificar")){
				
		           verificarComprobantes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_gerenciadoras"));		
			}
			
			if(cmd.equals("recuperarimgs")){
				
		           recuperarImagenes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_gerenciadoras"));		
	   			  	
			}
			
			if(cmd.equals(Constants.EDIT) ){
				 String desdeResult = ParamUtil.get(renderRequest, "desde_result", "");
				 String cuit = ParamUtil.get(renderRequest, "cuit", "");
				 String tipo = ParamUtil.get(renderRequest, "tipo", "");
				 String letra = ParamUtil.get(renderRequest, "letraComprobante", "");
				 String ptovta = ParamUtil.get(renderRequest, "ptovta", "");
				 String nro = ParamUtil.get(renderRequest, "nro", "");
				 session.setAttribute("esPopUp","N");
				 session.setAttribute("desde_result",desdeResult);
				 ComprobanteFiltro c =new ComprobanteFiltro();
				 Empresa e =new Empresa();
				 e.setCuit(cuit);
				 c.setAcreedorEmpresa(e);
				 c.setCuit(cuit);
				 c.setTipoComprobante(tipo);
				 c.setLetraComprobante(letra);
				 c.setPtoVenta(Integer.parseInt(ptovta));
				 c.setNroComprobante(nro);
				 List<ComprobanteHospital> lista = ComprobanteServiceUtil.getListaGerenciadoras(c,0);
				 
				 session.setAttribute(WebKeysComprobantes.COMPROBANTE_GERENCIADORA_EN_EDICION, lista.get(0));
				 return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobante_gerenciadoras_editar"));
			}
           
			if(cmd.equals(Constants.UPDATE) ){
				
				comprobante=(ComprobanteHospital)  session.getAttribute(WebKeysComprobantes.COMPROBANTE_GERENCIADORA_EN_EDICION);
				actualizaComprobante(comprobante,PortalUtil.getHttpServletRequest(renderRequest));
				updateComprobante(comprobante, user.getScreenName());
				
				msg = LanguageUtil.get(defaultLocale, "Actualizó comprobante");
				msg = msg + " "+ comprobante.getAcreedorEmpresa().getCuit() + " " +
				comprobante.getTipoComprobante()+ " "+ comprobante.getLetraComprobante() + " "+
						String.valueOf(comprobante.getPtoVenta()) + " " + comprobante.getNroComprobante();
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id comprobante: " + comprobante.getTipoComprobante()+ " "+ comprobante.getLetraComprobante() + " "+
						String.valueOf(comprobante.getPtoVenta())+ " " + comprobante.getNroComprobante()
				);
				 
				ComprobanteFiltro c =new ComprobanteFiltro();
				c.setCuit(comprobante.getAcreedorEmpresa().getCuit());
				c.setTipoComprobante(comprobante.getTipoComprobante());
				c.setLetraComprobante(comprobante.getLetraComprobante());
				c.setPtoVenta(comprobante.getPtoVenta());
				c.setNroComprobante(comprobante.getNroComprobante());
				List<ComprobanteHospital> lista = ComprobanteServiceUtil.getListaGerenciadoras(c,0);
				session.setAttribute(WebKeysComprobantes.COMPROBANTE_GERENCIADORA_EN_EDICION, lista.get(0));
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.comprobantes.comprobante_gerenciadoras_editar"));
				
			}
			
			if(cmd.equals("generarLiquidaciones")){
				
		           generarLiquidaciones(renderRequest,session,entidad,user);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_gerenciadoras"));		
	   			  	
			}
			
		}
		renderRequest.setAttribute("tab", tabSel);
		return mapping.findForward("portlet.comprobantes.comprobantes_proveedores_gerenciadoras");
		
	}
	
	
	
	
private void filterComprobantes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	
	
	Integer pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
	String tipoC = ParamUtil.getString(renderRequest, "tipo_comprobante");
	String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
	String letra = ParamUtil.getString(renderRequest, "letra", " ");
	Boolean pendientes= ParamUtil.getBoolean(renderRequest, "pendientes");
	
	String fechaEmisionComprobanteMesDde = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteMesDde");
	String fechaEmisionComprobanteDiaDde = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteDiaDde");
	String fechaEmisionComprobanteAnioDde = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteAnioDde");
	SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	Date fechaEmisionCDde = null;
	try {
		fechaEmisionCDde = formatoDeFecha.parse(fechaEmisionComprobanteDiaDde
				+ "/" + (Integer.parseInt(fechaEmisionComprobanteMesDde) + 1)
				+ "/" + fechaEmisionComprobanteAnioDde);
	} catch (Exception e) {
		fechaEmisionCDde = null;
	}

	String fechaEmisionComprobanteMesHta = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteMesHta");
	String fechaEmisionComprobanteDiaHta = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteDiaHta");
	String fechaEmisionComprobanteAnioHta = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteAnioHta");
	Date fechaEmisionCHta = null;
	try {
		fechaEmisionCHta = formatoDeFecha.parse(fechaEmisionComprobanteDiaHta
				+ "/" + (Integer.parseInt(fechaEmisionComprobanteMesHta) + 1)
				+ "/" + fechaEmisionComprobanteAnioHta);
	} catch (Exception e) {
		fechaEmisionCHta = null;
	}
	
	
	String fechaRecepcionComprobanteMesDde = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteMesDde");
	String fechaRecepcionComprobanteDiaDde = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteDiaDde");
	String fechaRecepcionComprobanteAnioDde = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteAnioDde");

	Date fechaRecepcionCDde = null;
	try {
		fechaRecepcionCDde = formatoDeFecha.parse(fechaRecepcionComprobanteDiaDde
				+ "/"
				+ (Integer.parseInt(fechaRecepcionComprobanteMesDde) + 1)
				+ "/" + fechaRecepcionComprobanteAnioDde);
	} catch (Exception e) {
		fechaRecepcionCDde = null;
	}

	
	String fechaRecepcionComprobanteMesHta = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteMesHta");
	String fechaRecepcionComprobanteDiaHta = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteDiaHta");
	String fechaRecepcionComprobanteAnioHta = ParamUtil.getString(
			renderRequest, "fechaRecepcionComprobanteAnioHta");

	Date fechaRecepcionCHta = null;
	try {
		fechaRecepcionCHta = formatoDeFecha.parse(fechaRecepcionComprobanteDiaHta
				+ "/"
				+ (Integer.parseInt(fechaRecepcionComprobanteMesHta) + 1)
				+ "/" + fechaRecepcionComprobanteAnioHta);
	} catch (Exception e) {
		fechaRecepcionCHta = null;
	}

	
	String fechaVencimientoComprobanteMesDde = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteMesDde");
	String fechaVencimientoComprobanteDiaDde = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteDiaDde");
	String fechaVencimientoComprobanteAnioDde = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteAnioDde");

	Date fechaVencimientoCDde = null;
	try {
		fechaVencimientoCDde = formatoDeFecha.parse(fechaVencimientoComprobanteDiaDde
				+ "/"
				+ (Integer.parseInt(fechaVencimientoComprobanteMesDde) + 1)
				+ "/" + fechaVencimientoComprobanteAnioDde);
	} catch (Exception e) {
		fechaVencimientoCDde = null;
	}
	
	
	String fechaVencimientoComprobanteMesHta = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteMesHta");
	String fechaVencimientoComprobanteDiaHta = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteDiaHta");
	String fechaVencimientoComprobanteAnioHta = ParamUtil.getString(
			renderRequest, "fechaVencimientoComprobanteAnioHta");

	Date fechaVencimientoCHta = null;
	try {
		fechaVencimientoCHta = formatoDeFecha.parse(fechaVencimientoComprobanteDiaHta
				+ "/"
				+ (Integer.parseInt(fechaVencimientoComprobanteMesHta) + 1)
				+ "/" + fechaVencimientoComprobanteAnioHta);
	} catch (Exception e) {
		fechaVencimientoCHta = null;
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

	String periodoMesAnioHasta = ParamUtil.getString(renderRequest,
			"periodoMesAnioHasta");

	Date periHta = null;
	try {
		periHta = formatoDeFecha.parse("01/"
				+ (Integer.parseInt(periodoMesAnioHasta.split("_")[0]) + 1)
				+ "/" + periodoMesAnioHasta.split("_")[1]);
	} catch (Exception e) {
		periHta = null;
	}

	Integer liquidacion=null;
	try {
	  liquidacion=ParamUtil.getInteger(renderRequest, "liquidacion");	
    } catch (Exception e) {}
	
	
	String estado=ParamUtil.getString(renderRequest,"estado",null);
	String sector=ParamUtil.getString(renderRequest,"sector",null);
	String usr=ParamUtil.getString(renderRequest,"user",null);
	
	
	String cuitAcreedor = renderRequest.getParameter("cuit_compr_emisor");
	String razon_soc = renderRequest.getParameter("razon_soc");
	String dni = renderRequest.getParameter("dni");
	
	String prestacion = renderRequest.getParameter("prestacion");
	String medicamento = renderRequest.getParameter("medicamento");
	
	String prestaciondesc = renderRequest.getParameter("prestaciondesc");
	String medicamentodesc = renderRequest.getParameter("medicamentodesc");
	
	ComprobanteFiltro filtro = new ComprobanteFiltro();
	
	filtro.setTipoComprobante(tipoC);
	filtro.setNroComprobante(nroC);
	filtro.setLetraComprobante(letra);
	filtro.setPtoVenta(pto_venta);
	Empresa emp = new Empresa(cuitAcreedor, null,razon_soc);
	filtro.setAcreedorEmpresa(emp);
	
	filtro.setEntidad(entidad);
	
	Afiliado afi =new Afiliado();
	afi.setDocu_numero(dni);
	filtro.setAfiliado(afi);
	filtro.setAlta_usr(usr);
	filtro.setPeriodoPrestacion(peri);
	filtro.setCodigoPrestacion(prestacion);
	filtro.setDescripcionPrestacion(prestaciondesc);
	filtro.setMedicamentoCodigo(medicamento);
	filtro.setMedicamentoDescripcion(medicamentodesc);
	
	filtro.setEstado(estado);
	filtro.setSectorDestino(sector);
	
	filtro.setFechaEmisionDesde(fechaEmisionCDde);
	filtro.setFechaEmisionHasta(fechaEmisionCHta);
	filtro.setFechaRecepcionDesde(fechaRecepcionCDde);
	filtro.setFechaRecepcionHasta(fechaRecepcionCHta);
	filtro.setFechaVencimientoDesde(fechaVencimientoCDde);
	filtro.setFechaVencimientoHasta(fechaVencimientoCHta);
	filtro.setPeriodoPrestacion(peri);
	filtro.setPeriodoHasta(periHta);
	filtro.setLiquidacionId(new BigDecimal(liquidacion) );
	filtro.setPendientes(pendientes);
		
	List<ComprobanteHospital> lista = ComprobanteServiceUtil.getListaGerenciadoras(filtro,0);
	
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_FILTRO_GERENCIADORAS ,filtro);
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS,lista);
}

private void verificarComprobantes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	String ids = ParamUtil.getString(renderRequest, "ids");
    List<ComprobanteHospital> lista = (List<ComprobanteHospital>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS);
    List<ComprobanteHospital> listaAux=new ArrayList<ComprobanteHospital>();
    if(lista!=null && !lista.isEmpty()) {
      for(Integer i=0;i<lista.size();i++) {
    	if (ids.contains(lista.get(i).getId().toString().trim())) {
    		ComprobanteFiltro filtro= new ComprobanteFiltro();
    		filtro.setAcreedorEmpresa(lista.get(i).getAcreedorEmpresa());
    		filtro.setTipoComprobante(lista.get(i).getTipoComprobante());
    		filtro.setLetraComprobante(lista.get(i).getLetraComprobante());
    	    filtro.setPtoVenta(lista.get(i).getPtoVenta());
    	    filtro.setNroComprobante(lista.get(i).getNroComprobante());
    	    listaAux=ComprobanteServiceUtil.getListaGerenciadoras(filtro,0);
    	    if(listaAux!=null && !listaAux.isEmpty()) lista.set(i, listaAux.get(0));
    		verificaComprobante(lista.get(i));
    	}else {
    		lista.get(i).setOrden(999999);
    	}
      }
      
      Collections.sort(lista, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable<Integer>) ((ComprobanteHospital) (o1)).getOrden())
						.compareTo(((ComprobanteHospital) (o2)).getOrden());
			}
		});
      
    }
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS,lista);
}



///////////////////////////////
///////////////////////////////
//////////////////////////////

private String verificaComprobante(ComprobanteHospital c) throws Exception {
	String cError="";
	if(c.getCae()==null || c.getCae()=="") {
		if(cError.length()>0) cError+=";"; 
		cError+="CAE";
	}
	
	/*
	if(c.getAfiliado().getCuil_titular()==null) {
		if(cError.length()>0) cError+=";";
		cError+="AFI";
	}else { 

	}
	*/
	if(c.getIdPrestador()==null || c.getIdPrestador()==0){
		 if(cError.length()>0) cError+=";";
			cError+="PST"; 
	}
	
	/*
	if(c.getCodigoPrestacion()==null) {
		if(cError.length()>0) cError+=";";
		cError+="NOM";
	}else {
		
		Nomenclador n= ComprobanteServiceUtil.buscaNomencladorSSSByCodigo(c.getCodigoPrestacion());
		if(n==null || n.getIdPrestacionOSPIM()==0) {
			if(cError.length()>0) cError+=";";
			cError+="III";
		}
		
	}
	*/
	if(c.getImporteComprobante()==null || c.getImporteComprobante().doubleValue()== 0D ){
		if(cError.length()>0) cError+=";";
		cError+="SIC";
	}
	
	
	if(c.getReclamos().size()>0) {
		if(!"CERRADO".equals(c.getReclamoEstado())) {
			if(cError.length()>0) cError+=";";
			cError+="RPO";
		}
	}
	
	if(!"".equals(cError)) {
		c.setConProblema(true);
		c.setError(cError);
		c.setOrden(0);
	}else {
	    c.setError("OK");
	    c.setOrden(1);
	}
	
	return cError;
}
	

private void actualizaComprobante(ComprobanteHospital comprobante,HttpServletRequest renderRequest) throws SystemException{

	Integer pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
	String tipoC = ParamUtil.getString(renderRequest, "tipo_comprobante");
	String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
	String letra = ParamUtil.getString(renderRequest, "letra", " ");
	
	String fechaEmisionComprobanteMes = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteMes");
	String fechaEmisionComprobanteDia = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteDia");
	String fechaEmisionComprobanteAnio = ParamUtil.getString(renderRequest,
			"fechaEmisionComprobanteAnio");
	SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	Date fechaEmisionC = null;
	try {
		fechaEmisionC = formatoDeFecha.parse(fechaEmisionComprobanteDia
				+ "/" + (Integer.parseInt(fechaEmisionComprobanteMes) + 1)
				+ "/" + fechaEmisionComprobanteAnio);
	} catch (Exception e) {
		fechaEmisionC = null;
	}

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
		fechaVencimientoC = formatoDeFecha.parse(fechaVencimientoComprobanteDia
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

	String estado=ParamUtil.getString(renderRequest,"estado",null);
	String sector=ParamUtil.getString(renderRequest,"sector",null);
	String usr=ParamUtil.getString(renderRequest,"user",null);
	
	
	String cuitAcreedor = renderRequest.getParameter("cuit_compr_emisor");
	String razon_soc = renderRequest.getParameter("razon_social");
	String dni = renderRequest.getParameter("dni");
	
	String prestacion = renderRequest.getParameter("codigo_trat");
	
	String prestaciondesc = renderRequest.getParameter("prestacion_trat");
	
	String cantidad = renderRequest.getParameter("cantidad");
	String cae =renderRequest.getParameter("cae");
	String importe=renderRequest.getParameter("importe");
	String importe_solicitado=renderRequest.getParameter("importe_solicitado");
	String observaciones= renderRequest.getParameter("observaciones");
	String comentario= renderRequest.getParameter("comentario");
	
	String idPrestador= renderRequest.getParameter("id_prestador");
	
	
    comprobante.setTipoComprobante(tipoC);
	comprobante.setNroComprobante(nroC);
	comprobante.setLetraComprobante(letra);
	comprobante.setPtoVenta(pto_venta);
	Empresa emp = new Empresa(cuitAcreedor, null,razon_soc);
	comprobante.setAcreedorEmpresa(emp);
	
	Afiliado afi =new Afiliado();
	afi.setDocu_numero(dni);
	comprobante.setAfiliado(afi);
	comprobante.setAlta_usr(usr);
	comprobante.setPeriodoPrestacion(peri);
	comprobante.setCodigoPrestacion(prestacion);
	comprobante.setDescripcionPrestacion(prestaciondesc);
	
	comprobante.setEstado(estado);
	comprobante.setSectorDestino(sector);
	comprobante.setFechaEmision(fechaEmisionC);
	comprobante.setFechaRecepcion(fechaRecepcionC);
	comprobante.setFechaVencimiento(fechaVencimientoC);
	
	comprobante.setComentario(comentario);
	comprobante.setObservaciones(observaciones);
	comprobante.setCae(cae);
	comprobante.setCantidad(Integer.parseInt(cantidad));
	
	comprobante.setPeriodoPrestacion(peri);
	if(importe.contains(",")) {
	   importe=importe.replace(".", "").replace(",", ".");
	}
	comprobante.setImporteComprobante(BigDecimal.valueOf(Double.valueOf(importe)));
	
	comprobante.setIdPrestador(Integer.parseInt(idPrestador));
	
}


private long updateComprobante(ComprobanteHospital comprobante, String user) throws Exception{
	long id = 0;
	
	id = ComprobanteServiceUtil.updateComprobanteHospital(comprobante, user);
	return id;
}
	


private void generarLiquidaciones(RenderRequest renderRequest,HttpSession session,String entidad,User user) throws Exception{
	String ids = ParamUtil.getString(renderRequest, "ids");
	
	errores = new ArrayList<String>();
	
    List<ComprobanteHospital> lista = (List<ComprobanteHospital>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS);
    List<ComprobanteHospital> listaAux=new ArrayList<ComprobanteHospital>();
    if(lista!=null && !lista.isEmpty()) {
      for(Integer i=0;i<lista.size();i++) {
    	lista.get(i).setOrden(999999999);
    	if (ids.contains(lista.get(i).getId().toString().trim())) {
    		ComprobanteFiltro filtro= new ComprobanteFiltro();
    		filtro.setAcreedorEmpresa(lista.get(i).getAcreedorEmpresa());
    		filtro.setTipoComprobante(lista.get(i).getTipoComprobante());
    		filtro.setLetraComprobante(lista.get(i).getLetraComprobante());
    	    filtro.setPtoVenta(lista.get(i).getPtoVenta());
    	    filtro.setNroComprobante(lista.get(i).getNroComprobante());
    	    listaAux=ComprobanteServiceUtil.getListaGerenciadoras(filtro,0);
    	    if(listaAux!=null && !listaAux.isEmpty()) lista.set(i, listaAux.get(0));
    	    
    	    ComprobanteHospital cbte =lista.get(i);
    	    
    		verificaComprobante(cbte);

//generar Liquidacion    
    			if("OK".equalsIgnoreCase(cbte.getError()) &&
        				(cbte.getLiquidacionId()==null || cbte.getLiquidacionId().intValue()==0
        				) ) {
                         				
		  
    			    Integer idLiquidacion =0;
    			    BigDecimal cargoOspim=BigDecimal.ZERO;
    			    BigDecimal cargoPrestadora=BigDecimal.ZERO;
    			    BigDecimal cargoImesa=BigDecimal.ZERO;
    			    		
    			    if(cbte.getReclamos().size()>0) {
    			     for(ReclamoPrestacional rx:cbte.getReclamos()){ 	
    			       ReclamoPrestacional reclamo = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(rx.getId_reclamo());
    			       for(PrestacionesReclamo p:reclamo.getPrestaciones()) {
    			    	   
    			    	 if(p.getComprobanteCUIT().equals( cbte.getAcreedorEmpresa().getCuit()) &&
    	    			    	   p.getComprobanteTipo().equals(cbte.getTipoComprobante())	&&
    	    			    	   p.getComprobanteLetra().equals(cbte.getLetraComprobante()) &&
    	    			    	   Integer.parseInt(p.getComprobanteSucursal())==cbte.getPtoVenta()	&&
    	    			    	   p.getComprobanteNro().equals(cbte.getNroComprobante())) {
    			    	   
    			    	     String importeTotal =p.getTotalString().replace(",", ".");
    			    	     cargoOspim=cargoOspim.add(new BigDecimal(p.getCargo_ospim()));
    			    	     cargoPrestadora=cargoPrestadora.add(new BigDecimal(p.getCargo_ps()));
    			    	     cargoImesa=cargoImesa.add(new BigDecimal(p.getCargo_imesa()));
    			             if(idLiquidacion==0) {	
    			    		  idLiquidacion=EditarLiquidacionServiceUtil.cargaLiquidacionEntry(new Date(), cbte.getFechaEmision(),
    			    		  cbte.getFechaRecepcion(), cbte.getFechaVencimiento(), cbte.getPeriodoPrestacion(), "O.S.P.I.M.",
    			    		  cbte.getIdPrestador(), 0, cbte.getTipoComprobante(),
    			    		  cbte.getLetraComprobante(), cbte.getPtoVenta(),
    			    		  cbte.getNroComprobante(), "O.S.P.I.M.",
    						  reclamo.getAfiliado().getCuil_titular(),reclamo.getAfiliado().getInte(), 0, p.getFechaPrestacion(),
    						  p.getId_prestacion(), WebKeysLiquidaciones.LIQUIDACION_PRE, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO,
    						  user,p.getCantidadString(), p.getImporteString().replace(",", "."), "0",
    						  "0", "0", "0","0", 
    						  cbte.getImporteComprobante().toString(), cbte.getImporteComprobante().toString(), "0",
    						  "", "0",  cbte.getAcreedorEmpresa().getCuit(), 
    						  89, importeTotal,cbte.getPeriodoPrestacion(), 
    						  0,reclamo.getId_reclamo(),p.getIdRegistro() , 
    						  new BigDecimal(p.getCargo_ospim()), BigDecimal.ZERO,BigDecimal.ZERO, new BigDecimal(p.getCargo_ps() ),
    						  BigDecimal.ZERO,new BigDecimal(p.getCargo_imesa()));
    			             } else {
    			        	   EditarLiquidacionServiceUtil.actualizaLiquidacionEntry(idLiquidacion,
    			        			new Date(), cbte.getFechaEmision(), cbte.getFechaRecepcion(), cbte.getFechaVencimiento(),
    			        			cbte.getPeriodoPrestacion(), reclamo.getAfiliado().getCuil_titular(),
    			        			reclamo.getAfiliado().getInte(), cbte.getIdPrestador(), 0, p.getId_prestacion(),
    								p.getFechaPrestacion(), p.getCantidadString(), p.getImporteString().replace(",", "."),
    								cbte.getTipoComprobante(),
    								cbte.getLetraComprobante(), cbte.getPtoVenta(), cbte.getNroComprobante(), "0", user,
    								"0", "0", "0", "0", cbte.getImporteComprobante().toString(),
    								cbte.getImporteComprobante().toString(), "0", "", "0",
    								 cbte.getAcreedorEmpresa().getCuit(), 89, importeTotal, renderRequest, 
    								 cbte.getPeriodoPrestacion(), 0,reclamo.getId_reclamo()
    								,p.getIdRegistro() , new BigDecimal(p.getCargo_ospim()), BigDecimal.ZERO,BigDecimal.ZERO,
    								new BigDecimal(p.getCargo_ps()),BigDecimal.ZERO,new BigDecimal(p.getCargo_imesa()));
    			            }
    			    	 }  
    			       }
    			    
    			     }
    			     EditarLiquidacionServiceUtil.actualizaCargosTotal(idLiquidacion,
    			    		cargoOspim.setScale(2, RoundingMode.HALF_UP),	cargoPrestadora.setScale(2, RoundingMode.HALF_UP),
    			    		cargoImesa.setScale(2,RoundingMode.HALF_UP));
    			    
                          			    
    			    }else {
    			    	idLiquidacion =EditarLiquidacionServiceUtil.cargaLiquidacionEntry(new Date(), cbte.getFechaEmision(),
        			    		cbte.getFechaRecepcion(), cbte.getFechaVencimiento(), cbte.getPeriodoPrestacion(), "O.S.P.I.M.",
        			    		cbte.getIdPrestador(), 0, cbte.getTipoComprobante(),
        			    		cbte.getLetraComprobante(), cbte.getPtoVenta(),
        			    		cbte.getNroComprobante(), "O.S.P.I.M.",
        						"", 0, 0, cbte.getPeriodoPrestacion(),
        						0, WebKeysLiquidaciones.LIQUIDACION_PRE, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO,
        						user, "1", cbte.getImporte().toString(), "1",
        						"0", "0", "0","0", 
        						cbte.getImporte().toString(), cbte.getImporte().toString(), "0",
        						"", "1",  cbte.getAcreedorEmpresa().getCuit(), 
        						0, "0",cbte.getPeriodoPrestacion(), 
        						0,0,0, 
        						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        						BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
    			    }
    			    
    			    cbte.setLiquidacionId(new BigDecimal(idLiquidacion));
    			    cbte.setOrden(idLiquidacion);
// Fin generar Liquidacion    			
    			}	
    	}
      }
            
    }
    if (SessionErrors.isEmpty(renderRequest)) {
		renderRequest.setAttribute("errores", errores);
	}
    
    Collections.sort(lista, new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((Comparable<Integer>) ((ComprobanteHospital) (o1)).getOrden())
					.compareTo(((ComprobanteHospital) (o2)).getOrden());
		}
	});
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS,lista);
  }
	
  private void recuperarImagenes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
		String ids = ParamUtil.getString(renderRequest, "ids");
		
	    List<ComprobanteHospital> lista = (List<ComprobanteHospital>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS);
	    List<Comprobante> listaAux=new ArrayList<Comprobante>();
	    if(lista!=null && !lista.isEmpty()) {
	      for(Integer i=0;i<lista.size();i++) {
	    	if (ids.contains(lista.get(i).getId().toString().trim())) {
	    	  if(lista.get(i).getImagenes().size()==0) {	
	    		Comprobante filtro= new Comprobante();
	    		filtro.setAcreedorEmpresa(lista.get(i).getAcreedorEmpresa());
	    		filtro.setTipoComprobante(lista.get(i).getTipoComprobante());
	    		filtro.setLetraComprobante(lista.get(i).getLetraComprobante());
	    	    filtro.setPtoVenta(lista.get(i).getPtoVenta());
	    	    filtro.setNroComprobante(lista.get(i).getNroComprobante());
	    	    filtro.setId(lista.get(i).getId());
//	    	    lista.get(i).setOrden(0);
	    	    listaAux.add(filtro);
	    	  }   
	    	}else {
//	    		lista.get(i).setOrden(999999);
	    	}
	      }
	      
	      
	      if(listaAux!=null && !listaAux.isEmpty()) {
	    	  ClienteProveedoresLPA.getComprobantes(listaAux);
	    	  ClienteProveedoresLPA.getAdjuntos(listaAux);
	      }
	      
	      String idFacturaImg="";
	      for(ComprobanteHospital comprobante:lista) {
	    	  if(comprobante.getImagenes()==null || comprobante.getImagenes().size()==0 ) {
				idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
						comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();
				List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
				comprobante.setImagenes(list);
	    	  }	
		  }
	      
/*	      
	      Collections.sort(lista, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					return ((Comparable<Integer>) ((ComprobanteAcompanante) (o1)).getOrden())
							.compareTo(((ComprobanteAcompanante) (o2)).getOrden());
				}
			});
*/
	    }
		session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_GERENCIADORAS,lista);
	}

  

}