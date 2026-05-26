<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
RequerimientoCompra reqAfi = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqAfi == null) {
    reqAfi = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqAfi == null) {
    reqAfi = new RequerimientoCompra();
}

String afiliadoCuilTitular = reqAfi.getAfiliadoCuilTitularVisible();

if (afiliadoCuilTitular == null) {
    afiliadoCuilTitular = "";
}

String afiliadoInte = reqAfi.getAfiliadoInteString();

if (afiliadoInte == null) {
    afiliadoInte = "";
}
%>

<fieldset class="block-labels" id="<portlet:namespace />afiliado_requerimiento_fieldset">
    <legend>Afiliado</legend>

    <input type="hidden"
           name="<portlet:namespace />afiliado_cuil_titular"
           id="<portlet:namespace />afiliado_cuil_titular"
           value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

    <input type="hidden"
           name="<portlet:namespace />afiliado_inte"
           id="<portlet:namespace />afiliado_inte"
           value="<%= HtmlUtil.escape(afiliadoInte) %>" />

    <liferay-util:include page="/html/portlet/afiliados/busqueda_afiliado.jsp" />
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />sincronizarAfiliadoRequerimientoDesdeBusquedaAfiliados() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(jQuery.trim(jQuery('#<portlet:namespace />cuil').val()));
        jQuery('#<portlet:namespace />afiliado_inte').val(jQuery.trim(jQuery('#<portlet:namespace />inte').val()));
    }

    jQuery(function() {
        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte').change(function() {
            <portlet:namespace />sincronizarAfiliadoRequerimientoDesdeBusquedaAfiliados();
        });

        <portlet:namespace />sincronizarAfiliadoRequerimientoDesdeBusquedaAfiliados();
    });
</script>