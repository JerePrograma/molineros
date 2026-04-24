<%@ include file="/html/portlet/hoteles/init.jsp"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Mesa mesa=(Mesa)request.getSession().getAttribute(WebKeysHoteles.MESA_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	Integer id_mesa=mesa!=null?mesa.getNumero():0;
	if(mesa==null){
		mesa= new Mesa();
	} 
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
		
    <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Mesa</legend>
		
		<table class="lfr-table">
		   <tr>
			    <td>Número:</td> 
			    <td><input id="<portlet:namespace />nro"
					name="<portlet:namespace />nro" size="5"
					maxlength="2" type="text" tabindex="1" <%=!esEdicion ?"readonly=\"readonly\"":"" %>
					value='<%=mesa.getNumero()==0?"":mesa.getNumero()%>' />
				</td>
				<td>Descripción:</td> 
			    <td><input id="<portlet:namespace />descripcion"
					name="<portlet:namespace />descripcion" size="20"
					maxlength="50" type="text" tabindex="2" re
					value="<%=mesa.getDescripcion()==null?"":mesa.getDescripcion() %>" />
				</td>
				
				<td>Grupo:</td> 
			    <td><input id="<portlet:namespace />grupo"
					name="<portlet:namespace />grupo" size="10"
					maxlength="50" type="text"  tabindex="3"
					value="<%=mesa.getGrupo()==null?"":mesa.getGrupo() %>" />
				</td>
				
			</tr>
						                
	    </table>


		
		
	</fieldset>
	
	<br>
	<input type="hidden" name="<portlet:namespace />id_mesa"
		id="<portlet:namespace />id_mesa" value="<%=id_mesa%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 

    <table>
	 <tr>
	  <td>


      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" tabindex="4"
	  />
		 
	</td>
	</tr>
	</table>      
   <input type="hidden" value="" name="view" id="view" />
   
</form>

<script type="text/javascript">

function <portlet:namespace />validarCampos(){
	var result = true;
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_mesas_abm" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}




</script>

