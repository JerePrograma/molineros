<%@ include file="/html/portlet/farmacia/init.jsp"%>

<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
	}

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
		<liferay-ui:message key="subir-archivo-farmacia" />
	</legend>		
		<table class="lfr-table">
			<tr>
				<td><liferay-ui:message key="manual-dat" />:</td>
				<td  align="center">
					<input type="file" name="archivoManualDat"/>
				</td>
				<td >
					&nbsp;					
				</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="listado-sssalud" />:</td>
				<td  align="center">
					<input type="file" name="archivoListadoSSSalud"/>
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
				<td>
					<input type="button" value="<liferay-ui:message key="exportar-vademecum" />" onClick="<portlet:namespace />obtenerVademecum()"/>					
				</td>
			</tr>
		</table>
	</fieldset>

<script type="text/javascript">
		function <portlet:namespace />uploadArchivos() {	
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivo_farmacia';						
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url+'&archivo=todos');
		}
		function <portlet:namespace />obtenerVademecum(){			
			window.location.href ='/xlsservlet/?reporte=OBTENER_VADEMECUM';						
		}
</script>
