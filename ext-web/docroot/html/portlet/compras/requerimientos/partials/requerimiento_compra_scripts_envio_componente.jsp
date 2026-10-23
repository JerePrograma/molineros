<%--
    Envio parcial legacy compartido por el editor y las acciones del listado.
    Conserva los controles originales y solo navega con exito confirmado.
    Referencia: Liquidaciones editar_orden_pago_ospim.jsp, submitFormNotSavePOP;
    Compras requerimiento_compra_scripts_edicion_guardado_componente.jsp.
--%>
<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/compras/init.jsp" %>
<script type="text/javascript">
    /* Liquidaciones: editar_orden_pago_ospim.jsp, submitFormNotSavePOP.
     * Compras: guardar() conserva el DOM y sus archivos mediante un iframe.
     * Las operaciones auxiliares comparten la misma regla ante cualquier error.
     */
    var <portlet:namespace />secuenciaOperacionCompra = 0;
    var <portlet:namespace />enviandoOperacionCompra = false;
    var <portlet:namespace />operacionCompraIncierta = false;

    function <portlet:namespace />enviarOperacionCompra(form, opciones) {
        opciones = opciones || {};
        if (!form || <portlet:namespace />enviandoOperacionCompra) { return false; }
        if (form.comprasResultadoIncierto || <portlet:namespace />operacionCompraIncierta) {
            alert('Verifique el resultado de la operacion antes de volver a enviarla. Los datos permanecen en pantalla.');
            return false;
        }
        <portlet:namespace />enviandoOperacionCompra = true;
        var botones = jQuery(form).find('input[type="submit"], input[type="button"], button');
        if (opciones.boton) { botones = botones.add(opciones.boton); }
        var estadosBotones = [];
        botones.each(function() {
            estadosBotones.push({ nodo: this, disabled: this.disabled });
            this.disabled = true;
        });
        var flag = jQuery(form).find('input[name="<portlet:namespace />compras_conservar_campos"]');
        if (flag.length == 0) {
            flag = jQuery('<input type="hidden" />')
                    .attr('name', '<portlet:namespace />compras_conservar_campos').appendTo(form);
        }
        flag.val('true');
        var idAntes = jQuery(form).find('input[name="<portlet:namespace />id_requerimiento_compra"]').val();
        var nombre = '<portlet:namespace />iframeOperacionCompra_' + (++<portlet:namespace />secuenciaOperacionCompra);
        var iframe = jQuery('<iframe name="' + nombre + '" src="about:blank" title="Operacion de compras" style="display:none;"></iframe>');
        var destinoAnterior = jQuery(form).attr('target');
        var finalizado = false;
        var temporizador = null;

        function finalizar(documentoRespuesta) {
            if (finalizado) { return; }
            finalizado = true;
            window.clearTimeout(temporizador);
            iframe.unbind('load');
            window.setTimeout(function() { iframe.remove(); }, 100);
            <portlet:namespace />enviandoOperacionCompra = false;
            var respuesta = jQuery(documentoRespuesta || []);
            var marca = respuesta.find('#<portlet:namespace />respuestaOperacionCompra');
            var mismoId = marca.length == 1 && parseInt(idAntes, 10) > 0
                    && String(marca.attr('data-compras-id')) == String(idAntes);
            var errorServidor = mismoId && marca.attr('data-compras-error') == 'true';
            var url = mismoId ? marca.attr('data-compras-destino') : '';
            var enlace = document.createElement('a');
            enlace.href = url || '';
            var exito = mismoId && marca.attr('data-compras-error') == 'false'
                    && marca.attr('data-compras-guardado') == 'true' && url
                    && enlace.protocol == window.location.protocol && enlace.host == window.location.host;
            var reintentoSeguro = errorServidor && marca.attr('data-compras-reintento-seguro') == 'true';
            if (reintentoSeguro) {
                var token = respuesta.find('input[name="<portlet:namespace />compras_save_token"]').eq(0).val();
                if (token && jQuery.trim(token) != '' && token != 'null') {
                    jQuery('input[name="<portlet:namespace />compras_save_token"]').val(token);
                } else { reintentoSeguro = false; }
            }
            if (!exito) {
                var mensaje = mismoId ? jQuery.trim(marca.attr('data-compras-error-mensaje') || '') : '';
                if (mensaje == '') { mensaje = jQuery.trim(respuesta.find('.portlet-msg-error').eq(0).text()); }
                if (mensaje == '') {
                    mensaje = jQuery.trim(respuesta.find('.compras-notificacion-resumen, .portlet-msg-alert').eq(0).text()).replace(/\s+/g, ' ');
                }
                if (mensaje == '') {
                    mensaje = 'No se pudo confirmar la operacion. La informacion cargada permanece en pantalla. Verifique el resultado antes de volver a enviarla.';
                }
                form.comprasResultadoIncierto = !errorServidor;
                <portlet:namespace />operacionCompraIncierta = !errorServidor;
                if (errorServidor) {
                    for (var i = 0; i < estadosBotones.length; i++) {
                        estadosBotones[i].nodo.disabled = estadosBotones[i].disabled;
                    }
                }
                if (opciones.alError) { opciones.alError(reintentoSeguro); }
                var aviso = jQuery('#<portlet:namespace />resultado_operacion_compra');
                if (aviso.length == 0) {
                    aviso = jQuery('<div></div>').attr('id', '<portlet:namespace />resultado_operacion_compra');
                    var posicion = jQuery('#<portlet:namespace />compras_layout');
                    aviso.insertBefore(posicion.length > 0 ? posicion : form);
                }
                aviso.addClass('portlet-msg-error').attr('role', 'alert').text(mensaje).show();
                alert(mensaje);
            }
            if (opciones.alFinalizar) { opciones.alFinalizar(); }
            if (exito) { window.location.href = url; }
        }

        iframe.bind('load', function() {
            var respuesta;
            try {
                respuesta = this.contentWindow ? this.contentWindow.document : this.contentDocument;
                if (!respuesta || respuesta.location.href == 'about:blank') { return; }
            } catch (e) { finalizar(null); return; }
            finalizar(respuesta);
        });
        temporizador = window.setTimeout(function() { finalizar(null); }, 120000);
        iframe.appendTo('body');
        try {
            jQuery(form).attr('target', nombre);
            form.submit();
        } catch (e) {
            finalizar(null);
        } finally {
            if (destinoAnterior) { jQuery(form).attr('target', destinoAnterior); }
            else { jQuery(form).removeAttr('target'); }
        }
        return false;
    }

</script>
