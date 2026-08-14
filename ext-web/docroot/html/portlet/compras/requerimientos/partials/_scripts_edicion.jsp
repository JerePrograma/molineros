<portlet:renderURL
        var="comprasBuscarAfiliadosURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/buscar_afiliados" />

</portlet:renderURL>
<script type="text/javascript">
    var popup = null;
    var popupAfill = null;
    var <portlet:namespace />guardandoCompra = false;

    function <portlet:namespace />setGuardandoCompraActivo(activo) {
        <portlet:namespace />guardandoCompra = activo;

        var botonGuardar = document.getElementById('<portlet:namespace />btnGuardarCompras');

        if (botonGuardar) {
            if (activo) {
                botonGuardar.disabled = true;
                botonGuardar.setAttribute('disabled', 'disabled');
                botonGuardar.value = 'Guardando...';

                jQuery(botonGuardar).addClass('compras-btn-guardando');
            } else {
                botonGuardar.disabled = false;
                botonGuardar.removeAttribute('disabled');
                botonGuardar.value = 'Guardar';

                jQuery(botonGuardar).removeClass('compras-btn-guardando');
            }
        }
    }

    function <portlet:namespace />cancelarGuardadoCompra() {
        <portlet:namespace />setGuardandoCompraActivo(false);
        return false;
    }

    function <portlet:namespace />focusSeguroCompra(selector) {
        setTimeout(function() {
            jQuery(selector).focus();
        }, 200);
    }

    var <portlet:namespace />sectorRequiereAfiliadoMap = {};

    <%
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());
        String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
    %>
        <portlet:namespace />sectorRequiereAfiliadoMap['<%= sectorId %>'] = <%= requiereAfiliado %>;
    <%
    }
    %>

    function <portlet:namespace />valorSeguroAfiliado(value) {
        if (value == null || typeof value == 'undefined' || value == 'null') {
            return '';
        }

        return value;
    }

    function <portlet:namespace />fechaReferenciaAfiliado() {
        var d = new Date();
        var currDate = d.getDate();
        var currMonth = d.getMonth() + 1;
        var currYear = d.getFullYear();

        return currDate + '/' + currMonth + '/' + currYear;
    }

    function <portlet:namespace />trimValue(id) {
        var input = jQuery('#<portlet:namespace />' + id);

        if (input.length == 0) {
            return '';
        }

        return jQuery.trim(input.val());
    }

    function <portlet:namespace />valorAfiliado(id) {
        return <portlet:namespace />trimValue(id);
    }

    function <portlet:namespace />valorCredencialAfiliado() {
        return jQuery('#<portlet:namespace />' + 'num' + 'ero_afi').val();
    }

    function <portlet:namespace />paramCredencialAfiliado() {
        return 'num' + 'ero_afi';
    }

    function <portlet:namespace />buscarAfiliados() {
        if (<portlet:namespace />guardandoCompra) {
            return false;
        }

        var cuil = jQuery('#<portlet:namespace />cuil').val();
        var inte = jQuery('#<portlet:namespace />inte').val();
        var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
        var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();
        var seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var apellido = jQuery('#<portlet:namespace />apellido').val();
        var nombre = jQuery('#<portlet:namespace />nombre').val();
        var entidad = jQuery('#<portlet:namespace />entidad').val();
        var numeroAfi = jQuery('#<portlet:namespace />numero_afi').val();
        var nroCredencialPrevencion = jQuery('#<portlet:namespace />nroCredencialPrevencion').val();
        var nroSocioPrevencion = jQuery('#<portlet:namespace />nroSocioPrevencion').val();
        var fechaReferencia = <portlet:namespace />fechaReferenciaAfiliado();

        if (!<portlet:namespace />validarBusqueda(cuil, inte, tipoDoc, nroDoc, seccional, apellido, nombre, entidad, numeroAfi)) {
            return false;
        }

        if (cuil.length > 0) {
            if (typeof validarCuil == 'function' && !validarCuil(cuil, '<liferay-ui:message key="valida-cuil-mensaje-limpiar"/>')) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        if (jQuery('#<portlet:namespace />secc_seleccionada').val() != '1') {
            jQuery('#<portlet:namespace />seccional').val('');
            jQuery('#<portlet:namespace />id_seccional').val('');
        }

        popupAfill = Liferay.Popup({
            title: '<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />',
            modal: true,
            width: 830
        });

        var url =
                '${comprasBuscarAfiliadosURL}'
                + '&cuil='
                + encodeURIComponent(cuil)
                + '&inte='
                + encodeURIComponent(inte)
                + '&tipoDoc='
                + encodeURIComponent(tipoDoc)
                + '&nroDoc='
                + encodeURIComponent(nroDoc)
                + '&seccional='
                + encodeURIComponent(seccional)
                + '&nombre='
                + encodeURIComponent(nombre)
                + '&apellido='
                + encodeURIComponent(apellido)
                + '&entidad='
                + encodeURIComponent(entidad)
                + '&numero_afi='
                + encodeURIComponent(numeroAfi)
                + '&fecha_referencia='
                + encodeURIComponent(fechaReferencia)
                + '&nroCredencialPrevencion='
                + encodeURIComponent(nroCredencialPrevencion)
                + '&nroSocioPrevencion='
                + encodeURIComponent(nroSocioPrevencion)
                + '&origen='
                + '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccional() {
        var id_seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var seccional = jQuery('#<portlet:namespace />seccional').val();

        if (!<portlet:namespace />validaFormSecc(id_seccional, seccional)) {
            return false;
        }

        popup = Liferay.Popup({
            title: '<liferay-ui:message key="busqueda-seccionales" />',
            modal: true,
            width: 420
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_seccional' +
            '&id_seccional=' + encodeURIComponent(id_seccional) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&prefijo=';

        jQuery(popup).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccionalOnDiv(e) {
        var evtobj = window.event ? event : e;
        var keyPressed = evtobj.keyCode ? evtobj.keyCode : evtobj.charCode;

        if (jQuery('#<portlet:namespace />secc_seleccionada').val() == '1' && (keyPressed != 9 && keyPressed != 16)) {
            jQuery('#<portlet:namespace />seccional').val('');
            jQuery('#<portlet:namespace />id_seccional').val('');
            jQuery('#<portlet:namespace />secc_seleccionada').val('');
            jQuery('#<portlet:namespace />btnBuscarSeccional').show();

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            return false;
        }

        var id_seccional = jQuery('#<portlet:namespace />id_seccional').val();
        var seccional = jQuery('#<portlet:namespace />seccional').val();

        if ((seccional.length >= 3 || id_seccional.length > 2) && (keyPressed != 9 && keyPressed != 16)) {
            if (id_seccional.length > 2) {
                jQuery('#<portlet:namespace />seccional').val('');
            } else {
                jQuery('#<portlet:namespace />id_seccional').val('');
            }

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery('#divSeccional').load(url);
            jQuery('#divSeccional').show();
        } else {
            jQuery('#divSeccional').hide('slow');
        }

        return false;
    }

    function seleccionaAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        seleccionaCamposAfiliado(
            cuil,
            inte,
            docu_tipo,
            docu_nro,
            nombre,
            apellido,
            id_secc,
            desc_secc,
            ospim,
            uoma,
            amtima,
            bajaFecha,
            nombre_plan,
            id_plan,
            fecha_alta_af,
            incapacidad_af,
            id_tercerizadora,
            afi_tercerizadora,
            reclamoPrestacional,
            nroSocioPrev,
            nroCredenPrev,
            fechaRecepcion,
            tieneAntecedentes
        );

        if (popupAfill != null) {
            Liferay.Popup.close(popupAfill);
        }
    }

    function seleccionaCamposAfiliado(cuil, inte, docu_tipo, docu_nro, nombre, apellido, id_secc, desc_secc, ospim, uoma, amtima, bajaFecha, nombre_plan, id_plan, fecha_alta_af, incapacidad_af, id_tercerizadora, afi_tercerizadora, reclamoPrestacional, nroSocioPrev, nroCredenPrev, fechaRecepcion, tieneAntecedentes) {
        nombre_plan = <portlet:namespace />valorSeguroAfiliado(nombre_plan);
        id_plan = <portlet:namespace />valorSeguroAfiliado(id_plan);
        id_tercerizadora = <portlet:namespace />valorSeguroAfiliado(id_tercerizadora);
        afi_tercerizadora = <portlet:namespace />valorSeguroAfiliado(afi_tercerizadora);
        fecha_alta_af = <portlet:namespace />valorSeguroAfiliado(fecha_alta_af);
        incapacidad_af = <portlet:namespace />valorSeguroAfiliado(incapacidad_af);
        nroSocioPrev = <portlet:namespace />valorSeguroAfiliado(nroSocioPrev);
        nroCredenPrev = <portlet:namespace />valorSeguroAfiliado(nroCredenPrev);
        bajaFecha = <portlet:namespace />valorSeguroAfiliado(bajaFecha);

        jQuery('#<portlet:namespace />cuil').val(cuil);
        jQuery('#<portlet:namespace />inte').val(inte);
        jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
        jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
        jQuery('#<portlet:namespace />id_seccional').val(id_secc);
        jQuery('#<portlet:namespace />seccional').val(desc_secc);
        jQuery('#<portlet:namespace />apellido').val(apellido);
        jQuery('#<portlet:namespace />nombre').val(nombre);
        jQuery('#<portlet:namespace />secc_seleccionada').val('1');

        var entidadSeleccionada = jQuery('#<portlet:namespace />entidad').val();
        var credencialId = '#<portlet:namespace />' + 'num' + 'ero_afi';

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(ospim));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(uoma));
        }

        if (entidadSeleccionada == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
            jQuery(credencialId).val(<portlet:namespace />valorSeguroAfiliado(amtima));
        }

        jQuery('#<portlet:namespace />baja_fecha').val(bajaFecha);

        if (bajaFecha != '') {
            document.getElementById('<portlet:namespace />baja_fecha').style.background = 'red';
            document.getElementById('<portlet:namespace />baja_fecha').style.color = 'white';
        } else {
            document.getElementById('<portlet:namespace />baja_fecha').style.background = 'white';
            document.getElementById('<portlet:namespace />baja_fecha').style.color = 'black';
        }

        jQuery('#<portlet:namespace />nombre_plan').val(nombre_plan);
        jQuery('#<portlet:namespace />id_plan').val(id_plan);
        jQuery('#<portlet:namespace />afi_tercerizadora').val(afi_tercerizadora);
        jQuery('#<portlet:namespace />fecha_alta_af').val(fecha_alta_af);
        jQuery('#<portlet:namespace />id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(id_tercerizadora);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val(id_tercerizadora);
        jQuery('#<portlet:namespace />incapacidad_af').val(incapacidad_af);
        jQuery('#<portlet:namespace />nroSocioPrevencion').val(nroSocioPrev);
        jQuery('#<portlet:namespace />nroCredencialPrevencion').val(nroCredenPrev);
        jQuery('#<portlet:namespace />tieneAntecedentes').val(tieneAntecedentes == '1' ? '1' : '0');

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(tieneAntecedentes);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();

        /*
         * Actualizar el vencimiento CUD inmediatamente después
         * de seleccionar el afiliado.
         */
        <portlet:namespace />actualizarVencimientoCudAfiliado(
                cuil,
                inte,
                incapacidad_af
        );

        /*
         * Regla exclusiva de Compras.
         *
         * Una vez seleccionado el afiliado, se comprueba si posee
         * al menos una situación médica vigente.
         */
        <portlet:namespace />actualizarSituacionMedicaAfiliado(
                cuil,
                inte
        );

        if (typeof <portlet:namespace />mostrarMensajeAfiliadoInicial == 'function') {
            <portlet:namespace />mostrarMensajeAfiliadoInicial('');
        }
    }

    function <portlet:namespace />sectorRequiereAfiliado() {
        var sectorId = jQuery.trim(jQuery('#<portlet:namespace />sector_id').val());

        if (sectorId != '' && sectorId != '0') {
            if (typeof <portlet:namespace />sectorRequiereAfiliadoMap[sectorId] != 'undefined') {
                return <portlet:namespace />sectorRequiereAfiliadoMap[sectorId] === true;
            }
        }

        var selected = jQuery('#<portlet:namespace />sector_id option:selected');
        var attr = selected.attr('data-requiere-afiliado');

        return attr == 'true' || attr == '1' || attr == 'Sí' || attr == 'S';
    }

    function <portlet:namespace />sectorUsaCodigoPrestacion() {
        var sectorId =
                <portlet:namespace />trimValue(
                        'sector_id'
                );

        if (sectorId == ''
                || sectorId == '0') {

            return false;
        }

        var selected =
                jQuery(
                        '#<portlet:namespace />sector_id option:selected'
                );

        var attr =
                selected.attr(
                        'data-usa-codigo-prestacion'
                );

        return attr == 'true'
                || attr == '1'
                || attr == 'Sí'
                || attr == 'S';
    }

    function <portlet:namespace />actualizarVisibilidadObservaciones(
            limpiarSiSeOculta) {

        var sectorId =
                <portlet:namespace />trimValue(
                        'sector_id'
                );

        var panel =
                jQuery(
                        '#<portlet:namespace />observaciones_panel'
                );

        var observaciones =
                jQuery(
                        '#<portlet:namespace />observaciones'
                );

        var observacionesHidden =
                jQuery(
                        '#<portlet:namespace />observaciones_hidden'
                );

        if (panel.length == 0) {
            return;
        }

        var tieneSector =
                sectorId != ''
                && sectorId != '0';

        var usaCodigoPrestacion =
                tieneSector
                && <portlet:namespace />sectorUsaCodigoPrestacion();

        var mostrarObservaciones =
                tieneSector
                && !usaCodigoPrestacion;

        if (mostrarObservaciones) {
            panel.show();
            return;
        }

        /*
         * Sólo se limpia cuando el usuario cambia de sector.
         *
         * En la carga inicial se oculta, pero se conserva cualquier
         * valor histórico que ya estuviera persistido.
         */
        if (limpiarSiSeOculta) {
            if (observaciones.length > 0) {
                observaciones.val('');
            }

            if (observacionesHidden.length > 0) {
                observacionesHidden.val('');
            }
        }

        panel.hide();
    }

    function <portlet:namespace />sincronizarAfiliadoRequerimiento() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(
                <portlet:namespace />trimValue('cuil')
        );

        jQuery('#<portlet:namespace />afiliado_int').val(
                <portlet:namespace />trimValue('inte')
        );

        var idTerc = '';

        if (jQuery('#<portlet:namespace />id_tercerizadora').length > 0) {
            idTerc = jQuery.trim(jQuery('#<portlet:namespace />id_tercerizadora').val());
        }

        if (idTerc == ''
                && jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').length > 0) {
            idTerc = jQuery.trim(jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val());
        }

        if (idTerc == ''
                && jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').length > 0) {
            idTerc = jQuery.trim(jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val());
        }

        if (idTerc != ''
                && jQuery('#<portlet:namespace />id_tercerizadora').length > 0
                && jQuery.trim(jQuery('#<portlet:namespace />id_tercerizadora').val()) == '') {
            jQuery('#<portlet:namespace />id_tercerizadora').val(idTerc);
        }

        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(idTerc);
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val(idTerc);
    }

    function <portlet:namespace />sectorSinAfiliadoForzaCargoOspim() {
        var sectorId = <portlet:namespace />trimValue('sector_id');

        return sectorId != ''
                && sectorId != '0'
                && !<portlet:namespace />sectorRequiereAfiliado();
    }

    function <portlet:namespace />limpiarCargosCompra() {
        jQuery('#<portlet:namespace />cargo_ospim').val('');
        jQuery('#<portlet:namespace />cargo_tercerizadora').val('');

        jQuery('#<portlet:namespace />cargo_ospim_hidden').val('');
        jQuery('#<portlet:namespace />cargo_tercerizadora_hidden').val('');

        <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(null);
    }

    function <portlet:namespace />aplicarReglaCargosPorSector(reiniciarCargosSiRequiereAfiliado) {
        var forzarCargoOspim = <portlet:namespace />sectorSinAfiliadoForzaCargoOspim();

        if (forzarCargoOspim) {
            jQuery('#<portlet:namespace />cargo_ospim').val('100');
            jQuery('#<portlet:namespace />cargo_tercerizadora').val('0');

            jQuery('#<portlet:namespace />cargo_ospim_hidden').val('100');
            jQuery('#<portlet:namespace />cargo_tercerizadora_hidden').val('0');

            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(0);

            jQuery('#<portlet:namespace />fila_cargos_compra').hide();
        } else {
            jQuery('#<portlet:namespace />fila_cargos_compra').show();
            jQuery('#<portlet:namespace />fila_cargos_forzados_compra').hide();

            /*
             * Solo limpiar cuando el usuario cambia sector.
             * No limpiar durante el document.ready, porque en edición pisaría valores existentes.
             */
            if (reiniciarCargosSiRequiereAfiliado) {
                <portlet:namespace />limpiarCargosCompra();
            }
        }

        return forzarCargoOspim;
    }

    function <portlet:namespace />sincronizarFormularioCompra() {
        <portlet:namespace />sincronizarAfiliadoRequerimiento();

        var cargoForzadoPorSector =
                <portlet:namespace />aplicarReglaCargosPorSector(false);

        jQuery('#<portlet:namespace />sector_id_hidden').val(
                <portlet:namespace />trimValue('sector_id')
        );

        jQuery('#<portlet:namespace />cargo_ospim_hidden').val(
                <portlet:namespace />trimValue('cargo_ospim')
        );

        jQuery('#<portlet:namespace />cargo_tercerizadora_hidden').val(
                <portlet:namespace />trimValue('cargo_tercerizadora')
        );

        var observacionesInput =
                jQuery(
                        '#<portlet:namespace />observaciones'
                );

        if (observacionesInput.length > 0) {
            jQuery(
                    '#<portlet:namespace />observaciones_hidden'
            ).val(
                    observacionesInput.val() || ''
            );
        }

        if (cargoForzadoPorSector) {
            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(0);
        } else {
            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora();
        }

        <portlet:namespace />actualizarSurgeCompra();
    }

    function <portlet:namespace />cargarAfiliadoInicial() {
        var afiliadoCuilTitular = jQuery('#<portlet:namespace />afiliado_cuil_titular').val();
        var afiliadoInt = jQuery('#<portlet:namespace />afiliado_int').val();
        var idTerc = jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val();

        if (afiliadoCuilTitular != '') {
            jQuery('#<portlet:namespace />cuil').val(afiliadoCuilTitular);
        }

        if (afiliadoInt != '') {
            jQuery('#<portlet:namespace />inte').val(afiliadoInt);
        }

        if (idTerc != '') {
            jQuery('#<portlet:namespace />id_tercerizadora').val(idTerc);
            jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val(idTerc);
        }

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />setAfiliadoValue(id, value) {
        var input = jQuery('#<portlet:namespace />' + id);

        if (input.length > 0) {
            input.val(value == null ? '' : value);
        }
    }

    function <portlet:namespace />aplicarColorBajaAfiliadoExistente() {
        var bajaInput = jQuery('#<portlet:namespace />baja_fecha');

        if (bajaInput.length > 0) {
            if (jQuery.trim(bajaInput.val()) != '') {
                bajaInput.css('background', 'red');
                bajaInput.css('color', 'white');
            } else {
                bajaInput.css('background', 'white');
                bajaInput.css('color', 'black');
            }
        }
    }

    function <portlet:namespace />cargarAfiliadoExistenteEnEdicion() {
        if (<%= esNuevo ? "true" : "false" %>) {
            return;
        }

        <portlet:namespace />setAfiliadoValue('cuil', '<%= jsCompra(afiliadoCuilVisible) %>');
        <portlet:namespace />setAfiliadoValue('inte', '<%= jsCompra(afiliadoIntVisible) %>');
        <portlet:namespace />setAfiliadoValue('tipoDoc', '<%= jsCompra(afiliadoTipoDocumento) %>');
        <portlet:namespace />setAfiliadoValue('nroDoc', '<%= jsCompra(afiliadoNumeroDocumento) %>');
        <portlet:namespace />setAfiliadoValue('apellido', '<%= jsCompra(afiliadoApellido) %>');
        <portlet:namespace />setAfiliadoValue('nombre', '<%= jsCompra(afiliadoNombre) %>');
        <portlet:namespace />setAfiliadoValue('id_seccional', '<%= jsCompra(afiliadoIdSeccional) %>');
        <portlet:namespace />setAfiliadoValue('seccional', '<%= jsCompra(afiliadoSeccional) %>');
        <portlet:namespace />setAfiliadoValue('baja_fecha', '<%= jsCompra(afiliadoBajaFecha) %>');
        <portlet:namespace />setAfiliadoValue('fecha_alta_af', '<%= jsCompra(afiliadoFechaAlta) %>');
        <portlet:namespace />setAfiliadoValue('id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
        <portlet:namespace />setAfiliadoValue('requerimiento_id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
        <portlet:namespace />setAfiliadoValue('requerimiento_id_tercerizadora_hidden', '<%= jsCompra(afiliadoIdTercerizadora) %>');
        <portlet:namespace />setAfiliadoValue('incapacidad_af', '<%= jsCompra(afiliadoIncapacidad) %>');
        <portlet:namespace />setAfiliadoValue('nombre_plan', '<%= jsCompra(afiliadoNombrePlan) %>');
        <portlet:namespace />setAfiliadoValue('id_plan', '<%= jsCompra(afiliadoIdPlan) %>');
        <portlet:namespace />setAfiliadoValue('afi_tercerizadora', '<%= jsCompra(afiliadoAfiTercerizadora) %>');

        var entidadSeleccionadaInicial = jQuery('#<portlet:namespace />entidad').val();
        var numeroAfiliadoInicial = '<%= jsCompra(afiliadoNumeroAfiliado) %>';

        if (entidadSeleccionadaInicial == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>'
                && '<%= jsCompra(afiliadoNumeroOspim) %>' != '') {
            numeroAfiliadoInicial = '<%= jsCompra(afiliadoNumeroOspim) %>';
        }

        if (entidadSeleccionadaInicial == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>'
                && '<%= jsCompra(afiliadoNumeroUoma) %>' != '') {
            numeroAfiliadoInicial = '<%= jsCompra(afiliadoNumeroUoma) %>';
        }

        if (entidadSeleccionadaInicial == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>'
                && '<%= jsCompra(afiliadoNumeroAmtima) %>' != '') {
            numeroAfiliadoInicial = '<%= jsCompra(afiliadoNumeroAmtima) %>';
        }

        <portlet:namespace />setAfiliadoValue('numero_afi', numeroAfiliadoInicial);

        if ('<%= jsCompra(afiliadoSeccional) %>' != '') {
            <portlet:namespace />setAfiliadoValue('secc_seleccionada', '1');
        }

        if ('<%= jsCompra(afiliadoAntecedentes) %>' == 'Sí') {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '1');
        } else {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '0');
        }

        <portlet:namespace />aplicarColorBajaAfiliadoExistente();

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(
                    '<%= jsCompra(afiliadoAntecedentes) %>' == 'Sí' ? '1' : '0'
            );
        }

        /*
         * Un requerimiento existente no pasa por
         * seleccionaCamposAfiliado(), por lo tanto su CUD
         * debe consultarse expresamente al hidratar la pantalla.
         */
        <portlet:namespace />actualizarVencimientoCudAfiliado(
                '<%= jsCompra(afiliadoCuilVisible) %>',
                '<%= jsCompra(afiliadoIntVisible) %>',
                '<%= jsCompra(afiliadoIncapacidad) %>'
        );

        /*
         * En edición de un requerimiento existente no se ejecuta
         * seleccionaCamposAfiliado(), por lo tanto también debe
         * inicializarse explícitamente Situación Médica.
         */
        <portlet:namespace />actualizarSituacionMedicaAfiliado(
                '<%= jsCompra(afiliadoCuilVisible) %>',
                '<%= jsCompra(afiliadoIntVisible) %>'
        );

        <portlet:namespace />sincronizarAfiliadoRequerimiento();
    }

    function <portlet:namespace />mostrarMensajeAfiliadoInicial(mensaje) {
        var panel = jQuery('#<portlet:namespace />afiliadoInicialMensaje');

        if (mensaje == null || jQuery.trim(mensaje) == '') {
            panel.hide();
            panel.text('');
            return;
        }

        panel.text(mensaje);
        panel.show();
    }

    function <portlet:namespace />cargarDatosAfiliadoInicial() {
        return false;
    }

    function <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste() {
        if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
            <portlet:namespace />limpiarCamposAfiliado();
        }

        jQuery('#<portlet:namespace />afiliado_cuil_titular').val('');
        jQuery('#<portlet:namespace />afiliado_int').val('');
        jQuery('#<portlet:namespace />id_seccional').val('');
        jQuery('#<portlet:namespace />seccional').val('');
        jQuery('#<portlet:namespace />numero_afi').val('');
        jQuery('#<portlet:namespace />id_tercerizadora').val('');
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
        jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val('');
        jQuery('#<portlet:namespace />nombre_plan').val('');
        jQuery('#<portlet:namespace />id_plan').val('');
        jQuery('#<portlet:namespace />afi_tercerizadora').val('');

        /*
         * Nunca dejar visible el vencimiento correspondiente
         * al afiliado anterior.
         */
        <portlet:namespace />ocultarVencimientoCudAfiliado();

        <portlet:namespace />ocultarSituacionMedicaAfiliado();

        <portlet:namespace />mostrarMensajeAfiliadoInicial('');
    }

    function <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere) {
        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        var tieneAfiliadoExistente =
                !<%= esNuevo ? "true" : "false" %>
                && (
                        jQuery.trim(jQuery('#<portlet:namespace />afiliado_cuil_titular').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />afiliado_int').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />cuil').val()) != ''
                        || jQuery.trim(jQuery('#<portlet:namespace />inte').val()) != ''
                );

        if (requiereAfiliado || tieneAfiliadoExistente) {
            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').show();
        } else {
            if (limpiarSiNoRequiere) {
                <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            }

            <portlet:namespace />sincronizarAfiliadoRequerimiento();
            jQuery('#<portlet:namespace />afiliado_requerimiento_panel').hide();
        }
    }

    function <portlet:namespace />cambiarSectorCompra(
            limpiarSiNoRequiere) {

        <portlet:namespace />actualizarVisibilidadAfiliado(
                limpiarSiNoRequiere
        );

        <portlet:namespace />actualizarVisibilidadObservaciones(
                true
        );

        /*
         * Si el usuario cambia hacia un sector que requiere afiliado,
         * se limpian los cargos para evitar arrastrar el 100/0 forzado.
         */
        <portlet:namespace />aplicarReglaCargosPorSector(
                true
        );

        <portlet:namespace />sincronizarFormularioCompra();

        if (typeof window[
                '<portlet:namespace />actualizarTipoNomencladorDetallePorSector'
        ] == 'function') {

            window[
                    '<portlet:namespace />actualizarTipoNomencladorDetallePorSector'
            ](
                    true
            );
        }

        if (typeof window[
                '<portlet:namespace />filtrarArticulosPorSector'
        ] == 'function') {

            window[
                    '<portlet:namespace />filtrarArticulosPorSector'
            ]();
        }
    }

    function <portlet:namespace />parsePorcentaje(id, label) {
        var value = <portlet:namespace />trimValue(id);

        if (value == '') {
            value = '0';
            jQuery('#<portlet:namespace />' + id).val('0');
        }

        if (!/^[0-9]+$/.test(value)) {
            alert(label + ': debe ser un número entero entre 0 y 100. Valor recibido: "' + value + '".');
            jQuery('#<portlet:namespace />' + id).focus();
            return null;
        }

        var parsed = parseInt(value, 10);

        if (parsed < 0 || parsed > 100) {
            alert(label + ': debe estar entre 0 y 100. Valor recibido: ' + parsed + '.');
            jQuery('#<portlet:namespace />' + id).focus();
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />submitFormularioCompra(form) {
        if (!form) {
            alert('No se pudo encontrar el formulario principal de Compras.');
            return false;
        }

        try {
            form.submit();
            return true;
        } catch (e) {
            try {
                jQuery(form).submit();
                return true;
            } catch (e2) {
                alert(
                    'No se pudo enviar el formulario de Compras. ' +
                    'Error: ' + (e2 && e2.message ? e2.message : e2)
                );

                return false;
            }
        }
    }

    function <portlet:namespace />validarTokenGuardadoCompra() {
        var tokenInput = document.getElementById('<portlet:namespace />compras_save_token');

        if (!tokenInput
                || tokenInput.value == null
                || jQuery.trim(tokenInput.value) == ''
                || jQuery.trim(tokenInput.value) == 'null') {

            alert(
                'No se pudo preparar el guardado seguro del requerimiento. ' +
                'Falta el token de guardado. Vuelva a cargar la pantalla e intente nuevamente.'
            );

            return false;
        }

        return true;
    }

    <c:if test="<%= esNuevo %>">
        function <portlet:namespace />ordenMedicaDosDigitos(valor) {
            valor = parseInt(valor, 10);

            return valor < 10
                    ? '0' + valor
                    : String(valor);
        }

        function <portlet:namespace />validarOrdenMedicaAlta(form) {
            <portlet:namespace />actualizarContratoFilasOrdenMedica();

            var filas = jQuery(
                    '#<portlet:namespace />ordenes_medicas_body '
                            + 'tr.orden-medica-activa'
            );

            var fechaHidden = document.getElementById(
                    '<portlet:namespace />fecha_orden_medica_hidden'
            );

            if (!form || filas.length == 0 || !fechaHidden) {
                alert('No se pudieron preparar las Órdenes médicas para el envío.');
                return false;
            }

            var valido = true;

            filas.each(function(index) {
                if (!valido) {
                    return;
                }

                var fila = jQuery(this);

                var archivo =
                        fila.find(
                                'input.orden-medica-archivo'
                        ).get(0);

                /*
                 * Este hidden continúa siendo el valor que se envía
                 * al backend en formato AAAA-MM-DD.
                 */
                var fechaValorInput =
                        fila.find(
                                'input.orden-medica-fecha-valor'
                        ).get(0);

                /*
                 * La fecha visible ya no es un input de texto ni un
                 * calendario JavaScript propio.
                 *
                 * Se toma de Día / Mes / Año.
                 *
                 * Mes conserva la semántica de Calendar.MONTH:
                 * enero = 0, diciembre = 11.
                 */
                var fechaDia =
                        fila.find(
                                'select.orden-medica-fecha-dia'
                        ).get(0);

                var fechaMes =
                        fila.find(
                                'select.orden-medica-fecha-mes'
                        ).get(0);

                var fechaAnio =
                        fila.find(
                                'select.orden-medica-fecha-anio'
                        ).get(0);

                var numeroOrden = index + 1;

                if (!archivo
                        || jQuery.trim(
                                archivo.value || ''
                        ) == '') {

                    alert(
                            'Orden médica '
                                    + numeroOrden
                                    + ': debe seleccionar una imagen JPEG o PNG.'
                    );

                    if (archivo) {
                        archivo.focus();
                    }

                    valido = false;
                    return;
                }

                var nombreArchivo =
                        (archivo.value || '')
                                .replace(
                                        /^.*[\\\/]/,
                                        ''
                                );

                if (!/\.(jpe?g|png)$/i.test(nombreArchivo)) {
                    alert(
                            'Orden médica '
                                    + numeroOrden
                                    + ': sólo se permiten archivos JPG, JPEG o PNG.'
                    );

                    archivo.focus();

                    valido = false;
                    return;
                }

                if (!fechaDia
                        || !fechaMes
                        || !fechaAnio
                        || !fechaValorInput) {

                    alert(
                            'No se pudo preparar la fecha de la Orden médica '
                                    + numeroOrden
                                    + '.'
                    );

                    valido = false;
                    return;
                }

                var dia =
                        parseInt(
                                fechaDia.value,
                                10
                        );

                var mes =
                        parseInt(
                                fechaMes.value,
                                10
                        );

                var anio =
                        parseInt(
                                fechaAnio.value,
                                10
                        );

                /*
                 * Los controles nullable utilizan una opción vacía.
                 */
                if (isNaN(dia)
                        || isNaN(mes)
                        || isNaN(anio)
                        || dia < 1
                        || mes < 0
                        || anio < 1) {

                    alert(
                            'Fecha de la orden médica '
                                    + numeroOrden
                                    + ': debe informar día, mes y año.'
                    );

                    fechaDia.focus();

                    valido = false;
                    return;
                }

                var fechaControl =
                        new Date(
                                anio,
                                mes,
                                dia
                        );

                /*
                 * Evita fechas como 31/02.
                 */
                if (fechaControl.getFullYear() != anio
                        || fechaControl.getMonth() != mes
                        || fechaControl.getDate() != dia) {

                    alert(
                            'Fecha de la orden médica '
                                    + numeroOrden
                                    + ': la fecha informada no existe.'
                    );

                    fechaDia.focus();

                    valido = false;
                    return;
                }

                /*
                 * El backend conserva su contrato AAAA-MM-DD.
                 *
                 * El +1 es obligatorio porque el selector de mes
                 * utiliza enero=0.
                 */
                var fechaISO =
                        String(anio)
                                + '-'
                                + <portlet:namespace />ordenMedicaDosDigitos(
                                        mes + 1
                                )
                                + '-'
                                + <portlet:namespace />ordenMedicaDosDigitos(
                                        dia
                                );

                fechaValorInput.value =
                        fechaISO;

                /*
                 * Compatibilidad con el parámetro histórico correspondiente
                 * a la primera Orden médica.
                 */
                if (index == 0) {
                    fechaHidden.value =
                            fechaISO;
                }
            });

            if (!valido) {
                return false;
            }

            var cantidad = document.getElementById(
                    '<portlet:namespace />orden_medica_count'
            );

            if (!cantidad) {
                alert(
                        'No se pudo preparar la cantidad de Órdenes médicas para el envío.'
                );

                return false;
            }

            cantidad.value =
                    filas.length;

            return true;
        }

        function <portlet:namespace />incorporarOrdenesMedicas(form) {
            <portlet:namespace />actualizarContratoFilasOrdenMedica();

            var filas = jQuery(
                    '#<portlet:namespace />ordenes_medicas_body '
                            + 'tr.orden-medica-activa'
            );
            var cantidad = document.getElementById(
                    '<portlet:namespace />orden_medica_count'
            );

            if (!form || filas.length == 0 || !cantidad) {
                return null;
            }

            var contextos = [];
            var valido = true;

            function incorporarNodo(nodo) {
                if (!nodo || !nodo.parentNode) {
                    valido = false;
                    return;
                }

                contextos.push({
                    nodo: nodo,
                    padre: nodo.parentNode,
                    siguiente: nodo.nextSibling
                });

                try {
                    form.appendChild(nodo);
                } catch (e) {
                    valido = false;
                }
            }

            filas.each(function() {
                if (!valido) {
                    return;
                }

                var fila = jQuery(this);

                incorporarNodo(
                        fila.find('input.orden-medica-archivo').get(0)
                );
                incorporarNodo(
                        fila.find('input.orden-medica-fecha-valor').get(0)
                );
            });

            if (valido) {
                incorporarNodo(cantidad);
            }

            if (!valido) {
                <portlet:namespace />restaurarOrdenesMedicas(
                        contextos
                );
                return null;
            }

            return contextos;
        }

        function <portlet:namespace />restaurarOrdenesMedicas(contextos) {
            if (!contextos) {
                return;
            }

            for (var i = contextos.length - 1; i >= 0; i--) {
                var contexto = contextos[i];

                if (!contexto || !contexto.nodo || !contexto.padre) {
                    continue;
                }

                if (contexto.siguiente
                        && contexto.siguiente.parentNode == contexto.padre) {

                    contexto.padre.insertBefore(
                            contexto.nodo,
                            contexto.siguiente
                    );
                } else {
                    contexto.padre.appendChild(contexto.nodo);
                }
            }
        }
    </c:if>

    function <portlet:namespace />obtenerSerializadorDetallesCompra() {
        if (typeof <portlet:namespace />serializarDetallesCompras == 'function') {
            return <portlet:namespace />serializarDetallesCompras;
        }

        if (typeof window['<portlet:namespace />serializarDetallesCompras'] == 'function') {
            return window['<portlet:namespace />serializarDetallesCompras'];
        }

        return null;
    }

    function <portlet:namespace />guardarCotizacion() {
        if (<portlet:namespace />guardandoCompra) {
            return false;
        }

        <portlet:namespace />setGuardandoCompraActivo(true);

        var form =
                document.getElementById(
                        '<portlet:namespace />fmCompras'
                );

        if (!form) {
            alert('No se encontró el formulario de Compras.');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        jQuery(
                '#<portlet:namespace />compras_cmd'
        ).val('saveCotizacion');

        if (!<portlet:namespace />validarTokenGuardadoCompra()) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var serializador =
                <portlet:namespace />obtenerSerializadorDetallesCompra();

        if (serializador == null) {
            alert(
                    'No se encontró la función de '
                            + 'serialización de detalles.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (!serializador()) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        submitForm(form);

        return false;
    }

    function <portlet:namespace />guardar() {
        if (<portlet:namespace />guardandoCompra) {
            return false;
        }

        <portlet:namespace />setGuardandoCompraActivo(true);

        var form = document.getElementById('<portlet:namespace />fmCompras');

        if (!form) {
            alert('No se pudo encontrar el formulario principal de Compras. No se puede guardar el requerimiento.');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var cmdInput = document.getElementById('<portlet:namespace />compras_cmd');

        if (cmdInput) {
            cmdInput.value = 'saveAll';
        }

        var tokenInput = document.getElementById('<portlet:namespace />compras_save_token');

        if (!tokenInput
                || tokenInput.value == null
                || jQuery.trim(tokenInput.value) == ''
                || jQuery.trim(tokenInput.value) == 'null') {

            alert(
                'No se pudo preparar el guardado seguro del requerimiento. ' +
                'Falta el token de guardado. Vuelva a cargar la pantalla e intente nuevamente.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        <c:if test="<%= esNuevo %>">
            if (!<portlet:namespace />validarOrdenMedicaAlta(form)) {
                return <portlet:namespace />cancelarGuardadoCompra();
            }
        </c:if>

        var sectorId = <portlet:namespace />trimValue('sector_id');

        if (sectorId == '' || sectorId == '0') {
            alert('Sector: debe seleccionar un sector.');
            <portlet:namespace />focusSeguroCompra('#<portlet:namespace />sector_id');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (!<portlet:namespace />validarSurgeCompra()) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (!requiereAfiliado) {
            <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            <portlet:namespace />aplicarReglaCargosPorSector(false);
        }

        var cargoOspim = <portlet:namespace />parsePorcentaje('cargo_ospim', 'Cargo OSPIM');

        if (cargoOspim == null) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var cargoTercerizadora = <portlet:namespace />parsePorcentaje('cargo_tercerizadora', 'Cargo tercerizadora');

        if (cargoTercerizadora == null) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(cargoTercerizadora);

        if (cargoOspim + cargoTercerizadora != 100) {
            alert(
                'Cargos: la suma de Cargo OSPIM (' + cargoOspim +
                ') y Cargo tercerizadora (' + cargoTercerizadora +
                ') es ' + (cargoOspim + cargoTercerizadora) +
                '. Debe ser exactamente 100.'
            );

            <portlet:namespace />focusSeguroCompra('#<portlet:namespace />cargo_tercerizadora');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (requiereAfiliado) {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            var afiliadoCuilTitular = <portlet:namespace />trimValue('afiliado_cuil_titular');
            var afiliadoInt = <portlet:namespace />trimValue('afiliado_int');

            if (afiliadoCuilTitular == '') {
                alert('Afiliado: debe seleccionar un afiliado. Falta CUIL titular.');
                return <portlet:namespace />cancelarGuardadoCompra();
            }

            if (afiliadoInt == '') {
                alert('Afiliado: debe seleccionar un afiliado. Falta integrante.');
                return <portlet:namespace />cancelarGuardadoCompra();
            }
        } else {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        }

        <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(cargoTercerizadora);

        if (requiereAfiliado
                && cargoTercerizadora > 0
                && <portlet:namespace />trimValue('requerimiento_id_tercerizadora') == '') {
            alert('Tercerizadora: debe seleccionar un afiliado con tercerizadora porque Cargo tercerizadora es mayor a 0.');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        <portlet:namespace />sincronizarFormularioCompra();

        var serializadorDetalles = null;

        if (typeof <portlet:namespace />serializarDetallesCompras == 'function') {
            serializadorDetalles = <portlet:namespace />serializarDetallesCompras;
        } else if (typeof window['<portlet:namespace />serializarDetallesCompras'] == 'function') {
            serializadorDetalles = window['<portlet:namespace />serializarDetallesCompras'];
        }

        if (serializadorDetalles == null) {
            alert(
                'Detalles: no se encontró la función <portlet:namespace />serializarDetallesCompras(). ' +
                'El JSP embebido no se está renderizando correctamente o Liferay está usando una versión vieja compilada.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (!serializadorDetalles()) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var detalleCountInput = jQuery(form).find('input[name$="detalle_count"]');

        if (detalleCountInput.length == 0) {
            alert(
                'Detalles: serializarDetallesCompras() se ejecutó, pero no dejó detalle_count dentro del formulario principal. ' +
                'Revisar que el JSP embebido agregue los hidden a #<portlet:namespace />fmCompras.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var detalleCount = parseInt(detalleCountInput.val(), 10);

        if (isNaN(detalleCount) || detalleCount <= 0) {
            alert('Detalles: no hay detalles para guardar. detalle_count=' + detalleCountInput.val());
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var contextosOrdenesMedicas = null;

        <c:if test="<%= esNuevo %>">
            contextosOrdenesMedicas =
                    <portlet:namespace />incorporarOrdenesMedicas(form);

            if (!contextosOrdenesMedicas) {
                alert('No se pudieron incorporar las Órdenes médicas al formulario de envío.');
                return <portlet:namespace />cancelarGuardadoCompra();
            }
        </c:if>

        if (!<portlet:namespace />submitFormularioCompra(form)) {
            <c:if test="<%= esNuevo %>">
                <portlet:namespace />restaurarOrdenesMedicas(
                        contextosOrdenesMedicas
                );
            </c:if>

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        return false;
    }

    jQuery(function() {
        <portlet:namespace />cargarAfiliadoInicial();

        <c:if test="<%= !esNuevo %>">
            <portlet:namespace />cargarAfiliadoExistenteEnEdicion();
        </c:if>

        <portlet:namespace />actualizarVisibilidadAfiliado(false);
        <portlet:namespace />actualizarVisibilidadObservaciones(false);
        <portlet:namespace />aplicarReglaCargosPorSector(false);
        <portlet:namespace />sincronizarFormularioCompra();

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').change(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').keyup(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />sector_id, #<portlet:namespace />id_sector').change(function() {
            <portlet:namespace />cambiarSectorCompra(true);
        });

        jQuery('#<portlet:namespace />observaciones').change(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />observaciones').keyup(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />surge').change(function() {
            <portlet:namespace />actualizarSurgeCompra();
        });

        /*
         * Si CUIL o integrante se modifican manualmente, el CUD
         * visible deja de ser confiable hasta volver a seleccionar
         * un afiliado.
         */
        jQuery(
                '#<portlet:namespace />cuil, '
                + '#<portlet:namespace />inte'
        ).change(function() {

            <portlet:namespace />ocultarVencimientoCudAfiliado();
            <portlet:namespace />ocultarSituacionMedicaAfiliado();
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery(
                '#<portlet:namespace />cuil, '
                + '#<portlet:namespace />inte'
        ).keyup(function() {

            <portlet:namespace />ocultarVencimientoCudAfiliado();
            <portlet:namespace />ocultarSituacionMedicaAfiliado();
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery(
                '#<portlet:namespace />id_tercerizadora'
        ).change(function() {

            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery(
                '#<portlet:namespace />id_tercerizadora'
        ).keyup(function() {

            <portlet:namespace />sincronizarFormularioCompra();
        });

        if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
            window['<portlet:namespace />filtrarArticulosPorSector']();
        }

        setTimeout(function() {
            <portlet:namespace />actualizarVisibilidadAfiliado(false);
            <portlet:namespace />actualizarVisibilidadObservaciones(false);
            <portlet:namespace />aplicarReglaCargosPorSector(false);
            <portlet:namespace />sincronizarFormularioCompra();

            if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
                window['<portlet:namespace />filtrarArticulosPorSector']();
            }
        }, 300);
    });
</script>
