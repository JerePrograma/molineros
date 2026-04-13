<%@ include file="/html/portlet/uoma/init.jsp"%>
<portlet:defineObjects/>
<%
Incidente incidente=(Incidente)request.getAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION);
String idIncidente =  incidente!=null ? String.valueOf(incidente.getIdIncidente()):null;
String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}


String tabsA = ParamUtil.getString(request, "tabs1", "");

if (tabsA == null){
	tabsA = (String) request.getAttribute("tabs1");
}

if (tabsA == null){
	tabsA = (String) request.getSession().getAttribute("tabs1"); 
}


if((tabsA == null) || (tabsA != null && tabsA.equals(""))) {
	tabsA = "Incidente";
}else if ("Imagenes".equals(tabsA)){
	tabsA = "Imagenes";
}else if("Incidente".equals(tabsA)){
	tabsA = "Incidente";
}

StringBuilder tabsAValues = new StringBuilder("Incidente");
tabsAValues.append(",Imagenes");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/uoma/editar_incidente_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("id_incidente", String.valueOf(idIncidente));

 
String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH);
if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null") ){
	tabsANames = tabsAMostrar;
	tabsAValues= new StringBuilder(tabsAMostrar);
}
%>
<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 
	<c:choose>
		<c:when test='<%= tabsA.equals("Incidente")%>'>
			<liferay-util:include page="/html/portlet/uoma/editar_incidente.jsp" >
			<liferay-util:param name="id_incidente" value="<%=String.valueOf(idIncidente) %>" />
			</liferay-util:include>	
		</c:when> 
		
		<c:when test='<%= tabsA.equals("Imagenes")%>'>
	        <liferay-util:include page="/html/portlet/uoma/incidente_imagenes.jsp">
	        <liferay-util:param name="id_incidente" value="<%=String.valueOf(idIncidente) %>" />
	   
	        </liferay-util:include>
	    </c:when>	
	</c:choose>

<script type="text/javascript">




</script>
	