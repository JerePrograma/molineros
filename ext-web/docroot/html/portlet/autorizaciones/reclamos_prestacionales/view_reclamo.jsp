<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>

<script type="text/javascript">
(function(window, jQuery) {
    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";

    function valor(sufijo) {
        var control = jQuery("#" + namespace + sufijo);
        return control.length ? control.val() : "";
    }

    window.ReclamoPrestacionalBootstrapSnapshot = {
        troquel: valor("troquel"),
        codigo: valor("codigoSeguimiento_filtro"),
        descripcion: valor("descripcionSeguimiento_filtro"),
        tipoNomenclador: valor("tipoNomencladorSeguimiento_filtro"),
        tipoNomencladorSeleccionado: valor("tipoNomenclador"),
        nomSeleccionado: valor("nom_seleccionado"),
        sector: valor("sector"),
        tipoPedido: valor("tipopedido")
    };
})(window, jQuery);
</script>

<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=20260716-p0-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js?v=20260716-p0-1"></script>
