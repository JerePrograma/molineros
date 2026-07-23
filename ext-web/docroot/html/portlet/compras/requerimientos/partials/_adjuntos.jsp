<c:if test="<%= !esNuevo && req.puedeVerPresupuestos() %>">
    <liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp">
        <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(!puedeEditarCotizacionPantalla) %>" />
    </liferay-util:include>

    <script type="text/javascript">
    (function(window, jQuery) {
        if (!jQuery) {
            return;
        }

        jQuery(function() {
            var formulario = jQuery(
                    "#<portlet:namespace />compra_presupuesto_fm"
            );
            var leyenda = formulario.find("fieldset legend").eq(0);

            if (leyenda.length) {
                leyenda.text("Pedidos de presupuestos");
            }
        });
    })(window, window.jQuery);
    </script>
</c:if>
