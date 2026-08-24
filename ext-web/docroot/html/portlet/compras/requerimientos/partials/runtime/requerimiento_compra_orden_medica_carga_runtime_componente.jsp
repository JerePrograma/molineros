<%--
Responsabilidad:
    Recupera atributos request e incluye la carga de Órdenes Médicas.
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
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>

<%--
Requiere:
- esNuevo
- modoEditable
- puedeEditarEstructuraPantalla
--%>
<%
boolean esNuevo =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.esNuevo"
                )
        );

boolean modoEditable =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.modoEditable"
                )
        );

boolean puedeEditarEstructuraPantalla =
        Boolean.TRUE.equals(
                request.getAttribute(
                        "compras.requerimiento.puedeEditarEstructura"
                )
        );
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_orden_medica_carga_componente.jsp" %>