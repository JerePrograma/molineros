<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

CajaChica cajaChica= (CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
List<User>  usuarios= new ArrayList<User>();

if(cajaChica.getUsuariosHabilitados()!=null && cajaChica.getUsuariosHabilitados().size()>0 ){
	usuarios=cajaChica.getUsuariosHabilitados();
}

List<String> headerNames = new ArrayList<String>();
headerNames.add("Usuario");
headerNames.add("Eliminar");


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "usuario-no-encontrado"));

					
if (usuarios != null && !usuarios.isEmpty()){
	int total = usuarios.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < usuarios.size(); i++) {	    
		User liq = (User) usuarios.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText(liq.getFullName());
		
		
		StringBuilder sb= new StringBuilder();
			
		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/delete.png\" onClick=\"javascript:borraCajaChicaUsuario('");
		sb.append(liq.getUserId());
		sb.append("');\" />");
		row.addText(sb.toString());
		
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

