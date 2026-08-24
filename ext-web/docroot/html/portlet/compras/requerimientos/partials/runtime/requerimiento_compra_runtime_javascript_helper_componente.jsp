<%--
Responsabilidad:
    Expone la función de escape JavaScript usada por scripts runtime.
Incluido desde:
    requerimiento_compra_scripts_consulta_runtime_componente.jsp, requerimiento_compra_scripts_edicion_afiliado_componente.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
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
<%!
private String jsCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>
