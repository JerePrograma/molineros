<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<portlet:defineObjects/>
<%
List<Concepto> conceptos = (List<Concepto>)portletSession.getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO, PortletSession.APPLICATION_SCOPE);

%>
<select id="<portlet:namespace />otro_concepto" name="<portlet:namespace />otro_concepto" onchange="debeMostrarDetalleAportes(this.value)">
	<option value=""></option>
	<% for (Concepto cc : conceptos) { %>
		<option value="<%=String.valueOf(cc.getId())+(null!=cc.getAnticipoComproNro()?"_"+cc.getAnticipoComproNro()+"_":"")%>"><%= cc.getDescripcion()%></option>
	<% }%>
</select>&nbsp;&nbsp;
				