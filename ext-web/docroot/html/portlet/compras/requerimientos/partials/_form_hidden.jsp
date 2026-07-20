<form action="<%= actionURL.toString() %>"
      method="post"
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

    <div id="<portlet:namespace />detalle_payload"></div>
</form>
