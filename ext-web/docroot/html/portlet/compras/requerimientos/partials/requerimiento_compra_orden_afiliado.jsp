<%--
Incluye el mismo componente compuesto de afiliado y contacto usado por Requerimientos.
Carga el padron con seleccionaCamposAfiliado del componente original, sin duplicar su presentacion.
Requiere ordenRequerimiento, ordenCompra y ordenSuministro del caller.
Mantiene la identidad en consulta y reutiliza el flujo real de contacto de Compras.
No persiste los campos provisorios del boceto de suministro.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_javascript_helper_componente.jsp" %>
<% if (ordenRequerimiento != null && ordenRequerimiento.tieneAfiliadoInformado()) {
    Afiliado ordenAfiliado = (Afiliado) renderRequest.getAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);
%>
<%
    RequerimientoCompra req = ordenRequerimiento;
    boolean modoEditable = false;
    boolean puedeEditarEstructuraPantalla = false;
    boolean esNuevo = false;
    boolean puedeABM = false;
    boolean mostrarPanelAfiliadoEnVista = true;
    renderRequest.setAttribute("compras.requerimiento.consultaAfiliadoOrden", Boolean.TRUE);
%>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_estilos_componente.jsp" %>
<div class="compras-formulario-requerimiento">
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_afiliado_editable_componente.jsp" %>
</div>
<% renderRequest.removeAttribute("compras.requerimiento.consultaAfiliadoOrden"); %>
<% if (ordenSuministro) { %>
<input type="hidden" id="<portlet:namespace />contacto_afiliado_identidad_fija" value="1" />
<% } %>
<portlet:renderURL var="comprasActualizaDomicilioURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
    <portlet:param name="struts_action" value="/compras/actualiza_domicilio" />
</portlet:renderURL>
<script type="text/javascript">
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_contacto_afiliado_componente.jsp" %>
(function() {
    function cargarAfiliadoOrdenCompra() {
        var panel = jQuery('#<portlet:namespace />afiliado_requerimiento_panel');
        panel.find('#<portlet:namespace />entidad').val('<%= jsCompra(WebKeysGlobal.ENTIDADES_UOMA[0]) %>');
        <% if (ordenAfiliado != null) { %>
        seleccionaCamposAfiliado(
                '<%= jsCompra(ordenAfiliado.getCuil_titular()) %>',
                '<%= jsCompra(ordenAfiliado.getInteAsString()) %>',
                '<%= jsCompra(ordenAfiliado.getDocumento_tipo()) %>',
                '<%= jsCompra(ordenAfiliado.getDocu_numero()) %>',
                '<%= jsCompra(ordenAfiliado.getNombre()) %>',
                '<%= jsCompra(ordenAfiliado.getApellido()) %>',
                '<%= ordenAfiliado.getSeccional() != null ? String.valueOf(ordenAfiliado.getSeccional().getId()) : "" %>',
                '<%= jsCompra(ordenAfiliado.getSeccional() != null ? ordenAfiliado.getSeccional().getDescripcion() : "") %>',
                '<%= ordenAfiliado.getId_ospim() > 0 ? String.valueOf(ordenAfiliado.getId_ospim()) : "" %>',
                '<%= ordenAfiliado.getId_uoma() > 0 ? String.valueOf(ordenAfiliado.getId_uoma()) : "" %>',
                '<%= ordenAfiliado.getId_amtima() > 0 ? String.valueOf(ordenAfiliado.getId_amtima()) : "" %>',
                '<%= jsCompra(ordenAfiliado.getBaja_fechaAsString()) %>',
                '<%= jsCompra(ordenAfiliado.getNombrePlan()) %>',
                '<%= ordenAfiliado.getUltimo_plan() != null ? String.valueOf(ordenAfiliado.getUltimo_plan().getId()) : "" %>',
                '<%= jsCompra(ordenAfiliado.getAlta_fechaAsString()) %>',
                '<%= jsCompra(ordenAfiliado.getDiscapacitado()) %>',
                '<%= jsCompra(ordenAfiliado.getId_tercerizadora()) %>',
                '<%= jsCompra(ordenAfiliado.getDesc_tercerizadora()) %>',
                false, '', '', '',
                '<%= ordenAfiliado.getTieneAntecedentesJudiciales() %>',
                '<%= jsCompra(ordenAfiliado.getColorAntecedenteJudicial()) %>',
                '<%= jsCompra(ordenAfiliado.getCodigoAntecedenteJudicial()) %>');
        <% } else { %>
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
        <% } %>
    }
    function actualizarContactoOrdenCompra() {
        jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();
        <portlet:namespace />establecerDisponibilidadActualizarContacto(
                <portlet:namespace />valorInputCompra('contacto_afiliado_token') != ''
                && <portlet:namespace />valorInputCompra('cuil') == '<%= jsCompra(ordenRequerimiento.getAfiliadoCuilTitularVisible()) %>'
                && <portlet:namespace />valorInputCompra('inte') == '<%= jsCompra(ordenRequerimiento.getAfiliadoIntString()) %>');
    }
    jQuery(function() {
        var cargarCamposAfiliado = seleccionaCamposAfiliado;
        seleccionaCamposAfiliado = function() {
            cargarCamposAfiliado.apply(this, arguments);
            actualizarContactoOrdenCompra();
        };
        jQuery('#<portlet:namespace />afiliado_requerimiento_panel input, #<portlet:namespace />afiliado_requerimiento_panel select')
                .bind('keyup change', actualizarContactoOrdenCompra);
        jQuery('#<portlet:namespace />limpiarCampos').bind('click', actualizarContactoOrdenCompra);
        cargarAfiliadoOrdenCompra();
        actualizarContactoOrdenCompra();
    });
})();
</script>
<% } %>