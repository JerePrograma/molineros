<c:if test="<%= !esNuevo %>">
    <liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp">
        <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(!modoEditable) %>" />
    </liferay-util:include>
</c:if>
