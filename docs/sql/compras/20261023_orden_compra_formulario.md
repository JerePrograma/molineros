# Orden de Compra desde requerimientos AUTORIZADOS

Implementacion local del 23/10/2026. Repositorio https://github.com/JerePrograma/molineros.git, ruta C:\wsmolineros\ext, rama main.

## Comportamiento

El boton Crear Orden de Compra abre el formulario para requerimientos activos AUTORIZADOS y usuarios con rol COTIZAR_COMPRAS. Se precargan proveedor adjudicado, datos del requerimiento, cantidades y condiciones disponibles. El usuario completa moneda ARS/USD, pago, entrega y contactos. Para Prestaciones Medicas, cantidades, precios e impuestos provienen de la comparativa adjudicada y no pueden alterarse desde el formulario.

El guardado crea una cabecera y su detalle, asigna numero correlativo propio y confirma ORDEN_COMPRA (5) en una sola transaccion. Bloquea el requerimiento, valida la adjudicacion y, para el circuito medico, el RP activo. Los reintentos concurrentes recuperan la misma orden. Despues se puede consultar y descargar el PDF desde el requerimiento. Las ordenes historicas que solo tienen estado 5 no muestran un acceso a un documento inexistente.

Se conservan proveedor/CUIT e importes de la orden emitida aunque posteriormente cambie la cotizacion. Los datos del afiliado/sector se leen del requerimiento, inmutable despues de emitir la orden. Las firmas permanecen como espacios de firma; no se fabrican datos de autorizacion.

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
| ext-impl/src/ar/com/ospim/compras/requerimientos/action/RequerimientoCompraOrdenAction.java | Adaptacion HTTP, roles, token, formulario y consulta. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/action/RequerimientoCompraRenderActionUtil.java | Publicar existencia real de OC, incluidos historicos sin documento. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/beans/RequerimientoCompraOrden.java | Datos de la orden emitida y total decimal. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/helper/RequerimientoCompraOrdenHelper.java | Precarga, adjudicacion, validacion y cantidades/precios canonicos medicos. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/helper/RequerimientoCompraOrdenPdfHelper.java | Reporte con datos de la OC persistida y variante medica. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraOrdenServiceUtil.java | Delegacion de consulta/guardado. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraOrdenServiceImpl.java | JDBC transaccional y snapshot cabecera/detalle. |
| ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql | Tablas, bloqueo, confirmacion y transicion medica compatibles. |
| ext-impl/src/ar/com/ospim/servlets/PdfServlet.java | Endpoint protegido accion=ordenCompra; bytes/EOL mixtos originales preservados. |
| ext-web/docroot/WEB-INF/classes/jasper/compras/orden_compra.jrxml | Plantilla A4 con paginacion y campos medicos condicionales. |
| ext-web/docroot/WEB-INF/struts-config.xml | Registro de la ruta nueva. |
| ext-web/docroot/WEB-INF/tiles-defs.xml | Registro de la vista nueva. |
| ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp | Acceso autorizado al formulario y consulta de orden emitida. |
| ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_compra_orden.jsp | Formulario legacy y consulta; calculo ES5 exacto de centavos. |
| docs/sql/compras/20261023_orden_compra_formulario.sql | Migracion de instalacion sin operaciones destructivas. |
| test/compras/ComprasOrdenHelperTest.java | Reglas con consultas/persistencia simuladas. |
| test/compras/ComprasOrdenPdfTest.java | Jasper real, paginacion y entradas invalidas. |
| test/compras/orden_compra_importes_test.js | Centavos exactos, incluidos importes grandes, sin redondeo binario. |
| test/compras/orden_compra_test.ps1 | Runner Java 8/source1.5 de clases productivas y pruebas. |
| test/compras/orden_compra_test.sql | Migracion y contratos SQL en base aislada con fixtures. |
| Este documento | Instalacion, referencias y evidencia verificable. |

## Instalacion

1. Aplicar docs/sql/compras/20261023_orden_compra_formulario.sql sobre la base destino antes de habilitar el codigo. La migracion incluye BEGIN/COMMIT y no fue aplicada a la base activa en esta tarea.
2. Si la numeracion debe continuar las OC manuales, ajustar la secuencia compras.requerimiento_orden_compra_id_orden_compra_seq al ultimo numero aprobado antes de la primera emision. No se dedujo ese valor de los dos ejemplos. Sin ajuste, comienza una nueva numeracion propia del modulo.
3. Compilar/desplegar con las acciones configuradas del proyecto, incluyendo el JRXML nuevo en WEB-INF/classes/jasper/compras. Se compila en runtime como los reportes dinamicos existentes; utiliza Jasper y el compilador ECJ ya provistos por el entorno Tomcat.
4. Probar con sesion autenticada: Sistemas AUTORIZADO -> formulario -> guardar -> estado5 -> descargar; repetir Prestaciones Medicas y comprobar RP/afiliado/importes. Revisar tambien errores de validacion y reconsulta de orden emitida.

La plantilla esta en una carpeta excluida localmente por .git/info/exclude; se marco con intent-to-add forzado para que quede visible en el diff. No se agregaron librerias ni frameworks.

## Validacion y limites

- Compilacion focalizada con JDK1.8.0_251, encoding ISO-8859-1, source/target1.5: exit0. Incluye Action, render helper, servicios y PdfServlet.
- Reglas del Helper: aprobadas; ver output/orden-compra-qa/final/ComprasOrdenHelperTest.log para el contador final.
- Prueba JS de importes: 12 comprobaciones aprobadas, exit0.
- Jasper real: Sistemas USD una pagina, Medica ARS una pagina, 60 renglones extensos siete paginas, campos extensos dos paginas. Rechaza borrador sin ID, moneda invalida y detalles vacios. Exit0. Revision visual de PNG normal y multipagina aprobada.
- PostgreSQL17.5 aislado 127.0.0.1:55439: initdb/start/createdb/psql/stop exit0; orden_compra_test: OK. Fixtures externos minimos; bloqueo, trigger y confirmacion reales de la migracion. No prueba los datos de la base activa.
- Concurrencia real de dos sesiones: ambas exit0, una sola cabecera y detalle, misma OC, estado5; CONCURRENCIA_IDEMPOTENTE_OK. Servidor aislado detenido y puerto liberado.
- git diff --check: exit0. Cada archivo de texto modificado verificado byte a byte como ISO-8859-1 sin BOM ni marcadores de mojibake. Sin recodificacion masiva. Diff focalizado revisado.
- El PDF de pedido de presupuesto y los flujos de carga/cotizacion existentes permanecen fuera del cambio.

Comandos reproducibles de pruebas:

```powershell
& '.\test\compras\orden_compra_test.ps1' -EvidenceName 'final'
& 'C:\Users\Jerem\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' '.\test\compras\orden_compra_importes_test.js'
# Solo en una base vacia aislada compras_orden_test_*:
psql -X -v ON_ERROR_STOP=1 -d compras_orden_test_<id> -f test/compras/orden_compra_test.sql
git diff --check
git status --short
git diff --stat
```

Evidencia SQL/concurrencia: output/orden-compra-qa/20261023_155319. Evidencia Java/PDF final: output/orden-compra-qa/final. PNG de revision visual: output/orden-compra-qa/pdf-visual.

PRUEBA UI MANUAL PENDIENTE. No hay listener8080 ni sesion de portal preparada en esta copia. No esta Ant en C:\wsmolineros\tools\apache-ant-1.10.17 y no se encontro la accion configurada Build Java focalizado. No se ejecuto build Ant completo, compilacion JSP del portal, despliegue ni smoke test web. El intento de usar JspC del Tomcat local no encontro su clase; falta ademas html/common/init.jsp en la copia del docroot. No se afirma readiness de produccion.

## Trazabilidad Git

HEAD inicial encontrado: 1ff07e1849f595ba75c907cea69c346c78b379b6.
Baseline integrado mediante fetch/switch main/merge --ff-only: 84d7d65481d1317b4723f44c10f08bc7e87ae819. Exit0. No habia cambios locales previos.
HEAD final y referencia origin/main consultada: 84d7d65481d1317b4723f44c10f08bc7e87ae819. Los cambios de esta tarea quedan locales; no se creo commit ni se ejecuto push. No se crearon ramas ni pull requests.
