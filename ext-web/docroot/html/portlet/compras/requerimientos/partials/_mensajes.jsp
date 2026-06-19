<%
boolean msgEstadoRequerimientoActualizado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "estado-requerimiento-compra-actualizado"
        );

boolean msgRequerimientoEnviadoACotizar =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-enviado-a-cotizar"
        );

boolean msgCotizacionGuardada =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-cotizacion-guardada"
        );

boolean msgCotizacionCerrada =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-cotizacion-cerrada"
        );

boolean msgPrestadoresNotificados =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-notificados"
        );

boolean msgPrestadoresConErrores =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-notificados-con-errores"
        );

boolean msgPrestadoresSinResultado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-sin-resultado"
        );

ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado resultadoNotificacionCotizacion =
        (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado)
                renderRequest.getAttribute(WebKeysCompras.RESULTADO_NOTIFICACION_COTIZACION);
%>

<c:if test="<%= mostrarMensajeRequerimientoGuardado %>">
    <div class="portlet-msg-success">
        <strong>Requerimiento de compra guardado correctamente.</strong>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID del requerimiento:
            <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(comprasDetallesGuardados) %>">
            <br />
            Detalles guardados/procesados:
            <%= HtmlUtil.escape(comprasDetallesGuardados) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= msgEstadoRequerimientoActualizado %>">
    <div class="portlet-msg-success">
        El estado del requerimiento fue actualizado correctamente.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoEnviadoACotizar %>">
    <div class="portlet-msg-success">
        El requerimiento fue enviado a cotizar correctamente.
    </div>
</c:if>

<c:if test="<%= msgCotizacionGuardada %>">
    <div class="portlet-msg-success">
        Avance de cotización guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgCotizacionCerrada %>">
    <div class="portlet-msg-success">
        Cotización cerrada correctamente.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresNotificados %>">
    <div class="portlet-msg-success">
        Los prestadores candidatos fueron notificados correctamente.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresConErrores %>">
    <div class="portlet-msg-error">
        Uno o más prestadores no pudieron ser notificados.
        Verifique los correos configurados y revise el log de la aplicación.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinResultado %>">
    <div class="portlet-msg-error">
        El proceso de notificación finalizó sin devolver
        un resultado verificable.
    </div>
</c:if>

<c:if test="<%= resultadoNotificacionCotizacion != null %>">
    <div class="portlet-msg-info">
        Prestadores candidatos:
        <%= resultadoNotificacionCotizacion.getTotalCandidatos() %>.
        Enviados:
        <%= resultadoNotificacionCotizacion.getEnviados() %>.
        Errores:
        <%= resultadoNotificacionCotizacion.getErrores() %>.
        Correos inválidos:
        <%= resultadoNotificacionCotizacion.getEmailsInvalidos() %>.
        Omitidos:
        <%= resultadoNotificacionCotizacion.getOmitidos() %>.
    </div>
</c:if>

<c:if test="<%= msgDetalleGuardado %>">
    <div class="portlet-msg-success">
        Detalle del requerimiento guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgDetalleBorrado %>">
    <div class="portlet-msg-success">
        Detalle del requerimiento eliminado correctamente.
    </div>
</c:if>

<c:if test="<%= msgArticuloGuardado %>">
    <div class="portlet-msg-success">
        Artículo de compra guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgArticuloBorrado %>">
    <div class="portlet-msg-success">
        Artículo de compra eliminado correctamente.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoAnulado %>">
    <div class="portlet-msg-success">
        Requerimiento de compra anulado correctamente.
    </div>
</c:if>

<c:if test="<%= mostrarErrorGenericoCompra %>">
    <div class="portlet-msg-error">
        <strong>
            No se pudo procesar el requerimiento de compra.
        </strong>
    </div>
</c:if>

<c:if test="<%= !WebKeysCompras.isEmpty(errorParaAlert) %>">
    <div class="portlet-msg-error">
        <strong>
            No se pudo guardar/procesar el requerimiento de compra.
        </strong>

        <br />

        <%= HtmlUtil.escape(errorParaAlert) %>

        <c:if test="<%= !WebKeysCompras.isEmpty(errorCampoCompra) %>">
            <br />

            Campo relacionado:
            <strong>
                <%= HtmlUtil.escape(errorCampoCompra) %>
            </strong>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />

            ID activo:
            <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && !puedeABM && !puedeCotizar %>">
    <div class="portlet-msg-error">
        No posee permisos para modificar requerimientos de compras.
    </div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada
        && puedeABM
        && !editablePorEstado %>">

    <div class="portlet-msg-info">
        La estructura del requerimiento solo puede editarse en estado Pendiente.
    </div>
</c:if>