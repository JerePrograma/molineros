<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%--
Responsabilidad:
    Publica en request el modelo preparado para includes runtime.
Incluido desde:
    requerimiento_compra_alta.jsp, requerimiento_compra_consulta.jsp, requerimiento_compra_edicion.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
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
<%--
Publica el contrato explícito consumido por los componentes JSP independientes.
Los atributos conservan los valores calculados por requerimiento_compra_modelo_vista_componente.jsp y
propagan el contexto de cotización/documentos preparado por los Actions.
--%>
<%
request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MODELO,
        req
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_ES_NUEVO,
        Boolean.valueOf(esNuevo)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_SURGE_SELECCIONADO,
        surgeSeleccionadoCompra
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_ABM,
        Boolean.valueOf(puedeABM)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_COTIZAR,
        Boolean.valueOf(puedeCotizar)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_SOLO_LECTURA_SOLICITADA,
        Boolean.valueOf(soloLecturaSolicitada)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_ESTRUCTURA,
        Boolean.valueOf(puedeEditarEstructuraPantalla)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_SURGE,
        Boolean.valueOf(puedeEditarSurgePantalla)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_COTIZACION,
        Boolean.valueOf(puedeEditarCotizacionPantalla)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MODO_EDITABLE,
        Boolean.valueOf(modoEditable)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_SECTORES,
        sectores
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_VOLVER_URL,
        volverURL
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_IMPRIMIR_URL,
        imprimirURL
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_SECTOR_ID,
        reqSectorId
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_SECTOR_DESCRIPCION,
        sectorDescripcionSoloLectura
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_ID_TERCERIZADORA,
        idTercerizadora
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_CARGO_OSPIM,
        cargoOspimVisible
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_CARGO_TERCERIZADORA,
        cargoTercerizadoraVisible
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_CUIL,
        afiliadoCuilVisible
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_INT,
        afiliadoIntVisible
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_TIPO_DOCUMENTO,
        afiliadoTipoDocumento
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_DOCUMENTO,
        afiliadoNumeroDocumento
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_APELLIDO,
        afiliadoApellido
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NOMBRE,
        afiliadoNombre
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_SECCIONAL,
        afiliadoSeccional
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_BAJA_FECHA,
        afiliadoBajaFecha
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_FECHA_ALTA,
        afiliadoFechaAlta
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_TERCERIZADORA,
        afiliadoIdTercerizadora
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_INCAPACIDAD,
        afiliadoIncapacidad
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ANTECEDENTES,
        afiliadoAntecedentes
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_SECCIONAL,
        afiliadoIdSeccional
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_OSPIM,
        afiliadoNumeroOspim
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_UOMA,
        afiliadoNumeroUoma
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_AMTIMA,
        afiliadoNumeroAmtima
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO,
        afiliadoNumeroAfiliado
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NOMBRE_PLAN,
        afiliadoNombrePlan
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_PLAN,
        afiliadoIdPlan
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_TERCERIZADORA,
        afiliadoAfiTercerizadora
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_PANEL_AFILIADO,
        Boolean.valueOf(mostrarPanelAfiliadoEnVista)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_ERROR_ALERT,
        errorParaAlert
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_ERROR_CAMPO,
        errorCampoCompra
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MSG_DETALLE_GUARDADO,
        Boolean.valueOf(msgDetalleGuardado)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MSG_DETALLE_BORRADO,
        Boolean.valueOf(msgDetalleBorrado)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MSG_ANULADO,
        Boolean.valueOf(msgRequerimientoAnulado)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_OPERACION,
        comprasOperacion
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_MENSAJE_GUARDADO,
        Boolean.valueOf(mostrarMensajeRequerimientoGuardado)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_ERROR_GENERICO,
        Boolean.valueOf(mostrarErrorGenericoCompra)
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_ID_MENSAJE,
        idRequerimientoMensaje
);

request.setAttribute(
        WebKeysCompras.ATTR_REQUERIMIENTO_TITULO,
        tituloPantalla
);

/*
 * Puente RenderRequest -> HttpServletRequest para los partials/runtime y
 * liferay-util:include. El Action es la única capa que consulta backend;
 * este JSP solamente propaga atributos ya resueltos.
 */
String[] atributosPresentacionCompra =
        new String[] {
                WebKeysCompras.ATTR_PRESTADORES_ENVIADOS_REQUERIMIENTO,
                WebKeysCompras.ATTR_ERROR_PRESTADORES_ENVIADOS_REQUERIMIENTO,
                WebKeysCompras.ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO,
                WebKeysCompras.ATTR_PRESUPUESTOS_REQUERIMIENTO,
                WebKeysCompras.ATTR_IDS_PRESTADORES_CON_PRESUPUESTO,
                WebKeysCompras.ATTR_ERROR_PRESUPUESTOS_REQUERIMIENTO,
                WebKeysCompras.ATTR_PRESUPUESTO_DOCUMENTO_VALIDO,
                WebKeysCompras.ATTR_PRESUPUESTO_DOWNLOAD_URL,
                WebKeysCompras.ATTR_ORDENES_MEDICAS_REQUERIMIENTO,
                WebKeysCompras.ATTR_ERROR_ORDENES_MEDICAS_REQUERIMIENTO
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
