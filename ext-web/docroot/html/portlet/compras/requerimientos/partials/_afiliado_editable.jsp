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
     class="compras-seccion compras-seccion-afiliado"
     style="<%= ocultarPanelAfiliado ? "display:none;" : "" %>">
    <fieldset class="block-labels">
        <legend>
            <liferay-ui:message key="datos-afiliado" />
        </legend>

        <table class="lfr-table compras-afiliado-contacto-layout">
            <tr>
                <td class="compras-afiliado-contacto-datos">

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

                </td>

                <% if (afiliadoComponenteEditable) { %>

                    <td class="compras-afiliado-contacto-acciones">

                        <div class="compras-verificar-contacto">

                            <label>
                                Verificar<br />
                                datos<br />
                                contacto:
                            </label>

                            <div id="<portlet:namespace />divBotonActualizar">

                                <input
                                        type="button"
                                        value="Actualizar"
                                        onclick="return <portlet:namespace />mostrarDomicilioAfiliado();" />

                            </div>

                            <div
                                    id="<portlet:namespace />divResultadoActualizarOK"
                                    style="display:none;">

                                <p>
                                    <b>
                                        <liferay-ui:message
                                                key="crm-actualiza-domicilio" />
                                    </b>
                                </p>

                            </div>

                        </div>

                    </td>

                <% } %>

            </tr>
        </table>
    </fieldset>
</div>
