<%
/*
 * La identidad del afiliado se selecciona exclusivamente durante el alta.
 *
 * Actualizar los datos de contacto es una capacidad distinta:
 * no modifica el afiliado asociado al requerimiento.
 */
boolean afiliadoComponenteEditable =
        esNuevo
        && modoEditable
        && puedeEditarEstructuraPantalla;

/*
 * La actualización de los datos de contacto debe estar disponible
 * en cualquier estado y modo de visualización del requerimiento.
 *
 * Se conserva únicamente el permiso funcional de ABM.
 */
boolean puedeActualizarContactoAfiliado =
        puedeABM;

boolean ocultarPanelAfiliado =
        !mostrarPanelAfiliadoEnVista;
%>

<div id="<portlet:namespace />afiliado_requerimiento_panel"
     class="compras-seccion compras-seccion-afiliado"
     style="<%= ocultarPanelAfiliado ? "display:none;" : "" %>">

    <table class="lfr-table compras-afiliado-contacto-layout">
        <tr>
            <td class="compras-afiliado-contacto-datos">

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

            </td>

            <% if (puedeActualizarContactoAfiliado) { %>
                <td class="compras-afiliado-contacto-acciones">

                    <div id="<portlet:namespace />seccionVerificarDomicilio"
                         class="compras-verificar-contacto">

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

                        <div id="<portlet:namespace />divResultadoActualizarOK"
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

</div>