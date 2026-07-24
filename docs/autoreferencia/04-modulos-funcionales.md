# Modulos funcionales

## Afiliados

Proposito comprobado: busqueda, validacion, documentacion, aportes, planes, credenciales, reincorporaciones e historicos.

Entradas:
- `ext-web/docroot/WEB-INF/struts-config.xml`, acciones `/afiliados/*`.
- Actions bajo `ar.com.ospim.afiliados.action`.

Riesgo: nombres de actions y forwards son contratos publicos legacy.

## Autorizaciones y reclamos prestacionales

Proposito comprobado: visualizar y editar reclamos y prestaciones.

Entrada:
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`.

Contratos:
- `Constants.CMD`;
- namespace de portlet;
- endpoints AJAX de afiliado;
- fragmentos JSP incluidos.

## Compras y requerimientos

Comprobado: existe integracion Compras -> Reclamo Prestacional mediante `WebKeysCompras` y `ReclamoPrestacionalCompraContexto`.

El contexto valida nonce, usuario y vigencia antes de forzar modo alta.

Pendiente: mapa completo de requerimientos, presupuestos, adjudicacion, prestadores, archivos y notificaciones.

## Documentos y archivos

Comprobado: Struts contiene recuperacion y busqueda de documentacion, incluyendo `GetFileActionExt` y forwards de Document Library.

No documentar credenciales, rutas sensibles ni datos personales.
