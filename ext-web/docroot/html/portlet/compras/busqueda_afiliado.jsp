<%@ include file="/html/portlet/compras/init.jsp" %>

<%
    String editModeParam = ParamUtil.getString(request, "edit_mode", "true");
    boolean editMode = Boolean.parseBoolean(editModeParam);

    String prefijo = ParamUtil.getString(request, "origen", "");

    String cuil = ParamUtil.getString(request, "cuil", "");
    String inte = ParamUtil.getString(request, "inte", "");
    String tipoDocSeleccionado = ParamUtil.getString(request, "tipoDoc", "");
    String nroDoc = ParamUtil.getString(request, "nroDoc", "");
    String apellido = ParamUtil.getString(request, "apellido", "");
    String nombre = ParamUtil.getString(request, "nombre", "");
    String entidadSeleccionada = ParamUtil.getString(request, "entidad", WebKeysGlobal.ENTIDAD_OSPIM);
    String numeroAfi = ParamUtil.getString(request, "numero_afi", "");
    String idSeccional = ParamUtil.getString(request, "id_seccional", "");
    String seccional = ParamUtil.getString(request, "seccional", "");
    String bajaFecha = ParamUtil.getString(request, "baja_fecha", "");

    PortletURL buscarAfiliadosURL = renderResponse.createRenderURL();
    buscarAfiliadosURL.setWindowState(LiferayWindowState.EXCLUSIVE);
    buscarAfiliadosURL.setParameter("struts_action", "/compras/buscar_afiliados");
%>

<style type="text/css">
    #<portlet:namespace />afiliado_requerimiento_panel<%= prefijo %> {
        position: relative;
    }

    #<portlet:namespace />afiliado_requerimiento_panel<%= prefijo %> .compras-afiliado-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 5px;
    }

    #<portlet:namespace />afiliado_requerimiento_panel<%= prefijo %> .compras-afiliado-readonly {
        background: #f3f3f3;
    }

    #<portlet:namespace />afiliado_requerimiento_panel<%= prefijo %> .compras-afiliado-baja {
        background: #c62828 !important;
        color: #ffffff !important;
        font-weight: bold;
    }
</style>

<div id="<portlet:namespace />afiliado_requerimiento_panel<%= prefijo %>">
    <table class="lfr-table compras-afiliado-table">
        <tr>
            <td><label>Entidad:</label></td>
            <td>
                <select name="<portlet:namespace />entidad<%= prefijo %>"
                        id="<portlet:namespace />entidad<%= prefijo %>"
                        <%= !editMode ? "disabled=\"disabled\"" : "" %>>
                    <%
                        for (int i = 0; i < WebKeysGlobal.ENTIDADES_UOMA.length; i++) {
                            String entidad = WebKeysGlobal.ENTIDADES_UOMA[i];
                            String selected = entidad.equalsIgnoreCase(entidadSeleccionada) ? "selected=\"selected\"" : "";
                    %>
                        <option value="<%= HtmlUtil.escape(entidad) %>" <%= selected %>>
                            <%= HtmlUtil.escape(entidad) %>
                        </option>
                    <%
                        }
                    %>
                </select>
            </td>

            <td><label>N&uacute;mero afi.:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />numero_afi<%= prefijo %>"
                       id="<portlet:namespace />numero_afi<%= prefijo %>"
                       value="<%= HtmlUtil.escape(numeroAfi) %>"
                       size="8"
                       maxlength="10"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>

            <td><label>CUIL:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />cuil<%= prefijo %>"
                       id="<portlet:namespace />cuil<%= prefijo %>"
                       value="<%= HtmlUtil.escape(cuil) %>"
                       size="13"
                       maxlength="11"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>

            <td><label>Integrante:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />inte<%= prefijo %>"
                       id="<portlet:namespace />inte<%= prefijo %>"
                       value="<%= HtmlUtil.escape(inte) %>"
                       size="2"
                       maxlength="2"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>
        </tr>

        <tr>
            <td><label>Tipo doc.:</label></td>
            <td>
                <select name="<portlet:namespace />tipoDoc<%= prefijo %>"
                        id="<portlet:namespace />tipoDoc<%= prefijo %>"
                        <%= !editMode ? "disabled=\"disabled\"" : "" %>>
                    <option value=""></option>
                    <%
                        for (int i = 0; i < WebKeysAfiliados.TIPOS_DOCUMENTO.length; i++) {
                            String tipoDoc = WebKeysAfiliados.TIPOS_DOCUMENTO[i];
                            String selected = tipoDoc.equalsIgnoreCase(tipoDocSeleccionado) ? "selected=\"selected\"" : "";
                    %>
                        <option value="<%= HtmlUtil.escape(tipoDoc) %>" <%= selected %>>
                            <%= HtmlUtil.escape(tipoDoc) %>
                        </option>
                    <%
                        }
                    %>
                </select>
            </td>

            <td><label>Nro. doc.:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />nroDoc<%= prefijo %>"
                       id="<portlet:namespace />nroDoc<%= prefijo %>"
                       value="<%= HtmlUtil.escape(nroDoc) %>"
                       size="10"
                       maxlength="8"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>

            <td><label>Apellido:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />apellido<%= prefijo %>"
                       id="<portlet:namespace />apellido<%= prefijo %>"
                       value="<%= HtmlUtil.escape(apellido) %>"
                       size="22"
                       maxlength="100"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>

            <td><label>Nombre:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />nombre<%= prefijo %>"
                       id="<portlet:namespace />nombre<%= prefijo %>"
                       value="<%= HtmlUtil.escape(nombre) %>"
                       size="22"
                       maxlength="100"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>
        </tr>

        <tr>
            <td><label>Seccional:</label></td>
            <td colspan="3">
                <input type="hidden"
                       name="<portlet:namespace />id_seccional<%= prefijo %>"
                       id="<portlet:namespace />id_seccional<%= prefijo %>"
                       value="<%= HtmlUtil.escape(idSeccional) %>" />

                <input type="hidden"
                       name="<portlet:namespace />secc_seleccionada<%= prefijo %>"
                       id="<portlet:namespace />secc_seleccionada<%= prefijo %>"
                       value="<%= Validator.isNotNull(idSeccional) ? "1" : "0" %>" />

                <input type="text"
                       name="<portlet:namespace />seccional<%= prefijo %>"
                       id="<portlet:namespace />seccional<%= prefijo %>"
                       value="<%= HtmlUtil.escape(seccional) %>"
                       size="40"
                       maxlength="100"
                       <%= !editMode ? "readonly=\"readonly\"" : "" %> />
            </td>

            <td><label>Baja:</label></td>
            <td>
                <input type="text"
                       name="<portlet:namespace />baja_fecha<%= prefijo %>"
                       id="<portlet:namespace />baja_fecha<%= prefijo %>"
                       value="<%= HtmlUtil.escape(bajaFecha) %>"
                       size="12"
                       readonly="readonly"
                       class="<%= Validator.isNotNull(bajaFecha) ? "compras-afiliado-baja" : "compras-afiliado-readonly" %>" />
            </td>

            <td colspan="2" align="right">
                <c:if test="<%= editMode %>">
                    <input type="button"
                           id="<portlet:namespace />btnBuscarAfiliado<%= prefijo %>"
                           value="Buscar afiliado"
                           onClick="javascript:<portlet:namespace />buscarAfiliados<%= prefijo %>();" />

                    &nbsp;&nbsp;

                    <input type="button"
                           id="<portlet:namespace />btnLimpiarAfiliado<%= prefijo %>"
                           value="Limpiar campos"
                           onClick="javascript:<portlet:namespace />limpiarCamposAfiliado<%= prefijo %>();" />
                </c:if>
            </td>
        </tr>
    </table>
</div>

<input type="hidden"
       name="<portlet:namespace />fecha_alta_af<%= prefijo %>"
       id="<portlet:namespace />fecha_alta_af<%= prefijo %>"
       value="" />

<input type="hidden"
       name="<portlet:namespace />incapacidad_af<%= prefijo %>"
       id="<portlet:namespace />incapacidad_af<%= prefijo %>"
       value="" />

<input type="hidden"
       name="<portlet:namespace />id_tercerizadora<%= prefijo %>"
       id="<portlet:namespace />id_tercerizadora<%= prefijo %>"
       value="" />

<input type="hidden"
       name="<portlet:namespace />nroSocioPrevencion<%= prefijo %>"
       id="<portlet:namespace />nroSocioPrevencion<%= prefijo %>"
       value="" />

<input type="hidden"
       name="<portlet:namespace />nroCredencialPrevencion<%= prefijo %>"
       id="<portlet:namespace />nroCredencialPrevencion<%= prefijo %>"
       value="" />

<script type="text/javascript">
    var <portlet:namespace />popupAfiliadoCompras<%= prefijo %> = null;

    function <portlet:namespace />trimAfiliadoCompras<%= prefijo %>(value) {
        if (value == null) {
            return '';
        }

        return jQuery.trim(String(value));
    }

    function <portlet:namespace />getAfiliadoValue<%= prefijo %>(id) {
        return <portlet:namespace />trimAfiliadoCompras<%= prefijo %>(
            jQuery('#<portlet:namespace />' + id + '<%= prefijo %>').val()
        );
    }

    function <portlet:namespace />setAfiliadoValue<%= prefijo %>(id, value) {
        jQuery('#<portlet:namespace />' + id + '<%= prefijo %>').val(value == null || value == 'null' ? '' : value);
    }

    function <portlet:namespace />sincronizarConRequerimientoCompra<%= prefijo %>() {
        if (typeof <portlet:namespace />sincronizarAfiliadoRequerimiento == 'function') {
            <portlet:namespace />sincronizarAfiliadoRequerimiento();
        }
    }

    function <portlet:namespace />validarBusquedaAfiliadoCompras<%= prefijo %>() {
        var cuil = <portlet:namespace />getAfiliadoValue<%= prefijo %>('cuil');
        var inte = <portlet:namespace />getAfiliadoValue<%= prefijo %>('inte');
        var tipoDoc = <portlet:namespace />getAfiliadoValue<%= prefijo %>('tipoDoc');
        var nroDoc = <portlet:namespace />getAfiliadoValue<%= prefijo %>('nroDoc');
        var seccional = <portlet:namespace />getAfiliadoValue<%= prefijo %>('id_seccional');
        var apellido = <portlet:namespace />getAfiliadoValue<%= prefijo %>('apellido');
        var nombre = <portlet:namespace />getAfiliadoValue<%= prefijo %>('nombre');
        var entidad = <portlet:namespace />getAfiliadoValue<%= prefijo %>('entidad');
        var numeroAfi = <portlet:namespace />getAfiliadoValue<%= prefijo %>('numero_afi');

        if (cuil == '' &&
            inte == '' &&
            tipoDoc == '' &&
            nroDoc == '' &&
            seccional == '' &&
            apellido == '' &&
            nombre == '' &&
            entidad == '' &&
            numeroAfi == '') {

            alert('Debe ingresar al menos un parámetro de búsqueda.');
            return false;
        }

        if (cuil != '' && typeof validarCuil == 'function') {
            if (!validarCuil(cuil, 'CUIL inválido. Verifique el dato ingresado.')) {
                jQuery('#<portlet:namespace />cuil<%= prefijo %>').focus();
                return false;
            }
        }

        return true;
    }

    function <portlet:namespace />buscarAfiliados<%= prefijo %>() {
        if (!<portlet:namespace />validarBusquedaAfiliadoCompras<%= prefijo %>()) {
            return false;
        }

        var cuil = <portlet:namespace />getAfiliadoValue<%= prefijo %>('cuil');
        var inte = <portlet:namespace />getAfiliadoValue<%= prefijo %>('inte');
        var tipoDoc = <portlet:namespace />getAfiliadoValue<%= prefijo %>('tipoDoc');
        var nroDoc = <portlet:namespace />getAfiliadoValue<%= prefijo %>('nroDoc');
        var seccional = <portlet:namespace />getAfiliadoValue<%= prefijo %>('id_seccional');
        var apellido = <portlet:namespace />getAfiliadoValue<%= prefijo %>('apellido');
        var nombre = <portlet:namespace />getAfiliadoValue<%= prefijo %>('nombre');
        var entidad = <portlet:namespace />getAfiliadoValue<%= prefijo %>('entidad');
        var numeroAfi = <portlet:namespace />getAfiliadoValue<%= prefijo %>('numero_afi');

        if (jQuery('#<portlet:namespace />secc_seleccionada<%= prefijo %>').val() != '1') {
            jQuery('#<portlet:namespace />seccional<%= prefijo %>').val('');
            jQuery('#<portlet:namespace />id_seccional<%= prefijo %>').val('');
            seccional = '';
        }

        <portlet:namespace />popupAfiliadoCompras<%= prefijo %> = Liferay.Popup({
            title: 'Búsqueda de afiliado',
            modal: true,
            width: 830
        });

        var url = '<%= HtmlUtil.escapeJS(buscarAfiliadosURL.toString()) %>';
        url += '&cuil=' + encodeURIComponent(cuil);
        url += '&inte=' + encodeURIComponent(inte);
        url += '&tipoDoc=' + encodeURIComponent(tipoDoc);
        url += '&nroDoc=' + encodeURIComponent(nroDoc);
        url += '&seccional=' + encodeURIComponent(seccional);
        url += '&apellido=' + encodeURIComponent(apellido);
        url += '&nombre=' + encodeURIComponent(nombre);
        url += '&entidad=' + encodeURIComponent(entidad);
        url += '&numero_afi=' + encodeURIComponent(numeroAfi);
        url += '&origen=' + encodeURIComponent('<%= prefijo %>');
        url += '&popup=true';

        jQuery(<portlet:namespace />popupAfiliadoCompras<%= prefijo %>).load(url);

        return false;
    }

    function seleccionaAfiliado<%= prefijo %>(
        cuil,
        inte,
        docuTipo,
        docuNro,
        nombre,
        apellido,
        idSeccional,
        descSeccional,
        ospim,
        uoma,
        amtima,
        bajaFecha,
        nombrePlan,
        idPlan,
        fechaAltaAf,
        incapacidadAf,
        idTercerizadora,
        afiTercerizadora,
        reclamoPrestacional,
        nroSocioPrev,
        nroCredenPrev,
        fechaRecepcion,
        tieneAntecedentes
    ) {
        seleccionaCamposAfiliado<%= prefijo %>(
            cuil,
            inte,
            docuTipo,
            docuNro,
            nombre,
            apellido,
            idSeccional,
            descSeccional,
            ospim,
            uoma,
            amtima,
            bajaFecha,
            nombrePlan,
            idPlan,
            fechaAltaAf,
            incapacidadAf,
            idTercerizadora,
            afiTercerizadora,
            reclamoPrestacional,
            nroSocioPrev,
            nroCredenPrev,
            fechaRecepcion,
            tieneAntecedentes
        );

        if (<portlet:namespace />popupAfiliadoCompras<%= prefijo %> != null) {
            Liferay.Popup.close(<portlet:namespace />popupAfiliadoCompras<%= prefijo %>);
        }
    }

    function seleccionaCamposAfiliado<%= prefijo %>(
        cuil,
        inte,
        docuTipo,
        docuNro,
        nombre,
        apellido,
        idSeccional,
        descSeccional,
        ospim,
        uoma,
        amtima,
        bajaFecha,
        nombrePlan,
        idPlan,
        fechaAltaAf,
        incapacidadAf,
        idTercerizadora,
        afiTercerizadora,
        reclamoPrestacional,
        nroSocioPrev,
        nroCredenPrev,
        fechaRecepcion,
        tieneAntecedentes
    ) {
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('cuil', cuil);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('inte', inte);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('tipoDoc', docuTipo);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroDoc', docuNro);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('apellido', apellido);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nombre', nombre);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('id_seccional', idSeccional);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('seccional', descSeccional);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('baja_fecha', bajaFecha);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('fecha_alta_af', fechaAltaAf);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('incapacidad_af', incapacidadAf);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('id_tercerizadora', idTercerizadora);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroSocioPrevencion', nroSocioPrev);
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroCredencialPrevencion', nroCredenPrev);

        jQuery('#<portlet:namespace />secc_seleccionada<%= prefijo %>').val(idSeccional == null || idSeccional == '' || idSeccional == 'null' ? '0' : '1');

        var entidad = <portlet:namespace />getAfiliadoValue<%= prefijo %>('entidad');
        var numeroAfi = '';

        if (entidad == '<%= HtmlUtil.escapeJS(WebKeysGlobal.ENTIDAD_OSPIM) %>') {
            numeroAfi = ospim;
        }
        else if (entidad == '<%= HtmlUtil.escapeJS(WebKeysGlobal.ENTIDAD_UOMA) %>') {
            numeroAfi = uoma;
        }
        else if (entidad == '<%= HtmlUtil.escapeJS(WebKeysGlobal.ENTIDAD_AMTIMA) %>') {
            numeroAfi = amtima;
        }

        <portlet:namespace />setAfiliadoValue<%= prefijo %>('numero_afi', numeroAfi);

        if (<portlet:namespace />getAfiliadoValue<%= prefijo %>('baja_fecha') != '') {
            jQuery('#<portlet:namespace />baja_fecha<%= prefijo %>').addClass('compras-afiliado-baja');
        }
        else {
            jQuery('#<portlet:namespace />baja_fecha<%= prefijo %>').removeClass('compras-afiliado-baja');
        }

        <portlet:namespace />sincronizarConRequerimientoCompra<%= prefijo %>();
    }

    function <portlet:namespace />limpiarCamposAfiliado<%= prefijo %>() {
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('cuil', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('inte', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('tipoDoc', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroDoc', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('apellido', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nombre', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('numero_afi', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('id_seccional', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('seccional', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('baja_fecha', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('fecha_alta_af', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('incapacidad_af', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('id_tercerizadora', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroSocioPrevencion', '');
        <portlet:namespace />setAfiliadoValue<%= prefijo %>('nroCredencialPrevencion', '');

        jQuery('#<portlet:namespace />secc_seleccionada<%= prefijo %>').val('0');
        jQuery('#<portlet:namespace />baja_fecha<%= prefijo %>').removeClass('compras-afiliado-baja');

        <portlet:namespace />sincronizarConRequerimientoCompra<%= prefijo %>();
    }

    /*
     * Alias intencional.
     * El JSP padre de edición de requerimiento chequea/call-ea:
     *   <portlet:namespace />limpiarCampos()
     */
    function <portlet:namespace />limpiarCampos() {
        <portlet:namespace />limpiarCamposAfiliado<%= prefijo %>();
    }

    jQuery(function() {
        if (<portlet:namespace />getAfiliadoValue<%= prefijo %>('baja_fecha') != '') {
            jQuery('#<portlet:namespace />baja_fecha<%= prefijo %>').addClass('compras-afiliado-baja');
        }

        <portlet:namespace />sincronizarConRequerimientoCompra<%= prefijo %>();
    });
</script>