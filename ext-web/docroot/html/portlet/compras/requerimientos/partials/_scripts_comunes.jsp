<script type="text/javascript">
    function <portlet:namespace />valorInputCompra(id) {
        var el = document.getElementById('<portlet:namespace />' + id);

        if (!el || typeof el.value == 'undefined' || el.value == null) {
            return '';
        }

        return String(el.value).replace(/^\s+|\s+$/g, '');
    }

    function <portlet:namespace />parsePorcentajeSilencioso(id) {
        var value = <portlet:namespace />valorInputCompra(id);

        if (value == '') {
            return null;
        }

        if (!/^[0-9]+$/.test(value)) {
            return null;
        }

        var parsed = parseInt(value, 10);

        if (isNaN(parsed) || parsed < 0 || parsed > 100) {
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(cargoTercerizadoraForzado) {
        var cargoTercerizadora = null;

        if (typeof cargoTercerizadoraForzado != 'undefined'
                && cargoTercerizadoraForzado != null) {
            cargoTercerizadora = cargoTercerizadoraForzado;
        } else {
            cargoTercerizadora = <portlet:namespace />parsePorcentajeSilencioso('cargo_tercerizadora');
        }

        var recuperoActivo = cargoTercerizadora != null && cargoTercerizadora > 0;

        var recuperoEl = document.getElementById('<portlet:namespace />recupero');

        if (recuperoEl) {
            recuperoEl.checked = recuperoActivo;
            recuperoEl.defaultChecked = recuperoActivo;

            if (recuperoActivo) {
                recuperoEl.setAttribute('checked', 'checked');
            } else {
                recuperoEl.removeAttribute('checked');
            }
        }

        var recuperoHiddenEl = document.getElementById('<portlet:namespace />recupero_hidden');

        if (recuperoHiddenEl) {
            recuperoHiddenEl.value = recuperoActivo ? 'true' : 'false';
        }

        return recuperoActivo;
    }

    function <portlet:namespace />actualizarSurgeCompra() {
        var surgeEl =
                document.getElementById(
                        '<portlet:namespace />surge'
                );

        var surgeHiddenEl =
                document.getElementById(
                        '<portlet:namespace />surge_hidden'
                );

        var surgeValue = '';

        if (surgeEl
                && typeof surgeEl.value != 'undefined'
                && surgeEl.value != null) {

            surgeValue =
                    String(surgeEl.value)
                            .replace(/^\s+|\s+$/g, '');
        }

        /*
         * Sólo 0 y 1 son valores válidos.
         * Cualquier otro valor se normaliza a vacío.
         */
        if (surgeValue != '0'
                && surgeValue != '1') {

            surgeValue = '';
        }

        if (surgeHiddenEl) {
            surgeHiddenEl.value = surgeValue;
        }

        return surgeValue;
    }

    function <portlet:namespace />validarSurgeCompra() {
        var surgeValue =
                <portlet:namespace />actualizarSurgeCompra();

        var surgeEl =
                document.getElementById(
                        '<portlet:namespace />surge'
                );

        if (surgeValue == '0'
                || surgeValue == '1') {

            return true;
        }

        alert('Surge: debe seleccionar Sí o No.');

        if (surgeEl
                && typeof surgeEl.focus == 'function') {

            surgeEl.focus();
        }

        return false;
    }

</script>
