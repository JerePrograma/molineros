<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%
PortletURL portletURL =
        renderResponse.createRenderURL();

String cmd =
        (String) request.getAttribute(
                Constants.CMD
        );

boolean inHabilitar =
        Constants.VIEW.equalsIgnoreCase(
                cmd
        );

int cantrevisionesok =
        0;

boolean auditoriaAdministrativa =
        false;

@SuppressWarnings("unchecked")
List<RevisionesReclamo> revisionesreclamo =
        (List<RevisionesReclamo>)
                request
                        .getSession()
                        .getAttribute(
                                WebKeysAutorizaciones
                                        .LISTADO_REVISIONES_RECLAMOS_EN_SESION
                        );

String revisionOperacionOk =
        ParamUtil.getString(
                request,
                "revisionOperacionOk",
                "1"
        );

List<String> headerNames =
        new ArrayList<String>();

headerNames.add(
        "Fecha Revisión"
);

headerNames.add(
        "Presentes"
);

headerNames.add(
        "Resolución"
);

headerNames.add(
        "Resp Resolución"
);

headerNames.add(
        "Observaciones"
);

headerNames.add(
        "Eliminar"
);

SearchContainer searchContainer =
        new SearchContainer(
                renderRequest,
                null,
                null,
                SearchContainer.DEFAULT_CUR_PARAM,
                Integer.MAX_VALUE,
                portletURL,
                headerNames,
                LanguageUtil.get(
                        pageContext,
                        "no-revisiones-were-found"
                )
        );

if (revisionesreclamo != null
        && !revisionesreclamo.isEmpty()) {

    searchContainer.setTotal(
            revisionesreclamo.size()
    );

    @SuppressWarnings("rawtypes")
    List resultRows =
            searchContainer.getResultRows();

    for (int i = 0;
            i < revisionesreclamo.size();
            i++) {

        RevisionesReclamo revreclamo =
                revisionesreclamo.get(
                        i
                );

        if (revreclamo == null) {
            continue;
        }

        ResultRow row =
                new ResultRow(
                        revreclamo,
                        Integer.valueOf(
                                i + 1
                        ),
                        i
                );

        /*
         * Fecha de revisión.
         */
        row.addText(
                revreclamo
                        .getFecha_revisionTostring()
        );

        /*
         * Usuarios presentes.
         */
        row.addText(
                Validator.isNotNull(
                        revreclamo.getUsr_presente()
                )
                        ? com.liferay.portal.kernel.util.HtmlUtil.escape(
                                revreclamo.getUsr_presente()
                        )
                        : ""
        );

        /*
         * Usuario que resolvió.
         */
        row.addText(
                Validator.isNotNull(
                        revreclamo.getUsr_resolucion()
                )
                        ? com.liferay.portal.kernel.util.HtmlUtil.escape(
                                revreclamo.getUsr_resolucion()
                        )
                        : ""
        );

        /*
         * Responsable de la resolución.
         */
        row.addText(
                Validator.isNotNull(
                        revreclamo
                                .getUsr_responsable_resolucion()
                )
                        ? com.liferay.portal.kernel.util.HtmlUtil.escape(
                                revreclamo
                                        .getUsr_responsable_resolucion()
                        )
                        : ""
        );

        /*
         * Observación.
         *
         * Para observaciones largas se guarda el texto escapado en un
         * elemento oculto. De esta forma no se inserta directamente el
         * contenido dentro de una cadena JavaScript.
         */
        String observacion =
                revreclamo.getObservacion();

        if (observacion == null) {
            observacion =
                    "";
        }

        String observacionEscapada =
                com.liferay.portal.kernel.util.HtmlUtil.escape(
                        observacion
                );

        if (observacion.length() > 15) {

            String observacionId =
                    renderResponse.getNamespace()
                            + "observacion_"
                            + i;

            StringBuilder sbo =
                    new StringBuilder();

            sbo.append(
                    "<span id=\""
            );

            sbo.append(
                    observacionId
            );

            sbo.append(
                    "\" style=\"display:none;\">"
            );

            sbo.append(
                    observacionEscapada
            );

            sbo.append(
                    "</span>"
            );

            sbo.append(
                    "&nbsp;&nbsp;&nbsp;&nbsp;"
            );

            sbo.append(
                    "<img alt=\"ver observacion\" src=\""
            );

            sbo.append(
                    themeDisplay.getPathThemeImages()
            );

            sbo.append(
                    "/common/conversation.png\""
            );

            sbo.append(
                    " title=\""
            );

            sbo.append(
                    observacionEscapada
            );

            sbo.append(
                    "\""
            );

            sbo.append(
                    " style=\"cursor:pointer;\""
            );

            sbo.append(
                    " onclick=\"VtnaObs("
            );

            sbo.append(
                    "jQuery(document.getElementById('"
            );

            sbo.append(
                    observacionId
            );

            sbo.append(
                    "')).text(), "
            );

            sbo.append(
                    "'Observacion de la Revision');\""
            );

            sbo.append(
                    " />"
            );

            row.addText(
                    sbo.toString()
            );

        } else {

            row.addText(
                    observacionEscapada
            );
        }

        /*
         * Una revisión es activa mientras no tenga estado BAJA.
         *
         * El conteo no depende de que el JSP se encuentre en modo
         * edición o vista.
         */
        boolean revisionActiva =
                revreclamo.getEstado() == null
                        || !RevisionesReclamo
                                .ESTADOS
                                .BAJA
                                .equals(
                                        revreclamo.getEstado()
                                );

        if (revisionActiva) {

            cantrevisionesok++;

            /*
             * Auditoría Administrativa solamente debe marcarse cuando
             * la revisión que la originó continúa activa.
             */
            if ("AUDITORIA ADMINISTRATIVA"
                    .equals(
                            revreclamo
                                    .getUsr_responsable_resolucion()
                    )) {

                auditoriaAdministrativa =
                        true;
            }
        }

        /*
         * Columna Eliminar.
         */
        StringBuilder sb =
                new StringBuilder();

        if (revisionActiva) {

            if (!inHabilitar) {

                sb.append(
                        "&nbsp;&nbsp;&nbsp;&nbsp;"
                );

                sb.append(
                        "<img alt=\"borrar revision\" src=\""
                );

                sb.append(
                        themeDisplay.getPathThemeImages()
                );

                sb.append(
                        "/common/delete.png\""
                );

                sb.append(
                        " title=\"Eliminar revision\""
                );

                sb.append(
                        " style=\"cursor:pointer;\""
                );

                sb.append(
                        " onclick=\"borrarRevision('"
                );

                sb.append(
                        String.valueOf(
                                revreclamo.getId()
                        )
                );

                sb.append(
                        "');\""
                );

                sb.append(
                        " />"
                );
            }

        } else {

            sb.append(
                    "&nbsp;&nbsp;&nbsp;&nbsp;"
            );

            sb.append(
                    "<img alt=\"revision eliminada\""
            );

            sb.append(
                    " height=\"16\""
            );

            sb.append(
                    " width=\"16\""
            );

            sb.append(
                    " src=\""
            );

            sb.append(
                    themeDisplay.getPathThemeImages()
            );

            sb.append(
                    "/common/close.png\""
            );

            sb.append(
                    " />"
            );
        }

        row.addText(
                sb.toString()
        );

        resultRows.add(
                row
        );
    }
}
%>

<liferay-ui:error
    exception="<%=RevisionesReclamosException.class%>"
    message="error-en-revision-reclamo"
/>

<input
    type="hidden"
    id="<portlet:namespace />revision_operacion_ok"
    name="<portlet:namespace />revision_operacion_ok"
    value="<%=com.liferay.portal.kernel.util.HtmlUtil.escape(
            revisionOperacionOk
    )%>"
/>

<%
if (revisionesreclamo == null
        || revisionesreclamo.isEmpty()) {
%>

<table
    class="lfr-table"
    style="width: 100%;"
>
    <thead>
        <tr>
            <th>
                <%=headerNames.get(0)%>
            </th>

            <th>
                <%=headerNames.get(1)%>
            </th>

            <th>
                <%=headerNames.get(2)%>
            </th>

            <th>
                <%=headerNames.get(3)%>
            </th>

            <th>
                <%=headerNames.get(4)%>
            </th>

            <th>
                <%=headerNames.get(5)%>
            </th>
        </tr>
    </thead>

    <tbody>
        <tr>
            <td
                colspan="<%=headerNames.size()%>"
                style="text-align:center;"
            >
                <%=LanguageUtil.get(
                        pageContext,
                        "no-revisiones-were-found"
                )%>
            </td>
        </tr>
    </tbody>
</table>

<%
} else {
%>

<liferay-ui:search-iterator
    searchContainer="<%=searchContainer%>"
/>

<%
}
%>

<%--
La URL se genera completamente en el servidor.

Se utiliza un marcador para idRevision y luego se reemplaza desde
JavaScript. Esto evita intentar construir un tag JSP concatenando
cadenas JavaScript.
--%>
<portlet:renderURL
    var="borrarRevisionURL"
    windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"
    escapeXml="false"
>
    <portlet:param
        name="struts_action"
        value="/autorizaciones/borrar_reclamosrevisiones"
    />

    <portlet:param
        name="idRevision"
        value="__ID_REVISION__"
    />
</portlet:renderURL>

<script type="text/javascript">

/*
 * Se actualiza siempre, incluso cuando no quedan revisiones activas.
 * De esta forma no permanece el valor anterior después de eliminar
 * la última revisión.
 */
jQuery(
    "#<portlet:namespace />cantrevisionesactivas"
).val(
    "<%=cantrevisionesok%>"
);

/*
 * Debe quedar vacío cuando ya no existe una revisión activa cuyo
 * responsable sea Auditoría Administrativa.
 */
jQuery(
    "#<portlet:namespace />auditoriaadministrativa"
).val(
    "<%=auditoriaAdministrativa ? "Ok" : ""%>"
);

function borrarRevision(idRevision) {

    var url =
            "<%=borrarRevisionURL%>";

    url =
            url.replace(
                    "__ID_REVISION__",
                    encodeURIComponent(
                            idRevision
                    )
            );

    jQuery(
        "#<portlet:namespace />lista_revisiones"
    ).load(
        url,
        function(
                responseText,
                status,
                xhr) {

            if (status === "error") {

                if (window.console
                        && window.console.error) {

                    window.console.error(
                            "No se pudo eliminar la revisión.",
                            xhr
                    );
                }

                return;
            }

            jQuery(
                "#<portlet:namespace />botonrevision"
            ).show();

            jQuery(
                "#<portlet:namespace />"
                        + "mensajerevisionefectuada"
            ).html(
                ""
            );
        }
    );
}

</script>