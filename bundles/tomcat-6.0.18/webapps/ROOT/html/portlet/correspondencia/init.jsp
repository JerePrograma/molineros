<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.global.WebKeysPortal" %>

<%@ page import="ar.com.ospim.global.beans.Domicilio" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.tesoreria.DuplicateActaIdException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.math.BigDecimal" %>

<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.service.OrganizationLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.GroupLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.model.Organization" %>
<%@ page import="com.liferay.portal.model.Group" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil"%>

<%@ page import="ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia" %>
<%@ page import="ar.com.ospim.correspondencia.beans.ItemCorrespondencia" %>
<%@ page import="ar.com.ospim.correspondencia.beans.ItemCorrespondencia.RemitenteDestinatario"%>
<%@ page import="ar.com.ospim.correspondencia.beans.Paquete" %>
<%@ page import="ar.com.ospim.correspondencia.beans.BusquedaBandejaCorreoFiltro" %>
<%@ page import="ar.com.ospim.correspondencia.beans.TipoRemitente" %>
<%@ page import="ar.com.ospim.correspondencia.WebKeysCorrespondencia" %>
<%@ page import="ar.com.ospim.correspondencia.services.EmpresaSectorUsuarioServiceUtil" %>
<%@ page import="ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil"%>

<%@ page import="ar.com.ospim.correspondencia.services.EmpresaLiferay" %>
<%@ page import="ar.com.ospim.correspondencia.services.SectorLiferay" %>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.util.Date"%>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%>
<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

<script src="/html/js/combosDinamicos.js" type="text/javascript"></script>

<script type="text/javascript">

<% 
   int cont_emp=0;
   int cont_sec=0;
   int cont_usu=0;
   
   List<EmpresaLiferay> empresasSectoresUsuarios = (ArrayList<EmpresaLiferay>) portletSession
		   .getAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
		   		PortletSession.APPLICATION_SCOPE);

		   if (empresasSectoresUsuarios == null) {
			   empresasSectoresUsuarios = EmpresaSectorUsuarioServiceUtil.getEmpresasSectoresUsuarios();;
		   		portletSession.setAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
		   		empresasSectoresUsuarios,PortletSession.APPLICATION_SCOPE);	
		   }   


// Empresas
for(EmpresaLiferay empSecUsu : empresasSectoresUsuarios){ %>
	data_<%=cont_emp%> = new Option("<%=empSecUsu.getEmpresa().getName() %>", "<%=empSecUsu.getEmpresa().getOrganizationId()%>");   	
	<% cont_sec=0;
	
	// Sectores	
	for(SectorLiferay secUsu : empSecUsu.getSectores() ){ %>
		data_<%=cont_emp%>_<%=cont_sec%>=new Option("<%=secUsu.getSector().getName() %>","<%=secUsu.getSector().getUserGroupId()%>");
		<% cont_usu=0;
		
		// Usuarios
		%>
		// Primer opcion de usuario es 'A todos los usuarios del grupo'
		data_<%=cont_emp%>_<%=cont_sec%>_<%=cont_usu%>=new Option("<%="A todos los usuarios" %>","<%="TODOS"%>");
		<%
		cont_usu++;
		for(User usu : secUsu.getUsuarios() ){ 
			if(usu.getActive()){%>
				data_<%=cont_emp%>_<%=cont_sec%>_<%=cont_usu%>=new Option("<%=usu.getFullName() %>","<%=usu.getScreenName()%>");
			<%
			cont_usu++;
			}
		}
		cont_sec++;
	}
	cont_emp++;
} %>

	
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
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/organization_groups&organizatioId='+idOrganiz;
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
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/groups_users&usergroupId='+idugrp+'&esBusqueda='+esBusq+'&esEntrada='+esEntrada;
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
<%
List<TipoRemitente> tipoRemitentes = (ArrayList<TipoRemitente>) portletSession.getAttribute(WebKeysCorrespondencia.TIPOS_REMITENTES_EN_SESSION , PortletSession.APPLICATION_SCOPE);

if (tipoRemitentes == null) {
	tipoRemitentes = CorrespondenciaServiceUtil.getInstance().getTiposRemitentes() ;
	portletSession.setAttribute(WebKeysCorrespondencia.TIPOS_REMITENTES_EN_SESSION, 
			tipoRemitentes,PortletSession.APPLICATION_SCOPE);	
}

String seccionalString=null;
String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString(); 		
int seccionalFijada=null!=seccionalDefecto&& !seccionalDefecto.trim().equals("")&& !seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):0;
if(seccionalFijada!=0){
	seccionalString=user.getExpandoBridge().getAttribute("seccional").toString();
}

%>
