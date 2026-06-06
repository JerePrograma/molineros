<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

<portlet:defineObjects/>

<%!
private String jsCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}

private String valorMetodoCompra(Object bean, String metodoNombre) {
    if (bean == null || metodoNombre == null) {
        return "";
    }

    try {
        Method metodo = bean.getClass().getMethod(metodoNombre, new Class[0]);
        Object value = metodo.invoke(bean, new Object[0]);

        if (value == null) {
            return "";
        }

        String stringValue = String.valueOf(value);

        if ("null".equalsIgnoreCase(stringValue)) {
            return "";
        }

        return stringValue.trim();
    } catch (Exception e) {
        return "";
    }
}

private String primerValorMetodoCompra(Object bean, String[] metodos) {
    if (bean == null || metodos == null) {
        return "";
    }

    for (int i = 0; i < metodos.length; i++) {
        String value = valorMetodoCompra(bean, metodos[i]);

        if (value != null && value.trim().length() > 0) {
            return value.trim();
        }
    }

    return "";
}

private String primerValorGetterPorCoincidenciaCompra(Object bean, String[] debeContener, String[] noDebeContener) {
    if (bean == null) {
        return "";
    }

    try {
        Method[] metodos = bean.getClass().getMethods();

        for (int i = 0; i < metodos.length; i++) {
            Method metodo = metodos[i];

            if (metodo.getParameterTypes().length != 0) {
                continue;
            }

            String nombre = metodo.getName();

            if (!nombre.startsWith("get") && !nombre.startsWith("is")) {
                continue;
            }

            String nombreLower = nombre.toLowerCase();

            boolean coincide = true;

            if (debeContener != null) {
                for (int j = 0; j < debeContener.length; j++) {
                    if (debeContener[j] != null
                            && nombreLower.indexOf(debeContener[j].toLowerCase()) < 0) {
                        coincide = false;
                        break;
                    }
                }
            }

            if (!coincide) {
                continue;
            }

            if (noDebeContener != null) {
                for (int j = 0; j < noDebeContener.length; j++) {
                    if (noDebeContener[j] != null
                            && nombreLower.indexOf(noDebeContener[j].toLowerCase()) >= 0) {
                        coincide = false;
                        break;
                    }
                }
            }

            if (!coincide) {
                continue;
            }

            Object value = metodo.invoke(bean, new Object[0]);

            if (value != null) {
                String stringValue = String.valueOf(value).trim();

                if (stringValue.length() > 0 && !"null".equalsIgnoreCase(stringValue)) {
                    return stringValue;
                }
            }
        }
    } catch (Exception e) {
        return "";
    }

    return "";
}
%>

<%
String namespaceCompra = renderResponse.getNamespace();

RequerimientoCompra req =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (req == null) {
    req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

Object soloLecturaAttr = renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String strutsActionActual = ParamUtil.getString(renderRequest, "struts_action", "");
String modo = ParamUtil.getString(renderRequest, "modo", "");

boolean soloLecturaSolicitada =
        Boolean.TRUE.equals(soloLecturaAttr)
        || "/compras/ver_requerimiento".equals(strutsActionActual)
        || "ver".equalsIgnoreCase(modo);

boolean editablePorEstado = esNuevo || req.isEditable();

boolean layoutEdicion =
        puedeABM
        && editablePorEstado;

boolean modoEditable =
        layoutEdicion
        && !soloLecturaSolicitada;

boolean modoVista = !modoEditable;

renderRequest.setAttribute(
        WebKeysCompras.SOLO_LECTURA_ATTR,
        Boolean.valueOf(modoVista)
);

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO);

if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

Object articulosAttr = renderRequest.getAttribute("ARTICULOS_COMPRA");

List<CompraArticulo> articulosCompra = null;

if (articulosAttr instanceof List) {
    articulosCompra = (List<CompraArticulo>) articulosAttr;
}

if (articulosCompra == null || articulosCompra.size() == 0) {
    articulosCompra = new ArrayList<CompraArticulo>();

    String[] metodosArticulos = new String[] {
            "listarArticulos",
            "listarArticulosCompra",
            "listarCompraArticulos",
            "listarArticulosRequerimientoCompra",
            "listarRequerimientoCompraArticulos",
            "getArticulosCompra"
    };

    for (int i = 0; i < metodosArticulos.length && articulosCompra.size() == 0; i++) {
        try {
            Method metodo =
                    BusquedaRequerimientoCompraServiceUtil.class.getMethod(
                            metodosArticulos[i],
                            new Class[0]
                    );

            Object resultado = metodo.invoke(null, new Object[0]);

            if (resultado instanceof List) {
                articulosCompra = (List<CompraArticulo>) resultado;
            }
        } catch (NoSuchMethodException nsme) {
            // Intencional: compatibilidad con distintos nombres de service.
        } catch (Exception e) {
            // Intencional: si un nombre existe pero falla, se intenta el siguiente.
        }
    }
}

renderRequest.setAttribute("ARTICULOS_COMPRA", articulosCompra);

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL verURL = renderResponse.createRenderURL();
verURL.setWindowState(WindowState.MAXIMIZED);
verURL.setParameter("struts_action", "/compras/ver_requerimiento");
verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL editarURL = renderResponse.createRenderURL();
editarURL.setWindowState(WindowState.MAXIMIZED);
editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String reqSectorId = req.getSectorId() != null ? String.valueOf(req.getSectorId().intValue()) : "";
String sectorDescripcionSoloLectura = req.getSectorDescripcionVisible();

boolean sectorRequiereAfiliadoActual = req.isRequiereAfiliado();

if (sectorDescripcionSoloLectura.length() == 0 || !sectorRequiereAfiliadoActual) {
    for (int i = 0; i < sectores.size(); i++) {
        RequerimientoCompraSector sector = sectores.get(i);
        String sectorId = String.valueOf(sector.getIdSector());

        if (reqSectorId.equals(sectorId)) {
            if (sectorDescripcionSoloLectura.length() == 0) {
                sectorDescripcionSoloLectura = sector.getDescripcionVisible();
            }

            sectorRequiereAfiliadoActual = sector.isRequiereAfiliado();
            break;
        }
    }
}

String afiliadoCuilTitular = req.getAfiliadoCuilTitularVisible();
String afiliadoInt = req.getAfiliadoIntString();
String idTercerizadora = req.getIdTercerizadora();

boolean sectorSeleccionadoValido =
        !WebKeysCompras.isEmpty(reqSectorId)
        && !"0".equals(reqSectorId);

boolean sectorSinAfiliadoForzaCargoOspim =
        sectorSeleccionadoValido
        && !sectorRequiereAfiliadoActual;

int cargoOspimActual = req.getCargoOspim() != null
        ? req.getCargoOspim().intValue()
        : 0;

int cargoTercerizadoraActual = req.getCargoTercerizadora() != null
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
        && cargoTercerizadoraActual == 100;

Afiliado afiliadoRequerimiento =
        (Afiliado) renderRequest.getAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);

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

if (afiliadoRequerimiento != null) {
    afiliadoCuilVisible = afiliadoRequerimiento.getCuil_titular() != null ? afiliadoRequerimiento.getCuil_titular() : afiliadoCuilTitular;
    afiliadoIntVisible = afiliadoRequerimiento.getInteAsString();
    afiliadoTipoDocumento = afiliadoRequerimiento.getDocumento_tipo() != null ? afiliadoRequerimiento.getDocumento_tipo() : "";
    afiliadoNumeroDocumento = afiliadoRequerimiento.getDocu_numero() != null ? afiliadoRequerimiento.getDocu_numero() : "";
    afiliadoApellido = afiliadoRequerimiento.getApellido() != null ? afiliadoRequerimiento.getApellido() : "";
    afiliadoNombre = afiliadoRequerimiento.getNombre() != null ? afiliadoRequerimiento.getNombre() : "";

    if (afiliadoRequerimiento.getSeccional() != null) {
        afiliadoSeccional = afiliadoRequerimiento.getSeccional().getDescripcion() != null
                ? afiliadoRequerimiento.getSeccional().getDescripcion()
                : "";

        afiliadoIdSeccional = primerValorMetodoCompra(
                afiliadoRequerimiento.getSeccional(),
                new String[] {
                        "getId_seccional",
                        "getIdSeccional",
                        "getId",
                        "getCodigo"
                }
        );
    }

    afiliadoBajaFecha = afiliadoRequerimiento.getBaja_fechaAsString();
    afiliadoFechaAlta = afiliadoRequerimiento.getAlta_fechaAsString();
    afiliadoIdTercerizadora = afiliadoRequerimiento.getId_tercerizadora() != null
            ? afiliadoRequerimiento.getId_tercerizadora()
            : idTercerizadora;
    afiliadoIncapacidad = afiliadoRequerimiento.getDiscapacitado() != null ? afiliadoRequerimiento.getDiscapacitado() : "";
    afiliadoAntecedentes = afiliadoRequerimiento.getTieneAntecedentesJudiciales() == 1 ? "SI" : "NO";

    afiliadoNumeroOspim = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getOspim",
                    "getOSPIM",
                    "getOspimString",
                    "getNroOspim",
                    "getNumeroOspim",
                    "getNumero_ospim",
                    "getNro_ospim",
                    "getNroAfiliadoOspim",
                    "getNro_afiliado_ospim"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoNumeroOspim)) {
        afiliadoNumeroOspim = primerValorGetterPorCoincidenciaCompra(
                afiliadoRequerimiento,
                new String[] { "ospim" },
                new String[] { "entidad", "rol", "permiso" }
        );
    }

    afiliadoNumeroUoma = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getUoma",
                    "getUOMA",
                    "getUomaString",
                    "getNroUoma",
                    "getNumeroUoma",
                    "getNumero_uoma",
                    "getNro_uoma",
                    "getNroAfiliadoUoma",
                    "getNro_afiliado_uoma"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoNumeroUoma)) {
        afiliadoNumeroUoma = primerValorGetterPorCoincidenciaCompra(
                afiliadoRequerimiento,
                new String[] { "uoma" },
                new String[] { "entidad", "rol", "permiso" }
        );
    }

    afiliadoNumeroAmtima = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getAmtima",
                    "getAMTIMA",
                    "getAmtimaString",
                    "getNroAmtima",
                    "getNumeroAmtima",
                    "getNumero_amtima",
                    "getNro_amtima",
                    "getNroAfiliadoAmtima",
                    "getNro_afiliado_amtima"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoNumeroAmtima)) {
        afiliadoNumeroAmtima = primerValorGetterPorCoincidenciaCompra(
                afiliadoRequerimiento,
                new String[] { "amtima" },
                new String[] { "entidad", "rol", "permiso" }
        );
    }

    afiliadoNumeroAfiliado = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getNumero_afi",
                    "getNumeroAfi",
                    "getNumeroAfiliado",
                    "getNroAfiliado",
                    "getNro_afiliado"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoNumeroAfiliado)) {
        afiliadoNumeroAfiliado = afiliadoNumeroOspim;
    }

    if (WebKeysCompras.isEmpty(afiliadoNumeroOspim)) {
        afiliadoNumeroOspim = afiliadoNumeroAfiliado;
    }

    afiliadoNombrePlan = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getNombre_plan",
                    "getNombrePlan",
                    "getPlanDescripcion",
                    "getDescripcionPlan",
                    "getPlan"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoNombrePlan)) {
        afiliadoNombrePlan = primerValorGetterPorCoincidenciaCompra(
                afiliadoRequerimiento,
                new String[] { "plan" },
                new String[] { "id" }
        );
    }

    afiliadoIdPlan = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getId_plan",
                    "getIdPlan"
            }
    );

    afiliadoAfiTercerizadora = primerValorMetodoCompra(
            afiliadoRequerimiento,
            new String[] {
                    "getAfi_tercerizadora",
                    "getAfiTercerizadora",
                    "getTercerizadoraDescripcion",
                    "getDescripcionTercerizadora",
                    "getNombreTercerizadora"
            }
    );

    if (WebKeysCompras.isEmpty(afiliadoAfiTercerizadora)) {
        afiliadoAfiTercerizadora = primerValorGetterPorCoincidenciaCompra(
                afiliadoRequerimiento,
                new String[] { "tercerizadora" },
                new String[] { "id" }
        );
    }
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

boolean mostrarPanelAfiliadoEnVista = sectorRequiereAfiliadoActual || tieneAfiliadoVisible;

String recuperoChecked = recuperoPorCargoTercerizadoraActual ? "checked=\"checked\"" : "";
String camposVistaReadOnly = modoVista ? "readonly=\"readonly\"" : "";

String bloqueoSinEstiloVista = modoVista
        ? " class=\"compras-bloqueado-sin-estilo\" tabindex=\"-1\" onmousedown=\"return false;\" onkeydown=\"return false;\" onclick=\"return false;\""
        : "";

String errorParaAlert =
        (String) renderRequest.getAttribute(WebKeysCompras.ERROR_PARA_ALERT);

if (errorParaAlert == null) {
    errorParaAlert = "";
}

String errorCampoCompra =
        (String) renderRequest.getAttribute(WebKeysCompras.ERROR_CAMPO_COMPRA);

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

boolean msgArticuloGuardado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-articulo-guardado"
        );

boolean msgArticuloBorrado =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-articulo-borrado"
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

String comprasGuardado = ParamUtil.getString(renderRequest, "compras_guardado", "");
String comprasDetallesGuardados = ParamUtil.getString(renderRequest, "compras_detalles_guardados", "");
String comprasOperacion = ParamUtil.getString(renderRequest, "compras_operacion", "");

boolean comprasGuardadoPorParametro =
        "true".equalsIgnoreCase(comprasGuardado);

boolean mostrarMensajeRequerimientoGuardado =
        msgRequerimientoGuardado || comprasGuardadoPorParametro;

boolean mostrarErrorGenericoCompra =
        errorRequerimientoCompra && WebKeysCompras.isEmpty(errorParaAlert);

String idRequerimientoMensaje =
        req != null && req.getIdRequerimientoCompra() > 0
                ? req.getIdRequerimientoCompraString()
                : ParamUtil.getString(renderRequest, "id_requerimiento_compra", "");

String tituloPantalla = "";

if (modoVista) {
    tituloPantalla = "Ver requerimiento de compra";
} else if (esNuevo) {
    tituloPantalla = "Nuevo requerimiento de compra";
} else {
    tituloPantalla = "Editar requerimiento de compra";
}
%>

<style type="text/css">
    .compras-bloqueado-sin-estilo {
        pointer-events: none;
    }

    .compras-form-colector {
        display: none;
    }

    .compras-btn-guardando {
        opacity: 0.65;
        cursor: wait;
    }
</style>

<c:if test="<%= mostrarMensajeRequerimientoGuardado %>">
    <div class="portlet-msg-success">
        <strong>Requerimiento de compra guardado correctamente.</strong>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID del requerimiento: <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(comprasDetallesGuardados) %>">
            <br />
            Detalles guardados/procesados: <%= HtmlUtil.escape(comprasDetallesGuardados) %>
        </c:if>

        <br />
    </div>
</c:if>

<c:if test="<%= msgDetalleGuardado %>">
    <div class="portlet-msg-success">
        Detalle del requerimiento guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgDetalleBorrado %>">
    <div class="portlet-msg-success">
        Detalle del requerimiento eliminado correctamente.
    </div>
</c:if>

<c:if test="<%= msgArticuloGuardado %>">
    <div class="portlet-msg-success">
        Articulo de compra guardado correctamente.
    </div>
</c:if>

<c:if test="<%= msgArticuloBorrado %>">
    <div class="portlet-msg-success">
        Articulo de compra eliminado correctamente.
    </div>
</c:if>

<c:if test="<%= msgRequerimientoAnulado %>">
    <div class="portlet-msg-success">
        Requerimiento de compra anulado correctamente.
    </div>
</c:if>

<c:if test="<%= mostrarErrorGenericoCompra %>">
    <div class="portlet-msg-error">
        <strong>No se pudo procesar el requerimiento de compra.</strong>
    </div>
</c:if>

<c:if test="<%= !WebKeysCompras.isEmpty(errorParaAlert) %>">
    <div class="portlet-msg-error">
        <strong>No se pudo guardar/procesar el requerimiento de compra.</strong>
        <br />
        <%= HtmlUtil.escape(errorParaAlert) %>

        <c:if test="<%= !WebKeysCompras.isEmpty(errorCampoCompra) %>">
            <br />
            Campo relacionado: <strong><%= HtmlUtil.escape(errorCampoCompra) %></strong>
        </c:if>

        <c:if test="<%= !WebKeysCompras.isEmpty(idRequerimientoMensaje) %>">
            <br />
            ID activo: <%= HtmlUtil.escape(idRequerimientoMensaje) %>
        </c:if>
    </div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= !soloLecturaSolicitada && puedeABM && !editablePorEstado %>">
    <div class="portlet-msg-info">El requerimiento solo puede editarse en estado Borrador.</div>
</c:if>

<form action="<%= actionURL.toString() %>"
      method="post"
      name="<portlet:namespace />fmCompras"
      id="<portlet:namespace />fmCompras"
      class="compras-form-colector">

    <input type="hidden"
           name="<portlet:namespace />compras_save_token"
           id="<portlet:namespace />compras_save_token"
           value="<%= HtmlUtil.escape(String.valueOf(renderRequest.getAttribute("COMPRAS_SAVE_TOKEN"))) %>" />

    <input type="hidden"
           name="<portlet:namespace /><%= Constants.CMD %>"
           id="<portlet:namespace />compras_cmd"
           value="saveAll" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_compra"
           id="<portlet:namespace />id_requerimiento_compra"
           value="<%= req.getIdRequerimientoCompra() %>" />

    <input type="hidden"
           name="<portlet:namespace />sector_id"
           id="<portlet:namespace />sector_id_hidden"
           value="<%= HtmlUtil.escape(reqSectorId) %>" />

    <input type="hidden"
           name="<portlet:namespace />cargo_ospim"
           id="<portlet:namespace />cargo_ospim_hidden"
           value="<%= HtmlUtil.escape(cargoOspimVisible) %>" />

    <input type="hidden"
           name="<portlet:namespace />cargo_tercerizadora"
           id="<portlet:namespace />cargo_tercerizadora_hidden"
           value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>" />

    <input type="hidden"
           name="<portlet:namespace />recupero"
           id="<portlet:namespace />recupero_hidden"
           value="<%= recuperoPorCargoTercerizadoraActual ? "true" : "false" %>" />

    <input type="hidden"
           name="<portlet:namespace />afiliado_cuil_titular"
           id="<portlet:namespace />afiliado_cuil_titular"
           value="<%= HtmlUtil.escape(afiliadoCuilTitular) %>" />

    <input type="hidden"
           name="<portlet:namespace />afiliado_int"
           id="<portlet:namespace />afiliado_int"
           value="<%= HtmlUtil.escape(afiliadoInt) %>" />

    <input type="hidden"
           name="<portlet:namespace />id_tercerizadora"
           id="<portlet:namespace />requerimiento_id_tercerizadora_hidden"
           value="<%= HtmlUtil.escape(idTercerizadora) %>" />

    <input type="hidden"
           name="<portlet:namespace />observaciones"
           id="<portlet:namespace />observaciones_hidden"
           value="<%= HtmlUtil.escape(req.getObservacionesVisible()) %>" />

    <div id="<portlet:namespace />detalle_payload"></div>
</form>

<div id="<portlet:namespace />compras_layout"
     class="<%= modoVista ? "compras-modo-vista" : "" %>">

    <fieldset class="block-labels">
        <legend><%= tituloPantalla %></legend>

        <table class="lfr-table">
            <tr>
                <td><label>ID:</label></td>
                <td><%= HtmlUtil.escape(req.getIdString()) %></td>

                <td><label>Estado:</label></td>
                <td colspan="3">
                    <strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong>
                </td>
            </tr>

            <tr>
                <td colspan="6">&nbsp;</td>
            </tr>

            <tr>
                <td><label>Sector:</label></td>
                <td colspan="5">
                    <select id="<portlet:namespace />sector_id"
                            onChange="<portlet:namespace />cambiarSectorCompra(true);"
                            <%= bloqueoSinEstiloVista %>>
                        <option value="0" data-requiere-afiliado="false">Seleccione</option>

                        <%
                        for (int i = 0; i < sectores.size(); i++) {
                            RequerimientoCompraSector sector = sectores.get(i);
                            String sectorId = String.valueOf(sector.getIdSector());
                            String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            String requiereAfiliado = sector.isRequiereAfiliado() ? "true" : "false";
                        %>
                            <option value="<%= sectorId %>"
                                    data-requiere-afiliado="<%= requiereAfiliado %>"
                                    <%= selected %>>
                                <%= HtmlUtil.escape(sector.getDescripcionVisible()) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                </td>
            </tr>

            <tr>
                <td colspan="6">&nbsp;</td>
            </tr>

            <tr id="<portlet:namespace />fila_cargos_compra"
                style="<%= sectorSinAfiliadoForzaCargoOspim ? "display:none;" : "" %>">
                <td><label>Cargo OSPIM %:</label></td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />cargo_ospim"
                           value="<%= HtmlUtil.escape(cargoOspimVisible) %>"
                           size="5"
                           maxlength="3"
                           onkeyup="<portlet:namespace />sincronizarFormularioCompra();"
                           onchange="<portlet:namespace />sincronizarFormularioCompra();"
                           onblur="<portlet:namespace />sincronizarFormularioCompra();"
                           <%= camposVistaReadOnly %> />
                </td>

                <td><label>Cargo tercerizadora %:</label></td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />cargo_tercerizadora"
                           value="<%= HtmlUtil.escape(cargoTercerizadoraVisible) %>"
                           size="5"
                           maxlength="3"
                           onchange="<portlet:namespace />sincronizarFormularioCompra();"
                           onblur="<portlet:namespace />sincronizarFormularioCompra();"
                           <%= camposVistaReadOnly %> />
                </td>

                <td><label>Recupero:</label></td>
                <td>
                    <input type="checkbox"
                           id="<portlet:namespace />recupero"
                           value="true"
                           <%= recuperoChecked %>
                           onclick="return false;"
                           onkeydown="return false;"
                           tabindex="-1"
                           aria-disabled="true" />
                </td>
            </tr>
        </table>

        <input type="hidden"
               id="<portlet:namespace />requerimiento_id_tercerizadora"
               value="<%= HtmlUtil.escape(idTercerizadora) %>" />
    </fieldset>

    <c:if test="<%= layoutEdicion || (modoVista && mostrarPanelAfiliadoEnVista) %>">
        <div id="<portlet:namespace />afiliado_requerimiento_panel"
             style="<%= modoEditable && !mostrarPanelAfiliadoEnVista ? "display:none;" : "" %>">
            <fieldset class="block-labels">
                <legend>
                    <liferay-ui:message key="datos-afiliado" />
                </legend>

                <div id="<portlet:namespace />afiliadoInicialMensaje"
                     class="portlet-msg-info"
                     style="display:none;"></div>

                <div id="<portlet:namespace />afiliadoInicialAutoSelect"
                     style="display:none;"></div>

                <liferay-util:include page="/html/portlet/autorizaciones/busqueda_afiliado.jsp">
                    <liferay-util:param value="<%= String.valueOf(true) %>"
                                        name="edit_mode" />
                    <liferay-util:param name="pag_reintegro"
                                        value="1" />
                    <liferay-util:param name="origen"
                                        value="" />
                </liferay-util:include>
            </fieldset>
        </div>
    </c:if>

    <c:if test="<%= modoVista && mostrarPanelAfiliadoEnVista %>">
        <script type="text/javascript">
            jQuery(function() {
                var ns = '<portlet:namespace />';
                var panel = jQuery('#' + ns + 'afiliado_requerimiento_panel');

                function setAfiliadoValue(id, value) {
                    var input = jQuery('#' + ns + id);

                    if (input.length > 0) {
                        input.val(value == null ? '' : value);
                    }
                }

                setAfiliadoValue('cuil', '<%= jsCompra(afiliadoCuilVisible) %>');
                setAfiliadoValue('inte', '<%= jsCompra(afiliadoIntVisible) %>');
                setAfiliadoValue('tipoDoc', '<%= jsCompra(afiliadoTipoDocumento) %>');
                setAfiliadoValue('nroDoc', '<%= jsCompra(afiliadoNumeroDocumento) %>');
                setAfiliadoValue('apellido', '<%= jsCompra(afiliadoApellido) %>');
                setAfiliadoValue('nombre', '<%= jsCompra(afiliadoNombre) %>');
                setAfiliadoValue('id_seccional', '<%= jsCompra(afiliadoIdSeccional) %>');
                setAfiliadoValue('seccional', '<%= jsCompra(afiliadoSeccional) %>');
                setAfiliadoValue('baja_fecha', '<%= jsCompra(afiliadoBajaFecha) %>');
                setAfiliadoValue('fecha_alta_af', '<%= jsCompra(afiliadoFechaAlta) %>');
                setAfiliadoValue('id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
                setAfiliadoValue('requerimiento_id_tercerizadora', '<%= jsCompra(afiliadoIdTercerizadora) %>');
                setAfiliadoValue('requerimiento_id_tercerizadora_hidden', '<%= jsCompra(afiliadoIdTercerizadora) %>');
                setAfiliadoValue('incapacidad_af', '<%= jsCompra(afiliadoIncapacidad) %>');

                setAfiliadoValue('nombre_plan', '<%= jsCompra(afiliadoNombrePlan) %>');
                setAfiliadoValue('id_plan', '<%= jsCompra(afiliadoIdPlan) %>');
                setAfiliadoValue('afi_tercerizadora', '<%= jsCompra(afiliadoAfiTercerizadora) %>');

                var entidadSeleccionadaInicial = jQuery('#' + ns + 'entidad').val();
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

                setAfiliadoValue('numero_afi', numeroAfiliadoInicial);

                if ('<%= jsCompra(afiliadoSeccional) %>' != '') {
                    setAfiliadoValue('secc_seleccionada', '1');
                }

                if ('<%= jsCompra(afiliadoAntecedentes) %>' == 'SI') {
                    setAfiliadoValue('tieneAntecedentes', '1');
                } else {
                    setAfiliadoValue('tieneAntecedentes', '0');
                }

                var bajaInput = jQuery('#' + ns + 'baja_fecha');

                if (bajaInput.length > 0) {
                    if (jQuery.trim(bajaInput.val()) != '') {
                        bajaInput.css('background', 'red');
                        bajaInput.css('color', 'white');
                    } else {
                        bajaInput.css('background', 'white');
                        bajaInput.css('color', 'black');
                    }
                }

                if (typeof window[ns + 'aplicarAntecedentesAfiliado'] == 'function') {
                    window[ns + 'aplicarAntecedentesAfiliado'](
                            '<%= jsCompra(afiliadoAntecedentes) %>' == 'SI' ? '1' : '0'
                    );
                }

                panel.find('input[type="text"], textarea').attr('readonly', 'readonly');

                panel.find('select')
                        .addClass('compras-bloqueado-sin-estilo')
                        .attr('tabindex', '-1')
                        .bind('mousedown keydown click change', function() {
                            return false;
                        });

                panel.find('input[type="checkbox"], input[type="radio"]')
                        .attr('tabindex', '-1')
                        .bind('click keydown change', function() {
                            return false;
                        });

                panel.find('input[type="button"], button, img[onclick], a[onclick]')
                        .removeAttr('onclick')
                        .bind('click', function() {
                            return false;
                        });
            });
        </script>
    </c:if>

    <fieldset class="block-labels">
        <legend>Observaciones</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <textarea id="<portlet:namespace />observaciones"
                              cols="100"
                              rows="4"
                              <%= camposVistaReadOnly %>><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                </td>
            </tr>
        </table>
    </fieldset>

    <liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_detalle_embebido.jsp">
        <liferay-util:param name="solo_lectura" value="<%= Boolean.toString(modoVista) %>" />
    </liferay-util:include>

    <c:if test="<%= !esNuevo %>">
        <liferay-util:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp" />
    </c:if>

    <table class="lfr-table">
        <tr>
            <td>
                <c:if test="<%= modoEditable %>">
                    <input type="button"
                           id="<portlet:namespace />btnGuardarCompras"
                           value="Guardar"
                           onClick="return <%= namespaceCompra %>guardar();" />

                    &nbsp;&nbsp;
                </c:if>

                <input type="button"
                       id="<portlet:namespace />btnVolverCompras"
                       class="compras-btn-volver"
                       value="Volver"
                       onClick="window.location.href='<%= volverURL.toString() %>';" />
            </td>
        </tr>
    </table>

    <c:if test="<%= modoVista %>">
        <script type="text/javascript">
            jQuery(function() {
                var ns = '<portlet:namespace />';
                var layout = jQuery('#' + ns + 'compras_layout');

                function ocultarBotonesModoVista() {
                    layout.find('input[type="button"], input[type="submit"], button')
                            .not('#' + ns + 'btnVolverCompras')
                            .hide();

                    layout.find('a[onclick], img[onclick]')
                            .hide();

                    jQuery('#' + ns + 'btnVolverCompras').show();
                }

                ocultarBotonesModoVista();

                setTimeout(ocultarBotonesModoVista, 300);
                setTimeout(ocultarBotonesModoVista, 1000);
            });
        </script>
    </c:if>
</div>
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

        var recuperoActivo = cargoTercerizadora === 100;

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
</script>
<c:if test="<%= modoEditable %>">
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

        return currDate + "/" + currMonth + "/" + currYear;
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
            if (typeof validarCuil == "function" && !validarCuil(cuil, "<liferay-ui:message key='valida-cuil-mensaje-limpiar'/>")) {
                jQuery('#<portlet:namespace />cuil').focus();
                return false;
            }
        }

        if (jQuery("#<portlet:namespace />secc_seleccionada").val() != "1") {
            jQuery("#<portlet:namespace />seccional").val("");
            jQuery("#<portlet:namespace />id_seccional").val("");
        }

        popupAfill = Liferay.Popup({
            title: '<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />',
            modal: true,
            width: 830
        });

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
            '&struts_action=/compras/buscar_afiliados' +
            '&cuil=' + encodeURIComponent(cuil) +
            '&inte=' + encodeURIComponent(inte) +
            '&tipoDoc=' + encodeURIComponent(tipoDoc) +
            '&nroDoc=' + encodeURIComponent(nroDoc) +
            '&seccional=' + encodeURIComponent(seccional) +
            '&nombre=' + encodeURIComponent(nombre) +
            '&apellido=' + encodeURIComponent(apellido) +
            '&entidad=' + encodeURIComponent(entidad) +
            '&numero_afi=' + encodeURIComponent(numeroAfi) +
            '&fecha_referencia=' + encodeURIComponent(fechaReferencia) +
            '&nroCredencialPrevencion=' + encodeURIComponent(nroCredencialPrevencion) +
            '&nroSocioPrevencion=' + encodeURIComponent(nroSocioPrevencion) +
            '&origen=' +
            '&popup=true';

        jQuery(popupAfill).load(url);

        return false;
    }

    function <portlet:namespace />buscarSeccional() {
        var id_seccional = jQuery("#<portlet:namespace />id_seccional").val();
        var seccional = jQuery("#<portlet:namespace />seccional").val();

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

        if (jQuery("#<portlet:namespace />secc_seleccionada").val() == "1" && (keyPressed != 9 && keyPressed != 16)) {
            jQuery("#<portlet:namespace />seccional").val("");
            jQuery("#<portlet:namespace />id_seccional").val("");
            jQuery("#<portlet:namespace />secc_seleccionada").val("");
            jQuery("#<portlet:namespace />btnBuscarSeccional").show();

            <portlet:namespace />sincronizarAfiliadoRequerimiento();

            return false;
        }

        var id_seccional = jQuery("#<portlet:namespace />id_seccional").val();
        var seccional = jQuery("#<portlet:namespace />seccional").val();

        if ((seccional.length >= 3 || id_seccional.length > 2) && (keyPressed != 9 && keyPressed != 16)) {
            if (id_seccional.length > 2) {
                jQuery("#<portlet:namespace />seccional").val("");
            } else {
                jQuery("#<portlet:namespace />id_seccional").val("");
            }

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                '&struts_action=/compras/buscar_seccional' +
                '&id_seccional=' + encodeURIComponent(id_seccional) +
                '&seccional=' + encodeURIComponent(seccional) +
                '&prefijo=';

            jQuery("#divSeccional").load(url);
            jQuery("#divSeccional").show();
        } else {
            jQuery("#divSeccional").hide("slow");
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
            document.getElementById("<portlet:namespace />baja_fecha").style.background = "red";
            document.getElementById("<portlet:namespace />baja_fecha").style.color = "white";
        } else {
            document.getElementById("<portlet:namespace />baja_fecha").style.background = "white";
            document.getElementById("<portlet:namespace />baja_fecha").style.color = "black";
        }

        jQuery('#<portlet:namespace />nombre_plan').val(nombre_plan);
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

        return attr == 'true' || attr == '1' || attr == 'SI' || attr == 'S';
    }

    function <portlet:namespace />sincronizarAfiliadoRequerimiento() {
        jQuery('#<portlet:namespace />afiliado_cuil_titular').val(
                <portlet:namespace />trimValue('cuil')
        );

        jQuery('#<portlet:namespace />afiliado_int').val(
                <portlet:namespace />trimValue('inte')
        );

        /*
         * La tercerizadora del afiliado NO se limpia por cargos.
         * Antes se limpiaba cuando OSPIM era 100 / tercerizadora 0.
         * Eso rompía el componente de búsqueda de afiliado.
         */
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

        /*
         * Importante:
         * NO tocar afi_tercerizadora acá.
         * Es un campo visual del componente de búsqueda de afiliado.
         */
    }

    function <portlet:namespace />sectorSinAfiliadoForzaCargoOspim() {
        var sectorId = <portlet:namespace />trimValue('sector_id');

        return sectorId != ''
                && sectorId != '0'
                && !<portlet:namespace />sectorRequiereAfiliado();
    }

    function <portlet:namespace />aplicarReglaCargosPorSector() {
        var forzarCargoOspim = <portlet:namespace />sectorSinAfiliadoForzaCargoOspim();

        if (forzarCargoOspim) {
            jQuery('#<portlet:namespace />cargo_ospim').val('100');
            jQuery('#<portlet:namespace />cargo_tercerizadora').val('0');

            jQuery('#<portlet:namespace />cargo_ospim_hidden').val('100');
            jQuery('#<portlet:namespace />cargo_tercerizadora_hidden').val('0');

            jQuery('#<portlet:namespace />requerimiento_id_tercerizadora').val('');
            jQuery('#<portlet:namespace />requerimiento_id_tercerizadora_hidden').val('');

            if (jQuery('#<portlet:namespace />id_tercerizadora').length > 0) {
                jQuery('#<portlet:namespace />id_tercerizadora').val('');
            }

            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(0);

            jQuery('#<portlet:namespace />fila_cargos_compra').hide();
        } else {
            jQuery('#<portlet:namespace />fila_cargos_compra').show();
            jQuery('#<portlet:namespace />fila_cargos_forzados_compra').hide();
        }

        return forzarCargoOspim;
    }

    function <portlet:namespace />sincronizarFormularioCompra() {
        <portlet:namespace />sincronizarAfiliadoRequerimiento();

        var cargoForzadoPorSector =
                <portlet:namespace />aplicarReglaCargosPorSector();

        jQuery('#<portlet:namespace />sector_id_hidden').val(
                <portlet:namespace />trimValue('sector_id')
        );

        jQuery('#<portlet:namespace />cargo_ospim_hidden').val(
                <portlet:namespace />trimValue('cargo_ospim')
        );

        jQuery('#<portlet:namespace />cargo_tercerizadora_hidden').val(
                <portlet:namespace />trimValue('cargo_tercerizadora')
        );

        jQuery('#<portlet:namespace />observaciones_hidden').val(
                jQuery('#<portlet:namespace />observaciones').val()
        );

        /*
         * Recupero NO se toma del click del checkbox.
         * El checkbox no es fuente de verdad.
         * La fuente de verdad es cargo_tercerizadora == 100.
         */
        if (cargoForzadoPorSector) {
            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(0);
        } else {
            <portlet:namespace />actualizarRecuperoPorCargoTercerizadora();
        }
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

        if ('<%= jsCompra(afiliadoAntecedentes) %>' == 'SI') {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '1');
        } else {
            <portlet:namespace />setAfiliadoValue('tieneAntecedentes', '0');
        }

        <portlet:namespace />aplicarColorBajaAfiliadoExistente();

        if (typeof <portlet:namespace />aplicarAntecedentesAfiliado == 'function') {
            <portlet:namespace />aplicarAntecedentesAfiliado(
                    '<%= jsCompra(afiliadoAntecedentes) %>' == 'SI' ? '1' : '0'
            );
        }

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

    function <portlet:namespace />cambiarSectorCompra(limpiarSiNoRequiere) {
        <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere);
        <portlet:namespace />aplicarReglaCargosPorSector();
        <portlet:namespace />sincronizarFormularioCompra();

        if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
            window['<portlet:namespace />filtrarArticulosPorSector']();
        }
    }

    function <portlet:namespace />parsePorcentaje(id, label) {
        var value = <portlet:namespace />trimValue(id);

        if (value == '') {
            value = '0';
            jQuery('#<portlet:namespace />' + id).val('0');
        }

        if (!/^[0-9]+$/.test(value)) {
            alert(label + ': debe ser un numero entero entre 0 y 100. Valor recibido: "' + value + '".');
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

        var sectorId = <portlet:namespace />trimValue('sector_id');

        if (sectorId == '' || sectorId == '0') {
            alert('Sector: debe seleccionar un sector.');
            <portlet:namespace />focusSeguroCompra('#<portlet:namespace />sector_id');
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (!requiereAfiliado) {
            <portlet:namespace />limpiarAfiliadoRequerimientoSiExiste();
            <portlet:namespace />aplicarReglaCargosPorSector();
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

        if (cargoOspim + cargoTercerizadora > 100) {
            alert(
                'Cargos: la suma de Cargo OSPIM (' + cargoOspim +
                ') y Cargo tercerizadora (' + cargoTercerizadora +
                ') es ' + (cargoOspim + cargoTercerizadora) +
                '. No puede superar 100.'
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

        /*
         * Recupero se calcula únicamente por Cargo tercerizadora.
         * Si Cargo tercerizadora no es 100, se envía recupero=false.
         * No se limpia tercerizadora por cargos.
         */
        <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(cargoTercerizadora);

        /*
         * Validación opcional de negocio:
         * Si hay cargo a tercerizadora, debería existir afiliado con tercerizadora.
         * Pero NO se muestra/oculta ni se limpia nada automáticamente.
         */
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
                'Detalles: no se encontro la funcion <portlet:namespace />serializarDetallesCompras(). ' +
                'El JSP embebido no se esta renderizando correctamente o Liferay esta usando una version vieja compilada.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (!serializadorDetalles()) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var detalleCountInput = jQuery(form).find('input[name$="detalle_count"]');

        if (detalleCountInput.length == 0) {
            alert(
                'Detalles: serializarDetallesCompras() se ejecuto, pero no dejo detalle_count dentro del formulario principal. ' +
                'Revisar que el JSP embebido agregue los hidden a #<portlet:namespace />fmCompras.'
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        var detalleCount = parseInt(detalleCountInput.val(), 10);

        if (isNaN(detalleCount) || detalleCount <= 0) {
            alert(
                'Detalles: no hay detalles para guardar. detalle_count=' + detalleCountInput.val()
            );

            return <portlet:namespace />cancelarGuardadoCompra();
        }

        if (!<portlet:namespace />submitFormularioCompra(form)) {
            return <portlet:namespace />cancelarGuardadoCompra();
        }

        return false;
    }

    jQuery(function() {
        <portlet:namespace />cargarAfiliadoInicial();

        <c:if test="<%= !esNuevo %>">
            <portlet:namespace />cargarAfiliadoExistenteEnEdicion();
        </c:if>

        /*
         * Orden correcto:
         * 1) ajustar panel afiliado según sector
         * 2) aplicar regla de cargos por sector
         * 3) sincronizar hidden del formulario
         */
        <portlet:namespace />actualizarVisibilidadAfiliado(false);
        <portlet:namespace />aplicarReglaCargosPorSector();
        <portlet:namespace />sincronizarFormularioCompra();

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').change(function() {
            /*
             * No calcular recupero desde Cargo OSPIM.
             * sincronizarFormularioCompra() ya calcula recupero desde Cargo tercerizadora.
             */
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />cargo_ospim, #<portlet:namespace />cargo_tercerizadora').keyup(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />sector_id, #<portlet:namespace />id_sector').change(function() {
            /*
             * cambiarSectorCompra() ya hace:
             * - actualizarVisibilidadAfiliado()
             * - aplicarReglaCargosPorSector()
             * - sincronizarFormularioCompra()
             * - filtrarArticulosPorSector()
             */
            <portlet:namespace />cambiarSectorCompra(true);
        });

        jQuery('#<portlet:namespace />observaciones').change(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />observaciones').keyup(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        /*
         * El checkbox recupero queda visual, pero NO clickeable.
         * Por eso no conviene escuchar su change como fuente de verdad.
         */
        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').change(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        jQuery('#<portlet:namespace />cuil, #<portlet:namespace />inte, #<portlet:namespace />id_tercerizadora').keyup(function() {
            <portlet:namespace />sincronizarFormularioCompra();
        });

        if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
            window['<portlet:namespace />filtrarArticulosPorSector']();
        }

        setTimeout(function() {
            /*
             * Segundo pase defensivo.
             * Necesario porque busqueda_afiliado.jsp puede terminar de inicializar
             * campos después del document ready principal.
             */
            <portlet:namespace />actualizarVisibilidadAfiliado(false);
            <portlet:namespace />aplicarReglaCargosPorSector();
            <portlet:namespace />sincronizarFormularioCompra();

            if (typeof window['<portlet:namespace />filtrarArticulosPorSector'] == 'function') {
                window['<portlet:namespace />filtrarArticulosPorSector']();
            }
        }, 300);
    });

</script>
</c:if>