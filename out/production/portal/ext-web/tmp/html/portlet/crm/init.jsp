<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="ar.com.global.WebKeysPortal" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>

<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="javax.portlet.WindowState"%>

<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>

<%@ page import="ar.com.ospim.crm.WebKeysCrm" %>

<%@ page import="ar.com.ospim.crm.beans.CategoriaContacto" %>
<%@ page import="ar.com.ospim.crm.beans.TipoContacto" %>
<%@ page import="ar.com.ospim.crm.beans.MotivoContacto" %>
<%@ page import="ar.com.ospim.crm.beans.ContactoCRM"%>
<%@ page import="ar.com.ospim.crm.beans.DerivacionSeguimiento"%>
<%@ page import="ar.com.ospim.crm.beans.NoAfiliado"%>
<%@ page import="ar.com.ospim.crm.beans.BusquedaContactoFiltro"%>
<%@ page import="ar.com.ospim.crm.beans.CRMEstadistica"%>
<%@ page import="ar.com.ospim.crm.beans.CRMEficacia"%>
<%@ page import="ar.com.ospim.crm.beans.DocumentoLegalCRM"%>
<%@ page import="ar.com.ospim.crm.beans.TipoReclamo"%>
<%@ page import="ar.com.ospim.crm.beans.BusquedaDocumLegalFiltro"%>

<%@ page import="ar.com.ospim.crm.services.CrmServiceUtil"%>

<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>

<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>

<%@ page import="ar.com.ospim.global.beans.Domicilio" %>

<%@ page import="ar.com.empresas.beans.Contacto" %>


<%@ page import="java.util.Date" %>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>



<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%> 
<portlet:defineObjects />

<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

<script src="/html/js/combosDinamicos.js" type="text/javascript"></script>

<script type="text/javascript">


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

