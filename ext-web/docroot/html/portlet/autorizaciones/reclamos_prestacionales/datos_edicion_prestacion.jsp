<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%
// Prestación en proceso de edición.
PrestacionesReclamo prestacionEnEdicion =
        (PrestacionesReclamo) request
                .getSession()
                .getAttribute(
                        WebKeysAutorizaciones
                                .PRESTACION_EN_PROCESO_DE_EDICION
                );

request.getSession().removeAttribute(
        WebKeysAutorizaciones
                .PRESTACION_EN_PROCESO_DE_EDICION
);

Integer tipoedicion = Integer.valueOf(0);
String ocultarSeccional = null;

Calendar fechaseccional = Calendar.getInstance();
Calendar fechaPrestacion = Calendar.getInstance();

if (prestacionEnEdicion != null) {
    Object tipoEdicionObj =
            request.getAttribute(
                    "tipoEdicion"
            );

    if (tipoEdicionObj instanceof Integer) {
        tipoedicion =
                (Integer) tipoEdicionObj;
    }

    if (prestacionEnEdicion.getComprobanteFecha() != null) {
        fechaseccional.setTime(
                prestacionEnEdicion
                        .getComprobanteFecha()
        );
    }

    if (prestacionEnEdicion.getFechaPrestacion() != null) {
        fechaPrestacion.setTime(
                prestacionEnEdicion
                        .getFechaPrestacion()
        );
    }
}

String captionbotoncancelar =
        "Cancelar Edicion de la Prestacion";

String captionlabelproceso =
        "PRESTACION EN PROCESO DE EDICION";

String estiloLabel = "";

if (tipoedicion.intValue() == 1) {
    captionbotoncancelar =
            "Cancelar Autorizacion de la Prestacion";

    captionlabelproceso =
            "PRESTACION EN PROCESO DE AUTORIZACION";

    estiloLabel =
            "style='color:green;'";
}

if (tipoedicion.intValue() == 2) {
    captionbotoncancelar =
            "Cancelar Rechazo de la Prestacion";

    captionlabelproceso =
            "PRESTACION EN PROCESO DE RECHAZO";

    estiloLabel =
            "style='color:red;'";
}

ocultarSeccional =
        (String) request.getAttribute(
                "ocultar"
        );

String frecuenciaEdicion =
        prestacionEnEdicion != null
                ? prestacionEnEdicion.getFrecuencia()
                : null;

String comprobanteTipoEdicion =
        prestacionEnEdicion != null
                ? prestacionEnEdicion.getComprobanteTipo()
                : null;

String comprobanteCuitEdicion =
        prestacionEnEdicion != null
        && prestacionEnEdicion.getComprobanteCUIT() != null
                ? prestacionEnEdicion.getComprobanteCUIT()
                : "";

String comprobanteCuitSucursalEdicion =
        prestacionEnEdicion != null
        && prestacionEnEdicion
                .getComprobanteCUITSucursal() != null
                ? prestacionEnEdicion
                        .getComprobanteCUITSucursal()
                : "";

String comprobanteRazonSocialEdicion =
        prestacionEnEdicion != null
        && prestacionEnEdicion
                .getComprobanteRazonSocial() != null
                ? prestacionEnEdicion
                        .getComprobanteRazonSocial()
                : "";

String comprobanteLetraEdicion =
        prestacionEnEdicion != null
        && prestacionEnEdicion
                .getComprobanteLetra() != null
                ? prestacionEnEdicion
                        .getComprobanteLetra()
                : "";

Integer idPrest = null;
Integer idMedic = null;

if (prestacionEnEdicion != null) {
    idPrest =
            prestacionEnEdicion
                    .getId_prestacion();

    idMedic =
            prestacionEnEdicion
                    .getId_medicamento();
}

boolean sinMedicamento =
        idMedic == null
        || idMedic.intValue() == 0;

boolean hayPrestacion =
        idPrest != null
        && idPrest.intValue() != 0;

boolean mostrarCodigoPresentado =
        hayPrestacion
        || sinMedicamento;
%>

<input
    type="hidden"
    id="<portlet:namespace />idRegistro"
    name="<portlet:namespace />idRegistro"
    value="<%=prestacionEnEdicion != null
            ? prestacionEnEdicion.getIdRegistro()
            : ""%>"
/>

<input
    type="hidden"
    id="<portlet:namespace />nom_seleccionado_edit"
    name="<portlet:namespace />nom_seleccionado_edit"
    value=""
/>

<input
    type="hidden"
    id="<portlet:namespace />tipoNomenclador_edit"
    name="<portlet:namespace />tipoNomenclador_edit"
    value=""
/>

<label <%=estiloLabel%>>
    <b>
        <liferay-ui:message
            key="<%=captionlabelproceso%>"
        />
    </b>
</label>

<table
    class="lfr-table"
    style="border-collapse: separate; border-spacing: 3px;"
>
    <tr>
        <td>
            <label>F. Prestación:</label>

            <liferay-ui:input-date
                dayParam="fechaPrestacionDiaEdicion"
                dayValue="<%=prestacionEnEdicion != null
                        && prestacionEnEdicion
                                .getFechaPrestacion() != null
                                ? fechaPrestacion.get(
                                        Calendar.DAY_OF_MONTH
                                )
                                : 0%>"
                dayNullable="<%=true%>"
                monthParam="fechaPrestacionMesEdicion"
                monthValue="<%=prestacionEnEdicion != null
                        && prestacionEnEdicion
                                .getFechaPrestacion() != null
                                ? fechaPrestacion.get(
                                        Calendar.MONTH
                                )
                                : -1%>"
                monthNullable="<%=true%>"
                yearParam="fechaPrestacionAnioEdicion"
                yearValue="<%=prestacionEnEdicion != null
                        && prestacionEnEdicion
                                .getFechaPrestacion() != null
                                ? fechaPrestacion.get(
                                        Calendar.YEAR
                                )
                                : -1%>"
                yearNullable="<%=true%>"
                yearRangeStart="<%=fechaseccional.get(
                        Calendar.YEAR
                ) - 5%>"
                yearRangeEnd="<%=fechaseccional.get(
                        Calendar.YEAR
                ) + 1%>"
                firstDayOfWeek=""
            />
        </td>

        <% if (mostrarCodigoPresentado) { %>

            <td>
                <label>
                    <liferay-ui:message
                        key="codigo-presentado"
                    />:
                </label>
            </td>

            <td>
                <input
                    id="<portlet:namespace />codigoSeguimiento_filtro_edit"
                    name="<portlet:namespace />codigoSeguimiento_filtro_edit"
                    size="10"
                    maxlength="20"
                    type="text"
                    value=""
                />
            </td>

            <td>
                <input
                    id="<portlet:namespace />descripcionSeguimiento_filtro_edit"
                    name="<portlet:namespace />descripcionSeguimiento_filtro_edit"
                    size="60"
                    maxlength="200"
                    type="text"
                    value=""
                />
            </td>

            <td>
                <div
                    style="width:4%;"
                    id="<portlet:namespace />divBtnBusca"
                >
                    <a
                        href="javascript:void(0);"
                        onclick="<portlet:namespace />buscarNomencladorAutocompletar_edit();"
                        tabindex="-1"
                    >
                        Buscar
                    </a>

                    <a
                        href="javascript:void(0);"
                        onclick="<portlet:namespace />limpiarNomencladorAutocompletar();"
                        tabindex="-1"
                    >
                        Limpiar
                    </a>
                </div>
            </td>

        <% } else { %>

            <td colspan="6">
                <liferay-util:include
                    page="/html/portlet/utils/medicamentos_edit/busqueda_medicamentos_edit.jsp"
                >
                    <liferay-util:param
                        name="search_url_edit"
                        value="/autorizaciones/buscar_medicamentos_edit"
                    />

                    <liferay-util:param
                        name="troquel"
                        value=""
                    />

                    <liferay-util:param
                        name="nombre_medicamento_edit"
                        value=""
                    />

                    <liferay-util:param
                        name="id_medicamento_edit"
                        value=""
                    />

                    <liferay-util:param
                        name="esEditable"
                        value="true"
                    />

                    <liferay-util:param
                        name="mostrar_con_presentacion_edit"
                        value="true"
                    />
                </liferay-util:include>
            </td>

        <% } %>

        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
</table>

<table
    class="lfr-table"
    style="border-collapse: separate; border-spacing: 3px;"
>
    <tr>
        <td colspan="15">
            <div id="<portlet:namespace />datos_comprobante">
                <fieldset class="block-labels">
                    <legend>
                        <liferay-ui:message
                            key="Datos del Comprobante"
                        />
                    </legend>

                    <table>
                        <% if (ocultarSeccional == null) { %>

                            <tr>
                                <td>
                                    <label>
                                        <liferay-ui:message
                                            key="Frecuencia"
                                        />:
                                    </label>
                                </td>

                                <td>
                                    <select
                                        name="<portlet:namespace />frecuenciaEdicion"
                                        id="<portlet:namespace />frecuenciaEdicion"
                                    >
                                        <option
                                            value="SELECCIONE"
                                            <%="SELECCIONE".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            SELECCIONE
                                        </option>

                                        <option
                                            value="UNICA"
                                            <%="UNICA".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            UNICA
                                        </option>

                                        <option
                                            value="SEMANAL"
                                            <%="SEMANAL".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            SEMANAL
                                        </option>

                                        <option
                                            value="TRIMESTRAL"
                                            <%="TRIMESTRAL".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            TRIMESTRAL
                                        </option>

                                        <option
                                            value="MENSUAL"
                                            <%="MENSUAL".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            MENSUAL
                                        </option>

                                        <option
                                            value="SEMESTRAL"
                                            <%="SEMESTRAL".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            SEMESTRAL
                                        </option>

                                        <option
                                            value="ANUAL"
                                            <%="ANUAL".equals(
                                                    frecuenciaEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            ANUAL
                                        </option>
                                    </select>
                                </td>

                        <% } else { %>

                            <tr>

                        <% } %>

                                <td>
                                    <label>
                                        <liferay-ui:message
                                            key="comprobante"
                                        />:
                                    </label>
                                </td>

                                <td>
                                    <select
                                        name="<portlet:namespace />comprobante_tipo_edicion"
                                        id="<portlet:namespace />comprobante_tipo_edicion"
                                    >
                                        <option
                                            value="FCP"
                                            <%="FCP".equals(
                                                    comprobanteTipoEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            FCP
                                        </option>

                                        <option
                                            value="RCB"
                                            <%="RCB".equals(
                                                    comprobanteTipoEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            RCB
                                        </option>

                                        <option
                                            value="OTR"
                                            <%="OTR".equals(
                                                    comprobanteTipoEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            OTR
                                        </option>

                                        <option
                                            value="AUT"
                                            <%="AUT".equals(
                                                    comprobanteTipoEdicion
                                            )
                                                    ? "selected"
                                                    : ""%>
                                        >
                                            AUT
                                        </option>
                                    </select>
                                </td>

                                <td>
                                    <label>
                                        <liferay-ui:message
                                            key="letra"
                                        />:
                                    </label>
                                </td>

                                <td>
                                    <select
                                        name="<portlet:namespace />comprobante_letra_edicion"
                                        id="<portlet:namespace />comprobante_letra_edicion"
                                    >
                                    </select>
                                </td>

                                <td>Suc:</td>

                                <td>
                                    <input
                                        id="<portlet:namespace />comprobante_suc_edicion"
                                        name="<portlet:namespace />comprobante_suc_edicion"
                                        size="8"
                                        maxlength="5"
                                        type="text"
                                        onblur="this.value = completarConCeros(this.value, 5);"
                                        value="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteSucursal() != null
                                                        ? prestacionEnEdicion
                                                                .getComprobanteSucursal()
                                                        : ""%>"
                                    />
                                </td>

                                <td>Nro:</td>

                                <td>
                                    <input
                                        id="<portlet:namespace />comprobante_nro_edicion"
                                        name="<portlet:namespace />comprobante_nro_edicion"
                                        size="11"
                                        maxlength="15"
                                        type="text"
                                        onblur="this.value = completarConCeros(this.value, 8);"
                                        value="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteNro() != null
                                                        ? prestacionEnEdicion
                                                                .getComprobanteNro()
                                                        : ""%>"
                                    />
                                </td>

                                <td>
                                    <label>F. Emisión:</label>
                                </td>

                                <td>
                                    <liferay-ui:input-date
                                        dayParam="fechaComprobanteDiaEdicion"
                                        dayValue="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteFecha() != null
                                                        ? fechaseccional.get(
                                                                Calendar.DAY_OF_MONTH
                                                        )
                                                        : 0%>"
                                        dayNullable="<%=true%>"
                                        monthParam="fechaComprobanteMesEdicion"
                                        monthValue="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteFecha() != null
                                                        ? fechaseccional.get(
                                                                Calendar.MONTH
                                                        )
                                                        : -1%>"
                                        monthNullable="<%=true%>"
                                        yearParam="fechaComprobanteAnioEdicion"
                                        yearValue="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteFecha() != null
                                                        ? fechaseccional.get(
                                                                Calendar.YEAR
                                                        )
                                                        : -1%>"
                                        yearRangeStart="<%=fechaseccional.get(
                                                Calendar.YEAR
                                        ) - 5%>"
                                        yearRangeEnd="<%=fechaseccional.get(
                                                Calendar.YEAR
                                        ) + 5%>"
                                        yearNullable="<%=true%>"
                                        firstDayOfWeek="<%=fechaseccional
                                                .getFirstDayOfWeek() - 1%>"
                                    />
                                </td>
                            </tr>

                            <tr>
                                <td>&nbsp;</td>
                            </tr>

                            <tr>
                                <td colspan="15">
                                    <liferay-util:include
                                        page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp"
                                    >
                                        <liferay-util:param
                                            name="esEditable"
                                            value="true"
                                        />

                                        <liferay-util:param
                                            name="cuit"
                                            value="<%=comprobanteCuitEdicion%>"
                                        />

                                        <liferay-util:param
                                            name="sucu"
                                            value="<%=comprobanteCuitSucursalEdicion%>"
                                        />

                                        <liferay-util:param
                                            name="razon"
                                            value="<%=comprobanteRazonSocialEdicion%>"
                                        />

                                        <liferay-util:param
                                            name="id_seccional"
                                            value=""
                                        />

                                        <liferay-util:param
                                            name="esEmpresaPrestador"
                                            value="true"
                                        />

                                        <liferay-util:param
                                            name="suf_entidad"
                                            value="_edicion"
                                        />

                                        <liferay-util:param
                                            name="suf"
                                            value="_edicion"
                                        />
                                    </liferay-util:include>
                                </td>
                            </tr>

                            <tr>
                                <td>&nbsp;</td>
                            </tr>

                            <tr>
                                <td>
                                    <label>
                                        <liferay-ui:message
                                            key="Cantidad"
                                        />:
                                    </label>
                                </td>

                                <td>
                                    <input
                                        id="<portlet:namespace />cantidadFC_edicion"
                                        name="<portlet:namespace />cantidadFC_edicion"
                                        size="8"
                                        maxlength="20"
                                        type="text"
                                        value="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteCantidad() != null
                                                        ? prestacionEnEdicion
                                                                .getComprobanteCantidad()
                                                        : ""%>"
                                        onblur="calculatotalFCEdicion();"
                                    />
                                </td>

                                <td>
                                    <label>
                                        <liferay-ui:message
                                            key="Importe"
                                        />:
                                    </label>
                                </td>

                                <td>
                                    <input
                                        id="<portlet:namespace />importeUnitarioFC_edicion"
                                        name="<portlet:namespace />importeUnitarioFC_edicion"
                                        size="12"
                                        maxlength="20"
                                        type="text"
                                        value="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteImporte() != null
                                                        ? new BigDecimal(
                                                                prestacionEnEdicion
                                                                        .getComprobanteImporte()
                                                                        .toString()
                                                        ).setScale(
                                                                2,
                                                                RoundingMode.HALF_UP
                                                        ).toPlainString()
                                                        : ""%>"
                                        onkeydown="allowOnlyDigitsAndDecimals(event);"
                                        onblur="calculatotalFCEdicion();"
                                    />
                                </td>

                                <td>
                                    <label>
                                        Total Comprobante:
                                    </label>
                                </td>

                                <td>
                                    <input
                                        id="<portlet:namespace />importeFC_edicion"
                                        name="<portlet:namespace />importeFC_edicion"
                                        size="12"
                                        maxlength="20"
                                        type="text"
                                        readonly="readonly"
                                        value="<%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getComprobanteTotal() != null
                                                        ? new BigDecimal(
                                                                prestacionEnEdicion
                                                                        .getComprobanteTotal()
                                                                        .toString()
                                                        ).setScale(
                                                                2,
                                                                RoundingMode.HALF_UP
                                                        ).toPlainString()
                                                        : ""%>"
                                    />
                                </td>
                            </tr>
                    </table>
                </fieldset>
            </div>
        </td>
    </tr>

    <tr>
        <td colspan="8">
            <div id="<portlet:namespace />Autorizado">
                <fieldset class="block-labels">
                    <legend>
                        <liferay-ui:message
                            key="Autorizado por Área Médica:"
                        />
                    </legend>

                    <table>
                        <tr>
                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Cantidad"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />cantidadEdicion"
                                    name="<portlet:namespace />cantidadEdicion"
                                    size="2"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? prestacionEnEdicion.getCantidad()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                    onblur="calculatotal();"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Importe"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />importeEdicion"
                                    name="<portlet:namespace />importeEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? new BigDecimal(
                                                    prestacionEnEdicion
                                                            .getImporte()
                                            ).setScale(
                                                    2,
                                                    RoundingMode.HALF_UP
                                            ).toPlainString()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                    onblur="calculatotal();"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Total"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />totalEdicion"
                                    name="<portlet:namespace />totalEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    readonly="readonly"
                                    value="<%=prestacionEnEdicion != null
                                            ? prestacionEnEdicion
                                                    .getTotalString()
                                            : ""%>"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Cargo OSPIM"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />cargoospimEdicion"
                                    name="<portlet:namespace />cargoospimEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? new BigDecimal(
                                                    prestacionEnEdicion
                                                            .getCargo_ospim()
                                            ).setScale(
                                                    2,
                                                    RoundingMode.HALF_UP
                                            ).toPlainString()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                    onkeydown="allowOnlyDigitsAndDecimals(event);"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Cargo Prestadora"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />cargopsEdicion"
                                    name="<portlet:namespace />cargopsEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? new BigDecimal(
                                                    prestacionEnEdicion
                                                            .getCargo_ps()
                                            ).setScale(
                                                    2,
                                                    RoundingMode.HALF_UP
                                            ).toPlainString()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Cargo Monotributo"
                                    />:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />cargoimesaEdicion"
                                    name="<portlet:namespace />cargoimesaEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? new BigDecimal(
                                                    prestacionEnEdicion
                                                            .getCargo_imesa()
                                            ).setScale(
                                                    2,
                                                    RoundingMode.HALF_UP
                                            ).toPlainString()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                    onkeydown="allowOnlyDigitsAndDecimals(event);"
                                />
                            </td>

                            <td>
                                <label>
                                    Reconocido SSS:
                                </label>
                            </td>

                            <td>
                                <input
                                    id="<portlet:namespace />reconocidoSSSEdicion"
                                    name="<portlet:namespace />reconocidoSSSEdicion"
                                    size="12"
                                    maxlength="20"
                                    type="text"
                                    value="<%=prestacionEnEdicion != null
                                            ? new BigDecimal(
                                                    prestacionEnEdicion
                                                            .getReconocidoSSS()
                                            ).setScale(
                                                    2,
                                                    RoundingMode.HALF_UP
                                            ).toPlainString()
                                            : ""%>"
                                    onkeypress="return validaMonto(event, this);"
                                />
                            </td>

                            <td>
                                <label>
                                    <liferay-ui:message
                                        key="Recuperable SSS"
                                    />:
                                </label>
                            </td>

                            <td>
                                <select
                                    name="<portlet:namespace />recuperable_surEdicion"
                                    id="<portlet:namespace />recuperable_surEdicion"
                                    onchange="cambiorecuperableEdicion();"
                                >
                                    <option value="0">
                                        Seleccione Integración
                                    </option>

                                    <option
                                        value="1"
                                        <%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getRecuperable() != null
                                                && prestacionEnEdicion
                                                        .getRecuperable()
                                                        .intValue() == 1
                                                        ? "selected"
                                                        : ""%>
                                    >
                                        SURGE
                                    </option>

                                    <option
                                        value="3"
                                        <%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getRecuperable() != null
                                                && prestacionEnEdicion
                                                        .getRecuperable()
                                                        .intValue() == 3
                                                        ? "selected"
                                                        : ""%>
                                    >
                                        Integración
                                    </option>

                                    <option
                                        value="2"
                                        <%=prestacionEnEdicion != null
                                                && prestacionEnEdicion
                                                        .getRecuperable() != null
                                                && prestacionEnEdicion
                                                        .getRecuperable()
                                                        .intValue() == 2
                                                        ? "selected"
                                                        : ""%>
                                    >
                                        NO Recuperable
                                    </option>
                                </select>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </div>
        </td>
    </tr>

    <tr>
        <td>
            <c:choose>
                <c:when test="<%=tipoedicion.intValue() == 1%>">
                    <liferay-ui:message
                        key="Observacion Edicion"
                    />:
                </c:when>

                <c:when test="<%=tipoedicion.intValue() == 2%>">
                    <liferay-ui:message
                        key="Observacion Autorizacion"
                    />:
                </c:when>

                <c:when test="<%=tipoedicion.intValue() == 3%>">
                    <liferay-ui:message
                        key="Observacion Rechazo"
                    />:
                </c:when>

                <c:otherwise>
                    <liferay-ui:message
                        key="Observacion"
                    />:
                </c:otherwise>
            </c:choose>
        </td>

        <td>
            <textarea
                rows="3"
                cols="70"
                id="<portlet:namespace />observacion_prestacionEdicion"
                maxlength="250"
                name="<portlet:namespace />observacion_prestacionEdicion"
            ><%=prestacionEnEdicion != null
                    && prestacionEnEdicion
                            .getObservaciones() != null
                            ? prestacionEnEdicion
                                    .getObservaciones()
                            : ""%></textarea>
        </td>

        <td></td>
        <td></td>

        <td>
            <% if (tipoedicion.intValue() == 0) { %>

                <input
                    type="button"
                    name="<portlet:namespace />btnedita_prestacion"
                    id="<portlet:namespace />btnedita_prestacion"
                    value="<liferay-ui:message key="Editar Prestación" />"
                    onclick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"
                    title="<liferay-ui:message key="Edita la prestacion" />"
                />

            <% } %>

            <% if (tipoedicion.intValue() == 1) { %>

                <input
                    type="button"
                    name="<portlet:namespace />btnautoriza_prestacion"
                    id="<portlet:namespace />btnautoriza_prestacion"
                    value="<liferay-ui:message key="Autoriza Prestación" />"
                    onclick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"
                    title="<liferay-ui:message key="Autoriza la prestacion" />"
                />

            <% } %>

            <% if (tipoedicion.intValue() == 2) { %>

                <input
                    type="button"
                    name="<portlet:namespace />btnrechaza_prestacion"
                    id="<portlet:namespace />btnrechaza_prestacion"
                    value="<liferay-ui:message key="Rechaza Prestación" />"
                    onclick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"
                    title="<liferay-ui:message key="Rechaza la Prestacion" />"
                />

            <% } %>
        </td>

        <td></td>
        <td></td>

        <td>
            <input
                type="button"
                value="<liferay-ui:message key="<%=captionbotoncancelar%>" />"
                onclick="<portlet:namespace />cancelaEdicionPrestacion();"
            />
        </td>
    </tr>
</table>

<script type="text/javascript">
(function() {
    <% if (prestacionEnEdicion != null) { %>

    jQuery(
        "#<portlet:namespace />datos_edicion_prestacion"
    ).show();

    jQuery(
        "#<portlet:namespace />codigoprestacion"
    ).val(
        "<%=UnicodeFormatter.toString(
                prestacionEnEdicion
                        .getCodigoPrestacion() != null
                        ? prestacionEnEdicion
                                .getCodigoPrestacion()
                        : ""
        )%>"
    );

    jQuery(
        "#<portlet:namespace />idRegistro"
    ).val(
        "<%=prestacionEnEdicion.getIdRegistro()%>"
    );

        <% if (hayPrestacion) { %>

    jQuery(
        "#<portlet:namespace />codigoSeguimiento_filtro_edit"
    ).val(
        "<%=UnicodeFormatter.toString(
                prestacionEnEdicion
                        .getCodigoPrestacion() != null
                        ? prestacionEnEdicion
                                .getCodigoPrestacion()
                        : ""
        )%>"
    );

    jQuery(
        "#<portlet:namespace />descripcionSeguimiento_filtro_edit"
    ).val(
        "<%=UnicodeFormatter.toString(
                prestacionEnEdicion
                        .getDescripcion() != null
                        ? prestacionEnEdicion
                                .getDescripcion()
                        : ""
        )%>"
    );

    <portlet:namespace />buscarNomencladorAutocompletar_edit();

        <% } else { %>

    jQuery(
        "#<portlet:namespace />troquel_edit"
    ).val(
        "<%=idMedic != null
                ? idMedic
                : Integer.valueOf(0)%>"
    );

        <% } %>

        <% if (ocultarSeccional != null) { %>

    jQuery(
        "#<portlet:namespace />Autorizado"
    ).hide();

        <% } %>

    <% } %>

    filtrarLetraComprobanteEdicion();
    cambiorecuperableEdicion();
})();

function calculatotal() {
    var importe =
            jQuery(
                "#<portlet:namespace />importeEdicion"
            ).val();

    var importeNormalizado =
            String(
                importe || ""
            ).replace(
                ",",
                "."
            );

    var cantidad =
            jQuery(
                "#<portlet:namespace />cantidadEdicion"
            ).val();

    var total =
            parseFloat(
                importeNormalizado
            ) *
            parseFloat(
                cantidad
            );

    if (isNaN(total)) {
        total = 0;
    }

    jQuery(
        "#<portlet:namespace />totalEdicion"
    ).val(
        total.toFixed(2)
    );
}

function calculatotalFCEdicion() {
    var importe =
            jQuery(
                "#<portlet:namespace />importeUnitarioFC_edicion"
            ).val();

    var importeNormalizado =
            String(
                importe || ""
            ).replace(
                ",",
                "."
            );

    var cantidad =
            jQuery(
                "#<portlet:namespace />cantidadFC_edicion"
            ).val();

    var total =
            parseFloat(
                importeNormalizado
            ) *
            parseFloat(
                cantidad
            );

    if (isNaN(total)) {
        total = 0;
    }

    jQuery(
        "#<portlet:namespace />importeFC_edicion"
    ).val(
        Math.round(
            total * 100
        ) / 100
    );
}

function filtrarLetraComprobanteEdicion() {
    var tipoPedido =
            jQuery(
                "#<portlet:namespace />tipopedido"
            ).val();

    var url =
            '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>'
            + '&struts_action=/autorizaciones/filtrarLetraComprobante'
            + '&tipo_pedido='
            + tipoPedido;

    var selector =
            "#<portlet:namespace />comprobante_letra_edicion";

    jQuery(
        selector
    ).attr(
        "disabled",
        "disabled"
    );

    jQuery.ajax({
        url: url,
        async: false,

        success: function(data) {
            var control =
                    document.getElementById(
                        "<portlet:namespace />comprobante_letra_edicion"
                    );

            if (control) {
                control.length = 0;
            }

            jQuery(
                selector
            ).removeAttr(
                "disabled"
            );

            jQuery(
                selector
            ).html(
                data
            ).fadeIn();

            jQuery(
                selector
            ).val(
                "<%=UnicodeFormatter.toString(
                        comprobanteLetraEdicion
                )%>"
            );
        }
    });
}

function cambiorecuperableEdicion() {
    try {
        var recuperable =
                jQuery(
                    "#<portlet:namespace />recuperable_surEdicion"
                ).val();

        if (
            recuperable == "3"
            || recuperable == "1"
        ) {
            jQuery(
                "#<portlet:namespace />reconocidoSSSEdicion"
            ).attr(
                "readonly",
                false
            );
        } else {
            jQuery(
                "#<portlet:namespace />reconocidoSSSEdicion"
            ).val(
                0
            );

            jQuery(
                "#<portlet:namespace />reconocidoSSSEdicion"
            ).attr(
                "readonly",
                true
            );
        }
    } catch (err) {
        /*
         * Se conserva el comportamiento silencioso legacy.
         */
    }
}

function completarConCeros(value, longitud) {
    var ceros = "";
    var i;

    if (!value) {
        return "";
    }

    value =
            String(
                value
            ).replace(
                /\D/g,
                ""
            );

    for (i = 0; i < longitud; i++) {
        ceros += "0";
    }

    return (
        ceros + value
    ).slice(
        -longitud
    );
}
</script>