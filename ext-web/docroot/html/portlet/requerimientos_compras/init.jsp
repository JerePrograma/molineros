<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchEntry" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.model.User" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>

<%@ page import="ar.com.ospim.requerimientos_compras.WebKeysRequerimientosCompras" %>
<%@ page import="ar.com.ospim.requerimientos_compras.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraItem" %>
<%@ page import="ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraHistorial" %>
<%@ page import="ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraAdjunto" %>
<%@ page import="ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraFiltro" %>
<%@ page import="ar.com.ospim.requerimientos_compras.service.BusquedaRequerimientoCompraServiceUtil" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<portlet:defineObjects />

<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
