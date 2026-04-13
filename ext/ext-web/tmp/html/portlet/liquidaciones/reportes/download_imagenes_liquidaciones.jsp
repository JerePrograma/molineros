<%@ include file="/html/portlet/liquidaciones/init.jsp" %>


<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/liquidaciones/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	
	String rps=(String)request.getSession().getAttribute("LIQUIDACIONES_PROCESAR_IMAGENES");
	
	//verificar los calendars
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();	
	
	 List<String> errores = (List<String>)request.getAttribute("errores");
	 if (errores != null && !errores.isEmpty()){
	 	%>
	 	<table  style="color:red" >
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

<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="Proceso finalizado" />		
	</c:when>
</c:choose>	

<form action="" method="post" name="<portlet:namespace />fmSSS" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	<legend>
		Liquidaciones a Procesar
	</legend>

		<table class="lfr-table">
		<tr>
		    <td align="center">
				<input type="file" name="archivo"/>
		    </td> 
			<td align="center">
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>			
		</tr>		
		</table>
		<br>
		<label style="color:red; font-size: 15px">El archivo debe ser una planilla de cálculo con extensión xls. Debe contener una sola columna sin títulos en donde figure el nro
		     de liquidación a procesar.
		   </label>
		</fieldset>	
		<fieldset class="block-labels">				
	    <legend> Exportar Imágenes</legend>		
	
	
        <table style="align: center;">
	    <tr>
		<td>Liquidaciones a Exportar</td>
		<td> 
		
		   <textarea id="<portlet:namespace />rps"
			name="<portlet:namespace />rps" rows="5" cols="82"><%=rps!=null?rps:""%></textarea>
		</td>  	
		<td align="center">
		
		 <input id="<portlet:namespace />exportar-imagenes" value="Exportar Imágenes" 
					title="Exportar Imágenes" type="button" />
		
		</td>
	</tr>
	<tr>
	<table><tr>
	<td><label style="color:blue; font-size: 15px" >Los números de Liquidaciones deben estar separados por punto y coma</label></td>
	</tr></table>
	</tr>
   </table>	
			
  

</fieldset>
		
	
</form>		

<script type="text/javascript">	
	

function <portlet:namespace />uploadArchivo() {
	
		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_liquidacion_entry';
		url = url + '&cmd=upload'; 
		document.<portlet:namespace />fmSSS.method = 'post';
		submitForm(document.<portlet:namespace />fmSSS, url);
}
	
	
jQuery('#<portlet:namespace />exportar-imagenes').click(function exportarImagenes(){
		
		var rps=jQuery('#<portlet:namespace />rps').val();
		 
		if(trim(rps).length == 0 ){
			alert("Debe ingresar lista de Liquidaciones separados por punto y coma");
			jQuery('#<portlet:namespace />in').focus();
			return false;
		}
		
		window.location.href ='/txtservlet/?reporte=LIQUIDACIONES_EXPORTAR_IMAGENES'
			+'&in='+rps ;	
});

	
</script>
