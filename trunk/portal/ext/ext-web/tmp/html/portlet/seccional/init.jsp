<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>


<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.security.auth.PrincipalException" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.SystemException" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.StringUtils"%>

<%@ page import="ar.com.ospim.global.beans.Seccional"%>

<%@ page import="ar.com.ospim.seccional.beans.WebKeysSeccionales"%>
<%@ page import="ar.com.ospim.seccional.beans.GestionSeccional"%>
<%@ page import="ar.com.empresas.beans.Contacto"%>
<%@ page import="ar.com.ospim.global.beans.Empresa"%>
<%@ page import="ar.com.uoma.beans.CentroCosto"%>

<%@ page import="java.text.NumberFormat"%>

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
