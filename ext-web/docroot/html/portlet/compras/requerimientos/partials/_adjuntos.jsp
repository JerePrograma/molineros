<!-- compras-presupuestos-debug
idRequerimiento=<%= req.getIdRequerimientoCompra() %>
estadoPersistido=<%= req.getEstado() %>
esNuevo=<%= esNuevo %>
modoVistaForzado=<%= modoVistaForzado %>
soloLecturaSolicitada=<%= soloLecturaSolicitada %>
puedeCotizar=<%= puedeCotizar %>
puedeEditarCotizacionPantalla=<%= puedeEditarCotizacionPantalla %>
modoEditable=<%= modoEditable %>
puedeVerPresupuestos=<%= req.puedeVerPresupuestos() %>
puedeAdministrarPresupuestos=<%= req.puedeAdministrarPresupuestos() %>
struts_action=<%= HtmlUtil.escape(strutsActionActual) %>
modo=<%= HtmlUtil.escape(modoParam) %>
-->
<c:if test="<%= !esNuevo && req.puedeVerPresupuestos() %>">
    <liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp">
        <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(!puedeEditarCotizacionPantalla) %>" />
    </liferay-util:include>
</c:if>
