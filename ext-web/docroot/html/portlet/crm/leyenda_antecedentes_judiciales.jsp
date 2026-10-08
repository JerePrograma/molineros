<%--
    Este componente muestra la referencia visual de los colores utilizados
    para identificar afiliados con antecedentes judiciales.

    El color corresponde al tipo del último reclamo/documento legal
    registrado para el grupo familiar.

    Los colores se encuentran configurados en la tabla:
    crm.tipo_reclamo

    Relación actual:
        AMPARO                 -> Rojo
        CARTA DOCUMENTO / NOTA -> Naranja
        EXPEDIENTE SSS         -> Amarillo
--%>

<style type="text/css">

    .leyenda-antecedentes-judiciales {
	    margin-top: 15px;
	    padding: 12px 15px;
	    border: 1px solid #d9d9d9;
	    background-color: #f8f8f8;
	    border-radius: 4px;
	    font-size: 13px;
	    color: #444444;
	    text-align: left !important;
	}

    .leyenda-antecedentes-judiciales-titulo {
        font-weight: bold;
        margin-bottom: 5px;
        color: #333333;
    }

    .leyenda-antecedentes-judiciales-texto {
        margin-bottom: 8px;
    }

    .leyenda-antecedentes-judiciales-items {
	    display: flex;
	    flex-wrap: wrap;
	    gap: 18px;
	    justify-content: flex-start;
	}

    .leyenda-antecedentes-judiciales-item {
        display: inline-flex;
        align-items: center;
    }

    .leyenda-antecedentes-judiciales-color {
        width: 14px;
        height: 14px;
        margin-right: 6px;
        border-radius: 2px;
        display: inline-block;
        border: 1px solid #999999;
    }

</style>

<div class="leyenda-antecedentes-judiciales">

    <div class="leyenda-antecedentes-judiciales-titulo">
        Referencia de antecedentes judiciales
    </div>

    <div class="leyenda-antecedentes-judiciales-texto">
        El color identifica el tipo del
        <strong>último documento legal registrado</strong>
        para el grupo familiar.
    </div>

    <div class="leyenda-antecedentes-judiciales-items">

        <span class="leyenda-antecedentes-judiciales-item">
            <span
                class="leyenda-antecedentes-judiciales-color"
                style="background-color: #ff4d4d;">
            </span>
            <strong>Rojo:</strong>&nbsp;Amparo
        </span>

        <span class="leyenda-antecedentes-judiciales-item">
            <span
                class="leyenda-antecedentes-judiciales-color"
                style="background-color: #ffa724;">
            </span>
            <strong>Naranja:</strong>&nbsp;Carta Documento / Nota
        </span>

        <span class="leyenda-antecedentes-judiciales-item">
            <span
                class="leyenda-antecedentes-judiciales-color"
                style="background-color: #fae561;">
            </span>
            <strong>Amarillo:</strong>&nbsp;Expediente SSS
        </span>

    </div>

</div>