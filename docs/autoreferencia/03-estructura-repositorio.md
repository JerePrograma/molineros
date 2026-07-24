# Estructura del repositorio

## Mapa practico comprobado

| Ruta | Finalidad |
|---|---|
| `build.xml` | Orquestacion Ant, copia de dependencias y armado de `ROOT.war`. |
| `build-parent.xml` | Configuracion Ant heredada; inspeccionar antes de inventar targets. |
| `ext-web/docroot` | Recursos web desplegables. |
| `ext-web/docroot/WEB-INF` | Descriptores Struts, Tiles, Liferay y web. |
| `ext-web/docroot/html/portlet` | JSP/JSPF organizados por portlet y modulo. |
| `ext-web/tmp` | Arbol temporal usado para empaquetar; no editar como fuente. |
| `ext-impl` | Implementacion Java y properties copiados al WAR. |
| `lib/global`, `lib/portal` | Dependencias copiadas al WAR. |
| `modules` | Contiene al menos `portal-impl.jar` usado por el build. |
| `classes` | Properties copiados a `WEB-INF/classes`. |

## Rutas funcionales comprobadas

- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/`
- `ext-web/docroot/WEB-INF/struts-config.xml`

## Precaucion

El mapa no es un inventario completo. Antes de editar, confirmar la ruta real, callers directos y si el archivo es fuente o generado.
