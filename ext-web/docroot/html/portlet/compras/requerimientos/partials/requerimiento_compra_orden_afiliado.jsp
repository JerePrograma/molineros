<%--
Referencia: Liquidaciones/busqueda_afiliado.jsp y componente de afiliado de Compras.
Adapta cargarAfiliadoVista/bloquearComponenteAfiliadoVista a la consulta de OC.
Requiere las variables ordenRequerimiento y ordenCompra del caller.
No consulta datos ni actualiza al afiliado.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_javascript_helper_componente.jsp" %>
<% if (ordenRequerimiento != null && ordenRequerimiento.tieneAfiliadoInformado()) { %>
<fieldset class="block-labels">
    <legend><liferay-ui:message key="datos-afiliado" /></legend>
    <div id="<portlet:namespace />orden_afiliado_panel">
        <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
            <liferay-util:param name="edit_mode" value="false" />
            <liferay-util:param name="pag_reintegro" value="1" />
            <liferay-util:param name="origen" value="" />
            <liferay-util:param name="cuil" value="" />
            <liferay-util:param name="inte" value="" />
            <liferay-util:param name="datos_afiliado_struts_action" value="/compras/buscar_afiliado_datos" />
        </liferay-util:include>
    </div>
</fieldset>
<script type="text/javascript">
(function() {
    function cargarAfiliadoOrdenCompra() {
        var panel = jQuery('#<portlet:namespace />orden_afiliado_panel');
        panel.find('#<portlet:namespace />entidad').val('<%= jsCompra(WebKeysGlobal.ENTIDADES_UOMA[0]) %>');
        panel.find('#<portlet:namespace />numero_afi').val('<%= jsCompra(ordenRequerimiento.getAfiliadoIdOspimString()) %>');
        panel.find('#<portlet:namespace />cuil').val('<%= jsCompra(ordenRequerimiento.getAfiliadoCuilTitularVisible()) %>');
        panel.find('#<portlet:namespace />inte').val('<%= jsCompra(ordenRequerimiento.getAfiliadoIntString()) %>');
        panel.find('#<portlet:namespace />tipoDoc').val('<%= jsCompra(ordenRequerimiento.getAfiliadoDocumentoTipoVisible()) %>');
        panel.find('#<portlet:namespace />nroDoc').val('<%= jsCompra(ordenRequerimiento.getAfiliadoDocumentoNroVisible()) %>');
        panel.find('#<portlet:namespace />apellido').val('<%= jsCompra(ordenRequerimiento.getAfiliadoApellidoVisible()) %>');
        panel.find('#<portlet:namespace />nombre').val('<%= jsCompra(ordenRequerimiento.getAfiliadoNombreVisible()) %>');
        panel.find('#<portlet:namespace />id_seccional').val('<%= ordenRequerimiento.getAfiliadoIdSeccional() != null && ordenRequerimiento.getAfiliadoIdSeccional().intValue() > 0 ? String.valueOf(ordenRequerimiento.getAfiliadoIdSeccional()) : "" %>');
        panel.find('#<portlet:namespace />seccional').val('<%= jsCompra(ordenRequerimiento.getAfiliadoSeccionalDescripcion()) %>');
        panel.find('#<portlet:namespace />secc_seleccionada').val('<%= WebKeysCompras.isEmpty(ordenRequerimiento.getAfiliadoSeccionalDescripcion()) ? "" : "1" %>');
        panel.find('#<portlet:namespace />nombre_plan').val('<%= jsCompra(ordenCompra.getPlan()) %>');
        panel.find('input[type="text"], textarea').attr('readonly', 'readonly');
        panel.find('select').attr('disabled', 'disabled');
        panel.find('#<portlet:namespace />id_seccional, #<portlet:namespace />seccional')
                .removeAttr('onkeyup').removeAttr('onblur');
        panel.find('#<portlet:namespace />btnBuscarSeccional, #<portlet:namespace />buscarAfiliado, #<portlet:namespace />limpiarCampos').hide();
    }
    jQuery(function() {
        cargarAfiliadoOrdenCompra();
    });
})();
</script>
<% } %>