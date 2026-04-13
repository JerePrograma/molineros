<%@ include file="/html/portlet/crm/init.jsp"%>
<portlet:defineObjects />
<%
String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}
String accion = Constants.VIEW; 

/* DocumentoLegalCRM docLegal = (DocumentoLegalCRM)session.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW); */
DocumentoLegalCRM docLegal = (DocumentoLegalCRM)request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);	
int inte = 0;
if (docLegal!=null && docLegal.getAfiliado()!=null) {
	inte = docLegal.getAfiliado().getInte();	
}
String desdePopup = (String) request.getAttribute("desdePopup");

String tabsA = ParamUtil.getString(request, "tabs1", "");

if(tabsA.equals("")){
	tabsA = (String)request.getAttribute("tabs1");
}

if((tabsA == null) || (tabsA != null && tabsA.equals(""))) {
	tabsA = "informacion_general";
}

StringBuilder tabsAValues = new StringBuilder("informacion_general");

if(desdePopup==null){
	tabsAValues.append(",imagenes_afiliados");
} 

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/afiliados/editar_crm_legales_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("cmd", accion);
portletURL.setParameter("id", String.valueOf(docLegal.getId()));

/* if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null")){
	portletURL.setParameter("tabs_a_mostrar", tabsAMostrar);
} */

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH);
/* if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null") ){
	tabsANames = tabsAMostrar;
	tabsAValues= new StringBuilder(tabsAMostrar);
} */

String portlet_name = "afiliados";
%>

	<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 
		

	<c:choose>
		<c:when test='<%= tabsA.equals("informacion_general")%>'>
			<liferay-util:include page="/html/portlet/crm/editar_docum_legal.jsp" >		
			</liferay-util:include>		
		</c:when>

		<c:when test='<%=tabsA.equals("imagenes_afiliados")%>'>
	        <liferay-util:include page="/html/portlet/crm/doc_legal_imagenes.jsp">
	        </liferay-util:include>
	    </c:when>	
	</c:choose>

