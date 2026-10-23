# Orden de Compra directa desde requerimientos AUTORIZADOS

Implementacion local del 23/10/2026. Repositorio https://github.com/JerePrograma/molineros.git, ruta C:\wsmolineros\ext, rama main.

## Comportamiento

El boton Crear Orden de Compra emite directamente una orden para requerimientos activos AUTORIZADOS y usuarios con rol COTIZAR_COMPRAS. No abre un formulario de carga. Solo envia ID y token; la Action obtiene toda la informacion desde el requerimiento y la cotizacion adjudicada en el servidor.

El guardado crea una cabecera y su detalle, asigna numero correlativo propio y confirma ORDEN_COMPRA (5) en una sola transaccion. Bloquea el requerimiento, valida la adjudicacion y, para el circuito medico, el RP activo. Los reintentos concurrentes recuperan la misma orden. Al finalizar se inicia la descarga del PDF y queda disponible su consulta de solo lectura y reimpresion. Un GET solo consulta y nunca emite una orden.

Para Sistemas/RRHH se usan proveedor/CUIT adjudicados y los precios estimados estructurados del requerimiento si existen. Para Prestaciones Medicas se usan cantidades, precios, impuestos y condiciones de la comparativa adjudicada. Ninguno de esos contratos guarda moneda: se conserva como dato no informado; no se supone ARS ni USD. Los importes y campos sin fuente se conservan nulos, distinguidos de ceros expresamente registrados. Si falta un importe necesario no se muestra una suma parcial como total completo. No se extraen precios ni condiciones del contenido de los PDF presupuestados.

Los valores enviados desde una pantalla anterior (precios, moneda, impuestos o condiciones) se ignoran. La firma Java anterior crear(id,Map,usuario) se conserva delegando a crearDirecta, sin un bypass de edicion. Confeccionado por y auditoria se obtienen del usuario autenticado. La fecha de alta del requerimiento no se inventa como fecha de recepcion en Compras.

Se conservan los datos y numeros de las ordenes ya emitidas, sin recalcularlas. Los datos del afiliado/sector se leen del requerimiento, inmutable despues de emitir la orden. Las firmas permanecen como espacios de firma; no se fabrican datos de autorizacion.

## Referencia legacy elegida

- Liquidaciones: ext-web/docroot/html/portlet/liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp, fieldset/lfr-table y funcion imprimirOP().
- Liquidaciones: ext-impl/src/ar/com/ospim/liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java, generacion de PDF posterior al guardado.
- Compras: RequerimientoCompraComparativaServiceImpl, transaccion JDBC de cabecera/detalle; RequerimientoCompraGuardadoToken, proteccion del envio; PdfServlet.generaRequerimientoCompra, permisos y descarga.

Se adaptaron esas convenciones dentro de Compras sin crear dependencias nuevas entre modulos. Los dos PDF suministrados se usaron como modelos visuales/de campos, no como instrucciones operativas. Los ejemplos de QA contienen exclusivamente datos ficticios.

## Archivos y motivo

| Archivo (relativo al repositorio) | Motivo |
| --- | --- |
| ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java | Mantener consulta de RP vinculado despues de emitir OC medica. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/action/CambiarEstadoRequerimientoCompraAction.java | Evitar que la ruta anterior cierre el requerimiento sin completar la orden. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/action/RequerimientoCompraOrdenAction.java | Adaptacion HTTP, roles, token, emision directa y consulta. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/action/RequerimientoCompraRenderActionUtil.java | Publicar existencia real de OC, incluidos historicos sin documento. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/beans/RequerimientoCompraOrden.java | Datos de la orden emitida y total decimal o desconocido ante datos incompletos. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/helper/RequerimientoCompraOrdenHelper.java | Emision canonica sin entrada editable; adjudicacion, validacion y valores desconocidos. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/helper/RequerimientoCompraOrdenPdfHelper.java | Reporte con datos de la OC persistida y variante medica. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraOrdenServiceUtil.java | Delegacion de consulta/guardado. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraOrdenServiceImpl.java | JDBC transaccional y snapshot cabecera/detalle; conserva campos monetarios nulos. |
| ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql | Tablas, bloqueo, confirmacion y transicion medica compatibles. |
| ext-impl/src/ar/com/ospim/servlets/PdfServlet.java | Endpoint protegido accion=ordenCompra; bytes/EOL mixtos originales preservados. |
| ext-web/docroot/WEB-INF/classes/jasper/compras/orden_compra.jrxml | Plantilla A4 con paginacion y campos medicos condicionales. |
| ext-web/docroot/WEB-INF/struts-config.xml | Registro de la ruta nueva. |
| ext-web/docroot/WEB-INF/tiles-defs.xml | Registro de la vista nueva. |
| ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp | POST directo con ID/token y consulta de orden emitida. |
| ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_compra_orden.jsp | Consulta de solo lectura e inicio de descarga PDF tras emitir, sin datos editables. |
| docs/sql/compras/20261023_orden_compra_formulario.sql | Migracion de instalacion sin operaciones destructivas. |
| test/compras/ComprasOrdenHelperTest.java | Reglas con consultas/persistencia simuladas. |
| test/compras/ComprasOrdenPdfTest.java | Jasper real, paginacion y entradas invalidas. |
| test/compras/orden_compra_directa_test.js | Envio directo, proteccion de doble clic y recuperacion ante fallo de envio. |
| test/compras/orden_compra_test.ps1 | Runner Java 8/source1.5 de clases productivas y pruebas. |
| test/compras/orden_compra_test.sql | Migracion y contratos SQL, importes desconocidos y ceros, en base aislada. |
| test/compras/orden_compra_upgrade_test.sql | Actualizacion del DDL anterior y preservacion de ordenes existentes. |
| docs/sql/compras/20261023_orden_compra_generacion_directa.sql | Migracion incremental ALTER para instalaciones previas. |
| Este documento | Instalacion, referencias y evidencia verificable. |

## Instalacion

1. Instalacion nueva: aplicar docs/sql/compras/20261023_orden_compra_formulario.sql. Instalacion que ya aplico la version anterior: aplicar solo docs/sql/compras/20261023_orden_compra_generacion_directa.sql. Ambas incluyen BEGIN/COMMIT. La incremental solo cambia nulabilidad/constraints: no actualiza datos de ordenes existentes. Ninguna fue aplicada a la base activa durante esta tarea.
2. Si la numeracion debe continuar las OC manuales, ajustar la secuencia compras.requerimiento_orden_compra_id_orden_compra_seq al ultimo numero aprobado antes de la primera emision. No se dedujo ese valor de los dos ejemplos. Sin ajuste, comienza una nueva numeracion propia del modulo.
3. Compilar/desplegar con las acciones configuradas del proyecto, incluyendo el JRXML nuevo en WEB-INF/classes/jasper/compras. Se compila en runtime como los reportes dinamicos existentes; utiliza Jasper y el compilador ECJ ya provistos por el entorno Tomcat.
4. Probar con sesion autenticada: Sistemas AUTORIZADO -> Crear Orden de Compra -> estado5 y descarga directa; repetir Prestaciones Medicas y comprobar RP/afiliado/importes. Revisar permisos, estado vigente, token, reconsulta de orden emitida y faltantes marcados No informado.

La plantilla esta en una carpeta excluida localmente por .git/info/exclude; se marco con intent-to-add forzado para que quede visible en el diff. No se agregaron librerias ni frameworks.

## Validacion y limites

- Compilacion focalizada con JDK1.8.0_251, encoding ISO-8859-1, source/target1.5: exit0. Incluye Action, render helper, servicios y PdfServlet.
- Reglas del Helper: 93 comprobaciones aprobadas; ver output/orden-compra-qa/directa-final/ComprasOrdenHelperTest.log para el contador final.
- Prueba JS de envio directo: 16 comprobaciones aprobadas, exit0.
- Jasper real: ocho casos, incluidos los cuatro anteriores con datos completos y nuevos casos sin moneda, sin importes, con detalle sin precio y con ceros explicitos. Paginas 1/1/7/2/1/1/1/1. Rechaza borrador, moneda invalida, importes negativos o inconsistentes y detalles vacios. Exit0. Extraccion de texto confirma ausencia de ceros/monedas inventados y preservacion de ordenes completas. Revision visual aprobada.
- PostgreSQL17.5 aislado 127.0.0.1:55439: initdb/start/createdb/psql/stop exit0; orden_compra_test: OK. Fixtures externos minimos; bloqueo, trigger y confirmacion reales de la migracion. No prueba los datos de la base activa.
- Concurrencia real de dos sesiones: ambas exit0, una sola cabecera y detalle, misma OC, estado5; CONCURRENCIA_IDEMPOTENTE_OK. Servidor aislado detenido y puerto liberado.
- git diff --check: exit0. Cada archivo de texto modificado verificado byte a byte como ISO-8859-1 sin BOM ni marcadores de mojibake. Sin recodificacion masiva. Diff focalizado revisado.
- El PDF de pedido de presupuesto y los flujos de carga/cotizacion existentes permanecen fuera del cambio.

Comandos reproducibles de pruebas:

```powershell
& '.\test\compras\orden_compra_test.ps1' -EvidenceName 'directa-final'
& 'C:\Users\Jerem\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' '.\test\compras\orden_compra_directa_test.js'
# Solo en una base vacia aislada compras_orden_test_*:
psql -X -v ON_ERROR_STOP=1 -d compras_orden_test_<id> -f test/compras/orden_compra_test.sql
git diff --check
git status --short
git diff --stat
```

Evidencia SQL inicial/concurrencia: output/orden-compra-qa/20261023_155319. Actualizacion SQL directa+upgrade: output/orden-compra-qa/directa_20261023_182211. Evidencia Java/PDF final: output/orden-compra-qa/directa-final. PDF y PNG de revision visual directa: output/orden-compra-qa/directa.

PRUEBA UI MANUAL PENDIENTE. Esta actualizacion no fue desplegada ni ejecutada en una sesion autenticada del portal. Al cierre se detecto un listener8080; no se reinicio ni modifico su instancia. No esta Ant en C:\wsmolineros\tools\apache-ant-1.10.17 y no se encontro la accion configurada Build Java focalizado. No se ejecuto build Ant completo, compilacion JSP del portal, despliegue ni smoke test web. El intento de usar JspC del Tomcat local no encontro su clase; falta ademas html/common/init.jsp en la copia del docroot. No se afirma readiness de produccion.

## Trazabilidad Git

HEAD inicial encontrado: 1ff07e1849f595ba75c907cea69c346c78b379b6.
Baseline integrado mediante fetch/switch main/merge --ff-only: 84d7d65481d1317b4723f44c10f08bc7e87ae819. Exit0. No habia cambios locales previos.
Publicacion inicial: 6c382267b230356a3634d9fb43b2c8b0eeca472b. La respuesta posterior del usuario eligio generacion directa; esta actualizacion parte de ese commit en main. El SHA de publicacion de la actualizacion se registra en el historial Git. No se crearon ramas ni pull requests.
