<%--
Publica el contrato explícito consumido por los componentes JSP independientes.
Los atributos conservan los valores calculados por _modelo_requerimiento.jsp y
propagan el contexto de cotización/documentos preparado por los Actions.
--%>
<%
request.setAttribute(
        "compras.requerimiento.req",
        req
);

request.setAttribute(
        "compras.requerimiento.esNuevo",
        Boolean.valueOf(esNuevo)
);

request.setAttribute(
        "compras.requerimiento.surgeSeleccionado",
        surgeSeleccionadoCompra
);

request.setAttribute(
        "compras.requerimiento.puedeABM",
        Boolean.valueOf(puedeABM)
);

request.setAttribute(
        "compras.requerimiento.puedeCotizar",
        Boolean.valueOf(puedeCotizar)
);

request.setAttribute(
        "compras.requerimiento.soloLecturaSolicitada",
        Boolean.valueOf(soloLecturaSolicitada)
);

request.setAttribute(
        "compras.requerimiento.puedeEditarEstructura",
        Boolean.valueOf(puedeEditarEstructuraPantalla)
);

request.setAttribute(
        "compras.requerimiento.puedeEditarCotizacion",
        Boolean.valueOf(puedeEditarCotizacionPantalla)
);

request.setAttribute(
        "compras.requerimiento.modoEditable",
        Boolean.valueOf(modoEditable)
);

request.setAttribute(
        "compras.requerimiento.sectores",
        sectores
);

request.setAttribute(
        "compras.requerimiento.volverURL",
        volverURL
);

request.setAttribute(
        "compras.requerimiento.imprimirURL",
        imprimirURL
);

request.setAttribute(
        "compras.requerimiento.reqSectorId",
        reqSectorId
);

request.setAttribute(
        "compras.requerimiento.sectorDescripcion",
        sectorDescripcionSoloLectura
);

request.setAttribute(
        "compras.requerimiento.idTercerizadora",
        idTercerizadora
);

request.setAttribute(
        "compras.requerimiento.cargoOspim",
        cargoOspimVisible
);

request.setAttribute(
        "compras.requerimiento.cargoTercerizadora",
        cargoTercerizadoraVisible
);

request.setAttribute(
        "compras.requerimiento.afiliadoCuil",
        afiliadoCuilVisible
);

request.setAttribute(
        "compras.requerimiento.afiliadoInt",
        afiliadoIntVisible
);

request.setAttribute(
        "compras.requerimiento.afiliadoTipoDocumento",
        afiliadoTipoDocumento
);

request.setAttribute(
        "compras.requerimiento.afiliadoNumeroDocumento",
        afiliadoNumeroDocumento
);

request.setAttribute(
        "compras.requerimiento.afiliadoApellido",
        afiliadoApellido
);

request.setAttribute(
        "compras.requerimiento.afiliadoNombre",
        afiliadoNombre
);

request.setAttribute(
        "compras.requerimiento.afiliadoSeccional",
        afiliadoSeccional
);

request.setAttribute(
        "compras.requerimiento.afiliadoBajaFecha",
        afiliadoBajaFecha
);

request.setAttribute(
        "compras.requerimiento.afiliadoFechaAlta",
        afiliadoFechaAlta
);

request.setAttribute(
        "compras.requerimiento.afiliadoIdTercerizadora",
        afiliadoIdTercerizadora
);

request.setAttribute(
        "compras.requerimiento.afiliadoIncapacidad",
        afiliadoIncapacidad
);

request.setAttribute(
        "compras.requerimiento.afiliadoAntecedentes",
        afiliadoAntecedentes
);

request.setAttribute(
        "compras.requerimiento.afiliadoIdSeccional",
        afiliadoIdSeccional
);

request.setAttribute(
        "compras.requerimiento.afiliadoNumeroOspim",
        afiliadoNumeroOspim
);

request.setAttribute(
        "compras.requerimiento.afiliadoNumeroUoma",
        afiliadoNumeroUoma
);

request.setAttribute(
        "compras.requerimiento.afiliadoNumeroAmtima",
        afiliadoNumeroAmtima
);

request.setAttribute(
        "compras.requerimiento.afiliadoNumero",
        afiliadoNumeroAfiliado
);

request.setAttribute(
        "compras.requerimiento.afiliadoNombrePlan",
        afiliadoNombrePlan
);

request.setAttribute(
        "compras.requerimiento.afiliadoIdPlan",
        afiliadoIdPlan
);

request.setAttribute(
        "compras.requerimiento.afiliadoTercerizadora",
        afiliadoAfiTercerizadora
);

request.setAttribute(
        "compras.requerimiento.mostrarPanelAfiliado",
        Boolean.valueOf(mostrarPanelAfiliadoEnVista)
);

request.setAttribute(
        "compras.requerimiento.errorParaAlert",
        errorParaAlert
);

request.setAttribute(
        "compras.requerimiento.errorCampo",
        errorCampoCompra
);

request.setAttribute(
        "compras.requerimiento.msgDetalleGuardado",
        Boolean.valueOf(msgDetalleGuardado)
);

request.setAttribute(
        "compras.requerimiento.msgDetalleBorrado",
        Boolean.valueOf(msgDetalleBorrado)
);

request.setAttribute(
        "compras.requerimiento.msgAnulado",
        Boolean.valueOf(msgRequerimientoAnulado)
);

request.setAttribute(
        "compras.requerimiento.operacion",
        comprasOperacion
);

request.setAttribute(
        "compras.requerimiento.mostrarMensajeGuardado",
        Boolean.valueOf(mostrarMensajeRequerimientoGuardado)
);

request.setAttribute(
        "compras.requerimiento.mostrarErrorGenerico",
        Boolean.valueOf(mostrarErrorGenericoCompra)
);

request.setAttribute(
        "compras.requerimiento.idMensaje",
        idRequerimientoMensaje
);

request.setAttribute(
        "compras.requerimiento.titulo",
        tituloPantalla
);

/*
 * Puente RenderRequest -> HttpServletRequest para los partials/runtime y
 * liferay-util:include. El Action es la única capa que consulta backend;
 * este JSP solamente propaga atributos ya resueltos.
 */
String[] atributosPresentacionCompra =
        new String[] {
                "compras.requerimiento.prestadoresEnviados",
                "compras.requerimiento.errorPrestadoresEnviados",
                "compras.requerimiento.prestadoresDisponiblesPresupuesto",
                "compras.requerimiento.presupuestos",
                "compras.requerimiento.idsPrestadoresConPresupuesto",
                "compras.requerimiento.errorPresupuestos",
                "compras.requerimiento.presupuestoDocumentoValido",
                "compras.requerimiento.presupuestoDownloadURL",
                "compras.requerimiento.ordenesMedicas",
                "compras.requerimiento.errorOrdenesMedicas"
        };

for (int i = 0;
        i < atributosPresentacionCompra.length;
        i++) {

    String nombreAtributo =
            atributosPresentacionCompra[i];

    Object valorAtributo =
            renderRequest.getAttribute(
                    nombreAtributo
            );

    if (valorAtributo != null) {
        request.setAttribute(
                nombreAtributo,
                valorAtributo
        );
    }
}
%>
