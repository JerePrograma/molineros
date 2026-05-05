<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
		<fieldset class="block-labels">
			<legend><liferay-ui:message key="reporte-excel" /></legend>
			<table class="lfr-table">
				<tr>
					<td><label><liferay-ui:message key="numero-lista" />:</label></td>
					<td><input type="text" name="<portlet:namespace />nro" id="<portlet:namespace />nro"/></td>
					<td colspan="5" align="left">
						<input type="submit" value="<liferay-ui:message key="obtener-lista"/>" onClick="<portlet:namespace />verLista();return false;"/>
					</td>
				</tr>
			</table>	      	  
		</fieldset>			
<script type="text/javascript">
function <portlet:namespace />verLista(){
	if (!IsNumeric(trim(document.getElementById("<portlet:namespace />nro").value))){
		alert("Ingrese un numero de lista");
		document.getElementById("<portlet:namespace />nro").focus();
		return false;
	}
	window.location.href ='/xlsservlet/?reporte=OP_REINTEGRO_FARMACIA&idLista=' + trim(document.getElementById("<portlet:namespace />nro").value);
}
</script>