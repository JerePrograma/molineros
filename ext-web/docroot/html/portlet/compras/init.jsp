<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>

<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>

<%@ page import="ar.com.ospim.liquidaciones.WebKeysLiquidaciones" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchEntry" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.model.User" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>

<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<portlet:defineObjects />

<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>

<link rel="stylesheet"
      type="text/css"
      href="/html/jquery.mb.containerPlus/css/mbContainer.css" />

<script type="text/javascript"
        src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>

<script type="text/javascript"
        src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>

<script type="text/javascript">
jQuery(document).ready(function() {
    jQuery(".compras-container-ayuda").buildContainers({
        containment: "document",
        elementsPath: "/html/jquery.mb.containerPlus/elements/",
        onResize: function(o) {},
        onClose: function(o) {},
        onCollapse: function(o) {},
        onIconize: function(o) {},
        onDrag: function(o) {},
        onLoad: function(o) {}
    });
});

function comprasCloseHelps() {
    var elements =
            jQuery(
                    '.compras-container-ayuda'
            );

    elements.each(function() {
        jQuery(this).mb_close();
    });
}

function comprasHelp(event, id) {
    comprasCloseHelps();

    var ayuda =
            jQuery(
                    "#" + id
            );

    ayuda.mb_open();

    ayuda.css(
            "top",
            event.clientY
                    + jQuery(document).scrollTop()
    );

    ayuda.css(
            "left",
            300
    );

    return false;
}
</script>
