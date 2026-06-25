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

boolean msgRequerimientoEnviadoACotizarConErrores =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-enviado-a-cotizar-con-errores"
        );

boolean msgCotizacionGuardada =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-cotizacion-guardada"
        );

boolean msgCotizacionCompleta =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-cotizacion-completa"
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

boolean msgPrestadoresNoEnviados =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-no-enviados"
        );

boolean msgPrestadoresSinNuevosEnvios =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-sin-nuevos-envios"
        );

boolean msgPrestadoresSinCompatiblesSector =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-sin-compatibles-sector"
        );

boolean msgPrestadoresTodosOmitidosPrevios =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-todos-omitidos-previos"
        );

boolean msgPrestadoresEmailsInvalidos =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-emails-invalidos"
        );

boolean msgPrestadoresErroresEnvio =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "cotizacion-prestadores-errores-envio"
        );

ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado resultadoNotificacionCotizacion =
        (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado)
                com.liferay.portal.kernel.servlet.SessionMessages.get(
                        renderRequest,
                        WebKeysCompras.RESULTADO_NOTIFICACION_COTIZACION
                );
%>

<c:if test="<%= mostrarMensajeRequerimientoGuardado %>">
    <div class="portlet-msg-success">
        <strong>Requerimiento de compra guardado correctamente.</strong>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID del requerimiento:
            <%= HtmlUtil.escape(idRequerimientoMensaje) %>
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
        Prestadores notificados y requerimiento pasado a A COTIZAR.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoEnviadoACotizarConErrores %>">
    <div class="portlet-msg-error">
        El requerimiento pasó a A COTIZAR porque al menos un correo
        fue aceptado por el servicio de mail. Otros envíos fallaron.
    </div>
</c:if>

<c:if test="<%= msgCotizacionGuardada %>">
    <div class="portlet-msg-success">
        Avance de cotización guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgCotizacionCompleta %>">
    <div class="portlet-msg-success">
        Cotización guardada y requerimiento pasado a COTIZADO.
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

<c:if test="<%= msgPrestadoresNoEnviados %>">
    <div class="portlet-msg-error">
        No se pudo enviar la solicitud a ningún prestador.
        El requerimiento permanece PENDIENTE.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinNuevosEnvios %>">
    <div class="portlet-msg-info">
        No se enviaron nuevas notificaciones.
        El requerimiento conserva su estado actual.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinCompatiblesSector %>">
    <div class="portlet-msg-error">
        Existen prestadores habilitados para cotizar, pero ninguno
        tiene un tipo activo asociado al sector del requerimiento.
        El requerimiento permanece PENDIENTE.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresTodosOmitidosPrevios %>">
    <div class="portlet-msg-info">
        Los prestadores compatibles ya estaban ENVIADO o PROCESANDO.
        No se reenviaron notificaciones.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresEmailsInvalidos %>">
    <div class="portlet-msg-error">
        Existen candidatos para cotizar, pero el email destino resulto
        invalido. Revise la configuracion de correo y el log.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresErroresEnvio %>">
    <div class="portlet-msg-error">
        Existen candidatos para cotizar, pero hubo errores durante
        el envio o la persistencia del resultado.
    </div>
</c:if>

<c:if test="<%= resultadoNotificacionCotizacion != null %>">
    <div class="portlet-msg-info">
        Prestadores candidatos:
        <%= resultadoNotificacionCotizacion.getTotalCandidatos() %>.
        Habilitados activos:
        <%= resultadoNotificacionCotizacion.getPrestadoresHabilitados() %>.
        Compatibles con sector:
        <%= resultadoNotificacionCotizacion.getPrestadoresCompatiblesSector() %>.
        Bloqueados por envio previo:
        <%= resultadoNotificacionCotizacion.getPrestadoresBloqueadosEstadoPrevio() %>.
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
