# Contexto del proyecto

## Hechos comprobados

Molineros es una aplicacion empresarial legacy extendida sobre Liferay. El repositorio contiene configuracion Struts para afiliados y vistas de autorizaciones/reclamos prestacionales. El build raiz genera `ROOT.war`.

Actores visibles:
- afiliado;
- usuario autenticado del portal;
- prestador;
- sectores operativos de autorizaciones y Compras.

Evidencia:
- `build.xml`, proyecto Ant `ext`, targets `deploy`, `war` y `war-qa`.
- `ext-web/docroot/WEB-INF/struts-config.xml`, acciones `/afiliados/*`.
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`.

## Inferencias

- El sistema soporta procesos administrativos y prestacionales de una obra social.
- Compras puede originar un reclamo prestacional mediante un handoff cross-portlet.
- La aplicacion se despliega como contexto raiz.

Estas conclusiones son inferidas por nombres de paquetes, vistas y artefacto `ROOT.war`; deben validarse contra documentacion funcional y usuarios del sistema.

## Restricciones operativas

El proyecto exige cambios minimos, compatibilidad legacy, preservacion de contratos y edicion ISO-8859-1 sin BOM. La documentacion nunca debe tomarse como evidencia suficiente para modificar codigo sin inspeccion actual.
