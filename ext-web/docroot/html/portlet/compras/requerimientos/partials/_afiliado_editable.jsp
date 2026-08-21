<%
/*
 * Compatibilidad:
 * se conserva el nombre historico del partial para no afectar includes,
 * aunque funciona como componente comun de ALTA, EDICION y VISTA.
 *
 * El afiliado se selecciona exclusivamente durante el alta.
 * Una vez creado el requerimiento, su identidad queda inmutable aunque
 * el resto de la estructura siga editable mientras permanezca PENDIENTE.
 */
boolean afiliadoComponenteEditable =
        esNuevo
        && modoEditable
        && puedeEditarEstructuraPantalla;

boolean ocultarPanelAfiliado =
        !mostrarPanelAfiliadoEnVista;
%>

<div id="<portlet:namespace />afiliado_requerimiento_panel"
     class="compras-seccion compras-seccion-afiliado"
     style="<%= ocultarPanelAfiliado ? "display:none;" : "" %>">

    <%--
        El componente compartido contiene exclusivamente
        la busqueda/seleccion del afiliado.
    --%>
    <fieldset class="block-labels">
        <legend>
            <liferay-ui:message key="datos-afiliado" />
        </legend>

        <div id="<portlet:namespace />afiliadoInicialMensaje"
             class="portlet-msg-info"
             style="display:none;"></div>

        <div id="<portlet:namespace />afiliadoInicialAutoSelect"
             style="display:none;"></div>

        <liferay-util:include
                page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">

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

    <%--
        IMPORTANTE:
        La verificacion/actualizacion de datos de contacto queda FUERA
        del componente de busqueda de afiliado.

        busqueda_afiliado.jsp ya utiliza estos IDs como contrato externo:
          - seccionVerificarDomicilio
          - divBotonActualizar
          - divResultadoActualizarOK
    --%>
    <% if (afiliadoComponenteEditable) { %>

        <div id="<portlet:namespace />seccionVerificarDomicilio"
             class="compras-verificar-contacto"
             style="display:none;">

            <label>
                Verificar
                datos
                contacto:
            </label>

            <div id="<portlet:namespace />divBotonActualizar">
                <input
                        type="button"
                        value="Actualizar"
                        onclick="return <portlet:namespace />mostrarDomicilioAfiliado();" />
            </div>

            <div id="<portlet:namespace />divResultadoActualizarOK"
                 style="display:none;">

                <p>
                    <b>
                        <liferay-ui:message key="crm-actualiza-domicilio" />
                    </b>
                </p>

            </div>
        </div>

    <% } %>

</div>