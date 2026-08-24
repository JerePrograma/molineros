<%--
Responsabilidad:
    Wrapper legacy del bloque de afiliado editable dentro de un formulario.
Incluido desde:
    Sin caller JSP activo; entry point de compatibilidad conservado por su ruta legacy.
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
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
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_afiliado_editable_componente.jsp" %>
