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

<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>

<%@ page import="ar.com.ospim.afiliados.action.ActionUtil" %>
<%@ page import="ar.com.ospim.afiliados.beans.CategoriaLaboral" %>

<%@ page import="ar.com.empresas.WebKeysEmpresas" %>

<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.global.beans.Banco" %>
<%@ page import="ar.com.ospim.global.beans.RamoEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="ar.com.ospim.global.beans.Domicilio" %>
<%@ page import="ar.com.ospim.global.beans.ContactoElectronico" %>
<%@ page import="ar.com.ospim.global.beans.EntidadPadronUnificado" %>
<%@ page import="ar.com.ospim.global.beans.Regimen" %>

<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.EntidadCamaraEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.PosicionIva" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.CuilUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>


<%@ page import="ar.com.empresas.beans.Contacto" %>
<%@ page import="ar.com.empresas.beans.Actividad" %>
<%@ page import="ar.com.empresas.beans.ReporteEntidadCamaraMasaBean" %>

<%@ page import="ar.com.ospim.estudioisidro.WebKeysEstudioIsidro" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.LlamadosEstudio" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.EstadoGestion" %>
<%@ page import="ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimientoResumen" %>
<%@ page import="ar.com.ospim.estudioisidro.service.LlamadoServiceUtil" %>

<%@ page import="ar.com.ospim.tesoreria.beans.CuentaBancaria" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="javax.servlet.ServletContext" %>

<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>


<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>
<portlet:defineObjects />
<%

List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (seccionales == null) {
	seccionales = TraeListasServiceUtil.getSeccionales();
	portletSession.setAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
	seccionales,PortletSession.APPLICATION_SCOPE);
}

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "empresas";
}

if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_AFI_1_")){
	portlet_name = "afiliados";
}
if(renderResponse.getNamespace().equals("_CGT_1_")){
	portlet_name = "cgt";
}
if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}

if(renderResponse.getNamespace().equals("_EMP_1_")){
	portlet_name = "empresas";
}
		
if(renderResponse.getNamespace().equals("_LIQ_1_")){
	portlet_name = "liquidaciones";
}

if(renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
}
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
</script>