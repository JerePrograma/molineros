<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<%@ page import="ar.com.ospim.tesoreria.beans.PrecioPlanSuperador" %>
<%@ page import="ar.com.ospim.tesoreria.service.LiquidacionPlanesSuperadoresServiceUtil" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects/>
			<%
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			
			DecimalFormat df = new DecimalFormat("#,##0.00");
			String portlet_name = ParamUtil.getString(request, "portlet_name");
			Integer entidad = WebKeysGlobal.OSPIM;
			
			List<Afiliado> grupo = (List<Afiliado>) request.getAttribute("grupoFamiliar");
			String planWeb  = ParamUtil.getString(request, "plan", null);
			Integer provinciaId  = ParamUtil.getInteger(request, "provincia");
			String fecha  = ParamUtil.getString(request, "fecha", null);
			
			
			
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			
			Map<String,Integer> planesEquivalencias=LiquidacionPlanesSuperadoresServiceUtil.getPlanesEquivalentes();
			List<String> parentescosACotizar=new ArrayList<String>();
			
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
		    
			PortletURL portletURLPreAutMed = renderResponse.createRenderURL();
	 		List<String> headerNamesPreAutMed = new ArrayList<String>();
	 		headerNamesPreAutMed.add("Inte");
	 		headerNamesPreAutMed.add("Nombre");
	 		headerNamesPreAutMed.add("Parentesco");
	 		headerNamesPreAutMed.add("Edad");
	 		SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLPreAutMed, headerNamesPreAutMed,
			LanguageUtil.get(pageContext, "no hay valores cargados"));
			if(null!=grupo){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < grupo.size(); i++) {
 			 		Afiliado h = grupo.get(i);
	 					ResultRow row = new ResultRow(h, h.getInte() , i);
	 					row.addText(String.valueOf(h.getInte()));
	 				    row.addText(h.getApellidoNombre());
	 				    row.addText(h.getParentesco());
	 				    row.addText(String.valueOf(h.getEdad()));
	 				    parentescosACotizar.add(String.valueOf(h.getId_parentesco()) +"-"+h.getEdad());
	 					resultRowsInspector.add(row);
 			 		}
	 		}
			
			Integer planMolinero =planesEquivalencias.get(planWeb);
			Double importeGrupo=0D;
			/*
			List<PrecioPlanSuperador> precios =LiquidacionPlanesSuperadoresServiceUtil.cotizar(planMolinero, provinciaId,
					   formato.parse(fecha) , parentescosACotizar.toArray(new String[parentescosACotizar.size()]));
			*/
			
			//Nuevo
			List<PrecioPlanSuperador> precios=(List<PrecioPlanSuperador>)request.getSession().getAttribute(WebKeysTesoreria.PRECIOS_COTIZACION_RESULT);
			if(precios==null || precios.size()==0){
			   precios =LiquidacionPlanesSuperadoresServiceUtil.cotizar(planMolinero, provinciaId,
					   formato.parse(fecha) , parentescosACotizar.toArray(new String[parentescosACotizar.size()]));
			}   
			//Fin nuevo
			request.getSession().setAttribute(WebKeysTesoreria.PRECIOS_COTIZACION_RESULT,precios);
			
			List<AjustePlanSuperador> ajustes =LiquidacionPlanesSuperadoresServiceUtil.getAjustesPersonalizables(planMolinero, provinciaId,
					   formato.parse(fecha) , parentescosACotizar.toArray(new String[parentescosACotizar.size()]));
			request.getSession().setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_RESULT,ajustes);
			
			PortletURL portletURLCot = renderResponse.createRenderURL();
	 		List<String> headerNamesCot = new ArrayList<String>();
	 		headerNamesCot.add("Descripción");
	 		headerNamesCot.add("Valor");
	 		headerNamesCot.add("Ajuste");
	 		headerNamesCot.add("Importe Final");
	 		SearchContainer searchContainerCot= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLPreAutMed, headerNamesCot,
			LanguageUtil.get(pageContext, "no hay valores cargados"));
			if(null!=precios && !precios.isEmpty()){
				List resultRowsPrecios = searchContainerCot.getResultRows();
 			 	for (int i = 0; i < grupo.size(); i++) {
 			 		PrecioPlanSuperador h = precios.get(i);
 			 		importeGrupo+=h.getImporteNeto();
	 					ResultRow row = new ResultRow(h, h.getEdadDesde() , i);
	 					row.addText(h.getDescripcion());
	 					row.addText("right", "middle", df.format(h.getImporteBruto()));
	 			        row.addText("right", "middle", df.format(h.getAjuste()));
	 			        row.addText("right", "middle", df.format(h.getImporteNeto()));    
	 					resultRowsPrecios.add(row);
 			 		}
			}
			
			request.getSession().setAttribute(WebKeysTesoreria.PRECIO_COTIZACION,importeGrupo);
 		%>
 		
 	 
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	<table style="width: 100%; table-layout: fixed; border-collapse: collapse;">
     <tr>
      <td style="width: 45%; vertical-align: top;">   
	     <liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	  </td>
	   <td style="width: 10%; vertical-align: top;"> </td>
	  
	  <td style="width: 45%; vertical-align: top;">   
	       <liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainerCot%>" />
	  </td>
	 </tr>
	 <tfoot>
	 <tr>
	 <td></td>
	 <td> </td>
	 <td style="font-weight: bold; background-color: #f0f0f0; border-top: 2px solid #ccc;text-align:right;color:blue;font-size: 15px;" >
	    <label>Total del Grupo Familiar:  <%=df.format(importeGrupo) %></label> 
	 </td>
	 </tr>
	 </tfoot>
	</table>
	
	<fieldset class="block-labels">
	  <legend>Ajustes</legend>
	 <div id="<portlet:namespace />divAjustes">
	    <liferay-util:include page='/html/portlet/afiliados/formulario_cotizar_ajustes.jsp'> 
	    <liferay-util:param value="<%= df.format(importeGrupo)%>"  name="importeCotizado" />
	    <liferay-util:param value="<%=fecha%>"  name="fechaCotizado" />
	    </liferay-util:include>
	 </div>
	</fieldset>
	
	    
    <input type="hidden" name="<portlet:namespace />q_precio"
		id="<portlet:namespace />q_precio" value="<%=grupo.size()%>" />
		
	<input type="hidden" name="<portlet:namespace />i_precio"
		id="<portlet:namespace />i_precio" value="<%=importeGrupo%>" />	

<script type="text/javascript">
function <portlet:namespace />seleccionarAjuste(){
	var params = jQuery("#<portlet:namespace />ajustes_disponibles").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/solicitud_afiliacion'
		+	'&<%= Constants.CMD%>=' + 'seleccionarAjuste'
		+ '&tabs1=seguimiento-formulario'
		+ '&ajustesid=' + encodeURI(params); 	
		jQuery('#<portlet:namespace/>divAjustes').load(url, function() {});
	
    return false;	
	
}	
</script>