<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

Seccional seccional = (Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
List<Delegacion>  pcuentas= new ArrayList<Delegacion>();

if(seccional.getDelegaciones() !=null && seccional.getDelegaciones().size()>0 ){
	pcuentas=seccional.getDelegaciones();
}

boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);

List<String> headerNames = new ArrayList<String>();
headerNames.add("ID. Delegación");
headerNames.add("Descripción Delegación");
headerNames.add("Rúbrica");
headerNames.add("Libro");
headerNames.add("Tomo");
if(rolABMSeccionales){
    headerNames.add("Eliminar");
}else{
	headerNames.add("");
}


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "delegación-no-encontrado"));

					
if (pcuentas != null && !pcuentas.isEmpty()){
	int total = pcuentas.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < pcuentas.size(); i++) {	    
		
		Delegacion liq = (Delegacion) pcuentas.get(i);
		
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText( String.valueOf(liq.getId()));
		row.addText(liq.getDescripcion());
		row.addText(String.valueOf(liq.getRubrica()));
		row.addText(String.valueOf(liq.getLibro()));
		row.addText(String.valueOf(liq.getTomo()));
		
		
		StringBuilder sb= new StringBuilder();
		if(rolABMSeccionales){	
		  sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		  sb.append(themeDisplay.getPathThemeImages());
		  sb.append("/common/delete.png\" onClick=\"javascript:borraDelegacionAsociada('");
		  sb.append(liq.getId());
		  sb.append("');\" />");
		}else{
			sb.append("");		
		}
		
		row.addText(sb.toString());
		
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

