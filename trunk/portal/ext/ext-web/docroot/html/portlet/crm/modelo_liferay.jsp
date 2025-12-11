<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.service.OrganizationLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.GroupLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Group" %>
<%@ page import="com.liferay.portal.model.Organization" %>

<%@ page import="ar.com.ospim.correspondencia.services.EmpresaSectorUsuarioServiceUtil" %>
<%@ page import="ar.com.ospim.correspondencia.services.EmpresaLiferay" %>
<%@ page import="ar.com.ospim.correspondencia.services.SectorLiferay" %>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%
String portlet_name_space = portletDisplay.getId();
String portlet_name="afiliados";
if (portlet_name_space == null || portlet_name_space.trim().equals("")){
   portlet_name = "afiliados";
}else if(portlet_name != null && portlet_name_space.trim().equals("CAI_1")){
   portlet_name = "cai";
}

%>

<script type="text/javascript">

<% 
   int cont_emp=0;
   int cont_sec=0;
   int cont_usu=0;
   
List<EmpresaLiferay> empresasSectoresUsuarios = EmpresaSectorUsuarioServiceUtil.getEmpresasSectoresUsuarios();

// Empresas
for(EmpresaLiferay empSecUsu : empresasSectoresUsuarios){ %>
	data_<%=cont_emp%> = new Option("<%=empSecUsu.getEmpresa().getName() %>", "<%=empSecUsu.getEmpresa().getOrganizationId()%>");   	
	<% cont_sec=0;
	
	// Sectores	
	for(SectorLiferay secUsu : empSecUsu.getSectores() ){ %>
		data_<%=cont_emp%>_<%=cont_sec%>=new Option("<%=secUsu.getSector().getName() %>","<%=secUsu.getSector().getUserGroupId()%>");
		<% cont_usu=0;
		
		// Usuarios
		for(User usu : secUsu.getUsuarios() ){ %>
			data_<%=cont_emp%>_<%=cont_sec%>_<%=cont_usu%>=new Option("<%=usu.getFullName() %>","<%=usu.getScreenName()%>");
			<% cont_usu++;
		}
		cont_sec++;
	}
	cont_emp++;
} %>
	 displaywhenempty=""
	 valuewhenempty=-1
	
	 displaywhennotempty="-Seleccione-"
	 valuewhennotempty=1
	
function addEvent(obj, evType, fn, useCapture) {
// General function for adding an event listener
	if (obj.addEventListener) {
		obj.addEventListener(evType, fn, useCapture);
		return true;} else if (obj.attachEvent) {
	var r = obj.attachEvent("on" + evType, fn);
		return r;
	} else {
		alert(evType+" handler could not be attached");
	}
}

function addKeyEvent() {
// Specific function for this particular browser
	var e = (document.addEventListener) ? 'keypress' : 'keydown';
	addEvent(document,e,keyEventHandler,false);
}

addKeyEvent();
//To disable the right mouse button
document.oncontextmenu=new Function("return false");
</script>

<!-- Script para JSON de Organizacion, Grupo y Usuarios -->
<script type="text/javascript">
function filtrarGrupos() {
	var idOrganiz = jQuery('#<portlet:namespace/>edificio_destino').val();
	//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/organization_groups&organizatioId='+idOrganiz;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/organization_groups&organizatioId='+idOrganiz;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />sector_destino").length = 0;	
			//document.getElementById("<portlet:namespace />usuario_destino").length = 0;	//resetea el 3er combo
			//addElementToSelect("<portlet:namespace/>usuario_destino", "Seleccione un usuario", ""); 
			var obj = jQuery.parseJSON(data);
			//addElementToSelect("<portlet:namespace/>sector_destino", "Seleccione un sector", ""); 
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelect("<portlet:namespace/>sector_destino", text, value);					
			}	                                                                                                                                                                                                                                                            
		}
	});	
}

function filtrarUsuarios(esBusq, esEntrada) {
	
	var idugrp = jQuery('#<portlet:namespace/>sector_destino').val();
	//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/groups_users&usergroupId='+idugrp+'&esBusqueda='+esBusq+'&esEntrada='+esEntrada;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/groups_users&usergroupId='+idugrp+'&esBusqueda='+esBusq+'&esEntrada='+esEntrada;
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace />usuario_destino").length = 0;						
			var obj = jQuery.parseJSON(data);
			//addElementToSelect("<portlet:namespace/>usuario_destino", "Seleccione un usuario", ""); 
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelect("<portlet:namespace/>usuario_destino", text, value);					
			}	                                                                                                                                                                                                                                                            
		}
	});	
}

function addElementToSelect(id_combo, texto, valor) {
	var combo = document.getElementById(id_combo);
	var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
	combo.options[idxElemento] = new Option();
	combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
	combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
}



</script>

