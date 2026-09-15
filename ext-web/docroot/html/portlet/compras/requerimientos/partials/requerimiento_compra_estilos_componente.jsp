<%--
Responsabilidad:
    Declara estilos locales de las pantallas de requerimiento de compra.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
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

    .compras-campo-solo-lectura {
        padding: 4px 0;
        min-height: 18px;
    }

    .compras-resumen-requerimiento {
        width: auto;
        max-width: 100%;
        margin-bottom: 8px;
    }

    .compras-resumen-requerimiento td {
        padding: 3px 12px 3px 0;
        vertical-align: middle;
        white-space: normal;
    }

    .compras-resumen-requerimiento .compras-campo-solo-lectura {
        display: inline-block;
        padding: 0;
        min-height: 0;
    }

    .compras-cargos-requerimiento {
        margin-top: 8px;
    }

    .compras-observaciones-vista {
        white-space: pre-wrap;
        border: 1px solid #cccccc;
        padding: 8px;
        min-height: 60px;
        background: #f8f8f8;
    }

    .compras-afiliado-readonly td {
        padding: 4px 8px 4px 0;
    }

    /*
     * Separación vertical uniforme entre las secciónes principales.
     * El margen pertenece al bloque que efectivamente se renderiza, por lo
     * que una sección condicional no deja espacios vacíos al ocultarse.
     */
    .compras-formulario-requerimiento > .compras-seccion {
        margin-top: 0;
        margin-bottom: 12px;
    }

    .compras-formulario-requerimiento
    > .compras-seccion
    > fieldset.block-labels {
        margin: 0;
    }

    .compras-formulario-requerimiento
    > .compras-seccion-botonera {
        margin-bottom: 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-botonera
    table.lfr-table {
        margin: 0;
    }

    .compras-formulario-requerimiento
    .compras-botonera-acciones
    > input,
    .compras-formulario-requerimiento
    .compras-botonera-acciones
    > span {
        margin-right: 8px;
        vertical-align: middle;
    }

    .compras-formulario-requerimiento
    .compras-botonera-acciones
    > input.compras-btn-volver {
        margin-right: 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-detalle
    > .compras-detalle-editor {
        margin: 0 0 12px 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-detalle
    > .compras-detalle-tabla {
        margin: 0 0 12px 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-detalle
    > .compras-observaciones {
        margin: 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-adjuntos
    .compras-adjuntos-formulario {
        margin: 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-adjuntos
    .compras-adjuntos-formulario
    > fieldset.block-labels {
        margin: 0;
    }

    .compras-formulario-requerimiento
    .compras-seccion-adjuntos
    .compras-adjuntos-formulario
    > fieldset.compras-adjuntos-cotizaciones {
        margin-top: 12px;
    }

    /* CQA-001: ritmo visual exclusivo del formulario de Compras. */
    .compras-formulario-requerimiento fieldset.block-labels
    table.lfr-table td {
        padding-top: 5px;
        padding-bottom: 5px;
        vertical-align: middle;
    }

    .compras-formulario-requerimiento label {
        margin-right: 6px;
        line-height: 26px;
        vertical-align: middle;
    }

    .compras-formulario-requerimiento input[type="text"],
    .compras-formulario-requerimiento input[type="search"],
    .compras-formulario-requerimiento input[type="number"],
    .compras-formulario-requerimiento select {
        height: 26px;
        line-height: 20px;
        padding: 2px 5px;
        box-sizing: border-box;
        vertical-align: middle;
        margin-top: 1px;
        margin-bottom: 1px;
        max-width: 100%;
    }

    .compras-formulario-requerimiento textarea {
        padding: 5px;
        box-sizing: border-box;
        vertical-align: top;
        max-width: 100%;
        margin-top: 2px;
        margin-bottom: 2px;
    }

    .compras-formulario-requerimiento .compras-nro-rp {
        color: #0066cc;
        font-weight: bold;
    }

    .compras-afiliado-contacto-layout {
        width: 100%;
    }

    .compras-afiliado-contacto-datos {
        vertical-align: top;
    }

    .compras-afiliado-contacto-acciones {
        width: 150px;
        padding-left: 12px;
        vertical-align: top;
    }

    .compras-verificar-contacto {
        padding: 8px 5px;
        text-align: center;
    }

    .compras-formulario-requerimiento
    .compras-verificar-contacto label {
        display: block;
        margin: 0 0 10px 0;
        line-height: 16px;
    }

    .compras-verificar-contacto input[type="button"] {
        width: 100px;
        height: 30px;
        padding: 3px 0;
        font-size: 12px;
    }

    /*
     * Sugerencias de ítems históricos del afiliado.
     *
     * Se utiliza la misma gramática visual de las tablas legacy
     * de Liferay: lfr-table, taglib-search-iterator, results-header
     * y results-row.
     *
     * No se fija table-layout porque la Descripción debe aprovechar
     * naturalmente todo el ancho disponible.
     */
    .compras-formulario-requerimiento
    .compras-historicos-panel {
        margin: 0 0 14px 0;
    }

    .compras-formulario-requerimiento
    .compras-historicos-encabezado {
        margin: 0 0 7px 0;
        padding: 0;
        line-height: 18px;
    }

    .compras-formulario-requerimiento
    .compras-historicos-ayuda {
        margin-left: 8px;
        color: #666666;
        font-size: 11px;
        font-weight: normal;
    }

    .compras-formulario-requerimiento
    .compras-historicos-estado {
        margin: 0 0 7px 0;
        color: #666666;
        line-height: 18px;
    }

    .compras-formulario-requerimiento
    .compras-historicos-tabla {
        width: 100%;
        margin: 0;
        border-collapse: collapse;
    }

    .compras-formulario-requerimiento
    .compras-historicos-tabla th,
    .compras-formulario-requerimiento
    .compras-historicos-tabla td {
        padding: 6px 8px;
        vertical-align: middle;
    }

    .compras-formulario-requerimiento
    .compras-historicos-col-check {
        width: 36px;
        text-align: center;
    }

    .compras-formulario-requerimiento
    .compras-historicos-col-check input[type="checkbox"] {
        margin: 0;
        padding: 0;
        vertical-align: middle;
    }

    .compras-formulario-requerimiento
    .compras-historicos-col-tipo {
        width: 190px;
        white-space: normal;
    }

    .compras-formulario-requerimiento
    .compras-historicos-col-codigo {
        width: 120px;
        white-space: nowrap;
    }

    .compras-formulario-requerimiento
    .compras-historicos-col-descripcion {
        white-space: normal;
        word-wrap: break-word;
    }

    .compras-formulario-requerimiento
    .compras-historicos-fila-agregada td {
        color: #777777;
    }

    .compras-formulario-requerimiento
    .compras-historicos-estado-item {
        display: inline-block;
        margin-left: 9px;
        color: #777777;
        font-size: 11px;
        font-style: italic;
        white-space: nowrap;
    }

    .compras-formulario-requerimiento
    .compras-historicos-acciones {
        margin: 8px 0 0 0;
        text-align: right;
    }

    .compras-formulario-requerimiento
    .compras-historicos-acciones input[type="button"] {
        min-width: 90px;
    }
</style>