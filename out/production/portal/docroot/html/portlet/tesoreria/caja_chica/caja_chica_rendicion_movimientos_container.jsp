<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map"%>

<%
	CajaChica cajaChica= (CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
List<ComprobanteCajaChica>  comprobantesAux= cajaChica.getComprobantesEnviadosARendicion();
List<ComprobanteCajaChica>  comprobantes= new ArrayList<ComprobanteCajaChica>();
request.getSession().removeAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION);
request.getSession().removeAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO);

boolean showABMButtonsADM = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA);
boolean showABMButtonsADMSINOP = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA_SIN_OP);

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}


if(comprobantesAux != null && !comprobantesAux.isEmpty()){
	for(ComprobanteCajaChica c:comprobantesAux){
		if(showABMButtonsADMSINOP && !c.getReposicionAprobadaSinOP()) {
				comprobantes.add(c);
			} else if (showABMButtonsADM) {
				comprobantes.add(c);
			}
		}
	}

    Integer entidad = WebKeysGlobal.OSPIM;
    if(renderResponse.getNamespace().equals("_UOM_1_")){
	   entidad = WebKeysGlobal.UOMA;
    }
	Double saldo = cajaChica.getSaldo();
	Map<String, Double> map = new HashMap<String, Double>();

	if (comprobantes != null && !comprobantes.isEmpty()) {
		for (int i = comprobantes.size() - 1; i >= 0; i--) {
			comprobantes.get(i).setImporteComprobanteOriginal(
					BigDecimal.valueOf(saldo));
			
			if(entidad == WebKeysGlobal.OSPIM){
				  saldo += comprobantes.get(i).getImporteComprobante().doubleValue();
			}  
			if(entidad == WebKeysGlobal.UOMA){
					  saldo -= comprobantes.get(i).getImporteComprobante().doubleValue();
			} 
			

			Double total = map.get(comprobantes.get(i).getConceptos()
					.get(0).getConceptoComprobante().getDescripcion());
			if (total == null) {
				map.put(comprobantes.get(i).getConceptos().get(0)
						.getConceptoComprobante().getDescripcion(),
						comprobantes.get(i).getImporteComprobante()
								.doubleValue());
			} else {
				map.put(comprobantes.get(i).getConceptos().get(0)
						.getConceptoComprobante().getDescripcion(),
						comprobantes.get(i).getImporteComprobante()
								.doubleValue()
								+ total);
			}
		}
		request.getSession()
				.setAttribute(
						WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION,
						comprobantes);
		request.getSession()
				.setAttribute(
						WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO,
						map);

	}
%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<form action="" method="post" name="<portlet:namespace />fmCJCHRE">
<fieldset class="block-labels">
		<legend>Movimientos a Aprobar</legend>

		<table class="lfr-table" width="100%" >
			<tr>
			   <td><liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_comprobantes_enviados_a_rendir_result.jsp"></liferay-util:include>
			   </td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
</fieldset>
<fieldset class="block-labels">
		<legend>Agrupados por Concepto</legend>

		<table class="lfr-table" width="70%">
			<tr>
			   <td><liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_ultimos_movimientos_agrupados_result.jsp"></liferay-util:include>
			   </td>
			   <td valign="middle" align="center">
			   
			   <% if(showABMButtonsADM && "tesoreria".equalsIgnoreCase(portlet_name)) { %>		
			      <input id="<portlet:namespace />grabaComprobantesReposicion"
		              value="<liferay-ui:message key="siguiente"/>"
		              title="<liferay-ui:message key="siguiente" />"
		              onClick="javascript: <portlet:namespace />salvarReposicionComprobante();"
		              type="button"/>
		        <%}%>      
		        
		        <% if(showABMButtonsADMSINOP && "tesoreria".equalsIgnoreCase(portlet_name) ) { %>		      
		           <input id="<portlet:namespace />grabaComprobantesReposicionSinOP"
		              value="<liferay-ui:message key="siguienteSinOP"/>"
		              title="<liferay-ui:message key="siguienteSinOP" />"
		              onClick="javascript: <portlet:namespace />salvarReposicionComprobanteSinOP();"
		              type="button"/>    
		        <%}%>
		        
		        <% if(showABMButtonsADM && "uoma".equalsIgnoreCase(portlet_name)) { %>		
			      <input id="<portlet:namespace />grabaComprobantesReposicionUoma"
		              value="<liferay-ui:message key="siguiente"/>"
		              title="<liferay-ui:message key="siguiente" />"
		              onClick="javascript: <portlet:namespace />salvarReposicionComprobanteUoma();"
		              type="button"/>
		        <%}%>
		              
			   </td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
</fieldset>
</form>

<script type="text/javascript">
function <portlet:namespace />salvarReposicionComprobante() {
	var inputs=jQuery('input:checkbox');
	var aprobado="";
	var rechazado="";
	
	for(i=0;i<inputs.length;i++){
		if(inputs[i].checked){
			aprobado += inputs[i].value + ";";
		}else{
			rechazado += inputs[i].value + ";";
		}
	}
	if(rechazado.length>1){
	  var params = "&<%= Constants.CMD %>=" + "rechazareposicion";
	  params+="&rechazados="+encodeURI(rechazado);
	  params+="&aprobados="+encodeURI(aprobado);
	  params+="&id_caja_chica="+<%= cajaChica.getId() %>;
//	  url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
      var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
	  url = url + params;
	  submitForm(document.<portlet:namespace />fmCJCHRE, url);	
				
		
	}else{
		var params = "&<%= Constants.CMD %>=" + "apruebareposicion";
		params+="&aprobados="+encodeURI(aprobado);
		params+="&id_caja_chica="+<%= cajaChica.getId() %>;
//		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCJCHRE, url);	
		
	}
}


function <portlet:namespace />salvarReposicionComprobanteSinOP() {
		var inputs=jQuery('input:checkbox');
		var aprobado="";
		var rechazado="";
		
		for(i=0;i<inputs.length;i++){
			if(inputs[i].checked){
				aprobado += inputs[i].value + ";";
			}else{
				rechazado += inputs[i].value + ";";
			}
		}
		if(rechazado.length>1){
		  var params = "&<%= Constants.CMD %>=" + "rechazareposicion";
		  params+="&rechazados="+encodeURI(rechazado);
		  params+="&aprobados="+encodeURI(aprobado);
		  params+="&id_caja_chica="+<%= cajaChica.getId() %>;
//		  url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
          var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		  url = url + params;
		  submitForm(document.<portlet:namespace />fmCJCHRE, url);	
					
			
		}else{
			var params = "&<%= Constants.CMD %>=" + "apruebareposicionsinop";
			params+="&aprobados="+encodeURI(aprobado);
			params+="&id_caja_chica="+<%= cajaChica.getId() %>;
//			url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
			url = url + params;
			submitForm(document.<portlet:namespace />fmCJCHRE, url);	
			
		}

}	

function <portlet:namespace />salvarReposicionComprobanteUoma() {
	var inputs=jQuery('input:checkbox');
	var aprobado="";
	var rechazado="";
	
	for(i=0;i<inputs.length;i++){
		if(inputs[i].checked){
			aprobado += inputs[i].value + ";";
		}else{
			rechazado += inputs[i].value + ";";
		}
	}
	
	if(aprobado.length>1){
		var params = "&<%= Constants.CMD %>=" + "apruebareposicion";
		params+="&aprobados="+encodeURI(aprobado);
		params+="&id_caja_chica="+<%= cajaChica.getId() %>;
//		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_caja_chica';
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
		submitForm(document.<portlet:namespace />fmCJCHRE, url);	
	}
	
	
}


</script>