<%--
Responsabilidad:
    Renderiza los campos ocultos que preservan el contrato HTTP del formulario.
Incluido desde:
    requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    fmCompras, compras_save_token, compras_cmd, id_requerimiento_compra, sector_id, sector_id_hidden, cargo_ospim, cargo_ospim_hidden, cargo_tercerizadora
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<form action="<%= actionURL.toString() %>"
      method="post"
      enctype="multipart/form-data"
      name="<portlet:namespace />fmCompras"
      id="<portlet:namespace />fmCompras"
      class="compras-form-colector">

    <input type="hidden"
           name="<portlet:namespace />compras_save_token"
           id="<portlet:namespace />compras_save_token"
           value="<%= HtmlUtil.escape(String.valueOf(renderRequest.getAttribute("COMPRAS_SAVE_TOKEN"))) %>" />

    <input type="hidden"
           name="<portlet:namespace /><%= Constants.CMD %>"
           id="<portlet:namespace />compras_cmd"
           value="saveAll" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_compra"
           id="<portlet:namespace />id_requerimiento_compra"
           value="<%= req.getIdRequerimientoCompra() %>" />

    <input type="hidden"
           name="<portlet:namespace />sector_id"
           id="<portlet:namespace />sector_id_hidden"
           value="<%= HtmlUtil.escape(reqSectorId) %>" />

    <input type="hidden"
           name="<portlet:namespace />cargo_ospim"
           id="<portlet:namespace />cargo_ospim_hidden"
           value="<%= HtmlUtil.escape(cargoOspimVisible) %>" />

    <input type="hidden"
           name="<portlet:namespace />cargo_tercerizadora"
           id="<portlet:namespace />cargo_tercerizadora_hidden"
           value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>" />

    <input type="hidden"
           name="<portlet:namespace />recupero"
           id="<portlet:namespace />recupero_hidden"
           value="<%= recuperoPorCargoTercerizadoraActual ? "true" : "false" %>" />

    <input type="hidden"
           name="<portlet:namespace />surge"
           id="<portlet:namespace />surge_hidden"
           value="<%= HtmlUtil.escape(surgeSeleccionadoCompra) %>" />

    <% if (!esNuevo) { %>
        <input type="hidden"
               name="<portlet:namespace />legales"
               id="<portlet:namespace />legales_hidden"
               value="<%= req.isLegales() ? "true" : "false" %>" />
    <% } %>

    <input type="hidden"
           name="<portlet:namespace />afiliado_cuil_titular"
           id="<portlet:namespace />afiliado_cuil_titular"
           value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

    <input type="hidden"
           name="<portlet:namespace />afiliado_int"
           id="<portlet:namespace />afiliado_int"
           value="<%= HtmlUtil.escape(afiliadoInt) %>" />

    <input type="hidden"
           name="<portlet:namespace />id_tercerizadora"
           id="<portlet:namespace />requerimiento_id_tercerizadora_hidden"
           value="<%= HtmlUtil.escape(idTercerizadora) %>" />

    <input type="hidden"
           name="<portlet:namespace />observaciones"
           id="<portlet:namespace />observaciones_hidden"
           value="<%= HtmlUtil.escape(req.getObservacionesVisible()) %>" />

    <input type="hidden"
           name="<portlet:namespace />fecha_orden_medica"
           id="<portlet:namespace />fecha_orden_medica_hidden"
           value="<%= HtmlUtil.escape(
                   ParamUtil.getString(
                           renderRequest,
                           "fecha_orden_medica",
                           ""
                   )
           ) %>" />

    <div id="<portlet:namespace />detalle_payload"></div>
</form>
