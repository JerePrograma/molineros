<c:if test="<%= !esNuevo && req.puedeVerPresupuestos() %>">
    <div class="compras-seccion compras-seccion-adjuntos">
        <liferay-util:include
            page="/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp">

            <liferay-util:param
                name="solo_lectura"
                value="<%= Boolean.toString(
                        !puedeEditarCotizacionPantalla
                ) %>" />
        </liferay-util:include>
    </div>
</c:if>
