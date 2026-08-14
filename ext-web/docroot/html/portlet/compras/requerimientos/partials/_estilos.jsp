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
     * Separación vertical uniforme entre las secciones principales.
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
</style>
