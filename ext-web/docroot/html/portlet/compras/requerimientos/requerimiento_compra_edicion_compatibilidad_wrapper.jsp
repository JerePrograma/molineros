<%--
Responsabilidad:
    Conserva el wrapper legacy que compone la pantalla de edición.
Incluido desde:
    Sin caller JSP activo; entry point de compatibilidad conservado por su ruta legacy.
Pantallas o estados de uso:
    Edición de requerimiento cuando un caller legacy resuelve este JSP por ruta.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ include file="/html/portlet/compras/requerimientos/requerimiento_compra_edicion.jsp" %>
