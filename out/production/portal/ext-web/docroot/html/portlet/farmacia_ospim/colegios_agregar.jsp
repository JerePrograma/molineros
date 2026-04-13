<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%

PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia_ospim";
	}
%>

<form action="" method="post" name="<portlet:namespace />fmSB">

<liferay-ui:error key="avisoPatologiaExistente"
		message="<%=(String)request.getAttribute(\"msgPatError\") %>" />
<liferay-ui:success key="updatePatOk"
		message="<%=(String)request.getAttribute(\"msgPatOk\")  %>" />
			
					
<table class="lfr-table">	
	<tr>
	     <td>Colegio </td>
		  
		  <td colspan="5">
		 
		 <input id="<portlet:namespace />descripcionColegio"
								       name="<portlet:namespace />descripcionColegio" size="35"
								       maxlength="200" type="text" />		   
		 </td>  
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<input id="<portlet:namespace />save"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />agregarColegio();"
				type="button" /> 
				
</form>

<script type="text/javascript">
function <portlet:namespace />agregarColegio(){
	  var vapertura;
	  var params = "&<%= Constants.CMD %>=" + "agregarColegio";
	 
	  var descripcion=jQuery('#<portlet:namespace />descripcionColegio').val();
	  
	  params += "&descripcion="+encodeURI(descripcion);
	  url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmaciaospim/actualiza_colegiofarmacia';
	  url = url + params;

      jQuery.ajax({   
	   url: url,
	   async:false,
	   success: function(data){
		var obj = jQuery.parseJSON(data);
		var idColegio  = obj.id;
		var deColegio  = obj.descripcion;
		
		<portlet:namespace />cerrarColegioNuevo(idColegio,deColegio);
		
	   }});
 	  return false;		
}

</script>

