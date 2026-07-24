# Arquitectura y tecnologias

## Comprobado

- Build: Apache Ant; `build.xml` importa `build-parent.xml`.
- Empaquetado: `ROOT.war` desde `ext-web/tmp`.
- Framework MVC: Struts 1.2, segun DTD de `struts-config.xml`.
- Presentacion: JSP/JSPF y JavaScript con jQuery.
- Plataforma: extension Liferay.
- Capas observables: vistas en `ext-web/docroot/html`, actions Java por paquete, configuracion en `ext-web/docroot/WEB-INF`, implementacion en `ext-impl`, librerias en `lib/global` y `lib/portal`.
- Entornos de build: produccion y QA mediante reemplazo de tokens.

Evidencia:
- `build.xml`.
- `ext-web/docroot/WEB-INF/struts-config.xml`.
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`.

## Reglas de compatibilidad

- Java 8.
- Liferay 5.2.
- JavaScript ES5 y jQuery legacy.
- JSP/JSPF, Struts y Tiles existentes.

Estas versiones son reglas operativas del proyecto; salvo Struts 1.2, no todas fueron verificadas remotamente en archivos de version.

## Riesgos derivados

- No usar APIs Java posteriores a 8.
- No introducir `let`, `const`, arrow functions, Promises ni APIs modernas sin soporte comprobado.
- No reemplazar integraciones Struts/Tiles por rutas nuevas.
- El build copia JAR y properties a `ext-web/tmp`; ese directorio debe tratarse como generado.
- Los tokens de entorno no deben documentarse con valores sensibles.

## PostgreSQL

Pendiente de verificacion documental. No afirmar PostgreSQL 9.6 hasta localizar evidencia de version no sensible.
