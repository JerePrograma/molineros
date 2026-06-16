<c:if test="<%= mostrarMensajeRequerimientoGuardado %>">
    <div class="portlet-msg-success">
        <strong>Requerimiento de compra guardado correctamente.</strong>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID del requerimiento: <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(comprasDetallesGuardados) %>">
            <br />
            Detalles guardados/procesados: <%= HtmlUtil.escape(comprasDetallesGuardados) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "estado-requerimiento-compra-actualizado") %>">
    <div class="portlet-msg-success">El estado del requerimiento fue actualizado correctamente.</div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-notificados") %>">
    <div class="portlet-msg-success">
        El requerimiento paso a cotizaciones y se notifico a todos los prestadores candidatos.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "estado-requerimiento-compra-actualizado") %>">
    <div class="portlet-msg-success">
        El estado del requerimiento fue actualizado correctamente.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-notificados") %>">
    <div class="portlet-msg-success">
        Los prestadores candidatos fueron notificados correctamente.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-notificados-con-errores") %>">
    <div class="portlet-msg-error">
        Uno o más prestadores no pudieron ser notificados.
        Verifique los correos configurados y revise el log de la aplicación.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-sin-destinatarios") %>">
    <div class="portlet-msg-info">
        No existen prestadores habilitados pendientes de notificación
        para el sector del requerimiento.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-error") %>">
    <div class="portlet-msg-error">
        Falló el proceso general de notificación a prestadores.
        El estado del requerimiento no fue revertido.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-sin-resultado") %>">
    <div class="portlet-msg-error">
        El proceso de notificación finalizó sin devolver un resultado verificable.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-sin-destinatarios") %>">
    <div class="portlet-msg-info">
        El requerimiento paso a cotizaciones, pero no existen prestadores habilitados pendientes de notificacion para su sector.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-error") %>">
    <div class="portlet-msg-error">
        El requerimiento paso a cotizaciones, pero fallo el proceso general de notificacion a prestadores.
    </div>
</c:if>

<c:if test="<%= com.liferay.portal.kernel.servlet.SessionMessages.contains(renderRequest, "cotizaciones-prestadores-sin-resultado") %>">
    <div class="portlet-msg-error">
        El proceso de notificacion finalizo sin devolver un resultado verificable.
    </div>
</c:if>

<c:if test="<%= msgDetalleGuardado %>">
    <div class="portlet-msg-success">Detalle del requerimiento guardado correctamente.</div>
</c:if>

<c:if test="<%= msgDetalleBorrado %>">
    <div class="portlet-msg-success">Detalle del requerimiento eliminado correctamente.</div>
</c:if>

<c:if test="<%= msgArticuloGuardado %>">
    <div class="portlet-msg-success">Articulo de compra guardado correctamente.</div>
</c:if>

<c:if test="<%= msgArticuloBorrado %>">
    <div class="portlet-msg-success">Articulo de compra eliminado correctamente.</div>
</c:if>

<c:if test="<%= msgRequerimientoAnulado %>">
    <div class="portlet-msg-success">Requerimiento de compra anulado correctamente.</div>
</c:if>

<c:if test="<%= mostrarErrorGenericoCompra %>">
    <div class="portlet-msg-error">
        <strong>No se pudo procesar el requerimiento de compra.</strong>
    </div>
</c:if>

<c:if test="<%= !WebKeysCompras.isEmpty(errorParaAlert) %>">
    <div class="portlet-msg-error">
        <strong>No se pudo guardar/procesar el requerimiento de compra.</strong>
        <br />
        <%= HtmlUtil.escape(errorParaAlert) %>

        <c:if test="<%= !WebKeysCompras.isEmpty(errorCampoCompra) %>">
            <br />
            Campo relacionado: <strong><%= HtmlUtil.escape(errorCampoCompra) %></strong>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID activo: <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && puedeABM && !editablePorEstado %>">
    <div class="portlet-msg-info">El requerimiento solo puede editarse en estado Borrador.</div>
</c:if>
