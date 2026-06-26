<%
/*
 * Compatibilidad:
 * se conserva el nombre histórico del partial para no afectar includes,
 * aunque ahora funciona como componente común de ALTA, EDICION y VISTA.
 *
 * Solo se permite editar el afiliado cuando la pantalla está realmente en
 * modo editable y el estado/rol permiten modificar la estructura.
 * En cotización y en vista se renderiza exactamente el mismo componente,
 * pero con edit_mode=false.
 */
boolean afiliadoComponenteEditable =
        modoEditable
        && puedeEditarEstructuraPantalla;

boolean ocultarPanelAfiliado =
        !mostrarPanelAfiliadoEnVista;
%>

<div id="<portlet:namespace />afiliado_requerimiento_panel"
     style="<%= ocultarPanelAfiliado ? "display:none;" : "" %>">
    <fieldset class="block-labels">
        <legend>
            <liferay-ui:message key="datos-afiliado" />
        </legend>

        <div id="<portlet:namespace />afiliadoInicialMensaje"
             class="portlet-msg-info"
             style="display:none;"></div>

        <div id="<portlet:namespace />afiliadoInicialAutoSelect"
             style="display:none;"></div>

        <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
            <liferay-util:param
                    name="edit_mode"
                    value="<%= String.valueOf(afiliadoComponenteEditable) %>" />
            <liferay-util:param
                    name="pag_reintegro"
                    value="1" />
            <liferay-util:param
                    name="origen"
                    value="" />
        </liferay-util:include>
    </fieldset>
</div>
