# Convenciones de codigo

## Observadas

- Actions Struts con rutas `/modulo/verbo_objeto`.
- Implementaciones Java bajo paquetes `ar.com.ospim.*.action`.
- Forwards con nombres `portlet.<modulo>.<vista>`.
- JSP incluyen `init.jsp` del portlet y submodulo.
- IDs y funciones JavaScript prefijados con `<portlet:namespace />`.
- Parametros leidos con utilidades Liferay como `ParamUtil`.
- Validacion con `Validator`.
- Salida HTML dinamica escapada con `HtmlUtil.escape`.
- JavaScript encapsulado en IIFE y compatible con sintaxis ES5.
- Guardas idempotentes mediante propiedades como `__rpAfiliadoNoBloqueante`.

## Recomendaciones documentales

- Citar ruta, clase/metodo o bloque JSP.
- Separar hecho, inferencia y regla operativa.
- Explicar por que un archivo adicional es indispensable.

## Restricciones obligatorias

- Diff minimo.
- Sin reformateo integral.
- Sin renombrar contratos.
- ISO-8859-1 sin BOM.
- Compatibilidad Java 8 y ES5.
