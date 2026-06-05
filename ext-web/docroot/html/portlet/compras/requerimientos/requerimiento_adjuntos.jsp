<form action="<portlet:actionURL><portlet:param name="struts_action" value="/compras/upload_imagenes_requerimiento" /></portlet:actionURL>"
      method="post"
      name="<portlet:namespace />compra_img_fm"
      id="<portlet:namespace />compra_img_fm"
      enctype="multipart/form-data">

    <fieldset class="block-labels">
        <legend>Archivos del requerimiento</legend>

        <liferay-ui:error key="errorUploadFile"
                          message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

        <input type="file"
               name="importa_imagenes"
               id="importa_imagenes" />

        <label>Descripción:</label>

        <input id="<portlet:namespace />descripcionFile"
               name="<portlet:namespace />descripcionFile"
               size="90"
               maxlength="120"
               type="text"
               value="" />

        <input type="hidden"
               name="<portlet:namespace />imagen"
               id="<portlet:namespace />imagen"
               value="" />

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               id="<portlet:namespace />id_requerimiento_compra_img"
               value="<%= req.getIdRequerimientoCompra() %>" />

        <input type="button"
               value="Subir archivo"
               onclick="return <portlet:namespace />uploadImagenRequerimientoCompra();" />
    </fieldset>

    <div id="<portlet:namespace />listado_imagenes_requerimiento">
        <jsp:include page="/html/portlet/compras/requerimientos/requerimiento_imagenes_search_documentos.jsp" />
    </div>
</form>