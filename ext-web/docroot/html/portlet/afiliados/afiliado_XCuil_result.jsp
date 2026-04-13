<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
String tipoCuil = ParamUtil.getString(request, "tipoCuil");
Afiliado afiliado = null;
if(tipoCuil.equalsIgnoreCase("cuil")) {
	afiliado = ActionUtil.getAfiliadoActivoXCuil(ParamUtil.getString(request, "cuil"));
} else if (tipoCuil.equalsIgnoreCase("cuil_titular")) {
	afiliado = ActionUtil.getAfiliadoActivoXCuil(ParamUtil.getString(request, "cuil_titular"));
}
%>

<script type="text/javascript">
verificarAfi();

function verificarAfi() {
	//si cuil ya existe muestra mensaje y retorna;
	if (cuilYaExiste()) {		
		jQuery("#<portlet:namespace />cuil").val('');
		jQuery("#<portlet:namespace />nroDoc").val('');
		return;
	}
}

function cuilYaExiste() {
	<c:if test="<%= Validator.isNotNull(afiliado)%>">
		alert ('<liferay-ui:message key="cuil-existente" />');
		return true;
	</c:if>
	return false;
}
</script>