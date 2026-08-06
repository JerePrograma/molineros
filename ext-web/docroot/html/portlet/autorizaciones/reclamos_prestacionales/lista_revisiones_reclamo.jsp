<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%@ taglib
    uri="http://java.sun.com/portlet_2_0"
    prefix="portlet"
/>

<portlet:defineObjects/>

<%
PortletURL portletURL =
        renderResponse.createRenderURL();

boolean inHabilitar =
        false;

boolean auditoriaAdministrativa =
        false;

String cmd =
        (String) request.getAttribute(
                Constants.CMD
        );

int cantrevisionesok =
        0;

if (cmd != null
        && cmd.equalsIgnoreCase(
                Constants.VIEW
        )) {

    inHabilitar =
            true;
}

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

    int total =
            revisionesreclamo.size();

    searchContainer.setTotal(
            total
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
                                1 + i
                        ),
                        i
                );

        row.addText(
                revreclamo
                        .getFecha_revisionTostring()
        );

        row.addText(
                Validator.isNotNull(
                        revreclamo.getUsr_presente()
                )
                        ? revreclamo.getUsr_presente()
                        : ""
        );

        row.addText(
                Validator.isNotNull(
                        revreclamo.getUsr_resolucion()
                )
                        ? revreclamo.getUsr_resolucion()
                        : ""
        );

        row.addText(
                Validator.isNotNull(
                        revreclamo
                                .getUsr_responsable_resolucion()
                )
                        ? revreclamo
                                .getUsr_responsable_resolucion()
                        : ""
        );

        if (revreclamo.getObservacion() != null
                && revreclamo
                        .getObservacion()
                        .length() > 15) {

            StringBuilder sbo =
                    new StringBuilder();

            sbo.append(
                    "&nbsp;&nbsp;&nbsp;&nbsp;"
            );

            sbo.append(
                    "<img alt=\"edita prestacion\" src=\""
            );

            sbo.append(
                    themeDisplay.getPathThemeImages()
            );

            sbo.append(
                    "/common/conversation.png\""
            );

            sbo.append(
                    " title='"
            );

            sbo.append(
                    revreclamo.getObservacion()
            );

            sbo.append(
                    "'"
            );

            sbo.append(
                    " onClick=\"javascript:VtnaObs('"
            );

            sbo.append(
                    String.valueOf(
                            revreclamo.getObservacion()
                    )
            );

            sbo.append(
                    "','Observacion de la Revision');\""
            );

            sbo.append(
                    " />"
            );

            row.addText(
                    sbo.toString()
            );

        } else {
            row.addText(
                    Validator.isNotNull(
                            revreclamo.getObservacion()
                    )
                            ? revreclamo.getObservacion()
                            : ""
            );
        }

        if (revreclamo
                .getUsr_responsable_resolucion()
                != null
                && "AUDITORIA ADMINISTRATIVA"
                        .equals(
                                revreclamo
                                        .getUsr_responsable_resolucion()
                        )) {

            auditoriaAdministrativa =
                    true;
        }

        StringBuilder sb =
                new StringBuilder();

        if (revreclamo.getEstado() == null
                || !RevisionesReclamo
                        .ESTADOS
                        .BAJA
                        .equals(
                                revreclamo.getEstado()
                        )) {

            if (!inHabilitar) {
                sb.append(
                        "&nbsp;&nbsp;&nbsp;&nbsp;"
                );

                sb.append(
                        "<img alt=\"borrar especialidades\" src=\""
                );

                sb.append(
                        themeDisplay.getPathThemeImages()
                );

                sb.append(
                        "/common/delete.png\""
                );

                sb.append(
                        " onClick=\"javascript:borrarRevision('"
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

                cantrevisionesok++;

            } else {
                sb.append(
                        " "
                );
            }

        } else {
            sb.append(
                    "&nbsp;&nbsp;&nbsp;&nbsp;"
            );

            sb.append(
                    "<img height='16'"
            );

            sb.append(
                    " width='16'"
            );

            sb.append(
                    " src='/html/themes/classic/"
            );

            sb.append(
                    "images/common/close.png'"
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
    value="<%=revisionOperacionOk%>"
/>

<liferay-ui:search-iterator
    searchContainer="<%=searchContainer%>"
/>

<script type="text/javascript">

/*
 * Se actualiza siempre, incluso cuando no quedan revisiones activas.
 * De esta forma no permanece el valor 1 después de eliminar la
 * última revisión.
 */
jQuery(
    "#<portlet:namespace />cantrevisionesactivas"
).val(
    "<%=cantrevisionesok%>"
);

/*
 * También debe limpiarse cuando la revisión de Auditoría
 * Administrativa deja de existir.
 */
jQuery(
    "#<portlet:namespace />auditoriaadministrativa"
).val(
    "<%=auditoriaAdministrativa ? "Ok" : ""%>"
);

function borrarRevision(idRevision) {

    var url =
            '<portlet:renderURL '
            + 'windowState="'
            + '<%=LiferayWindowState.EXCLUSIVE.toString()%>'
            + '"/>'
            + '&struts_action=/autorizaciones/'
            + 'borrar_reclamosrevisiones'
            + '&idRevision='
            + encodeURIComponent(
                    idRevision
            );

    jQuery(
        "#<portlet:namespace />lista_revisiones"
    ).load(
        url,
        function(
                responseText,
                status) {

            if (status == "error") {
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