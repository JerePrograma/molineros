package ar.com.ospim.comprobantesPortalProveedores.action;

import java.math.BigDecimal;
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

import ar.com.ospim.afiliados.beans.AfiDocumentacion;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.DocumentacionServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.IntegracionReglasValidacion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteIntegracion;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.proveedoresLPA.ClienteProveedoresLPA;


public class ComprobantesProveedoresIntegracionAction extends PortletAction {
	
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

		
//		PreAutorizacion preautorizacion=null;
		
		ComprobanteIntegracion comprobante =null;
		
		Integer idComprobante = 0;
		String msg = "";
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		
		if (!StringUtils.checkEmpty(cmd)) {
			idComprobante = ParamUtil.getInteger(renderRequest,"id_comprobante", 0);
			
			if(cmd.equals("buscar")){
				
		           filterComprobantes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
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
			
			if(cmd.equals("asignarCarpeta")){
				
		           asignarComprobantesCarpeta(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
			}
			
			if(cmd.equals("verificar")){
				
		           verificarComprobantes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
			}
			
			if(cmd.equals("recuperarimgs")){
				
		           recuperarImagenes(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
			}
			
			if(cmd.equals("recuperarimgsrecibo")){
				
		           recuperarImagenRecibo(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
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
				 List<ComprobanteIntegracion> lista = ComprobanteServiceUtil.getListaIntegracion(c,0);
				 session.setAttribute(WebKeysComprobantes.COMPROBANTE_INTEGRACION_EN_EDICION, lista.get(0));
				 return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobante_integracion_editar"));
			}
           
			if(cmd.equals(Constants.UPDATE) ){
				
				comprobante=(ComprobanteIntegracion)  session.getAttribute(WebKeysComprobantes.COMPROBANTE_INTEGRACION_EN_EDICION);
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
				List<ComprobanteIntegracion> lista = ComprobanteServiceUtil.getListaIntegracion(c,0);
				session.setAttribute(WebKeysComprobantes.COMPROBANTE_INTEGRACION_EN_EDICION, lista.get(0));
				
				
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.comprobantes.comprobante_integracion_editar"));
				
			}
			
			if(cmd.equals("exportarCarpeta")){
				
		           exportarCarpeta(renderRequest,session,entidad);		   	
		           
		           return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result_integracion"));		
	   			  	
			}
			
		}
		renderRequest.setAttribute("tab", tabSel);
		return mapping.findForward("portlet.comprobantes.comprobantes_proveedores_integracion");
		
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

	String carpetaStr = ParamUtil.getString(renderRequest,
			"carpetaMesAnio");
	Date carpeta = null;
	try {
		carpeta = formatoDeFecha.parse("01/"
				+ (Integer.parseInt(carpetaStr.split("_")[0]) + 1)
				+ "/" + carpetaStr.split("_")[1]);
	} catch (Exception e) {
		carpeta = null;
	}
	
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
	filtro.setCarpeta(carpeta);
	filtro.setPendientes(pendientes);
		
	List<ComprobanteIntegracion> lista = ComprobanteServiceUtil.getListaIntegracion(filtro,0);
	
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_FILTRO_INTEGRACION ,filtro);
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
	
	//Seteos Paginadaor
	/*
	Integer tRegistros=0;
	if(lista!=null && !lista.isEmpty()) {
		 tRegistros=lista.get(0).getTotalRegistros();
	}	

	session.removeAttribute(WebKeysComprobantes.COMPROBANTES_PROVEEDORES_TOTAL_REGISTROS);
    session.removeAttribute( WebKeysComprobantes.COMPROBANTES_PROVEDORES_OFFSET_REG);
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_PROVEEDORES_TOTAL_REGISTROS, tRegistros );
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_PROVEDORES_OFFSET_REG, pagina);
	*/
}
	

private void asignarComprobantesCarpeta(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	
	
	
	String ids = ParamUtil.getString(renderRequest, "ids");
	Boolean operacion=ParamUtil.getBoolean(renderRequest, "operacion");
	String carpetaStr = ParamUtil.getString(renderRequest,"carpeta");
	SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	Date carpeta = null;
	try {
		carpeta = formatoDeFecha.parse("01/"
				+ (Integer.parseInt(carpetaStr.split("_")[0]) + 1)
				+ "/" + carpetaStr.split("_")[1]);
	} catch (Exception e) {
		carpeta = null;
	}
	
	ComprobanteServiceUtil.updateCarpetaIntegracion(ids,operacion,carpeta);
	
	ComprobanteFiltro filtro = new ComprobanteFiltro();
	filtro=(ComprobanteFiltro) session.getAttribute(WebKeysComprobantes.COMPROBANTES_FILTRO_INTEGRACION);
    List<ComprobanteIntegracion> lista = ComprobanteServiceUtil.getListaIntegracion(filtro,0);
	
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
	
}



private void verificarComprobantes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	String ids = ParamUtil.getString(renderRequest, "ids");
	
    List<ComprobanteIntegracion> lista = (List<ComprobanteIntegracion>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION);
    List<ComprobanteIntegracion> listaAux=new ArrayList<ComprobanteIntegracion>();
    if(lista!=null && !lista.isEmpty()) {
      for(Integer i=0;i<lista.size();i++) {
    	if (ids.contains(lista.get(i).getId().toString().trim())) {
    		ComprobanteFiltro filtro= new ComprobanteFiltro();
    		filtro.setAcreedorEmpresa(lista.get(i).getAcreedorEmpresa());
    		filtro.setTipoComprobante(lista.get(i).getTipoComprobante());
    		filtro.setLetraComprobante(lista.get(i).getLetraComprobante());
    	    filtro.setPtoVenta(lista.get(i).getPtoVenta());
    	    filtro.setNroComprobante(lista.get(i).getNroComprobante());
    	    listaAux=ComprobanteServiceUtil.getListaIntegracion(filtro,0);
    	    if(listaAux!=null && !listaAux.isEmpty()) lista.set(i, listaAux.get(0));
    		verificaComprobante(lista.get(i));
    	}else {
    		lista.get(i).setOrden(999999);
    	}
      }
      
      Collections.sort(lista, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable<Integer>) ((ComprobanteIntegracion) (o1)).getOrden())
						.compareTo(((ComprobanteIntegracion) (o2)).getOrden());
			}
		});
      
    }
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
}



///////////////////////////////
///////////////////////////////
//////////////////////////////

private String verificaComprobante(ComprobanteIntegracion c) throws Exception {
	String cError="";
	if(c.getCae()==null || c.getCae()=="") {
		if(cError.length()>0) cError+=";"; 
		cError+="CAE";
	}
	
	if(c.getAfiliado().getCuil_titular()==null) {
		if(cError.length()>0) cError+=";";
		cError+="AFI";
	}else { 
		
	  Afiliado a =EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(c.getAfiliado().getCuil_titular(), c.getAfiliado().getInte());
	  if(a!=null && a.getBaja_fecha()!=null && a.getBaja_fecha().before((new Date()))) {
		  if(cError.length()>0) cError+=";";
		  cError+="ABJ";
	  }
			  	
	  if(c.getCud()==null ) {
	    List<AfiDocumentacion> documentacionList= DocumentacionServiceUtil.buscaDocumentacion(c.getAfiliado().getCuil_titular(),c.getAfiliado().getInte());
	    Boolean existeCUD=false;
	    for(AfiDocumentacion d:documentacionList) {
		  if(d.getCodigoCUD()!=null && d.getFecha_baja()!=null && 
				  (d.getFecha_ingre().before(c.getPeriodoPrestacion()) || 
				   d.getFecha_ingre().equals(c.getPeriodoPrestacion())
				  )
			&&( d.getFecha_baja().after(c.getPeriodoPrestacion()) ||
				d.getFecha_baja().equals(c.getPeriodoPrestacion())	
			  )		
			&& c.getAfiliado().getInte()==d.getAfiliado().getInte()) {
			existeCUD=true;
			break;
		  }
	    }
	    if(!existeCUD) {
		  if(cError.length()>0) cError+=";";
		  cError+="CUD";
  	    }
	  }
	}
	
	List<Prestador> lp = PrestadorServiceUtil.getPrestadores(0, c.getAcreedorEmpresa().getCuit() ,null, false);
	if(lp==null || lp.size()==0){
		 if(cError.length()>0) cError+=";";
			cError+="PST"; 
	}
	
	if(c.getCodigoPrestacion()==null) {
		if(cError.length()>0) cError+=";";
		cError+="NOM";
	}else {
		Nomenclador n= ComprobanteServiceUtil.buscaNomencladorSSSByCodigo(c.getCodigoPrestacion());
		if(n==null || n.getId_prestacion()==0) {
			if(cError.length()>0) cError+=";";
			cError+="III";
		}
	}
	
	if(c.getDependencia()==null) {
		if(cError.length()>0) cError+=";";
		cError+="DEP";
	}
	
	if(c.getProvincia()==null) {
		if(cError.length()>0) cError+=";";
		cError+="PRO";
	}
	
	if(c.getImporteComprobante()==null ||  c.getImporteComprobante().doubleValue()== 0D){
		if(cError.length()>0) cError+=";";
		cError+="SIC";
	}
	
	if(c.getImporteSolicitado()==null || c.getImporteSolicitado()==0){
		if(cError.length()>0) cError+=";";
		cError+="SIS";
	}
	
	if(c.getImporteSolicitado()!=null && !c.getImporteSolicitado().equals(BigDecimal.ZERO)
		&&c.getImporteComprobante()!=null && !c.getImporteComprobante().equals(BigDecimal.ZERO) &&
		c.getImporteComprobante().doubleValue()-c.getImporteSolicitado()<-.01){
		if(cError.length()>0) cError+=";";
		cError+="S>I";
	}
	
	ComprobanteFiltro filtro= new ComprobanteFiltro();
	filtro.setAcreedorEmpresa(c.getAcreedorEmpresa());
	filtro.setTipoComprobante(c.getTipoComprobante());
	filtro.setLetraComprobante(c.getLetraComprobante());
    filtro.setPtoVenta(c.getPtoVenta());
    filtro.setNroComprobante(c.getNroComprobante());
	filtro.setCarpeta(c.getCarpeta());
	/*
    if(verificaCarpeta!=null && verificaCarpeta!=0) {
        filtro.setCarpeta(sdf.parse(String.valueOf(verificaCarpeta*100+1)));
    }else {
    	filtro.setCarpeta(c.getCarpeta());
    }
    */
    List<ComprobanteIntegracion> list = ComprobanteServiceUtil.validaExistenciaComprobante(filtro);
	if(list!=null && !list.isEmpty()) {
		if(cError.length()>0) cError+=";";
		cError+="DUP";	
		
	   for(ComprobanteIntegracion ci :list) {
		   if(ci.getOrdenPagoId()!=null && !ci.getOrdenPagoId().equals(BigDecimal.ZERO) ) {
			   cError += " op " +ci.getOrdenPagoId().toPlainString();
		   }
		   if(ci.getLiquidacionId()!=null && !ci.getLiquidacionId().equals(BigDecimal.ZERO)) {
			   cError += " liq " +ci.getLiquidacionId().toPlainString();
		   }
		   
		   if(ci.getLoteSSS()!=null && ci.getLoteSSS()!=0 ) {
			   cError += " lot " +ci.getLoteSSS().toString();
		   }
		   
		   if(ci.getCabeceraId()!=null && ci.getCabeceraId()!=0) {
			   cError += " cab " +ci.getCabeceraId().toString();
		   }
	   }
	}
	
	ComprobanteFiltro filtro1= new ComprobanteFiltro();
	filtro1.setAcreedorEmpresa(c.getAcreedorEmpresa());
	filtro1.setAfiliado(c.getAfiliado());
	filtro1.setCodigoPrestacion(c.getCodigoPrestacion());
	filtro1.setPeriodoPrestacion(c.getPeriodoPrestacion());
	
    List<AutorizacionPrestacional> list1 = ComprobanteServiceUtil.getListaAutorizacionesPrestacionales(filtro1);
	if(list1==null || list1.isEmpty()) {
		if(cError.length()>0) cError+=";";
		cError+="AUT";	
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
	

private void actualizaComprobante(ComprobanteIntegracion comprobante,HttpServletRequest renderRequest) throws SystemException{

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
	
	
    String cud = ParamUtil.getString(renderRequest, "cud", " ");
	
	String fechaCudMes = ParamUtil.getString(renderRequest,
			"fechaCudMes");
	String fechaCudDia = ParamUtil.getString(renderRequest,
			"fechaCudDia");
	String fechaCudAnio = ParamUtil.getString(renderRequest,
			"fechaCudAnio");
	
	Date fechaCudC = null;
	try {
		fechaCudC = formatoDeFecha.parse(fechaCudDia
				+ "/" + (Integer.parseInt(fechaCudMes) + 1)
				+ "/" + fechaCudAnio);
	} catch (Exception e) {
		fechaCudC = null;
	}

	Integer provincia = ParamUtil.getInteger(renderRequest, "provincia");
	String dependencia = ParamUtil.getString(renderRequest, "dependencia");
	
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
	comprobante.setCud(cud);
	comprobante.setCudVto(fechaCudC);
	comprobante.setProvincia(provincia);
	comprobante.setDependencia(dependencia);
	if(importe.contains(",")) {
	   importe=importe.replace(".", "").replace(",", ".");
	}
	if(importe_solicitado.contains(",")) {
	  importe_solicitado=importe_solicitado.replace(".", "").replace(",", ".");
	}  
	comprobante.setImporteComprobante(BigDecimal.valueOf(Double.valueOf(importe)));
	comprobante.setImporteSolicitado(Double.valueOf(importe_solicitado));
	
}


private long updateComprobante(ComprobanteIntegracion comprobante, String user) throws Exception{
	long id = 0;
	
	id = ComprobanteServiceUtil.updateComprobanteIntegracion(comprobante, user);
	return id;
}
	
	

private void exportarCarpeta(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	String carpetaStr = ParamUtil.getString(renderRequest,"carpeta");
	SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMM");
	Boolean forzar = ParamUtil.getBoolean(renderRequest,"forzar");
	Date carpeta = null;
	errores = new ArrayList<String>();
	User user = PortalUtil.getUser(renderRequest); 
	Integer carpetaInt=0;
	String codError="";
	IntegracionReglasValidacion reglas = IntegracionServiceUtil.getReglasValidacion();
	try {
		carpeta = formatoDeFecha.parse("01/"
				+ (Integer.parseInt(carpetaStr.split("_")[0]) + 1)
				+ "/" + carpetaStr.split("_")[1]);
		
		carpetaInt=Integer.valueOf(sdf.format(carpeta));
		ComprobanteFiltro filtro = new ComprobanteFiltro();
		filtro.setCarpeta(carpeta);
		List<ComprobanteIntegracion> lista = ComprobanteServiceUtil.getListaIntegracion(filtro,0);
	    Boolean cError=false;
	    if(!lista.isEmpty()) {
	      if(!forzar) {	
	    	for(ComprobanteIntegracion c:lista) {
	    		String error= verificaComprobante(c);
	    		if(!cError && error.length()>0) {
	    			cError=true;
	    			errores.add("Existen errores en los registros que se quieren exportar");
	    		}
	    	}
	      }	
	    }else {
	    	cError=true;
	    	errores.add("No Existen registros asignados a esta carpeta para exportar");
	    }
	    
	    if(!cError) {
	    	IntegracionCabeceraDS cab = new IntegracionCabeceraDS();
			cab.setEntidad("OS");
			cab.setPeriodo(carpetaInt);
			cab.setFecha(new Date());
			for(ComprobanteIntegracion c:lista) {
				IntegracionDetalleDS ds = creaDetalleDS(c);
				codError = IntegracionServiceUtil.validaDetalle(ds, false, reglas);
				ds.setError(codError);
				cab.getItems().add(ds);
			}
			ComprobanteServiceUtil.eliminarIntegracionPeriodo(carpetaInt, "OS");
			Integer idLote=IntegracionServiceUtil.saveLote(cab,user.getScreenName());
			String msg = "Se ha exportado la carpeta en la cabecera de integración nro ";
			msg = msg +" " +idLote;
			SessionMessages.add(renderRequest, "insertCabOk");
			renderRequest.setAttribute("msgCabOk", msg);
	    }
	    session.setAttribute(WebKeysComprobantes.COMPROBANTES_FILTRO_INTEGRACION,filtro);
		session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
	} catch (Exception e) {
		carpeta = null;
	}
	if (SessionErrors.isEmpty(renderRequest)) {
		renderRequest.setAttribute("errores", errores);
	}
}

private IntegracionDetalleDS creaDetalleDS(ComprobanteIntegracion c) {
	IntegracionDetalleDS archivo = new IntegracionDetalleDS();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	Integer tipo_comprobante_p=0;
	Nomenclador prestacionSSS=null;
	try{
		if("FCP".equals(c.getTipoComprobante()) && "A".equals(c.getLetraComprobante())) { 
				   tipo_comprobante_p=1;
		}else if("RCB".equals(c.getTipoComprobante()) && "A".equals(c.getLetraComprobante())) { 		   
				   tipo_comprobante_p=2; 
		}else if("FCP".equals(c.getTipoComprobante()) && "B".equals(c.getLetraComprobante())) {				   
				   tipo_comprobante_p=3;
		}else if("RCB".equals(c.getTipoComprobante()) && "B".equals(c.getLetraComprobante())) {		   
				   tipo_comprobante_p=4;
		}else if("FCP".equals(c.getTipoComprobante()) && "C".equals(c.getLetraComprobante())) {				   
				   tipo_comprobante_p=5;
		}else if("RCB".equals(c.getTipoComprobante()) && "C".equals(c.getLetraComprobante())) {				   
				   tipo_comprobante_p=6;
		}else if("FCP".equals(c.getTipoComprobante()) && "M".equals(c.getLetraComprobante())) {				   
				   tipo_comprobante_p=7;
		}else if("RCB".equals(c.getTipoComprobante()) && "M".equals(c.getLetraComprobante())) {		   
				   tipo_comprobante_p=8;
		}
		prestacionSSS=ComprobanteServiceUtil.buscaNomencladorSSSByCodigo(c.getCodigoPrestacion());
         		
		archivo.setTipoArchivo("DS"); //Tipo de Archivo
		archivo.setIdObraSocial(112608); //Código de obra social
		archivo.setCuil(c.getAfiliado().getCuil()); //CUIL Beneficiario
		archivo.setCertificadoCodigo(c.getCud()); //Código del Certificado
		if(c.getCudVto() !=null) {
	     archivo.setCertificadoVencimiento(c.getCudVto()); //Vencimiento del Certificado
	    } 
		archivo.setPeriodoPrestacion(Integer.parseInt(sdf.format(c.getPeriodoPrestacion()))); //Periodo Prestacion
		archivo.setCuitPrestador(c.getAcreedorEmpresa().getCuit()); //CUIT de prestador
		archivo.setComprobanteTipo(tipo_comprobante_p);  //Tipo de comprobante
		archivo.setComprobanteTipoEmision("E"); //Tipo de emisión  
		if(c.getFechaEmision()!=null) {
		     archivo.setComprobanteFechaEmision(c.getFechaEmision()); //Fecha Emision Comprobante
	    }   
		archivo.setComprobanteCAECAI(c.getCae()); //Numero CAE-CAI  
		archivo.setComprobantePtoVta(c.getPtoVenta());//Punto de Venta  
		archivo.setComprobanteNro(Integer.parseInt(c.getNroComprobante())); //Número Comprobante  
		archivo.setComprobanteImporte(c.getImporteComprobante().doubleValue()*100); //Importe Comprobante 
		archivo.setImporteSolicitado(c.getImporteSolicitado()*100); //Importe Solicitado  
		archivo.setPrestacionCodigo(prestacionSSS.getId_prestacion_string()); //Código de Práctica		
		archivo.setPrestacionCantidad(c.getCantidad()); //Cantidad de Practicas
		
		
		//Ajuste de cantidad de Practicas si no estan dentro del rango preestablecido
		boolean esCorrecto=true;
		if(prestacionSSS!=null) {
		  if(prestacionSSS.getCantidadDesde()!=null && prestacionSSS.getCantidadDesde()!=0) {
			if(c.getCantidad()<prestacionSSS.getCantidadDesde()) {
				esCorrecto=false;
			}
		  }
		  if(prestacionSSS.getCantidadHasta()!=null && prestacionSSS.getCantidadHasta()!=0) {
			if(c.getCantidad()>prestacionSSS.getCantidadHasta()) {
				esCorrecto=false;
			}
		  }
		
		  if(!esCorrecto) {
			if(prestacionSSS.getCantidadCorrecta()!=null) {
				archivo.setPrestacionCantidad(prestacionSSS.getCantidadCorrecta());
			}
		  }
		} 
		///////////////
		
		archivo.setProvincia(c.getProvincia()); // Provincia
		archivo.setDependencia(c.getDependencia()); //Dependencia  
	}catch(Exception e){
			_log.debug(e);
	}
	return archivo;
}
	

private void recuperarImagenes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	String ids = ParamUtil.getString(renderRequest, "ids");
	
    List<ComprobanteIntegracion> lista = (List<ComprobanteIntegracion>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION);
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
 //   	    lista.get(i).setOrden(0);
    	    listaAux.add(filtro);
    	  }  
    	}else {
 //   		lista.get(i).setOrden(999999);
    	}
      }
      
      
      if(listaAux!=null && !listaAux.isEmpty()) {
    	  ClienteProveedoresLPA.getComprobantes(listaAux);
    	  ClienteProveedoresLPA.getAdjuntos(listaAux);
      }
      
      
      String idFacturaImg="";
      for(ComprobanteIntegracion comprobante:lista) {
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
				return ((Comparable<Integer>) ((ComprobanteIntegracion) (o1)).getOrden())
						.compareTo(((ComprobanteIntegracion) (o2)).getOrden());
			}
		});
*/
      
    }
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
}


private void recuperarImagenRecibo(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	String ids = ParamUtil.getString(renderRequest, "ids");
	
    List<ComprobanteIntegracion> lista = (List<ComprobanteIntegracion>) session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION);
    List<Comprobante> listaAux=new ArrayList<Comprobante>();
    if(lista!=null && !lista.isEmpty()) {
      for(Integer i=0;i<lista.size();i++) {
    	if (ids.contains(lista.get(i).getId().toString().trim())) {
    	  if(lista.get(i).getImagenRecibo()==null) {	
    		Comprobante filtro= new Comprobante();
    		filtro.setAcreedorEmpresa(lista.get(i).getAcreedorEmpresa());
    		filtro.setTipoComprobante(lista.get(i).getTipoComprobante());
    		filtro.setLetraComprobante(lista.get(i).getLetraComprobante());
    	    filtro.setPtoVenta(lista.get(i).getPtoVenta());
    	    filtro.setNroComprobante(lista.get(i).getNroComprobante());
    	    filtro.setId(lista.get(i).getId());
 //   	    lista.get(i).setOrden(0);
    	    listaAux.add(filtro);
    	  }  
    	}else {
 //   		lista.get(i).setOrden(999999);
    	}
      }
      
      
      if(listaAux!=null && !listaAux.isEmpty()) {
    	  ClienteProveedoresLPA.getComprobanteRecibo(listaAux);
      }
      
      String idFacturaImg="";
      for(ComprobanteIntegracion comprobante:lista) {
    	  if(comprobante.getImagenes()==null || comprobante.getImagenes().size()==0 ) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();
			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
    	  }	
	  }
    }
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_INTEGRACION,lista);
}


 


}