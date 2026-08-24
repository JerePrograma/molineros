<%--
Responsabilidad:
    Normaliza el modelo de pantalla y calcula capacidades por estado.
Incluido desde:
    requerimiento_compra_alta.jsp, requerimiento_compra_consulta.jsp, requerimiento_compra_edicion.jsp
Pantallas o estados de uso:
    Consulta y estados de solo lectura.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%
String namespaceCompra = renderResponse.getNamespace();

String modoRequerimientoCompra =
        (String) request.getAttribute("MODO_REQUERIMIENTO_COMPRA");

if (WebKeysCompras.isEmpty(modoRequerimientoCompra)) {
    modoRequerimientoCompra =
            ParamUtil.getString(
                    renderRequest,
                    "modo",
                    "EDICION"
            ).toUpperCase();
}

boolean modoAltaForzado =
        "ALTA".equalsIgnoreCase(modoRequerimientoCompra);
boolean modoEdicionForzado =
        "EDICION".equalsIgnoreCase(modoRequerimientoCompra);
boolean modoVistaForzado =
        "VISTA".equalsIgnoreCase(modoRequerimientoCompra)
        || "VER".equalsIgnoreCase(modoRequerimientoCompra);

RequerimientoCompra req = null;

if (!modoAltaForzado) {
    req =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
            );

    if (req == null) {
        req =
                (RequerimientoCompra) renderRequest.getAttribute(
                        WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
                );
    }
}

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo =
        req.getIdRequerimientoCompra() == 0;

String surgeSeleccionadoCompra =
        esNuevo
                ? ""
                : (req.isSurge() ? "1" : "0");

boolean puedeABM =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        );

boolean puedeCotizar =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );

Object soloLecturaAttr =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String strutsActionActual =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

String modoParam =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

boolean soloLecturaSolicitada =
        modoVistaForzado
        || Boolean.TRUE.equals(soloLecturaAttr)
        || "/compras/ver_requerimiento".equals(strutsActionActual)
        || "ver".equalsIgnoreCase(modoParam);

boolean editablePorEstado =
        esNuevo || req.puedeEditarEstructura();

boolean surgeEditablePorEstado =
        esNuevo || req.puedeEditarSurge();

boolean cotizacionEditablePorEstado =
        req.puedeEditarCotizacion();

boolean puedeEditarEstructuraPantalla =
        puedeABM
        && editablePorEstado
        && !soloLecturaSolicitada;

boolean puedeEditarSurgePantalla =
        puedeABM
        && surgeEditablePorEstado
        && !soloLecturaSolicitada;

boolean puedeEditarCotizacionPantalla =
        !esNuevo
        && puedeCotizar
        && cotizacionEditablePorEstado
        && !soloLecturaSolicitada;

boolean eliminarDetalleEditablePorEstado =
        !esNuevo
        && req.puedeEliminarDetalle();

boolean puedeEliminarDetallePantalla =
        puedeABM
        && eliminarDetalleEditablePorEstado
        && !soloLecturaSolicitada;

boolean layoutEdicion =
        !modoVistaForzado
        && (
                puedeEditarEstructuraPantalla
                || puedeEditarSurgePantalla
                || puedeEditarCotizacionPantalla
        );

boolean modoEditable =
        layoutEdicion
        && !soloLecturaSolicitada;

/*
 * Un requerimiento puede tener una interacción limitada sin que
 * su estructura sea editable.
 *
 * Caso concreto:
 *
 * ENVIADO A COTIZAR + ABM_Compras
 *
 * - no puede agregar prestaciones;
 * - no puede editar prestaciones;
 * - sí puede eliminar una prestación;
 * - debe conservar al menos una.
 */
boolean modoInteractivo =
        modoEditable
        || puedeEliminarDetallePantalla;

boolean modoVista =
        modoVistaForzado
        || !modoInteractivo;

renderRequest.setAttribute(
        WebKeysCompras.SOLO_LECTURA_ATTR,
        Boolean.valueOf(modoVista)
);

/*
 * Los Actions de alta/edicion/vista publican el catalogo de sectores.
 * No se consulta persistencia desde la vista. Si un caller legacy no lo
 * publica, se muestra la pantalla sin opciones antes que abrir una consulta
 * lateral desde el JSP.
 */
List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO
        );

if (sectores == null) {
    sectores = new ArrayList<RequerimientoCompraSector>();
}

PortletURL volverURL =
        renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL verURL =
        renderResponse.createRenderURL();
verURL.setWindowState(WindowState.MAXIMIZED);
verURL.setParameter("struts_action", "/compras/ver_requerimiento");
verURL.setParameter(
        "id_requerimiento_compra",
        req.getIdRequerimientoCompraString()
);

PortletURL editarURL =
        renderResponse.createRenderURL();
editarURL.setWindowState(WindowState.MAXIMIZED);
editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
editarURL.setParameter(
        "id_requerimiento_compra",
        req.getIdRequerimientoCompraString()
);

PortletURL imprimirURL =
        renderResponse.createRenderURL();
imprimirURL.setWindowState(WindowState.MAXIMIZED);
imprimirURL.setParameter("struts_action", "/compras/imprimir_requerimiento");
imprimirURL.setParameter(
        "id_requerimiento_compra",
        req.getIdRequerimientoCompraString()
);

PortletURL actionURL =
        renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String reqSectorId =
        req.getSectorId() != null
                ? String.valueOf(req.getSectorId().intValue())
                : "";

String sectorDescripcionSoloLectura =
        req.getSectorDescripcionVisible();

boolean sectorRequiereAfiliadoActual =
        req.isRequiereAfiliado();

if (WebKeysCompras.isEmpty(sectorDescripcionSoloLectura)
        || !sectorRequiereAfiliadoActual) {

    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector =
                sectores.get(i);

        if (sector == null) {
            continue;
        }

        String sectorId =
                String.valueOf(sector.getIdSector());

        if (reqSectorId.equals(sectorId)) {
            if (WebKeysCompras.isEmpty(sectorDescripcionSoloLectura)) {
                sectorDescripcionSoloLectura =
                        sector.getDescripcionVisible();
            }

            sectorRequiereAfiliadoActual =
                    sector.isRequiereAfiliado();
            break;
        }
    }
}

String sectorDescripcionActualString =
        sectorDescripcionSoloLectura;

String afiliadoCuilTitular =
        req.getAfiliadoCuilTitularVisible();
String afiliadoInt =
        req.getAfiliadoIntString();
String idTercerizadora =
        req.getIdTercerizadora();

boolean sectorSeleccionadoValido =
        !WebKeysCompras.isEmpty(reqSectorId)
        && !"0".equals(reqSectorId);

boolean sectorSinAfiliadoForzaCargoOspim =
        sectorSeleccionadoValido
        && !sectorRequiereAfiliadoActual;

int cargoOspimActual =
        req.getCargoOspim() != null
                ? req.getCargoOspim().intValue()
                : 0;

int cargoTercerizadoraActual =
        req.getCargoTercerizadora() != null
                ? req.getCargoTercerizadora().intValue()
                : 0;

String cargoOspimVisible =
        sectorSinAfiliadoForzaCargoOspim
                ? "100"
                : req.getCargoOspimString();

String cargoTercerizadoraVisible =
        sectorSinAfiliadoForzaCargoOspim
                ? "0"
                : req.getCargoTercerizadoraString();

boolean recuperoPorCargoTercerizadoraActual =
        !sectorSinAfiliadoForzaCargoOspim
        && cargoTercerizadoraActual > 0;

Afiliado afiliadoRequerimiento =
        (Afiliado) renderRequest.getAttribute(
                WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA
        );

if (idTercerizadora == null) {
    idTercerizadora = "";
}

String afiliadoCuilVisible = afiliadoCuilTitular;
String afiliadoIntVisible = afiliadoInt;
String afiliadoTipoDocumento = "";
String afiliadoNumeroDocumento = "";
String afiliadoApellido = "";
String afiliadoNombre = "";
String afiliadoSeccional = "";
String afiliadoBajaFecha = "";
String afiliadoFechaAlta = "";
String afiliadoIdTercerizadora = idTercerizadora;
String afiliadoIncapacidad = "";
String afiliadoAntecedentes = "";
String afiliadoIdSeccional = "";
String afiliadoNumeroOspim = "";
String afiliadoNumeroUoma = "";
String afiliadoNumeroAmtima = "";
String afiliadoNumeroAfiliado = "";
String afiliadoNombrePlan = "";
String afiliadoIdPlan = "";
String afiliadoAfiTercerizadora = "";

/*
 * Acceso tipado al bean de Afiliados.
 *
 * Se elimina deliberadamente la reflexion que intentaba adivinar getters.
 * El componente de busqueda de afiliados de Compras ya utiliza este mismo
 * contrato tipado (id_ospim/id_uoma/id_amtima, Seccional y ultimo_plan).
 */
if (afiliadoRequerimiento != null) {
    afiliadoCuilVisible =
            afiliadoRequerimiento.getCuil_titular() != null
                    ? afiliadoRequerimiento.getCuil_titular()
                    : afiliadoCuilTitular;

    afiliadoIntVisible =
            afiliadoRequerimiento.getInteAsString();

    afiliadoTipoDocumento =
            afiliadoRequerimiento.getDocumento_tipo() != null
                    ? afiliadoRequerimiento.getDocumento_tipo()
                    : "";

    afiliadoNumeroDocumento =
            afiliadoRequerimiento.getDocu_numero() != null
                    ? afiliadoRequerimiento.getDocu_numero()
                    : "";

    afiliadoApellido =
            afiliadoRequerimiento.getApellido() != null
                    ? afiliadoRequerimiento.getApellido()
                    : "";

    afiliadoNombre =
            afiliadoRequerimiento.getNombre() != null
                    ? afiliadoRequerimiento.getNombre()
                    : "";

    if (afiliadoRequerimiento.getSeccional() != null) {
        afiliadoSeccional =
                afiliadoRequerimiento.getSeccional().getDescripcion() != null
                        ? afiliadoRequerimiento.getSeccional().getDescripcion()
                        : "";

        if (afiliadoRequerimiento.getSeccional().getId() > 0) {
            afiliadoIdSeccional =
                    String.valueOf(
                            afiliadoRequerimiento.getSeccional().getId()
                    );
        }
    }

    afiliadoBajaFecha =
            afiliadoRequerimiento.getBaja_fechaAsString();

    afiliadoFechaAlta =
            afiliadoRequerimiento.getAlta_fechaAsString();

    afiliadoIncapacidad =
            afiliadoRequerimiento.getDiscapacitado() != null
                    ? afiliadoRequerimiento.getDiscapacitado()
                    : "";

    afiliadoAntecedentes =
            afiliadoRequerimiento.getTieneAntecedentesJudiciales() == 1
                    ? "Sí"
                    : "No";

    if (afiliadoRequerimiento.getId_ospim() > 0) {
        afiliadoNumeroOspim =
                String.valueOf(afiliadoRequerimiento.getId_ospim());
    }

    if (afiliadoRequerimiento.getId_uoma() > 0) {
        afiliadoNumeroUoma =
                String.valueOf(afiliadoRequerimiento.getId_uoma());
    }

    if (afiliadoRequerimiento.getId_amtima() > 0) {
        afiliadoNumeroAmtima =
                String.valueOf(afiliadoRequerimiento.getId_amtima());
    }

    afiliadoNumeroAfiliado =
            afiliadoNumeroOspim;

    afiliadoNombrePlan =
            afiliadoRequerimiento.getNombrePlan() != null
                    ? afiliadoRequerimiento.getNombrePlan()
                    : "";

    if (afiliadoRequerimiento.getUltimo_plan() != null
            && afiliadoRequerimiento.getUltimo_plan().getId() > 0) {

        afiliadoIdPlan =
                String.valueOf(
                        afiliadoRequerimiento.getUltimo_plan().getId()
                );
    }


    afiliadoAfiTercerizadora =
            afiliadoRequerimiento.getDesc_tercerizadora() != null
                    ? afiliadoRequerimiento.getDesc_tercerizadora()
                    : "";
}

boolean tieneAfiliadoVisible =
        !WebKeysCompras.isEmpty(afiliadoCuilVisible)
        || !WebKeysCompras.isEmpty(afiliadoIntVisible)
        || !WebKeysCompras.isEmpty(afiliadoTipoDocumento)
        || !WebKeysCompras.isEmpty(afiliadoNumeroDocumento)
        || !WebKeysCompras.isEmpty(afiliadoApellido)
        || !WebKeysCompras.isEmpty(afiliadoNombre)
        || !WebKeysCompras.isEmpty(afiliadoIdSeccional)
        || !WebKeysCompras.isEmpty(afiliadoSeccional)
        || !WebKeysCompras.isEmpty(afiliadoNumeroAfiliado)
        || !WebKeysCompras.isEmpty(afiliadoNumeroOspim)
        || !WebKeysCompras.isEmpty(afiliadoNumeroUoma)
        || !WebKeysCompras.isEmpty(afiliadoNumeroAmtima)
        || !WebKeysCompras.isEmpty(afiliadoNombrePlan)
        || !WebKeysCompras.isEmpty(afiliadoIdPlan)
        || !WebKeysCompras.isEmpty(afiliadoAfiTercerizadora)
        || !WebKeysCompras.isEmpty(afiliadoBajaFecha)
        || !WebKeysCompras.isEmpty(afiliadoFechaAlta)
        || !WebKeysCompras.isEmpty(afiliadoIdTercerizadora)
        || !WebKeysCompras.isEmpty(afiliadoIncapacidad)
        || !WebKeysCompras.isEmpty(afiliadoAntecedentes);

boolean mostrarPanelAfiliadoEnVista =
        sectorRequiereAfiliadoActual
        || tieneAfiliadoVisible;

String recuperoChecked =
        recuperoPorCargoTercerizadoraActual
                ? "checked=\"checked\""
                : "";

String camposSoloLectura =
        !modoEditable
                ? "readonly=\"readonly\""
                : "";

String bloqueoSinEstiloVista =
        !modoEditable
                ? " class=\"compras-bloqueado-sin-estilo\" tabindex=\"-1\" onmousedown=\"return false;\" onkeydown=\"return false;\" onclick=\"return false;\""
                : "";

String errorParaAlert =
        (String) renderRequest.getAttribute(
                WebKeysCompras.ERROR_PARA_ALERT
        );

if (errorParaAlert == null) {
    errorParaAlert = "";
}

String errorCampoCompra =
        (String) renderRequest.getAttribute(
                WebKeysCompras.ERROR_CAMPO_COMPRA
        );

if (errorCampoCompra == null) {
    errorCampoCompra = "";
}

boolean msgRequerimientoGuardado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-guardado"
        );

boolean msgDetalleGuardado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-item-guardado"
        );

boolean msgDetalleBorrado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-item-borrado"
        );

boolean msgRequerimientoAnulado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-anulado"
        );

boolean errorRequerimientoCompra =
        com.liferay.portal.kernel.servlet.SessionErrors.contains(
                renderRequest,
                "requerimiento-compra-error"
        );

String comprasGuardado =
        ParamUtil.getString(
                renderRequest,
                "compras_guardado",
                ""
        );

String comprasOperacion =
        ParamUtil.getString(
                renderRequest,
                "compras_operacion",
                ""
        );

boolean comprasGuardadoPorParametro =
        "true".equalsIgnoreCase(comprasGuardado);

boolean mostrarMensajeRequerimientoGuardado =
        msgRequerimientoGuardado
        || comprasGuardadoPorParametro;

boolean mostrarErrorGenericoCompra =
        errorRequerimientoCompra
        && WebKeysCompras.isEmpty(errorParaAlert);

String idRequerimientoMensaje =
        req != null
        && req.getIdRequerimientoCompra() > 0
                ? req.getIdRequerimientoCompraString()
                : ParamUtil.getString(
                        renderRequest,
                        "id_requerimiento_compra",
                        ""
                );

String tituloPantalla;

if (modoVistaForzado) {
    tituloPantalla = "Ver requerimiento de compra";
} else if (esNuevo) {
    tituloPantalla = "Nuevo requerimiento de compra";
} else if (modoEditable || puedeEliminarDetallePantalla) {
    tituloPantalla = "Editar requerimiento de compra";
} else {
    tituloPantalla = "Ver requerimiento de compra";
}
%>