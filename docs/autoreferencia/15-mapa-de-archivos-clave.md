# Mapa de archivos clave

| Ruta | Responsabilidad | Advertencias |
|---|---|---|
| `AGENTS.md` | Entrada operativa para agentes. | No sustituye inspeccion actual. |
| `build.xml` | Build Ant y armado de `ROOT.war`. | Contiene reemplazos de entorno; no copiar valores sensibles. |
| `build-parent.xml` | Targets y propiedades heredadas. | Revisar antes de ejecutar build. |
| `ext-web/docroot/WEB-INF/struts-config.xml` | Actions y forwards Struts. | Alta centralidad; cambios pueden afectar muchos flujos. |
| `ext-web/docroot/WEB-INF/tiles-defs.xml` | Definitions Tiles, si existe con ese nombre. | Verificar ruta antes de editar. |
| `ext-web/docroot/html/portlet/autorizaciones/init.jsp` | Inicializacion compartida de autorizaciones. | Impacto transversal. |
| `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp` | Inicializacion del submodulo. | Preservar imports y atributos. |
| `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp` | Ensamblado y normalizacion del flujo. | Contiene seguridad y parches AJAX legacy. |
| `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp` | Botonera y edicion de prestaciones. | Preservar IDs y handlers. |
| `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js` | Compatibilidad visual/funcional del editor. | ES5 y jQuery legacy. |
