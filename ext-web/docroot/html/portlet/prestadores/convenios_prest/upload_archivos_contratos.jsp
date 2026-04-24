<%@ include file="/html/portlet/liquidaciones/init.jsp"%>

<%

List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
	%>
	<table>
	<%
	for (String error : errores){
		%>
		<tr><td>
		<%=error%>
		</td></tr>
		<%
	}
	%>
	</table>
	<%
}

%>
</form>
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	<legend>
		Subir Archivos Contrato
	</legend>		
		<table class="lfr-table">
			<tr>
				<td>Importar Contrato:</td>
				<td  align="center">
					<input type="file" name="importa_contrato"/>
				</td>
				<td >
					&nbsp;					
				</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td>Actualiza Valores:</td>
				<td  align="center">
					<input type="file" name="actualiza_valores"/>
				</td>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>				
				<td colspan="2" align="right">
					<input type="submit" value="<liferay-ui:message key="upload-files" />" onClick="<portlet:namespace />uploadArchivos()"/>
				</td>
			</tr>
		</table>
	</fieldset>

<script type="text/javascript">
		function <portlet:namespace />uploadArchivos() {	
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/upload_archivo_contratos"/></portlet:actionURL>';			
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url+'&archivo=todos');
		}
</script>
