<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
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
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS)|| portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
	
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	String ejDesde = (String) request.getAttribute("ejercicio_desde");
	String ejHasta = (String) request.getAttribute("ejercicio_hasta");
	Calendar  desde = null;
	Calendar  hasta = null;
	if (ejDesde !=null){
		desde = Calendar.getInstance();
		desde.setTime(format.parse(ejDesde));
		hasta = Calendar.getInstance();
		hasta.setTime(format.parse(ejHasta));
	}
%>
<form action="" method="post" name="<portlet:namespace />editar_concepto_plan" >
<input type="hidden" name="id" value="${concepto.id}"/>
<input type="hidden" name="ejercicio_desde_original" value="${ejercicio_desde_original}"/>
<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
<table style="width: 50%">
	<tr>
		<td><b>Ejercicio:</b></td>
		<td>
				<select name="ejercicio_desde">
					<% while (DateUtils.compararFechasTruncarEnDia(desde.getTime(), hasta.getTime()) <= 0){%>
						<option value="01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%>">01/<%=desde.get(Calendar.MONTH)+1%>/<%=desde.get(Calendar.YEAR)%></option>
					<%	desde.add(Calendar.MONTH, 1);
						}
					%>
				</select><b>&nbsp;-&nbsp;${ejercicio_hasta}</b></td>
	</tr>	
	<tr>
		<td>Parametro:</td>
		<td>${parametroConcepto.parametro}
		<input type="hidden" name="parametro" value="${parametroConcepto.parametro}"/>
		</td>
	</tr>
	<tr>
		<td>Detalle:</td>
		<td>${parametroConcepto.observaciones}
			<input type="hidden" name="observaciones" value="${parametroConcepto.observaciones}"/>
		</td>
	</tr>
	<tr>
		<td>Concepto:&nbsp;</td>
		<td>
			<select name="conceptoId" id="conceptoId">
				<c:forEach items="${conceptos}" var="con">
					<option value="${con.id}"/><c:out value="${con.descripcion}"/></option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="2"><span id="botonGuardar"><input type="button" value="Guardar" onclick="guargar()"/></span></td>
	</tr>
</table>
</form>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>

<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
	<%if(portlet_name.equals("farmacia")){%>
		<portlet:param name="struts_action" value="/farmacia/parametros_especiales" />
	<%}else if(portlet_name.equals("uoma")){%>	
		<portlet:param name="struts_action" value="/uoma/parametros_especiales" />
	<%}else{%>
		<portlet:param name="struts_action" value="/tesoreria/parametros_especiales" />
	<%}%>
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a></p>

<script type="text/javascript">
function guargar(){
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_parametro_especial';		
		document.<portlet:namespace />editar_concepto_plan.method = 'post';
		submitForm(document.<portlet:namespace />editar_concepto_plan, url);
}

	jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery("#conceptoId").val("${parametroConcepto.conceptoId}");	
	});
	
</script>

