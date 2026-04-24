<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
String tipoValidacion = ParamUtil.getString(request, "tipoValidacion");
Afiliado afiliado = null;
if (tipoValidacion.equalsIgnoreCase("cuil_titular")) {
	afiliado = ActionUtil.getAfiliadoActivoXCuil(ParamUtil.getString(request, "cuil_titular"));
} else if (tipoValidacion.equalsIgnoreCase("cuil")) {
	afiliado = ActionUtil.getAfiliadoActivoXCuilInte(ParamUtil.getString(request, "cuil"));
}

ActionUtil.getAfiliadoExistente(renderRequest);	

Afiliado afiliadoExistente = (Afiliado)request.getAttribute(WebKeysAfiliados.AFILIADO_EXISTENTE);
boolean afiliadoCargable = false;
boolean afiliadoEdicion = false;

String cuil_original=ParamUtil.getString(request, "cuil_original");
String inte_original=ParamUtil.getString(request, "inte_original");


if (Validator.isNotNull(afiliadoExistente) && (!afiliadoExistente.esTitular() && afiliadoExistente.esBaja())) {
	if (!afiliadoExistente.getCuil_titular().equalsIgnoreCase(ParamUtil.getString(request, "cuil_titular")) ) {
		afiliadoCargable = true;
	}
}

if (Validator.isNotNull(afiliadoExistente) && cuil_original.equalsIgnoreCase(afiliadoExistente.getCuil_titular()) &&
		inte_original.equalsIgnoreCase(String.valueOf(afiliadoExistente.getInte())) && !inte_original.equals("")) {
	afiliadoEdicion = true;
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

var popupaer;
function <portlet:namespace />cargaPopUpSiAfiliadoExiste() {	
	var cuil_titular=<%= Validator.isNotNull(afiliadoExistente) ? "'" + afiliadoExistente.getCuil_titular() + "'" : "''" %>;	
	if (cuil_titular.length > 0) {
		if (<%=afiliadoCargable %> || <%=afiliadoEdicion %>) {
			return;
		}		
		popupaer = Liferay.Popup({title:"<liferay-ui:message key="afiliado-ya-existe" />",modal:true,width:420});	    
	    var inte=<%=Validator.isNotNull(afiliadoExistente) ? afiliadoExistente.getInte() : "''" %>;
	    var nombre=<%=Validator.isNotNull(afiliadoExistente) ? "'" + afiliadoExistente.getNombre() + "'" : "''" %>;
	    var apellido=<%=Validator.isNotNull(afiliadoExistente) ? "'" + afiliadoExistente.getApellido() + "'" : "''" %>;
	    var doc=<%=Validator.isNotNull(afiliadoExistente) ? "'" + afiliadoExistente.getDocu_numero() + "'" : "''" %>;
	    var tipo=<%=Validator.isNotNull(afiliadoExistente) ? "'" + afiliadoExistente.getDocumento_tipo() + "'" : "''" %>;
	    jQuery("#<portlet:namespace />nroDoc").val("");
	    var cuil=<%=Validator.isNotNull(afiliadoExistente) ? afiliadoExistente.getCuil() : "''" %>;
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_existente_popup&cuil_titular='+cuil_titular+
	    		  '&inte='+inte+'&doc='+doc+'&tipo='+tipo+'&nombre='+nombre+'&apellido='+apellido+'&cuil='+cuil;
	    jQuery(popupaer).load(url);
	}
}

<portlet:namespace />cargaPopUpSiAfiliadoExiste();
</script>