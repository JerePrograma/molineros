<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%

Afiliado afiliadoDadoBaja = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(ParamUtil.getString(request, "cuil_titular"), 0);
boolean cuilValido = CuilUtils.validarNum(ParamUtil.getString(request, "cuil_titular"));
boolean baja = Validator.isNotNull(afiliadoDadoBaja) && Validator.isNotNull(afiliadoDadoBaja.getBaja_fecha()) && afiliadoDadoBaja.getBaja_fecha().getTime()<System.currentTimeMillis();
%>
<script type="text/javascript">

var popupate;
function <portlet:namespace />cargaPopUpSiAfiliadoDadoBaja() {
	//si cuit inválido muestra mensaje y retorna;	
	if (<portlet:namespace />cuilInvalido()) {
		jQuery("#<portlet:namespace />inte").focus();
		jQuery("#<portlet:namespace />cuil_titular").val('');
		return;
	}
	var cuil_titular=<%= Validator.isNotNull(afiliadoDadoBaja) ? "'" + afiliadoDadoBaja.getCuil_titular() + "'" : "''" %>;	
	if (cuil_titular.length > 0) {
		//si afiliado no baja
		if (!<%=baja%>) {
			//lo muestra para la edición					
			<portlet:namespace />showGrupoFliarExistente(cuil_titular);			
			return;
		}
		//avisa que estpa de baja	
		popupate = Liferay.Popup({title:"<liferay-ui:message key="afiliado-dado-de-baja" />",modal:true,width:420});
	    var inte=<%=Validator.isNotNull(afiliadoDadoBaja) ? afiliadoDadoBaja.getInte() : "''" %>;
	    var nombre=<%=Validator.isNotNull(afiliadoDadoBaja) ? "'" + afiliadoDadoBaja.getNombre() + "'" : "''" %>;
	    var apellido=<%=Validator.isNotNull(afiliadoDadoBaja) ? "'" + afiliadoDadoBaja.getApellido() + "'" : "''" %>;	   	   
	    jQuery("#<portlet:namespace />cuil_titular").val('');
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_titular_existente_popup&cuil_titular='+cuil_titular+
	    		  '&inte='+inte;		 
		jQuery(popupate).load(url);
	}
}

function <portlet:namespace />cuilInvalido() {
	if (!<%=cuilValido%>) {
		alert ('<liferay-ui:message key="cuil-invalido" />');
		return true;
	}
	return false;
}

function <portlet:namespace />showGrupoFliarExistente(cuil_titular){
	popupate = Liferay.Popup({title:"<liferay-ui:message key="grupo-familiar-existente" />",modal:true,width:420});
    var inte=<%=Validator.isNotNull(afiliadoDadoBaja) ? afiliadoDadoBaja.getInte() : "''" %>;
    var nombre=<%=Validator.isNotNull(afiliadoDadoBaja) ? "'" + afiliadoDadoBaja.getNombre() + "'" : "''" %>;
    var apellido=<%=Validator.isNotNull(afiliadoDadoBaja) ? "'" + afiliadoDadoBaja.getApellido() + "'" : "''" %>;	        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/afiliado_titular_existente_popup&cuil_titular='+cuil_titular+
    		  '&inte='+inte;
	jQuery(popupate).load(url);
}

<portlet:namespace />cargaPopUpSiAfiliadoDadoBaja();

</script>