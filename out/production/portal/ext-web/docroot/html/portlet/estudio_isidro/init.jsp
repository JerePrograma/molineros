<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.estudioisidro.WebKeysEstudioIsidro" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.TipoLoteEmpresa" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.Llamado" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.LlamadosEstudio" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimiento" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.EstadoGestion" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.ArchivoSubidoEstudio" %>
<%@ page import="ar.com.ospim.estudioisidro.service.LlamadoServiceUtil" %>
<%@ page import="ar.com.empresas.WebKeysEmpresas" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.tesoreria.reportes.ReporteListadoValoresExcel.ReporteListadoValores"%>
<%@ page import="ar.com.ospim.tesoreria.DuplicateActaIdException" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>
<%@ page import="ar.com.ospim.global.beans.Domicilio" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil"%>
<%@ page import="ar.com.global.WebKeysPortal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.DemandaJudicial" %>
<%@ page import="ar.com.ospim.util.StringUtils"%>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ page import="ar.com.ospim.tesoreria.beans.Acta" %>
<%@ page import="ar.com.ospim.tesoreria.beans.convenio.Convenio"%>
<%@ page import="ar.com.ospim.global.beans.Cheque"%>
<%@ page import="ar.com.ospim.global.beans.Banco" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Estado" %>

<link rel="stylesheet" type="text/css" href="/html/jquery.mb.containerPlus/css/mbContainer.css" />
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>


<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%>
<script src="/html/js/utils.js?1234" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

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

addKeyEvent();
//To disable the right mouse button
document.oncontextmenu=new Function("return false");

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
	jQuery("#" + id).css("left", 300);
}

</script>