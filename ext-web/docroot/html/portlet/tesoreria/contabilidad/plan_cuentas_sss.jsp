<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="ar.com.ospim.global.beans.PlanCuentasSSS"%>
<% 	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	} 
	
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	//String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);

%>

<portlet:defineObjects />

<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.ConceptoUtilizadoException.class %>" message="cuenta-utilizada" />

<form action="" method="POST" id="busqueda_cuentas"
	name="busqueda_cuentas">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Plan Cuentas - SSS</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
		  <td>
		   N&uacute;mero:&nbsp;<input type="text" id="numero" name="numero" value="" size="10" /> &nbsp; Descripci&oacute;n:&nbsp;
				<input type="text" id="descripcion" name="descripcion" value="" size="50" />
		  </td>		
        </tr> 				
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" onclick="buscarCuentas()" value="Buscar" />&nbsp;
				<%if (rolABMEquivalencias){ %>
				<input type="button" onclick="altaCuenta()" value="Alta Cuenta" />
				<%} %>
		</tr>
	</table>
</form>
<hr />
<br />
<%
		
		List<PlanCuentasSSS> cuentas = (List<PlanCuentasSSS>) request.getAttribute("planCuentasSSS");
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("N&uacute;mero" );
 		headerNames.add("Cuenta");
 		headerNames.add("Tipo");
 		 if (rolABMEquivalencias && !portlet_name.equalsIgnoreCase("farmacia")) {
 	 		headerNames.add("Editar");
 	 		headerNames.add("Eliminar");
 	 		 }
 		 if (rolABMEquivalencias && portlet_name.equalsIgnoreCase("farmacia")) {
  	 		headerNames.add("Editar");
  	 		/* headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>"); */
  	 	 }
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-cuentas-were-found"));
	
		if (cuentas != null){ 
		 	int total = 1;
			searchContainer.setTotal(total);
				List resultRows = searchContainer.getResultRows();
	
			int i = 0; 
			for (PlanCuentasSSS cuenta: cuentas){
				i++;
				ResultRow row = new ResultRow(i, i, i);
				row.addText(cuenta.getNumero());
				row.addText(cuenta.getCuenta());
				row.addText(cuenta.getTipo() );
				if (rolABMEquivalencias ) {
					row.addText("<a href='javascript:void(0)' onclick=\"editarCuenta(" + cuenta.getId()+")\">Editar</a>");
					row.addText("<a href='javascript:void(0)' onclick=\"eliminarCuenta(" + cuenta.getId() +")\">Eliminar</a>");
				}
				resultRows.add(row);
			}
		}
		
%>


<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />


<script type="text/javascript">
function buscarCuentas(){
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/plan_cuentas_sss';
	submitForm(document.busqueda_cuentas, url);
}
function altaCuenta(){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta_sss';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function editarCuenta(id){
    <%if(portlet_name.equals("farmacia")){%>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta_sss'
	+  '&id=' +id + '&<%=Constants.CMD%>=<%=Constants.SEARCH%>';
	<% }else{ %>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta_sss'
		+  '&id=' +id;
	<%}%>
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function eliminarCuenta(id){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta_sss'
	+  '&id=' +id + '&<%=Constants.CMD%>=<%=Constants.DELETE%>';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
</script>
