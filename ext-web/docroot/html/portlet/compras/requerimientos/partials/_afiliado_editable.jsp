<div id="<portlet:namespace />afiliado_requerimiento_panel"
     style="<%= modoEditable && !mostrarPanelAfiliadoEnVista ? "display:none;" : "" %>">
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
            <liferay-util:param value="<%= String.valueOf(true) %>"
                                name="edit_mode" />
            <liferay-util:param name="pag_reintegro"
                                value="1" />
            <liferay-util:param name="origen"
                                value="" />
        </liferay-util:include>
    </fieldset>
</div>
