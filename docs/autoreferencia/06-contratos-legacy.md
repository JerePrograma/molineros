# Contratos legacy

No alterar sin analizar productores, consumidores y configuracion.

## Navegacion y MVC

- Paths Struts, por ejemplo `/afiliados/buscar_afiliados`.
- Tipos Action, por ejemplo `ar.com.ospim.afiliados.action.BuscarAfiliadosAction`.
- Forwards `portlet.*`.
- Definitions Tiles asociadas a esos forwards.

## JSP y navegador

- IDs, `name`, handlers `onclick` y namespaces de portlet.
- `window.ReclamoPrestacionalNamespace`.
- Endpoints `filtrarLetraComprobante`, `evalua_permanencia_afiliado`, `tiene_observaciones_afiliado`, `buscar_afiliado_datos`.
- Ruta `struts_action=/autorizaciones/buscar_afiliados`.

## Integracion Compras -> Reclamo

- `WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE`.
- `WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA`.
- `ReclamoPrestacionalCompraContexto.coincideNonce`.
- `perteneceAUsuario`.
- `estaVigente`.
- `Constants.CMD` y `Constants.ADD`.

La validacion es fail-closed: el modo alta solo se fuerza cuando todas las condiciones son verdaderas.

Evidencia:
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`.
- `ext-web/docroot/WEB-INF/struts-config.xml`.
