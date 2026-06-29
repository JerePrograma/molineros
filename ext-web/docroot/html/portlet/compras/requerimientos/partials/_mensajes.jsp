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

boolean hayDetalleNotificacion =
        hayResultadoNotificacion
                && resultadoNotificacionCotizacion.getDetalles() != null
                && !resultadoNotificacionCotizacion
                .getDetalles()
                .isEmpty();
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

<c:if test="<%= msgEstadoRequerimientoActualizado %>">
    <div class="portlet-msg-success">
        El estado del requerimiento fue actualizado correctamente.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoEnviadoACotizar %>">
    <div class="portlet-msg-success">
        <strong>
            El requerimiento pasó a A COTIZAR.
        </strong>

        <br />

        Al menos una solicitud de cotización fue aceptada por
        el servicio de correo y quedó registrada como ENVIADA.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoEnviadoACotizarConErrores %>">
    <div class="portlet-msg-error">
        <strong>
            El requerimiento pasó a A COTIZAR con incidencias.
        </strong>

        <br />

        Al menos una solicitud fue enviada correctamente, pero
        otros prestadores tuvieron emails inválidos o errores técnicos.
        Revise el resumen y el detalle por prestador.
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
        <strong>
            Las nuevas notificaciones fueron procesadas correctamente.
        </strong>

        <br />

        Las solicitudes enviadas fueron aceptadas por el servicio
        de correo y registradas como ENVIADAS.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresConErrores %>">
    <div class="portlet-msg-error">
        <strong>
            La notificación terminó con resultados parciales.
        </strong>

        <br />

        Al menos una solicitud fue enviada correctamente, pero
        también se detectaron emails inválidos o errores técnicos.
        Revise el detalle por prestador.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinResultado %>">
    <div class="portlet-msg-error">
        <strong>
            El proceso no devolvió un resultado verificable.
        </strong>

        <br />

        No es posible determinar qué prestadores fueron procesados.
        Revise el log de la aplicación.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresNoEnviados %>">
    <div class="portlet-msg-info">
        <strong>
            No se envió ninguna nueva solicitud de cotización.
        </strong>

        <br />

        El requerimiento permanece PENDIENTE.
        Revise el resumen para conocer si no había candidatos
        o si todos fueron omitidos durante el procesamiento.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinNuevosEnvios %>">
    <div class="portlet-msg-info">
        <strong>
            No se realizaron nuevos envíos.
        </strong>

        <br />

        No se produjo un error general. Los candidatos pudieron
        haber sido omitidos porque no se obtuvo una reserva exclusiva.
        El requerimiento conserva su estado actual.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresSinCompatiblesSector %>">
    <div class="portlet-msg-error">
        <strong>
            No existen prestadores compatibles con el sector.
        </strong>

        <br />

        Hay prestadores habilitados para cotizar, pero ninguno
        tiene un tipo de prestador activo asociado al sector
        del requerimiento.

        <br />

        El requerimiento permanece PENDIENTE.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresTodosOmitidosPrevios %>">
    <div class="portlet-msg-info">
        <strong>
            Todos los prestadores compatibles ya estaban procesados.
        </strong>

        <br />

        Antes de comenzar esta ejecución ya se encontraban en estado
        ENVIADO o PROCESANDO. No integraron la lista de candidatos
        y no se realizaron reenvíos.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresEmailsInvalidos %>">
    <div class="portlet-msg-error">
        <strong>
            No se pudo enviar a uno o más prestadores por email inválido.
        </strong>

        <br />

        Se obtuvo la reserva de procesamiento, pero el destinatario
        efectivo era inexistente o no tenía un formato válido.

        <br />

        No debe confundirse este resultado con un error técnico
        del servicio de correo.
    </div>
</c:if>

<c:if test="<%= msgPrestadoresErroresEnvio %>">
    <div class="portlet-msg-error">
        <strong>
            Se produjeron errores técnicos durante la notificación.
        </strong>

        <br />

        Falló alguna operación de reserva, envío de correo
        o persistencia del estado final. Revise la etapa y el
        motivo correspondiente a cada prestador.
    </div>
</c:if>

<c:if test="<%= hayResultadoNotificacion %>">

    <div class="portlet-msg-info">
        <strong>
            Resumen de la ejecución
        </strong>

        <br />
        <br />

        Prestadores habilitados para cotizar:
        <strong>
            <%= resultadoNotificacionCotizacion
                    .getPrestadoresHabilitados() %>
        </strong>.

        <br />

        Prestadores compatibles con el sector:
        <strong>
            <%= resultadoNotificacionCotizacion
                    .getPrestadoresCompatiblesSector() %>
        </strong>.

        <br />

        Bloqueados antes de esta ejecución:
        <strong>
            <%= resultadoNotificacionCotizacion
                    .getPrestadoresBloqueadosEstadoPrevio() %>
        </strong>.

        <br />

        <span>
            Estos prestadores ya estaban ENVIADO o PROCESANDO
            y no integraron la lista de candidatos.
        </span>

        <br />
        <br />

        Candidatos procesables al iniciar esta ejecución:
        <strong>
            <%= resultadoNotificacionCotizacion
                    .getTotalCandidatos() %>
        </strong>.

        <br />

        Candidatos clasificados:
        <strong>
            <%= resultadoNotificacionCotizacion
                    .getTotalProcesados() %>
        </strong>.
    </div>

    <c:if test="<%=
            resultadoNotificacionCotizacion.getEnviados() > 0
    %>">
        <div class="portlet-msg-success">
            <strong>
                Enviados correctamente:
                <%= resultadoNotificacionCotizacion.getEnviados() %>
            </strong>

            <br />

            El servicio de correo aceptó el mensaje y el sistema
            pudo persistir el estado ENVIADO.
        </div>
    </c:if>

    <c:if test="<%=
            resultadoNotificacionCotizacion.getOmitidos() > 0
    %>">
        <div class="portlet-msg-info">
            <strong>
                Omitidos durante esta ejecución:
                <%= resultadoNotificacionCotizacion.getOmitidos() %>
            </strong>

            <br />

            Estos prestadores sí habían sido listados como candidatos,
            pero no se intentó enviarles el correo porque no se obtuvo
            la reserva exclusiva al momento de procesarlos.

            <br />

            Un omitido no es un email inválido ni un error de envío.
        </div>
    </c:if>

    <c:if test="<%=
            resultadoNotificacionCotizacion.getEmailsInvalidos() > 0
    %>">
        <div class="portlet-msg-alert">
            <strong>
                Emails efectivos inválidos:
                <%= resultadoNotificacionCotizacion
                        .getEmailsInvalidos() %>
            </strong>

            <br />

            Se obtuvo la reserva, pero no se intentó enviar porque
            el destinatario efectivo era inexistente o inválido.
        </div>
    </c:if>

    <c:if test="<%=
            resultadoNotificacionCotizacion.getErrores() > 0
    %>">
        <div class="portlet-msg-error">
            <strong>
                Errores técnicos:
                <%= resultadoNotificacionCotizacion.getErrores() %>
            </strong>

            <br />

            Falló una operación de reserva, envío o persistencia.
            La columna Etapa identifica dónde ocurrió el problema.
        </div>
    </c:if>

    <c:if test="<%=
            resultadoNotificacionCotizacion
                    .getEmailsRealesInvalidosAdvertidos() > 0
    %>">
        <div class="portlet-msg-alert">
            <strong>
                Advertencia del modo temporal de QA:
                <%= resultadoNotificacionCotizacion
                        .getEmailsRealesInvalidosAdvertidos() %>
                email(es) real(es) inválido(s).
            </strong>

            <br />

            El email real no bloqueó la prueba porque el mensaje fue
            redirigido al destinatario temporal configurado.

            <br />

            Estos emails deben corregirse antes de desactivar
            el modo temporal.
        </div>
    </c:if>

    <c:if test="<%=
            resultadoNotificacionCotizacion
                    .getPendientesSinClasificar() > 0
    %>">
        <div class="portlet-msg-error">
            <strong>
                Resultado inconsistente:
                <%= resultadoNotificacionCotizacion
                        .getPendientesSinClasificar() %>
                candidato(s) quedaron sin clasificación.
            </strong>

            <br />

            La cantidad de candidatos no coincide con la suma de
            enviados, omitidos, emails inválidos y errores técnicos.
            Revise el log de la aplicación.
        </div>
    </c:if>

</c:if>

<c:if test="<%= hayDetalleNotificacion %>">

    <div style="margin-top: 15px;">
        <h4>
            Detalle de la notificación por prestador
        </h4>

        <div class="portlet-msg-info">
            <strong>Interpretación de los resultados:</strong>

            <br />

            <strong>Enviado:</strong>
            el correo fue aceptado y se persistió ENVIADO.

            <br />

            <strong>Omitido:</strong>
            no se intentó enviar porque no se obtuvo la reserva.

            <br />

            <strong>Email inválido:</strong>
            no se intentó enviar porque el destinatario efectivo
            no era válido.

            <br />

            <strong>Error técnico:</strong>
            falló una operación de reserva, envío o persistencia.
        </div>

        <div style="overflow-x: auto;">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Prestador</th>
                        <th>Resultado</th>
                        <th>Etapa</th>
                        <th>Email registrado</th>
                        <th>Email utilizado</th>
                        <th>Motivo</th>
                    </tr>
                </thead>

                <tbody>
                    <%
                    java.util.List<
                            ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                    > detallesNotificacion =
                            resultadoNotificacionCotizacion
                                    .getDetalles();

                    for (int i = 0;
                            i < detallesNotificacion.size();
                            i++) {

                        ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                detalleNotificacion =
                                detallesNotificacion.get(i);

                        String claseResultado =
                                "label";

                        String descripcionResultado =
                                detalleNotificacion.getResultado();

                        if (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                .RESULTADO_ENVIADO
                                .equals(
                                        detalleNotificacion
                                                .getResultado()
                                )) {

                            claseResultado =
                                    "label label-success";

                            descripcionResultado =
                                    "Enviado";

                        } else if (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                .RESULTADO_OMITIDO
                                .equals(
                                        detalleNotificacion
                                                .getResultado()
                                )) {

                            claseResultado =
                                    "label label-info";

                            descripcionResultado =
                                    "Omitido";

                        } else if (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                .RESULTADO_EMAIL_INVALIDO
                                .equals(
                                        detalleNotificacion
                                                .getResultado()
                                )) {

                            claseResultado =
                                    "label label-warning";

                            descripcionResultado =
                                    "Email inválido";

                        } else if (ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle
                                .RESULTADO_ERROR
                                .equals(
                                        detalleNotificacion
                                                .getResultado()
                                )) {

                            claseResultado =
                                    "label label-important";

                            descripcionResultado =
                                    "Error técnico";
                        }

                        String etapa =
                                detalleNotificacion.getEtapa();

                        String descripcionEtapa =
                                etapa;

                        if ("VALIDACION".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Validación inicial";

                        } else if ("RESERVA".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Reserva";

                        } else if ("VALIDACION_EMAIL".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Validación de email";

                        } else if ("ENVIO".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Envío de correo";

                        } else if ("PERSISTENCIA".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Persistencia";

                        } else if ("FINALIZADO".equals(
                                etapa
                        )) {
                            descripcionEtapa =
                                    "Finalizado";
                        }

                        if (WebKeysCompras.isEmpty(
                                descripcionEtapa
                        )) {
                            descripcionEtapa =
                                    "Sin etapa informada";
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

                        String emailReal =
                                detalleNotificacion
                                        .getEmailReal();

                        if (WebKeysCompras.isEmpty(
                                emailReal
                        )) {
                            emailReal =
                                    "No informado";
                        }

                        String emailDestino =
                                detalleNotificacion
                                        .getEmailDestino();

                        if (WebKeysCompras.isEmpty(
                                emailDestino
                        )) {
                            emailDestino =
                                    "No se intentó enviar";
                        }

                        String motivo =
                                detalleNotificacion
                                        .getMotivo();

                        if (WebKeysCompras.isEmpty(
                                motivo
                        )) {
                            motivo =
                                    "Sin detalle informado";
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
                                <span class="<%= claseResultado %>">
                                    <%= HtmlUtil.escape(
                                            descripcionResultado
                                    ) %>
                                </span>

                                <%
                                if (detalleNotificacion
                                        .isEmailRealInvalidoAdvertido()) {
                                %>
                                    <br />
                                    <br />

                                    <span class="label label-warning">
                                        Email real inválido
                                    </span>
                                <%
                                }
                                %>
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        descripcionEtapa
                                ) %>
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        emailReal
                                ) %>
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        emailDestino
                                ) %>
                            </td>

                            <td>
                                <%= HtmlUtil.escape(
                                        motivo
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