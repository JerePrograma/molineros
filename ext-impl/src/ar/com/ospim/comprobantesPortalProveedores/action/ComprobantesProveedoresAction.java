package ar.com.ospim.comprobantesPortalProveedores.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.proveedoresLPA.ClienteProveedoresLPA;


public class ComprobantesProveedoresAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
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
		
		Comprobante comprobante =null;
		
		Integer idComprobante = 0;
		String msg = "";
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		
		if (!StringUtils.checkEmpty(cmd)) {
			idComprobante = ParamUtil.getInteger(renderRequest,"id_comprobante", 0);
			
			if(cmd.equals("buscar")){
				
		           filterComprobantes(renderRequest,session,entidad);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result"));	
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
				 // 		 c.getLetraComprobante()+String.format("%05d",c.getPtoVenta())+c.getNroComprobante();
				 //BORRAR
				 //idFacturaImg = c.getCuit()+"-"+c.getTipoComprobante()+"-"+
				 //		c.getSucuComprobante()+c.getLetraComprobante()+c.getNroComprobante();
				 //FIN - BORRAR
				 
				 
				 session.setAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW, c);
				 return mapping.findForward(getForward(renderRequest,
										"portlet.comprobantes.imagenes"));
			}
			
			if(cmd.equals("recuperarimagenes") ){
	           	if(idComprobante>0) {
				 String desdeResult = ParamUtil.get(renderRequest, "desde_result", "");
				 String cuit = ParamUtil.get(renderRequest, "cuit", "");
				 String tipo = ParamUtil.get(renderRequest, "tipo", "");
				 String letra = ParamUtil.get(renderRequest, "letraComprobante", "");
				 String ptovta = ParamUtil.get(renderRequest, "ptovta", "");
				 String nro = ParamUtil.get(renderRequest, "nro", "");
				
				 
				 session.setAttribute("esPopUp","N");
				 session.setAttribute("desde_result",desdeResult);
				 Comprobante c =new Comprobante();
				 Empresa e = new Empresa();
				 e.setCuit(cuit);
				 c.setAcreedorEmpresa(e);
				 c.setCuit(cuit);
				 c.setTipoComprobante(tipo);
				 c.setLetraComprobante(letra);
				 c.setPtoVenta(Integer.parseInt(ptovta));
				 c.setNroComprobante(nro);
				 c.setId(idComprobante);
				 
				 List<Comprobante> listaAux=new ArrayList<Comprobante>();
				 listaAux.add(c);
				 
				 if(listaAux!=null && !listaAux.isEmpty()) {
			    	  ClienteProveedoresLPA.getComprobantes(listaAux);
			    	  ClienteProveedoresLPA.getAdjuntos(listaAux);
			     }
				 
				 List<Comprobante> lista = (ArrayList<Comprobante>)session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT);
				 
				  String idFacturaImg="";
			      for(Comprobante x:lista) {
			    	  if(x.getImagenes()==null || x.getImagenes().size()==0 ) {
						idFacturaImg = x.getAcreedorEmpresa().getCuit()+"-"+x.getTipoComprobante()+"-"+
								x.getLetraComprobante()+String.format("%05d",x.getPtoVenta())+x.getNroComprobante();
						List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
						x.setImagenes(new ArrayList<DLFileEntryImpl>());
						for(DLFileEntryImpl d :list){
							if(!d.getTitle().contains("-Recibo")) {
								x.getImagenes().add(d);
							}else {
								x.setImagenRecibo(d);
							}
						}
			    	  }	
				  }
			      session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT,lista);
	           	} 
	           	return  mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result"));
			}
			
			if(cmd.equals("recuperarrecibo") ){
	           	if(idComprobante>0) {
				 String desdeResult = ParamUtil.get(renderRequest, "desde_result", "");
				 String cuit = ParamUtil.get(renderRequest, "cuit", "");
				 String tipo = ParamUtil.get(renderRequest, "tipo", "");
				 String letra = ParamUtil.get(renderRequest, "letraComprobante", "");
				 String ptovta = ParamUtil.get(renderRequest, "ptovta", "");
				 String nro = ParamUtil.get(renderRequest, "nro", "");
				
				 
				 session.setAttribute("esPopUp","N");
				 session.setAttribute("desde_result",desdeResult);
				 Comprobante c =new Comprobante();
				 Empresa e = new Empresa();
				 e.setCuit(cuit);
				 c.setAcreedorEmpresa(e);
				 c.setCuit(cuit);
				 c.setTipoComprobante(tipo);
				 c.setLetraComprobante(letra);
				 c.setPtoVenta(Integer.parseInt(ptovta));
				 c.setNroComprobante(nro);
				 c.setId(idComprobante);
				 
				 List<Comprobante> listaAux=new ArrayList<Comprobante>();
				 listaAux.add(c);
				 
				 if(listaAux!=null && !listaAux.isEmpty()) {
			    	  ClienteProveedoresLPA.getComprobanteRecibo(listaAux);
			     }
				 
				 List<Comprobante> lista = (ArrayList<Comprobante>)session.getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT);
				 
				  String idFacturaImg="";
			      for(Comprobante x:lista) {
			    	  if(x.getImagenRecibo()==null ) {
						idFacturaImg = x.getAcreedorEmpresa().getCuit()+"-"+x.getTipoComprobante()+"-"+
								x.getLetraComprobante()+String.format("%05d",x.getPtoVenta())+x.getNroComprobante();
						List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
						x.setImagenes(new ArrayList<DLFileEntryImpl>());
						for(DLFileEntryImpl d :list){
							if(!d.getTitle().contains("-Recibo")) {
								x.getImagenes().add(d);
							}else {
								x.setImagenRecibo(d);
							}
						}
			    	  }	
				  }
			      session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT,lista);
	           	} 
	           	return  mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobantes_search_result"));
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
				 c.setCuit(cuit);
				 Empresa emp = new Empresa(cuit, null,null);
				 c.setAcreedorEmpresa(emp);
				 c.setTipoComprobante(tipo);
				 c.setLetraComprobante(letra);
				 c.setPtoVenta(Integer.parseInt(ptovta));
				 c.setNroComprobante(nro);
				 List<Comprobante> lista = ComprobanteServiceUtil.getLista(c,0);
				 session.setAttribute(WebKeysComprobantes.COMPROBANTE_PROVEEDOR_EN_EDICION, lista.get(0));
				 return mapping.findForward(getForward(renderRequest,
							"portlet.comprobantes.comprobante_editar"));
			}
           
			if(cmd.equals(Constants.UPDATE) ){
				
				comprobante=(Comprobante)  session.getAttribute(WebKeysComprobantes.COMPROBANTE_PROVEEDOR_EN_EDICION);
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
				List<Comprobante> lista = ComprobanteServiceUtil.getLista(c,0);
				session.setAttribute(WebKeysComprobantes.COMPROBANTE_PROVEEDOR_EN_EDICION, lista.get(0));
				
				
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.comprobantes.comprobante_editar"));
				
			}
		}
		renderRequest.setAttribute("tab", tabSel);
		return mapping.findForward("portlet.comprobantes.comprobantes_proveedores");
		
	}
	
	
	
	
private void filterComprobantes(RenderRequest renderRequest,HttpSession session,String entidad) throws Exception{
	
	
	Integer pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
	String tipoC = ParamUtil.getString(renderRequest, "tipo_comprobante");
	String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
	String letra = ParamUtil.getString(renderRequest, "letra", " ");
	
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
	
	int pagina =ParamUtil.getInteger(renderRequest, "pagina",1);
	
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
		
	List<Comprobante> lista = ComprobanteServiceUtil.getLista(filtro,pagina);
	
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_FILTRO ,filtro);
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT,lista);
	
	//Seteos Paginadaor
	Integer tRegistros=0;
	if(lista!=null && !lista.isEmpty()) {
		 tRegistros=lista.get(0).getTotalRegistros();
	}	

	session.removeAttribute(WebKeysComprobantes.COMPROBANTES_PROVEEDORES_TOTAL_REGISTROS);
    session.removeAttribute( WebKeysComprobantes.COMPROBANTES_PROVEDORES_OFFSET_REG);
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_PROVEEDORES_TOTAL_REGISTROS, tRegistros );
	session.setAttribute(WebKeysComprobantes.COMPROBANTES_PROVEDORES_OFFSET_REG, pagina);
	
}
	
	
	

private void actualizaComprobante(Comprobante comprobante,HttpServletRequest renderRequest) throws SystemException{

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
	String observaciones= renderRequest.getParameter("observaciones");
	String comentario= renderRequest.getParameter("comentario");
	
	
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
	importe=importe.replace(".", "").replace(",", ".");
	comprobante.setImporteComprobante(BigDecimal.valueOf(Double.valueOf(importe)));
	
}


private long updateComprobante(Comprobante comprobante, String user) throws Exception{
	long id = 0;
	
	id = ComprobanteServiceUtil.updateComprobante(comprobante, user);
	return id;
}
	
	
		
	
}