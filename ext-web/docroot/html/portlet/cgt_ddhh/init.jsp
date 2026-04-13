<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.cgt.ddhh.WebKeysCGT" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.global.WebKeysPortal" %>
<%@ page import="ar.com.cgt.ddhh.beans.Organismo" %>
<%@ page import="ar.com.cgt.ddhh.beans.Area" %>
<%@ page import="ar.com.cgt.ddhh.beans.NormaDdHh" %>
<%@ page import="ar.com.cgt.ddhh.beans.TemasNormasDDHH" %>
<%@ page import="ar.com.cgt.ddhh.beans.TiposNormasDDHH" %>

<%@ page import="ar.com.cgt.ddhh.beans.LineaTrabajo" %>
<%@ page import="ar.com.cgt.ddhh.beans.Comentario" %>
<%@ page import="ar.com.cgt.ddhh.beans.Contacto" %>
<%@ page import="ar.com.ospim.global.beans.Domicilio" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Pais" %>
<%@ page import="ar.com.ospim.tesoreria.DuplicateActaIdException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.math.BigDecimal" %>

<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="ar.com.cgt.ddhh.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil"%>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>


<%@ page import="java.util.Date"%>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%>
<script src="/html/js/utils.js" type="text/javascript"></script>
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