<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

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

String modoRequerimientoCompra = (String) request.getAttribute("MODO_REQUERIMIENTO_COMPRA");

if (modoRequerimientoCompra == null || modoRequerimientoCompra.trim().length() == 0) {
    modoRequerimientoCompra = ParamUtil.getString(renderRequest, "modo", "EDICION").toUpperCase();
}

boolean modoAltaForzado = "ALTA".equalsIgnoreCase(modoRequerimientoCompra);
boolean modoEdicionForzado = "EDICION".equalsIgnoreCase(modoRequerimientoCompra);
boolean modoVistaForzado = "VISTA".equalsIgnoreCase(modoRequerimientoCompra)
        || "VER".equalsIgnoreCase(modoRequerimientoCompra);

RequerimientoCompra req = null;

if (!modoAltaForzado) {
    req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

    if (req == null) {
        req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
    }
}

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean puedeCotizar = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);

Object soloLecturaAttr = renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String strutsActionActual = ParamUtil.getString(renderRequest, "struts_action", "");
String modoParam = ParamUtil.getString(renderRequest, "modo", "");

boolean soloLecturaSolicitada =
        modoVistaForzado
        || Boolean.TRUE.equals(soloLecturaAttr)
        || "/compras/ver_requerimiento".equals(strutsActionActual)
        || "ver".equalsIgnoreCase(modoParam);

boolean editablePorEstado = esNuevo || req.puedeEditarEstructura();
boolean cotizacionEditablePorEstado = req.puedeEditarCotizacion();

boolean puedeEditarEstructuraPantalla =
        puedeABM
        && editablePorEstado;

boolean puedeEditarCotizacionPantalla =
        !esNuevo
        && puedeCotizar
        && cotizacionEditablePorEstado;

boolean layoutEdicion =
        !modoVistaForzado
        && (puedeEditarEstructuraPantalla || puedeEditarCotizacionPantalla);

boolean modoEditable =
        layoutEdicion
        && !soloLecturaSolicitada;

boolean modoVista = modoVistaForzado || !modoEditable;

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

if (articulosCompra == null) {
    articulosCompra = new ArrayList<CompraArticulo>();
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

PortletURL imprimirURL = renderResponse.createRenderURL();
imprimirURL.setWindowState(WindowState.MAXIMIZED);
imprimirURL.setParameter("struts_action", "/compras/imprimir_requerimiento");
imprimirURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

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

/*
 * Regla centralizada para la pantalla:
 * recupero = true si existe cargo a tercerizadora mayor a 0.
 * Si negocio exige exactamente 100, cambiar ac� y en actualizarRecuperoPorCargoTercerizadora().
 */
boolean recuperoPorCargoTercerizadoraActual =
        !sectorSinAfiliadoForzaCargoOspim
        && cargoTercerizadoraActual > 0;

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

    if (WebKeysCompras.isEmpty(afiliadoIdTercerizadora)) {
        afiliadoIdTercerizadora = idTercerizadora;
    }

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
String camposSoloLectura = !modoEditable ? "readonly=\"readonly\"" : "";

String bloqueoSinEstiloVista = !modoEditable
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

if (modoVistaForzado) {
    tituloPantalla = "Ver requerimiento de compra";
} else if (esNuevo) {
    tituloPantalla = "Nuevo requerimiento de compra";
} else if (modoEditable) {
    tituloPantalla = "Editar requerimiento de compra";
} else {
    tituloPantalla = "Ver requerimiento de compra";
}
%>
