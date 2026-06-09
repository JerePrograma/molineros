<liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_detalle_embebido.jsp">
    <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(!modoEditable) %>" />
</liferay-util:include>
