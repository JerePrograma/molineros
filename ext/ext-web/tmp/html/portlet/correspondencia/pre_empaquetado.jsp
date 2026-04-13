<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%
%>
<div id="<portlet:namespace/>selectPreEmpaquetado">
	<table style="left:50%;" class="lft-table">
		<tr>
			<td> <liferay-ui:message key="descrip-paquete" /></td>
			<td><input type="text" value="" id="<portlet:namespace/>paquete_descripcion"> </td>	
			<td>
				&nbsp;
			</td>		
			<td>
				<input type="button" value="<liferay-ui:message key="save" />" onClick="javascript:pasarDescripcion();" />
			</td>
		</tr>
	</table>
</div>


<script type="text/javascript"> 
	function pasarDescripcion(){		
		var desc=jQuery('#<portlet:namespace/>paquete_descripcion').val();
		jQuery('#<portlet:namespace />paq_descripcion').val(desc);	
		<portlet:namespace />cerrarPopUp();
	}
</script>