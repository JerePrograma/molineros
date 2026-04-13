<%@ include file="/html/portlet/hoteles/init.jsp" %>

<%

boolean showAdministracion=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_GESTION_ADMINISTRATIVA);
boolean showConfiteria=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_CONFITERIA);
boolean showPrestamos=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_PRESTAMOS_TURISMO);

String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

if (tabs1 == null || "".equalsIgnoreCase(tabs1) || tabs1.equals("bandeja-de-entrada")
		|| (!"confiteria".equalsIgnoreCase(tabs1) && !"gestion-adm".equalsIgnoreCase(tabs1) &&
			!"prestamos".equalsIgnoreCase(tabs1) && !"mesas".equalsIgnoreCase(tabs1) &&
			!"habitaciones".equalsIgnoreCase(tabs1) && !"categorias".equalsIgnoreCase(tabs1) &&	
			!"personal".equalsIgnoreCase(tabs1) && !"personal-mesas".equalsIgnoreCase(tabs1) && !"productos".equalsIgnoreCase(tabs1)
			&& !"reportes_hoteles".equalsIgnoreCase(tabs1)
				) ){
	if(showConfiteria){
	  tabs1="confiteria";
	}else if(!showConfiteria && showAdministracion){
		tabs1="gestion-adm";	
	}else if(!showConfiteria && !showAdministracion){
		tabs1="prestamos";
	}
}



String tabs1Values ="";

//tabs1Values = "confiteria,mesas,habitaciones,categorias,productos,personal,personal-mesas,gestion-adm";


if (showConfiteria) {
	tabs1Values += "confiteria,mesas,habitaciones,categorias,productos,personal,personal-mesas,reportes_hoteles";
}

if (showAdministracion) {
	if(tabs1Values.length()>0) tabs1Values += ",";
	tabs1Values += "gestion-adm";
}


if (showPrestamos) {
	if(tabs1Values.length()>0) tabs1Values += ",";
	tabs1Values += "prestamos";
}



String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/hoteles/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
	value="<%=tabs1%>"
/>

<c:choose>
	<c:when test='<%= tabs1.equals("confiteria") %>'>
		<liferay-util:include page="/html/portlet/hoteles/confiteria_gestion.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("mesas") %>'>
		<liferay-util:include page="/html/portlet/hoteles/mesas_abm.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("habitaciones") %>'>
		<liferay-util:include page="/html/portlet/hoteles/habitaciones_abm.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("categorias") %>'>
		<liferay-util:include page="/html/portlet/hoteles/categorias_productos_abm.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("productos") %>'>
		<liferay-util:include page="/html/portlet/hoteles/productos_abm.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("personal") %>'>
		<liferay-util:include page="/html/portlet/hoteles/personal_abm.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("personal-mesas") %>'>
		<liferay-util:include page="/html/portlet/hoteles/personal_mesa_edit.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("gestion-adm") %>'>
		<liferay-util:include page="/html/portlet/hoteles/gestion_administrativa.jsp">
		</liferay-util:include>
	</c:when>
	
	<c:when test='<%= tabs1.equals("reportes_hoteles") %>'>
		<liferay-util:include page="/html/portlet/hoteles/reportes/reportes.jsp"/>	
	</c:when>
	
	<c:when test='<%= tabs1.equals("prestamos") %>'>
		<liferay-util:include page="/html/portlet/hoteles/prestamosturismo/prestamos_turismo_list.jsp"/>	
	</c:when>	
	
</c:choose>

</form>

<script type="text/javascript">
	
</script>
