<%@ include file="/html/portlet/crm/init.jsp" %>
	
	
<%
    String cmd = (String) request.getAttribute(Constants.CMD);
    
	//obtengo lista del request	
	List<ContactoCRM> contactos = null;
	contactos = (List<ContactoCRM>) session.getAttribute(WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	int total = 0;
	int cantcontactos=0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("N° contacto");
	headerNames.add("Fecha");
	headerNames.add("Tipo");
	headerNames.add("Categoría");
	headerNames.add("Motivo");
	headerNames.add("Estado");
	headerNames.add("Usuario Alta");
	headerNames.add("Ver");
	headerNames.add("Asociado" );	
	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-contactos-crm-were-found"));	 	
	//recupero coincidencias		
	if (null != contactos && contactos.size() > 0) {
		total = contactos.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < contactos.size(); i++) {
			ContactoCRM c = (ContactoCRM) contactos.get(i);
			ResultRow row = new ResultRow(c, String.valueOf(c.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/editar_contacto_entry");
 			rowURL.setParameter("id_serialcontacto", String.valueOf(c.getId()));
 			rowURL.setParameter("view", "true"); 
			row.addText(String.valueOf(c.getIdContacto())); 
			row.addText(sdf.format(c.getAltaFecha()));
			row.addText(c.getTipo().getDescripcion()); 
			row.addText(c.getCategoria().getDescripcion());
			row.addText(c.getMotivo().getDescripcion());
			row.addText(c.getEstado().name());
			row.addText(c.getAltaUsr());
			
			StringBuilder sb= new StringBuilder();		
			sb.append("&nbsp;<img alt=\"<liferay-ui:message key='ver-contacto'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/view.png\" onClick=\"javascript:verCrmContacto('");
			sb.append(c.getId());
			sb.append("');\" />");
			row.addText(sb.toString());
			StringBuilder sb1= new StringBuilder();									
			sb1.append("<input type=\"checkbox\"");
			sb1.append("name=\"");
			sb1.append("contactorec" + c.getIdContacto() );
			if(c.getIdCrmReclamoPrestacional()>0 ){
					sb1.append("\" checked=\"checked");
					cantcontactos++;
			}
			
			if  (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW))				
			{
				sb1.append("\" disabled=\"disabled");				
			}
			sb1.append("\" id=\"");
			sb1.append("contactorec" + c.getIdContacto() );
			sb1.append("\" value=\""); 
			sb1.append(c.getIdContacto());
			sb1.append("\"/>");
			row.addText(sb1.toString());
		
			
			
			resultRows.add(row);
		}
%>

  <div>     	<table>      	<tr>      	<td>
  <br>	
   	<c:choose>
   		<c:when test="<%= total != 1 %>">
   			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>  &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
   			<input type="button" value="Ocultar Contactos Asociados al Caso."  onClick="<portlet:namespace />ocultacontactosdelreclamo();"  id="<portlet:namespace />botonocultacontactosreclamo1" >			
   		</c:when>
   		<c:otherwise>
   			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp				
   			<input type="button" value="Ocultar Contactos Asociados al Caso."  onClick="<portlet:namespace />ocultacontactosdelreclamo();"  id="<portlet:namespace />botonocultacontactosreclamo1" >
   		</c:otherwise>		
   	</c:choose>
<br>   	
<br>   	
   	<legend>
  <span align='left' style="color:white; background-color: gray ; font-size:135%">
		LISTADO DE CONTACTOS CRM DEL AFILIADO DEL CASO 
  </span>
  <br>
</legend>
	
   	</td>    	</tr>     	</table>    </div>
   	
<% if (null != contactos && contactos.size() > 0) {%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<% }  
   	}else{
		%> 
		<div align="center" style="vertical-align: bottom;"  >
		<b> AFILIADO SIN CONTACTOS CRM</b><br><br><br><br><br><br>
		</div> 
		<%}%>
<div>     	<table>      	<tr>      	<td>    

   	<c:choose>
   		<c:when test="<%= total != 1 %>">
   			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>  &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
   			<input type="button" value="Ocultar Contactos Asociados al Caso."  onClick="<portlet:namespace />ocultacontactosdelreclamo();"  id="<portlet:namespace />botonocultacontactosreclamo1" >			
   		</c:when>
   		<c:otherwise>
   			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp				
   			<input type="button" value="Ocultar Contactos Asociados al Caso."  onClick="<portlet:namespace />ocultacontactosdelreclamo();"  id="<portlet:namespace />botonocultacontactosreclamo1" >
   		</c:otherwise>		
   	</c:choose>	

<legend>		
<br>
<br>
<span align='left' style="color:white; background-color: gray ; font-size:135%">
		LISTADO DE CONTACTOS CRM DEL AFILIADO DEL CASO
</span>		<br> 
</legend>
<br>

   	</td>    	</tr>     	</table>    </div>


<% if (null != contactos && contactos.size() > 0) {%>
<script type="text/javascript">
    document.getElementById('CantidadDeContactosAsociados').innerHTML ='<span style="background-color: #d4d9d5	; font-size:135%"  "> (  <%=cantcontactos%>  )</span> Contactos Asociados a este Caso.';
</script>
<%}%>