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
    observaciones
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<fieldset class="block-labels compras-observaciones">
    <legend>Observación / Descripción</legend>

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