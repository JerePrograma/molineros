<%--
Responsabilidad:
    Renderiza presupuestos y Órdenes Médicas activas del requerimiento.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp, requerimiento_compra_formulario_componente.jsp
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
<c:if test="<%= !esNuevo && req.puedeVerPresupuestos() %>">
    <div class="compras-seccion compras-seccion-adjuntos">
        <liferay-util:include
            page="/html/portlet/compras/requerimientos/requerimiento_compra_documentos.jsp">

            <liferay-util:param
                name="solo_lectura"
                value="<%= Boolean.toString(
                        !puedeEditarCotizacionPantalla
                ) %>" />
        </liferay-util:include>
    </div>
</c:if>