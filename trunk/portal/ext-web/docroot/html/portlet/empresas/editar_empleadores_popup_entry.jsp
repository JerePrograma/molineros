<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%
String error =(String) request.getAttribute(WebKeysEmpleadores.CUIT);
String cuit =(String) request.getAttribute("cuit");
String empresa_ya_existe = (String) request.getAttribute("empresa_ya_existe");
String empresa_grabada = (String) request.getAttribute("empresa_grabada");

%>

<script type="text/javascript">
	<%if(empresa_ya_existe != null && empresa_ya_existe.equals("true")) {%>
		alert("Esta empresa ya existe");
	<%}if(empresa_grabada != null && empresa_grabada.equals("true")) {%>
		alert("Los cambios fueron grabados con éxito.");
	<%}else {%>
		jQuery('#<portlet:namespace />cuit_empleador').val(<%=cuit%>);		
		Liferay.Popup.close(cartel);
	<%}%>
</script>