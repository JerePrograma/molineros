<script type="text/javascript">
    (function() {
        function <portlet:namespace />setAfiliadoVistaValue(id, value) {
            var input = jQuery('#<portlet:namespace />' + id);

            if (input.length > 0) {
                input.val(value == null ? '' : value);
            }
        }

        function <portlet:namespace />aplicarColorBajaAfiliadoVista() {
            var bajaInput = jQuery('#<portlet:namespace />baja_fecha');

            if (bajaInput.length == 0) {
                return;
            }

            if (jQuery.trim(bajaInput.val()) != '') {
                bajaInput.css('background', 'red');
                bajaInput.css('color', 'white');
            } else {
                bajaInput.css('background', 'white');
                bajaInput.css('color', 'black');
            }
        }

        function <portlet:namespace />bloquearComponenteAfiliadoVista() {
            var panel = jQuery('#<portlet:namespace />afiliado_requerimiento_panel');

            if (panel.length == 0) {
                return;
            }

            panel.find('input[type="text"], input[type="search"], input[type="number"], textarea')
                    .attr('readonly', 'readonly');

            panel.find('select')
                    .attr('disabled', 'disabled');

            /*
             * busqueda_seccional.jsp no recibe edit_mode. Se bloquean de forma
             * local sus campos y se oculta únicamente su acción de búsqueda.
             */
            jQuery('#<portlet:namespace />id_seccional, #<portlet:namespace />seccional')
                    .attr('readonly', 'readonly')
                    .removeAttr('onkeyup')
                    .removeAttr('onblur');

            jQuery('#<portlet:namespace />btnBuscarSeccional').hide();
            jQuery('#<portlet:namespace />buscarAfiliado').hide();
            jQuery('#<portlet:namespace />limpiarCampos').hide();
        }

        function <portlet:namespace />cargarAfiliadoVista() {
            var panel = jQuery('#<portlet:namespace />afiliado_requerimiento_panel');

            if (panel.length == 0) {
                return;
            }

            if (!<%= mostrarPanelAfiliadoEnVista ? "true" : "false" %>) {
                panel.hide();
                return;
            }

            panel.show();

            <portlet:namespace />setAfiliadoVistaValue('cuil', '<%= jsCompra(afiliadoCuilVisible) %>');
            <portlet:namespace />setAfiliadoVistaValue('inte', '<%= jsCompra(afiliadoIntVisible) %>');
            <portlet:namespace />setAfiliadoVistaValue('tipoDoc', '<%= jsCompra(afiliadoTipoDocumento) %>');
            <portlet:namespace />setAfiliadoVistaValue('nroDoc', '<%= jsCompra(afiliadoNumeroDocumento) %>');
            <portlet:namespace />setAfiliadoVistaValue('apellido', '<%= jsCompra(afiliadoApellido) %>');
            <portlet:namespace />setAfiliadoVistaValue('nombre', '<%= jsCompra(afiliadoNombre) %>');
            <portlet:namespace />setAfiliadoVistaValue('id_seccional', '<%= jsCompra(afiliadoIdSeccional) %>');
            <portlet:namespace />setAfiliadoVistaValue('seccional', '<%= jsCompra(afiliadoSeccional) %>');
            <portlet:namespace />setAfiliadoVistaValue('secc_seleccionada', '<%= WebKeysCompras.isEmpty(afiliadoSeccional) ? "" : "1" %>');
            <portlet:namespace />setAfiliadoVistaValue('baja_fecha', '<%= jsCompra(afiliadoBajaFecha) %>');
            <portlet:namespace />setAfiliadoVistaValue('fecha_alta_af', '<%= jsCompra(afiliadoFechaAlta) %>');
            <portlet:namespace />setAfiliadoVistaValue('id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
            <portlet:namespace />setAfiliadoVistaValue('incapacidad_af', '<%= jsCompra(afiliadoIncapacidad) %>');
            <portlet:namespace />setAfiliadoVistaValue('nombre_plan', '<%= jsCompra(afiliadoNombrePlan) %>');
            <portlet:namespace />setAfiliadoVistaValue('id_plan', '<%= jsCompra(afiliadoIdPlan) %>');
            <portlet:namespace />setAfiliadoVistaValue('afi_tercerizadora', '<%= jsCompra(afiliadoAfiTercerizadora) %>');

            var entidadSeleccionada = jQuery('#<portlet:namespace />entidad').val();
            var numeroAfiliado = '<%= jsCompra(afiliadoNumeroAfiliado) %>';

            if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>'
                    && '<%= jsCompra(afiliadoNumeroOspim) %>' != '') {
                numeroAfiliado = '<%= jsCompra(afiliadoNumeroOspim) %>';
            }

            if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>'
                    && '<%= jsCompra(afiliadoNumeroUoma) %>' != '') {
                numeroAfiliado = '<%= jsCompra(afiliadoNumeroUoma) %>';
            }

            if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>'
                    && '<%= jsCompra(afiliadoNumeroAmtima) %>' != '') {
                numeroAfiliado = '<%= jsCompra(afiliadoNumeroAmtima) %>';
            }

            <portlet:namespace />setAfiliadoVistaValue('numero_afi', numeroAfiliado);

            var tieneAntecedentes =
                    '<%= jsCompra(afiliadoAntecedentes) %>' == 'SI'
                            ? '1'
                            : '0';

            <portlet:namespace />setAfiliadoVistaValue(
                    'tieneAntecedentes',
                    tieneAntecedentes
            );

            if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
                <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes);
            }

            <portlet:namespace />aplicarColorBajaAfiliadoVista();
            <portlet:namespace />bloquearComponenteAfiliadoVista();
        }

        jQuery(function() {
            <portlet:namespace />cargarAfiliadoVista();

            /*
             * Algunos includes legacy terminan de inicializar sus controles
             * después del ready. Se reaplica una sola vez sin alterar datos.
             */
            setTimeout(function() {
                <portlet:namespace />cargarAfiliadoVista();
            }, 300);
        });
    })();
</script>
