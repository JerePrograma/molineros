<%@ include file="/html/portlet/crm/init.jsp" %>
<%

	String item_corr=request.getParameter("item_corr");
	String id_crm_cont_derivado=request.getParameter("id_crm_cont_derivado");
	String tipo_marca=request.getParameter("tipo_marca");
	String es_cierre=request.getParameter("cierre");

%>
<div align="center">
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >

<tr>
	<td><label><liferay-ui:message key="crm-contacto-obs" />:</label></td>
	<td>
		<textarea rows="6" cols="75" 
				id="<portlet:namespace />obs_derivacion" 
				name="<portlet:namespace />obs_derivacion"
				style="resize: none;">
		</textarea>
	</td>			
</tr>	

<tr>
	<% String ejecutaCierreRecibidoURL="javascript:ejecutarCierreRecibido('"+item_corr+"','"+tipo_marca+"','"+id_crm_cont_derivado+"','"+es_cierre+"');"; %>		
	<td colspan="2" align="center">
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<%=ejecutaCierreRecibidoURL%>" />	
	</td>
</tr>
</table>
</div>
<script type="text/javascript">
jQuery('#<portlet:namespace />obs_derivacion').focus();

function validarCierreRecibido() {

	var obs = jQuery('#<portlet:namespace />obs_derivacion').val();

	if(trim(obs).length == 0 || obs==''){
		alert("Debe ingresar una observación");
		return false;
	}
	
	return true;
}
</script>


