<%--
Referencia: Liquidaciones/busqueda_afiliado.jsp y componente de afiliado de Compras.
Adapta cargarAfiliadoVista/bloquearComponenteAfiliadoVista a la consulta de OC.
Requiere ordenRequerimiento, ordenCompra y ordenSuministro del caller.
Mantiene la identidad en consulta y reutiliza el flujo real de contacto de Compras.
No persiste los campos provisorios del boceto de suministro.
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
    <% if (ordenSuministro) {
        Object ordenContactoTokenAttribute = renderRequest.getAttribute("COMPRAS_CONTACTO_AFILIADO_TOKEN");
        String ordenContactoToken = ordenContactoTokenAttribute == null ? "" : String.valueOf(ordenContactoTokenAttribute);
    %>
    <input type="hidden" id="<portlet:namespace />contacto_afiliado_token"
            value="<%= HtmlUtil.escape(ordenContactoToken) %>" />
    <input type="hidden" id="<portlet:namespace />contacto_afiliado_identidad_fija" value="1" />
    <div id="<portlet:namespace />seccionVerificarDomicilio">
        <table class="lfr-table"><tr>
            <td><label>Verificar datos de contacto:</label></td>
            <td><div id="<portlet:namespace />divBotonActualizar">
                <input type="button" id="<portlet:namespace />botonActualizarContactoAfiliado"
                        value="Actualizar" <%= WebKeysCompras.isEmpty(ordenContactoToken) ? "disabled=\"disabled\"" : "" %>
                        onclick="return <portlet:namespace />mostrarDomicilioAfiliado();" />
            </div></td>
        </tr></table>
        <div id="<portlet:namespace />divResultadoActualizarOK" style="display:none;">
            <p><b><liferay-ui:message key="crm-actualiza-domicilio" /></b></p>
        </div>
    </div>
    <% } %>
</fieldset>
<% if (ordenSuministro) { %>
<portlet:renderURL var="comprasActualizaDomicilioURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/compras/actualiza_domicilio" />
</portlet:renderURL>
<% } %>
<script type="text/javascript">
<% if (ordenSuministro) { %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_contacto_afiliado_componente.jsp" %>
<% } %>
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
        <% if (ordenSuministro) { %>
        <portlet:namespace />establecerDisponibilidadActualizarContacto(
                <portlet:namespace />valorInputCompra('contacto_afiliado_token') != '');
        <% } %>
    });
})();
</script>
<% } %>