<%@ include file="/html/portlet/appmobile/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PreAutorizacion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamoPrestacional" %>
<%@ page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl" %>
<%@ page import="ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil" %>
<%@ page import="ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "appmobile";
}
NumberFormat format2D = new DecimalFormat("#0.00");

%>
<portlet:defineObjects/>
			<%
			List<Comprobante> comprobantesProcesados = (ArrayList<Comprobante>)renderRequest.getPortletSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS);
			List<Comprobante> comprobantesErroneos = (ArrayList<Comprobante>)renderRequest.getPortletSession().getAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS);

 					
 					if (comprobantesProcesados == null) comprobantesProcesados = new ArrayList<Comprobante>();
 					if (comprobantesErroneos == null) comprobantesErroneos = new ArrayList<Comprobante>();
 					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Cuil");
			 		headerNames.add("apellido-y-nombre");
			 		headerNames.add("comprobante-tipo");			 	
			 		headerNames.add("<span title='Fecha en la que se dio de alta en molineros'>Fecha Recibido</span>");
			 		headerNames.add("<span title='Número de preautorización/reintegro'>Número</span>");
			 		headerNames.add("estado");	
			 		headerNames.add("<span title='Muestra si las imágenes fueron procesadas'>Acción</span>");
			 		
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
										
					List resultRows = searchContainer.getResultRows();

					for (int i = 0; i < comprobantesProcesados.size(); i++) {
					    Comprobante comp = comprobantesProcesados.get(i);
					    ResultRow row = new ResultRow(comp, comp.hashCode(), i);
					    row.addText(comp.getAcreedorEmpresa().getCuit());
					    row.addText(comp.getAfiliado().getApellido()  +" "+ comp.getAfiliado().getNombre());
					    row.addText(comp.getTipoComprobante());
					    //row.addText(comp.getAltaFechaAsString());
					    
					    if (comp.getReintegro() != null || comp.getIdReintegro() > 0) {
							 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							    ReclamoPrestacional rp = comp.getReintegro();

							    row.addText(rp != null && rp.getAlta_fecha() != null ? sdf.format(rp.getAlta_fecha()) : "-");
							    row.addText(rp != null ? String.valueOf(rp.getId_reclamo()) : String.valueOf(comp.getIdReintegro()));
							    
						}else{
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						    PreAutorizacion pre = null;
						    try {
						        pre = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(comp.getIdPreautorizacion());
						    } catch (Exception ignore) {}

						    row.addText(pre != null && pre.getAlta_fecha() != null ? sdf.format(pre.getAlta_fecha()) : "-");
						    row.addText(String.valueOf(comp.getIdPreautorizacion()));
						 }
					    
					    row.addText(comp.getEstado());
					    resultRows.add(row);
					}
					 
					
					 SearchContainer searchContainerErr = new SearchContainer(renderRequest, null, null,
							SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
							LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
						
					 int total = comprobantesProcesados.size();
					 searchContainer.setTotal(total);
					 	
					 List resultRowsErr = searchContainerErr.getResultRows();
					 
					 int totalEr = comprobantesErroneos.size();
					 searchContainerErr.setTotal(totalEr);

					 for (int i = 0; i < comprobantesErroneos.size(); i++) {
					     Comprobante comp = comprobantesErroneos.get(i);
					     ResultRow row = new ResultRow(comp, comp.hashCode(), i);
					     row.addText(comp.getAcreedorEmpresa().getCuit());
					     row.addText(comp.getAfiliado().getApellido()  + " "+ comp.getAfiliado().getNombre());
					     row.addText(comp.getTipoComprobante());
					     //row.addText(comp.getAltaFechaAsString());
					     
						 if (comp.getReintegro() != null || comp.getIdReintegro() > 0) {
							 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							    ReclamoPrestacional rp = comp.getReintegro();

							    row.addText(rp != null && rp.getAlta_fecha() != null ? sdf.format(rp.getAlta_fecha()) : "-");
							    row.addText(rp != null ? String.valueOf(rp.getId_reclamo()) : String.valueOf(comp.getIdReintegro()));
							    
						}else{
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						    PreAutorizacion pre = null;
						    try {
						        pre = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(comp.getIdPreautorizacion());
						    } catch (Exception ignore) {}

						    row.addText(pre != null && pre.getAlta_fecha() != null ? sdf.format(pre.getAlta_fecha()) : "-");
						    row.addText(String.valueOf(comp.getIdPreautorizacion()));
						 }
						 
					     row.addText(comp.getEstado());
					     
					     String titulo = "";
					     String tipo = "";
					     String iconoId = "";
					     boolean yaProcesado = false;

					     if (comp.getReintegro() != null) {
					         int idReintegro = comp.getReintegro().getId_reclamo();
					         titulo = "REINTEGRO_" + idReintegro;
					         tipo = "rei";
					         iconoId = renderResponse.getNamespace() + "iconoProcesarReintegro_" + idReintegro;

					         List<DLFileEntryImpl> imgs = ReclamoPrestacionServiceImpl.getImagenesReintegro(titulo);
					         yaProcesado = (imgs != null && !imgs.isEmpty());

					     } else {
					         int idPre = comp.getIdPreautorizacion();
					         titulo = "PREAUT_" + idPre;
					         tipo = "pre";
					         iconoId = renderResponse.getNamespace() + "iconoProcesar_" + idPre;

					         List<DLFileEntryImpl> imagenes = PreAutorizacionServiceUtil.getImagenesPreautorizacion(titulo);
					         yaProcesado = (imagenes != null && !imagenes.isEmpty());
					     }

					     StringBuilder sb = new StringBuilder();

					     if (yaProcesado) {
					         sb.append("<img id='").append(iconoId).append("' alt='Imágenes procesadas' ");
					         sb.append("title='Imágenes procesadas' ");
					         sb.append("src='").append(themeDisplay.getPathThemeImages()).append("/common/checked.png' ");
					         sb.append("style='cursor:default;' />");
					     } else {
					         sb.append("<img id='").append(iconoId).append("' alt='Procesar imágenes' ");
					         sb.append("title='Procesar imágenes' ");
					         sb.append("src='").append(themeDisplay.getPathThemeImages()).append("/common/action_right.png' ");
					         sb.append("style='cursor:pointer;' ");
					         sb.append("onclick=\"javascript:").append(renderResponse.getNamespace())
					           .append("descargarImagenes('").append(titulo).append("', '").append(tipo).append("');\"/>");
					     }

					     row.addText(sb.toString());

					     
					     resultRowsErr.add(row);
					 }
					%>
					
			<fieldset>
             <legend>Comprobantes Procesados</legend>
			  <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
			</fieldset>  
			
			<fieldset>
               <legend>Comprobantes NO Procesados</legend>
			   <liferay-ui:search-iterator searchContainer="<%= searchContainerErr %>" />
			</fieldset>   	

<script type="text/javascript">
function <portlet:namespace />descargarImagenes(titulo, tipo) {
	var iconoId = "";

	if (tipo === "pre") {
		iconoId = '#<portlet:namespace />iconoProcesar_' + titulo.replace("PREAUT_", "");
	} else if (tipo === "rei") {
		iconoId = '#<portlet:namespace />iconoProcesarReintegro_' + titulo.replace("REINTEGRO_", "");
	}

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
			  '&struts_action=/<%= portlet_name %>/administracion_appmobile' +
			  '&cmd=descargarImagenes' +
			  '&tipo=' + tipo +
			  '&titulo=' + titulo +
			  '&rnd=' + Math.random();

	jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		jQuery(iconoId)
			.attr('src', '/html/themes/classic/images/common/checked.png')
			.attr('alt', 'Imágenes procesadas')
			.attr('title', 'Imágenes procesadas') 
			.css('cursor', 'default')
			.off('click'); 

		alert('Imágenes procesadas correctamente.');
	});
}
</script>
