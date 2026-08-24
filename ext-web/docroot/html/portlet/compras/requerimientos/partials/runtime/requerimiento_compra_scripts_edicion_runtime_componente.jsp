<%--
Responsabilidad:
    Prepara el contexto e incluye scripts de edición en orden estable.
Incluido desde:
    requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
Entradas requeridas:
    Atributos request publicados por requerimiento_compra_contexto_publicacion_componente.jsp.
Atributos de request consumidos:
    Claves compras.requerimiento.* leídas por requerimiento_compra_runtime_inicializacion_componente.jsp.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Expone, en orden, las funciones declaradas por los dos componentes incluidos.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%--
Cada bloque se compila como una unidad JSP separada para mantener el bytecode
generado por Jasper por debajo del límite de método de la JVM. El orden es
funcional: afiliado e históricos deben declararse antes del guardado.
--%>
<jsp:include page="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_edicion_afiliado_componente.jsp" />
<jsp:include page="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_edicion_guardado_componente.jsp" />
