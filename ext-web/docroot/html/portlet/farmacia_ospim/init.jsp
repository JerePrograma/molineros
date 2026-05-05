<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBag" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBagPool"%>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>

<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim" %>
<%@ page import="ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil"%>
<%@ page import="ar.com.ospim.farmaciaOspim.beans.ArchivoMedEspecial"%>
<%@ page import="ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro"%>
<%@ page import="ar.com.ospim.farmaciaOspim.beans.TiposDeVentas"%>
<%@ page import="ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil"%>

<%@ page import="ar.com.ospim.procesaArchivos.beans.ArchivoVademecum"%>
<%@ page import="ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose"%>
<%@ page import="ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoAdmifarm"%>
<%@ page import="ar.com.ospim.procesaArchivos.exception.ArchivoMedEsIncorrectoException"%>

<%@ page import="ar.com.ospim.farmacia.beans.Medicamento"%>
<%@ page import="ar.com.ospim.farmacia.beans.Vademecum"%>
<%@ page import="ar.com.ospim.global.beans.Farmacia"%>
<%@ page import="ar.com.ospim.global.beans.ColegioFarmacia"%>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>

<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>


<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.reportes.beans.UltimosProcesosSisOld" %>

<%@ page import="ar.com.ospim.afip.service.AfipServiceUtil"%>

<%@ page import="ar.com.ospim.farmacia.WebKeysFarmacia"%>

<%@ page import="java.text.Format" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.math.BigDecimal" %>


<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);


List<Provincia> provincias =(ArrayList<Provincia>) portletSession.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
	provincias,PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,PortletSession.APPLICATION_SCOPE);	
}



Map<Integer,List<Localidad>> localidadesPorProvincia = (Map<Integer,List<Localidad>>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA, PortletSession.APPLICATION_SCOPE);

 if (localidadesPorProvincia == null || localidadesPorProvincia.size()==0) {
	localidadesPorProvincia = new HashMap<Integer,List<Localidad>>();	 
 		
	for(Localidad l:localidades){
		if(l!=null && l.getId_provincia()>0 && l.getDescripcion()!=null && !"".equalsIgnoreCase(l.getDescripcion().trim() )){
			List<Localidad> lst =  new ArrayList<Localidad>();
			try{
			  lst = localidadesPorProvincia.get(l.getId_provincia());
			  if(lst==null) lst =  new ArrayList<Localidad>();
			}catch(Exception e){
			  lst =  new ArrayList<Localidad>();
			}
			lst.add(l);
			localidadesPorProvincia.put(l.getId_provincia(), lst);
	   }
	}
	
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA,
			localidadesPorProvincia,PortletSession.APPLICATION_SCOPE);

 }


 

%>




<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" href="/html/jquery.mb.containerPlus/css/mbContainer.css" />
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>




<script type="text/javascript">
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


jQuery(document).ready(function() {
	jQuery(".containerPlus").buildContainers({
        containment:"document",
        elementsPath:"/html/jquery.mb.containerPlus/elements/",
        onResize:function(o){},
        onClose:function(o){},
        onCollapse:function(o){},
        onIconize:function(o){},
        onDrag:function(o){},
        onLoad:function(o){}
    });
});


function closeHelps(){
	 var elements = jQuery('.containerPlus');
	 elements.each(function() { jQuery(this).mb_close(); })	
}

function help(event, id){
	closeHelps();
	jQuery("#" + id).mb_open();
	jQuery("#" + id).css("top",event.clientY + jQuery(document).scrollTop());
	jQuery("#" + id).css("left", 600);
}


addKeyEvent();
//To disable the right mouse button
document.oncontextmenu=new Function("return false");




</script>