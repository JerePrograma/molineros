<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%

PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
%>

<form action="" method="post" name="<portlet:namespace />fmSB">

<liferay-ui:error key="avisoPatologiaExistente"
		message="<%=(String)request.getAttribute(\"msgPatError\") %>" />
<liferay-ui:success key="updatePatOk"
		message="<%=(String)request.getAttribute(\"msgPatOk\")  %>" />
			
					
<table class="lfr-table">	
	<tr>
	     <td>Patología </td>
		  
		  <td colspan="5">
		 
		 <input id="<portlet:namespace />descripcionPatologia"
								       name="<portlet:namespace />descripcionPatologia" size="70"
								       maxlength="200" type="text" />
		   
		 </td>  
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<input id="<portlet:namespace />save"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />agregarPatologia();"
				type="button" /> 
				
</form>

<script type="text/javascript">
function <portlet:namespace />agregarPatologia(){
	  var vapertura;
	  var params = "&<%= Constants.CMD %>=" + "agregarPatologia";
	 
	  var descripcion=jQuery('#<portlet:namespace />descripcionPatologia').val();
	  
	  params += "&descripcion="+encodeURI(descripcion);
	  url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_patologiasur';
	  url = url + params;
	  
//	  jQuery(popupPAT).load(url);
////	  <portlet:namespace />cerrarPatologiaNueva();


      jQuery.ajax({   
	   url: url,
	   async:false,
	   success: function(data){
		var obj = jQuery.parseJSON(data);
		var idPatologia = obj.id;
		var dePatologia = obj.descripcion;
		
		<portlet:namespace />cerrarPatologiaNueva(idPatologia,dePatologia);
		
	   }});
 	  return false;		
}

</script>

