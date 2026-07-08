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
int cantidadErroresEtapaReserva = 0;
int cantidadErroresOtrasEtapas = 0;

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

            if (esError) {
                if ("RESERVA".equals(
                        detalle.getEtapa()
                )) {
                    cantidadErroresEtapaReserva++;
                } else {
                    cantidadErroresOtrasEtapas++;
                }
            }

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

<style type="text/css">
    .compras-notificacion-resumen {
        margin-bottom: 12px;
    }

    .compras-notificacion-acciones {
        margin-top: 8px;
    }

    .compras-notificacion-enlace {
        display: inline-block;
        padding: 6px 11px;

        border: 1px solid #b8b8b8;
        border-radius: 3px;

        background: #f5f5f5;
        color: #333333;

        font-size: 12px;
        font-weight: bold;
        line-height: 18px;

        text-decoration: none;
        cursor: pointer;
    }

    .compras-notificacion-enlace:hover,
    .compras-notificacion-enlace:focus {
        border-color: #8c8c8c;
        background: #e9e9e9;
        color: #111111;
        text-decoration: none;
    }

    .compras-notificacion-detalle {
        margin-top: 10px;

        border: 1px solid #d1d1d1;
        border-radius: 3px;

        background: #ffffff;
    }

    .compras-notificacion-detalle-cabecera {
        padding: 10px 12px;

        border-bottom: 1px solid #d1d1d1;
        background: #f3f3f3;
    }

    .compras-notificacion-detalle-titulo {
        margin: 0;

        color: #333333;

        font-size: 14px;
        font-weight: bold;
        line-height: 20px;
    }

    .compras-notificacion-detalle-aclaracion {
        margin-top: 3px;

        color: #666666;

        font-size: 12px;
        line-height: 17px;
    }

    .compras-notificacion-tabla-contenedor {
        width: 100%;
        overflow-x: auto;
    }

    .compras-notificacion-tabla {
        width: 100%;
        margin: 0;

        border: 0;
        border-collapse: collapse;
        border-spacing: 0;
    }

    .compras-notificacion-tabla th {
        padding: 8px 10px;

        border-bottom: 1px solid #c7c7c7;

        background: #e9e9e9;
        color: #333333;

        font-size: 12px;
        font-weight: bold;
        line-height: 18px;

        text-align: left;
        vertical-align: middle;
    }

    .compras-notificacion-tabla td {
        padding: 9px 10px;

        border-bottom: 1px solid #e2e2e2;

        color: #333333;

        font-size: 12px;
        line-height: 18px;

        vertical-align: top;
    }

    .compras-notificacion-tabla tbody tr:last-child td {
        border-bottom: 0;
    }

    .compras-notificacion-prestador {
        color: #222222;
        font-weight: bold;
    }

    .compras-notificacion-prestador-id {
        display: block;
        margin-top: 2px;

        color: #777777;

        font-size: 11px;
        font-weight: normal;
        line-height: 15px;
    }

    .compras-notificacion-estado {
        display: inline-block;
        padding: 2px 7px;

        border-radius: 3px;

        font-size: 11px;
        font-weight: bold;
        line-height: 16px;

        white-space: nowrap;
    }

    .compras-notificacion-estado-error {
        border: 1px solid #b94a48;

        background: #f2dede;
        color: #8a1f1d;
    }

    .compras-notificacion-estado-advertencia {
        border: 1px solid #c09853;

        background: #fcf8e3;
        color: #7a5b15;
    }

    .compras-notificacion-estado-pendiente {
        border: 1px solid #999999;

        background: #eeeeee;
        color: #555555;
    }

    .compras-notificacion-motivo {
        max-width: 580px;

        color: #555555;

        white-space: normal;
        word-break: normal;
        overflow-wrap: break-word;
    }

    .compras-notificacion-contador {
        display: inline-block;
        min-width: 18px;
        margin-left: 5px;
        padding: 1px 6px;

        border-radius: 10px;

        background: #666666;
        color: #ffffff;

        font-size: 11px;
        font-weight: bold;
        line-height: 16px;

        text-align: center;
    }

    @media screen and (max-width: 700px) {
        .compras-notificacion-tabla th,
        .compras-notificacion-tabla td {
            padding: 7px;
        }

        .compras-notificacion-motivo {
            min-width: 240px;
        }
    }
</style>

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

    /*
     * Se considera fallo general de reserva cuando:
     *
     * - no se envió ninguna solicitud;
     * - todos los candidatos terminaron en error;
     * - todos esos errores ocurrieron en la etapa RESERVA;
     * - no hubo errores de otra etapa;
     * - no se trató de emails inválidos.
     *
     * En ese escenario no existen múltiples inconvenientes
     * independientes de prestadores. Existe un único fallo técnico
     * que afectó a todos los candidatos.
     */
    boolean falloGlobalReserva =
            candidatosResumen > 0
                    && enviadosResumen == 0
                    && erroresResumen == candidatosResumen
                    && emailsInvalidosResumen == 0
                    && cantidadErroresEtapaReserva == erroresResumen
                    && cantidadErroresOtrasEtapas == 0;

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

    } else if (falloGlobalReserva) {
        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo iniciar la notificación.";

        if (candidatosResumen == 1) {
            descripcionResumen =
                    "Ocurrió un error técnico general antes de iniciar "
                            + "el envío. No se envió ninguna solicitud. "
                            + "El prestador permanece pendiente y el "
                            + "requerimiento conserva su estado actual.";
        } else {
            descripcionResumen =
                    "Ocurrió un error técnico general antes de iniciar "
                            + "los envíos. No se envió ninguna solicitud. "
                            + "Los "
                            + candidatosResumen
                            + " prestadores permanecen pendientes y el "
                            + "requerimiento conserva su estado actual.";
        }

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

    } else if (erroresResumen > 0
            && emailsInvalidosResumen > 0) {

        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo completar la notificación.";

        descripcionResumen =
                erroresResumen
                        + (
                        erroresResumen == 1
                                ? " prestador tuvo un error técnico"
                                : " prestadores tuvieron errores técnicos"
                )
                        + " y "
                        + emailsInvalidosResumen
                        + (
                        emailsInvalidosResumen == 1
                                ? " prestador no tiene un email válido. "
                                : " prestadores no tienen un email válido. "
                )
                        + "El requerimiento conserva su estado actual.";

    } else if (erroresResumen > 0) {
        claseResumen =
                "portlet-msg-error";

        tituloResumen =
                "No se pudo completar la notificación.";

        if (erroresResumen == 1) {
            descripcionResumen =
                    "No se pudo notificar a un prestador. "
                            + "El requerimiento conserva su estado actual.";
        } else {
            descripcionResumen =
                    "No se pudo notificar a "
                            + erroresResumen
                            + " prestadores. "
                            + "El requerimiento conserva su estado actual.";
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

    String tituloDetalleIncidencias;

    if (falloGlobalReserva) {
        tituloDetalleIncidencias =
                cantidadDetallesIncidencia == 1
                        ? "Prestador pendiente"
                        : "Prestadores pendientes";
    } else {
        tituloDetalleIncidencias =
                cantidadDetallesIncidencia == 1
                        ? "Detalle de la incidencia"
                        : "Detalle de incidencias";
    }
    %>

    <div class="compras-notificacion-resumen">
        <div class="<%= claseResumen %>">
            <strong>
                <%= HtmlUtil.escape(tituloResumen) %>
            </strong>

            <br />

            <%= HtmlUtil.escape(descripcionResumen) %>
        </div>
    </div>

    <c:if test="<%= hayDetalleIncidencias %>">

        <script type="text/javascript">
            function <portlet:namespace />alternarDetalleNotificacion() {
                var detalle =
                        document.getElementById(
                                '<portlet:namespace />detalleNotificacion'
                        );

                var enlace =
                        document.getElementById(
                                '<portlet:namespace />enlaceDetalleNotificacion'
                        );

                if (!detalle) {
                    return false;
                }

                var estaOculto =
                        detalle.style.display === 'none'
                                || detalle.style.display === '';

                detalle.style.display =
                        estaOculto
                                ? 'block'
                                : 'none';

                if (enlace) {
                    enlace.innerHTML =
                            estaOculto
                                    ? 'Ocultar detalle'
                                    : 'Ver detalle (<%= cantidadDetallesIncidencia %>)';

                    enlace.setAttribute(
                            'aria-expanded',
                            estaOculto
                                    ? 'true'
                                    : 'false'
                    );
                }

                return false;
            }
        </script>

        <div class="compras-notificacion-acciones">
            <a
                id="<portlet:namespace />enlaceDetalleNotificacion"
                class="compras-notificacion-enlace"
                href="javascript:;"
                aria-controls="<portlet:namespace />detalleNotificacion"
                aria-expanded="false"
                onclick="return <portlet:namespace />alternarDetalleNotificacion();"
            >
                Ver detalle (<%= cantidadDetallesIncidencia %>)
            </a>
        </div>

        <div
            id="<portlet:namespace />detalleNotificacion"
            class="compras-notificacion-detalle"
            style="display: none;"
        >
            <div class="compras-notificacion-detalle-cabecera">
                <div class="compras-notificacion-detalle-titulo">
                    <%= HtmlUtil.escape(tituloDetalleIncidencias) %>

                    <span class="compras-notificacion-contador">
                        <%= cantidadDetallesIncidencia %>
                    </span>
                </div>

                <div class="compras-notificacion-detalle-aclaracion">
                    <%
                    if (falloGlobalReserva) {
                    %>
                        No se detectó un inconveniente individual en cada
                        prestador. Un error técnico general impidió iniciar
                        todos los envíos.
                    <%
                    } else {
                    %>
                        Se muestran únicamente los prestadores que requieren
                        revisión.
                    <%
                    }
                    %>
                </div>
            </div>

            <div class="compras-notificacion-tabla-contenedor">
                <table
                    class="taglib-search-iterator compras-notificacion-tabla"
                    cellspacing="0"
                    cellpadding="0"
                >
                    <thead>
                        <tr class="portlet-section-header">
                            <th style="width: 35%;">
                                Prestador
                            </th>

                            <th style="width: 18%;">
                                Estado
                            </th>

                            <c:if test="<%= !falloGlobalReserva %>">
                                <th style="width: 47%;">
                                    Motivo
                                </th>
                            </c:if>
                        </tr>
                    </thead>

                    <tbody>
                        <%
                        int filaVisible = 0;

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
                            String motivoVisible =
                                    detalleNotificacion.getMotivo();

                            if (falloGlobalReserva) {
                                claseEstado =
                                        "compras-notificacion-estado "
                                                + "compras-notificacion-estado-pendiente";

                                estadoVisible =
                                        "Pendiente";

                            } else if (esErrorTecnico) {
                                claseEstado =
                                        "compras-notificacion-estado "
                                                + "compras-notificacion-estado-error";

                                estadoVisible =
                                        "Error técnico";

                                if (WebKeysCompras.isEmpty(
                                        motivoVisible
                                )) {
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
                                }

                                if (esAdvertenciaQa) {
                                    motivoVisible =
                                            motivoVisible
                                                    + " El email registrado "
                                                    + "del prestador también "
                                                    + "debe revisarse.";
                                }

                            } else if (esEmailInvalido) {
                                claseEstado =
                                        "compras-notificacion-estado "
                                                + "compras-notificacion-estado-advertencia";

                                estadoVisible =
                                        "Email inválido";

                                if (WebKeysCompras.isEmpty(
                                        motivoVisible
                                )) {
                                    motivoVisible =
                                            "El prestador no tiene un email "
                                                    + "válido registrado.";
                                }

                            } else {
                                claseEstado =
                                        "compras-notificacion-estado "
                                                + "compras-notificacion-estado-advertencia";

                                estadoVisible =
                                        "Revisar email";

                                if (WebKeysCompras.isEmpty(
                                        motivoVisible
                                )) {
                                    motivoVisible =
                                            "El email registrado no es válido. "
                                                    + "El envío de prueba fue "
                                                    + "redirigido al destinatario "
                                                    + "temporal.";
                                }
                            }

                            String claseFila =
                                    filaVisible % 2 == 0
                                            ? "portlet-section-body"
                                            : "portlet-section-alternate";

                            filaVisible++;
                        %>

                            <tr class="<%= claseFila %>">
                                <td>
                                    <span class="compras-notificacion-prestador">
                                        <%= HtmlUtil.escape(
                                                nombrePrestador
                                        ) %>
                                    </span>

                                    <span class="compras-notificacion-prestador-id">
                                        ID:
                                        <%= detalleNotificacion
                                                .getIdPrestador() %>
                                    </span>
                                </td>

                                <td>
                                    <span class="<%= claseEstado %>">
                                        <%= HtmlUtil.escape(
                                                estadoVisible
                                        ) %>
                                    </span>
                                </td>

                                <c:if test="<%= !falloGlobalReserva %>">
                                    <td>
                                        <div class="compras-notificacion-motivo">
                                            <%= HtmlUtil.escape(
                                                    motivoVisible
                                            ) %>
                                        </div>
                                    </td>
                                </c:if>
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