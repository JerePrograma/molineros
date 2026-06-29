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

ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado
        resultadoNotificacionCotizacion =
        (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado)
                com.liferay.portal.kernel.servlet.SessionMessages.get(
                        renderRequest,
                        WebKeysCompras.RESULTADO_NOTIFICACION_COTIZACION
                );

boolean hayResultadoNotificacion =
        resultadoNotificacionCotizacion != null;

java.util.List<
        ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
> detallesNotificacionResultado = null;

int cantidadDetallesIncidencia = 0;

if (hayResultadoNotificacion) {
    detallesNotificacionResultado =
            resultadoNotificacionCotizacion.getDetalles();

    if (detallesNotificacionResultado != null) {
        for (int i = 0;
                i < detallesNotificacionResultado.size();
                i++) {

            ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                    detalle =
                    detallesNotificacionResultado.get(i);

            if (detalle == null) {
                continue;
            }

            boolean esError =
                    ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                            .RESULTADO_ERROR
                            .equals(
                                    detalle.getResultado()
                            );

            boolean esEmailInvalido =
                    ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                            .RESULTADO_EMAIL_INVALIDO
                            .equals(
                                    detalle.getResultado()
                            );

            if (esError
                    || esEmailInvalido
                    || detalle.isEmailRealInvalidoAdvertido()) {

                cantidadDetallesIncidencia++;
            }
        }
    }
}

boolean hayDetalleIncidencias =
        cantidadDetallesIncidencia > 0;

/*
 * Mensaje de respaldo.
 *
 * Se utiliza únicamente si la acción agregó un SessionMessage,
 * pero no se pudo recuperar el objeto de resultado.
 *
 * De esta forma no se muestran varias cajas con el mismo resultado.
 */
String claseMensajeNotificacionFallback = null;
String tituloMensajeNotificacionFallback = null;
String detalleMensajeNotificacionFallback = null;

if (!hayResultadoNotificacion) {
    if (msgPrestadoresSinResultado) {
        claseMensajeNotificacionFallback =
                "portlet-msg-error";

        tituloMensajeNotificacionFallback =
                "No se pudo verificar el resultado de la notificación.";

        detalleMensajeNotificacionFallback =
                "Contacte a Sistemas antes de reintentar.";

    } else if (msgRequerimientoEnviadoACotizarConErrores
            || msgPrestadoresConErrores) {

        claseMensajeNotificacionFallback =
                "portlet-msg-alert";

        tituloMensajeNotificacionFallback =
                "La notificación terminó con incidencias.";

        detalleMensajeNotificacionFallback =
                "Algunos prestadores requieren revisión.";

    } else if (msgRequerimientoEnviadoACotizar
            || msgPrestadoresNotificados) {

        claseMensajeNotificacionFallback =
                "portlet-msg-success";

        tituloMensajeNotificacionFallback =
                "La notificación se completó correctamente.";

        detalleMensajeNotificacionFallback =
                "Las solicitudes fueron enviadas.";

    } else if (msgPrestadoresSinCompatiblesSector) {
        claseMensajeNotificacionFallback =
                "portlet-msg-alert";

        tituloMensajeNotificacionFallback =
                "No hay prestadores compatibles con el sector.";

        detalleMensajeNotificacionFallback =
                "Revise la configuración de prestadores del sector.";

    } else if (msgPrestadoresEmailsInvalidos) {
        claseMensajeNotificacionFallback =
                "portlet-msg-error";

        tituloMensajeNotificacionFallback =
                "No se pudo completar la notificación.";

        detalleMensajeNotificacionFallback =
                "Uno o más prestadores no tienen un email válido.";

    } else if (msgPrestadoresErroresEnvio) {
        claseMensajeNotificacionFallback =
                "portlet-msg-error";

        tituloMensajeNotificacionFallback =
                "No se pudo completar la notificación.";

        detalleMensajeNotificacionFallback =
                "Contacte a Sistemas antes de reintentar.";

    } else if (msgPrestadoresTodosOmitidosPrevios) {
        claseMensajeNotificacionFallback =
                "portlet-msg-info";

        tituloMensajeNotificacionFallback =
                "No se realizaron nuevos envíos.";

        detalleMensajeNotificacionFallback =
                "Los prestadores ya habían sido notificados "
                        + "o estaban siendo procesados.";

    } else if (msgPrestadoresNoEnviados
            || msgPrestadoresSinNuevosEnvios) {

        claseMensajeNotificacionFallback =
                "portlet-msg-info";

        tituloMensajeNotificacionFallback =
                "No se realizaron nuevos envíos.";

        detalleMensajeNotificacionFallback =
                "El requerimiento conserva su estado actual.";
    }
}

boolean hayMensajeNotificacionFallback =
        !WebKeysCompras.isEmpty(
                tituloMensajeNotificacionFallback
        );
%>

<c:if test="<%= mostrarMensajeRequerimientoGuardado %>">
    <div class="portlet-msg-success">
        <strong>
            Requerimiento de compra guardado correctamente.
        </strong>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />

            ID del requerimiento:
            <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%=
        msgEstadoRequerimientoActualizado
                && !hayResultadoNotificacion
%>">
    <div class="portlet-msg-success">
        El estado del requerimiento fue actualizado correctamente.
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

<c:if test="<%= hayMensajeNotificacionFallback %>">
    <div class="<%= claseMensajeNotificacionFallback %>">
        <strong>
            <%= HtmlUtil.escape(
                    tituloMensajeNotificacionFallback
            ) %>
        </strong>

        <c:if test="<%=
                !WebKeysCompras.isEmpty(
                        detalleMensajeNotificacionFallback
                )
        %>">
            <br />

            <%= HtmlUtil.escape(
                    detalleMensajeNotificacionFallback
            ) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= hayResultadoNotificacion %>">

    <%
    int enviadosResumen =
            resultadoNotificacionCotizacion.getEnviados();

    int erroresResumen =
            resultadoNotificacionCotizacion.getErrores();

    int emailsInvalidosResumen =
            resultadoNotificacionCotizacion.getEmailsInvalidos();

    int omitidosResumen =
            resultadoNotificacionCotizacion.getOmitidos();

    int advertenciasQaResumen =
            resultadoNotificacionCotizacion
                    .getEmailsRealesInvalidosAdvertidos();

    int pendientesSinClasificarResumen =
            resultadoNotificacionCotizacion
                    .getPendientesSinClasificar();

    int candidatosResumen =
            resultadoNotificacionCotizacion
                    .getTotalCandidatos();

    int habilitadosResumen =
            resultadoNotificacionCotizacion
                    .getPrestadoresHabilitados();

    int compatiblesResumen =
            resultadoNotificacionCotizacion
                    .getPrestadoresCompatiblesSector();

    int bloqueadosResumen =
            resultadoNotificacionCotizacion
                    .getPrestadoresBloqueadosEstadoPrevio();

    int incidenciasResumen =
            erroresResumen + emailsInvalidosResumen;

    String claseResumen =
            "portlet-msg-info";

    String tituloResumen;
    String descripcionResumen;

    if (pendientesSinClasificarResumen > 0) {
        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo verificar el resultado completo.";

        descripcionResumen =
                "Contacte a Sistemas antes de reintentar.";

    } else if (enviadosResumen > 0
            && (
            incidenciasResumen > 0
                    || advertenciasQaResumen > 0
    )) {
        claseResumen =
                "portlet-msg-alert";

        if (msgRequerimientoEnviadoACotizarConErrores) {
            tituloResumen =
                    "El requerimiento pasó a A COTIZAR "
                            + "con incidencias.";
        } else {
            tituloResumen =
                    "La notificación terminó con incidencias.";
        }

        StringBuilder descripcion =
                new StringBuilder();

        if (enviadosResumen == 1) {
            descripcion.append(
                    "Se envió una solicitud."
            );
        } else {
            descripcion.append(
                    "Se enviaron "
            );

            descripcion.append(
                    enviadosResumen
            );

            descripcion.append(
                    " solicitudes."
            );
        }

        if (cantidadDetallesIncidencia > 0) {
            descripcion.append(" ");

            if (cantidadDetallesIncidencia == 1) {
                descripcion.append(
                        "Un prestador requiere revisión."
                );
            } else {
                descripcion.append(
                        cantidadDetallesIncidencia
                );

                descripcion.append(
                        " prestadores requieren revisión."
                );
            }
        }

        descripcionResumen =
                descripcion.toString();

    } else if (enviadosResumen > 0) {
        claseResumen =
                "portlet-msg-success";

        if (msgRequerimientoEnviadoACotizar) {
            tituloResumen =
                    "El requerimiento pasó a A COTIZAR.";
        } else {
            tituloResumen =
                    "Notificación completada.";
        }

        if (enviadosResumen == 1) {
            descripcionResumen =
                    "Se envió una solicitud de cotización.";
        } else {
            descripcionResumen =
                    "Se enviaron "
                            + enviadosResumen
                            + " solicitudes de cotización.";
        }

    } else if (erroresResumen > 0) {
        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo completar la notificación.";

        if (erroresResumen == 1) {
            descripcionResumen =
                    "No se pudo notificar a un prestador. "
                            + "El requerimiento conserva su estado.";
        } else {
            descripcionResumen =
                    "No se pudo notificar a "
                            + erroresResumen
                            + " prestadores. "
                            + "El requerimiento conserva su estado.";
        }

    } else if (emailsInvalidosResumen > 0) {
        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo completar la notificación.";

        if (emailsInvalidosResumen == 1) {
            descripcionResumen =
                    "Un prestador no tiene un email válido registrado.";
        } else {
            descripcionResumen =
                    emailsInvalidosResumen
                            + " prestadores no tienen un email "
                            + "válido registrado.";
        }

    } else if (candidatosResumen <= 0
            && habilitadosResumen <= 0) {

        claseResumen =
                "portlet-msg-info";

        tituloResumen =
                "No hay prestadores habilitados para cotizar.";

        descripcionResumen =
                "El requerimiento conserva su estado actual.";

    } else if (candidatosResumen <= 0
            && compatiblesResumen <= 0) {

        claseResumen =
                "portlet-msg-alert";

        tituloResumen =
                "No hay prestadores compatibles con el sector.";

        descripcionResumen =
                "Revise la configuración de tipos de prestador "
                        + "asociados al sector.";

    } else if (candidatosResumen <= 0
            && bloqueadosResumen > 0) {

        claseResumen =
                "portlet-msg-info";

        tituloResumen =
                "No se realizaron nuevos envíos.";

        descripcionResumen =
                "Los prestadores compatibles ya habían sido "
                        + "notificados o estaban siendo procesados.";

    } else if (omitidosResumen > 0) {
        claseResumen =
                "portlet-msg-info";

        tituloResumen =
                "No se realizaron nuevos envíos.";

        descripcionResumen =
                "Los candidatos ya estaban siendo procesados "
                        + "por otra ejecución.";

    } else {
        claseResumen =
                "portlet-msg-info";

        tituloResumen =
                "No se encontraron prestadores para notificar.";

        descripcionResumen =
                "El requerimiento conserva su estado actual.";
    }
    %>

    <div class="<%= claseResumen %>">
        <strong>
            <%= HtmlUtil.escape(tituloResumen) %>
        </strong>

        <br />

        <%= HtmlUtil.escape(descripcionResumen) %>
    </div>

    <c:if test="<%= hayDetalleIncidencias %>">

        <div style="margin-top: 15px;">
            <h4>
                Prestadores con inconvenientes
            </h4>

            <div style="overflow-x: auto;">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th>Prestador</th>
                            <th>Estado</th>
                            <th>Motivo</th>
                        </tr>
                    </thead>

                    <tbody>
                        <%
                        for (int i = 0;
                                i < detallesNotificacionResultado.size();
                                i++) {

                            ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                    detalleNotificacion =
                                    detallesNotificacionResultado.get(i);

                            if (detalleNotificacion == null) {
                                continue;
                            }

                            boolean esErrorTecnico =
                                    ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                            .RESULTADO_ERROR
                                            .equals(
                                                    detalleNotificacion
                                                            .getResultado()
                                            );

                            boolean esEmailInvalido =
                                    ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                            .RESULTADO_EMAIL_INVALIDO
                                            .equals(
                                                    detalleNotificacion
                                                            .getResultado()
                                            );

                            boolean esAdvertenciaQa =
                                    detalleNotificacion
                                            .isEmailRealInvalidoAdvertido();

                            if (!esErrorTecnico
                                    && !esEmailInvalido
                                    && !esAdvertenciaQa) {

                                continue;
                            }

                            String nombrePrestador =
                                    detalleNotificacion
                                            .getPrestador();

                            if (WebKeysCompras.isEmpty(
                                    nombrePrestador
                            )) {
                                nombrePrestador =
                                        "Prestador #"
                                                + detalleNotificacion
                                                .getIdPrestador();
                            }

                            String claseEstado;
                            String estadoVisible;
                            String motivoVisible;

                            if (esErrorTecnico) {
                                claseEstado =
                                        "label label-important";

                                estadoVisible =
                                        "Error técnico";

                                String etapa =
                                        detalleNotificacion
                                                .getEtapa();

                                if ("VALIDACION".equals(etapa)) {
                                    motivoVisible =
                                            "No se pudo preparar la "
                                                    + "notificación. "
                                                    + "Contacte a Sistemas.";

                                } else if ("RESERVA".equals(etapa)) {
                                    motivoVisible =
                                            "No se pudo iniciar el envío. "
                                                    + "Contacte a Sistemas "
                                                    + "antes de reintentar.";

                                } else if ("ENVIO".equals(etapa)) {
                                    motivoVisible =
                                            "El correo no pudo enviarse. "
                                                    + "Contacte a Sistemas "
                                                    + "antes de reintentar.";

                                } else if ("PERSISTENCIA".equals(etapa)) {
                                    motivoVisible =
                                            "El resultado del envío no pudo "
                                                    + "confirmarse. No reintente "
                                                    + "hasta verificarlo con "
                                                    + "Sistemas.";

                                } else {
                                    motivoVisible =
                                            "No se pudo procesar la "
                                                    + "notificación. "
                                                    + "Contacte a Sistemas "
                                                    + "antes de reintentar.";
                                }

                                if (esAdvertenciaQa) {
                                    motivoVisible +=
                                            " El email registrado del "
                                                    + "prestador también "
                                                    + "debe revisarse.";
                                }

                            } else if (esEmailInvalido) {
                                claseEstado =
                                        "label label-warning";

                                estadoVisible =
                                        "Email inválido";

                                motivoVisible =
                                        "El prestador no tiene un email "
                                                + "válido registrado.";

                            } else {
                                claseEstado =
                                        "label label-warning";

                                estadoVisible =
                                        "Revisar email";

                                motivoVisible =
                                        "El email registrado no es válido. "
                                                + "El envío de prueba fue "
                                                + "redirigido al destinatario "
                                                + "temporal.";
                            }
                        %>

                            <tr>
                                <td>
                                    <strong>
                                        <%= HtmlUtil.escape(
                                                nombrePrestador
                                        ) %>
                                    </strong>

                                    <br />

                                    ID:
                                    <%= detalleNotificacion
                                            .getIdPrestador() %>
                                </td>

                                <td>
                                    <span class="<%= claseEstado %>">
                                        <%= HtmlUtil.escape(
                                                estadoVisible
                                        ) %>
                                    </span>
                                </td>

                                <td>
                                    <%= HtmlUtil.escape(
                                            motivoVisible
                                    ) %>
                                </td>
                            </tr>

                        <%
                        }
                        %>
                    </tbody>
                </table>
            </div>
        </div>

    </c:if>

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

<c:if test="<%=
        mostrarErrorGenericoCompra
                && !hayResultadoNotificacion
                && !hayMensajeNotificacionFallback
%>">
    <div class="portlet-msg-error">
        <strong>
            No se pudo procesar el requerimiento de compra.
        </strong>
    </div>
</c:if>

<c:if test="<%=
        !WebKeysCompras.isEmpty(errorParaAlert)
                && !hayResultadoNotificacion
                && !hayMensajeNotificacionFallback
%>">
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