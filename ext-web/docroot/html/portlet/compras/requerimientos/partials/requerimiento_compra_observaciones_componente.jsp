<%--
Responsabilidad:
    Renderiza las observaciones del requerimiento en edición o consulta.
Incluido desde:
    requerimiento_compra_detalle_embebido.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    observaciones, observacion_interna
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<fieldset class="block-labels compras-observaciones">
    <legend>
        Observación
        <a href="javascript:void(0)"
           onclick="return comprasHelp(event, '<portlet:namespace />helpObservaciones');">
            <img style="height: 25px; width: 25px; vertical-align: middle;"
                 src="/html/images/help.png"
                 title="Ayuda para observaciones"
                 alt="Ayuda" />
        </a>
    </legend>

    <% if (puedeEditarEstructuraPantalla) { %>
        <table class="lfr-table">
            <tr>
                <td>
                    <textarea id="<portlet:namespace />observaciones"
                              cols="100"
                              rows="4"><%= HtmlUtil.escape(
                                      req.getObservacionesVisible()
                              ) %></textarea>
                </td>
            </tr>
        </table>
    <% } else { %>
        <div class="compras-observaciones-vista">
            <%= HtmlUtil.escape(
                    req.getObservacionesVisible()
            ) %>
        </div>
    <% } %>
</fieldset>

<div id="<portlet:namespace />helpObservaciones"
     class="containerPlus draggable compras-container-ayuda {buttons:'c', skin:'default', width:'700',title:'Ayuda - Observación',closed:'true'}"
     style="top: 500px; left: 200px">
    Son observaciones que se envian por correo al prestador junto al pedido de cotización.
</div>

    <%
    boolean observacionInternaGuardadoIndependiente =
            !puedeEditarEstructuraPantalla
            && req.puedeEditarObservacionInterna()
            && user != null
            && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_AUTORIZAR_COMPRAS);
    %>
    <fieldset class="block-labels compras-observaciones">
        <legend>
            Observación interna
            <a href="javascript:void(0)"
               onclick="return comprasHelp(event, '<portlet:namespace />helpObservacionInterna');">
                <img style="height: 25px; width: 25px; vertical-align: middle;"
                     src="/html/images/help.png"
                     title="Ayuda para observaciones internas"
                     alt="Ayuda" />
            </a>
        </legend>
        <% if (observacionInternaGuardadoIndependiente) {
            PortletURL observacionInternaURL = renderResponse.createActionURL();
            observacionInternaURL.setWindowState(WindowState.MAXIMIZED);
            observacionInternaURL.setParameter("struts_action", "/compras/editar_requerimiento");
            String observacionInternaValor = req.getObservacionInternaVisible();
            if (ParamUtil.getBoolean(renderRequest, "compras_error", false)
                    && "saveObservacionInterna".equals(
                            ParamUtil.getString(renderRequest, "compras_operacion", ""))) {
                observacionInternaValor = ParamUtil.getString(renderRequest, "observacion_interna", "");
            }
        %>
        <form action="<%= observacionInternaURL.toString() %>"
              method="post"
              id="<portlet:namespace />observacionInternaForm"
              onsubmit="return <portlet:namespace />enviarOperacionCompra(this);">
            <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>"
                   value="saveObservacionInterna" />
            <input type="hidden" name="<portlet:namespace />id_requerimiento_compra"
                   value="<%= req.getIdRequerimientoCompra() %>" />
            <input type="hidden" name="<portlet:namespace />compras_save_token"
                   value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
            <table class="lfr-table">
                <tr>
                    <td>
                        <textarea id="<portlet:namespace />observacion_interna"
                                  name="<portlet:namespace />observacion_interna"
                                  cols="100" rows="4"><%= HtmlUtil.escape(observacionInternaValor) %></textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="submit"
                               id="<portlet:namespace />btnGuardarObservacionInterna"
                               name="<portlet:namespace />btnGuardarObservacionInterna"
                               value="Guardar observación interna" />
                    </td>
                </tr>
            </table>
        </form>
        <% } else if (puedeEditarEstructuraPantalla) { %>
        <table class="lfr-table">
            <tr>
                <td>
                    <textarea id="<portlet:namespace />observacion_interna"
                              cols="100"
                              rows="4"><%= HtmlUtil.escape(
                                      req.getObservacionInternaVisible()
                              ) %></textarea>
                </td>
            </tr>
        </table>
        <% } else { %>
            <div class="compras-observaciones-vista">
                <%= HtmlUtil.escape(
                        req.getObservacionInternaVisible()
                ) %>
            </div>
        <% } %>
    </fieldset>

    <div id="<portlet:namespace />helpObservacionInterna"
         class="containerPlus draggable compras-container-ayuda {buttons:'c', skin:'default', width:'700',title:'Ayuda - Observación interna',closed:'true'}"
         style="top: 500px; left: 200px">
        Son observaciones para uso interno y no se comparten con personal externo.
    </div>
