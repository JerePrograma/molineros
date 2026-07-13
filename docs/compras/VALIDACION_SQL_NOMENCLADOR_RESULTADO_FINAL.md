# Validacion SQL/Nomenclador - Resultado final

Fecha: 2026-07-12

## Resumen ejecutivo

- Estado inicial: `main` limpio, commit `183aebe9`, igual a `origin/main`; compilaba, pero el SQL y el flujo de detalle tecnico estaban rotos.
- Estado final tecnico: SQL instalable, smoke verde, Java 8 y web merge verdes, JSPC dirigido verde, tests verdes, Jasper/PDF validado para ambos tipos y residuos runtime de articulos eliminados.
- Commitable segun el criterio estricto solicitado: **No todavia**.
- Pusheable: **No**; no se autorizo commit/push y falta la validacion funcional live de Liferay contra catalogos canonicos de QA.
- Bloqueador pendiente: ejecutar el checklist manual en una instancia QA con Farmacia/Autorizaciones reales y usuario con roles de Compras.

## Matriz de contrato

| Sector | Tipo esperado | Buscador | IDs resueltos | Validacion DB | Validacion Java | PDF |
| --- | --- | --- | --- | --- | --- | --- |
| Farmacia | MEDICAMENTO | Medicamentos | id_medicamento | Si, contrato SQL; QA canonica live pendiente | Si | Si |
| Prestaciones Medicas | NOMENCLADOR | Prestaciones | id_prestacion + id_tipo | Si, contrato SQL; QA canonica live pendiente | Si | Si |
| Legales | NOMENCLADOR | Prestaciones | id_prestacion + id_tipo | Si, contrato SQL; QA canonica live pendiente | Si | Si |

## Matriz de residuos

| Residuo | Archivo | Linea | Consumidor | Clasificacion | Accion |
| --- | --- | ---: | --- | --- | --- |
| `id_articulo` | `compras_smoke_test.sql` | 420 | smoke negativo | Seguro | conservar: falla si vuelve a una funcion |
| `compras.articulo` | `compras_smoke_test.sql` | 421 | smoke negativo | Seguro | conservar: falla si vuelve a una funcion |
| `id_articulo` | `ComprasRequerimientosUiContractTest.java` | 321 | test negativo | Seguro | conservar |
| `compras.articulo` | `ComprasRequerimientosUiContractTest.java` | 322 | test negativo | Seguro | conservar |
| `'ARTICULO'` | `ComprasRequerimientosUiContractTest.java` | 323 | test negativo | Seguro | conservar |

No hay residuo clasificado Aislar, Eliminar, Riesgoso, Rompe contrato o Rompe SQL en fuentes runtime finales.

## Evidencias principales

- Schema completo: exit 0 con `ON_ERROR_STOP=1` y `PGCLIENTENCODING=UTF8`.
- Smoke: `SQL_SMOKE_OK`, seguido de `ROLLBACK`.
- SQL source/classes: SHA-256 identico.
- Dependencias SQL de articulos: 0 filas en `pg_proc`.
- Builds: `ext-service`, `ext-impl`, `ext-web compile` y `ext-web merge`, todos BUILD SUCCESSFUL.
- JSPC Tomcat 8.5 dirigido: ambas JSP afectadas `Built File`.
- Java: todos los tests de contrato y notificacion exit 0.
- Jasper: compilado con 3.7.4; PDFs reales de medicamento y nomenclador generados y revisados visualmente.
- Git: `git diff --check` exit 0; `main` sigue en el mismo commit que `origin/main`.

## Riesgos pendientes

1. No se ejecuto la UI en un portal Liferay levantado. El JspC prueba compilacion, pero no reemplaza interaccion real, permisos, popup y re-apertura en navegador.
2. La DB descartable no contiene los catalogos Farmacia/Autorizaciones. Los services canonicos se cubrieron con tests y lectura de codigo, pero no se hizo una seleccion contra un registro real de QA.
3. No se envio SMTP real; se verificaron cuerpo, adjunto y estados con el test de notificacion.
4. El target legacy `compile-tomcat` no soporta su propia configuracion Tomcat 8.5. El JspC directo con Tomcat 8.5 paso, pero el target sigue siendo deuda de build fuera de este alcance.

## Veredicto

La implementacion queda compilada y con validacion automatizada fuerte. Sin embargo, los criterios definitivos del pedido exigen demostrar que ambos buscadores cargan IDs reales y completar alta/edicion/reapertura/envio desde Liferay. Esa prueba necesita la instancia QA y catalogos reales que no estaban disponibles en el entorno local.

```text
NO APTO PARA COMMIT
```
