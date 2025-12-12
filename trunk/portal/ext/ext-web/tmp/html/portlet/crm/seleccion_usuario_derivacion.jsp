<%@ include file="/html/portlet/crm/init.jsp" %>
<%

	String item_corr=request.getParameter("item_corr");
	String id_crm_cont_derivado=request.getParameter("id_crm_cont_derivado");
	String tipo_marca=request.getParameter("tipo_marca");

%>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">

<tr>
	<td><label><liferay-ui:message key="crm-contacto-obs" />:</label></td>
	<td>
		<textarea rows="6" cols="75" maxlength="20000" 
				id="<portlet:namespace />obs_derivacion" 
				name="<portlet:namespace />obs_derivacion"
				style="resize: none;">
		</textarea>
	</td>			
</tr>	
<tr>
	<td colspan="2">
		<%-- <div align="center" id="<portlet:namespace />divUsuarioDerivacion" >
			<liferay-util:include page="/html/portlet/crm/derivacion_liferay.jsp">
				<liferay-util:param name="view" value="<%=new String(\"false\") %>"/>
			</liferay-util:include>
		</div> --%>
		<div align="center" id="<portlet:namespace />divUsuarioDerivacion" >
			<liferay-util:include page="/html/portlet/correspondencia/destinatario_liferay.jsp">
			</liferay-util:include>
		</div>
	</td>
</tr>
<tr>
	<% String ejecutaDerivarURL="javascript:ejecutarDerivacion('"+item_corr+"','"+tipo_marca+"','"+id_crm_cont_derivado+"');";
	
	   String ejecutaCancelarURL="javascript:ejecutarDerivacion('0','ITEM','0');"; %>		
	<td colspan="2" align="right">
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<%=ejecutaDerivarURL%>"
		id="<portlet:namespace />ejecutaDerivacion" />
	
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
		<input type="button" value="<liferay-ui:message key="cancel" />" onClick="<%=ejecutaCancelarURL%>" />
	</td>
</tr>
</table>

<script type="text/javascript">
jQuery('#<portlet:namespace />obs_derivacion').focus();

function validarUsuDeriva() {

	var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
	var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
	var obs = jQuery('#<portlet:namespace />obs_derivacion').val();
	
	if(sec_dest==''){
		alert("Debe seleccionar el Sector derivación");
		return false;
	}
	if(usu_dest==''){
		alert("Debe seleccionar el Usuario derivación");
		return false;
	}
	if(trim(obs).length == 0 || obs==''){
		alert("Debe ingresar una observación");
		return false;
	}
	
	return true;
}
</script>


