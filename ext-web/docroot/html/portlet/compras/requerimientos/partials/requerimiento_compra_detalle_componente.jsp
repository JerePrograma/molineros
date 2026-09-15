<%--
Responsabilidad:
    Selecciona el editor o la consulta de prestaciones según capacidades.
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
<liferay-util:include
        page="/html/portlet/compras/requerimientos/requerimiento_compra_detalle_embebido.jsp">

    <liferay-util:param
            name="solo_lectura"
            value="<%= Boolean.toString(!modoInteractivo) %>" />

</liferay-util:include>
